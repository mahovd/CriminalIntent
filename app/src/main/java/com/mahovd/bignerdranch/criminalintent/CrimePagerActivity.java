package com.mahovd.bignerdranch.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;
import java.util.UUID;

/**
 * Created by mahovd on 24/11/15.
 * Controller
 */
public class CrimePagerActivity
        extends AppCompatActivity
        implements CrimeFragment.Callbacks{

    private static final String TAG = "CrimePagerActivity";

    private static final String EXTRA_CRIME_ID = "com.mahovd.bignerdranch.criminalintent.crime_id";
    private static final String INSERT_MODE = "com.mahovd.bignerdranch.insertMode";
    public static boolean isWorkModeInsert = false;

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    //New method for creating a new intent with crimeId
    //It uses when you need to edit some record of crime from the CrimeList
    public static Intent newIntent(Context packageContext, UUID crimeID){
        Intent intent = new Intent(packageContext,CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeID);

        return intent;
    }

    //CrimeFragment.Callbacks is supposed to be implemented in every activity which
    //hosts CrimeFragment so I provide empty implementation of onCrimeUpdated here
    @Override
    public void onCrimeUpdated(Crime crime) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crime_pager);

        final UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        isWorkModeInsert = getIntent().getBooleanExtra(INSERT_MODE,false);


        mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);
        mCrimes = CrimeLab.get(this).getCrimes();


        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        //Sets current item of array
        for (int i=0;i<mCrimes.size();i++){
            if (mCrimes.get(i).getId().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }

}
