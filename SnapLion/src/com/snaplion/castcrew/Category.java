package com.snaplion.castcrew;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable
{
	String categoryId;
	String categoryName;
	ArrayList<MultibioItem> players;
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public ArrayList<MultibioItem> getPlayers() {
		return players;
	}
	public void setPlayers(ArrayList<MultibioItem> players) {
		this.players = players;
	}
	
	public Category()
	{
		players=new ArrayList<MultibioItem>();
	}
	public Category(Parcel in)
	{
		this();
		categoryId = in.readString();
		categoryName = in.readString();
		in.readTypedList(players, MultibioItem.CREATOR);
	}
	@Override
	public int describeContents() 
	{
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeString(categoryId);
		dest.writeString(categoryName);
		dest.writeTypedList(players);
	}
	public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() 
	{
		public Category createFromParcel(Parcel in) 
		{
			return new Category(in);
		}
	
		public Category[] newArray (int size) 
		{
			return new Category[size];
		}
	};
}
