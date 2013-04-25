package com.snaplion.kingsxi;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.snaplion.myapp.SnapLionMyAppActivity;

public class SLIncomingCallReceiver extends BroadcastReceiver {
	@Override
    public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            
            if(null == bundle)
                    return;
            
            Log.i("IncomingCallReceiver",bundle.toString());
            
            String state = bundle.getString(TelephonyManager.EXTRA_STATE);
                            
            Log.i("IncomingCallReceiver","State: "+ state);
            
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
            {
        	 		SnapLionMyAppActivity.KillFlag = false;
            }
    }
}
