package com.snaplion.fanwall;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.snaplion.fanwall.wall.FanwallWallComentDetailsActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMyAppSplashActivity;
import com.snaplion.locationservices.UserLocationManager;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.preview.PreviewSnapLionAppsSelectActvity;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

public class FanwallAddCommentActivity extends FanwallSuperActivity {
	int type = 0;
	int section_id = 0;
	static BitmapDrawable background;
	private String BackFlag = "yes";
	private String appName = null;

	private LinearLayout mainBg = null;
	private Button openCommunityButton;
	private Button openPhotosButton;
	TextView addCommentLabel;
	EditText addCommentTextField;
	Button commentPostButton;
	Button takeCommentPhotoButton;
	RelativeLayout sendingProgressIndeterminateBarParent;
	ImageView deleteAttachedImage;
	ImageView commentAttachedImagePreview;
	TextView commentAttachedImageName;
	String parentId;
	Uri imageUri;
	String filePath = "";
	// AppManager appManager;

	final private static int REQUEST_CODE_IMAGE_PICK_AND_UPLOAD = 331;
	private static final int REQUEST_CODE_TAKE_PICTURE_AND_UPLOAD = 332;

	public class mWallHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case FanwalLoginManager.FLAG_COMMENT_SUCCES: {
				SnapLionMyAppActivity.KillFlag = false;
				String addedComentJsonString = (String) msg.obj;
				Intent intent = new Intent();
				intent.putExtra("TYPE", "JSON");
				intent.putExtra("VALUE", addedComentJsonString);
				intent.putExtra("PARENT_ID", parentId);
				setResult(RESULT_OK, intent);
				if (getIntent().getBooleanExtra("open_comment_screen", false)) {
					FanwallComment clicked_post = getIntent()
							.getParcelableExtra(
									"open_comment_screen_parent_post");
					clicked_post.setTotal_comments(String.valueOf(Integer
							.parseInt(clicked_post.getTotal_comments()) + 1));
					Intent i1010 = new Intent(getApplicationContext(),
							FanwallWallComentDetailsActivity.class);
					i1010.putExtra("CLICKED_POST", clicked_post);
					i1010.putExtra("FanwallSectionName", catName);
					startActivityForResult(i1010,
							FanwalLoginManager.ACTIVITY_CODE_SHOW_COMMENTS);
				} else {
					finish();
				}
				break;
			}
			case FanwalLoginManager.FLAG_ERROR: {
				String result = (String) msg.obj;
				Intent intent = new Intent();
				intent.putExtra("TYPE", "ERROR");
				intent.putExtra("VALUE", result);
				intent.putExtra("PARENT_ID", parentId);
				setResult(RESULT_OK, intent);
				SnapLionMyAppActivity.KillFlag = false;
				finish();
				break;
			}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fanwall_addcomment_activity);
		appManager = AppManager.getInstance();
		// ///////////////////////////
		LinearLayout mIMAdViewParent = (LinearLayout) findViewById(R.id.imAdview);
		appManager.displayAdds(mIMAdViewParent, this);
		// ///////////////////////////
		mHandler = new mWallHandler();
		initUI();
		setupAdaptersListeners();

		// if(com.snaplion.util.BottomClass.getBottomAvailable(getApplicationContext(),appManager.getAPP_ID(),"1010"))
		// {
		// Display display = getWindowManager().getDefaultDisplay();
		// //BackFlag = "no";
		// LinearLayout bottomLayout =
		// (LinearLayout)findViewById(R.id.video_bottom_bar);
		// bottomLayout.setVisibility(View.VISIBLE);
		//
		// try
		// {
		// com.snaplion.util.BottomClass.showBLayout(this,bottomLayout,display.getWidth(),appManager.getAPP_ID(),appName);
		// }
		// catch (OutOfMemoryError e)
		// {
		// e.printStackTrace();
		// }
		// }

		TextView tv = (TextView) findViewById(R.id.v_sub_name_txt);
		tv.setText(catName);
		tv.setTypeface(appManager.lucida_grande_regular);

		if (appManager.PREVIEWAPP_FLAG) {
			Button homeBtn = (Button) findViewById(R.id.v_home_btn);
			homeBtn.setVisibility(View.VISIBLE);
			homeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SnapLionMyAppActivity.KillFlag = false;
					try {
						com.snaplion.music.SLMusicPlayerActivity
								.closePlayer(getApplicationContext());
					} catch (Exception e) {
						e.printStackTrace();
					}
					Intent i = new Intent(FanwallAddCommentActivity.this,
							PreviewSnapLionAppsSelectActvity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					FanwallAddCommentActivity.this.startActivity(i);
					finish();
					try {
						System.gc();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}

		addCommentLabel = (TextView) findViewById(R.id.addCommentLabel);
		Button backBtn = (Button) findViewById(R.id.v_sub_back_btn);
		if (BackFlag.equalsIgnoreCase("no")) {
			if (!appManager.getAPP_ID().equalsIgnoreCase("222")) {
				backBtn.setVisibility(View.INVISIBLE);
			} else {
				backBtn.setBackgroundResource(R.drawable.top_bar_logo);
			}
		} else {
			if (appManager.getAPP_ID().equalsIgnoreCase("222")) {
				backBtn.setBackgroundResource(R.drawable.selector_back_btn);
			}
			backBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SnapLionMyAppActivity.KillFlag = false;
					finishFromChild(getParent());
				}
			});
		}

		mainBg = (LinearLayout) findViewById(R.id.fanwall_root_LinearLayout);
		BitmapDrawable background = new BitmapDrawable(
				Utility.getScaledCroppedImageByDisplay(
						this,
						SLMyAppSplashActivity.FetchImage("FanwallMainBg"
								+ appManager.getAPP_ID())));
		mainBg.setBackgroundDrawable(background);
		background = null;

	}

	private void initUI() {
		Intent i = getIntent();
		catName = i.getStringExtra("FanwallSectionName");
		type = i.getIntExtra("type", 0);
		section_id = i.getIntExtra("section_id", 0);

		openCommunityButton = (Button) findViewById(R.id.openCommunityButton);
		openPhotosButton = (Button) findViewById(R.id.openPhotosButton);
		addCommentTextField = (EditText) findViewById(R.id.addCommentTextField);
		commentPostButton = (Button) findViewById(R.id.commentPostButton);
		takeCommentPhotoButton = (Button) findViewById(R.id.takeCommentPhotoButton);
		deleteAttachedImage = (ImageView) findViewById(R.id.deleteAttachedImage);
		sendingProgressIndeterminateBarParent = (RelativeLayout) findViewById(R.id.sendingProgressIndeterminateBarParent);
		commentAttachedImagePreview = (ImageView) findViewById(R.id.commentAttachedImagePreview);
		commentAttachedImageName = (TextView) findViewById(R.id.commentAttachedImageName);
	}

	private void setupAdaptersListeners() {
		commentPostButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						addCommentTextField.getWindowToken(), 0);
				commentPostButton.setEnabled(false);
				ProgressDialog progress = new ProgressDialog(
						FanwallAddCommentActivity.this);
				progress.setMessage(getResources().getString(
						R.string.splash_loading_msg));
				new addNewCommentAsyncTask(progress).execute(
						addCommentTextField.getText().toString(), filePath);
			}
		});
		takeCommentPhotoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SnapLionMyAppActivity.KillFlag = false;
				showImageDialog();
			}
		});
		deleteAttachedImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				filePath = "";
				commentAttachedImagePreview.setVisibility(ImageView.INVISIBLE);
				commentAttachedImageName.setVisibility(TextView.INVISIBLE);
				deleteAttachedImage.setVisibility(ImageView.INVISIBLE);
			}
		});
	}

	private void showImageDialog() {
		final String[] items = new String[] { "From Camera", "From SD Card" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Select Image");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					dialog.cancel();
					// pictureUploadFromCamera();
					takePicture();
				} else {
					dialog.cancel();
					pictureUploadFromGallery();
				}
			}
		});
		final AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void pictureUploadFromGallery() {
		SnapLionMyAppActivity.KillFlag = false;
		Log.i("MyFilesScreen", "Item Selected----------------> Picture Upload");
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK_AND_UPLOAD);
	}

	// private void pictureUploadFromCamera()
	// {
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
	// String fileName = "COMMENT_" + sdf.format(new Date()) + ".jpg";
	// File file = new File(new File(appManager.DATA_DIRECTORY), fileName);
	// imageUri = Uri.fromFile(file);
	// System.out.println("imageUri : "+imageUri);
	// Intent intent = new
	// Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	// intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
	// startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE_AND_UPLOAD);
	// }
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == FanwalLoginManager.ACTIVITY_CODE_SHOW_COMMENTS) {
				finish();
			}

			if (requestCode == REQUEST_CODE_IMAGE_PICK_AND_UPLOAD) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				filePath = cursor.getString(columnIndex);
				showAttachedImagePreview(filePath);
				cursor.close();
			} else if (requestCode == REQUEST_CODE_TAKE_PICTURE_AND_UPLOAD) {
				// Bundle extras = data.getExtras();
				// Bitmap mImageBitmap = (Bitmap) extras.get("data");
				showAttachedImagePreview(filePath);
			} else {
				filePath = "";
			}
		}
	}

	private byte[] getBitmapBytes(Uri selectedImage) {
		byte[] byteArray = null;
		try {
			Utility.debug("selectedImage : " + selectedImage);
			InputStream imageStream = getContentResolver().openInputStream(
					selectedImage);
			Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byteArray = stream.toByteArray();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return byteArray;
	}

	private class addNewCommentAsyncTask extends
			AsyncTask<String, Void, Boolean> {
		// ProgressDialog progress;
		public addNewCommentAsyncTask(ProgressDialog progress) {
			// this.progress = progress;
		}

		public void onPreExecute() {
			sendingProgressIndeterminateBarParent
					.setVisibility(RelativeLayout.VISIBLE);
			// progress.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String message = params[0];
			String attachedFilePath = params[1];
			boolean isSuccessFlag = true;
			try {
				parentId = getIntent().getStringExtra("ParentCommentId");
				String fanid = getSharedPreferences(
						"FanwallRef" + appManager.getAPP_ID(), MODE_PRIVATE)
						.getString("fanid", null);
				if (fanid != null) {
					Location loc = UserLocationManager.getInstance()
							.getUserCurrentlocation();
					double latitude;
					double longitude;
					if (loc != null) {
						latitude = loc.getLatitude();
						longitude = loc.getLongitude();
					} else {
						latitude = 0;
						longitude = 0;
					}

					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(appManager.FANWALL_MSG_URL);
					MultipartEntity reqEntity = new MultipartEntity();
					reqEntity.addPart("fanId", new StringBody(fanid));
					reqEntity.addPart("appid",
							new StringBody(appManager.getAPP_ID()));
					reqEntity.addPart("parentId", new StringBody(parentId));
					reqEntity.addPart("message", new StringBody(message));
					reqEntity.addPart("lattitude",
							new StringBody(String.valueOf(latitude)));
					reqEntity.addPart("longitude",
							new StringBody(String.valueOf(longitude)));
					reqEntity.addPart("type",
							new StringBody(String.valueOf(type)));
					reqEntity.addPart("section_id",
							new StringBody(String.valueOf(section_id)));
					File file = new File(attachedFilePath);// path of image

					if (file.exists()) {
						ContentBody bin = new FileBody(file, "image/jpeg");
						reqEntity.addPart("photo", bin);
					}
					httppost.setEntity(reqEntity);
					HttpResponse response = httpclient.execute(httppost);
					String result = EntityUtils.toString(response.getEntity());
					Utility.debug(appManager.FANWALL_MSG_URL + "?fanId="
							+ fanid + "&appid=" + appManager.getAPP_ID()
							+ "&parentId=" + parentId + "&message="
							+ addCommentTextField.getText().toString()
							+ "&lattitude=" + String.valueOf(latitude)
							+ "&longitude=" + String.valueOf(longitude)
							+ "&type=" + type + "&section_id=" + section_id
							+ "&photo=bin<result=" + result + ">");

					try {
						JSONObject jsonobj = new JSONObject(result);
						Message msg = new Message();

						String loginType = getSharedPreferences(
								"FanwallRef" + appManager.getAPP_ID(),
								Activity.MODE_PRIVATE).getString("logintype",
								null);
						if (loginType != null
								&& loginType.equalsIgnoreCase("facebook")
								&& FanwalLoginManager.getInstance().FBhandle != null
								&& FanwalLoginManager.getInstance().FBhandle
										.authenticated()) {
							if (file.exists())
								postCommentOnFbWall(message, file);
							else
								postCommentOnFbWall(message, null);
						}
						msg.arg1 = FanwalLoginManager.FLAG_COMMENT_SUCCES;
						msg.obj = result;
						mHandler.sendMessage(msg);
						return true;
					} catch (JSONException jSONException) {
						Message msg = new Message();
						msg.arg1 = FanwalLoginManager.FLAG_ERROR;
						msg.obj = result;
						mHandler.sendMessage(msg);
						return false;
					}
				} else {
					Utility.debug("Fan id not found.");
					return false;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				// Toast.makeText(context, text, duration)
			}
			super.onPostExecute(result);
			// if(progress.isShowing())
			// {
			// progress.dismiss();
			// }
			sendingProgressIndeterminateBarParent
					.setVisibility(RelativeLayout.GONE);
			commentPostButton.setEnabled(true);
		}
	}

	public void postCommentOnFbWall(String message, File imageFile) {
		// shrikant
		// the comment can be posted on fb wall only if app is kingxi
		// i.e. if its app id is 222.
		if (appManager.getAPP_ID().equals("222")) {

			String url = "";
			AQuery aq = new AQuery(this);

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("message", "Via " + appManager.getAPP_NAME()
					+ " Mobile App: " + message);
			if (appManager.getAPP_ID().equalsIgnoreCase("222")) {
				params.put("link", "http://www.facebook.com/149756715038769");
			}
			if (imageFile != null) {
				params.put("source", imageFile);
				url = "https://graph.facebook.com/me/photos";
			} else {
				url = "https://graph.facebook.com/me/feed";
			}
			aq.auth(FanwalLoginManager.getInstance().FBhandle).ajax(url,
					params, JSONObject.class, new AjaxCallback<JSONObject>() {
						@Override
						public void callback(String url, JSONObject json,
								AjaxStatus status) {
							if (json != null) {
								try {
									System.out
											.println("postCommentOnFbWall json : "
													+ json.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							Log.d("MTK", "status code:" + status.getCode()
									+ "\n error:" + status.getError());
						}
					});
		}
	}

	// ////////////////////////////////////////////////////////////////////////

	private File createImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = timeStamp + "_";
		File image = File.createTempFile(imageFileName, "fanwall", new File(
				appManager.DATA_DIRECTORY));
		filePath = image.getPath();
		return image;
	}

	public void takePicture() {
		try {
			SnapLionMyAppActivity.KillFlag = false;
			Intent takePictureIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			takePictureIntent.putExtra(
					android.provider.MediaStore.EXTRA_SCREEN_ORIENTATION,
					ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			File f = createImageFile();
			takePictureIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			startActivityForResult(takePictureIntent,
					REQUEST_CODE_TAKE_PICTURE_AND_UPLOAD);
		} catch (Exception ex) {

		}
	}

	private void showAttachedImagePreview(String imagePath) {
		Bitmap bitmap = null;
		// = BitmapFactory.decodeFile(imagePath);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		try {
			bitmap = BitmapFactory.decodeFile(imagePath, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			bitmap = null;
			try {
				options.inSampleSize = 2;
				bitmap = BitmapFactory.decodeFile(imagePath, options);
			} catch (OutOfMemoryError e1) {
				e1.printStackTrace();
				bitmap = null;
				try {
					options.inSampleSize = 3;
					bitmap = BitmapFactory.decodeFile(imagePath, options);
				} catch (OutOfMemoryError e2) {
					e2.printStackTrace();
					bitmap = null;
					try {
						options.inSampleSize = 4;
						bitmap = BitmapFactory.decodeFile(imagePath, options);
					} catch (OutOfMemoryError e3) {
						e3.printStackTrace();
						bitmap = null;
					}
				}
			}
		}
		// //////////
		if (bitmap != null) {
			commentAttachedImagePreview.setVisibility(ImageView.VISIBLE);
			commentAttachedImagePreview.setImageBitmap(bitmap);
			commentAttachedImageName.setVisibility(TextView.VISIBLE);
			commentAttachedImageName.setText(new File(imagePath).getName());
			deleteAttachedImage.setVisibility(ImageView.VISIBLE);
		}

		// ////////////////////////
		try {
			File file = new File(AppManager.getInstance().DATA_DIRECTORY,
					"snaplion_tmp.png");
			FileOutputStream outStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
			outStream.flush();
			outStream.close();
			filePath = file.getPath();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		SnapLionMyAppActivity.KillFlag = false;
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SnapLionMyAppActivity.KillFlag = true;
	}
	// @Override
	// protected void onPause()
	// {
	// PowerManager powerManager = (PowerManager)
	// getSystemService(POWER_SERVICE);
	// boolean isScreenOn = powerManager.isScreenOn();
	// if (!isScreenOn) {
	// SnapLionMyAppActivity.KillFlag = false;
	// }
	// if(SnapLionMyAppActivity.KillFlag)
	// {
	// try{
	// Utility.killMyApp(getApplicationContext(),FanwallAddCommentActivity.this);
	// }catch (Exception e) {e.printStackTrace();}
	// }
	// super.onPause();
	// }
}
