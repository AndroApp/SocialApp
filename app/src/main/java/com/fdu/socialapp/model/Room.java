package com.fdu.socialapp.model;


import android.graphics.Bitmap;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.fdu.socialapp.utils.ConversationCacheUtils;
import com.fdu.socialapp.service.ColoredBitmapProvider;

import java.util.List;

/**
 * Created by mao on 2015/11/17 0017.
 */
public class Room {
    private static String TAG = "Room";
    private AVIMMessage lastMessage;
    private String conversationId;
    private int unreadCount;

    public AVIMMessage getLastMessage() {
        return lastMessage;
    }

    public long getLastModifyTime() {
        if (lastMessage != null) {
            return lastMessage.getTimestamp();
        }

        AVIMConversation conversation = getConversation();
        if (null != conversation && null != conversation.getUpdatedAt()) {
            return conversation.getUpdatedAt().getTime();
        }

        return 0;
    }

    public AVIMConversation getConversation() {
        return ConversationCacheUtils.getCacheConversation(getConversationId());
    }

    public void setLastMessage(AVIMMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }







    public static abstract class MultiRoomsCallback {
        public abstract void done(List<Room> roomList, AVException exception);
    }
}
