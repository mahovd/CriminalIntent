package com.mahovd.bignerdranch.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by mahovd on 08/11/15.
 * Controller
 * Creates list of crimes with RecyclerView
 * It consists of three important parts:
 * 1) CrimeHolder
 * 2) CrimeAdapter
 * 3) Methods onCreateView and updateUI
 **/

public class CrimeListFragment extends Fragment {

    private static final int REQUEST_CRIME = 1;
    private static final String TAG = "CrimeListFragment";
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final String INSERT_MODE = "com.mahovd.bignerdranch.insertMode";

    private RecyclerView mCrimeRecyclerView;
    private TextView mEmptyView;
    private CrimeAdapter mAdapter;
    private UUID idChangedItem;
    private boolean isChangedItemWasDeleted = false;

    private boolean mSubtitleVisible;

    private Callbacks mCallbacks;

    /*Required interface for hosting activities*/
    public interface Callbacks{
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //I should explicitly tell the FragmentManager that my fragment should receive a call
        //to onCreateOptionsMenu
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mEmptyView = (TextView) view.findViewById(R.id.empty_view);


        if(savedInstanceState!=null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult called " + "requestCode: " + requestCode);


        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CRIME){
            if(data==null){
                return;
            }else{
                Log.d(TAG, "onActivityResult and data is not null");
                idChangedItem = (UUID) data.getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
                if(data.getBooleanExtra(CrimeFragment.EXTRA_CRIME_DELETED,false)){
                   isChangedItemWasDeleted = true;
                }
                updateUI();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(),crime.getId());
                intent.putExtra(INSERT_MODE,true);

                startActivity(intent);
                return true;
            case R.id.menu_item_show_subtitle:

                //Sets mSubtitleVisible = true
                mSubtitleVisible = !mSubtitleVisible;
                //Recreates the menu (calls onCreateOptionsMenu)
                getActivity().invalidateOptionsMenu();

                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();

        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural,
                crimeCount,crimeCount);

        if(!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume was called");

        updateUI();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void updateUI(){

        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (crimes.isEmpty()){
            mCrimeRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            mCrimeRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }


        if(mAdapter==null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);


            //TODO: Add "Delete" button that should appear behind the swiped out View
            ItemTouchHelper.Callback callback = new CrimeTouchHelper(mAdapter);
            ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(mCrimeRecyclerView);
        }
        else{
            //I've done it! I've changed mAdapter.notifyDataSetChanged() to mAdapter.notifyItemChanged
            if(isChangedItemWasDeleted){
                mAdapter.notifyItemRemoved(mAdapter.getItemIndexById(idChangedItem));
                crimes.remove(crimeLab.getCrime(idChangedItem));
                crimeLab.delCrime(crimeLab.getCrime(idChangedItem));
                isChangedItemWasDeleted = false;
            }


            //If we have item that was changed than we should use notifyItemChanged
            if(idChangedItem != null){
                mAdapter.setCrimes(crimes);
                mAdapter.notifyItemChanged(mAdapter.getItemIndexById(idChangedItem));
                idChangedItem = null;
            }else{
                //TODO: At this point I don't know exactly if any item was inserted. I need to fix it.
                mAdapter.setCrimes(crimes);
                mAdapter.notifyDataSetChanged();
            }


        }

        updateSubtitle();

    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Crime mCrime;

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        public CrimeHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_check_box);

            mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d(TAG,"onChecked was changed");
                    mCrime.setSolved(isChecked);
                    CrimeLab.get(getActivity()).updateCrime(mCrime);
                }
            });

        }

        public void bindCrime(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(DateFormat.format("cccc, MMM d, yyyy HH:mm", mCrime.getDate()));
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(getActivity(),mCrime.getTitle()+" clicked!",Toast.LENGTH_LONG).show();
            Intent intent = CrimePagerActivity.newIntent(getActivity(),mCrime.getId());
            //I think I should use startActivityForResult instead just startActivity
            startActivityForResult(intent, REQUEST_CRIME);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }
        
        public int getItemIndexById(UUID crimeId){

            for (Crime crime:mCrimes
                 ) {
                if(crime.getId().equals(crimeId)){
                    return mCrimes.indexOf(crime);
                }
            }

            return -1;
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime,parent,false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {

            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);

        }

        public void onItemRemove(int position){
            mCrimes.remove(position);
            notifyItemRemoved(position);
        }

        //TODO: Method onItemMove isn't called. Fix it.
        public boolean onItemMove(int fromPosition, int toPosition){

            if(fromPosition < toPosition) {
                for(int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mCrimes,i,i+1);
                }
            } else{
                for(int i = fromPosition; i > toPosition; i--){
                    Collections.swap(mCrimes,i,i-1);
                }
            }

            notifyItemMoved(fromPosition,toPosition);

            return true;
        }

    }

    private class CrimeTouchHelper extends ItemTouchHelper.SimpleCallback{
        private CrimeAdapter mCrimeAdapter;


        public CrimeTouchHelper(CrimeAdapter crimeAdapter) {
            super(ItemTouchHelper.UP|ItemTouchHelper.DOWN, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT);
            mCrimeAdapter = crimeAdapter;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

            Log.d(TAG,"onMove was called");
            mCrimeAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());

            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            Log.d(TAG, "onSwiped was called");
            CrimeLab.get(getActivity()).delCrime(mCrimeAdapter.mCrimes.get(viewHolder.getAdapterPosition()));
            mCrimeAdapter.onItemRemove(viewHolder.getAdapterPosition());
            updateUI();
        }
    }

}
