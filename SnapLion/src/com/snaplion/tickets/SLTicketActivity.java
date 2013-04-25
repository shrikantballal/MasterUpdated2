package com.snaplion.tickets;

import java.io.IOException;
import java.io.InputStream;

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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inmobi.androidsdk.IMAdView;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class SLTicketActivity extends AppSuperActivity
{
	private WebView ticketUrl = null;
	private String url = null;
	private String BackFlag = "yes";
	private boolean isSuccessFlag=true;
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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ticket);
		Utility.closeAllBelowActivities(this);
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		SnapLionMyAppActivity.KillFlag = true;

		Intent i = getIntent();
		
		ticketUrl = (WebView)findViewById(R.id.ticket_url);
		ticketUrl.setPadding(0, 0, 0, 0);
		ticketUrl.setInitialScale(50);
		ticketUrl.getSettings().setLoadWithOverviewMode(true);
		ticketUrl.getSettings().setUseWideViewPort(true);
		ticketUrl.getSettings().setBuiltInZoomControls(true);

		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		
		if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1013"))
		{
			BackFlag = "no";
			LinearLayout bottomLayout = (LinearLayout)findViewById(R.id.ticket_tab_bar);
			bottomLayout.setVisibility(View.VISIBLE);
			com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,width,appManager.getAPP_ID(),i.getStringExtra("appname"));
		}

		TextView tv = (TextView)findViewById(R.id.ticket_name_txt);
		tv.setText(i.getStringExtra("TicketName"));
		tv.setTypeface(appManager.lucida_grande_regular);
		
		Button homeBtn = (Button)findViewById(R.id.ticket_home_btn);
		homeBtn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				SnapLionMyAppActivity.KillFlag = false;
				try
				{
					com.snaplion.music.SLMusicPlayerActivity.closePlayer(getApplicationContext());
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				/*Intent i = new Intent(getApplicationContext(),SnapLionAppScreenActvity.class);
    			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(i);*/
    			finish();
    		}
		});
		
		Button backBtn = (Button)findViewById(R.id.ticket_back_btn);
		if(BackFlag.equalsIgnoreCase("no"))
		{
			if(!appManager.PREVIEWAPP_FLAG) {
				backBtn.setBackgroundResource(R.drawable.top_bar_logo);
			} else {
				backBtn.setVisibility(View.INVISIBLE);
			}
		}
		else
		{
			if(!appManager.PREVIEWAPP_FLAG) {
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
		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("TicketBg"+appManager.getAPP_ID())));
		((LinearLayout)findViewById(R.id.tkt_bg)).setBackgroundDrawable(background);
		background = null;
		//handler.sendEmptyMessage(appManager.DISPLAY);
		
		ticketUrl.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) 
			{
				return super.shouldOverrideUrlLoading(ticketUrl, url);
			}
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) 
			{
				showDialog(appManager.PROGRESS);
				super.onPageStarted(ticketUrl, url, favicon);
			}
			@Override
			public void onPageFinished(WebView view, String url) 
			{
				try
				{
					dismissDialog(appManager.PROGRESS);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				super.onPageFinished(ticketUrl, url);
			}
		});
		new DownloadTicketData().execute();
	}
	
	class DownloadTicketData extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params) 
		{
			InputStream in = null;
			try
			{
				in = HttpConnection.connect(appManager.TICKET_URL);
				Utility.debug("TICKET_URL : "+appManager.TICKET_URL);
				if(in != null)
				{
					url = getTicketDetails(in);
					return url;
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}	
			return null;
		}
		@Override
		protected void onPostExecute(String result) 
		{
			super.onPostExecute(result);
			if(result!=null)
			{
				ticketUrl.loadUrl(result);
			}
			else
			{
				Toast.makeText(SLTicketActivity.this, "Tickets not available." , Toast.LENGTH_SHORT);
			}
		}
	}
	
