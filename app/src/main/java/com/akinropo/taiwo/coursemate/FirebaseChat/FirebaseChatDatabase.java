package com.akinropo.taiwo.coursemate.FirebaseChat;

import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by TAIWO on 2/19/2017.
 */
public class FirebaseChatDatabase {
    FirebaseApp firebaseApp;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    public FirebaseChatDatabase() {
        this.firebaseApp = FirebaseApp.getInstance();
        this.firebaseDatabase = FirebaseDatabase.getInstance();

    }

    public static FirebaseChatDatabase getInstance() {
        return new FirebaseChatDatabase();
    }

    public DatabaseReference getDbforChat(int user, int friend) {
        //function to get the chat_db for two users.
        return firebaseDatabase.getReference().child(EndPoints.FIREBASE_CHAT_PATH).child(getDBnameFor(user, friend));
    }

    public DatabaseReference getDbforGroup(String groupId) {
        String g = "group__" + groupId;
        return firebaseDatabase.getReference().child(EndPoints.FIREBASE_GROUP_PATH).child(g);
    }

    public String getDBnameFor(int user, int friend) {
        //function to get the chat_db name for two users
        String dbName = user + EndPoints.CHAT_SEPARATOR + friend;
        if (user > friend) {
            dbName = friend + EndPoints.CHAT_SEPARATOR + user;
        }
        return dbName;
    }

    public DatabaseReference getNotificationPath() {
        return firebaseDatabase.getReference().child(EndPoints.FIREBASE_QUEUE_PATH);
    }

    public DatabaseReference getDbForPrescence(int id) {
        return firebaseDatabase.getReference("/users/" + id + "/connections");
    }
}
