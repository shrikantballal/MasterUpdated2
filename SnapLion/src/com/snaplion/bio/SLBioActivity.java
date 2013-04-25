package com.snaplion.bio;

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

public class SLBioActivity extends AppSuperActivity
{
	private HashMap<String, String> bioDetails = new HashMap<String, String>();
	private ImageView bioImg = null;
	private RelativeLayout bioMainBg = null;
	private LinearLayout topLayout = null;
	private LinearLayout bottomLayout = null;
	private WebView webView = null;
	private int Flag = 0;
	private String BackFlag = "yes";
	AppManager appManager;
	@Override
	protected void onResume() 
	{
		SnapLionMyAppActivity.KillFlag = true;
		super.onResume();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		try
		{
			setContentView(R.layout.bio);
		}
		catch (OutOfMemoryError e) 
		{
			System.gc();e.printStackTrace();
		}
		Utility.closeAllBelowActivities(this);
		SnapLionMyAppActivity.KillFlag = true;
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		
		Intent i = getIntent();
		
		if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1002"))
		{
			((View)findViewById(R.id.bottomTabPlaceHolder)).setVisibility(View.VISIBLE);
			BackFlag = "no";
			bottomLayout = (LinearLayout)findViewById(R.id.bio_tab_bar);
			bottomLayout.setVisibility(View.VISIBLE);
			try
			{
				com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,width,appManager.getAPP_ID(),appManager.getAPP_NAME());
			}catch (OutOfMemoryError e) {e.printStackTrace();}	
		}

