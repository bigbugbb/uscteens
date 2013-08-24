package edu.neu.android.mhealth.uscteensver1.utils;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;

import android.content.Context;
import edu.neu.android.wocketslib.dataupload.DataSender;
import edu.neu.android.wocketslib.json.model.Note;
import edu.neu.android.wocketslib.json.model.WocketInfo;
import edu.neu.android.wocketslib.utils.Log;

public class NoteSender {
    private final static String TAG = "NoteSender";
    private Context mContext;
    private WocketInfo mWocketInfo;

    public NoteSender(Context context) {
        mContext = context;
        reset();
    }

    public void reset() {
        mWocketInfo = new WocketInfo(mContext);
    }

    public boolean send() {
        if (mWocketInfo.isEmpty()) {
            return false;
        }

        try {
            DataSender.queueWocketInfo(mContext, mWocketInfo);
        } catch (ConcurrentModificationException e) {
            Log.e(TAG, "Failed to queue Wocket info");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                Log.e(TAG, "Error: InterruptedException in ServerLogger.send(): " + e1.toString());
            }
            DataSender.queueWocketInfo(mContext, mWocketInfo);
        }

        return true;
    }

    public void addNote(Date date, String msg, boolean isPlot) {
        if (mWocketInfo.someNotes == null)
            mWocketInfo.someNotes = new ArrayList<Note>();

        Note aNote = new Note();
        aNote.startTime = date;
        aNote.note = msg;
        aNote.plot = isPlot ? 1 : 0;
        mWocketInfo.someNotes.add(aNote);
    }
}
