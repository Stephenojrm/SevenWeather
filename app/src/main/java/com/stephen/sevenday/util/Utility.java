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
 * Created by gritor on 2016/2/26.
 * 服务器返回的数据是JSON格式，此类用于JSON格式的解析和处理
 */
public class Utility {
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
                        //将解析出的Position数据存入到数据库中
                        sevenDayDB.savePosition(position);
                    }
                }
//                ObjectMapper mapper = new ObjectMapper();
//                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//                mapper.setVisibilityChecker(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
//                ArrayList<Position> list = mapper.readValue(array.toString(),new ArrayList<Position>(){}.getClass());
//                for(Position position : list){
//                    sevenDayDB.savePosition(position);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 处理服务器返回发的天气数据，并存入到SharedPreference中
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
