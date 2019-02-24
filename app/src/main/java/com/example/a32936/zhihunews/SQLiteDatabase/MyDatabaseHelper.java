package com.example.a32936.zhihunews.SQLiteDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_USER = "create table User("
            + "id integer primary key autoincrement,"
            + "UserName text, "
            + "UserCount integer, "
            + "Head_image blob, "
            + "Password text) ";

    public static final String CREATE_NEWS = "create table News("
            + "id integer primary key autoincrement,"
            + "NewsName text,"
            + "NewsPicture int) ";   //好像没啥用了。。。

    public static final String CREATE_LIKE_COLUMN = "create table Like_column("
            + "id integer primary key autoincrement,"
            + "Owner integer, "
            + "Column_image text, "
            + "Column_description text, "
            + "Column_name text, "
            + "Column_id text) ";

    public static final String CREATE_LIKE_NEWS = "create table Like_news("
            + "id integer primary key autoincrement,"
            + "Owner integer,"
            + "News_title text, "
            + "News_image text, "
            + "News_id text) ";


    private Context mContext;

    public MyDatabaseHelper(Context context){
        super(context,"ZhiHuNews.db",null,1);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_NEWS);
        db.execSQL(CREATE_LIKE_COLUMN);
        db.execSQL(CREATE_LIKE_NEWS);
}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists User");
        db.execSQL("drop table if exists News");
        db.execSQL("drop table if exists Like_column");
        db.execSQL("drop table if exists Like_news");
    }
}
