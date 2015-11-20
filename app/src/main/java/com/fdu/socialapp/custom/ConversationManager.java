package com.fdu.socialapp.custom;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.fdu.socialapp.model.ConversationType;
import com.fdu.socialapp.model.MyConversation;
import com.fdu.socialapp.utils.ConversationCacheUtils;
import com.fdu.socialapp.model.MyClientManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mao on 2015/11/20 0020.
 */
public class ConversationManager {
    private static ConversationManager conversationManager;

    public ConversationManager() {}

    public static synchronized ConversationManager getInstance() {
        if (conversationManager == null) {
            conversationManager = new ConversationManager();
        }
        return conversationManager;
    }

    public void findAndCacheRooms(final MyConversation.MultiConversationsCallback callback) {
        final List<MyConversation> conversations = findRecentConversations();
        List<String> conversationIds = new ArrayList<>();
        for (MyConversation conversation : conversations) {
            conversationIds.add(conversation.getConversationId());
        }

        if (conversationIds.size() > 0) {
            ConversationCacheUtils.cacheConversations(conversationIds, new ConversationCacheUtils.CacheConversationCallback() {
                @Override
                public void done(AVException e) {
                    if (e != null) {
                        callback.done(conversations, e);
                    } else {
                        callback.done(conversations, null);
                    }
                }
            });
        } else {
            callback.done(conversations, null);
        }
    }

    private List<MyConversation> findRecentConversations() {
        final List<MyConversation> list = new ArrayList<>();
        AVIMConversationQuery query = MyClientManager.getInstance().getClient().getQuery();
        query.withMembers(Arrays.asList(MyClientManager.getInstance().getClientId()));
        query.whereEqualTo(ConversationType.ATTR_TYPE_KEY, ConversationType.Single.getValue());
        query.orderByDescending(MyClientManager.KEY_UPDATED_AT);
        query.limit(100);
        query.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> conversations, AVIMException e) {
                if (e != null) {
                    for (AVIMConversation conversation : conversations) {
                        MyConversation m = new MyConversation();
                        m.setConversationId(conversation.getConversationId());
                        m.setUnreadCount(0);
                        list.add(m);
                    }
                }
            }
        });
        return list;
    }


}
