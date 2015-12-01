package com.fdu.socialapp.controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.fdu.socialapp.Constants;
import com.fdu.socialapp.R;
import com.fdu.socialapp.event.ImTypeMessageEvent;
import com.fdu.socialapp.model.ChatManager;
import com.fdu.socialapp.model.ConversationType;
import com.fdu.socialapp.service.NotificationBroadcastReceiver;
import com.fdu.socialapp.utils.AVUserCacheUtils;
import com.fdu.socialapp.utils.NotificationUtils;

import de.greenrobot.event.EventBus;

/**
 * Created by mao on 2015/11/14 0014.
 * 处理收到的消息
 */
public class MessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {
    private final static String TAG = "MessageHandler";
    private Context context;
    public MessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        if (message == null || message.getMessageId() == null) {
            Log.d(TAG, "may be SDK Bug, message or message id is null");
            return;
        }
        if (!ConversationHelper.isValidConversation(conversation)) {
            Log.d(TAG, "receive msg from invalid conversation");
        }
        String clientId;
        try {
            clientId = ChatManager.getInstance().getSelfId();
            if (clientId == null) {
                Log.d(TAG, "selfId is null, please call setupManagerWithUserId");
                client.close(null);
            } else {
                if (client.getClientId().equals(clientId)) {
                    ChatManager.getInstance().getRoomsTable().insertRoom(message.getConversationId());
                    if (!message.getFrom().equals(clientId)) { //排除自己发送的消息，不显示Notification和增加UnreadCount
                        if (NotificationUtils.isShowNotification(conversation.getConversationId())) {
                            sendNotification(message, conversation);
                        }
                        ChatManager.getInstance().getRoomsTable().increaseUnreadCount(message.getConversationId());
                    }
                    sendEvent(message, conversation);
                } else {
                    client.close(null);
                }
            }

        } catch (IllegalStateException e) {
            client.close(null);
        }
    }

    @Override
    public void onMessageReceipt(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        super.onMessageReceipt(message, conversation, client);
    }

    private void sendEvent(AVIMTypedMessage message, AVIMConversation conversation) {
        ImTypeMessageEvent event = new ImTypeMessageEvent();
        event.message = message;
        event.conversation = conversation;
        EventBus.getDefault().post(event);
    }


    private void sendNotification(AVIMTypedMessage message, AVIMConversation conversation) {
        if (conversation != null && message != null) {
            String notificationContent = message instanceof AVIMTextMessage ?
                    ((AVIMTextMessage)message).getText() : context.getString(R.string.unspport_message_type);

            AVUser user = AVUserCacheUtils.getCachedUser(message.getFrom());
            String title = (null != user ? user.getUsername() : "");

            Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
            intent.putExtra(Constants.CONVERSATION_ID, conversation.getConversationId());
            intent.putExtra(Constants.MEMBER_ID, message.getFrom());
            if (ConversationHelper.typeOfConversation(conversation) == ConversationType.Single) {
                intent.putExtra(Constants.NOTOFICATION_TAG, Constants.NOTIFICATION_SINGLE_CHAT);
            } else {
                intent.putExtra(Constants.NOTOFICATION_TAG, Constants.NOTIFICATION_GROUP_CHAT);
            }
            NotificationUtils.showNotification(context, title, notificationContent, null, intent);
        }

    }
}
