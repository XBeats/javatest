package com.aitangba.test.thread.priorityqueue;

import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by fhf11991 on 2017/5/25.
 */

public class BlockingPriorityQueue<E> {

    /** Lock held by poll, etc */
    private final ReentrantLock takeLock = new ReentrantLock();

    /** Wait queue for waiting takes */
    private final Condition notEmpty = takeLock.newCondition();

    /** Lock held by offer, etc */
    private final ReentrantLock putLock = new ReentrantLock();

    /** Current number of elements */
    private final AtomicInteger count = new AtomicInteger();

    transient PriorityQueue<E> priorityQueue;

    public BlockingPriorityQueue() {
        this.priorityQueue = new PriorityQueue<>();
    }

    public boolean offer(E e)  {
        putLock.tryLock();
        if (e == null) {
            throw new NullPointerException();
        }

        final AtomicInteger count = this.count;
        int c = -1;

        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            priorityQueue.offer(e);
            c = count.getAndIncrement();
        } finally {
            putLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();
        }
        return true;
    }

    /**
     * Signals a waiting take. Called only from put/offer (which do not
     * otherwise ordinarily lock takeLock.)
     */
    private void signalNotEmpty() {
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E x = null;
        int c;
        long nanos = unit.toNanos(timeout);
        final ReentrantLock takeLock = this.takeLock;
        final AtomicInteger count = this.count;

        takeLock.lockInterruptibly();
        try {
            while (count.get() == 0) {
                if (nanos <= 0)
                    return null;
                nanos = notEmpty.awaitNanos(nanos);
            }
            x = priorityQueue.poll();
            c = count.getAndDecrement();
            if (c > 1)
                notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
        return x;
    }
}
