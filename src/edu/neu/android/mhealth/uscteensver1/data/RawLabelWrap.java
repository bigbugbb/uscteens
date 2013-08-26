package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class RawLabelWrap extends HashMap<String, ArrayList<RawLabel>> {
    private static final long serialVersionUID = 6951199087406965582L;
    private String mDate = "";

    public RawLabelWrap() {
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getDate() {
        return mDate;
    }

    public ArrayList<RawLabel> toSortedArray() {
        ArrayList<RawLabel> array = new ArrayList<RawLabel>();

        Iterator iter = entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            ArrayList<RawLabel> rawLabels = (ArrayList<RawLabel>) entry.getValue();
            for (RawLabel rawLabel : rawLabels) {
                array.add(rawLabel);
            }
        }
        Collections.sort(array);

        return array;
    }

    public boolean add(String dateTime, String name) {
        boolean result = true;
        ArrayList<RawLabel> rawLabels = this.get(dateTime);

        if (rawLabels == null) {
            rawLabels = new ArrayList<RawLabel>();
            RawLabel rawLabel = new RawLabel(dateTime, name);
            rawLabels.add(rawLabel);
            put(dateTime, rawLabels);
        } else {
            boolean isExisted = false;
            for (RawLabel rawLabel : rawLabels) {
                if (rawLabel.getName().compareToIgnoreCase(name) == 0) {
                    isExisted = true;
                    break;
                }
            }
            if (!isExisted) {
                RawLabel rawLabel = new RawLabel(dateTime, name);
                rawLabels.add(rawLabel);
            }
        }

        return result;
    }

    public boolean remove(String dateTime, String name) {
        boolean result = false;
        ArrayList<RawLabel> rawLabels = this.get(dateTime);

        if (rawLabels != null) {
            ArrayList<RawLabel> discard = new ArrayList<RawLabel>();
            for (RawLabel rawLabel : rawLabels) {
                if (rawLabel.getName().compareToIgnoreCase(name) == 0) {
                    discard.add(rawLabel);
                }
            }
            result = rawLabels.removeAll(discard);
        }

        return result;
    }

    public boolean removeAll(String name) {
        boolean result = false;

        Iterator iter = entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            ArrayList<RawLabel> rawLabels = (ArrayList<RawLabel>) entry.getValue();
            ArrayList<RawLabel> discard = new ArrayList<RawLabel>();
            for (RawLabel rawLabel : rawLabels) {
                if (rawLabel.getName().compareToIgnoreCase(name) == 0) {
                    discard.add(rawLabel);
                }
            }

            if (rawLabels.removeAll(discard)) {
                result = true;
            }

            if (rawLabels.size() == 0) {
                iter.remove();
            }
        }

        return result;
    }

    @Override
    public void clear() {
        mDate = "";
        super.clear();
    }
}
