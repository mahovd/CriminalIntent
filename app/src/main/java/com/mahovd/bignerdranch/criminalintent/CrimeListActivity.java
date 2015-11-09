package com.mahovd.bignerdranch.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by mahovd on 08/11/15.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
