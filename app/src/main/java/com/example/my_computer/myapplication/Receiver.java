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
import android.support.v4.app.TaskStackBuilder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Receiver extends BroadcastReceiver {

    static int num;
    static Custum_StateListeners custum_stateListeners;
    Context context;
    TelephonyManager telephony;
    DataBaseHelper dataBaseHelper;
    SharedPreferences pref;
    Intent intent;
    boolean notification;
    String temple;
    String duration;
    private boolean p_calls;
    private boolean sms_enable;


    public void onReceive(Context context, Intent intent) {

        try {

//            Toast.makeText(context, "Started ", Toast.LENGTH_LONG).show();
            this.intent = intent;
            this.context = context;
            dataBaseHelper = new DataBaseHelper(context);
            telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            shared(context);
            pref = context.getSharedPreferences("MyPref", 0);
//          custum_stateListeners == null &&

            if (custum_stateListeners == null) {

                custum_stateListeners = new Custum_StateListeners();
                telephony.listen(custum_stateListeners, PhoneStateListener.LISTEN_CALL_STATE);
            } else if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

                String[] phonenumber = null;
                int key_name = 0;
                try {
                    phonenumber = sms_getnumber(intent);
                    key_name = pref.getInt("key_name", 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                switch (key_name) {
                    case 0: // accept all

                        break;
                    case 1: // block all

                        try {
                            final Bundle bundle = intent.getExtras();
                            if (bundle != null) {
                                if (notification) {
                                    notification("Message Blocked from " + get_Name(context, phonenumber[0]), phonenumber[1]);
                                }
                                abortBroadcast();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;

                    case 2: // allow only contact
                        try {

                            if (phonenumber != null) {

                                if (!(get_lookup(context, phonenumber[0]))) {

                                    if (notification) {
                                        notification("Message Blocked from " + get_Name(context, phonenumber[0]), phonenumber[1]);
                                    }

                                    abortBroadcast();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3: // black list
                        try {
                            if (phonenumber != null) {

                                Contact contact = dataBaseHelper.getContact(remove_plus(phonenumber[0]));

                                if (contact.get_is_Msg_block()) {
                                    if (notification) {

                                        notification("Message Blocked from " + get_Name(context, phonenumber[0]), phonenumber[1]);

                                    }
                                    abortBroadcast();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4:// do not disturb

                        try {
                            if (phonenumber != null) {


                                if (sms_enable && notification) {
                                    send_sms(phonenumber[0], temple, duration);
                                    notification("Message Blocked from " + get_Name(context, phonenumber[0]), phonenumber[1]);
                                } else if (notification) {
                                    send_sms(phonenumber[0], temple, duration);
                                }
                                abortBroadcast();

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

    public String[] sms_getnumber(Intent intent) {
        try {
            final Bundle bundle = intent.getExtras();
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getMessageBody();
                    return new String[]{phoneNumber, message};

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

    public void notification(String title, String text) {
        try {


//            RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.cus_notifiaction);
//            contentView.setImageViewResource(R.id.imageView, R.drawable.ic_launcher);
//            contentView.setTextViewText(R.id.textView, "Call Out");
//            contentView.setTextViewText(R.id.textView2, title);
//            contentView.setTextViewText(R.id.textView3, text);
//
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
//                    context)
//                    .setAutoCancel(true).setContent(contentView);
//
//
//            Intent intent = new Intent(context, MyActivity.class);
//            PendingIntent pi = PendingIntent.getActivity(context, num, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            mBuilder.setContentIntent(pi);
//            NotificationManager mNotificationManager = (NotificationManager) context
//                    .getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotificationManager.notify(num, mBuilder.build());
//            num++;


            RemoteViews expandedView = new RemoteViews(context.getPackageName(),
                    R.layout.cus_notifiaction);
            expandedView.setTextViewText(R.id.title, "Call Out");
            expandedView.setTextViewText(R.id.sub_title_1, "saa");
            expandedView.setTextViewText(R.id.sub_title_2, "asa");

            //     Intent viewIntent = new Intent(context.getApplicationContext(), MyActivity.class);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context.getApplicationContext())
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(title)
                            .setContentText(text).setContent(expandedView);

// Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(context.getApplicationContext(), MyActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context.getApplicationContext());
// Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MyActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
            mNotificationManager.notify(0, mBuilder.build());


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

            Toast.makeText(context, "Sending sms", Toast.LENGTH_LONG)
                    .show();

        } catch (Exception e) {
            Toast.makeText(context, "Unable to Send sms", Toast.LENGTH_LONG)
                    .show();
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

                if (p_calls && parse1 == 0) {

                    all_Call();

                } else if (parse1 > 0) {


                    int key_name = pref.getInt("key_name", 0);

                    switch (key_name) {
                        case 0: // accept all
                            break;
                        case 1: // block all
                            try {
                                if (notification) {

                                    notification("Call Out", "Call Blocked from " + get_Name(context, incomingNumber));

                                }
                                all_Call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case 2: // allow only contact
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
                        case 3: // black list
                            try {
                                Contact contact = dataBaseHelper.getContact(remove_plus(incomingNumber));
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
                        case 4: // do not disturb
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