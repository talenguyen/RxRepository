/**
 * RxRepository
 * <p/>
 * Created by Giang Nguyen on 2/27/16.
 */

package com.tale.rxrepositorydatabinding;

import rx.Scheduler;

/**
 * Thread scheduler class to define where to subscribe and observer
 */
public interface ThreadScheduler {

    /**
     * @return The scheduler which be subscribe on
     */
    Scheduler subscribeOn();

    /**
     * @return The scheduler which be observe on
     */
    Scheduler observeOn();

}
