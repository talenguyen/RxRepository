/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/25/15.
 * Copyright (c) 2015 Umbala. All rights reserved.
 */

package com.tale.rxrepository;

import java.util.List;

public class ListComparator<M> implements Comparator<List<M>> {

  protected boolean isSameItem(M lhs, M rhs) {
    if (lhs == null) {
      return rhs == null;
    } else if (rhs == null) {
      return false;
    } else {
      return lhs.toString().equals(rhs.toString());
    }
  }

  @Override public boolean isSame(List<M> lhs, List<M> rhs) {
    if (lhs == null) {
      return rhs == null;
    } else if (rhs == null) {
      return false;
    } else {
      final M first1 = lhs.size() > 0 ? lhs.get(0) : null;
      final M first2 = rhs.size() > 0 ? rhs.get(0) : null;
      return isSameItem(first1, first2);
    }
  }
}
