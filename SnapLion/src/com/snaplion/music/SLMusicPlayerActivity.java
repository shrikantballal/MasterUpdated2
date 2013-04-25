package com.snaplion.music;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.snaplion.beans.AlbumSong;
import com.snaplion.kingsxi.R;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;



public class SLMusicPlayerActivity 
{
	private static String TAG="SLMusicPlayerActivity";
	private static final int PLAY_AUDIO = 6;
	private static final int UPDATE_VIEW=8;
	static Context context;
	static Activity activity;
	static TextView trackname = null;
	public static MediaPlayer mediaPlayer;
	static int trackDuration =0;
	public static ImageButton playBtn = null;
	public static SeekBar seekBarProgress;
	static View view = null;
	static View view1 = null;
	static int second = 0;
	static int count = 0;
	static String trackUrl = null;
	static ImageButton playFwdBtn = null;
	static ImageView closeBtn = null;
	static ImageView playerShowBtn = null;
	static Animation raghtAnimation;
	static Animation leftAnimation;
	static Animation mAnimation;
	static String APPID = null;
	private static ArrayList<AlbumSong> albumList = new ArrayList<AlbumSong>();
	static int songPosition = 0;
	static boolean playOn=true;
	public static int seekbarPosition;
	static boolean firstTimeSong;
	static String commonUrl;
	private static int playPositionInMillisecconds;
	static int acc = 0;
	private static boolean M4ACOUNTER = false;
	static protected boolean playFlag = false;
	AppManager appManager;
	public SLMusicPlayerActivity()
	{
		appManager= AppManager.getInstance();
//		if(appManager.getAPP_ID().equalsIgnoreCase("222"))
//		{
//			playFlag = true;
//		}
	}
	
	public static void getPlayer(Activity activityClass,Context applicationContext, final LinearLayout playerLayout,String appId)
	{  
		context = applicationContext;
		activity = activityClass;
		view =  activity.getLayoutInflater().inflate(R.layout.player, null);
		view1 =  activity.getLayoutInflater().inflate(R.layout.btn, null);
		
		APPID = appId;
		trackname = (TextView)view.findViewById(R.id.player_track_name);
		trackname.setTypeface(AppManager.getInstance().lucida_grande_regular);
	    SharedPreferences settings = context.getSharedPreferences("MusicFlag"+APPID, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("key", true);
		editor.commit();

		playerShowBtn = (ImageView)view1.findViewById(R.id.audio_player_show_btn);
		closeBtn = (ImageView)view.findViewById(R.id.player_close_btn);
		closeBtn.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				SharedPreferences myPref1 = context.getSharedPreferences("MusicFlag"+APPID, Context.MODE_PRIVATE);
				Boolean resStr = myPref1.getBoolean("key", true);
				mediaPlayerShowIn();
				view.setVisibility(View.GONE);
      		 	
				if(resStr)
				{
					SharedPreferences settings = context.getSharedPreferences("MusicFlag"+APPID, Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean("key", false);
					editor.commit();
					playerLayout.addView(view1);
				}
				else
				{
					view1.setVisibility(View.VISIBLE);
				}
			}
		});
		
