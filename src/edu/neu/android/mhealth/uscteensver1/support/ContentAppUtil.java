package edu.neu.android.mhealth.uscteensver1.support;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import edu.neu.android.mhealth.uscteensver1.openyoutubeplayer.OpenYouTubePlayerActivity;

public class ContentAppUtil {
	private static final double TEST_RATE = 0.0;

	public static int NUM_AVAILABLE_REWARD = 4;

	public static final String TAG = "RewardSystem";

	public static final boolean isTesting = false;

	public static final String FRUIT_TRACKING = "Fruit Tracking";
	public static final String VEGGIES_TRACKING = "Veggies Tracking";
	public static final String GOAL_SETTING = "Goal Setting";
	public static final String SSB_TRACKING = "SSB Tracking";
	public static final String PA_TRACKING = "PA Tracking";
	public static final String MEAT_TRACKING = "MeatTracking";
	public static final String HEALTHY_MEAL_TRACKER = "Healthy Meal Tracker";
	public static final String HEALTHY_LIFESTYLE_TRACKER = "Healthy Lifestyle Tracker";
	public static final String STEP_LIVELY = "StepLively";
	public static final String WEIGHING = "Weighing";
	public static final String FOOD_TRACKING = "Food Tracking";

	public static final String BONUS_MESSAGE_NAME = "bonusMessage";
	public static final String VIDEO_DATA = "VideoData";
	public static final String BLOG_DATA = "BlogData";
	public static final String COMIC_DATA = "ComicData";
	public static final String BONUS_DATA = "BonusData";
	public static final String QUIZZES_DATA = "QuizzesData";

	public static final String REWARDAPP = "RewardApp";

	public static final String BLOG_NAME = "blog entry";
	public static final String COMIC_NAME = "Minus One Comic";
	public static final String VIDEO_NAME = "video";
	public static final String QUIZZES_NAME = "profile question";

	public static final int COMICSACTIVITY = 1;
	public static final int BLOGGINGACTIVITY = 0;
	public static final int VIDEOACTIVITY = 2;
	public static final int PROFILEQUIZZES = 3;

	public static final String BONUS_COUNT = "BonusCount";
	private static final int DAILY_MAX_BONUS = 3;
	private static final String LAST_MOD_DAY = "LastModDay";

	public static final String[] MOVIE_LIST = {"eWja0xxtO-Y", "kzVo33iPQSI",
			"_Z_diPjGM4s", "asDAb0wuDv4", "akEmHAdnKFc", "Kxw9uLWRV7Q",
			"Nm3i5LVXyYI", "R_bwMvjgGio", "cD-zZQjVgoQ", "yIGeVMl5194",
			"tbu6eJqG7xc", "M962tfvhX98", "KnaJ_A4GqFk", "SdiPQZMQ-mE",
			"HBly1tQzkgE", "tS7HaN87orY", "qMpegXHkraA", "6NXA0WYtd2Q",
			"iJpq2_wWrxA", "v0JMjpZ9pk4", "no7fszY7rwM", "eTM69W8Uo1M",
			"MsP1_g01xNg", "TNQESL12M9g", "guJL7MgEYP4", "KgPkR8fLP_Q",
			"jCObPm6LPxM", "SJmJEMU2uxs", "s__9ZJU14tQ", "vrgbq86pQmE",
			"jt-pit5seBE", "MyHKhlHrmXQ", "hfxB1Z4Bzf0", "QgkgIgJstMM",};

	public static final String[] MOVIE_LIST_TEST = {"eWja0xxtO-Y",
			"kzVo33iPQSI", "_Z_diPjGM4s", "asDAb0wuDv4", "akEmHAdnKFc",
			"Kxw9uLWRV7Q", "Nm3i5LVXyYI", "R_bwMvjgGio", "cD-zZQjVgoQ",
			"yIGeVMl5194", "131", "112", "13", "23-mE", "14", "fwe", "1323",
			"1412", "15124", "v0JMjpZ9pk4", "no7fszY7rwM", "eTM69W8Uo1M",
			"MsP1_g01xNg", "TNQESL12M9g", "guJL7MgEYP4", "KgPkR8fLP_Q",
			"jCObPm6LPxM", "SJmJEMU2uxs", "s__9ZJU14tQ", "vrgbq86pQmE",
			"jt-pit5seBE", "MyHKhlHrmXQ", "hfxB1Z4Bzf0", "QgkgIgJstMM",};

