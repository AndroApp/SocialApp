package com.fdu.socialapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.fdu.socialapp.Constants;
import com.fdu.socialapp.activities.Login;
import com.fdu.socialapp.activities.SingleChat;
import com.fdu.socialapp.model.MyClientManager;

/**
 * Created by mao on 2015/11/14 0014.
 * 将所有 notification 都发送至此类，然后由此类做分发。
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (MyClientManager.getInstance().getClientId() == null) {
            Intent startActivityIntent = new Intent(context, Login.class);
            startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startActivityIntent);
        } else {
            String conversationId = intent.getStringExtra(Constants.CONVERSATION_ID);
            if (!TextUtils.isEmpty(conversationId)) {
                gotoSingleChatActivity(context, intent);
            }
        }
    }

    private void gotoSingleChatActivity(Context context, Intent intent) {
        Intent startActivityIntent = new Intent(context, SingleChat.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityIntent.putExtra(Constants.MEMBER_ID, intent.getStringExtra(Constants.MEMBER_ID));
        context.startActivity(startActivityIntent);
    }
}
