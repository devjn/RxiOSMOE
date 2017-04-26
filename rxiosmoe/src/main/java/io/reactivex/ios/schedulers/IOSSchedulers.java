package io.reactivex.ios.schedulers;


import apple.foundation.NSOperationQueue;
import io.reactivex.Scheduler;

/**
 * Static factory methods for creating Schedulers.
 */
public class IOSSchedulers {

    private static final Scheduler MAIN_THREAD_SCHEDULER =
            new io.reactivex.ios.schedulers.HandlerThreadScheduler((NSOperationQueue) NSOperationQueue.mainQueue());

    private IOSSchedulers() {

    }

    /**
     * Converts an {@link NSOperationQueue} into a new Scheduler instance.
     *
     * @param operationQueue the operationQueue to wrap
     * @return the new Scheduler wrapping the NSOperationQueue
     */
    public static Scheduler handlerThread(final NSOperationQueue operationQueue) {
        return new io.reactivex.ios.schedulers.HandlerThreadScheduler(operationQueue);
    }

    /**
     * Creates and returns a {@link Scheduler} that executes work on the main thread.
     *
     * @return a {@link Scheduler} that queues work on the main thread
     */
    public static Scheduler mainThread() {
        return MAIN_THREAD_SCHEDULER;
    }

}
