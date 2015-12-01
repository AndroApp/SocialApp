package com.fdu.socialapp.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMClientEventHandler;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationEventHandler;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.fdu.socialapp.controller.MessageHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mao on 2015/11/14 0014.
 * 管理client
 */
public class ChatManager extends AVIMClientEventHandler {
    public static final String KEY_UPDATED_AT = "updatedAt";
    private static ChatManager chatManager;
    private volatile AVIMClient mClient;
    private String selfId;
    private RoomsTable roomsTable;
    private static Context context;
    private volatile boolean connect = false;

    public synchronized static ChatManager getInstance() {
        if (chatManager == null) {
            chatManager = new ChatManager();
        }
        return chatManager;
    }

    private ChatManager() {}

    /**
     * 在登录之后，进入 MainActivity 之前，调用此函数，因为此时可以拿到当前登录用户的 ID
     * */
    public void setupManagerWithUserId(String userId) {
        this.selfId = userId;
        roomsTable = RoomsTable.getInstanceByUserId(userId);
    }

    /**
     * 设置 AVIMConversationEventHandler，用来处理对话成员变更回调
     *
     * @param eventHandler
     */
    public void setConversationEventHandler(AVIMConversationEventHandler eventHandler) {
        AVIMMessageManager.setConversationEventHandler(eventHandler);
    }


    /**
     * 聊天连接状态接口
     * */
    public interface ConnectionListener {
        void onConnectionChanged(boolean connect);
    }
    /**
     * 默认的聊天连接状态监听器
     */
    private static ConnectionListener defaultConnectListener = new ConnectionListener() {
        @Override
        public void onConnectionChanged(boolean connect) {
        }
    };
    private ConnectionListener connectionListener = defaultConnectListener;

    @Override
    public void onConnectionPaused(AVIMClient client) {
        setConnectAndNotify(false);
    }

    @Override
    public void onConnectionResume(AVIMClient client) {
        setConnectAndNotify(true);
    }

    public void setConnectAndNotify(boolean connect) {
        this.connect = connect;
        connectionListener.onConnectionChanged(connect);
    }

    /**
     * 是否连上聊天服务
     */
    public boolean isConnect() {
        return connect;
    }

    /**
     * 请在应用一启动(Application onCreate)的时候就调用，因为 SDK 一启动，就会去连接聊天服务器
     * 如果没有调用此函数设置 messageHandler ，就可能丢失一些消息
     *
     * @param context
     */
    public void init(Context context) {
        ChatManager.context = context;
        AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, new MessageHandler(context));
        AVIMClient.setClientEventHandler(this);
        //签名
        //AVIMClient.setSignatureFactory(new SignatureFactory());
    }


    /**
     * 连接聊天服务器，用 userId 登录，在进入MainActivity 前调用
     *
     * @param callback AVException 常发生于网络错误、签名错误
     */
    public void open(final AVIMClientCallback callback) {
        if (this.selfId == null) {
            throw new IllegalStateException("please call setupManagerWithUserId() first");
        }
        mClient = AVIMClient.getInstance(this.selfId);
        mClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e != null) {
                    setConnectAndNotify(false);
                } else {
                    setConnectAndNotify(true);
                }
                if (callback != null) {
                    callback.done(avimClient, e);
                }
            }
        });
        AVIMConversationQuery query = mClient.getQuery();
        query.containsMembers(Arrays.asList(selfId));
        query.whereEqualTo(ConversationType.ATTR_TYPE_KEY, ConversationType.Single.getValue());
        query.orderByDescending(KEY_UPDATED_AT);
        query.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> conversations, AVIMException e) {
                if (e == null) {
                    for (AVIMConversation conv : conversations) {
                        roomsTable.insertRoom(conv.getConversationId());
                    }
                }
            }
        });

    }

    /**
     * 用户注销的时候调用，close 之后消息不会推送过来，也不可以进行发消息等操作
     *
     * @param callback AVException 常见于网络错误
     */
    public void closeWithCallback(final AVIMClientCallback callback) {
        mClient.close(new AVIMClientCallback() {

            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e != null) {
                    Log.e("log out:", e.getMessage());
                }
                if (callback != null) {
                    callback.done(avimClient, e);
                }
            }
        });
        mClient = null;
        selfId = null;
    }


    public String getSelfId() {
        if (TextUtils.isEmpty(selfId)) {
            throw new IllegalStateException("Please call ChatManager.open first");
        }
        return selfId;
    }

    public AVIMClient getClient() {
        return mClient;
    }



    /**
     * 获取和 userId 的对话，先去服务器查之前两人有没创建过对话，没有的话，创建一个
     */
    public void fetchConversationWithUserId(final String userId, final AVIMConversationCreatedCallback callback) {
        AVIMConversationQuery query = mClient.getQuery();
        query.withMembers(Arrays.asList(userId, selfId));
        query.whereEqualTo(ConversationType.ATTR_TYPE_KEY, ConversationType.Single.getValue());
        query.orderByDescending(KEY_UPDATED_AT);
        query.limit(1);
        query.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> conversations, AVIMException e) {
                if (e != null) {
                    callback.done(null, e);
                } else {
                    if (conversations.size() > 0) {
                        callback.done(conversations.get(0), null);
                    } else {
                        Map<String, Object> attrs = new HashMap<>();
                        attrs.put(ConversationType.TYPE_KEY, ConversationType.Single.getValue());
                        mClient.createConversation(Arrays.asList(userId, selfId), attrs, callback);
                    }
                }
            }
        });
    }

    public AVIMConversationQuery getConversationQuery() {
        return mClient.getQuery();
    }

    public void createConversation(List<String> members, Map<String, Object> attributes, AVIMConversationCreatedCallback callback) {
        mClient.createConversation(members, attributes, callback);
    }

    public AVIMConversation getConversation(String conversationId) {
        return mClient.getConversation(conversationId);
    }

    public List<Room> findRecentRooms() {
        return ChatManager.getInstance().getRoomsTable().selectRooms();
    }

    public RoomsTable getRoomsTable() {
        return roomsTable;
    }

    public static Context getContext() {
        return context;
    }
}
