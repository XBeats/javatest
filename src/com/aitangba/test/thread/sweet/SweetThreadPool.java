package com.aitangba.test.thread.sweet;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fhf11991 on 2017/6/9.
 */
public class SweetThreadPool implements ThreadPool {

    private AtomicInteger workersCount = new AtomicInteger(); // current threads
    private AtomicInteger currentJobsCount = new AtomicInteger(); // the count of jobs from running to waiting
    private volatile boolean isShutdown = false;

    @Override
    public boolean isShutdown() {
        return isShutdown;
    }

    @Override
    public void onJobFinished() {

    }
}
