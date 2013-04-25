package com.snaplion.beans;

import android.graphics.Bitmap;

public class AlbumSong {

	public String trackname;
	public String url;
	public String buyit;
	public String buyurl;
	public String noofplay;
	public String duration;
	public String noofcomment;
	public String comment;
	public String source;
	public String albumname;
	public String enableDownload;

	public String getEnableDownload() {
		return enableDownload;
	}

	public void setEnableDownload(String enableDownload) {
		this.enableDownload = enableDownload;
	}

	public String getDownload_url() {
		return download_url;
	}

	public void setDownload_url(String downloadUrl) {
		download_url = downloadUrl;
	}

	public String download_url;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAlbumname() {
		return albumname;
	}

	public void setAlbumname(String albumname) {
		this.albumname = albumname;
	}

	public String getTrackname() {
		return trackname;
	}

	public void setTrackname(String trackname) {
		this.trackname = trackname;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBuyit() {
		return buyit;
	}

	public void setBuyit(String buyit) {
		this.buyit = buyit;
	}

	public String getBuyurl() {
		return buyurl;
	}

	public void setBuyurl(String buyurl) {
		this.buyurl = buyurl;
	}

	public String getNoofplay() {
		return noofplay;
	}

	public void setNoofplay(String noofplay) {
		this.noofplay = noofplay;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getNoofcomment() {
		return noofcomment;
	}

	public void setNoofcomment(String noofcomment) {
		this.noofcomment = noofcomment;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public Bitmap bitmap;
}
