/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/23/15.
 * Copyright (c) 2015 Umbala. All rights reserved.
 */

package com.tale.rxrepository;

import java.util.List;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

public class Repository<T> {
  private Comparator<T> comparator;
  protected DiskProvider<T> diskProvider;
  protected CloudProvider<T> cloudProvider;
  protected T cache;

  public Repository(DiskProvider<T> diskProvider, CloudProvider<T> cloudProvider,
      Comparator<T> comparator, NetworkVerifier networkVerifier) {
    if (diskProvider == null) {
      throw new NullPointerException("diskProvider must not be null");
    }
    if (cloudProvider == null) {
      throw new NullPointerException("cloudProvider must not be null");
    }
    this.diskProvider = diskProvider;
    this.cloudProvider = new CloudProviderWrapper<>(cloudProvider, networkVerifier);
    this.comparator = comparator;
  }

  public Observable<T> get() {
    final Observable<T> cloud =
        cloudProvider.get(0).filter(filterNewData()).flatMap(new Func1<T, Observable<T>>() {
          @Override public Observable<T> call(T t) {
            return diskProvider.save(t);
          }
        }).doOnNext(cacheAction());
    return Observable.concat(getLocal(), cloud);
  }

  public T getCache() {
    return cache;
  }

  public boolean hasCache() {
    return cache != null;
  }

  /**
   * Get the Observable which emit local data. This guarantee that only one source which has data
   * will be emitted. Begin from cache, if cache has data then emit cache then stop, else query
   * from disk (database), if disk has data then emit then stop else return an EmptyObservable.
   *
   * @return an Observable
   */
  protected Observable<T> getLocal() {
    final Observable<T> disk = diskProvider.get().filter(filterNewData()).doOnNext(cacheAction());
    return Observable.concat(cache(), disk).first(new Func1<T, Boolean>() {
      @Override public Boolean call(T t) {
        return isNotNullOrEmpty(t);
      }
    }).onErrorResumeNext(Observable.<T>empty());
  }

  Action1<T> cacheAction() {
    return new Action1<T>() {
      @Override public void call(T t) {
        Repository.this.cache = t;
      }
    };
  }

  protected Observable<T> cache() {
    return Observable.defer(new Func0<Observable<T>>() {
      @Override public Observable<T> call() {
        return Observable.just(cache);
      }
    });
  }

  protected Observable<T> networkError() {
    return Observable.error(new NoNetworkException());
  }

  Func1<T, Boolean> filterNewData() {
    return new Func1<T, Boolean>() {
      @Override public Boolean call(T t) {
        // In case no comparator then we not filter.
        return comparator == null || !comparator.isSame(t, cache);
      }
    };
  }

  Boolean isNotNullOrEmpty(T t) {
    return t != null && (!(t instanceof List) || ((List) t).size() > 0);
  }
}
