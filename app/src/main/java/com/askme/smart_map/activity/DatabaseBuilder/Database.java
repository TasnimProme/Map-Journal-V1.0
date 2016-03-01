package com.askme.smart_map.activity.DatabaseBuilder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {
    //  table name
    public static final String fileTableName = "file_table";
    //table columnsDatabase_Name
    public static final String Column_ID = "_id";
    public static final String Column_TITLE = "title";
    public static final String Column_DESCRIPTION = "description";
    public static final String Column_TYPE = "file_type";
    public static final String Column_DATE = "date";
    public static final String Column_PATH = "path";
    public static final String Column_LOCATION = "location";
    public static final String Column_LOCATION_LOCAL_NAME = "location_local_name";


    //  table name
    public static final String TaskTableName = "task_table";
    //table columnsDatabase_Name
    public static final String Column_TASK_ID = "task_id";
    public static final String Column_TASK_NUMBER = "task_number";
    public static final String Column_TASK_BODY = "task_body";
    public static final String Column_TASK_DATE = "task_date";
    public static final String Column_TASK_TYPE = "task_type";
    public static final String Column_TASK_LOCATION = "task_location";
    public static final String Column_TASK_STATUS = "task_status";
    public static final String Column_TASK_FLAG = "task_flag";
    private static final String Logtag = "Message";
    private static final int DATABASE_VERSION = 1;
    //Database name
    private static final String Database_Name = "SmartMapDatabase.db";

    //table name
    public static final String addToFavTableName = "add_to_fav";

    public static final String Column_FAV_LOC_ID = "loc_id";
    public static final String Column_FAV_LOC_NAME = "loc_name";
    public static final String Column_FAV_LOC_LAT = "loc_lat";
    public static final String Column_FAV_LOC_LONG = "loc_long";



    //Database Create Query
    private static final String CREATE_QUERY = "CREATE TABLE " + fileTableName + " (" +
            Column_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Column_TITLE + " TEXT, " +
            Column_DESCRIPTION + " TEXT, " +
            Column_TYPE + " TEXT, " +
            Column_DATE + " TEXT, " +
            Column_PATH + " TEXT, " +
            Column_LOCATION + " TEXT, " +
            Column_LOCATION_LOCAL_NAME + " TEXT);";


    //Database Create Query
    private static final String CREATE_TASK_TABLE_QUERY = "CREATE TABLE " + TaskTableName + " (" +
            Column_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Column_TASK_NUMBER + " TEXT, " +
            Column_TASK_BODY + " TEXT, " +
            Column_TASK_DATE + " TEXT, " +
            Column_TASK_TYPE + " TEXT, " +
            Column_TASK_LOCATION + " TEXT, " +
            Column_TASK_STATUS + " TEXT, " +
            Column_TASK_FLAG + " TEXT);";


    //Database Create Query
    private static final String CREATE_FAV_TABLE_QUERY = "CREATE TABLE " + addToFavTableName + " (" +
            Column_FAV_LOC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Column_FAV_LOC_NAME + " TEXT, " +
            Column_FAV_LOC_LAT + " TEXT, " +
            Column_FAV_LOC_LONG + " TEXT);";





    public Database(Context context) {
        super(context, Database_Name, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY); //Create table FileTable
        db.execSQL(CREATE_TASK_TABLE_QUERY); //Create table TaskTable
        db.execSQL(CREATE_FAV_TABLE_QUERY); //Create table add to favourite

        Log.i(Logtag, "Table has been created.");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Starting the database


}

