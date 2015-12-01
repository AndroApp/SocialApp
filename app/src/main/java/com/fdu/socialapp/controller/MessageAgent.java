package com.fdu.socialapp.controller;

import android.util.Log;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.fdu.socialapp.model.ChatManager;
import com.fdu.socialapp.utils.PathUtils;
import com.fdu.socialapp.utils.PhotoUtils;
import com.fdu.socialapp.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by mao on 2015/11/24 0024.
 */
public class MessageAgent {
    private static final String TAG = "MessageAgent";
    private AVIMConversation conversation;
    private ChatManager chatManager;
    private SendCallback sendCallback = new SendCallback() {
        @Override
        public void onStart(AVIMTypedMessage message) {
        }

        @Override
        public void onError(AVIMTypedMessage message, Exception e) {
        }

        @Override
        public void onSuccess(AVIMTypedMessage message) {
        }
    };

    public MessageAgent(AVIMConversation conversation) {
        this.conversation = conversation;
        chatManager = ChatManager.getInstance();
    }

    public void setSendCallback(SendCallback sendCallback) {
        this.sendCallback = sendCallback;
    }

    private void sendMsg(final AVIMTypedMessage msg, final String originPath, final SendCallback callback) {
        if (!chatManager.isConnect()) {
            Log.i(TAG, "im not connect");
        }
        if (callback != null) {
            callback.onStart(msg);
        }
        conversation.sendMessage(msg, AVIMConversation.RECEIPT_MESSAGE_FLAG, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (e == null && originPath != null) {
                    File tmpFile = new File(originPath);
                    File newFile = new File(PathUtils.getChatFilePath(msg.getMessageId()));
                    boolean result = tmpFile.renameTo(newFile);
                    if (!result) {
                        Log.i(TAG, "move file failed, can't use local cache");
                    }
                }
                if (callback != null) {
                    if (e != null) {
                        callback.onError(msg, e);
                    } else {
                        ChatManager.getInstance().getRoomsTable().insertRoom(conversation.getConversationId());
                        callback.onSuccess(msg);
                    }
                }
            }
        });

    }

    public void resendMessage(final AVIMTypedMessage msg, final SendCallback sendCallback) {
        conversation.sendMessage(msg, AVIMConversation.RECEIPT_MESSAGE_FLAG, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (sendCallback != null) {
                    if (e != null) {
                        sendCallback.onError(msg, e);
                    } else {
                        sendCallback.onSuccess(msg);
                    }
                }
            }
        });
    }

    public void sendText(String content) {
        AVIMTextMessage textMsg = new AVIMTextMessage();
        textMsg.setText(content);
        sendMsg(textMsg, null, sendCallback);
    }

    public void sendImage(String imagePath) {
        final String newPath = PathUtils.getChatFilePath(Utils.uuid());
        PhotoUtils.compressImage(imagePath, newPath);
        try {
            AVIMImageMessage imageMsg = new AVIMImageMessage(newPath);
            sendMsg(imageMsg, newPath, sendCallback);
        } catch (IOException e) {

        }
    }

    public void sendLocation(double latitude, double longitude, String address) {
        AVIMLocationMessage locationMsg = new AVIMLocationMessage();
        AVGeoPoint geoPoint = new AVGeoPoint(latitude, longitude);
        locationMsg.setLocation(geoPoint);
        locationMsg.setText(address);
        sendMsg(locationMsg, null, sendCallback);
    }

    public void sendAudio(String audioPath) {
        try {
            AVIMAudioMessage audioMsg = new AVIMAudioMessage(audioPath);
            sendMsg(audioMsg, audioPath, sendCallback);
        } catch (IOException e) {

        }
    }

    public interface SendCallback {
        void onStart(AVIMTypedMessage message);

        void onError(AVIMTypedMessage message, Exception e);

        void onSuccess(AVIMTypedMessage message);

    }
}
