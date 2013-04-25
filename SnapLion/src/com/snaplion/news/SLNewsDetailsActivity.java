package com.snaplion.news;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inmobi.androidsdk.IMAdView;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

public class SLNewsDetailsActivity extends AppSuperActivity{
	@Override
	protected void onResume() {
		pauseFlag = false;
		SnapLionMyAppActivity.KillFlag = true;
		super.onResume();
	}

	private String newsURL = null;
	private String imageId = null;
	private LinearLayout newsLayout = null;
	private ImageView newsImg = null;
	private String catId = null;
	private String showUrl = null;
	private String catName = null;
	private String newsContent = null;
	private String newsDate = null;
	private String startDate = null;
	private String eventCity = null;
	private String Venue = null;
	private String newsTitle = null;
	//private Typeface tf1 = null;
	private boolean pauseFlag = false; 
	AppManager appManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.news_sub);
		}catch (OutOfMemoryError e) {System.gc();setContentView(R.layout.news_sub);}
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		SnapLionMyAppActivity.KillFlag = true;
		Intent i = getIntent();
		newsURL = i.getStringExtra("NewsImg");
		imageId = i.getStringExtra("NewsId");
		catId = i.getStringExtra("CATID");
		showUrl = i.getStringExtra("ShowURL");
		catName = i.getStringExtra("NewsName");
		newsDate = i.getStringExtra("NewsDate");
		newsContent = i.getStringExtra("NewsContent");
		startDate = i.getStringExtra("startDate");
		newsTitle = i.getStringExtra("NewsTitle");
		
		try{
			eventCity = i.getStringExtra("EventCity").trim();
		}catch (Exception e) {eventCity = "";}
		try{
			Venue = i.getStringExtra("Venue").trim();
		}catch (Exception e) {Venue = "";}
		
	
		try{
			newsLayout = (LinearLayout)findViewById(R.id.news_sub_bg);
		  if(catName != null){
			  TextView tv = (TextView)findViewById(R.id.n_sub_name_txt);
				tv.setText(catName);
				tv.setTypeface(appManager.lucida_grande_regular);
		  }
		}catch (Exception e) {e.printStackTrace();}
		
		Button homeBtn = (Button)findViewById(R.id.n_home_btn);
		homeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					SnapLionMyAppActivity.KillFlag = false;
