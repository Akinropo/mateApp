package com.akinropo.taiwo.coursemate.PrivateClasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.akinropo.taiwo.coursemate.ApiClasses.GroupRes;

/**
 * Created by TAIWO on 3/6/2017.
 */
public class RecentChatModel implements Parcelable {
    int flag; //this shows whether it's a freechat,groupchat or coursemate chats
    int id; //this is the user's id or group's id;
    String name;//this is the user's name or group's name
    int unreadCount; //this is the unread messages count;
    String photoUrl; //this is the user's photo url;
    String timeStamp; //this is the message's timestamp.
    int groupOwner; //this is the group's owner id;


    protected RecentChatModel(Parcel in) {
        flag = in.readInt();
        id = in.readInt();
        name = in.readString();
        unreadCount = in.readInt();
        photoUrl = in.readString();
        timeStamp = in.readString();
        groupOwner = in.readInt();
    }

    public static final Creator<RecentChatModel> CREATOR = new Creator<RecentChatModel>() {
        @Override
        public RecentChatModel createFromParcel(Parcel in) {
            return new RecentChatModel(in);
        }

        @Override
        public RecentChatModel[] newArray(int size) {
            return new RecentChatModel[size];
        }
    };

    public int getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(int groupOwner) {
        this.groupOwner = groupOwner;
    }

    public RecentChatModel(int flag, int id, String name, int unreadCount, String photoUrl, String timeStamp,int groupOwner) {
        this.flag = flag;
        this.id = id;
        this.name = name;
        this.unreadCount = unreadCount;
        this.photoUrl = photoUrl;
        this.timeStamp = timeStamp;
        this.groupOwner = groupOwner;
    }
    public static User converToUser(RecentChatModel recentChatModel){
        User user = new User();
        user.setId(recentChatModel.getId());
        user.setPhoto(recentChatModel.getPhotoUrl());
        String[] names = recentChatModel.getName().split(" ",2);
        user.setFirstname(names[0]);
        user.setOthername(names[1]);
        return user;
    }
    public static GroupRes convertToGroup(RecentChatModel recentChatModel,int currentUser){
        GroupRes groupRes = new GroupRes();
        groupRes.setGroupId(recentChatModel.getId());
        groupRes.setGroupName(recentChatModel.getName());
        if(recentChatModel.getGroupOwner() == currentUser) groupRes.setIsOwner(true);
        else groupRes.setIsOwner(false);
        return groupRes;
    };



    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(flag);
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(unreadCount);
        dest.writeString(photoUrl);
        dest.writeString(timeStamp);
        dest.writeInt(groupOwner);
    }
}
