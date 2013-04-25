package com.snaplion.kingsxi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snaplion.beans.Bottom;
import com.snaplion.bio.SLBioActivity;
import com.snaplion.castcrew.MultibioCategoryListActivity;
import com.snaplion.contact.SLContactActivity;
import com.snaplion.copyright.SLCopyRightActivity;
import com.snaplion.fanwall.wall.FanwallWallActivity;
import com.snaplion.mail.SLMailActivity;
import com.snaplion.menus.MenusActivity;
import com.snaplion.music.SLMusicActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.news.SLEventActivity;
import com.snaplion.news.SLNewsActivity;
import com.snaplion.photos.SLPhotosActivity;
import com.snaplion.promo.PromoActivity;
import com.snaplion.scorecard.ScoreCardActivity;
import com.snaplion.tickets.SLTicketActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.BottomClass;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;
import com.snaplion.videos.SLVideoActivity;

public class SLMoreActivtity extends AppSuperActivity
{
	@Override
	protected void onResume() 
	{
		SnapLionMyAppActivity.KillFlag = true;
		super.onResume();
	}
	private int Flag=0;
	private GridView allGrid = null;
	private int[] mThumbIds ={R.drawable.m1001,R.drawable.m1002,R.drawable.m1003,R.drawable.m1004,R.drawable.m1006,
			R.drawable.m1007,R.drawable.m1008,R.drawable.m1009,R.drawable.m1010,R.drawable.m1011,R.drawable.m1012,
			R.drawable.m1013,R.drawable.m1014,R.drawable.m1017,R.drawable.m1018,R.drawable.m1019,R.drawable.m1020};
	private String[] APP = {"1001","1002","1003","1004","1006","1007","1008","1009","1010","1011","1012","1013","1014", "1017", "1018","1019","1020"};
	ArrayList<Bottom> bottomIconeList = new ArrayList<Bottom>();
	LinearLayout topLayout = null;
	LinearLayout bgLayout = null;
	AppManager appManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sl_more);
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		SnapLionMyAppActivity.KillFlag = true;
		
		Utility.FileCreate();
		Intent i = getIntent();
		
		bgLayout = (LinearLayout)findViewById(R.id.all_bg_layout);
		
		topLayout = (LinearLayout)findViewById(R.id.sl_more_top_bar);
		com.snaplion.util.TopClass.getTopView(SLMoreActivtity.this,getApplicationContext(),topLayout,i.getStringExtra("MoreName"),"no");
		
		allGrid = (GridView)findViewById(R.id.all_grid);
		SharedPreferences myPref1 = getSharedPreferences("SLPref"+appManager.getAPP_ID(), Context.MODE_PRIVATE);
		String bottumStr = myPref1.getString("BottomTabData","");
		try {
			 InputStream in = new ByteArrayInputStream(bottumStr.getBytes("UTF-8"));
			 if(in != null)
				 bottomIconeList = BottomClass.getBottomDetails(in);
			 bottomIconeList.remove(4);
		}catch (Exception e) {e.printStackTrace();}
		
		Bottom b = new Bottom();
		b.setFeatureid("1014");
		b.setFeaturename("SnapLion");
		bottomIconeList.add(b);
		
//		Bottom btn = bottomIconeList.remove(3);
//		Bottom b1 = new Bottom();
//		b1.setFeatureid("1020");
//		b1.setFeaturename("ScoreCard");
//		bottomIconeList.add(3,b1);
//		bottomIconeList.add(4,btn);
		
