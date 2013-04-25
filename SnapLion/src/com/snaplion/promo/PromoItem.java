package com.snaplion.promo;

import android.os.Parcel;
import android.os.Parcelable;

public class PromoItem  implements Parcelable
{
	private String id;
	private String headline;
	private String description;
	private String url;
	private String image_thumb;
	private String image_thumb_big;
	private String image_big;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getHeadline() {
		return headline;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImage_thumb() {
		return image_thumb;
	}
	public void setImage_thumb(String image_thumb) {
		this.image_thumb = image_thumb;
	}
	public String getImage_thumb_big() {
		return image_thumb_big;
	}
	public void setImage_thumb_big(String image_thumb_big) {
		this.image_thumb_big = image_thumb_big;
	}
	public String getImage_big() {
		return image_big;
	}
	public void setImage_big(String image_big) {
		this.image_big = image_big;
	}
	
	public PromoItem(){}
	public PromoItem(Parcel in)
	{
		id = in.readString();
		headline = in.readString();
		description = in.readString();
		url = in.readString();
		image_thumb = in.readString();
		image_thumb_big = in.readString();
		image_big = in.readString();
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
		dest.writeString(headline);
		dest.writeString(description);
		dest.writeString(url);
		dest.writeString(image_thumb);
		dest.writeString(image_thumb_big);
		dest.writeString(image_big);
	}
	public static final Parcelable.Creator<PromoItem> CREATOR = new Parcelable.Creator<PromoItem>() 
	{
		public PromoItem createFromParcel(Parcel in) 
		{
			return new PromoItem(in);
		}
	
		public PromoItem[] newArray (int size) 
		{
			return new PromoItem[size];
		}
	};
}
