/**
 * RxRepository
 *
 * Created by Giang Nguyen on 1/4/16.
 * Copyright (c) 2016 Tale. All rights reserved.
 */

package com.tale.rxrepository;

public interface Comparator<T> {

  boolean isSame(T lhs, T rhs);

}
