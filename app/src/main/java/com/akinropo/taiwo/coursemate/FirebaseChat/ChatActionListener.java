package com.akinropo.taiwo.coursemate.FirebaseChat;

/**
 * Created by TAIWO on 2/13/2017.
 */
public interface ChatActionListener {
    void onUpPressed();

    void onMessageLengthChanged(int messageLength);

    void onSubmitMessage(String message);

    void onManageOwnersClicked();
}
