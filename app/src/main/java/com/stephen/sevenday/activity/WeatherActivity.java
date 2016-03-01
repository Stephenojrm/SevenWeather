package com.stephen.sevenday.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stephen.sevenday.R;
import com.stephen.sevenday.util.HttpUtil;
import com.stephen.sevenday.util.Utility;

public class WeatherActivity extends Activity {
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
        String districtName = getIntent().getStringExtra("districtName");
        if(!TextUtils.isEmpty(districtName)){
            if() {
                //没有网读取本地SharedPreference
                showWeather(districtName);
            }else{
                queryFromServer(address, new HttpUtil.HttpCallBackListener() {
                    @Override
                    public void onFinish(String response) {
                        Utility.handleWeather(WeatherActivity.this,response);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    public void showWeather(String districtName){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        district.setText(districtName);
        publishTime.setText(sharedPreferences.getString("publishTime",""));
        date.setText(sharedPreferences.getString("date",""));
        weather.setText(sharedPreferences.getString("weather",""));
        temp.setText(sharedPreferences.getString("temp",""));
        weatherInfo.setVisibility(View.VISIBLE);
        district.setVisibility(View.VISIBLE);
    }

    public void queryFromServer(String address,HttpUtil.HttpCallBackListener listener){
        HttpUtil.sendHttpRequest(address,listener);

    }
}
