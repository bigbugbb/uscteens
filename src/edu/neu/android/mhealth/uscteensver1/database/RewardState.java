package edu.neu.android.mhealth.uscteensver1.database;

public class RewardState {
	
	public final static String LOCKED   = "LOCKED";
	public final static String ACHIEVED = "ACHIEVED";	
	
	//private variables
	private int mID;
	private String mDate;
	private String mState;
	
	// Empty constructor
	public RewardState(){
		
	}
	// constructor
	public RewardState(int id, String date, String state){
		this.mID = id;
		this.mDate = date;
		this.mState = state;
	}
	
	// constructor
	public RewardState(String date, String state){
		this.mDate = date;
		this.mState = state;
	}
	// getting ID
	public int getID(){
		return this.mID;
	}
	
	// setting id
	public void setID(int id){
		this.mID = id;
	}
	
	// getting name
	public String getDate(){
		return this.mDate;
	}
	
	// setting name
	public void setDate(String date){
		this.mDate = date;
	}
	
	// getting phone number
	public String getState(){
		return this.mState;
	}
	
	// setting phone number
	public void setState(String state){
		this.mState = state;
	}
}
