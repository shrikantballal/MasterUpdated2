package com.snaplion.music;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.snaplion.beans.AlbumSong;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.mail.SLMailActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.news.SLNewsURLActivity;
import com.snaplion.preview.PreviewSnapLionAppsSelectActvity;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;



public class SLMusicActivity extends AppSuperActivity 
{
	private static boolean updateFlag=true;
	private static final int DIALOG_ALERT = 5;
	protected static final int REFRESH_LIST = 6;
	private LinearLayout mainBg = null;
	private ArrayList<String> albumDetails = new ArrayList<String>();
	//private ArrayList<String> songNameArraylist = new ArrayList<String>();
	private ArrayList<AlbumSong> albumSongArrayList;
	private static ListView songList;
	protected static AlbumSongAdapter adapter;
	private LinearLayout ll;
	boolean clicked = false;
	int second = 0;
	int count = 0;
	public static boolean music = false;
	private LinearLayout bottomLayout = null;
	private LinearLayout bottomPlayer = null;
	private int width = 0;
	private int Flag = 0;
	private String catName = null;
	private String track = null;
	private String BackFlag = "yes";
	private Gallery albumTrackList = null;
	private ImageAdapter albumAdapter = null;
	private ProgressBar songProgress = null;
	protected static String SongId = null;
	private static Context mContext = null;
	private int album_track = 0;
	private static String TAG="SLMusicAcivity";
	AppManager appManager;
	
	@Override
	protected void onResume() {
		//adapter.notifyDataSetChanged();
		SnapLionMyAppActivity.KillFlag = true;
		super.onResume();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music);
		Utility.closeAllBelowActivities(this);
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		SnapLionMyAppActivity.KillFlag = true;
		Log.d(TAG, "OnCreate() of SLMusicAcitivity");
		mContext = getApplicationContext();
		
        com.snaplion.music.SLMusicActivity.music = true;
		com.snaplion.myapp.SnapLionMyAppActivity.homeMusic = false;

		Display display = getWindowManager().getDefaultDisplay(); 
		width = display.getWidth();
		
		Intent i = getIntent();
		catName = i.getStringExtra("MusicName");
		
		bottomPlayer = (LinearLayout)findViewById(R.id.mus_player_bar);
		bottomLayout = (LinearLayout)findViewById(R.id.mus_bottom_bar);
		