//		Display display = getWindowManager().getDefaultDisplay();
//		float density = getApplicationContext().getResources().getDisplayMetrics().density;
//	    
//		int  bounding= (display.getWidth()-(Math.round((float)32 * density)))/3;
//		allGrid.setColumnWidth( bounding );
//	    allGrid.setStretchMode( GridView.NO_STRETCH ) ;    
	    
		allGrid.setAdapter(new ImageAdapter(this));
		allGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int APPID = 0;
				try{
					APPID = Integer.parseInt(bottomIconeList.get(arg2).getFeatureid());
				}catch (Exception e) {e.printStackTrace();}
				
				switch (APPID) {
				case 1001:
					//addEventGATracor(AppManager.getInstance().getAPP_ID(), "SECTION_OPENED", "HOME_1001", 0);
					SnapLionMyAppActivity.KillFlag = false;
					saveBData(APPID+"");
					Intent i1001 = new Intent(getApplicationContext(), SnapLionMyAppActivity.class);
					i1001.putExtra("Count", 3);
					i1001.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i1001);
					break;
				case 1002:
					SnapLionMyAppActivity.KillFlag = false;
					if(arg2<5)
						saveBData(APPID+"");
					Intent i1002 = new Intent(getApplicationContext(), SLBioActivity.class);
					i1002.putExtra("CAT_ID", APPID+"");
					i1002.putExtra("BioName",BottomClass.getCategoryName(bottomIconeList,APPID+""));
					i1002.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity(i1002);
					break;
				case 1003:
					SnapLionMyAppActivity.KillFlag = false;
					if(arg2<5)
						saveBData(APPID+"");
					Intent i1003 = new Intent(getApplicationContext(), SLPhotosActivity.class);
					//Intent i1003 = new Intent(getApplicationContext(), MenusActivity.class);
					i1003.putExtra("PhotoName",BottomClass.getCategoryName(bottomIconeList,APPID+""));
					i1003.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity(i1003);
					break;
				case 1004:
					SnapLionMyAppActivity.KillFlag = false;
					if(arg2<5)
						saveBData(bottomIconeList.get(arg2).getFeatureid());
					Intent i1004 = new Intent(getApplicationContext(), SLMusicActivity.class);
					i1004.putExtra("MusicName",BottomClass.getCategoryName(bottomIconeList,APPID+""));
					i1004.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity(i1004);
					break;
				case 1006:
				{
					SnapLionMyAppActivity.KillFlag = false;
					if(arg2<5)
					{
						saveBData(bottomIconeList.get(arg2).getFeatureid());
					}
					Intent i1006 = new Intent(getApplicationContext(), SLVideoActivity.class);
					i1006.putExtra("VideosName",BottomClass.getCategoryName(bottomIconeList,APPID+""));
					i1006.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity(i1006);
					break;
				}
				case 1007:
					SnapLionMyAppActivity.KillFlag = false;
					if(arg2<5)
						saveBData(bottomIconeList.get(arg2).getFeatureid());
					Intent i1007 = new Intent(getApplicationContext(), SLNewsActivity.class);
					i1007.putExtra("CAT_ID", APPID+"");
					i1007.putExtra("NewsName",BottomClass.getCategoryName(bottomIconeList,APPID+""));
					i1007.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity(i1007);
					break;
				case 1008:
					SnapLionMyAppActivity.KillFlag = false;
					if(arg2<5)
						saveBData(bottomIconeList.get(arg2).getFeatureid());
					Intent i1008 = new Intent(getApplicationContext(), SLEventActivity.class);
					i1008.putExtra("CAT_ID", APPID+"");
					i1008.putExtra("NewsName",BottomClass.getCategoryName(bottomIconeList,APPID+""));
					i1008.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity(i1008);
					break;
				case 1009:
					SnapLionMyAppActivity.KillFlag = false;
					if(arg2<5)
						saveBData(bottomIconeList.get(arg2).getFeatureid());
					Intent i1009 = new Intent(getApplicationContext(), SLMailActivity.class);
					i1009.putExtra("MailName",BottomClass.getCategoryName(bottomIconeList,APPID+""));
					i1009.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity(i1009);
					break;
				case 1010:
					SnapLionMyAppActivity.KillFlag = false;
					if(arg2<5)
						saveBData(bottomIconeList.get(arg2).getFeatureid());
					Intent i1010 = new Intent(getApplicationContext(), FanwallWallActivity.class);
					i1010.putExtra("FanwallSectionName",BottomClass.getCategoryName(bottomIconeList,APPID+""));
					i1010.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity(i1010);
					break;
				case 1011:
					SnapLionMyAppActivity.KillFlag = false;
					if(arg2<5)
						saveBData(bottomIconeList.get(arg2).getFeatureid());
					Intent i1011 = new Intent(getApplicationContext(), SLContactActivity.class);
					i1011.putExtra("ContactName",BottomClass.getCategoryName(bottomIconeList,APPID+""));
					i1011.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity(i1011);
					break;
				case 1012:
					SnapLionMyAppActivity.KillFlag = false;
					if(arg2<5)
						saveBData(bottomIconeList.get(arg2).getFeatureid());
					Intent i1012 = new Intent(getApplicationContext(), SLCopyRightActivity.class);
					i1012.putExtra("CopyRight",BottomClass.getCategoryName(bottomIconeList,APPID+""));
					i1012.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity(i1012);
					break;
				case 1013:
					SnapLionMyAppActivity.KillFlag = false;
					if(arg2<5)
						saveBData(bottomIconeList.get(arg2).getFeatureid());
					if(Utility.isOnline(getApplicationContext()))
					{
						Intent i1013 = new Intent(getApplicationContext(), SLTicketActivity.class);
						i1013.putExtra("TicketName",BottomClass.getCategoryName(bottomIconeList,APPID+""));
						i1013.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
						startActivity(i1013);
					}else{
						Toast.makeText(getApplicationContext(), "Network Fail.Please try later .!", Toast.LENGTH_LONG).show();
					}
					break;
				case 1014:
					SnapLionMyAppActivity.KillFlag = false;
					if(arg2<5)
						saveBData(bottomIconeList.get(arg2).getFeatureid());
					Intent i1014 = new Intent(getApplicationContext(), SnapLionActivity.class);
					i1014.putExtra("Snaplion",BottomClass.getCategoryName(bottomIconeList,APPID+""));
					i1014.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity(i1014);
					break;
				case 1017:
					SnapLionMyAppActivity.KillFlag = false;
					if(arg2<5)
						saveBData(bottomIconeList.get(arg2).getFeatureid());
					Intent i1017 = new Intent(getApplicationContext(), MultibioCategoryListActivity.class);
					i1017.putExtra("MultibioCatName",BottomClass.getCategoryName(bottomIconeList,APPID+""));
					i1017.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity(i1017);
					break;
				case 1018:
					SnapLionMyAppActivity.KillFlag = false;
					if(arg2<5)
						saveBData(APPID+"");
					//Intent i1003 = new Intent(getApplicationContext(), SLPhotosActivity.class);
					Intent i1018 = new Intent(getApplicationContext(), MenusActivity.class);
					i1018.putExtra("MenusName",BottomClass.getCategoryName(bottomIconeList,APPID+""));
					i1018.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity(i1018);
					break;	
				case 1019:
					SnapLionMyAppActivity.KillFlag = false;
					if(arg2<5)
						saveBData(APPID+"");
					Intent i1019 = new Intent(getApplicationContext(), PromoActivity.class);
					i1019.putExtra("PromoName",BottomClass.getCategoryName(bottomIconeList,APPID+""));
					i1019.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity(i1019);
					break;	
				case 1020:
					SnapLionMyAppActivity.KillFlag = false;
					if(arg2<5)
						saveBData(bottomIconeList.get(arg2).getFeatureid());
					if(Utility.isOnline(getApplicationContext()))
					{
						Intent i2000 = new Intent(getApplicationContext(), ScoreCardActivity.class);
						i2000.putExtra("ScoreCard",BottomClass.getCategoryName(bottomIconeList,APPID+""));
						i2000.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
						startActivity(i2000);
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Network Fail.Please try later .!", Toast.LENGTH_LONG).show();
					}
					break;	
				default:
					break;
				}
			}
		});
		
		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("AllBG"+appManager.getAPP_ID())));
		bgLayout.setBackgroundDrawable(background);
		//bgLayout.setBackgroundResource(R.drawable.bg_all);
		background = null;
	}
	
