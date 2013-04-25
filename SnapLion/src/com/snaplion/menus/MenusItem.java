package com.snaplion.menus;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class MenusItem implements Parcelable
{
	private String categaryid;
	private String categoryname;
	private String ModifiedDate;
	private String albumimage;
	
	private ArrayList<MenusItem> images;
	
	private String photoid;
	private String photoname;
	private String smallpicture;
	private String bigpicture;
	private String comment;
	
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
	public String getModifiedDate() {
		return ModifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		ModifiedDate = modifiedDate;
	}
	public String getAlbumimage() {
		return albumimage;
	}
	public void setAlbumimage(String albumimage) {
		this.albumimage = albumimage;
	}
	public ArrayList<MenusItem> getImages() {
		return images;
	}
	public void setImages(ArrayList<MenusItem> images) {
		this.images = images;
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
	
	
	
	
	public MenusItem(){}
	public MenusItem(Parcel in)
	{
		categaryid = in.readString();
		categoryname = in.readString();
		ModifiedDate = in.readString();
		albumimage = in.readString();
		
		photoid = in.readString();
		photoname = in.readString();
		smallpicture = in.readString();
		bigpicture = in.readString();
		comment = in.readString();
	}
	@Override
	public int describeContents() 
	{
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeString(categaryid);
		dest.writeString(categoryname);
		dest.writeString(ModifiedDate);
		dest.writeString(albumimage);
		dest.writeString(photoid);
		dest.writeString(photoname);
		dest.writeString(smallpicture);
		dest.writeString(bigpicture);
		dest.writeString(comment);
	}
	public static final Parcelable.Creator<MenusItem> CREATOR = new Parcelable.Creator<MenusItem>() 
	{
		public MenusItem createFromParcel(Parcel in) 
		{
			return new MenusItem(in);
		}
	
		public MenusItem[] newArray (int size) 
		{
			return new MenusItem[size];
		}
	};
}
