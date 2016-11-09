package com.example.espressage.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.espressage.global.DataInterface;

/**
 * Created by Administrator on 2016/7/2 0002.
 */
public class MyDbOpenHelper extends SQLiteOpenHelper {
    public static SQLiteDatabase dbHelper;

    /**
     * @param context 上下文
     *                name  数据库的名称
     *                factory  游标工厂  null用默认的游标工厂
     *                version  版本号>=1的整数
     */
    //写一个类继承SQLiteOpenHelper
    public MyDbOpenHelper(Context context) {
        super(context, "express.db", null, 1);
        dbHelper = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table expressInfo(_id integer primary key autoincrement,mailNo varchar(20),simpleName varchar(20),imgUrl varchar(20),expName varchar(20),phone varchar(20),goodsName varchar(20),content varchar(20) ,time long(20),status varchar(20))");
        db.execSQL("create table info(_id integer primary key autoincrement,expName varchar(20),phone varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
