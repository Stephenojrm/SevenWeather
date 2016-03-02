package com.stephen.sevenday.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stephen.sevenday.MyApplication;
import com.stephen.sevenday.R;
import com.stephen.sevenday.util.HttpUtil;
import com.stephen.sevenday.util.LogUtil;
import com.stephen.sevenday.util.Utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

    private Button homeBtn;

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

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(WeatherActivity.this,MainActivity.class);
                toMain.putExtra("isFromWea",true);
                startActivity(toMain);
                WeatherActivity.this.finish();
            }
        });

        final String districtName = getIntent().getStringExtra("selectDistrict");
        if (!TextUtils.isEmpty(districtName)) {
            //获取当前的网络连接服务
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取活动的网络连接信息
            NetworkInfo info = connMgr.getActiveNetworkInfo();
            if (info==null) {
                //没有网读取本地SharedPreference
                Toast.makeText(this,"网络未连接",Toast.LENGTH_SHORT).show();
                showWeather(districtName);
            } else {
                //当前有已激活的网络连接，但是否可用还需判断
                boolean isAlive = info.isAvailable();
                if(isAlive) {
                    //可用的话去服务器查询
                    String dNameEncode=null;
                    try {
                        dNameEncode = URLEncoder.encode(districtName,"utf8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String address = "http://v.juhe.cn/weather/index?format=2&cityname="+dNameEncode+"&key=5ac669f9d2389ba5ff602110186a2339";
                   LogUtil.i("weather",address);
                    queryFromServer(address, new HttpUtil.HttpCallBackListener() {
                        @Override
                        public void onFinish(String response) {
                            Utility.handleWeather(WeatherActivity.this, response);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showWeather(districtName);
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    });
                }else{
                    //不可用还是查询本地的SharedPreference
                    Toast.makeText(this,"当前网络不可用",Toast.LENGTH_SHORT).show();
                    showWeather(districtName);
                }
            }
        }else{
            showWeather(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString("selectDistrict",null));
        }
    }

    public void showWeather(String districtName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        district.setText(sharedPreferences.getString("selectDistrict",districtName));
        publishTime.setText("发布时间"+sharedPreferences.getString("publishTime", ""));
        date.setText(sharedPreferences.getString("date", ""));
        weather.setText(sharedPreferences.getString("weather", ""));
        temp.setText(sharedPreferences.getString("temp", ""));
        weatherInfo.setVisibility(View.VISIBLE);
        district.setVisibility(View.VISIBLE);
    }

    public void queryFromServer(String address, HttpUtil.HttpCallBackListener listener) {
        HttpUtil.sendHttpRequest(address, listener);
    }


}
