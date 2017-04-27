package com.akinropo.taiwo.coursemate.FirebaseChat;

import android.content.SharedPreferences;
import android.util.Log;

import com.akinropo.taiwo.coursemate.PrivateClasses.Config;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by TAIWO on 1/30/2017.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        storeRegIdInPref(refreshedToken);

    }
    private void SendTokenToServer(String token){
        Log.d(TAG,"sendTokentoServer: "+token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}
