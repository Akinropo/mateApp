package com.akinropo.taiwo.coursemate.FirebaseChat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.AllActivities.FireBaseActivity;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.ApiClasses.GroupRes;
import com.akinropo.taiwo.coursemate.PrivateClasses.Config;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.PrivateClasses.NotificationUtils;
import com.akinropo.taiwo.coursemate.PrivateClasses.RecentChatManager;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TAIWO on 1/30/2017.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;
    private MyPreferenceManager manager;
    private RecentChatManager recentChatManager;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e("from: ",remoteMessage.getFrom());
        if(remoteMessage == null) return;
        if(remoteMessage.getNotification() != null){
            handleNotification(remoteMessage.getNotification().getBody());
            Log.e("notification body: ", remoteMessage.getNotification().getBody());
            //Toast.makeText(getApplicationContext(), "notification body is my guy", Toast.LENGTH_SHORT).show();
        }
        if(remoteMessage.getData().size() > 0){
            //handleDataMessage(new JSONObject(remoteMessage.getData().toString()));
            //this.sendNotification(new JSONObject(remoteMessage.getData().toString()));
            determine(new JSONObject(remoteMessage.getData()));
            Log.e("notification body: ","hi i recieve a push notification right now.");
        }
        Log.e("notification body: ", "hi i recieve a notification right now.");

    }
    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }
    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            //String title = data.getString("title");
            String title = "new message";
            String message = data.getString(EndPoints.TOPIC_MESSAGE);
            boolean isBackground = data.getBoolean("is_background");
            isBackground = true;
            String imageUrl = data.getString(EndPoints.PHOTO_BASE_URL + EndPoints.TOPIC_SENDER_PHOTO);
            String timestamp = data.getString("timestamp");
            //the line to rewrite timestamp
            //timestamp = "12938474748";
            JSONObject payload = data.getJSONObject("data");

            ///Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            //Log.e(TAG, "timestamp: " + timestamp);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), FireBaseActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
    private void sendNotification(JSONObject jsonObject) {
        GroupRes group  = null;
        User user = null;
        //Toast.makeText(MyFirebaseMessagingService.this, "Executing send notification in Service", Toast.LENGTH_SHORT).show();
        try {
            JSONObject data = jsonObject.getJSONObject("data");
            String senderFullname = data.getString(EndPoints.TOPIC_SENDER_NAME);
            int senderId = data.getInt(EndPoints.TOPIC_SENDER_ID);
            String message = data.getString(EndPoints.TOPIC_MESSAGE);
            String senderPhoto = data.getString(EndPoints.PHOTO_BASE_URL + EndPoints.TOPIC_SENDER_PHOTO);
            int flag = data.getInt(EndPoints.TOPIC_FLAG);
            if(flag == EndPoints.TOPIC_FLAG_GROUP){
                group = new GroupRes();
                group.setIsOwner(false);
                group.setGroupId(senderId); // make sender id the group id in groups
                group.setGroupName(senderFullname);
                updateNotification(senderId, senderFullname, message, true, group, user);
            }else if(flag == EndPoints.TOPIC_FLAG_MESSAGE){
                user = new User();
                user.setFirstname(senderFullname);
                user.setId(senderId);
                updateNotification(senderId, senderFullname, message, true,group,user);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateNotification(int senderId, String senderFullName, String message, boolean needSound,GroupRes group,User user) {
        /*if (DialogPresenter.getCurrentPeerId() != null) {
            return;
        }*/
        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.putExtra(DialogActivity.KEY_PEER_ID, senderId);
        if(group != null){
            intent.putExtra(EndPoints.PASSED_GROUP,group);
        }else if(user != null){
            intent.putExtra(EndPoints.PASSED_USER,user);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(senderFullName)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        if (needSound) {
            notificationBuilder.setSound(defaultSoundUri);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(senderFullName.hashCode(), notificationBuilder.build());
    }
    public void determine(JSONObject jsonObject){
        Intent resultIntent;
        try {
            int flag = jsonObject.getInt(EndPoints.TOPIC_FLAG);
            int id = jsonObject.getInt(EndPoints.TOPIC_SENDER_ID);
            String name = jsonObject.getString(EndPoints.TOPIC_SENDER_NAME);
            String message = jsonObject.getString(EndPoints.TOPIC_MESSAGE);
            String timestamp = jsonObject.getString(EndPoints.TOPIC_TIMESTAMP);
            recentChatManager = new RecentChatManager(this);
            Log.e("notification body: ","hi,the values "+name+" "+message+" "+timestamp+" "+id+" "+flag);
            if(flag == EndPoints.TOPIC_FLAG_GROUP){
                //create a new group class and also store the value in table if exist add the number of unread message
                //then broadcast to the recent chats fragments
                int groupOwner = jsonObject.getInt(EndPoints.TOPIC_GROUP_OWNER);
                manager = new MyPreferenceManager(getApplicationContext());
                boolean owner  = groupOwner == manager.getId();
                GroupRes group = new GroupRes();
                group.setGroupId(id);
                group.setIsOwner(owner);
                group.setGroupName(name); //group class created now create
                resultIntent = ChatActivity.createIntentForGroup(getApplicationContext(), group);
                recentChatManager.addRecent(flag,id,name,1," ",timestamp,groupOwner);

            }else {
                String major = jsonObject.getString(EndPoints.TOPIC_SENDER_MAJOR);
                String photoUrl = jsonObject.getString(EndPoints.TOPIC_SENDER_PHOTO);
                String[] names = name.split(" ",2);
                User user = new User();
                user.setId(id);
                user.setPhoto(photoUrl);
                user.setMajor(major);
                user.setFirstname(names[0]);
                user.setOthername(names[1]);
                resultIntent = ChatActivity.createIntentFor(getApplicationContext(),user);
                resultIntent.putExtra(EndPoints.PASSED_ID,user.getId());
                recentChatManager.addRecent(flag,id,name,1,photoUrl,timestamp,0);
            }

            if(NotificationUtils.isAppIsInBackground(getApplicationContext())){
                //the app is in background, play notification sound and create notifictaion tray.
                Intent i = new Intent(EndPoints.NEW_MESSAGE_BROADCAST); //broadcast reciever's intent.
                notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
                showNotificationMessage(getApplicationContext(),name,message,timestamp,resultIntent);
            }else {
                //play notification sound and send a broadcast of new message.
                Intent i = new Intent(EndPoints.NEW_MESSAGE_BROADCAST);//broadcast receiver's intent.
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
                notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
                //showNotificationMessage(getApplicationContext(),name,message,timestamp,resultIntent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
