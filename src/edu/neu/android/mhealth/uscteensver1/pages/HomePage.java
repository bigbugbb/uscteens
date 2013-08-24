package edu.neu.android.mhealth.uscteensver1.pages;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.util.List;

import edu.neu.android.mhealth.uscteensver1.ui.BeginButton;
import edu.neu.android.mhealth.uscteensver1.ui.HomeBackground;
import edu.neu.android.mhealth.uscteensver1.ui.HomeTitle;
import edu.neu.android.mhealth.uscteensver1.ui.OnClickListener;
import edu.neu.android.mhealth.uscteensver1.ui.SetupTextView;
import edu.neu.android.mhealth.uscteensver1.ui.TutorialButton;
import edu.neu.android.wocketslib.support.DataStorage;

public class HomePage extends AppPage implements OnClickListener {

    protected HomeTitle mTitle = null;
    protected HomeBackground mBackground = null;
    protected BeginButton mBtnBegin = null;
    protected TutorialButton mBtnTutorial = null;
    protected SetupTextView mTextView = null;
    protected final static int TITLE = 0;
    protected final static int BKGND = 1;
    protected final static int BEGIN = 2;
    protected final static int TUTOR = 3;
    protected final static int SETUP = 4;

    protected View mView = null;

    public HomePage(Context context, View view, Handler handler) {
        super(context, handler);
        mView = view;
    }

    public List<AppObject> load() {
        // create game objects
        if (mBackground == null) {
            mBackground = new HomeBackground(mContext.getResources());
            mObjects.add(mBackground);
            mBackground.setID(BKGND);
        }
        if (mTitle == null) {
            mTitle = new HomeTitle(mContext.getResources());
            mObjects.add(mTitle);
            mTitle.setID(TITLE);
        }
        if (mBtnBegin == null) {
            mBtnBegin = new BeginButton(mContext.getResources());
            mObjects.add(mBtnBegin);
            mBtnBegin.setID(BEGIN);
            mBtnBegin.setOnClickListener(this);
        }
        if (mBtnTutorial == null) {
            mBtnTutorial = new TutorialButton(mContext.getResources());
            mObjects.add(mBtnTutorial);
            mBtnTutorial.setID(TUTOR);
            mBtnTutorial.setOnClickListener(this);
        }
        if (mTextView == null) {
            mTextView = new SetupTextView(mContext.getResources());
            mObjects.add(mTextView);
            mTextView.setID(SETUP);
            mTextView.setOnClickListener(this);
        }
        // order by Z
        orderByZ(mObjects);

        return mObjects;
    }

    public void resume() {
        String startDate = DataStorage.getStartDate(mContext, "");
        if (startDate.compareTo("") == 0) {
            mTextView.setVisible(true);
        } else {
            mTextView.setVisible(false);
        }
    }

    public void start() {
        load();
        for (AppObject obj : mObjects) {
            obj.onSizeChanged(mView.getWidth(), mView.getHeight());
        }
    }

    public void release() {
        super.release();
    }

    @Override
    public void onAppEvent(AppEvent e) {
        // TODO Auto-generated method stub
        super.onAppEvent(e);
    }

    @Override
    public void onSizeChanged(int width, int height) {
        // TODO Auto-generated method stub
        super.onSizeChanged(width, height);
    }

    @Override
    public void onClick(AppObject obj) {
        Message msg;

        switch (obj.getID()) {
            case BEGIN:
                msg = mHandler.obtainMessage();
                msg.what = AppCmd.BEGIN;
                mHandler.sendMessage(msg);
                break;
            case TUTOR:
                msg = mHandler.obtainMessage();
                msg.what = AppCmd.TUTOR;
                mHandler.sendMessage(msg);
                break;
            default:
                break;
        }
    }

}
