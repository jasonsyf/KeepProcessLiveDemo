package com.syf.keepprocesslivedemo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Result;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class keepLiveWork extends Worker {
    private static final String TAG = "keeplive";

    public keepLiveWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        startAllServices(getApplicationContext());
        return Result.success();
    }

    /**
     * 开启所有Service
     */
    private void startAllServices(Context context) {
        context.startService(new Intent(context, StepService.class));
        context.startService(new Intent(context, GuardService.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "startAllServices: ");
            //版本必须大于5.0
            context.startService(new Intent(context, JobWakeUpService.class));
        }
    }
}
