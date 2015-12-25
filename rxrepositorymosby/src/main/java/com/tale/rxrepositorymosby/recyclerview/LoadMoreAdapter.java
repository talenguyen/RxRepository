package com.tale.rxrepositorymosby.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public abstract class LoadMoreAdapter<T>
    extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public static final int VIEW_TYPE_LOAD_MORE = 1;

  private List<T> items = new ArrayList<>();
  private boolean enableLoadMore;

  public void setEnableLoadMore(boolean enableLoadMore) {
    if (this.enableLoadMore == enableLoadMore) {
      return;
    }
    this.enableLoadMore = enableLoadMore;
    notifyDataSetChanged();
  }

  public void setItems(List<T> items) {
    this.items.clear();
    addItems(items);
  }

  public void addItems(List<T> items) {
    this.items.addAll(items);
  }

  public T getItem(int position) {
    if (position < 0 || position > items.size() - 1) {
      return null;
    }
    return items.get(position);
  }

  @Override public int getItemCount() {
    return items == null ? 0 : items.size() + (enableLoadMore ? 1 : 0);
  }

  @Override public int getItemViewType(int position) {
    if (enableLoadMore && position == getItemCount() - 1) {
      return VIEW_TYPE_LOAD_MORE;
    }
    return super.getItemViewType(position);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == VIEW_TYPE_LOAD_MORE) {
      return onCreateLoadMoreViewHolder(parent);
    }
    return onCreateItemViewHolder(parent, viewType);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (!enableLoadMore || position != getItemCount() - 1) {
      onBindItemViewHolder(holder, position);
    }
  }

  public boolean isEnableLoadMore() {
    return enableLoadMore;
  }

  public abstract RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType);

  public abstract void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position);

  public abstract RecyclerView.ViewHolder onCreateLoadMoreViewHolder(ViewGroup parent);

}