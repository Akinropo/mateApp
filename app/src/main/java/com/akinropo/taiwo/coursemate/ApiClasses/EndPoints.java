package com.akinropo.taiwo.coursemate.ApiClasses;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.akinropo.taiwo.coursemate.PrivateClasses.CircleTransform;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;
import com.akinropo.taiwo.coursemate.StorageClasses.FirebasePhotoStorage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import co.mobiwise.materialintro.view.MaterialIntroView;

/**
 * Created by TAIWO on 1/6/2017.
 */
public class EndPoints {

    public static final String PhBASE_URL = "http://192.168.43.226/coursemate2/api/v1/";
    public static final String BASE_URL = "http://imate.herokuapp.com/api/v1/";
    public static final String POST_PHOTO_BASE_URL = "http://10.0.2.2/coursemate2/api/";
    public static final String PHOTO_BASE_URL = "http://imate.herokuapp.com/";
    public static final String WEB_BASE_URL = "http://imate.herokuapp.com/api/v1/";
    public static final String REGISTER = BASE_URL + "/register";
    public static final String LOGIN = BASE_URL + "/login";
    public static final String POST_FRAGMENT = "post_fragment";
    public static final String CHAT_FRAGMENT = "chat_fragment";
    public static final String CM_FRAGMENT = "cm_fragment";
    public static final String DISCOVER_FRAGMENT = "discover_fragment";
    public static final String ME_FRAGMENT = "me_fragment";
    public static final String PASSED_USER = "passed_user";
    public static final String PASSED_ID = "passed_id";
    public static final String PASSED_GROUP = "passed_group";
    public static final String PASSED_USER_CHATYPE = "chat_type";
    public static final String DISCOVER_FEATURE = "discover_feature";
    public static final String DISCOVER_FEATURE_RESULT = "discover_fetu_result";
    public static final int VIEW_TYPE_PROGRESS_BAR = 2;
    public static final int VIEW_TYPE_REAL = 3;
    public static final String FIREBASE_CHAT_PATH = "chats";
    public static final String FIREBASE_GROUP_PATH = "groups"; //this is the path to the group node in firebase.
    public static final String FIREBASE_QUEUE_PATH = "queue";
    public static final String FIREBASE_PROFILE_PHOTO_PATH = "profile_image";
    public static final String FIREBASE_POST_PHOTO_PATH = "post_picture";
    public static final String CHAT_SEPARATOR = "__";
    public static final String TOPIC_SENDER_NAME = "senderName";
    public static final String TOPIC_MESSAGE = "theMessage";
    public static final String TOPIC_SENDER_ID = "senderId";
    public static final String TOPIC_SENDER_PHOTO = "senderPhoto";
    public static final String TOPIC_SENDER_MAJOR = "senderMajor";
    public static final String TOPIC_GROUP_OWNER = "groupOwner"; //this is the key  to know the group owner.
    public static final String TOPIC_FLAG = "flag"; //this is the json key for the flag value of notifications.
    public static final String TOPIC_TIMESTAMP = "timestamp"; //this is the json key for timestamp;
    public static final int TOPIC_FLAG_MESSAGE = 1;  //this is to denote in chatactivity that users are chatting.
    public static final int TOPIC_FLAG_GROUP = 2;     //this is flag to denote in chatactivity that notification is a group chat.
    public static final int TOPIC_FLAG_FREECHAT = 3;   //this is flag to denote in chatactivity that users are freechating.
    public static final String NEW_MESSAGE_BROADCAST = "newMessage";
    public static boolean isAcceptRequest = false; //key to check whether to refresh coursemate list.
    public static List<GroupRes> groupIds = new ArrayList<>();
    public static List<User> storedCm = new ArrayList<>();

    public static AlertDialog buildDailog(Context context, String title, String message, boolean cancelable) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setCancelable(cancelable)
                .setCancelable(cancelable)
                .setMessage(message)
                .setTitle(title)
                .create();
        return dialog;
    }

    public static List<GroupRes> getGroupIds() {
        return groupIds;
    }

    public static void setGroupIds(List<GroupRes> groupIds) {
        EndPoints.groupIds = groupIds;
    }

    public static boolean isAcceptRequest() {
        return isAcceptRequest;
    }

    public static void setIsAcceptRequest(boolean isAcceptRequest) {
        EndPoints.isAcceptRequest = isAcceptRequest;
    }

    public static List<User> getStoredCm() {
        return storedCm;
    }

    public static void setStoredCm(List<User> storedCm) {
        EndPoints.storedCm = storedCm;
    }

    public static MaterialIntroView.Builder getBuilder(Activity activity) {
        return new MaterialIntroView.Builder(activity)
                .setMaskColor(R.color.colorAccent)
                .enableFadeAnimation(true)
                .enableDotAnimation(true)
                .setTargetPadding(20)
                .setInfoTextSize(20);
    }

    public static void loadFirebasePic(String photo, ImageView imageView, Context context) {
        FirebasePhotoStorage firebasePhotoStorage = new FirebasePhotoStorage();
        StorageReference firebaseProfile = firebasePhotoStorage.getProfilePhotoRef().child(photo);
        if (firebaseProfile != null) {
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(firebaseProfile)
                    .crossFade()
                    .placeholder(R.drawable.pro_loading)
                    .error(R.drawable.loading_profile)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new CircleTransform(context))
                    .into(imageView);
        }
    }
}

