package com.snaplion.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.service.MarketService;
import com.androidquery.util.AQUtility;
import com.inmobi.androidsdk.IMAdListener;
import com.inmobi.androidsdk.IMAdRequest;
import com.inmobi.androidsdk.IMAdRequest.ErrorCode;
import com.inmobi.androidsdk.IMAdView;
import com.inmobi.androidsdk.ai.controller.util.IMConstants;
import com.snaplion.fanwall.FanwalLoginManager;
import com.snaplion.fanwall.FanwallComment;
import com.snaplion.kingsxi.R;
import com.snaplion.locationservices.UserLocationManager;
import com.snaplion.menus.MenusItem;

public class AppManager
{
	public final boolean PREVIEWAPP_FLAG = true;
	
	//public final String LOGIN_URL = "http://dev.snaplion.com/dev/service_auth.php?LoginName=";//for preview app
	//public final String SERVICE_URL="http://dev.snaplion.com/dev/";
	public final String LOGIN_URL = "http://www.snaplion.com/service_auth.php?LoginName=";//for preview app
	public final String SERVICE_URL="http://www.snaplion.com/";

	public static final int PROGRESS = 0;
	public static final int DISPLAY = 1;
	public static final int ERROR = 2;
	public static final int WEB_CALL = 3;
	public static final int DIALOG_DISPLAY = 4;
	public static final int NOTIFY_ADAPTER = 5;
	
	//activity codes
		
	public final String DATA_DIRECTORY = "/sdcard/.SnapLion/";
	public final String DATA_DIR_BG = "/sdcard/.SnapLion/Bg/";
	public final String DATA_TEMP_BG = "/sdcard/.SnapLion/Temp/";
	public final String DATA_DIR_EVENTS = "/sdcard/.SnapLion/Events";
	
	//private String APP_ID="";
	//private String APP_NAME="";
	
	private String APP_ID="222";
	private String APP_NAME="Kings XI";
	
	//tausif : GCM constants
	//private String APP_ID = "135";
	//private String APP_NAME = "T-Raj";
	
	//private String APP_ID = "122";
	//private String APP_NAME = "Karmic Labz";
	
	//private String APP_ID="454";
	//private String APP_NAME="Ploof";
	
	//private String APP_ID="78";
	//private String APP_NAME="Humble P";
	
	public String getAPP_ID() {	return APP_ID;}
	public void setAPP_ID(String aPP_ID) {	APP_ID = aPP_ID;}
	public String getAPP_NAME() {	return APP_NAME;	}
	public void setAPP_NAME(String aPP_NAME) {		APP_NAME = aPP_NAME;	}
	
	//public String APP_ID = "87";
	//public String APP_NAME = "Dev_Tapan";
	 
	//public final String GCM_SERVER_URL = "http://dev.snaplion.com/dev/";
	public final String GCM_SERVER_URL = "http://www.snaplion.com";
	public final String GCM_SENDER_ID = "172304702254";
	public final String GCM_TAG = "GCMService";
	///////////////////////////////////////
	private static AppManager appManager;
	public Properties featureListProperties=null;
	public void loadfeatureListProperty(Context context)
	{
		try
		{
			String propertyStr=
					"1001=feature_home\n"+
					"1002=feature_bio\n"+
					"1003=feature_photo\n"+
					"1004=feature_music\n"+
					"1005=feature_all\n"+    
					"1006=feature_video\n"+
					"1007=feature_news\n"+
					"1008=feature_event\n"+
					"1009=feature_maillist\n"+
					"1010=feature_fanwall\n"+
					"1011=feature_contact\n"+
					"1012=feature_copyright\n"+
					"1013=feature_tickets\n"+
					"1015=feature_bgimages\n"+
					"1016=feature_appingredients\n"+
					"1017=feature_multibio\n"+
					"1018=feature_menus\n"+
					"1020=feature_scorecard\n"+
					"1019=feature_promo\n";
			appManager.featureListProperties = new Properties();
			//InputStream fileStream = context.getAssets().open("feature_list.properties");
			ByteArrayInputStream bais = new ByteArrayInputStream(propertyStr.getBytes("UTF-8"));
			appManager.featureListProperties.load(bais);
			//appManager.featureListProperties.load(fileStream);
	        //fileStream.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.gc();
			loadfeatureListProperty(context);
		}
	}
	//GoogleAnalyticsTracker tracker;
	//private String appId="";
	//private String appName="";
	public boolean fanwallPhotoRefreshFlag=false;
	private static FanwalLoginManager fanwalLoginManager;
	public Typeface lucida_grande_regular;
	public Typeface lucida_grande_bold;
	public Typeface lucida_sans_italic;
	public Typeface arial_regular;
	public Typeface lucida_sans_demibold_roman;
	public Typeface lucida_sans_regular;
	public void loadFont(Context superContext)
	{
		if(lucida_grande_regular==null)
		{
			AssetManager assetMgr = superContext.getAssets();
			lucida_grande_regular = Typeface.createFromAsset(assetMgr,"fonts/lg.ttf");
			lucida_grande_bold = Typeface.createFromAsset(assetMgr,"fonts/Lucida Grande Bold.ttf");
			lucida_sans_italic = Typeface.createFromAsset(assetMgr,"fonts/lucida_sans_italic.ttf");
			arial_regular = Typeface.createFromAsset(assetMgr,"fonts/arial.ttf");
			lucida_sans_demibold_roman = Typeface.createFromAsset(assetMgr,"fonts/lucida_sans_demibold_roman.ttf");
			lucida_sans_regular = Typeface.createFromAsset(assetMgr,"fonts/lucida_sans_regular.ttf");
		}
	}
	private AppManager()
	{}
	public static AppManager getInstance()
	{
		if(appManager==null)
		{
			appManager=new AppManager();
			File catchDir = new File("/sdcard/.SnapLion/"+appManager.getAPP_ID());
			if(!catchDir.exists())catchDir.mkdir();
			AQUtility.setCacheDir(catchDir);
			fanwalLoginManager = FanwalLoginManager.getInstance();
		}
		appManager.loadURL();
		return appManager;
	}
	
