package com.snaplion.fanwall.photo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

import com.snaplion.photos.util.TouchImageView;
public class PhotoSwipeInterceptingGallery extends Gallery
{
	//private int currenIndex = 0;
	private String TAG="SwipeInterceptingGallery";
	
	@Override
	public void setSelection(int position) 
	{
		// SLPhotoGalleryActivity1.setSelimageOfGallery();
		super.setSelection(position);
	}

	/** 
     * The distance the user has to move their finger, in density independent
     * pixels, before we count the motion as A) intended for the ScrollView if
     * the motion is in the vertical direction or B) intended for ourselfs, if
     * the motion is in the horizontal direction - after the user has moved this
     * amount they are "locked" into this direction until the next ACTION_DOWN
     * event
     */
    private static final int DRAG_BOUNDS_IN_DP = 20;
	//private static final int DRAG_BOUNDS_IN_DP = 0;
		

    /**
     * A value representing the "unlocked" state - we test all MotionEvents
     * when in this state to see whether a lock should be make
     */
	
    private static final int SCROLL_LOCK_NONE = 0;

    /**
     * A value representing a lock in the vertical direction - once in this state
     * we will never redirect MotionEvents from the ScrollView to ourself
     */
    private static final int SCROLL_LOCK_VERTICAL = 1;

    /**
     * A value representing a lock in the horizontal direction - once in this
     * state we will not deliver any more MotionEvents to the ScrollView, and
     * will deliver them to ourselves instead.
     */
    private static final int SCROLL_LOCK_HORIZONTAL = 2;
    
    /**
     * The drag bounds in density independent pixels converted to actual pixels
     */
    private int mDragBoundsInPx = 0;
    

    /**
     * The coordinates of the intercepted ACTION_DOWN event
     */
    private float mTouchStartX;
    private float mTouchStartY;

    /**
     * The current scroll lock state
     */
    private int mScrollLock = SCROLL_LOCK_NONE;

    public PhotoSwipeInterceptingGallery(Context context) {
        super(context);
        initCustomGallery(context);
    }

    public PhotoSwipeInterceptingGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCustomGallery(context);
    }

    public PhotoSwipeInterceptingGallery(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        initCustomGallery(context);
    }

    private void initCustomGallery(Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        mDragBoundsInPx = (int) (scale*DRAG_BOUNDS_IN_DP + 0.5f);
    }

    /**
     * This will be called before the intercepted views onTouchEvent is called
     * Return false to keep intercepting and passing the event on to the target view
     * Return true and the target view will recieve ACTION_CANCEL, and the rest of the
     * events will be delivered to our onTouchEvent
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) 
    {
    	//ImageViewTouch myImageView = (ImageViewTouch) super.getSelectedView();
    	TouchImageView myImageView = (TouchImageView) super.getSelectedView();
//    	RectF rect = myImageView.getBitmapRect();
//    	Utility.debug("GALLERY=rect.width():"+rect.width());
//    	Utility.debug("GALLERY=rect.height():"+rect.height());
    	//mDragBoundsInPx = (int) (myImageView.getScale()*DRAG_BOUNDS_IN_DP + 0.5f);
        final int action = ev.getAction();
        switch (action) 
        {
	        case MotionEvent.ACTION_DOWN:
	        {
	            mTouchStartX = ev.getX();
	            mTouchStartY = ev.getY();
	            mScrollLock = SCROLL_LOCK_NONE;
	
	            /**
	             * Deliver the down event to the Gallery to avoid jerky scrolling
	             * if we decide to redirect the ScrollView events to ourself
	             */
	            super.onTouchEvent(ev);
	            break;
	        }
	        case MotionEvent.ACTION_MOVE:
	        {
	            if (mScrollLock == SCROLL_LOCK_VERTICAL) 
	            {
	                // keep returning false to pass the events
	                // onto the ScrollView
	                return false;
	            }
	
	            final float touchDistanceX = (ev.getX() - mTouchStartX);
	            final float touchDistanceY = (ev.getY() - mTouchStartY);
	
	            if (Math.abs(touchDistanceY) > mDragBoundsInPx) 
	            {
	                mScrollLock = SCROLL_LOCK_VERTICAL;
	                return false;
	            }
	            
	            if (Math.abs(touchDistanceX) > mDragBoundsInPx)
	            {
	                mScrollLock = SCROLL_LOCK_HORIZONTAL; // gallery action
	                return true; // redirect MotionEvents to ourself
	            }
	            return false;
	            //break;
	        }
	        case MotionEvent.ACTION_CANCEL:
	        case MotionEvent.ACTION_UP:
	            // if we're still intercepting at this stage, make sure the gallery
	            // also recieves the up/cancel event as we gave it the down event earlier
	            super.onTouchEvent(ev);
	            break;
        }

        return false;
    }
    
    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2){ 
        return e2.getX() > e1.getX(); 
     }

//	@Override
//	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
//	{
//		int kEvent;
//		if(isScrollingLeft(e1, e2))
//		{ //Check if scrolling left
//			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
//			PhotoAlbumGalleryImagePreview.setSelimageOfGallery(0);
//			Log.d(TAG, "Is Scrolling letf");
//		}
//		else
//		{ //Otherwise scrolling right
//			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
//			PhotoAlbumGalleryImagePreview.setSelimageOfGallery(1);
//			Log.d(TAG, "Is Scrolling right");
//		}
//		onKeyDown(kEvent, null);
//		return true;  
//	}
}

