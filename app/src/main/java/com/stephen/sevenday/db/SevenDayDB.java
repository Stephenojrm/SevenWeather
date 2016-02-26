package com.stephen.sevenday.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.stephen.sevenday.model.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephen on 2016/2/24.
 * 此类封装了一些常用的数据库操作，数据库实例采用单例模式
 */
public class SevenDayDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "seven_day";

    /**
     * 数据库版本号
     */
    public static final int VERSION = 1;

    private static SevenDayDB sevenDayDB;

    private static SQLiteDatabase db;

    /**
     * 将构造方法私有化
     */
    private SevenDayDB(Context context) {
        SevenWeatherOpenHelper dbHelper = new SevenWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取SevenDayDB实例
     */
    public synchronized static SevenDayDB getInstance(Context context) {
        if (sevenDayDB == null) {
            sevenDayDB = new SevenDayDB(context);
        }
        return sevenDayDB;
    }

    /**
     * 将Position实例存储到数据库
     */
    public void savePosition(Position position) {
        if (position != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", position.getProvinceName());
            values.put("city_name", position.getCityName());
            values.put("district_name", position.getDistrictName());
            db.insert("Position", null, values);
        }
    }

    /**
     * 从数据库中读取全国所有省份信息
     */
    public static List<String> loadProvinces() {
        List<String> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select distinct province_name from Position", null);
        if (cursor.moveToFirst()) {
            do {
                String province_name = cursor.getString(0);
                list.add(province_name);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 从数据库中读取省份下的城市
     */
    public static List<String> loadCities(String provinceName) {
        List<String> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select distinct city_name from Position where province_name = ?", new String[]{provinceName});
        if (cursor.moveToFirst()) {
            do {
                String city_name = cursor.getString(0);
                list.add(city_name);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 从数据库中读取城市下的县
     */
    public static List<String> loadDistricts(String cityName) {
        List<String> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select distinct district_name from Position where city_name = ?", new String[]{cityName});
        if (cursor.moveToFirst()) {
            do {
                String district_name = cursor.getString(0);
                list.add(district_name);
            } while (cursor.moveToNext());
        }
        return list;
    }


}
