package com.snaplion.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snaplion.beans.Bottom;
import com.snaplion.bio.SLBioActivity;
import com.snaplion.castcrew.MultibioCategoryListActivity;
import com.snaplion.contact.SLContactActivity;
import com.snaplion.copyright.SLCopyRightActivity;
import com.snaplion.fanwall.wall.FanwallWallActivity;
import com.snaplion.kingsxi.AppSuperActivity;
import com.snaplion.kingsxi.R;
import com.snaplion.kingsxi.SLMoreActivtity;
import com.snaplion.kingsxi.SnapLionActivity;
import com.snaplion.mail.SLMailActivity;
import com.snaplion.menus.MenusActivity;
import com.snaplion.music.SLMusicActivity;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.news.SLEventActivity;
import com.snaplion.news.SLNewsActivity;
import com.snaplion.photos.SLPhotoGalleryActivity1;
import com.snaplion.photos.SLPhotosActivity;
import com.snaplion.promo.PromoActivity;
import com.snaplion.scorecard.ScoreCardActivity;
import com.snaplion.tickets.SLTicketActivity;
import com.snaplion.videos.SLVideoActivity;

public class BottomClass {
	protected static final String TAG = null;
	static Context context = null;
	static String appID = null;
	static String appName = null;
	static ArrayList<Bottom> bottomIconeList = new ArrayList<Bottom>();
	static boolean bottomFlag;
	static String catID = null;
	static String selData = null;
	AppManager appManager;

	public BottomClass() {
		appManager = AppManager.getInstance();
	}

