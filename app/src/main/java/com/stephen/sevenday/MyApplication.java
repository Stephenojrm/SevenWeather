package com.stephen.sevenday;

import android.app.Application;
import android.content.Context;

/**
 * Created by Stephen on 2016/3/1.
 *
 * 全局获取到context
 */
public class MyApplication extends Application {
    private static  Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        if(context==null){
            context=getApplicationContext();
        }
    }

    public static Context getContext(){
        return context;
    }

}
