package edu.neu.android.mhealth.uscteensver1.reward;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// date -> action
public class RewardWrap extends HashMap<String, Reward> {

	private static final long serialVersionUID = 6300932318196334092L;

	public RewardWrap() {}
	
	@Override
	public void clear() {
		Iterator iter = entrySet().iterator(); 
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 	
		    Reward reward = (Reward) entry.getValue();
//		    reward.clear();
		}
	}
	
	@Override
	public Reward put(String key, Reward value) {		
		return super.put(key.trim(), value);
	}
}
