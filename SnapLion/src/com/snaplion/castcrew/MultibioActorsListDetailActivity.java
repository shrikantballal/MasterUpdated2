package com.snaplion.castcrew;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.snaplion.fanwall.FanwallComment;
import com.snaplion.fanwall.photo.PhotoAlbumGalleryImagePreview;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.photos.lazyloading.ImageLoader;
import com.snaplion.preview.PreviewSnapLionAppsSelectActvity;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

public class MultibioActorsListDetailActivity extends AppSuperActivity
{
	private RelativeLayout mainBg = null;
	
	static BitmapDrawable background ;
	private String BackFlag = "yes";
	private String catName;
	MultibioItem castcrew;
	AppManager appManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.castcrew_list_details_activity);
		
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		SnapLionMyAppActivity.KillFlag = true;
		Intent i = getIntent();
		catName = i.getStringExtra("MultibioCatName");
		castcrew = i.getParcelableExtra("CLICKED_CASTCREW");
		
//		if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1017"))
//		{
//			Display display = getWindowManager().getDefaultDisplay(); 
//			BackFlag = "yes";
//			LinearLayout bottomLayout = (LinearLayout)findViewById(R.id.video_bottom_bar);
//			bottomLayout.setVisibility(View.VISIBLE);
//			
//			try
//			{
//				com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,display.getWidth(),appManager.getAPP_ID(),i.getStringExtra("appname"));
//			}
//			catch (OutOfMemoryError e) 
//			{
//				e.printStackTrace();
//			}
//		}
		
		TextView tv = (TextView)findViewById(R.id.v_sub_name_txt);
		tv.setText(catName);
		tv.setTypeface(appManager.lucida_grande_regular);
		
		Button homeBtn = (Button)findViewById(R.id.v_home_btn);
		if(appManager.PREVIEWAPP_FLAG)
		{
			homeBtn.setVisibility(Button.VISIBLE);
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
					Intent i = new Intent(MultibioActorsListDetailActivity.this,PreviewSnapLionAppsSelectActvity.class);
    				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    				MultibioActorsListDetailActivity.this.startActivity(i);
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
		
		mainBg = (RelativeLayout) findViewById(R.id.fanwall_root_LinearLayout);
		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("Multibio"+appManager.getAPP_ID())));
		mainBg.setBackgroundDrawable(background);
		background = null;
		
		initUI();
	}

	private void initUI() 
	{
		TextView actorName = (TextView)findViewById(R.id.actorName);
		//actorName.setTypeface(appManager.lucida_grande_bold);
		actorName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		actorName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
		
		TextView roleName = (TextView)findViewById(R.id.roleName);
		//roleName.setTypeface(appManager.lucida_grande_regular);
		roleName.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
		roleName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
		
		TextView bioLabel = (TextView)findViewById(R.id.bioLabel);
		//bioLabel.setTypeface(appManager.lucida_grande_bold);
		bioLabel.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		bioLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
		
		WebView bioTextView = (WebView)findViewById(R.id.bioTextView);
		bioTextView.setBackgroundColor(0);
		bioTextView.getSettings().setJavaScriptEnabled(true); 
		bioTextView.getSettings().setPluginState(PluginState.ON);
		bioTextView.getSettings().setAllowFileAccess(true); 
		bioTextView.setWebViewClient(new WebViewClient() {
			 public boolean shouldOverrideUrlLoading(WebView view, String url) { 
		            return true;
		        } 
		 });
		
		//bioTextView.setTypeface(appManager.lucida_sans_regular);
		//bioTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
		//bioTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		
		ImageView actorImage= (ImageView)findViewById(R.id.actorImage);
		ProgressBar commentBodyImage_ProgressBar= (ProgressBar)findViewById(R.id.commentBodyImage_ProgressBar);
		
		if(castcrew!=null)
		{
			//ImageLoader msgImageLoader=new ImageLoader(getApplicationContext(), R.drawable.comments_uploaded_img);
	    	//msgImageLoader.DisplayImage(castcrew.getThumb(), actorImage, commentBodyImage_ProgressBar);
			AQuery aq=new AQuery(this);
        	Bitmap bitmap = aq.getCachedImage(castcrew.getThumb());
	    	if(bitmap!=null)
	    	{
	    		aq.id(actorImage).image(bitmap);
	    	}
	    	else
	    	{
	    		aq.id(actorImage)
	    		.progress(commentBodyImage_ProgressBar)
	    		.image(castcrew.getThumb(), false, true);
	    	}
	    	
			actorName.setText(castcrew.getName());
	    	roleName.setText(castcrew.getRole());
	    	bioTextView.loadDataWithBaseURL(null,"<font color='#FFFFFF'>"+castcrew.getBio().replaceAll("\r\n", "<br/>")+"</font>", "text/html", "utf-8","");
	    }
		
		actorImage.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent=new Intent(MultibioActorsListDetailActivity.this , PhotoAlbumGalleryImagePreview.class);
				FanwallComment tmpComm=new FanwallComment();
				tmpComm.setMessage(castcrew.getName());
				tmpComm.setMessage_photo_url(castcrew.getPhoto());
				ArrayList<FanwallComment> FanwallPhotoArrayList=new ArrayList<FanwallComment>();
				FanwallPhotoArrayList.add(tmpComm);
				intent.putParcelableArrayListExtra("FanwallPhotoArrayList", FanwallPhotoArrayList);
				intent.putExtra("TYPE", "MULTIBIO");
				intent.putExtra("Position", 0);
				intent.putExtra("FanwallSectionName",catName);
				startActivity(intent);
				System.gc();
			}
		});
	}
	@Override
	protected void onPause() 
	{
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
//				Utility.killMyApp(getApplicationContext(),MultibioActorsListDetailActivity.this);
//			}catch (Exception e) {e.printStackTrace();}
//		}
		super.onPause();
	}	
	@Override
	protected void onStart() 
	{
		super.onStart();
		mGaTracker.sendView("MultiBio_Detail_"+castcrew.getId());
	}
}
