package com.snaplion.photos;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snaplion.beans.Album;
import com.snaplion.beans.AlbumImage;
import com.snaplion.beans.AlbumSub;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class SLPhotosActivity extends AppSuperActivity{
	@Override
	protected void onResume() {
		SnapLionMyAppActivity.KillFlag = true;
		super.onResume();
	}
	private RelativeLayout mainBg = null;
	private String bgUrl = null;
	private ArrayList<Album> albumDetails = new ArrayList<Album>();
	private GridView albumGrid = null;
	private ImageAdapter albumAdapter = null;
	private LinearLayout bottomLayout = null;
	private LinearLayout topLayout = null;
	//private LinearLayout playerLayout = null;
	private int Flag = 0;
	private String BackFlag = "yes";
	static ArrayList<AlbumSub> albumSubDetails = new ArrayList<AlbumSub>();
	private ArrayList<AlbumImage> albumImageDetails = new ArrayList<AlbumImage>();
	AppManager appManager;
	
	class PhotoDataDownloader extends AsyncTask<Void, Void, ArrayList<Album>>
	{
		ProgressDialog progress;
		public PhotoDataDownloader(ProgressDialog progress)
		{
			this.progress=progress;
		}
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			progress.show();
		}
		@Override
		protected void onPostExecute(ArrayList<Album> result) 
		{
			super.onPostExecute(result);
			handler.sendEmptyMessage(appManager.DISPLAY);
			progress.dismiss();
		}
		@Override
		protected ArrayList<Album> doInBackground(Void... params) 
		{
			String str=null;
			if(getSharedPreferences("SLPref" + appManager.getAPP_ID(),MODE_PRIVATE).getString("AlbumData", "").equals(""))
			{
				InputStream in = HttpConnection.connect(appManager.PHOTO_SUBFOLDER_DETAILS_URL);
				Utility.debug("PHOTO_SUBFOLDER_DETAILS_URL : "+ appManager.PHOTO_SUBFOLDER_DETAILS_URL);
				if (in != null) 
				{
					str = Utility.getString(in);
				}
			}
			if(str!=null)
			{
				SharedPreferences.Editor editor = getSharedPreferences("SLPref" + appManager.getAPP_ID(),MODE_PRIVATE).edit();
				editor.putString("AlbumData", str);
				editor.commit();
				
				SharedPreferences myPref = getSharedPreferences("SLUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
				String str1 = myPref.getString("MTime"+"1003"+appManager.getAPP_ID(), "");
				SharedPreferences.Editor editor1 = myPref.edit();
				editor1.putBoolean("1003", false);
				editor1.putString("1003"+appManager.getAPP_ID(),str1);
				editor1.commit();
				
				if (Utility.isOnline(getApplicationContext())) 
				{
					getPhotoDetails(1003);
				}				
			}
			return null;
		}
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo);
		Utility.closeAllBelowActivities(this);
		appManager = AppManager.getInstance();
		ProgressDialog progress =new ProgressDialog(this);
		progress.setMessage(getResources().getString(R.string.splash_loading_msg));
		new PhotoDataDownloader(progress).execute();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		Log.d("Test","SLPhotosActivity::onCreate");
		com.snaplion.music.SLMusicActivity.music = false;
		com.snaplion.myapp.SnapLionMyAppActivity.homeMusic = false;
		
		SnapLionMyAppActivity.KillFlag = true;

		
	    Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		
		Intent i = getIntent();
		
		mainBg = (RelativeLayout)findViewById(R.id.album_main_bg);
		bottomLayout = (LinearLayout)findViewById(R.id.album_tab_bar);
		
		if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1003")){
			BackFlag = "no";
			bottomLayout.setVisibility(View.VISIBLE);
			com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,width,appManager.getAPP_ID(),appManager.getAPP_NAME());
		 }

		topLayout = (LinearLayout)findViewById(R.id.photo_top_bar);
		com.snaplion.util.TopClass.getTopView(SLPhotosActivity.this,getApplicationContext(),topLayout,i.getStringExtra("PhotoName"),BackFlag);

		
		//playerLayout = (LinearLayout)findViewById(R.id.photo_player_layout);
		
		albumGrid = (GridView)findViewById(R.id.album_grid);
		albumGrid.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				SnapLionMyAppActivity.KillFlag = false;
				if(albumDetails.size() == 1)
				{
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
						i.putExtra("Index", 1);
						try{
							i.putExtra("AlbumPhotoId", arg2);
							i.putExtra("PhotoName", albumSubDetails.get(arg2).getPhotoname());
						}catch (Exception e) {e.printStackTrace();}
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);	
				}
				else
				{
					if(arg2 >= (albumDetails.size()-1))
					{
						int a = arg2-(albumDetails.size()-1);
						SharedPreferences indPrefs = getSharedPreferences("Index",MODE_PRIVATE);
						SharedPreferences.Editor indexEditor = indPrefs.edit();
						indexEditor.putInt("Index", 0);
						indexEditor.commit();
			    		
						SharedPreferences indexPrefs = getSharedPreferences("currentIndex"+appManager.getAPP_ID(),MODE_PRIVATE);
						SharedPreferences.Editor indexEditor1 = indexPrefs.edit();
						indexEditor1.putInt("currentIndex", a);
						indexEditor.putInt("GalleryIndex",0);
						indexEditor1.commit();
					
						Intent i = new Intent(getApplicationContext(), SLPhotoGalleryActivity1.class);
							i.putExtra("Index", 1);
							try{
								i.putExtra("AlbumPhotoId", a);
								i.putExtra("PhotoName", albumSubDetails.get(a).getPhotoname());
							}catch (Exception e) {e.printStackTrace();}
							i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(i);
					}
					else
					{
						//album
						Intent i = new Intent(getApplicationContext(), SLPhotoAlbumActivity.class);
						i.putExtra("CategaryId", albumDetails.get(arg2+1).getCategaryid());
						i.putExtra("CategaryName", albumDetails.get(arg2+1).getCategoryname());
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
					}
				}
			}
		});
		
		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("AlbumBG"+appManager.getAPP_ID())));
		mainBg.setBackgroundDrawable(background);
		background = null;
	}

	Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	        case AppManager.DISPLAY:
	        	Utility.FileCreate();
