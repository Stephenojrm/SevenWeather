package com.stephen.sevenday.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.stephen.sevenday.db.SevenDayDB;
import com.stephen.sevenday.model.Position;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by stephen on 2016/2/26
 */
public class Utility{
    /**
     * 对从服务器获取的Json数据进行解析，并存储到数据库中
     *
     * @param sevenDayDB 要存储数据的数据库
     * @param response  服务器返回的Json数据
     * @return
     */
    public synchronized static boolean handlePosition(SevenDayDB sevenDayDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray array = jsonObject.getJSONArray("result");
                if (array != null && array.length() > 0) {
                    Position position = new Position();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json;
                        json = array.getJSONObject(i);
                        String provinceName = json.getString("province");
                        String cityName = json.getString("city");
                        String district_name = json.getString("district");
                        position.setProvinceName(provinceName);
                        position.setCityName(cityName);
                        position.setDistrictName(district_name);
                        sevenDayDB.savePosition(position);//将解析出的Position数据存入到数据库中
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 处理服务器返回的天气json数据，并存储到SharedPreference中
     *
     * @param context
     * @param response 服务器返回的json数据
     * @return
     */
    public static boolean handleWeather(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject result = jsonObject.getJSONObject("result");
            JSONObject sk = result.getJSONObject("sk");
            JSONObject today = result.getJSONObject("today");
            String date = today.getString("date_y");
            String weather = today.getString("weather");
            String temp = today.getString("temperature");
            String publishTime = sk.getString("time");
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putBoolean("isFirstCome",true);
            editor.putString("date", date);
            editor.putString("weather", weather);
            editor.putString("temp", temp);
            editor.putString("publishTime", publishTime);
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;

    }


}
