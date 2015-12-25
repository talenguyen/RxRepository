/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/25/15.
 * Copyright (c) 2015 Umbala. All rights reserved.
 */

package com.tale.rxrepositorymosby;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

public interface MvpLcemView<M> extends MvpLceView<M> {
  /**
   * Notify when load more but no more data. Should handle to hide load more indicator.
   */
  void onNoMore();
}
