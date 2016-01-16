/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/23/15.
 * Copyright (c) 2015 Tale. All rights reserved.
 */

package com.tale.rxrepository;

import rx.Observable;

public interface DiskProvider<T> {
  Observable<T> get();
  Observable<T> save(T data);
}
