package com.snaplion.fanwall.photo;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snaplion.fanwall.CustomCommentButton;
import com.snaplion.fanwall.CustomLikeButton;
import com.snaplion.fanwall.FanwalLoginManager;
import com.snaplion.fanwall.FanwallComment;
import com.snaplion.fanwall.FanwallSuperActivity;
import com.snaplion.fanwall.wall.PostUpdateLocalService;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.locationservices.UserLocationManager;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.photos.lazyloading.ImageLoader;
import com.snaplion.preview.PreviewSnapLionAppsSelectActvity;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;


public class PhotoAlbumGalleryImageComments extends FanwallSuperActivity 
{
	private LinearLayout mainBg = null;
	public static ArrayList<FanwallComment> commentsArrayList = new ArrayList<FanwallComment>();
	
	public static String buyurl;
	static String comments;
	static String description;
	private String urlThumnail;
	static BitmapDrawable background ;
	private ImageAdapter adapter = null;
	private String BackFlag = "yes";
	private String appName = null;
	int Flag = 0;
	
	private ListView commentsListView;
	View listHeaderView;
	
	FanwallComment clicked_post;
	FanwallComment currentHeaderPost;
	String parentPostId;
	//Handler uiRefreshHandler;
	//AppManager appManager;
	public ImageLoader userImageLoader; 
    public ImageLoader msgImageLoader; 
    TextView loadPrevous_TextView;
    ProgressBar loadPrevous_ProgressBar;
    int CommentCounter=10;
    
	public class mWallHandler extends Handler 
	{ 
        public void handleMessage(Message msg) 
        { 
        	switch(msg.arg1)
        	{
        		case FanwalLoginManager.FLAG_LIKE_SUCCES:
        		{
        			FanwallComment likeResultedComment=(FanwallComment)msg.obj;
        			Utility.debug("PhotoAlbumGalleryImageComments like == index "+likeResultedComment.getIndex()+" is like by me :"+likeResultedComment.getLiked_by_me());
        			if(likeResultedComment.getIndex()==999999999)
        			{
        				currentHeaderPost=likeResultedComment;
        				clicked_post.setLiked_by_me(currentHeaderPost.getLiked_by_me());
        				clicked_post.setLikes_total(currentHeaderPost.getLikes_total());
        				clicked_post.setLiked_by(currentHeaderPost.getLiked_by());
        				addHeaderPostData(currentHeaderPost);
        			}
        			else
        			{
        				commentsArrayList.remove(likeResultedComment.getIndex());
            			commentsArrayList.add(likeResultedComment.getIndex(), likeResultedComment);
        			}
        			adapter.notifyDataSetChanged();
        			break;
        		}
        		case FanwalLoginManager.FLAG_ERROR:
        		{
        			String result=(String)msg.obj;
        			Utility.debug("FanwallWallActivity like ==error : "+result);
        			Toast.makeText(PhotoAlbumGalleryImageComments.this, result, Toast.LENGTH_SHORT).show();
        			break;
        		}
        		case PostUpdateLocalService.REQTYPE_UPDATE_COMMENT:
	    		{
	    			Utility.debug("PHOTO COMMENT : recieved message from service.");
	    			PhotoAlbumGalleryImageComments.this.resetUI(false);
	    			break;
	    		}
        	}
        }
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fanwall_comment_detail_activity);
		userImageLoader=new ImageLoader(getApplicationContext(), R.drawable.comment_user_logo);
        msgImageLoader=new ImageLoader(getApplicationContext(), R.drawable.comments_uploaded_img);
		//appManager = AppManager.getInstance();
		handleFanwallTabs(CURRENT_TAB_PHOTOS);
		mHandler = new mWallHandler();
		SnapLionMyAppActivity.KillFlag = false;
		
