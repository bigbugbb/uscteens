package edu.neu.android.mhealth.uscteensver1.pages;

public class AppScale {

    protected static float sScaleW = 1;
    protected static float sScaleH = 1;
    protected static float sScaleT = 1;

    protected static float sExpectW = 1280;
    protected static float sExpectH = 720;

    protected static boolean sCreated = false;
    protected static AppScale sUiScale = null;

    public static float doScaleW(float srcWidth) {
        return srcWidth * sScaleW;
    }

    public static float doScaleH(float srcHeight) {
        return srcHeight * sScaleH;
    }

    public static float doScaleT(float textSize) {
        return textSize * sScaleT;
    }

    public static float getScaleW() {
        return sScaleW;
    }

    public static float getScaleH() {
        return sScaleH;
    }

    public static void setScale(float sw, float sh) {
        sScaleW = sw;
        sScaleH = sh;
    }

    public static void calcScale(float baseW, float baseH, float expectW, float expectH) {
        sExpectW = expectW;
        sExpectH = expectH;
        calcScale(baseW, baseH);
    }

    public static void calcScale(float baseW, float baseH) {
        baseW = Math.max(baseW, baseH);
        baseH = Math.min(baseW, baseH);
        sScaleW = baseW / sExpectW;
        sScaleH = baseH / sExpectH;
        sScaleT = sScaleH;
    }
}
