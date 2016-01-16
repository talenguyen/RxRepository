/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/25/15.
 * Copyright (c) 2015 Tale. All rights reserved.
 */

package com.tale.rxrepositorymosby;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.tale.rxrepository.ListRepository;
import java.util.List;
import java.util.NoSuchElementException;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public abstract class RxRepositoryMvpLcePresenter<BM, M, V extends MvpLcemView<List<M>>>
    extends MvpBasePresenter<V> {

  protected ListRepository<BM> repository;
  protected Subscriber<List<M>> subscriber;
  protected boolean loadMore;
  protected boolean pullToRefresh;

  public RxRepositoryMvpLcePresenter(ListRepository<BM> repository) {
    this.repository = repository;
  }

  @NonNull protected abstract Func1<List<BM>, List<M>> mapFunction();

  /**
   * Unsubscribes the subscriber and set it to null
   */
  protected void unsubscribe() {
    if (subscriber != null && !subscriber.isUnsubscribed()) {
      subscriber.unsubscribe();
    }

    subscriber = null;
  }

  public void loadData() {
    subscribe(repository.get().map(mapFunction()), false);
  }

  public void refresh() {
    subscribe(repository.refresh().map(mapFunction()), true);
  }

  public void loadMore() {
    if (loadMore) {
      // No load more while it's loading.
      return;
    }
    loadMore = true;
    subscribe(repository.more().map(mapFunction()), false);
  }

  /**
   * Subscribes the presenter himself as subscriber on the observable
   *
   * @param observable The observable to subscribe
   * @param pullToRefresh Pull to refresh?
   */
  public void subscribe(Observable<List<M>> observable, final boolean pullToRefresh) {
    this.pullToRefresh = pullToRefresh;
    if (!repository.hasCache() && !loadMore) {
      if(isViewAttached()) {
        getView().showLoading(pullToRefresh);
      }
    }

    unsubscribe();

    subscriber = new Subscriber<List<M>>() {
      private boolean ptr = pullToRefresh;

      @Override public void onCompleted() {
        RxRepositoryMvpLcePresenter.this.onCompleted();
      }

      @Override public void onError(Throwable e) {
        RxRepositoryMvpLcePresenter.this.onError(e, ptr);
      }

      @Override public void onNext(List<M> m) {
        RxRepositoryMvpLcePresenter.this.onNext(m);
      }
    };

    if (test()) {
      observable.subscribe(subscriber);
    } else {
      observable.subscribeOn(getScheduler())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(subscriber);
    }
  }

  @VisibleForTesting boolean test() {
    return false;
  }

  protected Scheduler getScheduler() {
    return Schedulers.io();
  }

  protected void onCompleted() {

    if (loadMore) {
      // If loadMore flash is not reset, that mean onNext will not be called or no data then we
      // reset loadMore flag and set notify the view that no more to hide load more progress.
      if (isViewAttached()) {
        getView().onNoMore();
      }
    } else if (pullToRefresh) {
      if (isViewAttached()) {
        getView().showContent();
      }
    } else if (!repository.hasCache()) {
      if (isViewAttached()) {
        // onCompleted is called but there is no error and no data then notify the view that no
        // element to show and it should show empty view.
        getView().showError(new NoSuchElementException(), this.pullToRefresh);
      }
    }

    unsubscribe();
    this.loadMore = false;
    this.pullToRefresh = false;
  }

  protected void onError(Throwable e, boolean pullToRefresh) {

    if (loadMore) {
      if (isViewAttached()) {
        // Show light error in case pullToRefresh or loadMore.
        getView().onNoMore();
        getView().showError(e, true);
      }
    } else if (pullToRefresh) {
      if (isViewAttached()) {
        // Show light error in case pullToRefresh or loadMore.
        getView().showError(e, true);
      }
    } else if (!repository.hasCache()) {
      if (!repository.hasCache()) {
        if (isViewAttached()) {
          getView().showError(e, false);
        }
      }
    } else if (isViewAttached() && getView().getData() == null) {
      getView().setData(mapFunction().call(repository.getCache()));
      getView().showContent();
    }

    unsubscribe();
  }

  protected void onNext(List<M> data) {
    if (isViewAttached()) {
      final V view = getView();
      view.setData(data);
      view.showContent();
    }

    // If have data then we reset loadMore flag.
    loadMore = false;
  }

  @Override public void detachView(boolean retainInstance) {
    super.detachView(retainInstance);
    if (!retainInstance) {
      unsubscribe();
    }
  }
}
