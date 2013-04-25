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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.photos.lazyloading.ImageLoader;
import com.snaplion.preview.PreviewSnapLionAppsSelectActvity;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

public class MultibioActorsListActivity extends AppSuperActivity 
{
	private LinearLayout mainBg = null;
	
	static BitmapDrawable background ;
	private String BackFlag = "yes";
	private String catName;
	//ArrayList<MultibioItem> actorsList=new ArrayList<MultibioItem>();
	Category category=null;
	private ListView castCrewListView;
	ImageAdapter adapter;
	AppManager appManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multibio_actors_list_activity);
		
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		SnapLionMyAppActivity.KillFlag = true;
		
		Intent i = getIntent();
		category = i.getParcelableExtra("CLICKED_MULTIBIO_CATEGORY_LIST");
		
		
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
		tv.setText(category.getCategoryName());
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
					Intent i = new Intent(MultibioActorsListActivity.this,PreviewSnapLionAppsSelectActvity.class);
    				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    				MultibioActorsListActivity.this.startActivity(i);
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
		
		
		
		mainBg = (LinearLayout) findViewById(R.id.fanwall_root_LinearLayout);
		
		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("Multibio"+appManager.getAPP_ID())));
		mainBg.setBackgroundDrawable(background);
		background = null;
		
		castCrewListView = (ListView) findViewById(R.id.actors_listview);
		setupAdaptersListeners();
	}
	
	private void setupAdaptersListeners()
	{
		adapter = new ImageAdapter(category.getPlayers());
		castCrewListView.setAdapter(adapter);
		castCrewListView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				System.gc();
				SnapLionMyAppActivity.KillFlag=false;
				MultibioItem castcrew = (MultibioItem)adapter.getItem(arg2);
				Intent i1010 = new Intent(getApplicationContext(), MultibioActorsListDetailActivity.class);
				i1010.putExtra("CLICKED_CASTCREW",castcrew);
				i1010.putExtra("MultibioCatName",category.getCategoryName());
				i1010.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
				startActivity(i1010);
			}
		});
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
	@Override
	protected void onPause()
	{
//		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//	    boolean isScreenOn = powerManager.isScreenOn();
//	    if (!isScreenOn) {
//	    	SnapLionMyAppActivity.KillFlag = false;
//	    }
//		if(SnapLionMyAppActivity.KillFlag)
//		{
//			try{
//				Utility.killMyApp(getApplicationContext(),MultibioActorsListActivity.this);
//			}catch (Exception e) {e.printStackTrace();}
//		}
//		System.gc();
		super.onPause();
	}	

	@Override
	public void onBackPressed() 
	{
		SnapLionMyAppActivity.KillFlag = false;
		System.gc();
		super.onBackPressed();
	}
	private class ViewHolder
    {
		ImageView userImage;
		ProgressBar commentBodyImage_ProgressBar;
    	TextView actorName;
    	TextView characterName;
    }
	class ImageAdapter extends BaseAdapter 
	{
		ArrayList<MultibioItem> list;
		public ImageAdapter(ArrayList<MultibioItem> list)
		{
			this.list=list;
		}
		public int getCount() 
        {
            return list.size();
        }

        public MultibioItem getItem(int arg0) 
        {
            return list.get(arg0);
        }

        public long getItemId(int arg0) 
        {
            return arg0;
        }
        public View getView(int position, View convertView, ViewGroup arg2) 
        {
        	ViewHolder viewHolder;
            if (convertView == null) 
            {
            	convertView =  getLayoutInflater().inflate(R.layout.castcrew_list_item, null);
            	viewHolder=new ViewHolder();
            	
            	viewHolder.userImage=(ImageView)convertView.findViewById(R.id.userImage);
            	viewHolder.commentBodyImage_ProgressBar=(ProgressBar)convertView.findViewById(R.id.commentBodyImage_ProgressBar);
            	viewHolder.actorName=(TextView)convertView.findViewById(R.id.actorName);
            	//viewHolder.actorName.setTypeface(appManager.lucida_grande_bold);
            	viewHolder.actorName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            	viewHolder.actorName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            	
            	viewHolder.characterName=(TextView)convertView.findViewById(R.id.characterName);
            	//viewHolder.characterName.setTypeface(appManager.lucida_grande_regular);
            	viewHolder.characterName.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            	viewHolder.characterName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            	
            	convertView.setTag(viewHolder);
            }
            else
            {
            	viewHolder=(ViewHolder)convertView.getTag();
            }
        	
        	if(position%2==0)
        	{
        		convertView.setBackgroundResource(R.drawable.multibio_actorlist_dark_bg);
        	}
        	else
        	{
        		convertView.setBackgroundResource(R.drawable.multibio_actorlist_light_bg);
        	}
        	
        	MultibioItem castcrew = list.get(position);
        	viewHolder.actorName.setText(castcrew.getName());
        	viewHolder.characterName.setText(castcrew.getRole());
        	AQuery aq=new AQuery(convertView);
        	Bitmap bitmap = aq.getCachedImage(castcrew.getThumb());
	    	if(bitmap!=null)
	    	{
	    		aq.id(viewHolder.userImage).image(bitmap);
	    	}
	    	else
	    	{
	    		aq.id(viewHolder.userImage)
	    		.progress(viewHolder.commentBodyImage_ProgressBar)
	    		.image(castcrew.getThumb(), false, true);
	    	}
	    	
//        	ImageLoader msgImageLoader=new ImageLoader(getApplicationContext(), R.drawable.comments_uploaded_img);
//        	msgImageLoader.DisplayImage(castcrew.getThumb(), viewHolder.userImage, viewHolder.commentBodyImage_ProgressBar);
			return convertView;
        }
    }
	@Override
	protected void onStart() 
	{
		super.onStart();
		mGaTracker.sendView("MultiBio_"+category.getCategoryId());
	}
}