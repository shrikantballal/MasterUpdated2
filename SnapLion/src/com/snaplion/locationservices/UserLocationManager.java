package com.snaplion.locationservices;

import java.text.DecimalFormat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.snaplion.locationservices.UserLocation.LocationResult;
import com.snaplion.util.AppManager;

public class UserLocationManager 
{
	private final static String TAG="UserLocationManager";
	private Location userCurrentlocation;
	private static UserLocationManager userLocationManager;
	Context context;
	private UserLocationManager()
	{}
	public static UserLocationManager getInstance()
	{
		if(userLocationManager==null)
			userLocationManager=new UserLocationManager();
		return userLocationManager;
	}
	public void updateLocation(final Context context)
	{
		this.context=context;
		checkGPSAvailability(context);
		//new BackProcess().execute();
		Handler handler=new Handler();
		handler.post(new Runnable() 
		{
			@Override
			public void run() 
			{
				UserLocation userLocation=new UserLocation();
				if(userLocation.getLocation(context, locationResult))
				{
					Log.d(TAG, "User location updated.");
				}
			}
		});
	}
//	class BackProcess extends AsyncTask<Void, Void, Boolean>
//	{
//
//		@Override
//		protected Boolean doInBackground(Void... params) 
//		{
//			UserLocation userLocation=new UserLocation();
//			if(userLocation.getLocation(context, locationResult))
//			{
//				Log.d(TAG, "User location updated.");
//				return true;
//			}
//			return false;
//		}
//	}
	LocationResult locationResult=new LocationResult() 
	{
		@Override
		public void gotLocation(Location location) 
		{
			System.out.println("location : "+location);
			
			/*//////////////For Debug Only//////////////
			Location usLocation=new Location("usLoc");
			usLocation.setLatitude(32.5897222);
			usLocation.setLongitude(-96.8566667);
			location=usLocation;
			/////////////////////////////////////////*/
			
			setUserCurrentlocation(location);
		}
	}; 
	public Location getUserCurrentlocation() 
	{
		if(userCurrentlocation!=null)
		{
			SharedPreferences prefs = context.getSharedPreferences("SLUpdate"+AppManager.getInstance().getAPP_ID(), context.MODE_PRIVATE);
			if(!prefs.contains("SendFirstLocation"))
			{
				AppManager.getInstance().sendDeviceLocation(context, userCurrentlocation);
				Editor editor = prefs.edit();
				editor.putBoolean("SendFirstLocation", false);
				editor.commit();
			}
		}
		return userCurrentlocation;
	}
	public void setUserCurrentlocation(Location userCurrentlocation) 
	{
		this.userCurrentlocation = userCurrentlocation;
	}
	public double getDistance(double targetLatitude,double targetLongitude)
	{
		Location docLocation = new Location("Target_Point");  
		docLocation.setLatitude(targetLatitude);  
		docLocation.setLongitude(targetLongitude);
		double dst=0.00;
		if(this.getUserCurrentlocation()!=null)
		{
			dst=(this.getUserCurrentlocation().distanceTo(docLocation)) * 0.00062137119;
		}
		DecimalFormat newFormat = new DecimalFormat("#.##");
		return Double.valueOf(newFormat.format(dst));//multiply by 0.00062137119 for convert meters to Miles
	}	
	public void checkGPSAvailability(Context context)
	{
		final LocationManager manager = (LocationManager)context.getSystemService( Context.LOCATION_SERVICE );
	    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) 
	    {
	        buildAlertMessageNoGps(context);
	    }
	    else
	    {
	    	SharedPreferences settings = context.getSharedPreferences("SLUpdate"+AppManager.getInstance().getAPP_ID(), context.MODE_PRIVATE);
	    	if(settings.getBoolean("isShowLocation", true))
	    	{
	    		SharedPreferences.Editor editor = settings.edit();
	    		editor.putBoolean("isShowLocation", false);
	    		editor.commit();
	    	}
	    }
	}
	private void buildAlertMessageNoGps(final Context context) 
    {
		SharedPreferences prefs = context.getSharedPreferences("SLUpdate"+AppManager.getInstance().getAPP_ID(), context.MODE_PRIVATE);
		boolean isShowLocation = prefs.getBoolean("isShowLocation", true);
		if(isShowLocation)
		{
			Editor editor = prefs.edit();
			editor.putBoolean("isShowLocation", false);
			editor.commit();
			
			final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		    builder.setMessage("Enable location services.")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() 
	           {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) 
	               {
	                   launchGPSOptions(context); 
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() 
	           {
	               public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) 
	               {
	                    dialog.cancel();
	               }
	           });
		    final AlertDialog alert = builder.create();
		    alert.show();
		}
	}
	private void launchGPSOptions(Context context)
	{
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		context.startActivity(intent);
	}
	/*protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
        if(requestCode == REQUEST_CODE && resultCode == 0)
        {
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if(provider != null)
            {
                Log.v(TAG, " Location providers: "+provider);
                //Start searching for location and update the location text when update available. 
                //Do whatever you want
                startFetchingLocation();
            }
            else
            {
                //Users did not switch on the GPS
            }
        }
    }*/
}
