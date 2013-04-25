package com.snaplion.fanwall;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.snaplion.kingsxi.R;
import com.snaplion.util.AppManager;

public class CustomLikeButton extends ImageView
{
	Context context;
	private int window_height;
	private int window_width;
	public Handler uiHandler;
	
	String num;
	int textColorId;
	AppManager appManager;
	
	public void setTotalComments(String num, int textColorId)
	{
		this.num=num;
		this.textColorId=textColorId;
	}
	public CustomLikeButton(Context context) 
	{
		super(context);
		this.context=context;
		loadResources();
	}
	public CustomLikeButton(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		this.context=context;
		loadResources();
	}
	public CustomLikeButton(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		this.context=context;
		loadResources();
	}
	public void loadResources()
	{
		appManager=AppManager.getInstance();
		setHandler();
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
		super.onSizeChanged(w, h, oldw, oldh);
		ViewGroup.LayoutParams params=this.getLayoutParams();
		window_height=params.height;
		window_width=params.width;
	}
	@Override
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		
		Paint numColor=new Paint();
		numColor.setColor(getResources().getColor(textColorId));
		numColor.setStyle(Paint.Style.FILL);
		
		int scaledSize = getResources().getDimensionPixelSize(R.dimen.fanwall_bubble_thumb_text_size);
		
		numColor.setTextSize(scaledSize);
		numColor.setTextAlign(Paint.Align.CENTER);
		numColor.setTypeface(Typeface.DEFAULT_BOLD);
		
		//numColor.setTypeface(appManager.arial_regular);
		//canvas.drawText(num,getWidth()/2, (getHeight()/2)+14, numColor);
		canvas.drawText(num,getWidth()/2, (getHeight()/2)+(getHeight()/2.5f), numColor);
  	}
	
	private void setHandler()
	{
		uiHandler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				invalidate();
			}
		};
	}
}
