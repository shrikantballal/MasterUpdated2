package com.snaplion.fanwall;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.auth.FacebookHandle;
import com.androidquery.auth.TwitterHandle;
import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.locationservices.UserLocationManager;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.preview.PreviewSnapLionAppsSelectActvity;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class LoginActivity extends Activity 
{
	public static String fbPermission = "read_stream,read_friendlists,manage_friendlists,manage_notifications,publish_stream,publish_checkins,offline_access,user_photos,user_likes,user_groups,friends_photos,publish_actions";
	AQuery aq;
	//String permission="";
	String catName;
	String BackFlag = "yes";
	LinearLayout mainBg = null;
	
	Button facebook_loginbutton;
	Button twitter_loginbutton;
	AppManager appManager;
	
	private final int ACTIVITY_SSO = 1000;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fanwall_login_activity);
		setResult(RESULT_OK);
		appManager=AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		aq=new AQuery(this);
		SnapLionMyAppActivity.KillFlag = true;
		UserLocationManager.getInstance().updateLocation(this);
		Intent i = getIntent();
		catName = i.getStringExtra("FanwallSectionName");
		
		TextView tv = (TextView)findViewById(R.id.v_sub_name_txt);
		tv.setText(catName);
		tv.setTypeface(appManager.lucida_grande_regular);
		
		if(appManager.PREVIEWAPP_FLAG)
		{
			Button homeBtn = (Button)findViewById(R.id.v_home_btn);
			homeBtn.setVisibility(View.VISIBLE);
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
					Intent i = new Intent(LoginActivity.this,PreviewSnapLionAppsSelectActvity.class);
    				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    				LoginActivity.this.startActivity(i);
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
					finishFromChild(getParent());
				}
			});
		}

		mainBg = (LinearLayout) findViewById(R.id.fanwall_root_LinearLayout);
		BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("FanwallMainBg"+appManager.getAPP_ID())));
		mainBg.setBackgroundDrawable(background);
		background = null;
		
		//facebook
		FanwalLoginManager.getInstance().FBhandle = new FacebookHandle(this, getResources().getString(R.string.facebook_app_id), fbPermission)
	    {
			@Override
			public boolean expired(AbstractAjaxCallback<?, ?> cb, AjaxStatus status) 
			{
				Log.d("MTK",status.toString());
				if(status.getCode() == 401)
				{
					return true;
				}
				return super.expired(cb, status);
			}
		};
		
		FanwalLoginManager.getInstance().TWhandle = new TwitterHandle(this, getResources().getString(R.string.consumerKey), getResources().getString(R.string.consumerSecret))
		{
	        @Override
	        protected void authenticated(String secret, String token) 
	        {
	                AQUtility.debug("secret", secret);
	                AQUtility.debug("token", token);
	        }
	    };
	    
	    
	    
	    facebook_loginbutton=(Button)findViewById(R.id.facebookLoginButton);
		facebook_loginbutton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				System.gc();
				if(FanwalLoginManager.getInstance().FBhandle.authenticated())
				{
					unauthorizeFacebook();
				}
				else
				{
					unauthorizeTwitter();
					authorizeFacebook();
				}
			}
		});
		twitter_loginbutton=(Button)findViewById(R.id.twitterLoginButton);
		twitter_loginbutton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				System.gc();
				
				//postCommentOnFbWall(null, "tausif fb testing");
				
				if(FanwalLoginManager.getInstance().TWhandle.authenticated())
				{
					unauthorizeTwitter();
				}
				else
				{
					unauthorizeFacebook();
					authorizeTwitter();
				}
			}
		});
		
	}
	@Override
	protected void onResume() 
	{
		initilizeButtonStates();
		super.onResume();
	}
	private void initilizeButtonStates()
	{
		if(FanwalLoginManager.getInstance().FBhandle.authenticated())
		{
			facebook_loginbutton.setBackgroundResource(R.drawable.selector_facebook_logout);
		}
		else
		{
			facebook_loginbutton.setBackgroundResource(R.drawable.selector_facebook_login);
		}
		if(FanwalLoginManager.getInstance().TWhandle.authenticated())
		{
			twitter_loginbutton.setBackgroundResource(R.drawable.selector_twitter_logout);
		}
		else
		{
			twitter_loginbutton.setBackgroundResource(R.drawable.selector_twitter_login);
		}
	}
	/////////////////////////Twitter methods/////////////////////////
	public boolean isTwitterAuthorized()
	{
		return FanwalLoginManager.getInstance().TWhandle.authenticated();
	}
	public void unauthorizeTwitter()
	{
		if(FanwalLoginManager.getInstance().TWhandle.authenticated())
		{
			FanwalLoginManager.getInstance().TWhandle.unauth();
			String TW_TOKEN= "aq.tw.token";
			String TW_SECRET = "aq.tw.secret";
			Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
			editor.putString(TW_TOKEN, null);
			editor.putString(TW_SECRET, null);
			editor.commit();
			
			SharedPreferences myPref = getSharedPreferences("FanwallRef"+AppManager.getInstance().getAPP_ID(), android.content.Context.MODE_PRIVATE);
			SharedPreferences.Editor prefEditor = myPref.edit();
			prefEditor.putString("fanid", null);
			prefEditor.putString("username", null);
			prefEditor.putString("email", null);
			prefEditor.putString("logintype", null);
			prefEditor.commit();
			
			twitter_loginbutton.setBackgroundResource(R.drawable.selector_twitter_login);
			Toast.makeText(this, getResources().getString(R.string.tw_logout_success), Toast.LENGTH_SHORT).show();
		}
	}
	public void authorizeTwitter()
	{
		String url = "https://api.twitter.com/1/statuses/user_timeline.json";
	    aq.auth(FanwalLoginManager.getInstance().TWhandle).progress(R.id.loginProgressBar).ajax(url, JSONArray.class, this, "twitterLoginResultHandler");
	    
	    
	}
	public void twitterLoginResultHandler(String url, JSONArray ja, AjaxStatus status)
	{
		String str = null;
		if(ja != null)
		{
			try 
			{
				JSONObject jsonObject = ja.getJSONObject(0);
				JSONObject userJsonObject = jsonObject.getJSONObject("user");
				String profile_image_url = userJsonObject.getString("profile_image_url");
				String name = userJsonObject.getString("name");
				String id = userJsonObject.getString("id");
				Log.d("MTK","profile_image_url:"+profile_image_url+"\n name:"+name+"\n id:"+id);
				new RegisterOnCMS(id,name,profile_image_url, "twitter").execute();
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		Log.d("MTK", "status code:"+status.getCode()+"\n error:"+status.getError());
	}
	
	////////////////////////////Facebook methods//////////////////////////
	public boolean isFacebookAuthorized()
	{
		return FanwalLoginManager.getInstance().FBhandle.authenticated();
	}
	public void authorizeFacebook()
	{
		FanwalLoginManager.getInstance().FBhandle.sso(ACTIVITY_SSO);
		String url = "https://graph.facebook.com/me";
	    aq.auth(FanwalLoginManager.getInstance().FBhandle).progress(R.id.loginProgressBar).ajax(url, JSONObject.class, this, "facebookLoginResultHandler");
	}
	
	public void unauthorizeFacebook()
	{
		if(FanwalLoginManager.getInstance().FBhandle.authenticated())
		{
			String FB_TOKEN = "aq.fb.token";
			String FB_PERMISSION = "aq.fb.permission";
			FanwalLoginManager.getInstance().FBhandle.unauth();
			Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
			editor.putString(FB_TOKEN, null);
			editor.putString(FB_PERMISSION, null);
			AQUtility.apply(editor);
			
			SharedPreferences myPref = getSharedPreferences("FanwallRef"+AppManager.getInstance().getAPP_ID(), android.content.Context.MODE_PRIVATE);
			SharedPreferences.Editor prefEditor = myPref.edit();
			prefEditor.putString("fanid", null);
			prefEditor.putString("username", null);
			prefEditor.putString("email", null);
			prefEditor.putString("logintype", null);
			prefEditor.commit();
			
			facebook_loginbutton.setBackgroundResource(R.drawable.selector_facebook_login);
			Toast.makeText(this, getResources().getString(R.string.fb_logout_success), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void facebookLoginResultHandler(String url, JSONObject jo, AjaxStatus status)
	{
		String str = null;
		if(jo != null)
		{
			try 
			{
				String id = jo.getString("id");
				String name = jo.getString("name");
				String photo = "http://graph.facebook.com/"+id+"/picture?type=small";
				Log.d("MTK","photo:"+photo+"\n name:"+name+"\n id:"+id);
				new RegisterOnCMS(id,name,photo,"facebook").execute();
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		Log.d("MTK", "status code:"+status.getCode()+"\n error:"+status.getError());
		//aq.id(R.id.result).visible().text(str);
	}
	
	////////////////////////////////////Sending Login Data to CMS///////////////////////////////////
	class RegisterOnCMS extends AsyncTask<String, Void, Boolean>
	{
		String id, name, photo,loginType;
		public RegisterOnCMS(String id, String name, String photo,String loginType)
		{
			this.id=id;
			this.name=name;
			this.photo=photo;
			this.loginType=loginType;
		}
		@Override
		protected Boolean doInBackground(String... params) 
		{
			boolean flag=true;
			try 
	        {
				Location loc=UserLocationManager.getInstance().getUserCurrentlocation();
				double latitude;
				double longitude;
				if(loc!=null)
				{
					latitude=loc.getLatitude();
					longitude=loc.getLongitude();
				}
				else
				{
					latitude=0;
					longitude=0;
				}
				
				
				String DeviceID = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
				
				String fanwall_login_url =  AppManager.getInstance().FANWALL_LOGIN_URL+"?" +
									"deviceId="+DeviceID+"&" +
									"deviceType=android&" +
									"name="+name+"&" +
									"email="+id+"&" +
									"photo="+photo+"&" +
									"loginThrough=2&" +
									"latitude="+latitude+"&" +
									"longitude="+longitude;
				fanwall_login_url = Utility.replace(fanwall_login_url, " ", "%20");
				Utility.debug("facebook login url : "+fanwall_login_url);
				
				InputStream in = HttpConnection.connect(fanwall_login_url);
				String result = Utility.getString(in);
				if(result!=null && result.split("=").length==2)
				{
					SharedPreferences myPref = getSharedPreferences("FanwallRef"+AppManager.getInstance().getAPP_ID(), android.content.Context.MODE_PRIVATE);
					SharedPreferences.Editor prefEditor = myPref.edit();
					prefEditor.putString("fanid", result.split("=")[1]);
					prefEditor.putString("username", name);
					prefEditor.putString("email", String.valueOf(id));
					prefEditor.putString("logintype", loginType);
					prefEditor.commit();
				}
				else
				{
					flag=false;
					Utility.debug("facebook login result (error): "+result);
				}
			} 
	        catch (Exception e) 
	        {
	        	flag=false;
	        }
			return flag;
		}
		@Override
		protected void onPostExecute(Boolean result) 
		{
			if(result)
			{
				if(loginType.equals("facebook"))
				{
					Toast.makeText(LoginActivity.this, getResources().getString(R.string.fb_login_success), Toast.LENGTH_SHORT).show();
					facebook_loginbutton.setBackgroundResource(R.drawable.selector_facebook_logout);
				}
				else
				{
					Toast.makeText(LoginActivity.this, getResources().getString(R.string.tw_login_success), Toast.LENGTH_SHORT).show();
					twitter_loginbutton.setBackgroundResource(R.drawable.selector_twitter_logout);
				}
				finish();
			}
			else
			{
				if(loginType.equals("facebook"))
				{
					Toast.makeText(LoginActivity.this, getResources().getString(R.string.fb_login_failed), Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(LoginActivity.this, getResources().getString(R.string.tw_login_failed), Toast.LENGTH_SHORT).show();
				}
			}
			super.onPostExecute(result);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch(requestCode) {
			
	    	case ACTIVITY_SSO: {
	    		if(FanwalLoginManager.getInstance().FBhandle != null){
	    			FanwalLoginManager.getInstance().FBhandle.onActivityResult(requestCode, resultCode, data);	  
	    		}
	    		break;
	    	}
	    	
		}
	}
	
}
