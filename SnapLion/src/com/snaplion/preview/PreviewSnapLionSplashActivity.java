package com.snaplion.preview;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;

import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;


public class PreviewSnapLionSplashActivity extends AppSuperActivity{
	 private static final String TAG = "SnapLionSplashActivity";
	 protected boolean _active = true;
     protected int _splashTime = 1800;
     
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_splash);
		Thread splashTread = new Thread() 
		{
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while(_active && (waited < _splashTime)) {
                        sleep(100);
                        if(_active) {
                            waited += 100;
                        }
                    }
                } catch(InterruptedException e) {
                } finally {
                    startMainScreen();
                }
            }
        };
       splashTread.start();
	   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
   }

	protected void startMainScreen() {
	//	SnapLionAppScreenActvity.KillFlag = false;
		Intent i = new Intent(getApplicationContext(), PreviewSnapLionMainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
        finish();
	}
	

	@Override
	   public boolean onTouchEvent(MotionEvent event) {
	      if (event.getAction() == MotionEvent.ACTION_DOWN) {
	           _active = false;
	       }
	       return true;
	    }
	}
