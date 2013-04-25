package com.snaplion.fanwall.photo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.snaplion.fanwall.FanwalLoginManager;
import com.snaplion.fanwall.FanwallComment;
import com.snaplion.fanwall.FanwallSuperActivity;
import com.snaplion.fanwall.wall.PostUpdateLocalService;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.locationservices.UserLocationManager;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.preview.PreviewSnapLionAppsSelectActvity;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;

public class PhotoAlbumGallery  extends FanwallSuperActivity 
{
	public ArrayList<FanwallComment> FanwallPhotoArrayList;
	public ArrayList<ArrayList<FanwallComment>> CONTENT = new ArrayList<ArrayList<FanwallComment>>();
	int pageCount;
	
    private static final Random RANDOM = new Random();
    public PhotoAlbumGalleryAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;
    
    private LinearLayout mainBg = null;
    private String BackFlag = "yes";
    //AppManager appManager;
    UserLocationManager userLocationManager;
    
    Button addNewCommentWallButton;
    RelativeLayout allnearbyToggleBtn;
    public static int currentPageIndex;
    public class mWallHandler extends Handler 
	{ 
        public void handleMessage(Message msg) 
        { 
        	switch(msg.arg1)
        	{
        		case FanwalLoginManager.FLAG_REFRESH_LISTVIEW:
        		{
        			// TODO regenerate all pages
        			FanwallComment photo = (FanwallComment) msg.obj;
        			if(photo.getMessage_thumb_url()!=null && photo.getMessage_thumb_url().length()>1)
        			{
        				FanwallPhotoArrayList.add(0, photo);
            			refreshAdapter();
            		}
        			break;
        		}
        		case FanwalLoginManager.FLAG_ERROR:
        		{
        			String result=(String)msg.obj;
        			Toast.makeText(PhotoAlbumGallery.this, result, Toast.LENGTH_SHORT).show();
        			break;
        		}
        		case PostUpdateLocalService.REQTYPE_UPDATE_PHOTO:
	    		{
	    			System.gc();
	    			Utility.debug("PHOTO : recieved message from service.");
	    			SharedPreferences fanwallRef = getSharedPreferences("FanwallRef"+appManager.getAPP_ID(), Activity.MODE_PRIVATE);
	    			SharedPreferences.Editor editor = fanwallRef.edit();
					editor.putString("PHOTO_DATA", null);
					editor.commit();
	    			new downloadPhtosAsyncTask(null,true).execute();
	    			break;
	    		}
        	}
        }
    };
  
    @Override
    protected void onCreate(Bundle arg0) 
    {
    	super.onCreate(arg0);
    	setContentView(R.layout.fanwall_photo_album_gallery_activity);
    	
    	Utility.closeAllBelowActivities(this); 
    	
    	appManager=AppManager.getInstance();
		/////////////////////////////
    	LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
    	mHandler = new mWallHandler();
    	handleFanwallTabs(CURRENT_TAB_PHOTOS);
		
		SnapLionMyAppActivity.KillFlag = true;
    	catName = getIntent().getStringExtra("FanwallSectionName");
    	
    	if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1010"))
		{
			Display display = getWindowManager().getDefaultDisplay(); 
			BackFlag = "no";
			LinearLayout bottomLayout = (LinearLayout)findViewById(R.id.video_bottom_bar);
			bottomLayout.setVisibility(View.VISIBLE);
			
			try
			{
				com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,display.getWidth(),appManager.getAPP_ID(),appManager.getAPP_ID());
			}
			catch (OutOfMemoryError e) 
			{
				e.printStackTrace();
			}
		}
		
		TextView tv = (TextView)findViewById(R.id.v_sub_name_txt);
		tv.setText(catName);
		tv.setTypeface(appManager.lucida_grande_regular);
		
