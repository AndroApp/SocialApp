package com.fdu.socialapp.controller;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMReservedMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.fdu.socialapp.R;
import com.fdu.socialapp.model.ChatManager;
import com.fdu.socialapp.utils.AVUserCacheUtils;
import com.fdu.socialapp.utils.PathUtils;

import java.util.List;

/**
 * Created by mao on 2015/11/24 0024.
 */
public class MessageHelper {
    public static String getFilePath(AVIMTypedMessage msg) {
        return PathUtils.getChatFilePath(msg.getMessageId());
    }

    public static boolean fromMe(AVIMTypedMessage msg) {
        ChatManager chatManager = ChatManager.getInstance();
        String selfId = chatManager.getSelfId();
        return msg.getFrom() != null && msg.getFrom().equals(selfId);
    }

    private static String bracket(String s) {
        return String.format("[%s]", s);
    }

    public static CharSequence outlineOfMsg(AVIMTypedMessage msg) {
        AVIMReservedMessageType type = AVIMReservedMessageType.getAVIMReservedMessageType(msg.getMessageType());
        switch (type) {
            case TextMessageType:
                return EmotionHelper.replace(ChatManager.getContext(), ((AVIMTextMessage) msg).getText());
            case ImageMessageType:
                return bracket(ChatManager.getContext().getString(R.string.chat_image));
            case LocationMessageType:
                AVIMLocationMessage locMsg = (AVIMLocationMessage) msg;
                String address = locMsg.getText();
                if (address == null) {
                    address = "";
                }
                return bracket(ChatManager.getContext().getString(R.string.chat_position)) + address;
            case AudioMessageType:
                return bracket(ChatManager.getContext().getString(R.string.chat_audio));
        }
        return null;
    }

    public static String nameByUserIds(List<String> userIds) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < userIds.size(); i++) {
            String id = userIds.get(i);
            if (i != 0) {
                sb.append(",");
            }
            sb.append(nameByUserId(id));
        }
        return sb.toString();
    }

    public static String nameByUserId(String id) {
        AVUser user = AVUserCacheUtils.getCachedUser(id);
        if (user != null) {
            return user.getUsername();
        } else {
            return id;
        }
    }}
