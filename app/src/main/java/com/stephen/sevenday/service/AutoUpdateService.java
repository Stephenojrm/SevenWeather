package com.stephen.sevenday.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.stephen.sevenday.MyApplication;
import com.stephen.sevenday.receiver.AutoUpdateReceiver;
import com.stephen.sevenday.util.HttpUtil;
import com.stephen.sevenday.util.LogUtil;
import com.stephen.sevenday.util.Utility;

import java.net.URLEncoder;

/**
 * 作者：Stephen on 2016/3/5 16:01.
 * 邮箱：stephenojrm@gmail.com
 */
public class AutoUpdateService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);//获取系统闹钟服务
        int onceUpdate = 60 * 60 * 1000;//设置每次更新间隔为一个小时
        long triggerAtTime = SystemClock.elapsedRealtime() + onceUpdate;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);//返回一个执行broadcast的PendingIntent，1小时后自动发送广播
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);//以唤醒设备的方式执行广播
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String districtName = sharedPreferences.getString("selectDistrict","");
        try {
            String code = URLEncoder.encode(districtName, "utf8");
            String address = "http://v.juhe.cn/weather/index?format=2&cityname=" + code + "&key=5ac669f9d2389ba5ff602110186a2339";
            HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallBackListener() {
                @Override
                public void onFinish(String response) {
                    Utility.handleWeather(MyApplication.getContext(),response);
                    LogUtil.i("----->>>>","update");
                }

                @Override
                public void onError(Exception e) {
                    //
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
