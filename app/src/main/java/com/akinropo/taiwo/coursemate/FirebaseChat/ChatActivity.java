package com.akinropo.taiwo.coursemate.FirebaseChat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.akinropo.taiwo.coursemate.AllActivities.MainActivity;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.ApiClasses.GroupRes;
import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;


public class ChatActivity extends AppCompatActivity {
    public static int CURRENT_PEER = 0;
    ChatPresenter chatPresenter;
    MyPreferenceManager myPreferenceManager;
    Bundle userArgument = new Bundle(); //this user argument has a boolea key that tells if the profile should be completely loaded or just bind with the view.
    private ChatView chatView;
    private User friend;
    private GroupRes groupRes;
    private int userId;

    //Either a GroupRes or User class will always be passed into this fragment
    //when a user is passed also we pass an integer that shows the type of chat
    //wheter a freechat or known coursemate chat
    //0 will represent a known user chat while 1 will represent a freechat;

    public static Intent createIntentFor(Context context, User user) {
        Intent i = new Intent(context, ChatActivity.class);
        i.putExtra(EndPoints.PASSED_USER, user);
        return i;
    }

    public static Intent createIntentForGroup(Context context, GroupRes groupRes) {
        Intent i = new Intent(context, ChatActivity.class);
        i.putExtra(EndPoints.PASSED_GROUP, groupRes);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        myPreferenceManager = new MyPreferenceManager(getApplicationContext());
        friend = getIntent().getParcelableExtra(EndPoints.PASSED_USER);
        groupRes = getIntent().getParcelableExtra(EndPoints.PASSED_GROUP);
        userId = getIntent().getIntExtra(EndPoints.PASSED_ID, 0);
        // Toast.makeText(ChatActivity.this, "Chat Activity Created", Toast.LENGTH_SHORT).show();
        chatView = (ChatView) findViewById(R.id.chat_view);
        if (friend != null) {
            CURRENT_PEER = friend.getId();
            if (userId > 0) {
                userArgument.putBoolean("reLoad", true);
                userArgument.putInt(EndPoints.PASSED_USER, userId);
            } else {
                userArgument.putBoolean("reLoad", false);
                userArgument.putParcelable(EndPoints.PASSED_USER, friend);
            }
            chatPresenter = new ChatPresenter(myPreferenceManager.getId(), friend.getId(), chatView, friend, userArgument, getSupportFragmentManager());
            if (getIntent().getIntExtra(EndPoints.PASSED_USER_CHATYPE, -1) == EndPoints.TOPIC_FLAG_FREECHAT)
                chatPresenter.setMESSAGE_FLAG(EndPoints.TOPIC_FLAG_FREECHAT);
        } else if (groupRes != null) {
            chatPresenter = new ChatPresenter(myPreferenceManager.getId(), chatView, groupRes);
        }

        chatPresenter.setContext(getApplicationContext());
        ChatActionListener chatActionListener = new ChatActionListener() {
            @Override
            public void onUpPressed() {
                Intent i = new Intent(ChatActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }

            @Override
            public void onMessageLengthChanged(int messageLength) {
                if (messageLength > 0) {
                    chatView.enableInteraction();
                } else {
                    chatView.disableInteraction();
                }
            }

            @Override
            public void onSubmitMessage(String message) {
                if (!message.isEmpty()) {
                    if (friend != null) {
                        // Toast.makeText(ChatActivity.this, "send to friend", Toast.LENGTH_SHORT).show();
                        chatPresenter.sendMessage(myPreferenceManager.getId(), friend.getId(), message);
                    } else if (groupRes != null) {
                        // Toast.makeText(ChatActivity.this, "send to group", Toast.LENGTH_SHORT).show();
                        chatPresenter.sendMessagetoGroup(myPreferenceManager.getLoggedName(), myPreferenceManager.getLoggedPhoto(), message);
                    }
                }
            }

            @Override
            public void onManageOwnersClicked() {

            }
        };
        chatView.attach(chatActionListener);

    }

    @Override
    protected void onStart() {
        super.onStart();
        chatPresenter.init();
    }

    @Override
    protected void onStop() {
        super.onStop();
        chatPresenter.removeChat();
    }
}
