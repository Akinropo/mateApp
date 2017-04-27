package com.akinropo.taiwo.coursemate;

import android.app.Application;
import android.widget.Toast;

import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.github.javiersantos.appupdater.AppUpdater;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by TAIWO on 2/20/2017.
 */
public class App extends Application {

    MyPreferenceManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = new MyPreferenceManager(getApplicationContext());
        if(manager.getId() > 0){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            FirebaseMessaging.getInstance().subscribeToTopic("user__"+manager.getId());
            setOnlineStatus();
        }



    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        manager = new MyPreferenceManager(getApplicationContext());
        if(manager.getId() > 0){
            //FirebaseMessaging.getInstance().unsubscribeFromTopic("user__"+manager.getId());
        }
    }
    public void setOnlineStatus(){
        MyPreferenceManager manager = new MyPreferenceManager(getApplicationContext());
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference connectionRef = firebaseDatabase.getReference("/users/" + manager.getId() + "/connections");

        final DatabaseReference lastOnlineRef = firebaseDatabase.getReference("/users/"+manager.getId()+"/lastOnline");
        final DatabaseReference connectedRef = firebaseDatabase.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if(connected){
                    //add this device to connection list
                    DatabaseReference con = connectionRef.push();
                    con.setValue(Boolean.TRUE);
                    con.onDisconnect().removeValue(); //remove value when device disconnects.

                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
               // Toast.makeText(getApplicationContext(), "Error at setOnlineStatus()", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
