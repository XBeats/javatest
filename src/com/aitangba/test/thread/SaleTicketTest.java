package com.aitangba.test.thread;

/**
 * Created by fhf11991 on 2017/4/1.
 */
public class SaleTicketTest {

    public static void main(String[] args) {
        MyRunnable runnable = new MyRunnable();

        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        Thread thread3 = new Thread(runnable);
        thread1.start();
        thread2.start();
        thread3.start();

    }

    static class MyRunnable implements Runnable {

        private int ticket = 10;

        public void run() {
            for(int i=0;i<10;i++){
                synchronized (this) {
                    if(ticket > 0){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        --ticket;
                        System.out.println(" 当前剩余车票 " + ticket);
                    }
                }
            }
        }
    }
}
