package com.mahovd.bignerdranch.criminalintent;

import android.content.Context;

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

    //List of crimes
    private List<Crime> mCrimes;

    public void addCrime(Crime c){
        mCrimes.add(c);
    }

    public void delCrime(Crime c){
        mCrimes.remove(c);
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
        mCrimes = new ArrayList<>();

    }

    //Returns list of crimes
    public List<Crime> getCrimes(){
        return mCrimes;
    }

    //Returns one crime from the list by id
    public Crime getCrime(UUID id){
        for (Crime crime: mCrimes) {
            if (crime.getId().equals(id)){
                return crime;
            }

        }
        return null;
    }
}
