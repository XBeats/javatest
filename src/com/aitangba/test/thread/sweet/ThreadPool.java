package com.aitangba.test.thread.sweet;

/**
 * Created by fhf11991 on 2017/6/9.
 */
public interface ThreadPool {

    boolean isShutdown();

    void onJobFinished();
}
