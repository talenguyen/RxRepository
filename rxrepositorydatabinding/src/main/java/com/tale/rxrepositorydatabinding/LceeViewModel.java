/**
 * RxRepository
 * <p/>
 * Created by Giang Nguyen on 3/1/16.
 */

package com.tale.rxrepositorydatabinding;

import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.SerializedRelay;

import rx.Observable;

public class LceeViewModel {

    private final SerializedRelay<Boolean, Boolean> showContent;
    private final SerializedRelay<Boolean, Boolean> loading;

    public LceeViewModel() {
        this.showContent = BehaviorRelay.<Boolean>create().toSerialized();
        this.loading = BehaviorRelay.<Boolean>create().toSerialized();
    }

    public void showLoading() {
        loading.call(true);
        showContent.call(false);
    }

    public Observable<Boolean> isShowContent() {
        return showContent.asObservable();
    }

    public Observable<Boolean> isLoading() {
        return loading.asObservable();
    }
}
