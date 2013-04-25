package com.snaplion.beans;

import android.graphics.Bitmap;

public class Album {
	public String categaryid;
	public String categoryname;
	public String picture;
	public Bitmap bitmap;
	public String ModifiedDate;

	public String getCategaryid() {
		return categaryid;
	}

	public void setCategaryid(String categaryid) {
		this.categaryid = categaryid;
	}

	public String getCategoryname() {
		return categoryname;
	}

	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public String getModifiedDate() {
		return ModifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		ModifiedDate = modifiedDate;
	}
}
