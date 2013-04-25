package com.snaplion.myapp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.snaplion.beans.BGImages;
import com.snaplion.beans.Bottom;
import com.snaplion.beans.NewsDetails;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.BGImageDownloaderService;
import com.snaplion.kingsxi.R;
import com.snaplion.mail.SLMailActivity;
import com.snaplion.music.SLMusicPlayerActivity;
import com.snaplion.news.SLNewsActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.BottomClass;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class SnapLionMyAppActivity extends AppSuperActivity {
	protected static final int SHOW_NEXT_ACTIVITY = 5;
	private static final int BOTTUM_BAR = 6;
	private static final int SHOW_PLAYER = 7;
	private static final int DIALOG_ALERT = 8;
	private static final int DIALOG_DISPLAY = 9;
	private GalleryView bgGallery = null;
	private ArrayList<BGImages> bgImges = new ArrayList<BGImages>();
	private ArrayList<NewsDetails> newsDetails = new ArrayList<NewsDetails>();
	private HashMap<String, String> audioDetails = new HashMap<String, String>();
	// private static ImageAdapter adapter = null;
	private LinearLayout bbLL = null;
	private LinearLayout playerLayout = null;
	private int width = 0;
	private TextView flashNewsTxt = null;
	private int Flag = 0;
	public static int countPhoto = 0;
	public static boolean homeMusic = true;
	private int count = 0;
	private int news_count = 0;
	public int HOME_IMAGE = 1;
	public static boolean KillFlag = false;
	AppManager appManager;
	private LinearLayout homeTopLayout;

	@Override
	protected void onResume() {
		SnapLionMyAppActivity.KillFlag = true;

		try {
			com.snaplion.music.SLMusicPlayerActivity.seekBarProgress
					.setSecondaryProgress(com.snaplion.music.SLMusicPlayerActivity.seekbarPosition);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		com.snaplion.music.SLMusicPlayerActivity.getPlayer(
				SnapLionMyAppActivity.this, getApplicationContext(),
				playerLayout, appManager.getAPP_ID());

//		if (Utility.isOnline(this)
//				&& appManager.getAPP_ID().equalsIgnoreCase("222")) {
		if (Utility.isOnline(this)) {
			SharedPreferences settings = getSharedPreferences("SLUpdate"
					+ appManager.getAPP_ID(), MODE_PRIVATE);
			if (settings.getBoolean("isPauseMusic", false)) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("isPauseMusic", false);
				editor.commit();
				pauseMusiconStartup();
			}
		}
		super.onResume();
	}

	private void pauseMusiconStartup() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					if (com.snaplion.music.SLMusicPlayerActivity.mediaPlayer
							.isPlaying())
						com.snaplion.music.SLMusicPlayerActivity.playBtn
								.performClick();
					else
						pauseMusiconStartup();
				} catch (Exception ex) {
					pauseMusiconStartup();
				}
			}
		}, 10);
	}

	class HomeContentDownloader extends
			AsyncTask<Void, Void, ArrayList<String>> {
		Context context;

		public HomeContentDownloader(Context context) {
			this.context = context;
		}

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			try {
				if (getSharedPreferences("SLBGImage" + appManager.getAPP_ID(),
						MODE_PRIVATE).getBoolean("DownloadBGImage", false)) {
					try {
						if (Utility.isOnline(getApplicationContext())) {
							Intent BGImageDownloaderServiceIntent = new Intent(
									context, BGImageDownloaderService.class);
							startService(BGImageDownloaderServiceIntent);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				String str = null;
				if (getSharedPreferences("SLPref" + appManager.getAPP_ID(),
						MODE_PRIVATE).getString("AppBasicData", "").equals("")) {
					InputStream in = HttpConnection
							.connect(appManager.HOME_DETAILS_URL);
					Utility.debug("HOME_DETAILS_URL : "
							+ appManager.HOME_DETAILS_URL);
					if (in != null) {
						str = Utility.getString(in);
					}
				}
				if (str != null) {
					SharedPreferences.Editor editor = getSharedPreferences(
							"SLPref" + appManager.getAPP_ID(), MODE_PRIVATE)
							.edit();
					editor.putString("AppBasicData" + appManager.getAPP_ID(),
							str);
					editor.commit();

					SharedPreferences myPref = getSharedPreferences("SLUpdate"
							+ appManager.getAPP_ID(), MODE_PRIVATE);
					String str1 = myPref.getString("MTime" + "1001"
							+ appManager.getAPP_ID(), "");
					SharedPreferences.Editor editor1 = myPref.edit();
					editor1.putBoolean("1001", false);
					editor1.putString("1001" + appManager.getAPP_ID(), str1);
					editor1.commit();

					ArrayList<String> bgImges = new ArrayList<String>();
					int idx1 = 0;
					int idx2 = 0;
					while (str.indexOf("<bgpicture>", idx1) != -1) {
						idx1 = str.indexOf("<bgpicture>", idx1);
						if (idx1 != -1) {
							idx1 = idx1 + "<bgpicture>".length();
							idx2 = str.indexOf("</bgpicture>", idx1);
							String url = str.substring(idx1, idx2);
							Utility.debug("service home img url" + url);
							bgImges.add(url);
							idx1 = idx2;
						}
					}
					return bgImges;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			super.onPostExecute(result);
			if (result != null) {
				ImageAdapter adapter = new ImageAdapter(context, result);
				bgGallery.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_home);
		bgGallery = (GalleryView) findViewById(R.id.home_gallery_bg);

		Utility.closeAllBelowActivities(this);
		appManager = AppManager.getInstance();

		// shrikant
		// Version check for Preview app will be done on Login screen.
		if (!appManager.PREVIEWAPP_FLAG) {
			if (getSharedPreferences("SLUpdate" + appManager.getAPP_ID(),
					MODE_PRIVATE).getBoolean("checkAppVersion", false)) {
				appManager.checkVersion(this);
				Editor edt = getSharedPreferences(
						"SLUpdate" + appManager.getAPP_ID(), MODE_PRIVATE)
						.edit();
				edt.putBoolean("checkAppVersion", false);
				edt.commit();
			}
		}
		// ///////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent, this);
		// ///////////////////////////
		SnapLionMyAppActivity.KillFlag = true;
		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();

		Intent i = getIntent();

		com.snaplion.music.SLMusicActivity.music = false;

		playerLayout = (LinearLayout) findViewById(R.id.home_player_layout);

		try {
			if (i.getIntExtra("Count", 0) == 2) {
				news_count = 1;
			} else if (i.getIntExtra("Count", 0) == 3) {
				news_count = 1;
				audioDetails = null;
				// try
				// {
				// com.snaplion.music.SLMusicPlayerActivity.seekBarProgress.setSecondaryProgress(com.snaplion.music.SLMusicPlayerActivity.seekbarPosition);
				// }
				// catch (Exception e)
				// {
				// e.printStackTrace();
				// }
				// com.snaplion.music.SLMusicPlayerActivity.getPlayer(SnapLionMyAppActivity.this,getApplicationContext(),playerLayout,appManager.getAPP_ID());
				audioDetails = new HashMap<String, String>();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		TextView appTxt = (TextView) findViewById(R.id.app_name_txt);
		appTxt.setText(appManager.getAPP_NAME());
		appTxt.setTypeface(appManager.lucida_grande_regular);

		bbLL = (LinearLayout) findViewById(R.id.tab_bar);

		if (appManager.PREVIEWAPP_FLAG) {
			Button homeBtn = (Button) findViewById(R.id.home_btn);
			homeBtn.setVisibility(View.VISIBLE);
			(homeBtn).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					SnapLionMyAppActivity.KillFlag = false;
					HOME_IMAGE = 1;
					try {
						com.snaplion.music.SLMusicPlayerActivity
								.closePlayer(getApplicationContext());
					} catch (Exception e) {
						e.printStackTrace();
					}
					finishFromChild(getParent());
				}
			});
		}

		flashNewsTxt = (TextView) findViewById(R.id.flash_news);
		flashNewsTxt.setTypeface(appManager.lucida_grande_regular);

		SharedPreferences settings = getSharedPreferences(
				"MLFlag" + appManager.getAPP_ID(), MODE_PRIVATE);
		if (!settings.getBoolean("First", false)) {
			handler.sendEmptyMessage(DIALOG_ALERT);
			SharedPreferences settings1 = getSharedPreferences("MLFlag"
					+ appManager.getAPP_ID(), MODE_PRIVATE);
			SharedPreferences.Editor editor = settings1.edit();
			editor.putBoolean("First", true);
			editor.commit();
			appManager.sendDeviceInformation(this);
		}

		handler.sendEmptyMessage(appManager.PROGRESS);

		new HomeContentDownloader(this).execute();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppManager.PROGRESS:
				Utility.FileCreate();

				SharedPreferences myPref = getSharedPreferences("SLPref"
						+ appManager.getAPP_ID(), MODE_PRIVATE);
				String bottumStr = myPref.getString("BottomTabData", "");

				if (bottumStr != "") {
					if (!showBBar(bottumStr)) {
						new Thread(new Runnable() {
							public void run() {
								handler.sendEmptyMessage(SHOW_PLAYER);
							}
						}).start();
					} else {
						// shrikant
						newsData();
					}
				} else {
					new Thread(new Runnable() {
						public void run() {
							handler.sendEmptyMessage(SHOW_PLAYER);
						}
					}).start();
				}
				break;
			case SHOW_PLAYER:
				SharedPreferences myPref1 = getSharedPreferences("SLPref"
						+ appManager.getAPP_ID(), MODE_PRIVATE);
				String resStr = myPref1.getString(
						"AppBasicData" + appManager.getAPP_ID(), "");
				try {
					if (resStr != "") {
						Flag = 1;
						try {
							InputStream in = new ByteArrayInputStream(
									resStr.getBytes("UTF-8"));

							try {
								bgImges.clear();
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								getAppBasicDetails(in);
							} catch (Exception e) {
								getAppBasicDetails(in);
							}

							if (news_count == 1) {
								SharedPreferences settings = getSharedPreferences(
										"NewsPref" + appManager.getAPP_ID(),
										MODE_PRIVATE);
								newsDetails = getNewsSetails(settings
										.getString("NewsHome", ""));
								news_count = 2;
								newsData();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						for (int j = 0; j < bgImges.size(); j++) {
							BGImages bi = new BGImages();
							bi = bgImges.get(j);
							// bi.setStringPath("HomeBG"+appManager.getAPP_ID()+bgImges.get(j).getBghomeid());
							bi.setStringPath("HomeBG" + appManager.getAPP_ID()
									+ bgImges.get(j).getBgpicture().hashCode());
							bgImges.set(j, bi);
							bi = null;
						}
					}
					// bgGallery.setAdapter(adapter);
					// adapter.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// handler.sendEmptyMessage(BOTTUM_BAR);
				break;
			case BOTTUM_BAR:
				// getMusicPlayer();
				if (Utility.isOnline(getApplicationContext())) {
					try {
						if (Flag == 0)
							showDialog(appManager.PROGRESS);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						getBottumBarDetails(appManager.getAPP_ID());
					} catch (Exception e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(appManager.DISPLAY);
				} else {
					handler.sendEmptyMessage(appManager.ERROR);
				}
				break;
			case AppManager.DISPLAY:
				try {
					getAppBasicDetails(appManager.getAPP_ID());
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.sendEmptyMessage(SHOW_NEXT_ACTIVITY);
				break;
			case SHOW_NEXT_ACTIVITY:
				try {
					getSpalshScreen(appManager.getAPP_ID());
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case AppManager.ERROR:
				Toast.makeText(getApplicationContext(),
						"Network Fail.Please try later .!", Toast.LENGTH_LONG)
						.show();
				break;
			case DIALOG_ALERT:
				showDialog(appManager.DIALOG_DISPLAY);
				// break;
			}
		}
	};

	// Bottom bar details here
	private boolean showBBar(String bottumStr) {
		ArrayList<Bottom> bottomIconeList = new ArrayList<Bottom>();
		boolean bb = false;
		try {
			try {
				bbLL.removeAllViews();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				InputStream in = new ByteArrayInputStream(
						bottumStr.getBytes("UTF-8"));
				try {
					bottomIconeList.clear();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (in != null)
					bottomIconeList = BottomClass.getBottomDetails(in);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (bottomIconeList.size() > 5) {
				com.snaplion.util.BottomClass.showBLayout(this, bbLL, width,
						appManager.getAPP_ID(), appManager.getAPP_NAME());
				if (homeMusic) {
					com.snaplion.music.SLMusicPlayerActivity.getPlayer(
							SnapLionMyAppActivity.this,
							getApplicationContext(), playerLayout,
							appManager.getAPP_ID());
				}
				return bb = false;
			} else {
				try {
					showDialog(DIALOG_DISPLAY);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return bb = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bb;
	}

	protected ArrayList<NewsDetails> getNewsSetails(String string) {
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(string.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		final int bgimage = 1;
		final int newsrecord = 2;
		final int newsid = 3;
		final int title = 4;
		final int content = 5;
		final int image = 6;
		final int eventdate = 7;
		final int caldate = 8;
		final int type = 9;
		final int imagename = 10;
		final int month = 11;
		final int year = 12;
		final int showurl = 13;
		final int img1 = 14;
		final int starttime = 15;
		final int endtime = 16;
		final int city = 17;
		final int country = 18;
		final int venuedetails = 19;
		int tagName = 0;

		NewsDetails value = null;
		ArrayList<NewsDetails> values = new ArrayList<NewsDetails>();

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, HTTP.UTF_8);

			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("bgimage")) {
						tagName = bgimage;
					} else if (parser.getName().equals("newsrecord")) {
						value = new NewsDetails();
						tagName = newsrecord;
					} else if (parser.getName().equals("newsid")) {
						tagName = newsid;
					} else if (parser.getName().equals("title")) {
						tagName = title;
					} else if (parser.getName().equals("content")) {
						tagName = content;
					} else if (parser.getName().equals("image")) {
						tagName = image;
					} else if (parser.getName().equals("eventdate")) {
						tagName = eventdate;
					} else if (parser.getName().equals("caldate")) {
						tagName = caldate;
					} else if (parser.getName().equals("type")) {
						tagName = type;
					} else if (parser.getName().equals("imagename")) {
						tagName = imagename;
					} else if (parser.getName().equals("month")) {
						tagName = month;
					} else if (parser.getName().equals("year")) {
						tagName = year;
					} else if (parser.getName().equals("showurl")) {
						tagName = showurl;
					} else if (parser.getName().equals("img1")) {
						tagName = img1;
					} else if (parser.getName().equals("starttime")) {
						tagName = starttime;
					} else if (parser.getName().equals("endtime")) {
						tagName = endtime;
					} else if (parser.getName().equals("city")) {
						tagName = city;
					} else if (parser.getName().equals("country")) {
						tagName = country;
					} else if (parser.getName().equals("venuedetails")) {
						tagName = venuedetails;
					}
				}
				if (eventType == XmlPullParser.TEXT) {
					switch (tagName) {
					case bgimage:
						// newsBG = parser.getText();
						break;
					case newsrecord:
						break;
					case newsid:
						value.setNewsid(parser.getText());
						break;
					case title:
						value.setNewstext(parser.getText());
						break;
					case content:
						// value.setContent(parser.getText());
						break;
					case image:
						// value.setImage(parser.getText());
						break;
					case eventdate:
						// value.setEventdate(parser.getText());
						break;
					case caldate:
						// value.setCaldate(parser.getText());
						break;
					case type:
						// value.setType(parser.getText());
						break;
					case imagename:
						// value.setImagename(parser.getText());
						break;
					case month:
						// value.setMonth(parser.getText());
						break;
					case year:
						// value.setYear(parser.getText());
						break;
					case showurl:
						// value.setShowurl(parser.getText());
						break;
					case img1:
						// value.setImg1(parser.getText());
						break;
					case starttime:
						// value.setStarttime(parser.getText());
						break;
					case endtime:
						// value.setEndtime(parser.getText());
						break;
					case city:
						// value.setCity(parser.getText());
						break;
					case country:
						// value.setCountry(parser.getText());
						break;
					case venuedetails:
						// value.setVenuedetails(parser.getText());
						break;
					default:
						break;
					}
					tagName = 0;
				}

				if (eventType == XmlPullParser.END_TAG) {
					if (parser.getName().equals("newsrecord")) {
						values.add(value);
						value = null;
					}
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return values;
	}

	// News Display
	private void newsData() {
		final Handler mHandler = new Handler();

		Timer t = new Timer();
		TimerTask scanTask = new TimerTask() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					public void run() {
						String newsStr = null;
						try {
							newsStr = newsDetails.get(count).getNewstext();
						} catch (Exception e) {
							count = 0;
						}

						if (newsStr != null) {
							flashNewsTxt.setText(newsDetails.get(count)
									.getNewstext());
							// Utility.debug("News Flash["+count+"]:"+newsDetails.get(count).getNewstext());
						}
						count++;
						if (count == newsDetails.size()) {
							count = 0;
						}
					}
				});
			}
		};
		t.schedule(scanTask, 0, 5000);
	}

	protected void getBottumBarDetails(String appId2) {
		try {
			// new
			// MyBDownloadTask().execute("http://unikove.com/projects/snapliondemo/service_feature.php?AppId="+appId2);
			// new
			// MyBDownloadTask().execute("http://snaplion.com/service_feature.php?AppId="+appId2);
			new MyBDownloadTask().execute(appManager.APP_TABS_DETAILS_URL);
			Utility.debug("APP_TABS_DETAILS_URL : "
					+ appManager.APP_TABS_DETAILS_URL);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private class MyBDownloadTask extends
			AsyncTask<String, Integer, InputStream> {

		@Override
		protected void onPostExecute(InputStream result) {
			SharedPreferences myPref1 = getSharedPreferences("SLPref"
					+ appManager.getAPP_ID(), MODE_PRIVATE);
			String resStr = myPref1.getString("BottomTabData", "");
			try {
				showBBar(resStr);
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
			super.onPostExecute(result);
		}

		@Override
		protected InputStream doInBackground(String... params) {
			InputStream in = null;
			try {
				in = HttpConnection.connect(params[0]);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				SharedPreferences settings = getSharedPreferences("SLPref"
						+ appManager.getAPP_ID(), MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("BottomTabData", sb.toString());
				editor.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	// Basic details here
	private void getAppBasicDetails(String appId2) {
		if (HOME_IMAGE == 1) {
			try {
				if (Utility.isOnline(getApplicationContext()))
					new MyDownloadTask().execute(appManager.HOME_DETAILS_URL);
				Utility.debug("HOME_DETAILS_URL : "
						+ appManager.HOME_DETAILS_URL);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private class MyDownloadTask extends
			AsyncTask<String, Integer, InputStream> {
		@Override
		protected void onPostExecute(InputStream result) {
			try {
				SharedPreferences myPref1 = getSharedPreferences("SLPref"
						+ appManager.getAPP_ID(), MODE_PRIVATE);
				String resStr = myPref1.getString(
						"AppBasicData" + appManager.getAPP_ID(), "");
				try {
					InputStream in = new ByteArrayInputStream(
							resStr.getBytes("UTF-8"));
					try {
						if (in != null) {
							try {
								bgImges.clear();
							} catch (Exception e) {
								e.printStackTrace();
							}
							getAppBasicDetails(in);
							if (news_count == 1)
								newsData();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (HOME_IMAGE == 1)
				downloadBgImages();
			super.onPostExecute(result);
		}

		@Override
		protected InputStream doInBackground(String... params) {
			InputStream in = null;
			try {
				in = HttpConnection.connect(params[0]);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				SharedPreferences settings = getSharedPreferences("SLPref"
						+ appManager.getAPP_ID(), MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("AppBasicData" + appManager.getAPP_ID(),
						sb.toString());
				editor.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public void getAppBasicDetails(InputStream in) {
		final int music = 1;
		final int duration = 2;
		final int bgrecord = 3;
		final int bghomeid = 4;
		final int bgpicture = 5;
		final int newsrecord = 6;
		final int newsid = 7;
		final int newstext = 8;
		final int name = 9;

		int tagName = 0;

		BGImages value = null;
		NewsDetails value1 = null;

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, HTTP.UTF_8);

			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("music")) {
						tagName = music;
					} else if (parser.getName().equals("duration")) {
						tagName = duration;
					} else if (parser.getName().equals("name")) {
						tagName = name;
					} else if (parser.getName().equals("bgrecord")) {
						value = new BGImages();
						tagName = bgrecord;
					} else if (parser.getName().equals("bghomeid")) {
						tagName = bghomeid;
					} else if (parser.getName().equals("bgpicture")) {
						tagName = bgpicture;
					} else if (parser.getName().equals("newsrecord")) {
						value1 = new NewsDetails();
						tagName = newsrecord;
					} else if (parser.getName().equals("newsid")) {
						tagName = newsid;
					} else if (parser.getName().equals("newstext")) {
						tagName = newstext;
					}
				}
				if (eventType == XmlPullParser.TEXT) {
					switch (tagName) {
					case music:
						try {
							audioDetails.put("music", parser.getText());
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case duration:
						audioDetails.put("duration", parser.getText());
						break;
					case name:
						audioDetails.put("name", parser.getText());
						break;
					case bgrecord:
						break;
					case bghomeid:
						value.setBghomeid(parser.getText());
						break;
					case bgpicture:
						value.setBgpicture(parser.getText());
						break;
					case newsrecord:
						break;
					case newsid:
						value1.setNewsid(parser.getText());
						break;
					case newstext:
						value1.setNewstext(parser.getText());
						break;
					default:
						break;
					}
					tagName = 0;
				}

				if (eventType == XmlPullParser.END_TAG) {
					if (parser.getName().equals("bgrecord")) {
						bgImges.add(value);
						value = null;
					}/*
					 * else if(parser.getName().equals("newsrecord")){
					 * newsDetails.add(value1); value1 = null; }
					 */
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void downloadBgImages() {
		new MyDownloadBGTask().execute();
	}

	private class MyDownloadBGTask extends AsyncTask<URL, Integer, Bitmap> {

		@Override
		protected void onPostExecute(Bitmap result) {
			HOME_IMAGE = 2;
			if (Flag == 0) {
				// bgGallery.setAdapter(adapter);
				// adapter.notifyDataSetChanged();
				try {
					dismissDialog(appManager.PROGRESS);
				} catch (Exception e) {
					e.printStackTrace();
				}
				SharedPreferences settings = getSharedPreferences("MailPref"
						+ appManager.getAPP_ID(), MODE_PRIVATE);
				if (!settings.getBoolean("MailContact", false)) {
					handler.sendEmptyMessage(DIALOG_ALERT);
					Editor edit = settings.edit();
					edit.putBoolean("MailContact", false);
					edit.commit();
				}
			} else {
				// bgGallery.setAdapter(adapter);
				// adapter.notifyDataSetChanged();
			}
			super.onPostExecute(result);
		}

		@Override
		protected Bitmap doInBackground(URL... params) {
			URL url = null;
			for (int i = 0; i < bgImges.size(); i++) {
				try {
					url = new URL(Utility.replace(
							bgImges.get(i).getBgpicture(), " ", "%20"));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				try {
					URLConnection connection = url.openConnection();
					connection.connect();
					InputStream is = connection.getInputStream();
					// BufferedInputStream bis = new BufferedInputStream(is);
					try {
						String path = null;
						path = "HomeBG" + appManager.getAPP_ID()
								+ bgImges.get(i).getBghomeid();
						BGImages ad = new BGImages();
						ad = bgImges.get(i);
						ad.setStringPath(path);
						// ad.setBitmap(Utility.FetchImage(path));
						bgImges.set(i, ad);
						Utility.SaveImageSDCard(is, path);
						is = null;
						ad = null;
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		ArrayList<String> galUrlList;

		public ImageAdapter(Context c, ArrayList<String> galUrlList) {
			this.galUrlList = galUrlList;
			this.mContext = c;
		}

		public int getCount() {
			return galUrlList.size();
		}

		public String getItem(int position) {
			return galUrlList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);

			AQuery aq = new AQuery(mContext);
			Bitmap bitmap = aq.getCachedImage(getItem(position));
			if (bitmap != null) {
				aq.id(i).image(bitmap);
			} else {
				aq.id(i).image(getItem(position), false, true);
			}
			return i;
		}
	}

	// Splash work here

	private void getSpalshScreen(String appId) {
		try {
			new MyDownloadTask2().execute(appManager.SPLASH_DETAILS_URL);
			Utility.debug("SPLASH_DETAILS_URL : "
					+ appManager.SPLASH_DETAILS_URL);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private class MyDownloadTask2 extends
			AsyncTask<String, Integer, InputStream> {
		@Override
		protected void onPostExecute(InputStream result) {
			// try
			// {
			// TelephonyManager tManager =
			// (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			// LocationManager lm =
			// (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			// Location location =
			// lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			// SharedPreferences settings = getSharedPreferences("PushPref",
			// MODE_PRIVATE);
			// if(Utility.isOnline(getApplicationContext()))
			// {
			// // String str
			// =appManager.SERVICE_URL+"service_notification_andr.php?AppId="+appId+"&device="+tManager.getDeviceId()+"&tid="+settings.getString("RegID",
			// "");
			// String str = null;
			// try
			// {
			// if(location != null)
			// str
			// =appManager.SERVICE_URL+"home/service_notification_andr_lat_long.php?AppId="+appId+"&tid="+settings.getString("RegID",
			// "")+"&device="+tManager.getDeviceId()+"&lat="+location.getLatitude()+""+"&long="+location.getLongitude()+"";
			// else
			// str
			// =appManager.SERVICE_URL+"home/service_notification_andr_lat_long.php?AppId="+appId+"&tid="+settings.getString("RegID",
			// "")+"&device="+tManager.getDeviceId()+"&lat="+"0.0"+"&long="+"0.0";
			// System.out.println("str : "+str);
			// str = Utility.replace(str," ", "%20");
			// Log.v("Lat/Lon", str);
			// }
			// catch (Exception e)
			// {
			// e.printStackTrace();
			// }
			// HttpConnection.connect(str);
			// }
			// }
			// catch (Exception e)
			// {
			// e.printStackTrace();
			// }
			super.onPostExecute(result);
		}

		@Override
		protected InputStream doInBackground(String... params) {
			InputStream in = null;
			// Bitmap bitmap = null;

			try {
				in = HttpConnection.connect(params[0]);
				String splashUrl = getSplashUrl(in);
				try {
					URL url = new URL(Utility.replace(splashUrl, " ", "%20"));
					URLConnection connection = url.openConnection();
					connection.connect();
					InputStream is = connection.getInputStream();
					Utility.SaveImageSDCard(is,
							"SplashBG" + appManager.getAPP_ID());
					is = null;
					SharedPreferences settings = getSharedPreferences("SLPref"
							+ appManager.getAPP_ID(), MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("SplashData", "splash");
					editor.commit();
				} catch (Exception e) {
					return null;
				}
				/*
				 * if(bitmap != null){ ByteArrayOutputStream bos = new
				 * ByteArrayOutputStream(); bitmap.compress(CompressFormat.PNG,
				 * 0, bos); byte[] bitmapdata = bos.toByteArray(); String Image
				 * = null; try{ Image =
				 * com.snaplion.util.Base64.encodeBytes(bitmapdata); }catch
				 * (OutOfMemoryError e) {e.printStackTrace();} SharedPreferences
				 * settings = getSharedPreferences("SLPref"+appId,
				 * MODE_PRIVATE); SharedPreferences.Editor editor =
				 * settings.edit(); editor.putString("SplashData",Image);
				 * editor.commit(); bitmapdata = null; bitmap = null; }
				 */
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	public String getSplashUrl(InputStream in) {
		final int image = 1;
		int tagName = 0;

		String value = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, HTTP.UTF_8);

			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("image")) {
						tagName = image;
					}
				}
				if (eventType == XmlPullParser.TEXT) {
					switch (tagName) {
					case image:
						value = parser.getText();
						break;
					default:
						break;
					}
					tagName = 0;
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

	protected void getMusicPlayer() {
		new MusicPlayClass().execute();
	}

	private class MusicPlayClass extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			if (Utility.isOnline(getApplicationContext())) {
				com.snaplion.music.SLMusicPlayerActivity.getPlayer(
						SnapLionMyAppActivity.this, getApplicationContext(),
						playerLayout, appManager.getAPP_ID());
				homeMusic = true;
			}
			return null;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			try {
				SLMusicPlayerActivity.closePlayer(getApplicationContext());
			} catch (Exception e) {
				e.printStackTrace();
			}
			com.snaplion.myapp.SnapLionMyAppActivity.homeMusic = true;
			SnapLionMyAppActivity.KillFlag = false;
			HOME_IMAGE = 1;
			finish();
			// moveTaskToBack(true);
			return true;
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case AppManager.PROGRESS:
			final ProgressDialog progDialog = new ProgressDialog(this);
			progDialog.setMessage("Loading, Please wait...");
			progDialog.setCanceledOnTouchOutside(false);
			progDialog.setCancelable(true);
			return progDialog;
		case AppManager.DIALOG_DISPLAY:
			SnapLionMyAppActivity.KillFlag = false;
			return new AlertDialog.Builder(SnapLionMyAppActivity.this)
					.setTitle("Mailing List")
					.setMessage("Do you want to subscribe to the mailing list?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									SnapLionMyAppActivity.KillFlag = false;
									Intent i1009 = new Intent(
											getApplicationContext(),
											SLMailActivity.class);
									i1009.putExtra("MailName", "Mailing List");
									i1009.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(i1009);
									// SharedPreferences settings =
									// getSharedPreferences("MLFlag"+appManager.getAPP_ID(),
									// MODE_PRIVATE);
									// SharedPreferences.Editor editor =
									// settings.edit();
									// editor.putBoolean("First", true);
									// editor.commit();
								}
							})
					.setNegativeButton("Later",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									SnapLionMyAppActivity.KillFlag = true;
									dialog.dismiss();
									// SharedPreferences settings =
									// getSharedPreferences("MLFlag"+appManager.getAPP_ID(),
									// MODE_PRIVATE);
									// SharedPreferences.Editor editor =
									// settings.edit();
									// editor.putBoolean("First", false);
									// editor.commit();
								}
							}).create();
		case DIALOG_DISPLAY:
			return new AlertDialog.Builder(SnapLionMyAppActivity.this)
					.setTitle("App Features")
					.setMessage(
							getResources().getString(
									R.string.msg_server_problem))
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									SnapLionMyAppActivity.this.finish();
									dialog.dismiss();
								}
							})
					// return new
					// AlertDialog.Builder(SnapLionMyAppActivity.this)
					// .setTitle("App Features" )
					// .setMessage("You need to have content for atleast 3 tabs."
					// )
					// .setPositiveButton("OK", new
					// DialogInterface.OnClickListener() {
					// public void onClick(DialogInterface dialog, int which) {
					// SnapLionMyAppActivity.this.finish();
					// dialog.dismiss();
					// }
					// })
					.create();
		}
		return null;
	}

	@Override
	protected void onPause() {
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		boolean isScreenOn = powerManager.isScreenOn();
		if (!isScreenOn) {
			SnapLionMyAppActivity.KillFlag = false;
		}
		if (SnapLionMyAppActivity.KillFlag) {
			try {
				Utility.killMyApp(getApplicationContext(),
						SnapLionMyAppActivity.this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGaTracker.sendView("Home_Screen");
	}
}
