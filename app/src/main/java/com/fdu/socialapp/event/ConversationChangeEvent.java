package com.fdu.socialapp.event;

import com.avos.avoscloud.im.v2.AVIMConversation;

/**
 * Created by mao on 2015/11/24 0024.
 */
public class ConversationChangeEvent {
    private AVIMConversation conv;

    public ConversationChangeEvent(AVIMConversation conv) {
        this.conv = conv;
    }

    public AVIMConversation getConv() {
        return conv;
    }
}
