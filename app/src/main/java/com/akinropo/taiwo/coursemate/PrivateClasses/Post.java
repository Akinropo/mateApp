package com.akinropo.taiwo.coursemate.PrivateClasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TAIWO on 1/21/2017.
 */
public class Post implements Parcelable {
    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
    int id;
    String flag;
    String message;
    int composer;
    String composername;
    int likes;
    int unlikes;
    String photo;
    String photocaption;
    String time_stamp;
    User user = null;
    boolean isLiked;
    boolean isDisliked;

    protected Post(Parcel in) {
        id = in.readInt();
        flag = in.readString();
        message = in.readString();
        composer = in.readInt();
        composername = in.readString();
        likes = in.readInt();
        unlikes = in.readInt();
        photo = in.readString();
        photocaption = in.readString();
        time_stamp = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        isLiked = in.readByte() != 0;
        isDisliked = in.readByte() != 0;
    }

    public void setIsDisliked(boolean isDisliked) {
        this.isDisliked = isDisliked;
    }

    public boolean isDisliked() {
        return isDisliked;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public int getId() {
        return id;
    }

    public String getFlag() {
        return flag;
    }

    public String getMessage() {
        return message;
    }

    public int getComposerId() {
        return composer;
    }

    public String getComposername() {
        return composername;
    }

    public int getLikes() {
        return likes;
    }

    public int getUnlikes() {
        return unlikes;
    }

    public String getPhoto() {
        return photo;
    }

    public String getPhotocaption() {
        return photocaption;
    }

    public String getTime_stamp() {
        return time_stamp;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(flag);
        dest.writeString(message);
        dest.writeInt(composer);
        dest.writeString(composername);
        dest.writeInt(likes);
        dest.writeInt(unlikes);
        dest.writeString(photo);
        dest.writeString(photocaption);
        dest.writeString(time_stamp);
        dest.writeParcelable(user, flags);
        dest.writeByte((byte) (isLiked ? 1 : 0));
        dest.writeByte((byte) (isDisliked ? 1 : 0));
    }
}
