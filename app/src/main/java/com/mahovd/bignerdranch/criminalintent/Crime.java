package com.mahovd.bignerdranch.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by mahovd on 01/11/15.
 * Model
 */


/*Base model class*/
public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;
    private Long mSuspectId;

    /*Constructor*/
    public Crime(){
        this(UUID.randomUUID());
    }

    public Crime(UUID id){
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public Long getSuspectId(){
        return mSuspectId;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public void setSuspectId(Long suspectId){
        mSuspectId = suspectId;
    }

    public String getPhotoFileName(){
        return "IMG_"+getId().toString()+"jpg";
    }
}
