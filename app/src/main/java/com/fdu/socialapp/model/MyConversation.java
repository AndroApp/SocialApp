package com.fdu.socialapp.model;


import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.fdu.socialapp.ConversationCacheUtils;
import com.fdu.socialapp.viewholder.MyClientManager;

import java.util.List;

/**
 * Created by mao on 2015/11/17 0017.
 */
public class MyConversation {
    private static String TAG = "MyConversation";
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

    public static boolean isValidConversation(AVIMConversation conversation) {
        if (conversation == null) {
            Log.d(TAG, "conversation is null");
            return false;
        }
        if (conversation.getMembers() == null || conversation.getMembers().size() == 0) {
            Log.d(TAG, "conversation member is null or empty");
            return false;
        }
        Object type = conversation.getAttribute(ConversationType.TYPE_KEY);
        if (type == null) {
            Log.d(TAG, "conversation type is null");
            return false;
        }
        int intType = (Integer) type;
        if (intType == ConversationType.Single.getValue()) {
            if (conversation.getMembers().size() != 2 || !conversation.getMembers().contains(MyClientManager.getInstance().getClientId())) {
                Log.d(TAG, "Single conversation has wrong members");
                return false;
            } else {
                return true;
            }
        } else if (intType != ConversationType.Group.getValue()) {
            Log.d(TAG, "conversation type is wrong");
            return false;
        } else {
            return true;
        }
    }

    public static ConversationType getType(AVIMConversation conversation){
        if (isValidConversation(conversation)) {
            Object typeObject = conversation.getAttribute(ConversationType.TYPE_KEY);
            int typeInt = (Integer) typeObject;
            return ConversationType.fromInt(typeInt);
        } else {
            Log.e(TAG, "invalid conversation ");
            return ConversationType.Group;
        }
    }



    public static abstract class MultiConversationsCallback {
        public abstract void done(List<MyConversation> myConversationList, AVException exception);
    }
}
