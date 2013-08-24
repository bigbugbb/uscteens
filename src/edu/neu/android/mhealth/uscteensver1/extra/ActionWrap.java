package edu.neu.android.mhealth.uscteensver1.extra;

import java.util.HashMap;
import java.util.Map;

// id -> action
public class ActionWrap extends HashMap<String, Action> {

    private static final long serialVersionUID = 6300932318196334092L;

    public ActionWrap() {
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void clear() {
        for (Map.Entry<String, Action> entry : entrySet()) {
            Action action = entry.getValue();
            action.clear();
        }
    }

    @Override
    public Action put(String key, Action value) {
        return super.put(key.trim(), value);
    }
}
