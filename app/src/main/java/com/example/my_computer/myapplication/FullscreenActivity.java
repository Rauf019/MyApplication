package com.example.my_computer.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class FullscreenActivity extends Activity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                Intent i = new Intent(FullscreenActivity.this, MyActivity.class);
                startActivity(i);
                finish();

                overridePendingTransition(R.animator.activityfadein,
                        R.animator.splashfadeout);
            }
        }, SPLASH_TIME_OUT);
    }
}


