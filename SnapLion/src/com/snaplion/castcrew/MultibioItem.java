package com.snaplion.castcrew;

import android.os.Parcel;
import android.os.Parcelable;

public class MultibioItem implements Parcelable 
{
	String id;
	String name;
	String role;
	String photo;
	String thumb;
	String bio;
	String created;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	//////////////////////////////////////////////////////
	public MultibioItem(){}
	public MultibioItem(Parcel in)
	{
		id = in.readString();
		name = in.readString();
		role = in.readString();
		photo = in.readString();
		thumb = in.readString();
		bio = in.readString();
		created = in.readString();
	}
	@Override
	public int describeContents() 
	{
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(role);
		dest.writeString(photo);
		dest.writeString(thumb);
		dest.writeString(bio);
		dest.writeString(created);
	}
	public static final Parcelable.Creator<MultibioItem> CREATOR = new Parcelable.Creator<MultibioItem>() 
	{
		public MultibioItem createFromParcel(Parcel in) 
		{
			return new MultibioItem(in);
		}
	
		public MultibioItem[] newArray (int size) 
		{
			return new MultibioItem[size];
		}
	};
}
