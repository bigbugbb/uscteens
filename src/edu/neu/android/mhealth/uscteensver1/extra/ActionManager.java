package edu.neu.android.mhealth.uscteensver1.extra;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.content.Context;
import android.util.Log;
import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.FileHelper;
import edu.neu.android.wocketslib.utils.WOCKETSException;

public class ActionManager {
    private final static String TAG = "ActionManager";
    // result code
    public final static int LOADING_SUCCEEDED  = 0;
    public final static int ERR_CANCELLED      = -1;
    public final static int ERR_NO_ACTION_DATA = -2;
    public final static int ERR_IO_EXCEPTION   = -3;

    public final static int MOST_RECENT_ACTIONS_COUNT = 3;
    public final static String MOST_RECENT_ACTION_ID = "MOST_RECENT_ACTION_ID";

    private final static String ASSETS_DIR = "activities";

    protected static boolean sCopied;
    protected static ActionWrap sActionWrap;
    protected static ArrayList<Action> sMostRecentActions;
    protected static HashMap<String, ArrayList<Action>> sActivatedActions;
    
    static {
    	sCopied = false;
    	sActionWrap = new ActionWrap();
    	sMostRecentActions = new ArrayList<Action>();
    	sActivatedActions  = new LinkedHashMap<String, ArrayList<Action>>();
    }

    public static void start() {
        loadActions();
    }

    public static void stop() {
        release();
    }

    public static Action getAction(String actID) {
        return sActionWrap.get(actID);
    }

    public static ActionWrap getActions() {
        return sActionWrap;
    }

    public static HashMap<String, ArrayList<Action>> getActivatedActions() {
        return sActivatedActions;
    }

    public static ArrayList<Action> getMostRecentActions() {
        sMostRecentActions.clear();
        HashMap<String, ArrayList<Action>> activated = getActivatedActions();
        ArrayList<Action> actions = new ArrayList<Action>();
        
        for (Map.Entry<String, ArrayList<Action>> entry : activated.entrySet()) {
        	actions.addAll(entry.getValue());
        }

        // try to get the most recent actions as much as possible
        for (int i = 0; i < MOST_RECENT_ACTIONS_COUNT; ++i) {
            String actID = DataStorage.GetValueString(TeensAppManager.getAppContext(), MOST_RECENT_ACTION_ID + i, null);
            if (actID != null) {            	
            	for (Action action : actions) {
                    if (action.getActionID().equals(actID)) {
                        sMostRecentActions.add(action);
                        break;
                    }
            	}
            }
        }

        // if the actions got are not enough, add the last several
        // actions from the activated action list for convenience
        for (int j = actions.size() - 1; j >= 0; --j) {
            if (sMostRecentActions.size() >= MOST_RECENT_ACTIONS_COUNT) {
                break;
            }
            Action action = actions.get(j);
            // check whether the current action has already been added
            boolean isExistent = false;
            for (Action recent : sMostRecentActions) {
                if (recent.getActionID().equals(action.getActionID())) {
                    // already in the most recent action list, skip it
                    isExistent = true;
                    break;
                }
            }
            if (!isExistent) {
                sMostRecentActions.add(action);
            }
        }

        // make sure all the action id are stored
        for (int i = 0; i < sMostRecentActions.size(); ++i) {
            Action action = sMostRecentActions.get(i);
            DataStorage.SetValue(TeensAppManager.getAppContext(), MOST_RECENT_ACTION_ID + i, action.getActionID());
        }

        return sMostRecentActions;
    }

    public static void setMostRecentAction(Action action) {
        Context context = TeensAppManager.getAppContext();

        int i = 0;
        for (i = 0; i < MOST_RECENT_ACTIONS_COUNT; ++i) {
            String actID = DataStorage.GetValueString(context, MOST_RECENT_ACTION_ID + i, null);
            if (actID.equals(action.getActionID())) { // the activity is currently in the most recent list
                ++i;
                break;
            }
        }

        for (int j = i - 2; j >= 0; --j) {
            int next = j + 1;
            String actID = DataStorage.GetValueString(context, MOST_RECENT_ACTION_ID + j, null);
            DataStorage.SetValue(context, MOST_RECENT_ACTION_ID + next, actID);
        }
        DataStorage.SetValue(context, MOST_RECENT_ACTION_ID + 0, action.getActionID());
    }

