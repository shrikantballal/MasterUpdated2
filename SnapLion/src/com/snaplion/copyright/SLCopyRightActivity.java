package com.snaplion.copyright;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

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
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.inmobi.androidsdk.IMAdView;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class SLCopyRightActivity extends AppSuperActivity
{
	String appName = null;
	HashMap<String, String> crDetails = new HashMap<String, String>();
	ImageView crImg = null;
	RelativeLayout crMainBg = null;
	LinearLayout bottomLayout = null;
	WebView crCommentTxt=null;
	LinearLayout copyLayout = null;
	int Flag = 0;
	private String BackFlag = "yes";
	private String TAG="SLCopyRightActivity";

	AppManager appManager;
	@Override
	protected void onResume() {
		SnapLionMyAppActivity.KillFlag = true;
		super.onResume();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			setContentView(R.layout.copy_right);
		SnapLionMyAppActivity.KillFlag = true;
		Utility.closeAllBelowActivities(this);
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		Intent i = getIntent();
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		
		if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1012")){
			((View)findViewById(R.id.bottomTabPlaceHolder)).setVisibility(View.VISIBLE);
			BackFlag = "no";
			LinearLayout bottomLayout = (LinearLayout)findViewById(R.id.cr_tab_bar);
			bottomLayout.setVisibility(View.VISIBLE);
			com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,width,appManager.getAPP_ID(),appName);
		 }
		
		copyLayout = (LinearLayout)findViewById(R.id.cr_top_bar);
		com.snaplion.util.TopClass.getTopView(SLCopyRightActivity.this,getApplicationContext(),copyLayout,i.getStringExtra("CopyRight"),BackFlag);

		 crMainBg = (RelativeLayout)findViewById(R.id.cr_main_bg);
		 crImg = (ImageView)findViewById(R.id.cr_img);
		 bottomLayout = (LinearLayout)findViewById(R.id.cr_tab_bar);
		 crCommentTxt = (WebView)findViewById(R.id.cr_comments);
		 crCommentTxt.setBackgroundColor(0);
		 crCommentTxt.getSettings().setJavaScriptEnabled(true);
		 crCommentTxt.getSettings().setPluginState(PluginState.ON);
		 crCommentTxt.getSettings().setAllowFileAccess(true); 
		 // To disable link click
		 crCommentTxt.setWebViewClient(new WebViewClient() {
			 public boolean shouldOverrideUrlLoading(WebView view, String url) { 
		            return true;
		        } 
		 });

		 BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("BgCrImage"+appManager.getAPP_ID())));
		 crMainBg.setBackgroundDrawable(background);
		 background = null;
		 
		handler.sendEmptyMessage(appManager.DISPLAY);
	}
	
	
	Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	        case AppManager.DISPLAY:
	        	Utility.FileCreate();
	    		SharedPreferences myPref1 = getSharedPreferences("CRPref"+appManager.getAPP_ID(), MODE_PRIVATE);
	    		String resStr = myPref1.getString("CrData", "");
	    		if(resStr != ""){
	    			Flag = 1;
	    			crDetails = getCRDetails(resStr);
	    			Bitmap bitmap = Utility.FetchImage("CRImage"+appManager.getAPP_ID());
					 if(bitmap != null){
						 crImg.setVisibility(View.VISIBLE);
						 Display display=getWindowManager().getDefaultDisplay();
						 crImg.setImageBitmap(Utility.getScaledImageByWinWidth(bitmap,display.getWidth(),display.getHeight()));
						 //crImg.setImageBitmap(bitmap);
					}else{
						crImg.setVisibility(View.GONE);
					 }
					 bitmap = null;
	                try{
	                	crCommentTxt.loadData("<font color='#FFFFFF'>"+Html.fromHtml(crDetails.get("comments").trim()).toString()+"</font>", "text/html", "utf-8");
	                }catch (Exception e) {e.printStackTrace();}
	    		}
	    		handler.sendEmptyMessage(appManager.PROGRESS);
	        	break;
	        case AppManager.PROGRESS:
	        	try {
	        		if(Utility.isOnline(getApplicationContext())){
	        			if(Flag == 0){
	    	    			showDialog(appManager.PROGRESS);
	    	    			new MyDownloadTask().execute(appManager.COPYRIGHT_DETAILS_URL);
	    	    			Utility.debug("COPYRIGHT_DETAILS_URL : "+appManager.COPYRIGHT_DETAILS_URL);
	        			}
	        		}
	    		 } catch (Exception e1) {}	
	        	break;
	        case AppManager.ERROR:
				Toast.makeText(getApplicationContext(), "Network Fail.Please try later .!", Toast.LENGTH_LONG).show();
				break;
	        }
	    }
	};
	
	private class MyDownloadTask extends AsyncTask<String, Integer, InputStream> 
	{
		private boolean updateFlag = true;
		String resStr = null;
		
		@Override
		protected InputStream doInBackground(String... params) 
		{
			InputStream in = null;
			try
			{
				in = HttpConnection.connect(params[0]);
				if(in != null)
					resStr = Utility.getString(in);
				else
					updateFlag = false;

           		resStr = Utility.replace(resStr, "&", "123aaaa123");// @imran what i seed to replace & ????

				SharedPreferences settings = getSharedPreferences("CRPref"+appManager.getAPP_ID(), MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
       		 	editor.putString("CrData",resStr);
       		 	editor.commit();
			}
			catch (Exception e) 
			{
				Flag=1;
				e.printStackTrace();
			}
			
			try
			{
				crDetails = getCRDetails(resStr);
		
				if(resStr != "")
				{
					URL url = null;
					try 
					{
						url = new URL(Utility.replace(crDetails.get("picture")," ","%20"));
					} 
					catch (MalformedURLException e1) 
					{
						Flag=1;
						e1.printStackTrace();
					}
							        
					try 
					{
						if(url!=null)
						{
							URLConnection connection = url.openConnection();
							connection.connect();
							InputStream is = connection.getInputStream();
							if(is != null)
								Utility.SaveImageSDCard(is, "CRImage"+appManager.getAPP_ID());
							else
								updateFlag = false;

							is = null;
						}
						else
						{
							Utility.removeImage("CRImage"+appManager.getAPP_ID());
						}
					} 
					catch (Exception e) 
					{
						Flag=1;
						e.printStackTrace();
					}
				}
			}
			catch (Exception e) 
			{
				Flag=1;
				e.printStackTrace();
			}
			return null;
		}
	
		@Override
		protected void onPostExecute(InputStream result) 
		{
				 Bitmap bitmap = Utility.FetchImage("CRImage"+appManager.getAPP_ID());
				 if(bitmap != null)
				 {
					 crImg.setVisibility(View.VISIBLE);
					 crImg.setImageBitmap(bitmap);
				 }
				 else
				 {
						crImg.setVisibility(View.GONE);
				 }
				 bitmap = null;
				 try
				 {
					 crCommentTxt.loadData("<font color='#FFFFFF'>"+Html.fromHtml(crDetails.get("comments").trim()).toString()+"</font>", "text/html", "utf-8");
	             }
				 catch (Exception e) 
				 {
					 Flag=1;
					 e.printStackTrace();
				 }
               	             
	            try
	            {
	 				dismissDialog(appManager.PROGRESS);
	 			}
	            catch (Exception e) 
	            {
	            	Flag=1;
	            	e.printStackTrace();
	            }
	            
	            if(Flag == 0 && updateFlag)
	   			 {
		            try
		            {
	   					SharedPreferences myPref = getSharedPreferences("SLUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
	   					String str = myPref.getString("MTime"+"1012"+appManager.getAPP_ID(), "");
	   					SharedPreferences.Editor editor = myPref.edit();
	   					editor.putBoolean("1012", false);
	   					editor.putString("1012"+appManager.getAPP_ID(),str);
	   					editor.commit();
		            }
		            catch (Exception e) 
		            {
		            	Flag=1;
		            	e.printStackTrace();
		            }
	   			}
	            else
	            {
	            	Utility.debug("1012 : Not downloaded properly");
	            }
			}
		}

	protected HashMap<String, String> getCRDetails(String resStr) 
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
		final int comments  = 1;
	  	final int picture = 2;
	  	final int cprightunique = 3;
	  	final int bgimage = 4;

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
					if (parser.getName().equals("comments")) 
					{
						tagName = comments;
					} 
					else if (parser.getName().equals("picture")) 
					{
						tagName = picture;
					} 
					else if (parser.getName().equals("cprightunique")) 
					{
						tagName = cprightunique;
					} 
					else if (parser.getName().equals("bgimage")) 
					{
						tagName = bgimage;
					}
				}
				if (eventType == XmlPullParser.TEXT) 
				{
					switch (tagName) 
					{
						case comments:
							String resStr1 = parser.getText();
							resStr1 = Utility.replace(resStr1, "123aaaa123", "&");
							values.put("comments", resStr1);
							break;
						case picture:
							values.put("picture",parser.getText());
							break;
						case cprightunique:
							values.put("cprightunique",parser.getText());
							break;
						case bgimage:
							values.put("bgimage",parser.getText());
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
	protected void onPause() {
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
	    boolean isScreenOn = powerManager.isScreenOn();
	    if (!isScreenOn) {
	    	SnapLionMyAppActivity.KillFlag = false;
	    }
		if(SnapLionMyAppActivity.KillFlag)
		{
			try{
				Utility.killMyApp(getApplicationContext(),SLCopyRightActivity.this);
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
		System.gc();
		super.onStart();
		mGaTracker.sendView("Copyright_Screen");
	}
}
