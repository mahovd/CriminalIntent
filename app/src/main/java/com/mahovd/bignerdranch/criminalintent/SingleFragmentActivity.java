package com.mahovd.bignerdranch.criminalintent;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by mahovd on 08/11/15.
 * Controller
 * Creates activity with one fragment (is inherited by CrimeListActivity)
 */
public abstract class SingleFragmentActivity extends AppCompatActivity{

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment==null){
            fragment = createFragment();
            fm.beginTransaction().
                    add(R.id.fragment_container,fragment).
                    commit();
        }

    }

    //Returns the Id of the layout that the activity will inflate
    @LayoutRes
    protected int getLayoutResId() {

        return R.layout.activity_masterdetail;

    }

}
