package com.snaplion.fanwall.photo;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.snaplion.castcrew.MultibioItem;
import com.snaplion.fanwall.FanwalLoginManager;
import com.snaplion.fanwall.FanwallComment;
import com.snaplion.fanwall.FanwallSuperActivity;
import com.snaplion.fanwall.wall.FanwallWallComentDetailsActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.preview.PreviewSnapLionAppsSelectActvity;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

public class PhotoAlbumGalleryImagePreview extends FanwallSuperActivity 
{
	ProgressBar my_gallery_progress;
	ArrayList<FanwallComment> FanwallPhotoArrayList;
	static ArrayList<String> urlList=new ArrayList<String>();
	private LinearLayout mainBg = null;
	private String BackFlag = "yes";
	
	LinearLayout fanwall_top_layout;
	LinearLayout video_bottom_bar;
	static ImageView PhotoAlbumGalleryImagePreview_sharePhoto;
	static ImageView PhotoAlbumGalleryImagePreview_likeImage;
	static ImageView PhotoAlbumGalleryImagePreview_openComments;
	static TextView PhotoAlbumGalleryImagePreview_atTime;
	static TextView PhotoAlbumGalleryImagePreview_personName;
	static TextView PhotoAlbumGalleryImagePreview_imageCaption;
	static TableLayout bottonsRoot;
	private FanwallComment comment;
	MultibioItem multibioItem;
	//AppManager appManager;
	static String type;
	String openImageFilePath;
	private static int currentIndex=0;
	static ViewPager my_gallery;
	TestAdapter adapter;
	boolean refreshGalleryFlag=true;
	AQuery aq=null;
	private void getURLs()
	{
		SharedPreferences settings = getSharedPreferences("SLUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("12", true);
		editor.putString("FanwallPhoto_MTime"+"12"+appManager.getAPP_ID(),"");
		editor.commit();
		urlList.clear();
		for(int i=0;i<FanwallPhotoArrayList.size();i++)
		{
			urlList.add(FanwallPhotoArrayList.get(i).getMessage_photo_url());
		}
	}
	public class mWallHandler extends Handler 
	{ 
        public void handleMessage(Message msg) 
        { 
        	switch(msg.arg1)
        	{
        		case FanwalLoginManager.FLAG_LIKE_SUCCES:
        		{
        			FanwallComment likeResultedComment=(FanwallComment)msg.obj;
        			FanwallPhotoArrayList.remove(currentIndex);
        			FanwallPhotoArrayList.add(currentIndex, likeResultedComment);
    				Utility.debug("PhotoAlbumGalleryImagePreview ;;; "+likeResultedComment.getIndex()+"==is like by me :"+likeResultedComment.getLiked_by_me());
        			if(likeResultedComment.getLiked_by_me().equalsIgnoreCase("1"))
        			{
        				PhotoAlbumGalleryImagePreview_likeImage.setImageResource(R.drawable.selector_photo_prev_heart_red);
        			}
        			else
        			{
        				PhotoAlbumGalleryImagePreview_likeImage.setImageResource(R.drawable.selector_photo_prev_heart_black);
        			}
        			Intent intent=new Intent();
        			intent.putExtra("RESULTED_POST", likeResultedComment);
        			intent.putExtra("RESULTED_POST_INDEX", likeResultedComment.getIndex());
        			setResult(Activity.RESULT_OK, intent);
        			adapter.notifyDataSetChanged();
        			break;
				}
        		case FanwalLoginManager.FLAG_ERROR:
        		{
        			String result=(String)msg.obj;
        			Toast.makeText(PhotoAlbumGalleryImagePreview.this, result, Toast.LENGTH_SHORT);
        			break;
        		}
        	}
        }
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_album_gallery_image_preview);
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		Intent i = getIntent();
		catName = i.getStringExtra("FanwallSectionName");
		FanwallPhotoArrayList = i.getParcelableArrayListExtra("FanwallPhotoArrayList");
		currentIndex = i.getIntExtra("Position", 0);
		type = i.getStringExtra("TYPE");
		
		comment=FanwallPhotoArrayList.get(currentIndex);
		
		SnapLionMyAppActivity.KillFlag = true;
		
		mHandler = new mWallHandler();
		
		aq=new AQuery(this);
		
		getURLs();
		initUI();
		addListeners();
		updateUI(comment);
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
					Intent i = new Intent(PhotoAlbumGalleryImagePreview.this,PreviewSnapLionAppsSelectActvity.class);
    				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    				PhotoAlbumGalleryImagePreview.this.startActivity(i);
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
			if(type.equalsIgnoreCase("MULTIBIO"))
			{
				homeBtn.setVisibility(Button.INVISIBLE);
			}
			else
			{
				homeBtn.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						FanwalLoginManager.getInstance().openLoginActivity(PhotoAlbumGalleryImagePreview.this, null, catName, CURRENT_TAB_PHOTOS);
					}
				});
			}
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
		
