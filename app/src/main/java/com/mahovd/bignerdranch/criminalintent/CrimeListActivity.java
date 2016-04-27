package com.mahovd.bignerdranch.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by mahovd on 08/11/15.
 * Controller
 * Creates activity with list of crimes
 * Inherits from SingleFrameActivity
 */
public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks{

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
            startActivity(intent);
        } else{
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.detail_fragment_container,newDetail).commit();
        }

    }
}
