/**
 * RxRepository
 * <p/>
 * Created by Giang Nguyen on 2/27/16.
 */

package com.tale.rxrepositorydatabinding;

import rx.Scheduler;

public interface ThreadScheduler {

    Scheduler subscribeOn();

    Scheduler observeOn();

}
