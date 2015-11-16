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

    //Return instance of singleton
    public static CrimeLab get(Context context) {
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    //Constructor, creates new empty ArrayList
    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();

        /*Generating crimes*/
        for (int i=0; i<100; i++){
            Crime crime = new Crime();
            crime.setTitle("Crime #"+i);
            crime.setSolved(i%2 ==0); //Every other one
            mCrimes.add(crime);
        }

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
