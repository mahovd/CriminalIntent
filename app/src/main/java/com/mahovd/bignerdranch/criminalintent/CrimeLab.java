package com.mahovd.bignerdranch.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mahovd.bignerdranch.criminalintent.database.CrimeBaseHelper;
import com.mahovd.bignerdranch.criminalintent.database.CrimeCursorWrapper;
import com.mahovd.bignerdranch.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by mahovd on 06/11/15.
 * Model
 * Factory for instances of Crime class
 */

/*Factory for base model class*/
public class CrimeLab {

    //Singleton instance
    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public void addCrime(Crime c){

        ContentValues values = getContentValues(c);

        mDatabase.insert(CrimeTable.NAME,null,values);
    }

    public void delCrime(Crime c){

        String uuidString = c.getId().toString();

        mDatabase.delete(CrimeTable.NAME,CrimeTable.Cols.UUID +" = ?", new String[]{uuidString});

    }

    //Return instance of singleton
    public static CrimeLab get(Context context) {
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    //Constructor, creates new empty ArrayList
    //then fills it with Crime objects
    private CrimeLab(Context context) {

        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();

    }

    //Returns list of crimes
    public List<Crime> getCrimes(){

        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null,null);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return crimes;

    }

    //Returns one crime from the list by id
    public Crime getCrime(UUID id){

        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + " =?",
                new String[] {id.toString()});

        try{
            if (cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();

        } finally {
            cursor.close();
        }
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();

        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME,values,CrimeTable.Cols.UUID +" = ?",new String[]{uuidString});
    }

    private static ContentValues getContentValues(Crime crime){

        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID,crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE,crime.getTitle());
        values.put(CrimeTable.Cols.DATE,crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);

        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){

        Cursor cursor = mDatabase.query(CrimeTable.NAME,
                null, //Columns - null selects all columns
                whereClause,
                whereArgs,
                null, //GroupBy
                null, //having
                null //orderBy
                    );

        return new CrimeCursorWrapper(cursor);

    }

}
