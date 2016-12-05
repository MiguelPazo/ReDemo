package com.miguelpazo.com.redemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.credenceid.biometrics.BiometricsActivity;
import com.miguelpazo.com.dnie.Dnie;

public class MainActivity extends BiometricsActivity {

    private Dnie oDnie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        oDnie = Dnie.getInstance(this);
    }

    public void handleLoginPin(View view) {
        Log.d("-->", "handleLoginPin");

        oDnie.validate(new Dnie.IValidate() {
            @Override
            public void isValid() {
                Log.d("-->", "isValid");
            }

            @Override
            public void isInvalid() {
                Log.d("-->", "isInvalid");
            }

            @Override
            public void close() {
                Log.d("-->", "close");
            }
        });
    }

    public void handleLoginFinger(View view) {
        Log.d("-->", "handleLoginFinger");

        oDnie.validate(new Dnie.IValidate() {
            @Override
            public void isValid() {
                Log.d("-->", "isValid 2");
            }

            @Override
            public void isInvalid() {
                Log.d("-->", "isInvalid 2");
            }

            @Override
            public void close() {
                Log.d("-->", "close 2");
            }
        });
    }
}
