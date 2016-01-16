/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/25/15.
 * Copyright (c) 2015 Tale. All rights reserved.
 */

package com.tale.rxrepositorymosby.recyclerview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.tale.rxrepositorymosby.MvpLcemView;
import com.tale.rxrepositorymosby.R;
import com.tale.rxrepositorymosby.RxRepositoryMvpLceFragment;
import com.tale.rxrepositorymosby.RxRepositoryMvpLcePresenter;
import java.util.List;

public abstract class RecyclerFragment<BM, M, V extends MvpLcemView<List<M>>, P extends RxRepositoryMvpLcePresenter<BM, M, V>>
    extends RxRepositoryMvpLceFragment<SwipeRefreshLayout, BM, M, V, P>
    implements SwipeRefreshLayout.OnRefreshListener {

  protected RecyclerView recyclerView;
  protected LoadMoreAdapter<M> adapter;

  /**
   * Create LayoutManager. <b>NOTE:</b> the LayoutManager must be one of
   * {@link SupportLoadMoreGridLayoutManager} or {@link SupportLoadMoreLinearLayoutManager} to
   * support load more feature.
   *
   * @return {@link SupportLoadMoreGridLayoutManager} or {@link SupportLoadMoreLinearLayoutManager}
   */
  protected abstract RecyclerView.LayoutManager createLayoutManager();

  /**
   * Create adapter for {@link RecyclerView}
   * @return {@link LoadMoreAdapter} object.
   */
  protected abstract LoadMoreAdapter<M> createAdapter();

  /**
   * Return height of load more view then Recycler know how to scroll to hide load more view.
   * @return height of load more view.
   */
  protected abstract int getLoadMoreHeight();

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override protected void animateLoadingViewIn() {
    super.animateLoadingViewIn();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    final RecyclerView.LayoutManager layoutManager = createLayoutManager();
    verifyLayoutManager(layoutManager);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.addOnScrollListener(new OnVerticalScrollListener() {
      @Override public void onScrolledDownToLastItem() {
        super.onScrolledDownToLastItem();
        loadMore();
      }

      @Override public void onScrolledToBottom() {
        super.onScrolledToBottom();
        loadMore();
      }
    });
    adapter = createAdapter();
    recyclerView.setAdapter(adapter);
    contentView.setOnRefreshListener(this);
  }

  @Override public void onNoMore() {
    recyclerView.smoothScrollBy(0, -getLoadMoreHeight());
  }

  @Override public void onRefresh() {
    loadData(true);
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    super.showError(e, pullToRefresh);
    contentView.setRefreshing(false);
    e.printStackTrace();
  }

  @Override public void showContent() {
    super.showContent();
    contentView.setRefreshing(false);
  }

  @Override public void showLoading(boolean pullToRefresh) {
    super.showLoading(pullToRefresh);
    if (pullToRefresh && !contentView.isRefreshing()) {
      // Workaround for measure bug: https://code.google.com/p/android/issues/detail?id=77712
      contentView.post(new Runnable() {
        @Override public void run() {
          contentView.setRefreshing(true);
        }
      });
    }
  }

  @Override public void setData(List<M> data) {
    super.setData(data);
    adapter.setItems(data);
    adapter.notifyDataSetChanged();
  }

  private void verifyLayoutManager(RecyclerView.LayoutManager layoutManager) {
    if (!(layoutManager instanceof SupportLoadMoreGridLayoutManager)
        && !(layoutManager instanceof SupportLoadMoreLinearLayoutManager)) {
      throw new IllegalArgumentException(
          "layout manager must be SupportLoadMoreGridLayoutManager or SupportLoadMoreLinearLayoutManager");
    }
  }

}
