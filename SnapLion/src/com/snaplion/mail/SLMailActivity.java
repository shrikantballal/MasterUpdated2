package com.snaplion.mail;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inmobi.androidsdk.IMAdView;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class SLMailActivity extends AppSuperActivity
{
	private boolean isSuccessFlag=true;
	@Override
	protected void onResume() 
	{
		SnapLionMyAppActivity.KillFlag = true;
		super.onResume();
	}
	protected static final int DOWNLOAD_BG = 6;
	protected static final int CALENDER_VIEW = 7;
	protected static final int GENDER_VIEW = 8;
	protected static final int COUNTRY_VIEW = 9;

	private LinearLayout mailBgLayout = null;
	private LinearLayout mailTopLayout = null;
	private String res = null;
	private String BackFlag = "yes";
	private String status_flag = null;
	
	private EditText et = null;
	private EditText et1 = null; 
	private EditText et2 = null; 
	private EditText et3 = null; 
	private EditText et4 = null; 
	private EditText et5 = null; 
	private EditText et6 = null; 
	private String[] yearArray = new String[73];
	private String[] sexArray = {"Male","Female"};
	static final String[] COUNTRIES = new String[] {
		    "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
		    "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
		    "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
		    "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",
		    "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia",
		    "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory",
		    "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi",
		    "Cote d'Ivoire", "Cambodia", "Cameroon", "Canada", "Cape Verde",
		    "Cayman Islands", "Central African Republic", "Chad", "Chile", "China",
		    "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo",
		    "Cook Islands", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czech Republic",
		    "Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica", "Dominican Republic",
		    "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea",
		    "Estonia", "Ethiopia", "Faeroe Islands", "Falkland Islands", "Fiji", "Finland",
		    "Former Yugoslav Republic of Macedonia", "France", "French Guiana", "French Polynesia",
		    "French Southern Territories", "Gabon", "Georgia", "Germany", "Ghana", "Gibraltar",
		    "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau",
		    "Guyana", "Haiti", "Heard Island and McDonald Islands", "Honduras", "Hong Kong", "Hungary",
		    "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica",
		    "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos",
		    "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg",
		    "Macau", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands",
		    "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia", "Moldova",
		    "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia",
		    "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand",
		    "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "North Korea", "Northern Marianas",
		    "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru",
		    "Philippines", "Pitcairn Islands", "Poland", "Portugal", "Puerto Rico", "Qatar",
		    "Reunion", "Romania", "Russia", "Rwanda", "Sqo Tome and Principe", "Saint Helena",
		    "Saint Kitts and Nevis", "Saint Lucia", "Saint Pierre and Miquelon",
		    "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Saudi Arabia", "Senegal",
		    "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands",
		    "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "South Korea",
		    "Spain", "Sri Lanka", "Sudan", "Suriname", "Svalbard and Jan Mayen", "Swaziland", "Sweden",
		    "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "The Bahamas",
		    "The Gambia", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey",
		    "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Virgin Islands", "Uganda",
		    "Ukraine", "United Arab Emirates", "United Kingdom",
		    "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan",
		    "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Wallis and Futuna", "Western Sahara",
		    "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"
		  };
	 
	AppManager appManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.mail);
		}catch (OutOfMemoryError e) {System.gc();}
		Utility.closeAllBelowActivities(this);
		SnapLionMyAppActivity.KillFlag = true;
		appManager = AppManager.getInstance();
		/////////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent,this);
		/////////////////////////////
		Intent i = getIntent();
		
		et = (EditText)findViewById(R.id.mail_name);
		et.setTypeface(appManager.lucida_grande_regular);
		et1 = (EditText)findViewById(R.id.mail_last_name);
		et1.setTypeface(appManager.lucida_grande_regular);
		et2 = (EditText)findViewById(R.id.mail_email);
		et2.setTypeface(appManager.lucida_grande_regular);
		et3 = (EditText)findViewById(R.id.mail_bday);
		et3.setTypeface(appManager.lucida_grande_regular);
		et4 = (EditText)findViewById(R.id.mail_gender);
		et4.setTypeface(appManager.lucida_grande_regular);
		et5 = (EditText)findViewById(R.id.mail_country);
		et5.setTypeface(appManager.lucida_grande_regular);
		et6 = (EditText)findViewById(R.id.mail_postal);
		et6.setTypeface(appManager.lucida_grande_regular);

		mailBgLayout = (LinearLayout)findViewById(R.id.mail_bg);
		mailTopLayout = (LinearLayout)findViewById(R.id.mail_top_bar);
		
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		
		if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1009")){
			BackFlag = "no";
			LinearLayout bottomLayout = (LinearLayout)findViewById(R.id.mail_bottom_bar);
			bottomLayout.setVisibility(View.VISIBLE);
			com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,width,appManager.getAPP_ID(),i.getStringExtra("appname"));
		 }
		
		com.snaplion.util.TopClass.getTopView(SLMailActivity.this,getApplicationContext(),mailTopLayout,i.getStringExtra("MailName"),BackFlag);
		
		et3.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				for(int i=0;i<73;i++)
				{
					yearArray[i] = (1940+i)+"";
				}
				showDialog(CALENDER_VIEW);
				return false;
			}
		});
		
		
		et4.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showDialog(GENDER_VIEW);
				return false;
			}
		});
		et5.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showDialog(COUNTRY_VIEW);
				return false;
			}
		});
		
		Button mailSubBtn = (Button)findViewById(R.id.mail_submit);
			mailSubBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) 
				{
					TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

					if(et.getText().toString().equalsIgnoreCase(""))
						Toast.makeText(getApplicationContext(), "Please enter First Name.", Toast.LENGTH_LONG).show();
					else if(et1.getText().toString().equalsIgnoreCase(""))
						Toast.makeText(getApplicationContext(), "Please enter Last Name.", Toast.LENGTH_LONG).show();
					else if(et2.getText().toString().equalsIgnoreCase(""))
						Toast.makeText(getApplicationContext(), "Please enter email id.", Toast.LENGTH_LONG).show();
					else if(!isEmailValid(et2.getText().toString()))
					{
						Toast.makeText(getApplicationContext(), "Please enter Correct Email Id.", Toast.LENGTH_LONG).show();
					}
					else if(com.snaplion.util.Utility.isOnline(getApplicationContext()))
					{
						String mailingListUrlurl = appManager.REGISTER_MAILING_LIST_URL;
						mailingListUrlurl = mailingListUrlurl.replaceAll("<<EMAIL>>",et2.getText().toString());
						mailingListUrlurl = mailingListUrlurl.replaceAll("<<BIRTHDAY>>",et3.getText().toString());
						mailingListUrlurl = mailingListUrlurl.replaceAll("<<GENDER>>",et4.getText().toString());
						mailingListUrlurl = mailingListUrlurl.replaceAll("<<COUNTRY>>",et5.getText().toString());
						mailingListUrlurl = mailingListUrlurl.replaceAll("<<POSTAL>>",et6.getText().toString());
						mailingListUrlurl = mailingListUrlurl.replaceAll("<<FIRST_NAME>>",et.getText().toString());
						mailingListUrlurl = mailingListUrlurl.replaceAll("<<LAST_NAME>>",et1.getText().toString());
						mailingListUrlurl = mailingListUrlurl.replaceAll("<<DEVICE_ID>>",tManager.getDeviceId());
						
						//String sumMailStr = appManager.SERVICE_URL+"service_mailing_newdevice.php?AppId="+appId+"&email="+et2.getText().toString()+"&birthday="+et3.getText().toString()+"&gender="+et4.getText().toString()+"&country="+et5.getText().toString()+"&postal="+et6.getText().toString()+"&firstname="+et.getText().toString()+"&lastname="+et1.getText().toString()+"&deviceid="+tManager.getDeviceId();
						//	String sumMailStr = appManager.SERVICE_URL+"service_mailing_newdevice.php?AppId="+appId+"&email="+et1.getText().toString()+"&birthday="+et2.getText().toString()+"&gender="+et3.getText().toString()+"&country="+et4.getText().toString()+"&postal="+et5.getText().toString()+"&message="+et6.getText().toString()+"&name="+et.getText().toString()+"&deviceid="+tManager.getDeviceId();
						mailingListUrlurl = Utility.replace(mailingListUrlurl, " ", "%20");
							Log.v("MAIL_URL", mailingListUrlurl);
							showDialog(appManager.PROGRESS);
							Utility.debug("REGISTER_MAILING_LIST_URL : "+mailingListUrlurl);
							new MailSubAsync().execute(mailingListUrlurl);
					}else{
							Toast.makeText(getApplicationContext(), "Network Fail.Please try later.!", Toast.LENGTH_LONG).show();
						}
					}					
			});
			
			BitmapDrawable background = new BitmapDrawable(Utility.getScaledCroppedImageByDisplay(this, SLMyAppSplashActivity.FetchImage("MailBG"+appManager.getAPP_ID())));
			mailBgLayout.setBackgroundDrawable(background);
			background = null;
			
        	SharedPreferences myPref1 = getSharedPreferences("MailPref"+appManager.getAPP_ID(), MODE_PRIVATE);
    		  if(myPref1.getString("MLName", "") != ""){
    			et.setText(myPref1.getString("MLName", ""));
    			et1.setText(myPref1.getString("MLLName", ""));
    			et2.setText(myPref1.getString("MLEmail", ""));
    			et3.setText(myPref1.getString("MLBD", ""));
    			et4.setText(myPref1.getString("MLGEN", ""));
    			et5.setText(myPref1.getString("MLC", ""));
    			et6.setText(myPref1.getString("MLPC", ""));
    		 }
    		  
    	 ((TextView)findViewById(R.id.mtv1)).setTypeface(appManager.lucida_grande_regular);
    	 ((TextView)findViewById(R.id.mtv2)).setTypeface(appManager.lucida_grande_regular);
    	 ((TextView)findViewById(R.id.mtv3)).setTypeface(appManager.lucida_grande_regular);
    	 ((TextView)findViewById(R.id.mtv4)).setTypeface(appManager.lucida_grande_regular);
    	 ((TextView)findViewById(R.id.mtv5)).setTypeface(appManager.lucida_grande_regular);

	}
	
	
	Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	        case AppManager.DISPLAY:
	        	Utility.FileCreate();
	        	try {
	        		if(status_flag != null)
	        		{
	        			if(status_flag.equalsIgnoreCase("1"))
	        			{
	        				SharedPreferences settings = getSharedPreferences("MailPref"+appManager.getAPP_ID(), MODE_PRIVATE);
				        	SharedPreferences.Editor editor = settings.edit();
		       		 		editor.putBoolean("MailContact",true);
		       		 		editor.commit();
	        			}
	        			else if(status_flag.equalsIgnoreCase("0"))
	        			{
	        				SharedPreferences settings = getSharedPreferences("MailPref"+appManager.getAPP_ID(), MODE_PRIVATE);
				        	SharedPreferences.Editor editor = settings.edit();
		       		 		editor.putBoolean("MailContact",false);
		       		 		editor.commit();
	        			}
	        		}
	        		if(res != null && res.equalsIgnoreCase("Thank you for your information")){
	        			SharedPreferences settings = getSharedPreferences("MailPref"+appManager.getAPP_ID(), MODE_PRIVATE);
			        	SharedPreferences.Editor editor = settings.edit();
	       		 		editor.putString("MLName", et.getText().toString());
	       		 		editor.putString("MLLName", et1.getText().toString());
	       		 		editor.putString("MLEmail", et2.getText().toString());
	       		 		editor.putString("MLBD", et3.getText().toString());
	       		 		editor.putString("MLGEN", et4.getText().toString());
	       		 		editor.putString("MLC", et5.getText().toString());
	       		 		editor.putString("MLPC", et6.getText().toString());
	       		 		editor.commit();
	       		 		finishFromChild(getParent());
	        		}
	        		Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
	        		dismissDialog(appManager.PROGRESS);
	    		 } catch (Exception e1) {e1.printStackTrace();}	
	        	break;
	        case AppManager.PROGRESS:
	        		try {
	        			if(Utility.isOnline(getApplicationContext()))
	        			{
	        				new MyDownloadTask().execute(appManager.MAILINGLIST_BGIMAGE_URL);
	        			Utility.debug("MAILINGLIST_BGIMAGE_URL : "+appManager.MAILINGLIST_BGIMAGE_URL);
	        			}
	    		 	} catch (Exception e1) {}	

	        	break;
	        case AppManager.ERROR:
	        	try
	        	{
 					dismissDialog(appManager.PROGRESS);
 				}
	        	catch (Exception e) 
	        	{
	        		e.printStackTrace();
	        	}
				Toast.makeText(getApplicationContext(), "Please fill correct info !", Toast.LENGTH_LONG).show();
				break;
	        }
	    }
	};
	
	protected void getMailSubResponse(String sumMailStr) 
	{
		showDialog(appManager.PROGRESS);
		InputStream in = null;
		try
		{
			in = HttpConnection.connect(sumMailStr);
			res = getMailRes(in);
			handler.sendEmptyMessage(appManager.DISPLAY);
		}
		catch (Exception e) 
		{
			handler.sendEmptyMessage(appManager.ERROR);
		}
	}
	
	private class MailSubAsync extends AsyncTask<String, Integer, InputStream>
	{
		@Override
		protected void onPostExecute(InputStream result) 
		{
			if(res != null && res.equalsIgnoreCase("Thank you for your information"))
			{
				submitMail();
			}
			else
			{
				try
				{
					dismissDialog(appManager.PROGRESS);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					isSuccessFlag=false;
				}
				Toast.makeText(getApplicationContext(), "Please filled correct info details.!", Toast.LENGTH_LONG).show();
			}
			if(isSuccessFlag)
			{
				SharedPreferences myPref1 = getSharedPreferences("SLUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
				SharedPreferences.Editor editor = myPref1.edit();
				editor.putBoolean("1009", false);
				editor.commit();
			}
			else
			{
				Utility.debug("1009 : Not downloaded properly :"+this.getClass().getName());
			}
			super.onPostExecute(result);
		}

		@Override
		protected InputStream doInBackground(String... param) 
		{
			try
			{
				InputStream in = HttpConnection.connect(param[0]);
				res = getMailRes(in);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				isSuccessFlag=false;
			}
			return null;
		}
	}
	
	public static boolean isEmailValid(String email) {
	    boolean isValid = false;
	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
	}

	
	public void submitMail() 
	{
		try 
		{
    		if(status_flag != null)
    		{
    			if(status_flag.equalsIgnoreCase("1"))
    			{
    				SharedPreferences settings = getSharedPreferences("MailPref"+appManager.getAPP_ID(), MODE_PRIVATE);
		        	SharedPreferences.Editor editor = settings.edit();
       		 		editor.putBoolean("MailContact",true);
       		 		editor.commit();
    			}
    			else if(status_flag.equalsIgnoreCase("0"))
    			{
    				SharedPreferences settings = getSharedPreferences("MailPref"+appManager.getAPP_ID(), MODE_PRIVATE);
		        	SharedPreferences.Editor editor = settings.edit();
       		 		editor.putBoolean("MailContact",false);
       		 		editor.commit();
    			}
    		}
    		if(res != null && res.equalsIgnoreCase("Thank you for your information"))
    		{
    			SharedPreferences settings = getSharedPreferences("MailPref"+appManager.getAPP_ID(), MODE_PRIVATE);
	        	SharedPreferences.Editor editor = settings.edit();
   		 		editor.putString("MLName", et.getText().toString());
   		 		editor.putString("MLLName", et1.getText().toString());
   		 		editor.putString("MLEmail", et2.getText().toString());
   		 		editor.putString("MLBD", et3.getText().toString());
   		 		editor.putString("MLGEN", et4.getText().toString());
   		 		editor.putString("MLC", et5.getText().toString());
   		 		editor.putString("MLPC", et6.getText().toString());
   		 		editor.commit();
    		}
    		Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
    		dismissDialog(appManager.PROGRESS);
    		SnapLionMyAppActivity.KillFlag = false;
		 	finish();
		} 
		catch (Exception e1) 
		{
			e1.printStackTrace();
			isSuccessFlag=false;
		}	
	}
	
	private String getMailRes(InputStream in) {
		final int answer  = 1;
		final int status = 2;
		
		int tagName = 0;

        String values = null;

        try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, HTTP.UTF_8);
	
        	int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("answer")) {
						tagName = answer;
					}else if (parser.getName().equals("status")) {
						tagName = status;
					}
				}
				if (eventType == XmlPullParser.TEXT) {
					switch (tagName) {
					case answer:
						values = parser.getText();
						break;
					case status:
						status_flag = parser.getText();
						break;
					default:
						break;
					}
					tagName = 0;
				}
				 
				if (eventType == XmlPullParser.END_TAG) {}
	                eventType = parser.next();
	            }
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return values;
	}
	

	private class MyDownloadTask extends AsyncTask<String, Integer, InputStream> {
		@Override
		protected void onPostExecute(InputStream result) {
			 BitmapDrawable background = new BitmapDrawable(Utility.FetchImage("MailBG"+appManager.getAPP_ID()));
	         mailBgLayout.setBackgroundDrawable(background);
	         background = null;
			super.onPostExecute(result);
		}

		@Override
		protected InputStream doInBackground(String... params) {
			InputStream in = null;
			try{
				in = HttpConnection.connect(params[0]);
           		String resMailBg = getMailBg(in);
           		if(resMailBg != null){
					URL url = null;
					try {
						url = new URL(resMailBg);
					} catch (MalformedURLException e1) {e1.printStackTrace();}
			        
			        try {
			            URLConnection connection = url.openConnection();
			            connection.connect();
			            InputStream is = connection.getInputStream();
			            String path = "MailBG"+appManager.getAPP_ID();
			            Utility.SaveImageSDCard(is, path);
			            is = null;
			            path = null;
			            in = null;
			        } catch (Exception e) {e.printStackTrace();}
			        }
			}catch (Exception e) {e.printStackTrace();}
			return null;
		}
	}
	
	public String getMailBg(InputStream in) {
		final int image  = 1;

		int tagName = 0;

        String values = null;

        try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, HTTP.UTF_8);
	
        	int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("image")) {
						tagName = image;
					} 
				}
				if (eventType == XmlPullParser.TEXT) {
					switch (tagName) {
					case image:
						values = parser.getText();
						break;
					default:
						break;
					}
					tagName = 0;
				}
				 
				if (eventType == XmlPullParser.END_TAG) {}
	                eventType = parser.next();
	            }
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return values;
	}

	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
	          "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
	          "\\@" +
	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
	          "(" +
	          "\\." +
	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
	          ")+"
	      );
	
	/**
	 * imran@author 
	 * we can create on dialog method that will take filed, Title and textArray. to write one code.
	 */
	@Override
	 protected Dialog onCreateDialog(int id) {
	     switch (id) {
	         case AppManager.PROGRESS:
	             final ProgressDialog progDialog = new ProgressDialog(this);
	             progDialog.setMessage("Loading, Please wait...");
	             progDialog.setCanceledOnTouchOutside(false);
	             progDialog.setCancelable(true);
	             return progDialog;
	         case CALENDER_VIEW:
	             final AlertDialog listDailog = new AlertDialog.Builder(SLMailActivity.this)
	        	    .setTitle("Birth Year")
	        	    .setItems(yearArray, new DialogInterface.OnClickListener() {
	        	        public void onClick(DialogInterface dialog, int which) {
	        	        		et3.setText(yearArray[which]);
	        	        	}
	        	    	}).create();
	        	 	return listDailog;
	         case GENDER_VIEW:
	        	 final AlertDialog listDailog1 = new AlertDialog.Builder(SLMailActivity.this)
	        	    .setTitle("Sex")
	        	    .setItems(sexArray, new DialogInterface.OnClickListener() {
	        	        public void onClick(DialogInterface dialog, int which) {
	        	        		et4.setText(sexArray[which]);
	        	        	}
	        	    	}).create();
	        	 	return listDailog1; 
	         case COUNTRY_VIEW:
	        	 final AlertDialog listDailog2 = new AlertDialog.Builder(SLMailActivity.this)
	        	    .setTitle("Country")
	        	    .setItems(COUNTRIES, new DialogInterface.OnClickListener() {
	        	        public void onClick(DialogInterface dialog, int which) {
	        	        		et5.setText(COUNTRIES[which]);
	        	        	}
	        	    	}).create();
	        	 	return listDailog2;
	     }
	     return null;
	 }

	
	@Override
	protected void onPause() {
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
	    boolean isScreenOn = powerManager.isScreenOn();
	    if (!isScreenOn) {
	    	SnapLionMyAppActivity.KillFlag = false;
	    }
		if(SnapLionMyAppActivity.KillFlag)
		{
			try{
				Utility.killMyApp(getApplicationContext(),SLMailActivity.this);
			}catch (Exception e) {e.printStackTrace();}
		}
		super.onPause();
	}
	
	@Override
	public void onBackPressed() {
		SnapLionMyAppActivity.KillFlag = false;
		super.onBackPressed();
	}
	@Override
	protected void onStart() 
	{
		super.onStart();
		mGaTracker.sendView("Mailinglist_Screen");
	}
}
