package com.aitangba.test.thread.sweet;


import java.util.HashSet;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by fhf11991 on 2017/5/26.
 */

public class ThreadManager {

    private final static int CORE_NUM = 1;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    private int coreNum = CORE_NUM;
    private int waitTime = 1; //ms

    private AtomicInteger workersCount = new AtomicInteger(); // current threads
    private AtomicInteger currentJobsCount = new AtomicInteger(); // the count of jobs from running to waiting
    private volatile boolean isShutdown = false;

    private final HashSet<Worker> workers = new HashSet<>();
    private final ReentrantLock mainLock = new ReentrantLock();

    private PriorityBlockingQueue<Request> mBlockingPriorityQueue = new PriorityBlockingQueue<>();

    private Delivery mExecutorDelivery;

    public ThreadManager() {
        mExecutorDelivery = new ExecutorDelivery();
    }

    public void execute(Request command) {
        if(isShutdown) {
            return;
        }

        if(!addWorker(command)) {
            mBlockingPriorityQueue.offer(command);
            currentJobsCount.incrementAndGet();
        }
    }

    public void shutdown() {
        isShutdown = true;
        mBlockingPriorityQueue.clear();

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers) {
                SweetLog.d("ThreadManager", "开始关闭线程");
                w.close();
            }
            workers.clear();
        } finally {
            mainLock.unlock();
        }
    }

    private int threadIndex;

    protected Thread newThread(Runnable r) {
        String threadName = "#" + threadIndex ++;
        SweetLog.d("ThreadManager", "创建了一个新的线程 named = " + threadName);
        return new Thread(r, threadName);
    }

    private boolean addWorker(Request request) {
        for(;;) {
            int threadNum = this.workersCount.get();
            int workerNum = this.currentJobsCount.get();
            SweetLog.d("ThreadManager", "任务 named = " + " workersCount = " + threadNum + " workerNum = " + workerNum);
            if(workerNum == MAXIMUM_POOL_SIZE) {
                return false;
            } else if(threadNum != 0 && workerNum < threadNum) { // some thread is sleep
                return false;
            } else {
                Worker dispatcher = new Worker(this, request);
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    workers.add(dispatcher);
                } finally {
                    mainLock.unlock();
                }
                dispatcher.thread.start();
                return true;
            }
        }
    }

    private static class Worker implements Runnable {

        private final static String TAG = "Worker";

        private ThreadManager mThreadManager;
        private Request mCurrentRequest;
        private Thread thread;

        public Worker(ThreadManager threadManager, Request firstRequest) {
            this.mThreadManager = threadManager;
            mCurrentRequest = firstRequest;
            thread = threadManager.newThread(this);

            mThreadManager.workersCount.incrementAndGet();
            mThreadManager.currentJobsCount.incrementAndGet();
        }

        @Override
        public void run() {
            boolean isCoreThread = false;
            try {
                retry:
                for(;;) {
                    while (!mThreadManager.isShutdown
                            && (mCurrentRequest != null || (mCurrentRequest = (isCoreThread ? mThreadManager.mBlockingPriorityQueue.take()
                            : mThreadManager.mBlockingPriorityQueue.poll(mThreadManager.waitTime, TimeUnit.MILLISECONDS))) != null)) {
                        mThreadManager.mExecutorDelivery.postResponse(mCurrentRequest, mCurrentRequest.performRequest());
                        mCurrentRequest = null;
                        isCoreThread = false;
                        mThreadManager.currentJobsCount.decrementAndGet();
                    }

                    if(mThreadManager.isShutdown) {
                        break;
                    } else if(mThreadManager.workersCount.get() <= mThreadManager.coreNum) {
                        isCoreThread = true;
                        continue retry;
                    } else {
                        break;
                    }
                }

            } catch (InterruptedException e) {

            } finally {
                final ReentrantLock mainLock = mThreadManager.mainLock;
                mainLock.lock();
                try {
                    mThreadManager.workers.remove(this);
                } finally {
                    mainLock.unlock();
                }
                mThreadManager.workersCount.decrementAndGet();
                SweetLog.d(TAG, "线程" + Thread.currentThread().getName() + "关闭");
            }
        }

        private void close() {
            if(mCurrentRequest != null) {
                mCurrentRequest.close();
                mCurrentRequest = null;
            }

            if (!thread.isInterrupted()) {
                try {
                    thread.interrupt();
                } catch (SecurityException ignore) {
                }
            }
        }
    }
}
