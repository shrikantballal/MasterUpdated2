package com.snaplion.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaplion.kingsxi.R;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.preview.PreviewSnapLionAppsSelectActvity;

public class TopClass {
	static Context context;
	static Activity activity;
	AppManager appManager;
	public TopClass()
	{
		appManager = AppManager.getInstance();
	}
	public static LinearLayout getTopView(Activity subActivity,Context applicationContext, LinearLayout bottomLayout, String string, String flag) {
		context = applicationContext;
		activity = subActivity;
		View view =  activity.getLayoutInflater().inflate(R.layout.top_layout, null);
		Button homeBtn = (Button)view.findViewById(R.id.top_home);
		Button backBtn = (Button)view.findViewById(R.id.top_back);
		TextView headerTxt = (TextView)view.findViewById(R.id.top_header_name);
		headerTxt.setText(string);
		headerTxt.setTypeface(AppManager.getInstance().lucida_grande_regular);
			
		if(flag.equalsIgnoreCase("no"))
		{
			backBtn.setVisibility(View.INVISIBLE);
		}
		else
		{
			//backBtn.setBackgroundResource(R.drawable.selector_back_btn);
			backBtn.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					SnapLionMyAppActivity.KillFlag = false;
					activity.finishFromChild(activity.getParent());
				}
			});
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
							com.snaplion.music.SLMusicPlayerActivity.closePlayer(context);
						}
						catch (Exception e) 
						{
							e.printStackTrace();
						}
						Intent i = new Intent(context,PreviewSnapLionAppsSelectActvity.class);
	    				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    				activity.startActivity(i);
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
		
		
		bottomLayout.addView(view);
		return bottomLayout;
	}
	

}
