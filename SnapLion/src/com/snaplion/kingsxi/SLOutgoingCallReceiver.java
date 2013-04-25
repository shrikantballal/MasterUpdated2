package com.snaplion.kingsxi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.snaplion.myapp.SnapLionMyAppActivity;

public class SLOutgoingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            
            if(null == bundle)
                    return;
    	 	SnapLionMyAppActivity.KillFlag = false;
    }
}
