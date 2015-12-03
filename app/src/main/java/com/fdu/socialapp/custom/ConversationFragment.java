package com.fdu.socialapp.custom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMSingleMessageQueryCallback;
import com.fdu.socialapp.Constants;
import com.fdu.socialapp.R;
import com.fdu.socialapp.activities.SingleChat;
import com.fdu.socialapp.adapter.ConversationListAdapter;
import com.fdu.socialapp.controller.ConversationHelper;
import com.fdu.socialapp.event.ConversationItemClickEvent;
import com.fdu.socialapp.event.ImTypeMessageEvent;
import com.fdu.socialapp.model.ChatManager;
import com.fdu.socialapp.model.ConversationType;
import com.fdu.socialapp.model.Room;
import com.fdu.socialapp.service.ConversationManager;
import com.fdu.socialapp.utils.AVUserCacheUtils;
import com.fdu.socialapp.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by mao on 2015/11/20 0020.
 * 聊天列表界面
 */
public class ConversationFragment extends BaseFragment implements ChatManager.ConnectionListener{
    protected ConversationListAdapter<Room> itemAdapter;
    protected RecyclerView recyclerView;
    protected LinearLayoutManager linearLayoutManager;
    protected SwipeRefreshLayout swipeRefreshLayout;

    private ConversationManager conversationManager;
    private boolean hidden;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sessions, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_conversation_list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_conversation_pullRefresh);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        conversationManager = ConversationManager.getInstance();
        recyclerView.setLayoutManager(linearLayoutManager);
        itemAdapter = new ConversationListAdapter<>();
        recyclerView.setAdapter(itemAdapter);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateConversationList();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateConversationList();
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            updateConversationList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            updateConversationList();
            NotificationUtils.setNeverShow(true);
        } else {
            NotificationUtils.setNeverShow(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        NotificationUtils.setNeverShow(false);
    }

    public void onEvent(ImTypeMessageEvent event) {
        updateConversationList();
    }

    public void onEvent(ConversationItemClickEvent event) {

        Intent intent = new Intent(getActivity(), SingleChat.class);
        intent.putExtra(Constants.CONVERSATION_ID, event.conversationId);
        startActivity(intent);
    }



    private void updateConversationList() {
        conversationManager.findAndCacheRooms(new Room.MultiRoomsCallback() {
            @Override
            public void done(List<Room> roomList, AVException exception) {
                swipeRefreshLayout.setRefreshing(false);
                if (filterException(exception)) {
                    updateLastMessage(roomList);
                    cacheRelatedUsers(roomList);

                    List<Room> sortedRooms = sortRooms(roomList);
                    itemAdapter.setDataList(sortedRooms);
                    itemAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void updateLastMessage(final List<Room> conversationList) {
        for (final Room room : conversationList) {
            AVIMConversation conversation = room.getConversation();
            if (null != conversation) {
                conversation.getLastMessage(new AVIMSingleMessageQueryCallback() {
                    @Override
                    public void done(AVIMMessage avimMessage, AVIMException e) {
                        if (filterException(e) && null != avimMessage) {
                            room.setLastMessage(avimMessage);
                            int index = conversationList.indexOf(room);
                            itemAdapter.notifyItemChanged(index);
                        }
                    }
                });
            }
        }
    }

    private void cacheRelatedUsers(List<Room> rooms) {
        List<String> needCacheUsers = new ArrayList<>();
        for(Room room : rooms) {
            AVIMConversation conversation = room.getConversation();
            if (ConversationHelper.typeOfConversation(conversation) == ConversationType.Single) {
                needCacheUsers.add(ConversationHelper.otherIdOfConversation(conversation));
            }
        }
        needCacheUsers.add(ChatManager.getInstance().getSelfId());
        AVUserCacheUtils.cacheUsers(needCacheUsers, new AVUserCacheUtils.CacheUserCallback() {
            @Override
            public void done(Exception e) {
                itemAdapter.notifyDataSetChanged();
            }
        });
    }

    private List<Room> sortRooms(final List<Room> roomList) {
        List<Room> sortedList = new ArrayList<>();
        if (null != roomList) {
            sortedList.addAll(roomList);
            Collections.sort(sortedList, new Comparator<Room>() {
                @Override
                public int compare(Room lhs, Room rhs) {
                    long value = lhs.getLastModifyTime() - rhs.getLastModifyTime();
                    if (value > 0) {
                        return -1;
                    } else if (value < 0) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
        }
        return sortedList;
    }

    @Override
    public void onConnectionChanged(boolean connect) {

    }
}
