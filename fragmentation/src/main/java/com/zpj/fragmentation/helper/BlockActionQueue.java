package com.zpj.fragmentation.helper;

import android.os.Handler;
import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Z-P-J
 */
public class BlockActionQueue {

    private static final String TAG = "BlockActionQueue";

    private final Queue<Action> mQueue = new Queue<>();
    private final AtomicBoolean start = new AtomicBoolean(false);

    private final Handler mHandler;

    private static class Queue<T> extends LinkedList<T> {

        private volatile boolean destroyed;

        public synchronized void onDestroyed() {
            clear();
            this.destroyed = true;
        }

        @Override
        public boolean add(T t) {
            if (destroyed) {
                return false;
            }
            return super.add(t);
        }

    }

    public abstract static class Action {

        public long delay = 0;

        public abstract void run();

    }

    public BlockActionQueue(Handler handler) {
        this.mHandler = handler;
    }

    public boolean isStart() {
        return start.get();
    }

    public void start() {
        start.set(true);
        handleAction();
    }

    public void stop() {
        start.set(false);
    }

    public void onDestroy() {
        start.set(false);
        this.mQueue.onDestroyed();
    }

    public void post(final Runnable runnable) {
        enqueue(new Action() {
            @Override
            public void run() {
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }

    public void postDelayed(final Runnable runnable, long delay) {
        Action action = new Action() {
            @Override
            public void run() {
                if (runnable != null) {
                    runnable.run();
                }
            }
        };
        action.delay = delay;
        enqueue(action);
    }

    public void enqueue(final Action action) {
        mHandler.post(() -> {
            mQueue.add(action);
            Log.d(TAG, "size=" + mQueue.size());
            if (mQueue.size() == 1) {
                handleAction();
            }
        });
    }

    private void handleAction() {
        if (!start.get() || mQueue.isEmpty()) return;

        final Action action = mQueue.peek();
        mHandler.postDelayed(() -> {
            if (start.get()) {
                action.run();
                mQueue.poll();
                handleAction();
            } else {
                mQueue.add(action);
            }
        }, action.delay);
    }

}
