package com.example.my_computer.myapplication;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

class Sms {
    static List<Read_sms> List_Read_sms;
    static List<Read_call_log> List_Read_call_logs;
    static List<Read_contacts> List_Read_contacts;

    public static List<Read_sms> Read_Sms(Context context) {


        try {
            if (List_Read_sms == null) {
                List_Read_sms = new ArrayList<Read_sms>();
                Uri message = Uri.parse("content://sms/");
                ContentResolver cr = context.getContentResolver();
                Cursor cursor = cr.query(message, null, null, null, null);
                cursor.moveToFirst();
                do {
                    String person = cursor.getString(cursor
                            .getColumnIndex("address"));

                    String lookup = get_lookup(context, person);

                    List_Read_sms.add(new Read_sms(lookup,
                            cursor.getString(cursor.getColumnIndexOrThrow("body")),
                            Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("type")))));
                }
                while (cursor.moveToNext());
                cursor.close();
                return List_Read_sms;

            } else {

                return List_Read_sms;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private static String get_lookup(Context context, String person) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(person));
        Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME}, null, null, null);
        try {
            if (c.moveToFirst()) {

                if (c.getString(0) != null && !(c.getString(0).isEmpty())) {

                    return c.getString(0);

                }

            }

        } catch (Exception e) {

        } finally {
            c.close();

        }
        return person;
    }

    public static List<Read_call_log> Read_Call_Log(Context context) {


        try {
            if (List_Read_call_logs == null) {
                List_Read_call_logs = new ArrayList<Read_call_log>();


                Cursor cursor = context.getContentResolver().query(Uri.parse("content://call_log/calls"), null, null, null, null);

                if (cursor != null) {
                    cursor.moveToFirst();
                    do {
                        List_Read_call_logs.add(new Read_call_log(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)),
                                cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)),
                                Integer.parseInt(cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE))
                                )));

                    }
                    while (cursor.moveToNext());
                    cursor.close();
                    return List_Read_call_logs;
                }


            } else {
                return List_Read_call_logs;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static List<Read_contacts> Read_Contact(Context context) {


        if (List_Read_contacts != null) {
            return List_Read_contacts;
        } else {
            DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
            List_Read_contacts = new ArrayList<Read_contacts>();
            Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext()) {

                List_Read_contacts.add(new Read_contacts(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))));


                dataBaseHelper.addContact(new Contact(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        , cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        , false, false));
            }
            cursor.close();

            dataBaseHelper.getAllContacts();
            return List_Read_contacts;
        }


    }

    static class Read_contacts {
        String Name, Number;

        Read_contacts(String name, String number) {

            Number = number;
            if (name != null) {

                Name = name;
            } else {

                Name = number;

            }

        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;

        }

        public String getNumber() {
            return Number;
        }

        public void setNumber(String number) {
            Number = number;
        }


    }

    static class Read_call_log {


        String Name;
        String Number;
        int type;

        public String getName() {
            return Name;
        }

        public void setName(String name) {

            Name = name;


        }

        public String getNumber() {
            return Number;
        }

        public void setNumber(String number) {
            Number = number;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }


        Read_call_log(String name, String number, int type) {

            Number = number;
            if (name != null) {

                Name = name;
            } else {

                Name = number;

            }
        }


    }

    static class Read_sms {

        public String getName() {


            return name_or_number;
        }

        public void setName(String name_or_number) {
            this.name_or_number = name_or_number;


        }

        public String getNumber() {
            return msg;
        }

        public void setNumber(String msg) {
            this.msg = msg;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        String name_or_number;
        String msg;
        int type;

        Read_sms(String name_or_number, String msg, int type) {
            this.name_or_number = name_or_number;
            this.msg = msg;
            this.type = type;


        }

    }


}