//public class SwipeInterceptingGallery extends Gallery 
//{
//	//private int currenIndex = 0;
//	private String TAG="SwipeInterceptingGallery";
//	
//	@Override
//	public void setSelection(int position) {
//		// SLPhotoGalleryActivity1.setSelimageOfGallery();
//		super.setSelection(position);
//	}
//
//	/** 
//     * The distance the user has to move their finger, in density independent
//     * pixels, before we count the motion as A) intended for the ScrollView if
//     * the motion is in the vertical direction or B) intended for ourselfs, if
//     * the motion is in the horizontal direction - after the user has moved this
//     * amount they are "locked" into this direction until the next ACTION_DOWN
//     * event
//     */
//    private static final int DRAG_BOUNDS_IN_DP = 20;
//
//    /**
//     * A value representing the "unlocked" state - we test all MotionEvents
//     * when in this state to see whether a lock should be make
//     */
//    private static final int SCROLL_LOCK_NONE = 0;
//
//    /**
//     * A value representing a lock in the vertical direction - once in this state
//     * we will never redirect MotionEvents from the ScrollView to ourself
//     */
//    private static final int SCROLL_LOCK_VERTICAL = 1;
//
//    /**
//     * A value representing a lock in the horizontal direction - once in this
//     * state we will not deliver any more MotionEvents to the ScrollView, and
//     * will deliver them to ourselves instead.
//     */
//    private static final int SCROLL_LOCK_HORIZONTAL = 2;
//
//    /**
//     * The drag bounds in density independent pixels converted to actual pixels
//     */
//    private int mDragBoundsInPx = 0;
//
//    /**
//     * The coordinates of the intercepted ACTION_DOWN event
//     */
//    private float mTouchStartX;
//    private float mTouchStartY;
//
//    /**
//     * The current scroll lock state
//     */
//    private int mScrollLock = SCROLL_LOCK_NONE;
//
//    public SwipeInterceptingGallery(Context context) {
//        super(context);
//        initCustomGallery(context);
//    }
//
//    public SwipeInterceptingGallery(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        initCustomGallery(context);
//    }
//
//    public SwipeInterceptingGallery(Context context, AttributeSet attrs,
//            int defStyle) {
//        super(context, attrs, defStyle);
//        initCustomGallery(context);
//    }
//
//    private void initCustomGallery(Context context) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        mDragBoundsInPx = (int) (scale*DRAG_BOUNDS_IN_DP + 0.5f);
//    }
//
//    /**
//     * This will be called before the intercepted views onTouchEvent is called
//     * Return false to keep intercepting and passing the event on to the target view
//     * Return true and the target view will recieve ACTION_CANCEL, and the rest of the
//     * events will be delivered to our onTouchEvent
//     */
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        final int action = ev.getAction();
//        switch (action) {
//        case MotionEvent.ACTION_DOWN:
//            mTouchStartX = ev.getX();
//            mTouchStartY = ev.getY();
//            mScrollLock = SCROLL_LOCK_NONE;
//
//            /**
//             * Deliver the down event to the Gallery to avoid jerky scrolling
//             * if we decide to redirect the ScrollView events to ourself
//             */
//            super.onTouchEvent(ev);
//            break;
//
//        case MotionEvent.ACTION_MOVE:
//            if (mScrollLock == SCROLL_LOCK_VERTICAL) {
//                // keep returning false to pass the events
//                // onto the ScrollView
//                return false;
//            }
//
//            final float touchDistanceX = (ev.getX() - mTouchStartX);
//            final float touchDistanceY = (ev.getY() - mTouchStartY);
//
//            if (Math.abs(touchDistanceY) > mDragBoundsInPx) {
//                mScrollLock = SCROLL_LOCK_VERTICAL;
//                return false;
//            }
//            if (Math.abs(touchDistanceX) > mDragBoundsInPx) {
//                mScrollLock = SCROLL_LOCK_HORIZONTAL; // gallery action
//                return true; // redirect MotionEvents to ourself
//            }
//            break;
//        case MotionEvent.ACTION_CANCEL:
//        case MotionEvent.ACTION_UP:
//            // if we're still intercepting at this stage, make sure the gallery
//            // also recieves the up/cancel event as we gave it the down event earlier
//            super.onTouchEvent(ev);
//            break;
//        }
//
//        return false;
//    }
//    
//    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2){ 
//        return e2.getX() > e1.getX(); 
//     }
//
//	@Override
//	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
//		int kEvent;
//		
//		if(isScrollingLeft(e1, e2)){ //Check if scrolling left
//			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
//			SLPhotoGalleryActivity1.setSelimageOfGallery(0);
//			Log.d(TAG, "Is Scrolling letf");
//		}
//		else{ //Otherwise scrolling right
//			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
//			SLPhotoGalleryActivity1.setSelimageOfGallery(1);
//			Log.d(TAG, "Is Scrolling right");
//		}
//		onKeyDown(kEvent, null);
//   return true;  
// }
//}
