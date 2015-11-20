package com.fdu.socialapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.fdu.socialapp.event.ImTypeMessageEvent;
import com.fdu.socialapp.service.NotificationBroadcastReceiver;
import com.fdu.socialapp.utils.NotificationUtils;
import com.fdu.socialapp.model.MyClientManager;

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
        String clientId;
        try {
            clientId = MyClientManager.getInstance().getClientId();
            if (client.getClientId().equals(clientId)) {

                if (!message.getFrom().equals(clientId)) {
                    sendEvent(message, conversation);
                    if (NotificationUtils.isShowNotification(conversation.getConversationId())) {
                        sendNotification(message, conversation);
                    }
                }
            } else {
                client.close(null);
            }
        } catch (IllegalStateException e) {
            client.close(null);
        }
    }

    private void sendEvent(AVIMTypedMessage message, AVIMConversation conversation) {
        ImTypeMessageEvent event = new ImTypeMessageEvent();
        event.message = message;
        event.conversation = conversation;
        EventBus.getDefault().post(event);
    }


    private void sendNotification(AVIMTypedMessage message, AVIMConversation conversation) {
        String notificationContent = message instanceof AVIMTextMessage ?
                ((AVIMTextMessage)message).getText() : context.getString(R.string.unspport_message_type);


        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.putExtra(Constants.CONVERSATION_ID, conversation.getConversationId());
        intent.putExtra(Constants.MEMBER_ID, message.getFrom());
        NotificationUtils.showNotification(context, "", notificationContent, null, intent);
    }
}
