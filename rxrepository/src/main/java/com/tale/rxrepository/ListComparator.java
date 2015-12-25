/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/25/15.
 * Copyright (c) 2015 Umbala. All rights reserved.
 */

package com.tale.rxrepository;

import java.util.Comparator;
import java.util.List;

public class ListComparator<M> implements Comparator<List<M>> {
  @Override public int compare(List<M> o1, List<M> o2) {
    if (o1 == null) {
      if (o2 == null) {
        return 0;
      }
      return -1;
    }
    if (o2 == null) {
      return 1;
    }

    final int size1 = o1.size();
    final int size2 = o2.size();
    if (size1 > size2) {
      return 1;
    } else if (size2 > size1) {
      return -1;
    } else if (size1 == 0) {
      return 0;
    } else {
      final M m1 = o1.get(0);
      final M m2 = o2.get(0);
      return m1.equals(m2) ? 0 : 1;
    }
  }
}
