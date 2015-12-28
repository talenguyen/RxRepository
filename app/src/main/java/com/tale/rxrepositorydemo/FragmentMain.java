/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/25/15.
 * Copyright (c) 2015 Umbala. All rights reserved.
 */

package com.tale.rxrepositorydemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tale.rxrepository.ListComparator;
import com.tale.rxrepository.ListRepository;
import com.tale.rxrepositorydemo.dataprovider.StringCloudProvider;
import com.tale.rxrepositorydemo.dataprovider.StringDiskProvider;
import com.tale.rxrepositorymosby.MvpLcemView;
import com.tale.rxrepositorymosby.RxRepositoryMvpLcePresenter;
import com.tale.rxrepositorymosby.recyclerview.LoadMoreAdapter;
import com.tale.rxrepositorymosby.recyclerview.RecyclerFragment;
import com.tale.rxrepositorymosby.recyclerview.SupportLoadMoreGridLayoutManager;
import java.util.List;
import java.util.NoSuchElementException;
import rx.functions.Func1;

public class FragmentMain extends
    RecyclerFragment<String, String, MvpLcemView<List<String>>, RxRepositoryMvpLcePresenter<String, String, MvpLcemView<List<String>>>> {

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.content_main, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    recyclerView.setHasFixedSize(true);
  }

  @Override protected RecyclerView.LayoutManager createLayoutManager() {
    return new SupportLoadMoreGridLayoutManager(getContext(), 2);
  }

  @Override protected LoadMoreAdapter<String> createAdapter() {
    return new StringAdapter();
  }

  @Override protected int getLoadMoreHeight() {
    return getResources().getDimensionPixelSize(R.dimen.load_more_height);
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    if (e instanceof NoSuchElementException) {
      return "No data";
    }
    return "Error: try again";
  }

  @Override
  public RxRepositoryMvpLcePresenter<String, String, MvpLcemView<List<String>>> createPresenter() {
    return new RxRepositoryMvpLcePresenter<String, String, MvpLcemView<List<String>>>(
        new ListRepository<>(new StringDiskProvider(), new StringCloudProvider(),
            new ListComparator<String>())) {
      @NonNull @Override protected Func1<List<String>, List<String>> mapFunction() {
        return new Func1<List<String>, List<String>>() {
          @Override public List<String> call(List<String> list) {
            return list;
          }
        };
      }
    };
  }
}
