package com.example.logictest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

public class BestResultDB extends Exception {

    public static final String KEY_LEVELID = "_id";
    public static final String KEY_TIME = "_time";

    private final String DATABASE_NAME = "ResultsDb";
    private final String DATABASE_TABLE = "ResultsTable";
    private final int DATABASE_VERSION = 1;

    private DBHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    public BestResultDB(Context context) {
        ourContext = context;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sqlCode = "CREATE TABLE " + DATABASE_TABLE + " (" +
                    KEY_LEVELID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_TIME + " TEXT NOT NULL);";

            db.execSQL(sqlCode);
        }
    }

    public BestResultDB open() throws SQLException
    {
        ourHelper = new DBHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        ourHelper.close();
    }

    public long createEntry(String time){
        ContentValues cv = new ContentValues();
        cv.put(KEY_TIME, time);
        return ourDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public String getData(int levelId){
        String [] columns = new String [] {KEY_LEVELID, KEY_TIME};
        Cursor cursor = ourDatabase.query(DATABASE_TABLE, columns, null,
                null, null, null, null);

        int iTime = cursor.getColumnIndex(KEY_TIME);

        cursor.move(levelId);
        String result = cursor.getString(iTime).trim();

        cursor.close();

        return result;
    }

    public boolean exists(){
        String [] columns = new String [] {KEY_LEVELID, KEY_TIME};
        Cursor cursor = ourDatabase.query(DATABASE_TABLE, columns, null,
                null, null, null, null);

        if (cursor.getCount() > 0) return true;
        else return false;
    }

    public long updateEntry(String levelId, String time){
        ContentValues cv = new ContentValues();
        cv.put(KEY_TIME, time);
        return ourDatabase.update(DATABASE_TABLE, cv, KEY_LEVELID + "=?", new String[]{levelId});
    }
}
