package com.mahovd.bignerdranch.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.UUID;

/**
 * Created by mahovd on 08/11/15.
 * Controller
 * Creates activity with list of crimes
 * Inherits from SingleFrameActivity
 */
public class CrimeListActivity
        extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks{


    private static final int REQUEST_CRIME = 1;
    private static final String TAG = "CrimeListActivity";


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CRIME){
            if(data==null){
                return;
            }else{
                Log.d(TAG, "onActivityResult and data is not null");
                //TODO: Is it really good decision to use idChangedItem and isChangedItemWasDeleted
                //TODO: as public static variables??? I  have to find out more.
                CrimeListFragment.idChangedItem = (UUID) data.getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
                if(data.getBooleanExtra(CrimeFragment.EXTRA_CRIME_DELETED,false)){
                    CrimeListFragment.isChangedItemWasDeleted = true;
                }
                onCrimeUpdated(CrimeLab.get(this).getCrime(CrimeListFragment.idChangedItem));
            }
        }

    }

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }


    @Override
    public void onCrimeSelected(Crime crime) {

        //Check whether the detail_fragment_container exists
        // if yes - add CrimeFragment to detail_fragment_container
        if(findViewById(R.id.detail_fragment_container) == null){
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            //startActivity(intent);
            startActivityForResult(intent,REQUEST_CRIME);
        } else{
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.detail_fragment_container,newDetail).commit();
        }

    }


    //Updates UI of fragment after certain crime was changed
    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager().
                findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
