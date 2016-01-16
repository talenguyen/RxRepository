/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/25/15.
 * Copyright (c) 2015 Tale. All rights reserved.
 */

package com.tale.rxrepositorydemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tale.rxrepositorymosby.recyclerview.LoadMoreAdapter;

public class StringAdapter extends LoadMoreAdapter<String> {

  @Override public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
    final View view = LayoutInflater.from(parent.getContext())
        .inflate(android.R.layout.simple_list_item_1, parent, false);
    return new StringVH(view);
  }

  @Override public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
    ((StringVH) holder).bind(getItem(position));
  }

  @Override public RecyclerView.ViewHolder onCreateLoadMoreViewHolder(ViewGroup parent) {
    final View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false);
    return new SimpleViewHolder(view);
  }

  public static class StringVH extends RecyclerView.ViewHolder {

    private final TextView textView;

    public StringVH(View itemView) {
      super(itemView);
      textView = ((TextView) itemView.findViewById(android.R.id.text1));
    }

    public void bind(String item) {
      textView.setText(item);
    }
  }
}