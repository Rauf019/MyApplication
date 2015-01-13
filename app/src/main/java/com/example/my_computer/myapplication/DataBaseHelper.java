package com.example.my_computer.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Call_blocker";
    private static final String TABLE_CONTACTS = "Contacts";
    private static final String PHONE_NUMBER = " PHONE_NUMBER ";
    private static final String NAME = " NAME ";
    private static final String IS_CALL_BLOCK = " IS_CALL_BLOCK ";
    private static final String IS_MSG_BLOCK = " IS_MSG_BLOCK ";
    private static final String PHOTO = " PHOTO ";
    private Context context;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static boolean stringToBool(String s) {
        if (s.equals("1"))
            return true;
        if (s.equals("0"))
            return false;
        throw new IllegalArgumentException(s + " is not a bool. Only 1 and 0 are.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + PHONE_NUMBER + " VARCHAR Primary Key ," + NAME + " VARCHAR," + IS_CALL_BLOCK
                + " BOOLEAN, " + IS_MSG_BLOCK + " BOOLEAN ," + PHOTO + " VARCHAR " + ");";


        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);


        onCreate(db);
    }

    public void addContact(Contact contact) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PHONE_NUMBER, contact.get_phoneNumber());
            values.put(NAME, contact.get_Name());
            values.put(IS_CALL_BLOCK, contact.get_is_Call_block());
            values.put(IS_MSG_BLOCK, contact.get_is_Msg_block());
            values.put(PHOTO, contact.getPhoto());
            db.insert(TABLE_CONTACTS, null, values);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Contact getContact(String a) {

        Contact contact = null;
        try {

            SQLiteDatabase db = this.getReadableDatabase();
            contact = new Contact();

            Cursor cursor = db.query(TABLE_CONTACTS, new String[]{
                            PHONE_NUMBER, NAME, IS_CALL_BLOCK, IS_MSG_BLOCK, PHOTO},
                    PHONE_NUMBER + "= ?", new String[]{String.valueOf(a)},
                    null, null, null, null);

            if (cursor.moveToFirst()) {

                do {

                    contact.set_phoneNumber(cursor.getString(0));
                    contact.set_Name(cursor.getString(1));
                    contact.set_is_Call_block(stringToBool(cursor.getString(2)));
                    contact.set_is_Msg_block(stringToBool(cursor.getString(3)));
                    contact.setPhoto(cursor.getString(4));

                } while (cursor.moveToNext());
            }
            cursor.close();
            this.getWritableDatabase().close();
            return contact;

        } catch (Exception e) {

            return contact;
        }


    }


//    public List<Contact> Sort_By(String val) {
//
//        List<Contact> contactList = null;
//        try {
//
//
//            SQLiteDatabase db = this.getReadableDatabase();
//            contactList = new ArrayList<Contact>();
//
//            String sql_statement = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + val + " = " + " 1 ";
//            Cursor cursor = db.rawQuery(sql_statement, null);
//            cursor.moveToFirst();
//
//            do {
//                Contact contact = new Contact();
//                contact.set_phoneNumber(cursor.getString(0));
//                contact.set_Name(cursor.getString(1));
//                contact.set_is_Call_block(stringToBool(cursor.getString(2)));
//                contact.set_is_Msg_block(stringToBool(cursor.getString(3)));
//                contact.setPhoto(cursor.getString(4));
//                contactList.add(contact);
//            } while (cursor.moveToNext());
//
//            cursor.close();
//            db.close();
//            return contactList;
//        } catch (Exception e) {
//
//            String message = e.getMessage();
//
//            return contactList;
//        }
//
//
//    }

//    public List<Contact> getAllContacts() {
//
//        List<Contact> contactList = null;
//
//        try {
//            contactList = new ArrayList<Contact>();
//
//            String selectQuery = "SELECT * FROM " + TABLE_CONTACTS;
//
//            SQLiteDatabase db = this.getWritableDatabase();
//            Cursor cursor = db.rawQuery(selectQuery, null);
//            cursor.moveToFirst();
//
//            do {
//
//                contactList.add(new Contact(cursor.getString(0), cursor.getString(1),
//                        stringToBool(cursor.getString(2)), stringToBool(cursor.getString(3)), cursor.getString(4)));
//
//            } while (cursor.moveToNext());
//
//            cursor.close();
//            this.getWritableDatabase().close();
//            return contactList;
//
//        } catch (Exception e) {
//
//
//            return contactList;
//        }
//    }

    public List<Contact> getAllContacts() {

        List<Contact> contactList = null;

        try {
            contactList = new ArrayList<Contact>();

            String sql_statement = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + IS_CALL_BLOCK + " != " + " 0 " + " OR " + IS_MSG_BLOCK + " != " + " 0 ";

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql_statement, null);
            cursor.moveToFirst();

            do {

                contactList.add(new Contact(cursor.getString(0), cursor.getString(1),
                        stringToBool(cursor.getString(2)), stringToBool(cursor.getString(3)), cursor.getString(4)));

            } while (cursor.moveToNext());

            cursor.close();
            this.getWritableDatabase().close();
            return contactList;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return contactList;
        }

    }

//    public int getContactsCount() {
//        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();
//
//        return cursor.getCount();
//    }

    public boolean updateContact(Contact contact) {
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PHONE_NUMBER, contact.get_phoneNumber());
            values.put(NAME, contact.get_Name());
            values.put(IS_CALL_BLOCK, contact.get_is_Call_block());
            values.put(IS_MSG_BLOCK, contact.get_is_Msg_block());
            values.put(PHOTO, contact.getPhoto());

            db.update(TABLE_CONTACTS, values, PHONE_NUMBER + " = ?",
                    new String[]{String.valueOf(contact.get_phoneNumber())});
            this.getWritableDatabase().close();
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    public int deleteContact(String val) {
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(TABLE_CONTACTS, PHONE_NUMBER + " = ?",
                new String[]{String.valueOf(val)});
        db.close();
        return delete;
    }

//    public int deleteContact_sort(String val) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        int delete = db.delete(TABLE_CONTACTS, PHONE_NUMBER + " = ?",
//                new String[]{String.valueOf(val)});


//        db.close();
//        return delete;
//    }
}