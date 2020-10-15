package com.example.serverapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ServerService extends Service {
    MyServer server;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, ServerService.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // build the notification for the service
        Notification notification =
                new NotificationCompat.Builder(this, "FOREGROUND")
                        .setContentTitle("Server App")
                        .setContentText("Server is running")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .build();

        // start the service in foreground
        startForeground(1, notification);

        PublicKey publicKey = (PublicKey) intent
                .getSerializableExtra("PUBLIC_KEY");
        PrivateKey privateKey = (PrivateKey) intent
                .getSerializableExtra("PRIVATE_KEY");
        server = new MyServer(8080, publicKey, privateKey);

        // create the message to be passed to the broadcast receiver
        Intent serverMessage = new Intent();
        serverMessage.setAction("com.example.serverapp");

        try {
            // start the server
            server.start();
            serverMessage.putExtra("SERVER_LAUNCHED", true);
        } catch (IOException e) {
            // if the server couldn't be started successfully
            // stop the service
            stopSelf();
            Log.e("error server", e.toString());
            serverMessage.putExtra("SERVER_LAUNCHED", false);
        }

        // send the broadcast to the listening receivers
        sendBroadcast(serverMessage);

        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "FOREGROUND",
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager =
                    getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        server.stop();

        Intent serverMessage = new Intent();
        serverMessage.setAction("com.example.serverapp");
        serverMessage.putExtra("SERVER_LAUNCHED", false);
        sendBroadcast(serverMessage);

        super.onDestroy();
    }
}