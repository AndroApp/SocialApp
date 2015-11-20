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

    public static Bitmap getConversationIcon(AVIMConversation conversation) {
        return ColoredBitmapProvider.getInstance().createColoredBitmapByHashString(conversation.getConversationId());
    }

    /**
     * 获取单聊对话的另外一个人的 userId
     * @return 如果非法对话，则为 selfId
     */
    public static String otherIdOfConversation(AVIMConversation conversation) {
        if (isValidConversation(conversation)) {
            if (getType(conversation) == ConversationType.Single) {
                List<String> members = conversation.getMembers();
                if (members.size() == 2) {
                    if (members.get(0).equals(MyClientManager.getInstance().getClientId())) {
                        return members.get(1);
                    } else {
                        return members.get(0);
                    }
                }
            }
        }
        // 尽管异常，返回可以使用的 userId
        return MyClientManager.getInstance().getClientId();
    }

    public static String nameOfConversation(AVIMConversation conversation) {
        if (isValidConversation(conversation)) {
            if (getType(conversation) == ConversationType.Single) {
                return otherIdOfConversation(conversation);
            } else {
                return conversation.getName();
            }
        } else {
            return "";
        }
    }


    public static abstract class MultiConversationsCallback {
        public abstract void done(List<MyConversation> myConversationList, AVException exception);
    }
}
