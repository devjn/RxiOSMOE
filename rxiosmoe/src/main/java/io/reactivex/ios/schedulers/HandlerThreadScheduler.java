package io.reactivex.ios.schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import apple.foundation.NSOperationQueue;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.internal.schedulers.RxThreadFactory;

/**
 * Schedules actions to run on an iOS Handler thread.
 */
class HandlerThreadScheduler extends Scheduler {

    private final NSOperationQueue operationQueue;
    private static final String THREAD_PREFIX = "RxiOSScheduledExecutorPool-";

    public HandlerThreadScheduler(NSOperationQueue operationQueue) {
        this.operationQueue = operationQueue;
    }

    @Override
    public Worker createWorker() {
        return new HandlerThreadWorker(operationQueue);
    }

    private static class HandlerThreadWorker extends Worker {

        private final NSOperationQueue operationQueue;
        private final CompositeDisposable innerSubscription = new CompositeDisposable();

        HandlerThreadWorker(NSOperationQueue operationQueue) {
            this.operationQueue = operationQueue;
        }

        @Override
        public void dispose() {
            innerSubscription.dispose();
        }

        @Override
        public boolean isDisposed() {
            return innerSubscription.isDisposed();
        }

        @Override
        public Disposable schedule(final Runnable action, long delayTime, TimeUnit unit) {
            if (innerSubscription.isDisposed()) {
                return Disposables.empty();
            }

            final ScheduledAction scheduledAction = new ScheduledAction(action, operationQueue);
            final ScheduledExecutorService executor = IOSScheduledExecutorPool.getInstance();

            Future<?> future;
            if (delayTime <= 0) {
                future = executor.submit(scheduledAction);
            } else {
                future = executor.schedule(scheduledAction, delayTime, unit);
            }

            scheduledAction.add(Disposables.fromFuture(future));
            scheduledAction.addParent(innerSubscription);

            return scheduledAction;
        }

        @Override
        public Disposable schedule(final Runnable action) {
            return schedule(action, 0, null);
        }

    }

    private static final class IOSScheduledExecutorPool {
        private static final RxThreadFactory THREAD_FACTORY = new RxThreadFactory(THREAD_PREFIX);
        private static final IOSScheduledExecutorPool INSTANCE = new IOSScheduledExecutorPool();
        private final ScheduledExecutorService executorService;

        private IOSScheduledExecutorPool() {
            executorService = Executors.newScheduledThreadPool(1, THREAD_FACTORY);
        }

        public static ScheduledExecutorService getInstance() {
            return INSTANCE.executorService;
        }
    }

}
