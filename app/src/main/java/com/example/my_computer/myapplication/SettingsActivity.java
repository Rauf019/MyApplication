package com.example.my_computer.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.flurry.android.FlurryAgent;


public class SettingsActivity extends PreferenceActivity {


    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            String stringValue = value.toString();
            String key = preference.getKey();


            if (preference instanceof ListPreference) {

                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);


            } else if (key.equals("key"))

            {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Try  " + preference.getContext().getString(R.string.app_name) + " its great");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "I am using " + preference.getContext().getString(R.string.app_name) + " app  try this app its great !");
                preference.setIntent((Intent.createChooser(emailIntent, "Share via")));


            } else if (key.equals("key1"))

            {
                Uri uri = Uri.parse("market://details?id=" + preference.getContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                preference.setIntent(goToMarket);


            } else

            {
                preference.setSummary(stringValue);
            }

            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {

        try {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        } catch (ClassCastException e) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), false));
        }

        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);


    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, getString(R.string.FlurryAgent));
        FlurryAgent.logEvent("In Setting");
        FlurryAgent.setLogEnabled(true);
        FlurryAgent.setLogEvents(true);
    }

    @Override
    protected void onStop() {
        super.onStop();

        FlurryAgent.onEndSession(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }


    private void setupSimplePreferencesScreen() {


        try {

            addPreferencesFromResource(R.xml.pref_notification);
            bindPreferenceSummaryToValue(findPreference("key"));
            bindPreferenceSummaryToValue(findPreference("key1"));
            bindPreferenceSummaryToValue(findPreference("duration"));
            bindPreferenceSummaryToValue(findPreference("temple"));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

