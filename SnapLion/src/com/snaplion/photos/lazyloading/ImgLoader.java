package com.snaplion.photos.lazyloading;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

public class ImgLoader 
{
	ExecutorService executorService; 
	private static ImgLoader imgLoader;
	private ImgLoader()
	{
		executorService=Executors.newFixedThreadPool(5);
	}
	public static ImgLoader getInstance()
	{
		if(imgLoader==null)
			imgLoader=new ImgLoader();
		return imgLoader;
	}
	public void load(ImageView imageView, ProgressBar progress, String url)
	{
		//final String fileKey=getKeyFromStr(url);
		final String fileKey=String.valueOf(url.hashCode());
		String path = AppManager.getInstance().DATA_DIRECTORY+ fileKey +".PNG";
		File file=new File(path);
		if(file.exists())
		{
			BitmapDisplayer bd=new BitmapDisplayer(Utility.FetchImage(fileKey), imageView, progress);
            Activity a=(Activity)imageView.getContext();
            a.runOnUiThread(bd);
		}
		else
		{
			executorService.submit(new DownloadTask(imageView, progress, url, fileKey));
		}
	}
	class DownloadTask implements Runnable
	{
		ImageView imageView;
		ProgressBar progress;
		String url;
		String key;
		
		public DownloadTask(ImageView imageView, ProgressBar progress, String url, String key)
		{
			this.imageView=imageView;
			this.progress=progress;
			this.url=url;
			this.key=key;
		}
		@Override
		public void run() 
		{
			Bitmap resultBitmap = downloadImage(url , key);
			
			BitmapDisplayer bd=new BitmapDisplayer(resultBitmap, imageView, progress);
            Activity a=(Activity)imageView.getContext();
            a.runOnUiThread(bd);
		}
	}
	class BitmapDisplayer implements Runnable
	{
		ImageView imageView;
		ProgressBar progress;
		Bitmap bitmap;
		public BitmapDisplayer(Bitmap bitmap, ImageView imageView, ProgressBar progress)
		{
			this.imageView=imageView;
			this.progress=progress;
			this.bitmap=bitmap;
		}
		@Override
		public void run() 
		{
			progress.setVisibility(ProgressBar.INVISIBLE);
			imageView.setImageBitmap(bitmap);
		}
	}
	private Bitmap downloadImage(String imageUrl, String imageKey)
	{
		Bitmap bitmap = null;
    	URL url=null;
		 try 
		 {
			 imageUrl = Utility.replace(imageUrl, " ", "%20");
			 url = new URL(imageUrl);
			 URLConnection connection = url.openConnection();
			 connection.connect();
			 InputStream is = connection.getInputStream();
			 if(is != null)
			 {
				 BitmapFactory.Options options = new BitmapFactory.Options();
				 options.inSampleSize = 1;
				 try
				 {
					 bitmap = BitmapFactory.decodeStream(is,null, options);
				 }
				 catch (OutOfMemoryError e) 
				 {
					e.printStackTrace();
					bitmap=null;
			 		try 
			 		{
				   		options.inSampleSize = 2;
				   		bitmap = BitmapFactory.decodeStream(is,null, options);
			 		} 
			 		catch (OutOfMemoryError e1) 
			 		{
			 			e1.printStackTrace();
			 			bitmap=null;
				 		try 
				 		{
				 			options.inSampleSize = 3;
				 			bitmap = BitmapFactory.decodeStream(is,null, options);
				 		} 
				 		catch (OutOfMemoryError e2) 
				 		{
				 			e2.printStackTrace();
				 			bitmap=null;
					 		try 
					 		{
					 			options.inSampleSize = 4;
					 			bitmap = BitmapFactory.decodeStream(is,null, options);
					 		} 
					 		catch (OutOfMemoryError e3) 
					 		{
					 			e3.printStackTrace();
					 			bitmap=null;
					 		}
					 	}
			 		}
				 }
			 }
			 if (bitmap != null) 
			 {
			 	OutputStream outStream = null;
				File file = new File(AppManager.getInstance().DATA_DIRECTORY , imageKey+".PNG");
			    outStream = new FileOutputStream(file);
			   bitmap.compress(Bitmap.CompressFormat.PNG, 0, outStream);
			   outStream.flush();
			   outStream.close();
			   return bitmap;
			}
		 }
		 catch (Exception e) 
		 {}
		 return null;
	}
	
	
	public static String getKeyFromStr(String str) 
	{
		try
		{
			byte[] seed=str.getBytes();
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(seed);
		    kgen.init(128, sr); // 192 and 256 bits may not be available
		    SecretKey skey = kgen.generateKey();
		    byte[] raw = skey.getEncoded();
		    return new String(raw);
		}
		catch (Exception e) 
		{
			// TODO: handle exception
		}
		return null;
	}
}
