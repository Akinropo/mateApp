package com.akinropo.taiwo.coursemate.FirebaseChat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.akinropo.taiwo.coursemate.PrivateClasses.MyPreferenceManager;
import com.akinropo.taiwo.coursemate.PrivateClasses.User;
import com.akinropo.taiwo.coursemate.R;

import java.util.ArrayList;

import static com.akinropo.taiwo.coursemate.FirebaseChat.MessageBubbleDrawable.Gravity;

class ChatAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_THIS_USER = 0;
    private static final int VIEW_TYPE_MESSAGE_OTHER_USERS = 1;
    private final LayoutInflater inflater;
    private MyPreferenceManager manager;
    private Chat chat = new Chat(new ArrayList<Message>());
    private User friendChatter = new User();

    ChatAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
        setHasStableIds(true);
        manager = new MyPreferenceManager(inflater.getContext());
    }

    public void update(Chat chat, User user) {
        this.chat = chat;
        this.friendChatter = user;
        notifyDataSetChanged();
    }

    public void updateUser(User user) {
        this.friendChatter = user;
        notifyDataSetChanged();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MessageBubbleDrawable bubbleDrawable = null;
        MessageView messageView = null;
        if (viewType == VIEW_TYPE_MESSAGE_THIS_USER) {
            bubbleDrawable = new MessageBubbleDrawable(parent.getContext(), R.color.test_primary_light, Gravity.END);
            messageView = (MessageView) inflater.inflate(R.layout.self_message_item_layout, parent, false);
        } else if (viewType == VIEW_TYPE_MESSAGE_OTHER_USERS) {
            bubbleDrawable = new MessageBubbleDrawable(parent.getContext(), R.color.bubble_grey, Gravity.START);
            messageView = (MessageView) inflater.inflate(R.layout.message_item_layout, parent, false);
        } else {
            //no view like this
        }
        messageView.setTextBackground(bubbleDrawable);
        return new MessageViewHolder(messageView);
    }

    public void addMessage(Message message) {
        chat.addMessage(message);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = chat.get(position);
        if (message.getSenderId() == friendChatter.getId()) {
            message.setSenderPhoto(friendChatter.getPhoto());
        }
        holder.bind(chat.get(position));
    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return chat.get(position).getSenderId() == manager.getId() ? VIEW_TYPE_MESSAGE_THIS_USER : VIEW_TYPE_MESSAGE_OTHER_USERS;
    }
}
