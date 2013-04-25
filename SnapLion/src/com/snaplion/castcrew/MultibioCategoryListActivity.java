package com.snaplion.castcrew;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.preview.PreviewSnapLionAppsSelectActvity;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class MultibioCategoryListActivity extends AppSuperActivity 
{
	private LinearLayout mainBg = null;
	
	static BitmapDrawable background ;
	private String BackFlag = "yes";
	private String catName;
	ArrayList<Category> multibioList=new ArrayList<Category>();
	private ListView castCrewListView;
	ImageAdapter adapter;
	AppManager appManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.castcrew_list_activity);
		Utility.closeAllBelowActivities(this);
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		SnapLionMyAppActivity.KillFlag = true;
		
		Intent i = getIntent();
		catName = i.getStringExtra("MultibioCatName");
		
		if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1017"))
		{
			Display display = getWindowManager().getDefaultDisplay(); 
			BackFlag = "no";
			LinearLayout bottomLayout = (LinearLayout)findViewById(R.id.video_bottom_bar);
			bottomLayout.setVisibility(View.VISIBLE);
			
			try
			{
				com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,display.getWidth(),appManager.getAPP_ID(),i.getStringExtra("appname"));
			}
			catch (OutOfMemoryError e) 
			{
				e.printStackTrace();
			}
		}
		
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
					Intent i = new Intent(MultibioCategoryListActivity.this,PreviewSnapLionAppsSelectActvity.class);
    				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    				MultibioCategoryListActivity.this.startActivity(i);
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
		
		castCrewListView = (ListView) findViewById(R.id.category_listview);
		setupAdaptersListeners();
		
		ProgressDialog progress = new ProgressDialog(MultibioCategoryListActivity.this);
		progress.setMessage(getResources().getString(R.string.splash_loading_msg));
		new DownloadCastCrewBGTask(progress).execute();
	}
	
	private void setupAdaptersListeners()
	{
		
		adapter = new ImageAdapter();
		
		castCrewListView.setAdapter(adapter);
		castCrewListView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				Category category=multibioList.get(arg2);
				System.gc();
				SnapLionMyAppActivity.KillFlag=false;
				Intent i1010 = new Intent(getApplicationContext(), MultibioActorsListActivity.class);
				i1010.putExtra("CLICKED_MULTIBIO_CATEGORY_LIST",category);
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
//				Utility.killMyApp(getApplicationContext(),MultibioCategoryListActivity.this);
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
		TextView categoryList;
    }
	class ImageAdapter extends BaseAdapter 
	{
		public int getCount() 
        {
            if (multibioList != null) 
            {
                return multibioList.size();
            }
            return 0;
        }

        public Category getItem(int arg0) 
        {
            return multibioList.get(arg0);
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
            	convertView =  getLayoutInflater().inflate(R.layout.multibio_category_list_item, null);
            	viewHolder=new ViewHolder();
            	
            	viewHolder.categoryList=(TextView)convertView.findViewById(R.id.multibioCategoryName);
            	//viewHolder.categoryList.setTypeface(appManager.lucida_grande_regular);
            	viewHolder.categoryList.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            	viewHolder.categoryList.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            	
            	convertView.setTag(viewHolder);
            }
            else
            {
            	viewHolder=(ViewHolder)convertView.getTag();
            }
        	convertView.setBackgroundResource(R.drawable.multibio_actorlist_light_bg);
        	
//        	Category category = multibioList.get(position);
//        	String catName = category.getCategoryName();
//        	ArrayList<MultibioItem> players = category.getPlayers(); 
        	if(multibioList.get(position).getPlayers()!=null)
        	{
        		viewHolder.categoryList.setText(multibioList.get(position).getCategoryName()+" ("+multibioList.get(position).getPlayers().size()+")");
        	}
        	else
        	{
        		viewHolder.categoryList.setText(multibioList.get(position).getCategoryName()+" (0)");
        	}
        	
        	return convertView;
        }
    }
	private class DownloadCastCrewBGTask extends AsyncTask<Void, Integer, Void> 
	{
		String resStr = null;
		ProgressDialog progress;
		
		public DownloadCastCrewBGTask(ProgressDialog progress)
		{
			this.progress = progress;
		}
		public void onPreExecute() 
		{
			progress.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) 
		{
			InputStream in = null;
			try 
			{
				SharedPreferences pref = getSharedPreferences("Multibio"+appManager.getAPP_ID(), MODE_PRIVATE);
				resStr = pref.getString("MULTIBIO_DATA",null);
				if (resStr==null) 
				{
					String url="";
					url = appManager.MULTIBIO_URL+"?AppId="+appManager.getAPP_ID();
					Utility.debug("cast crew url :"+url);
					in = HttpConnection.connect(url);
					if(in != null)
					{
						resStr = Utility.getString(in);
						SharedPreferences.Editor e0 = pref.edit();
		            	e0.putString("MULTIBIO_DATA", resStr);
		            	e0.commit();
					}
				}
				multibioList = getCastCrew(resStr);
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
			adapter.notifyDataSetChanged();
			System.gc();
			super.onPostExecute(result);
			progress.dismiss();
		}
	}
	public ArrayList<Category> getCastCrew(String resStr)
	{
		try
		{
			ArrayList<Category> tmpList = new ArrayList<Category>();
			JSONArray categoryJsonArray = new JSONArray(resStr);
			if(categoryJsonArray!=null)
			{
				MultibioItem multibioItem;
				for(int i=0;i<categoryJsonArray.length();i++)
				{
					Category category = new Category();
					JSONObject categoryJsonArrayItem = categoryJsonArray.getJSONObject(i);
					category.setCategoryId(categoryJsonArrayItem.getString("categoryId"));
					category.setCategoryName(categoryJsonArrayItem.getString("categoryName"));
					JSONArray playersJSONArray = categoryJsonArrayItem.getJSONArray("players");
					ArrayList<MultibioItem> tmp=new ArrayList<MultibioItem>();
					for(int j=0;j<playersJSONArray.length();j++)
					{
						JSONObject playerJSONObject = playersJSONArray.getJSONObject(j);
						multibioItem = new MultibioItem();
						multibioItem.setId(playerJSONObject.getString("id"));
						multibioItem.setName(playerJSONObject.getString("name"));
						multibioItem.setRole(playerJSONObject.getString("role"));
						multibioItem.setPhoto(playerJSONObject.getString("photo"));
						multibioItem.setThumb(playerJSONObject.getString("thumb"));
						multibioItem.setBio(playerJSONObject.getString("bio"));
						multibioItem.setCreated(playerJSONObject.getString("created"));
						tmp.add(multibioItem);
						multibioItem=null;
					}
					category.setPlayers(tmp);
					tmpList.add(category);
				}
			}
			return tmpList;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
//	public ArrayList<String> getCastCrew(String resStr)
//	{
//		try
//		{
//			ArrayList<String> tmpList = new ArrayList<String>();
//			JSONArray castcrewJsonArray = new JSONArray(resStr);
//			if(castcrewJsonArray!=null)
//			{
//				MultibioItem castcrew;
//				for (int i = 0; i < castcrewJsonArray.length(); i++) 
//				{
//					JSONObject jsonObject = castcrewJsonArray.getJSONObject(i);
//					castcrew = new MultibioItem();
//					castcrew.setCategory(jsonObject.getString("category_name"));
//					castcrew.setName(jsonObject.getString("name"));
//					castcrew.setRole(jsonObject.getString("role"));
//					castcrew.setBio(jsonObject.getString("bio"));
//					castcrew.setImage(jsonObject.getString("photo"));
//					castcrew.setThumbImage(jsonObject.getString("thumb"));
//					
//					if(multibioTable.containsKey(castcrew.getCategory()))
//					{
//						multibioTable.get(castcrew.getCategory()).add(castcrew);
//					}
//					else
//					{
//						ArrayList<MultibioItem> tmp=new ArrayList<MultibioItem>();
//						tmp.add(castcrew);
//						multibioTable.put(castcrew.getCategory(), tmp);
//						tmpList.add(castcrew.getCategory());
//					}
//					castcrew=null;
//				}
//			}
//			return tmpList;
//		}
//		catch (Exception e) 
//		{
//			e.printStackTrace();
//		}
//		return null;
//	}
	@Override
	protected void onStart() 
	{
		super.onStart();
		mGaTracker.sendView("MultiBio_Screen");
	}
}