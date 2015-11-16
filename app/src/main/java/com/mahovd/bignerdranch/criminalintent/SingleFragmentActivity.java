package com.mahovd.bignerdranch.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by mahovd on 08/11/15.
 * Controller
 * Creates activity with one fragment (is inherited by CrimeActivity and CrimeListActivity)
 */
public abstract class SingleFragmentActivity extends FragmentActivity{

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment==null){
            fragment = createFragment();
            fm.beginTransaction().
                    add(R.id.fragment_container,fragment).
                    commit();
        }

    }

}
