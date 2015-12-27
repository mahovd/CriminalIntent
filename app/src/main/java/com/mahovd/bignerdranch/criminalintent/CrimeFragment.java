package com.mahovd.bignerdranch.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

/**
 * Created by mahovd on 01/11/15.
 * Controller
 * Creates fragment with details of a crime
 */
public class CrimeFragment extends Fragment {

    private Crime    mCrime;
    private EditText mTitleField;
    private Button   mDateButton;
    private CheckBox mSolvedCheckBox;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String TAG = "CrimeFragment";
    private static final String DIALOG_DATE = "DialogDate";
    public static final String EXTRA_CRIME_ID = "ru.mahovd.bignerdranch.criminalintent.crime_id";
    public static final String EXTRA_CRIME_DELETED = "ru.mahovd.bignerdranch.criminalintent.del_mark";
    private static final int REQUEST_DATE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        //Restore the main argument from bundle's arguments store
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    public static CrimeFragment newInstance(UUID crimeId){

        //We have to persist our main argument in fragment arguments,
        //because at this way it won't be thrown away if configuration changes
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=Activity.RESULT_OK){
            return;
        }
        if(requestCode==REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_item_del_crime:
                //TODO:If it's creating mode I should delete the crime
                //CrimeLab.get(getActivity()).delCrime(mCrime);
                returnResult(true);
                getActivity().finish();
                return true;
            case android.R.id.home:
                //TODO: I shouldn't forget about Back button
                returnResult(false);
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void updateDate() {
        mDateButton.setText(DateFormat.format("cccc, MMM d, yyyy HH:mm", mCrime.getDate()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime,container,false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());

        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //This one too
                Log.d(TAG,"afterTextChanged was called");
                //returnResult(false);
            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);

        //mDateButton.setText(DateFormat.getMediumDateFormat(getContext()).format(mCrime.getDate()));
        //It is not the best practise to use your own format string, instead we should use system format
        // as shown above

        updateDate();

        mDateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                dialog.show(manager,DIALOG_DATE);
                //returnResult(false);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Set the crime's solved property
                mCrime.setSolved(isChecked);
                Log.d(TAG, "crime_solved was changed");
                //returnResult(false);
            }
        });



        return v;
    }

    private void returnResult(boolean isMarkAsDeleted){
        Intent data = new Intent();
        data.putExtra(EXTRA_CRIME_ID,mCrime.getId());
        data.putExtra(EXTRA_CRIME_DELETED,isMarkAsDeleted);
        getActivity().setResult(Activity.RESULT_OK,data);
    }

}
