package com.snaplion.fanwall.wall;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.auth.FacebookHandle;
import com.androidquery.auth.TwitterHandle;
import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.snaplion.fanwall.FanwalLoginManager;
import com.snaplion.fanwall.FanwallComment;
import com.snaplion.fanwall.FanwallSuperActivity;
import com.snaplion.fanwall.LoginActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.locationservices.UserLocationManager;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.preview.PreviewSnapLionAppsSelectActvity;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;


public class FanwallWallActivity extends FanwallSuperActivity 
{
	private LinearLayout mainBg = null;
	
	static String comments;
	static String description;
	static BitmapDrawable background ;
	private TestDynamicViewAdapter adapter = null;
	private String BackFlag = "yes";
	int Flag = 0;
	private Button addNewCommentWallButton;
	private ListView commentsListView;
	
	RelativeLayout allnearbyToggleBtn;
	Handler uiRefreshHandler;
	UserLocationManager userLocationManager;
	//AppManager appManager;
	public static int clicked_position; 
	
	public class mWallHandler extends Handler 
	{ 
        public void handleMessage(Message msg) 
        { 
        	switch(msg.arg1)
        	{
        		case FanwalLoginManager.FLAG_LIKE_SUCCES:
        		{
        			FanwallComment likeResultedComment=(FanwallComment)msg.obj;
        			Utility.debug("FanwallWallActivity like == index "+likeResultedComment.getIndex()+" is like by me :"+likeResultedComment.getLiked_by_me());
        			
        			adapter._model.postArrayList.remove(likeResultedComment.getIndex());
        			adapter._model.postArrayList.add(likeResultedComment.getIndex(), likeResultedComment);
        			adapter.mHandler.sendEmptyMessage(0);
        			//adapter.notifyDataSetChanged();
        			break;
				}
        		case FanwalLoginManager.FLAG_REFRESH_LISTVIEW:
        		{
        			adapter.notifyDataSetChanged();
        			break;
        		}
        		case FanwalLoginManager.FLAG_ERROR:
        		{
        			String result=(String)msg.obj;
        			if(!result.equalsIgnoreCase("success"))
        			{
        				Toast.makeText(FanwallWallActivity.this, result, Toast.LENGTH_SHORT).show();
        			}
        			break;
        		}
        		case PostUpdateLocalService.REQTYPE_UPDATE_POST:
	    		{
	    			Utility.debug("POST : recieved message from service.");
	    			adapter.reloadDataInAdapter();
	    			break;
	    		}
        	}
        }
    };
    private void reStoreLoginInfo()
	{
		FanwalLoginManager.getInstance().FBhandle = new FacebookHandle(this, getResources().getString(R.string.facebook_app_id), LoginActivity.fbPermission)
	    {
			@Override
			public boolean expired(AbstractAjaxCallback<?, ?> cb, AjaxStatus status) 
			{
				Log.d("MTK",status.toString());
				if(status.getCode() == 401)
				{
					return true;
				}
				return super.expired(cb, status);
			}
		};
		FanwalLoginManager.getInstance().TWhandle = new TwitterHandle(this, getResources().getString(R.string.consumerKey), getResources().getString(R.string.consumerSecret))
		{
	        @Override
	        protected void authenticated(String secret, String token) 
	        {
	                AQUtility.debug("secret", secret);
	                AQUtility.debug("token", token);
	        }
	    };
	    
		FanwalLoginManager.getInstance().TWhandle.authenticated();
		FanwalLoginManager.getInstance().FBhandle.authenticated();
	}
    @Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fanwall_wall_activity);
		
		Utility.closeAllBelowActivities(this);
		reStoreLoginInfo();
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		handleFanwallTabs(CURRENT_TAB_WALL);
		mHandler = new mWallHandler();
		SnapLionMyAppActivity.KillFlag = true;
		
		Intent i = getIntent();
		catName = i.getStringExtra("FanwallSectionName");
		
		if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1010"))
		{
			Display display = getWindowManager().getDefaultDisplay(); 
			BackFlag = "no";
			LinearLayout bottomLayout = (LinearLayout)findViewById(R.id.video_bottom_bar);
			bottomLayout.setVisibility(View.VISIBLE);
			
			try
			{
				com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,display.getWidth(),appManager.getAPP_ID(),i.getStringExtra("appname"));
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
					Intent i = new Intent(FanwallWallActivity.this,PreviewSnapLionAppsSelectActvity.class);
    				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    				FanwallWallActivity.this.startActivity(i);
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
					FanwalLoginManager.getInstance().openLoginActivity(FanwallWallActivity.this, null, catName, CURRENT_TAB_WALL);
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
		
		commentsListView = (ListView) findViewById(R.id.album_listview);
		mainBg = (LinearLayout) findViewById(R.id.fanwall_root_LinearLayout);
		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("FanwallMainBg"+appManager.getAPP_ID())));
		
		mainBg.setBackgroundDrawable(background);
		background = null;
		initUI();
		setupAdaptersListeners();
		UserLocationManager userLocationManager=UserLocationManager.getInstance();
    	userLocationManager.updateLocation(this);
    }
	private void initUI()
	{
		addNewCommentWallButton=(Button)findViewById(R.id.addNewCommentWallButton);
		allnearbyToggleBtn=(RelativeLayout)findViewById(R.id.allnearbyParentLayout);
	}
	private void setupAdaptersListeners()
	{
		allnearbyToggleBtn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				final LocationManager manager = (LocationManager)getSystemService( Context.LOCATION_SERVICE );
			    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ))
			    {
			    	SharedPreferences settings = getSharedPreferences("SLUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean("isShowLocation", true);
					editor.commit();
					userLocationManager=UserLocationManager.getInstance();
					userLocationManager.updateLocation(FanwallWallActivity.this);
			    }
			    else
			    {
			    	userLocationManager=UserLocationManager.getInstance();
					userLocationManager.updateLocation(FanwallWallActivity.this);
			    	if(FanwalLoginManager.getInstance().isWallNearBy)
					{
						FanwalLoginManager.getInstance().isWallNearBy=false;
						allnearbyToggleBtn.setBackgroundResource(R.drawable.all_nearme_enable_all);
					}
					else
					{
						FanwalLoginManager.getInstance().isWallNearBy=true;
						allnearbyToggleBtn.setBackgroundResource(R.drawable.all_nearme_enable_nearme);
					}
					FanwalLoginManager.getInstance().isWallNearByToggled=true;
					adapter.reloadDataInAdapter();
					System.gc();
			    }
			}
		});
		addNewCommentWallButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				SnapLionMyAppActivity.KillFlag = false;
				FanwalLoginManager.getInstance().addComment(FanwallWallActivity.this, "0", catName, 1 , 12, CURRENT_TAB_WALL);
			}
		});
		
