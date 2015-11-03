package com.fdu.socialapp.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FollowCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.fdu.socialapp.activities.Main;

import java.util.concurrent.TimeUnit;

/**
 * Created by mao on 2015/10/28 0028.
 */
public class MsnaUser extends AVUser{
    public static final String TAG = "MsnaUser";
    public static final String USERNAME = "username";
    public static final String AVATAR = "avatar";
    public static final String LOCATION = "location";
    public static final String INSTALLATION = "installation";

    public static MsnaUser getCurrentUser() {
        return getCurrentUser(MsnaUser.class);
    }

    public void updateUserInfo(final Activity activity) {
        AVInstallation installation = AVInstallation.getCurrentInstallation();
        if (installation != null) {
            put(INSTALLATION, installation);
            put("num", 1);
            saveInBackground(new SaveCallback() {
                public void done(AVException e) {
                    if (e == null) {
                        // 保存成功
                        Main.goMainActivityFromActivity(activity);
                    } else {
                        // 保存失败，输出错误信息
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static void signUpByNameAndPwd(String name, String password, SignUpCallback callback) {
        AVUser user = new AVUser();
        user.setUsername(name);
        user.setPassword(password);
        user.put("num", 0);
        user.signUpInBackground(callback);
    }
    public void removeFriend(String friendId, final SaveCallback saveCallback) {
        unfollowInBackground(friendId, new FollowCallback() {
            @Override
            public void done(AVObject object, AVException e) {
                if (saveCallback != null) {
                    saveCallback.done(e);
                }
            }
        });
    }

    public void findFriendsWithCachePolicy(AVQuery.CachePolicy cachePolicy, FindCallback<MsnaUser>
            findCallback) {
        AVQuery<MsnaUser> q = null;
        try {
            q = followeeQuery(MsnaUser.class);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        q.setCachePolicy(cachePolicy);
        q.setMaxCacheAge(TimeUnit.MINUTES.toMillis(1));
        q.findInBackground(findCallback);
    }
}
