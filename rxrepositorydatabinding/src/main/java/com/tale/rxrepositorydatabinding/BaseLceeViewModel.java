/**
 * RxRepository
 * <p/>
 * Created by Giang Nguyen on 2/27/16.
 */

package com.tale.rxrepositorydatabinding;

import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.SerializedRelay;
import com.tale.rxrepository.ListRepository;

import java.util.List;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

public class BaseLceeViewModel<T> {
    private final SerializedRelay<Boolean, Boolean> loading;
    private final SerializedRelay<Boolean, Boolean> error;
    private final ListRepository<T> repository;
    private final ThreadScheduler threadScheduler;

    public BaseLceeViewModel(ListRepository<T> repository, ThreadScheduler threadScheduler) {
        this.repository = repository;
        this.threadScheduler = threadScheduler;
        this.loading = BehaviorRelay.<Boolean>create().toSerialized();
        this.error = BehaviorRelay.<Boolean>create().toSerialized();
    }

    public Observable<Boolean> loadingObservable() {
        return loading.asObservable();
    }

    public Observable<Boolean> errorObservable() {
        return error.asObservable();
    }

    public Observable<List<T>> load() {
        loading.call(true);
        return repository.get()
                .subscribeOn(threadScheduler.subscribeOn())
                .observeOn(threadScheduler.observeOn())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        loading.call(false);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        loading.call(false);
                    }
                });
    }

}
