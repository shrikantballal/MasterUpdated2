package com.snaplion.photos;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.Tracker;
import com.snaplion.beans.AlbumSub;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.photos.util.SwipeInterceptingGallery;
import com.snaplion.photos.util.TouchImageView;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

public class SLPhotoGalleryActivity1 extends AppSuperActivity {
	private ArrayList<String> ImageList = new ArrayList<String>();

	private static int currentIndex = 0;

	FileOutputStream output = null;
	OutputStreamWriter writer = null;

	static SwipeInterceptingGallery albumGallery = null;
	private static TextView photoName = null;
	public static ArrayList<AlbumSub> albumGalleryDetails = new ArrayList<AlbumSub>();
	private RelativeLayout galleryTobBarLayout = null;
	private String albumName = null;
	private int Flag = 0;
	private int Loader_Flag = 0;
	private LinearLayout shareLayout = null;
	private LinearLayout bottumLayout = null;
	private Timer timer;
	private ImageAdapter adapter = null;
	private boolean pauseFlag = false;
	private boolean updateFlag = true;
	private static String TAG = "SLPhotoGalleryActivity1";
	AppManager appManager;
	private static Tracker localGaTracker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		try {
			setContentView(R.layout.album_gallery);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
		}
		appManager = AppManager.getInstance();
		localGaTracker = mGaTracker;
		// GoogleAnalytics mGaInstance = GoogleAnalytics.getInstance(this);
		// mGaInstance.setDebug(true);
		// mGaInstance.getTracker(AppManager.getInstance().GoogleAnalyticsID);
		// ///////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent, this);
		// ///////////////////////////
		Log.d("Test", "SLPhotoGalleryActivity1::onCreate");
		SnapLionMyAppActivity.KillFlag = true;

		currentIndex = 0;

		Intent i = getIntent();
		albumName = i.getStringExtra("PhotoName");

		if (i.getIntExtra("Index", 0) == 0) {
			albumGalleryDetails = SLPhotoAlbumActivity.albumSubDetails;
		} else {
			albumGalleryDetails = SLPhotosActivity.albumSubDetails;
		}

		albumGallery = (SwipeInterceptingGallery) findViewById(R.id.my_gallery);
		SharedPreferences indexPrefs = getSharedPreferences("currentIndex"
				+ appManager.getAPP_ID(), MODE_PRIVATE);
		currentIndex = indexPrefs.getInt("currentIndex", 0);
		photoName = (TextView) findViewById(R.id.album_gallery_name_txt);
		try {
			photoName.setText(albumGalleryDetails.get(currentIndex)
					.getPhotoname());
		} catch (Exception e) {
			e.printStackTrace();
		}
		photoName.setTypeface(appManager.lucida_grande_regular);

