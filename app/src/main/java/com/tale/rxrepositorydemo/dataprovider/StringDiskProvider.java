/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/25/15.
 * Copyright (c) 2015 Umbala. All rights reserved.
 */

package com.tale.rxrepositorydemo.dataprovider;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.functions.Func0;

public class StringDiskProvider implements com.tale.rxrepository.DiskProvider<List<String>> {
  public static final List<String> DEFAULT;
  static {
    DEFAULT = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      DEFAULT.add("Disk item: " + i);
    }
  }
  private List<String> data = DEFAULT;

  @Override public Observable<List<String>> get() {
    return Observable.defer(new Func0<Observable<List<String>>>() {
      @Override public Observable<List<String>> call() {
        // Mock up loading delay.
        SystemClock.sleep(500);
        return Observable.just(data);
      }
    });
  }

  @Override public Observable<List<String>> save(final List<String> data) {
    this.data = data;
    return Observable.defer(new Func0<Observable<List<String>>>() {
      @Override public Observable<List<String>> call() {
        return Observable.just(data);
      }
    });
  }
}
