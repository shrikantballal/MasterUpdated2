package com.snaplion.promo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.inmobi.androidsdk.IMAdView;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.menus.MenusGallery;
import com.snaplion.menus.MenusItem;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

public class PromoDetailsActivity extends AppSuperActivity
{
	@Override
	protected void onResume() 
	{
		pauseFlag = false;
		SnapLionMyAppActivity.KillFlag = true;
		super.onResume();
	}

	private String newsURL = null;
	private String imageId = null;
	private LinearLayout newsLayout = null;
	private String catId = null;
	private String showUrl = null;
	private String catName = null;
	private String newsContent = null;
	private String newsDate = null;
	private String startDate = null;
	private String eventCity = null;
	private String Venue = null;
	private String newsTitle = null;
	//private Typeface tf1 = null;
	private boolean pauseFlag = false; 
	AppManager appManager;
	PromoItem promoItem;
	AQuery aq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.promo_detail_activity);
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		SnapLionMyAppActivity.KillFlag = true;
		aq = new AQuery(this);
		
		Intent i = getIntent();
		catName = i.getStringExtra("PromoName");
		promoItem = (PromoItem)i.getParcelableExtra("PROMO_ITEM");
		
		
		
		TextView tv = (TextView)findViewById(R.id.n_sub_name_txt);
		tv.setText(catName);
		tv.setTypeface(appManager.lucida_grande_regular);
		
		Button homeBtn = (Button)findViewById(R.id.n_home_btn);
		homeBtn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
					SnapLionMyAppActivity.KillFlag = false;
    				finishFromChild(getParent());
			}
		});
		
		Button backBtn = (Button)findViewById(R.id.n_sub_back_btn);
		backBtn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				SnapLionMyAppActivity.KillFlag = false;
				finishFromChild(getParent());
				System.gc();
			}
		});
		
		
		LinearLayout llShare = (LinearLayout)findViewById(R.id.news_share_layout);
		llShare.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				pauseFlag = true;
				Bitmap bitmap = aq.getCachedImage(promoItem.getImage_thumb_big());
				if(bitmap != null)
    			{
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    				bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
    				
    				File f = new File(Environment.getExternalStorageDirectory()+ File.separator +catName+".jpg");
        			try 
        			{
        				f.createNewFile();
        				FileOutputStream fo = new FileOutputStream(f);
        				fo.write(bytes.toByteArray());
        			}catch (Exception e){e.printStackTrace();}
        				
        		    Intent i = new Intent(Intent.ACTION_SEND);
        			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        			i.setType("image/jpg");
        			String str = null;
        			str = "Promotion shared from "+appManager.getAPP_NAME()+" app";
        			i .putExtra(android.content.Intent.EXTRA_SUBJECT, str);
//        			i .putExtra(android.content.Intent.EXTRA_TEXT, newsTitle+System.getProperty("line.separator")+newsDate+System.getProperty("line.separator")+
//        					startDate+System.getProperty("line.separator")+eventCity+System.getProperty("line.separator")+"Venue: "+Venue+
//        					System.getProperty("line.separator")+newsContent+System.getProperty("line.separator"));
        			
        			i .putExtra(android.content.Intent.EXTRA_TEXT, 
        					promoItem.getHeadline()+System.getProperty("line.separator")+System.getProperty("line.separator")+
        					promoItem.getDescription()+System.getProperty("line.separator"));
        			
        			i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(aq.getCachedFile(promoItem.getImage_thumb_big())));
        			//i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/"+catName+".jpg"));
        			startActivity(i);
        			bitmap = null;
    			}
    		}
		});
			
		LinearLayout buyLayout =  (LinearLayout)findViewById(R.id.news_buy_tkt_layout);
		
		if(promoItem.getUrl()==null || promoItem.getUrl().length()<6)
		{
			buyLayout.setVisibility(LinearLayout.GONE);
		}
		else
		{
			buyLayout.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					SnapLionMyAppActivity.KillFlag = false;
					Intent i = new Intent(getApplicationContext(), PromoURLActivity.class);
					i.putExtra("ShowURL", promoItem.getUrl());
					i.putExtra("CatName", catName);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}
			});
		}
		
		ImageView promo_details_header_img = (ImageView) findViewById(R.id.news_sub_img);
		ProgressBar progress = (ProgressBar)findViewById(R.id.news_sub_img_progress);
		if(promoItem.getImage_thumb_big()!=null && promoItem.getImage_thumb_big().length()>0)
		{
			Bitmap bitmap = aq.getCachedImage(promoItem.getImage_thumb_big());
	    	if(bitmap!=null)
	    	{
	    		aq.id(promo_details_header_img).image(bitmap);
	    	}
	    	else
	    	{
	    		aq.id(promo_details_header_img)
	    		.progress(progress)
	    		.image(promoItem.getImage_thumb_big(), false, true);
	    	}
	    	promo_details_header_img.setOnClickListener(new OnClickListener() 
	    	{
				@Override
				public void onClick(View v) 
				{
					MenusItem menuItem=new MenusItem();
					menuItem.setBigpicture(promoItem.getImage_big());
					ArrayList<MenusItem> menuImagesArray=new ArrayList<MenusItem>();
					menuImagesArray.add(menuItem);
					Intent intent=new Intent(PromoDetailsActivity.this, MenusGallery.class);
					intent.putParcelableArrayListExtra("MenusArrayList", menuImagesArray);
					intent.putExtra("SECTION_OPENED_BY", "promos");
					mGaTracker.sendView("Promo_Image_"+promoItem.getId());
					startActivity(intent);
				}
			});
		}
		else
		{
			((RelativeLayout)findViewById(R.id.news_sub_img_root)).setVisibility(RelativeLayout.GONE);
		}
    	
		TextView promo_details_title = (TextView) findViewById(R.id.news_sub_headers);
		promo_details_title.setText(promoItem.getHeadline());
		
		TextView promo_details_description = (TextView) findViewById(R.id.news_sub_content_txt);
		promo_details_description.setText(promoItem.getDescription());
		
		LinearLayout news_sub_bg=(LinearLayout)findViewById(R.id.news_sub_bg);
		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("PromoBg"+appManager.getAPP_ID())));
		news_sub_bg.setBackgroundDrawable(background);
		background = null;
		
	}
	@Override
	protected void onStart() 
	{
		super.onStart();
		mGaTracker.sendView("Promo_"+promoItem.getId());
	}
}
