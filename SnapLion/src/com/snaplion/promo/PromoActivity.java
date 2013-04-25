package com.snaplion.promo;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.inmobi.androidsdk.IMAdView;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

public class PromoActivity extends AppSuperActivity
{
	private String getString(String fileName)
	{
		try
		{
			InputStream in = getAssets().open(fileName);
			return Utility.getString(in);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	private ArrayList<PromoItem> getMenusItems(String jsonStr)
	{
		try
		{
			ArrayList<PromoItem> list=new ArrayList<PromoItem>();
			JSONArray jsonarray=new JSONArray(jsonStr);
			for(int i=0;i<jsonarray.length();i++)
			{
				PromoItem item=new PromoItem();
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				item.setId(jsonobject.getString("id"));
				item.setHeadline(jsonobject.getString("headline"));
				item.setDescription(jsonobject.getString("description"));
				item.setUrl(jsonobject.getString("url"));
				item.setImage_thumb(jsonobject.getString("image_thumb"));
				item.setImage_thumb_big(jsonobject.getString("image_thumb_big"));
				item.setImage_big(jsonobject.getString("image_big"));
				list.add(item);
			}
			return list;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	@Override
	protected void onResume() {
		SnapLionMyAppActivity.KillFlag = true;
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		finish();
		System.gc();
		super.onDestroy();
	}

	ArrayList<PromoItem> promoListArray=new ArrayList<PromoItem>();
	
	private ListView promoList = null;
	private String catName = null;
	private int Flag = 0;
	
	private LinearLayout newsLayout = null;
	private LinearLayout newsTopLayout = null;
	
	private ImageAdapter adapter = null;
	private String BackFlag = "yes";
	
	private boolean updateFlag = true;
	AppManager appManager;
	AQuery aq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.promo);
		Utility.closeAllBelowActivities(this);
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		SnapLionMyAppActivity.KillFlag = true;
		aq=new AQuery(this);
		
		SharedPreferences myPref1 = getSharedPreferences("PromoPref"+appManager.getAPP_ID(), MODE_PRIVATE);
		String promoData = myPref1.getString("PromoData", null);
		
		promoListArray=getMenusItems(promoData);
		
		Intent i = getIntent();
		catName = i.getStringExtra("PromoName");
				
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		
		if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1019"))
		{
			BackFlag = "no";
			LinearLayout bottomLayout = (LinearLayout)findViewById(R.id.news_bottom_bar);
			bottomLayout.setVisibility(View.VISIBLE);
			com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,width,appManager.getAPP_ID(),appManager.getAPP_NAME());
		 }
		
		newsTopLayout = (LinearLayout)findViewById(R.id.news_top_bar);
		
		newsLayout = (LinearLayout)findViewById(R.id.news_bg);
		
		promoList = (ListView)findViewById(R.id.news_list);
		promoList.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) 
			{
				SnapLionMyAppActivity.KillFlag = false;
				Intent i = new Intent(getApplicationContext(), PromoDetailsActivity.class);
				i.putExtra("PROMO_ITEM", promoListArray.get(position));
				i.putExtra("PromoName",catName);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});
		
		com.snaplion.util.TopClass.getTopView(PromoActivity.this,getApplicationContext(),newsTopLayout,catName,BackFlag);
		
		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("PromoBg"+appManager.getAPP_ID())));
		newsLayout.setBackgroundDrawable(background);
		background = null;
    	
		adapter = new ImageAdapter(getApplicationContext(),promoListArray);
		promoList.setAdapter(adapter);
	}

	public class ImageAdapter extends BaseAdapter
	{
		ArrayList<PromoItem> list = null;
		Context context;
		public ImageAdapter(Context context, ArrayList<PromoItem> list)
		{
			this.list=list;
			this.context=context;
		}
		public int getCount() 
		{
			return list.size();
		}
		class ViewHolder
		{
			TextView headline;
			TextView description;
			ImageView image_thumb;
			ProgressBar promolist_ProgressBar_progress;
		}
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			ViewHolder viewHolder;
			
			if (convertView == null )
			{
				convertView =  getLayoutInflater().inflate(R.layout.promo_list_item, null);
				viewHolder=new ViewHolder();
				viewHolder.headline=(TextView)convertView.findViewById(R.id.promolist_TextView_headline);
				viewHolder.description=(TextView)convertView.findViewById(R.id.promolist_TextView_description);
				viewHolder.image_thumb=(ImageView)convertView.findViewById(R.id.promolist_ImageView_thumbimage);
				viewHolder.promolist_ProgressBar_progress=(ProgressBar)convertView.findViewById(R.id.promolist_ProgressBar_progress);
				convertView.setTag(viewHolder);
			}
			else
			{
				viewHolder=(ViewHolder)convertView.getTag();
			}
			int[] colors = new int[] { R.drawable.dark_gray_bg,R.drawable.light_gray_bg};
			int colorPos = position % colors.length;
			try
			{
				convertView.setBackgroundResource(colors[colorPos]);
			}
			catch (OutOfMemoryError e) 
			{
				e.printStackTrace();
			}
			viewHolder.headline.setText(list.get(position).getHeadline());
			viewHolder.headline.setTypeface(appManager.lucida_grande_bold);
			viewHolder.description.setText(list.get(position).getDescription());
			viewHolder.description.setTypeface(appManager.lucida_grande_regular);
			Bitmap bitmap = aq.getCachedImage(list.get(position).getImage_thumb());
	    	if(bitmap!=null)
	    	{
	    		aq.id(viewHolder.image_thumb).image(bitmap);
	    	}
	    	else
	    	{
	    		aq.id(viewHolder.image_thumb)
	    		.image(list.get(position).getImage_thumb(), false, true);
	    		//if(list.get(position).getImage_thumb()!=null && list.get(position).getImage_thumb().length()>20)
	    		//{
//	    			aq.id(viewHolder.image_thumb)
//		    		.progress(viewHolder.promolist_ProgressBar_progress)
//		    		.image(list.get(position).getImage_thumb(), false, true);
	    		//}
	    	}
	    	
			return convertView;
		}
		public PromoItem getItem(int position) 
		{
			return list.get(position);
		}
		public long getItemId(int position) 
		{
			return position;
		}
	}
	
	@Override
	public void onBackPressed() {
		SnapLionMyAppActivity.KillFlag = false;
		super.onBackPressed();
	}	
	@Override
	protected void onPause() 
	{
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
	    boolean isScreenOn = powerManager.isScreenOn();
	    if (!isScreenOn) {
	    	SnapLionMyAppActivity.KillFlag = false;
	    }
		if(SnapLionMyAppActivity.KillFlag)
		{
			try{
				Utility.killMyApp(getApplicationContext(),PromoActivity.this);
			}catch (Exception e) {e.printStackTrace();}
		}
		super.onPause();
	}
	@Override
	protected void onStart() 
	{
		super.onStart();
		mGaTracker.sendView("Promo_Screen");
	}
}
