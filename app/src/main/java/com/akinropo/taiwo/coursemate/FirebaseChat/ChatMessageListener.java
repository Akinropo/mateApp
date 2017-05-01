package com.akinropo.taiwo.coursemate.FirebaseChat;

/**
 * Created by TAIWO on 2/19/2017.
 */
public interface ChatMessageListener {

    public void onNewMessage(Message message);

    public void onChatLoaded(Chat chat);

    public void onErrorNewMessage(String error);

    public void onErrorLoaded(String error);

    public interface ChatSelectionListener {
        public void onAddMember();

        public void onViewGroup();
    }
}
