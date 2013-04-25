package com.snaplion.scorecard;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.snaplion.kingsxi.R;
import com.snaplion.scorecard.entities.AllMatches;
import com.snaplion.scorecard.entities.Match;

public class ScorecardManager 
{
	private boolean isTestEnv=false;
	private final String TEST_URL_BASE_PATH = "http://test.litzscore.com";
	private final String URL_BASE_PATH = "http://api.litzscore.com";
		
	private String access_token=null;
	private final String access_key = "9f86784f15c246793fb457e02301b408";
	private final String secret_key = "5aa7f167b32f27e7111137b7c889e2b7";
	private final String app_id = "com.snaplion.kingsxi";
	
	
	private final String URL_REGISTER_USER = "http://www.litzscore.com/rest/apps/user/register/";
	private final String URL_LOGIN_USER = "http://www.litzscore.com/rest/apps/user/login/";
	private final String SEASION_BASE_URL="/rest/apps/season/";
	
	private static ScorecardManager scorecardManager;
	private ScorecardManager()
	{}
	public static ScorecardManager getInstance()
	{
		if(scorecardManager==null)
		{
			scorecardManager=new ScorecardManager();
		}
		return scorecardManager;
	}
	public String getDeviceID(Activity act)
	{
		TelephonyManager tManager = (TelephonyManager)act.getSystemService(Context.TELEPHONY_SERVICE);
		return tManager.getDeviceId();
	}
	public boolean registerUser(final Activity act)
	{
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(URL_REGISTER_USER);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("access_key", access_key));
	        nameValuePairs.add(new BasicNameValuePair("secret_key", secret_key));
	        nameValuePairs.add(new BasicNameValuePair("app_id",app_id));
	        nameValuePairs.add(new BasicNameValuePair("device_id", getDeviceID(act)));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			String result = EntityUtils.toString(response.getEntity());
			
