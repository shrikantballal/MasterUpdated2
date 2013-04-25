package com.snaplion.menus;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaplion.fanwall.FanwalLoginManager;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.preview.PreviewSnapLionAppsSelectActvity;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

public class MenusActivity extends AppSuperActivity 
{
	TextView headerTxt;
	public static int folderCounter=0;
	private RelativeLayout mainBg = null;
	private LinearLayout bottomLayout = null;
	private LinearLayout topLayout = null;
	private String BackFlag = "yes";
	
	AppManager appManager;
	FrameLayout menu_home_activity;
	
	public Handler mHandler;
	public class mMenusHandler extends Handler 
	{ 
        public void handleMessage(Message msg) 
        { 
        	switch(msg.arg1)
        	{
        		case FanwalLoginManager.SHOW_BACK_BUTTON:
        		{
        			((Button)findViewById(R.id.top_back)).setVisibility(View.VISIBLE);
        			bottomLayout.setVisibility(LinearLayout.GONE);
        			((View)findViewById(R.id.menu_home_activity_bottom_place_holder)).setVisibility(View.GONE);
        			break;
        		}
        		case FanwalLoginManager.HIDE_BACK_BUTTON:
        		{
        			if(BackFlag.equalsIgnoreCase("no"))
        			{
        				((Button)findViewById(R.id.top_back)).setVisibility(View.INVISIBLE);
        				bottomLayout.setVisibility(LinearLayout.VISIBLE);
        				((View)findViewById(R.id.menu_home_activity_bottom_place_holder)).setVisibility(View.VISIBLE);
        			}
        			else
        			{
        				((Button)findViewById(R.id.top_back)).setVisibility(View.VISIBLE);
        				bottomLayout.setVisibility(LinearLayout.GONE);
        				((View)findViewById(R.id.menu_home_activity_bottom_place_holder)).setVisibility(View.GONE);
        			}
        			break;
        		}
        		case FanwalLoginManager.SET_TOP_HEADING:
        		{
        			String heading = (String)msg.obj;
        			headerTxt.setText(heading);
        			break;
        		}
        	}
        }
    };
    @Override
	protected void onCreate(Bundle arg0) 
	{
		super.onCreate(arg0);
		setContentView(R.layout.menu_activity);
		mHandler=new mMenusHandler();
		appManager=AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		Utility.closeAllBelowActivities(this);
		com.snaplion.music.SLMusicActivity.music = false;
		com.snaplion.myapp.SnapLionMyAppActivity.homeMusic = false;
		SnapLionMyAppActivity.KillFlag = true;
		
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		Intent i = getIntent();
		bottomLayout = (LinearLayout)findViewById(R.id.album_tab_bar);
		if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1018"))
		{
			BackFlag = "no";
			bottomLayout.setVisibility(View.VISIBLE);
			com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,width,appManager.getAPP_ID(),appManager.getAPP_NAME());
			bottomLayout.setVisibility(LinearLayout.VISIBLE);
			((View)findViewById(R.id.menu_home_activity_bottom_place_holder)).setVisibility(View.VISIBLE);
		}
		else
		{
			bottomLayout.setVisibility(LinearLayout.GONE);
			((View)findViewById(R.id.menu_home_activity_bottom_place_holder)).setVisibility(View.GONE);
		}
		topLayout = (LinearLayout)findViewById(R.id.photo_top_bar);
		getTopView(topLayout,i.getStringExtra("MenusName"),BackFlag);
		
		
		mainBg = (RelativeLayout)findViewById(R.id.album_main_bg);
		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("MenusBG"+appManager.getAPP_ID())));
		mainBg.setBackgroundDrawable(background);
		background = null;
		
		SharedPreferences myPref1 = getSharedPreferences("MenusPref"+appManager.getAPP_ID(), MODE_PRIVATE);
