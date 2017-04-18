package com.aitangba.test.thread;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by fhf11991 on 2017/4/6.
 */
public class UploadManager {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT * 2;

    public ThreadPoolExecutor mExecutorService;
    private List<String> mTaskList = new ArrayList<>();
    private List<String> mResultList = new ArrayList<>();

    private List<WalkRunnable> runnableList = new ArrayList<>();

    public UploadManager addTasks(List<String> taskList) {
        mTaskList.clear();
        mTaskList.addAll(taskList);
        return this;
    }

    public void start() {
        final int size = mTaskList.size();
        if (size == 0) return;

        if (mExecutorService == null) {
            mExecutorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(CORE_POOL_SIZE);
        }

        runnableList.clear();
        for (int i = 0; i < size; i++) {
            mExecutorService.submit(new WalkRunnable(this, mTaskList.get(i), size, 10000 + i));
        }
    }

    public void stop() {
        if (mExecutorService != null) {
            mExecutorService.shutdownNow();
        }

        if(!mExecutorService.isTerminated()) {
            Iterator<WalkRunnable> threadIterator = runnableList.iterator();
            while(threadIterator.hasNext()){
                WalkRunnable walkRunnable = threadIterator.next();
                walkRunnable.stop();
            }
        }
        runnableList.clear();
        System.out.println("size = " + mExecutorService.getActiveCount());
    }

    private static class WalkRunnable implements Runnable {

        private UploadManager mUploadManager;
        private final String task;
        private final int taskSize;
        private int socket;

        public WalkRunnable(UploadManager uploadManager, String task, int taskSize, int socket) {
            this.task = task;
            this.taskSize = taskSize;
            this.mUploadManager = uploadManager;
            this.socket = socket;
        }
        private ServerSocket ss = null;

        @Override
        public void run() {
            synchronized (mUploadManager.runnableList) {
                if(!mUploadManager.runnableList.contains(this)) {
                    mUploadManager.runnableList.add(this);
                }
            }

                        System.out.println(task + "开始了");
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
//                e.printStackTrace();
                System.out.println("有人主动关闭我了");
                return;
            }

//            System.out.println(task + "开始了");
//            try {
//                ss = new ServerSocket(socket);
//                ss.accept();
//            } catch (IOException e) {
////                e.printStackTrace();
//                System.out.println("有人主动关闭我了");
//                return;
//            }

            mUploadManager.runnableList.remove(this);
            System.out.println(task + "结束了");
            synchronized (mUploadManager.mResultList) {
                mUploadManager.mResultList.add(task + "结束了");
                if (mUploadManager.mResultList.size() == taskSize) {
                    System.out.println("所有任务结束了----");
                    mUploadManager.stop();
                }
            }
        }

        public void stop() {
//            try {
//                ss.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
}
