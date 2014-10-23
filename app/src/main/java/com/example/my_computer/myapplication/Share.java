package com.example.my_computer.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;


public class Share extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Object temple = savedInstanceState.get("temple");


        super.onCreate(savedInstanceState);
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey there! Cheers!");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Try App !" + getApplication().getString(R.string.app_name) + "its great");
        startActivity(Intent.createChooser(emailIntent, "Share via"));
    }
}

