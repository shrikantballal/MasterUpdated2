package com.snaplion.scorecard;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.snaplion.util.Utility;

public class AlarmManagerBroadcastReceiver  extends BroadcastReceiver 
{
	String matchKey;
//	public AlarmManagerBroadcastReceiver(String matchKey)
//	{
//		this.matchKey=matchKey;
//	}
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
		wl.acquire();
		Utility.debug("onReceive:"+String.valueOf(System.currentTimeMillis()));
		//new DownloadDataAsyncTask(context, matchKey,false,null,false).execute();
		wl.release();
	}
	public void setAlarm(Context context)
	{
		AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5 , pi);
	}
	
	public void cancelAlarm(Context context)
	{
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
}