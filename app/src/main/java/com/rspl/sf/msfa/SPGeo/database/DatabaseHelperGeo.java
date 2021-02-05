package com.rspl.sf.msfa.SPGeo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelperGeo extends SQLiteOpenHelper {

    private static DatabaseHelperGeo databaseHelperGeo = null;
    private static Context context = null;

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "location_db";

//    Singleton Class

    public static DatabaseHelperGeo getInstance(Context context)
    {

        if (databaseHelperGeo == null) {
            databaseHelperGeo = new DatabaseHelperGeo(context);
        }

        return databaseHelperGeo;
    }

    private DatabaseHelperGeo(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context= context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(LocationBean.CREATE_TABLE);
        db.execSQL(ServiceStartStopBean.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        if(newVersion>oldVersion){
           /* if(context!=null) {
                Constants.getDataFromSqliteDB(context);
            }*/
            db.execSQL("ALTER TABLE "+LocationBean.TABLE_NAME+ " ADD COLUMN Distance TEXT");
        }
//        db.execSQL("DROP TABLE IF EXISTS " + LocationBean.TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + ServiceStartStopBean.TABLE_NAME);

        // Create tables again
//        onCreate(db);
    }

    public void createRecord(LocationBean syncHistoryModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(LocationBean.COLUMNSPNO, syncHistoryModel.getColumnSpno());
            contentValues.put(LocationBean.COLUMNSPNAME, syncHistoryModel.getColumnSpname());
            contentValues.put(LocationBean.COLUMNLAT, syncHistoryModel.getColumnLat());
            contentValues.put(LocationBean.COLUMNLONG, syncHistoryModel.getColumnLong());
            contentValues.put(LocationBean.COLUMNSTARTDATE, syncHistoryModel.getColumnStartdate());
            contentValues.put(LocationBean.COLUMNSTARTTIME, syncHistoryModel.getColumnStarttime());
            contentValues.put(LocationBean.COLUMNStatus, syncHistoryModel.getCOLUMN_Status());
            contentValues.put(LocationBean.COLUMNTEMPNO, syncHistoryModel.getColumnTempno());
            contentValues.put(LocationBean.COLUMNTIMESTAMP, syncHistoryModel.getColumnTimestamp());
            contentValues.put(LocationBean.COLUMNAPPVISBILITY, syncHistoryModel.getCOLUMN_AppVisibility());
            contentValues.put(LocationBean.COLUMNBATTERYLEVEL, syncHistoryModel.getCOLUMN_BATTERYLEVEL());
            contentValues.put(LocationBean.COLUMNDISTANCE, syncHistoryModel.getCOLUMN_DISTANCE());
            db.insert(LocationBean.TABLE_NAME, (String) null, contentValues);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createRecordService(ServiceStartStopBean syncHistoryModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ServiceStartStopBean.COLUMNTIME, syncHistoryModel.getTime());
            contentValues.put(ServiceStartStopBean.COLUMNDESCRIPTION, syncHistoryModel.getServiceDescription());
            db.insert(ServiceStartStopBean.TABLE_NAME, (String) null, contentValues);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<LocationBean> getData(){
            List movieDetailsList = new ArrayList();
            String selectQuery = "SELECT * FROM " + LocationBean.TABLE_NAME;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            //if TABLE has rows
            if (cursor.moveToFirst()) {
                //Loop through the table rows
                do {
                    LocationBean movieDetails = new LocationBean();
                    movieDetails.setColumnSpno(cursor.getString(0));
                    movieDetails.setColumnSpname(cursor.getString(1));
                    movieDetails.setColumnLat(cursor.getString(2));
                    movieDetails.setColumnLong(cursor.getString(3));
                    movieDetails.setColumnStartdate(cursor.getString(4));
                    movieDetails.setColumnStarttime(cursor.getString(5));
                    movieDetails.setCOLUMN_Status(cursor.getString(6));
                    movieDetails.setColumnTempno(cursor.getString(7));
                    movieDetails.setColumnTimestamp(cursor.getString(8));

                    //Add movie details to list
                    movieDetailsList.add(movieDetails);
                } while (cursor.moveToNext());
            }
            db.close();
            return movieDetailsList;
    }

    public void deleteLatLong(String id) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + LocationBean.TABLE_NAME + " where "+LocationBean.COLUMNID+"='" + id + "'";
        Log.d("query", deleteQuery);
        database.execSQL(deleteQuery);
    }

    public void deleteStudent(String tempNo) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + LocationBean.TABLE_NAME + " where "+LocationBean.COLUMNTEMPNO+"='" + tempNo + "'";
        Log.d("query", deleteQuery);
        database.execSQL(deleteQuery);
    }

    public Cursor getLatLongDetails(String tempNo) {
        SQLiteDatabase database = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + LocationBean.TABLE_NAME + " where "+LocationBean.COLUMNTEMPNO+"='" + tempNo + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getDataLatLong(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + LocationBean.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public int getSqlLocationDataCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + LocationBean.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount();
    }

    public Cursor getDataService(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + ServiceStartStopBean.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }


}
