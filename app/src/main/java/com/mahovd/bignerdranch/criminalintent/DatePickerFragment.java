package com.mahovd.bignerdranch.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by mahovd on 27/11/15.
 * Controller
 * TODO: I have to start activity from CrimeFragment
 * TODO: that should host DatePickerFragment with a full-screen fragment instead of Dialog
 * TODO: if user uses a phone and shows Fragment if user uses a tablet
 */
public class DatePickerFragment extends DialogFragment {

    private DatePicker mDatePicker;

    //I'm trying to do challenge about time
    private TimePicker mTimePicker;

    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "ru.mahovd.bignerdranch.criminalintent.date";

    private static final String TAG_DATE = "date";
    private static final String TAG_TIME = "time";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Get date passed as parameter
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        //Inflate layout
        //View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
        View v = createDateTimeTabbedView(getActivity().getLayoutInflater());

        //Get DatePicker and pass initial arguments to it
        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year,month,day,null);


        //Get TimePicker from layout
        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_date_time_picker);

        //Check SDK-version in order to use appropriate method to initialize parameters
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        } else{
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);
        }

        //Build dialog
        return new AlertDialog.Builder(getActivity()).
                setView(v).
                setTitle(R.string.date_picker_title).
                setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();


                        int hour;
                        int minute;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour = mTimePicker.getHour();
                            minute = mTimePicker.getMinute();
                        } else {
                            hour = mTimePicker.getCurrentHour();
                            minute = mTimePicker.getCurrentMinute();
                        }


                        Date date = new GregorianCalendar(year, month, day, hour, minute).getTime();


                        sendResult(Activity.RESULT_OK, date);
                    }
                }).create();
    }

    public static DatePickerFragment newInstance(Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE,date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode, Date date){
        if(getTargetFragment()==null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE,date);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }


    //create TabbedView
    private View createDateTimeTabbedView(LayoutInflater layoutInflater){

        //Inflate the XML Layout with tabs
        View mView = layoutInflater.inflate(R.layout.dialog_date_tabbed,null);

        //Extract the TabHost
        TabHost mTabHost = (TabHost) mView.findViewById(R.id.tab_host);
        mTabHost.setup();

        //Create DateTab and add to TabHost
        TabHost.TabSpec mDateTab = mTabHost.newTabSpec(TAG_DATE);
        mDateTab.setIndicator("date");
        mDateTab.setContent(R.id.date_content);
        mTabHost.addTab(mDateTab);

        //Create TimeTab and add to TabHost
        TabHost.TabSpec mTimeTab = mTabHost.newTabSpec(TAG_TIME);
        mTimeTab.setIndicator("time");
        mTimeTab.setContent(R.id.time_content);
        mTabHost.addTab(mTimeTab);

        return mView;

    }


}
