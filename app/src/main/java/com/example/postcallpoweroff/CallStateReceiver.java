package com.example.postcallpoweroff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallStateReceiver extends BroadcastReceiver {

    private static final String TAG = "CallStateReceiver";
    private static boolean wasInCall = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.d(TAG, "Phone State Changed: " + state);

            if (state == null) {
                return;
            }

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING) || state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                wasInCall = true;
            }
            else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                if (wasInCall) {
                    wasInCall = false;
                    Log.d(TAG, "Call ended. Checking for overlay permission.");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(context)) {
                        Log.d(TAG, "Overlay permission granted. Starting PowerOffService.");
                        Intent serviceIntent = new Intent(context, PowerOffService.class);
                        context.startService(serviceIntent);
                    } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        Log.d(TAG, "Pre-Marshmallow device. Starting PowerOffService.");
                        Intent serviceIntent = new Intent(context, PowerOffService.class);
                        context.startService(serviceIntent);
                    } else {
                        Log.w(TAG, "Overlay permission not granted. Cannot display dialog.");
                    }
                }
            }
        }
    }
}