	public static final String[] FAILURE_MESSAGE = {"A little effort goes a long way."};
	public static final String DATABASE_ERROR_MESSAGE = "Great job. Unfortunately, CITY is having some trouble connecting to the CITY database so it can't give you a reward right now.";
	public static final String FILE_MISSING_MESSAGE = "Try downloading again!";
	public static final String NOT_AVAILABLE_MESSAGE = "That's all that's available so far. To see more content, continue doing what CITY asks you to do each day...";
	public static final String NOT_AVAILABLE_ON_SERVER_MESSAGE = "The next chapter is coming soon. CITY needs to connect to the Internet to get it but cannot right now.";
	public static final String REWARD_TITLE = "%s, on %s, you completed %d out of %d requests in the last week. As a reward, here is a new %s";
	public static final String REWARD_TITLE_EXTRA = "%s, on %s, you did even more (%d) than CITY asked (%d) in the last week. Terrific! As a reward, here is a new %s";
	public static final String REWARD_MESSAGE = "Congratulations! %s No. %d is ready for you! Go get it!";
	public static final String[] FAILURE_TITLE = {
			"%s, on %s, you've stayed on schedule %d out of %d times in the last week. It'll take some effort, but the CITY app will get a lot more interesting if you keep plugging away at this!",
			"%s, on %s, you've stayed on schedule %d out of %d times in the last week. You're about halfway there in unlocking some great CITY perks!",
			" %s, on %s, you've stayed on schedule %d out of %d times in the last week. With just a tiny bit more effort, CITY perks await!"};
	public static final String BONUS_MESSAGE = "Congratulations! You've won a bonus for %s.\n\nYou've won access to a new %s (No. %d).";
	public static final String BONUS_BUDDY_MESSAGE="Congratulations! You've won a bonus for %s.\n\nYou've won access to a new %s: %s";
	public static final String BONUS_MESSAGE_VACATION = "Congratulations, We noticed you tracked %s on a day when CITY wasn't asking you to. You've earned a reward %s";
	public static final String APP_NAME = "AppName";
	public static final String TITLE_1 = "Congratulations! You've won a reward!";
	public static final String TITLE_BUDDY="Congratulations! You've won a buddy quiz!";
	public static final String[] TITLE_2 = {"Keep plugging away!",
			"Halfway there!", "Just a tiny bit more!"};

	public static String SHORT_QUESTION = "";

	public static final int FOOD_TRACKING_ID = 0;
	public static final int WEIGHING_ID = 1;
	public static final int STEP_LIVELY_ID = 2;
	public static final int HEALTHY_MEAL_TRACKER_ID = 3;
	public static final int MEAT_TRACKING_ID = 4;
	public static final int PA_PTRACKING_ID = 5;
	public static final int SSB_TRACKING_ID = 6;
	public static final int GOAL_SETTING_ID = 7;
	public static final int VEGGIES_TRACKING_ID = 8;
	public static final int FRUIT_TRACKING_ID = 9;

	public static final String TRIGGER = "Trigger";
	public static final int NORMAL = 0;
	public static final int BONUS = 1;
	public static final int BONUS_EXTRA = 2;

	public static final String REWARDPOSITION = "RewardPosition";

	private static int selectedReward = 0;
	private static Bundle bundle_quiz;

	public static String BUDDY_QUESTION="";

