package com.snaplion.promo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.inmobi.androidsdk.IMAdView;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

public class PromoURLActivity extends AppSuperActivity{
	private WebView ticketUrl = null;
	private LinearLayout newsWebTopLayout = null;
	private String url = null;
	AppManager appManager;
	
	@Override
	protected void onResume() {
		SnapLionMyAppActivity.KillFlag = true;
		super.onResume();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.promo_web);
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		SnapLionMyAppActivity.KillFlag = true;

		Intent i = getIntent();
		url = i.getStringExtra("ShowURL");
		//String appId = i.getStringExtra("appId"); 
		
		ticketUrl = (WebView)findViewById(R.id.news_url);
		ticketUrl.setPadding(0, 0, 0, 0);
		ticketUrl.setInitialScale(50);
		ticketUrl.getSettings().setLoadWithOverviewMode(true);
		ticketUrl.getSettings().setUseWideViewPort(true);
		ticketUrl.getSettings().setBuiltInZoomControls(true);

		newsWebTopLayout = (LinearLayout)findViewById(R.id.news_web_top_bar);
		com.snaplion.util.TopClass.getTopView(PromoURLActivity.this,getApplicationContext(),newsWebTopLayout,i.getStringExtra("CatName"),"yes");

		handler.sendEmptyMessage(appManager.DISPLAY);
		
		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("PromoBg"+appManager.getAPP_ID())));
		((LinearLayout)findViewById(R.id.promoDetails_bg)).setBackgroundDrawable(background);
		background = null;
	}

	Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	        case AppManager.DISPLAY:
				try{
					showDialog(appManager.PROGRESS);
						ticketUrl.loadUrl(url);
				}catch (Exception e) {
					e.printStackTrace();
				}	
	    		handler.sendEmptyMessage(appManager.PROGRESS);
	        	break;
	        case AppManager.PROGRESS:
	    		ticketUrl.setWebViewClient(new WebViewClient()
	    		{
	    			@Override
	    			public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    				// TODO Auto-generated method stub
	    				return super.shouldOverrideUrlLoading(ticketUrl, url);
	    			}
	    			@Override
	    			public void onPageStarted(WebView view, String url, Bitmap favicon) {
	    				try{
	    					showDialog(appManager.PROGRESS);
	    				}catch (Exception e) {e.printStackTrace();}
	    				super.onPageStarted(ticketUrl, url, favicon);
	    			}
	    			@Override
	    			public void onPageFinished(WebView view, String url) {
	    				try{
	    					dismissDialog(appManager.PROGRESS);
	    				}catch (Exception e) {}
	    				super.onPageFinished(ticketUrl, url);
	    			}
	    		});
	        	break;
	        case AppManager.ERROR:
				Toast.makeText(getApplicationContext(), "Network Fail.Please try later .!", Toast.LENGTH_LONG).show();
				break;
	        }
	    }
	};

		@Override
		protected Dialog onCreateDialog(int id) {
		  switch (id) {
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
			if(SnapLionMyAppActivity.KillFlag)
			{
				try{
					Utility.killMyApp(getApplicationContext(),PromoURLActivity.this);
				}catch (Exception e) {e.printStackTrace();}
			}
			super.onPause();
		}
}
