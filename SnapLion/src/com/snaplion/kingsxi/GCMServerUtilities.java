package com.snaplion.kingsxi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

/**
 * Helper class used to communicate with the demo server.
 */
public final class GCMServerUtilities {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    public static String serverUrl,  senderId,  appId,  appName;
    
    public GCMServerUtilities(String serverUrl, String senderId, String appId, String appName)
    {
    	this.serverUrl=serverUrl;
    	this.senderId=senderId;
    	this.appId=appId;
    	this.appName=appName;
    }
    private static String getUniqueDeviceID(Context context)
    {
    	return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }
    /**
     * Register this account/device pair within the server.
     *
     * @return whether the registration succeeded or not.
     */
    static boolean register(final Context context, final String regId) 
    {
        Log.i(AppManager.getInstance().GCM_TAG, "registering device (regId = " + regId + ")");
        String serverUrl = AppManager.getInstance().REGISTER_DEVICE_GCM_URL;
        Map<String, String> params = new HashMap<String, String>();
        params.put("reg_id", regId);
        params.put("app_id", AppManager.getInstance().getAPP_ID());
        String device_id=getUniqueDeviceID(context);
        Utility.debug("device_id : "+device_id);
        params.put("device_id", device_id);
        Utility.debug("REGISTER_DEVICE_GCM_URL : "+AppManager.getInstance().REGISTER_DEVICE_GCM_URL+"?reg_id="+regId+"&app_id="+AppManager.getInstance().getAPP_ID());
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            Log.d(AppManager.getInstance().GCM_TAG, "Attempt #" + i + " to register");
            try 
            {
                post(serverUrl, params);
                GCMRegistrar.setRegisteredOnServer(context, true);
                return true;
            } 
            catch (IOException e) 
            {
                Log.e(AppManager.getInstance().GCM_TAG, "Failed to register on attempt " + i, e);
                if (i == MAX_ATTEMPTS) 
                {
                    break;
                }
                try 
                {
                    Log.d(AppManager.getInstance().GCM_TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } 
                catch (InterruptedException e1) 
                {
                    Log.d(AppManager.getInstance().GCM_TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return false;
                }
                backoff *= 2;
            }
        }
        return false;
    }

    /**
     * Unregister this account/device pair within the server.
     */
    static void unregister(final Context context, final String regId) 
    {
        Log.i(AppManager.getInstance().GCM_TAG, "unregistering device (regId = " + regId + ")");
        String serverUrl = AppManager.getInstance().UNREGISTER_DEVICE_GCM_URL;
        Map<String, String> params = new HashMap<String, String>();
        
        params.put("reg_id", regId);
        params.put("app_id", AppManager.getInstance().getAPP_ID());
        Utility.debug("UNREGISTER_DEVICE_GCM_URL : "+AppManager.getInstance().UNREGISTER_DEVICE_GCM_URL+"="+regId+"="+AppManager.getInstance().getAPP_ID());
        try 
        {
            post(serverUrl, params);
            GCMRegistrar.setRegisteredOnServer(context, false);
        } 
        catch (IOException e) 
        {
        	e.printStackTrace();
        }
    }

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    private static void post(String endpoint, Map<String, String> params) throws IOException 
    {
        URL url;
        try 
        {
            url = new URL(endpoint);
        } 
        catch (MalformedURLException e) 
        {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) 
        {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
            if (iterator.hasNext()) 
            {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(AppManager.getInstance().GCM_TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try 
        {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) 
            {
              throw new IOException("Post failed with error code " + status);
            }
        } 
        finally 
        {
            if (conn != null) 
            {
                conn.disconnect();
            }
        }
     }
}
