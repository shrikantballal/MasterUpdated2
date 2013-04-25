package com.snaplion.fanwall.wall;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.snaplion.fanwall.FanwalLoginManager;
import com.snaplion.fanwall.FanwallComment;
import com.snaplion.kingsxi.R;
import com.snaplion.locationservices.UserLocationManager;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class TestDynamicModel
{
	private final int PageSize = 10;

	private Handler _loadCompleted;

	//public static ArrayList<FanwallComment> postArrayList = new ArrayList<FanwallComment>();
	public ArrayList<FanwallComment> postArrayList = new ArrayList<FanwallComment>();
	AppManager appManager;
	Context context;
	public boolean loadMoreItem=true;
	public TestDynamicModel(Context context)
	{
		appManager = AppManager.getInstance();  
		this.context = context;
	}
	public void loadMore(boolean refreshflag)
	{	
		ProgressDialog progress = new ProgressDialog(context);
		progress.setMessage(context.getResources().getString(R.string.splash_loading_msg));
		(new BackgroundTask(refreshflag,progress)).execute();
	}
	public FanwallComment removeItemAt(int pos)
	{
		return postArrayList.remove(pos);
	}
	public void addItemAt(int pos, FanwallComment comment)
	{
		postArrayList.add(pos, comment);
	}
	public int getCount()
	{
		return postArrayList.size();
	}

	public FanwallComment getItem(int position)
	{
		return postArrayList.get(position);
	}

	public void setLoadCompleted(Handler loadCompleted)
	{
		_loadCompleted = loadCompleted;
	}

	private class BackgroundTask extends AsyncTask<String, Void, Integer> 
	{
		ProgressDialog progress;
		boolean refreshFlag;
		String resStr = null;
		private int startIndex;
		private int endIndex;
		SharedPreferences fanwallRef = context.getSharedPreferences("FanwallRef"+appManager.getAPP_ID(), Activity.MODE_PRIVATE);
		public BackgroundTask(boolean refreshFlag,ProgressDialog progress)
		{
			this.refreshFlag=refreshFlag;
			this.progress=progress;
			
			if(refreshFlag)
			{
				startIndex=0;
				endIndex=postArrayList.size();
			}
			else
			{
				startIndex=postArrayList.size();
				endIndex=startIndex+10;
			}
		}
		public void onPreExecute() 
		{
			if(FanwalLoginManager.getInstance().isWallNearByToggled)
				progress.show();
		}
		@Override
		protected Integer doInBackground(String... params)
		{
			Utility.debug("DownloadArtistPostBGTask START");
			InputStream in = null;
			try 
			{
				//boolean isFirstLoad = fanwallRef.getBoolean("LOAD_CACHED_POST", false);
				//if(!isFirstLoad)
				//{
					String fanid = fanwallRef.getString("fanid", null);
					String url="";
					if(FanwalLoginManager.getInstance().isWallNearBy)
					{
						UserLocationManager userLocationManager=UserLocationManager.getInstance();
						//userLocationManager.updateLocation(context);
						Location loc=userLocationManager.getUserCurrentlocation();
						double latitude=0;
						double longitude=0;
						if(loc!=null)
						{
							latitude=loc.getLatitude();
							longitude=loc.getLongitude();
						}
						
						if(fanid!=null)
						{
							if(loc!=null)
								url = appManager.FANWALL_POST_URL+"?user_id="+fanid+"&appid="+appManager.getAPP_ID()+"&latitude="+latitude+"&longitude="+longitude+"&index_start="+startIndex+"&index_end="+endIndex;
							else
								url = appManager.FANWALL_POST_URL+"?user_id="+fanid+"&appid="+appManager.getAPP_ID()+"&index_start="+startIndex+"&index_end="+endIndex;
						}
						else
						{
							if(loc!=null)
								url = appManager.FANWALL_POST_URL+"?appid="+appManager.getAPP_ID()+"&latitude="+latitude+"&longitude="+longitude+"&index_start="+startIndex+"&index_end="+endIndex;
							else
								url = appManager.FANWALL_POST_URL+"?appid="+appManager.getAPP_ID()+"&index_start="+startIndex+"&index_end="+endIndex;
						}
					}
					else
					{
						if(fanid!=null)
						{
							url = appManager.FANWALL_POST_URL+"?user_id="+fanid+"&appid="+appManager.getAPP_ID()+"&index_start="+startIndex+"&index_end="+endIndex;
						}
						else
						{
							url = appManager.FANWALL_POST_URL+"?appid="+appManager.getAPP_ID()+"&index_start="+startIndex+"&index_end="+endIndex;
						}
					}
					
					Utility.debug("coment url :"+url);
					in = HttpConnection.connect(url);
					if(in != null)
					{
						resStr = Utility.getString(in);
						if(refreshFlag)
						{
							SharedPreferences.Editor editor = fanwallRef.edit();
							editor.putString("ARTIST_POST_DATA", resStr);
							editor.commit();
						}
					}
//				}
//				else
//				{
//					resStr=fanwallRef.getString("ARTIST_POST_DATA", null);
//					SharedPreferences.Editor editor = fanwallRef.edit();
//					editor.putBoolean("LOAD_CACHED_POST", false);
//					editor.commit();
//				}
				
				if (resStr != null && resStr.length()>0 && !resStr.equalsIgnoreCase("[]")) 
				{
					loadMoreItem=true;
					ArrayList<FanwallComment> tmplist = getArtistComments(resStr);
					if(tmplist!=null && tmplist.size()>0)
					{
						if(refreshFlag)
						{
							postArrayList.clear();
						}
						postArrayList.addAll(tmplist);
					}
				}
				else
				{
					loadMoreItem=false;
					return 100;
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				loadMoreItem=false;
			}	
			return 0;
		}
		@Override
		protected void onPostExecute(Integer result)
		{
			if (_loadCompleted != null)
			{
				_loadCompleted.sendEmptyMessage(0);
			}
			if(FanwalLoginManager.getInstance().isWallNearByToggled)
			{
				FanwalLoginManager.getInstance().isWallNearByToggled=false;
				progress.dismiss();
			}
			if(result==100)
			{
				if(FanwalLoginManager.getInstance().isWallNearBy && startIndex==0)
					Toast.makeText(context, "No activity near you.", Toast.LENGTH_SHORT).show();
			}
		}
	}
	public ArrayList<FanwallComment> getArtistComments(String resStr)
	{
		try
		{
			ArrayList<FanwallComment> fanwallCommentList = new ArrayList<FanwallComment>();
			JSONArray jsonArray = new JSONArray(resStr);
			for (int i = 0; i < jsonArray.length(); i++) 
			{
				FanwallComment comment = new FanwallComment();
				comment.setIndex(i);	
				comment.setParentID("0");
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				comment.setPost_id(jsonObject.getString("post_id"));
				comment.setUser_id(jsonObject.getString("fan_id"));
				comment.setMessage(jsonObject.getString("post"));
				comment.setViews(jsonObject.getString("views"));
				comment.setDate_created(jsonObject.getString("date_created"));
				comment.setUser_name(jsonObject.getString("UserName"));
				comment.setLikes_total(jsonObject.getString("likes"));
				comment.setLiked_by(jsonObject.getString("likedBy"));
				comment.setUser_photo_url(jsonObject.getString("user_photo"));
				comment.setMessage_photo_url(jsonObject.getString("post_photo"));
				comment.setMessage_thumb_url(jsonObject.getString("post_thumb"));
				comment.setTime_elapsed(jsonObject.getString("time_elapsed"));
				comment.setLiked_by_me(jsonObject.getString("liked_by_me"));
				comment.setTotal_comments(jsonObject.getString("total_comments"));
				fanwallCommentList.add(comment);
		    }
			return fanwallCommentList;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
}
