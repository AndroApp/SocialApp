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
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.fdu.socialapp.Constants;
import com.fdu.socialapp.R;
import com.fdu.socialapp.activities.BaseActivity;
import com.fdu.socialapp.activities.Login;
import com.fdu.socialapp.activities.Main;
import com.fdu.socialapp.viewholder.MyClientManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by mao on 2015/10/28 0028.
 * 用户的抽象
 */
public class MsnaUser extends AVUser{
    public static final String TAG = "MsnaUser";
    public static final String USERNAME = "username";
    public static final String AVATAR = "avatar";
    public static final String LOCATION = "location";

    public static MsnaUser getCurrentUser() {
        return getCurrentUser(MsnaUser.class);
    }

    public static boolean isLogin() {
        return (AVUser.getCurrentUser() != null);
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
        if (q != null) {
            q.setCachePolicy(cachePolicy);
            q.setMaxCacheAge(TimeUnit.MINUTES.toMillis(1));
            q.findInBackground(findCallback);
        }
    }


}
