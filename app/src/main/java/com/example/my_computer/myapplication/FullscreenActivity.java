package com.example.my_computer.myapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.widget.ProgressBar;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class FullscreenActivity extends Activity {

    public static Custum_Class custum_class;
    AsyncTask<Void, Integer, Custum_Class> execute;

    @Override
    protected void onPause() {
        super.onPause();
        execute.cancel(true);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        execute.cancel(true);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        execute = new Task();
        startMyTask(execute);


    }


    void startMyTask(AsyncTask asyncTask) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);

        else

            asyncTask.execute();
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

    public String remove_plus(String phoneNumber) {

        try {
            if (phoneNumber.charAt(0) == '+') {

                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

                Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, "");

                return "0" + String.valueOf(numberProto.getNationalNumber());

            } else {

                return phoneNumber;
            }

        } catch (Exception e) {


            return null;
        }

    }

    class Task extends AsyncTask<Void, Integer, Custum_Class> {

        int Total_count, dec_count;
        LinkedHashMap<String, Read_contacts> List_Read_sms = null;
        LinkedHashMap<String, Read_contacts> List_Read_contacts = null;
        LinkedHashMap<String, Read_contacts> List_Read_call_logs = null;


        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressBar.setProgress(values[0]);


        }

        protected Custum_Class doInBackground(Void... urls) {
            try {


                ContentResolver cr = getApplicationContext().getContentResolver();


                Cursor cursor = cr.query(Uri.parse("content://sms/"), new String[]{"address"}, null, null, null);

                Cursor cursor1 = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

                Cursor cursor2 = cr.query(CallLog.Calls.CONTENT_URI, new String[]{"number", "name"}
                        , null, null, null);


                Total_count = cursor.getCount() + cursor1.getCount() + cursor2.getCount();

                progressBar.setMax(Total_count);

                cursor.moveToFirst();

                cursor1.moveToFirst();

                cursor2.moveToFirst();

                List_Read_sms = new LinkedHashMap<String, Read_contacts>();

                do {

                    String Number = remove_plus(cursor.getString(cursor
                            .getColumnIndex("address")));

                    HashMap<String, String> lookup = get_lookup(getApplicationContext(), Number);

                    List_Read_sms.put(Number, new Read_contacts(lookup.get("Name"),
                                    Number,
                                    lookup.get("Photo_url"))
                    );

                    onProgressUpdate(dec_count++);
                }
                while (cursor.moveToNext());
                cursor.close();


                List_Read_contacts = new LinkedHashMap<String, Read_contacts>();


                do {
                    String string = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String string1 = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String string13 = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    List_Read_contacts.put(string1, new Read_contacts(string,
                            string1,
                            string13));

                    onProgressUpdate(dec_count++);


                }
                while (cursor1.moveToNext());

                cursor1.close();


                List_Read_call_logs = new LinkedHashMap<String, Read_contacts>();

                do {
                    String string = cursor2.getString(cursor2.getColumnIndex(CallLog.Calls.NUMBER));
                    HashMap<String, String> lookup = get_lookup(getApplicationContext(), string);


                    List_Read_call_logs.put(string, new Read_contacts(cursor2.getString(cursor2.getColumnIndex(CallLog.Calls.CACHED_NAME)),
                            string,

                            lookup.get("Photo_url")));

                    onProgressUpdate(dec_count++);
                }
                while (cursor2.moveToNext());


                cursor2.close();

                return new Custum_Class(new ArrayList<Read_contacts>(List_Read_sms.values()), new ArrayList<Read_contacts>(List_Read_call_logs.values()), new ArrayList<Read_contacts>(List_Read_contacts.values()));

            } catch (Exception e) {


                return null;

            }
        }

        @Override
        protected void onPostExecute(Custum_Class aClass) {

            super.onPostExecute(aClass);

            if (aClass != null) {
                custum_class = aClass;
                custum_class.is_intialize = true;
                Intent intent
                        = new Intent(getApplicationContext(), MyActivity.class);

                startActivity(intent);
            }

        }


    }

    class Custum_Class {


        private final List<Read_contacts> sms_list;
        private final List<Read_contacts> callLog_list;
        private final List<Read_contacts> contact_list;
        boolean is_intialize;


        public Custum_Class(List<Read_contacts> Sms_list, List<Read_contacts> CallLog_list, List<Read_contacts> Contact_list) {

            sms_list = Sms_list;
            callLog_list = CallLog_list;
            contact_list = Contact_list;
        }

        public List<Read_contacts> getSms_list() {
            return sms_list;
        }

        public List<Read_contacts> getCallLog_list() {
            return callLog_list;
        }

        public List<Read_contacts> getContact_list() {
            return contact_list;
        }


    }

}







