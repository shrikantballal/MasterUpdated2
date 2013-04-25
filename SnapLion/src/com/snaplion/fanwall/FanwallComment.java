package com.snaplion.fanwall;

import android.os.Parcel;
import android.os.Parcelable;

public class FanwallComment implements Parcelable
{
	private int index;
	private String parentID;
	
	private String post_id;
	private String user_id;
	private String user_name;
	private String user_photo_url;
	private String message;
	
	private String message_photo_url;
	private String date_created;
	private String likes_total;
	private String liked_by;
	private String liked_by_me;
	
	private String total_comments;
	private String message_thumb_url;
	private String views;
	private String time_elapsed;
	private String photos140;
	
//	private Bitmap userPhoto;
//	private Bitmap messagePhoto;
	
	public String getPhotos140() {
		return photos140;
	}
	public void setPhotos140(String photos140) {
		this.photos140 = photos140;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getParentID() {
		return parentID;
	}
	public void setParentID(String parentID) {
		this.parentID = parentID;
	}
	public String getViews() {
		return views;
	}
	public void setViews(String views) {
		this.views = views;
	}
	public String getTime_elapsed() {
		return time_elapsed;
	}
	public void setTime_elapsed(String time_elapsed) {
		this.time_elapsed = time_elapsed;
	}
	public String getMessage_thumb_url() {
		return message_thumb_url;
	}
	public void setMessage_thumb_url(String message_thumb_url) {
		this.message_thumb_url = message_thumb_url;
	}
//	public Bitmap getUserPhoto() {
//		return userPhoto;
//	}
//	public void setUserPhoto(Bitmap userPhoto) {
//		this.userPhoto = userPhoto;
//	}
//	public Bitmap getMessagePhoto() {
//		return messagePhoto;
//	}
//	public void setMessagePhoto(Bitmap messagePhoto) {
//		this.messagePhoto = messagePhoto;
//	}
	public String getPost_id() 
	{
		return post_id;
	}
	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_photo_url() {
		return user_photo_url;
	}
	public void setUser_photo_url(String user_photo_url) {
		this.user_photo_url = user_photo_url;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage_photo_url() {
		return message_photo_url;
	}
	public void setMessage_photo_url(String message_photo_url) {
		this.message_photo_url = message_photo_url;
	}
	public String getDate_created() {
		return date_created;
	}
	public void setDate_created(String date_created) {
		this.date_created = date_created;
	}
	public String getLikes_total() {
		return likes_total;
	}
	public void setLikes_total(String likes_total) {
		this.likes_total = likes_total;
	}
	public String getLiked_by() {
		return liked_by;
	}
	public void setLiked_by(String liked_by) {
		this.liked_by = liked_by;
	}
	public String getLiked_by_me() {
		return liked_by_me;
	}
	public void setLiked_by_me(String liked_by_me) {
		this.liked_by_me = liked_by_me;
	}
	public String getTotal_comments() {
		return total_comments;
	}
	public void setTotal_comments(String total_comments) {
		this.total_comments = total_comments;
	}
	
	///////////////////////create parcel/////////////////////
	public FanwallComment(){}
	public FanwallComment(Parcel in)
	{
		index = in.readInt();
		parentID = in.readString();
		post_id = in.readString();
		user_id = in.readString();
		user_name = in.readString();
		user_photo_url = in.readString();
		message = in.readString();
		message_photo_url = in.readString();
		date_created = in.readString();
		likes_total = in.readString();
		liked_by = in.readString();
		liked_by_me = in.readString();
		total_comments = in.readString();
		message_thumb_url = in.readString();
		views = in.readString();
		time_elapsed = in.readString();
		photos140 = in.readString();
//		userPhoto = in.readParcelable(Bitmap.class.getClassLoader());
//		messagePhoto = in.readParcelable(Bitmap.class.getClassLoader());
	}
	@Override
	public int describeContents() 
	{
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeInt(index);
		dest.writeString(parentID);
		dest.writeString(post_id);
		dest.writeString(user_id);
		dest.writeString(user_name);
		dest.writeString(user_photo_url);
		dest.writeString(message);
		dest.writeString(message_photo_url);
		dest.writeString(date_created);
		dest.writeString(likes_total);
		dest.writeString(liked_by);
		dest.writeString(liked_by_me);
		dest.writeString(total_comments);
		dest.writeString(message_thumb_url);
		dest.writeString(views);
		dest.writeString(time_elapsed);
		dest.writeString(photos140);
//		dest.writeParcelable(userPhoto, flags);
//		dest.writeParcelable(messagePhoto, flags);
	}
	public static final Parcelable.Creator<FanwallComment> CREATOR = new Parcelable.Creator<FanwallComment>() 
	{
		public FanwallComment createFromParcel(Parcel in) 
		{
			return new FanwallComment(in);
		}
	
		public FanwallComment[] newArray (int size) 
		{
			return new FanwallComment[size];
		}
	};
}