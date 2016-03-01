package com.askme.smart_map.activity.DatabaseBuilder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBadapter {
    private Database database;
    private Context context;
    private SQLiteDatabase db;

    public DBadapter(Context context) {
        this.context = context;
        database = new Database(context);
        db = database.getWritableDatabase();
    }


    public void insertFile(MyFile saveFile) {
        ContentValues values = new ContentValues();
        values.put(database.Column_TITLE, saveFile.getTitle());
        values.put(database.Column_DESCRIPTION, saveFile.getDescription());
        values.put(database.Column_TYPE, saveFile.getType());
        values.put(database.Column_DATE, saveFile.getDate());
        values.put(database.Column_PATH, saveFile.getPath());
        values.put(database.Column_LOCATION, saveFile.getLocation());
        values.put(database.Column_LOCATION_LOCAL_NAME, saveFile.getLocationLocalName());
        long insert = db.insert(database.fileTableName, null, values);
    }


    public ArrayList<MyFile> getFileDetails() {
        ArrayList<MyFile> FileDetails = new ArrayList<>();
        String id, title, description, type, date, path, location, locationLocalName;
        String select_date_query = "SELECT " +
                "* FROM " + database.fileTableName;
        Cursor cursor = db.rawQuery(select_date_query, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {

                id = cursor.getString(cursor.getColumnIndex(database.Column_ID));
                title = cursor.getString(cursor.getColumnIndex(database.Column_TITLE));
                description = cursor.getString(cursor.getColumnIndex(database.Column_DESCRIPTION));
                type = cursor.getString(cursor.getColumnIndex(database.Column_TYPE));
                date = cursor.getString(cursor.getColumnIndex(database.Column_DATE));
                path = cursor.getString(cursor.getColumnIndex(database.Column_PATH));
                location = cursor.getString(cursor.getColumnIndex(database.Column_LOCATION));
                locationLocalName = cursor.getString(cursor.getColumnIndex(database.Column_LOCATION_LOCAL_NAME));

                MyFile myFile = new MyFile(id, title, description, type, date, path, location, locationLocalName);

                FileDetails.add(myFile);
                cursor.moveToNext();
            }
        }
        return FileDetails;
    }

    public ArrayList<MyFile> getFileWithImageDetails() {
        ArrayList<MyFile> FileDetails = new ArrayList<>();
        String id, title, description, type, date, path, location, locationLocalName;
        String select_date_query = "SELECT " +
                "* FROM " + database.fileTableName;
        Cursor cursor = db.rawQuery(select_date_query, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                type = cursor.getString(cursor.getColumnIndex(database.Column_TYPE));
                if (type.equalsIgnoreCase("PICTURE") || type.equalsIgnoreCase("NOTE,PICTURE")) {
                    id = cursor.getString(cursor.getColumnIndex(database.Column_ID));
                    title = cursor.getString(cursor.getColumnIndex(database.Column_TITLE));
                    description = cursor.getString(cursor.getColumnIndex(database.Column_DESCRIPTION));
                    date = cursor.getString(cursor.getColumnIndex(database.Column_DATE));
                    path = cursor.getString(cursor.getColumnIndex(database.Column_PATH));
                    location = cursor.getString(cursor.getColumnIndex(database.Column_LOCATION));
                    locationLocalName = cursor.getString(cursor.getColumnIndex(database.Column_LOCATION_LOCAL_NAME));

                    MyFile myFile = new MyFile(id, title, description, type, date, path, location, locationLocalName);
                    FileDetails.add(myFile);
                }
                cursor.moveToNext();
            }
        }

        return FileDetails;
    }


    public void insertTask(String task_number, String task_body, String task_date, String task_type, String task_location, String task_status, String task_flag) {
        ContentValues values = new ContentValues();
        values.put(database.Column_TASK_NUMBER, task_number);
        values.put(database.Column_TASK_BODY, task_body);
        values.put(database.Column_TASK_DATE, task_date);
        values.put(database.Column_TASK_TYPE, task_type);
        values.put(database.Column_TASK_LOCATION, task_location);
        values.put(database.Column_TASK_STATUS, task_status);
        values.put(database.Column_TASK_FLAG, task_flag);
        long insert = db.insert(database.TaskTableName, null, values);

    }


    public int getTaskID(String flag) {
        int id = 0;
        String select_id = "SELECT " +
                database.Column_TASK_ID +
                " FROM " + database.TaskTableName +
                " WHERE " + database.Column_TASK_FLAG + "=" + "'" + flag + "'";
        Cursor cursor = db.rawQuery(select_id, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndex(database.Column_TASK_ID));
        }

        return id;

    }

    public ArrayList<String[]> getSmsTaskDetails() {

        String sms_id, sms_number, sms_body, sms_date, sms_type, sms_status, sms_location, sms_flag;
        String select_date_query = "SELECT " +
                "* FROM " + database.TaskTableName + " WHERE " + database.Column_TASK_TYPE + " = 'SMS+TIME' OR "+database.Column_TASK_TYPE + " = 'SMS+LOCATION'";
        Cursor cursor = db.rawQuery(select_date_query, null);

        ArrayList<String[]> FileDetails = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {

                sms_id = cursor.getString(cursor.getColumnIndex(database.Column_TASK_ID));
                sms_number = cursor.getString(cursor.getColumnIndex(database.Column_TASK_NUMBER));
                sms_body = cursor.getString(cursor.getColumnIndex(database.Column_TASK_BODY));
                sms_date = cursor.getString(cursor.getColumnIndex(database.Column_TASK_DATE));
                sms_type = cursor.getString(cursor.getColumnIndex(database.Column_TASK_TYPE));
                sms_location = cursor.getString(cursor.getColumnIndex(database.Column_TASK_LOCATION));
                sms_status = cursor.getString(cursor.getColumnIndex(database.Column_TASK_STATUS));
                sms_flag = cursor.getString(cursor.getColumnIndex(database.Column_TASK_FLAG));

                String[] temp = {sms_id, sms_number, sms_body, sms_date, sms_type, sms_location, sms_status, sms_flag};

                FileDetails.add(temp);

                cursor.moveToNext();
            }
        }
        return FileDetails;
    }


    public void insertFavPlace(String place_name,String place_lat,String place_long) {
        ContentValues values = new ContentValues();
        values.put(database.Column_FAV_LOC_NAME, place_name);
        values.put(database.Column_FAV_LOC_LAT, place_lat);
        values.put(database.Column_FAV_LOC_LONG, place_long);
        long insert = db.insert(database.addToFavTableName, null, values);
    }

    public ArrayList<String[]> getFavPlaceDetails() {

        String place_id,place_name,place_lat,place_long;
        String select_date_query = "SELECT " +
                "* FROM " + database.addToFavTableName;
        Cursor cursor = db.rawQuery(select_date_query, null);

        ArrayList<String[]> FileDetails = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                place_id = cursor.getString(cursor.getColumnIndex(database.Column_FAV_LOC_ID));
                place_name= cursor.getString(cursor.getColumnIndex(database.Column_FAV_LOC_NAME));
                place_lat = cursor.getString(cursor.getColumnIndex(database.Column_FAV_LOC_LAT));
                place_long = cursor.getString(cursor.getColumnIndex(database.Column_FAV_LOC_LONG));
                String[] temp = {place_id,place_name,place_lat,place_long};


                FileDetails.add(temp);

                cursor.moveToNext();
            }
        }
        return FileDetails;
    }

    public void deleteFromFav(String id)
    {
        db.execSQL("DELETE FROM "+database.addToFavTableName+" WHERE "+database.Column_FAV_LOC_ID+"="+"'"+id+"'");
    }


    public ArrayList<String[]> getReminderTaskDetails() {
        String sms_id, sms_number, sms_body, sms_date, sms_type, sms_status, sms_location, sms_flag;
        String select_date_query = "SELECT " +
                "* FROM " + database.TaskTableName + " WHERE " + database.Column_TASK_TYPE + " = 'REMINDER+TIME' OR "+database.Column_TASK_TYPE + " = 'REMINDER+LOCATION'";
        Cursor cursor = db.rawQuery(select_date_query, null);

        ArrayList<String[]> FileDetails = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {

                sms_id = cursor.getString(cursor.getColumnIndex(database.Column_TASK_ID));
                sms_number = cursor.getString(cursor.getColumnIndex(database.Column_TASK_NUMBER));
                sms_body = cursor.getString(cursor.getColumnIndex(database.Column_TASK_BODY));
                sms_date = cursor.getString(cursor.getColumnIndex(database.Column_TASK_DATE));
                sms_type = cursor.getString(cursor.getColumnIndex(database.Column_TASK_TYPE));
                sms_location = cursor.getString(cursor.getColumnIndex(database.Column_TASK_LOCATION));
                sms_status = cursor.getString(cursor.getColumnIndex(database.Column_TASK_STATUS));
                sms_flag = cursor.getString(cursor.getColumnIndex(database.Column_TASK_FLAG));

                String[] temp = {sms_id, sms_number, sms_body, sms_date, sms_type, sms_location, sms_status, sms_flag};

                FileDetails.add(temp);

                cursor.moveToNext();
            }
        }
        return FileDetails;
    }

    public void UpdateTaskStatus(String id) {
        String change_pass = "UPDATE " + database.TaskTableName + " SET " + database.Column_TASK_STATUS + " ='SUCCESS'" + " WHERE " +
                database.Column_TASK_ID + "=" + "'" + id + "'" + " AND " + database.Column_TASK_STATUS + " ='PENDING'";
        db.execSQL(change_pass);

    }
}
