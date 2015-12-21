package com.fdu.socialapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.fdu.socialapp.Constants;
import com.fdu.socialapp.activities.Login;
import com.fdu.socialapp.activities.Main;
import com.fdu.socialapp.activities.SingleChat;
import com.fdu.socialapp.model.ChatManager;

/**
 * Created by mao on 2015/11/14 0014.
 * 将所有 notification 都发送至此类，然后由此类做分发。
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ChatManager.getInstance().getSelfId() == null) {
            Intent startActivityIntent = new Intent(context, Login.class);
            startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startActivityIntent);
        } else {

            String tag = intent.getStringExtra(Constants.NOTOFICATION_TAG);
            if (Constants.NOTIFICATION_GROUP_CHAT.equals(tag)) {
                gotoSingleChatActivity(context, intent);
            } else if (Constants.NOTIFICATION_SINGLE_CHAT.equals(tag)) {
                gotoSingleChatActivity(context, intent);
            } else if (Constants.NOTIFICATION_SYSTEM.equals(tag)) {
                gotoNewFriendActivity(context, intent);

            }
        }
    }

    private void gotoSingleChatActivity(Context context, Intent intent) {
        Intent startActivityIntent = new Intent(context, SingleChat.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityIntent.putExtra(Constants.MEMBER_ID, intent.getStringExtra(Constants.MEMBER_ID));
        context.startActivity(startActivityIntent);
    }

    private void gotoNewFriendActivity(Context context, Intent intent) {
        Intent startActivityIntent = new Intent(context, Main.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startActivityIntent);
    }
}
