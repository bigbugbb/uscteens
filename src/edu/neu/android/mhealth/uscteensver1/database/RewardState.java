package edu.neu.android.mhealth.uscteensver1.database;

public class RewardState {

    public final static String LOCKED   = "LOCKED";
    public final static String ACHIEVED = "ACHIEVED";

    //private variables
    private int    mID;
    private String mDate;
    private String mState;

    // Empty constructor
    public RewardState() {

    }

    // constructor
    public RewardState(int id, String date, String state) {
        mID    = id;
        mDate  = date;
        mState = state;
    }

    // constructor
    public RewardState(String date, String state) {
        mDate  = date;
        mState = state;
    }

    // getting ID
    public int getID() {
        return mID;
    }

    // setting id
    public void setID(int id) {
        mID = id;
    }

    // getting name
    public String getDate() {
        return mDate;
    }

    // setting name
    public void setDate(String date) {
        mDate = date;
    }

    // getting phone number
    public String getState() {
        return mState;
    }

    // setting phone number
    public void setState(String state) {
        mState = state;
    }
}
