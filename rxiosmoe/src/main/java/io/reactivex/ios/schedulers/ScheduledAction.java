package io.reactivex.ios.schedulers;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import apple.foundation.NSBlockOperation;
import apple.foundation.NSOperationQueue;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * A {@code Runnable} that executes an {@code Runnable} that can be cancelled.
 */
final class ScheduledAction implements Runnable, Disposable {

    private final CompositeDisposable cancel;
    private final Runnable action;
    private final NSBlockOperation nsBlockOperation;
    private final NSOperationQueue operationQueue;
    volatile int once;
    private static final AtomicIntegerFieldUpdater<ScheduledAction> ONCE_UPDATER
            = AtomicIntegerFieldUpdater.newUpdater(ScheduledAction.class, "once");

    ScheduledAction(Runnable action, NSOperationQueue operationQueue) {
        this.action = action;
        this.operationQueue = operationQueue;
        this.cancel = new CompositeDisposable();
        nsBlockOperation = NSBlockOperation.alloc().init();
    }

    @Override
    public void run() {
        nsBlockOperation.addExecutionBlock(new NSBlockOperation.Block_addExecutionBlock() {
            @Override
            public void call_addExecutionBlock() {
                try {
                    action.run();
                } catch (Throwable e) {
                    // nothing to do but print a System error as this is fatal and there is nowhere else to throw this
                    IllegalStateException ie;
                    ie = new IllegalStateException("Fatal Exception thrown on Scheduler.Worker thread.", e);
                    Thread thread = Thread.currentThread();
                    thread.getUncaughtExceptionHandler().uncaughtException(thread, ie);
                } finally {
                    dispose();
                }
            }
        });
        operationQueue.addOperation(nsBlockOperation);
    }

    @Override
    public void dispose() {
        if (ONCE_UPDATER.compareAndSet(this, 0, 1)) {
            nsBlockOperation.cancel();
            cancel.dispose();
        }
    }

    @Override
    public boolean isDisposed() {
        return cancel.isDisposed();
    }

    /**
     * Adds a {@code Disposable} to the {@link CompositeDisposable} to be later cancelled on dispose
     *
     * @param s disposable to add
     */
    void add(Disposable s) {
        cancel.add(s);
    }

    /**
     * Adds a parent {@link io.reactivex.disposables.CompositeDisposable} to this {@code ScheduledAction}
     * so when the action is cancelled or terminates, it can remove itself from this parent
     * @param parent the parent {@code CompositeSubscription} to add
     */
    void addParent(CompositeDisposable parent) {
        cancel.add(new Remover(this, parent));
    }

    /**
     * Remove a child disposable from a composite when disposing.
     */
    private static final class Remover implements Disposable {

        final Disposable s;
        final CompositeDisposable parent;
        volatile int once;
        static final AtomicIntegerFieldUpdater<Remover> ONCE_UPDATER
                = AtomicIntegerFieldUpdater.newUpdater(Remover.class, "once");

        Remover(Disposable s, CompositeDisposable parent) {
            this.s = s;
            this.parent = parent;
        }

        @Override
        public boolean isDisposed() {
            return s.isDisposed();
        }

        @Override
        public void dispose() {
            if (ONCE_UPDATER.compareAndSet(this, 0, 1)) {
                parent.remove(s);
            }
        }

    }

}