//		mainBg = (LinearLayout) findViewById(R.id.fanwall_root_LinearLayout);
//		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("FanwallMainBg"+appManager.getAPP_ID())));
//		mainBg.setBackgroundDrawable(background);
//		background = null;
	}
	private void updateUI(FanwallComment photoPost)
	{
		openImageFilePath = AQUtility.getCacheFile(AQUtility.getCacheDir(this), photoPost.getMessage_photo_url()).getPath();
		comment = photoPost;
		if(!type.equals("MULTIBIO"))
		{
			mGaTracker.sendView("Fanwall_Photo_"+comment.getPost_id());
			if(photoPost.getLiked_by_me().equalsIgnoreCase("1"))
			{
				PhotoAlbumGalleryImagePreview_likeImage.setImageResource(R.drawable.selector_photo_prev_heart_red);
			}
			else
			{
				PhotoAlbumGalleryImagePreview_likeImage.setImageResource(R.drawable.selector_photo_prev_heart_black);
			}
			PhotoAlbumGalleryImagePreview_atTime.setText(photoPost.getTime_elapsed());
			PhotoAlbumGalleryImagePreview_personName.setEllipsize(TextUtils.TruncateAt.END);
			PhotoAlbumGalleryImagePreview_personName.setText(photoPost.getUser_name());
			PhotoAlbumGalleryImagePreview_imageCaption.setText(photoPost.getMessage());
			if(photoPost.getMessage().equalsIgnoreCase(""))
			{
				PhotoAlbumGalleryImagePreview_imageCaption.setVisibility(TextView.GONE);
			}
			else
			{
				PhotoAlbumGalleryImagePreview_imageCaption.setVisibility(TextView.VISIBLE);
			}
			if(photoPost!=null && photoPost.getParentID()!=null && !photoPost.getParentID().equalsIgnoreCase("0"))
			{
				PhotoAlbumGalleryImagePreview_openComments.setVisibility(ImageView.GONE);
			}
			else
			{
				PhotoAlbumGalleryImagePreview_openComments.setVisibility(ImageView.VISIBLE);
			}
		}
		else
		{
			PhotoAlbumGalleryImagePreview_imageCaption.setText(photoPost.getMessage());
			bottonsRoot.setVisibility(TableLayout.GONE);
		}
	}
	private void initUI()
	{
		my_gallery_progress=(ProgressBar)findViewById(R.id.my_gallery_progress);
		fanwall_top_layout=(LinearLayout)findViewById(R.id.fanwall_top_layout);
		video_bottom_bar=(LinearLayout)findViewById(R.id.video_bottom_bar);
		
		PhotoAlbumGalleryImagePreview_imageCaption=(TextView)findViewById(R.id.PhotoAlbumGalleryImagePreview_imageCaption);
		PhotoAlbumGalleryImagePreview_sharePhoto=(ImageView)findViewById(R.id.PhotoAlbumGalleryImagePreview_sharePhoto);
		PhotoAlbumGalleryImagePreview_likeImage=(ImageView)findViewById(R.id.PhotoAlbumGalleryImagePreview_likeImage);
		PhotoAlbumGalleryImagePreview_openComments=(ImageView)findViewById(R.id.PhotoAlbumGalleryImagePreview_openComments);
		PhotoAlbumGalleryImagePreview_atTime=(TextView)findViewById(R.id.PhotoAlbumGalleryImagePreview_atTime);
		PhotoAlbumGalleryImagePreview_personName=(TextView)findViewById(R.id.PhotoAlbumGalleryImagePreview_personName);
		bottonsRoot=(TableLayout) findViewById(R.id.PhotoAlbumGalleryImagePreview_bottomButtonParentLayout);
		my_gallery=(ViewPager)findViewById(R.id.my_gallery);
		adapter = new TestAdapter(getSupportFragmentManager(), urlList);
		my_gallery.setAdapter(adapter);
		my_gallery.setCurrentItem(currentIndex);
		
		my_gallery.setOnPageChangeListener(new OnPageChangeListener() 
		{
			@Override
			public void onPageSelected(int arg0) 
			{
				currentIndex=arg0;
				updateUI(FanwallPhotoArrayList.get(arg0));
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2){}
			@Override
			public void onPageScrollStateChanged(int arg0){}
		});
	}
	private void addListeners()
	{
		PhotoAlbumGalleryImagePreview_sharePhoto.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				shareImage();
			}
		});
		PhotoAlbumGalleryImagePreview_likeImage.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				comment=FanwallPhotoArrayList.get(currentIndex);
				if(comment.getParentID().equals("0"))//post
				{
					FanwalLoginManager.getInstance().likeComment(PhotoAlbumGalleryImagePreview.this, comment, catName, 1, 12, CURRENT_TAB_PHOTOS);
				}
				else
				{
					FanwalLoginManager.getInstance().likeComment(PhotoAlbumGalleryImagePreview.this, comment, catName, 2, 12, CURRENT_TAB_PHOTOS);
				}
			}
		});
		PhotoAlbumGalleryImagePreview_openComments.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				System.gc();
				SnapLionMyAppActivity.KillFlag = false;
				FanwalLoginManager.getInstance().addComment(PhotoAlbumGalleryImagePreview.this, comment.getPost_id(), catName, 2 , 12, CURRENT_TAB_PHOTOS);
			}
		});
		if(comment!=null && comment.getParentID()!=null && !comment.getParentID().equalsIgnoreCase("0"))
		{
			PhotoAlbumGalleryImagePreview_openComments.setVisibility(ImageView.GONE);
		}
	}
	private void shareImage()
	{
		final Dialog dialog = new Dialog(PhotoAlbumGalleryImagePreview.this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.share_popup);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Button saveBtn = (Button) dialog.findViewById(R.id.p_save_lib);
        saveBtn.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        saveBtn.setOnClickListener(new OnClickListener() 
        {
			public void onClick(View v) 
			{
				//SharedPreferences indexPrefs = getSharedPreferences("currentIndex"+appManager.getAPP_ID(),MODE_PRIVATE);
				//int count = indexPrefs.getInt("currentIndex", 0);
				//Date date = new Date();
				//String name = appManager.getAPP_NAME();
				String name = "SnapLion_"+new Date().getTime();
				Bitmap bitmapImage = null;
           	 	
				try
           	 	{
           	 		BitmapFactory.Options options = new BitmapFactory.Options();
           	 		options.inSampleSize = 1;
           	 		bitmapImage = BitmapFactory.decodeFile(openImageFilePath, options);
           	 	}
           	 	catch(OutOfMemoryError e)
           	 	{
           	 		e.printStackTrace();System.gc(); bitmapImage = null;
           	 		try
           	 		{
           	 			BitmapFactory.Options options = new BitmapFactory.Options();
           	 			options.inSampleSize = 2;
           	 			bitmapImage = BitmapFactory.decodeFile(openImageFilePath, options);
           	 		}
           	 		catch(OutOfMemoryError e1) 
           	 		{
           	 			e1.printStackTrace();
           	 		}
           	 	}

				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				bitmapImage.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
			    File f = new File(Environment.getExternalStorageDirectory()+ File.separator + name+".jpg");
				try 
				{
				      f.createNewFile();
				      FileOutputStream fo = new FileOutputStream(f);
				      fo.write(bytes.toByteArray());
				      Toast.makeText(getApplicationContext(), "Image is Saved Successfully...", 1000).show();
				      scanSDcardToShowImageInGallery();
				} 
				catch (Exception e) 
				{}
				dialog.dismiss();  
				bitmapImage = null;
			}
    	});
        
        //set up button
        Button emailBtn = (Button) dialog.findViewById(R.id.p_email);
        emailBtn.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        emailBtn.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	//pauseFlag = true;
				//SharedPreferences indexPrefs = getSharedPreferences("currentIndex"+appManager.getAPP_ID(),MODE_PRIVATE);
				//int count = indexPrefs.getInt("currentIndex", 0);
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setType("image/jpg");
				String s = "Photo shared from "+appManager.getAPP_NAME()+" app";
				i .putExtra(android.content.Intent.EXTRA_SUBJECT, s);
				Uri uri=null;
				try
				{
//					if(count>albumGalleryDetails.size())
//						count=0;
					//i .putExtra(android.content.Intent.EXTRA_TEXT, "Photo Name - "+appManager.getAPP_NAME());
					i .putExtra(android.content.Intent.EXTRA_TEXT, " ");
					uri=Uri.fromFile(new File(openImageFilePath));
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				if(uri!=null) 
				{
					i.putExtra(Intent.EXTRA_STREAM,uri );
				}
				startActivity(i);
    			dialog.dismiss();
    		}
    	});
        	
        Button cancelBtn = (Button) dialog.findViewById(R.id.p_cancel);
    	//cancelBtn.setTypeface(appManager.lucida_grande_regular);
    	cancelBtn.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
    	cancelBtn.setOnClickListener(new OnClickListener() 
    	{
    		public void onClick(View v) 
    		{
    			dialog.dismiss();
    		}
    	});
        	
    	if(new File(openImageFilePath).exists()) 
        {
        	dialog.show();
        }
        else
        {
        	Utility.showToastMessage(getApplicationContext(), getString(R.string.no_image_to_share));
        }
	}
	private void scanSDcardToShowImageInGallery() 
	{
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
	}
	@Override
	protected void onResume() 
	{
		SnapLionMyAppActivity.KillFlag = true;
		System.gc();
		super.onResume();
	}
	@Override
	protected void onDestroy() 
	{
		System.gc();
		super.onDestroy();
	}
