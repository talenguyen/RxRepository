package com.tale.rxrepositorymosby;

import android.support.annotation.NonNull;
import com.tale.rxrepository.CloudProvider;
import com.tale.rxrepository.DiskProvider;
import com.tale.rxrepository.ListComparator;
import com.tale.rxrepository.ListRepository;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.functions.Func1;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/26/15.
 * Copyright (c) 2015 Umbala. All rights reserved.
 */
public class RxRepositoryMvpLcePresenterTest {

  private static final Object OBJECT_1 = new Object();
  private static final Object OBJECT_2 = new Object();
  private static final Object OBJECT_3 = new Object();
  private static final Object OBJECT_4 = new Object();

  @Mock MvpLcemView<List<Object>> view;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test public void testLoadData_onNext_onNext_onCompleted() throws Exception {
    final ListRepository<Object> repository =
        new ListRepository<>(diskProvider(), cloudProvider(), new ListComparator<>());
    final RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>
        presenter =
        new RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>(repository) {
          @NonNull @Override protected Func1<List<Object>, List<Object>> mapFunction() {
            return new Func1<List<Object>, List<Object>>() {
              @Override public List<Object> call(List<Object> objects) {
                return objects;
              }
            };
          }

          @Override boolean test() {
            return true;
          }
        };

    presenter.attachView(view);
    presenter.loadData();
    Mockito.verify(view, Mockito.times(2)).setData(Mockito.anyList());
    Mockito.verify(view, Mockito.times(2)).showContent();
  }


  @Test public void testLoadData_onNext_onNext_loadData_onCompleted() throws Exception {
    final ListRepository<Object> repository =
        new ListRepository<>(diskProvider(), cloudProvider(), new ListComparator<>());
    RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>
        presenter =
        new RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>(repository) {
          @NonNull @Override protected Func1<List<Object>, List<Object>> mapFunction() {
            return new Func1<List<Object>, List<Object>>() {
              @Override public List<Object> call(List<Object> objects) {
                return objects;
              }
            };
          }

          @Override boolean test() {
            return true;
          }
        };

    presenter.attachView(view);
    presenter.loadData();
    Mockito.verify(view, Mockito.times(1)).showLoading(any(Boolean.class));

    presenter =  new RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>(repository) {
      @NonNull @Override protected Func1<List<Object>, List<Object>> mapFunction() {
        return new Func1<List<Object>, List<Object>>() {
          @Override public List<Object> call(List<Object> objects) {
            return objects;
          }
        };
      }

      @Override boolean test() {
        return true;
      }
    };
    // New view and new presenter but use same repository will share same cache items. We expect the view will not show loading if has cached.
    MvpLcemView view = Mockito.mock(MvpLcemView.class);
    presenter.attachView(view);
    presenter.loadData();
    Mockito.verify(view, Mockito.never()).showLoading(any(Boolean.class));
  }

  @Test public void testLoadData_onNext_onError_onCompleted() throws Exception {
    final ListRepository<Object> repository =
        new ListRepository<>(diskProvider(), cloudProviderError(), new ListComparator<>());
    final RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>
        presenter =
        new RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>(repository) {
          @NonNull @Override protected Func1<List<Object>, List<Object>> mapFunction() {
            return new Func1<List<Object>, List<Object>>() {
              @Override public List<Object> call(List<Object> objects) {
                return objects;
              }
            };
          }

          @Override boolean test() {
            return true;
          }
        };

    presenter.attachView(view);
    presenter.loadData();
    Mockito.verify(view, Mockito.times(1)).setData(Mockito.anyList());
    Mockito.verify(view, Mockito.times(1)).showContent();
    Mockito.verify(view, Mockito.never()).showError(any(Throwable.class), any(Boolean.class));
  }

  @Test public void testLoadData_onNext_Empty_onCompleted() throws Exception {
    final ListRepository<Object> repository =
        new ListRepository<>(diskProvider(), cloudProviderEmpty(), new ListComparator<>());
    final RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>
        presenter =
        new RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>(repository) {
          @NonNull @Override protected Func1<List<Object>, List<Object>> mapFunction() {
            return new Func1<List<Object>, List<Object>>() {
              @Override public List<Object> call(List<Object> objects) {
                return objects;
              }
            };
          }

          @Override boolean test() {
            return true;
          }
        };

    presenter.attachView(view);
    presenter.loadData();
    Mockito.verify(view, Mockito.times(1)).setData(Mockito.anyList());
    Mockito.verify(view, Mockito.times(1)).showContent();
    Mockito.verify(view, Mockito.never()).showError(any(Throwable.class), any(Boolean.class));
  }