	public static void notifyStatusBar(Context c, Intent onClickIntent,
			String title, String desc, int lastnum, int noID) {
//
//		String ns = Context.NOTIFICATION_SERVICE;
//
//		onClickIntent.setAction(Long.toString(System.currentTimeMillis()));
//		onClickIntent.putExtra("LastNum", lastnum);
//		onClickIntent.putExtra("Bundles", bundle_quiz);
//		PendingIntent pending = PendingIntent.getActivity(c, noID,
//				onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//		RemoteViews contentView = new RemoteViews(c.getPackageName(),
//				R.layout.reward_notification);
//		int icon = R.drawable.steplively_notification_on;
//		long when = System.currentTimeMillis();
//		Notification noticed = new Notification(icon, title, when);
//		noticed.flags = (noticed.flags | Notification.FLAG_AUTO_CANCEL);
//		contentView.setTextViewText(R.id.notify_title, title);
//		contentView.setTextViewText(R.id.notify_text, desc);
//		contentView.setImageViewResource(R.id.notify_image, icon);
//		contentView.setTextColor(R.id.notify_title, NotificationStyle.getNotificationTextColor(c));
//		contentView.setTextColor(R.id.notify_text, NotificationStyle.getNotificationTextColor(c));
//
//		// contentView.setImageViewResource(R.id.image, icon);
//		noticed.contentView = contentView;
//		noticed.defaults = Notification.DEFAULT_SOUND;
//		noticed.contentIntent = pending;
//
//		// noticed.setLatestEventInfo(c, text, desc, pending);
//		NotificationManager noticedManager = (NotificationManager) c
//				.getSystemService(ns);
//		noticedManager.notify(noID, noticed);

	}

