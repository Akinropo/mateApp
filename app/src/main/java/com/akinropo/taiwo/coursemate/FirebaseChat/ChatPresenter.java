package com.akinropo.taiwo.coursemate.FirebaseChat;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.ApiClasses.ApiInterface;
import com.akinropo.taiwo.coursemate.ApiClasses.ApiRetrofit;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.ApiClasses.GroupRes;
import com.akinropo.taiwo.coursemate.ApiClasses.ServerResponse;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.PrivateClasses.RecentChatManager;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;

import com.google.android.gms.common.api.Api;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ServerSocketFactory;
import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by TAIWO on 2/19/2017.
 */
public class ChatPresenter  {
    FirebaseChatDatabase firebaseChatDatabase;
    int senderId,receiverId;
    ChatMessageListener messageListener;
    public static int CHAT_MESSAGE_LIMIT = 1000;
    ChatView chatView;
    User friendChatter;
    GroupRes groupRes;
    DatabaseReference chatReference;
    ChildEventListener childEventListener;
    Context context;
    MyPreferenceManager manager;
    int MESSAGE_FLAG;
    RecentChatManager recentChatManager;
    Bundle userArgument;
    FragmentManager fragmentManager;


    public ChatPresenter(){
    }
    public ChatPresenter(int senderId,int receiverId,ChatView chatView,User friendChatter,Bundle userArgument,FragmentManager manager){
        this.firebaseChatDatabase = FirebaseChatDatabase.getInstance();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.chatView = chatView;
        this.friendChatter = friendChatter;
        this.userArgument = userArgument;
        this.fragmentManager = manager;
        this.MESSAGE_FLAG = EndPoints.TOPIC_FLAG_MESSAGE;
    }
    public ChatPresenter(int senderId,ChatView chatView,GroupRes groupRes){
        this.firebaseChatDatabase = FirebaseChatDatabase.getInstance();
        this.senderId = senderId;
        this.receiverId = groupRes.getGroupId();
        this.chatView = chatView;
        this.groupRes = groupRes;
        this.MESSAGE_FLAG = EndPoints.TOPIC_FLAG_GROUP;
    }
    public void setContext(Context context){
        this.context = context;
        manager = new MyPreferenceManager(context);
        recentChatManager = new RecentChatManager(context);
        if(this.MESSAGE_FLAG == EndPoints.TOPIC_FLAG_FREECHAT) Toast.makeText(context,"This is a freechat with user "+friendChatter.getFirstname(),Toast.LENGTH_SHORT).show();
    }
    public void setMESSAGE_FLAG(int MESSAGE_FLAG) {
        this.MESSAGE_FLAG = MESSAGE_FLAG;
    }


    public void attachMessageListener(ChatMessageListener chatMessageListener){
        this.messageListener = chatMessageListener;
    }
    public void detachMessageListener(){
        this.messageListener = null;
    }

