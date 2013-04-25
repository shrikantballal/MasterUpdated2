package com.snaplion.menus;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class TouchInterceptViewPager extends ViewPager// implements OnImageScrollCompleteListener
{
	public TouchInterceptViewPager(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
	}
	public TouchInterceptViewPager(Context context) 
	{
		super(context);
	}
	
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) 
//	{
////		if ( bitmapRect.left + scrollRect.left >= 0 )//left scroll end
////		{
////			scrollRect.left = (int) ( 0 - bitmapRect.left );
////			System.out.println("5");
////			onImageScrollCompleteListener.scrollComplete(true);
////		}
////		if ( bitmapRect.right + scrollRect.left <= ( width - 0 ) )//right scroll end
////		{
////			scrollRect.left = (int) ( ( width - 0 ) - bitmapRect.right );
////			System.out.println("6");
////			onImageScrollCompleteListener.scrollComplete(true);
////		}
//		
//		
//		
//		
//		//System.out.println("ZOOM: "+ev.getX());
//		super.onInterceptTouchEvent(ev);
//		System.out.println("goFlag :"+goFlag);
//		return goFlag;
//	}
//	public boolean goFlag=false; 
//	@Override
//	public void scrollComplete(boolean flag) 
//	{
//		goFlag=flag;
//	}
	
}