		Button homeBtn = (Button) findViewById(R.id.photo_gallery_home_btn);
		homeBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SnapLionMyAppActivity.KillFlag = false;
				// try{timer.cancel();}catch (Exception e)
				// {e.printStackTrace();}
				try {
					com.snaplion.music.SLMusicPlayerActivity
							.closePlayer(getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
				}
				finish();
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					albumGalleryDetails.clear();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		Button backBtn = (Button) findViewById(R.id.photo_gallery_back_btn);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SnapLionMyAppActivity.KillFlag = false;

				// try{timer.cancel();}catch (Exception e)
				// {e.printStackTrace();}
				finish();
			}
		});

		shareLayout = (LinearLayout) findViewById(R.id.gallery_bottum_bar);
		shareLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final Dialog dialog = new Dialog(SLPhotoGalleryActivity1.this,
						android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
				dialog.setContentView(R.layout.share_popup);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(true);
				Button saveBtn = (Button) dialog.findViewById(R.id.p_save_lib);
				saveBtn.setTypeface(appManager.lucida_grande_regular);
				saveBtn.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						saveImageOnSD(0);
						dialog.dismiss();
						// //SharedPreferences indexPrefs =
						// getSharedPreferences("currentIndex"+appManager.getAPP_ID(),MODE_PRIVATE);
						// //int count = indexPrefs.getInt("currentIndex", 0);
						//
						// Date date = new Date();
						// //String name =
						// albumGalleryDetails.get(count).getPhotoname()+String.valueOf(
						// date.getDate()+date.getTime());
						// String name =
						// "SnapLion_"+String.valueOf(date.getTime());
						// Bitmap bitmapImage = null;
						// try
						// {
						// BitmapFactory.Options options = new
						// BitmapFactory.Options();
						// options.inSampleSize = 1;
						// bitmapImage =
						// BitmapFactory.decodeFile(ImageList.get(currentIndex),
						// options);
						// }catch (OutOfMemoryError e)
						// {
						// e.printStackTrace();System.gc(); bitmapImage = null;
						// try
						// {
						// BitmapFactory.Options options = new
						// BitmapFactory.Options();
						// options.inSampleSize = 2;
						// bitmapImage =
						// BitmapFactory.decodeFile(ImageList.get(currentIndex),
						// options);
						// }
						// catch (OutOfMemoryError e1)
						// {
						// e1.printStackTrace();
						// }
						// }
						//
						// ByteArrayOutputStream bytes = new
						// ByteArrayOutputStream();
						// bitmapImage.compress(Bitmap.CompressFormat.JPEG, 40,
						// bytes);
						// File f = new
						// File(Environment.getExternalStorageDirectory()+
						// File.separator + name+".jpg");
						// try
						// {
						// f.createNewFile();
						// FileOutputStream fo = new FileOutputStream(f);
						// fo.write(bytes.toByteArray());
						// Toast.makeText(getApplicationContext(),
						// "Image is Saved Successfully...", 1000).show();
						// scanSDcardToShowImageInGallery();
						// }
						// catch (Exception e) {}
						// dialog.dismiss();
						// bitmapImage = null;
					}
				});

				// set up button
				Button emailBtn = (Button) dialog.findViewById(R.id.p_email);
				emailBtn.setTypeface(appManager.lucida_grande_regular);
				emailBtn.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						pauseFlag = true;
						SharedPreferences indexPrefs = getSharedPreferences(
								"currentIndex" + appManager.getAPP_ID(),
								MODE_PRIVATE);
						int count = indexPrefs.getInt("currentIndex", 0);

						Intent i = new Intent(Intent.ACTION_SEND);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.setType("image/jpg");
						String s = "Photo shared from "
								+ appManager.getAPP_NAME() + " app";
						i.putExtra(android.content.Intent.EXTRA_SUBJECT, s);
						Uri uri = null;
						try {
							if (currentIndex > albumGalleryDetails.size())
								currentIndex = 0;
							String emailBody = "Photo shared from "
									+ appManager.getAPP_NAME() + " app";
							if (albumGalleryDetails.get(currentIndex)
									.getPhotoname() != null
									&& albumGalleryDetails.get(currentIndex)
											.getPhotoname().trim().length() > 0) {
								emailBody = emailBody
										+ System.getProperty("line.separator")
										+ "Photo Caption - "
										+ albumGalleryDetails.get(currentIndex)
												.getPhotoname();
							}
							i.putExtra(android.content.Intent.EXTRA_TEXT,
									emailBody);

							// uri=Uri.parse("file:///sdcard/"+albumName+".jpg");
							uri = Uri.fromFile(saveImageOnSD(1));
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (uri != null) {
							i.putExtra(Intent.EXTRA_STREAM, uri);
						}
						startActivity(i);
						dialog.dismiss();
					}
				});

				Button cancelBtn = (Button) dialog.findViewById(R.id.p_cancel);
				cancelBtn.setTypeface(appManager.lucida_grande_regular);
				cancelBtn.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				Log.d(TAG, "image list size" + ImageList.size());
				if (ImageList.size() > 0) {
					dialog.show();
				} else {
					Utility.showToastMessage(getApplicationContext(),
							getString(R.string.no_image_to_share));
				}
			}

		});

		bottumLayout = (LinearLayout) findViewById(R.id.gallery_bottum_bar);
		galleryTobBarLayout = (RelativeLayout) findViewById(R.id.gallery_tob_bar);

		albumGallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				try {
					photoName.setText(albumGalleryDetails.get(arg2)
							.getPhotoname());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (Flag == 0) {
					Flag = 1;
					galleryTobBarLayout.setVisibility(View.GONE);
					bottumLayout.setVisibility(View.GONE);
				} else if (Flag == 1) {
					Flag = 0;
					galleryTobBarLayout.setVisibility(View.VISIBLE);
					bottumLayout.setVisibility(View.VISIBLE);
				}
			}
		});

		adapter = new ImageAdapter(getApplicationContext());

		Flag = indexPrefs.getInt("GalleryIndex", 0);
		if (Flag == 0) {
			galleryTobBarLayout.setVisibility(View.VISIBLE);
			bottumLayout.setVisibility(View.VISIBLE);
		} else if (Flag == 1) {
			galleryTobBarLayout.setVisibility(View.GONE);
			bottumLayout.setVisibility(View.GONE);
		}
		handler.sendEmptyMessage(appManager.DISPLAY);
	}

	private File saveImageOnSD(int param) {
		Date date = new Date();
		// String name =
		// albumGalleryDetails.get(count).getPhotoname()+String.valueOf(
		// date.getDate()+date.getTime());
		String name = "SnapLion_" + String.valueOf(date.getTime());
		Bitmap bitmapImage = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			bitmapImage = BitmapFactory.decodeFile(ImageList.get(currentIndex),
					options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
			bitmapImage = null;
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				bitmapImage = BitmapFactory.decodeFile(
						ImageList.get(currentIndex), options);
			} catch (OutOfMemoryError e1) {
				e1.printStackTrace();
			}
		}

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmapImage.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
		File f = new File(Environment.getExternalStorageDirectory()
				+ File.separator + name + ".jpg");
		try {
			f.createNewFile();
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());
			if (param == 0)
				Toast.makeText(getApplicationContext(),
						"Image is Saved Successfully...", 1000).show();
			scanSDcardToShowImageInGallery();
		} catch (Exception e) {
		}
		// dialog.dismiss();
		bitmapImage = null;
		return f;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppManager.DISPLAY:
				Utility.FileCreate();
				try {
					SharedPreferences indexPrefs = getSharedPreferences(
							"AlbumInfo"
									+ albumGalleryDetails.get(0).getAlbumid()
									+ appManager.getAPP_ID(), MODE_PRIVATE);
					if (indexPrefs.getBoolean("AlbumInfo", false)) {
						Loader_Flag = 1;
						getImageList(albumGalleryDetails);
						SharedPreferences indPrefs = getSharedPreferences(
								"Index", MODE_PRIVATE);
						SharedPreferences.Editor indEditor = indPrefs.edit();
						indEditor.putInt("Index", 1);
						indEditor.commit();
						albumGallery.setAdapter(adapter);
						albumGallery.setSelection(currentIndex);
						adapter.notifyDataSetChanged();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Loader_Flag = 0;
				}
				handler.sendEmptyMessage(appManager.PROGRESS);
				break;
			case AppManager.PROGRESS:
				SharedPreferences indPrefs = getSharedPreferences("Index",
						MODE_PRIVATE);
				if (indPrefs.getInt("Index", 0) == 0) {
					if (Loader_Flag == 0) {
						// try
						// {
						// showDialog(appManager.PROGRESS);
						// }
						// catch (Exception e)
						// {
						// e.printStackTrace();
						// }
						if (Utility.isOnline(getApplicationContext())) {
							new MyDownloadBGTask().execute();
						} else {
							try {
								// dismissDialog(appManager.PROGRESS);
								Utility.showToastMessage(
										getApplicationContext(),
										getString(R.string.no_net_not_downloaded));
							} catch (Exception e) {
								Log.d(TAG, "Exception to dismiss dialog", e);
							}
						}
					}
				}
				break;
			case AppManager.ERROR:
				Toast.makeText(getApplicationContext(),
						"Network Fail.Please try later .!", Toast.LENGTH_LONG)
						.show();
				break;
			}
		}
	};

	private void scanSDcardToShowImageInGallery() {
		sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));
	}

	private class MyDownloadBGTask extends AsyncTask<Void, Integer, Void> {
		@Override
		protected void onPostExecute(Void result) {
			if (Loader_Flag == 0) {
				try {
					try {
						ImageList.clear();
					} catch (Exception e) {
						e.printStackTrace();
					}
					getImageList(albumGalleryDetails);

					SharedPreferences indexPrefs = getSharedPreferences(
							"AlbumInfo"
									+ albumGalleryDetails.get(0).getAlbumid()
									+ appManager.getAPP_ID(), MODE_PRIVATE);
					SharedPreferences.Editor indexEditor = indexPrefs.edit();
					indexEditor.putBoolean("AlbumInfo", true);
					indexEditor.commit();

					SharedPreferences indPrefs = getSharedPreferences("Index",
							MODE_PRIVATE);
					SharedPreferences.Editor indEditor = indPrefs.edit();
					indEditor.putInt("Index", 1);
					indEditor.commit();

					albumGallery.setAdapter(adapter);
					albumGallery.setSelection(currentIndex);
					adapter.notifyDataSetChanged();

					if (updateFlag) {
						SharedPreferences myPref2 = getSharedPreferences(
								"AlbumUpdate" + appManager.getAPP_ID(),
								MODE_PRIVATE);
						SharedPreferences.Editor editor2 = myPref2.edit();
						editor2.putBoolean("AlbumBig"
								+ albumGalleryDetails.get(0).getAlbumid()
								+ appManager.getAPP_ID(), false);
						String str = myPref2.getString("MAlbum"
								+ albumGalleryDetails.get(0).getAlbumid()
								+ appManager.getAPP_ID(), "");
						editor2.putString("AlbumTime"
								+ albumGalleryDetails.get(0).getAlbumid()
								+ appManager.getAPP_ID(), str);
						editor2.commit();
					}
					// dismissDialog(appManager.PROGRESS);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Void... params) {

			// for(int i=0;i<albumGalleryDetails.size();i++)
			// {
			// try
			// {
			// String bigPictureUrl =
			// albumGalleryDetails.get(i).getBigpicture();
			// String bigPictureKey =
			// "big"+albumGalleryDetails.get(0).getAlbumname()+albumGalleryDetails.get(0).getAlbumid()+albumGalleryDetails.get(i).getPhotoid()+appManager.getAPP_ID();
			// downloadBigImage(bigPictureUrl, bigPictureKey);
			// }
			// catch(Exception ex)
			// {
			// ex.printStackTrace();
			// }
			// }
			return null;
		}
	}

	private void downloadBigImage(String bigPictureUrl, String bigPictureKey) {
		Bitmap bitmap = null;
		URL url = null;
		try {
			bigPictureUrl = Utility.replace(bigPictureUrl, " ", "%20");
			url = new URL(bigPictureUrl);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			URLConnection connection = url.openConnection();
			connection.connect();
			InputStream is = connection.getInputStream();
			if (is == null) {
				updateFlag = false;
			} else {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				try {
					bitmap = BitmapFactory.decodeStream(is, null, options);
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
					bitmap = null;
					try {
						options.inSampleSize = 2;
						bitmap = BitmapFactory.decodeStream(is, null, options);
					} catch (OutOfMemoryError e1) {
						e1.printStackTrace();
						bitmap = null;
						try {
							options.inSampleSize = 3;
							bitmap = BitmapFactory.decodeStream(is, null,
									options);
						} catch (OutOfMemoryError e2) {
							e2.printStackTrace();
							bitmap = null;
							try {
								options.inSampleSize = 4;
								bitmap = BitmapFactory.decodeStream(is, null,
										options);
							} catch (OutOfMemoryError e3) {
								e3.printStackTrace();
								bitmap = null;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (bitmap != null) {
			OutputStream outStream = null;
			File file = new File(appManager.DATA_DIRECTORY, bigPictureKey
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

		} else {
			updateFlag = false;
		}
		bitmap = null;
		System.gc();
	}

	public void saveAlbumInSDCard(ArrayList<AlbumSub> albumGalleryDetails2) {
		for (int i = 0; i < albumGalleryDetails2.size(); i++) {
			String key = "big" + albumGalleryDetails2.get(i).getAlbumname()
					+ albumGalleryDetails2.get(0).getAlbumid()
					+ albumGalleryDetails2.get(i).getPhotoid()
					+ appManager.getAPP_ID();

			OutputStream outStream = null;
			File file = new File(appManager.DATA_DIRECTORY, key + ".PNG");
			try {
				outStream = new FileOutputStream(file);
				albumGalleryDetails2.get(i).getBitmapBig()
						.compress(Bitmap.CompressFormat.PNG, 0, outStream);
				outStream.flush();
				outStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void getImageList(ArrayList<AlbumSub> albumGalleryDetails2) {
		for (int i = 0; i < albumGalleryDetails2.size(); i++) {
			String key = "big" + albumGalleryDetails2.get(0).getAlbumname()
					+ albumGalleryDetails2.get(0).getAlbumid()
					+ albumGalleryDetails2.get(i).getPhotoid()
					+ appManager.getAPP_ID();
			try {
				ImageList.add(appManager.DATA_DIRECTORY + key + ".PNG");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences indexPrefs = getSharedPreferences("currentIndex"
				+ appManager.getAPP_ID(), MODE_PRIVATE);
		SharedPreferences.Editor indexEditor = indexPrefs.edit();
		currentIndex = albumGallery.getSelectedItemPosition();
		indexEditor.putInt("currentIndex", currentIndex);
		indexEditor.putInt("GalleryIndex", Flag);
		indexEditor.commit();

		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		boolean isScreenOn = powerManager.isScreenOn();
		if (!isScreenOn) {
			SnapLionMyAppActivity.KillFlag = false;
		}
		pauseFlag = !pauseFlag;

		if (SnapLionMyAppActivity.KillFlag && pauseFlag) {
			try {
				Utility.killMyApp(getApplicationContext(),
						SLPhotoGalleryActivity1.this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		pauseFlag = false;
		SnapLionMyAppActivity.KillFlag = true;
		SharedPreferences indexPrefs = getSharedPreferences("currentIndex"
				+ appManager.getAPP_ID(), MODE_PRIVATE);
		if (indexPrefs.contains("currentIndex")) {
			currentIndex = indexPrefs.getInt("currentIndex", 0);
		}
	}

	public class ImageAdapter extends BaseAdapter {
		protected Context context;

		public ImageAdapter(Context c) {
			context = c;
		}

		// ---returns the number of images---
		public int getCount() {
			return ImageList.size();
		}

		// ---returns the ID of an item---
		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		// ////////
		public class BigPictureDownloadAsynchTask extends
				AsyncTask<Void, Void, Void> {
			ProgressDialog progress;
			int position;

			public BigPictureDownloadAsynchTask(ProgressDialog progress,
					int position) {
				this.progress = progress;
				this.position = position;
			}

			public void onPreExecute() {
				progress.show();
			}

			public Void doInBackground(Void... unused) {
				try {
					String bigPictureUrl = albumGalleryDetails.get(position)
							.getBigpicture();
					String bigPictureKey = "big"
							+ albumGalleryDetails.get(0).getAlbumname()
							+ albumGalleryDetails.get(0).getAlbumid()
							+ albumGalleryDetails.get(position).getPhotoid()
							+ appManager.getAPP_ID();
					downloadBigImage(bigPictureUrl, bigPictureKey);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				this.publishProgress(null);
				return null;
			}

			@Override
			protected void onProgressUpdate(Void... values) {
				super.onProgressUpdate(values);
			}

			public void onPostExecute(Void unused) {
				progress.dismiss();
				adapter.notifyDataSetChanged();
			}
		}

		// ////////
		// ---returns an ImageView view---
		public View getView(int position, View convertView, ViewGroup parent) {
			String bitmapPath = ImageList.get(position);
			TouchImageView img = new TouchImageView(getApplicationContext());
			if (!new File(bitmapPath).exists()) {
				ProgressDialog progress = new ProgressDialog(
						SLPhotoGalleryActivity1.this);
				progress.setMessage(getResources().getString(
						R.string.splash_loading_msg));
				new BigPictureDownloadAsynchTask(progress, position).execute();
			} else {
				Bitmap bitmapImage = null;
				try {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 1;

					bitmapImage = BitmapFactory.decodeFile(
							ImageList.get(position), options);
					// Utility.debug("file path:"+ImageList.get(position));
					img.setImageBitmap(bitmapImage);

				} catch (OutOfMemoryError e) {
					e.printStackTrace();
					System.gc();
					try {
						BitmapFactory.Options options1 = new BitmapFactory.Options();
						options1.inSampleSize = 2;

						bitmapImage = BitmapFactory.decodeFile(
								ImageList.get(position), options1);
						img.setImageBitmap(bitmapImage);
					} catch (OutOfMemoryError e1) {
						e1.printStackTrace();
						try {
							BitmapFactory.Options options2 = new BitmapFactory.Options();
							options2.inSampleSize = 3;

							bitmapImage = BitmapFactory.decodeFile(
									ImageList.get(position), options2);
							img.setImageBitmap(bitmapImage);
						} catch (OutOfMemoryError e2) {
							e1.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				bitmapImage = null;
				img.setLayoutParams(new Gallery.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				Runtime.getRuntime().gc();
			}
			return img;
		}
	}

	public Bitmap reduceImageSize(String mSelectedImagePath) {
		Bitmap m = null;
		try {
			File f = new File(mSelectedImagePath);
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			// The new size we want to scale to
			final int REQUIRED_SIZE = 150;
			// Find the correct scale value. It should be the power of 2.
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			m = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			Toast.makeText(
					getApplicationContext(),
					"Image File not found in your phone. Please select another image.",
					Toast.LENGTH_LONG).show();
		}
		return m;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case AppManager.PROGRESS:
			final ProgressDialog progDialog = new ProgressDialog(this);
			progDialog.setMessage("Loading, Please wait...");
			progDialog.setCanceledOnTouchOutside(false);
			progDialog.setCancelable(false);
			return progDialog;
		}
		return null;
	}

	@Override
	protected void onDestroy() {
		Runtime.getRuntime().gc();
		super.onDestroy();
	}

	public static void setSelimageOfGallery(int direction) { // direction 0 for
																// left 1 for
																// right.
	// int a = albumGallery.getSelectedItemPosition(); // It is giving wrong
	// value.
		int a = 0;
		if (direction == 0) {
			a = albumGallery.getFirstVisiblePosition(); // left
		} else {
			a = albumGallery.getLastVisiblePosition(); // right
		}
		if (!(currentIndex >= albumGalleryDetails.size())) {
			try {
				Log.d(TAG, "Caption="
						+ albumGalleryDetails.get(a).getPhotoname());
				photoName.setText(albumGalleryDetails.get(a).getPhotoname());
				localGaTracker.sendView("Photo_"
						+ albumGalleryDetails.get(a).getPhotoid());
			} catch (Exception e) {
				e.printStackTrace();
			}
			currentIndex = a;
		}
	}

	@Override
	public void onBackPressed() {
		SnapLionMyAppActivity.KillFlag = false;
		// try{timer.cancel();}catch (Exception e) {e.printStackTrace();}
		finish();
		super.onBackPressed();
	}

	@Override
	protected void onStart() {
		super.onStart();

		AlbumSub albumSub = albumGalleryDetails.get(currentIndex);
		String photoId = albumSub.getPhotoid();
		localGaTracker.sendView("Photo_" + photoId);
	}
}