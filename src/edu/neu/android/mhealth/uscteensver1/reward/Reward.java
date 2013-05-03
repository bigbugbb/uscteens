package edu.neu.android.mhealth.uscteensver1.reward;

import java.io.Serializable;

public class Reward implements Serializable {
	private static final long serialVersionUID = -3930451861740623111L;
	
	protected String mDate; // date for this reward
	protected String mHtml; // html file name or path?
	protected String mLink; // optional link to play store or app store?
	
	public Reward(String date, String html, String link) {
		mDate = date;
		mHtml = html;
		mLink = link;
	}
	
	public void setDate(String date) {
		mDate = date;
	}
	
	public void setHtml(String html) {
		mHtml = html;
	}
	
	public void setLink(String link) {
		mLink = link;
	}
	
	public String getDate() {
		return mDate;
	}
	
	public String getHtml() {
		return mHtml;
	}
	
	public String getLink() {
		return mLink;
	}
}
