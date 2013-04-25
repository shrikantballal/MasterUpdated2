package it.sephiroth.android.library.imagezoom;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ImageViewTouch extends ImageViewTouchBase 
{

	static final float MIN_ZOOM = 0.9f;
	protected ScaleGestureDetector mScaleDetector;
	protected GestureDetector mGestureDetector;
	protected int mTouchSlop;
	protected float mCurrentScaleFactor;
	protected float mScaleFactor;
	protected int mDoubleTapDirection;
	protected OnGestureListener mGestureListener;
	protected OnScaleGestureListener mScaleListener;
	protected boolean mDoubleTapEnabled = true;
	protected boolean mScaleEnabled = true;
	protected boolean mScrollEnabled = true;

	public ImageViewTouch(Context context) 
	{
		super( context);
	}
	public ImageViewTouch( Context context, AttributeSet attrs ) {
		super( context, attrs );
	}
	
	LinearLayout photo_share_layout;
	LinearLayout gallery_tob_bar;
	public ImageViewTouch(Context context, LinearLayout photo_share_layout, LinearLayout gallery_tob_bar) 
	{
		super( context);
		this.photo_share_layout=photo_share_layout;
		this.gallery_tob_bar=gallery_tob_bar;
	}
	@Override
	protected void init() {
		super.init();
		mTouchSlop = ViewConfiguration.getTouchSlop();
		mGestureListener = getGestureListener();
		mScaleListener = getScaleListener();

		mScaleDetector = new ScaleGestureDetector( getContext(), mScaleListener );
		mGestureDetector = new GestureDetector( getContext(), mGestureListener, null, true );

		mCurrentScaleFactor = 1f;
		mDoubleTapDirection = 1;
	}

	public void setDoubleTapEnabled( boolean value ) {
		mDoubleTapEnabled = value;
	}

	public void setScaleEnabled( boolean value ) {
		mScaleEnabled = value;
	}

	public void setScrollEnabled( boolean value ) {
		mScrollEnabled = value;
	}

	public boolean getDoubleTapEnabled() {
		return mDoubleTapEnabled;
	}

	public OnGestureListener getGestureListener() {
		return new GestureListener();
	}

	protected OnScaleGestureListener getScaleListener() {
		return new ScaleListener();
	}

	@Override
	protected void onBitmapChanged( Drawable drawable ) {
		super.onBitmapChanged( drawable );

		float v[] = new float[9];
		mSuppMatrix.getValues( v );
		mCurrentScaleFactor = v[Matrix.MSCALE_X];
	}

	@Override
	protected void _setImageDrawable( final Drawable drawable, final boolean reset, final Matrix initial_matrix, final float maxZoom ) {
		super._setImageDrawable( drawable, reset, initial_matrix, maxZoom );
		mScaleFactor = getMaxZoom() / 3;
	}

	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		mScaleDetector.onTouchEvent( event );
		if ( !mScaleDetector.isInProgress() )
		{
			boolean res = mGestureDetector.onTouchEvent( event );
		}
		int action = event.getAction();
		switch ( action & MotionEvent.ACTION_MASK ) {
			case MotionEvent.ACTION_UP:
				if ( getScale() < 1f ) {
					zoomTo( 1f, 50 );
				}
				break;
		}
		return true;
	}

	@Override
	protected void onZoom( float scale ) {
		super.onZoom( scale );
		if ( !mScaleDetector.isInProgress() ) mCurrentScaleFactor = scale;
	}

	protected float onDoubleTapPost( float scale, float maxZoom ) 
	{
		if ( mDoubleTapDirection == 1 ) 
		{
			/*if ( ( scale + ( mScaleFactor * 2 ) ) <= maxZoom ) 
			{
				return scale + mScaleFactor;
			} 
			else 
			{*/
				mDoubleTapDirection = -1;
				return maxZoom;
			//}
			
		} 
		else 
		{
			mDoubleTapDirection = 1;
			return 1f;
		}
	}
	public boolean goFlag=false;
	public class GestureListener extends GestureDetector.SimpleOnGestureListener 
	{
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) 
		{
			if(photo_share_layout!=null)
			{
				Log.i( LOG_TAG, "onSingleTapConfirmed.");
				if(photo_share_layout.getVisibility()==LinearLayout.VISIBLE)
				{
					photo_share_layout.setVisibility(LinearLayout.GONE);
					gallery_tob_bar.setVisibility(RelativeLayout.GONE);
				}
				else
				{
					photo_share_layout.setVisibility(LinearLayout.VISIBLE);
					gallery_tob_bar.setVisibility(RelativeLayout.VISIBLE);
				}
			}
			return super.onSingleTapConfirmed(e);
		}
		@Override
		public boolean onDoubleTap( MotionEvent e ) 
		{
			if ( mDoubleTapEnabled ) 
			{
				float scale = getScale();
				float targetScale = scale;
				targetScale = onDoubleTapPost( scale, getMaxZoom() );
				targetScale = Math.min( getMaxZoom(), Math.max( targetScale, MIN_ZOOM ) );
				mCurrentScaleFactor = targetScale;
				zoomTo( targetScale, e.getX(), e.getY(), 200 );
				invalidate();
			}
			return super.onDoubleTap( e );
		}

		@Override
		public void onLongPress( MotionEvent e ) {
			if ( isLongClickable() ) {
				if ( !mScaleDetector.isInProgress() ) {
					setPressed( true );
					performLongClick();
				}
			}
		}
		
		@Override
		public boolean onScroll( MotionEvent e1, MotionEvent e2, float distanceX, float distanceY ) 
		{
			if ( !mScrollEnabled ) 
			{
				System.out.println("ZOOM: !mScrollEnabled");
				return false;
			}
			if ( e1 == null || e2 == null )
			{
				System.out.println("ZOOM: e1 == null || e2 == null");
				return false;
			}
			if ( e1.getPointerCount() > 1 || e2.getPointerCount() > 1 )
			{
				System.out.println("ZOOM: e1.getPointerCount() > 1 || e2.getPointerCount() > 1");
				return false;
			}
			if ( mScaleDetector.isInProgress() )
			{
				System.out.println("ZOOM: mScaleDetector.isInProgress()");
				return false;
			}
			if ( getScale() == 1f )
			{
				System.out.println("ZOOM: getScale() == 1f");
				return false;
			}
			scrollBy( -distanceX, -distanceY );
			invalidate();
			boolean flag =  super.onScroll( e1, e2, distanceX, distanceY );
			return flag;
		}

		@Override
		public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY ) {
			if ( !mScrollEnabled ) return false;

			if ( e1.getPointerCount() > 1 || e2.getPointerCount() > 1 ) return false;
			if ( mScaleDetector.isInProgress() ) return false;

			float diffX = e2.getX() - e1.getX();
			float diffY = e2.getY() - e1.getY();

			if ( Math.abs( velocityX ) > 800 || Math.abs( velocityY ) > 800 ) {
				scrollBy( diffX / 2, diffY / 2, 300 );
				invalidate();
			}
			return super.onFling( e1, e2, velocityX, velocityY );
		}
	}

	public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@SuppressWarnings("unused")
		@Override
		public boolean onScale( ScaleGestureDetector detector ) {
			float span = detector.getCurrentSpan() - detector.getPreviousSpan();
			float targetScale = mCurrentScaleFactor * detector.getScaleFactor();
			if ( mScaleEnabled ) {
				targetScale = Math.min( getMaxZoom(), Math.max( targetScale, MIN_ZOOM ) );
				zoomTo( targetScale, detector.getFocusX(), detector.getFocusY() );
				mCurrentScaleFactor = Math.min( getMaxZoom(), Math.max( targetScale, MIN_ZOOM ) );
				mDoubleTapDirection = 1;
				invalidate();
				return true;
			}
			return false;
		}
	}
}
