package com.fdu.socialapp.utils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.fdu.socialapp.Constants;
import com.fdu.socialapp.model.MsnaUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mao on 2015/11/24 0024.
 */
public class AVUserCacheUtils {
    private static Map<String, MsnaUser> userMap;

    static {
        userMap = new HashMap<>();
    }

    public static MsnaUser getCachedUser(String objectId) {
        return userMap.get(objectId);
    }

    public static void cacheUser(String userId, MsnaUser user) {
        userMap.put(userId, user);
    }

    public static void cacheUsers(List<String> ids, final CacheUserCallback cacheUserCallback) {
        Set<String> uncachedIds = new HashSet<>();
        for (String id : ids) {
            if (!userMap.containsKey(id)) {
                uncachedIds.add(id);
            }
        }

        if (uncachedIds.isEmpty()) {
            if (null != cacheUserCallback) {
                cacheUserCallback.done(null);
                return;
            }
        }

        AVQuery<MsnaUser> q = AVUser.getQuery(MsnaUser.class);
        q.whereContainedIn(Constants.OBJECT_ID, uncachedIds);
        q.setLimit(1000);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        q.findInBackground(new FindCallback<MsnaUser>() {
            @Override
            public void done(List<MsnaUser> list, AVException e) {
                if (null == e) {
                    for (AVUser user : list) {
                        userMap.put(user.getObjectId(), (MsnaUser)user);
                    }
                }
                if (null != cacheUserCallback) {
                    cacheUserCallback.done(e);
                }
            }
        });
    }

    public static List<MsnaUser> getUsersFromCache(List<String> ids) {
        List<MsnaUser> userList = new ArrayList<>();
        for (String id : ids) {
            if (userMap.containsKey(id)) {
                userList.add(userMap.get(id));
            }
        }
        return userList;
    }

    public static void cacheUsers(List<String> ids) {
        cacheUsers(ids, null);
    }

    public static abstract class CacheUserCallback {
        public abstract void done(Exception e);
    }
}
