package com.example.my_computer.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
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

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;


public class ServiceReceiver extends BroadcastReceiver {

    static int num;
    Context context;
    TelephonyManager telephony;
    DataBaseHelper dataBaseHelper;
    SharedPreferences pref;
    Intent intent;
    boolean notification;
    String temple;
    String duration;
    Custum_StateListeners custum_stateListeners;
    AudioManager am;
    private boolean p_calls;
    private boolean sms_enable;

    public void onReceive(Context context, Intent intent) {
        try {

        this.intent = intent;
        this.context = context;
        dataBaseHelper = new DataBaseHelper(context);
        telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            shared(context);
        pref = context.getSharedPreferences("MyPref", 0);


            if (custum_stateListeners == null) {

                custum_stateListeners = new Custum_StateListeners();
                telephony.listen(custum_stateListeners, PhoneStateListener.LISTEN_CALL_STATE);
                telephony.listen(custum_stateListeners, PhoneStateListener.LISTEN_NONE);

            }



        if (intent.getAction() == "android.provider.Telephony.SMS_RECEIVED") {

            String phonenumber = null;
            int key_name = 0;
            try {
                phonenumber = sms_getnumber(intent);
                key_name = pref.getInt("key_name", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            switch (key_name) {

                case 0:      // accept all
                    break;


                case 1:        // block all

                    try {

                        final Bundle bundle = intent.getExtras();

                        if (bundle != null) {

                            if (notification) {
                                notification("Call Out", "Message Blocked from " + get_Name(context, phonenumber));
                            }
                            abortBroadcast();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case 2:     // allow only contact

                    try {
                        if (phonenumber != null) {
                            if (!(get_lookup(context, phonenumber))) {

                                if (notification) {
                                    notification("Call Out", "Message Blocked from " + get_Name(context, phonenumber));
                                }
                                abortBroadcast();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case 3:          // black list

                    try {
                        if (phonenumber != null) {

                            Contact contact = dataBaseHelper.getContact(phonenumber);

                            if (contact.get_is_Msg_block()) {

                                if (notification) {
                                    notification("Call Out", "Message Blocked from " + contact.get_Name());
                                }

                                abortBroadcast();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case 4:
                    // do not disturb
                    try {

                        if (phonenumber != null) {

                            if (sms_enable) {
                                send_sms(phonenumber, temple, duration);
                            }

                            if (notification) {
                                notification("Call Out", "Message Blocked from " + get_Name(context, phonenumber));
                            }

                            final Bundle bundle = intent.getExtras();

                            if (bundle != null) {

                                abortBroadcast();
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String sms_getnumber(Intent intent) {

        try {

            final Bundle bundle = intent.getExtras();

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    //       remove_plus();
                    return phoneNumber;


                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void shared(Context context) {

        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


            sms_enable = sharedPreferences.getBoolean("sms_enable", true);
            p_calls = sharedPreferences.getBoolean("p_calls", false);
            notification = sharedPreferences.getBoolean("notification", true);
            temple = sharedPreferences.getString("temple", "");
            duration = sharedPreferences.getString("duration", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean get_lookup(Context context, String Number) {


        try {
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(Number));
            Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME,}, null, null, null);


            if (c.moveToFirst()) {

                return true;
            } else {
                return false;

            }

        } catch (Exception e) {
            return false;
        }


    }

    private String get_Name(Context context, String Number) {


        try {

            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(Number));
            Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME,}, null, null, null);

            if (c.moveToFirst()) {

                return c.getString(0);
            } else {
                return Number;

            }

        } catch (Exception e) {
            return Number;
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

    public void notification(String Not_Title, String text) {

        try {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    context).setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(Not_Title)
                    .setContentText(text)
                    .setAutoCancel(true);
            Intent intent = new Intent(context, MyActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context, num, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pi);
            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(num, mBuilder.build());
            num++;
        } catch (Exception e) {
            e.printStackTrace();
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
            sms.sendTextMessage(incomingNumber, null, temple + " " + duration, null, null);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public Long getaLong(String incomingNumber) {

        Long parse1 = null;

        try {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            String s = remove_plus(incomingNumber);
            parse1 = numberFormat.parse(s).longValue();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse1;
    }

    private class Custum_StateListeners extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            if (state == TelephonyManager.CALL_STATE_RINGING) {

                Long parse1 = getaLong(incomingNumber);

                if (p_calls && parse1 < 0) {

                    all_Call();

                } else if (parse1 > 0) {

                    int key_name = pref.getInt("key_name", 0);
                    switch (key_name) {

                        case 0:      // accept all

                            break;

                        case 1:     // block all

                            try {
                                if (notification) {
                                    notification("Call Out", "Call Blocked from " + get_Name(context, incomingNumber));
                                }
                                all_Call();


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            break;

                        case 2:     // allow only contact
                            try {
                                boolean lookup = get_lookup(context, incomingNumber);
                                if (lookup == false) {

                                    if (notification) {
                                        notification("Call Out", "Call Blocked from " + get_Name(context, incomingNumber));
                                    }

                                    all_Call();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            break;

                        case 3:   // black list

                            try {

                                Contact contact = dataBaseHelper.getContact(incomingNumber);

                                if (contact.get_is_Call_block()) {

                                    if (notification) {
                                        notification("Call Out", "Call Blocked from " + get_Name(context, incomingNumber));
                                    }

                                    all_Call();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            break;

                        case 4:    // do not disturb

                            try {
                                send_sms(incomingNumber, temple, duration);
                                if (notification) {
                                    notification("Call Out", "Call Blocked from " + get_Name(context, incomingNumber));
                                }

                                all_Call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;

                    }
                }


            }
        }
    }

}