		initUI();
		setupAdaptersListeners();
		setBottomTabbar();
		setScreenTitle();
		showPreviewAppHomeButton();
		showBackButton();
		setSectionBackgroundImage();
		updateLocations();
		resetUI(true);
	}
	
	public void resetUI(boolean showProgress)
    {
    	try
    	{
    		if(Utility.isOnline(getApplicationContext()))
			{
    			ProgressDialog progress = new ProgressDialog(PhotoAlbumGalleryImageComments.this);
        		progress.setMessage(getResources().getString(R.string.splash_loading_msg));
				new DownloadUserCommentsBGTask(showProgress, progress,0,CommentCounter).execute();
				adapter.notifyDataSetChanged();
			}
			else 
			{
				Utility.showToastMessage(getApplicationContext(), getString(R.string.no_net_not_downloaded));
			}
    		//refreshUIAfterInterval(60000*2);
    		//refreshUIAfterInterval(15000);
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
	
	private void initUI()
	{
		commentsListView=(ListView)findViewById(R.id.commentsListView);
		mainBg = (LinearLayout) findViewById(R.id.fanwall_root_LinearLayout);
		
		Intent i = getIntent();
		catName = i.getStringExtra("FanwallSectionName");
		clicked_post = (FanwallComment)i.getParcelableExtra("CLICKED_POST");
		parentPostId = clicked_post.getPost_id();
	}
	
	private void setupAdaptersListeners()
	{
		listHeaderView = getLayoutInflater().inflate(R.layout.photo_commentlist_headerpost_item, null);
		commentsListView.addHeaderView(listHeaderView, null, false);
		commentsListView.setHeaderDividersEnabled(false);
		adapter = new ImageAdapter();
		commentsListView.setAdapter(adapter);
	}
	
	private class DownloadUserCommentsBGTask extends AsyncTask<Void, Integer, Void> 
	{
		ArrayList<FanwallComment> commentsTmpArrayList = new ArrayList<FanwallComment>();
		String resStr = null;
		boolean showProgress;
		ProgressDialog progress;
		int startIndex, endIndex;
		public DownloadUserCommentsBGTask(boolean showProgress, ProgressDialog progress,int startIndex, int endIndex)
		{
			this.showProgress = showProgress;
			this.progress = progress;
			this.startIndex=startIndex;
			this.endIndex=endIndex;
			
		}
		public void onPreExecute() 
		{
			if(showProgress)
			progress.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) 
		{
			Utility.debug("DownloadUserCommentsBGTask START");
			InputStream in = null;
			try 
			{
				SharedPreferences myPref = getSharedPreferences("FanwallRef"+appManager.getAPP_ID(), MODE_PRIVATE);
				String fanid = myPref.getString("fanid", null);
				String url="";
				if(fanid!=null)
				{
					url = appManager.FANWALL_COMMENT_URL+"?user_id=" + fanid + "&post_id="+parentPostId+"&index_start="+startIndex+"&index_end="+endIndex;
				}
				else
				{
					url = appManager.FANWALL_COMMENT_URL+"?post_id="+parentPostId+"&index_start="+startIndex+"&index_end="+endIndex;
				}
				Utility.debug("get post:"+url);
				in = HttpConnection.connect(url);
				if(in != null)
				{
					resStr = Utility.getString(in);
				}
				if (resStr != "") 
				{
					commentsTmpArrayList = getArtistComments(resStr);
					currentHeaderPost = commentsTmpArrayList.remove(0);
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) 
		{
			addHeaderPostData(currentHeaderPost);
			commentsArrayList.clear();
			commentsArrayList.addAll(commentsTmpArrayList);
			adapter.notifyDataSetChanged();
			if((CommentCounter-commentsTmpArrayList.size())>10)
			{
				CommentCounter=CommentCounter-10;
			}
			commentsTmpArrayList=null;
			System.gc();
			super.onPostExecute(result);
			if(showProgress)
			progress.dismiss();
			((View)findViewById(R.id.horizontalDivider)).setVisibility(View.VISIBLE);
			
			if(loadPrevous_TextView.getVisibility()==TextView.GONE)
			{
				loadPrevous_ProgressBar.setVisibility(ProgressBar.GONE);
				loadPrevous_TextView.setVisibility(TextView.VISIBLE);
			}
			
		}
	}
	
	public ArrayList<FanwallComment> getArtistComments(String resStr)
	{
		try
		{
			ArrayList<FanwallComment> fanwallCommentList = new ArrayList<FanwallComment>();
			
			JSONObject rootJsonObject = new JSONObject(resStr);
			JSONObject postJsonObject = rootJsonObject.getJSONObject("post");
			JSONArray commentsJsonArray = rootJsonObject.getJSONArray("comments");
			
			FanwallComment comment = new FanwallComment();
			comment.setIndex(999999999);
			comment.setParentID("0");
			comment.setPost_id(postJsonObject.getString("post_id"));
			comment.setUser_id(postJsonObject.getString("fan_id"));
			comment.setMessage(postJsonObject.getString("post"));
			comment.setViews(postJsonObject.getString("views"));
			comment.setDate_created(postJsonObject.getString("date_created"));
			comment.setUser_name(postJsonObject.getString("UserName"));
			comment.setLikes_total(postJsonObject.getString("likes"));
			comment.setLiked_by(postJsonObject.getString("likedBy"));
			comment.setUser_photo_url(postJsonObject.getString("user_photo"));
			comment.setMessage_photo_url(postJsonObject.getString("post_photo"));
			comment.setMessage_thumb_url(postJsonObject.getString("post_thumb"));
			comment.setTime_elapsed(postJsonObject.getString("time_elapsed"));
			comment.setLiked_by_me(postJsonObject.getString("liked_by_me"));
			comment.setTotal_comments(postJsonObject.getString("total_comments"));
			fanwallCommentList.add(comment);
			String parentID=comment.getPost_id();
			comment=null;
			
			if(commentsJsonArray!=null)
			{
				for (int i = 0; i < commentsJsonArray.length(); i++) 
				{
					comment = new FanwallComment();
					comment.setIndex(i);
					comment.setParentID(parentID);
					JSONObject jsonObject = commentsJsonArray.getJSONObject(i);
					comment.setPost_id(jsonObject.getString("comment_id"));
					comment.setUser_id(jsonObject.getString("fan_id"));
					comment.setMessage(jsonObject.getString("comment"));
					comment.setDate_created(jsonObject.getString("date_created"));
					comment.setUser_name(jsonObject.getString("UserName"));
					comment.setLikes_total(jsonObject.getString("likes"));
					comment.setLiked_by(jsonObject.getString("likedBy"));
					comment.setUser_photo_url(jsonObject.getString("user_photo"));
					comment.setMessage_photo_url(jsonObject.getString("post_photo"));
					comment.setMessage_thumb_url(jsonObject.getString("post_thumb"));
					comment.setTime_elapsed(jsonObject.getString("time_elapsed"));
					comment.setLiked_by_me(jsonObject.getString("liked_by_me"));
					fanwallCommentList.add(comment);
					comment=null;
			    }
			}
			return fanwallCommentList;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	class ImageAdapter extends BaseAdapter 
	{
		@Override
		public boolean isEnabled(int position) 
		{
			return false;
		}
		@Override
		public void notifyDataSetChanged() 
		{
			super.notifyDataSetChanged();
		}
		
        public int getCount() 
        {
            if (commentsArrayList != null) 
            {
                return commentsArrayList.size();
            }
            return 0;
        }

        public Object getItem(int arg0) 
        {
            return null;
        }

        public long getItemId(int arg0) 
        {
            return 0;
        }
        
        public View getView(int position, View convertView, ViewGroup arg2) 
        {
        	ViewHolder viewHolder;
            if (convertView == null) 
            {
            	convertView =  getLayoutInflater().inflate(R.layout.wall_commentlist_items, null);
            	viewHolder=new ViewHolder();
            	
            	viewHolder.commentBody_TextView=(TextView)convertView.findViewById(R.id.commentBody_TextView);
            	viewHolder.atTime_TextView=(TextView)convertView.findViewById(R.id.atTime_TextView);
            	viewHolder.userName_TextView=(TextView)convertView.findViewById(R.id.userName_TextView);
            	viewHolder.userImage_ProgressBar=(ProgressBar)convertView.findViewById(R.id.userImage_ProgressBar);
            	viewHolder.userImage=(ImageView)convertView.findViewById(R.id.userImage);
            	viewHolder.addComment=(CustomCommentButton)convertView.findViewById(R.id.addComment);
            	viewHolder.addComment.setTotalComments("", R.color.black);
            	viewHolder.addComment.uiHandler.sendEmptyMessage(0);
            	viewHolder.commentBodyImage_ImageViewParent=(RelativeLayout)convertView.findViewById(R.id.commentBodyImage_ImageViewParent);
            	viewHolder.commentBodyImage_ImageView=(ImageView)convertView.findViewById(R.id.commentBodyImage_ImageView);
            	viewHolder.commentBodyImage_ProgressBar=(ProgressBar)convertView.findViewById(R.id.commentBodyImage_ProgressBar);
            	viewHolder.addFavorite=(CustomLikeButton)convertView.findViewById(R.id.addFavorite);
            	viewHolder.likesParentLayout=(RelativeLayout)convertView.findViewById(R.id.likesParentLayout);
            	viewHolder.likedPersonList_TextView=(TextView)convertView.findViewById(R.id.likedPersonList_TextView);
            	viewHolder.replylike_TextView=(TextView)convertView.findViewById(R.id.replylike_TextView);
            	viewHolder.replylikeParent_RelativeLayout=(RelativeLayout)convertView.findViewById(R.id.replylikeParent_RelativeLayout);
            	convertView.setTag(viewHolder);
            }
            else
            {
            	viewHolder=(ViewHolder)convertView.getTag();
            }
        	final FanwallComment comment = commentsArrayList.get(position);
        	convertView.setBackgroundResource(R.drawable.comment_dark_cell_bg);
        	
			viewHolder.commentBody_TextView.setTextColor(getResources().getColor(R.color.white));
			viewHolder.atTime_TextView.setTextColor(getResources().getColor(R.color.white));
			viewHolder.userName_TextView.setTextColor(getResources().getColor(R.color.white));
			viewHolder.addComment.setBackgroundResource(R.drawable.selector_big_comment_plus_gray);
			viewHolder.commentBody_TextView.setText(comment.getMessage());
			viewHolder.atTime_TextView.setText(comment.getTime_elapsed());
			viewHolder.userName_TextView.setText(comment.getUser_name());
			
			if(Integer.parseInt(comment.getLiked_by_me())>0)
			{
				viewHolder.addFavorite.setImageResource(R.drawable.selector_small_heart_red);
			}
			else
			{
				viewHolder.addFavorite.setImageResource(R.drawable.selector_small_heart_gray);
			}
			viewHolder.addFavorite.setTotalComments("", R.color.white);
        	viewHolder.addFavorite.uiHandler.sendEmptyMessage(0);

			if(Integer.parseInt(comment.getLikes_total())>0)
			{
				viewHolder.likesParentLayout.setVisibility(RelativeLayout.VISIBLE);
				viewHolder.likedPersonList_TextView.setText(comment.getLiked_by());
				viewHolder.likedPersonList_TextView.setTextColor(getResources().getColor(R.color.black));
				viewHolder.replylikeParent_RelativeLayout.setVisibility(RelativeLayout.VISIBLE);
				viewHolder.replylike_TextView.setText("Like "+comment.getLikes_total());
				Utility.debug("viewHolder.replylike_TextView : "+viewHolder.replylike_TextView.getText().toString());
				convertView.setPadding(0, 0, 0, 15);
			}
			else
			{
				viewHolder.likesParentLayout.setVisibility(RelativeLayout.GONE);
				viewHolder.replylikeParent_RelativeLayout.setVisibility(RelativeLayout.GONE);
				convertView.setPadding(0, 0, 0, 10);
			}
			
			if(!FanwalLoginManager.getInstance().isUserAlreadyLoggedIn(PhotoAlbumGalleryImageComments.this))
        	{
				viewHolder.likesParentLayout.setVisibility(RelativeLayout.GONE);
				convertView.setPadding(0, 0, 0, 10);
        	}
			
			viewHolder.addFavorite.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					SnapLionMyAppActivity.KillFlag = false;
					FanwalLoginManager.getInstance().likeComment(PhotoAlbumGalleryImageComments.this, comment, catName, 2, 12, CURRENT_TAB_PHOTOS);
				}
			});
			
			if(comment.getMessage_thumb_url()!=null && comment.getMessage_thumb_url().length()>0)
			{
				viewHolder.commentBodyImage_ImageViewParent.setVisibility(ImageView.VISIBLE);
				msgImageLoader.DisplayImage(comment.getMessage_thumb_url(), viewHolder.commentBodyImage_ImageView,viewHolder.commentBodyImage_ProgressBar);
			}
			else
			{
				viewHolder.commentBodyImage_ImageViewParent.setVisibility(ImageView.GONE);
			}
			
			viewHolder.commentBodyImage_ImageViewParent.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					SnapLionMyAppActivity.KillFlag = false;
					FanwalLoginManager.getInstance().viewPicture(PhotoAlbumGalleryImageComments.this, comment, "CMNT", catName);
				}
			});
			
			if(comment.getUser_photo_url()!=null && comment.getUser_photo_url().length()>0)
			{
				viewHolder.userImage.setVisibility(ImageView.VISIBLE);
				userImageLoader.DisplayImage(comment.getUser_photo_url(), viewHolder.userImage,viewHolder.userImage_ProgressBar);
			}
			else
			{
				viewHolder.userImage.setImageResource(R.drawable.comment_user_logo);
			}
			viewHolder.addComment.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					SnapLionMyAppActivity.KillFlag = false;
					FanwalLoginManager.getInstance().addComment(PhotoAlbumGalleryImageComments.this, parentPostId, catName, 2 , 12, CURRENT_TAB_PHOTOS);
					adapter.notifyDataSetChanged();
				}
			});
    		return convertView;
        }
    }
	
	private void addHeaderPostData(final FanwallComment comment)
	{
		try
		{
			Intent intent=new Intent();
			intent.putExtra("RESULTED_POST", clicked_post);
			intent.putExtra("RESULTED_POST_INDEX", clicked_post.getIndex());
			setResult(Activity.RESULT_OK, intent);
			loadPrevous_ProgressBar=(ProgressBar)listHeaderView.findViewById(R.id.loadPrevous_ProgressBar);
			loadPrevous_TextView=(TextView)listHeaderView.findViewById(R.id.loadPrevous_TextView);
			loadPrevous_TextView.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					loadPrevous_ProgressBar.setVisibility(ProgressBar.VISIBLE);
					loadPrevous_TextView.setVisibility(TextView.GONE);
					CommentCounter=CommentCounter+10;
					new DownloadUserCommentsBGTask(false, null,0,CommentCounter).execute();
					adapter.notifyDataSetChanged();
				}
			});
		    
			if(Integer.parseInt(comment.getTotal_comments()) < Integer.parseInt(clicked_post.getTotal_comments()))
        	{
				loadPrevous_TextView.setText("Load previous comment.");
			}
        	else
        	{
        		TableLayout loadMoreRoot =(TableLayout) listHeaderView.findViewById(R.id.loadMoreRoot);
				loadMoreRoot.setVisibility(TableLayout.GONE);
        	}
			
			RelativeLayout rowShadowContainerLayout = (RelativeLayout)listHeaderView.findViewById(R.id.rowShadowContainerLayout);
			rowShadowContainerLayout.setBackgroundResource(R.drawable.comment_light_cell_bg);
	    	ViewHolder viewHolder=new ViewHolder();
	    	viewHolder.commentBodyImage_ImageViewParent=(RelativeLayout)listHeaderView.findViewById(R.id.commentBodyImage_ImageViewParent);
	    	viewHolder.commentBodyImage_ImageView=(ImageView)listHeaderView.findViewById(R.id.commentBodyImage_ImageView);
	    	viewHolder.commentBodyImage_ProgressBar=(ProgressBar)listHeaderView.findViewById(R.id.commentBodyImage_ProgressBar);
        	
	    	viewHolder.commentBody_TextView=(TextView)listHeaderView.findViewById(R.id.commentBody_TextView);
	    	viewHolder.commentBody_TextView.setTextColor(getResources().getColor(R.color.black));
	    	viewHolder.commentBody_TextView.setText(comment.getMessage());
	    	
        	viewHolder.atTime_TextView=(TextView)listHeaderView.findViewById(R.id.atTime_TextView);
        	viewHolder.atTime_TextView.setTextColor(getResources().getColor(R.color.black));
        	viewHolder.atTime_TextView.setText(comment.getTime_elapsed());
        	
        	viewHolder.userName_TextView=(TextView)listHeaderView.findViewById(R.id.userName_TextView);
        	viewHolder.userName_TextView.setTextColor(getResources().getColor(R.color.black));
        	viewHolder.userName_TextView.setText(comment.getUser_name());
        	
        	viewHolder.userImage_ProgressBar=(ProgressBar)listHeaderView.findViewById(R.id.userImage_ProgressBar);
        	viewHolder.userImage=(ImageView)listHeaderView.findViewById(R.id.userImage);
        	viewHolder.addFavorite=(CustomLikeButton)listHeaderView.findViewById(R.id.addFavorite);
        	viewHolder.likesParentLayout=(RelativeLayout)listHeaderView.findViewById(R.id.likesParentLayout);
        	viewHolder.likedPersonList_TextView=(TextView)listHeaderView.findViewById(R.id.likedPersonList_TextView);
        	viewHolder.likedPersonList_TextView.setTextColor(getResources().getColor(R.color.black));
        	
        	
        	if(Integer.parseInt(comment.getLiked_by_me())>0)
			{
				viewHolder.addFavorite.setImageResource(R.drawable.selector_big_heart_red);
			}
			else
			{
				viewHolder.addFavorite.setImageResource(R.drawable.selector_big_heart_black);
			}
        	viewHolder.addFavorite.setTotalComments("", R.color.white);
        	viewHolder.addFavorite.uiHandler.sendEmptyMessage(0);
			
        	viewHolder.addCommentLabel=(TextView)listHeaderView.findViewById(R.id.addCommentLabel);
        	viewHolder.addCommentLabel.setText("Reply "+clicked_post.getTotal_comments());
        	
			
			if(Integer.parseInt(comment.getLikes_total())>0)
			{
				viewHolder.likesParentLayout.setVisibility(RelativeLayout.VISIBLE);
				viewHolder.likedPersonList_TextView.setText(comment.getLiked_by());
				rowShadowContainerLayout.setPadding(0, 0, 0, 15);
			}
			else
			{
				viewHolder.likesParentLayout.setVisibility(RelativeLayout.GONE);
				rowShadowContainerLayout.setPadding(0, 0, 0, 10);
			}
			
			if(!FanwalLoginManager.getInstance().isUserAlreadyLoggedIn(PhotoAlbumGalleryImageComments.this))
        	{
				viewHolder.likesParentLayout.setVisibility(RelativeLayout.GONE);
				rowShadowContainerLayout.setPadding(0, 0, 0, 10);
        	}
			
			viewHolder.addFavoriteLabel=(TextView)listHeaderView.findViewById(R.id.addFavoriteLabel);
			viewHolder.addFavoriteLabel.setText("Like "+comment.getLikes_total());
        	
			viewHolder.addFavorite.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					SnapLionMyAppActivity.KillFlag = false;
					FanwalLoginManager.getInstance().likeComment(PhotoAlbumGalleryImageComments.this, comment, catName, 1, 12, CURRENT_TAB_PHOTOS);
				}
			});
			
			if(comment.getMessage_thumb_url()!=null && comment.getMessage_thumb_url().length()>0)
			{
				viewHolder.commentBodyImage_ImageViewParent.setVisibility(ImageView.VISIBLE);
				msgImageLoader.DisplayImage(FanwalLoginManager.getInstance().getPhoto140(comment.getMessage_photo_url()), viewHolder.commentBodyImage_ImageView,viewHolder.commentBodyImage_ProgressBar);
			}
			else
			{
				viewHolder.commentBodyImage_ImageViewParent.setVisibility(ImageView.GONE);
			}
			
			viewHolder.commentBodyImage_ImageViewParent.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					SnapLionMyAppActivity.KillFlag = false;
					FanwalLoginManager.getInstance().viewPicture(PhotoAlbumGalleryImageComments.this, comment, "CMNT", catName);
				}
			});
			
			if(comment.getUser_photo_url()!=null && comment.getUser_photo_url().length()>0)
			{
				viewHolder.userImage.setVisibility(ImageView.VISIBLE);
				userImageLoader.DisplayImage(comment.getUser_photo_url(), viewHolder.userImage, viewHolder.userImage_ProgressBar);
			}
			else
			{
				viewHolder.userImage.setImageResource(R.drawable.comment_user_logo);
			}
			
			viewHolder.addComment=(CustomCommentButton)listHeaderView.findViewById(R.id.addComment);
			viewHolder.addComment.setTotalComments("", R.color.white);
        	viewHolder.addComment.uiHandler.sendEmptyMessage(0);
			viewHolder.addComment.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					SnapLionMyAppActivity.KillFlag = false;
					FanwalLoginManager.getInstance().addComment(PhotoAlbumGalleryImageComments.this, parentPostId, catName, 2 , 12, CURRENT_TAB_PHOTOS);
					adapter.notifyDataSetChanged();
				}
			});
			listHeaderView.setVisibility(RelativeLayout.VISIBLE);
		}
	    catch (Exception e) 
	    {
	    	e.printStackTrace();
		}
	}
	
	private class ViewHolder
    {
    	public ProgressBar userImage_ProgressBar;
		TextView commentBody_TextView;
    	TextView atTime_TextView;
    	TextView userName_TextView;
    	CustomCommentButton addComment;
    	RelativeLayout commentBodyImage_ImageViewParent;
    	ImageView commentBodyImage_ImageView;
    	ProgressBar commentBodyImage_ProgressBar;
    	CustomLikeButton addFavorite;
    	ImageView userImage;
    	TextView addFavoriteLabel;
    	TextView addCommentLabel;
    	
    	RelativeLayout replylikeParent_RelativeLayout;
    	TextView replylike_TextView;
    	
    	RelativeLayout likesParentLayout;
    	TextView likedPersonList_TextView;
    }
	
	public Bitmap getResizedBitmap(Bitmap srcBitmap, int newWidth, int newHeight)
	{
	    try
	    {
	      return Bitmap.createScaledBitmap(srcBitmap, newWidth, newHeight, true);
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    return null;
	}
	@Override
	protected void onResume() 
	{
		SnapLionMyAppActivity.KillFlag = true;
		System.gc();
		super.onResume();
		doBindService(PostUpdateLocalService.REQTYPE_UPDATE_COMMENT, parentPostId);
	}
	@Override
	protected void onDestroy() 
	{
		try
		{
			if(commentsArrayList!=null)
			commentsArrayList.clear();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		System.gc();
		super.onDestroy();
		doUnbindService();
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
//				Utility.killMyApp(getApplicationContext(),PhotoAlbumGalleryImageComments.this);
//			}catch (Exception e) {e.printStackTrace();}
//		}
//		((View)findViewById(R.id.horizontalDivider)).setVisibility(View.INVISIBLE);
		super.onPause();
	}	

	@Override
	public void onBackPressed() 
	{
		SnapLionMyAppActivity.KillFlag = false;
		super.onBackPressed();
	}
	
	private void updateLocations()
	{
		UserLocationManager userLocationManager=UserLocationManager.getInstance();
    	userLocationManager.updateLocation(this);
	}
	private void setSectionBackgroundImage()
	{
		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("FanwallMainBg"+appManager.getAPP_ID())));
		mainBg.setBackgroundDrawable(background);
		background = null;
	}
	private void showBackButton()
	{
		Button backBtn = (Button)findViewById(R.id.v_sub_back_btn);
		if(BackFlag.equalsIgnoreCase("no"))
		{
			backBtn.setVisibility(View.INVISIBLE);
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
	private void showPreviewAppHomeButton()
	{
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
					
					Intent i = new Intent(PhotoAlbumGalleryImageComments.this,PreviewSnapLionAppsSelectActvity.class);
    				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    				PhotoAlbumGalleryImageComments.this.startActivity(i);
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
					FanwalLoginManager.getInstance().openLoginActivity(PhotoAlbumGalleryImageComments.this, null, catName, CURRENT_TAB_PHOTOS);
				}
			});
		}
	}
	private void setScreenTitle()
	{
		TextView tv = (TextView)findViewById(R.id.v_sub_name_txt);
		tv.setText(catName);
		tv.setTypeface(appManager.lucida_grande_regular);
	}
	private void setBottomTabbar()
	{
		if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1010"))
		{
			Display display = getWindowManager().getDefaultDisplay(); 
			//BackFlag = "no";
			LinearLayout bottomLayout = (LinearLayout)findViewById(R.id.video_bottom_bar);
			bottomLayout.setVisibility(View.VISIBLE);
			
			try
			{
				com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,display.getWidth(),appManager.getAPP_ID(),appName);
			}
			catch (OutOfMemoryError e) 
			{
				e.printStackTrace();
			}
		}
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
					Utility.debug("PhotoAlbumGalleryImageComments : onActivityResult ::: ACTIVITY_CODE_ADD_COMMENT:type="+type+", value="+value);
					if(type.equalsIgnoreCase("JSON"))
					{
						try
						{
							JSONObject rootJsonObject = new JSONObject(value);
							JSONObject postJsonObject = rootJsonObject.getJSONObject("post");
							if(postJsonObject.has("comment_id"))
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
								
						    	commentsArrayList.add(comment);
						    	clicked_post.setTotal_comments(String.valueOf(Integer.parseInt(clicked_post.getTotal_comments())+1));
						    	((TextView)listHeaderView.findViewById(R.id.addCommentLabel)).setText("Reply "+clicked_post.getTotal_comments());
								adapter.notifyDataSetChanged();
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
					Utility.debug("FanwallWallComentDetailsActivity : onActivityResult ::: ACTIVITY_CODE_FANWALL_LOGIN");
					break;
				}
			}
		}
	}
}