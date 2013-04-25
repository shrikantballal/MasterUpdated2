package com.snaplion.kingsxi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
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
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.google.android.gcm.GCMRegistrar;
import com.snaplion.beans.Album;
import com.snaplion.beans.AlbumSub;
import com.snaplion.beans.BG;
import com.snaplion.beans.BGImages;
import com.snaplion.beans.Module;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.photos.SLPhotoAlbumActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class SLMyAppSplashActivity extends AppSuperActivity {
	private ImageView bgImage = null;
	protected boolean _active = true;
	protected int _splashTime = 2000;
	// ArrayList<Module> updateModule = null;
	private ArrayList<BGImages> bgImges = null;
	private ArrayList<BG> bgDetails = null;
	private ArrayList<Album> albumDetails = null;
	private ArrayList<AlbumSub> albumSubDetails = null;
	private boolean updateFlag = false;
	private static boolean photoFlag = true;
	private boolean isSDCardContentsDeletedFlag = false;
	AppManager appManager;

	AsyncTask<Void, Void, Void> mRegisterTask;// tausif [GCM]

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_app);
		appManager = AppManager.getInstance();
		appManager.loadFont(this);
		appManager.loadfeatureListProperty(this);

		// variable initialization
		SharedPreferences settings = getSharedPreferences("SLUpdate"
				+ appManager.getAPP_ID(), MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("isAppJustStarted", true);
		editor.putBoolean("isPauseMusic", true);
		editor.putBoolean("checkAppVersion", true);
		editor.commit();

		// downloading for preview app
		Intent startingIntent = getIntent();
		if (appManager.PREVIEWAPP_FLAG
				&& startingIntent.getStringExtra("appid") != null
				&& startingIntent.getStringExtra("appname") != null) {
			appManager.setAPP_ID(startingIntent.getStringExtra("appid"));
			appManager.setAPP_NAME(startingIntent.getStringExtra("appname"));
			new DownloadPreviewSplash().execute();
			Utility.debug("appManager.getAPP_ID() :" + appManager.getAPP_ID());
		}

		if (!appManager.PREVIEWAPP_FLAG) {
			// appManager.setAppId(appManager.getAPP_ID());
			// appManager.setAppName(appManager.APP_NAME);
			appManager.loadURL();
		}

		// settings app folder structure
		if (!new File(Environment.getExternalStorageDirectory() + "/.SnapLion")
				.exists()) {
			// download all contents
			Utility.debug("Contents on SDCard is deleted, Downloading all data.");
			isSDCardContentsDeletedFlag = true;
		} else {
			Utility.debug("Contents on SDCard is exist.");
		}
		Utility.FileCreate();
		File data_directory = new File(appManager.DATA_DIR_BG);
		if (!data_directory.exists()) {
			if (data_directory.mkdir())
				;
		}

		// comparing time stamp
		if (Utility.isOnline(getApplicationContext())) 
		{
			new UpdateDataAsync().execute(appManager.MODIFIED_TAB_DETAILS_URL);
			Utility.debug("MODIFIED_TAB_DETAILS_URL : "
					+ appManager.MODIFIED_TAB_DETAILS_URL);
		} 
		else 
		{
			((ProgressBar) findViewById(R.id.progressBar2)).setVisibility(View.GONE);
			showDialog(appManager.DIALOG_DISPLAY);
		}

		//splash image setting
		bgImage = (ImageView)findViewById(R.id.app_splash_bg);
		if(appManager.PREVIEWAPP_FLAG)
		{
			 String splashPath=appManager.DATA_DIRECTORY+"SplashBG"+appManager.getAPP_ID()+".PNG";
     	 	 System.out.println("splashPath : "+splashPath);
     	 	File splashFile = new File(splashPath);
     	 	 if(splashFile.exists())
     	 	 {
     	 		splashFile.delete();
     	 	 }
     	 	downloadSplashScreen(); 
     	 	Bitmap icon = Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("SplashBG"+appManager.getAPP_ID()));
     	 	bgImage.setImageBitmap(icon);
			 ((ImageView)findViewById(R.id.poweredByLogo)).setVisibility(ImageView.VISIBLE);
		}
		else
		{
     	 	 bgImage.setImageResource(R.drawable.splash);
			 bgImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
     	 	((ImageView)findViewById(R.id.poweredByLogo)).setVisibility(ImageView.INVISIBLE);
		}
		
		// if not preview then register for notification
		if (!appManager.PREVIEWAPP_FLAG) 
		{
			// tausif [GCM]
			GCMRegistrar.checkDevice(this);
			GCMRegistrar.checkManifest(this);
			final String regId = GCMRegistrar.getRegistrationId(this);
			if (regId.equals("")) 
			{
				GCMRegistrar.register(this, appManager.GCM_SENDER_ID);
			}
			else 
			{
				if (!GCMRegistrar.isRegisteredOnServer(this)) 
				{
					final Context context = this;
					mRegisterTask = new AsyncTask<Void, Void, Void>() 
					{
						@Override
						protected Void doInBackground(Void... params) 
						{
							boolean registered = GCMServerUtilities.register(context, regId);
							if (!registered) 
							{
								GCMRegistrar.unregister(context);
							}
							return null;
						}

						@Override
						protected void onPostExecute(Void result) 
						{
							mRegisterTask = null;
						}

					};
					mRegisterTask.execute(null, null, null);
				}
			}
			// tausif [GCM] end
		}
		// /////////////////////download settings data///////////////////////
		new AppSettingsAsyncTask().execute();
	}

	class AppSettingsAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				String urlStr = appManager.SERVICE_URL
						+ "homeapplists/settings?AppId="
						+ appManager.getAPP_ID();
				Utility.debug("AppSettings URL : " + urlStr);
				URL url = new URL(urlStr);

				URLConnection connection = url.openConnection();
				connection.connect();
				InputStream is = connection.getInputStream();
				String result = Utility.getString(is);
				Utility.debug("AppSettings URL Result: " + result);

				JSONObject jsonObject = new JSONObject(result);
				String isDisplayAdds = jsonObject.getString("display_ads");
				String inMobiId = jsonObject.getString("inmobiId");
				String gAnalyticsId = jsonObject.getString("analytics_id");

				SharedPreferences settings = getSharedPreferences("SLUpdate"
						+ appManager.getAPP_ID(), MODE_PRIVATE);
				Editor editor = settings.edit();
				editor.putString("GANALYTICS_KEY", gAnalyticsId);
				appManager.GoogleAnalyticsID = gAnalyticsId;
				if (isDisplayAdds != null
						&& isDisplayAdds.equalsIgnoreCase("1")) {
					editor.putBoolean("IS_SHOW_ADDS", true);
					editor.putString("INMOBI_KEY", inMobiId);
				} else {
					editor.putBoolean("IS_SHOW_ADDS", false);
				}
				editor.commit();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}

	class DownloadPreviewSplash extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				if (appManager.PREVIEWAPP_FLAG) {
					if (Utility.isOnline(getApplicationContext())) {
						InputStream in = HttpConnection
								.connect(appManager.SPLASH_DETAILS_URL);
						Utility.debug("SPLASH_DETAILS_URL : "
								+ appManager.SPLASH_DETAILS_URL);
						if (in != null) {
							return getSplashUrl(in);
						}
					}
				} else {
					return "AUDIENCE";
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String splashUrl) {
			super.onPostExecute(splashUrl);
			if (splashUrl != null) {
				AQuery aq = new AQuery(SLMyAppSplashActivity.this);
				if (splashUrl.equalsIgnoreCase("AUDIENCE")) {
					aq.id(R.id.app_splash_bg).image(R.drawable.splash);
					aq.id(R.id.poweredByLogo).invisible();
				} else {
					aq.id(R.id.poweredByLogo).visible();
					Bitmap bitmap = aq.getCachedImage(splashUrl);
					if (bitmap != null) {
						aq.id(R.id.app_splash_bg).image(bitmap,
								AQuery.RATIO_PRESERVE);
					} else {
						aq.id(R.id.app_splash_bg).image(splashUrl, false, true);
					}
				}
			}
		}
	}

	public void downloadSplashScreen() {
		try {
			if (Utility.isOnline(getApplicationContext())) {
				InputStream in = HttpConnection
						.connect(appManager.SPLASH_DETAILS_URL);
				Utility.debug("SPLASH_DETAILS_URL : "
						+ appManager.SPLASH_DETAILS_URL);
				String splashUrl = null;
				if (in != null) {
					splashUrl = getSplashUrl(in);
				}
				try {
					URL url = new URL(Utility.replace(splashUrl, " ", "%20"));
					URLConnection connection = url.openConnection();
					connection.connect();
					InputStream is = connection.getInputStream();
					if (is != null) {
						Display display = getWindowManager()
								.getDefaultDisplay();
						float width = display.getWidth();
						float xx = width / 640;
						int yy = (int) (960 * xx);
						Utility.getResizedImage(is,
								"Bg/SplashBG" + appManager.getAPP_ID(), yy,
								display.getWidth());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class UpdateDataAsync extends AsyncTask<String, Integer, InputStream> 
	{
		@Override
		protected InputStream doInBackground(String... params) 
		{
			try {
				if (Utility.isOnline(getApplicationContext())) 
				{
					InputStream in = null;
					String str = null;
					try 
					{
						in = HttpConnection.connect(params[0]);
						if (in != null) 
						{
							str = Utility.getString(in);
						}
						if (str != null) 
						{
							in = null;
							ArrayList<Module> updateModule = getUpdateDetails(str);
							// comapring time stamp
							compareUpdateModule(updateModule);
							str = null;
							if (isSDCardContentsDeletedFlag) 
							{
								saveTimeStamp(updateModule);
							}
							
							SharedPreferences settings = getSharedPreferences("SLUpdate"+ appManager.getAPP_ID(), MODE_PRIVATE);
							if (updateModule != null) 
							{
								for (int i = 0; i < updateModule.size(); i++) 
								{
									boolean isUpdateTab = settings.getBoolean(updateModule.get(i).getTabId(), false);
									Utility.debug("Checkout Content: "
											+ updateModule.get(i).getTabId() + "="
											+ updateModule.get(i).getTabCustomName() + "="
											+ isUpdateTab);
									if (isUpdateTab) 
									{
										int id = Integer.parseInt(updateModule.get(i).getTabId());
										switch (id) 
										{
											case 1001: 
											{
												SharedPreferences.Editor editor = getSharedPreferences("SLPref"+appManager.getAPP_ID(), MODE_PRIVATE).edit();
												editor.putString("AppBasicData"+appManager.getAPP_ID(),"");
												editor.commit();
												break;
											}
											case 1002: 
											{
												SharedPreferences.Editor editor = getSharedPreferences("SLPref" + appManager.getAPP_ID(),MODE_PRIVATE).edit();
												editor.putString("BioData", "");
												editor.commit();
												break;
											}
											case 1003: 
											{
												SharedPreferences.Editor editor = getSharedPreferences("SLPref" + appManager.getAPP_ID(),MODE_PRIVATE).edit();
												editor.putString("AlbumData", "");
												editor.commit();
												break;
											}
										case 1004: 
										{
											if (Utility.isOnline(getApplicationContext())) 
											{
												getAlbumData(appManager.MUSIC_ALBUM_NAMES_URL);
											}
											Utility.debug("MUSIC_ALBUM_NAMES_URL : "+ appManager.MUSIC_ALBUM_NAMES_URL);
											break;
										}
										case 1005: 
										{
											break;
										}
										case 1006: 
										{
											SharedPreferences s1006 = getSharedPreferences("VidioPref" + appManager.getAPP_ID(),MODE_PRIVATE);
											SharedPreferences.Editor e1006 = s1006.edit();
											e1006.putString("youtubevideo", "");
											e1006.commit();
											break;
										}
										case 1007: 
										{
											SharedPreferences s1007 = getSharedPreferences("NewsPref" + appManager.getAPP_ID(),MODE_PRIVATE);
											SharedPreferences.Editor e1007 = s1007.edit();
											e1007.putString("NewsData", "");
											e1007.commit();
											File data_directory = new File(appManager.DATA_DIRECTORY);
											if (data_directory.exists()) 
											{
												String[] children = data_directory.list();
												for (int j = 0; j < children.length; j++) 
												{
													if (children[j].startsWith("News")
															&& !children[j].endsWith(appManager
																	.getAPP_ID()))
														new File(data_directory, children[j])
																.delete();
												}
											}
											if (Utility.isOnline(getApplicationContext())) 
											{
												Log.e("TAG", "Calling getNewsDetails()");
												getNewsDetails();
											}
											break;
										}
										case 1008: 
										{
											SharedPreferences s1008 = getSharedPreferences("NewsPref" + appManager.getAPP_ID(),
													MODE_PRIVATE);
											SharedPreferences.Editor e1008 = s1008.edit();
											e1008.putString("EventsData", "");
											e1008.commit();
											File dir = new File(appManager.DATA_DIRECTORY);
											if (dir.exists()) {
												String[] children = dir.list();
												for (int j = 0; j < children.length; j++) {
													if (children[j].startsWith("Events")
															&& !children[j].endsWith(appManager
																	.getAPP_ID())) {
														new File(dir, children[j]).delete();
													}
												}
											}
											break;
										}
										case 1009: {
											break;
										}
										case 1010: 
										{
//											SharedPreferences myPref = getSharedPreferences("FanwallRef" + appManager.getAPP_ID(),MODE_PRIVATE);
//											String fanid = myPref.getString("fanid", null);
//											String postUrl;
//											String photoUrl;
//											if (fanid != null) 
//											{
//												postUrl = appManager.FANWALL_POST_URL
//														+ "?user_id=" + fanid + "&appid="
//														+ appManager.getAPP_ID()
//														+ "&index_start=" + 0 + "&index_end="
//														+ 50;
//												photoUrl = appManager.FANWALL_PHOTO_URL
//														+ "?appid=" + appManager.getAPP_ID()
//														+ "&user_id=" + fanid;
//											} 
//											else 
//											{
//												postUrl = appManager.FANWALL_POST_URL
//														+ "?appid=" + appManager.getAPP_ID()
//														+ "&index_start=" + 0 + "&index_end="
//														+ 50;
//												photoUrl = appManager.FANWALL_PHOTO_URL
//														+ "?appid=" + appManager.getAPP_ID();
//											}
//											Utility.debug("Post caching : " + postUrl);
//											Utility.debug("Photo caching : " + photoUrl);
//											InputStream postIn = HttpConnection
//													.connect(postUrl);
//											InputStream photoIn = HttpConnection
//													.connect(photoUrl);
//											if (postIn != null && photoIn != null) {
//												String postResStr = Utility.getString(postIn);
//												String photoResStr = Utility.getString(photoIn);
//												Editor editor = myPref.edit();
//												editor.putString("ARTIST_POST_DATA", postResStr);
//												editor.putString("PHOTO_DATA", photoResStr);
//												editor.putBoolean("LOAD_CACHED_POST", true);
//												editor.commit();
//
//												SharedPreferences myPref1 = getSharedPreferences(
//														"SLUpdate" + appManager.getAPP_ID(),
//														MODE_PRIVATE);
//												String str1 = myPref1.getString("MTime" + "1010"+ appManager.getAPP_ID(), "");
//												SharedPreferences.Editor editor1 = myPref1.edit();
//												editor1.putBoolean("1010", false);
//												editor1.putString("1010" + appManager.getAPP_ID(), str1);
//												editor1.commit();
//											}
											break;
										}
										case 1011: 
										{
											SharedPreferences s1011 = getSharedPreferences("ContactPref" + appManager.getAPP_ID(),MODE_PRIVATE);
											SharedPreferences.Editor e1011 = s1011.edit();
											e1011.putString("Contact", "");
											e1011.commit();
											break;
										}
										case 1012: 
										{
											SharedPreferences s1012 = getSharedPreferences("CRPref" + appManager.getAPP_ID(),MODE_PRIVATE);
											SharedPreferences.Editor e1012 = s1012.edit();
											e1012.putString("CrData", "");
											e1012.commit();
											File dir2 = new File(appManager.DATA_DIRECTORY);
											if (dir2.exists()) 
											{
												String[] children1 = dir2.list();
												for (int j = 0; j < children1.length; j++) 
												{
													if (children1[j].equalsIgnoreCase("CRImage"+ appManager.getAPP_ID())) 
													{
														new File(dir2, children1[j]).delete();
														break;
													}
												}
											}
											break;
										}
										case 1013: 
										{
											break;
										}
										case 1014: {
											break;
										}
										case 1015: 
										{
											SharedPreferences.Editor editor = getSharedPreferences("SLBGImage"+appManager.getAPP_ID(), MODE_PRIVATE).edit();
											editor.putBoolean("DownloadBGImage", true);
											editor.commit();
											break;
										}
										case 1016: {
											// tausif
											// if(!appManager.PREVIEWAPP_FLAG)
											// {
											SharedPreferences s0 = getSharedPreferences(
													"SLPref" + appManager.getAPP_ID(),
													MODE_PRIVATE);
											SharedPreferences.Editor e0 = s0.edit();
											e0.putString("SplashData", "splash");
											e0.commit();

											SharedPreferences myPref = getSharedPreferences(
													"SLUpdate" + appManager.getAPP_ID(),
													MODE_PRIVATE);
											String str1 = myPref.getString("MTime" + "1016"
													+ appManager.getAPP_ID(), "");
											SharedPreferences.Editor ed0 = myPref.edit();
											ed0.putBoolean("1016", false);
											ed0.putString("1016" + appManager.getAPP_ID(), str1);
											ed0.commit();
											break;
										}
										case 1017: 
										{
//											try {
//												String url = appManager.MULTIBIO_URL
//														+ "?AppId=" + appManager.getAPP_ID();
//												Utility.debug("cast crew url :" + url);
//												InputStream in1 = HttpConnection.connect(url);
//												String resStr = null;
//												if (in1 != null) {
//													resStr = Utility.getString(in1);
//												}
//												SharedPreferences pref = getSharedPreferences(
//														"Multibio" + appManager.getAPP_ID(),
//														MODE_PRIVATE);
//												SharedPreferences.Editor e0 = pref.edit();
//												e0.putString("MULTIBIO_DATA", resStr);
//												e0.commit();
//											} catch (Exception e) {
//												e.printStackTrace();
//											}
											break;
										}
										case 1018: {
											if (Utility.isOnline(getApplicationContext())) {
												getMenussDetails();
											}
											break;
										}
										case 1019: {
											if (Utility.isOnline(getApplicationContext())) {
												getPromoDetails();
											}
											break;
										}
										}
									}
								}
								// if(Utility.isOnline(getApplicationContext()))
								// registerdDivice();
							}
						}

						// download bottom tab details
						getModuleDetails();
						
						
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}

			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}

			
			return null;
		}

		private void getPromoDetails() {
			String str = null;
			File data_directory = new File(appManager.DATA_TEMP_BG);
			if (!data_directory.exists())
				if (data_directory.mkdir())
					;

			InputStream in = HttpConnection.connect(appManager.PROMO_URL);
			Utility.debug("PROMO_URL : " + appManager.PROMO_URL);
			if (in != null) {
				str = Utility.getString(in);
				SharedPreferences myPref1 = getSharedPreferences("PromoPref"
						+ appManager.getAPP_ID(), MODE_PRIVATE);
				Editor editor = myPref1.edit();
				editor.putString("PromoData", str);
				editor.commit();

				SharedPreferences myPref21 = getSharedPreferences("SLUpdate"
						+ appManager.getAPP_ID(), MODE_PRIVATE);
				String str21 = myPref21.getString(
						"MTime" + "1019" + appManager.getAPP_ID(), "");
				SharedPreferences.Editor editor211 = myPref21.edit();
				editor211.putBoolean("1019", false);
				editor211.putString("1019" + appManager.getAPP_ID(), str21);
				editor211.commit();
			}
		}

		private void getMenussDetails() {
			String str = null;
			File data_directory = new File(appManager.DATA_TEMP_BG);
			if (!data_directory.exists())
				if (data_directory.mkdir())
					;

			InputStream in = HttpConnection.connect(appManager.MENUS_URL);
			Utility.debug("MENUS_URL : " + appManager.MENUS_URL);
			if (in != null) {
				str = Utility.getString(in);
				SharedPreferences myPref1 = getSharedPreferences("MenusPref"
						+ appManager.getAPP_ID(), MODE_PRIVATE);
				Editor editor = myPref1.edit();
				editor.putString("MenusData", str);
				editor.commit();

				SharedPreferences myPref21 = getSharedPreferences("SLUpdate"
						+ appManager.getAPP_ID(), MODE_PRIVATE);
				String str21 = myPref21.getString(
						"MTime" + "1018" + appManager.getAPP_ID(), "");
				SharedPreferences.Editor editor211 = myPref21.edit();
				editor211.putBoolean("1018", false);
				editor211.putString("1018" + appManager.getAPP_ID(), str21);
				editor211.commit();
			}
		}

		@Override
		protected void onPostExecute(InputStream result) {
			super.onPostExecute(result);
			startScreen();
		}

	}

	private void saveTimeStamp(ArrayList<Module> updateModule2) {
		SharedPreferences settings = getSharedPreferences("SLUpdate"
				+ appManager.getAPP_ID(), MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		SharedPreferences myPref1 = getSharedPreferences("AlbumUpdate"
				+ appManager.getAPP_ID(), MODE_PRIVATE);
		SharedPreferences.Editor editor1 = myPref1.edit();
		for (int newId = 0; newId < 50; newId++) {
			editor1.putString("AlbumTime" + newId + appManager.getAPP_ID(), "");
		}
		editor1.commit();
		for (int i = 0; i < updateModule2.size(); i++) {
			editor.putBoolean(updateModule2.get(i).getTabId(), true);
			editor.putString("MTime" + updateModule2.get(i).getTabId()
					+ appManager.getAPP_ID(), updateModule2.get(i)
					.getModifiedDate());
		}
		editor.putBoolean("FirstLogin", true);
		editor.commit();
	}

	public void getAlbumData(String string) {
		boolean musicFlag = true;
		String str1 = null;
		String str = null;
		InputStream in1 = null;
		InputStream in = null;
		try {
			in1 = HttpConnection.connect(string);
			if (in1 != null) {
				str1 = Utility.getString(in1);
			} else {
				musicFlag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			musicFlag = false;
		}

		try {
			String url = appManager.MUSIC_BY_ALBUM_NAME_URL.replaceAll(
					"<<ALBUM_NAME>>", "All");
			Utility.debug("MUSIC_BY_ALBUM_NAME_URL : " + url);
			in = HttpConnection.connect(url);
			if (in != null) {
				str = Utility.getString(in);
				// Utility.debug("MUSIC_BY_ALBUM_NAME_URL result : "+str);
			} else {
				musicFlag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			musicFlag = false;
		}

		if (musicFlag) {
			SharedPreferences settings = getSharedPreferences("MusicPref"
					+ appManager.getAPP_ID(), MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("MusciAlbumData", str1);
			editor.commit();
			in1 = null;
			str1 = null;
			SharedPreferences s1005 = getSharedPreferences("MusicPref"
					+ appManager.getAPP_ID(), Context.MODE_PRIVATE);
			SharedPreferences.Editor e1005 = s1005.edit();
			e1005.putString("AlbumSong" + "All", str);
			e1005.commit();
			in = null;
			str = null;

			SharedPreferences myPref21 = getSharedPreferences("SLUpdate"
					+ appManager.getAPP_ID(), MODE_PRIVATE);
			String str21 = myPref21.getString(
					"MTime" + "1004" + appManager.getAPP_ID(), "");
			SharedPreferences.Editor editor211 = myPref21.edit();
			editor211.putBoolean("1004", false);
			editor211.putString("1004" + appManager.getAPP_ID(), str21);
			editor211.commit();
			SharedPreferences s1004 = getSharedPreferences("MusicFlag"
					+ appManager.getAPP_ID(), Context.MODE_PRIVATE);
			SharedPreferences.Editor e1004 = s1004.edit();
			e1004.putInt("PlayNo", 0);
			e1004.putBoolean("lastPlay", false);
			e1004.commit();
		} else {
			Utility.debug("1004 : Not downloaded properly :"
					+ this.getClass().getName());
		}
	}

	public void getModuleDetails() {
		try {
			InputStream in = HttpConnection
					.connect(appManager.APP_TABS_DETAILS_URL);
			Utility.debug("APP_TABS_DETAILS_URL : "
					+ appManager.APP_TABS_DETAILS_URL);
			String str = null;
			if (in != null) {
				str = Utility.getString(in);
				if (str != null) {
					SharedPreferences settings = getSharedPreferences("SLPref"
							+ appManager.getAPP_ID(), MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("BottomTabData", str);
					editor.commit();
					in = null;
					str = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void compareUpdateModule(ArrayList<Module> updateModule2) {
		SharedPreferences settings = getSharedPreferences("SLUpdate"
				+ appManager.getAPP_ID(), MODE_PRIVATE);
		if (!settings.getBoolean("FirstLogin", false)) {
			saveTimeStamp(updateModule2);
		} else {
			SharedPreferences myPref1 = getSharedPreferences("SLUpdate"
					+ appManager.getAPP_ID(), MODE_PRIVATE);
			SharedPreferences.Editor editor1 = myPref1.edit();
			editor1.putBoolean("FirstLogin", true);
			editor1.commit();

			for (int i = 0; i < updateModule2.size(); i++) {
				String newId = updateModule2.get(i).getTabId();
				String newTime = updateModule2.get(i).getModifiedDate();

				String oldTime = settings.getString("MTime" + newId
						+ appManager.getAPP_ID(), "");
				if (oldTime.equalsIgnoreCase(newTime)) {
					// SharedPreferences myPref =
					// getSharedPreferences("SLUpdate"+appManager.getAPP_ID(),
					// MODE_PRIVATE);
					// SharedPreferences.Editor editor = myPref.edit();
					// editor.putBoolean(updateModule2.get(i).getTabId(),
					// false);
					// editor.commit();
				} else {
					SharedPreferences myPref = getSharedPreferences("SLUpdate"
							+ appManager.getAPP_ID(), MODE_PRIVATE);
					SharedPreferences.Editor editor = myPref.edit();
					editor.putBoolean(updateModule2.get(i).getTabId(), true);
					editor.putString("MTime" + updateModule2.get(i).getTabId()
							+ appManager.getAPP_ID(), updateModule2.get(i)
							.getModifiedDate());
					editor.commit();
				}
			}
		}
	}
	private void copyTempFile(String dataTempBg, String dataDirectory) {
		File dir2 = new File(dataTempBg);
		File dst = new File(dataDirectory);
		try {
			Utility.copyDirectory(dir2, dst);
			Utility.delete(dir2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * //TAUSIF [location not used now to show audience location. no need of it
	 * now.] private void registerdDivice() { try{ TelephonyManager tManager =
	 * (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	 * LocationManager lm =
	 * (LocationManager)getSystemService(Context.LOCATION_SERVICE); Location
	 * location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	 * SharedPreferences settings = getSharedPreferences("PushPref",
	 * MODE_PRIVATE); if(Utility.isOnline(getApplicationContext())){ String str
	 * = null; try{ if(location != null) str
	 * =appManager.SERVICE_URL+"home/service_notification_andr_lat_long.php?AppId="
	 * +appManager.getAPP_ID()+"&tid="+settings.getString("RegID",
	 * "")+"&device="
	 * +tManager.getDeviceId()+"&lat="+location.getLatitude()+""+"&long="
	 * +location.getLongitude()+""; else str
	 * =appManager.SERVICE_URL+"home/service_notification_andr_lat_long.php?AppId="
	 * +appManager.getAPP_ID()+"&tid="+settings.getString("RegID",
	 * "")+"&device="+tManager.getDeviceId()+"&lat="+"0.0"+"&long="+"0.0"; str =
	 * Utility.replace(str," ", "%20"); Log.v("Lat/Lon", str); }catch (Exception
	 * e) {e.printStackTrace();} HttpConnection.connect(str); } }catch
	 * (Exception e) {e.printStackTrace();} }
	 */

	// Download background images

	public void getBgImages(int i2) {
		boolean bgFlag = true;
		File data_directory = new File(appManager.DATA_TEMP_BG);
		if (!data_directory.exists()) {
			if (data_directory.mkdir())
				;
		}
		InputStream in = HttpConnection
				.connect(appManager.SECTIONS_BGIMAGES_URL);
		Utility.debug("SECTIONS_BGIMAGES_URL : "
				+ appManager.SECTIONS_BGIMAGES_URL);
		bgDetails = new ArrayList<BG>();
		if (in != null) {
			bgDetails = getBGDetails(in);
			// for(int i=0;i<bgDetails.size();i++)
			// {
			int i = 0;
			URLConnection connection = null;
			String str = bgDetails.get(i).getTabs().trim();
			if (str.length() > 0) {
				Utility.debug("splash original str:" + str);
				if (str.startsWith(",")) {
					str = str.substring(1, str.length());
				}
				if (str.endsWith(",")) {
					str = str.substring(0, str.length() - 1);
				}
				str = str.replaceAll(",,", ",");
				Utility.debug("splash converted str:" + str);

				String[] strArray = str.split(",");
				String imgStr = null;
				for (int j = 0; j < strArray.length; j++) {
					if (j == 0) {
						URL url = null;
						try {
							url = new URL(Utility.replace(bgDetails.get(i)
									.getBgimage(), " ", "%20"));
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						try {
							connection = url.openConnection();
							connection.connect();
							InputStream is = connection.getInputStream();
							String path = getPath(strArray[j]);
							imgStr = path;
							if (is == null) {
								bgFlag = false;
							}
							if (path != null) {
								Utility.SaveImageGPathSDCard(
										appManager.DATA_TEMP_BG, is, path);
							}
							Utility.debug("splash downloaded bg img path:"
									+ path);
							path = null;
						} catch (Exception e) {
							e.printStackTrace();
							bgFlag = false;
						}
					} else {
						String path = getPath(strArray[j]);
						Bitmap bitmap = tempFetchImage(imgStr);
						if (bitmap != null) {
							java.io.OutputStream outStream = null;
							File file = new File(appManager.DATA_TEMP_BG, path
									+ ".PNG");
							try {
								outStream = new FileOutputStream(file);
								bitmap.compress(Bitmap.CompressFormat.PNG, 0,
										outStream);
								outStream.flush();
								outStream.close();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
								bgFlag = false;
							} catch (IOException e) {
								e.printStackTrace();
								bgFlag = false;
							}
							Utility.debug("splash downloaded bg img path:"
									+ path);
						}

						path = null;
						bitmap = null;
					}
				}
			}
			// }
		} else {
			bgFlag = false;
		}
		if (bgFlag) {
			File dir1 = new File(appManager.DATA_DIR_BG);
			if (dir1.exists()) {
				String[] children = dir1.list();
				for (int j = 0; j < children.length; j++) {
					if (children[j].endsWith(appManager.getAPP_ID() + ".PNG")) {
						new File(dir1, children[j]).delete();
					}
				}
			}
			copyTempFile(appManager.DATA_TEMP_BG, appManager.DATA_DIR_BG);
			SharedPreferences myPref = getSharedPreferences("SLUpdate"
					+ appManager.getAPP_ID(), MODE_PRIVATE);
			String str = myPref.getString(
					"MTime" + "1015" + appManager.getAPP_ID(), "");
			SharedPreferences.Editor editor = myPref.edit();
			editor.putBoolean("1015", false);
			editor.putString("1015" + appManager.getAPP_ID(), str);
			editor.commit();
		} else {
			Utility.debug("1015 : Not downloaded properly :"
					+ this.getClass().getName());
		}
	}

	private String getPath(String string) {
		String str = null;
		int id = 0;
		try {
			id = Integer.parseInt(string);
		} catch (Exception e) {
			e.printStackTrace();
		}
		switch (id) {
		case 1001:
			break;
		case 1002:
			str = "BioMainBg" + appManager.getAPP_ID();
			break;
		case 1003:
			str = "AlbumBG" + appManager.getAPP_ID();
			break;
		case 1004:
			str = "MusicAlbumBG" + appManager.getAPP_ID();
			break;
		case 1005:
			str = "AllBG" + appManager.getAPP_ID();
			break;
		case 1006:
			str = "VideoBG" + appManager.getAPP_ID();
			break;
		case 1007:
			str = "NewsBg" + appManager.getAPP_ID();
			break;
		case 1008:
			str = "EventsBg" + appManager.getAPP_ID();
			break;
		case 1009:
			str = "MailBG" + appManager.getAPP_ID();
			break;
		case 1010:
			str = "FanwallMainBg" + appManager.getAPP_ID();
			break;
		case 1011:
			str = "ContactBG" + appManager.getAPP_ID();
			break;
		case 1012:
			str = "BgCrImage" + appManager.getAPP_ID();
			break;
		case 1013:
			str = "TicketBg" + appManager.getAPP_ID();
			break;
		case 1014:
			str = "SL" + appManager.getAPP_ID();
			break;
		case 1015:
			break;
		case 1017:
			str = "Multibio" + appManager.getAPP_ID();
			break;
		case 1018:
			str = "MenusBG" + appManager.getAPP_ID();
			break;
		case 1019:
			str = "PromoBg" + appManager.getAPP_ID();
			break;
		case 0:
			break;
		}
		Utility.debug("str : " + str);
		return str;
	}

	private ArrayList<BG> getBGDetails(InputStream in) {
		final int bgrecord = 1;
		final int bghomeid = 2;
		final int tabs = 3;
		final int bgimage = 4;

		int tagName = 0;

		BG value = null;
		ArrayList<BG> values = new ArrayList<BG>();

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, HTTP.UTF_8);

			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("bgrecord")) {
						tagName = bgrecord;
						value = new BG();
					} else if (parser.getName().equals("bghomeid")) {
						tagName = bghomeid;
					} else if (parser.getName().equals("tabs")) {
						tagName = tabs;
					} else if (parser.getName().equals("bgimage")) {
						tagName = bgimage;
					}
				}
				if (eventType == XmlPullParser.TEXT) {
					switch (tagName) {
					case bgrecord:
						break;
					case bghomeid:
						value.setBghomeid(parser.getText());
						break;
					case tabs:
						value.setTabs(parser.getText());
						break;
					case bgimage:
						value.setBgimage(parser.getText());
						break;
					default:
						break;
					}
					tagName = 0;
				}

				if (eventType == XmlPullParser.END_TAG) {
					if (parser.getName().equals("bgrecord")) {
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

	public ArrayList<BGImages> getAppBasicDetails(String str) {
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(str.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

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
		ArrayList<BGImages> value1 = new ArrayList<BGImages>();

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
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case duration:
						break;
					case name:
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
						break;
					case newstext:
						break;
					default:
						break;
					}
					tagName = 0;
				}

				if (eventType == XmlPullParser.END_TAG) {
					if (parser.getName().equals("bgrecord")) {
						value1.add(value);
						value = null;
						return value1;// tausif
					} else if (parser.getName().equals("newsrecord")) {
					}
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return value1;
	}

	public String getSplashUrl(InputStream in) {
		final int image = 1;
		int tagName = 0;

		String value = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			try {
				parser.setInput(in, HTTP.UTF_8);
			} catch (Exception e) {
				e.printStackTrace();
			}

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

	// Update Parser
	public ArrayList<Module> getUpdateDetails(String resStr) {
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(resStr.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		final int result = 1;
		final int updatedtab = 2;
		final int TabId = 3;
		final int TabCustomName = 4;
		final int ModifiedDate = 5;

		int tagName = 0;

		Module value = null;
		ArrayList<Module> values = null;

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, HTTP.UTF_8);

			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("result")) {
						tagName = result;
						values = new ArrayList<Module>();
					} else if (parser.getName().equals("updatedtab")) {
						tagName = updatedtab;
						value = new Module();
					} else if (parser.getName().equals("TabId")) {
						tagName = TabId;
					} else if (parser.getName().equals("TabCustomName")) {
						tagName = TabCustomName;
					} else if (parser.getName().equals("ModifiedDate")) {
						tagName = ModifiedDate;
					}
				}
				if (eventType == XmlPullParser.TEXT) {
					switch (tagName) {
					case result:
						break;
					case updatedtab:
						break;
					case TabId:
						value.setTabId(parser.getText());
						break;
					case TabCustomName:
						value.setTabCustomName(parser.getText());
						break;
					case ModifiedDate:
						value.setModifiedDate(parser.getText());
						break;
					default:
						break;
					}
					tagName = 0;
				}

				if (eventType == XmlPullParser.END_TAG) {
					if (parser.getName().equals("updatedtab")) {
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

	private void startScreen() {
		SharedPreferences settings = getSharedPreferences(
				"SLPref" + appManager.getAPP_ID(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("BRemData", "1001");
		editor.commit();
		SnapLionMyAppActivity.KillFlag = false;
		Intent i = new Intent(getApplicationContext(),
				SnapLionMyAppActivity.class);
		i.putExtra("Count", 2);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);

		// Intent i = new Intent(getApplicationContext(),
		// SLMoreActivtity.class);
		// i.putExtra("Count", 2);
		// i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(i);

		finish();
	}

	// photo details

	

	private void downLoadAlbumImage(ArrayList<Album> albumDetails2, String str2) {
		String str1 = null;

		for (int i = 0; i < albumDetails2.size(); i++) {
			URL url = null;
			try {
				String str = albumDetails2.get(i).getPicture();
				str = Utility.replace(str, " ", "%20");
				url = new URL(str);
			} catch (Exception e1) {
				photoFlag = false;
				e1.printStackTrace();
			}
			try {
				if (Utility.isOnline(getApplicationContext())) {
					URLConnection connection = url.openConnection();
					connection.connect();
					InputStream is = connection.getInputStream();
					if (is != null) {
						String path = "AlbumPref" + appManager.getAPP_ID()
								+ albumDetails2.get(i).getCategaryid();
						Utility.SaveImageGPathSDCard(appManager.DATA_TEMP_BG,
								is, path);
						is = null;
					} else {
						photoFlag = false;
					}
				} else {
					photoFlag = false;
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				photoFlag = false;
			} catch (IOException e) {
				e.printStackTrace();
				photoFlag = false;
			}
		}

		try {
			String photolistByCategoryUrl = appManager.PHOTOLIST_BY_CATEGORYID_URL;
			photolistByCategoryUrl = photolistByCategoryUrl.replaceAll(
					"<<CATEGORY_ID>>", albumDetails2.get(0).getCategaryid());
			Utility.debug("PHOTOLIST_BY_CATEGORYID_URL : "
					+ photolistByCategoryUrl);
			InputStream in = HttpConnection.connect(photolistByCategoryUrl);
			// InputStream in =
			// HttpConnection.connect(appManager.SERVICE_URL+"service_photolist.php?AppId="+appManager.getAPP_ID()+"&CategaryId="+albumDetails2.get(0).getCategaryid());
			if (in != null) {
				str1 = Utility.getString(in);
				in = null;
				if (str1 != "") {
					try {
						if (albumSubDetails != null)
							albumSubDetails.clear();
					} catch (Exception e) {
						e.printStackTrace();
					}
					albumSubDetails = new ArrayList<AlbumSub>();
					albumSubDetails = SLPhotoAlbumActivity
							.getAlbumSubDetails(str1);
				} else {
					photoFlag = false;
				}
				for (int i = 0; i < albumSubDetails.size(); i++) {
					URL url = null;
					try {
						String str = albumSubDetails.get(i).getSmallpicture();
						str = Utility.replace(str, " ", "%20");
						url = new URL(str);
					} catch (Exception e1) {
						e1.printStackTrace();
						photoFlag = false;
					}
					try {
						if (Utility.isOnline(getApplicationContext())) {
							URLConnection connection = url.openConnection();
							connection.connect();
							InputStream is = connection.getInputStream();
							String key = "small"
									+ albumDetails.get(0).getCategaryid()
									+ albumSubDetails.get(i).getPhotoid()
									+ albumSubDetails.get(0).getAlbumid()
									+ albumSubDetails.get(0).getAlbumname();
							Utility.SaveImageGPathSDCard(
									appManager.DATA_TEMP_BG, is, key);
							is = null;
							key = null;
						} else {
							photoFlag = false;
						}
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
						photoFlag = false;
					} catch (IOException e) {
						e.printStackTrace();
						photoFlag = false;
					}
				}
			} else {
				photoFlag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			photoFlag = false;
		}

		if (photoFlag) {
			copyTempFile(appManager.DATA_TEMP_BG, appManager.DATA_DIRECTORY);

			SharedPreferences settings = getSharedPreferences("AlbumPref"
					+ appManager.getAPP_ID(), MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("AlbumData", str2);
			editor.putString("AlbumSubData"
					+ albumDetails.get(0).getCategaryid(), str1);
			editor.commit();

			SharedPreferences myPref = getSharedPreferences("AlbumUpdate"
					+ appManager.getAPP_ID(), MODE_PRIVATE);
			for (int i = 0; i < albumDetails.size(); i++) {
				String newId = albumDetails.get(i).getCategaryid();
				String newTime = albumDetails.get(i).getModifiedDate();
				String oldTime = myPref.getString("AlbumTime" + newId
						+ appManager.getAPP_ID(), "");
				if (oldTime.equalsIgnoreCase(newTime)) {
					SharedPreferences myPref1 = getSharedPreferences(
							"AlbumUpdate" + appManager.getAPP_ID(),
							MODE_PRIVATE);
					SharedPreferences.Editor editor1 = myPref1.edit();
					editor1.putBoolean(
							"Album" + albumDetails.get(i).getCategaryid()
									+ appManager.getAPP_ID(), false);
					editor1.putBoolean(
							"AlbumBig" + albumDetails.get(i).getCategaryid()
									+ appManager.getAPP_ID(), false);
					editor1.putString(
							"MAlbum" + albumDetails.get(i).getCategaryid()
									+ appManager.getAPP_ID(),
							albumDetails.get(i).getModifiedDate());
					editor1.commit();
				} else {
					SharedPreferences myPref2 = getSharedPreferences(
							"AlbumUpdate" + appManager.getAPP_ID(),
							MODE_PRIVATE);
					SharedPreferences.Editor editor2 = myPref2.edit();
					editor2.putBoolean(
							"Album" + albumDetails.get(i).getCategaryid()
									+ appManager.getAPP_ID(), true);
					editor2.putBoolean(
							"AlbumBig" + albumDetails.get(i).getCategaryid()
									+ appManager.getAPP_ID(), true);
					editor2.putString(
							"MAlbum" + albumDetails.get(i).getCategaryid()
									+ appManager.getAPP_ID(),
							albumDetails.get(i).getModifiedDate());

					editor2.putString(
							"AlbumTime" + newId + appManager.getAPP_ID(),
							albumDetails.get(i).getModifiedDate());

					editor2.commit();
				}
			}
			albumDetails = null;
			albumSubDetails = null;

			SharedPreferences myPref1 = getSharedPreferences("SLUpdate"
					+ appManager.getAPP_ID(), MODE_PRIVATE);
			String str = myPref1.getString(
					"MTime" + "1003" + appManager.getAPP_ID(), "");
			SharedPreferences.Editor editor1 = myPref1.edit();
			editor1.putBoolean("1003", false);
			editor1.putString("1003" + appManager.getAPP_ID(), str);
			editor1.commit();
			str = null;
		} else {
			Utility.debug("1003 : Not downloaded properly :"
					+ this.getClass().getName());
		}
	}

	public static ArrayList<Album> getAlbumDetails(String resStr) {
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(resStr.getBytes("UTF-8"));
		} catch (Exception e) {
			photoFlag = false;
			e.printStackTrace();
		}
		final int albummodify = 1;
		final int categaryid = 2;
		final int categoryname = 3;
		final int ModifiedDate = 4;
		final int albumimage = 5;

		int tagName = 0;

		Album value = null;
		ArrayList<Album> values = new ArrayList<Album>();

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, HTTP.UTF_8);

			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("albummodify")) {
						tagName = albummodify;
						value = new Album();
					} else if (parser.getName().equals("categaryid")) {
						tagName = categaryid;
					} else if (parser.getName().equals("categoryname")) {
						tagName = categoryname;
					} else if (parser.getName().equals("ModifiedDate")) {
						tagName = ModifiedDate;
					} else if (parser.getName().equals("albumimage")) {
						tagName = albumimage;
					}
				}
				if (eventType == XmlPullParser.TEXT) {
					switch (tagName) {
					case albummodify:
						break;
					case categaryid:
						value.setCategaryid(parser.getText());
						break;
					case categoryname:
						value.setCategoryname(parser.getText());
						break;
					case ModifiedDate:
						value.setModifiedDate(parser.getText());
						break;
					case albumimage:
						value.setPicture(parser.getText());
						break;
					default:
						break;
					}
					tagName = 0;
				}

				if (eventType == XmlPullParser.END_TAG) {
					if (parser.getName().equals("albummodify")) {
						values.add(value);
						value = null;
					}
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			photoFlag = false;
		} catch (IOException e) {
			e.printStackTrace();
			photoFlag = false;
		}
		return values;
	}

	private void getNewsDetails() {
		InputStream in = null;
		try {
			in = HttpConnection.connect(appManager.NEWS_URL);
			Utility.debug("NEWS_URL : " + appManager.NEWS_URL);
			String str = Utility.getString(in);
			SharedPreferences settings = getSharedPreferences("NewsPref"
					+ appManager.getAPP_ID(), MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("NewsHome", str);
			editor.commit();
			in = null;
			str = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void SaveImageSDCard(InputStream is, String path) {
		Bitmap bitmap = null;
		Bitmap bitmap1 = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		try {
			bitmap = BitmapFactory.decodeStream(is, null, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			bitmap = null;
			try {
				BitmapFactory.Options options1 = new BitmapFactory.Options();
				options1.inSampleSize = 2;
				bitmap = BitmapFactory.decodeStream(is, null, options1);
			} catch (OutOfMemoryError e1) {
				e1.printStackTrace();
				bitmap = null;
				try {
					BitmapFactory.Options options2 = new BitmapFactory.Options();
					options2.inSampleSize = 3;
					bitmap = BitmapFactory.decodeStream(is, null, options2);
				} catch (OutOfMemoryError e2) {
					e2.printStackTrace();
					bitmap = null;
					try {
						BitmapFactory.Options options3 = new BitmapFactory.Options();
						options3.inSampleSize = 4;
						bitmap = BitmapFactory.decodeStream(is, null, options3);
					} catch (OutOfMemoryError e3) {
						e3.printStackTrace();
						bitmap = null;
					}
				}
			}
		}

		if (bitmap != null) {
			String key = null;
			try {
				key = path;
			} catch (Exception e) {
				e.printStackTrace();
			}

			java.io.OutputStream outStream = null;

			Display display = getWindowManager().getDefaultDisplay();
			float width = display.getWidth();
			float xx = width / 640;
			int yy = (int) (960 * xx);

			bitmap1 = Bitmap.createScaledBitmap(bitmap, display.getWidth(), yy,
					true);

			File file = new File(appManager.DATA_DIR_BG, key + ".PNG");
			try {
				outStream = new FileOutputStream(file);
				bitmap1.compress(Bitmap.CompressFormat.PNG, 0, outStream);
				outStream.flush();
				outStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		bitmap = null;
		bitmap1 = null;
		System.gc();
	}

	public static Bitmap FetchImage(String path) {
		String path1 = AppManager.getInstance().DATA_DIR_BG + path + ".PNG";
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bitmapImage = null;
		if (new File(path1).exists()) {
			try {
				options.inSampleSize = 1;
				bitmapImage = BitmapFactory.decodeFile(path1, options);

			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				System.gc();
				try {
					options.inSampleSize = 2;
					bitmapImage = BitmapFactory.decodeFile(path1, options);
				} catch (OutOfMemoryError e1) {
					e1.printStackTrace();
					try {
						options.inSampleSize = 3;
						bitmapImage = BitmapFactory.decodeFile(path1, options);
					} catch (OutOfMemoryError e2) {
						e2.printStackTrace();
						try {
							options.inSampleSize = 4;
							bitmapImage = BitmapFactory.decodeFile(path1,
									options);
						} catch (OutOfMemoryError e3) {
							e3.printStackTrace();
						}
					}
				}
			}
		}
		System.gc();
		return bitmapImage;
	}

	public static Bitmap tempFetchImage(String path) {
		String path1 = AppManager.getInstance().DATA_TEMP_BG + path + ".PNG";
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bitmapImage = null;
		if (new File(path1).exists()) {
			try {
				options.inSampleSize = 1;
				bitmapImage = BitmapFactory.decodeFile(path1, options);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				System.gc();
				try {
					options.inSampleSize = 2;
					bitmapImage = BitmapFactory.decodeFile(path1, options);
				} catch (OutOfMemoryError e1) {
					e1.printStackTrace();
					try {
						options.inSampleSize = 3;
						bitmapImage = BitmapFactory.decodeFile(path1, options);
					} catch (OutOfMemoryError e2) {
						e2.printStackTrace();
						try {
							options.inSampleSize = 4;
							bitmapImage = BitmapFactory.decodeFile(path1,
									options);
						} catch (OutOfMemoryError e3) {
							e3.printStackTrace();
						}
					}
				}
			}
		}
		System.gc();
		return bitmapImage;
	}

	public void getTempResizedImage(String filePath, InputStream is,
			String path, int yy, int width) {
		Bitmap bitmap = null;
		Bitmap bitmap1 = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		try {
			bitmap = BitmapFactory.decodeStream(is, null, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			bitmap = null;
			try {
				BitmapFactory.Options options1 = new BitmapFactory.Options();
				options1.inSampleSize = 2;
				bitmap = BitmapFactory.decodeStream(is, null, options1);
			} catch (OutOfMemoryError e1) {
				e1.printStackTrace();
				bitmap = null;
				try {
					BitmapFactory.Options options2 = new BitmapFactory.Options();
					options2.inSampleSize = 3;
					bitmap = BitmapFactory.decodeStream(is, null, options2);
				} catch (OutOfMemoryError e2) {
					e2.printStackTrace();
					bitmap = null;
					try {
						BitmapFactory.Options options3 = new BitmapFactory.Options();
						options3.inSampleSize = 4;
						bitmap = BitmapFactory.decodeStream(is, null, options3);
					} catch (OutOfMemoryError e3) {
						e3.printStackTrace();
						bitmap = null;
					}
				}
			}
		}

		if (bitmap != null) {
			String key = null;
			try {
				key = path;
			} catch (Exception e) {
				e.printStackTrace();
			}
			java.io.OutputStream outStream = null;
			bitmap1 = Bitmap.createScaledBitmap(bitmap, width, yy, true);
			File file = new File(filePath, key + ".PNG");
			try {
				outStream = new FileOutputStream(file);
				bitmap1.compress(Bitmap.CompressFormat.PNG, 0, outStream);
				outStream.flush();
				outStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			updateFlag = true;
		}
		bitmap = null;
		bitmap1 = null;
		System.gc();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @imran Ali, to capture back event.
	 */
	@Override
	public void onBackPressed() {
		try {
			finish();
			Utility.killMyApp(getApplicationContext(),
					SLMyAppSplashActivity.this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onBackPressed();
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
			return new AlertDialog.Builder(SLMyAppSplashActivity.this)
					.setTitle("Network Alert!")
					.setMessage(
							"No Internet Connection available.You can only view content you've viewed earlier.")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									startScreen();
								}
							}).create();
		}
		return null;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
