package com.snaplion.beans;

import java.util.Date;

import android.graphics.Bitmap;

public class News {
	public String newsid;
	public String title;
	public String content;
	public String image;
	public String eventdate;
	public String caldate;
	public String type;
	public String imagename;
	public String month;
	public String year;
	public String showurl;
	public String img1;
	public Bitmap bitmap;
	public String starttime;
	public String endtime;
	public String city;
	public String country;
	public String venuedetails;
	public String eventdateformat2;
    public Date hireDate;

	
	
	public Date getHireDate() {
		return hireDate;
	}
	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}
	public String getEventdateformat2() {
		return eventdateformat2;
	}
	public void setEventdateformat2(String eventdateformat2) {
		this.eventdateformat2 = eventdateformat2;
	}
	public String getCaldateformat2() {
		return caldateformat2;
	}
	public void setCaldateformat2(String caldateformat2) {
		this.caldateformat2 = caldateformat2;
	}
	public String caldateformat2;

	public String getVenuedetails() {
		return venuedetails;
	}
	public void setVenuedetails(String venuedetails) {
		this.venuedetails = venuedetails;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getImg1() {
		return img1;
	}
	public void setImg1(String img1) {
		this.img1 = img1;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public String getNewsid() {
		return newsid;
	}
	public void setNewsid(String newsid) {
		this.newsid = newsid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getEventdate() {
		return eventdate;
	}
	public void setEventdate(String eventdate) {
		this.eventdate = eventdate;
	}
	public String getCaldate() {
		return caldate;
	}
	public void setCaldate(String caldate) {
		this.caldate = caldate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getImagename() {
		return imagename;
	}
	public void setImagename(String imagename) {
		this.imagename = imagename;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getShowurl() {
		return showurl;
	}
	public void setShowurl(String showurl) {
		this.showurl = showurl;
	}
	
//	public class CompDate implements Comparator<News> 
//	{
//		private int mod = 1;
//	    public CompDate(boolean desc) 
//	    {
//	          if (desc) mod =-1;
//	    }
//	   @Override
//	   public int compare(News arg0, News arg1) 
//	   {
//	          return mod*arg0.hireDate.compareTo(arg1.hireDate);
//	   }
//	}
}
