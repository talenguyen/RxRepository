package com.tale.rxrepositorydatabinding;

import com.tale.rxrepository.ListRepository;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

/**
 * RxRepository
 * <p/>
 * Created by Giang Nguyen on 2/27/16.
 */
public class BaseLceeViewModelTest {
    private BaseLceeViewModel<Object> baseLceeViewModel;
    @Mock
    ListRepository<Object> repository;
    @Mock
    ThreadScheduler threadScheduler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(threadScheduler.subscribeOn()).thenReturn(Schedulers.immediate());
        Mockito.when(threadScheduler.observeOn()).thenReturn(Schedulers.immediate());
        baseLceeViewModel = new BaseLceeViewModel<>(repository, threadScheduler);
    }

    ////
    // Begin Loading field
    ////
    @Test
    public void testShouldHaveLoadingField() throws Exception {
        Assert.assertNotNull(baseLceeViewModel.loadingObservable());
    }

    @Test
    public void testLoadMethod_success_shouldShowThenHideLoading() throws Exception {
        Mockito.when(repository.get()).thenReturn(Observable.just(Collections.emptyList()));
        final TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        baseLceeViewModel.loadingObservable().subscribe(testSubscriber);
        baseLceeViewModel.load().toBlocking().subscribe();
        testSubscriber.assertValues(true, false);
    }

    @Test
    public void testLoadMethod_error_shouldShowThenHideLoading() throws Exception {
        Mockito.when(repository.get()).thenReturn(Observable.<List<Object>>error(new RuntimeException()));
        final TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        baseLceeViewModel.loadingObservable().subscribe(testSubscriber);
        baseLceeViewModel.load().toBlocking().subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Object> objects) {

            }
        });
        testSubscriber.assertValues(true, false);
    }

    @Test
    public void testLoadMethod_empty_shouldShowThenHideLoading() throws Exception {
        Mockito.when(repository.get()).thenReturn(Observable.<List<Object>>empty());
        final TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        baseLceeViewModel.loadingObservable().subscribe(testSubscriber);
        baseLceeViewModel.load().toBlocking().subscribe();
        testSubscriber.assertValues(true, false);
    }

    ////
    // Begin error field
    ////
    @Test
    public void testShouldHaveErrorField() throws Exception {
        Assert.assertNotNull(baseLceeViewModel.errorObservable());
    }

}