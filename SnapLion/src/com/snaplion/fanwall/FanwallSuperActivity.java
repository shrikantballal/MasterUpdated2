package com.snaplion.fanwall;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.snaplion.fanwall.photo.PhotoAlbumGallery;
import com.snaplion.fanwall.wall.FanwallWallActivity;
import com.snaplion.fanwall.wall.PostUpdateLocalService;
import com.snaplion.fanwall.wall.TestDynamicModel;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class FanwallSuperActivity extends AppSuperActivity
{
	public String catName=null;
	public final int CURRENT_TAB_WALL=1;
	public final int CURRENT_TAB_PHOTOS=2;
	public final int CURRENT_TAB_COMMUNITY=3;
	
	private Button openCommunityButton;
	private Button openPhotosButton;
	private Button openWallsButton;
	public Handler mHandler;
	public AppManager appManager;
	
	public void handleFanwallTabs(int currentTab)
	{
		openCommunityButton=(Button)findViewById(R.id.openCommunityButton);
		openPhotosButton=(Button)findViewById(R.id.openPhotosButton);
		openWallsButton=(Button)findViewById(R.id.openWallButton);
		
		resetTabs(currentTab);
		//new FanwallTabsHandler(currentTab).execute();
		
		FanwalLoginManager.getInstance().isCommunityVisible= false;
		setCommunity(openCommunityButton, currentTab);
	}
	class FanwallTabsHandler extends AsyncTask<Void, Void, Boolean>
	{
		int currentTab;
		public FanwallTabsHandler(int currentTab)
		{
			this.currentTab=currentTab;
		}
		@Override
		protected Boolean doInBackground(Void... params) 
		{
			String resStr=null;
	    	try
	    	{
	    		String setting_url=appManager.FANWALL_IsCommunityVisible_URL+"?appid="+appManager.getAPP_ID();
	        	InputStream in = HttpConnection.connect(setting_url);
	    		if(in != null)
	    			resStr = Utility.getString(in);
	    		JSONObject rootJsonObject = new JSONObject(resStr);
	    		String community = rootJsonObject.getString("community");
	    		if(community.equalsIgnoreCase("0"))
	    		{
	    			FanwalLoginManager.getInstance().isCommunityVisible= false;
	    		}
	    		else if(community.equalsIgnoreCase("1"))
	    		{
	    			FanwalLoginManager.getInstance().isCommunityVisible= true;
	    		}
	    		Utility.debug("haldleCommunityVisibility : "+resStr+"=="+community);
	    		return true;
	    	}
	    	catch (Exception e) 
	    	{
	    		e.printStackTrace();
			}
			return false;
		}
		@Override
		protected void onPostExecute(Boolean result) 
		{
			setCommunity(openCommunityButton, currentTab);
			super.onPostExecute(result);
		}
	}
	private void setCommunity(Button commBtn, int currentTab)
	{
		if(!FanwalLoginManager.getInstance().isCommunityVisible)
		{
			commBtn.setBackgroundResource(R.drawable.tab_middle_empty);
			commBtn.setEnabled(false);
		}
	}
	private void resetTabs(int currentTab)
	{
		openCommunityButton.setBackgroundResource(R.drawable.selector_tab_community_desable);
		openPhotosButton.setBackgroundResource(R.drawable.selector_tab_photos_desable);
		openWallsButton.setBackgroundResource(R.drawable.selector_tab_wall_desable);
		
		openCommunityButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
//				
//				System.gc();
//				SnapLionMyAppActivity.KillFlag = false;
//				Intent i1010 = new Intent(getApplicationContext(), FanwallWallActivity.class);
//				i1010.putExtra("FanwallSectionName", catName);
//				i1010.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
//				finish();
//				startActivity(i1010);
			}
		});
		openPhotosButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				//if(appManager.FanwallPhotoArrayList!=null)
				//	appManager.FanwallPhotoArrayList.clear();
//				if(PhotoAlbumGallery.CONTENT!=null)
//					PhotoAlbumGallery.CONTENT.clear();
				System.gc();
				SnapLionMyAppActivity.KillFlag = false;
				Intent i1010 = new Intent(getApplicationContext(), PhotoAlbumGallery.class);
				i1010.putExtra("FanwallSectionName", catName);
				//i1010.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
				finish();
				startActivity(i1010);
			}
		});
		openWallsButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				//if(TestDynamicModel.postArrayList!=null)
				//TestDynamicModel.postArrayList.clear();
				System.gc();
				SnapLionMyAppActivity.KillFlag = false;
				Intent i1010 = new Intent(getApplicationContext(), FanwallWallActivity.class);
				i1010.putExtra("FanwallSectionName", catName);
				//i1010.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
				finish();
				startActivity(i1010);
			}
		});
		switch(currentTab)
		{
			case CURRENT_TAB_WALL:
			{
				openWallsButton.setBackgroundResource(R.drawable.selector_tab_wall_enable);
				openWallsButton.setEnabled(false);
				break;
			}
			case CURRENT_TAB_PHOTOS:
			{
				openPhotosButton.setBackgroundResource(R.drawable.selector_tab_photos_enable);
				openPhotosButton.setEnabled(false);
				break;
			}
			case CURRENT_TAB_COMMUNITY:
			{
				openCommunityButton.setBackgroundResource(R.drawable.selector_tab_community_enable);
				openCommunityButton.setEnabled(false);
				break;
			}
		}
	}
/////////////////////////refresh UI if any change on server/////////////////////////////
	boolean mIsBound;
	private PostUpdateLocalService postUpdateLocalService;
	private ServiceConnection mConnection = new ServiceConnection() 
	{
	    public void onServiceConnected(ComponentName className, IBinder service) 
	    {
	    	postUpdateLocalService = ((PostUpdateLocalService.LocalBinder)service).getService();
	    }
	    public void onServiceDisconnected(ComponentName className) 
	    {
	    	postUpdateLocalService = null;
	    }
	};
	public void doBindService(int updateRequestType, String extraParam)
	{
		Intent i=new Intent(this, PostUpdateLocalService.class);
	    i.putExtra(PostUpdateLocalService.EXTRA_MESSENGER, new Messenger(mHandler));
	    i.putExtra("UPDATE_REQUEST_TYPE", updateRequestType);
	    i.putExtra("APP_ID", appManager.getAPP_ID());
	    i.putExtra("POST_ID", extraParam);
	    bindService(i, mConnection, Context.BIND_AUTO_CREATE);
	    mIsBound = true;
	}
	public void doUnbindService() 
	{
	    if (mIsBound) 
	    {
	        unbindService(mConnection);
	        mIsBound = false;
	    }
	}
//	private Handler updateLocalServiceHandler=new Handler() 
//	{
//	    @Override
//	    public void handleMessage(Message msg) 
//	    {
//	    	switch(msg.arg1)
//	    	{
//	    		case PostUpdateLocalService.REQTYPE_UPDATE_POST:
//	    		{
//	    			Utility.debug("recieved message from service.");
//	    			adapter.reloadDataInAdapter();
//	    			break;
//	    		}
//	    	}
//	    	
//	    }
//	};
}
