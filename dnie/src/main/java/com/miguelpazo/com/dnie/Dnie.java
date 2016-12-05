package com.miguelpazo.com.dnie;

import android.util.Log;

import com.credenceid.biometrics.Biometrics;
import com.credenceid.biometrics.BiometricsActivity;

/**
 * Created by 70273865AUT on 05/12/2016.
 */

public class Dnie {
    private static Dnie __instance;
    private BiometricsActivity biometricsActivity;

    private Dnie(BiometricsActivity biometricsActivity) {
        this.biometricsActivity = biometricsActivity;
    }

    public static Dnie getInstance(BiometricsActivity biometricsActivity) {
        if (__instance == null) {
            __instance = new Dnie(biometricsActivity);
        }

        return __instance;
    }

    public void validate(final IValidate iValidate) {
        biometricsActivity.cardOpenCommand(new Biometrics.CardReaderStatusListner() {
            @Override
            public void onCardReaderOpen(Biometrics.ResultCode resultCode) {
                if (resultCode.equals(Biometrics.ResultCode.OK)) {
                    iValidate.isValid();
                    Log.d("-->", "Conexion OK");
                } else {
                    iValidate.isInvalid();
                    Log.d("-->", "Conexion ERROR");
                }
            }

            @Override
            public void onCardReaderClosed(Biometrics.CloseReasonCode closeReasonCode) {
                iValidate.close();
                Log.d("-->", "Conexion CLOSE");
            }
        });
    }

    public interface IValidate {
        public void isValid();

        public void isInvalid();

        public void close();
    }
}
