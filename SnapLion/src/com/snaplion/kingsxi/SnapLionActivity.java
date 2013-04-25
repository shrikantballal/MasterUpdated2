package com.snaplion.kingsxi;

import java.util.HashMap;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

public class SnapLionActivity extends AppSuperActivity{
	@Override
	protected void onResume() {
		SnapLionMyAppActivity.KillFlag = true;
		super.onResume();
	}

	int Flag = 0;
	private HashMap<String, String> snaplionDetails = new HashMap<String, String>();
	private ImageView snaplionImg = null;
	private TextView snapInfo = null;
	private LinearLayout snapTopLayout = null;
	private String BackFlag = "yes";
	private String TAG="SnapLionActivity";
	AppManager appManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.snaplion);
		Utility.closeAllBelowActivities(this);
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		SnapLionMyAppActivity.KillFlag = true;
		
		Intent i = getIntent();
		
		snapTopLayout = (LinearLayout)findViewById(R.id.snap_top_bar);
		
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		
		if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1014")){
			BackFlag = "no";
			LinearLayout bottomLayout = (LinearLayout)findViewById(R.id.snaplion_bottom_bar);
			bottomLayout.setVisibility(View.VISIBLE);
			com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,width,appManager.getAPP_ID(),appManager.getAPP_NAME());
		 }
		
		try{
			com.snaplion.util.TopClass.getTopView(SnapLionActivity.this,getApplicationContext(),snapTopLayout,i.getStringExtra("Snaplion"),BackFlag);
		}catch (Exception e) {e.printStackTrace();}
		
		snaplionImg = (ImageView)findViewById(R.id.snaplion_img);
		snapInfo = (TextView)findViewById(R.id.snaplion_txt_email);
        snapInfo.setTypeface(appManager.lucida_grande_regular);
        snapInfo.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				SnapLionMyAppActivity.KillFlag = false;
				return false;
			}
		});

		/*BitmapDrawable background = new BitmapDrawable(Utility.FetchImage("SL"+appId));
		snaplionBg.setBackgroundDrawable(background);
		background = null;
		handler.sendEmptyMessage(appManager.DISPLAY);*/
	}
	
//	Handler handler = new Handler() {
//	    @Override
//	    public void handleMessage(Message msg) {
//	        switch (msg.what) {
//	        case AppManager.DISPLAY:
//	        	Utility.FileCreate();
//	    		SharedPreferences myPref1 = getSharedPreferences("SnaplionPref"+appManager.getAPP_ID(), MODE_PRIVATE);
//	    		String resStr = myPref1.getString("Snaplion", "");
//	    		if(resStr != ""){
//	    			Flag = 1;
//	    			snaplionDetails = getSnaplionDetails(resStr);
//	    			displayData();
//	    		}
//	    		if(Flag == 0)
//	    			showDialog(appManager.PROGRESS);
//	    		try {
//	        		new MyDownloadTask().execute(appManager.SNAPLIONABOUT_DETAILS_URL);
//	        		Utility.debug("SNAPLIONABOUT_DETAILS_URL : "+appManager.SNAPLIONABOUT_DETAILS_URL);
//	    		 } catch (Exception e1) {}	
//	        	break;
//	        case AppManager.PROGRESS:
//	        	try {
//	        		new MyDownloadTask().execute(appManager.SNAPLIONABOUT_DETAILS_URL);
//	        		Utility.debug("SNAPLIONABOUT_DETAILS_URL : "+appManager.SNAPLIONABOUT_DETAILS_URL);
//	    		 } catch (Exception e1) {}	
//	        	break;
//	        case AppManager.ERROR:
//				Toast.makeText(getApplicationContext(), "Network Fail.Please try later .!", Toast.LENGTH_LONG).show();
//				break;
//	        }
//	    }
//	};

