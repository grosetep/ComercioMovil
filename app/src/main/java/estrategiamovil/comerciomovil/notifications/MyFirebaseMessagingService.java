package estrategiamovil.comerciomovil.notifications;

/**
 * Created by administrator on 25/08/2016.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.IntentService;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.ui.activities.FCMPluginActivity;
import estrategiamovil.comerciomovil.ui.activities.ReplyActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static String KEY_REPLY = "key_reply_message";
    public static String REPLY_ACTION = "MyFirebaseMessagingService";
    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = new HashMap<>();
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //Log.d(TAG, "onMessageReceived From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage. getData());
            data = remoteMessage.getData();
            for (String key : data.keySet()) {
                String value = data.get(key);
                //Log.d(TAG, "\tKey: " + key + " Value: " + value);
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        sendNotification(data);
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param data FCM message body received.
     */
    private void sendNotification(Map<String, String> data ) {

        //processing data json response
        NotificationCompat.Builder notificationBuilder = null;
        JSONObject object = null;
        int request_code = 0;
        String process_data = data.get("tickerText");
        if (process_data!=null && process_data.length()>0){
            try {
                object = new JSONObject(process_data);
                if (object!=null){
                    //Log.d(TAG,"type_notification from json:"+object.get("type_notification"));
                    request_code = Integer.parseInt(object.get("type_notification").toString()!=null?Constants.notifycation_types.get(object.get("type_notification").toString()).getPredefinedId():"0");
                }else{
                    object =null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                object = null;
            }
        }else{//create simple notification
            object = null;
        }


        if (object!=null) {
            try {
                if (object.get("type_notification").toString().equals("customer")) {
                        notificationBuilder = makeReplyNotification(object, data);
                }else if(object.get("type_notification").toString().equals("publication")) {
                        //notify user only if offer is for his city
                        String localState = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.cityUser);
                        String localCountry = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.countryUser);
                        String[] temp_values_cities = object.get("ids_states_publication").toString().split(",");
                        if (temp_values_cities!=null && temp_values_cities.length>0) {
                            ArrayList<String> temp = new ArrayList(Arrays.asList(temp_values_cities));
                            if ( temp.size()>0) {
                                if (temp.contains(localState) && object.get("idCountry").toString().equals(localCountry)) {
                                    notificationBuilder = makeNotification(object, data);
                                }
                            }
                        }
                }else{
                        notificationBuilder = makeNotification(object, data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            notificationBuilder = makeSimpleNotification(data);
        }

            if (notificationBuilder!=null) {
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(object != null ? request_code : 0, notificationBuilder.build());
            }

    }

    private NotificationCompat.Builder makeReplyNotification(JSONObject object, Map<String, String> data){
        Log.d(TAG,"makeReplyNotification:"+object.toString());
        NotificationCompat.Builder mBuilder = null;
        if (object!=null) {
            // Key for the string that's delivered in the action's intent.
            String KEY_TEXT_REPLY = "key_text_reply";
            String replyLabel = getResources().getString(R.string.action_notify_reply);
            RemoteInput remoteInput = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
                remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                        .setLabel(replyLabel)
                        .build();


            PendingIntent replyIntent = getReplyPendingIntent(object);
            NotificationCompat.Action replyAction =
                    new NotificationCompat.Action.Builder(R.drawable.ic_reply,
                            replyLabel, replyIntent)
                            .addRemoteInput(remoteInput)
                            .setAllowGeneratedReplies(true)
                            .build();
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(data.get("title"))
                    .setContentText(data.get("message"))
                    .setShowWhen(true)
                    .setSmallIcon(R.drawable.ic_cloud)
                    .setColor(0x0288D1)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .addAction(replyAction); // reply action from step b above
               return mBuilder;
            }
        }
        return makeSimpleNotification(data);
    }
    private PendingIntent getReplyPendingIntent(JSONObject object){
        Intent intent;
        String type_notification = "";
        String id_notification = "0";
        String id_merchant = "";
        String id_user = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.localUserId);
        int mNotificationId = 0;
        try {
            type_notification = object.get("type_notification").toString();
            id_notification = Constants.notifycation_types.get(type_notification).getPredefinedId();
            mNotificationId = Integer.parseInt(id_notification);
            id_merchant = object.get("id_merchant").toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


       // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // start a
            // (i)  broadcast receiver which runs on the UI thread or
            // (ii) service for a background task to b executed , but for the purpose of
            // this codelab, will be doing a broadcast receiver
           /* intent = NotificationBroadcastReceiver.getReplyMessageIntent(this, mNotificationId, 0,id_merchant,id_user);
            return PendingIntent.getBroadcast(getApplicationContext(), 100, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        } else {*/
            // start your activity for Android M and below
            intent = ReplyActivity.getReplyMessageIntent(this, mNotificationId, 0,id_merchant);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
       // }
    }
    public static CharSequence getReplyMessage(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_REPLY);
        }
        return null;
    }
    private NotificationCompat.Builder makeNotification(JSONObject object, Map<String, String> data){
        String type_notification = "";
        String id_notification = "0";
        String company = "";
        int request_code = 0;
        if (object!=null) {
            try {
                type_notification = object.get("type_notification").toString();
                company = object.get("company").toString();
                id_notification = Constants.notifycation_types.get(type_notification).getPredefinedId();
                request_code = Integer.parseInt(id_notification);
            } catch (JSONException e) {
                e.printStackTrace();
                return makeSimpleNotification(data);
            }

            Intent intent = new Intent(this, FCMPluginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,request_code, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            //Assign inbox style notification accordance type notification on tickerText field
            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText(data.get("message"));
            bigText.setBigContentTitle(data.get("title"));
            bigText.setSummaryText("By: " + company);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_cloud)
                    .setColor(0x0288D1)
                    .setContentTitle(data.get("title"))
                    .setContentText(data.get("message"))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setStyle(bigText);
            return notificationBuilder;


        }else{
            return makeSimpleNotification(data);
        }
    }
    private NotificationCompat.Builder makeSimpleNotification(Map<String, String> data){
        Intent intent = new Intent(this, FCMPluginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);



        //Assign inbox style notification accordance type notification on tickerText field
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(data.get("message"));
        bigText.setBigContentTitle(data.get("title"));
        bigText.setSummaryText("By: "+getResources().getString(R.string.app_name));

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_cloud)
                .setColor(0x0288D1)
                .setContentTitle(data.get("title"))
                .setContentText(data.get("message"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(bigText);
        return notificationBuilder;
    }
}