		Button homeBtn = (Button)findViewById(R.id.v_home_btn);
		if(appManager.PREVIEWAPP_FLAG)
		{
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
					Intent i = new Intent(PhotoAlbumGallery.this,PreviewSnapLionAppsSelectActvity.class);
    				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    				PhotoAlbumGallery.this.startActivity(i);
					finish();
					try
					{
						System.gc();
	    			}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			});
		}
		else
		{
			homeBtn.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					SnapLionMyAppActivity.KillFlag = false;
					FanwalLoginManager.getInstance().openLoginActivity(PhotoAlbumGallery.this, null, catName, CURRENT_TAB_PHOTOS);
				}
			});
		}
		
		Button backBtn = (Button)findViewById(R.id.v_sub_back_btn);
		if(BackFlag.equalsIgnoreCase("no"))
		{
			if(!appManager.getAPP_ID().equalsIgnoreCase("222")) {
				backBtn.setVisibility(View.INVISIBLE);
			} else {
				backBtn.setBackgroundResource(R.drawable.top_bar_logo);
			}
		}
		else
		{
			if(appManager.getAPP_ID().equalsIgnoreCase("222")) {
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
		
		mainBg = (LinearLayout) findViewById(R.id.fanwall_root_LinearLayout);
		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("FanwallMainBg"+appManager.getAPP_ID())));
		mainBg.setBackgroundDrawable(background);
		background = null;
		//------------
		initUI();
		
		ProgressDialog progress = new ProgressDialog(PhotoAlbumGallery.this);
		progress.setMessage(getResources().getString(R.string.splash_loading_msg));
		new downloadPhtosAsyncTask(progress,false).execute();
	}
    
    private void initUI()
    {
    	mPager = (ViewPager)findViewById(R.id.pager);
        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mAdapter = new PhotoAlbumGalleryAdapter(getSupportFragmentManager());
		mPager.setAdapter(mAdapter);
		mIndicator.setViewPager(mPager);
		mPager.setOnPageChangeListener(new OnPageChangeListener() 
		{
			@Override
			public void onPageSelected(int arg0) 
			{
				currentPageIndex=arg0;
				Utility.debug("currentPageIndex:"+currentPageIndex);
				//mAdapter.notifyDataSetChanged();
	            //mIndicator.notifyDataSetChanged();
	            mIndicator.setCurrentItem(currentPageIndex);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
		
        addNewCommentWallButton=(Button)findViewById(R.id.addNewCommentWallButton);
        addNewCommentWallButton.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				FanwalLoginManager.getInstance().addComment(PhotoAlbumGallery.this, "0", catName, 1 , 12, CURRENT_TAB_PHOTOS);
			}
		});
        allnearbyToggleBtn=(RelativeLayout)findViewById(R.id.allnearbyParentLayout);
        allnearbyToggleBtn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				final LocationManager manager = (LocationManager)getSystemService( Context.LOCATION_SERVICE );
			    if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER))
			    {
			    	SharedPreferences settings = getSharedPreferences("SLUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean("isShowLocation", true);
					editor.commit();
					userLocationManager=UserLocationManager.getInstance();
					userLocationManager.updateLocation(PhotoAlbumGallery.this);
			    }
			    else
			    {
			    	userLocationManager=UserLocationManager.getInstance();
					userLocationManager.updateLocation(PhotoAlbumGallery.this);
			    	mPager.removeAllViews();
					mAdapter = new PhotoAlbumGalleryAdapter(getSupportFragmentManager());
					mPager.setAdapter(mAdapter);
					if(FanwalLoginManager.getInstance().isPhotoNearBy)
					{
						FanwalLoginManager.getInstance().isPhotoNearBy=false;
						allnearbyToggleBtn.setBackgroundResource(R.drawable.all_nearme_enable_all);
					}
					else
					{
						FanwalLoginManager.getInstance().isPhotoNearBy=true;
						allnearbyToggleBtn.setBackgroundResource(R.drawable.all_nearme_enable_nearme);
					}
					SharedPreferences fanwallRef = getSharedPreferences("FanwallRef"+appManager.getAPP_ID(), Activity.MODE_PRIVATE);
					Editor edt=fanwallRef.edit();
					edt.putString("PHOTO_DATA", null);
					edt.commit();
					ProgressDialog progress = new ProgressDialog(PhotoAlbumGallery.this);
					progress.setMessage(getResources().getString(R.string.splash_loading_msg));
					new downloadPhtosAsyncTask(progress,false).execute();
					System.gc();
			    }
			}
		});
        
    }
    private class downloadPhtosAsyncTask extends AsyncTask<String, Void, Void> 
	{
    	private boolean successFlag=false;
		boolean refreshFlag;
		String resStr = null;
		ProgressDialog progress;
		SharedPreferences fanwallRef = getSharedPreferences("FanwallRef"+appManager.getAPP_ID(), Activity.MODE_PRIVATE);
		public downloadPhtosAsyncTask(ProgressDialog progress,boolean refreshFlag)
		{
			this.refreshFlag=refreshFlag;
			this.progress = progress;
		}
		public void onPreExecute() 
		{
			if(!refreshFlag)
			progress.show();
		}
		
		@Override
		protected Void doInBackground(String... params)
		{
			Utility.debug("downloadPhtosAsyncTask START");
			System.gc();
			InputStream in = null;
			try 
			{
				String fanid = fanwallRef.getString("fanid", null);
				resStr=fanwallRef.getString("PHOTO_DATA", null);
				String url=null;
				if(resStr==null)
				{
					if(FanwalLoginManager.getInstance().isPhotoNearBy)
					{
						UserLocationManager userLocationManager=UserLocationManager.getInstance();
						Location loc=userLocationManager.getUserCurrentlocation();
						double latitude;
						double longitude;
						if(loc!=null)
						{
							latitude=loc.getLatitude();
							longitude=loc.getLongitude();
						}
						else
						{
							latitude=0;
							longitude=0;
						}
						
						if(fanid!=null)
						{
							url = appManager.FANWALL_PHOTO_URL+"?appid="+appManager.getAPP_ID()+"&user_id="+fanid+"&latitude="+latitude+"&longitude="+longitude;
						}
						else
						{
							url = appManager.FANWALL_PHOTO_URL+"?appid="+appManager.getAPP_ID()+"&latitude="+latitude+"&longitude="+longitude;
						}
					}
					else
					{
						if(fanid!=null)
						{
							url = appManager.FANWALL_PHOTO_URL+"?appid="+appManager.getAPP_ID()+"&user_id="+fanid;
						}
						else
						{
							url = appManager.FANWALL_PHOTO_URL+"?appid="+appManager.getAPP_ID();
						}
					}
					Utility.debug("photo url :"+url);
					in = HttpConnection.connect(url);
					if(in != null)
					{
						resStr = Utility.getString(in);
						
						SharedPreferences.Editor editor = fanwallRef.edit();
						editor.putString("PHOTO_DATA", resStr);
						editor.commit();
					}
				}
				
				if (resStr != null && resStr.length()>0) 
				{
					if(FanwallPhotoArrayList!=null)
					{
						FanwallPhotoArrayList.clear();
						FanwallPhotoArrayList=null;
						System.gc();
					}
					FanwallPhotoArrayList = getPhotos(resStr);
				}
				else
				{
					successFlag=false;
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				successFlag=false;
			}	
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			if(FanwallPhotoArrayList!=null)
			{
				Utility.debug("downloadPhtosAsyncTask END:"+FanwallPhotoArrayList.size());
				refreshAdapter();
			}
			if(!refreshFlag)
	        progress.dismiss();
		}
	}
    private void refreshAdapter()
    {
    	try
    	{
    		System.gc();
    		createPages();
    		mAdapter.notifyDataSetChanged();
            mIndicator.notifyDataSetChanged();
            Intent intent = new Intent();
            intent.setAction("com.snaplion.fanwall.photo.refresbroadcast");
            sendBroadcast(intent);
        }
    	catch(IllegalStateException ex)
    	{
    		ex.printStackTrace();
    		System.gc();
    	}
    }
    
    public ArrayList<FanwallComment> getPhotos(String resStr)
	{
		try
		{
			ArrayList<FanwallComment> fanwallPhotosList = new ArrayList<FanwallComment>();
			JSONArray jsonArray = new JSONArray(resStr);
			for (int i = 0; i < jsonArray.length(); i++) 
			{
				FanwallComment photo = new FanwallComment();
				photo.setIndex(i);	
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				photo.setParentID(jsonObject.getString("parent_id"));
				photo.setPost_id(jsonObject.getString("post_id"));
				photo.setMessage(jsonObject.getString("post"));
				photo.setMessage_photo_url(jsonObject.getString("photo_url"));
				photo.setMessage_thumb_url(jsonObject.getString("thumb_url"));
				photo.setDate_created(jsonObject.getString("date_created"));
				photo.setUser_name(jsonObject.getString("UserName"));
				photo.setLikes_total(jsonObject.getString("likes"));
				photo.setLiked_by(jsonObject.getString("likedBy"));
				photo.setLiked_by_me(jsonObject.getString("likedByMe"));
				photo.setTime_elapsed(jsonObject.getString("date"));
				photo.setPhotos140(jsonObject.getString("photo140"));
				photo.setTotal_comments(jsonObject.getString("total_comments"));
				fanwallPhotosList.add(photo);
		    }
			return fanwallPhotosList;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
    
    private void createPages()
    {
    	if(CONTENT!=null)
    	{
    		CONTENT.clear();
    	}
    	if(FanwallPhotoArrayList!=null && FanwallPhotoArrayList.size()>0)
    	{
    		int listLenght=FanwallPhotoArrayList.size();
    		int counter=0;
    		while(listLenght>16)
    		{
    			ArrayList<FanwallComment>  tmpPhotoArrayList=new ArrayList<FanwallComment>(FanwallPhotoArrayList.subList((counter), (counter+16)));
    			CONTENT.add(tmpPhotoArrayList);
    			listLenght=listLenght-16;
    			counter=(counter+16);
    		}
    		if(listLenght>0)
    		{
    			ArrayList<FanwallComment>  tmpPhotoArrayList=new ArrayList<FanwallComment>(FanwallPhotoArrayList.subList((counter), (counter+listLenght)));
    			CONTENT.add(tmpPhotoArrayList);
    		}
    	}
    	Utility.debug("createPages():"+CONTENT.size());
    }
    
    @Override
	protected void onResume() 
	{
    	FanwalLoginManager.getInstance().selectedPhotoPage=0;
		SnapLionMyAppActivity.KillFlag = true;
		System.gc();
		super.onResume();
		doBindService(PostUpdateLocalService.REQTYPE_UPDATE_PHOTO, null);
	}
	@Override
	protected void onDestroy() 
	{
		System.gc();
		super.onDestroy();
		doUnbindService() ;
	}
	@Override
	protected void onPause()
	{
		doUnbindService();
//		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//	    boolean isScreenOn = powerManager.isScreenOn();
//	    if (!isScreenOn) {
//	    	SnapLionMyAppActivity.KillFlag = false;
//	    }
//		if(SnapLionMyAppActivity.KillFlag)
//		{
//			try{
//				Utility.killMyApp(getApplicationContext(),PhotoAlbumGallery.this);
//			}catch (Exception e) {e.printStackTrace();}
//		}
		//((View)findViewById(R.id.horizontalDivider)).setVisibility(View.INVISIBLE);
		System.gc();
		super.onPause();
	}	

	@Override
	public void onBackPressed() 
	{
		SnapLionMyAppActivity.KillFlag = false;
		System.gc();
		super.onBackPressed();
	}	
    /////////////////////////FanwallSuperActivity methods./////////////////////////////
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK)
		{
			switch(requestCode)
			{
				case FanwalLoginManager.ACTIVITY_CODE_SHOW_COMMENTS:
				{
					Utility.debug("PhotoAlbumGallery : onActivityResult ::: ACTIVITY_CODE_SHOW_COMMENTS");
					break;
				}
				case FanwalLoginManager.ACTIVITY_CODE_ADD_COMMENT:
				{
					String type = data.getStringExtra("TYPE");
					String value = data.getStringExtra("VALUE");
					Utility.debug("PhotoAlbumGallery : onActivityResult ::: ACTIVITY_CODE_ADD_COMMENT:type="+type+", value="+value);
					if(type.equalsIgnoreCase("JSON"))
					{
						try
						{
							JSONObject rootJsonObject = new JSONObject(value);
							JSONObject postJsonObject = rootJsonObject.getJSONObject("post");
							if(postJsonObject.has("post"))
							{
								FanwallComment comment = new FanwallComment();
								comment.setIndex(0);
								if(postJsonObject.has("comment_id"))
								{
									comment.setPost_id(postJsonObject.getString("comment_id"));
								}
								else
								{
									comment.setPost_id(postJsonObject.getString("post_id"));
								}
								
								comment.setUser_id(postJsonObject.getString("fan_id"));
								if(postJsonObject.has("comment"))
								{
									comment.setMessage(postJsonObject.getString("comment"));
								}
								else
								{
									comment.setMessage(postJsonObject.getString("post"));
								}
								
								comment.setDate_created(postJsonObject.getString("date_created"));
								comment.setUser_name(postJsonObject.getString("UserName"));
								comment.setLikes_total(postJsonObject.getString("likes"));
								comment.setLiked_by(postJsonObject.getString("likedBy"));
								comment.setUser_photo_url(postJsonObject.getString("user_photo"));
								comment.setMessage_photo_url(postJsonObject.getString("post_photo"));
								comment.setMessage_thumb_url(postJsonObject.getString("post_thumb"));
								comment.setTime_elapsed(postJsonObject.getString("time_elapsed"));
								comment.setLiked_by_me(postJsonObject.getString("liked_by_me"));
								comment.setTotal_comments("0");
								
								Message msg=new Message();
						    	msg.arg1=FanwalLoginManager.FLAG_REFRESH_LISTVIEW;
								msg.obj=comment;
								mHandler.sendMessage(msg);
							}
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
						}
					}
					else if(type.equalsIgnoreCase("ERROR"))
					{
						Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
					}
					break;
				}
				case FanwalLoginManager.ACTIVITY_CODE_FANWALL_LOGIN:
				{
					Utility.debug("PhotoAlbumGallery : onActivityResult ::: ACTIVITY_CODE_FANWALL_LOGIN");
					break;
				}
			}
		}
	}
    //////////////////////////////////////////////////////////////////
    class PhotoAlbumGalleryAdapter extends FragmentPagerAdapter implements IconPagerAdapter 
    {
    	FragmentManager mgr; 
    	public PhotoAlbumGalleryAdapter(FragmentManager fragmentManager) 
        {
    		super(fragmentManager);
    		this.mgr=fragmentManager;
        }
    	public void removeAllPages()
    	{
    		for(int i = 0; i < mgr.getBackStackEntryCount(); ++i) 
    		{    
    		    mgr.popBackStack();
    		}
    	}
    	@Override
        public Fragment getItem(int position) 
        {
        	System.gc();
        	PhotoAlbumGalleryPageInternalFragment fragment = new PhotoAlbumGalleryPageInternalFragment();
        	fragment.catName=catName;
        	fragment.posIndex=position;
        	return fragment;
        }
        @Override
        public int getCount() 
        {
            return CONTENT.size();
        }

		@Override
		public int getIconResId(int index) 
		{
			return index;
		}
    }
    //////////////////////////////////////////////////////////////////
    class PhotoAlbumGalleryPageInternalFragment extends Fragment 
    {
    	
    	public String catName;
    	public int posIndex;
        PhotoAlbumGalleryPageFragmentAdapter adapter;
        int windowWidth;
        AQuery aq;
        BroadcastReceiver myBroadcastReceiver;
        
        public PhotoAlbumGalleryPageInternalFragment()
        {
        	
        }
        @Override
        public void onResume() 
        {
        	super.onResume();
        	adapter.notifyDataSetChanged();
        	IntentFilter filter = new IntentFilter("com.snaplion.fanwall.photo.refresbroadcast");
        	myBroadcastReceiver=new BroadcastReceiver() 
        	{
    			@Override
    			public void onReceive(Context context, Intent intent) 
    			{
    				//if(posIndex>PhotoAlbumGallery.CONTENT.size())
    					adapter.notifyDataSetChanged();
    			}
    		};
        	getActivity().registerReceiver(myBroadcastReceiver, filter);
        }
        @Override
        public void onPause() 
        {
        	getActivity().unregisterReceiver(myBroadcastReceiver);
        	super.onPause();
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
        {
        	aq=new AQuery(getActivity());
        	View rootLayout = inflater.inflate(R.layout.photo_album_gallery_page_fragment, container, false);
            GridView gridView = (GridView) rootLayout.findViewById(R.id.PhotoAlbumGalleryPageFragment_gridView);
        	float borderPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getActivity().getResources().getDisplayMetrics());
        	windowWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        	windowWidth=windowWidth-Math.round(borderPX);
        	
        	gridView.setLayoutParams(new RelativeLayout.LayoutParams(windowWidth, windowWidth));
        	
        	adapter = new PhotoAlbumGalleryPageFragmentAdapter(getActivity());
     		gridView.setAdapter(adapter);
     		gridView.setOnItemClickListener(new OnItemClickListener() 
     		{
    			public void onItemClick(AdapterView<?> parent, View v,int position, long id) 
    			{
    				if(FanwallPhotoArrayList!=null)
    				{
    					//Toast.makeText(getActivity(),"fanwall photo clicked.", Toast.LENGTH_SHORT).show();
	    			   Intent PhotoAlbumGalleryImagePreviewIntent=new Intent(getActivity(),PhotoAlbumGalleryImagePreview.class);
	    			   FanwallComment photo = CONTENT.get(posIndex).get(position);
	    			   if(photo!=null)
	    			   {
	    				   if(photo.getParentID()!=null)
	    				   {
	    					   System.gc();
	    					   if(photo.getParentID().trim().equalsIgnoreCase("0"))
	    					   {
	    						   PhotoAlbumGalleryImagePreviewIntent.putExtra("TYPE", "POST");
	    					   }
	    					   else
	    					   {
	    						   PhotoAlbumGalleryImagePreviewIntent.putExtra("TYPE", "CMNT");
	    					   }
	    					   PhotoAlbumGalleryImagePreviewIntent.putExtra("com.snaplion.fanwall.wall.FanwallComment", photo);
	    					   PhotoAlbumGalleryImagePreviewIntent.putExtra("FanwallSectionName", catName);
	    					   PhotoAlbumGalleryImagePreviewIntent.putExtra("FanwallPhotoArrayList", FanwallPhotoArrayList);
	    					   PhotoAlbumGalleryImagePreviewIntent.putExtra("Position", (posIndex*16)+position);
	    					   startActivityForResult(PhotoAlbumGalleryImagePreviewIntent,FanwalLoginManager.ACTIVITY_CODE_SHOW_COMMENTS);
	    				   }
	    				   else
	    				   {
	    					   Utility.debug("ERROR:parent id not found in photo, post id:"+photo.getPost_id());
	    				   }
	    			   }
	    			   else
	    			   {
	    				   Utility.debug("ERROR:post object is null.");
	    			   }
    				}
    			}
    		});
     		return rootLayout;
        }
        @Override
        public void onSaveInstanceState(Bundle outState) 
        {
            outState.putInt("position", posIndex);
            super.onSaveInstanceState(outState);
        }
        private class PhotoAlbumGalleryPageFragmentAdapter extends BaseAdapter
        {
        	Activity MyContext;
        	
        	@Override
        	public void unregisterDataSetObserver(DataSetObserver observer) 
        	{
        		if (observer != null) 
        		{
        			super.unregisterDataSetObserver(observer);
        	    }
        	}
        	public PhotoAlbumGalleryPageFragmentAdapter(Activity _MyContext)
        	{
        		MyContext = _MyContext;
        	}
        	public int getCount() 
        	{
        		return CONTENT.get(posIndex).size();
        	}
        	class ViewHolder
        	{
        		ImageView iv;
        		ProgressBar progress;
        	}
        	public View getView(int position, View convertView, ViewGroup parent) 
        	{
        		ViewHolder viewHolder;
        		if(convertView==null)
        		{
        			convertView = MyContext.getLayoutInflater().inflate(R.layout.fanwall_pager_photo_item, null);
        			viewHolder=new ViewHolder();
        			viewHolder.iv=(ImageView)convertView.findViewById(R.id.alb_sub_bg);
        			int squareDimen = ((windowWidth-8)/4);
        			viewHolder.iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        			viewHolder.iv.getLayoutParams().height = squareDimen;
        			viewHolder.iv.getLayoutParams().width = squareDimen;
        			viewHolder.progress = (ProgressBar)convertView.findViewById(R.id.commentBodyImage_ProgressBar);
        			convertView.setTag(viewHolder);
            	}
        		else
        		{
        			viewHolder=(ViewHolder)convertView.getTag();
        		}
        		
        		Bitmap bitmap = aq.getCachedImage(CONTENT.get(posIndex).get(position).getMessage_thumb_url());
    	    	if(bitmap!=null)
    	    	{
    	    		aq.id(viewHolder.iv).image(bitmap);
    	    	}
    	    	else
    	    	{
    	    		aq.id(viewHolder.iv)
    	    		.progress(viewHolder.progress)
    	    		.image(CONTENT.get(posIndex).get(position).getMessage_thumb_url(), false, true);
    	    	}
    	    	return convertView;
        	}

        	public FanwallComment getItem(int arg0) 
        	{
        		return CONTENT.get(posIndex).get(arg0);
        	}

        	public long getItemId(int arg0) 
        	{
        		return arg0;
        	}
        }
    }
    @Override
    protected void onStart() 
    {
    	super.onStart();
    	mGaTracker.sendView("Fanwall_Photo_Screen");
    }
}