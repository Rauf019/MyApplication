package com.example.my_computer.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Window;
import android.widget.Toast;


public class Review extends PreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {


//        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//        emailIntent.setType("text/plain");
//        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey there! Cheers!");
//        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Try App !" + getApplication().getString(R.string.app_name) + "its great");
//        startActivity(Intent.createChooser(emailIntent, "Share via"));

        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);
            super.onCreate(savedInstanceState);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Unable to send ", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