//	private class MyDownloadTask extends AsyncTask<String, Integer, InputStream> {
//		@Override
//		protected void onPostExecute(InputStream result) {
//			Bitmap bitmap = SLMyAppSplashActivity.FetchImage("SLImg"+appManager.getAPP_ID());
//			if(bitmap != null)
//			   snaplionImg.setImageBitmap(bitmap);
//            bitmap = null;
//           if(snaplionDetails.get("comments") != null)
//           {
//        	   String cont=snaplionDetails.get("comments").replaceAll("Email:", "<br/>Email:");
//        	   Utility.debug("cont : "+cont);
//	        	snapInfo.setText(Html.fromHtml(snaplionDetails.get("comments").replaceAll("Email:", "<br/>Email:")));
//           }
//			super.onPostExecute(result);
//		}
//
//		@Override
//		protected InputStream doInBackground(String... params) {
//			InputStream in = null;
//			try{
//				in = HttpConnection.connect(params[0]);
//				BufferedReader br = new BufferedReader(new InputStreamReader(in));
//                StringBuilder sb = new StringBuilder();
//           		String line;
//           		while ((line = br.readLine()) != null) {
//           			sb.append(line);
//           		}
//				SharedPreferences settings = getSharedPreferences("SnaplionPref"+appManager.getAPP_ID(), MODE_PRIVATE);
//				SharedPreferences.Editor editor = settings.edit();
//       		 	editor.putString("Snaplion",sb.toString());
//       		 	editor.commit();
//			}catch (Exception e) {e.printStackTrace();}
//			
//			SharedPreferences myPref1 = getSharedPreferences("SnaplionPref"+appManager.getAPP_ID(), MODE_PRIVATE);
//			String resStr = myPref1.getString("Snaplion", "");
//			if(resStr != "")
//			{
//				snaplionDetails = getSnaplionDetails(resStr);
//				
//				URL url = null;
//				try 
//				{
//				
//					url = new URL(snaplionDetails.get("picture1"));
//			
//				} 
//				catch (MalformedURLException e1) 
//				{
//					Flag=1;
//					e1.printStackTrace();
//				}
//			        
//			  
//				try 
//				{
//			       URLConnection connection = url.openConnection();
//			       connection.connect();
//			       InputStream is = connection.getInputStream();
//			   	   Utility.SaveImageSDCard(is, "SLImg"+appManager.getAPP_ID());
//			   	   is = null;
//			    } 
//				catch (Exception e) 
//				{
//					Flag=1;
//					e.printStackTrace();
//				}	
//
//			    if(Flag == 0)
//			    {
//		        	SharedPreferences myPref = getSharedPreferences("SLUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
//		        	String str = myPref.getString("MTime"+"1014"+appManager.getAPP_ID(), "");
//					SharedPreferences.Editor editor = myPref.edit();
//					editor.putBoolean("1014", false);
//					editor.putString("1014"+appManager.getAPP_ID(),str);
//					editor.commit();
//					try
//					{
//						dismissDialog(appManager.PROGRESS);
//					}
//					catch (Exception e) 
//					{
//						Flag=1;
//						e.printStackTrace();
//					}
//				}
//			    else
//			    {
//			    	Utility.debug("1014 : Not downloaded properly :"+this.getClass().getName());
//			    }
//			}
//			else
//			{
//				Flag=1;
//			}
//			return null;
//		}
//	}
//	
//	protected HashMap<String, String> getSnaplionDetails(String resStr) 
//	{
//		InputStream in =null;
//		try
//		{
//			in = new ByteArrayInputStream(resStr.getBytes("UTF-8"));
//		}
//		catch (Exception e) 
//		{
//			Flag=1;
//			e.printStackTrace();
//		}
//		final int comments  = 1;
//	  	final int picture1 = 2;
//	  	final int picture2 = 3;
//
//        int tagName = 0;
//
//        HashMap<String, String> values = new HashMap<String, String>();
//
//        try 
//        {
//			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//			XmlPullParser parser = factory.newPullParser();
//			parser.setInput(in, HTTP.UTF_8);
//	
//        	int eventType = parser.getEventType();
//
//			while (eventType != XmlPullParser.END_DOCUMENT) {
//				if (eventType == XmlPullParser.START_TAG) {
//					if (parser.getName().equals("comments")) {
//						tagName = comments;
//					} else if (parser.getName().equals("picture1")) {
//						tagName = picture1;
//					} else if (parser.getName().equals("picture2")) {
//						tagName = picture2;
//					}
//				}
//				if (eventType == XmlPullParser.TEXT) {
//					switch (tagName) {
//					case comments:
//						values.put("comments", parser.getText());
//						break;
//					case picture1:
//						values.put("picture1",parser.getText());
//						break;
//					case picture2:
//						values.put("picture2",parser.getText());
//						break;
//					default:
//						break;
//					}
//					tagName = 0;
//				}
//				 
//				if (eventType == XmlPullParser.END_TAG) {}
//	                eventType = parser.next();
//	            }
//		} 
//        catch (XmlPullParserException e) 
//        {
//        	Flag=1;
//        	e.printStackTrace();
//		} 
//        catch (IOException e) 
//        {
//        	Flag=1;
//        	e.printStackTrace();
//        }
//		return values;
//	}
//	
//	protected void displayData() 
//	{
//		Bitmap bitmap = Utility.FetchImage("SLImg"+appManager.getAPP_ID());
//		if(bitmap != null)
//		   snaplionImg.setImageBitmap(bitmap);
//        bitmap = null;
//        String cont=snaplionDetails.get("comments").replaceAll("Email:", "<br/>Email:");
// 	   Utility.debug("cont : "+cont);
//        snapInfo.setText(Html.fromHtml(snaplionDetails.get("comments").replaceAll("Email:", "<br/>Email:")));
//	}

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
	public void onBackPressed() 
	{
		SnapLionMyAppActivity.KillFlag = false;
		super.onBackPressed();
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
				Utility.killMyApp(getApplicationContext(),SnapLionActivity.this);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		super.onPause();
	}
}