	public static boolean showReward(Context c, int count, int schedule,
			int id, int CheckCount, SharedPreferences pref) {
//		if (isTesting) {
//			count = 6;
//			schedule = 7;
//		}
//
//		// ignore 0 of 0 tasks
//		if (schedule < 0) {
//			Log.i(TAG, "StartDate is not set");
//			// Log.i(TAG, "condition -1");
//			return false;
//		}
//		CheckCount++;
//
//		pref.edit().putInt(ControllerService.CHECKCOUNT, CheckCount).commit();
//		if (isTesting) {
//		} else {
//			if (count == 0 && schedule == 0) {
//				Log.i(TAG, "0 of 0. No need to check timing");
//				// Log.i(TAG, "condition 0");
//				return true;
//
//			}
//		}
//
//		String title = "";
//		int AchiefCount = pref.getInt(ControllerService.ACHIEFCOUNT, 0);
//		double p = ((double) count) / schedule;
//
//		// Log.i(TAG, "p = " + p);
//
//		Intent startDialog = new Intent(c, InvisibleActivityForDialogBox.class);
//		int noID = -1;
//		if (p >= 0.8 || (count > 0 && schedule == 0)) {
//			int last_num = -2;
//			int reward_num = -2;
//			AchiefCount++;
//			pref.edit().putInt(ControllerService.ACHIEFCOUNT, AchiefCount)
//					.commit();
//
//			if (BuddyUtil.quizzesAvailable(c)) {
//				NUM_AVAILABLE_REWARD = 4;
//			} else {
//				NUM_AVAILABLE_REWARD = 3;
//			}
//			selectedReward = (int) (Math.random() * NUM_AVAILABLE_REWARD);
//
//			if (isTesting) {
//				// selectedReward = ContentAppUtil.BLOGGINGACTIVITY;
//			}
//		//	if (BuddyUtil.quizzesAvailable(c))
//		//		selectedReward = PROFILEQUIZZES;
//
//			RewardInfo rewardInfo = pickAReward(c, selectedReward, NORMAL);
//			rewardInfo = checker(c, rewardInfo, selectedReward);
//
//			title = String.format(
//					((p <= 1) ? REWARD_TITLE : REWARD_TITLE_EXTRA),
//					DataStorage.getFirstName(c, "Buddy"), getAppName(c, id),
//					count, schedule, rewardInfo.getRewardName());
//			title = (count > 0 && schedule == 0) ? String.format(
//					BONUS_MESSAGE_VACATION, getAppName(c, id),
//					rewardInfo.getRewardName()) : title;
//
//			if (selectedReward != PROFILEQUIZZES) {
//				last_num = StatDbContentProvider.getCurrentContentPosition(c,
//						rewardInfo.getColumnName());
//
//				if (last_num == -1) {
//					noID = Utilities.getBuddyNotifyId(c) + 1;
//					Utilities.setBuddyNotifyId(c, noID);
//					startDialog.putExtra("k", -1).putExtra("trigger", -1)
//							.putExtra("title", "Oops")
//							.putExtra("desc", DATABASE_ERROR_MESSAGE)
//							.putExtra("noID", noID);
//
//					notifyStatusBar(c, startDialog, TITLE_1, title + ".", -2,
//							noID);
//					Log.e(TAG, "error accessing database");
//					return true;
//				}
//
//				last_num++;
//				StatDbContentProvider.setCurrentContentPosition(c, last_num,
//						rewardInfo.getColumnName());
//				reward_num = StatDbContentProvider.getCurrentContentPosition(c,
//						rewardInfo.getColName());
//				if (!rewardInfo.getRewardName().equals(VIDEO_NAME)) {
//					reward_num = last_num;
//					StatDbContentProvider.setCurrentContentPosition(c,
//							last_num, rewardInfo.getColName());
//				}
//
//				putToSP(c, last_num, rewardInfo);
//				putNumToSP(c, reward_num, rewardInfo);
//				syncData(c, Integer.toString(last_num),
//						rewardInfo.getColumnName());
//				syncData(c, Integer.toString(reward_num),
//						rewardInfo.getColName());
//				title = title + " (No." + last_num + ").";
//				if (last_num > 0) {
//					startDialog.putExtra("desc", title);
//				}
//			}
//
//			if (selectedReward == PROFILEQUIZZES) {
//				startDialog.putExtra("desc", "\""+BUDDY_QUESTION+"\"");
//			}
//
//			noID = Utilities.getBuddyNotifyId(c) + 1;
//			Utilities.setBuddyNotifyId(c, noID);
//			startDialog.putExtra("k", selectedReward)
//					.putExtra("trigger", NORMAL).putExtra("title", (selectedReward == ContentAppUtil.PROFILEQUIZZES)?TITLE_BUDDY:TITLE_1)
//					.putExtra("noID", noID);
//
//			notifyStatusBar(c, startDialog, (selectedReward == ContentAppUtil.PROFILEQUIZZES)?TITLE_BUDDY:TITLE_1, title, last_num, noID);
//			logTriggerToDatabase(c, TAG, (count > 0 && schedule == 0)
//					? "VacationDays"
//					: "Normal", ContentAppUtil.getAppName(c, id),
//					String.format("%d of %d tracked", count, schedule),
//					rewardInfo.getRewardName(), Utilities.getBuddyNotifyId(c));
//
//			// Log.i(TAG, "condition 1");
//		} else if (p >= 0.6) {
//			String desc = String.format(FAILURE_TITLE[2],
//					DataStorage.getFirstName(c, "Buddy"), getAppName(c, id),
//					count, schedule);
//			int kError = (int) (Math.random() * FAILURE_MESSAGE.length);
//			noID = Utilities.getBuddyNotifyId(c) + 1;
//			Utilities.setBuddyNotifyId(c, noID);
//			startDialog.putExtra("k", -1).putExtra("trigger", -1)
//					.putExtra("title", TITLE_2[2]).putExtra("desc", desc)
//					.putExtra("noID", noID);
//
//			notifyStatusBar(c, startDialog, TITLE_2[2],
//					FAILURE_MESSAGE[kError], -2, noID);
//			Log.i(TAG, desc);
//
//		} else if (p >= 0.4) {
//			String desc = String.format(FAILURE_TITLE[1],
//					DataStorage.getFirstName(c, "Buddy"), getAppName(c, id),
//					count, schedule);
//			int kError = (int) (Math.random() * FAILURE_MESSAGE.length);
//			noID = Utilities.getBuddyNotifyId(c) + 1;
//			Utilities.setBuddyNotifyId(c, noID);
//			startDialog.putExtra("k", -1).putExtra("trigger", -1)
//					.putExtra("title", TITLE_2[1]).putExtra("desc", desc)
//					.putExtra("noID", noID);
//
//			notifyStatusBar(c, startDialog, TITLE_2[1],
//					FAILURE_MESSAGE[kError], -2, noID);
//			Log.i(TAG, desc);
//			// Log.i(TAG, "condition 3");
//
//		} else {
//			String desc = String.format(FAILURE_TITLE[0],
//					DataStorage.getFirstName(c, "Buddy"), getAppName(c, id),
//					count, schedule);
//			int kError = (int) (Math.random() * FAILURE_MESSAGE.length);
//			noID = Utilities.getBuddyNotifyId(c) + 1;
//			Utilities.setBuddyNotifyId(c, noID);
//			startDialog.putExtra("k", -1).putExtra("trigger", -1)
//					.putExtra("title", TITLE_2[0]).putExtra("desc", desc)
//					.putExtra("noID", noID);
//
//			notifyStatusBar(c, startDialog, TITLE_2[0],
//					FAILURE_MESSAGE[kError], -2, noID);
//			Log.i(TAG, desc);
//			// Log.i(TAG, "condition 4");
//
//		}

		return true;
	}

