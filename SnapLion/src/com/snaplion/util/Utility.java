package com.snaplion.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.widget.Toast;

public class Utility {
	public static String getKeyFromStr(String str) {
		try {
			byte[] seed = str.getBytes();
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(seed);
			kgen.init(128, sr); // 192 and 256 bits may not be available
			SecretKey skey = kgen.generateKey();
			byte[] raw = skey.getEncoded();
			return new String(raw);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public static boolean isOnline(Context ctx) {
		boolean isConnected = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) ctx
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			isConnected = cm.getActiveNetworkInfo().isConnectedOrConnecting();
		} catch (Exception ex) {
			isConnected = false;
		}
		return isConnected;
	}

	public static String replace(String _text, String _searchStr,
			String _replacementStr) {
		// String buffer to store str
		StringBuffer sb = new StringBuffer();

		// Search for search
		int searchStringPos = _text.indexOf(_searchStr);
		int startPos = 0;
		int searchStringLength = _searchStr.length();

		// Iterate to add string
		while (searchStringPos != -1) {
			sb.append(_text.substring(startPos, searchStringPos)).append(
					_replacementStr);
			startPos = searchStringPos + searchStringLength;
			searchStringPos = _text.indexOf(_searchStr, startPos);
		}

		// Create string
		sb.append(_text.substring(startPos, _text.length()));

		return sb.toString();

	}

	public static void FileCreate() {
		File data_directory = new File(AppManager.getInstance().DATA_DIRECTORY);
		if (!data_directory.exists())
			if (data_directory.mkdir())
				;
	}

