package com.tale.rxrepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import org.junit.Test;
import rx.Observable;
import rx.functions.Func0;
import rx.observers.TestObserver;

/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/24/15.
 * Copyright (c) 2015 Umbala. All rights reserved.
 */
public class RepositoryTest {

  private static final String DISK = "DISK";
  private static final String CLOUD = "CLOUD";
  private static final String CLOUD_1 = "CLOUD_1";
  private static final String CLOUD_2 = "CLOUD_2";
  Repository<String> repository;

  @Test public void testGet1() throws Exception {
    DiskProvider<String> mockDisk = getDiskProvider(DISK);

    CloudProvider<String> mockCloudProvider = getCloudProvider(CLOUD);

    repository = new Repository<>(mockDisk, mockCloudProvider, getStringComparator());

    TestObserver<String> testObserver;
    // First time. Expect receive both disk and cloud.
    testObserver = new TestObserver<>();
    repository.get().subscribe(testObserver);
    testObserver.assertReceivedOnNext(Arrays.asList(DISK, CLOUD));

    // Second time. Expect receive only cache.
    testObserver = new TestObserver<>();
    repository.get().subscribe(testObserver);
    testObserver.assertReceivedOnNext(Collections.singletonList(CLOUD));
  }


  @Test public void testGet2() throws Exception {
    DiskProvider<String> mockDisk = getDiskProvider(DISK);

    CloudProvider<String> mockCloudProvider = getCloudChangeProvider(CLOUD_1, CLOUD_2);

    repository = new Repository<>(mockDisk, mockCloudProvider, getStringComparator());

    TestObserver<String> testObserver;
    // First time. Expect receive both disk and cloud.
    testObserver = new TestObserver<>();
    repository.get().subscribe(testObserver);
    testObserver.assertReceivedOnNext(Arrays.asList(DISK, CLOUD_1));

    // Second time. Expect receive both cache and cloud.
    testObserver = new TestObserver<>();
    repository.get().subscribe(testObserver);
    testObserver.assertReceivedOnNext(Arrays.asList(CLOUD_1, CLOUD_2));
  }

  private Comparator<String> getStringComparator() {
    return new Comparator<String>() {
      @Override public int compare(String o1, String o2) {
        if (o1 == null) {
          if (o2 == null) {
            return 0;
          } else {
            return -1;
          }
        } else {
          return o1.equals(o2) ? 0 : 1;
        }
      }
    };
  }

  private CloudProvider<String> getCloudChangeProvider(final String data1, final String data2) {
    return new CloudProvider<String>() {
      boolean first = true;
      @Override public Observable<String> get() {
        return Observable.defer(new Func0<Observable<String>>() {
          @Override public Observable<String> call() {
            if (first) {
              first = false;
              return Observable.just(data1);
            } else {
              first = true;
              return Observable.just(data2);
            }
          }
        });
      }
    };
  }


  private CloudProvider<String> getCloudProvider(final String data) {
    return new CloudProvider<String>() {
      @Override public Observable<String> get() {
        return Observable.defer(new Func0<Observable<String>>() {
          @Override public Observable<String> call() {
            return Observable.just(data);
          }
        });
      }
    };
  }

  private DiskProvider<String> getDiskProvider(final String defaultData) {
    return new DiskProvider<String>() {
      String data = defaultData;

      @Override public Observable<String> save(final String data) {
        this.data = data;
        return get();
      }

      @Override public Observable<String> get() {
        return Observable.defer(new Func0<Observable<String>>() {
          @Override public Observable<String> call() {
            return Observable.just(data);
          }
        });
      }
    };
  }

}