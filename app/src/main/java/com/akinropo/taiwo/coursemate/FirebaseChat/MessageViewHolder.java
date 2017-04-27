package com.akinropo.taiwo.coursemate.FirebaseChat;

import android.support.v7.widget.RecyclerView;


class MessageViewHolder extends RecyclerView.ViewHolder {

    private final MessageView messageView;

    public MessageViewHolder(MessageView messageView) {
        super(messageView);
        this.messageView = messageView;
    }

    public void bind(Message message) {
        messageView.display(message);
    }
}
