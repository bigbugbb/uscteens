package edu.neu.android.mhealth.uscteensver1.extra;

import java.util.HashMap;

// date -> action
public class RewardWrap extends HashMap<String, Reward> {

    private static final long serialVersionUID = 6300932318196334092L;

    public RewardWrap() {
    }

    @Override
    public void clear() {
//		Iterator iter = entrySet().iterator(); 
//		while (iter.hasNext()) { 
//		    Map.Entry entry = (Map.Entry) iter.next(); 	
//		    Reward reward = (Reward) entry.getValue();
//		    reward.clear();
//		}
        super.clear();
    }

    @Override
    public Reward put(String key, Reward value) {
        return super.put(key, value);
    }
}
