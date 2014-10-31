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

    static int num;
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
        String action = intent.getAction();


        telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        shared(context);
        pref = context.getSharedPreferences("MyPref", 0);
        int key_name = pref.getInt("key_name", 3);
        switch (key_name) {

            case 0:      // accept all


                break;

            case 1:        // block all


                try {
                    all_Call();
                    all_Sms();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case 2:     // allow only contact


                try {

                    Contact_StateListeners listener1 = new Contact_StateListeners();
                    telephony.listen(listener1, PhoneStateListener.LISTEN_CALL_STATE);
                    //           telephony.listen(listener1, PhoneStateListener.LISTEN_NONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case 3:          // black list


                try {

                    //  android.intent.action.PHONE_STATE

                    if (intent.getAction() == "android.provider.Telephony.SMS_RECEIVED") {
                        Sms_Filter();
                    }


                    Call_Filter_StateListeners listener1 = new Call_Filter_StateListeners();
                    telephony.listen(listener1, PhoneStateListener.LISTEN_CALL_STATE);
                    //         telephony.listen(listener1, PhoneStateListener.LISTEN_NONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case 4:
                // do not disturb

                try {


                    all_Call();
                    all_Sms_with_reply();
                    Do_not_Dis_StateListeners listener1 = new Do_not_Dis_StateListeners();
                    telephony.listen(listener1, PhoneStateListener.LISTEN_CALL_STATE);
                    //   telephony.listen(listener1, PhoneStateListener.LISTEN_NONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }

    }

    public void shared(Context context) {

//        SharedPreferences.Editor editor =  PreferenceManager.getDefaultSharedPreferences(context).edit();
//        editor.putInt(getString(R.string.saved_high_score), newHighScore);
//        editor.commit();

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

                final Bundle bundle = intent.getExtras();

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        if (notification) {
                            notification("Call Blocker", "Call Block " + phoneNumber);
                        }

                        abortBroadcast();
                    }


                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "Unable to Delete Sms ", Toast.LENGTH_SHORT).show();

        }
    }

    public void all_Sms_with_reply() {


        try {
            final Bundle bundle = intent.getExtras();

            if (intent.getAction() == "android.provider.Telephony.SMS_RECEIVED") {

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
//                        send_sms(phoneNumber, temple, duration);
                        abortBroadcast();
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        abortBroadcast();
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

                all_Call();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Unable to Block Call", Toast.LENGTH_SHORT).show();


        }
    }

    public void all_Call() {

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

    public void send_sms(String incomingNumber, String temple, String duration) {
        try {

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("03463113142", null, temple + "" + duration, null, null);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    class Contact_StateListeners extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            super.onCallStateChanged(state, incomingNumber);
            if (state == TelephonyManager.CALL_STATE_RINGING) {


                try {
                    if (!((get_lookup(context, incomingNumber)))) {

                        all_Call();
                        all_Sms();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }

    }

    class Call_Filter_StateListeners extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            if (state == TelephonyManager.CALL_STATE_RINGING) {


                Call_Filter(incomingNumber);


            }

        }
    }

    class Do_not_Dis_StateListeners extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            send_sms(incomingNumber, temple, duration);

        }

    }
}
