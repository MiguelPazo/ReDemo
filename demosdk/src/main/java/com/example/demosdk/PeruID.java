package com.example.demosdk;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 18/12/2016.
 */

public class PeruID {

    private final String TAG = "PeruID";
    private static PeruID __instance;
    private ServiceConnection oServConnection;
    private Messenger localMessenger = new Messenger(new IncommingMessages());
    private Messenger remoteMessenger;
    private Activity oActivity;
    private IValidate iValidate;

    public static PeruID getInstance(Activity oActivity) {
        if (__instance == null) {
            __instance = new PeruID(oActivity);
        }

        return __instance;
    }

    private PeruID(Activity oActivity) {
        this.oActivity = oActivity;

        oServConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                remoteMessenger = new Messenger(iBinder);

                Log.d(TAG, "Service connected.");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                remoteMessenger = null;

                Log.d(TAG, "Service disconnected.");
            }
        };

        bootstrap();
    }

    private void bootstrap() {
        if (remoteMessenger == null) {
            Intent intent = new Intent();
            intent.setClassName(Constants.ACTION_SERVICE_PKG, Constants.ACTION_SERVICE);

            oActivity.startService(intent);
            oActivity.bindService(intent, oServConnection, Context.BIND_AUTO_CREATE);
        } else {
            Log.d(TAG, "Service already connected.");
        }
    }

    public void validate(IValidate iValidate) {
        this.iValidate = iValidate;
        Message message = Message.obtain(new Handler(), Constants.ACTION_VALIDATE);

        sendMessage(message);
    }

    private void handleValidate(Bundle data) {
        Boolean isValid = data.getBoolean(Constants.EXTRA_VALIDATE_RESULT, false);
        Log.d(TAG, "Response to SDK");

        if (isValid) {
            iValidate.isValid();
        } else {
            iValidate.isInvalid();
        }
    }

    private void sendMessage(Message message) {
        if (remoteMessenger != null) {
            try {
                message.replyTo = localMessenger;
                remoteMessenger.send(message);
            } catch (RemoteException e) {
                Log.e(TAG, "Error sending remote message - " + e.getMessage());
            }
        } else {
            bootstrap();
            Log.d(TAG, "No service messagin bootstraped.");
        }
    }

    private class IncommingMessages extends Handler {
        @Override
        public void handleMessage(Message message) {
            if (message != null) {
                switch (message.what) {
                    case 1: //Constants.ACTION_VALIDATE
                        handleValidate(message.getData());
                        break;
                }
            } else {
                super.handleMessage(message);
            }
        }
    }


    public interface IValidate {
        public void isValid();

        public void isInvalid();

        public void close();
    }
}
