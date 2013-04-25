package com.snaplion.kingsxi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.snaplion.beans.BG;
import com.snaplion.util.AppManager;
import com.snaplion.util.HttpConnection;
import com.snaplion.util.Utility;

public class BGImageDownloaderService extends IntentService
{
	AppManager appManager;
	private ArrayList<BG> bgDetails = null;
	
	private final static String DATA_TEMP2_BG="/sdcard/.SnapLion/Temp2/";
	
	public BGImageDownloaderService() 
	{
		super("BGImageDownloaderService");
		appManager = AppManager.getInstance();
	}
	
	@Override
	protected void onHandleIntent(Intent intent) 
	{
		System.out.println("downloading BGImageDownloaderService started");
		getBgImages(0);
		System.out.println("downloading BGImageDownloaderService end");
	}
		
	public void getBgImages(int i2) 
	{
		boolean bgFlag = true;
		File data_directory = new File(DATA_TEMP2_BG);
		if (!data_directory.exists())
		{
			if (data_directory.mkdir());
		}
		
		InputStream in = HttpConnection.connect(appManager.SECTIONS_BGIMAGES_URL);
		Utility.debug("SECTIONS_BGIMAGES_URL : "+appManager.SECTIONS_BGIMAGES_URL);
		bgDetails = new ArrayList<BG>();
		if(in != null)
		{
			bgDetails = getBGDetails(in);
			for(int i=0;i<bgDetails.size();i++)
			{
				URLConnection connection = null;
				String str = bgDetails.get(i).getTabs().trim();
				
				if(str.length() >0)
				{
					Utility.debug("service original str:"+str);
					if(str.startsWith(","))
					{
						str=str.substring(1,str.length());
					}
					if(str.endsWith(","))
					{
						str=str.substring(0,str.length()-1);
					}
					str=str.replaceAll(",,",",");
					Utility.debug("service converted str:"+str);
					
					String[] strArray = str.split(",");
					String imgStr = null;
					for(int j=0;j<strArray.length;j++)
					{
						if(j == 0)
						{
							URL url = null;
							try 
							{
								url = new URL(Utility.replace(bgDetails.get(i).getBgimage()," ","%20"));
							} 
							catch (Exception e1) 
							{
								e1.printStackTrace();
							}	
							try 
							{
								connection = url.openConnection();
								connection.connect();
								InputStream is = connection.getInputStream();
								String path = getPath(strArray[j]);
								imgStr = path;
								if(is == null)
								{
									bgFlag = false;
								}
								if(path != null)
								{
									Utility.SaveImageGPathSDCard(DATA_TEMP2_BG,is, path);
								}
								Utility.debug("downloaded bg img path:"+path);
								path = null;
							}
							catch (Exception e) 
							{
								e.printStackTrace();bgFlag = false;
							}	
						}
						else
						{
							String path = getPath(strArray[j]);
							Bitmap bitmap = tempFetchImage(imgStr);
							if (bitmap != null) 
							{
								java.io.OutputStream outStream = null;
								File file = new File(DATA_TEMP2_BG, path+".PNG");
								
								
								try 
								{
									outStream = new FileOutputStream(file);
									bitmap.compress(Bitmap.CompressFormat.PNG, 0, outStream);
									outStream.flush();
									outStream.close();
								} 
								catch (FileNotFoundException e) 
								{
									e.printStackTrace();bgFlag = false;
								} 
								catch (IOException e) 
								{
									e.printStackTrace();bgFlag = false;
								}
								Utility.debug("downloaded bg img path:"+path);
							}
							path = null;
							bitmap = null;
						}
					}
				}
			}
		}
		else
		{
			bgFlag = false;
		}
		if(bgFlag)
		{
			File dir1 = new File(appManager.DATA_DIR_BG);
			if (dir1.exists()) 
			{
				String[] children = dir1.list();
				for (int j = 0; j < children.length; j++) 
				{
					if(children[j].endsWith(appManager.getAPP_ID()+".PNG"))
					{
						new File(dir1, children[j]).delete();
					}
				}
			}
			copyTempFile(DATA_TEMP2_BG,appManager.DATA_DIR_BG);
			SharedPreferences myPref = getSharedPreferences("SLUpdate"+appManager.getAPP_ID(), MODE_PRIVATE);
			String str = myPref.getString("MTime"+"1015"+appManager.getAPP_ID(), "");
			SharedPreferences.Editor editor = myPref.edit();
			editor.putBoolean("1015", false);
			editor.putString("1015"+appManager.getAPP_ID(),str);
			editor.commit();
			
			SharedPreferences.Editor editor1 = getSharedPreferences("SLBGImage"+appManager.getAPP_ID(), MODE_PRIVATE).edit();
			editor1.putBoolean("DownloadBGImage", false);
			editor1.commit();
		}
		else
		{
			Utility.debug("1015 : Not downloaded properly :"+this.getClass().getName());
		}
	}
	