	public static void logTriggerToDatabase(Context c, String tag,
			String trigger, String appName, String condition,
			String rewardName, int noID) {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		String date = sdf.format(new Date());
//		String s = "Reward Triggered: %s for app: %s for conditions: %s with content: %s at time: %s with notification id: %d";
//		String string = String.format(s, trigger, appName, condition,
//				rewardName, date.toString(), noID);
//		Log.i(TAG, string);
//		StepLivelyActivity.logToLocalDb(c, TAG, string);

	}

	private static void logTriggerToDatabase(Context c, String tag,
			String trigger, String appName, String condition, String rewardName) {
		// TODO Auto-generated method stub
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		String date = sdf.format(new Date());
//		String s = "Reward Triggered: %s for app: %s for conditions: %s with content: %s at time %s";
//		String string = String.format(s, trigger, appName, condition,
//				rewardName, date.toString());
//		Log.i(TAG, string);
//		StepLivelyActivity.logToLocalDb(c, TAG, string);
	}

	public static void logNotificationClick(Context c, String tag, int noID) {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		String date = sdf.format(new Date());
//		String string = String.format(
//				"Accessed reward with id: %d triggered at: %s", noID,
//				date.toString());
//		Log.i(TAG, string);
//		StepLivelyActivity.logToLocalDb(c, TAG, string);
	}

	public static void logNewContentViewedNum(Context c, String tag,
			int numNew, int total) {
		// TODO Auto-generated method stub
//		String s = String.format("Viewed: %d of %d for content: %s", numNew,
//				total, tag);
//		Log.i(TAG, s);
//		StepLivelyActivity.logToLocalDb(c, TAG, s);
	}

	public static void logAdditionalContentViewedNum(Context c, String tag,
			int numAdd) {
		// TODO Auto-generated method stub
//		String s = "" + numAdd + " content has been viewed in " + tag;
//		Log.i(TAG, s);
//		StepLivelyActivity.logToLocalDb(c, TAG, s);
	}

	public static void logViewedToDatabase(Context c, String tag, int current,
			int total) {
//		String s = String.format("Viewed: %d of %d for content: %s", current,
//				total, tag);
//		Log.i(TAG, s);
//		StepLivelyActivity.logToLocalDb(c, TAG, s);

	}

	public static void syncData(Context c, String string, String columnName) {
		// Sync with server database
//		String[][] s = new String[4][2];
//		SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
//		s[0][0] = "PhoneID";
//		s[1][0] = "CreateDate";
//		s[2][0] = "ColumnName";
//		s[3][0] = "Value";
//		TelephonyManager tm = (TelephonyManager) c
//				.getSystemService(Context.TELEPHONY_SERVICE);
//		s[0][1] = tm.getDeviceId();
//		s[1][1] = sp.format(new Date());
//		s[2][1] = columnName;
//		s[3][1] = string;
//
//		SyncDatabase.sync_transit(ControllerService.getSwapServerAddress(ControllerService.getIsTester(c))+SyncDatabase.SYNC_REWARD_URL, s);
	}

