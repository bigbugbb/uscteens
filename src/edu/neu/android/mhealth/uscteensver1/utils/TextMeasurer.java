package edu.neu.android.mhealth.uscteensver1.utils;

import java.util.ArrayList;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class TextMeasurer {
	
	public static Rect getTextSize(String text, Typeface typeface, int size, float density) {
		Rect bounds = new Rect();
    	Paint paint = new Paint();   
    	
    	paint.setTypeface(typeface); // your preference here
    	paint.setTextSize(size);     // have this the same as your text size
    	paint.getTextBounds(text, 0, text.length(), bounds);
    	
    	int width  = Math.round(bounds.width() * density);
    	int height = Math.round(bounds.height() * density);
    	
    	bounds.right  = bounds.left  + width;
    	bounds.bottom = bounds.right + height;
    	
    	return bounds;
    }
	
	public static int getMaxWidth(ArrayList<String> texts, Typeface typeface, int size, float density) {
		return getMaxWidth((String[]) texts.toArray(), typeface, size, density);
	}
	
	public static int getMaxWidth(String[] texts, Typeface typeface, int size, float density) {
		int max = 0;
		
		for (String text : texts) {
			Rect rect = getTextSize(text, typeface, size, density);
			if (rect.width() > max) {
				max = rect.width();
			}
		}
		
		return max;
	}
	
	public static int getMaxHeight(ArrayList<String> texts, Typeface typeface, int size, float density) {
		return getMaxHeight((String[]) texts.toArray(), typeface, size, density);
	}
	
	public static int getMaxHeight(String[] texts, Typeface typeface, int size, float density) {
		int max = 0;
		
		for (String text : texts) {
			Rect rect = getTextSize(text, typeface, size, density);
			if (rect.height() > max) {
				max = rect.height();
			}
		}
		
		return max;
	}
	
	public static int getMinWidth(ArrayList<String> texts, Typeface typeface, int size, float density) {
		return getMinWidth((String[]) texts.toArray(), typeface, size, density);
	}
	
	public static int getMinWidth(String[] texts, Typeface typeface, int size, float density) {
		int min = 0;
		
		for (String text : texts) {
			Rect rect = getTextSize(text, typeface, size, density);
			if (rect.width() < min) {
				min = rect.width();
			}
		}
		
		return min;
	}
	
	public static int getMinHeight(ArrayList<String> texts, Typeface typeface, int size, float density) {
		return getMinHeight((String[]) texts.toArray(), typeface, size, density);
	}
	
	public static int getMinHeight(String[] texts, Typeface typeface, int size, float density) {
		int min = 0;
		
		for (String text : texts) {
			Rect rect = getTextSize(text, typeface, size, density);
			if (rect.height() < min) {
				min = rect.height();
			}
		}
		
		return min;
	}
}
