package com.snaplion.beans;

import android.graphics.Bitmap;

public class AppDetail {
	public String appid;
	public String picture;
	public String picture2x;
	public String picture512;
	public String appname;
	public String getPicture2x() {
		return picture2x;
	}

	public void setPicture2x(String picture2x) {
		this.picture2x = picture2x;
	}

	public String getPicture512() {
		return picture512;
	}

	public void setPicture512(String picture512) {
		this.picture512 = picture512;
	}

	public Bitmap bitmap;

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}
}
