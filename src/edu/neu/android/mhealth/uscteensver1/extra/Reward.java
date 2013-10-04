package edu.neu.android.mhealth.uscteensver1.extra;

import java.io.Serializable;

public class Reward implements Serializable {
    private static final long serialVersionUID = -3930451861740623111L;

    protected String mDate; // date for this reward
    protected String mHtml; // html file name
    protected String mCode; // redeem code
    protected String mLink; // optional link to play store or app store?

    public Reward(String date, String html, String code, String link) {
        mDate = date;
        mHtml = html;
        mCode = code;
        mLink = link;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public void setHtml(String html) {
        mHtml = html;
    }
    
    public void setCode(String code) {
    	mCode = code;
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

    public String getCode() {
    	return mCode;
    }
    
    public String getLink() {
        return mLink;
    }
}
