package com.tale.rxrepository;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.functions.Func0;
import rx.observers.TestSubscriber;

/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/24/15.
 * Copyright (c) 2015 Tale. All rights reserved.
 */
public class RepositoryTest {

  private static final String DISK = "DISK";
  private static final String CLOUD = "CLOUD";
  private static final String CLOUD_1 = "CLOUD_1";
  private static final String CLOUD_2 = "CLOUD_2";
  Repository<String> repository;
  private NetworkVerifier networkVerifier;

  @Before public void setUp() throws Exception {
    networkVerifier = new NetworkVerifier() {
      @Override public boolean isConnected() {
        return true;
      }
    };
  }

  @Test public void testGet_DiskNext_NetworkNext() throws Exception {
    DiskProvider<String> mockDisk = getDiskProvider(DISK);

    CloudProvider<String> mockCloudProvider = getCloudProvider(CLOUD);

    repository = new Repository<>(mockDisk, mockCloudProvider, getStringComparator(),
        networkVerifier);
    TestSubscriber<String> testSubscriber = new TestSubscriber<>();
    repository.get().subscribe(testSubscriber);
    testSubscriber.assertReceivedOnNext(Arrays.asList(DISK, CLOUD));
    testSubscriber.assertCompleted();
  }

  @Test public void testGet_DiskNext_NetworkError() throws Exception {
    DiskProvider<String> mockDisk = getDiskProvider(DISK);

    CloudProvider<String> mockCloudProvider = getCloudProviderError();

    repository = new Repository<>(mockDisk, mockCloudProvider, getStringComparator(),
        networkVerifier);
    TestSubscriber<String> testSubscriber = new TestSubscriber<>();
    repository.get().subscribe(testSubscriber);
    testSubscriber.assertReceivedOnNext(Collections.singletonList(DISK));
    testSubscriber.assertError(NullPointerException.class);
    testSubscriber.assertNotCompleted();
  }



  @Test public void testGet_DiskNext_NetworkEmpty() throws Exception {
    DiskProvider<String> mockDisk = getDiskProvider(DISK);

    CloudProvider<String> mockCloudProvider = getCloudProviderEmpty();

    repository = new Repository<>(mockDisk, mockCloudProvider, getStringComparator(),
        networkVerifier);
    TestSubscriber<String> testSubscriber = new TestSubscriber<>();
    repository.get().subscribe(testSubscriber);
    testSubscriber.assertReceivedOnNext(Collections.singletonList(DISK));
    testSubscriber.assertNoErrors();
    testSubscriber.assertCompleted();
  }


  @Test public void testGet_DiskError_NetworkNext() throws Exception {
    DiskProvider<String> mockDisk = getDiskProviderError();

    CloudProvider<String> mockCloudProvider = getCloudProvider(CLOUD);

    repository = new Repository<>(mockDisk, mockCloudProvider, getStringComparator(),
        networkVerifier);
    TestSubscriber<String> testSubscriber = new TestSubscriber<>();
    repository.get().subscribe(testSubscriber);
    testSubscriber.assertReceivedOnNext(Collections.singletonList(CLOUD));
    testSubscriber.assertNoErrors();
    testSubscriber.assertCompleted();
  }

  @Test public void testGet_DiskError_NetworkError() throws Exception {
    DiskProvider<String> mockDisk = getDiskProviderError();

    CloudProvider<String> mockCloudProvider = getCloudProviderError();

    repository = new Repository<>(mockDisk, mockCloudProvider, getStringComparator(),
        networkVerifier);
    TestSubscriber<String> testSubscriber = new TestSubscriber<>();
    repository.get().subscribe(testSubscriber);
    testSubscriber.assertNoValues();
    testSubscriber.assertError(NullPointerException.class);
    testSubscriber.assertNotCompleted();
  }

  @Test public void testGet_DiskError_NetworkEmpty() throws Exception {
    DiskProvider<String> mockDisk = getDiskProviderError();


    CloudProvider<String> mockCloudProvider = getCloudProviderEmpty();
    repository = new Repository<>(mockDisk, mockCloudProvider, getStringComparator(),
        networkVerifier);
    TestSubscriber<String> testSubscriber = new TestSubscriber<>();
    repository.get().subscribe(testSubscriber);
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
    testSubscriber.assertCompleted();
  }

  @Test public void testGet_DiskNext_CloudNext_Twice() throws Exception {
    DiskProvider<String> mockDisk = getDiskProvider(DISK);

    CloudProvider<String> mockCloudProvider = getCloudProvider(CLOUD);

    repository = new Repository<>(mockDisk, mockCloudProvider, getStringComparator(),
        networkVerifier);

    TestSubscriber<String> testObserver;
    // First time. Expect receive both disk and cloud.
    testObserver = new TestSubscriber<>();
    repository.get().subscribe(testObserver);
    testObserver.assertReceivedOnNext(Arrays.asList(DISK, CLOUD));

    // Second time. Expect receive only cache.
    testObserver = new TestSubscriber<>();
    repository.get().subscribe(testObserver);
    testObserver.assertReceivedOnNext(Collections.singletonList(CLOUD));
  }


  @Test public void testGet_DiskNext_CloudNext_CloudChange() throws Exception {
    DiskProvider<String> mockDisk = getDiskProvider(DISK);

    CloudProvider<String> mockCloudProvider = getCloudChangeProvider(CLOUD_1, CLOUD_2);

    repository = new Repository<>(mockDisk, mockCloudProvider, getStringComparator(),
        networkVerifier);

    TestSubscriber<String> testObserver;
    // First time. Expect receive both disk and cloud.
    testObserver = new TestSubscriber<>();
    repository.get().subscribe(testObserver);
    testObserver.assertReceivedOnNext(Arrays.asList(DISK, CLOUD_1));

    // Second time. Expect receive both cache and cloud.
    testObserver = new TestSubscriber<>();
    repository.get().subscribe(testObserver);
    testObserver.assertReceivedOnNext(Arrays.asList(CLOUD_1, CLOUD_2));
  }



  private Comparator<String> getStringComparator() {
    return new Comparator<String>() {
      @Override public boolean isSame(String lhs, String rhs) {
        if (lhs == null) {
          return rhs == null;
        }
        return rhs != null && lhs.equals(rhs);
      }
    };
  }

  private CloudProvider<String> getCloudChangeProvider(final String data1, final String data2) {
    return new CloudProvider<String>() {
      boolean first = true;
      @Override public Observable<String> get(int page) {
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

  private DiskProvider<String> getDiskProviderError() {
    return new DiskProvider<String>() {
      @Override public Observable<String> get() {
        return Observable.error(new NullPointerException());
      }

      @Override public Observable<String> save(String data) {
        return Observable.just(data);
      }
    };
  }

  private CloudProvider<String> getCloudProviderError() {
    return new CloudProvider<String>() {
      @Override public Observable<String> get(int page) {
        return Observable.error(new NullPointerException());
      }
    };
  }
  private CloudProvider<String> getCloudProviderEmpty() {
    return new CloudProvider<String>() {
      @Override public Observable<String> get(int page) {
        return Observable.empty();
      }
    };
  }
  private CloudProvider<String> getCloudProvider(final String data) {
    return new CloudProvider<String>() {
      @Override public Observable<String> get(int page) {
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