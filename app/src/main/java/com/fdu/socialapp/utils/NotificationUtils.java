package com.fdu.socialapp.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.avos.avospush.notification.NotificationCompat;
import com.fdu.socialapp.Constants;
import com.fdu.socialapp.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by mao on 2015/11/11 0011.
 * 用来确定一个特定的conversation是否应该展示通知
 */
public class NotificationUtils {
    private static boolean neverShow = false;
    private static List<String> notificationTagList = new LinkedList<>();
    private static HashMap<String, Integer> notificationIdList = new HashMap<>();

    public static void setNeverShow(boolean b) {
        neverShow = b;
    }

    public static void addTag(String tag) {
        if (!notificationTagList.contains(tag)) {
            notificationTagList.add(tag);
        }
    }

    public static void removeTag(String tag) {
        notificationTagList.remove(tag);
    }

    /**
     * 在 MessageHandler 弹出 notification 前会判断是否应该弹出 notification
     * 判断标准是该 tag 是否包含在 tag list 中
     * @param tag conversationId
     */
    public static boolean isShowNotification(String tag) {
        if (neverShow) {
            return false;
        } else {
            return !notificationTagList.contains(tag);
        }

    }
    public static void showNotification(Context context, String title, String content, Intent intent) {
        showNotification(context, title, content, null, intent);
    }

    public static void showNotification(Context context, String title, String content, String sound, Intent intent) {
        intent.setFlags(0);
        String conversationId = intent.getStringExtra(Constants.CONVERSATION_ID);
        int notificationId;
        if (notificationIdList.containsKey(conversationId)) {
            notificationId = notificationIdList.get(conversationId);
        } else {
            notificationId = (new Random()).nextInt();
            notificationIdList.put(conversationId, notificationId);
        }

        PendingIntent contentIntent = PendingIntent.getBroadcast(context, notificationId, intent, 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(title).setAutoCancel(true).setContentIntent(contentIntent)
                        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                        .setContentText(content);
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        if (sound != null && sound.trim().length() > 0) {
            notification.sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + sound);
        }
        manager.notify(notificationId, notification);
    }

}
