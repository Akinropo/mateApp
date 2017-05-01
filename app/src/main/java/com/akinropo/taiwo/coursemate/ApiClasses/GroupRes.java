package com.akinropo.taiwo.coursemate.ApiClasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TAIWO on 1/18/2017.
 */
public class GroupRes implements Parcelable {
    public static final Creator<GroupRes> CREATOR = new Creator<GroupRes>() {
        @Override
        public GroupRes createFromParcel(Parcel in) {
            return new GroupRes(in);
        }

        @Override
        public GroupRes[] newArray(int size) {
            return new GroupRes[size];
        }
    };
    @SerializedName("group_id")
    int groupId;
    @SerializedName("group_name")
    String groupName;
    @SerializedName("is_owner")
    boolean isOwner;
    public GroupRes(Parcel in) {
        groupId = in.readInt();
        groupName = in.readString();
        isOwner = in.readByte() != 0;
    }

    public GroupRes() {

    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(groupId);
        dest.writeString(groupName);
        dest.writeByte((byte) (isOwner ? 1 : 0));
    }
}
