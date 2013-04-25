package com.snaplion.videos;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.snaplion.beans.Video;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.preview.PreviewSnapLionAppsSelectActvity;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class SLVideoActivity extends AppSuperActivity {
	@Override
	protected void onResume() {
		SnapLionMyAppActivity.KillFlag = true;
		System.gc();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		try {
			videoArrayList.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
		super.onDestroy();
	}

	private LinearLayout mainBg = null;
	public static ArrayList<Video> videoArrayList = new ArrayList<Video>();
	protected static ArrayList<ArrayList<String>> comentsDetails = new ArrayList<ArrayList<String>>();
	private String catName = null;
	private ListView songList;
	public static String buyurl;
	static String comments;
	static String description;
	private String urlThumnail;
	static BitmapDrawable background;
	private ImageAdapter adapter = null;
	private String BackFlag = "yes";
	int Flag = 0;
	AppManager appManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);
		Utility.closeAllBelowActivities(this);
		appManager = AppManager.getInstance();
		// ///////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent, this);
		// ///////////////////////////
		SnapLionMyAppActivity.KillFlag = true;

		Intent i = getIntent();
		catName = i.getStringExtra("VideosName");

		if (com.snaplion.util.BottomClass.getBottomAvailable(
				getApplicationContext(), appManager.getAPP_ID(), "1006")) {
			Display display = getWindowManager().getDefaultDisplay();
			BackFlag = "no";
			LinearLayout bottomLayout = (LinearLayout) findViewById(R.id.video_bottom_bar);
			bottomLayout.setVisibility(View.VISIBLE);
			try {
				com.snaplion.util.BottomClass.showBLayout(this, bottomLayout,
						display.getWidth(), appManager.getAPP_ID(),
						i.getStringExtra("appname"));
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}

		TextView tv = (TextView) findViewById(R.id.v_sub_name_txt);
		tv.setText(catName);
		tv.setTypeface(appManager.lucida_grande_regular);

		if (appManager.PREVIEWAPP_FLAG) {
			Button homeBtn = (Button) findViewById(R.id.v_home_btn);
			homeBtn.setVisibility(View.VISIBLE);
			homeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SnapLionMyAppActivity.KillFlag = false;
					try {
						com.snaplion.music.SLMusicPlayerActivity
								.closePlayer(getApplicationContext());
					} catch (Exception e) {
						e.printStackTrace();
					}
					Intent i = new Intent(SLVideoActivity.this,
							PreviewSnapLionAppsSelectActvity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					SLVideoActivity.this.startActivity(i);
					finish();
					try {
						System.gc();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}

		Button backBtn = (Button) findViewById(R.id.v_sub_back_btn);
		if (BackFlag.equalsIgnoreCase("no")) {
			if (!appManager.getAPP_ID().equalsIgnoreCase("222")) {
				backBtn.setVisibility(View.INVISIBLE);
			} else {
				backBtn.setBackgroundResource(R.drawable.top_bar_logo);
			}
		} else {
			if (!appManager.getAPP_ID().equalsIgnoreCase("222")) {
				backBtn.setVisibility(View.INVISIBLE);
			} else {
				backBtn.setBackgroundResource(R.drawable.selector_back_btn);
			}
			backBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SnapLionMyAppActivity.KillFlag = false;
					finishFromChild(getParent());
				}
			});
		}

		songList = (ListView) findViewById(R.id.album_listview);
		songList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SnapLionMyAppActivity.KillFlag = false;
				Intent i = new Intent(getApplicationContext(),
						SLVideoDetailActivity.class);
				i.putExtra("id", videoArrayList.get(position).getId());
				i.putExtra("catName", catName);
				i.putExtra("Position", position);
				i.putExtra("Title", videoArrayList.get(position).getTitle());
				i.putExtra("description", videoArrayList.get(position)
						.getMessage());
				i.putExtra("videoUrl", videoArrayList.get(position).getUrl());
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				Runtime.getRuntime().gc();
			}
		});

		mainBg = (LinearLayout) findViewById(R.id.album_main_bg);
		BitmapDrawable background = new BitmapDrawable(
				Utility.getScaledCroppedImageByDisplay(
						this,
						SLMyAppSplashActivity.FetchImage("VideoBG"
								+ appManager.getAPP_ID())));
		mainBg.setBackgroundDrawable(background);
		background = null;
		handler.sendEmptyMessage(appManager.DISPLAY);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppManager.DISPLAY:
				Utility.FileCreate();
				adapter = new ImageAdapter();

				SharedPreferences myPref = getSharedPreferences("VidioPref"
						+ appManager.getAPP_ID(), MODE_PRIVATE);
				String resStr = myPref.getString("youtubevideo", "");

				if (resStr != "") {
					Flag = 1;
					videoArrayList = getYouTubeData(resStr);
					if (videoArrayList.size() > 0) {
						songList.setAdapter(adapter);
						if (videoArrayList.size() > 0
								&& Utility.isOnline(getApplicationContext())) {
							// new MyDownloadBGTask1().execute();
						}
					}
				}
				try {
					if (Flag == 0) {

						if (Utility.isOnline(getApplicationContext())) {
							try {
								showDialog(appManager.PROGRESS);
							} catch (Exception e) {
								e.printStackTrace();
							}
							new MyDownloadBGTask().execute();
						} else {
							Utility.showToastMessage(getApplicationContext(),
									getString(R.string.no_net_not_downloaded));
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				break;
			case AppManager.PROGRESS:
				try {
					songList.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					dismissDialog(appManager.PROGRESS);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				handler.sendEmptyMessage(appManager.WEB_CALL);
				break;
			case AppManager.WEB_CALL:
				try {
					SharedPreferences myPref1 = getSharedPreferences("SLUpdate"
							+ appManager.getAPP_ID(), MODE_PRIVATE);
					String str = myPref1.getString("MTime" + "1006"
							+ appManager.getAPP_ID(), "");
					SharedPreferences.Editor editor = myPref1.edit();
					editor.putBoolean("1006", false);
					editor.putString("1006" + appManager.getAPP_ID(), str);
					editor.commit();
				} catch (Exception ex) {
					Utility.debug("1006 : Not downloaded properly :"
							+ this.getClass().getName());
				}
				if (Utility.isOnline(getApplicationContext()))
					new MyDownloadBGTask().execute();
				break;
			case AppManager.ERROR:
				Toast.makeText(getApplicationContext(),
						"Network Fail.Please try later .!", Toast.LENGTH_LONG)
						.show();
				break;
			}
		}
	};

	protected void getVideoDetails(String string) {
		InputStream in = null;
		try {
			in = HttpConnection.connect(string);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			SharedPreferences settings = getSharedPreferences("VidioPref"
					+ appManager.getAPP_ID(), MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("youtubevideo", sb.toString());
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			SharedPreferences myPref1 = getSharedPreferences("VidioPref"
					+ appManager.getAPP_ID(), MODE_PRIVATE);
			String resStr = myPref1.getString("youtubevideo", "");
			if (resStr != "") {
				try {
					videoArrayList.clear();
				} catch (Exception e) {
					e.printStackTrace();
				}
				videoArrayList = getYouTubeData(resStr);
			}

			handler.sendEmptyMessage(appManager.PROGRESS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class MyDownloadBGTask extends AsyncTask<Void, Integer, Void> {
		private boolean updateFlag = true;
		String resStr = null;

		@Override
		protected void onPostExecute(Void result) {
			if (updateFlag) {
				SharedPreferences myPref = getSharedPreferences("SLUpdate"
						+ appManager.getAPP_ID(), MODE_PRIVATE);
				String str = myPref.getString(
						"MTime" + "1006" + appManager.getAPP_ID(), "");
				SharedPreferences.Editor editor = myPref.edit();
				editor.putBoolean("1006", false);
				editor.putString("1006" + appManager.getAPP_ID(), str);
				editor.commit();
			} else {
				Utility.debug("1006 : Not downloaded properly :"
						+ this.getClass().getName());
			}
			try {
				dismissDialog(appManager.PROGRESS);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				songList.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (videoArrayList.size() > 0
					&& Utility.isOnline(getApplicationContext())) {
				// new MyDownloadBGTask1().execute();
			}
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (Flag == 0) {
				InputStream in = null;
				try {
					in = HttpConnection.connect(appManager.VIDEO_URL);
					Utility.debug("VIDEO_URL : " + appManager.VIDEO_URL);
					if (in != null)
						resStr = Utility.getString(in);
					else
						updateFlag = false;

					SharedPreferences settings = getSharedPreferences(
							"VidioPref" + appManager.getAPP_ID(), MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("youtubevideo", resStr);
					editor.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (resStr != "") {
					try {
						videoArrayList.clear();
					} catch (Exception e) {
						e.printStackTrace();
					}
					videoArrayList = getYouTubeData(resStr);
				}
			}
			return null;
		}
	}

	private class MyDownloadBGTask1 extends AsyncTask<Void, Integer, Void> {
		@Override
		protected void onProgressUpdate(Integer... values) {
			try {
				adapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.onProgressUpdate(values);
		}

		@Override
		protected Void doInBackground(Void... params) {
			for (int i = 0; i < videoArrayList.size(); i++) {
				URL url = null;
				;
				try {
					String[] extractUrl = videoArrayList.get(i).getUrl()
							.split("=");
					String thumbnailId = extractUrl[1];
					urlThumnail = "http://img.youtube.com/vi/" + thumbnailId
							+ "/0.jpg";
					System.out.println("thumnail is .........." + urlThumnail);
					url = new URL(urlThumnail);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				// Bitmap bitmap = null;
				try {
					// Video ad = new Video();
					// ad = videoArrayList.get(i);
					// //bitmap = Utility.getImageBitmap(url);
					// Display display = getWindowManager().getDefaultDisplay();
					// // ad.setBitmapSnap(bitmap);
					// Bitmap scaledBitmap =
					// Utility.getScaledImageByWinWidth(url,display.getWidth(),display.getHeight());
					// float density =
					// getApplicationContext().getResources().getDisplayMetrics().density;
					//
					// int boxHeight=Math.round((float)170 * density);
					// int y = (scaledBitmap.getHeight()-boxHeight)/2;
					// Bitmap cropedBitmap = Bitmap.createBitmap(scaledBitmap,
					// 0, y, display.getWidth(), boxHeight, null, true);
					// ad.setBitmapSnap(cropedBitmap);
					//
					// scaledBitmap.recycle();
					// scaledBitmap = null;
					// //
					// //ad.setBitmapSnap(Utility.getBitmapZoomToFitScaled(SLVideoActivity.this,
					// bitmap, display.getWidth(), Math.round((float)170 *
					// density)));
					//
					// videoArrayList.set(i, ad);
				} catch (Exception e) {
					e.printStackTrace();
				}
				publishProgress(0);
			}
			return null;
		}
	}

	public ArrayList<Video> getYouTubeData(String resStr) {
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(resStr.getBytes("UTF-8"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		final int picture = 1;
		final int videorecord = 2;
		final int title = 3;
		final int url = 4;
		final int message = 5;
		final int commentrecord = 6;
		final int comment = 7;
		final int id = 8;

		int tagName = 0;

		Video value = null;
		ArrayList<Video> values = new ArrayList<Video>();
		ArrayList<String> values1 = new ArrayList<String>();

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, HTTP.UTF_8);

			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("picture")) {
						tagName = picture;
					} else if (parser.getName().equals("videorecord")) {
						value = new Video();
						tagName = videorecord;
					} else if (parser.getName().equals("title")) {
						tagName = title;
					} else if (parser.getName().equals("url")) {
						tagName = url;
					} else if (parser.getName().equals("message")) {
						tagName = message;
					} else if (parser.getName().equals("commentrecord")) {
						tagName = commentrecord;
						values1 = new ArrayList<String>();
					} else if (parser.getName().equals("comment")) {
						tagName = comment;
					} else if (parser.getName().equals("id")) {
						tagName = id;
					}
				}
				if (eventType == XmlPullParser.TEXT) {
					switch (tagName) {
					case picture:
						// videoBgStr = parser.getText();
						break;
					case videorecord:
						break;
					case title:
						value.setTitle(parser.getText());
						break;
					case url:
						value.setUrl(parser.getText());
						break;
					case message:
						value.setMessage(parser.getText());
						break;
					case commentrecord:
						break;
					case comment:
						values1.add(parser.getText());
						break;
					case id:
						value.setId(parser.getText());
						break;
					default:
						break;
					}
					tagName = 0;
				}
				if (eventType == XmlPullParser.END_TAG) {
					if (parser.getName().equals("videorecord")) {
						values.add(value);
						value = null;
					}
					if (parser.getName().equals("commentrecord")) {
						comentsDetails.add(values1);
						values1 = null;
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

	class ImageAdapter extends BaseAdapter {
		public int getCount() {
			if (videoArrayList != null) {
				return videoArrayList.size();
			}
			return 0;
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		class ViewHolder {
			ImageView youtube_play_restrict;
			ImageView iv;
			TextView tv;
		}

		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.listview_video, null);
				viewHolder = new ViewHolder();

				viewHolder.youtube_play_restrict = (ImageView) convertView
						.findViewById(R.id.youtube_play_restrict);
				viewHolder.iv = (ImageView) convertView
						.findViewById(R.id.video_view1);
				viewHolder.tv = (TextView) convertView
						.findViewById(R.id.video_title);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			try {
				if (Utility.isOnline(getApplicationContext())) {
					viewHolder.youtube_play_restrict
							.setBackgroundResource(R.drawable.play_youtube);
				} else {
					viewHolder.youtube_play_restrict
							.setBackgroundResource(R.drawable.restrict_youtube);
				}

				String[] extractUrl = videoArrayList.get(position).getUrl()
						.split("=");
				String thumbnailId = extractUrl[1];
				urlThumnail = "http://img.youtube.com/vi/" + thumbnailId
						+ "/0.jpg";
				AQuery aq = new AQuery(convertView);
				Bitmap bitmap = aq.getCachedImage(urlThumnail);
				if (bitmap != null) {
					aq.id(viewHolder.iv).image(bitmap, AQuery.RATIO_PRESERVE);
				} else {
					int reqWidth = aq.id(R.id.video_view1).getImageView()
							.getWidth();
					aq.id(viewHolder.iv).image(urlThumnail, false, true,
							reqWidth, AQuery.INVISIBLE, null, 0,
							AQuery.RATIO_PRESERVE);
				}

				// if(videoArrayList.get(position).getBitmapSnap() != null)
				// {
				// viewHolder.iv.setImageBitmap(videoArrayList.get(position).getBitmapSnap());
				// }
				// else
				// {
				// viewHolder.iv.setImageBitmap(null);
				// }
				viewHolder.iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
				viewHolder.tv.setText(videoArrayList.get(position).getTitle());
				viewHolder.tv.setTypeface(appManager.lucida_grande_regular);
				// convertView.setPadding(1, 1, 1, 2);
				convertView.setPadding(0, 0, 0, 2);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
			return convertView;
		}
		// public View getView(int position, View convertView, ViewGroup arg2)
		// {
		// if (convertView == null)
		// {
		// convertView = getLayoutInflater().inflate(R.layout.listview_video,
		// null);
		// }
		// try
		// {
		// if(Utility.isOnline(getApplicationContext())) {
		// ((ImageView)convertView.findViewById(R.id.youtube_play_restrict)).setBackgroundResource(R.drawable.play_youtube);
		// }else {
		// ((ImageView)convertView.findViewById(R.id.youtube_play_restrict)).setBackgroundResource(R.drawable.restrict_youtube);
		// }
		//
		// ImageView iv = (ImageView)convertView.findViewById(R.id.video_view1);
		// if(videoArrayList.get(position).getBitmapSnap() != null)
		// {
		// iv.setImageBitmap(videoArrayList.get(position).getBitmapSnap());
		// }
		// else
		// {
		// iv.setImageBitmap(null);
		// }
		// iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
		// TextView tv = (TextView)convertView.findViewById(R.id.video_title);
		// tv.setText(videoArrayList.get(position).getTitle());
		// tv.setTypeface(appManager.lucida_grande_regular);
		// convertView.setPadding(1, 1, 1, 2);
		// }catch (OutOfMemoryError e) {e.printStackTrace();}
		// return convertView;
		// }
	}

	public Bitmap getResizedBitmap(Bitmap srcBitmap, int newWidth, int newHeight) {
		try {
			return Bitmap.createScaledBitmap(srcBitmap, newWidth, newHeight,
					true);
		} catch (Exception e) {
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
				Utility.killMyApp(getApplicationContext(), SLVideoActivity.this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onPause();
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
		}
		return null;
	}

	@Override
	public void onBackPressed() {
		SnapLionMyAppActivity.KillFlag = false;
		super.onBackPressed();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGaTracker.sendView("Videos_Screen");
	}
}
