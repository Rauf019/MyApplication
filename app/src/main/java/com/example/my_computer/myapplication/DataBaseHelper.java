package com.example.my_computer.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "Call_blocker";
    private static final String TABLE_CONTACTS = "Contacts";
    private static final String PHONE_NUMBER = " PHONE_NUMBER ";
    private static final String NAME = " NAME ";
    private static final String IS_CALL_BLOCK = " IS_CALL_BLOCK ";
    private static final String IS_MSG_BLOCK = " IS_MSG_BLOCK ";
    private static final String IS_BOTH_BLOCK = " IS_BOTH_BLOCK ";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + PHONE_NUMBER + " VARCHAR Primary Key ," + NAME + " VARCHAR," + IS_CALL_BLOCK
                + " BOOLEAN, " + IS_MSG_BLOCK + " BOOLEAN " + ");";

        Log.d("TAG", CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    public void addContact(Contact contact) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(PHONE_NUMBER, contact.get_phoneNumber());
            values.put(NAME, contact.get_Name());
            values.put(IS_CALL_BLOCK, contact._is_Call_block);
            values.put(IS_MSG_BLOCK, contact._is_Msg_block);
            db.insert(TABLE_CONTACTS, null, values);

            db.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public Contact getContact(String a) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(TABLE_CONTACTS, new String[]{
                            PHONE_NUMBER, IS_CALL_BLOCK, IS_MSG_BLOCK, IS_BOTH_BLOCK},
                    PHONE_NUMBER + "=?", new String[]{String.valueOf(a)},
                    null, null, null, null);
            if (cursor != null)
                cursor.moveToFirst();
            Contact contact = new Contact();
            contact.set_phoneNumber(cursor.getString(0));
            contact.set_Name(cursor.getString(1));
            contact.set_is_Call_block(Boolean.getBoolean(cursor.getString(2)));
            contact.set_is_Msg_block(Boolean.getBoolean(cursor.getString(3)));

            return contact;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }

    public void getAllContacts() {
        try {
            List<Contact> contactList = new ArrayList<Contact>();

            String selectQuery = "SELECT * FROM " + TABLE_CONTACTS;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Contact contact = new Contact();


                    Log.d("Database", cursor.getString(0));
                    Log.d("Database", cursor.getString(1));
                    Log.d("Database", cursor.getString(2));
                    Log.d("Database", cursor.getString(3));
                    //     contactList.add(contact);
                } while (cursor.moveToNext());

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public boolean updateContact(Contact contact) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(PHONE_NUMBER, contact.get_phoneNumber()); // Contact Name
            values.put(IS_CALL_BLOCK, contact._is_Call_block);
            values.put(IS_MSG_BLOCK, contact._is_Msg_block);


            db.update(TABLE_CONTACTS, values, PHONE_NUMBER + " = ?",
                    new String[]{String.valueOf(contact.get_phoneNumber())});

            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, PHONE_NUMBER + " = ?",
                new String[]{String.valueOf(contact.get_phoneNumber())});
        db.close();
    }
}