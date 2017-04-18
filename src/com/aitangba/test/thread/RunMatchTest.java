package com.aitangba.test.thread;

import java.util.Stack;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by fhf11991 on 2017/4/1.
 */
public class RunMatchTest {

    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(3);
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        Stack<String> stack = new Stack<>();
        CustomThread airThread = new CustomThread(50, stack);
        CustomThread trainThread = new CustomThread(20, stack);
        CustomThread busThread = new CustomThread(10, stack);

        airThread.start();
        trainThread.start();
        busThread.start();
    }

    private final static int first_distance = 100; //第一段距离100米

    private static class CustomThread extends Thread {

        private int mStep;
        private Stack<String> stack;

        public CustomThread(int mStep, Stack stack) {
            super("步长" + mStep + "的线程");
            this.mStep = mStep;
            this.stack = stack;
        }

        @Override
        public void run() {
            super.run();
            int firstCurrentDistance = first_distance;
            while (firstCurrentDistance > 0) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                firstCurrentDistance = firstCurrentDistance - mStep;
                System.out.println(Thread.currentThread().getName() + " 当前剩余距离 " + firstCurrentDistance);
            }
            System.out.println(Thread.currentThread().getName() + "已完成第一次任务");

            synchronized (stack) {
                stack.push(Thread.currentThread().getName());
                if(stack.size() == 3) {
                    System.out.println("----------------所有比赛者都完成了第一次任务-----------------");
                    stack.notifyAll();
                } else {
                    try {
                        stack.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            int currentDistance = first_distance;
            while (currentDistance > 0) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentDistance = currentDistance - mStep;
                System.out.println(Thread.currentThread().getName() + " 当前剩余距离 " + currentDistance);
            }
            System.out.println(Thread.currentThread().getName() + "已完成第二次任务");
        }
    }
}