	public static void SaveImageSDCard(InputStream is, String path) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		try {
			bitmap = BitmapFactory.decodeStream(is, null, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			bitmap = null;
			try {
				BitmapFactory.Options options1 = new BitmapFactory.Options();
				options1.inSampleSize = 2;
				bitmap = BitmapFactory.decodeStream(is, null, options1);
			} catch (OutOfMemoryError e1) {
				e1.printStackTrace();
				bitmap = null;
				try {
					BitmapFactory.Options options2 = new BitmapFactory.Options();
					options2.inSampleSize = 3;
					bitmap = BitmapFactory.decodeStream(is, null, options2);
				} catch (OutOfMemoryError e2) {
					e2.printStackTrace();
					bitmap = null;
					try {
						BitmapFactory.Options options3 = new BitmapFactory.Options();
						options3.inSampleSize = 4;
						bitmap = BitmapFactory.decodeStream(is, null, options3);
					} catch (OutOfMemoryError e3) {
						e3.printStackTrace();
						bitmap = null;
					}
				}
			}
		}

		if (bitmap != null) {
			String key = null;
			try {
				key = path;
			} catch (Exception e) {
				e.printStackTrace();
			}

			java.io.OutputStream outStream = null;
			File file = new File(AppManager.getInstance().DATA_DIRECTORY, key
					+ ".PNG");
			try {
				outStream = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 0, outStream);
				outStream.flush();
				outStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.gc();
		bitmap = null;
	}

	public static void SaveImageGPathSDCard(String sdPath, InputStream is,
			String path) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		try {
			bitmap = BitmapFactory.decodeStream(is, null, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			bitmap = null;
			try {
				BitmapFactory.Options options1 = new BitmapFactory.Options();
				options1.inSampleSize = 2;
				bitmap = BitmapFactory.decodeStream(is, null, options1);
			} catch (OutOfMemoryError e1) {
				e1.printStackTrace();
				bitmap = null;
				try {
					BitmapFactory.Options options2 = new BitmapFactory.Options();
					options2.inSampleSize = 3;
					bitmap = BitmapFactory.decodeStream(is, null, options2);
				} catch (OutOfMemoryError e2) {
					e2.printStackTrace();
					bitmap = null;
					try {
						BitmapFactory.Options options3 = new BitmapFactory.Options();
						options3.inSampleSize = 4;
						bitmap = BitmapFactory.decodeStream(is, null, options3);
					} catch (OutOfMemoryError e3) {
						e3.printStackTrace();
						bitmap = null;
					}
				}
			}
		}

		if (bitmap != null) {
			String key = null;
			try {
				key = path;
			} catch (Exception e) {
				e.printStackTrace();
			}
			java.io.OutputStream outStream = null;

			File file = new File(sdPath, key + ".PNG");
			try {
				outStream = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 0, outStream);
				outStream.flush();
				outStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.gc();
		bitmap = null;
	}

	public static Bitmap FetchJPGImage(String path) {
		String path1 = AppManager.getInstance().DATA_DIRECTORY + path + ".JPG";
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bitmapImage = null;
		try {
			options.inSampleSize = 1;
			bitmapImage = BitmapFactory.decodeFile(path1, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
			try {
				options.inSampleSize = 2;
				bitmapImage = BitmapFactory.decodeFile(path1, options);
			} catch (OutOfMemoryError e1) {
				e1.printStackTrace();
				try {
					options.inSampleSize = 3;
					bitmapImage = BitmapFactory.decodeFile(path1, options);
				} catch (OutOfMemoryError e2) {
					e2.printStackTrace();
					try {
						options.inSampleSize = 4;
						bitmapImage = BitmapFactory.decodeFile(path1, options);
					} catch (OutOfMemoryError e3) {
						e3.printStackTrace();
					}
				}
			}
		}
		System.gc();
		return bitmapImage;
	}

	public static void removeImage(String path) {
		String path1 = AppManager.getInstance().DATA_DIRECTORY + path + ".PNG";
		File file = new File(path1);
		if (file.exists())
			file.delete();
	}

	public static Bitmap FetchImage(String path) {
		String path1 = AppManager.getInstance().DATA_DIRECTORY + path + ".PNG";
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bitmapImage = null;
		if (new File(path1).exists()) {
			try {
				options.inSampleSize = 1;
				bitmapImage = BitmapFactory.decodeFile(path1, options);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				System.gc();
				try {
					options.inSampleSize = 2;
					bitmapImage = BitmapFactory.decodeFile(path1, options);
				} catch (OutOfMemoryError e1) {
					e1.printStackTrace();
					try {
						options.inSampleSize = 3;
						bitmapImage = BitmapFactory.decodeFile(path1, options);
					} catch (OutOfMemoryError e2) {
						e2.printStackTrace();
						try {
							options.inSampleSize = 4;
							bitmapImage = BitmapFactory.decodeFile(path1,
									options);
						} catch (OutOfMemoryError e3) {
							e3.printStackTrace();
						}
					}
				}
			}
		}
		System.gc();
		return bitmapImage;
	}

	public static String getString(InputStream in) {
		StringBuilder sb = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void killMyApp(Context context, Activity activity) {
		// try
		// {
		// com.snaplion.music.SLMusicPlayerActivity.closePlayer(context);
		// }
		// catch (Exception e)
		// {
		// e.printStackTrace();
		// }
		// android.os.Process.killProcess(android.os.Process.myPid());
	}

	/**
	 * @imran:: to show toast on center of phone.
	 * @param message
	 */
	public static void showToastMessage(Context context, String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static void closeAllBelowActivities(Activity current) {
		boolean flag = true;
		Activity below = current.getParent();
		if (below == null)
			return;
		System.out.println("Below Parent: " + below.getClass());
		while (flag) {
			Activity temp = below;
			try {
				below = temp.getParent();
				temp.finish();
			} catch (Exception e) {
				flag = false;
			}
		}
	}

	public static void getResizedImage(InputStream is, String path, int yy,
			int width) {
		Bitmap bitmap = null;
		Bitmap bitmap1 = null;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		try {
			bitmap = BitmapFactory.decodeStream(is, null, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			bitmap = null;
			try {
				BitmapFactory.Options options1 = new BitmapFactory.Options();
				options1.inSampleSize = 2;
				bitmap = BitmapFactory.decodeStream(is, null, options1);
			} catch (OutOfMemoryError e1) {
				e1.printStackTrace();
				bitmap = null;
				try {
					BitmapFactory.Options options2 = new BitmapFactory.Options();
					options2.inSampleSize = 3;
					bitmap = BitmapFactory.decodeStream(is, null, options2);
				} catch (OutOfMemoryError e2) {
					e2.printStackTrace();
					bitmap = null;
					try {
						BitmapFactory.Options options3 = new BitmapFactory.Options();
						options3.inSampleSize = 4;
						bitmap = BitmapFactory.decodeStream(is, null, options3);
					} catch (OutOfMemoryError e3) {
						e3.printStackTrace();
						bitmap = null;
					}
				}
			}
		}

		if (bitmap != null) {
			String key = null;
			try {
				key = path;
			} catch (Exception e) {
				e.printStackTrace();
			}

			java.io.OutputStream outStream = null;

			bitmap1 = Bitmap.createScaledBitmap(bitmap, width, yy, true);
			File file = new File(AppManager.getInstance().DATA_DIRECTORY, key
					+ ".PNG");
			try {
				outStream = new FileOutputStream(file);
				bitmap1.compress(Bitmap.CompressFormat.PNG, 0, outStream);
				outStream.flush();
				outStream.close();
				if (file.exists()) {
					Utility.debug("file path:" + file.getPath());
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		bitmap = null;
		bitmap1 = null;
		System.gc();
	}

	// ////////////////////////////////////////////////////////////////////////
	public static Bitmap getScaledImageByWinWidth(Bitmap bitmap,
			float winWidth, float winHeight) {
		try {
			float imgWidth = bitmap.getWidth();
			float imgHeight = bitmap.getHeight();
			float ratioInWidth = winWidth / imgWidth;

			float newImgWidth = imgWidth * ratioInWidth;
			float newImgHeight = imgHeight * ratioInWidth;
			return Bitmap.createScaledBitmap(bitmap, Math.round(newImgWidth),
					Math.round(newImgHeight), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap getScaledImageByWinWidth(Resources res,
			int resourceId, float winWidth, float winHeight) {
		try {
			Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId);
			float imgWidth = bitmap.getWidth();
			float imgHeight = bitmap.getHeight();
			winWidth = winWidth / 3 + 50;
			float ratioInWidth = winWidth / imgWidth;

			float newImgWidth = imgWidth * ratioInWidth;
			float newImgHeight = imgHeight * ratioInWidth;
			return Bitmap.createScaledBitmap(bitmap, Math.round(newImgWidth),
					Math.round(newImgHeight), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap getBitmapZoomToFitScaled(Activity activity,
			Bitmap bitmap, int boxWidth, int boxHeight) {
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();

		Utility.debug("original width = " + Integer.toString(bitmapWidth));
		Utility.debug("original height = " + Integer.toString(bitmapHeight));
		Utility.debug("box width = " + Integer.toString(boxWidth));
		Utility.debug("box height = " + Integer.toString(boxHeight));

		float xScale = ((float) boxWidth) / bitmapWidth;
		float yScale = ((float) boxHeight) / bitmapHeight;
		float scale = (xScale >= yScale) ? xScale : yScale;
		Utility.debug("xScale = " + Float.toString(xScale));
		Utility.debug("yScale = " + Float.toString(yScale));
		Utility.debug("scale = " + Float.toString(scale));

		Matrix matrix = new Matrix();
		matrix.preScale(scale, scale);
		// Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth,
		// bitmapHeight, matrix, true);
		// Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, boxWidth,
		// boxHeight, matrix, true);

		float ratioInWidth = boxWidth / bitmapWidth;

		float newImgWidth = bitmapWidth * ratioInWidth;
		float newImgHeight = bitmapHeight * ratioInWidth;
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
				Math.round(newImgWidth), Math.round(newImgHeight), true);
		// Bitmap cropedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, 100,
		// boxHeight, null, true);
		return scaledBitmap;
	}

	public static Bitmap getScaledCroppedImageByDisplay(Activity activity,
			Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		Display display = activity.getWindowManager().getDefaultDisplay();

		float imgWidth = bitmap.getWidth();
		float imgHeight = bitmap.getHeight();
		float ratioInWidth = display.getWidth() / imgWidth;
		float ratioInHeight = display.getHeight() / imgHeight;
		float scaleRatio = (ratioInWidth >= ratioInHeight ? ratioInWidth
				: ratioInHeight);

		float newImgWidth = imgWidth * scaleRatio;
		float newImgHeight = imgHeight * scaleRatio;
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
				Math.round(newImgWidth), Math.round(newImgHeight), true);
		
		bitmap.recycle();
		bitmap = null;

		float density = activity.getApplicationContext().getResources()
				.getDisplayMetrics().density;
		int y = (scaledBitmap.getHeight() - display.getHeight()) / 2;
		Bitmap cropedBitmap = Bitmap.createBitmap(scaledBitmap, 0, y,
				display.getWidth(), display.getHeight(), null, true);
		
		scaledBitmap.recycle();
		scaledBitmap = null;

		return cropedBitmap;
	}

	public static Bitmap getScaledImageByWinWidth(URL url, float winWidth,
			float winHeight) {
		InputStream is = null;
		Bitmap bitmap = null;
		URLConnection connection = null;
		try {
			connection = url.openConnection();
			connection.connect();
			is = connection.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			final Options options = new Options();
			options.inDither = true;
			options.inInputShareable = true;
			options.inPurgeable = true;
			bitmap = BitmapFactory.decodeStream(bis, null, options);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
		}
		
		is = null;

		// BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inSampleSize = 1;
		// try
		// {
		// bitmap = BitmapFactory.decodeStream(is,null, options);
		// }
		// catch (OutOfMemoryError e)
		// {
		// e.printStackTrace();bitmap=null;
		// try
		// {
		// BitmapFactory.Options options1 = new BitmapFactory.Options();
		// options1.inSampleSize = 2;
		// bitmap = BitmapFactory.decodeStream(is,null, options1);
		// }
		// catch (OutOfMemoryError e1)
		// {
		// e1.printStackTrace();
		// bitmap=null;
		// try
		// {
		// BitmapFactory.Options options2 = new BitmapFactory.Options();
		// options2.inSampleSize = 3;
		// bitmap = BitmapFactory.decodeStream(is,null, options2);
		// }
		// catch (OutOfMemoryError e2) {e2.printStackTrace();bitmap=null;
		// try
		// {
		// BitmapFactory.Options options3 = new BitmapFactory.Options();
		// options3.inSampleSize = 4;
		// bitmap = BitmapFactory.decodeStream(is,null, options3);
		// }
		// catch (OutOfMemoryError e3)
		// {
		// e3.printStackTrace();bitmap=null;
		// }
		// }
		// }
		// }

		float imgWidth = bitmap.getWidth();
		float imgHeight = bitmap.getHeight();
		
		float ratioInWidth = winWidth / imgWidth;

		float newImgWidth = imgWidth * ratioInWidth;
		float newImgHeight = imgHeight * ratioInWidth;
		
		Bitmap bitmap2 = null;
		try {
		bitmap2 = Bitmap.createScaledBitmap(bitmap, Math.round(newImgWidth),
				Math.round(newImgHeight), true);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		bitmap.recycle();
		bitmap = null;
		return bitmap2;
	}

	// ////////////////////////////////////////////////////////////
	public static Bitmap getImageBitmap(URL url) {
		Bitmap bitmap = null;
		URLConnection connection = null;
		try {
			connection = url.openConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			connection.connect();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		InputStream is = null;
		try {
			is = connection.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		BufferedInputStream bis = new BufferedInputStream(is);

		try {
			bitmap = BitmapFactory.decodeStream(bis);
		} catch (OutOfMemoryError e) {
			bitmap = null;
		}
		bis = null;
		is = null;
		Runtime.getRuntime().gc();

		return bitmap;
	}

	public static void copyDirectory(File sourceDir, File destDir)
			throws IOException {
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		File[] children = sourceDir.listFiles();
		if (children == null)
			return;
		for (File sourceChild : children) {
			String name = sourceChild.getName();
			File destChild = new File(destDir, name);
			if (sourceChild.isDirectory()) {
				copyDirectory(sourceChild, destChild);
			} else {
				copyFile(sourceChild, destChild);
			}
		}
	}

	public static void copyFile(File source, File dest) throws IOException {
		if (!dest.exists()) {
			dest.createNewFile();
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} finally {
			in.close();
			out.close();
		}
	}

	public static boolean delete(File resource) throws IOException {
		if (resource.isDirectory()) {
			File[] childFiles = resource.listFiles();
			for (File child : childFiles) {
				delete(child);
			}
		}
		return resource.delete();
	}

	public static void debug(String msg) {
		Log.d("MTK", msg);
	}
}