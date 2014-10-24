package com.example.my_computer.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Window;
import android.widget.Toast;


public class Share extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {

            requestWindowFeature(Window.FEATURE_NO_TITLE);

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hey there! Cheers!");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Try App !" + getApplication().getString(R.string.app_name) + "its great");
            startActivity(Intent.createChooser(emailIntent, "Share via"));
            super.onCreate(savedInstanceState);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Unable to share ", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

