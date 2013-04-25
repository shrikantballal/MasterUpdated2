package com.snaplion.fanwall;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Message;

import com.androidquery.auth.FacebookHandle;
import com.androidquery.auth.TwitterHandle;
import com.snaplion.fanwall.photo.PhotoAlbumGalleryImagePreview;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class FanwalLoginManager 
{
	public FacebookHandle FBhandle;
	public TwitterHandle TWhandle;
	
	public boolean isCommunityVisible;
	public final static int POST_COUNTER_SLICE=10; 
	public boolean isWallNearBy;
	public boolean isWallNearByToggled;
	public boolean isPhotoNearBy; 
	public int selectedPhotoPage=0;
	//twitter
	
	public final static int FLAG_ERROR=100;
	public final static int FLAG_LIKE_SUCCES=101;
	public final static int FLAG_COMMENT_SUCCES=102;
	public final static int FLAG_REFRESH_LISTVIEW=103;
	
	public final static int ACTIVITY_CODE_ADD_COMMENT=201;
	public final static int ACTIVITY_CODE_FANWALL_LOGIN=202;
	public final static int ACTIVITY_CODE_SHOW_COMMENTS=203;
	
	public final static int SHOW_BACK_BUTTON=301;
	public final static int HIDE_BACK_BUTTON=302;
	public final static int SET_TOP_HEADING=303;
	
	AppManager appManager;
	public static FanwalLoginManager fanwalLoginManager;
	private FanwalLoginManager()
	{
		appManager = AppManager.getInstance();
	}
	
	public static FanwalLoginManager getInstance()
	{
		if(fanwalLoginManager==null)
		{
			fanwalLoginManager=new FanwalLoginManager();
		}
		return fanwalLoginManager;
	}
	public String getPhoto140(String photoUrl)
	{
		StringBuffer buf=new StringBuffer(photoUrl);
		int index1 = buf.lastIndexOf("/fanwall/");
		if(index1!=-1)
		{
			String res = buf.insert(index1+"/fanwall/".length(), "thumb140/").toString(); 
			Utility.debug("res : "+res);
			return res;
		}
		return null;
	}
	
	public void viewPicture(FanwallSuperActivity fanwallSuperActivity, FanwallComment comment, String type, String categoryName)
	{
		Intent intent=new Intent(fanwallSuperActivity,PhotoAlbumGalleryImagePreview.class);
//		ArrayList<FanwallComment> tmp=new ArrayList<FanwallComment>();
//		tmp.add(comment);
		//intent.putExtra("FanwallPhotoArrayList", tmp);
//		if(appManager.FanwallPhotoArrayList==null)
//		{
//			appManager.FanwallPhotoArrayList=new ArrayList<FanwallComment>();
//		}
//		else
//		{
//			appManager.FanwallPhotoArrayList.clear();
//		}
//		appManager.FanwallPhotoArrayList.add(comment);
		
		ArrayList<FanwallComment> FanwallPhotoArrayList=new ArrayList<FanwallComment>();
		FanwallPhotoArrayList.add(comment);
		intent.putParcelableArrayListExtra("FanwallPhotoArrayList", FanwallPhotoArrayList);
		intent.putExtra("TYPE", "POST");
		intent.putExtra("Position", 0);
		intent.putExtra("FanwallSectionName",categoryName);
		fanwallSuperActivity.startActivityForResult(intent,ACTIVITY_CODE_SHOW_COMMENTS);
		System.gc();
	}
	
	public boolean addComment(FanwallSuperActivity superActivity, String parentID, String categoryName, int type, int section_id, int currentTab)
	{
		System.gc();
		SnapLionMyAppActivity.KillFlag=false;
		Intent i1010 = new Intent();
		i1010.putExtra("FanwallSectionName",categoryName);
		i1010.putExtra("ParentCommentId",parentID);
		i1010.putExtra("CURRENT_TAB", currentTab);
		if(isUserAlreadyLoggedIn(superActivity))
		{
			i1010.putExtra("type", type);
			i1010.putExtra("section_id", section_id);
			i1010.setClass(superActivity.getApplicationContext(), FanwallAddCommentActivity.class);
			superActivity.startActivityForResult(i1010, ACTIVITY_CODE_ADD_COMMENT);
		}
		else
		{
			i1010.setClass(superActivity.getApplicationContext(), LoginActivity.class);
			superActivity.startActivityForResult(i1010, ACTIVITY_CODE_FANWALL_LOGIN);
		}
		return false;
	}
	public void openLoginActivity(FanwallSuperActivity activity, String parentID, String categoryName, int currentTab)
	{
		System.gc();
		SnapLionMyAppActivity.KillFlag=false;
		Intent i1010 = new Intent();
		i1010.putExtra("FanwallSectionName",categoryName);
		i1010.putExtra("ParentCommentId",parentID);
		i1010.putExtra("CURRENT_TAB", currentTab);
		//i1010.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
		i1010.setClass(activity.getApplicationContext(), LoginActivity.class);
		activity.startActivityForResult(i1010, ACTIVITY_CODE_FANWALL_LOGIN);
	}
	public void likeComment(FanwallSuperActivity superContext, FanwallComment comment, String categoryName, int type, int section_id, int currentTab)
	{
		System.gc();
		if(isUserAlreadyLoggedIn(superContext))
		{
			String fanid = superContext.getSharedPreferences("FanwallRef"+appManager.getAPP_ID(), Activity.MODE_PRIVATE).getString("fanid",null);
			if(fanid!=null)
			{
				if(comment.getLiked_by_me().equalsIgnoreCase("1"))
				{
					new LikeUnlikeAsyncTask(comment,superContext).execute(Integer.parseInt(comment.getPost_id()), 0, type, Integer.parseInt(fanid), section_id);
					//red
				}
				else
				{
					new LikeUnlikeAsyncTask(comment,superContext).execute(Integer.parseInt(comment.getPost_id()), 1, type, Integer.parseInt(fanid), section_id);
					//gray
				}
			}
		}
		else
		{
			SnapLionMyAppActivity.KillFlag=false;
			Intent i1010 = new Intent(superContext.getApplicationContext(), LoginActivity.class);
			i1010.putExtra("FanwallSectionName",categoryName);
			i1010.putExtra("ParentCommentId",comment.getPost_id()); 
			i1010.putExtra("CURRENT_TAB", currentTab);
			//i1010.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
			superContext.startActivityForResult(i1010, ACTIVITY_CODE_FANWALL_LOGIN);
		}
	}
	
	private class LikeUnlikeAsyncTask extends AsyncTask<Integer, Integer, String> 
	{
		boolean flag=false;
		FanwallComment comment;
		FanwallSuperActivity superContext;
		String resStr = null;
		LikeUnlikeAsyncTask(FanwallComment comment,FanwallSuperActivity superContext)
		{
			this.comment=comment;
			this.superContext=superContext;
		}
		
		@Override
		protected String doInBackground(Integer... params) 
		{
			InputStream in = null;
			try 
			{
				Utility.debug("get post:"+appManager.FANWALL_MSGLIKE_URL+"?fanId="+params[3]+"&postId="+params[0]+"&like="+params[1]+"&type="+params[2]+"&section_id="+params[4]+"&appid="+appManager.getAPP_ID());
				in = HttpConnection.connect(appManager.FANWALL_MSGLIKE_URL+"?fanId="+params[3]+"&postId="+params[0]+"&like="+params[1]+"&type="+params[2]+"&section_id="+params[4]+"&appid="+appManager.getAPP_ID());
				if(in != null)
					resStr = Utility.getString(in);
				Utility.debug("like result : "+resStr);
				if(resStr.equalsIgnoreCase("success"))
				{
					comment.setLiked_by_me(String.valueOf(params[1]));
					String username = superContext.getSharedPreferences("FanwallRef"+appManager.getAPP_ID(), Activity.MODE_PRIVATE).getString("username",null);
					if(comment.getLiked_by_me().equalsIgnoreCase("1"))
					{
						if(Integer.parseInt(comment.getLikes_total())>0)
						{
							comment.setLiked_by(comment.getLiked_by()+", "+username);
						}
						else
						{
							comment.setLiked_by(username);
						}
						comment.setLikes_total(String.valueOf(Integer.parseInt(comment.getLikes_total())+1));
						flag=true;
					}
					else
					{
						if(Integer.parseInt(comment.getLikes_total())==1)
						{
							comment.setLiked_by(comment.getLiked_by().substring(0 , comment.getLiked_by().indexOf(username)));
						}
						else
						{
							comment.setLiked_by(comment.getLiked_by().substring(0 , comment.getLiked_by().indexOf(", "+username)));
						}
						comment.setLikes_total(String.valueOf(Integer.parseInt(comment.getLikes_total())-1));
						flag=true;
					}
				}
				else
				{
					flag=false;
				}
				return resStr;
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) 
		{
			super.onPostExecute(result);
			Message msg=new Message();
			if(flag)
			{
				msg.arg1=FLAG_LIKE_SUCCES;
				msg.obj=comment;
			}
			else
			{
				msg.arg1=FLAG_ERROR;
				msg.obj = resStr;
			}
			superContext.mHandler.sendMessage(msg);
		}
	}
	
	public boolean isUserAlreadyLoggedIn(FanwallSuperActivity superActivity)
	{
		String loginType = superActivity.getSharedPreferences("FanwallRef"+appManager.getAPP_ID(), Activity.MODE_PRIVATE).getString("logintype",null);
		if(loginType!=null && loginType.equalsIgnoreCase("facebook") && FBhandle!=null && FBhandle.authenticated())
		{
			Utility.debug("user login type:facebook");
			return true;
		}
		else if(loginType!=null && loginType.equalsIgnoreCase("twitter") && TWhandle!=null && TWhandle.authenticated())
		{
			Utility.debug("user login type:twitter");
			return true;
		}
		else if(loginType!=null && loginType.equalsIgnoreCase("preview_app") && isPreviewAppSessionValid(superActivity))
		{
			Utility.debug("user login type:preview_app");
			return true;
		}
		
		return false;
//		String loginType = superActivity.getSharedPreferences("FanwallRef"+appManager.getAPP_ID(), Activity.MODE_PRIVATE).getString("logintype",null);
//		if(loginType!=null && loginType.equalsIgnoreCase("facebook") && isFacebookSessionValid(superActivity))
//		{
//			Utility.debug("user login type:facebook");
//			return true;
//		}
//		else if(loginType!=null && loginType.equalsIgnoreCase("twitter") && isTwitterSessionValid(superActivity))
//		{
//			Utility.debug("user login type:twitter");
//			return true;
//		}
//		else if(loginType!=null && loginType.equalsIgnoreCase("preview_app") && isPreviewAppSessionValid(superActivity))
//		{
//			Utility.debug("user login type:preview_app");
//			return true;
//		}
//		return false;
	}
	
	public boolean isPreviewAppSessionValid(FanwallSuperActivity superActivity)
	{
		try
		{
			SharedPreferences myPref = superActivity.getSharedPreferences("FanwallRef"+appManager.getAPP_ID(), Activity.MODE_PRIVATE);
			return (myPref.getString("fanid", null)!=null && myPref.getString("username", null)!=null);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return false;
	}
}
