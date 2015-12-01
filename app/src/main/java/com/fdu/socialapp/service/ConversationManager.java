package com.fdu.socialapp.service;

import android.graphics.Bitmap;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationEventHandler;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.fdu.socialapp.Constants;
import com.fdu.socialapp.controller.MessageAgent;
import com.fdu.socialapp.controller.MessageHelper;
import com.fdu.socialapp.R;
import com.fdu.socialapp.event.ConversationChangeEvent;
import com.fdu.socialapp.model.App;
import com.fdu.socialapp.model.ChatManager;
import com.fdu.socialapp.model.ConversationType;
import com.fdu.socialapp.model.Room;
import com.fdu.socialapp.utils.ConversationCacheUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by mao on 2015/11/20 0020.
 */
public class ConversationManager {
    private static ConversationManager conversationManager;
    private static AVIMConversationEventHandler eventHandler = new AVIMConversationEventHandler() {
        @Override
        public void onMemberLeft(AVIMClient client, AVIMConversation conversation, List<String> members, String kickedBy) {
            refreshCacheAndNotify(conversation);
        }

        @Override
        public void onMemberJoined(AVIMClient client, AVIMConversation conversation, List<String> members, String invitedBy) {
            refreshCacheAndNotify(conversation);
        }

        @Override
        public void onKicked(AVIMClient client, AVIMConversation conversation, String kickedBy) {
            refreshCacheAndNotify(conversation);
        }

        @Override
        public void onInvited(AVIMClient client, AVIMConversation conversation, String operator) {
            refreshCacheAndNotify(conversation);
        }

        private void refreshCacheAndNotify(AVIMConversation conversation) {
            ConversationChangeEvent conversationChangeEvent = new ConversationChangeEvent(conversation);
            EventBus.getDefault().post(conversationChangeEvent);
        }
    };

    public ConversationManager() {}

    public static synchronized ConversationManager getInstance() {
        if (conversationManager == null) {
            conversationManager = new ConversationManager();
        }
        return conversationManager;
    }

    public void findAndCacheRooms(final Room.MultiRoomsCallback callback) {
        final List<Room> rooms = ChatManager.getInstance().findRecentRooms();
        List<String> conversationIds = new ArrayList<>();
        for (Room room : rooms) {
            conversationIds.add(room.getConversationId());
        }

        if (conversationIds.size() > 0) {
            ConversationCacheUtils.cacheConversations(conversationIds, new ConversationCacheUtils.CacheConversationCallback() {
                @Override
                public void done(AVException e) {
                    if (e != null) {
                        callback.done(rooms, e);
                    } else {
                        callback.done(rooms, null);
                    }
                }
            });
        } else {
            callback.done(rooms, null);
        }
    }





    public void findGroupConversationsIncludeMe(AVIMConversationQueryCallback callback) {
        AVIMConversationQuery conversationQuery = ChatManager.getInstance().getConversationQuery();
        if (null != conversationQuery) {
            conversationQuery.containsMembers(Arrays.asList(ChatManager.getInstance().getSelfId()));
            conversationQuery.whereEqualTo(ConversationType.ATTR_TYPE_KEY, ConversationType.Group.getValue());
            conversationQuery.orderByDescending(Constants.UPDATED_AT);
            conversationQuery.limit(1000);
            conversationQuery.findInBackground(callback);
        } else if (null != callback) {
            callback.done(new ArrayList<AVIMConversation>(), null);
        }
    }

    public void createGroupConversation(List<String> members, final AVIMConversationCreatedCallback callback) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ConversationType.TYPE_KEY, ConversationType.Group.getValue());
        final String name = MessageHelper.nameByUserIds(members);
        map.put("name", name);
        ChatManager.getInstance().createConversation(members, map, callback);
    }

    public static Bitmap getConversationIcon(AVIMConversation conversation) {
        return ColoredBitmapProvider.getInstance().createColoredBitmapByHashString(conversation.getConversationId());
    }

    public void sendWelcomeMessage(String toUserId) {
        ChatManager.getInstance().fetchConversationWithUserId(toUserId,
                new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation avimConversation, AVIMException e) {
                        if (e == null) {
                            MessageAgent agent = new MessageAgent(avimConversation);
                            agent.sendText(App.myApp.getString(R.string.message_when_agree_request));
                        }
                    }
                });
    }

    public static AVIMConversationEventHandler getEventHandler() {
        return eventHandler;
    }
}