	////////////////////////////////////Fanwall Data and Methods start/////////////////////////////////////
	//public ArrayList<FanwallComment> FanwallPhotoArrayList=new ArrayList<FanwallComment>();
	public static FanwalLoginManager getFanwallManager()
	{
		if(fanwalLoginManager==null)
		{
			AppManager.getInstance();
			fanwalLoginManager = FanwalLoginManager.getInstance();
		}
		return fanwalLoginManager;
	}
	////////////////////////////////Fanwall Data and Methods end////////////////////////////////
	/////////////////////////////////Menus data methods start///////////////////////////////
	public ArrayList<MenusItem> menusArrayList=new ArrayList<MenusItem>();
	/////////////////////////////////Menus data methods end///////////////////////////////
	
	
//Webservices URLs
	
	//COMMON URLS
	public String APP_TABS_DETAILS_URL = "";
	public String SECTIONS_BGIMAGES_URL = "";
	public String REGISTER_LOCATION_URL = "";
	
	//GCM
	public String REGISTER_DEVICE_GCM_URL = "";
	public String UNREGISTER_DEVICE_GCM_URL = "";
	
	//SPLASH
	public String MODIFIED_TAB_DETAILS_URL = "";
	public String SPLASH_DETAILS_URL = "";
	
	//BIO
	public String BIO_DETAILS_URL = "";
	public String SNAPLIONABOUT_DETAILS_URL = "";
	
	//CONTACT
	public String CONTACT_DETAILS_URL = "";
	
	//COPYRIGHT
	public String COPYRIGHT_DETAILS_URL = "";
	
	//HOME
	public String HOME_DETAILS_URL = "";
	
	//MUSIC
	public String MUSIC_BY_ALBUM_NAME_URL = "";
	public String REGISTER_MAILING_LIST_URL = "";
	public String MUSIC_ALBUM_NAMES_URL = "";
	public String SEND_SONG_DOWNLOAD_LINK_URL = "";
	
	//PHOTOS
	public String PHOTO_SUBFOLDER_DETAILS_URL = "";
	public String PHOTOLIST_BY_CATEGORYID_URL = "";


	public String PICS_SUBFOLDERS_DETAILS_URL = "";
	
	//NEWS
	public String NEWS_URL = "";
	
	//EVENT
	public String EVENT_URL = "";
	
	//TICKET
	public String TICKET_URL = "";
	
	//VIDEO
	public String VIDEO_URL = "";
	
	public String MAILINGLIST_BGIMAGE_URL = "";
	
	//FANWALL
	public String FANWALL_PHOTO_URL = "";
	public String FANWALL_COMMENT_URL = "";
	public String FANWALL_POST_URL = "";
	public String FANWALL_ISPOSTUPDATENEEDED_URL = "";
	public String FANWALL_ISCOMMENTUPDATENEEDED_URL = "";
	public String FANWALL_ISPHOTOUPDATENEEDED_URL = "";
	public String FANWALL_MSG_URL = "";
	public String FANWALL_MSGLIKE_URL="";
	public String FANWALL_IsCommunityVisible_URL="";
	public String FANWALL_LOGIN_URL="";
	
