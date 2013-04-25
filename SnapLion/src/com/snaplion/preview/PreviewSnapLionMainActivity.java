package com.snaplion.preview;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.snaplion.beans.AppDetail;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.music.SLMusicPlayerActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

public class PreviewSnapLionMainActivity extends AppSuperActivity {
    protected static final String TAG = "SnapLionMainActivity";
    static ArrayList<AppDetail> appDetails = new ArrayList<AppDetail>();
    static HashMap<String,String> userInfo = new HashMap<String, String>();
    private  EditText usetEt = null;
    private EditText pwdEt = null;
	private final String EMAIL_OF_SENDER = "snaplion2011@gmail.com";
	private Button loginBtn = null;
	private ProgressDialog progDialog;
	AppManager appManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        	setContentView(R.layout.preview_main);
        }catch (OutOfMemoryError e) {e.printStackTrace();System.gc();}
        appManager = AppManager.getInstance();
        PreviewSnapLionAppsSelectActvity.KillFlag = true;
        
 		// shrikant
		// variable initialization
		SharedPreferences settings = getSharedPreferences("SLUpdate"
				+ appManager.getAPP_ID(), MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("isAppJustStarted", true);
		editor.putBoolean("isPauseMusic", true);
		editor.putBoolean("checkAppVersion", true);
		editor.commit();
		
		// Version check for Preview app wiil be done on login screen.
		if (getSharedPreferences("SLUpdate" + appManager.getAPP_ID(),
				MODE_PRIVATE).getBoolean("checkAppVersion", false)) {
			appManager.checkVersion(this);
			Editor edt = getSharedPreferences(
					"SLUpdate" + appManager.getAPP_ID(), MODE_PRIVATE).edit();
			edt.putBoolean("checkAppVersion", false);
			edt.commit();
		}
		// end
        
//        new Thread(new Runnable() {
//            public void run() {
//                registerDeviceForPushNotication();
//            }
//        }).start();
       
        progDialog = new ProgressDialog(this);
        progDialog.setMessage("Loading, Please wait...");
        progDialog.setCanceledOnTouchOutside(false);
        progDialog.setCancelable(true);
        
        usetEt = (EditText)findViewById(R.id.user_edit);
        pwdEt = (EditText)findViewById(R.id.pwd_edit);
			
        SharedPreferences myPref = getSharedPreferences("SLPref", MODE_PRIVATE);
        String userStr = myPref.getString("User", "");
        if(userStr != ""){
        	usetEt.setText(userStr);
        	pwdEt.setText(myPref.getString("Pwd", ""));
        }
        	
        
        Button resetBtn = (Button)findViewById(R.id.reset_btn1);
        	resetBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					usetEt.setText("");
					pwdEt.setText("");
				}
			});
        	
       loginBtn = (Button)findViewById(R.id.login_btn);
       loginBtn.requestFocus();

        	loginBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(usetEt.getText().toString().equalsIgnoreCase("")){
						Toast.makeText(getApplicationContext(), "Please enter user name.", Toast.LENGTH_SHORT).show();
					}else if(pwdEt.getText().toString().equalsIgnoreCase("")){
						Toast.makeText(getApplicationContext(), "Please enter password.", Toast.LENGTH_SHORT).show();
					}else{
				    	if(Utility.isOnline(getApplicationContext())){
				    		if(!progDialog.isShowing()){
								//showDialog(appManager.PROGRESS);
								progDialog.show();
								  new Thread(new Runnable() {
							            public void run() {
											getLoginRes();
							            }
							        }).start();
							}
				    	}else{
							Toast.makeText(getApplicationContext(), "Network not available.Please try later..!", Toast.LENGTH_SHORT).show();
				    	}
					}
				}
			});
    }

    protected void getLoginRes() {
    	String str = appManager.LOGIN_URL+usetEt.getText().toString()+"&Pwd="+pwdEt.getText().toString();
    	str = Utility.replace(str, " ", "%20");
		getLoginResponse(str);
	}

	Handler handler = new Handler() 
	{
	    @Override
	    public void handleMessage(Message msg) 
	    {
	        switch (msg.what) 
	        {
	        case AppManager.DISPLAY:
	        	try
	        	{
	        		SharedPreferences myPref = getSharedPreferences("SLPref", MODE_PRIVATE);
					String appData = myPref.getString("AppDetails", "");
					try 
					{
						InputStream in = new ByteArrayInputStream(appData.getBytes("UTF-8"));
						appDetails = getAppDetails(in);
					} 
					catch (UnsupportedEncodingException e) 
					{
						e.printStackTrace();
					}
					
	        		if(userInfo.get("answer").equals("Sucessful"))
	        		{
	        			SharedPreferences settings = getSharedPreferences("SLPref", MODE_PRIVATE);
               			SharedPreferences.Editor editor = settings.edit();
               			editor.putString("UserName", userInfo.get("username"));
               			editor.putString("UserID", userInfo.get("userid"));
               			editor.putString("User", usetEt.getText().toString());
               			editor.putString("Pwd", pwdEt.getText().toString());
               			editor.putString("Login_Flag", "Main");
               			editor.commit();
               			PreviewSnapLionAppsSelectActvity.KillFlag = false;
	        			//Intent i = new Intent(getApplicationContext(),PreviewSnapLionAppScreenActvity.class);
               	     Intent i = new Intent(getApplicationContext(),PreviewSnapLionAppsSelectActvity.class);
               	     
	        			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        			startActivity(i);
	        		}
	        		else
	        		{
	    				Toast.makeText(getApplicationContext(), "User Id or password is incorrect.", Toast.LENGTH_LONG).show();
	        		}
	        			
	        		progDialog.dismiss();
	        	}catch (Exception e) {e.printStackTrace();}
	        	break;
	        case AppManager.ERROR:
	        	try{
	        		progDialog.dismiss();
	        	}catch (Exception e) {e.printStackTrace();}
				Toast.makeText(getApplicationContext(), "Network Fail.Please try later .!", Toast.LENGTH_LONG).show();
				break;
	        }
	    }
	};
	
	protected void getLoginResponse(String url) {
			InputStream in = null;
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				Utility.debug("preview app login url :"+url); 
				HttpGet httpget = new HttpGet(url);
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
						HttpEntity httpEntity = response.getEntity();
						in = httpEntity.getContent(); 
						BufferedReader br = new BufferedReader(new InputStreamReader(in));
	                        StringBuilder sb = new StringBuilder();
	                   		String line;
	                   		while ((line = br.readLine()) != null) {
	                   			sb.append(line);
	                   		}
	                   		SharedPreferences settings = getSharedPreferences("SLPref", MODE_PRIVATE);
                   			SharedPreferences.Editor editor = settings.edit();
                   			editor.putString("AppDetails", sb.toString());
                   			editor.commit();
                   			Utility.debug("preview app login response: "+sb.toString());
                   }
				handler.sendEmptyMessage(appManager.DISPLAY);
			}catch (Exception e) {
				handler.sendEmptyMessage(appManager.ERROR);
			}
	}

	static public ArrayList<AppDetail> getAppDetails(InputStream in) 
	{
		final int answer  = 1;
	  	final int userid = 2;
        final int username = 3;
        final int applist = 4;
        final int appid = 5;
        final int picture = 6;
        final int appname = 7;
        final int picture2x = 8;
        final int picture512 = 9;

        int tagName = 0;

        ArrayList<AppDetail> values = new ArrayList<AppDetail>();
        AppDetail value = null;

        try 
        {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, HTTP.UTF_8);
			//System.out.println("xml : "+Utility.getString(in));
			//parser.setInput ( new StringReader ( Utility.getString(in) ) );
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) 
			{
				if (eventType == XmlPullParser.START_TAG) 
				{
					if (parser.getName().equals("answer")) 
					{
						tagName = answer;
					}
					else if (parser.getName().equals("userid")) 
					{
						tagName = userid;
					}
					else if (parser.getName().equals("username")) 
					{
						tagName = username;
					}
					else if (parser.getName().equals("applist")) 
					{
						tagName = applist;
						value = new AppDetail();
					}
					else if (parser.getName().equals("appid")) 
					{
						tagName = appid;
					}
					else if (parser.getName().equals("picture")) 
					{
						tagName = picture;
					}
					else if (parser.getName().equals("picture2x")) 
					{
						tagName = picture2x;
					}
					else if (parser.getName().equals("picture512")) 
					{
						tagName = picture512;
					}
					else if (parser.getName().equals("appname")) 
					{
						tagName = appname;
					}
				}
				if (eventType == XmlPullParser.TEXT) 
				{
					switch (tagName) 
					{
						case answer:
							userInfo.put("answer", parser.getText());
							break;
						case userid:
							userInfo.put("userid", parser.getText());
							break;
						case username:
							userInfo.put("username", parser.getText());
							break;
						case applist:
							break;
						case appid:
							value.setAppid(parser.getText());
							break;
						case picture:
							value.setPicture(parser.getText());
							break;
						case picture2x:
							value.setPicture2x(parser.getText());
							break;
						case picture512:
							value.setPicture512(parser.getText());
							break;
						case appname:
							value.setAppname(parser.getText());
							break;
						default:
							break;
					}
					tagName = 0;
				}
				if (eventType == XmlPullParser.END_TAG) 
				{
                    if (parser.getName().equals("applist")) 
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
			e.printStackTrace();
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		}
		return values;
	}
	
