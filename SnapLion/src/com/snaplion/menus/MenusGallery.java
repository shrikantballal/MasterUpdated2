package com.snaplion.menus;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.preview.PreviewSnapLionAppsSelectActvity;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

public class MenusGallery extends AppSuperActivity 
{
	ProgressBar my_gallery_progress;
	String sectionOpenedFrom;
	TextView topHeaderTextView;
	String catName="";
	ArrayList<MenusItem> menuImagesArray=new ArrayList<MenusItem>();
	ArrayList<String> urlList=new ArrayList<String>();
	private LinearLayout mainBg = null;
	private String BackFlag = "yes";
	AppManager appManager;
	String openImageFilePath;
	private static int currentIndex=0;
	static TouchInterceptViewPager my_gallery;
	private LinearLayout photo_share_layout;
	private LinearLayout gallery_tob_bar;
	TestAdapter adapter;
	AQuery aq=null;
	int folderCounter=0;
	private void getURLs(ArrayList<MenusItem> tmpArray)
	{
		folderCounter=0;
		menuImagesArray.clear();
		urlList.clear();
		for(int i=0;i<tmpArray.size();i++)
		{
			MenusItem m = tmpArray.get(i);
			if(m!=null && m.getCategaryid()==null)//image
			{
				urlList.add(tmpArray.get(i).getBigpicture());
				menuImagesArray.add(tmpArray.get(i));
				//Utility.debug(tmpArray.get(i).getBigpicture());
			}
			else
			{
				folderCounter=folderCounter+1;
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menus_gallery);
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		Intent i = getIntent();
		catName = i.getStringExtra("MenuSectionName");
		ArrayList<MenusItem> tmpA = i.getParcelableArrayListExtra("MenusArrayList");
		sectionOpenedFrom = i.getStringExtra("SECTION_OPENED_BY");
		getURLs(tmpA);
		tmpA=null;
		currentIndex = i.getIntExtra("CURRENT_INDEX", 0);
		Utility.debug(currentIndex+"===="+folderCounter);
		currentIndex=currentIndex-folderCounter;
		SnapLionMyAppActivity.KillFlag = true;
		
		aq=new AQuery(this);
		
		
		initUI();
		updateUI(menuImagesArray.get(currentIndex));
		
		
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
					Intent i = new Intent(MenusGallery.this,PreviewSnapLionAppsSelectActvity.class);
    				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    				MenusGallery.this.startActivity(i);
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
			homeBtn.setVisibility(View.INVISIBLE);
		}
		
		
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
		
//		mainBg = (LinearLayout) findViewById(R.id.fanwall_root_LinearLayout);
//		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("FanwallMainBg"+appManager.getAPP_ID())));
//		mainBg.setBackgroundDrawable(background);
//		background = null;
	}
	private void updateUI(MenusItem menusItem)
	{
		mGaTracker.sendView("Menu_Album"+menusItem.getPhotoid());
		openImageFilePath = AQUtility.getCacheFile(AQUtility.getCacheDir(this), menusItem.getBigpicture()).getPath();
		topHeaderTextView.setText(menusItem.getPhotoname());
	}
	private void initUI()
	{
		my_gallery_progress=(ProgressBar)findViewById(R.id.my_gallery_progress);
		topHeaderTextView = (TextView)findViewById(R.id.menus_gallery_name_txt);
		//topHeaderTextView.setText(catName);
		topHeaderTextView.setTypeface(appManager.lucida_grande_regular);
		
		my_gallery=(TouchInterceptViewPager)findViewById(R.id.my_gallery);
		adapter = new TestAdapter(getSupportFragmentManager(), urlList);
		my_gallery.setAdapter(adapter);
		my_gallery.setCurrentItem(currentIndex);
		
		my_gallery.setOnPageChangeListener(new OnPageChangeListener() 
		{
			@Override
			public void onPageSelected(int arg0) 
			{
				currentIndex=arg0;
				updateUI(menuImagesArray.get(currentIndex));
				//my_gallery.goFlag=false;
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2){}
			@Override
			public void onPageScrollStateChanged(int arg0){}
		});
		
//		my_gallery.setOnClickListener(new OnClickListener() 
//		{
//			@Override
//			public void onClick(View v) 
//			{
//				Utility.debug("photo pager clicked");
//				
//			}
//		});
		
		photo_share_layout=(LinearLayout)findViewById(R.id.photo_share_layout);
		photo_share_layout.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				shareImage();
			}
		});
		gallery_tob_bar=(LinearLayout)findViewById(R.id.gallery_tob_bar); 
	}
	
	private void shareImage()
	{
		final Dialog dialog = new Dialog(MenusGallery.this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
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
				String s ="";
				if(sectionOpenedFrom.equalsIgnoreCase("menus"))
				{
					s = "Menu shared from "+appManager.getAPP_NAME()+" app";
				}
				else if(sectionOpenedFrom.equalsIgnoreCase("promos"))
				{
					s = "Promo shared from "+appManager.getAPP_NAME()+" app";
				}
				
				i .putExtra(android.content.Intent.EXTRA_SUBJECT, s);
				Uri uri=null;
				try
				{
//					if(count>albumGalleryDetails.size())
//						count=0;
					//i .putExtra(android.content.Intent.EXTRA_TEXT, "Photo Name - "+appManager.getAPP_NAME());
					i .putExtra(android.content.Intent.EXTRA_TEXT, "Content shared from "+appManager.getAPP_NAME()+" mobile application, Powered by SnapLion.");
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
	    public TestFragment getItem(int position) 
	    {
	        TestFragment f = new TestFragment();
	        f.bitmapUrl = urlList.get(position);
	        f.position = position;
	        return f;
	    }
	    public ImageViewTouch getCurrentSelectedImage()
	    {
	    	return getItem(currentIndex).img;
	    }
	}
	class TestFragment extends Fragment 
	{
		public String bitmapUrl=null;
		public Bitmap bitmap = null;
		public Integer position = 0;
		public ImageViewTouch img=null;
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
	    	img=new ImageViewTouch(getActivity(), photo_share_layout, gallery_tob_bar);
	    	
	    	//img.setOnImageScrollCompleteListener(my_gallery);
	    		    	
//	    	Matrix matrix = img.getDisplayMatrix();
//	    	float[] values=new float[9];
//	    	matrix.getValues(values);
//	    	Utility.debug("values : "+values[0]+"="+values[1]+"="+values[2]+"="+values[3]+"="+values[4]+"="+values[5]+"="+values[6]+"="+values[7]+"="+values[8]);
	    	
	    	
	    	img.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)) ;
	    	Bitmap bitmap = aq.getCachedImage(bitmapUrl);
	    	if(bitmap!=null)
	    	{
	    		aq.id(img).image(bitmap);
	    	}
	    	else
	    	{
	    		//ProgressDialog progress = new ProgressDialog(MenusGallery.this);
	    		//progress.setMessage(getResources().getString(R.string.splash_loading_msg));
	    		//ProgressBar progress=new ProgressBar(getActivity());
	    		//progress.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    		//progress.setBackgroundColor(getResources().getColor(R.color.red));
	    		aq.id(img).progress(my_gallery_progress).image(bitmapUrl, false, true);
	    	}
	    	return img;
	    }
	}
}
