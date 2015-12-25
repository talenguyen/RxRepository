package com.tale.rxrepositorymosby.recyclerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class SupportLoadMoreGridLayoutManager extends GridLayoutManager {

  private SpanSizeLookupWrapper spanSizeLookupWrapper;
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
      if (allVisible) {
        // All visible then we don't need to show load more
        spanSizeLookupWrapper.setLoadMorePosition(-1);
      } else {
        spanSizeLookupWrapper.setLoadMorePosition(adapter.getItemCount() - 1);
      }
      adapter.setEnableLoadMore(!allVisible);
    }
  };;

  public SupportLoadMoreGridLayoutManager(Context context, int spanCount) {
    this(context, spanCount, VERTICAL, false);
  }

  public SupportLoadMoreGridLayoutManager(Context context, int spanCount, int orientation,
      boolean reverseLayout) {
    super(context, spanCount, orientation, reverseLayout);
    spanSizeLookupWrapper = new SpanSizeLookupWrapper(spanCount);
    super.setSpanSizeLookup(spanSizeLookupWrapper);
  }

  @Override public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
    spanSizeLookupWrapper.setPlugIn(spanSizeLookup);
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

  static class SpanSizeLookupWrapper extends SpanSizeLookup {

    private final int spanCount;
    private SpanSizeLookup plugIn;
    private int loadMorePosition = -1;

    public SpanSizeLookupWrapper(int spanCount) {
      this.spanCount = spanCount;
    }

    public void setLoadMorePosition(int loadMorePosition) {
      this.loadMorePosition = loadMorePosition;
    }

    public void setPlugIn(SpanSizeLookup plugIn) {
      this.plugIn = plugIn;
    }

    @Override public void invalidateSpanIndexCache() {
      super.invalidateSpanIndexCache();
      loadMorePosition = -1;
    }

    @Override public int getSpanSize(int position) {
      final int spanSize;
      if (loadMorePosition == position) {
        spanSize = this.spanCount;
        return spanSize;
      } else if (plugIn != null) {
        spanSize = plugIn.getSpanSize(position);
      } else {
        spanSize = 1;
      }
      return spanSize;
    }
  }

}