package com.snaplion.videos;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
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

import com.snaplion.beans.Video;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.music.SLMusicPlayerActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;


public class SLVideoDetailActivity extends AppSuperActivity {
	@Override
	protected void onResume() {
		SnapLionMyAppActivity.KillFlag = true;
		pauseFlag = false; 
		super.onResume();
	}

	private ImageView youTube;
	private TextView descriptionText;
	LinearLayout bgImage;
	RelativeLayout videoBg;
	private  ArrayList<Video> videoArrayDetails = new ArrayList<Video>();
	private ArrayList<String> comList = new ArrayList<String>();
	private LinearLayout topLayout = null;
	private String videoUrl = null;
	private String id = null;
	private int positionUrl = 0;
	private String descriptionStr = null;
	private String catName = null;
	private String videoTitle = null;
	private boolean pauseFlag = false; 
	AppManager appManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		try
		{
			setContentView(R.layout.video_detail);
		}
		catch (OutOfMemoryError e) 
		{
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		SnapLionMyAppActivity.KillFlag = true;
		appManager=AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		videoArrayDetails = SLVideoActivity.videoArrayList;
		
		Intent i = getIntent();
		videoUrl = i.getStringExtra("videoUrl");
		id = i.getStringExtra("id");
		descriptionStr = i.getStringExtra("description");
		positionUrl = i.getIntExtra("Position", 0);
		catName = i.getStringExtra("catName");
		videoTitle = i.getStringExtra("Title");
		
		
		TextView tv = (TextView)findViewById(R.id.video_detail_title);
		try{
			if(videoTitle != null)
				tv.setText(videoTitle);
			tv.setTypeface(appManager.lucida_grande_regular);
		}catch (Exception e) {e.printStackTrace();}

		topLayout = (LinearLayout)findViewById(R.id.video_detail_top_bar);
		com.snaplion.util.TopClass.getTopView(SLVideoDetailActivity.this,getApplicationContext(),topLayout,catName,"yes");

		bgImage = (LinearLayout) findViewById(R.id.video_detail_main_bg);
		
		descriptionText = (TextView)findViewById(R.id.video_discription);
		descriptionText.setTypeface(appManager.lucida_grande_regular);
		descriptionText.setMovementMethod(ScrollingMovementMethod.getInstance()); 
		descriptionText.setFocusable(false); // to disable link
		youTube = (ImageView) findViewById(R.id.video_play_restrict);
		
		videoBg = (RelativeLayout)findViewById(R.id.video_details_bg);
		BitmapDrawable background = null;
		try
		{
			background = new BitmapDrawable(videoArrayDetails.get(positionUrl).getBitmapSnap());
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		videoBg.setBackgroundDrawable(background);
		background = null;
		if(Utility.isOnline(getApplicationContext())) 
		{
			youTube.setBackgroundResource(R.drawable.play_youtube);
			youTube.setOnClickListener(new OnClickListener() 
			{
				public void onClick(View v) 
				{
					try
					{
						pauseFlag = true;
						try
						{
							SLMusicPlayerActivity.pausePlayer();
						}
						catch (Exception e) 
						{
							e.printStackTrace();
						}
						String[] extractUrl = videoUrl.split("=");
						String thumbnailId = extractUrl[1];
						System.out.println("YOU TUBE VIDEO URL : "+"vnd.youtube:"+thumbnailId);
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+thumbnailId)); 
						intent.putExtra("VIDEO_ID", thumbnailId); 
						startActivity(intent); 
					}
					catch (Exception e) {e.printStackTrace();}
				}
			});
		}else {
			youTube.setBackgroundResource(R.drawable.restrict_youtube);
		}

		((Button)findViewById(R.id.mailImage)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseFlag = true;
			    Intent i = new Intent(Intent.ACTION_SEND);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setType("text/plain");
				String s = "Video shared from "+appManager.getAPP_NAME()+" app";
				i.putExtra(android.content.Intent.EXTRA_SUBJECT, s);
				i.putExtra(android.content.Intent.EXTRA_TEXT, "Find the latest video '"+videoTitle+"'"+System.getProperty("line.separator")+videoUrl);
				startActivity(i);
			}
		});
		BitmapDrawable background1 = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("VideoBG"+appManager.getAPP_ID())));
		bgImage.setBackgroundDrawable(background1);
		background1 = null;
		handler.sendEmptyMessage(AppManager.getInstance().DISPLAY);
	}
	
	Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	        case AppManager.DISPLAY:
	        	try {
	        		descriptionText.setText(Html.fromHtml(descriptionStr));
	    		 } catch (Exception e1) {}	
	        	break;
	        case AppManager.ERROR:
				Toast.makeText(getApplicationContext(), "Network Fail.Please try later .!", Toast.LENGTH_LONG).show();
				break;
	        }
	    }
	};
	class ImageAdapter extends BaseAdapter {
        public int getCount() {
            if (comList != null) {
                return comList.size();
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
            	convertView =  getLayoutInflater().inflate(R.layout.video_comment_cell_view, null);
            }
			((TextView)convertView.findViewById(R.id.comment_v_txt)).setText(comList.get(position));
			convertView.setPadding(1, 1, 1, 2);
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
	    pauseFlag = !pauseFlag;
		if(SnapLionMyAppActivity.KillFlag && pauseFlag)
		{
			try{

				Utility.killMyApp(getApplicationContext(),SLVideoDetailActivity.this);
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
		mGaTracker.sendView("Videos_"+id);
	}
}