	public static LinearLayout showBLayout(AppSuperActivity applicationContext,
			LinearLayout bbLL, int width2, String appId, String appName1) {
		// bbLL.setBackgroundResource(R.drawable.tabs_bottom_bg);
		bbLL.setWeightSum(5);
		context = applicationContext;
		appName = appName1;
		appID = appId;
		SharedPreferences myPref1 = context.getSharedPreferences("SLPref"
				+ appId, Context.MODE_PRIVATE);
		selData = myPref1.getString("BRemData", "");
		String bottumStr = myPref1.getString("BottomTabData", "");
		try {
			InputStream in = new ByteArrayInputStream(
					bottumStr.getBytes("UTF-8"));

			try {
				bottomIconeList.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (in != null)
				bottomIconeList = getBottomDetails(in);

			// Bottom btn = bottomIconeList.remove(3);
			// Bottom b1 = new Bottom();
			// b1.setFeatureid("1020");
			// b1.setFeaturename("ScoreCard");
			// bottomIconeList.add(3,b1);
			// bottomIconeList.add(5,btn);

		} catch (Exception e) {
		}

		for (int i = 0; i < 5; i++) {
			try {
				LinearLayout layout = new LinearLayout(context);
				layout.setGravity(Gravity.BOTTOM);
				layout.setPadding(0, 0, 0, 5);
				String str = null;
				if (bottomIconeList.get(i).getFeatureid()
						.equalsIgnoreCase(selData)) {
					str = "bb_sel_" + bottomIconeList.get(i).getFeatureid()
							+ ".png";
					layout.setClickable(false);
					layout.setEnabled(false);
				} else {
					str = "bb_unsel_" + bottomIconeList.get(i).getFeatureid()
							+ ".png";
				}
				BitmapDrawable background = new BitmapDrawable(
						getBitmapFromAsset(str));
				layout.setBackgroundDrawable(background);
				layout.setId(Integer.parseInt(bottomIconeList.get(i)
						.getFeatureid()));
				// layout.setLayoutParams(new LinearLayout.LayoutParams(58,58));
				// layout.setLayoutParams(new
				// LinearLayout.LayoutParams((width-40)/5,LayoutParams.FILL_PARENT));

				// LinearLayout.LayoutParams layoutParams = new
				// LinearLayout.LayoutParams((width2-a)/5,LayoutParams.FILL_PARENT);
				LinearLayout.LayoutParams layoutParams;
				TextView tv = new TextView(context);
				tv.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				tv.setTextColor(Color.WHITE);
				tv.setTextSize(9);
				int tabScaledWidth = context.getResources()
						.getDimensionPixelSize(R.dimen.bottom_tabs_width);
				int tabScaledHeight = context.getResources()
						.getDimensionPixelSize(R.dimen.bottom_tabs_height);
				layoutParams = new LinearLayout.LayoutParams(tabScaledWidth,
						tabScaledHeight);
				layout.setLayoutParams(layoutParams);
				tv.setSingleLine();
				int tv_scaledLeftPadding = context.getResources()
						.getDimensionPixelSize(
								R.dimen.bottom_tabs_text_left_padding);
				int tv_scaledRightPadding = context.getResources()
						.getDimensionPixelSize(
								R.dimen.bottom_tabs_text_right_padding);
				tv.setPadding(tv_scaledLeftPadding, 0, tv_scaledRightPadding, 0);
				// tv.setGravity(Gravity.CENTER);
				tv.setText(bottomIconeList.get(i).getFeaturename());
				// layout.setGravity(Gravity.CENTER_HORIZONTAL);
				layout.addView(tv);
				// /////////
				LinearLayout.LayoutParams layoutParentParams = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1);
				LinearLayout layoutParent = new LinearLayout(context);
				layoutParent.setGravity(Gravity.CENTER);
				layoutParent.setLayoutParams(layoutParentParams);
				layoutParent.addView(layout);
				bbLL.addView(layoutParent);
				// ///////////
				// bbLL.addView(layout);
				layout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!AppManager.getInstance().PREVIEWAPP_FLAG) {
							// AppManager.getInstance().setAppId(AppManager.getInstance().getAPP_ID());
							// AppManager.getInstance().setAppName(AppManager.getInstance().APP_NAME);
							AppManager.getInstance().loadURL();
						}

						// bbLL=resetClickableState(bbLL);
						AppSuperActivity appSuperActivity = (AppSuperActivity) v
								.getContext();
						switch (v.getId()) {
						case 1001:
							// appSuperActivity.addEventGATracor(AppManager.getInstance().getAPP_ID(),
							// "SECTION_OPENED", "HOME_1001", 0);
							SnapLionMyAppActivity.KillFlag = false;
							try {
								SLPhotoGalleryActivity1.albumGalleryDetails
										.clear();
							} catch (Exception e) {
								e.printStackTrace();
							}
							saveBData(v.getId());
							Intent i1001 = new Intent(context,
									SnapLionMyAppActivity.class);
							i1001.putExtra("Count", 3);
							i1001.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_CLEAR_TOP);
							context.startActivity(i1001);
							try {
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						case 1002:
							SnapLionMyAppActivity.KillFlag = false;
							try {
								SLPhotoGalleryActivity1.albumGalleryDetails
										.clear();
							} catch (Exception e) {
								e.printStackTrace();
							}
							saveBData(v.getId());
							Intent i1002 = new Intent(context,
									SLBioActivity.class);
							i1002.putExtra(
									"BioName",
									getCategoryName(bottomIconeList, v.getId()
											+ ""));
							i1002.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i1002);
							try {
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						case 1003:
							SnapLionMyAppActivity.KillFlag = false;
							try {
								SLPhotoGalleryActivity1.albumGalleryDetails
										.clear();
							} catch (Exception e) {
								e.printStackTrace();
							}
							saveBData(v.getId());
							Intent i1003 = new Intent(context,
									SLPhotosActivity.class);
							i1003.putExtra(
									"PhotoName",
									getCategoryName(bottomIconeList, v.getId()
											+ ""));
							i1003.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i1003);
							try {
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							// Log.v(TAG, "click3");
							break;
						case 1004:
							SnapLionMyAppActivity.KillFlag = false;
							try {
								SLPhotoGalleryActivity1.albumGalleryDetails
										.clear();
							} catch (Exception e) {
								e.printStackTrace();
							}
							saveBData(v.getId());
							Intent i1004 = new Intent(context,
									SLMusicActivity.class);
							i1004.putExtra(
									"MusicName",
									getCategoryName(bottomIconeList, v.getId()
											+ ""));
							i1004.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i1004);
							try {
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						case 1005:
							// appSuperActivity.addEventGATracor(AppManager.getInstance().getAPP_ID(),
							// "SECTION_OPENED", "MORE_1005", 0);
							SnapLionMyAppActivity.KillFlag = false;
							try {
								SLPhotoGalleryActivity1.albumGalleryDetails
										.clear();
							} catch (Exception e) {
								e.printStackTrace();
							}
							saveBData(v.getId());
							Intent i1005 = new Intent(context,
									SLMoreActivtity.class);
							i1005.putExtra(
									"MoreName",
									getCategoryName(bottomIconeList, v.getId()
											+ ""));
							i1005.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i1005);
							try {
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						case 1006:
							SnapLionMyAppActivity.KillFlag = false;
							saveBData(v.getId());
							Intent i1006 = new Intent(context,
									SLVideoActivity.class);
							i1006.putExtra("VideosName", BottomClass
									.getCategoryName(bottomIconeList, v.getId()
											+ ""));
							i1006.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i1006);
							try {
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						case 1007:
							SnapLionMyAppActivity.KillFlag = false;
							saveBData(v.getId());
							Intent i1007 = new Intent(context,
									SLNewsActivity.class);
							i1007.putExtra("CAT_ID", v.getId() + "");
							i1007.putExtra("NewsName", BottomClass
									.getCategoryName(bottomIconeList, v.getId()
											+ ""));
							i1007.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i1007);
							try {
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						case 1008:
							SnapLionMyAppActivity.KillFlag = false;
							saveBData(v.getId());
							Intent i1008 = new Intent(context,
									SLEventActivity.class);
							i1008.putExtra("CAT_ID", v.getId() + "");
							i1008.putExtra("NewsName", BottomClass
									.getCategoryName(bottomIconeList, v.getId()
											+ ""));
							i1008.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i1008);
							try {
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						case 1009:
							SnapLionMyAppActivity.KillFlag = false;
							saveBData(v.getId());
							Intent i1009 = new Intent(context,
									SLMailActivity.class);
							i1009.putExtra("MailName", BottomClass
									.getCategoryName(bottomIconeList, v.getId()
											+ ""));
							i1009.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i1009);
							try {
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						case 1010:
							SnapLionMyAppActivity.KillFlag = false;
							saveBData(v.getId());
							Intent i1010 = new Intent(context,
									FanwallWallActivity.class);
							i1010.putExtra("FanwallSectionName", BottomClass
									.getCategoryName(bottomIconeList, v.getId()
											+ ""));
							i1010.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i1010);
							break;
						case 1011:
							SnapLionMyAppActivity.KillFlag = false;
							saveBData(v.getId());
							Intent i1011 = new Intent(context,
									SLContactActivity.class);
							i1011.putExtra("ContactName", BottomClass
									.getCategoryName(bottomIconeList, v.getId()
											+ ""));
							i1011.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i1011);
							try {
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						case 1012:
							SnapLionMyAppActivity.KillFlag = false;
							saveBData(v.getId());
							Intent i1012 = new Intent(context,
									SLCopyRightActivity.class);
							i1012.putExtra("CopyRight", BottomClass
									.getCategoryName(bottomIconeList, v.getId()
											+ ""));
							i1012.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i1012);
							try {
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						case 1013:
							SnapLionMyAppActivity.KillFlag = false;
							if (Utility.isOnline(context)) {
								saveBData(v.getId());
								Intent i1013 = new Intent(context,
										SLTicketActivity.class);
								i1013.putExtra("TicketName", BottomClass
										.getCategoryName(bottomIconeList,
												v.getId() + ""));
								i1013.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
										| Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(i1013);
								try {
									System.gc();
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								Toast.makeText(context,
										"Network Fail.Please try later .!",
										Toast.LENGTH_LONG).show();
							}
							break;
						case 1014:
							SnapLionMyAppActivity.KillFlag = false;
							saveBData(v.getId());
							Intent i1014 = new Intent(context,
									SnapLionActivity.class);
							i1014.putExtra("Snaplion", BottomClass
									.getCategoryName(bottomIconeList, v.getId()
											+ ""));
							i1014.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i1014);
							try {
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						case 1017:
							SnapLionMyAppActivity.KillFlag = false;
							saveBData(v.getId());
							Intent i1017 = new Intent(context,
									MultibioCategoryListActivity.class);
							i1017.putExtra("MultibioCatName", BottomClass
									.getCategoryName(bottomIconeList, v.getId()
											+ ""));
							i1017.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i1017);
							try {
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						case 1018:
							SnapLionMyAppActivity.KillFlag = false;
							try {
								SLPhotoGalleryActivity1.albumGalleryDetails
										.clear();
							} catch (Exception e) {
								e.printStackTrace();
							}
							saveBData(v.getId());
							Intent i1018 = new Intent(context,
									MenusActivity.class);
							i1018.putExtra(
									"MenusName",
									getCategoryName(bottomIconeList, v.getId()
											+ ""));
							i1018.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i1018);
							try {
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							// Log.v(TAG, "click3");
							break;
						case 1019:
							SnapLionMyAppActivity.KillFlag = false;
							try {
								SLPhotoGalleryActivity1.albumGalleryDetails
										.clear();
							} catch (Exception e) {
								e.printStackTrace();
							}
							saveBData(v.getId());
							Intent i1019 = new Intent(context,
									PromoActivity.class);
							i1019.putExtra(
									"PromoName",
									getCategoryName(bottomIconeList, v.getId()
											+ ""));
							i1019.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i1019);
							try {
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							// Log.v(TAG, "click3");
							break;
						case 1020:
							SnapLionMyAppActivity.KillFlag = false;
							try {
								SLPhotoGalleryActivity1.albumGalleryDetails
										.clear();
							} catch (Exception e) {
								e.printStackTrace();
							}
							saveBData(v.getId());
							if (Utility.isOnline(context)) {
								Intent i1020 = new Intent(context,
										ScoreCardActivity.class);
								i1020.putExtra("ScoreCard", BottomClass
										.getCategoryName(bottomIconeList,
												v.getId() + ""));
								i1020.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
										| Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(i1020);
							} else {
								Toast.makeText(context,
										"Network Fail.Please try later .!",
										Toast.LENGTH_LONG).show();
							}
							try {
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							// Log.v(TAG, "click3");
							break;
						default:
							break;
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// bbLL.addView(bottomView);
		return bbLL;
	}

	private LinearLayout resetClickableState(LinearLayout bbLL) {
		for (int i = 0; i < bbLL.getChildCount(); i++) {
			bbLL.getChildAt(i).setClickable(true);
		}
		return bbLL;
	}

	public static boolean getBottomAvailable(Context context2, String appId2,
			String string) {
		context = context2;

		SharedPreferences myPref1 = context.getSharedPreferences("SLPref"
				+ appId2, Context.MODE_PRIVATE);
		String bottumStr = myPref1.getString("BottomTabData", "");
		selData = myPref1.getString("BRemData", "");
		try {
			InputStream in = new ByteArrayInputStream(
					bottumStr.getBytes("UTF-8"));
			try {
				bottomIconeList.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (in != null)
				bottomIconeList = getBottomDetails(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean flag = false;

		for (int i = 0; i < bottomIconeList.size(); i++) {
			if (bottomIconeList.get(i).getFeatureid().equalsIgnoreCase(string)
					&& i < 5) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public static String getCategoryName(ArrayList<Bottom> bottomIconeList2,
			String string) {
		String str = null;

		for (int i = 0; i < bottomIconeList2.size(); i++) {
			if (bottomIconeList2.get(i).getFeatureid().equalsIgnoreCase(string)) {
				str = bottomIconeList2.get(i).getFeaturename();
				break;
			}
		}
		return str.toUpperCase();
	}

	protected static void saveBData(int id) {
		SharedPreferences settings = context.getSharedPreferences("SLPref"
				+ appID, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("BRemData", id + "");
		editor.commit();
	}

	public static ArrayList<Bottom> getBottomDetails(InputStream in) {
		final int feature = 1;
		final int featureid = 2;
		final int featurename = 3;

		int tagName = 0;

		ArrayList<Bottom> values = new ArrayList<Bottom>();
		Bottom value = null;

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, HTTP.UTF_8);

			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("feature")) {
						value = new Bottom();
						tagName = feature;
					} else if (parser.getName().equals("featureid")) {
						tagName = featureid;
					} else if (parser.getName().equals("featurename")) {
						tagName = featurename;
					}
				}
				if (eventType == XmlPullParser.TEXT) {
					switch (tagName) {
					case feature:
						break;
					case featureid:
						value.setFeatureid(parser.getText());
						break;
					case featurename:
						value.setFeaturename(parser.getText());
						break;
					default:
						break;
					}
					tagName = 0;
				}
				if (eventType == XmlPullParser.END_TAG) {
					if (parser.getName().equals("feature")) {
						if (AppManager.getInstance().featureListProperties == null) {
							AppManager.getInstance().loadfeatureListProperty(
									context);
						}

						if (AppManager.getInstance().featureListProperties
								.getProperty(value.getFeatureid()) != null) {
							values.add(value);
						}
						value = null;
					}
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return values;
	}

	public static Bitmap getBitmapFromAsset(String strName) throws IOException {
		AssetManager assetManager = context.getAssets();
		InputStream istr = assetManager.open(strName);
		Bitmap bitmap = BitmapFactory.decodeStream(istr);
		return bitmap;
	}
}
