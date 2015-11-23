package com.mahovd.bignerdranch.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

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
    public static final String EXTRA_CRIME_ID = "ru.mahovd.bignerdranch.criminalintent.crime_id";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
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
                returnResult();
            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);

        //mDateButton.setText(DateFormat.getMediumDateFormat(getContext()).format(mCrime.getDate()));
        //It is not the best practise to use your own format string, instead we should use system format
        // as shown above
        mDateButton.setText(DateFormat.format("cccc, MMM d, yyyy", mCrime.getDate()));

        mDateButton.setEnabled(false);

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Set the crime's solved property
                mCrime.setSolved(isChecked);
                Log.d(TAG, "crime_solved was changed");
                returnResult();
            }
        });



        return v;
    }

    private void returnResult(){
        Intent data = new Intent();
        //TODO: Fix it
        data.putExtra(EXTRA_CRIME_ID,mCrime.getId());
        getActivity().setResult(Activity.RESULT_OK,data);
    }

}
