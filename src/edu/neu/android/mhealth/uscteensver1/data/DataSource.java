package edu.neu.android.mhealth.uscteensver1.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.content.Context;
import android.widget.Toast;

public class DataSource {
	
	public static final int PIXEL_SCALE = 8;
	
	public ArrayList<ActivityData>  mActList1 = new ArrayList<ActivityData>();
	public ArrayList<ActivityData>  mActList2 = new ArrayList<ActivityData>();
	public ArrayList<ChunkDataCell> mChkData  = new ArrayList<ChunkDataCell>();
	
	protected int mDay  = 1;
	protected int mWeek = 1;
	protected Context mContext = null;
	static protected DataSource sManager = null;
	
	static {
		System.loadLibrary("datasrc");
	}		
	
	static public DataSource getInstance(Context context) {
		if (sManager == null) {
			sManager = new DataSource(context);			
		}
		return sManager;
	}
	
	protected DataSource(Context context) {
		mContext = context;
	}
	
	public void setDay(int day) {
		mDay = day;
	}
	
	public void setWeek(int week) {
		mWeek = week;
	}
	
	public boolean loadActList(int week) {
		boolean result = false;
		String fileName = "/sdcard/TestData/week" + week + ".xml";
		
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try {
			document = saxReader.read(new File(fileName));								
			Element root = document.getRootElement();
			//
		    for (Iterator i = root.elementIterator(); i.hasNext();) {
		       Element annotations = (Element) i.next();
		       for (Iterator j = annotations.elementIterator(); j.hasNext();) {
		    	   Element annotation = (Element) j.next();
		    	   for (Iterator k = annotation.elementIterator(); k.hasNext();) {
			    	   Element label = (Element) k.next();
			    	   Element start = (Element) k.next();
			    	   Element stop  = (Element) k.next();
			    	   Element prop  = (Element) k.next();
			    	   
			    	   String modify = "";
			    	   String create = "";
			    	   for (Iterator n = prop.attributeIterator(); n.hasNext();) {
			    	       Attribute attribute = (Attribute) n.next();
			    	       if (attribute.getName().compareTo("LAST_MODIFIED") == 0) {
			    	    	   modify = attribute.getText();
			    	       } else if (attribute.getName().compareTo("DATE_CREATED") == 0) {
			    	    	   create = attribute.getText(); 	    	   
			    	       }
			    	   }
			    	   
			    	   ActivityData data = new ActivityData(start.getText(), stop.getText(), create, modify);
			    	   if (week == 1) {
			    		   mActList1.add(data);
			    	   } else if (week == 2) {
			    		   mActList2.add(data);
			    	   }
			       }
		        
		       }
		    }
		    
		    result = true;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}
		
		return result;
	}
	
	public ArrayList<ActivityData> getActList(int week) {
		if (week == 1) {
			return mActList1;
		} else if (week == 2) {
			return mActList2;			
		}
		
		return null;
	}

	public boolean loadData() {		
		if (loadActData() == false) {
			Toast.makeText(mContext, "Can't find the activity data!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (loadChkData() == false) {
			Toast.makeText(mContext, "Can't find the chunk data!", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}	
	
	public void saveData() {
		
	}		
	
	private boolean loadActData() {		
		boolean result = false;
		String fileName = "/sdcard/TestData/day" + mDay + ".txt";
		ArrayList<ActivityData> actList = null;
		
		if (mWeek == 1) {
			actList = mActList1;
		} else if (mWeek == 2) {
			actList = mActList2;
		}
		
		int index = mDay - 1 - (mWeek == 1 ? 0 : 7);	
		ActivityData data = actList.get(index);
		
		try {  	    
		    data.setInternalData(loadActivityData(fileName));	
		    result = true;
		} catch (Exception e) { 
			e.printStackTrace();
			result = false;
		}
				
		return result;		
	}

	public ActivityData getActData() {
		ArrayList<ActivityData> actList = null;
		
		if (mWeek == 1) {
			actList = mActList1;
		} else if (mWeek == 2) {
			actList = mActList2;
		}
		
		int index = mDay - 1 - (mWeek == 1 ? 0 : 7);
		ActivityData data = actList.get(index);
		
		return data;
	}
	
	public int getActLengthInPixel() {
		ArrayList<ActivityData> actList = null;
		
		if (mWeek == 1) {
			actList = mActList1;
		} else if (mWeek == 2) {
			actList = mActList2;
		}
		int index = mDay - 1 - (mWeek == 1 ? 0 : 7);	
		ActivityData data = actList.get(index);
		int start = data.getStartPosition();
		int stop  = data.getStopPosition();
		assert(stop >= start);
		return (stop - start) * PIXEL_SCALE;
	}
	
	private boolean loadChkData() {
		//String filePath = "/sdcard/TestData/chunk" + mDay + ".xml";
		String filePath = "/sdcard/TestData/chunk2.xml";
		boolean result = false;
		mChkData.clear();
		
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try {
			document = saxReader.read(new File(filePath));								
			Element root = document.getRootElement();
			//
		    for (Iterator i = root.elementIterator(); i.hasNext();) {
		       Element annotations = (Element) i.next();
		       for (Iterator j = annotations.elementIterator(); j.hasNext();) {
		    	   Element annotation = (Element) j.next();
		    	   for (Iterator k = annotation.elementIterator(); k.hasNext();) {
			    	   Element label = (Element) k.next();
			    	   Element start = (Element) k.next();
			    	   Element stop  = (Element) k.next();
			    	   Element prop  = (Element) k.next();
			    	   
			    	   String modify = "";
			    	   String create = "";
			    	   for (Iterator n = prop.attributeIterator(); n.hasNext();) {
			    	       Attribute attribute = (Attribute) n.next();
			    	       if (attribute.getName().compareTo("LAST_MODIFIED") == 0) {
			    	    	   modify = attribute.getText();
			    	       } else if (attribute.getName().compareTo("DATE_CREATED") == 0) {
			    	    	   create = attribute.getText(); 	    	   
			    	       }
			    	    }
			    	   
			    	   ChunkDataCell cell = new ChunkDataCell(start.getText(), stop.getText(), -1, create, modify);
			    	   mChkData.add(cell);
			       }
		        
		       }
		    }
		    
		    result = true;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}

		return result;
	}
	
	public boolean saveChunkData(ArrayList<ChunkDataCell> cells) {
		boolean result = false;
//		String toSave = "";
//		String fileName = "/sdcard/TestData/chunk" + mDay + ".txt";
//		
//		for (DataCell cell : cells) {
//			toSave += cell.mPosition + ":" + cell.mActionID + "\n";
//		}
//		
//		try { 
//			FileOutputStream fout = new FileOutputStream(fileName);
//	        byte[] bytes = toSave.getBytes(); 
//	        fout.write(bytes); 
//	        fout.close();
//	        
//	        result = true;
//	    } catch(Exception e) { 
//	    	e.printStackTrace();
//	    	// ....
//	    } 	
		
		return result;
	}
	
	public native int create();
	public native int destroy();
    public native int[] loadActivityData(String path);
    public native int unloadActivityData(String path);
}
