package com.fdu.socialapp.model;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FollowCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by mao on 2015/10/28 0028.
 * 用户的抽象
 */
public class MsnaUser extends AVUser{
    public static final String TAG = "MsnaUser";
    public static final String AVATAR = "avatar";

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

    public static String getNickname() {
        MsnaUser user = getCurrentUser();
        return user.getString("nickname");
    }

    public String getAvatarUrl() {
        AVFile avatar = getAVFile(AVATAR);
        if (avatar != null) {
            return avatar.getUrl();
        } else {
            return null;
        }
    }


    public void saveAvatar(String path, final SaveCallback saveCallback) {
        final AVFile file;
        try {
            file = AVFile.withAbsoluteLocalPath(getUsername(), path);
            put(AVATAR, file);
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (null == e) {
                        saveInBackground(saveCallback);
                    } else {
                        if (null != saveCallback) {
                            saveCallback.done(e);
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
