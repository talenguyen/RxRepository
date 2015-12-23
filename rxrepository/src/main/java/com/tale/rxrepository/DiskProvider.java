/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/23/15.
 * Copyright (c) 2015 Umbala. All rights reserved.
 */

package com.tale.rxrepository;

import rx.Observable;

public interface DiskProvider<T> extends Provider<T> {
  Observable<T> save(T data);
}
