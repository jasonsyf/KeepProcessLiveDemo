package com.syf.keepprocesslivedemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import static com.syf.keepprocesslivedemo.StepService.CHANNEL_ID;
import static com.syf.keepprocesslivedemo.StepService.CHANNEL_NAME;

public class GuardService extends Service {

    public GuardService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new IProcessConnection.Stub() {};
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);
            channel.setSound(null, null);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID).build();
            startForeground(1, notification);
        } else {
            startForeground(1, new Notification());
        }
        bindService(new Intent(this, StepService.class),
                mServiceConnection, BIND_IMPORTANT);
        return START_STICKY;
    }

    private ServiceConnection mServiceConnection =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //链接上
            Log.d("keeplive","GuardService:建立链接");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("keeplive","GuardService:断开连接");
            startService(new Intent(GuardService.this, StepService.class));
            bindService(new Intent(GuardService.this, StepService.class), mServiceConnection, Context.BIND_IMPORTANT);
        }
    };



}
