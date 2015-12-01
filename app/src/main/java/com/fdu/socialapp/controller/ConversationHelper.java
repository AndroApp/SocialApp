package com.fdu.socialapp.controller;

import android.util.Log;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.fdu.socialapp.model.ChatManager;
import com.fdu.socialapp.model.ConversationType;
import com.fdu.socialapp.utils.AVUserCacheUtils;

import java.util.List;

/**
 * Created by mao on 2015/11/24 0024.
 */
public class ConversationHelper {
    private final static String TAG = "ConversationHelper";
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
            if (conversation.getMembers().size() != 2 || !conversation.getMembers().contains(ChatManager.getInstance().getSelfId())) {
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

    public static ConversationType typeOfConversation(AVIMConversation conversation) {
        if (isValidConversation(conversation)) {
            Object typeObject = conversation.getAttribute(ConversationType.TYPE_KEY);
            int typeInt = (Integer) typeObject;
            return ConversationType.fromInt(typeInt);
        } else {
            Log.e(TAG, "invalid conversation ");
            return ConversationType.Group;
        }
    }

    /**
     * 获取单聊对话的另外一个人的 userId
     *
     * @param conversation The desired conversation
     * @return 如果非法对话，则为 selfId
     */
    public static String otherIdOfConversation(AVIMConversation conversation) {
        if (isValidConversation(conversation)) {
            if (typeOfConversation(conversation) == ConversationType.Single) {
                List<String> members = conversation.getMembers();
                if (members.size() == 2) {
                    if (members.get(0).equals(ChatManager.getInstance().getSelfId())) {
                        return members.get(1);
                    } else {
                        return members.get(0);
                    }
                }
            }
        }
        // 尽管异常，返回可以使用的 userId
        return ChatManager.getInstance().getSelfId();
    }


    public static String nameOfConversation(AVIMConversation conversation) {
        if (isValidConversation(conversation)) {
            if (typeOfConversation(conversation) == ConversationType.Single) {
                String otherId = otherIdOfConversation(conversation);
                AVUser user = AVUserCacheUtils.getCachedUser(otherId);
                if (user != null) {
                    return user.getUsername();
                } else {
                    Log.e(TAG, "use is null");
                    return "对话";
                }
            } else {
                return conversation.getName();
            }
        } else {
            return "";
        }
    }

    public static String titleOfConversation(AVIMConversation conversation) {
        if (isValidConversation(conversation)) {
            if (typeOfConversation(conversation) == ConversationType.Single) {
                return nameOfConversation(conversation);
            } else {
                List<String> members = conversation.getMembers();
                return nameOfConversation(conversation) + " (" + members.size() + ")";
            }
        } else {
            return "";
        }
    }
}
