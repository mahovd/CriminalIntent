package com.mahovd.bignerdranch.criminalintent.database;

import com.mahovd.bignerdranch.criminalintent.database.CrimeDbSchema.CrimeTable;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mahovd on 05/01/16.
 */
public class CrimeBaseHelper extends SQLiteOpenHelper{

    private static final int VERSION = 1;

    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table "+CrimeDbSchema.CrimeTable.NAME + "(" +
        "_id integer primary key autoincrement, "+
                CrimeTable.Cols.UUID + ", "+
                CrimeTable.Cols.TITLE + ", "+
                CrimeTable.Cols.DATE + ", "+
                CrimeTable.Cols.SOLVED + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
