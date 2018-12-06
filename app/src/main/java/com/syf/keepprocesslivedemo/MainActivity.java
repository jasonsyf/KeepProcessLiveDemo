package com.syf.keepprocesslivedemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG ="keeplive" ;
    private static final String TAG_KEEP_WORK ="keep_work" ;

    PowerManager.WakeLock mWakeLock;
    BootUpReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startKeepWork();
        getLock(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mReceiver = new BootUpReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        releaseLock();
    }


    synchronized private void releaseLock() {
        if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
                Log.v(TAG, "release lock");
            }

            mWakeLock = null;
        }
    }

    /**
     *      * 同步方法   得到休眠锁
     *      * @param context
     *      * @return
     *     
     */
    synchronized private void getLock(Context context) {
        if (mWakeLock == null) {
            PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, StepService.class.getName());
            mWakeLock.setReferenceCounted(true);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis((System.currentTimeMillis()));
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour >= 23 || hour <= 6) {
                mWakeLock.acquire(5000);
                mWakeLock.acquire(300000);
            }
        }
        Log.v(TAG, "get lock");
    }

//    /**
//     * 开启所有Service
//     */
//    private void startAllServices()
//    {
//        startService(new Intent(this, StepService.class));
//        startService(new Intent(this, GuardService.class));
//        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
//            Log.d(TAG, "startAllServices: ");
//            //版本必须大于5.0
//            startService(new Intent(this, JobWakeUpService.class));
//        }
//    }

    public void startKeepWork() {
        WorkManager.getInstance().cancelAllWorkByTag(TAG_KEEP_WORK);
        Log.d(TAG, "keep-> dowork startKeepWork");
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(keepLiveWork.class)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 5, TimeUnit.SECONDS)
                .addTag(TAG_KEEP_WORK)
                .build();
        WorkManager.getInstance().enqueue(oneTimeWorkRequest);
    }

}
