package com.tale.rxrepositorymosby.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class SupportLoadMoreLinearLayoutManager extends LinearLayoutManager {

  private LoadMoreAdapter adapter;
  private Runnable computeLoadMore = new Runnable() {
    @Override public void run() {
      final int firstCompletelyVisibleItemPosition = findFirstCompletelyVisibleItemPosition();
      final int lastCompletelyVisibleItemPosition = findLastCompletelyVisibleItemPosition();
      if (firstCompletelyVisibleItemPosition < 0 || lastCompletelyVisibleItemPosition < 0) {
        return;
      }

      final boolean allVisible = firstCompletelyVisibleItemPosition == 0
          && lastCompletelyVisibleItemPosition == adapter.getItemCount() - 1;
      adapter.setEnableLoadMore(!allVisible);
    }
  };

  public SupportLoadMoreLinearLayoutManager(Context context, int orientation,
      boolean reverseLayout) {
    super(context, orientation, reverseLayout);
  }

  @Override public void onItemsChanged(final RecyclerView recyclerView) {
    super.onItemsChanged(recyclerView);
    final RecyclerView.Adapter adapter = recyclerView.getAdapter();
    if (!(adapter instanceof LoadMoreAdapter)) {
      throw new IllegalArgumentException("adapter must be instance of LoadMoreAdapter");
    }
    this.adapter = (LoadMoreAdapter) adapter;
    // Remove the old to keep only one callback execute.
    recyclerView.removeCallbacks(computeLoadMore);
    recyclerView.post(computeLoadMore);
  }

}