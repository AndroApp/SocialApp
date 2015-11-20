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
import com.fdu.socialapp.event.ConversationItemClickEvent;
import com.fdu.socialapp.event.ImTypeMessageEvent;
import com.fdu.socialapp.model.MyConversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by mao on 2015/11/20 0020.
 */
public class ConversationFragment extends BaseFragment {
    protected ConversationListAdapter<MyConversation> itemAdapter;
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
        }
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
        conversationManager.findAndCacheRooms(new MyConversation.MultiConversationsCallback() {
            @Override
            public void done(List<MyConversation> conversationList, AVException exception) {
                swipeRefreshLayout.setRefreshing(false);
                if (filterException(exception)) {

                    updateLastMessage(conversationList);

                    List<MyConversation> sortedConversations = sortConversations(conversationList);
                    itemAdapter.setDataList(sortedConversations);
                    itemAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void updateLastMessage(final List<MyConversation> conversationList) {
        for (final MyConversation myConversation : conversationList) {
            AVIMConversation conversation = myConversation.getConversation();
            if (null != conversation) {
                conversation.getLastMessage(new AVIMSingleMessageQueryCallback() {
                    @Override
                    public void done(AVIMMessage avimMessage, AVIMException e) {
                        if (filterException(e) && null != avimMessage) {
                            myConversation.setLastMessage(avimMessage);
                            int index = conversationList.indexOf(myConversation);
                            itemAdapter.notifyItemChanged(index);
                        }
                    }
                });
            }
        }
    }

    private List<MyConversation> sortConversations(final List<MyConversation> roomList) {
        List<MyConversation> sortedList = new ArrayList<>();
        if (null != roomList) {
            sortedList.addAll(roomList);
            Collections.sort(sortedList, new Comparator<MyConversation>() {
                @Override
                public int compare(MyConversation lhs, MyConversation rhs) {
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

}