    public void init(){
        if(friendChatter != null){
            getChat();
            setChatView();
        }else if(groupRes != null){
            getGroupChat();
            setGroupView();
        }

    }
    public void getChat(){
        chatReference = firebaseChatDatabase.getDbforChat(this.senderId, this.receiverId);
        
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                chatView.addMessage(message);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        chatReference.limitToLast(CHAT_MESSAGE_LIMIT)
                .addChildEventListener(this.childEventListener);
        resetCount(friendChatter.getId(),friendChatter.getFirstname()+" "+friendChatter.getOthername(),friendChatter.getPhoto(),0);
    }
    public void getGroupChat(){
        chatReference = firebaseChatDatabase.getDbforGroup(groupRes.getGroupId() + "");
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                chatView.addMessage(message);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        chatReference.limitToLast(CHAT_MESSAGE_LIMIT).addChildEventListener(childEventListener);
        int ow = 0; //resets the count for the new messages to 0.
        if(groupRes.isOwner()) ow = manager.getId();
        resetCount(groupRes.getGroupId(), groupRes.getGroupName(), "null", ow); //this calls the reset method
    }
    public void sendMessage(int sender,int receiver,String body){
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setBody(body);
        message.setPhotoUrl(" ");
        message.setFlag(this.MESSAGE_FLAG);
        chatReference.push().setValue(message);
        //sendNotification(message, EndPoints.TOPIC_FLAG_MESSAGE);
        //sendToQueue(message, this.MESSAGE_FLAG);
       // Toast.makeText(context.getApplicationContext(),"Done pushing,now call sendNotifyApi",Toast.LENGTH_SHORT).show();
        sendNotifyApi(message, this.MESSAGE_FLAG);
    }
    public void sendMessagetoGroup(String senderName,String senderPhoto,String body){
        Message message = new Message();
        message.setBody(body);
        message.setSenderName(senderName);
        message.setSenderPhoto(senderPhoto);
        message.setSenderId(this.senderId);
        chatReference.push().setValue(message);
        //sendNotification(message, EndPoints.TOPIC_FLAG_GROUP);
        sendToQueue(message, this.MESSAGE_FLAG);
        sendNotifyApi(message, this.MESSAGE_FLAG);

    }
    public void setChatView(){
        chatView.setTitle(friendChatter.getFirstname() + " " + friendChatter.getOthername());
        chatView.enableInteraction();
        chatView.setUser(friendChatter);
        chatView.showAddMembersButton(false, null); //hide the add member button
        chatView.showUserProfile(userArgument,fragmentManager);
    }
    public void setGroupView(){
        chatView.setTitle(groupRes.getGroupName());
        chatView.enableInteraction();
        chatView.showAddMembersButton(groupRes.isOwner(), this.groupRes);
    }
    public void removeChat(){
        chatView.detach();
        detachMessageListener();
        chatReference.removeEventListener(this.childEventListener);
    }
    private void sendNotification(final Message messageEntity, final int flag) {
        //send Push Notification
        if(flag != EndPoints.TOPIC_FLAG_GROUP){
            messageEntity.setSenderPhoto(manager.getLoggedPhoto());
        }
        AsyncTask<String ,String, String> t = new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                HttpsURLConnection connection = null;
                try {
                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    //Put below you FCM API Key instead
                    connection.setRequestProperty("Authorization", "key="
                            + context.getString(R.string.fcm_api_key));

                    JSONObject root = new JSONObject();
                    JSONObject data = new JSONObject();
                    data.put(EndPoints.TOPIC_MESSAGE, messageEntity.getBody());
                    data.put(EndPoints.TOPIC_SENDER_ID, messageEntity.getSenderId());
                    data.put(EndPoints.TOPIC_SENDER_NAME,messageEntity.getSenderName());
                    data.put(EndPoints.TOPIC_TIMESTAMP, ServerValue.TIMESTAMP);
                    if(messageEntity.getSenderPhoto() != null){
                        data.put(EndPoints.TOPIC_SENDER_PHOTO,messageEntity.getSenderPhoto());
                    }
                    if(flag == EndPoints.TOPIC_FLAG_GROUP){
                        if(groupRes.isOwner()) data.put(EndPoints.TOPIC_GROUP_OWNER,manager.getId()); //send the id of the sender as the owner of the group
                        else {
                            data.put(EndPoints.TOPIC_GROUP_OWNER,0);
                        }
                    }
                    data.put(EndPoints.TOPIC_FLAG,flag);
                    root.put("data", data);
                    if(flag == EndPoints.TOPIC_FLAG_GROUP){
                        root.put("to", "/topics/group__" + messageEntity.getSenderId());
                    }else {
                        root.put("to", "/topics/user__" + messageEntity.getReceiverId());
                    }

                    byte[] outputBytes = root.toString().getBytes("UTF-8");
                    OutputStream os = connection.getOutputStream();
                    os.write(outputBytes);
                    os.flush();
                    os.close();
                    connection.getInputStream(); //do not remove this line. request will not work without it gg
                   // Toast.makeText(context, "Push message sent to "+messageEntity.getReceiverId(), Toast.LENGTH_SHORT).show();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) connection.disconnect();
                }
                return null;
            }
        };
        t.execute(null, null, null);

    }
    private void sendToQueue(Message mesa,int flag){
        if(flag == EndPoints.TOPIC_FLAG_GROUP){
            if(groupRes.isOwner()) mesa.setGroupOwner(manager.getId());//send the id of the sender as the owner of the group
            else {
                mesa.setGroupOwner(0);
            }
        }
        mesa.setFlag(flag);
        firebaseChatDatabase.getNotificationPath().push().setValue(mesa);
    }
    private void sendNotifyApi(Message message,int flag){
        if(flag == EndPoints.TOPIC_FLAG_GROUP){
            if(groupRes.isOwner()) message.setGroupOwner(manager.getId());//send the id of the sender as the owner of the group
            else {
                message.setGroupOwner(-50);
            }
            message.setSenderPhoto("taiwoIs");//this is just a jargon value. not needed for a group.
            message.setSenderMajor("ceoTaiwo");//this is just a jargon value. not needed for a group.
            message.setReceiverId(this.receiverId);
            message.setSenderId(this.groupRes.getGroupId());
            message.setSenderName(this.groupRes.getGroupName());
        }else {
            message.setSenderMajor(manager.getLoggedMajor());
            message.setSenderPhoto(manager.getLoggedPhoto());
            message.setSenderName(this.manager.getLoggedName());
        }

        message.setFlag(flag);
        apiSendNotify(message);
    }
    public void resetCount(int id,String name,String photo,int groupOwner ){
        recentChatManager.addRecent(MESSAGE_FLAG,id,name,1,photo,System.currentTimeMillis()+"",groupOwner);
        recentChatManager.updateTimestamp(id, "7844884", true);
    }
    public void apiSendNotify(final Message m){
       // Toast.makeText(context.getApplicationContext(),"inside the apiSendNotify",Toast.LENGTH_SHORT).show();
       ApiInterface apiInterface = ApiRetrofit.getClient().create(ApiInterface.class);
       // Toast.makeText(context.getApplicationContext(),"rMajor: "+m.getSenderMajor(),Toast.LENGTH_SHORT).show();
        Call<ServerResponse> sendNotify = apiInterface.sendMessageNotify(m.getReceiverId(),m.getBody(),m.getSenderName(),m.getSenderPhoto(),m.getFlag(),"1233455",m.getSenderId(),m.getGroupOwner(),m.getSenderMajor());
        sendNotify.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                // Toast.makeText(context.getApplicationContext(), response.body().getMessage(),Toast.LENGTH_SHORT).show();
                //Toast.makeText(context.getApplicationContext(),"rId "+m.getReceiverId(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
               // Toast.makeText(context.getApplicationContext(),"Onfailure : "+t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
