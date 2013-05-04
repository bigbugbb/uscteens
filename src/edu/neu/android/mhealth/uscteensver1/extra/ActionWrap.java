package edu.neu.android.mhealth.uscteensver1.extra;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// id -> action
public class ActionWrap extends HashMap<String, Action> {

	private static final long serialVersionUID = 6300932318196334092L;

	public ActionWrap() {}
	
	@Override
	public void clear() {
		Iterator iter = entrySet().iterator(); 
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 	
		    Action action = (Action) entry.getValue();
		    action.clear();
		}
	}
	
	@Override
	public Action put(String key, Action value) {		
		return super.put(key.trim(), value);
	}
}
