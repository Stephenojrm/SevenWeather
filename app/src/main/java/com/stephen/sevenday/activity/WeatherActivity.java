package com.stephen.sevenday.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stephen.sevenday.AutoSwipeRefreshLayout;
import com.stephen.sevenday.MyApplication;
import com.stephen.sevenday.R;
import com.stephen.sevenday.util.HttpUtil;
import com.stephen.sevenday.util.Utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WeatherActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {
    private LinearLayout weatherInfo;
    /**
     * 用于显示城市的名字
     */
    private TextView district;
    /**
     * 用于显示发布时间
     */
    private TextView publishTime;
    /**
     * 用于显示今天的日期
     */
    private TextView date;
    /**
     * 用于显示今天的天气状况
     */
    private TextView weather;
    /**
     * 用于显示惊天的气温
     */
    private TextView temp;

    private Button homeBtn;

    private AutoSwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        district = (TextView) findViewById(R.id.districtText);
        publishTime = (TextView) findViewById(R.id.publishTime);
        date = (TextView) findViewById(R.id.current_day);
        weather = (TextView) findViewById(R.id.weather);
        weatherInfo = (LinearLayout) findViewById(R.id.weatherInfo);
        temp = (TextView) findViewById(R.id.temp);
        homeBtn = (Button) findViewById(R.id.homeBtn);
        swipeRefreshLayout = (AutoSwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        final String districtName = getIntent().getStringExtra("selectDistrict");

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(WeatherActivity.this, MainActivity.class);
                toMain.putExtra("isFromWea", true);
                startActivity(toMain);
                WeatherActivity.this.finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.autoRefresh();

        //获取当前的网络连接服务
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取活动的网络连接信息
        NetworkInfo info = connMgr.getActiveNetworkInfo();

        if (info == null) {
            if (districtName.equals(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("selectDistrict", ""))) {
                showWeather(districtName);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(WeatherActivity.this, "网络未连接", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if (!info.isAvailable()) {
            if (districtName.equals(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("selectDistrict", ""))) {
                showWeather(districtName);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(WeatherActivity.this, "当前网络不可用", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            if (!TextUtils.isEmpty(districtName)) {
                String dNameEncode = null;
                try {
                    dNameEncode = URLEncoder.encode(districtName, "utf8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String address = "http://v.juhe.cn/weather/index?format=2&cityname=" + dNameEncode + "&key=5ac669f9d2389ba5ff602110186a2339";
                queryFromServer(address, new HttpUtil.HttpCallBackListener() {
                    @Override
                    public void onFinish(String response) {
                        Utility.handleWeather(WeatherActivity.this, response);
                        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putString("selectDistrict", districtName).commit();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showWeather(districtName);
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(WeatherActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                String dNameEncode = null;
                try {
                    String selectDistrict = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("selectDistrict", "");
                    if (!TextUtils.isEmpty(selectDistrict)) {
                        dNameEncode = URLEncoder.encode(selectDistrict, "utf8");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (dNameEncode != null) {
                    String address = "http://v.juhe.cn/weather/index?format=2&cityname=" + dNameEncode + "&key=5ac669f9d2389ba5ff602110186a2339";
                    queryFromServer(address, new HttpUtil.HttpCallBackListener() {
                        @Override
                        public void onFinish(String response) {
                            Utility.handleWeather(WeatherActivity.this, response);
                            PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putString("selectDistrict", districtName).commit();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showWeather(districtName);
                                    swipeRefreshLayout.setRefreshing(false);
                                    Toast.makeText(WeatherActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    });
                }


            }
        }
    }


    public void showWeather(String districtName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        district.setText(sharedPreferences.getString("selectDistrict", districtName));
        publishTime.setText("发布时间" + sharedPreferences.getString("publishTime", ""));
        date.setText(sharedPreferences.getString("date", ""));
        weather.setText(sharedPreferences.getString("weather", ""));
        temp.setText(sharedPreferences.getString("temp", ""));
        weatherInfo.setVisibility(View.VISIBLE);
        district.setVisibility(View.VISIBLE);
    }

    public void queryFromServer(String address, HttpUtil.HttpCallBackListener listener) {
        HttpUtil.sendHttpRequest(address, listener);
    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        }, 0);
    }
}
