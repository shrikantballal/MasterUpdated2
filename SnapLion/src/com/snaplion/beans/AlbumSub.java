package com.snaplion.beans;

import android.graphics.Bitmap;

public class AlbumSub {
	public String albumid;
	public String albumname;
	public String photoid;
	public String photoname;
	public String smallpicture;
	public String bigpicture;
	public String comment;
	public Bitmap bitmapSmall;
	public Bitmap bitmapBig;

	public String getAlbumid() {
		return albumid;
	}

	public void setAlbumid(String albumid) {
		this.albumid = albumid;
	}

	public String getAlbumname() {
		return albumname;
	}

	public void setAlbumname(String albumname) {
		this.albumname = albumname;
	}

	public String getPhotoid() {
		return photoid;
	}

	public void setPhotoid(String photoid) {
		this.photoid = photoid;
	}

	public String getPhotoname() {
		return photoname;
	}

	public void setPhotoname(String photoname) {
		this.photoname = photoname;
	}

	public String getSmallpicture() {
		return smallpicture;
	}

	public void setSmallpicture(String smallpicture) {
		this.smallpicture = smallpicture;
	}

	public String getBigpicture() {
		return bigpicture;
	}

	public void setBigpicture(String bigpicture) {
		this.bigpicture = bigpicture;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Bitmap getBitmapSmall() {
		return bitmapSmall;
	}

	public void setBitmapSmall(Bitmap bitmapSmall) {
		this.bitmapSmall = bitmapSmall;
	}

	public Bitmap getBitmapBig() {
		return bitmapBig;
	}

	public void setBitmapBig(Bitmap bitmapBig) {
		this.bitmapBig = bitmapBig;
	}

}
