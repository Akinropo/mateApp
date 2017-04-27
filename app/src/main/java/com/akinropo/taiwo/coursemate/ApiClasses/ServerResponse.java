package com.akinropo.taiwo.coursemate.ApiClasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.akinropo.taiwo.coursemate.PrivateClasses.Course;
import com.akinropo.taiwo.coursemate.PrivateClasses.Post;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by TAIWO on 1/6/2017.
 */
public class ServerResponse implements Parcelable {

    boolean error; //api varible for response error or not.
    User data; //api varible for fetch single user;
    String message; //api variable for server's attach message/details.
    List<User> coursemates;  //api variable for user's coursemate list.
    List<Course> courseList; //api variable for user's courselist.
    List<GroupRes> groups; //api variable for user's grouplist;
    List<Post> posts; //api variable for post list;
    int status; //api varible to check friend status of two users
    boolean hasNext; //api variable to determine if the request still has a next page.
    int last_page; //api variable to get the last page requested on the server.
    GroupRes newGroup; //api variable to get the just created group;


    protected ServerResponse(Parcel in) {
        error = in.readByte() != 0;
        data = in.readParcelable(User.class.getClassLoader());
        message = in.readString();
        coursemates = in.createTypedArrayList(User.CREATOR);
        courseList = in.createTypedArrayList(Course.CREATOR);
        groups = in.createTypedArrayList(GroupRes.CREATOR);
        posts = in.createTypedArrayList(Post.CREATOR);
        status = in.readInt();
        hasNext = in.readByte() != 0;
        last_page = in.readInt();
        newGroup = in.readParcelable(GroupRes.class.getClassLoader());
    }

    public static final Creator<ServerResponse> CREATOR = new Creator<ServerResponse>() {
        @Override
        public ServerResponse createFromParcel(Parcel in) {
            return new ServerResponse(in);
        }

        @Override
        public ServerResponse[] newArray(int size) {
            return new ServerResponse[size];
        }
    };

    public GroupRes getNewGroup() {
        return newGroup;
    }

    public int getLast_page() {
        return last_page;
    }



    public boolean isHasNext() {
        return hasNext;
    }

    public List<Post> getPosts() {
        return posts;
    }



    public int getStatus() {
        return status;
    }

    public List<GroupRes> getGroups() {
        return groups;
    }

    public List<User> getCoursemates() {
        return coursemates;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public String getMessage() {
        return message;
    }


    public boolean isError() {
        return error;
    }

    public User getData() {
        return data;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (error ? 1 : 0));
        dest.writeParcelable(data, flags);
        dest.writeString(message);
        dest.writeTypedList(coursemates);
        dest.writeTypedList(courseList);
        dest.writeTypedList(groups);
        dest.writeTypedList(posts);
        dest.writeInt(status);
        dest.writeByte((byte) (hasNext ? 1 : 0));
        dest.writeInt(last_page);
        dest.writeParcelable(newGroup, flags);
    }
}
