package edu.neu.android.mhealth.uscteensver1.video;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.support.ContentAppUtil;
import edu.neu.android.wocketslib.utils.BaseActivity;
import edu.neu.android.wocketslib.utils.Log;
import edu.neu.android.wocketslib.utils.NetworkDetector;
import edu.neu.android.wocketslib.video.openyoutubeplayer.OpenYouTubePlayerActivity;

public class VideoActivity extends BaseActivity {
	private Button btnPlayLatestVideo;
	static final String TAG = "Video";
	public static final String LAST_MOVIE = "Last_Movie";
//	private boolean isCPCondition = true;
	private Button btnNeverMind;
	private String movieUrl = null;
	private ListView videoList;
	private boolean isConnected;
	public SimpleAdapter listAdapter;
	public int lastnum;

	public ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, TAG);
		// initializing
		isConnected = NetworkDetector.isServerAvailable();
		lastnum = 5;
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.movie_list_activity);
		btnPlayLatestVideo = (Button) findViewById(R.id.btnPlayTutorial);
		btnNeverMind = (Button) findViewById(R.id.btnNeverMind);
		videoList = (ListView) findViewById(R.id.viewdMoviesList);

		if (!isConnected) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(
					VideoActivity.this)
					.setTitle("Argh")
					.setMessage(
							"Internet connection error. Please check your internet and try again later.")
					.setPositiveButton("All right",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
			dialog.show();
		}
		btnNeverMind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				VideoActivity.this.finish();
			}

		});
		setListView();
		InitTask it = new InitTask();
		it.execute(this);
	}

	public void noConnection() {

	}

	public void onResume() {
		super.onResume();

		movieUrl = getUrl(lastnum);

		String[] movieInfo = MoviePlayerHelper.getMovieNameLengthIcon(movieUrl);

		if (movieInfo != null) {
			String title = movieInfo[0];
			String length = movieInfo[1];

			btnPlayLatestVideo.setText(title);
			btnPlayLatestVideo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					Intent lVideoIntent = new Intent(
						null, Uri.parse("ytv://" + movieUrl), VideoActivity.this, OpenYouTubePlayerActivity.class
					);
					startActivity(lVideoIntent);
				}
			});
			String duration = "";
			int l = Integer.parseInt(length);
			int min = l / 60;
			int sec = l % 60;
			duration = "(About ";
			if (min == 1)
				duration += min + " minute ";
			else if (min != 0)
				duration += min + " minutes ";
			if (sec == 1)
				duration += sec + " second ";
			else if (sec != 0)
				duration += sec + " seconds ";
			duration = duration.substring(0, duration.length() - 1) + ")";

			TextView videoRunTime = (TextView) findViewById(R.id.videoRunTime);
			videoRunTime.setText(duration);
		}
	}

	public void setListView() {
		listAdapter = new SimpleAdapter(VideoActivity.this, list,
				R.layout.listview_videoshow, new String[] { "icon", "name" },
				new int[] { R.id.videoIcon, R.id.videoName });
		listAdapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View arg0, Object arg1, String arg2) {
				if (arg0 instanceof ImageView && arg1 instanceof Bitmap) {
					ImageView iv = (ImageView) arg0;
					iv.setImageBitmap((Bitmap) arg1);
					return true;
				}
				return false;
			}
		});
		videoList.setAdapter(listAdapter);
		if (isConnected)
			videoList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					String url = (String) list.get(position).get("id");
					Intent lVideoIntent = new Intent(
						null, Uri.parse("ytv://" + url), VideoActivity.this, OpenYouTubePlayerActivity.class
					);
					Log.i(TAG, "Picked Video " + id);
					Log.i(TAG, "Showed Video " + id);
					ContentAppUtil.logViewedToDatabase(VideoActivity.this, TAG,
							(int) id, 10);
					startActivity(lVideoIntent);
				}
			});
	}

	private String getUrl(int last_num) {
		last_num = Math.min(ContentAppUtil.MOVIE_LIST.length, last_num);
		return ContentAppUtil.MOVIE_LIST[last_num - 1];
	}

	private int getMovieLatestNum() {
		int ls = this.getSharedPreferences(ContentAppUtil.VIDEO_DATA,
				MODE_PRIVATE).getInt(ContentAppUtil.REWARDPOSITION, 0);
		// ls = 40;
		if (ls > ContentAppUtil.MOVIE_LIST.length) {
			ls = ContentAppUtil.MOVIE_LIST.length;
			AlertDialog.Builder alertbox1 = new AlertDialog.Builder(this);
			alertbox1
					.setMessage(ContentAppUtil.NOT_AVAILABLE_ON_SERVER_MESSAGE);
			alertbox1.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
						}
					}).show();
		}
		return ls;
	}

	public Bitmap getImgFromServer(String address) {
		try {
			URL aURL = new URL(address);
			URLConnection con = aURL.openConnection();
			con.connect();
			InputStream is = con.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			Bitmap bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
			return bm;
		} catch (IOException e) {
		}
		InputStream is = this.getResources().openRawResource(R.drawable.noidea);
		Bitmap bm = BitmapFactory.decodeStream(is);
		return bm;
	}

	protected class InitTask extends AsyncTask<Context, String, String> {

		@Override
		protected String doInBackground(Context... params) {

			addListItem();
			return "Complete";

		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		// -- called from the publish progress
		// -- notice that the datatype of the second param gets passed to this
		// method
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			// android.os.Debug.waitForDebugger();

			listAdapter.notifyDataSetChanged();
		}

		// -- called if the cancel button is pressed
		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		// -- called as soon as doInBackground method completes
		// -- notice that the third param gets passed to this method
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}

		private void addListItem() {
			if (isConnected) {
				for (int i = lastnum - 1; i > 0; i--) {
					String[] movieInfo = MoviePlayerHelper
							.getMovieNameLengthIcon(getUrl(i));
					HashMap<String, Object> map = new HashMap<String, Object>();
					try {
							map.put("icon", getImgFromServer(movieInfo[2]));
							map.put("name", movieInfo[0]);
							map.put("id", getUrl(i));
							list.add(map);
					} catch (Exception e) {
						Log.e(TAG, "Video " + i + " is not available");
					}
					publishProgress("");
				}
			} else {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("icon", R.drawable.noidea);
				map.put("name", "No Internet Connection.");
				list.add(map);
			}
		}
	}

}