//	private void registerDeviceForPushNotication()
//	{
//		Intent regIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
//        regIntent.putExtra("app",PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(), 0));
//        regIntent.putExtra("sender", EMAIL_OF_SENDER);
//        startService(regIntent);  
//	}
	

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
				Utility.killMyApp(getApplicationContext(),PreviewSnapLionMainActivity.this);
			}catch (Exception e) {e.printStackTrace();}
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		PreviewSnapLionAppsSelectActvity.KillFlag = true;
		super.onResume();
	}
	@Override
	public void onBackPressed() {
		PreviewSnapLionAppsSelectActvity.KillFlag = false;
		super.onBackPressed();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		switch (keyCode) {
		case KeyEvent.KEYCODE_CAMERA:
			try {

			} catch (Exception e) {
				// TODO: handle exception
			}
			//finish();
			return true;
		case KeyEvent.KEYCODE_1:
			try {

			} catch (Exception e) {
				// TODO: handle exception
			}
			//finish();
			return true;

		case KeyEvent.KEYCODE_BACK:
			//finish();
			try{
				finish();
			//	moveTaskToBack(true);
				System.gc();
				try{SLMusicPlayerActivity.closePlayer(getApplicationContext());}catch (Exception e) {e.printStackTrace();}
				android.os.Process.killProcess(android.os.Process.myPid());
			}catch (Exception e) {e.printStackTrace();}
			return true;
		}
		return false;
	}
}