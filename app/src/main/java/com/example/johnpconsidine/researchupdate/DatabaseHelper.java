package com.example.johnpconsidine.researchupdate;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by johnpconsidine on 10/31/15.
 */



public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "movement.db";


    public static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    //TABLE INFO FROM CONTRACT CLASS
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COL_1 + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COL_2 + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COL_3 + ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;

    /////\\\\\\\\\\\\\\\\\/////////////////////////////////////////////

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean insertData(String accel, String gyro, String grav) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put(FeedReaderContract.FeedEntry.COL_1, accel);
        contentValues.put(FeedReaderContract.FeedEntry.COL_2, gyro);
        contentValues.put(FeedReaderContract.FeedEntry.COL_3, grav);


        long newRowId;
        newRowId = db.insert(
                FeedReaderContract.FeedEntry.TABLE_NAME, null,
                contentValues);
        if (newRowId == -1) {
            return false;
        }
        return true;
    }
}