//	@Override
//	protected void onPause() 
//	{
//		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//	    boolean isScreenOn = powerManager.isScreenOn();
//	    if (!isScreenOn) 
//	    {
//	    	SnapLionMyAppActivity.KillFlag = false;
//	    }
//		if(SnapLionMyAppActivity.KillFlag)
//		{
//			try
//			{
//				Utility.killMyApp(getApplicationContext(),PhotoAlbumGalleryImagePreview.this);
//			}catch (Exception e) {e.printStackTrace();}
//		}
//		super.onPause();
//	}	

	@Override
	public void onBackPressed() 
	{
		SnapLionMyAppActivity.KillFlag = false;
		super.onBackPressed();
	}	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(resultCode, requestCode, data);
		if(resultCode==RESULT_OK)
		{
			switch(requestCode)
			{
				case FanwalLoginManager.ACTIVITY_CODE_FANWALL_LOGIN:
				{
					Utility.debug("FanwallWallActivity : onActivityResult ::: ACTIVITY_CODE_FANWALL_LOGIN");
					break;
				}
				case FanwalLoginManager.ACTIVITY_CODE_SHOW_COMMENTS:
				{
					comment=FanwallPhotoArrayList.get(currentIndex);
					Utility.debug("PhotoAlbumGalleryImagePreview : onActivityResult ::: ACTIVITY_CODE_SHOW_COMMENTS");
					FanwallComment return_resulted_post = (FanwallComment)data.getParcelableExtra("RESULTED_POST");
					return_resulted_post.setIndex(comment.getIndex());
					Intent intent=new Intent();
					intent.putExtra("RESULTED_POST", return_resulted_post);
					intent.putExtra("RESULTED_POST_INDEX", return_resulted_post.getIndex());
					setResult(Activity.RESULT_OK, intent);
					
					if(return_resulted_post!=null)
					{
						comment=return_resulted_post;
						if(return_resulted_post.getLiked_by_me().equalsIgnoreCase("1"))
	        			{
	        				PhotoAlbumGalleryImagePreview_likeImage.setImageResource(R.drawable.selector_photo_prev_heart_red);
	        			}
	        			else
	        			{
	        				PhotoAlbumGalleryImagePreview_likeImage.setImageResource(R.drawable.selector_photo_prev_heart_black);
	        			}
					}
					break;
				}
				case FanwalLoginManager.ACTIVITY_CODE_ADD_COMMENT:
				{
					String type = data.getStringExtra("TYPE");
					String value = data.getStringExtra("VALUE");
					Utility.debug("FanwallWallComentDetailsActivity : onActivityResult ::: ACTIVITY_CODE_ADD_COMMENT:type="+type+", value="+value);
					if(type.equalsIgnoreCase("JSON"))
					{
						comment=FanwallPhotoArrayList.get(currentIndex);
						comment.setTotal_comments(String.valueOf(Integer.parseInt(comment.getTotal_comments())+1));
						
						Intent intent=new Intent();
						intent.putExtra("RESULTED_POST", comment);
						intent.putExtra("RESULTED_POST_INDEX", comment.getIndex());
						setResult(Activity.RESULT_OK, intent);
						
						
						SnapLionMyAppActivity.KillFlag=false;
						Intent i1010 = new Intent(getApplicationContext(), FanwallWallComentDetailsActivity.class);
						i1010.putExtra("CLICKED_POST",comment);
						i1010.putExtra("isListScrollToBottom",true);
						i1010.putExtra("FanwallSectionName",catName);
						//i1010.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
						startActivityForResult(i1010, FanwalLoginManager.ACTIVITY_CODE_SHOW_COMMENTS);
						System.gc();
					}
					else if(type.equalsIgnoreCase("ERROR"))
					{
						Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
					}
					break;
				}
			}
		}
	}
	class TestAdapter  extends FragmentPagerAdapter 
	{
		ArrayList<String> urlList;
		public TestAdapter(FragmentManager fm, ArrayList<String> urlList) 
	    {
	        super(fm);
	        this.urlList=urlList;
	    }
	    @Override
	    public int getCount() 
	    {
	        return urlList.size();
	    }
	    @Override
	    public Fragment getItem(int position) 
	    {
	        TestFragment f = new TestFragment();
	        f.bitmapUrl = urlList.get(position);
	        f.position = position;
	        return f;
	    }
	}
	class TestFragment extends Fragment 
	{
		String bitmapUrl=null;
	    Bitmap bitmap = null;
	    Integer position = 0;
	    public TestFragment()
	    {
	        setRetainInstance(true);
	    }
	    @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        setHasOptionsMenu(true);
	    }
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	    {
	    	ImageViewTouch img=new ImageViewTouch(getActivity(),video_bottom_bar,fanwall_top_layout);
	    	img.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)) ;
	    	
	    	Bitmap bitmap = aq.getCachedImage(bitmapUrl);
	    	if(bitmap!=null)
	    	{
	    		aq.id(img).image(bitmap);
	    	}
	    	else
	    	{
	    		//ProgressDialog progress = new ProgressDialog(PhotoAlbumGalleryImagePreview.this);
	    		//progress.setMessage(getResources().getString(R.string.splash_loading_msg));
		    	aq.id(img).progress(my_gallery_progress).image(bitmapUrl, false, true);
	    	}
	    	
	    	return img;
	    }
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) 
	    {
	        return super.onOptionsItemSelected(item);
	    }
	}
}

