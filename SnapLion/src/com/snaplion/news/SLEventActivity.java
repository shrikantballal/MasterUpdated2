package com.snaplion.news;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.inmobi.androidsdk.IMAdView;
import com.snaplion.beans.News;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class SLEventActivity extends AppSuperActivity
{
	@Override
	protected void onResume() {
		SnapLionMyAppActivity.KillFlag = true;
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		finish();
		System.gc();
		super.onDestroy();
	}

	protected static final int GET_NEWS_DATA = 5;
	private static final int SHOW_NEWS_DIALOG = 6;

	private ArrayList<News> newsDetails = new ArrayList<News>();
	private ArrayList<News> upcomingDetails = new ArrayList<News>();
	private ArrayList<News> pastDetails = new ArrayList<News>();

	private ListView newsList = null;
	private String categoryName = null;
	private int Flag = 0;
	private String newsBG = null;
	private LinearLayout newsLayout = null;
	private LinearLayout newsTopLayout = null;
	private ImageAdapter adapter = null;
	private ImageAdapter1 adapter1 = null;

	private TextView upcomingTxt = null;
	private TextView pastTxt = null;
	private int NEWS_FLAG = 0;
	private String CATID = null;
	private String BackFlag = "yes";
	private int NFlag = 0;
	
	
	private boolean updateFlag = true;
	AppManager appManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);
		Utility.closeAllBelowActivities(this);
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		SnapLionMyAppActivity.KillFlag = true;
		Intent i = getIntent();
		categoryName = i.getStringExtra("NewsName");
		CATID = i.getStringExtra("CAT_ID");
		
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		
		if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),CATID)){
			BackFlag = "no";
			LinearLayout bottomLayout = (LinearLayout)findViewById(R.id.news_bottom_bar);
			bottomLayout.setVisibility(View.VISIBLE);
			com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,width,appManager.getAPP_ID(),appManager.getAPP_NAME());
		 }
		
		newsTopLayout = (LinearLayout)findViewById(R.id.news_top_bar);

		upcomingTxt = ((TextView)findViewById(R.id.upcoming_txt));
		upcomingTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				NEWS_FLAG = 0;
				upcomingTxt.setBackgroundResource(R.drawable.upcoming_sel);
				pastTxt.setBackgroundResource(R.drawable.past_unsel);
				adapter = new ImageAdapter(getApplicationContext(),upcomingDetails);
				newsList.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		});
	
		pastTxt = ((TextView)findViewById(R.id.past_txt));
		pastTxt.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				NEWS_FLAG = 1;
				pastTxt.setBackgroundResource(R.drawable.past_sel);
				upcomingTxt.setBackgroundResource(R.drawable.upcoming_unsel);
				adapter = new ImageAdapter(getApplicationContext(),pastDetails);
				newsList.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		});
		newsLayout = (LinearLayout)findViewById(R.id.news_bg);
		
		newsList = (ListView)findViewById(R.id.news_list);
		newsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				SnapLionMyAppActivity.KillFlag = false;
				Intent i = new Intent(getApplicationContext(), SLNewsDetailsActivity.class);
				if(NEWS_FLAG == 0){
					i.putExtra("NewsTitle", upcomingDetails.get(arg2).getTitle());
					i.putExtra("NewsContent", upcomingDetails.get(arg2).getContent());
					i.putExtra("NewsId", upcomingDetails.get(arg2).getNewsid());
					
					if(CATID.equalsIgnoreCase("1008")){
						String s1 = upcomingDetails.get(arg2).getCity().trim();
						if(!s1.equalsIgnoreCase("")){
							if(!upcomingDetails.get(arg2).getCountry().equalsIgnoreCase(""))
								s1 = s1+" , "+upcomingDetails.get(arg2).getCountry();
						}else
							s1 = upcomingDetails.get(arg2).getCountry();
						
						i.putExtra("EventCity", s1);
						i.putExtra("NewsDate", upcomingDetails.get(arg2).getEventdate()+", "+upcomingDetails.get(arg2).getStarttime());
						
						i.putExtra("NewsImg", upcomingDetails.get(arg2).getImage());
					}else{
						i.putExtra("NewsDate", upcomingDetails.get(arg2).getEventdate());
						i.putExtra("NewsImg", upcomingDetails.get(arg2).getImg1());
					}
					i.putExtra("ShowURL", upcomingDetails.get(arg2).getShowurl());
					try{
						i.putExtra("startDate", upcomingDetails.get(arg2).getCaldate()+", "+upcomingDetails.get(arg2).getEndtime());
					}catch (Exception e) {e.printStackTrace();}
					i.putExtra("Venue", upcomingDetails.get(arg2).getVenuedetails());
				}else{
					i.putExtra("NewsTitle", pastDetails.get(arg2).getTitle());
					i.putExtra("NewsContent", pastDetails.get(arg2).getContent());
					i.putExtra("NewsId", pastDetails.get(arg2).getNewsid());
					
					if(CATID.equalsIgnoreCase("1008")){
						String s = null;
						s = pastDetails.get(arg2).getCity().trim();
						if(!s.equalsIgnoreCase("")){
							if(!pastDetails.get(arg2).getCountry().trim().equalsIgnoreCase(""))
								s = s+" , "+pastDetails.get(arg2).getCountry();
						}else
							s = pastDetails.get(arg2).getCountry();
						
						i.putExtra("EventCity", s);
						s = null;
						i.putExtra("NewsDate", pastDetails.get(arg2).getEventdate()+", "+pastDetails.get(arg2).getStarttime());
						i.putExtra("NewsImg", pastDetails.get(arg2).getImage());
					}else{
						i.putExtra("NewsDate", pastDetails.get(arg2).getEventdate());
						i.putExtra("NewsImg", pastDetails.get(arg2).getImg1());
					}
					i.putExtra("ShowURL", pastDetails.get(arg2).getShowurl());
					try{
						i.putExtra("startDate",pastDetails.get(arg2).getCaldate()+", "+pastDetails.get(arg2).getEndtime());
					}catch (Exception e) {e.printStackTrace();}
					i.putExtra("Venue", pastDetails.get(arg2).getVenuedetails());
				}
					i.putExtra("CATID", CATID);
					i.putExtra("appId", appManager.getAPP_ID());
					i.putExtra("appname",appManager.getAPP_NAME());
					i.putExtra("NewsName",categoryName);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
			}
		});
		
		if(CATID.equalsIgnoreCase("1008"))
		{
			BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("EventsBg"+appManager.getAPP_ID())));
			newsLayout.setBackgroundDrawable(background);
			background = null;
    	}
		else
    	{
			BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("NewsBg"+appManager.getAPP_ID())));
    		newsLayout.setBackgroundDrawable(background);
			background = null;
    	}
		
		handler.sendEmptyMessage(appManager.DISPLAY);
	}

	@Override
	protected void onStart() 
	{
		try{newsTopLayout.removeAllViews();}catch (Exception e) {e.printStackTrace();};
		com.snaplion.util.TopClass.getTopView(SLEventActivity.this,getApplicationContext(),newsTopLayout,categoryName,BackFlag);
		super.onStart();
		if(CATID.equalsIgnoreCase("1007"))//news
		{
			mGaTracker.sendView("News_Screen");
		}
		else//event
		{
			mGaTracker.sendView("Event_Screen");
		}
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
	        	String resStr = "";
	        	try
	        	{
	        		SharedPreferences myPref1 = getSharedPreferences("NewsPref"+appManager.getAPP_ID(), MODE_PRIVATE);
	        	 	if(CATID.equalsIgnoreCase("1008"))
	        	 		resStr = myPref1.getString("EventsData", "");	
	        	 	else
	        	 		resStr = myPref1.getString("NewsData", "");
	        	}
	        	catch (OutOfMemoryError e) 
	        	{
	        		e.printStackTrace();
	        	}
	    		
	        	 if(resStr != ""){
	    			Flag = 1;
	    			newsDetails = getNewsDetails(resStr);
					getPastUpcomingData(newsDetails);
					
	                if(CATID.equalsIgnoreCase("1008"))
	                {
	                	if(upcomingDetails!=null && upcomingDetails.size() >0)
	                	{
	                		NFlag = 1;
	                		// shrikant
	                		pastTxt.setBackgroundResource(R.drawable.past_unsel);
	                		upcomingTxt.setBackgroundResource(R.drawable.upcoming_sel);
	                		// end
							adapter = new ImageAdapter(getApplicationContext(),upcomingDetails);
	                	}
	                	else if(pastDetails!=null && pastDetails.size() >0)
	                	{
	                		NFlag = 1;
							NEWS_FLAG = 1;
							pastTxt.setTextColor(Color.WHITE);
							pastTxt.setTypeface(appManager.lucida_grande_bold);
							upcomingTxt.setTypeface(appManager.lucida_grande_regular);
							upcomingTxt.setTextColor(Color.BLACK);
							// shrikant
							upcomingTxt.setBackgroundResource(R.drawable.upcoming_unsel);
							pastTxt.setBackgroundResource(R.drawable.past_sel);
							// end
							adapter = new ImageAdapter(getApplicationContext(),pastDetails);
						}
	                	else if(NFlag ==0)
						{
							NFlag = 1;
							showDialog(SHOW_NEWS_DIALOG);
						}
	                	newsList.setAdapter(adapter);
					}
	                else
	                {
						getEventImage();
					}
	    		}
	    		if(Flag == 0)
	    		{
	    			if(Utility.isOnline(getApplicationContext()))
	    			{
	    				showDialog(appManager.PROGRESS);
	    				handler.sendEmptyMessage(GET_NEWS_DATA);
	    			}
	    		}
	        	break;
	        case GET_NEWS_DATA:
	        	try {
	        			
		        		if(CATID.equalsIgnoreCase("1008"))
		                {
		        			//new MyDownloadTask().execute("http://snaplion.com/service_event_past_v14m.php?AppId="+appId);
		        			new MyDownloadTask().execute(appManager.EVENT_URL);
		        			Utility.debug("EVENT_URL : "+appManager.EVENT_URL);
		                }
		                else
		                {
		                	//new MyDownloadTask().execute("http://snaplion.com/service_news2.php?AppId="+appId);
		                	new MyDownloadTask().execute(appManager.NEWS_URL);
		                	Utility.debug("NEWS_URL : "+appManager.NEWS_URL);
		                }
	    		 } catch (Exception e1) {e1.printStackTrace();}	
	        	break;
	        case AppManager.ERROR:
				Toast.makeText(getApplicationContext(), "Network Fail.Please try later .!", Toast.LENGTH_LONG).show();
				break;
	        }
	    }
	};
	
	protected void getEventImage() {
		if(upcomingDetails.size() !=0){
			NFlag = 1;
			pastTxt.setTypeface(appManager.lucida_grande_regular);
			upcomingTxt.setTypeface(appManager.lucida_grande_bold);
			adapter1 = new ImageAdapter1(getApplicationContext(),upcomingDetails);
			newsList.setAdapter(adapter1);
			adapter1.notifyDataSetChanged();
		}else if(pastDetails.size() !=0){
			NFlag = 1;
			NEWS_FLAG = 1;
			pastTxt.setTextColor(Color.WHITE);
			pastTxt.setTypeface(appManager.lucida_grande_bold);
			upcomingTxt.setTypeface(appManager.lucida_grande_regular);
			upcomingTxt.setTextColor(Color.BLACK);
			adapter1 = new ImageAdapter1(getApplicationContext(),pastDetails);
			newsList.setAdapter(adapter1);
			adapter1.notifyDataSetChanged();
		}else if(NFlag == 0){
			NFlag = 1;
			showDialog(SHOW_NEWS_DIALOG);
		}
		
		loadImages(NEWS_FLAG);
	}

	private void loadImages(int nEWS_FLAG2) {
		ArrayList<News> values = new ArrayList<News>();
		try{values.clear();}catch (Exception e) {e.printStackTrace();}
		
		if(nEWS_FLAG2 == 0)
			values = upcomingDetails;
		else
			values = pastDetails;
		
		for(int i=0;i<values.size();i++)
		{
			String path = "NewsThumb"+values.get(i).getNewsid();
			Bitmap bitmap = Utility.FetchImage(path);
			path = null;
			if(bitmap != null)
			try 
			{
    			News n = new News();
    			n =  values.get(i);
    			n.setBitmap(bitmap);
    			if(nEWS_FLAG2 == 0)
					upcomingDetails.set(i, n);
    			else
					pastDetails.set(i, n);
    			bitmap = null;
    			n = null;
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			adapter1.notifyDataSetChanged();
		}
		values = null;
	}

	private class MyDownloadTask3 extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPostExecute(Void result) {
			try{
				getEventImage();
			}catch (OutOfMemoryError e) {e.printStackTrace();}
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Void... params) {
				for(int i=0;i<newsDetails.size();i++){
					URL url = null;
					try {
						url = new URL(newsDetails.get(i).getImage());
					} catch (MalformedURLException e1) {e1.printStackTrace();}
				try{
					URLConnection connection = url.openConnection();
					connection.connect();
					InputStream is = connection.getInputStream();
					String path = "NewsThumb"+newsDetails.get(i).getNewsid();
					Utility.SaveImageSDCard(is, path);
					is = null;
					path = null;
				}catch (Exception e) {e.printStackTrace();}
			}
			return null;
		}
	}	
	
	private class MyDownloadTask extends AsyncTask<String, Integer, InputStream> 
	{
		private String resStr = null;
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
				
				SharedPreferences settings = getSharedPreferences("NewsPref"+appManager.getAPP_ID(), MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				if(CATID.equalsIgnoreCase("1008"))
       		 		editor.putString("EventsData",resStr);
				else
       		 		editor.putString("NewsData",resStr);
       		 	editor.commit();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return null;
		}
	
		@Override
		protected void onPostExecute(InputStream result) 
		{
			try
			{
				try
				{
					newsDetails.clear();
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				newsDetails = getNewsDetails(resStr);
				getPastUpcomingData(newsDetails);
							
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
					
			if(Flag == 0)
			{
				if(CATID.equalsIgnoreCase("1008"))
				{
					try
					{
						if(upcomingDetails.size() !=0)
						{
							NFlag = 1;
							pastTxt.setTypeface(appManager.lucida_grande_regular);
							upcomingTxt.setTypeface(appManager.lucida_grande_bold);
							// shrikant
	                		pastTxt.setBackgroundResource(R.drawable.past_unsel);
	                		upcomingTxt.setBackgroundResource(R.drawable.upcoming_sel);
	                		// end
							adapter = new ImageAdapter(getApplicationContext(),upcomingDetails);
							newsList.setAdapter(adapter);
						}
						else if(pastDetails.size() !=0)
						{
							NFlag = 1;
							NEWS_FLAG = 1;
							pastTxt.setTextColor(Color.WHITE);
							pastTxt.setTypeface(appManager.lucida_grande_bold);
							upcomingTxt.setTypeface(appManager.lucida_grande_regular);
							upcomingTxt.setTextColor(Color.BLACK);
							// shrikant
	                		pastTxt.setBackgroundResource(R.drawable.past_sel);
	                		upcomingTxt.setBackgroundResource(R.drawable.upcoming_unsel);
	                		// end
							adapter = new ImageAdapter(getApplicationContext(),pastDetails);
							newsList.setAdapter(adapter);
						}
						else if(NFlag == 0)
						{
							NFlag = 1;
							showDialog(SHOW_NEWS_DIALOG);
						}
						if(updateFlag)
						{
							SharedPreferences myPref = getSharedPreferences("SLUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
							String str = myPref.getString("MTime"+"1008"+appManager.getAPP_ID(), "");
							SharedPreferences.Editor editor = myPref.edit();
							editor.putBoolean("1008", false);
							editor.putString("1008"+appManager.getAPP_ID(),str);
							editor.commit();
						}
						else
						{
							Utility.debug("1008 : Not downloaded properly :"+this.getClass().getName());
						}
						dismissDialog(appManager.PROGRESS);
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
				else
				{
					try
					{
						getEventImage();
						SharedPreferences myPref = getSharedPreferences("SLUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
						String str = myPref.getString("MTime"+"1007"+appManager.getAPP_ID(), "");
						SharedPreferences.Editor editor = myPref.edit();
						editor.putBoolean("1007", false);
						editor.putString("1007"+appManager.getAPP_ID(),str);
						editor.commit();
						dismissDialog(appManager.PROGRESS);
					}
					catch (Exception e) 
					{
						e.printStackTrace();
						Utility.debug("1007 : Not downloaded properly :"+this.getClass().getName());
					}
				}
			}
			else
			{
				if(CATID.equalsIgnoreCase("1008"))
				{
					adapter = new ImageAdapter(getApplicationContext(),upcomingDetails);
					adapter.notifyDataSetChanged();
				}
				else
				{
					adapter1 = new ImageAdapter1(getApplicationContext(),pastDetails);
					adapter1.notifyDataSetChanged();
				}
			}
				
			try
			{
				if(!CATID.equalsIgnoreCase("1008"))
					getImageList();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}

	public ArrayList<News> getNewsDetails(String resStr) 
	{
		InputStream in =null;
		try
		{
			in = new ByteArrayInputStream(resStr.getBytes("UTF-8"));
		}
		catch (Exception e) 
		{
			// TODO: handle exception
		}
		final int bgimage  = 1;
	  	final int newsrecord = 2;
        final int newsid = 3;
        final int title = 4;
		final int content  = 5;
	  	final int image = 6;
        final int eventdate = 7;
        final int caldate = 8;
		final int type  = 9;
	  	final int imagename = 10;
        final int month = 11;
        final int year = 12;
        final int showurl = 13;
        final int img1 = 14;
        final int starttime = 15;
        final int endtime = 16;
        final int city = 17;
        final int country = 18;
        final int venuedetails = 19;
        final int eventdateformat2 = 20;
        final int caldateformat2 = 21;
        int tagName = 0;

        	
        News value = null;
        ArrayList<News> values = new ArrayList<News>();
        
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
					if (parser.getName().equals("bgimage")) 
					{
						tagName = bgimage;
					} 
					else if (parser.getName().equals("newsrecord")||parser.getName().equals("eventrecord")) 
					{
						value = new News();
						tagName = newsrecord;
					}
					else if (parser.getName().equals("newsid")||parser.getName().equals("eventid")) 
					{
						tagName = newsid;
					}
					else if (parser.getName().equals("title")) 
					{
						tagName = title;
					}
					else if (parser.getName().equals("content")) 
					{
						tagName = content;
					}
					else if (parser.getName().equals("image")) 
					{
						tagName = image;
					}
					else if (parser.getName().equals("eventdate")) 
					{
						tagName = eventdate;
					}
					else if (parser.getName().equals("caldate")) 
					{
						tagName = caldate;
					}
					else if (parser.getName().equals("type")) 
					{
						tagName = type;
					}
					else if (parser.getName().equals("imagename")) 
					{
						tagName = imagename;
					}
					else if (parser.getName().equals("month")) 
					{
						tagName = month;
					}
					else if (parser.getName().equals("year")) 
					{
						tagName = year;
					}
					else if (parser.getName().equals("showurl")) 
					{
						tagName = showurl;
					}
					else if (parser.getName().equals("img1")) 
					{
						tagName = img1;
					}
					else if (parser.getName().equals("starttime")) 
					{
						tagName = starttime;
					}
					else if (parser.getName().equals("endtime")) 
					{
						tagName = endtime;
					}
					else if (parser.getName().equals("city")) 
					{
						tagName = city;
					}
					else if (parser.getName().equals("country")) 
					{
						tagName = country;
					}
					else if (parser.getName().equals("venuedetails")) 
					{
						tagName = venuedetails;
					}
					else if (parser.getName().equals("eventdateformat")||parser.getName().equals("eventdateformat2")) 
					{
						tagName = eventdateformat2;
					}
					else if (parser.getName().equals("caldateformat")||parser.getName().equals("caldateformat2")) 
					{
						tagName = caldateformat2;
					}
				}
				if (eventType == XmlPullParser.TEXT) 
				{
					switch (tagName) 
					{
						case bgimage:
							newsBG =  parser.getText();
							break;
						case newsrecord:
							break;
						case newsid:
							value.setNewsid(parser.getText());
							break;
						case title:
							value.setTitle(parser.getText());
							break;
						case content:
							value.setContent(parser.getText());
							break;
						case image:
							value.setImage(parser.getText());
							break;
						case eventdate:
							value.setEventdate(parser.getText());
							break;
						case caldate:
							value.setCaldate(parser.getText());
							break;
						case type:
							value.setType(parser.getText());
							break;
						case imagename:
							value.setImagename(parser.getText());
							break;
						case month:
							value.setMonth(parser.getText());
							break;
						case year:
							value.setYear(parser.getText());
							break;
						case showurl:
							value.setShowurl(parser.getText());
							break;
						case img1:
							value.setImg1(parser.getText());
							break;
						case starttime:
							value.setStarttime(parser.getText());
							break;
						case endtime:
							value.setEndtime(parser.getText());
							break;
						case city:
							value.setCity(parser.getText());
							break;
						case country:
							value.setCountry(parser.getText());
							break;
						case venuedetails:
							value.setVenuedetails(parser.getText());
							break;
						case eventdateformat2:
							String str = parser.getText().trim();
							value.setEventdateformat2(str);
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
							//if(!CATID.equalsIgnoreCase("1008"))
							//{
								try 
								{
									value.setHireDate(df.parse(str));
								} 
								catch (ParseException e) 
								{
									e.printStackTrace();
								}	
							//}
							break;
						case caldateformat2:
							String str1 = parser.getText().trim();
							//SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
							SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
							value.setCaldateformat2(str1);
	//						if(CATID.equalsIgnoreCase("1008"))
	//						{
	//							try {
	//								value.setHireDate(df1.parse(str1));
	//							} catch (ParseException e) {e.printStackTrace();}	
	//						}
							break;
						default:
							break;
					}
					tagName = 0;
				}
				 
				if (eventType == XmlPullParser.END_TAG) 
				{
					if (parser.getName().equals("newsrecord")||parser.getName().equals("eventrecord")) 
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


	public void getImageList() 
	{
		new MyDownloadTask3().execute();
	}

	public void getPastUpcomingData(ArrayList<News> newsDetails2) 
	{
		try{pastDetails.clear();}catch (Exception e) {}
		try{upcomingDetails.clear();}catch (Exception e) {}

		for(int i=0;i<newsDetails2.size();i++)
		{
			News news = new News();
			news = newsDetails2.get(i);
			if(newsDetails2.get(i).getType().equalsIgnoreCase("past"))
			{
				pastDetails.add(news);
			}
			else
			{
				if(CATID.equalsIgnoreCase("1008"))
				{
					Calendar cal = Calendar.getInstance();
					Calendar currentcal = Calendar.getInstance();
					String s = newsDetails2.get(i).getCaldateformat2().trim();
					s = Utility.replace(s," ","-");
					String[] s1 = s.split("-");
					
					String hours="";
					String minutes="";
					
					try
					{
						String[] s3 = s1[3].split(":");
						hours = s3[0];
						minutes = s3[1];
						if(minutes.equalsIgnoreCase("00"))
							hours = 0+"";
						if(minutes.equalsIgnoreCase("00"))
							minutes = 0+"";
					}
					catch(Exception ex)
					{
						hours = 0+"";
						minutes = 0+"";
					}
					
					cal.set(Integer.parseInt(s1[0]),getMonth(Integer.parseInt(s1[1])),Integer.parseInt(s1[2]),Integer.parseInt(hours),Integer.parseInt(minutes));
					currentcal.set(currentcal.get(Calendar.YEAR), currentcal.get(Calendar.MONTH), currentcal.get(Calendar.DAY_OF_MONTH),currentcal.get(Calendar.HOUR_OF_DAY),currentcal.get(Calendar.MINUTE));
					if(cal.before(currentcal))
						pastDetails.add(news);
					else
						upcomingDetails.add(news);
						
				}
				else
				{
					Calendar cal1 = Calendar.getInstance();
					Calendar currentcal1 = Calendar.getInstance();
					String s3 = newsDetails2.get(i).getEventdateformat2().trim();
					String[] s4 = s3.split("-");
					cal1.set(Integer.parseInt(s4[0]),getMonth(Integer.parseInt(s4[1])),Integer.parseInt(s4[2]));
					currentcal1.set(currentcal1.get(Calendar.YEAR), currentcal1.get(Calendar.MONTH), currentcal1.get(Calendar.DAY_OF_MONTH));
					if(cal1.before(currentcal1))
						pastDetails.add(news);
					else
						upcomingDetails.add(news);	
					}
	
				}
		}
		Collections.sort(pastDetails, new CompDate(true));
		Collections.sort(upcomingDetails, new CompDate(false));
	}
	public static class CompDate implements Comparator<News> 
	{
	        private int mod = 1;
	        public CompDate(boolean desc) 
	        {
	            if (desc)
	            	mod =-1;
	        }
	        @Override
	        public int compare(News arg0, News arg1) 
	        {
	        	return mod*arg0.hireDate.compareTo(arg1.hireDate);
	        }
	 }

	private int getMonth(int parseInt) 
	{
		int month = 0;
		switch (parseInt) 
		{
			case 1:month = Calendar.JANUARY;	break;
			case 2:month = Calendar.FEBRUARY;	break;
			case 3:month = Calendar.MARCH;		break;
			case 4:month = Calendar.APRIL;		break;
			case 5:month = Calendar.MAY;		break;
			case 6:month = Calendar.JUNE;		break;
			case 7:month = Calendar.JULY;		break;
			case 8:month = Calendar.AUGUST;		break;
			case 9:month = Calendar.SEPTEMBER;	break;
			case 10:month = Calendar.OCTOBER;	break;
			case 11:month = Calendar.NOVEMBER;	break;
			case 12:month = Calendar.DECEMBER;	break;
			default:
				break;
		}
		return month;
	}

	public class ImageAdapter extends BaseAdapter
	{
		ArrayList<News> values = null;
		Context MyContext;
		public ImageAdapter(Context _MyContext, ArrayList<News> upcomingDetails1)
		{
			MyContext = _MyContext;
			try
			{
				values.clear();
			}catch (Exception e) {}
			values = new ArrayList<News>();
			values = upcomingDetails1;
		}
		public int getCount() 
		{
			return values.size();
		}

		public View getView(int position, View convertView, ViewGroup parent) 
		{
			if (convertView == null )
			{
				convertView =  getLayoutInflater().inflate(R.layout.news_cell_view, null);
			}
			convertView.setBackgroundResource(R.drawable.events_cell_bg);
//				int[] colors = new int[] { R.drawable.dark_gray_bg,R.drawable.light_gray_bg};
//				int colorPos = position % colors.length;
//				try
//				{
//					convertView.setBackgroundResource(colors[colorPos]);
//				}
//				catch (OutOfMemoryError e) 
//				{
//					e.printStackTrace();
//				}
				//((LinearLayout)convertView.findViewById(R.id.cal_layout_bg)).setBackgroundResource(R.drawable.calender_bg);
				TextView tv = (TextView)convertView.findViewById(R.id.month_txt);
					tv.setText(values.get(position).getMonth());
					tv.setTypeface(appManager.lucida_grande_regular);
				TextView tv1 = (TextView)convertView.findViewById(R.id.year_txt);
					tv1.setText(values.get(position).getYear());
					tv1.setTypeface(appManager.lucida_grande_regular);
					
				TextView year2_txt = (TextView)convertView.findViewById(R.id.year2_txt);
				String strtmp=values.get(position).getEventdate();
				strtmp=strtmp.substring(strtmp.lastIndexOf(" "), strtmp.length());
				year2_txt.setText(strtmp);
				year2_txt.setTypeface(appManager.lucida_grande_regular);
					
				TextView tv2 = (TextView)convertView.findViewById(R.id.title_txt);
					tv2.setText(values.get(position).getTitle());
					int SDK_INT = android.os.Build.VERSION.SDK_INT;
					if(SDK_INT<13)
					{
						tv2.setTypeface(appManager.lucida_grande_bold);
					}
				TextView tv3 = (TextView)convertView.findViewById(R.id.content_txt);
					tv3.setText(values.get(position).getEventdate()+" from "+values.get(position).getStarttime());
					tv3.setTypeface(appManager.lucida_grande_regular);
				String str = values.get(position).getCity().trim();
				if(str.length()>0 && values.get(position).getCountry().trim().length()>0)
					str = str+", "+values.get(position).getCountry();
				else if(values.get(position).getCountry().trim().length()>0)
					str = values.get(position).getCountry();
				TextView tv4 = (TextView)convertView.findViewById(R.id.e_venue_txt);
					tv4.setText(str);
					tv4.setTypeface(appManager.lucida_grande_regular);
			return convertView;
		}

		
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	
	public class ImageAdapter1 extends BaseAdapter
	{
		ArrayList<News> values = null;
		Context MyContext;
		public ImageAdapter1(Context _MyContext, ArrayList<News> upcomingDetails1)
		{
			MyContext = _MyContext;
			try{
				values.clear();
			}catch (Exception e) {}
			values = new ArrayList<News>();
			values = upcomingDetails1;
		}
		public int getCount() 
		{
			return values.size();
		}

		public View getView(int position, View convertView, ViewGroup parent) 
		{
			if (convertView == null )
			{
				convertView =  getLayoutInflater().inflate(R.layout.news_cell_view1, null);
			}

			int[] colors = new int[] { R.drawable.dark_gray_bg,R.drawable.light_gray_bg};
			int colorPos = position % colors.length;
			try{
				convertView.setBackgroundResource(colors[colorPos]);
			}catch (OutOfMemoryError e) {e.printStackTrace();}

			ImageView iv = (ImageView)convertView.findViewById(R.id.event_img);
				
			if(values.get(position).getBitmap() !=null){
				iv.setImageBitmap(values.get(position).getBitmap());
				iv.setScaleType(ImageView.ScaleType.FIT_XY);
			}
			TextView tv1 = (TextView)convertView.findViewById(R.id.title_txt1);
				tv1.setText(values.get(position).getTitle());
				tv1.setTypeface(appManager.lucida_grande_bold);
			TextView tv2 = (TextView)convertView.findViewById(R.id.content_txt1);
				tv2.setText(values.get(position).getContent());
				tv2.setTypeface(appManager.lucida_grande_regular);
			TextView tv3 = (TextView)convertView.findViewById(R.id.eventdate_txt1);
				tv3.setText(values.get(position).getEventdate());
				tv3.setTypeface(appManager.lucida_grande_regular);
			return convertView;
		}

		
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	
		
	@Override
	 protected Dialog onCreateDialog(int id) {
	     switch (id) {
	         case AppManager.PROGRESS:
	             final ProgressDialog progDialog = new ProgressDialog(this);
	             progDialog.setMessage("Loading, Please wait...");
	             progDialog.setCanceledOnTouchOutside(false);
	             progDialog.setCancelable(true);
	             return progDialog;
	         case SHOW_NEWS_DIALOG:
	        	 String str = null;
	        	 if(CATID.equalsIgnoreCase("1008"))
	        		 str = "Events";
	        	 else
	        		 str = "News";
	        	 
	        	 new AlertDialog.Builder( SLEventActivity.this)
	        	 .setTitle(str)
	        	 .setMessage("No "+str+" found.")
	             .setIcon(null)
	             .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                // Write your code here to execute after dialog closed
	                	NFlag = 1;
	                	dialog.dismiss();
	                }
	        }).show();
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
				Utility.killMyApp(getApplicationContext(),SLEventActivity.this);
			}catch (Exception e) {e.printStackTrace();}
		}
		super.onPause();
	}
}