//					Intent i = new Intent(getApplicationContext(),SnapLionAppScreenActvity.class);
//    				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//    				startActivity(i);
    				finishFromChild(getParent());
				}
		});
		
		Button backBtn = (Button)findViewById(R.id.n_sub_back_btn);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SnapLionMyAppActivity.KillFlag = false;
				finishFromChild(getParent());
				System.gc();
			}
		});
		
		
		newsImg = (ImageView)findViewById(R.id.news_sub_img);
		 LinearLayout llShare = (LinearLayout)findViewById(R.id.news_share_layout);
			 llShare.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					pauseFlag = true;
					Bitmap bitmap = null;
					if(catId.equalsIgnoreCase("1008"))
		            	bitmap = Utility.FetchImage("Events"+imageId);
	            	else
		            	bitmap = Utility.FetchImage("News"+imageId);
    				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    				if(bitmap != null)
    					bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
    				
    			    File f = new File(Environment.getExternalStorageDirectory()+ File.separator +catName+".jpg");
    				 try {
    					    f.createNewFile();
    					    FileOutputStream fo = new FileOutputStream(f);
    					    fo.write(bytes.toByteArray());
    					  } catch (Exception e) {e.printStackTrace();}
    				
    			    Intent i = new Intent(Intent.ACTION_SEND);
    				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    				i.setType("image/jpg");
    				String str = null;
    				if(catId.equalsIgnoreCase("1008"))
    					str = "Event shared from "+appManager.getAPP_NAME()+" app";
    				else
    					str = "News shared from "+appManager.getAPP_NAME()+" app";
    				i .putExtra(android.content.Intent.EXTRA_SUBJECT, str);
    				String emailBody=newsTitle;
    				
    				if(catId.equalsIgnoreCase("1007"))
    				{
    					emailBody+=System.getProperty("line.separator")+"Published on: "+newsDate;
    				}
    				else
    				{
    					emailBody+=System.getProperty("line.separator")+"Start Date: "+newsDate;
    					emailBody+=System.getProperty("line.separator")+"End Date: "+startDate;
    				}
    				
    				if(Venue!=null && Venue.trim().length()>0)
    					emailBody+=System.getProperty("line.separator")+"Venue: "+Venue;
    				emailBody+=System.getProperty("line.separator")+System.getProperty("line.separator")+newsContent;		
    				i .putExtra(android.content.Intent.EXTRA_TEXT,emailBody);
    				i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/"+catName+".jpg"));
    				startActivity(i);
    				bitmap = null;
				}
			});
			
			LinearLayout buyLayout =  (LinearLayout)findViewById(R.id.news_buy_tkt_layout);
			TextView tv = (TextView)findViewById(R.id.buy_tkt_url_btn);
			if(catId.equalsIgnoreCase("1008"))
				tv.setText("BUY TICKETS");
			else
				tv.setText("DETAILS");
			tv.setTypeface(appManager.lucida_grande_regular);
			
			try{
				if(showUrl == null || showUrl.equalsIgnoreCase(" ,  ") ||  showUrl.length()<6 ){
					buyLayout.setVisibility(View.GONE);
					llShare.setBackgroundResource(R.drawable.mail);
				}else{
					llShare.setBackgroundResource(R.drawable.black_left_bar);
					((Button)findViewById(R.id.n_mail_button)).setBackgroundResource(R.drawable.mail_img);
				}
			}catch (Exception e) {e.printStackTrace();}
				
			buyLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SnapLionMyAppActivity.KillFlag = false;
					Intent i = new Intent(getApplicationContext(), SLNewsURLActivity.class);
					i.putExtra("ShowURL", showUrl);
					i.putExtra("CatName", catName);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}
			});
			
			handler.sendEmptyMessage(appManager.DISPLAY);
	}

	Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	        case AppManager.DISPLAY:
	    		TextView tv2 = (TextView)findViewById(R.id.news_city);
	    		TextView tv3 = (TextView)findViewById(R.id.news_venue);
	    		
	    		if(catId.equalsIgnoreCase("1008")){
	    			((LinearLayout)findViewById(R.id.ev_etxt_layout)).setVisibility(View.VISIBLE);
	    			((LinearLayout)findViewById(R.id.ev_stxt_layout)).setVisibility(View.VISIBLE);
	    			TextView tv6 = (TextView)findViewById(R.id.news_edate);
	    			tv6.setText(startDate);
	    			tv6.setTypeface(appManager.lucida_grande_regular);
	    			TextView tv7 = (TextView)findViewById(R.id.news_sdate);
	    			tv7.setText(newsDate);
	    			tv7.setTypeface(appManager.lucida_grande_regular);
	    			if(!eventCity.equalsIgnoreCase("") && eventCity.length()>2){
	    				tv2.setVisibility(View.VISIBLE);
	    				tv2.setText(eventCity);
	    				tv2.setTypeface(appManager.lucida_grande_regular);
	    			}
	    			if(!Venue.equalsIgnoreCase("")){
	    				tv3.setVisibility(View.VISIBLE);
	    				tv3.setText("Venue: "+Venue);	
	    				tv3.setTypeface(appManager.lucida_grande_regular);
	    			}
	    			BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(SLNewsDetailsActivity.this, SLMyAppSplashActivity.FetchImage("EventsBg"+appManager.getAPP_ID())));
	    			newsLayout.setBackgroundDrawable(background);
	    			background = null;
	        	}else{
	        		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(SLNewsDetailsActivity.this, SLMyAppSplashActivity.FetchImage("NewsBg"+appManager.getAPP_ID())));
	        		newsLayout.setBackgroundDrawable(background);
	    			background = null;
		    		TextView tv5 = (TextView)findViewById(R.id.news_date);
		    		tv5.setVisibility(View.VISIBLE);
	    			tv5.setText(Html.fromHtml(newsDate));
	    			tv5.setTypeface(appManager.lucida_grande_regular);
	        	}
	    		
	    		TextView tv4 = (TextView)findViewById(R.id.news_sub_headers);
	    			tv4.setText(newsTitle);
	    			tv4.setTypeface(appManager.lucida_grande_bold);
	    		TextView tv6 = (TextView)findViewById(R.id.news_sub_content_txt);
	    			tv6.setText(Html.fromHtml(newsContent));
	    			tv6.setTypeface(appManager.lucida_grande_regular);

	    		Bitmap bitmap = null;
	    	    try {
	    	    	System.out.println("catId : "+catId);
	    	    	if(catId.equalsIgnoreCase("1008"))
		            	bitmap = Utility.FetchImage("Events"+imageId);
	            	else
		            	bitmap = Utility.FetchImage("News"+imageId);
	            	
	            	if(bitmap != null){
	            		newsImg.setVisibility(View.VISIBLE);
	            		newsImg.setImageBitmap(bitmap);
	            	}else{
	            		newsImg.setVisibility(View.GONE);
	            	}
	            	bitmap = null;
	            //	Runtime.getRuntime().gc();
	    			handler.sendEmptyMessage(appManager.PROGRESS);
	              }catch (Exception e) {e.printStackTrace();}
	        	break;
	        case AppManager.PROGRESS:
	        	try {
	        		if(Utility.isOnline(getApplicationContext()))
	        			new MyDownloadTask().execute(newsURL);
	    		 } catch (Exception e1) {e1.printStackTrace();}	
	        	break;
	        case AppManager.ERROR:
				Toast.makeText(getApplicationContext(), "Network Fail.Please try later .!", Toast.LENGTH_LONG).show();
				break;
	        }
	    }
	};

	
	private class MyDownloadTask extends AsyncTask<String, Integer, InputStream> {
		@Override
		protected void onPostExecute(InputStream result) {
			Bitmap bitmap = null;
    	    try {
    	    	if(catId.equalsIgnoreCase("1008"))
	            	bitmap = Utility.FetchImage("Events"+imageId);
            	else
	            	bitmap = Utility.FetchImage("News"+imageId);
            	
            	if(bitmap != null){
            		newsImg.setVisibility(View.VISIBLE);
            		newsImg.setImageBitmap(bitmap);
            	}else{
            		newsImg.setVisibility(View.GONE);
            	}
            	bitmap = null;
              }catch (Exception e) {e.printStackTrace();}

			super.onPostExecute(result);
		}

		@Override
		protected InputStream doInBackground(String... params) {
			URL url = null;
			try {
				url = new URL(Utility.replace(params[0], " ", "%20"));
			} catch (MalformedURLException e1) {e1.printStackTrace();}
	        
	        try {
	            URLConnection connection = url.openConnection();
	            connection.connect();
	            InputStream is = connection.getInputStream();
	            
	            if(catId.equalsIgnoreCase("1008"))
	            	Utility.SaveImageSDCard(is, "Events"+imageId);
	        	else
	            	Utility.SaveImageSDCard(is, "News"+imageId);
	            is = null;
	            url = null;

	        } catch (Exception e) {e.printStackTrace();}
			return null;
		}
	}
	@Override
	public void onBackPressed() {
		SnapLionMyAppActivity.KillFlag = false;
		super.onBackPressed();
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
				Utility.killMyApp(getApplicationContext(),SLNewsDetailsActivity.this);
			}catch (Exception e) {e.printStackTrace();}
		}
		super.onPause();
	}
	@Override
	protected void onStart() 
	{
		super.onStart();
		if(catId.equalsIgnoreCase("1007"))//news
		{
			mGaTracker.sendView("News_"+imageId);
		}
		else//event
		{
			mGaTracker.sendView("Event_"+imageId);
		}
	}
}
