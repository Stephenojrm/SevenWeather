package com.stephen.sevenday.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by stephen on 2016/2/26.
 *
 * 网络请求
 */
public class HttpUtil {


    /**
     * HttpCallBackListener接口回调服务返回的结果
     */
    public interface HttpCallBackListener{
        void onFinish(String response);

        void onError(Exception e);
    }


    /**
     * 发送Http请求
     *
     * @param address 请求地址
     * @param listener  用于回调请求结果处理
     */
    public static void sendHttpRequest(final String address, final HttpCallBackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        //回调onFinish方法
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        //回调onError方法
                        listener.onError(e);
                    }
                    e.printStackTrace();
                } finally {
                    /*
                     数据读取结束关掉连接
                     */
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
