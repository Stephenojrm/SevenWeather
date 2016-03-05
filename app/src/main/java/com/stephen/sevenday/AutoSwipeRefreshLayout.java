package com.stephen.sevenday;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Stephen on 2016/3/4.
 */
public class AutoSwipeRefreshLayout extends SwipeRefreshLayout {

    public AutoSwipeRefreshLayout(Context context) {
        super(context);
    }

    public AutoSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 自动刷新
     */
    public void autoRefresh() {
        try {
            Field mCircleView = SwipeRefreshLayout.class.getDeclaredField("mCircleView");//反射获取到CircleView
            mCircleView.setAccessible(true);//设置CicleView可访问(私有属性)
            View progress = (View) mCircleView.get(this);//获取CicleView的进度
            progress.setVisibility(VISIBLE);//设置进度可见

            //反射获取到setRefreshing传两个参数的方法
            Method setRefreshing = SwipeRefreshLayout.class.getDeclaredMethod("setRefreshing", boolean.class, boolean.class);
            setRefreshing.setAccessible(true);//设置此方法可访问
            setRefreshing.invoke(this,true, true);//反射调用刷新方法
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
