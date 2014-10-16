package com.example.my_computer.myapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FullscreenActivity extends Activity {

    public static Custum_Class custum_class;

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        new Task().execute();


    }

    private HashMap<String, String> get_lookup(Context context, String Number) {
        HashMap<String, String> Lookup_list = new HashMap<String, String>();
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(Number));
        Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data.PHOTO_URI}, null, null, null);
        try {
            if (c.moveToFirst()) {


                if (c.getString(0) != null && !(c.getString(0).isEmpty())) {

                    Lookup_list.put("Name", c.getString(0));
                    Lookup_list.put("Photo_url", c.getString(1));
                    return Lookup_list;
                }

            }

        } catch (Exception e) {

        } finally {
            c.close();

        }
        return Lookup_list;
    }

    class Task extends AsyncTask<Void, Integer, Custum_Class> {

        int Total_count, dec_count;

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressBar.setProgress(values[0]);


        }

        protected Custum_Class doInBackground(Void... urls) {
            try {

                ArrayList<Read_contacts> List_Read_sms = null;
                ArrayList<Read_contacts> List_Read_contacts = null;
                ArrayList<Read_contacts> List_Read_call_logs = null;


                ContentResolver cr = getApplicationContext().getContentResolver();
                Cursor cursor = cr.query(Uri.parse("content://sms/"), null, null, null, null);
                Cursor cursor1 = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                Cursor cursor2 = cr.query(Uri.parse("content://call_log/calls"),
                        null, null, null, null);
                Total_count = cursor.getCount() + cursor1.getCount() + cursor2.getCount();
                progressBar.setMax(Total_count);

                cursor.moveToFirst();

                cursor1.moveToFirst();

                cursor2.moveToFirst();

                List_Read_sms = new ArrayList<Read_contacts>();

                do {

                    String Number = cursor.getString(cursor
                            .getColumnIndex("address"));

                    HashMap<String, String> lookup = get_lookup(getApplicationContext(), Number);

                    List_Read_sms.add(new Read_contacts(lookup.get("Name"),
                                    Number,
                                    lookup.get("Photo_url"))
                    );

                    onProgressUpdate(dec_count++);
                }
                while (cursor.moveToNext());
                cursor.close();


                List_Read_contacts = new ArrayList<Read_contacts>();


                do {
                    String string = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String string1 = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String string13 = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    List_Read_contacts.add(new Read_contacts(string,
                            string1,
                            string13));

                    onProgressUpdate(dec_count++);


                }
                while (cursor1.moveToNext());

                cursor1.close();


                List_Read_call_logs = new ArrayList<Read_contacts>();

                do {
                    String string = cursor2.getString(cursor2.getColumnIndex(CallLog.Calls.NUMBER));
                    HashMap<String, String> lookup = get_lookup(getApplicationContext(), string);


                    List_Read_call_logs.add(new Read_contacts(cursor2.getString(cursor2.getColumnIndex(CallLog.Calls.CACHED_NAME)),
                            string,

                            lookup.get("Photo_url")));

                    onProgressUpdate(dec_count++);
                }
                while (cursor2.moveToNext());


                cursor2.close();

                return new Custum_Class(List_Read_sms, List_Read_call_logs, List_Read_contacts);

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), "Unable to get Data", Toast.LENGTH_SHORT).show();
                return custum_class;

            }
        }

        @Override
        protected void onPostExecute(Custum_Class aClass) {


            super.onPostExecute(aClass);

            custum_class = aClass;
            custum_class.is_intialize = true;
            Intent intent
                    = new Intent(getApplicationContext(), MyActivity.class);

            startActivity(intent);

        }


    }

    class Custum_Class {

        List<Read_contacts> read_smses;
        List<Read_contacts> read_call_logs;
        List<Read_contacts> read_contactses;
        boolean is_intialize;

        public Custum_Class(List<Read_contacts> read_smses, List<Read_contacts> read_call_logs, List<Read_contacts> read_contactses) {
            this.read_smses = read_smses;
            this.read_call_logs = read_call_logs;
            this.read_contactses = read_contactses;
        }


        public List<Read_contacts> getRead_smses() {
            return read_smses;
        }

        public void setRead_smses(List<Read_contacts> read_smses) {
            this.read_smses = read_smses;
        }

        public List<Read_contacts> getRead_call_logs() {
            return read_call_logs;
        }

        public void setRead_call_logs(List<Read_contacts> read_call_logs) {
            this.read_call_logs = read_call_logs;
        }

        public List<Read_contacts> getRead_contactses() {
            return read_contactses;
        }

        public void setRead_contactses(List<Read_contacts> read_contactses) {
            this.read_contactses = read_contactses;
        }


    }

}







