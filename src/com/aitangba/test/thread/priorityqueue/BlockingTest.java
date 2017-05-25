package com.aitangba.test.thread.priorityqueue;

import java.util.concurrent.TimeUnit;

/**
 * Created by fhf11991 on 2017/5/25.
 */
public class BlockingTest {

    private static BlockingPriorityQueue<String> blockingPriorityQueue = new BlockingPriorityQueue<>();

    public static void main(String[] args) {

        for(int i = 0 ; i < 10 ; i  ++) {
            new PutThread(blockingPriorityQueue, "name" + i).start();
        }

        for(int i = 0 ; i < 15 ; i  ++) {
            new GetThread(blockingPriorityQueue).start();
        }
    }

    private static class PutThread extends Thread {

        private BlockingPriorityQueue<String> blockingPriorityQueue;
        private String name;

        public PutThread(BlockingPriorityQueue<String> blockingPriorityQueue, String name) {
            this.blockingPriorityQueue = blockingPriorityQueue;
            this.name = name;
        }

        @Override
        public void run() {
            blockingPriorityQueue.offer(name);
            System.out.println("添加成功 name = " + name);
        }
    }

    private static class GetThread extends Thread {

        private BlockingPriorityQueue<String> blockingPriorityQueue;

        public GetThread(BlockingPriorityQueue<String> blockingPriorityQueue) {
            this.blockingPriorityQueue = blockingPriorityQueue;
        }

        @Override
        public void run() {
            String name = null;
            try {
                name = blockingPriorityQueue.poll(1000, TimeUnit.MICROSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("取值成功 name = " + name);
        }
    }
}
