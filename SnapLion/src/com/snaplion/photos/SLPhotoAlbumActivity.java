package com.snaplion.photos;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inmobi.androidsdk.IMAdView;
import com.snaplion.beans.AlbumSub;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class SLPhotoAlbumActivity extends AppSuperActivity
{
	@Override
	protected void onResume() 
	{
		SnapLionMyAppActivity.KillFlag = true;
		super.onResume();
	}
	private String CategaryId = null;
	private LinearLayout bottomLayout = null;
	private GridView albumGrid = null;
	private ImageAdapter albumAdapter = null;
	static ArrayList<AlbumSub> albumSubDetails = new ArrayList<AlbumSub>();
	//private LinearLayout playerLayout = null;
	private int Flag = 0;
	private HashMap<String, String> audioDetails = new HashMap<String, String>();
	private static boolean updateFlag = true;
	AppManager appManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photos_sub);
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		Log.d("Test","SLPhotoAlbumActivity::onCreate");
		SnapLionMyAppActivity.KillFlag = true;
		com.snaplion.music.SLMusicActivity.music = false;
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		
		Intent i = getIntent();
		CategaryId = i.getStringExtra("CategaryId");
		
		TextView tv = (TextView)findViewById(R.id.p_sub_name_txt);
			tv.setText(i.getStringExtra("CategaryName"));
			tv.setTypeface(appManager.lucida_grande_regular);
		
		Button homeBtn = (Button)findViewById(R.id.p_home_btn);
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
    				try
    				{
    					albumSubDetails.clear();
    				}
    				catch (Exception e) 
    				{
    					e.printStackTrace();
    				}
    				finishFromChild(getParent());
				}
		});
		
		
		bottomLayout = (LinearLayout)findViewById(R.id.album_sub_tab_bar);
		if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1003"))
		{
			bottomLayout.setVisibility(View.VISIBLE);
			com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,width,appManager.getAPP_ID(),appManager.getAPP_NAME());
		}

		Button backBtn = (Button)findViewById(R.id.p_sub_back_btn);
		backBtn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				SnapLionMyAppActivity.KillFlag = false;

				try
				{
					albumSubDetails.clear();
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				finishFromChild(getParent());
			}
		});
		
		
		//playerLayout = (LinearLayout)findViewById(R.id.photo_sub_player_layout);

		albumGrid = (GridView)findViewById(R.id.album_sub_grid);
		albumGrid.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				SnapLionMyAppActivity.KillFlag = false;

				SharedPreferences indPrefs = getSharedPreferences("Index",MODE_PRIVATE);
				SharedPreferences.Editor indexEditor = indPrefs.edit();
				indexEditor.putInt("Index", 0);
				indexEditor.commit();
				
				SharedPreferences indexPrefs = getSharedPreferences("currentIndex"+appManager.getAPP_ID(),MODE_PRIVATE);
				SharedPreferences.Editor indexEditor1 = indexPrefs.edit();
				indexEditor1.putInt("currentIndex", arg2);
				indexEditor.putInt("GalleryIndex",0);
				indexEditor1.commit();
				
				Intent i = new Intent(getApplicationContext(), SLPhotoGalleryActivity1.class);
				i.putExtra("AlbumPhotoId", arg2);
				try
				{
					i.putExtra("PhotoName", albumSubDetails.get(arg2).getPhotoname());
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				i.putExtra("Index", 0);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});
		
		albumAdapter = new ImageAdapter(getApplicationContext());

		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("AlbumBG"+appManager.getAPP_ID())));
		((RelativeLayout)findViewById(R.id.album_main_bg)).setBackgroundDrawable(background);
		background= null;
		
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
		        {
//		        	try
//		        	{
//		        		com.snaplion.music.SLMusicPlayerActivity.seekBarProgress.setSecondaryProgress(com.snaplion.music.SLMusicPlayerActivity.seekbarPosition);
//		        		com.snaplion.music.SLMusicPlayerActivity.getPlayer(SLPhotoAlbumActivity.this,getApplicationContext(),playerLayout,appManager.getAPP_ID());
//		        	}
//		        	catch (Exception e) 
//		        	{
//		        		e.printStackTrace();
//		        	}
		        	
		        	SharedPreferences myPref1 = getSharedPreferences("AlbumUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
		        	if(myPref1.getBoolean("Album"+CategaryId+appManager.getAPP_ID(), false))
		        	{
						showDialog(appManager.PROGRESS);
						try 
						{
							if(Utility.isOnline(getApplicationContext())) 
							{
								String photolistByCategoryUrl = appManager.PHOTOLIST_BY_CATEGORYID_URL;
								photolistByCategoryUrl = photolistByCategoryUrl.replaceAll("<<CATEGORY_ID>>",CategaryId);
								Utility.debug("PHOTOLIST_BY_CATEGORYID_URL : "+photolistByCategoryUrl);
								 //String url=appManager.SERVICE_URL+"service_photolist.php?AppId="+appManager.getAPP_ID()+"&CategaryId="+CategaryId;
								new MyDownloadTask().execute(photolistByCategoryUrl);
							}
							else 
							{
								dismissDialog(appManager.PROGRESS);
								Utility.showToastMessage(getApplicationContext(),getString(R.string.no_net_not_downloaded));
							}
						} 
						catch (Exception e1) 
						{
							e1.printStackTrace();
						}	
		        	}
		        	else
		        	{
		        		SharedPreferences myPref = getSharedPreferences("AlbumPref"+appManager.getAPP_ID(), MODE_PRIVATE);
			            String subAlbumData = myPref.getString("AlbumSubData"+CategaryId, "");
			            albumSubDetails = getAlbumSubDetails(subAlbumData);
			            showAlbums(albumSubDetails);
			            subAlbumData = null;
		        	}
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


	private void showAlbums(ArrayList<AlbumSub> albumSubDetails2) {
		for(int i=0;i<albumSubDetails2.size();i++)
		{
			String key =  "small"+CategaryId+albumSubDetails.get(i).getPhotoid()+albumSubDetails.get(0).getAlbumid()+albumSubDetails.get(0).getAlbumname();
            try {
       			AlbumSub ad = new AlbumSub();
	        	ad = albumSubDetails.get(i);
	        	ad.setBitmapSmall(Utility.FetchImage(key));
	        	albumSubDetails.set(i, ad);
            } catch (Exception e) {e.printStackTrace();}
		}
		albumGrid.setAdapter(albumAdapter);
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
				{
					resStr = Utility.getString(in);
					SharedPreferences settings = getSharedPreferences("AlbumPref"+appManager.getAPP_ID(), MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
	       		 	editor.putString("AlbumSubData"+CategaryId,resStr);
	       		 	editor.commit();
				}
				else
				{	
					updateFlag = false;
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				updateFlag = false;
			}

			try
			{
				if(resStr != "")
				{
					try
					{
						albumSubDetails.clear();
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
					albumSubDetails = getAlbumSubDetails(resStr);
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				updateFlag = false;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(InputStream result) 
		{
				if(Utility.isOnline(getApplicationContext()))
					new MyDownloadBGTask().execute();
				else
					updateFlag = false;
		}
	  }

	public static ArrayList<AlbumSub> getAlbumSubDetails(String subAlbumData) 
	{
		InputStream in =null;
		try
		{
			in = new ByteArrayInputStream(subAlbumData.getBytes("UTF-8"));
		}
		catch (Exception e) 
		{
			updateFlag=false;
		}
		final int albumphoto  = 1;
	  	final int albumid = 2;
        final int albumname = 3;
        final int photoid = 4;
        final int photoname = 5;
        final int smallpicture = 6;
        final int bigpicture = 7;
        final int comment = 8;
        int tagName = 0;

        AlbumSub value = null;
        ArrayList<AlbumSub> values = new ArrayList<AlbumSub>();

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
					if (parser.getName().equals("albumphoto")) 
					{
						tagName = albumphoto;
						value = new AlbumSub();
					} 
					else if (parser.getName().equals("albumid")) 
					{
						tagName = albumid;
					}
					else if (parser.getName().equals("albumname")) 
					{
						tagName = albumname;
					}
					else if (parser.getName().equals("photoid")) 
					{
						tagName = photoid;
					}
					else if (parser.getName().equals("photoname")) 
					{
						tagName = photoname;
					}
					else if (parser.getName().equals("smallpicture")) 
					{
						tagName = smallpicture;
					}
					else if (parser.getName().equals("bigpicture")) 
					{
						tagName = bigpicture;
					}
					else if (parser.getName().equals("comment")) 
					{
						tagName = comment;
					}
				}
				if (eventType == XmlPullParser.TEXT) 
				{
					switch (tagName) 
					{
						case albumphoto:
							break;
						case albumid:
							value.setAlbumid(parser.getText());
							break;
						case albumname:
							value.setAlbumname(parser.getText());
							break;
						case photoid:
							value.setPhotoid(parser.getText());
							break;
						case photoname:
							value.setPhotoname(parser.getText());
							break;
						case smallpicture:
							value.setSmallpicture(parser.getText());
							break;
						case bigpicture:
							value.setBigpicture(parser.getText());
							break;
						case comment:
							value.setComment(parser.getText());
							break;
						default:
							break;
					}
					tagName = 0;
				}
				 
				if (eventType == XmlPullParser.END_TAG) 
				{
                    if (parser.getName().equals("albumphoto")) 
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
			updateFlag=false;
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
			updateFlag=false;
		}
		return values;
	}
	
	private class MyDownloadBGTask extends AsyncTask<Void, Integer, Void> 
	{
		@Override
	    protected Void doInBackground(Void... params) 
		{
			URL url = null;
			for(int i=0;i<albumSubDetails.size();i++)
			{
				try 
				{
					String str = albumSubDetails.get(i).getSmallpicture();
					str = Utility.replace(str, " ", "%20");
					url = new URL(str);
				} 
				catch (Exception e1) 
				{
					e1.printStackTrace();
					updateFlag=false;
				}	
				 
				try 
				{
		            URLConnection connection = url.openConnection();
		            connection.connect();
		            InputStream is = connection.getInputStream();
		            if(is == null)
		            {
		            	updateFlag = false;
		            }
		            else
		            {
		            	String key =  "small"+CategaryId+albumSubDetails.get(i).getPhotoid()+albumSubDetails.get(0).getAlbumid()+albumSubDetails.get(0).getAlbumname();
		            	Utility.SaveImageSDCard(is, key);
		            	is=null;
		            	AlbumSub ad = new AlbumSub();
		            	try
		            	{
		            		ad = albumSubDetails.get(i);
		            		ad.setBitmapSmall(Utility.FetchImage(key));
		            		albumSubDetails.set(i, ad);
		            	}
		            	catch (Exception e) 
		            	{
		            		e.printStackTrace();
		            		updateFlag = false;
		            	}
		            }
		        } 
		        catch (Exception e) 
		        {
		        	e.printStackTrace(); 
		        	updateFlag = false;
		        }
	        }
	        return null;
	    }
		
		@Override
		protected void onPostExecute(Void result) 
		{
			if(Flag == 0)
			{
		 		try
		 		{
		 			albumGrid.setAdapter(albumAdapter);
		 			if(updateFlag)
		 			{
		 				SharedPreferences myPref2 = getSharedPreferences("AlbumUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
		 				SharedPreferences.Editor editor2 = myPref2.edit();
		 				editor2.putBoolean("Album"+CategaryId+appManager.getAPP_ID(), false);
		 				editor2.putBoolean("AlbumBig"+CategaryId+appManager.getAPP_ID(), true);
		 				editor2.commit();
		 				SharedPreferences indexPrefs = getSharedPreferences("AlbumInfo"+CategaryId+appManager.getAPP_ID(),MODE_PRIVATE);
		 				SharedPreferences.Editor indexEditor = indexPrefs.edit();
						indexEditor.putBoolean("AlbumInfo", false);
						indexEditor.commit();
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
				
		 	} 
			super.onPostExecute(result);
		 }
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
			return albumSubDetails.size();
		}

		public View getView(int position, View convertView, ViewGroup parent) 
		{
			if ( convertView == null )
			{
				convertView = getLayoutInflater().inflate(R.layout.photos_album_sub, null);
			}
			try{
				ImageView iv = (ImageView)convertView.findViewById(R.id.alb_sub_bg);
				iv.setImageBitmap(albumSubDetails.get(position).getBitmapSmall());
				
				Display display = getWindowManager().getDefaultDisplay(); 
	     	 	int width = display.getWidth();
	     	 	
	     	 	int squareDimen = (width/3);
	     	 	iv.getLayoutParams().height = squareDimen-8;
     	 		iv.getLayoutParams().width = squareDimen;
	     	 	iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
	     		
			}catch (Exception e) {e.printStackTrace();}
			return convertView;
		}

		
		public Object getItem(int arg0) 
		{
			// TODO Auto-generated method stub
			return null;
		}

		
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	
	public Bitmap getResizedBitmap(Bitmap srcBitmap, int newWidth, int newHeight)
	  {
	    try
	    {
	      return Bitmap.createScaledBitmap(srcBitmap, newWidth, newHeight, true);
	    }
	    catch(Exception e) {}
	    
	    return null;
	  }
	
	

	@Override
	 protected Dialog onCreateDialog(int id) {
	     switch (id) {
	         case AppManager.PROGRESS:
	             final ProgressDialog progDialog = new ProgressDialog(this);
	             progDialog.setMessage("Loading, Please wait...");
	             progDialog.setCanceledOnTouchOutside(false);
	             progDialog.setCancelable(false);
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
				Utility.killMyApp(getApplicationContext(),SLPhotoAlbumActivity.this);
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
		mGaTracker.sendView("Album_"+CategaryId);
	}
}