	//MULTIBIO
	public String MULTIBIO_URL = "";
	
	//MENUS 
	public String MENUS_URL = "";
	
	//PROMO_URL
	public String PROMO_URL = "";
	
	
		
	public void loadURL()
	{
		//FANWALL
		FANWALL_PHOTO_URL = SERVICE_URL+"fanwalls/getphotos";
		FANWALL_COMMENT_URL = SERVICE_URL+"fanwalls/service_get_post_comments";
		FANWALL_POST_URL = SERVICE_URL+"fanwalls/service_get_artist_posts";
		FANWALL_ISPOSTUPDATENEEDED_URL = SERVICE_URL+"fanwalls/tabmodify";
		FANWALL_ISCOMMENTUPDATENEEDED_URL = SERVICE_URL+"fanwalls/tabmodify_post";
		FANWALL_ISPHOTOUPDATENEEDED_URL = SERVICE_URL+"fanwalls/tabmodify";
		FANWALL_MSG_URL = SERVICE_URL+"fanwalls/postmessage";
		FANWALL_MSGLIKE_URL = SERVICE_URL+"fanwalls/postlike";
		FANWALL_IsCommunityVisible_URL = SERVICE_URL+"fanwalls/service_settings";
		FANWALL_LOGIN_URL = SERVICE_URL+"fanwalls/login";
		//MULTIBIO
		MULTIBIO_URL = SERVICE_URL+"multibios/multi_bio_service";
		
		//COMMON URLS
		APP_TABS_DETAILS_URL = SERVICE_URL+"service_feature_v1.3.php?AppId="+APP_ID;
		SECTIONS_BGIMAGES_URL = SERVICE_URL+"service_bgimageV2modify.php?AppId="+APP_ID;
		/*pending*/REGISTER_LOCATION_URL = SERVICE_URL+"home/service_notification_andr_lat_long.php?AppId="+APP_ID+"&tid="+"<<TID>>"+"&device="+"<<DEVICE>>"+"&lat="+"<<LATITUDE>>"+""+"&long="+"<<LONGITUDE>>";
		
		//GCM
		REGISTER_DEVICE_GCM_URL = GCM_SERVER_URL + "/registerdevice.php";
		UNREGISTER_DEVICE_GCM_URL = GCM_SERVER_URL + "/unregisterdevice.php";
		
		//SPLASH
		MODIFIED_TAB_DETAILS_URL = SERVICE_URL+"service_tabmodifyV2.php?AppId="+APP_ID;
		SPLASH_DETAILS_URL = SERVICE_URL+"service_splash.php?AppId="+APP_ID;
		
		//BIO
		BIO_DETAILS_URL = SERVICE_URL+"service_bio.php?AppId="+APP_ID;
		SNAPLIONABOUT_DETAILS_URL = SERVICE_URL+"service_snaplion_about.php?AppId="+APP_ID;
		
		//CONTACT
		CONTACT_DETAILS_URL = SERVICE_URL+"service_contact.php?AppId="+APP_ID;
		
		//COPYRIGHT
		COPYRIGHT_DETAILS_URL = SERVICE_URL+"service_copyrightV2.php?AppId="+APP_ID;
		
		//HOME
		HOME_DETAILS_URL = SERVICE_URL+"service_bgimageV2.php?AppId="+APP_ID;
		
		//MUSIC
		/*done*/MUSIC_BY_ALBUM_NAME_URL = SERVICE_URL+"service_music_allv1.php?AppId="+APP_ID + "&albumname="+"<<ALBUM_NAME>>";
		/*done*/REGISTER_MAILING_LIST_URL = SERVICE_URL+"service_mailing_newdevice.php?" +
				"AppId="+APP_ID+
				"&email="+"<<EMAIL>>"+
				"&birthday="+"<<BIRTHDAY>>"+
				"&gender="+"<<GENDER>>"+
				"&country="+"<<COUNTRY>>"+
				"&postal="+"<<POSTAL>>"+
				"&firstname="+"<<FIRST_NAME>>"+
				"&lastname="+"<<LAST_NAME>>"+
				"&deviceid="+"<<DEVICE_ID>>";
		
		MUSIC_ALBUM_NAMES_URL = SERVICE_URL+"service_music.php?AppId="+APP_ID;
		/*done*/SEND_SONG_DOWNLOAD_LINK_URL = SERVICE_URL+"service_music_email.php?" +
				"AppId="+APP_ID+
				"&deviceid="+"<<DEVICE_ID>>"+
				"&MusicTrackId="+"<<MUSIC_TRACK_ID>>";
			
		//PHOTOS
		PHOTO_SUBFOLDER_DETAILS_URL = SERVICE_URL+"service_albumodify.php?AppId="+APP_ID;
	/*done*/	PHOTOLIST_BY_CATEGORYID_URL = SERVICE_URL+"service_photolist.php?AppId="+APP_ID+"&CategaryId="+"<<CATEGORY_ID>>";


		PICS_SUBFOLDERS_DETAILS_URL = SERVICE_URL+"service_photo.php?AppId="+APP_ID;
		
		//NEWS
		NEWS_URL = SERVICE_URL+"service_news2.php?AppId="+APP_ID;
		
		//EVENT
		EVENT_URL = SERVICE_URL+"service_event_past_v14m.php?AppId="+APP_ID;
		
		//TICKET
		TICKET_URL = SERVICE_URL+"service_ticketV2.php?AppId="+APP_ID;
		
		//VIDEO
		VIDEO_URL = SERVICE_URL+"service_video.php?AppId="+APP_ID;
		
		MAILINGLIST_BGIMAGE_URL = SERVICE_URL+"service_bgmailing.php?AppId="+APP_ID;
		
		//MENUS
		MENUS_URL = SERVICE_URL+"menus/menuDataApi?AppId="+APP_ID;
		
		//PROMO
		PROMO_URL = SERVICE_URL+"Promotions/NewsApi?AppId="+APP_ID;
	}
	@Override
	protected void finalize() throws Throwable 
	{
		//super.finalize();
		//stopSessionGSTracor();
		getInstance();
	}
///////////////////////////INMOBI///////////////////////////////
	public void displayAdds(LinearLayout mIMAdViewParent, Activity activity)
	{
		SharedPreferences settings = activity.getSharedPreferences("SLUpdate"+appManager.getAPP_ID(), activity.MODE_PRIVATE);
		boolean flag = settings.getBoolean("IS_SHOW_ADDS", false);
		if(flag)
		{
			mIMAdViewParent.setVisibility(IMAdView.VISIBLE);
			String inmobiAppId = settings.getString("INMOBI_KEY", "xyz");
			
			IMAdView mIMAdView = new IMAdView(activity, IMAdView.INMOBI_AD_UNIT_320X50,inmobiAppId);
			//IMAdView mIMAdView = new IMAdView(activity, IMAdView.INMOBI_AD_UNIT_320X50,activity.getResources().getString(R.string.inmobi_test_app_id));
			//mIMAdView.setRefreshInterval(20);
			IMAdRequest mAdRequest = new IMAdRequest();
			mAdRequest.setTestMode(false);
			mIMAdView.setIMAdRequest(mAdRequest);
			mIMAdView.loadNewAd(mAdRequest);
			mIMAdView.setIMAdListener(new MyIMAdListener(activity, mIMAdView));
			mIMAdViewParent.addView(mIMAdView);
		}
		else
		{
			mIMAdViewParent.setVisibility(IMAdView.GONE);
		}
	}
	class MyIMAdListener implements IMAdListener
	{
		Context context;
		IMAdView mIMAdView;
		public MyIMAdListener(Context context,IMAdView mIMAdView)
		{
			this.context=context;
			this.mIMAdView=mIMAdView;
		}
		@Override
		public void onShowAdScreen(IMAdView adView) 
		{
			Log.i(IMConstants.LOGGING_TAG,"InMobiAdActivity-> onShowAdScreen, adView: " + adView);
		}
		@Override
		public void onDismissAdScreen(IMAdView adView) 
		{
			Log.i(IMConstants.LOGGING_TAG,"InMobiAdActivity-> onDismissAdScreen, adView: " + adView);
		}
		@Override
		public void onAdRequestFailed(IMAdView adView, ErrorCode errorCode) 
		{
			Log.i(IMConstants.LOGGING_TAG,"InMobiAdActivity-> onAdRequestFailed, adView: " + adView	+ " ,errorCode: " + errorCode);
			//Toast.makeText(context,"Ad failed to load. Check the logcat for logs. Errorcode: "+ errorCode, Toast.LENGTH_SHORT).show();
//			if(errorCode.toString().trim().equalsIgnoreCase("NO_FILL"))
//			{
//				mIMAdView.loadNewAd();
//			}
		}
		@Override
		public void onAdRequestCompleted(IMAdView adView) 
		{
			Log.i(IMConstants.LOGGING_TAG,"InMobiAdActivity-> onAdRequestCompleted, adView: "+ adView);
		}
		@Override
		public void onLeaveApplication(IMAdView adView) 
		{
			Log.i(IMConstants.LOGGING_TAG,"InMobiAdActivity-> onLeaveApplication, adView: "+ adView);
		}
	}
////////////////////////////////////////////////////////////////
	public String GoogleAnalyticsID = "";
////////////////////////////////////////////////////////////////
	public void sendDeviceInformation(Context context)
	{
		Utility.debug("sendDevice Information...");
		String url = SERVICE_URL+"homeapplists/saveDeviceDetail";
		url=url+ "?AppId="+AppManager.getInstance().getAPP_ID();
		TelephonyManager tManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	    url=url+ "&device_id="+tManager.getDeviceId();
	    url=url+ "&device_type=Android";
	    sendHttpURL(url);
	}
	public void sendDeviceLocation(Context context, Location location)
	{
		Utility.debug("sendDevice Location...");
		String url = SERVICE_URL+"homeapplists/saveDeviceDetail";
		url=url+ "?AppId="+AppManager.getInstance().getAPP_ID();
		TelephonyManager tManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	    url=url+ "&device_id="+tManager.getDeviceId();
	    url=url+ "&device_type=Android";
	    url=url+ "&latitude="+location.getLatitude();
	    url=url+ "&longitude="+location.getLongitude();
	    sendHttpURL(url);		
	}
	public void checkVersion(final Activity context)
	{
		try
		{
			AQuery aq=new AQuery(context);
			final String pkgName=context.getApplicationInfo().packageName;
			final String currVar = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			String url="https://androidquery.appspot.com/api/market?app="+pkgName;
			Utility.debug("version check url :"+url);
			aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() 
			{
	            @Override
	            public void callback(String url, JSONObject json, AjaxStatus status) 
	            {
	            	try
	            	{
	            		if(json != null)
	                    {
	                		if(json.has("version"))
	                		{
	                			double newVer=json.getDouble("version");
	                			if(newVer>Double.parseDouble(currVar))
	                			{
	                				Utility.debug("found new version.");
	                				
	                				if(json.has("dialog"))
	                				{
	                					JSONObject dialogJsonObj=json.getJSONObject("dialog");
	                					if(dialogJsonObj.has("wbody"))
	                					{
	                						String wbody=dialogJsonObj.getString("wbody");
	                						TextView textView=new TextView(context);
	                						textView.setPadding(10, 5, 5, 5);
	    	                				textView.setText(Html.fromHtml(wbody));
	    	                				final AlertDialog dialog = new AlertDialog.Builder(context)
	    	                		        //.setIcon(context.getResources().getDrawable(R.drawable.icon))
	    	                				.setTitle("New Updates").setView(textView)
	    	                				.setPositiveButton("Update", new OnClickListener() 
	    	                				{
	    										@Override
	    										public void onClick(DialogInterface dialog, int which) 
	    										{
	    											String marketUrl="https://play.google.com/store/apps/details?id="+pkgName;
	    											Uri uri = Uri.parse(marketUrl);
	    											Intent intent = new Intent(Intent.ACTION_VIEW, uri);	    	
	    											context.startActivity(intent);
	    										}
	    									})
	    	                		        .setNegativeButton("Later", new OnClickListener() 
	    	                				{
	    										@Override
	    										public void onClick(DialogInterface dialog, int which) 
	    										{
	    											dialog.dismiss();
	    										}
	    									})
	    	                		        .create();
	    	                				dialog.show();
	                					}
	                				}
	                			}
	                			else
	                			{
	                				Utility.debug("you have updated version.");
	                			}
	                		}
	                    }
	                	else
	                	{
	                		
	                    }
	            	}
	            	catch(Exception ex)
	            	{
	            		ex.printStackTrace();
	            	}
	            }
			});
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	private void sendHttpURL(String urlStr) 
	{
		try
		{
			Utility.debug("URL: "+urlStr);
			URL url = new URL(urlStr);
	        URLConnection connection = url.openConnection();
	        connection.connect();
	        InputStream is = connection.getInputStream();
	        String result = Utility.getString(is);
	        Utility.debug("testURL result : "+result);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
