package com.snaplion.contact;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inmobi.androidsdk.IMAdView;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.music.SLMusicPlayerActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class SLContactActivity extends AppSuperActivity 
{
	@Override
	protected void onResume() 
	{
		SnapLionMyAppActivity.KillFlag = true;
		super.onResume();
	}

	//private String appId = null;
	private HashMap<String, String>	 contactDetails = new HashMap<String, String>();
	private RelativeLayout contactBgLayout = null;
	private TextView contactInfo = null;
	private int Flag = 0;
	private LinearLayout contactLayout = null;
	private String appName = null;
	private String BackFlag = "yes";
	AppManager appManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SnapLionMyAppActivity.KillFlag = true;
		try
		{ 
			setContentView(R.layout.contact);
		}catch (OutOfMemoryError e) {e.printStackTrace();System.gc();}
		Utility.closeAllBelowActivities(this);
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		Intent i = getIntent();
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		
		if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1011"))
		{
			BackFlag = "no";
			LinearLayout bottomLayout = (LinearLayout)findViewById(R.id.contact_bottom_bar);
			bottomLayout.setVisibility(View.VISIBLE);
			try{
				com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,width,appManager.getAPP_ID(),appName);
			}catch (OutOfMemoryError e) {e.printStackTrace();}
		 }

		contactLayout = (LinearLayout)findViewById(R.id.contact_top_bar);
		com.snaplion.util.TopClass.getTopView(SLContactActivity.this,getApplicationContext(),contactLayout,i.getStringExtra("ContactName"),BackFlag);
		
		
		contactBgLayout = (RelativeLayout)findViewById(R.id.contact_main_bg);
		contactInfo = (TextView)findViewById(R.id.contact_msg);
		
		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("ContactBG"+appManager.getAPP_ID())));
		contactBgLayout.setBackgroundDrawable(background);
		background = null;
		
		handler.sendEmptyMessage(appManager.DISPLAY);
	}

	Handler handler = new Handler() 
	{
	    @Override
	    public void handleMessage(Message msg) 
	    {
	        switch (msg.what) 
	        {
	        case AppManager.DISPLAY:
	        	Utility.FileCreate();
	        	String resStr = null;
	        	SharedPreferences myPref1 = null;
	        	try
	        	{
	        		myPref1 = getSharedPreferences("ContactPref"+appManager.getAPP_ID(), MODE_PRIVATE);
		    		resStr = myPref1.getString("Contact", "");
	        	}
	        	catch (Exception e) 
	        	{
	        		e.printStackTrace();
	        	}
	    		if(resStr != "")
	    		{
	    			Flag = 1;
	    			contactDetails = getContactDetails(resStr);
     			    contactInfo.setText(Html.fromHtml(contactDetails.get("message")));
	    		}
	    		handler.sendEmptyMessage(appManager.PROGRESS);
	        	break;
	        case AppManager.PROGRESS:
	        	try 
	        	{
	        		if(Utility.isOnline(getApplicationContext()))
	        		{
	        			if(Flag == 0)
	        			{
	    	    			showDialog(appManager.PROGRESS);
	    	    			new MyDownloadTask().execute(appManager.CONTACT_DETAILS_URL);
	    	    			Utility.debug("CONTACT_DETAILS_URL : "+appManager.CONTACT_DETAILS_URL);
	        			}
	        		}
	    		 } 
	        	catch (Exception e1) 
	        	{
	        		
	        	}	
	        	break;
	        case AppManager.ERROR:
				Toast.makeText(getApplicationContext(), "Network Fail.Please try later .!", Toast.LENGTH_LONG).show();
				break;
	        }
	    }
	};
	
	private class MyDownloadTask extends AsyncTask<String, Integer, InputStream> 
	{
		@Override
		protected void onPostExecute(InputStream result) 
		{
			try
			{
				contactInfo.setText(Html.fromHtml(contactDetails.get("message")));
			}
			catch (Exception e) 
			{
				Flag=1;
				e.printStackTrace();
			}
			
			if(Flag == 0)
			{
				SharedPreferences myPref = getSharedPreferences("SLUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
				String str = myPref.getString("MTime"+"1011"+appManager.getAPP_ID(), "");
				SharedPreferences.Editor editor = myPref.edit();
				editor.putBoolean("1011", false);
				editor.putString("1011"+appManager.getAPP_ID(),str);
				editor.commit();
				try
				{
					dismissDialog(appManager.PROGRESS);
				}
				catch (Exception e) 
				{
					Flag=1;
					e.printStackTrace();
				}
			}
			else
			{
				Utility.debug("1011 : Not downloaded properly :"+this.getClass().getName());
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
           		
           		String resStr = sb.toString();
           		resStr = Utility.replace(resStr, "&#xD;", "123aa123");
				resStr = Utility.replace(resStr, "&nbsp;", "123bb123");
				resStr = Utility.replace(resStr, "&ldquo;", "123cc123");
				resStr = Utility.replace(resStr, "&rdquo;", "123dd123");
				resStr = Utility.replace(resStr, "&ndash;", "123ee123");
				resStr = Utility.replace(resStr, "&mdash;", "123ff123");
				resStr = Utility.replace(resStr, "&rsquo;", "123gg123");
			//	resStr = Utility.replace(resStr, "&lt;", "123hh123");
           	//	resStr = Utility.replace(resStr, "p&gt;", "123ii123");
           		
				SharedPreferences settings = getSharedPreferences("ContactPref"+appManager.getAPP_ID(), MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
       		 	editor.putString("Contact",resStr);
       		 	editor.commit();
			}
			catch (Exception e) 
			{
				Flag=1;
				e.printStackTrace();
			}
			
			SharedPreferences myPref1 = getSharedPreferences("ContactPref"+appManager.getAPP_ID(), MODE_PRIVATE);
			String resStr = myPref1.getString("Contact", "");
			if(resStr != "")
			{
					contactDetails = getContactDetails(resStr);
			}
			else
			{
				Flag=1;
			}
			return null;
	}
}

	public HashMap<String, String> getContactDetails(String resStr) 
	{
		InputStream in =null;
		try
		{
			in = new ByteArrayInputStream(resStr.getBytes("UTF-8"));
		}
		catch (Exception e) 
		{
			Flag=1;
			e.printStackTrace();
		}
		final int picture  = 1;
	  	final int message = 2;
        int tagName = 0;

        HashMap<String, String> values = new HashMap<String, String>();

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
					} else if (parser.getName().equals("message")) 
					{
						tagName = message;
					}
				}
				if (eventType == XmlPullParser.TEXT) 
				{
					switch (tagName) 
					{
						case picture:
							values.put("picture", parser.getText());
							break;
						case message:
							String resStr1 = parser.getText();
							resStr1 = Utility.replace(resStr1, "123aa123", "&#xD;");
							resStr1 = Utility.replace(resStr1, "123bb123", "&nbsp;");
							resStr1 = Utility.replace(resStr1, "123cc123", "&ldquo;");
							resStr1 = Utility.replace(resStr1, "123dd123", "&rdquo;");
							resStr1 = Utility.replace(resStr1, "123ee123", "&ndash;");
							resStr1 = Utility.replace(resStr1, "123ff123", "&mdash;");
							resStr1 = Utility.replace(resStr1, "123gg123", "&rsquo;");
						//	resStr = Utility.replace(resStr, "123hh123", "&lt;");
			           	//	resStr = Utility.replace(resStr, "123ii123", "p&gt;");
							values.put("message",resStr1);
							break;
						default:
							break;
					}
					tagName = 0;
				}
				 
				if (eventType == XmlPullParser.END_TAG) 
				{
	                   
	            }
	            eventType = parser.next();
			}
		} 
        catch (XmlPullParserException e) 
        {
        	Flag=1;
			e.printStackTrace();
		} 
        catch (IOException e) 
        {
        	Flag=1;
			e.printStackTrace();
		}
		return values;
	}
	
	@Override
	 protected Dialog onCreateDialog(int id) 
	{
	     switch (id) 
	     {
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
	protected void onPause() 
	{
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
	    boolean isScreenOn = powerManager.isScreenOn();
	    if (!isScreenOn) 
	    {
	    	SnapLionMyAppActivity.KillFlag = false;
	    }
		if(SnapLionMyAppActivity.KillFlag)
		{
			try
			{
				try{SLMusicPlayerActivity.pausePlayer();}catch (Exception e) {e.printStackTrace();}

				//Utility.killMyApp(getApplicationContext(),SLContactActivity.this);
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
		mGaTracker.sendView("Contact_Screen");
	}
}