    public static int loadActions() {
        // first clear the action container
        sActionWrap.clear();

        String dirPath = TeensGlobals.DIRECTORY_PATH + File.separator + Globals.APP_DATA_DIRECTORY + TeensGlobals.ICON_FOLDER;
        String[] actionDir = FileHelper.getFilePathsDir(dirPath);
        if (actionDir == null || actionDir.length == 0 || (TeensGlobals.sUpdateConfig && !sCopied)) {
            sCopied = true;
            copyActionsFromAssets();
            actionDir = FileHelper.getFilePathsDir(dirPath);
            if (actionDir == null || actionDir.length == 0) {
                return ERR_NO_ACTION_DATA;
            }
        }

        /**
         *  load all activities
         */
        sActionWrap.put(TeensGlobals.UNLABELLED_GUID, Action.createUnlabelledAction());

        File aMappingFile = getMappingFile(dirPath);
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(aMappingFile);
            Element activities = document.getRootElement();
            for (Iterator i = activities.elementIterator(); i.hasNext(); ) {
                Element activity = (Element) i.next();
                Element id      = activity.element("ID");
                Element name    = activity.element("NAME");
                Element subname = activity.element("SUBNAME");
                Element icon    = activity.element("ICON");
                Action action = new Action(id.getTextTrim(), name.getText(), subname.getText(), 
                		icon.getText(), dirPath + icon.getText());
                sActionWrap.put(id.getTextTrim(), action); // key = actID, value = Action
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        
        sActivatedActions.clear();

        // load the activated activities
        File aActivatedFile = getActivatedFile(dirPath);
        try {
        	Document document = saxReader.read(aActivatedFile);
            Element activated = document.getRootElement();
            for (Iterator i = activated.elementIterator(); i.hasNext(); ) {
            	Element category = (Element) i.next();
            	String name = category.attributeValue("NAME");
            	ArrayList<Action> actions = new ArrayList<Action>();
            	for (Iterator j = category.elementIterator(); j.hasNext(); ) {
            		Element item = (Element) j.next();
            		Element id = item.element("ID");
            		Action action = getActions().get(id.getTextTrim());
            		if (action != null) {
            			action.loadIcon();
            			actions.add(action);
            		}
            	}            	
            	sActivatedActions.put(name, actions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            
        }

        return LOADING_SUCCEEDED;
    }

    public static void release() {
        sActionWrap.clear();
        sActivatedActions.clear();
        sMostRecentActions.clear();
        System.gc();
    }

    private static File getMappingFile(String dirPath) {
        String[] filePaths = FileHelper.getFilePathsDir(dirPath);
        String filePath = filePaths[0]; // set a default value

        for (String path : filePaths) {
            String extName = path.substring(path.lastIndexOf("/") + 1, path.length());
            if (extName.equals("activity_mapping.xml")) {
                filePath = path;
                break;
            }
        }

        return new File(filePath);
    }

    private static File getActivatedFile(String dirPath) {
        String[] filePaths = FileHelper.getFilePathsDir(dirPath);
        String filePath = filePaths[0]; // set a default value

        for (String path : filePaths) {
            String extName = path.substring(path.lastIndexOf("/") + 1, path.length());
            if (extName.equals("activity_activated.xml")) {
                filePath = path;
                break;
            }
        }

        return new File(filePath);
    }

    // users can delete the Activity folder under the data directory, and when they
    // do that, the whole Activity folder will be copied from the assets to the data
    // directory which locates in the external storage
    private static void copyActionsFromAssets() {
        String outfilePath = TeensGlobals.DIRECTORY_PATH + File.separator + Globals.APP_DATA_DIRECTORY + TeensGlobals.ICON_FOLDER;
        // create new directory if doesn't exist
        try {
            FileHelper.createDir(outfilePath);
        } catch (WOCKETSException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // get all file names needed
        String[] files = null;
        try {
            files = TeensAppManager.getAppAssets().list(ASSETS_DIR);
        } catch (IOException e) {
            Log.e(TAG, "Failed to get asset file list.", e);
        }
        // copy each file from assets to external storage
        for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = TeensAppManager.getAppAssets().open(ASSETS_DIR + File.separator + filename);
                out = new FileOutputStream(outfilePath + filename);
                copyFile(in, out);
                in.close();
                out.flush();
                out.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to copy asset file: " + filename, e);
            }
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

}
