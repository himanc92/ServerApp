package com.example.serverapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class MainActivity extends AppCompatActivity {
    static PublicKey publicKey;
    static PrivateKey privateKey;
    TextView textView;

    // this is used to receive the information
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean serverLaunched =
                    intent.getBooleanExtra("SERVER_LAUNCHED", false);
            if (serverLaunched) {
                textView.setText("Server started");
            } else {
                textView.setText("Failed starting server");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KeyStoreService manager = new KeyStoreService();
        KeyPair clientKeys = manager.getKeyPair();
        Log.e("public key", clientKeys.getPublic().toString());
        publicKey = clientKeys.getPublic();
        privateKey = clientKeys.getPrivate();

        textView = findViewById(R.id.textView);

        startService(clientKeys);
    }

    // start the service in foreground
    public void startService(KeyPair clientKeys) {
        Intent serviceIntent = new Intent(this, ServerService.class);
        serviceIntent.putExtra("PUBLIC_KEY", clientKeys.getPublic());
        serviceIntent.putExtra("PRIVATE_KEY", clientKeys.getPrivate());
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    // stop the service
    public void stopService() {
        Intent serviceIntent = new Intent(this, ServerService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        stopService();
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // registers the broadcast receiver to the given intent
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.serverapp");
        registerReceiver(broadcastReceiver, intentFilter);
    }
}