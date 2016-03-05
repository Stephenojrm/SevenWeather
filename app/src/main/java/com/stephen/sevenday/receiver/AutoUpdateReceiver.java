package com.stephen.sevenday.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.stephen.sevenday.service.AutoUpdateService;

/**
 * 作者：Stephen on 2016/3/5 16:13.
 * 邮箱：stephenojrm@gmail.com
 */
public class AutoUpdateReceiver extends BroadcastReceiver{
    /**
     * 接收到广播后，直接调用一遍开启服务
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
