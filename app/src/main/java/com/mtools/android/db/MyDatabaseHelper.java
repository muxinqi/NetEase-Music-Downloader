package com.mtools.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_FAVOURITE_LIST =
            "create table FavouriteList (" +
                    "id integer primary key autoincrement, " +
                    "musicId text, " +
                    "songName text, " +
                    "artistName text, " +
                    "songCoverUrl text, " +
                    "isShow integer)";

    public static final String CREATE_FAVOURITE_LIST_INFO =
            "create table FavouriteListInfo (" +
                    "id integer primary key autoincrement, " +
                    "listName text)";

    private Context mContext;

    private String dbName;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
        dbName = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (dbName.equals("FavouriteList.db")) {
            db.execSQL(CREATE_FAVOURITE_LIST);
        } else if (dbName.equals("FavouriteListInfo.db")) {
            db.execSQL(CREATE_FAVOURITE_LIST_INFO);
        } else {
            Toast.makeText(mContext, "Create "+dbName+" failed", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(mContext, "Create Database succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
