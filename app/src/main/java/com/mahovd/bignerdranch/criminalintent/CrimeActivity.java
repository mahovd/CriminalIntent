package com.mahovd.bignerdranch.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * Created by mahovd on 06/11/15.
 * Controller
 * Creates activity with crime details
 * Inherits from SingleFrameActivity
 */

public class CrimeActivity extends SingleFragmentActivity {

    private static final String EXTRA_CRIME_ID = "com.mahovd.bignerdranch.criminalintent.crime_id";

    //New method for creating a new intent with crimeId
    //It uses when you need to edit some record of crime from the CrimeList
    public static Intent newIntent(Context packageContext, UUID crimeID){
        Intent intent = new Intent(packageContext,CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,crimeID);
        return intent;
    }

    //This method was refactored
    //Since it CrimeFragment creates by calling CrimeFragment.newInstance method
    //It's supposed to do because we have to keep crimeId in arguments bundle
    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }
}
