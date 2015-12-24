package com.tale.rxrepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import rx.Observable;
import rx.functions.Func0;
import rx.observers.TestSubscriber;

/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/24/15.
 * Copyright (c) 2015 Umbala. All rights reserved.
 */
public class ListRepositoryTest {

  private static final List<String> DISK;
  private static final List<String> CLOUD_1;
  private static final List<String> CLOUD_2;

  static {
    DISK = new ArrayList<>();
    DISK.add("Disk 1");
    DISK.add("Disk 2");

    CLOUD_1 = new ArrayList<>();
    CLOUD_1.add("Cloud 1");
    CLOUD_1.add("Cloud 2");

    CLOUD_2 = new ArrayList<>();
    CLOUD_2.add("Cloud 3");
    CLOUD_2.add("Cloud 4");
  }

  @Test public void testRefresh() throws Exception {
    final DiskProvider<List<String>> disk = disk();
    final CloudProvider<List<String>> cloud = cloud();
    final Comparator<List<String>> comparator = comparator();
    final ListRepository<String> repository =
        new ListRepository<>(disk, cloud, comparator);

    // Refresh the first
    TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();
    repository.refresh().subscribe(testSubscriber);
    testSubscriber.assertValueCount(1);
    testSubscriber.assertValue(CLOUD_1);

    // Refresh the second. Expect no onNext because the first is cached.
    testSubscriber = new TestSubscriber<>();
    repository.refresh().subscribe(testSubscriber);
    testSubscriber.assertValueCount(0);
  }

  @Test public void testMore() throws Exception {
    final DiskProvider<List<String>> disk = disk();
    final CloudProvider<List<String>> cloud = cloud();
    final Comparator<List<String>> comparator = comparator();
    final ListRepository<String> repository =
        new ListRepository<>(disk, cloud, comparator);

    // Get. Expect 2 values, one from disk and one from cloud.
    TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();
    repository.get().subscribe(testSubscriber);
    testSubscriber.assertValueCount(2);
    testSubscriber.assertValues(DISK, CLOUD_1);

    // Load more. Expect 1 from cloud
    testSubscriber = new TestSubscriber<>();
    repository.more().subscribe(testSubscriber);
    testSubscriber.assertValueCount(1);
    final List<String> result = testSubscriber.getOnNextEvents().get(0);
    Assert.assertEquals(4, result.size());
    final String first = result.get(0);
    Assert.assertEquals(CLOUD_1.get(0), first);
    final String last = result.get(3);
    Assert.assertEquals(CLOUD_2.get(1), last);

  }

  Comparator<List<String>> comparator() {
    return new Comparator<List<String>>() {
      @Override public int compare(List<String> o1, List<String> o2) {
        if (o1 == null) {
          if (o2 == null) {
            return 0;
          } else {
            return -1;
          }
        } else {
          if (o2 == null) {
            return 1;
          } else {
            final int size1 = o1.size();
            final int size2 = o2.size();
            if (size1 == size2) {
              final String first1 = o1.size() > 0 ? o1.get(0) : null;
              final String first2 = o2.size() > 0 ? o2.get(0) : null;
              if (first1 == null) {
                if (first2 == null) {
                  return 0;
                } else {
                  return -1;
                }
              } else {
                return first1.equals(first2) ? 0 : 1;
              }
            } else {
              return 1;
            }
          }
        }
      }
    };
  }
  CloudProvider<List<String>> cloud() {
    return new CloudProvider<List<String>>() {
      @Override public Observable<List<String>> get(int page) {
        if (page == 0) {
          return Observable.defer(new Func0<Observable<List<String>>>() {
            @Override public Observable<List<String>> call() {
              return Observable.just(CLOUD_1);
            }
          });
        } else {
          return Observable.defer(new Func0<Observable<List<String>>>() {
            @Override public Observable<List<String>> call() {
              return Observable.just(CLOUD_2);
            }
          });
        }
      }
    };
  }
  DiskProvider<List<String>> disk() {
    return new DiskProvider<List<String>>() {
      List<String> data = DISK;
      @Override public Observable<List<String>> get() {
        return Observable.defer(new Func0<Observable<List<String>>>() {
          @Override public Observable<List<String>> call() {
            return Observable.just(data);
          }
        });
      }

      @Override public Observable<List<String>> save(List<String> data) {
        this.data = data;
        return get();
      }
    };
  }

}