package com.syf.keepprocesslivedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author syf
 */
public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mIntent = new Intent(context, StepService.class);
        context.startService(mIntent);
        Log.i("keeplive", "onReceive: 收到开屏唤醒开机广播了");
    }
}
