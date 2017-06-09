package com.aitangba.test.thread.sweet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by fhf11991 on 2017/5/25.
 */

public abstract class Request implements Comparable<Request> {

    private Listener mListener;
    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public Request(Listener listener) {
        mListener = listener;
    }

    public abstract String performRequest();

    public void deliverResponse(String response) {
        if(mListener != null) {
            mListener.onResponse(response);
        }
    }

    public void onFinish() {
        if(mCallback != null) {
            mCallback.onFinish(this);
        }
    }

    public abstract void close();

    protected String getStringFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 模板代码 必须熟练
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();// 把流中的数据转换成字符串,采用的编码是utf-8(模拟器默认编码)
        os.close();
        return state;
    }

    @Override
    public int compareTo(Request another) {
        return 0;
    }

    public interface Listener {
        void onResponse(String response);
    }

    public interface Callback {
        void onFinish(Request request);
    }

    public static class Tracker {

        private final List<Request> mTasks = new LinkedList<>();

        public void add(Request task) {
            mTasks.add(task);
        }

        public void remove(Request task) {
            task.close();
            mTasks.remove(task);
        }

        /**
         * Cancel all registered tasks.
         */
        public void cancelAll() {
            for (Request task : mTasks) {
                task.close();
            }
            mTasks.clear();
        }

        /**
         * Cancel all instances of the same class as {@code current} other than
         * {@code current} itself.
         */
        public void cancelOthers(Request task) {
            synchronized (mTasks) {
                Iterator<Request> iterator = mTasks.iterator();
                while(iterator.hasNext()) {
                    if(iterator.next() != task) {
                        iterator.remove();
                    }
                }
            }
        }
    }
}
