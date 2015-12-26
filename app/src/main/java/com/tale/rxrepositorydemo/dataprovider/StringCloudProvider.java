/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/25/15.
 * Copyright (c) 2015 Umbala. All rights reserved.
 */

package com.tale.rxrepositorydemo.dataprovider;

import android.os.SystemClock;
import com.tale.rxrepository.CloudProvider;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.functions.Func0;

public class StringCloudProvider implements CloudProvider<List<String>> {

  private static final int PAGE_SIZE = 30;

  @Override public Observable<List<String>> get(final int page) {
    return Observable.defer(new Func0<Observable<List<String>>>() {
      @Override public Observable<List<String>> call() {
        // Mock up loading delay.
        SystemClock.sleep(2000);
        return Observable.just(getData(page));
      }
    });
  }

  public static List<String> getData(int page) {
    if (page == 3) {
      return null;
    }
    final int startIndex = page * PAGE_SIZE;
    final int endIndex = startIndex + PAGE_SIZE;
    final List<String> result = new ArrayList<>(PAGE_SIZE);
    for (int i = startIndex; i < endIndex; i++) {
      result.add("Cloud item: " + i);
    }
    return result;
  }
}