		if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1004")){
			BackFlag = "no";
			bottomLayout.setVisibility(View.VISIBLE);
			com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,width,appManager.getAPP_ID(),appManager.getAPP_NAME());
		}
		
		TextView mSubTxt = (TextView)findViewById(R.id.m_sub_name_txt);
		mSubTxt.setText(catName);
		mSubTxt.setTypeface(appManager.lucida_grande_regular);
		
		if(appManager.PREVIEWAPP_FLAG)
		{
			Button homeBtn = (Button)findViewById(R.id.m_home_btn);
			homeBtn.setVisibility(View.VISIBLE);
			homeBtn.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					PreviewSnapLionAppsSelectActvity.KillFlag = false;
					try
					{
						com.snaplion.music.SLMusicPlayerActivity.closePlayer(getApplicationContext());
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
					Intent i = new Intent(getApplicationContext(),PreviewSnapLionAppsSelectActvity.class);
    				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    				startActivity(i);
    				finishFromChild(getParent());
				}
			});
		}
		
		
		Button backBtn = (Button)findViewById(R.id.m_sub_back_btn);
		if(BackFlag.equalsIgnoreCase("no"))
		{
			if(!appManager.getAPP_ID().equalsIgnoreCase("222")) {
				backBtn.setVisibility(View.INVISIBLE);
			} else {
				backBtn.setBackgroundResource(R.drawable.top_bar_logo);
			}
		}
		else
		{
			if(!appManager.getAPP_ID().equalsIgnoreCase("222")) {
				// backBtn.setVisibility(View.INVISIBLE);
			} else {
				backBtn.setBackgroundResource(R.drawable.selector_back_btn);
			}
			backBtn.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					SnapLionMyAppActivity.KillFlag = false;
					finishFromChild(getParent());
				}
			});
		}
		
		albumSongArrayList = new ArrayList<AlbumSong>();
		songList = (ListView) findViewById(R.id.album_listview);
		songList.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
			 if(Utility.isOnline(getApplicationContext()))
			 {
				 mGaTracker.sendView("Music_play_"+albumSongArrayList.get(position).getNoofplay());
				 SLMusicPlayerActivity.listRefresh(albumSongArrayList,position);
				 if(albumSongArrayList.get(position).getSource().equals("iTunes")) 
				 {
					 Utility.showToastMessage(getApplicationContext(), getString(R.string.unable_to_play));
				 }
			 }
			 else
			 {
			 	handler.sendEmptyMessage(appManager.ERROR);
			 } 
			}
		});
		
		albumTrackList = (Gallery)findViewById(R.id.album_name_list);
		albumAdapter = new ImageAdapter();
		adapter = new AlbumSongAdapter(mContext);
		albumTrackList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view1, int arg2,long arg3) {
				try{
					track = albumDetails.get(arg2);
					album_track = arg2;
				}catch (Exception e) {e.printStackTrace();}
				albumAdapter.notifyDataSetChanged();
				SharedPreferences myPref1 = getSharedPreferences("MusicPref"+appManager.getAPP_ID(),MODE_PRIVATE);
				String resStr = myPref1.getString("AlbumSong"+track, "");
				if(resStr != "")
				{
					albumSongArrayList = getAlbumSongs(resStr);
					songList.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				}else{
				  try {
					songProgress.setVisibility(View.VISIBLE);
					songList.setVisibility(View.INVISIBLE);
					String url = appManager.MUSIC_BY_ALBUM_NAME_URL.replaceAll("<<ALBUM_NAME>>", track);
					Utility.debug("MUSIC_BY_ALBUM_NAME_URL : "+url);
					new MyDownloadTask1().execute(url);
					//new MyDownloadTask1().execute(appManager.SERVICE_URL+"service_music_allv1.php?AppId="+appManager.getAPP_ID()+"&albumname="+track);
				 } catch (Exception e1) {e1.printStackTrace();}
				}
			}
		});
		songProgress = (ProgressBar)findViewById(R.id.song_progress);
		mainBg = (LinearLayout) findViewById(R.id.album_main_bg);
		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("MusicAlbumBG"+appManager.getAPP_ID())));
		mainBg.setBackgroundDrawable(background);
		background = null;
		handler.sendEmptyMessage(appManager.PROGRESS);
	}
 


	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppManager.PROGRESS:
				Utility.FileCreate();
				
				try
				{
					 com.snaplion.music.SLMusicPlayerActivity.seekBarProgress.setSecondaryProgress(com.snaplion.music.SLMusicPlayerActivity.seekbarPosition);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				com.snaplion.music.SLMusicPlayerActivity.getPlayer(SLMusicActivity.this,getApplicationContext(),bottomPlayer,appManager.getAPP_ID());

				SharedPreferences myPref = getSharedPreferences("MusicPref"+appManager.getAPP_ID(), MODE_PRIVATE);
				String albumStr = myPref.getString("MusciAlbumData", "");
				
				if (albumStr != "") {
					Flag=1;
					try {albumDetails.clear();} catch (Exception e) {e.printStackTrace();}
						albumDetails = getAlbumDetails(albumStr);
						album_track = 0;
						albumTrackList.setAdapter(albumAdapter);
						
					String resStr = myPref.getString("AlbumSong"+"All", "");
					Log.d(TAG, "String value="+ resStr);
					albumSongArrayList = getAlbumSongs(resStr);
					
					Log.d(TAG,"albumSongArrayList size is .........."+ albumSongArrayList.size());
					
					songList.setAdapter(adapter);
				}

				break;
			case AppManager.DISPLAY:
				try {
					if(Flag == 0){
		        		showDialog(appManager.PROGRESS);
		        		if(Utility.isOnline(getApplicationContext()))
		        		{
		        			new MyDownloadTask().execute(appManager.MUSIC_ALBUM_NAMES_URL);
		        			Utility.debug("MUSIC_ALBUM_NAMES_URL : "+appManager.MUSIC_ALBUM_NAMES_URL);
		        		}
					}
				} catch (Exception e1) {e1.printStackTrace();}
				break;
	        case DIALOG_ALERT:
	        	showDialog(appManager.DIALOG_DISPLAY);
	        	break;
	        case REFRESH_LIST:
				//SLMusicPlayerActivity.listRefresh(albumSongArrayList);
				//adapter.notifyDataSetChanged();
	        	break;
			case AppManager.ERROR:
				Toast.makeText(getApplicationContext(),"Network Fail.Please try later .!", Toast.LENGTH_LONG).show();
				break;
			}
		}
	};
	
	public void generateUIforAlbum() {
		albumTrackList.setAdapter(albumAdapter);
		albumAdapter.notifyDataSetChanged();
		
		for (int i = 0; i < albumDetails.size(); i++) {
			System.out.println("size is ..........." + albumDetails.size());
			final TextView txtAlbum = new TextView(this);
			txtAlbum.setPadding(5,8, 20, 5);
			txtAlbum.setText(albumDetails.get(i));
			txtAlbum.setTextColor(Color.WHITE);

			txtAlbum.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String albumSong = txtAlbum.getText().toString();
					int index = albumDetails.indexOf(albumSong);
					clear(index);
					System.out.println("albumSong............." + albumSong);
					track = albumSong.replace(" ", "%20");
					SharedPreferences myPref1 = getSharedPreferences("MusicPref"+appManager.getAPP_ID(),MODE_PRIVATE);
					String resStr = myPref1.getString("AlbumSong"+track, "");
					if(resStr != "")
					{
						albumSongArrayList = getAlbumSongs(resStr);
						songList.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					}else{
						try 
						{
							String url = appManager.MUSIC_BY_ALBUM_NAME_URL.replaceAll("<<ALBUM_NAME>>", track);
							Utility.debug("MUSIC_BY_ALBUM_NAME_URL : "+url);
							new MyDownloadTask1().execute(url);
							//new MyDownloadTask1().execute(appManager.SERVICE_URL+"service_music_allv1.php?AppId="+appManager.getAPP_ID()+"&albumname="+track);
						} catch (Exception e1) {e1.printStackTrace();}
					}
				 }

				private void clear(int index) {
					for (int j = 0; j < albumDetails.size(); j++) {
						if (j == index) {
							txtAlbum.setTypeface(null, Typeface.BOLD);
						} else {
							txtAlbum.setTypeface(null, Typeface.NORMAL);
						}
					}
				}
			});
			ll.addView(txtAlbum);
		}
	}

	private class MyDownloadTask extends AsyncTask<String, Integer, InputStream> 
	{
		@Override
		protected void onPostExecute(InputStream result) 
		{
			if(Flag == 0)
			{
				try
				{
					if(albumDetails != null)
					{
						try
						{
							track = albumDetails.get(0);
							album_track = 0;
						}
						catch (Exception e) 
						{
							e.printStackTrace();
							try
							{
								dismissDialog(appManager.PROGRESS);
							}
							catch (Exception e1) 
							{
								e1.printStackTrace();
							}
						}
						String url = appManager.MUSIC_BY_ALBUM_NAME_URL.replaceAll("<<ALBUM_NAME>>", track);
						Utility.debug("MUSIC_BY_ALBUM_NAME_URL : "+url);
						new MyDownloadTask1().execute(url);
						//new MyDownloadTask1().execute(appManager.SERVICE_URL+"service_music_allv1.php?AppId="+appManager.getAPP_ID()+"&albumname="+track);
					 }
				}
				catch (Exception e) 
				{
					updateFlag=false;
					e.printStackTrace();
				}
			}
			else
			{
			  	songList.setAdapter(adapter);
			  	adapter.notifyDataSetChanged();
			}
			super.onPostExecute(result);
		}

		@Override
		protected InputStream doInBackground(String... params) 
		{
			InputStream in = null;
			try 
			{
				in = HttpConnection.connect(params[0]);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) 
				{
					sb.append(line);
				}
				SharedPreferences settings = getSharedPreferences("MusicPref"+appManager.getAPP_ID(),MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("MusciAlbumData", sb.toString());
				editor.commit();
			} 
			catch (Exception e) 
			{
				updateFlag=false;
				e.printStackTrace();
			}
			try 
			{
				SharedPreferences myPref1 = getSharedPreferences("MusicPref"+appManager.getAPP_ID(),MODE_PRIVATE);
				String resStr = myPref1.getString("MusciAlbumData", "");
				try 
				{
					albumDetails.clear();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				albumDetails = getAlbumDetails(resStr);
			} 
			catch (Exception e) 
			{
				updateFlag=false;
				e.printStackTrace();
			}
			return null;
		}

	}

	private class MyDownloadTask1 extends AsyncTask<String, Integer, InputStream> 
	{
		@Override
		protected void onPostExecute(InputStream result) 
		{
			SharedPreferences myPref1 = getSharedPreferences("MusicPref"+appManager.getAPP_ID(),MODE_PRIVATE);
			String resStr = myPref1.getString("AlbumSong"+track, "");
			
			try
			{
				albumSongArrayList.clear();
			}
			catch (Exception e) 
			{
				updateFlag=false;
				e.printStackTrace();
			}

			albumSongArrayList = getAlbumSongs(resStr);
			
			if(Flag == 0)
			{
				Flag=1;
				albumTrackList.setAdapter(albumAdapter);
				albumAdapter.notifyDataSetChanged();
				songList.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				
			}
			else
			{
				songProgress.setVisibility(View.GONE);
				songList.setVisibility(View.VISIBLE);
			  	songList.setAdapter(adapter);
			  	adapter.notifyDataSetChanged();
			}
			if(updateFlag)
			{
				try
				{
					SharedPreferences myPref = getSharedPreferences("SLUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
					String str = myPref.getString("MTime"+"1004"+appManager.getAPP_ID(), "");
					SharedPreferences.Editor editor = myPref.edit();
					editor.putBoolean("1004", false);
					editor.putString("1004"+appManager.getAPP_ID(),str);
					editor.commit();
					dismissDialog(appManager.PROGRESS);
				}
				catch (Exception e) 
				{
					updateFlag=false;
					e.printStackTrace();
				}
			}
			else
			{
				Utility.debug("1004 : Not downloaded properly :"+this.getClass().getName());
			}
		}
		
		@Override
		protected InputStream doInBackground(String... params) 
		{
			InputStream in = null;
			try 
			{
				String str = Utility.replace(params[0], " ", "%20");
				in = HttpConnection.connect(str);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) 
				{
					sb.append(line);
				}
				SharedPreferences settings = getSharedPreferences("MusicPref"+appManager.getAPP_ID(),MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("AlbumSong"+track, sb.toString());
				editor.commit();
			} 
			catch (Exception e) 
			{
				updateFlag=false;
				e.printStackTrace();
			}
			return null;
		}

	}

	public ArrayList<String> getAlbumDetails(String resStr) 
	{
		InputStream in = null;
		try 
		{
			in = new ByteArrayInputStream(resStr.getBytes("UTF-8"));
		} 
		catch (Exception e) 
		{
			updateFlag=false;
			e.printStackTrace();
		}
		final int picture = 1;
		final int artist = 2;

		int tagName = 0;
		String value = null;
		ArrayList<String> values = new ArrayList<String>();

		try 
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, HTTP.UTF_8);

			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) 
			{
				if (eventType == XmlPullParser.START_TAG) 
				{
					if (parser.getName().equals("picture")) 
					{
						tagName = picture;
					} 
					else if (parser.getName().equals("artist")) 
					{
						tagName = artist;
					}
				}
				if (eventType == XmlPullParser.TEXT) 
				{
					switch (tagName) 
					{
						case picture:
							//str = parser.getText();
							break;
						case artist:
							value = parser.getText();
							break;
						default:
							break;
					}
					tagName = 0;
				}

				if (eventType == XmlPullParser.END_TAG) 
				{
					if (parser.getName().equals("artist")) 
					{
						values.add(value);
						value = null;
					}
				}
				eventType = parser.next();
			}
		} 
		catch (XmlPullParserException e) 
		{
			updateFlag=false;
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			updateFlag=false;
			e.printStackTrace();
		}
		return values;
	}

	public static ArrayList<AlbumSong> getAlbumSongs(String resStr) 
	{
		InputStream in = null;
		try 
		{
			in = new ByteArrayInputStream(resStr.getBytes("UTF-8"));
		} 
		catch (Exception e) 
		{
			updateFlag=false;
			e.printStackTrace();
		}
		final int trackname = 1;
		final int url = 2;
		final int buyit = 3;
		final int buyurl = 4;
		final int noofplay = 5;
		final int duration = 6;
		final int noofcomment = 7;
		final int source = 8;
		final int albumname = 9;
		final int enableDownload = 10;
		final int download_url = 11;

		int tagName = 0;

		AlbumSong value = null;
		ArrayList<AlbumSong> values = new ArrayList<AlbumSong>();

		try 
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, HTTP.UTF_8);

			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) 
			{
				if (eventType == XmlPullParser.START_TAG) 
				{
					if (parser.getName().equals("trackname")) 
					{
						tagName = trackname;
						value = new AlbumSong();
					} 
					else if (parser.getName().equals("url")) 
					{
						tagName = url;
					} 
					else if (parser.getName().equals("buyit")) 
					{
						tagName = buyit;
					} 
					else if (parser.getName().equals("buyurl")) 
					{
						tagName = buyurl;
					} 
					else if (parser.getName().equals("noofplay")) 
					{
						tagName = noofplay;
					} 
					else if (parser.getName().equals("duration")) 
					{
						tagName = duration;
					} 
					else if (parser.getName().equals("noofcomment")) 
					{
						tagName = noofcomment;
					} 
					else if (parser.getName().equals("source")) 
					{
						tagName = source;
					} 
					else if (parser.getName().equals("albumname")) 
					{
						tagName = albumname;
					}
					else if (parser.getName().equals("enableDownload")) 
					{
						tagName = enableDownload;
					} 
					else if (parser.getName().equals("download_url")) 
					{
						tagName = download_url;
					}
				}
				if (eventType == XmlPullParser.TEXT) 
				{
					switch (tagName) 
					{
						case trackname:
							value.setTrackname(parser.getText());
							break;
						case url:
							value.setUrl(parser.getText());
							break;
						case buyit:
							value.setBuyit(parser.getText());
							break;
						case buyurl:
							value.setBuyurl(parser.getText());
							break;
						case noofplay:
							value.setNoofplay(parser.getText());
							break;
						case duration:
							value.setDuration(parser.getText());
							break;
						case noofcomment:
							value.setNoofcomment(parser.getText());
							break;
						case source:
							value.setSource(parser.getText());
							break;
						case albumname:
							value.setAlbumname(parser.getText());
							break;
						case enableDownload:
							value.setEnableDownload(parser.getText());
							break;
						case download_url:
							value.setDownload_url(parser.getText());
							break;
						default:
							break;
					}
					tagName = 0;
				}

				if (eventType == XmlPullParser.END_TAG) 
				{
					if (parser.getName().equals("music")) 
					{
						values.add(value);
						value = null;
					}
				}
				eventType = parser.next();
			}
		} 
		catch (XmlPullParserException e) 
		{
			updateFlag=false;
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			updateFlag=false;
			e.printStackTrace();
		}
		return values;
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
	        	 return new AlertDialog.Builder(SLMusicActivity.this)
	        		.setMessage("Pls subscribe to the mailing list first" )
	        		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface dialog, int which) {
	        				Intent i1009 = new Intent(getApplicationContext(), SLMailActivity.class);
	    					i1009.putExtra("MailName","Mailing List");
	    					i1009.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    					startActivity(i1009);
	        			}
	        		})
	        		.setNegativeButton("No", new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface dialog, int which) {
	        				try{
	        					dismissDialog(appManager.DIALOG_DISPLAY);
	        				}catch (Exception e) {e.printStackTrace();}
	        			}
	        		} ).create();
	     }
	     return null;
	 }	

	
	private class AlbumSongAdapter extends BaseAdapter 
	{
		public AlbumSongAdapter(Context applicationContext) 
		{
			  mContext = applicationContext;
		}
		public int getCount() 
		{
            if (albumSongArrayList != null) 
            {
                return albumSongArrayList.size();
            }
            return 0;
        }

        public Object getItem(int arg0) 
        {
            return null;
        }

        public long getItemId(int arg0) 
        {
            return 0;
        }
	        
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) 
		{
		if(convertView == null)
			convertView = getLayoutInflater().inflate(R.layout.listview, parent, false);
		 try
		 {
			int[] colors = new int[] { R.drawable.music_dark_bar,R.drawable.music_light_bar };
			int colorPos = position % colors.length;
			
			if(SongId.equalsIgnoreCase(albumSongArrayList.get(position).getNoofplay()))
			{
				try
				{
					convertView.setBackgroundResource(R.drawable.music_extra_dark_bar);
				}
				catch (OutOfMemoryError e) 
				{
					e.printStackTrace();
				}
			}
			else
			{
			  try
			  {
				convertView.setBackgroundResource(colors[colorPos]);
			  }
			  catch (OutOfMemoryError e) 
			  {e.printStackTrace();}
			}
			
			
			 if (albumSongArrayList != null) 
			 {
				 TextView tv = (TextView) convertView.findViewById(R.id.songText);
				 tv.setText(albumSongArrayList.get(position).getTrackname());
				 tv.setTypeface(appManager.lucida_grande_regular);
				 TextView tv1 = (TextView) convertView.findViewById(R.id.artisText);
				 tv1.setText(albumSongArrayList.get(position).getAlbumname());
				 tv1.setTypeface(appManager.lucida_grande_regular);
			 try
			 {
				if(albumSongArrayList.get(position).getEnableDownload() != null && albumSongArrayList.get(position).getEnableDownload().equalsIgnoreCase("Yes")){
					TextView tv3 = (TextView) convertView.findViewById(R.id.buyText);
					tv3.setText("Download");	
					tv3.setTypeface(appManager.lucida_grande_regular);
			 	}
				else if(albumSongArrayList.get(position).getBuyurl() != null && albumSongArrayList.get(position).getBuyurl().length()>15){
					TextView tv4 = (TextView) convertView.findViewById(R.id.buyText);
			 		if(albumSongArrayList.get(position).getSource() != null && albumSongArrayList.get(position).getSource().equalsIgnoreCase("iTunes")){
						
			 			tv4.setText("Available on iTunes");
			 		}
			 		else
			 			tv4.setText("Buy it from " + albumSongArrayList.get(position).getSource());	
			 		tv4.setTypeface(appManager.lucida_grande_regular);
				}
				else
					((TextView) convertView.findViewById(R.id.buyText)).setText("");
			 	}
			 catch (Exception e) {e.printStackTrace();}
			 }
 			LinearLayout llBuy = (LinearLayout) convertView.findViewById(R.id.song_buy_layout);
				llBuy.setOnClickListener(new OnClickListener() 
				{
					public void onClick(View v) 
					{
						if(((TextView) v.findViewById(R.id.buyText)).getText().toString().equalsIgnoreCase("Download"))
						{
							mGaTracker.sendView("Music_Download_"+albumSongArrayList.get(position).getNoofplay());
							SnapLionMyAppActivity.KillFlag = false;
							TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
							
							String soundDownloadLinkUrl = appManager.SEND_SONG_DOWNLOAD_LINK_URL;
							soundDownloadLinkUrl = soundDownloadLinkUrl.replaceAll("<<DEVICE_ID>>",tManager.getDeviceId());
							soundDownloadLinkUrl = soundDownloadLinkUrl.replaceAll("<<MUSIC_TRACK_ID>>",albumSongArrayList.get(position).getNoofplay());
							Utility.debug("SEND_SONG_DOWNLOAD_LINK_URL : "+soundDownloadLinkUrl);
							//String str = appManager.SERVICE_URL+"service_music_email.php?AppId="+appManager.getAPP_ID()+"&deviceid="+tManager.getDeviceId()+"&MusicTrackId="+albumSongArrayList.get(position).getNoofplay();
							SharedPreferences settings = getSharedPreferences("MailPref"+appManager.getAPP_ID(), MODE_PRIVATE);
				        	if(!settings.getBoolean("MailContact", false))
				        	{
				        		handler.sendEmptyMessage(DIALOG_ALERT);
				        	}else{
				        		try{showDialog(appManager.PROGRESS);}catch (Exception e) {e.printStackTrace();}
				        		new MyDownloadTask3().execute(soundDownloadLinkUrl);
				        	}
						}
						else if(!((TextView) v.findViewById(R.id.buyText)).getText().toString().equalsIgnoreCase(""))
						{
							mGaTracker.sendView("Music_Download_"+albumSongArrayList.get(position).getNoofplay());
							if(!albumSongArrayList.get(position).getSource().equalsIgnoreCase("iTunes"))
							{
								SnapLionMyAppActivity.KillFlag = false;
								Intent i = new Intent(getApplicationContext(), SLNewsURLActivity.class);
								try{
									Log.v("url buy music", albumSongArrayList.get(position).getBuyurl());
								}catch (Exception e) {e.printStackTrace();}
								i.putExtra("ShowURL", albumSongArrayList.get(position).getBuyurl());
								i.putExtra("CatName", catName);
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(i);
							}
						}
					}
				});
			}catch (Exception e) {e.printStackTrace();}
			
			return convertView;
		}

	}

	private class MyDownloadTask3 extends AsyncTask<String, Void, InputStream>
	{
		@Override
		protected void onPostExecute(InputStream result) 
		{
			String str = getDownLoadDetail(result);

			try{dismissDialog(appManager.PROGRESS);}catch (Exception e) {e.printStackTrace();}
			Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
		}

		@Override
		protected InputStream doInBackground(String... params) 
		{
			InputStream in = null;
			try 
			{
				in = HttpConnection.connect(params[0]);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return in;
		}
	}
	
	public static void RefreshAdapter(Context context) 
	{
		mContext = context;
		adapter.notifyDataSetChanged();
	}

	
	public String getDownLoadDetail(InputStream in) 
	{
		final int result = 1;
		final int record = 2;
		final int answer = 3;
		
		int tagName = 0;
		String value = null;

		try 
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, HTTP.UTF_8);

			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) 
			{
				if (eventType == XmlPullParser.START_TAG) 
				{
					if (parser.getName().equals("result")) 
					{
						tagName = result;
					}
					else if (parser.getName().equals("record")) 
					{
						tagName = record;
					}
					else if (parser.getName().equals("answer")) 
					{
						tagName = answer;
					}
				}
				if (eventType == XmlPullParser.TEXT) 
				{
					switch (tagName) 
					{
						case result:
							break;
						case record:
							break;
						case answer:
							value = parser.getText();
							break;
						default:
							break;
					}
					tagName = 0;
				}

				if (eventType == XmlPullParser.END_TAG) 
				{
					if (parser.getName().equals("result")) 
					{
					}
				}
				eventType = parser.next();
			}
		} 
		catch (XmlPullParserException e) 
		{
			updateFlag=false;
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			updateFlag=false;
			e.printStackTrace();
		}
		return value;
	}

	class ImageAdapter extends BaseAdapter {
        public int getCount() {
            if (albumDetails != null) {
                return albumDetails.size();
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

        public View getView(int position, View convertView, ViewGroup arg2) {
            if (convertView == null) {
            	convertView =  getLayoutInflater().inflate(R.layout.album_cell_view_list, null);
            }
            TextView tv = (TextView)convertView.findViewById(R.id.album_cell_txt_name);
            try{
            	if(album_track == position){
            		tv.setText(albumDetails.get(position));
            		tv.setTextColor(Color.WHITE);
            		tv.setTypeface(null, Typeface.BOLD);
            		tv.setTypeface(appManager.lucida_grande_regular);
            	}else{
            		tv.setText(albumDetails.get(position));
            		tv.setTypeface(appManager.lucida_grande_regular);
            	}
        		convertView.setPadding(2, 2, 15, 2);
            }catch (Exception e) {e.printStackTrace();}
            return convertView;
        }
    }
	
	@Override
	protected void onPause() {
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
	    boolean isScreenOn = powerManager.isScreenOn();
	    if (!isScreenOn) {
	    	SnapLionMyAppActivity.KillFlag = false;
	    }
		if(SnapLionMyAppActivity.KillFlag)
		{
			try{
				Utility.killMyApp(getApplicationContext(),SLMusicActivity.this);
			}catch (Exception e) {e.printStackTrace();}
		}
		super.onPause();
	}
	
	@Override
	public void onBackPressed() {
		SnapLionMyAppActivity.KillFlag = false;
		super.onBackPressed();
	}
	@Override
	protected void onStart() 
	{
		super.onStart();
		mGaTracker.sendView("Music_Screen");
	}
}

