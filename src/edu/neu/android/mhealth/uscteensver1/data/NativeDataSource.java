package edu.neu.android.mhealth.uscteensver1.data;


public class NativeDataSource {
	
	protected static NativeDataSource sDataLoader = null;
	
	public static NativeDataSource getDataSource() {
		System.loadLibrary("datasrc");		
		if (sDataLoader == null) {
			sDataLoader = new NativeDataSource();
		}
		return sDataLoader;		
	}
	
	protected NativeDataSource() {
		create();
	}

	public native int create();
	public native int destroy();
    public native float[] loadActivityData(String path);
    public native int unloadActivityData(String path);
}
