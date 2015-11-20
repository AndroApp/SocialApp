package com.fdu.socialapp.viewholder;

import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.fdu.socialapp.model.ConversationType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mao on 2015/11/14 0014.
 * 管理client
 */
public class MyClientManager {
    private static final String KEY_UPDATED_AT = "updatedAt";
    private static MyClientManager myClientManager;
    private volatile AVIMClient mClient;
    private String clientId;


    public synchronized static MyClientManager getInstance() {
        if (myClientManager == null) {
            myClientManager = new MyClientManager();
        }
        return myClientManager;
    }

    private MyClientManager() {

    }

    public void open(String clientId, final AVIMClientCallback callback) {
        this.clientId = clientId;
        mClient = AVIMClient.getInstance(clientId);
        mClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (callback != null) {
                    callback.done(avimClient, e);
                }
            }

        });
    }

    public AVIMClient getMyClient() {
        return mClient;
    }

    public String getClientId() {
        if (TextUtils.isEmpty(clientId)) {
            throw new IllegalStateException("Please call MyClientManager.open first");
        }
        return clientId;
    }

    public AVIMClient getClient() {
        return mClient;
    }



    /**
     * 获取和 userId 的对话，先去服务器查之前两人有没创建过对话，没有的话，创建一个
     */
    public void fetchConversationWithUserId(final String userId, final AVIMConversationCreatedCallback callback) {
        AVIMConversationQuery query = mClient.getQuery();
        query.withMembers(Arrays.asList(userId, clientId));
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
                        mClient.createConversation(Arrays.asList(userId, clientId), attrs, callback);
                    }
                }
            }
        });
    }

    public AVIMConversationQuery getConversationQuery() {
        return mClient.getQuery();
    }
}
