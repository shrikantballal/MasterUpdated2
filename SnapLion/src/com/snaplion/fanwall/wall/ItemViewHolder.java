package com.snaplion.fanwall.wall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.codinglines.lib.dynamiclist.AdapterItem;
import com.codinglines.lib.dynamiclist.ViewHolder;
import com.snaplion.fanwall.CustomCommentButton;
import com.snaplion.fanwall.CustomLikeButton;
import com.snaplion.fanwall.FanwalLoginManager;
import com.snaplion.fanwall.FanwallAddCommentActivity;
import com.snaplion.fanwall.FanwallComment;
import com.snaplion.fanwall.LoginActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.util.AppManager;

public class ItemViewHolder extends ViewHolder
{
    public final static int ResourceId = R.layout.wall_postlist_items;
    
    private TextView commentBody_TextView;
    private TextView atTime_TextView;
    private TextView userName_TextView;
	
    private CustomCommentButton addComment;
    RelativeLayout commentBodyImage_ImageViewParent;
    private ImageView commentBodyImage_ImageView;
    private ProgressBar commentBodyImage_ProgressBar;
    private CustomLikeButton addFavorite;
    ProgressBar userImage_ProgressBar;
    private ImageView userImage;
    View convertView;
    AppManager appManager;
    FanwallWallActivity activity;
    String catName;
    AQuery aq;
    //public ImageLoader userImageLoader; 
    //public ImageLoader msgImageLoader; 
    public ItemViewHolder(View convertView,FanwallWallActivity activity,String catName)
    {
        super(convertView, ResourceId);  
        //userImageLoader=new ImageLoader(activity.getApplicationContext(), R.drawable.comment_user_logo);
        //msgImageLoader=new ImageLoader(activity.getApplicationContext(), R.drawable.comments_uploaded_img);
        this.catName=catName;
        appManager=AppManager.getInstance();
        this.convertView = convertView;
        this.activity=activity;
        
        aq=new AQuery(activity);
        commentBody_TextView=(TextView)convertView.findViewById(R.id.commentBody_TextView);
    	atTime_TextView=(TextView)convertView.findViewById(R.id.atTime_TextView);
    	userName_TextView=(TextView)convertView.findViewById(R.id.userName_TextView);
    	addComment=(CustomCommentButton)convertView.findViewById(R.id.addComment);
    	
    	commentBodyImage_ImageViewParent=(RelativeLayout)convertView.findViewById(R.id.commentBodyImage_ImageViewParent);
    	commentBodyImage_ImageView=(ImageView)convertView.findViewById(R.id.commentBodyImage_ImageView);
    	commentBodyImage_ProgressBar=(ProgressBar)convertView.findViewById(R.id.commentBodyImage_ProgressBar);
    	addFavorite=(CustomLikeButton)convertView.findViewById(R.id.addFavorite);
    	userImage_ProgressBar=(ProgressBar)convertView.findViewById(R.id.userImage_ProgressBar);
    	userImage=(ImageView)convertView.findViewById(R.id.userImage);
    }
    
