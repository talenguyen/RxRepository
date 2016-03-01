package com.tale.rxrepositorydatabinding;

import org.junit.Before;
import org.junit.Test;

import rx.observers.TestSubscriber;

/**
 * RxRepository
 * <p>
 * Created by Giang Nguyen on 3/1/16.
 */
public class LceeViewModelTest {

    private LceeViewModel lceeViewModel;

    @Before
    public void setUp() throws Exception {
        lceeViewModel = new LceeViewModel();
    }

    @Test
    public void testLoading_showLoading_shouldShowLoading() throws Exception {
        lceeViewModel.showLoading();
        final TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        lceeViewModel.isLoading().subscribe(testSubscriber);
        testSubscriber.assertValue(true);
    }

    @Test
    public void testLoading_showLoading_shouldNotShowContent() throws Exception {
        lceeViewModel.showLoading();
        final TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        lceeViewModel.isShowContent().subscribe(testSubscriber);
        testSubscriber.assertValue(false);
    }
}