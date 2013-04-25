package com.snaplion.kingsxi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.snaplion.util.AppManager;


/**
 * {@link IntentService} responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService 
{
	@SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";
    
    public GCMIntentService() 
    {
    	super(AppManager.getInstance().GCM_SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) 
    {
    	Log.i(TAG, "Device registered: regId = " + registrationId);
        GCMServerUtilities.register(context, registrationId);
    }
    public static String getString(InputStream in) 
    {
		StringBuilder sb = null;
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
            sb = new StringBuilder();
       		String line;
       		while ((line = br.readLine()) != null) 
       		{
       			sb.append(line);
       		}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return sb.toString();
	}
    
    
    @Override
    protected void onUnregistered(Context context, String registrationId) 
    {
        Log.i(TAG, "Device unregistered");
        if (GCMRegistrar.isRegisteredOnServer(context)) 
        {
            GCMServerUtilities.unregister(context, registrationId);
        } 
        else 
        {
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) 
    {
        Log.i(TAG, "Received message");
        generateNotification(context, intent);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) 
    {
        Log.i(TAG, "Received deleted messages notification");
    }

    @Override
    public void onError(Context context, String errorId) 
    {
        Log.i(TAG, "Received error: " + errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) 
    {
        Log.i(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, Intent intent) 
    {
        Log.v("Push","Received");
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		if(!taskInfo.get(0).topActivity.getClassName().equalsIgnoreCase("com.snaplion.SnapLionMainActivity"))
		{
			Bundle str = intent.getExtras();
			Log.v(TAG, str.getString("message"));
			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);	
			Notification notification = new Notification(R.drawable.icon, AppManager.getInstance().getAPP_NAME(), System.currentTimeMillis());
			notification.sound = Uri.parse(getRingtone(context));
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			Intent notifyIntent = new Intent(Intent.ACTION_MAIN);
			notifyIntent.setClass(context, SLMyAppSplashActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0);
			notification.setLatestEventInfo(context ,AppManager.getInstance().getAPP_NAME() ,str.getString("message"), pendingIntent);
			mNotificationManager.notify(12, notification);
		}
    }
    public static String getRingtone(Context context)
	{
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
		return preference.getString("ringtonePref", "DEFAULT_SOUND");        		 
	}
}
