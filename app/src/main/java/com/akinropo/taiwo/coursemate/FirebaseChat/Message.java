package com.akinropo.taiwo.coursemate.FirebaseChat;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

@IgnoreExtraProperties
public class Message {
    public String senderPhoto;
    public String body;
    public String senderMajor;
    public HashMap<String, Object> timestampCreated;
    public String photoUrl;
    public int receiverId;
    public int senderId;
    public int groupOwner;
    public int flag;
    public String senderName; //this is to be use in groupchat message

    @SuppressWarnings("unused") //Used by Firebase
    public Message() {
        timestampCreated = new HashMap<>();
        timestampCreated.put("timestamp", ServerValue.TIMESTAMP);
    }

    @Exclude
    public long getTimestamp() {
        if (timestampCreated.get("timestamp") != null)
            return (long) timestampCreated.get("timestamp");
        else return 6554535;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public String getSenderMajor() {
        return senderMajor;
    }

    public void setSenderMajor(String senderMajor) {
        this.senderMajor = senderMajor;
    }

    public int getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(int groupOwner) {
        this.groupOwner = groupOwner;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhoto() {
        return senderPhoto;
    }

    public void setSenderPhoto(String senderPhoto) {
        this.senderPhoto = senderPhoto;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }


    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }


    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;

    }

}
