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

    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     *
     */
    private SevenDayDB(Context context){
        SevenWeatherOpenHelper dbHelper = new SevenWeatherOpenHelper(context,DB_NAME,null,1);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取SevenDayDB实例
     */
    public synchronized static SevenDayDB getInstance(Context context){
        if(sevenDayDB==null){
            sevenDayDB=new SevenDayDB(context);
        }
        return sevenDayDB;
    }

    /**
     * 将Position实例存储到数据库
     */
    public void savePosition(Position position){
        if(position!=null){
            ContentValues values = new ContentValues();
            values.put("province_name",position.getProvinceName());
            values.put("city_name",position.getCityName());
            values.put("district_name",position.getDistrictName());
            db.insert("Position",null,values);
        }
    }

    /**
     * 从数据库中读取全国所有省份信息
     */
    public List<String> loadProvices(){
        List<String> list = new ArrayList<>();
        db.rawQuery()
    }

}
