package com.aitangba.test.thread.sweet;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fhf11991 on 2017/5/27.
 */

public class HttpRequest extends Request {

    private String mUrl;
    private String mRequestMethod;
    private HttpURLConnection conn = null;
    private String mContent; // post content

    public HttpRequest(String url, Listener listener) {
        this(url, "GET", null, listener);
    }

    public HttpRequest(String url, String content, Listener listener) {
        this(url, "POST", content, listener);
    }

    public HttpRequest(String url, String requestMethod, String content, Listener listener) {
        super(listener);
        mUrl = url;
        mRequestMethod = requestMethod;
        mContent = content;
    }

    @Override
    public String performRequest() {
        try {
            // 利用string url构建URL对象
            URL mURL = new URL(mUrl);
            conn = (HttpURLConnection) mURL.openConnection();

            conn.setRequestMethod(mRequestMethod);
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(10000);

            // POST way
            if("POST".equals(mRequestMethod) && mContent != null) {
                conn.setDoOutput(true);// 设置此方法,允许向服务器输出内容

                // post请求的参数
                String data = mContent;
                // 获得一个输出流,向服务器写数据,默认情况下,系统不允许向服务器输出内容
                OutputStream out = conn.getOutputStream();// 获得一个输出流,向服务器写数据
                out.write(data.getBytes());
                out.flush();
                out.close();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = conn.getInputStream();
                String response = getStringFromInputStream(is);
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    @Override
    public void close() {
        if(conn != null) {
            conn.disconnect();
        }
    }
}
