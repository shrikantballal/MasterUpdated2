package com.snaplion.kingsxi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class NotifAlertDialogActivity extends AppSuperActivity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.custom_notification);
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon);

		Intent startingIntent = getIntent();
		String notifMsg= startingIntent.getStringExtra("NOTIF_TEXT");
		
		//ImageView image = (ImageView)findViewById(R.id.image);
		//TextView title = (TextView)findViewById(R.id.title);
		TextView text = (TextView)findViewById(R.id.text);
		
		//image.setBackgroundResource(R.drawable.icon);
		//title.setText(ConstantUtil.APP_NAME);
		text.setText(notifMsg);
	}
}
