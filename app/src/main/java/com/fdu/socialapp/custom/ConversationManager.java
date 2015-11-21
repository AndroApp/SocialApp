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


    public void findRecentConversations(final MyConversation.MultiConversationsCallback callback) {
        AVIMConversationQuery query = MyClientManager.getInstance().getClient().getQuery();
        query.containsMembers(Arrays.asList(MyClientManager.getInstance().getClientId()));
        query.whereEqualTo(ConversationType.ATTR_TYPE_KEY, ConversationType.Single.getValue());
        query.orderByDescending(MyClientManager.KEY_UPDATED_AT);
        query.limit(100);
        query.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> list, AVIMException e) {
                List<String> conversationIds = new ArrayList<>();
                final List<MyConversation> conversations = new ArrayList<>();

                for (AVIMConversation c : list) {
                    MyConversation mConversation = new MyConversation();
                    conversationIds.add(c.getConversationId());

                    mConversation.setConversationId(c.getConversationId());
                    mConversation.setUnreadCount(0);
                    conversations.add(mConversation);
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
        });
    }


}
