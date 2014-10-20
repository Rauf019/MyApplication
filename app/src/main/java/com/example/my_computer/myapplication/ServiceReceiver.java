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
import android.widget.Toast;

import java.lang.reflect.Method;


public class ServiceReceiver extends BroadcastReceiver {


    Context context;
    TelephonyManager telephony;
    DataBaseHelper dataBaseHelper;


    public void onReceive(Context context, Intent intent) {

        this.context = context;

        dataBaseHelper = new DataBaseHelper(context);


        telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);


        if (intent.getAction() != "android.intent.action.PHONE_STATE") {

            Sms(intent);
        }


        MyPhoneStateListener listener = new MyPhoneStateListener();

        telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    public void Sms(Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    Contact contact = dataBaseHelper.getContact(phoneNumber);

                    if (contact.get_is_Msg_block()) {
                        notification("Call Blocker", "Message Block " + phoneNumber);
                        abortBroadcast();
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "Unable to Delete Sms ", Toast.LENGTH_SHORT).show();

        }
    }

    public void onDestroy() {
        telephony.listen(null, PhoneStateListener.LISTEN_NONE);
    }

    public void notification(String Not_Title, String text) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.call)
                .setContentTitle(Not_Title).setContentText(text)
                .setAutoCancel(true);
        Intent intent = new Intent(context, MyActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void Call_Filter(String incomingNumber) {


        try {
            Contact contact = dataBaseHelper.getContact(incomingNumber);

            if (contact.get_is_Call_block()) {

                notification("Call Blocker", "Call Block from" + incomingNumber);
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

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Unable to Block Call", Toast.LENGTH_SHORT).show();


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
                    Call_Filter(incomingNumber);

                    break;

            }
        }

    }
}

