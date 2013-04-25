/**
 * 
 */
package com.snaplion.custom;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author "Imran Ali"
 *
 */
public class TextViewMarquee extends TextView 
{
	/**
	 * @param context
	 */
	public TextViewMarquee(Context context) {
		super(context);
	}

/**
	 * @param context
	 * @param attrs
	 */
	public TextViewMarquee(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public TextViewMarquee(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	

	/* (non-Javadoc)
	 * @see android.widget.TextView#onFocusChanged(boolean, int, android.graphics.Rect)
	 */
	@Override
	protected void onFocusChanged(boolean focused, int direction,Rect previouslyFocusedRect) 
	{
		if(focused)
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}

	/* (non-Javadoc)
	 * @see android.widget.TextView#onWindowFocusChanged(boolean)
	 */
	@Override
	public void onWindowFocusChanged(boolean foucus) {
		if(foucus)
		super.onWindowFocusChanged(foucus);
	}

	/* (non-Javadoc)
	 * @see android.view.View#isFocused()
	 */
	@Override
	public boolean isFocused() {
		return true;
	}
	

}