	private ArrayList<BG> getBGDetails(InputStream in) 
	{
		final int bgrecord  = 1;
	  	final int bghomeid = 2;
        final int tabs = 3;
        final int bgimage = 4;

        int tagName = 0;

        BG value = null;
        ArrayList<BG> values = new ArrayList<BG>();

        try 
        {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, HTTP.UTF_8);
	
        	int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) 
			{
				if (eventType == XmlPullParser.START_TAG) 
				{
					if (parser.getName().equals("bgrecord")) 
					{
						tagName = bgrecord;
						value = new BG();
					} 
					else if (parser.getName().equals("bghomeid")) 
					{
						tagName = bghomeid;
					}
					else if (parser.getName().equals("tabs")) 
					{
						tagName = tabs;
					}
					else if (parser.getName().equals("bgimage")) 
					{
						tagName = bgimage;
					}
				}
				if (eventType == XmlPullParser.TEXT) 
				{
					switch (tagName) 
					{
						case bgrecord:
							break;
						case bghomeid:
							value.setBghomeid(parser.getText());
							break;
						case tabs:
							value.setTabs(parser.getText());
							break;
						case bgimage:
							value.setBgimage(parser.getText());
							break;
						default:
							break;
					}
					tagName = 0;
				}
				 
				if (eventType == XmlPullParser.END_TAG) 
				{
	               if (parser.getName().equals("bgrecord")) 
	               {
	                   values.add(value);
	                   value = null;
	               }
	            }
	            eventType = parser.next();
	        }
		} 
        catch (XmlPullParserException e) 
        {
        	e.printStackTrace();
		} 
        catch (IOException e) 
        {
        	e.printStackTrace();
        }
		return values;
	}
	
	private String getPath(String string) 
	{
		String str = null;
		int id = 0;
		try
		{
			id = Integer.parseInt(string);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		switch(id)
		{
			case 1001:
			  break;
			case 1002:
				str = "BioMainBg"+appManager.getAPP_ID();
				 break;
			case 1003:
				str = "AlbumBG"+appManager.getAPP_ID();
				 break;
			case 1004:
				str = "MusicAlbumBG"+appManager.getAPP_ID();
				 break;
			case 1005:
				str = "AllBG"+appManager.getAPP_ID();
				 break;
			case 1006:
				str = "VideoBG"+appManager.getAPP_ID();
				 break;
			case 1007:
				str = "NewsBg"+appManager.getAPP_ID();
				 break;
			case 1008:
				str = "EventsBg"+appManager.getAPP_ID();
				 break;
			case 1009:
				str = "MailBG"+appManager.getAPP_ID();
				 break;
			case 1010:
				str = "FanwallMainBg"+appManager.getAPP_ID();
				 break;
			case 1011:
				str = "ContactBG"+appManager.getAPP_ID();
				 break;
			case 1012:
				str = "BgCrImage"+appManager.getAPP_ID();
				 break;
			case 1013:
				str = "TicketBg"+appManager.getAPP_ID();
				 break;
			case 1014:
				str = "SL"+appManager.getAPP_ID();
				 break;
			case 1015:
				 break;
			case 1017:
				str = "Multibio"+appManager.getAPP_ID();
				 break;	
			case 1018:
				str = "MenusBG"+appManager.getAPP_ID();
				break;
			case 1019:
				str = "PromoBg"+appManager.getAPP_ID();	
				 break;		 
			case 0:
				break;
	   }
		return str;
	}
	
	public static Bitmap tempFetchImage(String path)
	{
		String path1 = DATA_TEMP2_BG+path+".PNG";
  		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bitmapImage = null;
		try
		{
      		options.inSampleSize = 1;
      		bitmapImage = BitmapFactory.decodeFile(path1, options);
      	}
		catch (OutOfMemoryError e) 
		{
			e.printStackTrace();System.gc();
      	 	try 
      	 	{
				options.inSampleSize = 2;
				bitmapImage = BitmapFactory.decodeFile(path1, options);
			} 
      	 	catch (OutOfMemoryError e1) 
      	 	{
      	 		e1.printStackTrace();
				try 
				{
					options.inSampleSize = 3;
					bitmapImage = BitmapFactory.decodeFile(path1, options);
				} 
				catch (OutOfMemoryError e2) 
				{
					e2.printStackTrace();
					try 
					{
						options.inSampleSize = 4;
						bitmapImage = BitmapFactory.decodeFile(path1, options);
					} 
					catch (OutOfMemoryError e3) 
					{
						e3.printStackTrace();
					}
				}
			}
      	}
      	System.gc();
      	return bitmapImage;
	}
	private void copyTempFile(String dataTempBg, String dataDirectory) 
	{
		File dir2 = new File(dataTempBg);
		File dst = new File(dataDirectory);
		try 
		{
			Utility.copyDirectory(dir2, dst);
			Utility.delete(dir2);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}


