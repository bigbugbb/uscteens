package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;

import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.extra.Action;

class RawChunksWrap extends ArrayList<RawChunk> {

    /**
     *
     */
    private static final long serialVersionUID = -1412862931407805188L;

    public boolean areAllChunksLabelled() {
        for (RawChunk rawChunk : this) {
            Action action = rawChunk.getAction();
            if (action.getActionID().equals(TeensGlobals.UNLABELLED_GUID)) {
                return false;
            }
        }
        return true;
    }

}