			JSONObject rootJsonObject = new JSONObject(result);
			JSONObject dataJsonObject = rootJsonObject.getJSONObject("data");
			Boolean status = dataJsonObject.getBoolean("status");
			if(status)
			{
				String deviceToken = dataJsonObject.getString("device_token");
				SharedPreferences myPref = act.getSharedPreferences("SLScore"+app_id, act.MODE_PRIVATE);
		    	Editor edt = myPref.edit();
		    	edt.putString("DEVICE_TOKEN", deviceToken);
		    	edt.commit();
		    	return true;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}
	public boolean loginUser(final Activity act)
	{
		try
		{
			final SharedPreferences myPref = act.getSharedPreferences("SLScore"+app_id, act.MODE_PRIVATE);
			if(myPref.getString("DEVICE_TOKEN", null)==null)
			{
				if(!registerUser(act))
				{
					return false;
				}
			}
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(URL_LOGIN_USER);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("device_id", getDeviceID(act)));
	        nameValuePairs.add(new BasicNameValuePair("device_token", myPref.getString("DEVICE_TOKEN", null)));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			String result = EntityUtils.toString(response.getEntity());
			JSONObject rootJsonObject = new JSONObject(result);
			JSONObject dataJsonObject = rootJsonObject.getJSONObject("data");
			Boolean status = dataJsonObject.getBoolean("status");
			if(status)
			{
				String accessToken = dataJsonObject.getString("access_token");
				Editor edt = myPref.edit();
		    	edt.putString("ACCESS_TOKEN", accessToken);
		    	edt.commit();
		    	access_token=accessToken;
		    	return true;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}
	
	public ArrayList<String> getList(JSONArray json) throws Exception
	{
		ArrayList<String> list=new ArrayList<String>();
		for(int i=0;i<json.length();i++)
		{
			list.add(json.getString(i));
		}
		return list;
	}
	public ArrayList<ArrayList<String>> getList2(JSONArray json) throws Exception
	{
		ArrayList<ArrayList<String>> list=new ArrayList<ArrayList<String>>();
		for(int i=0;i<json.length();i++)
		{
			JSONArray jsonArr=json.getJSONArray(i);
			ArrayList<String> list2=new ArrayList<String>();
			for(int j=0;j<jsonArr.length();j++)
			{
				list2.add(jsonArr.getString(j));
			}
			list.add(list2);
		}
		return list;
	}
	public Match getMatche(String matchKey)
	{
		try
		{
			String strUrl="";
			if(isTestEnv)
			{
				strUrl = TEST_URL_BASE_PATH+SEASION_BASE_URL+"iplt20_2013/"+matchKey+"/";
			}
			else
			{
				strUrl = URL_BASE_PATH+SEASION_BASE_URL+"iplt20_2013/"+matchKey+"/?access_token="+access_token;
			}
			
			//strUrl = "http://litzscore.com/rest/apps/season/iplt20_2013/iplt20_2013_g6_livetest3/?access_token="+access_token;
			
			Log.d("MTK","Match_Url:"+strUrl);
			URL url = new URL(strUrl);
			URLConnection connection = url.openConnection();
	        connection.connect();
	        InputStream is = connection.getInputStream();
	        BufferedReader br = new BufferedReader(new InputStreamReader(is));
	        StringBuilder sb = new StringBuilder();
	   		String line;
	   		while ((line = br.readLine()) != null) 
	   		{
	   			sb.append(line);
	   		}
			JSONObject obj=new JSONObject(sb.toString());
			return new Match(obj.has("data")?obj.getJSONObject("data"):null);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	public AllMatches getAllMatchesByTeam()
	{
		try
		{
			String urlStr="";
			if(isTestEnv) // shrikant
				urlStr=TEST_URL_BASE_PATH+SEASION_BASE_URL+"iplt20_2013/";//filter_by_team/"+"iplt20_2013_KingsXI/";
			else {
				// urlStr=URL_BASE_PATH+SEASION_BASE_URL+"iplt20_2013/filter_by_team/"+"iplt20_2013_kxip/?access_token="+access_token;
				urlStr=URL_BASE_PATH+SEASION_BASE_URL+"iplt20_2013/?access_token="+access_token;
			}
			Log.d("MTK","strUrl:"+urlStr);
			URL url = new URL(urlStr);
			URLConnection connection = url.openConnection();
	        connection.connect();
	        InputStream is = connection.getInputStream();
	        BufferedReader br = new BufferedReader(new InputStreamReader(is));
	        StringBuilder sb = new StringBuilder();
	   		String line;
	   		while ((line = br.readLine()) != null) 
	   		{
	   			sb.append(line);
	   		}
			JSONObject obj=new JSONObject(sb.toString()); 
			return new AllMatches(obj.getJSONObject("data"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	public int getTeamLogoName(String card_name)
	{
		if(card_name.startsWith("PW"))
		{
			return R.drawable.pwi;
		}
		else if(card_name.equals("KKR"))
		{
			return R.drawable.kkr;
		}
		else if(card_name.equals("MI"))
		{
			return R.drawable.mi;
		}
		else if(card_name.equals("RR"))
		{
			return R.drawable.rr;
		}
		else if(card_name.equals("CSK"))
		{
			return R.drawable.csk;
		}
		else if(card_name.equals("SRH"))
		{
			return R.drawable.sr;
		}
		else if(card_name.equals("DD"))
		{
			return R.drawable.dd;
		}
		else if(card_name.equals("RCB"))
		{
			return R.drawable.rc;
		}
		else if(card_name.startsWith("KXI"))
		{
			return R.drawable.kxip;
		}
		else
		{
			return -1;
		}
	}
	public String getDate(String longNum)
	{
	    return new SimpleDateFormat("MMMM dd, yyyy").format(new Date(Long.parseLong(longNum)*1000));
	}
	public Date getDateObject(String longNum)
	{
	    return new Date(Long.parseLong(longNum)*1000);
	}
}