//      adapter = new ImageAdapter();
//		SharedPreferences fanwallRef = getSharedPreferences("FanwallRef"+appManager.getAPP_ID(), Activity.MODE_PRIVATE);
//		Editor editor = fanwallRef.edit();
//		editor.putBoolean("LOAD_CACHED_POST", true);
//		editor.commit();
		
		adapter = new TestDynamicViewAdapter(this, new com.snaplion.fanwall.wall.TestDynamicModel(this), catName);
		
		commentsListView.setAdapter(adapter);
		commentsListView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				SnapLionMyAppActivity.KillFlag=false;
				FanwallComment clicked_post = (FanwallComment)adapter.getItem(arg2).value;
				Intent i1010 = new Intent(getApplicationContext(), FanwallWallComentDetailsActivity.class);
				i1010.putExtra("CLICKED_POST",clicked_post);
				i1010.putExtra("FanwallSectionName",catName);
				//i1010.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
				startActivityForResult(i1010, FanwalLoginManager.ACTIVITY_CODE_SHOW_COMMENTS);
				System.gc();
			}
		});
	}

	@Override
	protected void onResume() 
	{
		SnapLionMyAppActivity.KillFlag = true;
		System.gc();
		super.onResume();
		doBindService(PostUpdateLocalService.REQTYPE_UPDATE_POST, null);
	}
	@Override
	protected void onDestroy() 
	{
		System.gc();
		super.onDestroy();
		doUnbindService() ;
//		TestDynamicModel.postArrayList.clear();
//		TestDynamicModel.postArrayList=null;
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
//				Utility.killMyApp(getApplicationContext(),FanwallWallActivity.this);
//			}catch (Exception e) {e.printStackTrace();}
//		}
//		((View)findViewById(R.id.horizontalDivider)).setVisibility(View.INVISIBLE);
//		System.gc();
		super.onPause();
	}	

	@Override
	public void onBackPressed() 
	{
		SnapLionMyAppActivity.KillFlag = false;
		System.gc();
		super.onBackPressed();
	}	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK)
		{
			switch(requestCode)
			{
				case FanwalLoginManager.ACTIVITY_CODE_ADD_COMMENT:
				{
					String type = data.getStringExtra("TYPE");
					String value = data.getStringExtra("VALUE");
					Utility.debug("FanwallWallActivity : onActivityResult ::: ACTIVITY_CODE_ADD_COMMENT:type="+type+", value="+value);
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
						    	adapter.addItemAt(comment,0, 0);
						    	adapter._model.postArrayList.add(0, comment);
						    	adapter.reIndex();
						    	
						    	Message msg=new Message();
						    	msg.arg1=FanwalLoginManager.FLAG_REFRESH_LISTVIEW;
								msg.obj="";
								mHandler.sendMessage(msg);
							}
							else if(postJsonObject.has("comment"))
							{
								FanwallComment post = (FanwallComment)adapter.getItem(FanwallWallActivity.clicked_position).value;
								post.setTotal_comments(String.valueOf(Integer.parseInt(post.getTotal_comments())+1));
								adapter.getItem(FanwallWallActivity.clicked_position).value=post;
								adapter.notifyDataSetChanged();
								
//								SnapLionMyAppActivity.KillFlag=false;
//								Intent i1010 = new Intent(getApplicationContext(), FanwallWallComentDetailsActivity.class);
//								FanwallComment clicked_post = (FanwallComment)adapter.getItem(FanwallWallActivity.clicked_position).value;
//								i1010.putExtra("CLICKED_POST",clicked_post);
//								i1010.putExtra("FanwallSectionName",catName);
//								i1010.putExtra("refreshByThread", false);
//								startActivityForResult(i1010, FanwalLoginManager.ACTIVITY_CODE_SHOW_COMMENTS);
//								System.gc();
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
					Utility.debug("FanwallWallActivity : onActivityResult ::: ACTIVITY_CODE_FANWALL_LOGIN");
					Message msg=Message.obtain();
					msg.arg1=PostUpdateLocalService.REQTYPE_UPDATE_POST;
					msg.obj=true;
					mHandler.sendMessage(msg);
					break;
				}
				case FanwalLoginManager.ACTIVITY_CODE_SHOW_COMMENTS:
				{
					Utility.debug("FanwallWallActivity : onActivityResult ::: ACTIVITY_CODE_SHOW_COMMENTS");
					FanwallComment return_resulted_post = (FanwallComment)data.getParcelableExtra("RESULTED_POST");
					int return_resulted_post_index = data.getIntExtra("RESULTED_POST_INDEX", 999999999);
					if(return_resulted_post!=null && return_resulted_post_index != 999999999)
					{
						Utility.debug("return_resulted_post_index:"+return_resulted_post_index);
						return_resulted_post.setIndex(return_resulted_post_index);
						adapter.getItem(return_resulted_post.getIndex()).value=return_resulted_post;
						adapter.notifyDataSetChanged();
					}
					break;
				}
			}
		}
	}
	@Override
	protected void onStart() 
	{
		super.onStart();
		mGaTracker.sendView("Fanwall_Wall_Screen");
	}
}

