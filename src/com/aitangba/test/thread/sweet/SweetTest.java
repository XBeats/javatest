package com.aitangba.test.thread.sweet;

import java.util.concurrent.Executors;

/**
 * Created by fhf11991 on 2017/6/9.
 */
public class SweetTest {

    public final static String TAG = "SweetTest";

    public static void main(String[] args) {
//        ThreadManager threadManager = new ThreadManager();
//
//        threadManager.execute(new HttpRequest("https://www.baidu.com/", new Request.Listener() {
//            @Override
//            public void onResponse(String response) {
//                SweetLog.d(TAG, response);
//            }
//        }));
//        threadManager.execute(new HttpRequest("https://www.baidu.com/", new Request.Listener() {
//            @Override
//            public void onResponse(String response) {
//                SweetLog.d(TAG, response);
//            }
//        }));
//
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        SweetLog.d(TAG, "尝试关闭线程池");
//        threadManager.shutdown();
        testRequest();
        Executors.newCachedThreadPool();
    }

    public static void testRequest() {
        retry:// 1<span style="font-family: Arial, Helvetica, sans-serif;">（行2）</span>
        for (int i = 0; i < 10; i++) {
            while (i == 5) {
                break retry;
            }
            System.out.print(i + " ");
        }
        System.out.print("退出循环 ");
    }
}
