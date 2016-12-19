package com.miguelpazo.com.redemo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.credenceid.biometrics.BiometricsActivity;
import com.miguelpazo.com.dnie.Dnie;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 18/12/2016.
 */

public class DnieService extends Service {

    private static final String TAG = "DnieService";
    private final Messenger messenger = new Messenger(new IncommingMessages());
    private Messenger mClient;
    private Dnie oDnie;

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    private class IncommingMessages extends Handler {
        @Override
        public void handleMessage(Message message) {
            Log.d(TAG, "Incomming message");

            if (message != null) {
                mClient = message.replyTo;

                switch (message.what) {
                    case 1: //Constants.ACTION_VALIDATE
                        validate(message.getData());
                        break;
                }
            } else {
                super.handleMessage(message);
            }
        }
    }

    @Override
    public void onCreate() {
        BiometricsActivity activity = new BiometricsActivity();
        oDnie = Dnie.getInstance(activity);
        Intent intent = new Intent("com.miguelpazo.com.redemo.DnieService");
        this.startService(intent);
    }

    private void validate(Bundle bundle) {
        final Bundle response = new Bundle();
        response.putBoolean(Constants.EXTRA_VALIDATE_RESULT, false);

        oDnie.validate(new Dnie.IValidate() {
            @Override
            public void isValid() {
                response.putBoolean(Constants.EXTRA_VALIDATE_RESULT, true);
                sendMessage(response, Constants.ACTION_VALIDATE);
                Log.d(TAG, "Valid PIN.");
            }

            @Override
            public void isInvalid() {
                response.putBoolean(Constants.EXTRA_VALIDATE_RESULT, false);
                sendMessage(response, Constants.ACTION_VALIDATE);
                Log.d(TAG, "Invalid PIN.");
            }

            @Override
            public void close() {
                response.putBoolean(Constants.EXTRA_VALIDATE_RESULT, false);
                sendMessage(response, Constants.ACTION_VALIDATE);
                Log.d(TAG, "Closing reader.");
            }
        });
    }

    private void sendMessage(Bundle bundle, Integer what) {
        try {
            Message message = Message.obtain(new Handler(), what);
            message.setData(bundle);
            mClient.send(message);
        } catch (RemoteException e) {
            Log.e(TAG, "Error sending remote message - " + e.getMessage());
        }
    }
}
