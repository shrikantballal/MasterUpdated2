package com.snaplion.fanwall.wall;

import java.io.InputStream;

import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import com.snaplion.beans.Module;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class PostUpdateLocalService extends Service implements Runnable
{
	public static final String EXTRA_MESSENGER="com.commonsware.android.downloader.EXTRA_MESSENGER";
	public static final int REQTYPE_UPDATE_POST=301;
	public static final int REQTYPE_UPDATE_COMMENT=302;
	public static final int REQTYPE_UPDATE_PHOTO=303;
	
	private Intent callingIntent;
	private final IBinder mBinder = new LocalBinder();
	
	
	public class LocalBinder extends Binder 
	{
		public PostUpdateLocalService getService() 
        {
            return PostUpdateLocalService.this;
        }
    }
	boolean runLoopFlag=false;
	@Override
	public IBinder onBind(Intent intent) 
	{
		Utility.debug("onBind");
		this.callingIntent=intent;
		runLoopFlag=true;
		Thread t=new Thread(this);
		t.start();
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) 
	{
		runLoopFlag=false;
		return super.onUnbind(intent);
	}
	@Override
	public void run() 
	{
		while(runLoopFlag)
		{
			System.gc();
			try
			{
				if(isUpdateNeeded())
				{
					Messenger messenger=(Messenger)callingIntent.getParcelableExtra(EXTRA_MESSENGER);
					Message msg=Message.obtain();
					msg.arg1=callingIntent.getIntExtra("UPDATE_REQUEST_TYPE", 0);
					msg.obj=true;
					try 
					{
						messenger.send(msg);
					}
					catch (android.os.RemoteException e1) 
					{
						Utility.debug("problem in send msg back to update post");
					}
				}
				Thread.sleep(2*60*1000);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	private boolean isUpdateNeeded()
	{
		switch(callingIntent.getIntExtra("UPDATE_REQUEST_TYPE", 0))
		{
			case REQTYPE_UPDATE_POST:
			{
				return isPostUpdateNeeded();
			}
			case REQTYPE_UPDATE_COMMENT:
			{
				//return isCommentUpdateNeeded();
				return true;
			}
			case REQTYPE_UPDATE_PHOTO:
			{
				return isPhotoUpdateNeeded();
			}
			default:
			{
				Utility.debug("wrong upadte request type");
				break;
			}
		}
		return false;
	}
/////////////////////////////////////////////////POST UPDATE/////////////////////////////////////////////////
	private boolean isPostUpdateNeeded()
	{
		try
		{
			Utility.debug("UPDATE SERVICE : POST UPDATE check start");
			String app_id = callingIntent.getStringExtra("APP_ID");
			String url=AppManager.getInstance().FANWALL_ISPOSTUPDATENEEDED_URL+"?AppId="+app_id;
			Utility.debug("url:"+url);
			InputStream in = HttpConnection.connect(url);
			String jsonStr = Utility.getString(in);
			JSONObject jsonRootObject = new JSONObject(jsonStr);
			JSONObject jsonFanwallObject = jsonRootObject.getJSONObject("Apptab");
			Module fanwallModule=new Module();
			fanwallModule.setTabId(jsonFanwallObject.getString("TabId"));
			fanwallModule.setTabCustomName(jsonFanwallObject.getString("TabCustomName"));
			fanwallModule.setModifiedDate(jsonFanwallObject.getString("ModifiedDate"));
			
			SharedPreferences settings = getSharedPreferences("SLUpdate"+app_id, MODE_PRIVATE);
			String oldTime = settings.getString("FanwallPost_MTime"+fanwallModule.getTabId()+app_id, "");
			String newTime = fanwallModule.getModifiedDate();
			Utility.debug("oldTime:"+oldTime+", newTime"+newTime+", isPostUpdateNeeded : "+(!oldTime.equalsIgnoreCase(newTime)));
			if(!oldTime.equalsIgnoreCase(newTime))
			{
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(fanwallModule.getTabId(), true);
				editor.putString("FanwallPost_MTime"+fanwallModule.getTabId()+app_id,fanwallModule.getModifiedDate());
				editor.commit();
				
				SharedPreferences fanwallPrefs = getSharedPreferences("FanwallRef"+app_id, MODE_PRIVATE);
				SharedPreferences.Editor fanwallPrefsEditor = fanwallPrefs.edit();
				fanwallPrefsEditor.putString("ARTIST_POST_DATA", "");
				fanwallPrefsEditor.commit();
				return true;
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return false;
	}
///////////////////////////////////////////////////COMMENT UPDATE/////////////////////////////////////////////////
	private boolean isCommentUpdateNeeded()
	{
		try
		{
			Utility.debug("UPDATE SERVICE : COMMENT UPDATE check start");
			String app_id = callingIntent.getStringExtra("APP_ID");
			//InputStream in = HttpConnection.connect("http://dev.snaplion.com/dev/fanwalls/tabmodify?AppId="+app_id);
			String postId = callingIntent.getStringExtra("POST_ID");
			String urt=AppManager.getInstance().FANWALL_ISCOMMENTUPDATENEEDED_URL+"?AppId="+app_id+"&PostId="+postId;
			Utility.debug("urt:"+urt);
			InputStream in = HttpConnection.connect(urt);
			
			String jsonStr = Utility.getString(in);
			JSONObject jsonRootObject = new JSONObject(jsonStr);
			JSONObject jsonFanwallObject = jsonRootObject.getJSONObject("Apptab");
			Module fanwallModule=new Module();
			fanwallModule.setTabId(jsonFanwallObject.getString("TabId"));
			fanwallModule.setTabCustomName(jsonFanwallObject.getString("TabCustomName"));
			fanwallModule.setModifiedDate(jsonFanwallObject.getString("ModifiedDate"));
			
			SharedPreferences settings = getSharedPreferences("SLUpdate"+app_id, MODE_PRIVATE);
			String oldTime = settings.getString("FanwallComment_MTime"+fanwallModule.getTabId()+app_id, "");
			String newTime = fanwallModule.getModifiedDate();
			Utility.debug("oldTime:"+oldTime+", newTime"+newTime+", isPostUpdateNeeded : "+(!oldTime.equalsIgnoreCase(newTime)));
			if(!oldTime.equalsIgnoreCase(newTime))
			{
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(fanwallModule.getTabId(), true);
				editor.putString("FanwallComment_MTime"+fanwallModule.getTabId()+app_id,fanwallModule.getModifiedDate());
				editor.commit();
				
				SharedPreferences fanwallPrefs = getSharedPreferences("FanwallRef"+app_id, MODE_PRIVATE);
				SharedPreferences.Editor fanwallPrefsEditor = fanwallPrefs.edit();
				fanwallPrefsEditor.putString("ARTIST_POST_DATA", "");
				fanwallPrefsEditor.commit();
				Utility.debug("isUpdateNeeded:"+true);
				return true;
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		Utility.debug("isUpdateNeeded:"+false);
		return false;
	}
///////////////////////////////////////////////////PHOTO UPDATE/////////////////////////////////////////////////	
	private boolean isPhotoUpdateNeeded()
	{
		try
		{
			Utility.debug("UPDATE SERVICE : PHOTO UPDATE check start");
			String app_id = callingIntent.getStringExtra("APP_ID");
			String url=AppManager.getInstance().FANWALL_ISPHOTOUPDATENEEDED_URL+"?AppId="+app_id;
			Utility.debug("url:"+url);
			InputStream in = HttpConnection.connect(url);
			String jsonStr = Utility.getString(in);
			Utility.debug("jsonStr:"+jsonStr);
			JSONObject jsonRootObject = new JSONObject(jsonStr);
			JSONObject jsonFanwallObject = jsonRootObject.getJSONObject("Apptab");
			Module fanwallModule=new Module();
			fanwallModule.setTabId(jsonFanwallObject.getString("TabId"));
			fanwallModule.setTabCustomName(jsonFanwallObject.getString("TabCustomName"));
			fanwallModule.setModifiedDate(jsonFanwallObject.getString("ModifiedDate"));
			
			SharedPreferences settings = getSharedPreferences("SLUpdate"+app_id, MODE_PRIVATE);
			String oldTime = settings.getString("FanwallPhoto_MTime"+fanwallModule.getTabId()+app_id, "");
			String newTime = fanwallModule.getModifiedDate();
			Utility.debug("oldTime:"+oldTime+", newTime"+newTime+", isPostUpdateNeeded : "+(!oldTime.equalsIgnoreCase(newTime)));
			if(!oldTime.equalsIgnoreCase(newTime))
			{
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(fanwallModule.getTabId(), true);
				editor.putString("FanwallPhoto_MTime"+fanwallModule.getTabId()+app_id,fanwallModule.getModifiedDate());
				editor.commit();
				
				SharedPreferences fanwallPrefs = getSharedPreferences("FanwallRef"+app_id, MODE_PRIVATE);
				SharedPreferences.Editor fanwallPrefsEditor = fanwallPrefs.edit();
				fanwallPrefsEditor.putString("ARTIST_POST_DATA", "");
				fanwallPrefsEditor.commit();
				return true;
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return false;
	}
}