		playerShowBtn.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				view.setVisibility(View.VISIBLE);
				mediaPlayerShowOutSide();
				view1.setVisibility(View.GONE);
			}
		});
		
		playBtn = (ImageButton)view.findViewById(R.id.player_play_btn);
		playBtn.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				if(Utility.isOnline(context))
				{
					try
					{
						TextView txtTimer = (TextView)view.findViewById(R.id.player_timer);
						//if(!txtTimer.getText().toString().equalsIgnoreCase("00:00"))
						//{
							if(playFlag) 
							{
								playFlag = false;
								if (!mediaPlayer.isPlaying()) 
								{
									mediaPlayer.start();
									playBtn.setImageResource(R.drawable.mp_pause);
								}
							}
							else 
							{
								playFlag = true;
								if (mediaPlayer.isPlaying())
								{
									mediaPlayer.pause();
								}
								playBtn.setImageResource(R.drawable.mp_play);
							}
						//}
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
				else
				{
					Toast.makeText(context,"Network Fail.Please try later .!", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		playFwdBtn = (ImageButton)view.findViewById(R.id.player_forword_btn);
		playFwdBtn.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				if(Utility.isOnline(context))
				{
					try 
					{
						try
						{
							closePlayer1(context);
						}
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					
						if(albumList != null && M4ACOUNTER)
						{
							TextView txtTimer = (TextView)view.findViewById(R.id.player_timer);
							txtTimer.setText(getAsTime(0));
							try
							{
								seekBarProgress.setSecondaryProgress(0);
								seekBarProgress.setProgress(0);
							}
							catch (Exception e) 
							{
								e.printStackTrace();
							}
							
							songPosition = songPosition + 1;
							
							if(songPosition == albumList.size())
							{
								songPosition = 0;
							}
							
							if(albumList.size() > songPosition)
							{
								trackname.setText(albumList.get(songPosition).getTrackname());
								SLMusicActivity.SongId = albumList.get(songPosition).getNoofplay();
								
								try
								{
									if(com.snaplion.music.SLMusicActivity.music && albumList.size()>0)
									{
										SLMusicActivity.RefreshAdapter(context);
									}
								}
								catch (Exception e) 
								{
									e.printStackTrace();
								}
								new Thread(new Runnable() 
								{
									public void run() 
									{
										nextSong();
									}
								}).start();
								playBtn.setImageResource(R.drawable.mp_pause);
								playFlag = false;
							}
						}
						else
						{
							Toast.makeText(context,"Audio file format not supported by this device.", Toast.LENGTH_SHORT).show();
						}
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
				else
				{
					handler.sendEmptyMessage(AppManager.getInstance().ERROR);
				}
			}
		});
		
		
		if(com.snaplion.myapp.SnapLionMyAppActivity.homeMusic)
		{
		  try
		  {
			acc = 0;
			SharedPreferences myPref1 = context.getSharedPreferences("MusicPref"+APPID,Context.MODE_PRIVATE);
			String resStr = myPref1.getString("AlbumSong"+"All", "");
			
			if(resStr != "" && com.snaplion.myapp.SnapLionMyAppActivity.homeMusic)
			{
				 com.snaplion.myapp.SnapLionMyAppActivity.homeMusic = false;
				 SharedPreferences s1 = context.getSharedPreferences("MusicFlag"+APPID, Context.MODE_PRIVATE);
				 if(s1.getBoolean("lastPlay", false))
				 {
					 songPosition = s1.getInt("PlayNo", 0);
				 }
				 else
				 {
					songPosition = 0;
				 }

				albumList = com.snaplion.music.SLMusicActivity.getAlbumSongs(resStr);
				
				if(albumList != null)
				{
					trackname.setText(albumList.get(songPosition).getTrackname());
				}

				SLMusicActivity.SongId = albumList.get(songPosition).getNoofplay();
				M4ACOUNTER = getM4aDetails(albumList);
				if(M4ACOUNTER)
				{
					playFlag = false;
					if(Utility.isOnline(context) )
					{
				 		new Thread(new Runnable() 
				 		{
							public void run() 
							{
								nextSong();
							}
						}).start();  
				 	}
					else
				 	{
				 		handler.sendEmptyMessage(AppManager.getInstance().ERROR);
				 	}
				}
			}
			else
			{
				songPosition = 0;
				playBtn.setImageResource(R.drawable.mp_pause);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		}
		else
		{
			try
			{
				try
				{
					if(com.snaplion.music.SLMusicActivity.music && albumList.size()>0)
					{
						SLMusicActivity.RefreshAdapter(context);
					}
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
				trackname.setText(albumList.get(songPosition).getTrackname());
				acc = 1;
				initView(context);
				((TextView)view.findViewById(R.id.player_timer)).setText(getAsTime(mediaPlayer.getCurrentPosition()));
				seekBarProgress.setSecondaryProgress(seekbarPosition);

			}catch (Exception e) {e.printStackTrace();}
		}
		Log.d(TAG, "Player Flag ="+playFlag);
		
		if(playFlag) {
			playBtn.setImageResource(R.drawable.mp_play);
		}else {
			playBtn.setImageResource(R.drawable.mp_pause);
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		playerLayout.addView(view,params);
	}
	

	private static boolean getM4aDetails(ArrayList<AlbumSong> albumList2) 
	{
		boolean m4aflag = false;
		for(int i=0;i<albumList2.size();i++)
		{
			if(!albumList2.get(i).getSource().equals("iTunes"))
			{
				return m4aflag = true;
			}
		}
		return m4aflag;
	}


	static Handler handler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
			case AppManager.ERROR:
				try
				{
					((TextView)view.findViewById(R.id.player_timer)).setText(getAsTime(0));
					playBtn.setImageResource(R.drawable.mp_play);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				Toast.makeText(context,"Network Fail.Please try later .!", Toast.LENGTH_LONG).show();
				break;
			case PLAY_AUDIO:
				try
				{
					playMusic( commonUrl);
	        	}catch (Exception e) 
	        	{
	        		e.printStackTrace();
	        	}
				break;
			case UPDATE_VIEW:
				trackname.setText(albumList.get(songPosition).getTrackname());
				if(com.snaplion.music.SLMusicActivity.music && albumList.size()>0)
				{
					SLMusicActivity.RefreshAdapter(context);
				}
				break;
			}
		}
	};
	
	public static void playMusic( String url) 
	{
        Log.d(TAG, "inside playMusic() with url="+ url);
		try 
		{
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setDataSource(url); // setup
			//mediaPlayer.prepareAsync();
			mediaPlayer.prepare();
			
			trackDuration = mediaPlayer.getDuration(); // gets
			//if(!AppManager.getInstance().getAPP_ID().equalsIgnoreCase("222"))
			//{
				mediaPlayer.start();
			//}
			
//			if(AppManager.getInstance().getAPP_ID().equalsIgnoreCase("222"))
//			{
//				if(context.getSharedPreferences("SLUpdate"+AppManager.getInstance().getAPP_ID(), context.MODE_PRIVATE).getBoolean("isAppJustStarted", false))
//				{
//					playFlag = true;
//					Editor edt = context.getSharedPreferences("SLUpdate"+AppManager.getInstance().getAPP_ID(), context.MODE_PRIVATE).edit();
//					edt.putBoolean("isAppJustStarted", false);
//					edt.commit();
//				}
//			}
		} 
		catch (Exception ee) 
		{
			Log.d(TAG, "playMusic():: Exception to play", ee);
		}
		try
		{
			primarySeekBarProgressUpdater();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}	
	}
	
	private static void primarySeekBarProgressUpdater() 
	{
		try
		{
			seekBarProgress.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / trackDuration) * 100)); // This
			if (mediaPlayer.isPlaying()) 
			{
				Runnable notification = new Runnable() 
				{
					public void run() 
					{
						try
						{
							primarySeekBarProgressUpdater();
						}
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
				};
				handler.postDelayed(notification, 1000);
			}
		}
		catch (Exception e) 
		{
			// TODO: handle exception
		}
	}

	private static void initView(Context context2) 
	{
		seekBarProgress = (SeekBar)view.findViewById(R.id.player_seekbar);
		seekBarProgress.setMax(99); // It means 100% .0-99
		if(acc == 0)
		{
			mediaPlayer = new MediaPlayer();
		}
		seekBarProgress.setOnTouchListener(new OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event) 
			{
				if (v.getId() == R.id.player_seekbar) 
				{
					/**
					 * Seekbar onTouch event handler. Method which seeks MediaPlayer to
					 * seekBar primary progress position
					 */
					try
					{
						if (mediaPlayer.isPlaying()) 
						{
							seekBarProgress = (SeekBar)view.findViewById(R.id.player_seekbar);	
							playPositionInMillisecconds = (trackDuration/100)* seekBarProgress.getProgress();
							mediaPlayer.seekTo(playPositionInMillisecconds);
						}
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
				return false;
			}
		});
	 
		mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() 
		{
			public void onBufferingUpdate(MediaPlayer mp, int percent) 
			{
				try
				{
					TextView tv = (TextView)view.findViewById(R.id.player_timer);
					tv.setText(getAsTime(trackDuration-mediaPlayer.getCurrentPosition()));
					tv.setTypeface(AppManager.getInstance().lucida_grande_regular);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				seekbarPosition = percent;
				
				if(!((TextView)view.findViewById(R.id.player_timer)).getText().toString().equalsIgnoreCase(getAsTime(0)))
  			    trackname = (TextView)view.findViewById(R.id.player_track_name);
				seekBarProgress = (SeekBar)view.findViewById(R.id.player_seekbar);	

				seekBarProgress.setSecondaryProgress(percent);
				
				try
				{
					primarySeekBarProgressUpdater();
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() 
		{
			public void onCompletion(MediaPlayer mp) 
			{
				((TextView)view.findViewById(R.id.player_timer)).setText(getAsTime(0));
				try
				{
					seekBarProgress.setSecondaryProgress(0);
					seekBarProgress.setProgress(0);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
				songPosition = songPosition + 1;
				try
				{
					if(albumList.size()<=songPosition && M4ACOUNTER) 
					{
						songPosition = 0;
					}
				  
				if(albumList.size() > songPosition)
				{
					trackname.setText(albumList.get(songPosition).getTrackname());

					SLMusicActivity.SongId = albumList.get(songPosition).getNoofplay();
					if(com.snaplion.music.SLMusicActivity.music && albumList.size()>0)
					{
						SLMusicActivity.RefreshAdapter(context);
					}
					
					if(Utility.isOnline(context))
					{
						new Thread(new Runnable() 
						{
							public void run() 
							{
							   nextSong();
							}
						}).start(); 
					}
					else
						handler.sendEmptyMessage(AppManager.getInstance().ERROR);
				}
			   }catch (Exception e) {e.printStackTrace();}
			}
		});
	}

	public static void nextSong() 
	{
     try
     {
		if (albumList.size() > songPosition && albumList.size()>0) 
		{
			try
			{
				closePlayer1(context);
			}
			catch (Exception e) 
			{
				Log.d(TAG, "inside nextSong() closePlayer1(context) exception");
			}
			
			setNonItunesPosition();
           if(songPosition!=-1) 
           {
        	   SLMusicActivity.SongId = albumList.get(songPosition).getNoofplay();
				handler.sendEmptyMessage(UPDATE_VIEW);
			trackDuration = 0;
			acc = 0;
			initView(context);
			String s  = albumList.get(songPosition).getUrl();
			commonUrl=s;
			playMusic(s);
           }
		}}catch (Exception e) {e.printStackTrace();
	  }
	}

	private static void setNonItunesPosition() 
	{
		int count=0;
		if(albumList!=null) 
		{
			int totalCont=albumList.size();
			if(totalCont<=songPosition) 
			{
				songPosition=0;
			}
			while(albumList.get(songPosition).getSource().equals("iTunes") &&  songPosition!=-1)
			{
				songPosition++;
				count++;
				if(songPosition>=totalCont) 
				{
					songPosition=0;
				}
				if(count>=totalCont) 
				{
					songPosition=-1;
				}
			}
		}
	}
	
	public static void ListSong(int position) 
	{
		songPosition = position;
		trackname.setText(albumList.get(songPosition).getTrackname());
		new Thread(new Runnable() 
		{
			public void run() 
			{
				nextSong();
			}
		}).start();
	}
	
	public static void listRefresh(ArrayList<AlbumSong> albumSongArrayList2, int position) {
		albumList = null;
		albumList = albumSongArrayList2;
		Log.d(TAG,"Item clicked with position="+position);
		songPosition = position;
		trackname.setText(albumList.get(songPosition).getTrackname());
		
		try{closePlayer1(context);}catch (Exception e) {e.printStackTrace();}
		((TextView)view.findViewById(R.id.player_timer)).setText(getAsTime(0));
		
		try
		{
			seekBarProgress.setSecondaryProgress(0);
			seekBarProgress.setProgress(0);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		playBtn.setImageResource(R.drawable.mp_pause);
		playFlag = false;
		
			new Thread(new Runnable() {
				public void run() {
					nextSong();
				}
			}).start();
	}
	public static void closePlayer1(Context context2) {
		try
		{
			if(mediaPlayer!=null)
			{
				mediaPlayer.stop();
				mediaPlayer.release();
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			//closePlayer1(context2);
		}
	}
	
	public static void closePlayer(Context context2) {
		try{
			 SharedPreferences settings = context2.getSharedPreferences("MusicFlag"+APPID, Context.MODE_PRIVATE);
			 SharedPreferences.Editor editor = settings.edit();
			 editor.putInt("PlayNo", songPosition);
			 editor.putBoolean("lastPlay", true);
			 editor.commit();
			 mediaPlayer.stop();
			 mediaPlayer.release();
		}catch (Exception e) {e.printStackTrace();}
	}
	
	public static void pausePlayer() {
		playFlag = true;
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			playBtn.setImageResource(R.drawable.mp_play);
		}
	}
	
	protected static String getAsTime(int t) {
		if(t==0)
			return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toSeconds(t) / 60, TimeUnit.MILLISECONDS.toSeconds(t) - TimeUnit.MILLISECONDS.toSeconds(t) / 60* 60);
		else
			return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toSeconds(t) / 60, TimeUnit.MILLISECONDS.toSeconds(t) - TimeUnit.MILLISECONDS.toSeconds(t) / 60* 60);
	}
	
	/**
	 * Method which updates the SeekBar primary progress by current song playing
	 * position
	 */
	
	public static void mediaPlayerShowIn() {
		/**
		 * Media player in and out Animation .
		 */
		int a = 480;
		Display display = activity.getWindowManager().getDefaultDisplay(); 
	 	int width = display.getWidth();
	 	
	 	//if(width > 480)
	 	//	a = 545;
	 	a = width;
	 	
		mAnimation = new TranslateAnimation(0, -a, 0, 0);
		mAnimation.setDuration(1000);
		mAnimation.setFillAfter(true);
		mAnimation.setRepeatCount(0);
		mAnimation.setRepeatMode(Animation.RESTART);
		view.setAnimation(mAnimation);
		//view.setVisibility(View.GONE);
		//mediaPlayerShowOut.setVisibility(View.VISIBLE);
	}

	public static void mediaPlayerShowOutSide() {
		/**
		 * Media player in and out Animation .
		 */
		int a = 480;
		Display display = activity.getWindowManager().getDefaultDisplay(); 
	 	int width = display.getWidth();
	 	if(width > 480)
	 		a = 545;
		mAnimation = new TranslateAnimation(-a, 0, 0, 0);
		mAnimation.setDuration(1000);
		mAnimation.setFillAfter(true);
		mAnimation.setRepeatCount(0);
		mAnimation.setRepeatMode(Animation.RESTART);
		view.setAnimation(mAnimation);
		view.setVisibility(View.VISIBLE);
	//	mediaPlayerShowOut.setVisibility(View.INVISIBLE);

	}

}
