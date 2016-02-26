package com.stephen.sevenday.util;

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
    public synchronized static boolean handlePosition(SevenDayDB sevenDayDB,String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray array = jsonObject.getJSONArray("result");
                if(array.length()>0 && array!=null){
                    Position position = new Position();
                    for(int i=0;i<array.length();i++){
                        JSONObject json = array.getJSONObject(i);
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
