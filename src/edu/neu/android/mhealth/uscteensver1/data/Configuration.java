package edu.neu.android.mhealth.uscteensver1.data;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.content.Context;

import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.wocketslib.support.DataStorage;


public class Configuration implements Serializable {
	private static final long serialVersionUID = 7747559708291341374L;
	protected final String CONFIG_FILE_PATH = "/sdcard/TestData/config.xml";
	protected Context mContext = null;
	
	protected String mStartDate = "";
	
	public Configuration(Context context) {
		mContext = context;
	}
		
	public boolean load(String path) {
		
		boolean result = true;
//		SAXReader saxReader = new SAXReader();
//
//		try {
//			Document document = saxReader.read(new File(path));
//			Element root = document.getRootElement();
//
//	        // iterate through child elements of root
//	        for (Iterator i = root.elementIterator(); i.hasNext();) {
//	            Element start = (Element) i.next();
//	            for (Iterator n = start.attributeIterator(); n.hasNext();) {
//	    	       Attribute value = (Attribute) n.next();
//	    	       mStartDate = value.getText();	    	       
//	    	    }	            
//	        }
//
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			result = false;
//		}
		mStartDate = DataStorage.GetValueString(mContext, USCTeensGlobals.START_DATE, "");
		
		return result;
	}
	
	public String getStartDate() {
		mStartDate = DataStorage.GetValueString(mContext, USCTeensGlobals.START_DATE, "");
		return mStartDate;
	}
	
}
