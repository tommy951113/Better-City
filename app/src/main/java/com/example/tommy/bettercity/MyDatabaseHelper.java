package com.example.tommy.bettercity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by tommy on 2015/11/29.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_USERINFO = "create table userinfo(" +
            "loginname text primary key ," +
            "password text," +
            "username text," +
            "sex text default null," +
            "birthday text default null," +
            "homeland text default null," +
            "email text default null," +
            "is_login text)";

    private Context mContext;
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERINFO);
        Toast.makeText(mContext, "create succeed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists userinfo");
        onCreate(db);
        Log.d("register","recreate success!");
    }
}
