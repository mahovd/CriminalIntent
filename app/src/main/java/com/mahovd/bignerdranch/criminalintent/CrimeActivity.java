package com.mahovd.bignerdranch.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by mahovd on 06/11/15.
 * Controller
 * Creates activity with crime details
 * Inherits from SingleFrameActivity
 */

public class CrimeActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }
}
