package com.snaplion.preview;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.snaplion.beans.AppDetail;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

public class PreviewSnapLionAppsSelectActvity extends AppSuperActivity
{
	private ArrayList<AppDetail> appListDetails = new ArrayList<AppDetail>();
	public static Boolean KillFlag = true;
	AppManager appManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		PreviewSnapLionAppsSelectActvity.KillFlag = true;
		setContentView(R.layout.preview_app_layout);
		appManager = AppManager.getInstance();
		
		((TextView)findViewById(R.id.top_header_name)).setTypeface(appManager.lucida_grande_regular);
		SharedPreferences myPref = getSharedPreferences("SLPref", MODE_PRIVATE);
		TextView logOutTxt = (TextView)findViewById(R.id.logout_txt);
		logOutTxt.setText("Logout : "+myPref.getString("UserName", ""));
		logOutTxt.setTypeface(appManager.lucida_grande_regular);
		
		((RelativeLayout)findViewById(R.id.logout_ll)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PreviewSnapLionAppsSelectActvity.KillFlag = false;
				try{
					com.snaplion.music.SLMusicPlayerActivity.closePlayer(getApplicationContext());
				}catch (Exception e) {e.printStackTrace();}
				finish();
				try{System.gc();}catch (Exception e) {e.printStackTrace();}
			}
		});
		
//		gridApp = (GridView)findViewById(R.id.grid_app_list);
//		gridApp.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
//				CopyOfPreviewSnapLionAppsSelectActvity.KillFlag = false;
//				
//						
//           		SharedPreferences settings = getSharedPreferences("SLPref"+appListDetails.get(arg2).getAppid(), MODE_PRIVATE);
//       			SharedPreferences.Editor editor = settings.edit();
//       			editor.putString("AppId", appListDetails.get(arg2).getAppid());
//       			editor.commit();
//				com.snaplion.myapp.SnapLionMyAppActivity.homeMusic = true;
//		        CopyOfPreviewSnapLionAppsSelectActvity.KillFlag = false;
//
//				Intent i = new Intent(CopyOfPreviewSnapLionAppsSelectActvity.this, SLMyAppSplashActivity.class);
//				appManager.setAPP_ID(appListDetails.get(arg2).getAppid());
//				appManager.setAPP_NAME(appListDetails.get(arg2).getAppname());
//				appManager.loadURL();
//				saveFanwallLoginPrefs(appListDetails.get(arg2));
//				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(i);
//			}
//		});
		
		//TextView tv = (TextView)findViewById(R.id.app_txt);
		//tv.setText("Any changes you make to your account on SnapLion.com will only reflect on your app when you relaunch it from this page");
		//tv.setTypeface(appManager.lucida_grande_regular);
		
		new DownloadAppDetails().execute();
	}
	class DownloadAppDetails extends AsyncTask<Void,Void,Void>
	{
		@Override
		protected Void doInBackground(Void... params) 
		{
			try
			{
				SharedPreferences myPref = getSharedPreferences("SLPref", MODE_PRIVATE);
	        	String appData = myPref.getString("AppDetails", "");
	    		InputStream in = new ByteArrayInputStream(appData.getBytes("UTF-8"));
	    		appListDetails = com.snaplion.preview.PreviewSnapLionMainActivity.getAppDetails(in);
	    		Utility.debug(appListDetails.toString());
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);
			if(appListDetails.size()>0)
			{
				AQuery aq=new AQuery(PreviewSnapLionAppsSelectActvity.this);
				final AppDetail appDetail = appListDetails.get(0);
				aq.id(R.id.appName).text(appDetail.getAppname());
				ImageOptions options = new ImageOptions();
				options.round = 50;
				aq.id(R.id.appIconImage).image(appDetail.getPicture512() ,options);
		    	aq.id(R.id.simulateButton).clicked(new OnClickListener() 
		    	{
					@Override
					public void onClick(View v) 
					{
						PreviewSnapLionAppsSelectActvity.KillFlag = false;
						SharedPreferences settings = getSharedPreferences("SLPref"+appDetail.getAppid(), MODE_PRIVATE);
		       			SharedPreferences.Editor editor = settings.edit();
		       			editor.putString("AppId", appDetail.getAppid());
		       			editor.putString("AppName", appDetail.getAppname());
		       			editor.commit();
						com.snaplion.myapp.SnapLionMyAppActivity.homeMusic = true;
				        PreviewSnapLionAppsSelectActvity.KillFlag = false;
		
						Intent i = new Intent(PreviewSnapLionAppsSelectActvity.this, SLMyAppSplashActivity.class);
						appManager.setAPP_ID(appDetail.getAppid());
						appManager.setAPP_NAME(appDetail.getAppname());
						appManager.loadURL();
						saveFanwallLoginPrefs(appDetail);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
					}
				});
			}
		}
	}
	private void saveFanwallLoginPrefs(AppDetail appDetail)
	{
		String fanid = getSharedPreferences("SLPref", MODE_PRIVATE).getString("UserID", null);
		String name = appDetail.getAppname();
		String email = getSharedPreferences("SLPref", MODE_PRIVATE).getString("UserName", null);
		
		SharedPreferences myPref = getSharedPreferences("FanwallRef"+appDetail.getAppid(), MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = myPref.edit();
		prefEditor.putString("twitterToken", null);
		prefEditor.putString("fanid", fanid);
		prefEditor.putString("username", name);
		prefEditor.putString("email", email);
		prefEditor.putString("twitterSecret", null);
		prefEditor.putString("logintype", "preview_app");
		prefEditor.putBoolean("isTwitterSignedIn", false);
		prefEditor.commit();
		
		SharedPreferences myPref2 = getSharedPreferences("FanwallRef"+appManager.getAPP_ID(), Activity.MODE_PRIVATE);
		Utility.debug("preview app login detail:"+myPref2.getString("fanid", null)+"=="+myPref2.getString("username", null)+"=="+myPref2.getString("logintype", null));
	}
	
	@Override
	protected void onPause() {
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
	    boolean isScreenOn = powerManager.isScreenOn();
	    if (!isScreenOn) {
	    	PreviewSnapLionAppsSelectActvity.KillFlag = false;
	    }
		if(PreviewSnapLionAppsSelectActvity.KillFlag)
		{
			try{
				Utility.killMyApp(getApplicationContext(),PreviewSnapLionAppsSelectActvity.this);
			}catch (Exception e) {e.printStackTrace();}
		}
		super.onPause();
	}
	@Override
	protected void onResume() {
		PreviewSnapLionAppsSelectActvity.KillFlag = true;
		super.onResume();
	}
}