  @Test public void testLoadData_Empty_onNext_onCompleted() throws Exception {
    final ListRepository<Object> repository =
        new ListRepository<>(diskProviderEmpty(), cloudProvider(), new ListComparator<>());
    final RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>
        presenter =
        new RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>(repository) {
          @NonNull @Override protected Func1<List<Object>, List<Object>> mapFunction() {
            return new Func1<List<Object>, List<Object>>() {
              @Override public List<Object> call(List<Object> objects) {
                return objects;
              }
            };
          }

          @Override boolean test() {
            return true;
          }
        };

    presenter.attachView(view);
    presenter.loadData();
    Mockito.verify(view, Mockito.times(1)).setData(Mockito.anyList());
    Mockito.verify(view, Mockito.times(1)).showContent();
    Mockito.verify(view, Mockito.never()).showError(any(Throwable.class), any(Boolean.class));
  }

  @Test public void testLoadData_Empty_onError_onCompleted() throws Exception {
    final ListRepository<Object> repository =
        new ListRepository<>(diskProviderEmpty(), cloudProviderError(), new ListComparator<>());
    final RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>
        presenter =
        new RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>(repository) {
          @NonNull @Override protected Func1<List<Object>, List<Object>> mapFunction() {
            return new Func1<List<Object>, List<Object>>() {
              @Override public List<Object> call(List<Object> objects) {
                return objects;
              }
            };
          }

          @Override boolean test() {
            return true;
          }
        };

    presenter.attachView(view);
    presenter.loadData();
    Mockito.verify(view, Mockito.never()).setData(Mockito.anyList());
    Mockito.verify(view, Mockito.never()).showContent();
    Mockito.verify(view, Mockito.times(1)).showError(any(Throwable.class), eq(false));
  }

  @Test public void testLoadData_Empty_Empty_onCompleted() throws Exception {
    final ListRepository<Object> repository =
        new ListRepository<>(diskProviderEmpty(), cloudProviderEmpty(), new ListComparator<>());
    final RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>
        presenter =
        new RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>(repository) {
          @NonNull @Override protected Func1<List<Object>, List<Object>> mapFunction() {
            return new Func1<List<Object>, List<Object>>() {
              @Override public List<Object> call(List<Object> objects) {
                return objects;
              }
            };
          }

          @Override boolean test() {
            return true;
          }
        };

    presenter.attachView(view);
    presenter.loadData();
    Mockito.verify(view, Mockito.never()).setData(Mockito.anyList());
    Mockito.verify(view, Mockito.never()).showContent();
    Mockito.verify(view, Mockito.times(1)).showError(any(NoSuchElementException.class), eq(false));
  }

  @Test public void testRefresh_onNext_onCompleted() throws Exception {
    final ListRepository<Object> repository =
        new ListRepository<>(diskProviderEmpty(), cloudProvider(), new ListComparator<>());
    final RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>
        presenter =
        new RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>(repository) {
          @NonNull @Override protected Func1<List<Object>, List<Object>> mapFunction() {
            return new Func1<List<Object>, List<Object>>() {
              @Override public List<Object> call(List<Object> objects) {
                return objects;
              }
            };
          }

          @Override boolean test() {
            return true;
          }
        };

    presenter.attachView(view);
    presenter.refresh();
    Mockito.verify(view, Mockito.times(1)).setData(Mockito.anyList());
    Mockito.verify(view, Mockito.times(2)).showContent();
    Mockito.verify(view, Mockito.never()).showError(any(Throwable.class), any(Boolean.class));
  }

  @Test public void testRefresh_onError_onCompleted() throws Exception {
    final ListRepository<Object> repository =
        new ListRepository<>(diskProviderEmpty(), cloudProviderError(), new ListComparator<>());
    final RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>
        presenter =
        new RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>(repository) {
          @NonNull @Override protected Func1<List<Object>, List<Object>> mapFunction() {
            return new Func1<List<Object>, List<Object>>() {
              @Override public List<Object> call(List<Object> objects) {
                return objects;
              }
            };
          }

          @Override boolean test() {
            return true;
          }
        };

    presenter.attachView(view);
    presenter.refresh();
    Mockito.verify(view, Mockito.never()).setData(Mockito.anyList());
    Mockito.verify(view, Mockito.times(1)).showContent();
    Mockito.verify(view, Mockito.times(1)).showError(any(Throwable.class), eq(true));
  }
  @Test public void testRefresh_onEmpty_onCompleted() throws Exception {
    final ListRepository<Object> repository =
        new ListRepository<>(diskProviderEmpty(), cloudProviderEmpty(), new ListComparator<>());
    final RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>
        presenter =
        new RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>(repository) {
          @NonNull @Override protected Func1<List<Object>, List<Object>> mapFunction() {
            return new Func1<List<Object>, List<Object>>() {
              @Override public List<Object> call(List<Object> objects) {
                return objects;
              }
            };
          }

          @Override boolean test() {
            return true;
          }
        };

    presenter.attachView(view);
    presenter.refresh();
    Mockito.verify(view, Mockito.never()).setData(Mockito.anyList());
    Mockito.verify(view, Mockito.times(1)).showContent();
    Mockito.verify(view, Mockito.never()).showError(any(Throwable.class), any(Boolean.class));
  }