//	        	try{
//	        		com.snaplion.music.SLMusicPlayerActivity.seekBarProgress.setSecondaryProgress(com.snaplion.music.SLMusicPlayerActivity.seekbarPosition);
//	        		com.snaplion.music.SLMusicPlayerActivity.getPlayer(SLPhotosActivity.this,getApplicationContext(),playerLayout,appManager.getAPP_ID());
//	        	}catch (Exception e) {e.printStackTrace();}
	        	
	        	SharedPreferences myPref1 = getSharedPreferences("AlbumPref"+appManager.getAPP_ID(), MODE_PRIVATE);
	    		String albumStr = myPref1.getString("AlbumData", "");
	    		
	    		if(albumStr !=""){
	    			Flag = 1;
                	try{albumDetails.clear();}catch (Exception e) {e.printStackTrace();}
	    			albumDetails = getAlbumDetails(albumStr);

	                if(albumDetails != null){
	                	String subAlbumData = myPref1.getString("AlbumSubData"+albumDetails.get(0).getCategaryid(), "");
	                	try{albumSubDetails.clear();}catch (Exception e) {e.printStackTrace();}
	                	if(subAlbumData != "")
	                    {
	                    	albumSubDetails = SLPhotoAlbumActivity.getAlbumSubDetails(subAlbumData);
	                    }
	                }
	                displayAlbums(albumDetails);
	    		 }
	    		break;
	        case AppManager.WEB_CALL:
				if(Flag == 0)
					showDialog(appManager.PROGRESS);
	        	try {
	        		if(Utility.isOnline(getApplicationContext()))
	        		{
	        			String url = appManager.PICS_SUBFOLDERS_DETAILS_URL;
	        			Utility.debug("PICS_SUBFOLDERS_DETAILS_URL : "+appManager.PICS_SUBFOLDERS_DETAILS_URL);
	        			new MyDownloadTask().execute(url);
	        		}
	    		 } catch (Exception e1) {}	
	        	break;
	        case AppManager.ERROR:
				Toast.makeText(getApplicationContext(), "Network Fail.Please try later .!", Toast.LENGTH_LONG).show();
				break;
	        }
	    }
	};

	private void displayAlbums(ArrayList<Album> albumDetails2) {
		try{albumImageDetails.clear();}catch (Exception e) {e.printStackTrace();}
		
		albumImageDetails = new ArrayList<AlbumImage>();
	
		 for(int i = 0;i<(albumDetails2.size()-1);i++)
		 {
			String path = "AlbumPref"+appManager.getAPP_ID()+albumDetails.get(i+1).getCategaryid();
            try {
        		AlbumImage ai = new AlbumImage();
        		ai.setAlbumName(albumDetails2.get(i+1).getCategoryname());
				ai.setBitmap(Utility.FetchImage(path));
				albumImageDetails.add(ai);
            } catch (OutOfMemoryError e) {e.printStackTrace();}
		  }
		 for(int j=0;j<albumSubDetails.size();j++)
		 {
			 String key =  "small"+albumDetails2.get(0).getCategaryid()+albumSubDetails.get(j).getPhotoid()+albumSubDetails.get(0).getAlbumid()+albumSubDetails.get(0).getAlbumname();
			 try {
		       	AlbumImage ai = new AlbumImage();
				ai.setBitmap(Utility.FetchImage(key));
				albumImageDetails.add(ai);
	          } catch (OutOfMemoryError e) {e.printStackTrace();}
		}
		albumAdapter = new ImageAdapter(getApplicationContext());
		albumGrid.setAdapter(albumAdapter);
	}


	private class MyDownloadTask extends AsyncTask<String, Integer, InputStream> {
		@Override
		protected InputStream doInBackground(String... params) {
			InputStream in = null;
			try{
				in = HttpConnection.connect(params[0]);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
           		String line;
           		while ((line = br.readLine()) != null) {
           			sb.append(line);
           		}
				SharedPreferences settings = getSharedPreferences("AlbumPref"+appManager.getAPP_ID(), MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
       		 	editor.putString("AlbumData",sb.toString());
       		 	editor.commit();
			}catch (Exception e) {e.printStackTrace();}
			
			try{
				SharedPreferences myPref1 = getSharedPreferences("AlbumPref"+appManager.getAPP_ID(), MODE_PRIVATE);
				String resStr = myPref1.getString("AlbumData", "");
					if(resStr != ""){
						try{albumDetails.clear();}catch (Exception e) {e.printStackTrace();}
						  albumDetails = getAlbumDetails(resStr);
					  }
					} catch (Exception e) {e.printStackTrace();}
						
			     URL url = null;
				   try {
						url = new URL(Utility.replace(bgUrl," ","%20"));
					    } catch (MalformedURLException e1) {e1.printStackTrace();}
				        
					  //  Bitmap bitmap = null;
				       if(url != null){
					    try {
				            URLConnection connection = url.openConnection();
				            connection.connect();
				            InputStream is = connection.getInputStream();
				            String path = "AlbumBG"+appManager.getAPP_ID();
				            Utility.SaveImageSDCard(is, path);
				        } catch (OutOfMemoryError e) {e.printStackTrace(); } catch (IOException e) {e.printStackTrace();}
				       }
				return null;
		}
		
		@Override
		protected void onPostExecute(InputStream result) {
            if(albumDetails != null)
            {
            	String photolistByCategoryUrl = appManager.PHOTOLIST_BY_CATEGORYID_URL;
				photolistByCategoryUrl = photolistByCategoryUrl.replaceAll("<<CATEGORY_ID>>",albumDetails.get(0).getCategaryid());
				Utility.debug("PHOTOLIST_BY_CATEGORYID_URL : "+photolistByCategoryUrl);
				//String url = appManager.SERVICE_URL+"service_photolist.php?AppId="+appManager.getAPP_ID()+"&CategaryId="+albumDetails.get(0).getCategaryid();
            	new MyDefaultAlbumTask().execute(photolistByCategoryUrl);
            }
		}
	}
	
	private class MyDefaultAlbumTask extends AsyncTask<String, Integer, InputStream> {
		@Override
		protected InputStream doInBackground(String... params) {
			InputStream in = null;
			try{
				in = HttpConnection.connect(params[0]);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
           		String line;
           		while ((line = br.readLine()) != null) {
           			sb.append(line);
           		}
				SharedPreferences settings = getSharedPreferences("AlbumPref"+appManager.getAPP_ID(), MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
       		 	editor.putString("AlbumSubData"+albumDetails.get(0).getCategaryid(),sb.toString());
       		 	editor.commit();
			}catch (Exception e) {e.printStackTrace();}
			
			try{
				SharedPreferences myPref1 = getSharedPreferences("AlbumPref"+appManager.getAPP_ID(), MODE_PRIVATE);
				String resStr = myPref1.getString("AlbumSubData"+albumDetails.get(0).getCategaryid(), "");
				if(resStr != ""){
				try{albumSubDetails.clear();}catch (Exception e) {e.printStackTrace();}
					albumSubDetails = SLPhotoAlbumActivity.getAlbumSubDetails(resStr);
				}
			 } catch (Exception e) {e.printStackTrace();}
			return null;
		}
		
		@Override
		protected void onPostExecute(InputStream result) {
				if(Utility.isOnline(getApplicationContext())){
					for(int i=0;i<albumSubDetails.size();i++){
						URL url = null;
						try {
							String str = albumSubDetails.get(i).getSmallpicture();
							str = Utility.replace(str, " ", "%20");
							url = new URL(str);
						   } catch (Exception e1) {e1.printStackTrace();}	
				        try {
				            URLConnection connection = url.openConnection();
				            connection.connect();
				            InputStream is = connection.getInputStream();
							String key =  "small"+albumDetails.get(0).getCategaryid()+albumSubDetails.get(i).getPhotoid()+albumSubDetails.get(0).getAlbumid()+albumSubDetails.get(0).getAlbumname();
							Utility.SaveImageSDCard(is, key);
							is = null;
			        		AlbumSub ad = new AlbumSub();
				        	try{
				        		ad = albumSubDetails.get(i);
					        	ad.setBitmapSmall(Utility.FetchImage(key));
				        		albumSubDetails.set(i, ad);
					          } catch (OutOfMemoryError e) {e.printStackTrace();
					        }
				           }catch (OutOfMemoryError e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
					}
					new MyDownloadBGTask().execute();
				}
			}
	  }
	
	
	private class MyDownloadBGTask extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			URL url = null;
		for(int i=0;i<albumDetails.size();i++) {
			try {
				String str = albumDetails.get(i).getPicture();
				str = Utility.replace(str, " ", "%20");
				url = new URL(str);
   			 } catch (Exception e1) {e1.printStackTrace();}	
	        try {
	            URLConnection connection = url.openConnection();
	            connection.connect();
	            InputStream is = connection.getInputStream();
	            String path = "AlbumPref"+appManager.getAPP_ID()+albumDetails.get(i).getCategaryid();
	            Utility.SaveImageSDCard(is, path);
	            is = null;
        		Album ad = new Album();
	        	try{
	        		ad = albumDetails.get(i);
	        		ad.setBitmap(Utility.FetchImage(path));
		        	albumDetails.set(i, ad);
		        	} catch (Exception e) {e.printStackTrace();
		          }
	            }catch (OutOfMemoryError e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
			}return null;
	    }
		
		@Override
		protected void onPostExecute(Void result) {
			if(Flag == 0){
	 		 try{
	            displayAlbums(albumDetails);
	    		albumAdapter = new ImageAdapter(getApplicationContext());
	 			albumGrid.setAdapter(albumAdapter);
	 			albumAdapter.notifyDataSetChanged();
		 		dismissDialog(appManager.PROGRESS);
	 		 }catch (Exception e) {e.printStackTrace();}
	 		}
			super.onPostExecute(result);
		}
	}	

	public ArrayList<Album> getAlbumDetails(String resStr) {
		InputStream in =null;
		try{
			in = new ByteArrayInputStream(resStr.getBytes("UTF-8"));
		}catch (Exception e) {
			// TODO: handle exception
		}
		final int albummodify  = 1;
	  	final int categaryid = 2;
        final int categoryname = 3;
        final int ModifiedDate = 4;
        final int albumimage = 5;

        int tagName = 0;

        Album value = null;
        ArrayList<Album> values = new ArrayList<Album>();

        try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, HTTP.UTF_8);
	
        	int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("albummodify")) {
						tagName = albummodify;
						value = new Album();
					} else if (parser.getName().equals("categaryid")) {
						tagName = categaryid;
					}else if (parser.getName().equals("categoryname")) {
						tagName = categoryname;
					}else if (parser.getName().equals("ModifiedDate")) {
						tagName = ModifiedDate;
					}else if (parser.getName().equals("albumimage")) {
						tagName = albumimage;
					}
				}
				if (eventType == XmlPullParser.TEXT) {
					switch (tagName) {
					case albummodify:
						break;
					case categaryid:
						value.setCategaryid(parser.getText());
						break;
					case categoryname:
						value.setCategoryname(parser.getText());
						break;
					case ModifiedDate:
						value.setModifiedDate(parser.getText());
						break;
					case albumimage:
						value.setPicture(parser.getText());
						break;
					default:
						break;
					}
					tagName = 0;
				}
				 
				if (eventType == XmlPullParser.END_TAG) {
	                    if (parser.getName().equals("albummodify")) {
	                    	values.add(value);
	                    	value = null;
	                    }
	                }
	                eventType = parser.next();
	            }
		} catch (XmlPullParserException e) {e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();}
		
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
			return albumImageDetails.size();
		}

		public View getView(int position, View convertView, ViewGroup parent) 
		{
			View MyView = null;
			ImageView iv = null;
			
			if(position >= (albumDetails.size()-1)){
				MyView = getLayoutInflater().inflate(R.layout.photos_album_sub, null);
				try{
					iv = (ImageView)MyView.findViewById(R.id.alb_sub_bg);
					iv.setImageBitmap(albumImageDetails.get(position).getBitmap());
					Display display = getWindowManager().getDefaultDisplay(); 
		     	 	int width = display.getWidth();
		     	 	
		     	 	int squareDimen = (width/3);
		     	 	iv.getLayoutParams().height = squareDimen-8;
	     	 		iv.getLayoutParams().width = squareDimen;
		     	 	iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
				}catch (OutOfMemoryError e) {e.printStackTrace();}catch (Exception e) {e.printStackTrace();}
			}else{
				MyView = getLayoutInflater().inflate(R.layout.photos_album, null);
				try{
					iv = (ImageView)MyView.findViewById(R.id.album_img);
					iv.setImageBitmap(albumImageDetails.get(position).getBitmap());
					TextView tv = (TextView)MyView.findViewById(R.id.album_name);
					tv.setText(albumImageDetails.get(position).getAlbumName());
					tv.setTypeface(appManager.lucida_grande_regular);
					
					Display display = getWindowManager().getDefaultDisplay(); 
					int width = display.getWidth();
					
					int squareDimen = (width/3);
		     	 	iv.getLayoutParams().height = squareDimen-8;
	     	 		iv.getLayoutParams().width = squareDimen;
		     	 	iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
				}catch (OutOfMemoryError e) {e.printStackTrace();}catch (Exception e) {e.printStackTrace();}
			}
			return MyView;
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
				Utility.killMyApp(getApplicationContext(),SLPhotosActivity.this);
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
		mGaTracker.sendView("Photo_Screen");
	}
	
	//////////////////////for loading time minimization/////////////////////
	private static boolean photoFlag = true;
	public void getPhotoDetails(int i2) 
	{
		String str = null;
		File data_directory = new File(appManager.DATA_TEMP_BG);
		if (!data_directory.exists())
			if (data_directory.mkdir())
				;

		InputStream in = HttpConnection.connect(appManager.PHOTO_SUBFOLDER_DETAILS_URL);
		Utility.debug("PHOTO_SUBFOLDER_DETAILS_URL : "+ appManager.PHOTO_SUBFOLDER_DETAILS_URL);
		if (in != null) 
		{
			str = Utility.getString(in);
			in = null;
			try 
			{
				albumDetails = new ArrayList<Album>();
				if (str != "") {
					try {
						albumDetails.clear();
					} catch (Exception e) {
						e.printStackTrace();
						photoFlag = false;
					}
					albumDetails = getAlbumDetails(str);
				} else {
					photoFlag = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				photoFlag = false;
			}
			downLoadAlbumImage(albumDetails, str);
		} else {
			photoFlag = false;
		}
	}
	
	private void downLoadAlbumImage(ArrayList<Album> albumDetails2, String str2) {
		String str1 = null;

		for (int i = 0; i < albumDetails2.size(); i++) {
			URL url = null;
			try {
				String str = albumDetails2.get(i).getPicture();
				str = Utility.replace(str, " ", "%20");
				url = new URL(str);
			} catch (Exception e1) {
				photoFlag = false;
				e1.printStackTrace();
			}
			try {
				if (Utility.isOnline(getApplicationContext())) {
					URLConnection connection = url.openConnection();
					connection.connect();
					InputStream is = connection.getInputStream();
					if (is != null) {
						String path = "AlbumPref" + appManager.getAPP_ID()
								+ albumDetails2.get(i).getCategaryid();
						Utility.SaveImageGPathSDCard(appManager.DATA_TEMP_BG,
								is, path);
						is = null;
					} else {
						photoFlag = false;
					}
				} else {
					photoFlag = false;
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				photoFlag = false;
			} catch (IOException e) {
				e.printStackTrace();
				photoFlag = false;
			}
		}

		try {
			String photolistByCategoryUrl = appManager.PHOTOLIST_BY_CATEGORYID_URL;
			photolistByCategoryUrl = photolistByCategoryUrl.replaceAll(
					"<<CATEGORY_ID>>", albumDetails2.get(0).getCategaryid());
			Utility.debug("PHOTOLIST_BY_CATEGORYID_URL : "
					+ photolistByCategoryUrl);
			InputStream in = HttpConnection.connect(photolistByCategoryUrl);
			// InputStream in =
			// HttpConnection.connect(appManager.SERVICE_URL+"service_photolist.php?AppId="+appManager.getAPP_ID()+"&CategaryId="+albumDetails2.get(0).getCategaryid());
			if (in != null) {
				str1 = Utility.getString(in);
				in = null;
				if (str1 != "") {
					try {
						if (albumSubDetails != null)
							albumSubDetails.clear();
					} catch (Exception e) {
						e.printStackTrace();
					}
					albumSubDetails = new ArrayList<AlbumSub>();
					albumSubDetails = SLPhotoAlbumActivity
							.getAlbumSubDetails(str1);
				} else {
					photoFlag = false;
				}
				for (int i = 0; i < albumSubDetails.size(); i++) {
					URL url = null;
					try {
						String str = albumSubDetails.get(i).getSmallpicture();
						str = Utility.replace(str, " ", "%20");
						url = new URL(str);
					} catch (Exception e1) {
						e1.printStackTrace();
						photoFlag = false;
					}
					try {
						if (Utility.isOnline(getApplicationContext())) {
							URLConnection connection = url.openConnection();
							connection.connect();
							InputStream is = connection.getInputStream();
							String key = "small"
									+ albumDetails.get(0).getCategaryid()
									+ albumSubDetails.get(i).getPhotoid()
									+ albumSubDetails.get(0).getAlbumid()
									+ albumSubDetails.get(0).getAlbumname();
							Utility.SaveImageGPathSDCard(
									appManager.DATA_TEMP_BG, is, key);
							is = null;
							key = null;
						} else {
							photoFlag = false;
						}
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
						photoFlag = false;
					} catch (IOException e) {
						e.printStackTrace();
						photoFlag = false;
					}
				}
			} else {
				photoFlag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			photoFlag = false;
		}

		if (photoFlag) {
			copyTempFile(appManager.DATA_TEMP_BG, appManager.DATA_DIRECTORY);

			SharedPreferences settings = getSharedPreferences("AlbumPref"
					+ appManager.getAPP_ID(), MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("AlbumData", str2);
			editor.putString("AlbumSubData"
					+ albumDetails.get(0).getCategaryid(), str1);
			editor.commit();

			SharedPreferences myPref = getSharedPreferences("AlbumUpdate"
					+ appManager.getAPP_ID(), MODE_PRIVATE);
			for (int i = 0; i < albumDetails.size(); i++) {
				String newId = albumDetails.get(i).getCategaryid();
				String newTime = albumDetails.get(i).getModifiedDate();
				String oldTime = myPref.getString("AlbumTime" + newId
						+ appManager.getAPP_ID(), "");
				if (oldTime.equalsIgnoreCase(newTime)) {
					SharedPreferences myPref1 = getSharedPreferences(
							"AlbumUpdate" + appManager.getAPP_ID(),
							MODE_PRIVATE);
					SharedPreferences.Editor editor1 = myPref1.edit();
					editor1.putBoolean(
							"Album" + albumDetails.get(i).getCategaryid()
									+ appManager.getAPP_ID(), false);
					editor1.putBoolean(
							"AlbumBig" + albumDetails.get(i).getCategaryid()
									+ appManager.getAPP_ID(), false);
					editor1.putString(
							"MAlbum" + albumDetails.get(i).getCategaryid()
									+ appManager.getAPP_ID(),
							albumDetails.get(i).getModifiedDate());
					editor1.commit();
				} else {
					SharedPreferences myPref2 = getSharedPreferences(
							"AlbumUpdate" + appManager.getAPP_ID(),
							MODE_PRIVATE);
					SharedPreferences.Editor editor2 = myPref2.edit();
					editor2.putBoolean(
							"Album" + albumDetails.get(i).getCategaryid()
									+ appManager.getAPP_ID(), true);
					editor2.putBoolean(
							"AlbumBig" + albumDetails.get(i).getCategaryid()
									+ appManager.getAPP_ID(), true);
					editor2.putString(
							"MAlbum" + albumDetails.get(i).getCategaryid()
									+ appManager.getAPP_ID(),
							albumDetails.get(i).getModifiedDate());

					editor2.putString(
							"AlbumTime" + newId + appManager.getAPP_ID(),
							albumDetails.get(i).getModifiedDate());

					editor2.commit();
				}
			}
			albumDetails = null;
			albumSubDetails = null;

			SharedPreferences myPref1 = getSharedPreferences("SLUpdate"
					+ appManager.getAPP_ID(), MODE_PRIVATE);
			String str = myPref1.getString(
					"MTime" + "1003" + appManager.getAPP_ID(), "");
			SharedPreferences.Editor editor1 = myPref1.edit();
			editor1.putBoolean("1003", false);
			editor1.putString("1003" + appManager.getAPP_ID(), str);
			editor1.commit();
			str = null;
		} else {
			Utility.debug("1003 : Not downloaded properly :"
					+ this.getClass().getName());
		}
	}
	
	private void copyTempFile(String dataTempBg, String dataDirectory) {
		File dir2 = new File(dataTempBg);
		File dst = new File(dataDirectory);
		try {
			Utility.copyDirectory(dir2, dst);
			Utility.delete(dir2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
