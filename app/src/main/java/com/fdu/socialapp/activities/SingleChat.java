package com.fdu.socialapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.fdu.socialapp.Constants;
import com.fdu.socialapp.R;
import com.fdu.socialapp.controller.ConversationHelper;
import com.fdu.socialapp.custom.ChatFragment;
import com.fdu.socialapp.model.ChatManager;
import com.fdu.socialapp.utils.ConversationCacheUtils;

import butterknife.Bind;

public class SingleChat extends BaseActivity {

    @Bind(R.id.id_toolbar)
    protected Toolbar toolbar;

    private ChatFragment chatFragment;
    private String conversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);

        chatFragment = (ChatFragment) getFragmentManager().findFragmentById(R.id.fragment_chat);

        setActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        conversationId = getIntent().getStringExtra(Constants.CONVERSATION_ID);
        if (conversationId != null) {
            ChatManager.getInstance().getRoomsTable().clearUnread(conversationId);
            AVIMConversation con = ConversationCacheUtils.getCacheConversation(conversationId);
            chatFragment.setConversation(con);
            toolbar.setTitle(ConversationHelper.nameOfConversation(con));
        } else {
            String memberId = getIntent().getStringExtra(Constants.MEMBER_ID);
            String memberName = getIntent().getStringExtra(Constants.MEMBER_NAME);
            toolbar.setTitle(memberName);
            getConversation(memberId);
        }

    }

    @Override
    public void onBackPressed() {
        ChatManager.getInstance().getRoomsTable().clearUnread(conversationId);
        super.onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (null != extras && extras.containsKey(Constants.MEMBER_ID)) {
            String memberId = extras.getString(Constants.MEMBER_ID);
            String memberName = getIntent().getStringExtra(Constants.MEMBER_NAME);
            toolbar.setTitle(memberName);
            getConversation(memberId);
        } else if (null != extras && extras.containsKey(Constants.CONVERSATION_ID)){
            String conversationId = getIntent().getStringExtra(Constants.CONVERSATION_ID);
            AVIMConversation con = ConversationCacheUtils.getCacheConversation(conversationId);
            chatFragment.setConversation(con);
            toolbar.setTitle(ConversationHelper.nameOfConversation(con));
        }
    }

    private void getConversation(final String memberId) {
        ChatManager.getInstance().fetchConversationWithUserId(memberId, new AVIMConversationCreatedCallback() {
            @Override
            public void done(AVIMConversation avimConversation, AVIMException e) {
                if (filterException(e)) {
                    chatFragment.setConversation(avimConversation);
                    conversationId = avimConversation.getConversationId();
                    ChatManager.getInstance().getRoomsTable().insertRoom(conversationId);
                    ChatManager.getInstance().getRoomsTable().clearUnread(conversationId);
                }
            }
        });
    }
}