	public static void startBonusActivity(Context c, String appName) {

//		if (getIsRewardAvailable(c)) {
//			SharedPreferences sp = c.getSharedPreferences(BONUS_DATA,
//					Context.MODE_PRIVATE);
//			Date date = new Date();
//			int today = date.getYear() * 366 + date.getMonth() * 31
//					+ date.getDate();
//			int lastModDay = sp.getInt(LAST_MOD_DAY, -1);
//			if (today != lastModDay) {
//				sp.edit().putInt(LAST_MOD_DAY, today).commit();
//				sp.edit().putInt(BONUS_COUNT, 0).commit();
//			}
//			int bonusCount = sp.getInt(BONUS_COUNT, 0);
//			Log.i(TAG, "bonusCount=" + bonusCount);
//			if (bonusCount >= DAILY_MAX_BONUS)
//				return;
//
//			double r = Math.random();
//
//			if (isTesting) {
//				r = 0;
//			}
//			SharedPreferences rewardData = c.getSharedPreferences("rewardData",
//					Context.MODE_PRIVATE);
//			Log.i(TAG, "lucky num = " + r);
//			if (r <= 0.1) {
//				if (BuddyUtil.quizzesAvailable(c)) {
//					NUM_AVAILABLE_REWARD = 4;
//				} else {
//					NUM_AVAILABLE_REWARD = 3;
//				}
//				selectedReward = (int) (Math.random() * NUM_AVAILABLE_REWARD);
//
//				if (isTesting) {
//				//	 selectedReward = ContentAppUtil.BLOGGINGACTIVITY;
//				}
//			//	if (BuddyUtil.quizzesAvailable(c))
//			//		selectedReward = ContentAppUtil.PROFILEQUIZZES;
//				
//				// android.os.Debug.waitForDebugger();
//				RewardInfo ri = pickAReward(c, selectedReward, BONUS);
//				ri = checker(c, ri, selectedReward);
//				Intent i = new Intent(c, BonusActivity.class);
//				i.putExtra(REWARDAPP, selectedReward);
//				i.putExtra(APP_NAME, appName);
//
//				if (selectedReward != PROFILEQUIZZES) {
//					int last_num = StatDbContentProvider
//							.getCurrentContentPosition(c, ri.getColumnName());
//
//					if (last_num == -1) {
//						Intent startDialog = new Intent(c,
//								InvisibleActivityForDialogBox.class);
//						rewardData.edit()
//								.putString("desc", DATABASE_ERROR_MESSAGE)
//								.commit();
//						startDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						c.startActivity(startDialog);
//						return;
//					}
//					last_num++;
//					StatDbContentProvider.setCurrentContentPosition(c,
//							last_num, ri.getColumnName());
//
//					int reward_num = StatDbContentProvider
//							.getCurrentContentPosition(c, ri.getColName());
//					if (!ri.getRewardName().equals(VIDEO_NAME)) {
//						reward_num = last_num;
//						StatDbContentProvider.setCurrentContentPosition(c,
//								last_num, ri.getColName());
//					}
//
//					putToSP(c, last_num, ri);
//					putNumToSP(c, reward_num, ri);
//
//					syncData(c, Integer.toString(last_num), ri.getColumnName());
//					syncData(c, Integer.toString(reward_num), ri.getColName());
//					i.putExtra(ri.getExtraName(), last_num);
//					i.putExtra(BONUS_MESSAGE_NAME, BONUS_MESSAGE);
//				}else{
//					i.putExtra("desc", BUDDY_QUESTION);
//					i.putExtra("Bundles", bundle_quiz);
//				}
//				
//				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//				Log.i(TAG, "Got bonus from app " + appName);
//
//				c.startActivity(i);
//				logTriggerToDatabase(c, TAG, "Bonus", appName, "Good luck",
//						ri.getRewardName());
//
//				bonusCount++;
//				sp.edit().putInt(BONUS_COUNT, bonusCount).commit();
//
//			} else {
//				return;
//			}
//		}
	}

