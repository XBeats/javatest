package com.aitangba.test.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fhf11991 on 2017/4/6.
 */
public class ThreadPoolTest {


    public static void main(String[] args) {

        List<String> taskList = new ArrayList<>();
        for(int i = 0; i < 10; i ++) {
            taskList.add("任务" + i);
        }
        UploadManager uploadManager = new UploadManager();
        uploadManager.addTasks(taskList).start();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("开始主动关闭所有任务----");
                uploadManager.stop();
            }
        }, 1000);
    }
}
