package com.snaplion.scorecard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.preview.PreviewSnapLionAppsSelectActvity;
import com.snaplion.util.AppManager;

public class ScoreCardActivity extends AppSuperActivity
{
	private String BackFlag = "yes";
	String catName;
	private boolean registerLoginUser()
	{
		ScorecardManager.getInstance().registerUser(this);
		return false;
	}
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
	}
	class LoginUser extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(Void... params) 
		{
			return ScorecardManager.getInstance().loginUser(ScoreCardActivity.this);
		}
		@Override
		protected void onPostExecute(Boolean result) 
		{
			super.onPostExecute(result);
			if(result)
			{
				initUI();
			}
			else
			{
				Toast.makeText(ScoreCardActivity.this, "You are not authorized.", Toast.LENGTH_SHORT);
			}
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_home_activity);
		new LoginUser().execute();
	}
	private void initUI()
	{
		FragmentManager fragmentManager=getSupportFragmentManager();
		SCHomeFragment fragment=(SCHomeFragment)fragmentManager.findFragmentByTag("SCHomeFragment");
		if(fragment==null)
		{
			fragment=new SCHomeFragment();
		}
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.scHomeFragment_FrameLayout, fragment, "SCHomeFragment");
		fragmentTransaction.commit();
		
		if(true)//com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),AppManager.getInstance().getAPP_ID(),"1020"))
		{
			Display display = getWindowManager().getDefaultDisplay(); 
			BackFlag = "no";
			LinearLayout bottomLayout = (LinearLayout)findViewById(R.id.scorecard_bottom_bar);
			bottomLayout.setVisibility(View.VISIBLE);
			try
			{
				com.snaplion.util.BottomClass.showBLayout(ScoreCardActivity.this,bottomLayout,display.getWidth(),AppManager.getInstance().getAPP_ID(),AppManager.getInstance().getAPP_NAME());
			}
			catch (OutOfMemoryError e) 
			{
				e.printStackTrace();
			}
		}
		
		TextView tv = (TextView)findViewById(R.id.v_sub_name_txt);
		tv.setText("KINGS XI PUNJAB FIXTURES");
		
		Button backBtn = (Button)findViewById(R.id.v_sub_back_btn);
		if(BackFlag.equalsIgnoreCase("no"))
		{
			backBtn.setBackgroundResource(R.drawable.top_bar_logo);
		}
		else
		{
			backBtn.setBackgroundResource(R.drawable.selector_back_btn);
			backBtn.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					SnapLionMyAppActivity.KillFlag = false;
					onBackPressed();
				}
			});
		}
	}
}


