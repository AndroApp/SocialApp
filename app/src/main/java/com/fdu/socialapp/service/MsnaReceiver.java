package com.fdu.socialapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.fdu.socialapp.Constants;
import com.fdu.socialapp.R;
import com.fdu.socialapp.event.InvitationEvent;
import com.fdu.socialapp.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by mao on 2015/11/14 0014.
 */
public class MsnaReceiver extends BroadcastReceiver{
    public final static String AVOS_DATA = "com.avoscloud.Data";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            if (action.equals(context.getString(R.string.invitation_action))) {
                String avosData = intent.getStringExtra(AVOS_DATA);
                if (!TextUtils.isEmpty(avosData)) {
                    try {
                        JSONObject json = new JSONObject(avosData);
                        String alertStr = json.getString(PushManager.ALERT);

                        Intent notificationIntent = new Intent(context, NotificationBroadcastReceiver.class);
                        notificationIntent.putExtra(Constants.NOTOFICATION_TAG, Constants.NOTIFICATION_SYSTEM);
                        NotificationUtils.showNotification(context, "MSNA", alertStr, notificationIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        EventBus.getDefault().post(new InvitationEvent());
    }
}
