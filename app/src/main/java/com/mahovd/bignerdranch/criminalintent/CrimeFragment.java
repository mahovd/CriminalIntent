package com.mahovd.bignerdranch.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by mahovd on 01/11/15.
 * Controller
 * Creates fragment with details of a crime
 */
public class CrimeFragment extends Fragment {

    private Crime    mCrime;
    private File     mPhotoFile;
    private EditText mTitleField;
    private Button   mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button   mReportButton;
    private Button   mSuspectButton;
    private Button   mCallSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private int mPhotoWidth = 0, mPhotoHeight = 0;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String TAG = "CrimeFragment";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String IMAGE_PREVIEW = "ImagePreview";
    public static final String EXTRA_CRIME_ID = "ru.mahovd.bignerdranch.criminalintent.crime_id";
    public static final String EXTRA_CRIME_DELETED = "ru.mahovd.bignerdranch.criminalintent.del_mark";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    private static boolean isMultiPaneMode = false;

    private Callbacks mCallbacks;

    /*Required interface for hosting activities*/
    public interface Callbacks{
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        //Restore the main argument from bundle's arguments store
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    public static CrimeFragment newInstance(UUID crimeId, boolean multiPaneMode){

        //We have to persist our main argument in fragment arguments,
        //because at this way it won't be thrown away if configuration changes
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        isMultiPaneMode = multiPaneMode;

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
            updateCrime();
            updateDate();
        }

        if(requestCode==REQUEST_CONTACT && data != null){
            Uri contractUri = data.getData();

            //Specify which fields you want your query to return values for
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};

            //Perform your query - the contactUri is like a where clause here
            Cursor c = getActivity().getContentResolver().query(contractUri,queryFields,null,null,null);

            try{
                //Double-check that you actually got results
                if(c.getCount() == 0){
                    return;
                }

                //Pull out the first column of the first row of data that is your suspect's name
                c.moveToFirst();
                String suspect = c.getString(0);
                Long suspectId = c.getLong(1);
                mCrime.setSuspect(suspect);
                mCrime.setSuspectId(suspectId);
                updateCrime();
                mSuspectButton.setText(suspect);
                mCallSuspectButton.setEnabled(true);
            } finally {
                c.close();
            }
        }

        if(requestCode==REQUEST_PHOTO){
            updateCrime();
            updatePhotoView();
        }

    }

    //Updates crime in DB and calls method for updating it in CrimeListFragment
    private void updateCrime(){
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_item_del_crime:
                if(!isMultiPaneMode){
                    returnResult(true);
                    getActivity().finish();
                }else{
                    CrimeListFragment.idChangedItem = mCrime.getId();
                    CrimeListFragment.isChangedItemWasDeleted = true;
                    mCallbacks.onCrimeUpdated(mCrime);
                }
                return true;
            case android.R.id.home:
                //TODO: I shouldn't forget about Back button
                //When I hit it in edit_mode, only first element will be updated
                //I should fix it
                returnResult(false);
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void updateDate() {
        mDateButton.setText(DateFormat.format("cccc, MMM d, yyyy HH:mm", mCrime.getDate()));
    }

    private String getCrimeReport(){

        String solvedString = null;

        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }else{
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MM dd";
        String dateString = DateFormat.format(dateFormat,mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if(suspect==null){
            suspect = getString(R.string.crime_report_no_suspect);
        } else{
          suspect = getString(R.string.crime_report_suspect,suspect);
        }

        String report = getString(R.string.crime_report,mCrime.getTitle(), dateString, solvedString, suspect);

        return report;

    }

    private void updatePhotoView(){
        if(mPhotoFile==null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),mPhotoWidth,mPhotoHeight);
            mPhotoView.setImageBitmap(bitmap);
        }
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
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //This one too
                Log.d(TAG,"afterTextChanged was called");
                //returnResult(false);
            }
        });

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(getActivity().getPackageManager()) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if(canTakePhoto){
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);



        final ViewTreeObserver observer =  mPhotoView.getViewTreeObserver();

        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                    //Log.d(TAG,mPhotoView.getHeight()+" "+mPhotoView.getWidth());
                    mPhotoHeight = mPhotoView.getHeight();
                    mPhotoWidth =  mPhotoView.getWidth();
                    mPhotoView.getViewTreeObserver().removeOnPreDrawListener(this);
                    updatePhotoView();
                    return true;
            }
        });



        if (mPhotoFile!=null && mPhotoFile.exists()){
            mPhotoView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    ImagePreviewFragment imagePreview = ImagePreviewFragment.newInstance(mPhotoFile);
                    imagePreview.show(manager,IMAGE_PREVIEW);
                }
            });
        }


        mDateButton = (Button) v.findViewById(R.id.crime_date);

        //mDateButton.setText(DateFormat.getMediumDateFormat(getContext()).format(mCrime.getDate()));
        //It is not the best practise to use your own format string, instead we should use system format
        // as shown above

        updateDate();

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
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
                updateCrime();
                Log.d(TAG, "crime_solved was changed");
                //returnResult(false);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               ShareCompat.IntentBuilder.from(getActivity()).
                       setType("text/plain").
                       setText(getCrimeReport()).
                       setSubject(getString(R.string.crime_report_subject)).
                       setChooserTitle(getString(R.string.send_report)).
                       startChooser();
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(pickContact, REQUEST_CONTACT);

            }
        });

        if(mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }

        mCallSuspectButton = (Button) v.findViewById(R.id.call_suspect);
        if(mCrime.getSuspect() == null){
            mCallSuspectButton.setEnabled(false);
        }

        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String suspectPhoneNumber = getSuspectPhone(mCrime.getSuspectId());

                Uri number = Uri.parse("tel:"+suspectPhoneNumber);

                final Intent callSuspect = new Intent(Intent.ACTION_DIAL, number);

                //Toast.makeText(getActivity(),suspectPhoneNumber,Toast.LENGTH_LONG).show();
                startActivity(callSuspect);


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

    private String getSuspectPhone(Long suspectId){

        String suspectPnone = "";

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] queryFields = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER};

        String mSelectionClause = ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID +" = ?" +
                " AND  " + ContactsContract.CommonDataKinds.Phone.MIMETYPE + "='"
                + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'";

        String[] mSelectionArgs = new String[] {String.valueOf(suspectId)};

        Cursor cursor = getActivity().getContentResolver().query(uri,queryFields,mSelectionClause,mSelectionArgs,null);


        try{
            //Double-check that you actually got results
            if(cursor.getCount() == 0){
                return suspectPnone;
            }
            //Pull out the first column of the first row of data that is your suspect's name
            cursor.moveToFirst();
            suspectPnone = cursor.getString(0);
        } finally {
            cursor.close();
        }

        return suspectPnone;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
