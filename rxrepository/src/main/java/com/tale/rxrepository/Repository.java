/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/23/15.
 * Copyright (c) 2015 Umbala. All rights reserved.
 */

package com.tale.rxrepository;

import java.util.Comparator;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

public class Repository<T> {
  private DiskProvider<T> diskProvider;
  private CloudProvider<T> cloudProvider;
  private T cache;
  private Comparator<T> comparator;

  public Repository(DiskProvider<T> diskProvider, CloudProvider<T> cloudProvider, Comparator<T> comparator) {
    if (diskProvider == null) {
      throw new NullPointerException("diskProvider must not be null");
    }
    if (cloudProvider == null) {
      throw new NullPointerException("cloudProvider must not be null");
    }
    this.diskProvider = diskProvider;
    this.cloudProvider = cloudProvider;
    this.comparator = comparator;
  }

  public Observable<T> get() {
    final Observable<T> cloud = cloudProvider.get().filter(new Func1<T, Boolean>() {
      @Override public Boolean call(T t) {
        if (comparator == null) {
          // In case no comparator then we not filter.
          return true;
        }
        return comparator.compare(t, cache)
            != 0; // Compare with cache data. If has same then no need to emit
      }
    }).flatMap(new Func1<T, Observable<T>>() {
      @Override public Observable<T> call(T t) {
        return diskProvider.save(t);
      }
    }).doOnNext(cacheAction());

    return Observable.concat(getLocal(), cloud);
  }

  public T getCache() {
    return cache;
  }

  /**
   * Get the Observable which emit local data. This guarantee that only one source which has data
   * will be emitted. Begin from cache, if cache has data then emit cache then stop, else query
   * from disk (database), if disk has data then emit then stop else return an EmptyObservable.
   * @return an Observable
   */
  private Observable<T> getLocal() {
    final Observable<T> disk = diskProvider.get().doOnNext(cacheAction());
    return Observable.concat(cache(), disk).first(new Func1<T, Boolean>() {
      @Override public Boolean call(T t) {
        if (t == null) {
          return false;
        }
        if (t instanceof List) {
          return ((List) t).size() > 0;
        }
        return true;
      }
    }).onErrorResumeNext(Observable.<T>empty());
  }

  private Action1<T> cacheAction() {
    return new Action1<T>() {
      @Override public void call(T t) {
        Repository.this.cache = t;
      }
    };
  }

  private Observable<T> cache() {
    return Observable.defer(new Func0<Observable<T>>() {
      @Override public Observable<T> call() {
        return Observable.just(cache);
      }
    });
  }
}
