package com.akinropo.taiwo.coursemate.FirebaseChat;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.akinropo.taiwo.coursemate.AllActivities.AddMemberGroup;
import com.akinropo.taiwo.coursemate.AllActivities.GroupProfile;
import com.akinropo.taiwo.coursemate.AllFragments.FriendProfile;
import com.akinropo.taiwo.coursemate.ApiClasses.EndPoints;
import com.akinropo.taiwo.coursemate.ApiClasses.GroupRes;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


public class ChatView extends LinearLayout {

    private EmojiconEditText messageView;
    private ImageView submitButton;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ChatAdapter chatAdapter;
    private ImageView emojiSwicther;
    private EmojIconActions emojIconActions;

    private ChatActionListener actionListener;
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            disableInteraction();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            actionListener.onMessageLengthChanged(s.toString().trim().length());
        }
    };
    private final OnClickListener submitClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            actionListener.onSubmitMessage(messageView.getText().toString().trim());
            messageView.setText("");
        }
    };
    private final OnClickListener navigationClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            actionListener.onUpPressed();
        }
    };
    private User theFriend;
    private Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.view_profile) {
                actionListener.onManageOwnersClicked();
                return true;
            }
            return false;
        }
    };

    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        chatAdapter = new ChatAdapter(LayoutInflater.from(context));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = View.inflate(getContext(), R.layout.merge_chat_view, this);
        emojiSwicther = (ImageView) view.findViewById(R.id.emoji_switcher);
        messageView = (EmojiconEditText) view.findViewById(R.id.message_edit);
        submitButton = (ImageView) view.findViewById(R.id.submit_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.messages_recycler_view);
        recyclerView.addItemDecoration(new ChatItemDecoration());
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.chat_menu);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
        emojIconActions = new EmojIconActions(getContext(), view, messageView, emojiSwicther, "#C8E6C9", "#4CAF50", "#f4f4f4");
        emojIconActions.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIconActions.ShowEmojIcon();
    }

    public void attach(final ChatActionListener actionListener) {
        this.actionListener = actionListener;
        messageView.addTextChangedListener(textWatcher);
        submitButton.setOnClickListener(submitClickListener);
        toolbar.setNavigationOnClickListener(navigationClickListener);
        //toolbar.setOnMenuItemClickListener(menuItemClickListener);
    }

    public void detach() {
        submitButton.setOnClickListener(null);
        messageView.removeTextChangedListener(null);
        toolbar.setOnMenuItemClickListener(null);
        this.actionListener = null;
    }

    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    public void addMessage(Message message) {
        int lastMessagePosition = chatAdapter.getItemCount() == 0 ? 0 : chatAdapter.getItemCount() - 1;
        recyclerView.smoothScrollToPosition(lastMessagePosition);
        chatAdapter.addMessage(message);
    }

    public void showAddMembersButton(boolean show, final GroupRes groupRes) {
        toolbar.getMenu().findItem(R.id.add_member).setVisible(show);
        if (show) {
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.view_profile:
                            Intent j = new Intent(getContext().getApplicationContext(), GroupProfile.class);
                            j.putExtra(EndPoints.PASSED_GROUP, groupRes);
                            j.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getContext().getApplicationContext().startActivity(j);
                            break;
                        case R.id.add_member:
                            Intent i = new Intent(getContext().getApplicationContext(), AddMemberGroup.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra(EndPoints.PASSED_GROUP, groupRes);
                            getContext().getApplicationContext().startActivity(i);
                            break;
                    }
                    return true;
                }
            });
        }

    }

    public void showUserProfile(final Bundle userArgument, final FragmentManager fragmentManager) {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.view_profile:
                        FriendProfile friendProfile = new FriendProfile();
                        friendProfile.setPrivacy(false, false);
                        friendProfile.setArguments(userArgument);
                        friendProfile.setCancelable(true);
                        friendProfile.show(fragmentManager, EndPoints.PASSED_USER);
                        break;
                }
                return false;
            }
        });
    }

    public void display(Chat chat, User user) {
        chatAdapter.update(chat, user);
        int lastMessagePosition = chatAdapter.getItemCount() == 0 ? 0 : chatAdapter.getItemCount() - 1;
        //recyclerView.smoothScrollToPosition(lastMessagePosition);
        recyclerView.scrollToPosition(lastMessagePosition);
    }

    public void setUser(User user) {
        chatAdapter.updateUser(user);
    }

    public void enableInteraction() {
        submitButton.setEnabled(true);

        submitButton.setColorFilter(getResources().getColor(R.color.test_primary_light), PorterDuff.Mode.SRC_ATOP);
    }

    public void disableInteraction() {
        submitButton.setEnabled(false);
        submitButton.setColorFilter(getResources().getColor(R.color.disabled_grey), PorterDuff.Mode.SRC_ATOP);
    }

    private class ChatItemDecoration extends RecyclerView.ItemDecoration {

        private final int horizontalMargin = getResources().getDimensionPixelOffset(R.dimen.chat_item_horizontal_margin);
        private final int verticalMargin = getResources().getDimensionPixelOffset(R.dimen.chat_item_vertical_margin);

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = horizontalMargin;
            outRect.right = horizontalMargin;
            outRect.top = verticalMargin;
            outRect.bottom = verticalMargin;
        }

    }

}