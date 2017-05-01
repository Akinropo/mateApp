package com.akinropo.taiwo.coursemate.FirebaseChat;

import java.util.List;

public class Chat {

    private final List<Message> messages;

    public Chat(List<Message> messages) {
        this.messages = messages;
    }

    public int size() {
        return messages.size();
    }

    public Message get(int position) {
        return messages.get(position);
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Chat chat = (Chat) o;

        return messages != null ? messages.equals(chat.messages) : chat.messages == null;

    }

    @Override
    public int hashCode() {
        return messages != null ? messages.hashCode() : 0;
    }

}
