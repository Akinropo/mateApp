package com.akinropo.taiwo.coursemate.PrivateClasses;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by TAIWO on 1/10/2017.
 */
public class MyPreferenceManager {
    public static final String LOGGED_IN = "logged_in";
    public static final String KEY_ID = "user_id";
    public static final String PREF_NAME = "veechat";
    public static final String LOGGED_USER_NAME = "user_name";
    public static final String SET_INTEREST = "user_interest";
    public static final String LOGGED_PHOTO = "user_photo";
    public static final String LOGGED_MAJOR = "user_major";
    public static final String LAUNCH_FIRST = "firsttime_launch";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context myContext;
    int PRIVATE_MODE = 0;


    public MyPreferenceManager(Context context) {
        this.myContext = context;
        pref = myContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIsLoggedIn(boolean b, int id) {
        editor.putBoolean(LOGGED_IN, b);
        editor.putInt(KEY_ID, id);
        editor.commit();
    }

    public boolean isLoggedIn() {
        boolean result = pref.getBoolean(LOGGED_IN, false);
        editor.commit();
        return result;
    }

    public boolean isLaunchFirst() {
        boolean r = pref.getBoolean(LAUNCH_FIRST, true);
        return r;
    }

    public void setLaunchFirst(boolean b) {
        editor.putBoolean(LAUNCH_FIRST, b);
        editor.commit();
    }

    public int getId() {
        int i = pref.getInt(KEY_ID, -50);
        editor.commit();
        return i;
    }

    public void setId(int id) {
        editor.putInt(KEY_ID, id);
        editor.commit();
    }

    public void setLoggedUserName(String name) {
        editor.putString(LOGGED_USER_NAME, name);
        editor.commit();
    }

    public String getLoggedName() {
        String i = pref.getString(LOGGED_USER_NAME, "null");
        return i;
    }

    public String getLoggedPhoto() {
        String i = pref.getString(LOGGED_PHOTO, "null");
        return i;
    }

    public void setLoggedPhoto(String photoUrl) {
        editor.putString(LOGGED_PHOTO, photoUrl);
        editor.commit();
    }

    public String getLoggedMajor() {
        String m = pref.getString(LOGGED_MAJOR, "null");
        return m;
    }

    public void setLoggedMajor(String major) {
        editor.putString(LOGGED_MAJOR, major);
        editor.commit();
    }

    public boolean getInterest() {
        boolean i = pref.getBoolean(SET_INTEREST, false);
        return i;
    }

    public void setInterest(boolean interest) {
        editor.putBoolean(SET_INTEREST, interest);
        editor.commit();
    }

    public void logOut(boolean t) {
        if (t) {
            setIsLoggedIn(false, 0);
            setId(0);
            setLoggedUserName("");
            setLoggedPhoto("");
            setLoggedMajor("");
        }
    }
}