//	Handler handler = new Handler() 
//	{
//	    @Override
//	    public void handleMessage(Message msg) 
//	    {
//	        switch (msg.what) 
//	        {
//	        	case AppManager.DISPLAY:
//	        	{
//		        	InputStream in = null;
//					try
//					{
//						//showDialog(appManager.PROGRESS);
//						in = HttpConnection.connect(appManager.TICKET_URL);
//						Utility.debug("TICKET_URL : "+appManager.TICKET_URL);
//						if(in != null)
//						{
//							url = getTicketDetails(in);
//							if(url!=null)
//							{
//								ticketUrl.loadUrl(url);
//							}
//							else
//							{
//								Toast.makeText(SLTicketActivity.this, "Tickets not available." , Toast.LENGTH_SHORT);
//							}
//							
//						}
//						else
//						{
//							isSuccessFlag=false;
//							handler.sendEmptyMessage(appManager.ERROR);
//						}
//					}
//					catch (Exception e) 
//					{
//						e.printStackTrace();
//						isSuccessFlag=false;
//					}	
//		    		handler.sendEmptyMessage(appManager.PROGRESS);
//		    		
//		    		if(isSuccessFlag)
//		    		{
//		    			SharedPreferences myPref1 = getSharedPreferences("SLUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
//						SharedPreferences.Editor editor = myPref1.edit();
//						editor.putBoolean("1013", false);
//						editor.commit();
//		    		}
//		    		else
//		    		{
//		    			Utility.debug("1013 : Not downloaded properly :"+this.getClass().getName());
//		    		}
//		    		
//		        	break;
//	        	}
//	        	case AppManager.PROGRESS:
//	        	{
//		    		ticketUrl.setWebViewClient(new WebViewClient()
//		    		{
//		    			@Override
//		    			public boolean shouldOverrideUrlLoading(WebView view, String url) 
//		    			{
//		    				return super.shouldOverrideUrlLoading(ticketUrl, url);
//		    			}
//		    			@Override
//		    			public void onPageStarted(WebView view, String url, Bitmap favicon) 
//		    			{
//		    				showDialog(appManager.PROGRESS);
//		    				super.onPageStarted(ticketUrl, url, favicon);
//		    			}
//		    			@Override
//		    			public void onPageFinished(WebView view, String url) 
//		    			{
//		    				try
//		    				{
//		    					dismissDialog(appManager.PROGRESS);
//		    				}
//		    				catch (Exception e) 
//		    				{
//		    					e.printStackTrace();
//		    				}
//		    				super.onPageFinished(ticketUrl, url);
//		    			}
//		    		});
//		        	break;
//	        	}
//	        	case AppManager.ERROR:
//	        	{
//	        		Toast.makeText(getApplicationContext(), "Network Fail.Please try later .!", Toast.LENGTH_LONG).show();
//	        		break;
//	        	}
//	        }
//	    }
//	};


	public String getTicketDetails(InputStream in) 
	{
		final int url  = 1;
        int tagName = 0;

        String values = null;

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
					if (parser.getName().equals("url")) 
					{
						tagName = url;
					} 
				}
				if (eventType == XmlPullParser.TEXT) 
				{
					switch (tagName) 
					{
						case url:
							values = parser.getText();
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
        	e.printStackTrace();
        	isSuccessFlag=false;
		} 
        catch (IOException e) 
        {
        	e.printStackTrace();
        	isSuccessFlag=false;
        }
        if(values!=null && values.length()>5)
		return values;
        return null;
	}
	
		@Override
		protected Dialog onCreateDialog(int id) {
		  switch (id) {
		  case AppManager.PROGRESS:
		     final ProgressDialog progDialog = new ProgressDialog(this);
		           progDialog.setMessage("Loading. Please wait...");
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
					Utility.killMyApp(getApplicationContext(),SLTicketActivity.this);
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
			mGaTracker.sendView("Ticket_Screen");
		}
}
