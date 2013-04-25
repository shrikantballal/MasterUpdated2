package com.snaplion.util;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpConnection {
		private static final String TAG = "HTTPConnection";

		public static InputStream connect(String url)
	    {
			InputStream in = null;
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(url);
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
						HttpEntity httpEntity = response.getEntity();
						in = httpEntity.getContent(); 
				}
				
			}catch (Exception e) {e.printStackTrace();}
			
			return in;
	     }
}
