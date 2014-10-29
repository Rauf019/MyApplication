package com.example.my_computer.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.lang.reflect.Method;


public class ServiceReceiver extends BroadcastReceiver {

    static int num, Mode = -1;
    Context context;
    TelephonyManager telephony;
    DataBaseHelper dataBaseHelper;
    SharedPreferences pref;
    Intent intent;
    boolean p_calls;
    boolean notification;
    String temple;
    String duration;

    public void onReceive(Context context, Intent intent) {

        this.intent = intent;
        this.context = context;
        dataBaseHelper = new DataBaseHelper(context);

        telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        shared(context);
        pref = context.getSharedPreferences("MyPref", 0);
        int key_name = pref.getInt("key_name", 3);
        switch (key_name) {

            case 0:
                // accept all


                break;

            case 1:
                // block all

                try {
                    all_block();
                    all_Sms();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case 2:
                // allow only contact


                try {
                    Mode = 0;
                    PhoneStateListeners listener1 = new PhoneStateListeners();
                    telephony.listen(listener1, PhoneStateListener.LISTEN_CALL_STATE);
                    telephony.listen(listener1, PhoneStateListener.LISTEN_NONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case 3:
                // black list

                try {

                    //  android.intent.action.PHONE_STATE

                    if (intent.getAction() == "android.provider.Telephony.SMS_RECEIVED") {
                        Sms_Filter();
                    }
                    Mode = 1;
                    PhoneStateListeners listener1 = new PhoneStateListeners();
                    telephony.listen(listener1, PhoneStateListener.LISTEN_CALL_STATE);
                    telephony.listen(listener1, PhoneStateListener.LISTEN_NONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case 4:
                // do not disturb

                try {

                    Mode = 2;
                    all_block();
                    all_Sms();
                    PhoneStateListeners listener1 = new PhoneStateListeners();
                    telephony.listen(listener1, PhoneStateListener.LISTEN_CALL_STATE);
                    telephony.listen(listener1, PhoneStateListener.LISTEN_NONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }


    }

    public void shared(Context context) {


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        p_calls = sharedPreferences.getBoolean("p_calls", false);

        notification = sharedPreferences.getBoolean("notification", false);

        temple = sharedPreferences.getString("temple", "");

        duration = sharedPreferences.getString("duration", "");
    }

    private boolean get_lookup(Context context, String Number) {

        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(Number));
        Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME,}, null, null, null);

        try {


            if (c.moveToFirst()) {

                return true;
            }

        } catch (Exception e) {
            return false;
        }


        return false;
    }

    public void Sms_Filter() {


        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    Contact contact = dataBaseHelper.getContact(remove_plus(phoneNumber));

                    if (contact.get_is_Msg_block()) {
                        if (notification) {
                            notification("Call Blocker", "Message Block " + contact.get_Name());
                        }
                        abortBroadcast();
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "Unable to Delete Sms ", Toast.LENGTH_SHORT).show();

        }
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

    public void all_Sms() {


        try {

            if (intent.getAction() == "android.provider.Telephony.SMS_RECEIVED") {

                abortBroadcast();
            }


        } catch (Exception e) {
            Toast.makeText(context, "Unable to Delete Sms ", Toast.LENGTH_SHORT).show();

        }
    }

    public void notification(String Not_Title, String text) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.icon)
                .setContentTitle(Not_Title).setContentText(text)
                .setAutoCancel(true);
        Intent intent = new Intent(context, MyActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, num, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(num, mBuilder.build());
        num++;
    }

    public void Call_Filter(String incomingNumber) {


        try {
            Contact contact = dataBaseHelper.getContact(incomingNumber);

            if (contact.get_is_Call_block()) {

                if (notification) {
                    notification("Call Blocker", "Call Block " + contact.get_Name());
                }

                all_block();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Unable to Block Call", Toast.LENGTH_SHORT).show();


        }
    }

    public void all_block() {

        String serviceManagerName = "android.os.ServiceManager";
        String serviceManagerNativeName = "android.os.ServiceManagerNative";
        String telephonyName = "com.android.internal.telephony.ITelephony";
        Class<?> telephonyClass;
        Class<?> telephonyStubClass;
        Class<?> serviceManagerClass;
        Class<?> serviceManagerNativeClass;

        Method telephonyEndCall;
        Object telephonyObject;
        Object serviceManagerObject;
        try {
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService =
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod(
                    "asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(
                    serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface",
                    IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    class PhoneStateListeners extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {

                case TelephonyManager.CALL_STATE_RINGING:

                    switch (Mode) {

                        case 0:
                            try {
                                if (!((get_lookup(context, incomingNumber)))) {

                                    all_block();
                                    all_Sms();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            break;
                        case 1:
                            Call_Filter(incomingNumber);
                            break;
                        case 2:
                            try {
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(incomingNumber, null, temple + " " + duration, null, null);

                            } catch (Exception e) {

                                e.printStackTrace();
                            }
                            break;
                    }
                    break;
            }

        }
    }
}

