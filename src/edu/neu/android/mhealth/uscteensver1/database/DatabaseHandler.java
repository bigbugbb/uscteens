package edu.neu.android.mhealth.uscteensver1.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "rewardstatesManager";

    // RewardState table name
    private static final String TABLE_REWARD_STATES = "rewardstates";

    // RewardState Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_STATE = "state";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_REWARD_STATES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE + " TEXT,"
                + KEY_STATE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REWARD_STATES);

        // Create tables again
        onCreate(db);
    }

    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new reward state
    public void addRewardState(RewardState contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, contact.getDate()); // Reward Date
        values.put(KEY_STATE, contact.getState()); // Reward State

        // Inserting Row
        db.insert(TABLE_REWARD_STATES, null, values);
        db.close(); // Closing database connection
    }

    // Getting single reward state
    public RewardState getRewardState(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_REWARD_STATES, new String[]{KEY_ID,
                KEY_DATE, KEY_STATE}, KEY_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        RewardState state = new RewardState(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        cursor.close();
        db.close();

        // return contact
        return state;
    }

    // Getting all states
    public List<RewardState> getAllRewardStates() {
        List<RewardState> stateList = new ArrayList<RewardState>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_REWARD_STATES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                RewardState state = new RewardState();
                state.setID(Integer.parseInt(cursor.getString(0)));
                state.setDate(cursor.getString(1));
                state.setState(cursor.getString(2));
                // Adding contact to list
                stateList.add(state);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return state list
        return stateList;
    }

    // Updating single reward state
    public int updateRewardState(RewardState state) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, state.getDate());
        values.put(KEY_STATE, state.getState());

        // updating row
        return db.update(TABLE_REWARD_STATES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(state.getID())});
    }

    // Deleting single reward state
    public void deleteRewardState(RewardState state) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REWARD_STATES, KEY_ID + " = ?",
                new String[]{String.valueOf(state.getID())});
        db.close();
    }

    // Deleting all states
    public void deleteAllRewardState() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REWARD_STATES, null, null);
        db.close();
    }

    // Getting states count
    public int getStatesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_REWARD_STATES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
