/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/24/15.
 * Copyright (c) 2015 Tale. All rights reserved.
 */

package com.tale.rxrepository;

import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class ListRepository<T> extends Repository<List<T>> {

  private int page = 0;

  public ListRepository(DiskProvider<List<T>> diskProvider, CloudProvider<List<T>> cloudProvider,
      Comparator<List<T>> comparator, NetworkVerifier networkVerifier) {
    super(diskProvider, cloudProvider, comparator, networkVerifier);
    cache = new ArrayList<>();
  }

  public Observable<List<T>> refresh() {
    return cloudProvider.get(0)
        .filter(filterNewData())
        .flatMap(new Func1<List<T>, Observable<List<T>>>() {
          @Override public Observable<List<T>> call(List<T> ts) {
            return getDiskProvider().save(ts);
          }
        }).doOnNext(cacheAction()).doOnNext(new Action1<List<T>>() {
          @Override public void call(List<T> ts) {
            page = 0;
          }
        });
  }

  public Observable<List<T>> more() {
    final int nextPage = page + 1;
    return cloudProvider.get(nextPage).filter(new Func1<List<T>, Boolean>() {
      @Override public Boolean call(List<T> ts) {
        return ts != null && ts.size() > 0;
      }
    }).filter(filterNewData()).map(new Func1<List<T>, List<T>>() {
      @Override public List<T> call(List<T> ts) {
        page = nextPage;
        cache.addAll(ts);
        return cache;
      }
    });
  }

  @Override public boolean hasCache() {
    return cache != null && cache.size() > 0;
  }

  @Override Action1<List<T>> cacheAction() {
    return new Action1<List<T>>() {
      @Override public void call(List<T> ts) {
        if (ts == null || ts.size() == 0) {
          return;
        }
        cache.clear();
        cache.addAll(ts);
      }
    };
  }
}
