package com.snaplion.beans;

import android.graphics.Bitmap;

public class Video {
	public String title;
	public String url;
	public String message;
	public Bitmap bitmapSnap;
	public String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Bitmap getBitmapSnap() {
		return bitmapSnap;
	}

	public void setBitmapSnap(Bitmap bitmapSnap) {
		this.bitmapSnap = bitmapSnap;
	}

}