    public void render(AdapterItem item)
    {
    	final FanwallComment comment=(FanwallComment)item.value; 
    	try
        {
        	//commentBody_TextView.setEllipsize(TextUtils.TruncateAt.END);
        	commentBody_TextView.setMaxLines(4);
        	
        	int[] colors = new int[] { R.drawable.comment_dark_cell_bg,R.drawable.comment_light_cell_bg };
			int colorPos = comment.getIndex() % colors.length;
			switch(colorPos)
			{
    			case 1:
    			{
    				convertView.setBackgroundResource(colors[0]);
        			commentBody_TextView.setTextColor(activity.getResources().getColor(R.color.white));
    				atTime_TextView.setTextColor(activity.getResources().getColor(R.color.white));
    				userName_TextView.setTextColor(activity.getResources().getColor(R.color.white));
    				
    				if(Integer.parseInt(comment.getLiked_by_me())>0)
        			{
    					addFavorite.setImageResource(R.drawable.selector_small_heart_red);
					}
        			else
        			{
        				addFavorite.setImageResource(R.drawable.selector_small_heart_gray);
        			}
    					
    				String tlComent=comment.getTotal_comments();
    				if(Integer.parseInt(tlComent)>0)
        			{
    					addComment.setBackgroundResource(R.drawable.selector_small_comment_blank_gray);
        				addComment.setTotalComments(tlComent, R.color.black);
        			}
        			else
        			{
        				addComment.setBackgroundResource(R.drawable.selector_small_comment_plus_gray);
        				addComment.setTotalComments("", R.color.black);
        			}
    				break;
    			}
    			case 0:
    			{
    				convertView.setBackgroundResource(colors[1]);
        			commentBody_TextView.setTextColor(activity.getResources().getColor(R.color.black));
    				atTime_TextView.setTextColor(activity.getResources().getColor(R.color.black));
    				userName_TextView.setTextColor(activity.getResources().getColor(R.color.black));
    				
    				if(Integer.parseInt(comment.getLiked_by_me())>0)
        			{
    					addFavorite.setImageResource(R.drawable.selector_small_heart_red);
					}
        			else
        			{
        				addFavorite.setImageResource(R.drawable.selector_small_heart_black);
        			}
    				
    				if(Integer.parseInt(comment.getTotal_comments())>0)
        			{
    					addComment.setBackgroundResource(R.drawable.selector_small_comment_blank_black);
        				addComment.setTotalComments(comment.getTotal_comments(), R.color.white);
        			}
        			else
        			{
        				addComment.setBackgroundResource(R.drawable.selector_small_comment_plus_black);
        				addComment.setTotalComments("", R.color.white);
        			}
        			break;
    			}
			}
			
			addComment.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					SnapLionMyAppActivity.KillFlag = false;
					FanwallWallActivity.clicked_position=comment.getIndex();
					//FanwalLoginManager.getInstance().addComment(activity, comment.getPost_id(), catName, 2 , 12, 1);
					
					Intent i1010 = new Intent();
					i1010.putExtra("FanwallSectionName",catName);
					i1010.putExtra("ParentCommentId",comment.getPost_id());
					i1010.putExtra("CURRENT_TAB", 1);
					if(FanwalLoginManager.getInstance().isUserAlreadyLoggedIn(activity))
					{
						i1010.putExtra("type", 2);
						i1010.putExtra("section_id", 12);
						i1010.putExtra("open_comment_screen", true);
						i1010.putExtra("open_comment_screen_parent_post", comment);
						i1010.setClass(activity.getApplicationContext(), FanwallAddCommentActivity.class);
						activity.startActivityForResult(i1010, FanwalLoginManager.ACTIVITY_CODE_ADD_COMMENT);
					}
					else
					{
						i1010.setClass(activity.getApplicationContext(), LoginActivity.class);
						activity.startActivityForResult(i1010, FanwalLoginManager.ACTIVITY_CODE_FANWALL_LOGIN);
					}
				}
			});
			
			String tlLikes=comment.getLikes_total();
			if(Integer.parseInt(tlLikes)>0)
			{
				addFavorite.setTotalComments(tlLikes, R.color.white);
			}
			else
			{
				addFavorite.setTotalComments("", R.color.white);
			}
			addComment.uiHandler.sendEmptyMessage(0);
			addFavorite.uiHandler.sendEmptyMessage(0);
			addFavorite.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					SnapLionMyAppActivity.KillFlag = false;
					FanwalLoginManager.getInstance().likeComment(activity, comment, catName, 1, 12, 1);
				}
			});
			
			atTime_TextView.setText(comment.getTime_elapsed());
			userName_TextView.setText(comment.getUser_name());
			
			String postTextToDisplay=comment.getMessage();
			if(comment.getMessage_thumb_url()!=null && comment.getMessage_thumb_url().length()>0)
			{
				commentBodyImage_ImageViewParent.setVisibility(ImageView.VISIBLE);
				
				Bitmap bitmap = aq.getCachedImage(comment.getMessage_thumb_url());
		    	if(bitmap!=null)
		    	{
		    		aq.id(commentBodyImage_ImageView).image(bitmap);
		    	}
		    	else
		    	{
		    		aq.id(commentBodyImage_ImageView)
		    		.progress(commentBodyImage_ProgressBar)
		    		.image(comment.getMessage_thumb_url(), false, true);
		    	}
		    	
				if(postTextToDisplay.length()>51)
				{
					postTextToDisplay=postTextToDisplay.substring(0, 50)+"...";
				}
			}
			else
			{
				commentBodyImage_ImageViewParent.setVisibility(ImageView.GONE);
				if(postTextToDisplay.length()>79)
				{
					postTextToDisplay=postTextToDisplay.substring(0, 78)+"...";
				}
			}
			
			commentBody_TextView.setText(postTextToDisplay);
			
			commentBodyImage_ImageViewParent.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					SnapLionMyAppActivity.KillFlag = false;
					FanwalLoginManager.getInstance().viewPicture(activity, comment, "POST", catName);
					System.gc();
				}
			});
			
			if(comment.getUser_photo_url()!=null && comment.getUser_photo_url().length()>0)
			{
				userImage.setVisibility(ImageView.VISIBLE);
				//userImageLoader.DisplayImage(comment.getUser_photo_url(), userImage, userImage_ProgressBar);
				Bitmap bitmap = aq.getCachedImage(comment.getUser_photo_url());
		    	if(bitmap!=null)
		    	{
		    		aq.id(userImage).image(bitmap);
		    	}
		    	else
		    	{
		    		aq.id(userImage)
		    		.progress(userImage_ProgressBar)
		    		.image(comment.getUser_photo_url(), false, true);
		    	}
    		}
			else
			{
				userImage.setImageResource(R.drawable.comment_user_logo);
			}
			addComment.uiHandler.sendEmptyMessage(0);
		}
        catch (Exception e) 
        {
        	e.printStackTrace();
		}
    }
}