  @Test public void testLoadMore_onNext_onCompleted() throws Exception {
    final ListRepository<Object> repository =
        new ListRepository<>(diskProviderEmpty(), cloudProvider(), new ListComparator<>());
    final RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>
        presenter =
        new RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>(repository) {
          @NonNull @Override protected Func1<List<Object>, List<Object>> mapFunction() {
            return new Func1<List<Object>, List<Object>>() {
              @Override public List<Object> call(List<Object> objects) {
                return objects;
              }
            };
          }

          @Override boolean test() {
            return true;
          }
        };

    presenter.attachView(view);
    presenter.loadMore();
    Mockito.verify(view, Mockito.times(1)).setData(Mockito.anyList());
    Mockito.verify(view, Mockito.times(1)).showContent();
    Mockito.verify(view, Mockito.never()).showError(any(Throwable.class), any(Boolean.class));
  }

  @Test public void testLoadMore_onError_onCompleted() throws Exception {
    final ListRepository<Object> repository =
        new ListRepository<>(diskProviderEmpty(), cloudProviderError(), new ListComparator<>());
    final RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>
        presenter =
        new RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>(repository) {
          @NonNull @Override protected Func1<List<Object>, List<Object>> mapFunction() {
            return new Func1<List<Object>, List<Object>>() {
              @Override public List<Object> call(List<Object> objects) {
                return objects;
              }
            };
          }

          @Override boolean test() {
            return true;
          }
        };

    presenter.attachView(view);
    presenter.loadMore();
    Mockito.verify(view, Mockito.never()).setData(Mockito.anyList());
    Mockito.verify(view, Mockito.never()).showContent();
    Mockito.verify(view, Mockito.times(1)).showError(any(Throwable.class), eq(true));
    Mockito.verify(view, Mockito.times(1)).onNoMore();
  }

  @Test public void testLoadMore_Empty_onCompleted() throws Exception {
    final ListRepository<Object> repository =
        new ListRepository<>(diskProviderEmpty(), cloudProviderEmpty(), new ListComparator<>());
    final RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>
        presenter =
        new RxRepositoryMvpLcePresenter<Object, Object, MvpLcemView<List<Object>>>(repository) {
          @NonNull @Override protected Func1<List<Object>, List<Object>> mapFunction() {
            return new Func1<List<Object>, List<Object>>() {
              @Override public List<Object> call(List<Object> objects) {
                return objects;
              }
            };
          }

          @Override boolean test() {
            return true;
          }
        };

    presenter.attachView(view);
    presenter.loadMore();
    Mockito.verify(view, Mockito.never()).setData(Mockito.anyList());
    Mockito.verify(view, Mockito.never()).showContent();
    Mockito.verify(view, Mockito.never()).showError(any(Throwable.class), any(Boolean.class));
    Mockito.verify(view, Mockito.times(1)).onNoMore();
  }

  private DiskProvider<List<Object>> diskProvider() {
    return new DiskProvider<List<Object>>() {
      @Override public Observable<List<Object>> get() {
        final List<Object> value = Arrays.asList(OBJECT_1, OBJECT_2);
        return Observable.just(value);
      }

      @Override public Observable<List<Object>> save(List<Object> data) {
        return Observable.just(data);
      }
    };
  }

  private DiskProvider<List<Object>> diskProviderEmpty() {
    return new DiskProvider<List<Object>>() {
      @Override public Observable<List<Object>> get() {
        return Observable.empty();
      }

      @Override public Observable<List<Object>> save(List<Object> data) {
        return Observable.just(data);
      }
    };
  }

  private CloudProvider<List<Object>> cloudProvider() {
    return new CloudProvider<List<Object>>() {
      @Override public Observable<List<Object>> get(int page) {
        return Observable.just(Arrays.asList(OBJECT_3, OBJECT_4));
      }
    };
  }

  private CloudProvider<List<Object>> cloudProviderError() {
    return new CloudProvider<List<Object>>() {
      @Override public Observable<List<Object>> get(int page) {
        return Observable.error(new RuntimeException());
      }
    };
  }
  private CloudProvider<List<Object>> cloudProviderEmpty() {
    return new CloudProvider<List<Object>>() {
      @Override public Observable<List<Object>> get(int page) {
        return Observable.empty();
      }
    };
  }
}