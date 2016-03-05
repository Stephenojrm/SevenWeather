package com.stephen.sevenday.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
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
    /**
     * 返回主页的按钮
     */
    private Button homeBtn;

    private AutoSwipeRefreshLayout swipeRefreshLayout;
    /**
     * 选择的城市
     */
    private String selectDistrict;

    private SharedPreferences sharedPreferences;

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

        selectDistrict = getIntent().getStringExtra("selectDistrict");//获取主页面传过来的选择的城市

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());

        /**
         * 为主页按钮添加点击监听事件
         */
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(WeatherActivity.this, MainActivity.class);//返回主页的intent
                toMain.putExtra("isFromWea", true);//设置一个标志位isFrom，告诉主页面我是从Wea跳过来的
                startActivity(toMain);
                WeatherActivity.this.finish();
            }
        });

        /**
         * 配置SwipeRefreshLayout的属性
         */
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.autoRefresh();//进入界面自动刷新
    }


    /**
     * 显示天气状况
     *
     * @param districtName 要显示天气的城市
     */
    public void showWeather(String districtName) {
        district.setText(sharedPreferences.getString("selectDistrict", districtName));
        publishTime.setText("发布时间" + sharedPreferences.getString("publishTime", ""));
        date.setText(sharedPreferences.getString("date", ""));
        weather.setText(sharedPreferences.getString("weather", ""));
        temp.setText(sharedPreferences.getString("temp", ""));
//        weatherInfo.setVisibility(View.VISIBLE);
//        district.setVisibility(View.VISIBLE);
    }

    /**
     * 没有本地的SharedPreference向服务器请求数据
     *
     * @param address url
     * @param listener
     */
    public void queryFromServer(String address, HttpUtil.HttpCallBackListener listener) {
        HttpUtil.sendHttpRequest(address, listener);
    }


    /**
     * SwipeRefreshLayout.OnRefreshListener监听到refresh动作是触发
     * 在其中不同县城中进行网络请求，刷新UI
     */
    @Override
    public void onRefresh() {
        /*
        Runnable r 被handler添加到message_queue中，在指定的时间后触发r内部事件
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final String districtName;
                try {
                    if (selectDistrict == null) {
                        //直接进入Wea界面的
                        districtName = sharedPreferences.getString("selectDistrict", "");
                    } else {
                        //从主界面选择城市之后
                        districtName = selectDistrict;
                    }
                    String code = URLEncoder.encode(districtName, "utf8");
                    String address = "http://v.juhe.cn/weather/index?format=2&cityname=" + code + "&key=5ac669f9d2389ba5ff602110186a2339";
                    queryFromServer(address, new HttpUtil.HttpCallBackListener() {
                        @Override
                        public void onFinish(String response) {
                            Utility.handleWeather(WeatherActivity.this, response);
                            //查询到数据后，将选择的城市存入到sharedPreferences中去
                            sharedPreferences.edit().putString("selectDistrict", districtName).commit();
                            try {
                                Thread.sleep(1000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showWeather(districtName);
                                        swipeRefreshLayout.setRefreshing(false);
                                        Toast.makeText(WeatherActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            //1.选择城市后，退出后再次进入直接加载sharedPreferences
                            //2.在主界面选择的城市和sharedPreferences中的一致
                            if (selectDistrict == null || districtName.equals(sharedPreferences.getString("selectDistrict", ""))) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showWeather(districtName);
                                        Toast.makeText(WeatherActivity.this, "请检查网络后刷新", Toast.LENGTH_SHORT).show();
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                });
                            } else {
                                //在主界面选择的城市和sharedPreferences中的不一致
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(WeatherActivity.this, "请检查网络后刷新", Toast.LENGTH_SHORT).show();
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                });
                            }
                        }
                    });

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, 0);
    }
}