//	private void getBGImage() 
//	{
//		new MyDownloadTask().execute(appManager.SERVICE_URL+"service_bgimageall.php?AppId="+appManager.getAPP_ID());
//	}
	
	
	private class MyDownloadTask extends AsyncTask<String, Integer, InputStream> 
	{
		@Override
		protected void onPostExecute(InputStream result) 
		{
			BitmapDrawable background = new BitmapDrawable(Utility.FetchImage("AllBG"+appManager.getAPP_ID()));
	        bgLayout.setBackgroundDrawable(background);
	        background = null;
	      	  
			if(Flag==0)
			{
				SharedPreferences m05 = getSharedPreferences("SLUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
		      	String str = m05.getString("MTime"+"1005"+appManager.getAPP_ID(), "");
		      	SharedPreferences.Editor ed05 = m05.edit();
		      	ed05.putBoolean("1005", true);
		      	ed05.putString("1005"+appManager.getAPP_ID(),str);
		      	ed05.commit();
			}
			else
			{
				Utility.debug("1005 : Not downloaded properly :"+this.getClass().getName());
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
           		String resMailBg = getAllBg(in);
           		if(resMailBg != null)
           		{
					URL url = null;
					try 
					{
						url = new URL(resMailBg);
					} 
					catch (MalformedURLException e1) 
					{
						Flag=1;
						e1.printStackTrace();
					}
			        try 
			        {
			            URLConnection connection = url.openConnection();
			            connection.connect();
			            InputStream is = connection.getInputStream();
			            String path = "AllBG"+appManager.getAPP_ID();
			            Utility.SaveImageSDCard(is, path);
			            is = null;
			            path = null;
			        } 
			        catch (OutOfMemoryError e) 
			        {
			        	Flag=1;
			        	e.printStackTrace();
			        }
			    }
           		else
           		{
           			Flag=1;
           		}
			}
			catch (Exception e) 
			{
				Flag=1;
				e.printStackTrace();
			}
			return null;
		}
	}

	
	protected  void saveBData(String string) {
		SharedPreferences settings = getSharedPreferences("SLPref"+appManager.getAPP_ID(), Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = settings.edit();
	 	editor.putString("BRemData",string);
	 	editor.commit();
	}


	private String getAllBg(InputStream in) 
	{
		final int bgpicture  = 1;
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
					if (parser.getName().equals("bgpicture")) 
					{
						tagName = bgpicture;
					}
				}
				if (eventType == XmlPullParser.TEXT) 
				{
					switch (tagName) 
					{
					case bgpicture:
						values = parser.getText();
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
	public class ImageAdapter extends BaseAdapter
	{
		Context MyContext;
		public ImageAdapter(Context _MyContext)
		{
			MyContext = _MyContext;
		}
		public int getCount() 
		{
			return bottomIconeList.size();
		}
		class ViewHolder
		{
			ImageView iv;
			TextView tv;
		}
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			ViewHolder viewHolder;
			if(convertView==null)
			{
				convertView = getLayoutInflater().inflate(R.layout.all_grid_item, null);
				viewHolder=new ViewHolder();
				viewHolder.iv = (ImageView)convertView.findViewById(R.id.gridButtonImage);
				viewHolder.tv = (TextView) convertView.findViewById(R.id.all_grid_item_text);
				convertView.setTag(viewHolder);
			}
			else
			{
				viewHolder=(ViewHolder)convertView.getTag();
			}
			try
			{
				
				viewHolder.iv.setImageResource(mThumbIds[getIndex(bottomIconeList.get(position).getFeatureid())]);
				viewHolder.tv.setText(bottomIconeList.get(position).getFeaturename());
				viewHolder.tv.setTypeface(appManager.lucida_grande_regular);
				
				Display display = getWindowManager().getDefaultDisplay(); 
				int width = display.getWidth();
				
				int squareDimen = (width/3);
				viewHolder.iv.getLayoutParams().height = squareDimen-8;
				viewHolder.iv.getLayoutParams().width = squareDimen;
				viewHolder.iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
	     	 	
				DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);
				switch(metrics.densityDpi)
				{
				     case DisplayMetrics.DENSITY_LOW:
				     {
				    	 //Utility.debug("DENSITY_LOW");
				    	 break;
				     }
				     case DisplayMetrics.DENSITY_MEDIUM:
				     {
				    	 //Utility.debug("DENSITY_MEDIUM");
				                 break;
				     }
				     case DisplayMetrics.DENSITY_HIGH:
				     {
				    	 //Utility.debug("DENSITY_HIGH");
				    	 viewHolder.tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				    	 break;
				     }
				     case DisplayMetrics.DENSITY_XHIGH:
				     {
				    	 //Utility.debug("DENSITY_XHIGH");
				    	 viewHolder.tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				    	 viewHolder.tv.setPadding(0, 0, 0, 3);
				    	 break;
				     }
				     case DisplayMetrics.DENSITY_TV:
				     {
				    	 //Utility.debug("DENSITY_TV");
				    	 viewHolder.tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
				    	 viewHolder.tv.setPadding(10, 0, 0, 8);
				    	 break;
				     }
				}
			}
			catch (OutOfMemoryError e) 
			{
				e.printStackTrace();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
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

	public int getIndex(String featureid) 
	{
		int a = 0;
		for(int i=0;i<APP.length;i++)
		{
			if(featureid.equalsIgnoreCase(APP[i]))
			{
				a=i;
				break;
			}
		}
		return a;
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
				Utility.killMyApp(getApplicationContext(),SLMoreActivtity.this);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		super.onPause();
	}
}
