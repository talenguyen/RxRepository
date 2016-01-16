/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/25/15.
 * Copyright (c) 2015 Tale. All rights reserved.
 */

package com.tale.rxrepositorymosby;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

public interface MvpLcemView<M> extends MvpLceView<M> {
  /**
   * Get the data that has been set before in {@link #setData(Object)}
   * <p>
   * <b>It's necessary to return the same data as set before to ensure that {@link ViewState} works
   * correctly</b>
   * </p>
   *
   * @return The data
   */
  M getData();

  /**
   * Notify when load more but no more data. Should handle to hide load more indicator.
   */
  void onNoMore();
}