//		Editor editor = myPref1.edit();
//		editor.putString("MenusData", getString("menu_json_format.txt"));
//		editor.commit();
		
		String menusStr = myPref1.getString("MenusData", "");
		Utility.debug("menusStr:"+menusStr);
		//appManager.menusArrayList=getMenusItems(menusStr);
		
		MenuItemsGridFragment newFragment = new MenuItemsGridFragment();
		newFragment.menusItems=getMenusItems(menusStr);
		newFragment.showBackFlag=false;
		newFragment.topHeading=i.getStringExtra("MenusName");
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.menu_home_activity, newFragment, "MAIN_FRAGMENT").commit();
    }
	private String getString(String fileName)
	{
		try
		{
			InputStream in = getAssets().open(fileName);
			return Utility.getString(in);
		}
		catch(Exception ex)
		{
			
		}
		return null;
	}
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
	}
	
	private ArrayList<MenusItem> getMenusItems(String jsonStr)
	{
		folderCounter=0;
		try
		{
			//Utility.debug("before conversion:"+jsonStr);
			//jsonStr = jsonStr.replaceAll(",\\[\\]", "");
			//Utility.debug("before conversion:"+jsonStr);
			ArrayList<MenusItem> list=new ArrayList<MenusItem>();
			JSONArray jsonarray=new JSONArray(jsonStr);
			for(int i=0;i<jsonarray.length();i++)
			{
				MenusItem item=new MenusItem();
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				if(jsonobject.has("categaryid"))//folder
				{
					try
					{
						item.setCategaryid(jsonobject.getString("categaryid"));
						folderCounter++;
						item.setCategoryname(jsonobject.getString("categoryname"));
						item.setModifiedDate(jsonobject.getString("ModifiedDate"));
						if(jsonobject.has("albumimage") && jsonobject.getString("albumimage")!=null)
							item.setAlbumimage(jsonobject.getString("albumimage"));
						if(jsonobject.has("images") && jsonobject.getJSONArray("images")!=null)
						{
							JSONArray jsonarray_images = jsonobject.getJSONArray("images");
							if(jsonarray_images!=null && jsonarray_images.length()>0)
							{
								item.setImages(getMenusItems(jsonarray_images.toString()));
							}
						}
						//Utility.debug("added folder:"+item.getCategaryid());
					}
					catch(Exception jsonexp)
					{
						//Utility.debug("error:"+jsonexp.getMessage());
						jsonexp.printStackTrace();
					}
				}
				else if(jsonobject.has("photoid"))//image
				{
					try
					{
						item.setPhotoid(jsonobject.getString("photoid"));
						item.setPhotoname(jsonobject.getString("photoname"));
						item.setSmallpicture(jsonobject.getString("smallpicture"));
						item.setBigpicture(jsonobject.getString("bigpicture"));
						//Utility.debug("added image:"+item.getPhotoid());
					}
					catch(Exception jsonexp)
					{
						//Utility.debug("error:"+jsonexp.getMessage());
						jsonexp.printStackTrace();
					}
				}
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
	public LinearLayout getTopView(LinearLayout topL, String string, String flag) 
	{
		View view =  getLayoutInflater().inflate(R.layout.top_layout, null);
		Button homeBtn = (Button)view.findViewById(R.id.top_home);
		Button backBtn = (Button)view.findViewById(R.id.top_back);
		
		headerTxt = (TextView)view.findViewById(R.id.top_header_name);
		headerTxt.setText(string);
		headerTxt.setTypeface(AppManager.getInstance().lucida_grande_regular);
			
		if(flag.equalsIgnoreCase("no")){
			backBtn.setVisibility(View.INVISIBLE);
		}
		if(AppManager.getInstance().PREVIEWAPP_FLAG)
		{
			homeBtn.setVisibility(View.VISIBLE);
			homeBtn.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
						SnapLionMyAppActivity.KillFlag = false;
						try
						{
							com.snaplion.music.SLMusicPlayerActivity.closePlayer(MenusActivity.this);
						}
						catch (Exception e) 
						{
							e.printStackTrace();
						}
						Intent i = new Intent(MenusActivity.this,PreviewSnapLionAppsSelectActvity.class);
	    				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    				startActivity(i);
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
		
		backBtn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				SnapLionMyAppActivity.KillFlag = false;
				//finishFromChild(getParent());
				MenusActivity.super.onBackPressed();
			}
		});
		
		topL.addView(view);
		return topL;
	}
	@Override
    protected void onStart() 
    {
    	super.onStart();
    	mGaTracker.sendView("Menu_Screen");
    }
}