		topLayout = (LinearLayout)findViewById(R.id.bio_top_bar);
		com.snaplion.util.TopClass.getTopView(SLBioActivity.this,getApplicationContext(),topLayout,i.getStringExtra("BioName"),BackFlag);
		
		
		 bioMainBg = (RelativeLayout)findViewById(R.id.bio_main_bg);
		 bioImg = (ImageView)findViewById(R.id.bio_img);
		 webView = (WebView)findViewById(R.id.bio_comments);
		 webView.setBackgroundColor(0);
		 webView.getSettings().setJavaScriptEnabled(true); 
		 webView.getSettings().setPluginState(PluginState.ON);
		 webView.getSettings().setAllowFileAccess(true); 
		// To disable link 
		 webView.setWebViewClient(new WebViewClient() 
		 {
			 public boolean shouldOverrideUrlLoading(WebView view, String url) 
			 { 
		            return true;
		     } 
		 });

		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("BioMainBg"+appManager.getAPP_ID())));
		bioMainBg.setBackgroundDrawable(background);
		background = null;
		
		SharedPreferences myPref1 = getSharedPreferences("SLPref"+appManager.getAPP_ID(), MODE_PRIVATE);
		String resStr = myPref1.getString("BioData", "");
		if(resStr != "")
		{
			Flag = 1;
			bioDetails = getBioDetails(resStr);

            try 
            {
            	Bitmap decodedByte = Utility.FetchImage("BioPicture"+appManager.getAPP_ID());
        		if(decodedByte != null)
        		{
        				bioImg.setVisibility(View.VISIBLE);
        				bioImg.setImageBitmap(Utility.getScaledImageByWinWidth(decodedByte,display.getWidth(),display.getHeight()));
        				//bioImg.setImageBitmap(decodedByte);
        		}
        		else
        		{
        				bioImg.setVisibility(View.GONE);
        		}
        		 decodedByte = null;
            } 
            catch (OutOfMemoryError e) 
            {
            	e.printStackTrace();
            }
            
            if(bioDetails.get("comments") !=null)
            {
            	String aboutHtmlData="<font color='#FFFFFF'>"+Html.fromHtml(bioDetails.get("comments").trim()).toString()+"</font>";
            	Utility.debug("ABOUT CONTENT : "+aboutHtmlData);
	            webView.loadDataWithBaseURL(null,aboutHtmlData, "text/html", "utf-8",""); 
            }
		}
		
		handler.sendEmptyMessage(appManager.DISPLAY);
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
				Utility.killMyApp(getApplicationContext(),SLBioActivity.this);
			}catch (Exception e) {e.printStackTrace();}
		}
		super.onPause();
	}
	
	@Override
	public void onBackPressed() {
		SnapLionMyAppActivity.KillFlag = false;
		super.onBackPressed();
	}

	Handler handler = new Handler() 
	{
	    @Override
	    public void handleMessage(Message msg) 
	    {
	        switch (msg.what) 
	        {
		        case AppManager.DISPLAY:
		        {
		        	Utility.FileCreate();
		        	try 
		        	{
		        		if(Utility.isOnline(getApplicationContext()))
		        		{
		        			if(Flag == 0)
		        			{
		        				showDialog(appManager.PROGRESS);
		        				new MyDownloadTask().execute(appManager.BIO_DETAILS_URL);
		        				Utility.debug("BIO_DETAILS_URL : "+appManager.BIO_DETAILS_URL);
		        			}
		        		}
		    		} 
		        	catch (Exception e1) {}	
		        	break;
		        }
		        case AppManager.ERROR:
		        {
					Toast.makeText(getApplicationContext(), "Network Fail.Please try later .!", Toast.LENGTH_LONG).show();
					break;
		        }
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

           		resStr = Utility.replace(resStr, "&", "123aaaa123");
           		
    			SharedPreferences settings = getSharedPreferences("SLPref"+appManager.getAPP_ID(), MODE_PRIVATE);
    			SharedPreferences.Editor editor = settings.edit();
       		 	editor.putString("BioData",resStr);
       		 	editor.commit();
       		 	in = null;
			}
			catch (Exception e) 
			{
				Flag=1;
				e.printStackTrace();
			}
				
			try
			{
				bioDetails = getBioDetails(resStr);
				if(resStr != "")
				{
					URL url = null;
					try 
					{
						if(bioDetails.get("picture").length()>5)
						{
							url = new URL(bioDetails.get("picture"));
						}
						else
						{
							Utility.removeImage("BioPicture"+appManager.getAPP_ID());
						}
					} 
					catch (MalformedURLException e1) 
					{
						Flag=1;
						e1.printStackTrace();
					}
					try 
					{
						if(url != null)
						{
							URLConnection connection = url.openConnection();
							connection.connect();
							InputStream is = connection.getInputStream();
							if(is != null)
								Utility.SaveImageSDCard(is, "BioPicture"+appManager.getAPP_ID());
							else
								updateFlag = false;  
							is = null;
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
			Bitmap bitmap1 = Utility.FetchImage("BioPicture"+appManager.getAPP_ID());
				if(bitmap1 != null)
				{
					bioImg.setVisibility(View.VISIBLE);
					bioImg.setImageBitmap(bitmap1);
	        	}
				else
				{
	        		bioImg.setVisibility(View.GONE);
	        	}
				bitmap1 = null;
					
				if(bioDetails.get("comments") != null)
				{
		            webView.loadDataWithBaseURL(null,"<font color='#FFFFFF'>"+Html.fromHtml(bioDetails.get("comments").trim()).toString()+"</font>", "text/html", "utf-8",""); 
	            }
				
			  if(Flag == 0 && updateFlag)
			  {
			    try
			    {
			    	SharedPreferences myPref = getSharedPreferences("SLUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
			    	String str = myPref.getString("MTime"+"1002"+appManager.getAPP_ID(), "");
			    	SharedPreferences.Editor editor = myPref.edit();
			    	editor.putBoolean("1002", false);
			    	editor.putString("1002"+appManager.getAPP_ID(),str);
			    	editor.commit();
				    dismissDialog(appManager.PROGRESS);
			    }
			    catch (Exception e) 
			    {
			    	e.printStackTrace();
			    }
		    }
			else
			{
				Utility.debug("1002 : Not downloaded properly :"+this.getClass().getName());
			}
		}
	}

	public HashMap<String, String> getBioDetails(String resStr) 
	{
		InputStream in =null;
		try
		{
			in = new ByteArrayInputStream(resStr.getBytes("UTF-8"));
		}
		catch (Exception e) 
		{
			Flag=1;
		}
		
		final int comments  = 1;
	  	final int picture = 2;
        final int biounique = 3;
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
					if (parser.getName().equals("comments")) {
						tagName = comments;
					} else if (parser.getName().equals("picture")) {
						tagName = picture;
					}else if (parser.getName().equals("biounique")) {
						tagName = biounique;
					}else if (parser.getName().equals("bgimage")) {
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
						values.put("picture", parser.getText());
						break;
					case biounique:
						values.put("biounique", parser.getText());
						break;
					case bgimage:
						values.put("bgimage", parser.getText());
						break;
					default:
						break;
					}
					tagName = 0;
				}
				 
				if (eventType == XmlPullParser.END_TAG) {}
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
	protected void onStart() 
	{
		super.onStart();
		mGaTracker.sendView("Bio_Screen");
	}
}
