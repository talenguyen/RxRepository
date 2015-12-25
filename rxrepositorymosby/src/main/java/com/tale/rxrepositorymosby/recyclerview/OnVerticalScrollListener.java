package com.tale.rxrepositorymosby.recyclerview;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class OnVerticalScrollListener extends RecyclerView.OnScrollListener {

  private boolean lastItemVisible;

  @Override public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    if (!recyclerView.canScrollVertically(-1)) {
      onScrolledToTop();
    } else if (!recyclerView.canScrollVertically(1)) {
      if (dy > 0) {
        onScrolledToBottom();
      }
    } else if (dy < 0) {
      onScrolledUp();
      lastItemVisible = isLastItemVisible(recyclerView);
    } else if (dy > 0) {
      onScrolledDown();
      boolean isLastItemVisible = isLastItemVisible(recyclerView);
      if (lastItemVisible != isLastItemVisible) {
        lastItemVisible = isLastItemVisible;
        if (lastItemVisible) {
          onScrolledDownToLastItem();
        }
      }
    }
  }

  private boolean isLastItemVisible(RecyclerView recyclerView) {
    if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
      final int lastVisibleItemPosition =
          ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
      return lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
    }
    return false;
  }

  public void onScrolledDownToLastItem() {
  }

  public void onScrolledUp() {
  }

  public void onScrolledDown() {
  }

  public void onScrolledToTop() {
  }

  public void onScrolledToBottom() {
  }
}