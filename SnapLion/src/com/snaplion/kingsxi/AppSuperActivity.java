package com.snaplion.kingsxi;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.snaplion.util.AppManager;

public class AppSuperActivity extends FragmentActivity
{
	public Tracker mGaTracker;
	
	private void initGATracor(Context context)
	{
		GoogleAnalytics mGaInstance = GoogleAnalytics.getInstance(context);
		mGaInstance.setDebug(true);
		mGaTracker = mGaInstance.getTracker(AppManager.getInstance().GoogleAnalyticsID);
	}
	@Override
	protected void onCreate(Bundle arg0) 
	{
		super.onCreate(arg0);
		initGATracor(this);
	}
}
