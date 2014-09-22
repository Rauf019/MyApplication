package com.example.my_computer.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

import ClassLib.Contact;

interface ITelephony {
    boolean endCall();

    void answerRingingCall();

    void silenceRinger();
}

public class ServiceReceiver extends BroadcastReceiver {

    List<Contact> contacts;
    Context context;
    private List<messageData> datas;
    private String id;
    private String add;
    private TelephonyManager telephony;
    private SmsMessage[] msgs;

    public void onReceive(Context context, Intent intent) {
        this.context = context;
        telephony = (TelephonyManager) context

                .getSystemService(Context.TELEPHONY_SERVICE);

//        MainActivity.baseHelper.addContact(new Contact("033", "true", "true",
//                "true"));
//        List<Contact> contacts = MainActivity.baseHelper.getAllContacts();

        Sms(intent, contacts);

        MyPhoneStateListener listener = new MyPhoneStateListener();
        telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    public void Sms(Intent intent, List<Contact> contacts) {
        if (intent != null) {
            String action = intent.getAction();
            if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
                Bundle extras = intent.getExtras();
                if (extras != null) {

                    try {
                        Object[] pdus = (Object[]) extras.get("pdus");
                        msgs = new SmsMessage[pdus.length];
                        for (int i = 0; i < msgs.length; i++) {
                            msgs[i] = SmsMessage
                                    .createFromPdu((byte[]) pdus[i]);
                            String msg_from = msgs[i].getOriginatingAddress();

                            for (int j = 0; j < contacts.size(); j++)

                            {
                                if (msg_from.contains(contacts.get(j)
                                        .get_phoneNumber()))

                                {
                                    abortBroadcast();
                                }
                            }

                        }
                    } catch (Exception e) {
                        Log.d("Exception caught", e.getMessage());
                    }
                }
            }
        }
    }

    public void onDestroy() {
        telephony.listen(null, PhoneStateListener.LISTEN_NONE);
    }

    public void notification(String Not_Title, String text) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(Not_Title).setContentText(text)
                .setAutoCancel(true);
        Intent intent = new Intent(context, MyActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void Call_Filter() {
        try {

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
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
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

    class MyPhoneStateListener extends PhoneStateListener {

        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d("DEBUG", "CALL_STATE_IDLE");

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d("DEBUG", "CALL_STATE_OFFHOOK");

                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d("DEBUG", "CALL_STATE_RINGING");
                    notification("Call Blocker", "Call Block from ");
                    Call_Filter();
                    break;

            }
        }

    }


}

class messageData {

    long _id, _thread_id;
    String _Add, _Body;

    public messageData(long _id, long _thread_id, String _Add, String _Body) {
        super();
        this._id = _id;
        this._Add = _Add;
        this._Body = _Body;
        this._thread_id = _thread_id;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String get_Add() {
        return _Add;
    }

    public void set_Add(String _Add) {
        this._Add = _Add;
    }

    public String get_Body() {
        return _Body;
    }

    public void set_Body(String _Body) {
        this._Body = _Body;
    }

    public long get_thread_id() {
        return _thread_id;
    }

    public void set_thread_id(long _thread_id) {
        this._thread_id = _thread_id;
    }

}