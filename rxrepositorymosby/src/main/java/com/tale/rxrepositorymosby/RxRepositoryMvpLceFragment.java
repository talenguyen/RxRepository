/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/25/15.
 * Copyright (c) 2015 Umbala. All rights reserved.
 */

package com.tale.rxrepositorymosby;

import android.view.View;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;
import java.util.List;

public abstract class RxRepositoryMvpLceFragment<CV extends View, M, V extends MvpLcemView<List<M>>, P extends RxRepositoryMvpLcePresenter<M, V>>
    extends MvpLceViewStateFragment<CV, List<M>, V, P>  implements MvpLcemView<List<M>> {

  protected List<M> data;

  @Override public LceViewState<List<M>, V> createViewState() {
    return new RetainingLceViewState<>();
  }

  @Override public List<M> getData() {
    return data;
  }

  @Override public void setData(List<M> data) {
    this.data = data;
  }

  @Override public void loadData(boolean pullToRefresh) {
    if (pullToRefresh) {
      presenter.refresh();
    } else {
      presenter.loadData();
    }
  }

  public void loadMore() {
    presenter.loadMore();
  }
}