	private static String getAppName(Context c, int id) {
		switch (id) {
			case FOOD_TRACKING_ID :
				return FOOD_TRACKING;
			case WEIGHING_ID :
				return WEIGHING;
			case STEP_LIVELY_ID :
				return STEP_LIVELY;
			case HEALTHY_MEAL_TRACKER_ID :
//				if (TrackFood.getIsLifestyleAvailable(c, new Date()))
//					return HEALTHY_LIFESTYLE_TRACKER;
//				else
					return HEALTHY_MEAL_TRACKER;
			case MEAT_TRACKING_ID :
				return MEAT_TRACKING;
			case PA_PTRACKING_ID :
				return PA_TRACKING;
			case SSB_TRACKING_ID :
				return SSB_TRACKING;
			case GOAL_SETTING_ID :
				return GOAL_SETTING;
			case VEGGIES_TRACKING_ID :
				return VEGGIES_TRACKING;
			case FRUIT_TRACKING_ID :
				return FRUIT_TRACKING;
			default :
				return "";
		}
	}

	public static boolean comicExisted() {
		String mPath;
		if (havesd()) {
			mPath = Environment.getExternalStorageDirectory()+"/.city/comics/";
		} else {
			mPath = "/data/data/.city/comics/";
		}
		File f = new File(mPath);

		return f.exists();
	}

	private static boolean havesd() {
		String status = Environment.getExternalStorageState();
		return (status.equals(Environment.MEDIA_MOUNTED));
	}

	public static boolean blogExisted() {
		String mPath;
		if (havesd()) {
			mPath = Environment.getExternalStorageDirectory()+"/.city/blogs/";
		} else {
			mPath = "/data/data/.city/blogs/";
		}
		File f = new File(mPath);

		return f.exists();
	}

	public static boolean isMovieAvailable(int index) {
		String id = "";
		if (index > MOVIE_LIST.length)
			return false;
		else
			id = MOVIE_LIST[index - 1];
		HttpClient lClient = new DefaultHttpClient();

		HttpGet lGetMethod = new HttpGet(
				OpenYouTubePlayerActivity.YOUTUBE_VIDEO_INFORMATION_URL + id);

		HttpResponse lResp = null;

		try {
			lResp = lClient.execute(lGetMethod);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		ByteArrayOutputStream lBOS = new ByteArrayOutputStream();
		String lInfoStr = null;

		try {
			lResp.getEntity().writeTo(lBOS);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		try {
			lInfoStr = new String(lBOS.toString("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		String[] lArgs = lInfoStr.split("&");
		return ((!lArgs[0].equals("status=fail"))
				&& (!((lArgs.length > 1) && (lArgs[1].equals("status=fail")))));
	}
	
	public static List<Integer> getMoviesAvailable(int stopAtPosition) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 1; i <= MOVIE_LIST.length; i++ ) {
			if (isMovieAvailable(i)) {
				result.add(i);
			}
			if (result.size() >= stopAtPosition) {
				break;
			}
		}
		return result;
		
	}
	
	public static int getMovieAvailableForRewardPosition(int position) {
		List<Integer> l = getMoviesAvailable(position);
		
		if (position > l.size()) {
			return l.get(l.size() - 1);
		}
		return l.get(position - 1);
	}
	

	public static void setBonusOpp(Context c, String tag, boolean opp) {
		SharedPreferences pref = c.getSharedPreferences("BonusOpp",
				Context.MODE_PRIVATE);
		pref.edit().putBoolean(tag, opp).commit();

	}

	public static boolean getBonusOpp(Context c, String tag) {
		SharedPreferences pref = c.getSharedPreferences("BonusOpp",
				Context.MODE_PRIVATE);
		return pref.getBoolean(tag, false);
	}

	public static boolean getIsRewardAvailable(Context aContext) {
//		if (!AuthorizationChecker.IsCPAuthorized(aContext))
//			return false;
//		int day = DataStorage.getDayNumber(aContext, new Date());
//		return day >= 472;
		return true;
	}

	public static String getFileName(String s) {
		String[] tmp = s.split("/");
		return tmp[tmp.length - 1];
	}

}
