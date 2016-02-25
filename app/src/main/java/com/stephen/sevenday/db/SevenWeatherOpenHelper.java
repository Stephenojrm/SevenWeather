package com.stephen.sevenday.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gritor on 2016/2/24.
 */
public class SevenWeatherOpenHelper extends SQLiteOpenHelper{

    /**
     * Position表建表语句
     */
    public static final String CREATE_POSITION = "create table Position(" +
            "id integer primary key autoincrement," +
            "province_name text," +
            "city_name text," +
            "district_name text)";

    public SevenWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_POSITION);//创建Position表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }
}
