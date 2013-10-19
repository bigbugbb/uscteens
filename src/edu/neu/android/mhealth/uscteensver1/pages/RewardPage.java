package edu.neu.android.mhealth.uscteensver1.pages;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.database.DatabaseHandler;
import edu.neu.android.mhealth.uscteensver1.database.RewardState;
import edu.neu.android.mhealth.uscteensver1.extra.Reward;
import edu.neu.android.mhealth.uscteensver1.extra.RewardManager;
import edu.neu.android.mhealth.uscteensver1.threads.SendEmailTask;
import edu.neu.android.mhealth.uscteensver1.ui.DoneButton;
import edu.neu.android.mhealth.uscteensver1.ui.FixButton;
import edu.neu.android.mhealth.uscteensver1.ui.OnClickListener;
import edu.neu.android.mhealth.uscteensver1.ui.RewardBackground;
import edu.neu.android.mhealth.uscteensver1.ui.RewardButton;
import edu.neu.android.mhealth.uscteensver1.views.RewardView;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.WeekdayHelper;

public class RewardPage extends AppPage implements OnClickListener {

    protected RewardBackground mBackground = null;
    protected FixButton        mBtnFix     = null;
    protected DoneButton       mBtnDone    = null;
    protected RewardButton     mBtnReward  = null;
    protected RewardView       mRewardView = null;

    protected final static int BKGND  = 0;
    protected final static int FIX    = 1;
    protected final static int DONE   = 2;
    protected final static int REWARD = 3;

    protected View   mView   = null;
    protected Reward mReward = null;

    public RewardPage(Context context, View view, Handler handler) {
        super(context, handler);
        mView = view;
    }

    public void bindRewardView(RewardView view) {
        mRewardView = view;
    }

    public List<AppObject> load() {
        // create game objects
        if (mBackground == null) {
            mBackground = new RewardBackground(mContext.getResources());
            mObjects.add(mBackground);
            mBackground.setID(BKGND);
        }
        if (mBtnDone == null) {
            mBtnDone = new DoneButton(mContext.getResources());
            mObjects.add(mBtnDone);
            mBtnDone.setID(DONE);
            mBtnDone.setOnClickListener(this);
        }
        if (mBtnReward == null) {
            mBtnReward = new RewardButton(mContext.getResources());
            mObjects.add(mBtnReward);
            mBtnReward.setID(REWARD);
            mBtnReward.setOnClickListener(this);
        }
        if (mBtnFix == null) {
            mBtnFix = new FixButton(mContext.getResources());
            mObjects.add(mBtnFix);
            mBtnFix.setID(FIX);
            mBtnFix.setOnClickListener(this);
        }
        // order by Z
        orderByZ(mObjects);

        return mObjects;
    }

    public void start() {
        load();
        for (AppObject obj : mObjects) {
            obj.onSizeChanged(mView.getWidth(), mView.getHeight());
        }

        // get days between the start date and the selected date
        String startDate    = DataStorage.getStartDate(mContext, "");
        String selectedDate = DataStorage.GetValueString(mContext, TeensGlobals.CURRENT_SELECTED_DATE, "2013-01-01");
        Date aStartDate = null;
        try {
            aStartDate = DateHelper.serverDateFormat.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int daysAfterStarting = 0;
        while (daysAfterStarting < 14) {
            String date = WeekdayHelper.afterNDayFrom(aStartDate, daysAfterStarting);
            if (date.compareToIgnoreCase(selectedDate) == 0) {
                break;
            }
            daysAfterStarting++;
        } // 0-13

        mReward = RewardManager.getReward(daysAfterStarting + 1); // from 1-14
        if (mReward == null || mReward.getLink().equals("") || mReward.getCode().equals("")) {
            mBtnReward.setVisible(false);
            mBtnDone.setX(mWidth * 0.2f);
            mBtnFix.setX(mWidth * 0.8f - mBtnFix.getWidth());
        } else {    
            mRewardView.loadUrl(mReward.getHtml());
            mRewardView.setVisibility(View.VISIBLE);
        }
        
        DatabaseHandler db = new DatabaseHandler(mContext);

        /**
         * CRUD Operations
         **/
        // Inserting the reward state which is not contained in the table
        // Reading all states
        boolean isContained = false;
        String select = DataSource.getCurrentSelectedDate();
        Log.d("Reading: ", "Reading all states..");
        List<RewardState> states = db.getAllRewardStates();
        for (RewardState s : states) {
            if (s.getDate().equals(select)) {
                isContained = true;
                break;
            }
        }
        if (!isContained) {
            Log.d("Insert: ", "Inserting ..");
            db.addRewardState(new RewardState(select, RewardState.ACHIEVED));
            if (mReward.getCode() != null) {
	            String email = DataStorage.GetValueString(
	            	mContext, TeensGlobals.KEY_EMAIL_ADDRESS, "example@gmail.com"
	            );
	            String messageBody = String.format(
	            	"Congratulations on earning a one dollar reward for labeling your day.\n\n" +
	            	"To redeem the reward, use code %s on the Amazon Gift Card website.\n\n" +
	            	"Please note that you can only redeem a code once, " +
	            	"so if you already redeemed your reward for Day %d, then you can ignore this email.\n\n\n" +
	            	"The USC Study Team", mReward.getCode(), daysAfterStarting + 1
	            );
	            sendMail(email, String.format("USCTeens Reward Code for Day %d", daysAfterStarting + 1), messageBody);
            }
        }
    }

    public void stop() {
        if (mRewardView != null) {
            mRewardView.setVisibility(View.GONE);
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
    public void onDraw(Canvas c) {
        c.drawColor(Color.WHITE);
        for (AppObject obj : mObjects) {
            obj.onDraw(c);
        }
    }

    @Override
    public void onClick(AppObject obj) {
        Message msg = mHandler.obtainMessage();

        switch (obj.getID()) {
        case DONE:
            msg.what = AppCmd.DONE;
            mHandler.sendMessage(msg);
            break;
        case FIX:
            msg.what = AppCmd.BEGIN_LOADING;
            msg.obj  = DataSource.getCurrentSelectedDate();
            mHandler.sendMessage(msg);
            break;
        case REWARD:
            msg.what = AppCmd.REWARD;
            msg.obj  = (mReward == null) ? null : new String[] { mReward.getCode(), mReward.getLink() };
            mHandler.sendMessage(msg);
            break;
        default:
            break;
        }
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        boolean ret = false;

        if (mSelObject != null) {
            if (mSelObject.contains(e2.getX(), e2.getY())) {
                ret = mSelObject.onScroll(e1, e2, distanceX, distanceY);
            }
        }

        return ret;
    }
    
    private void sendMail(String email, String subject, String messageBody) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("uscteens@gmail.com", "@uscteens@");
            }
        });
     
        try {
            javax.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("numobileappdevelopment@gmail.com", "Teen Activity Game"));
            message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(email, email));
            message.setSubject(subject);
            message.setText(messageBody);
            new SendEmailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
