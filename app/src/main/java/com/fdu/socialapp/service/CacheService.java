package com.fdu.socialapp.service;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.fdu.socialapp.Constants;
import com.fdu.socialapp.model.MsnaUser;
import com.fdu.socialapp.utils.AVUserCacheUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mh on 2015/12/1.
 */
public class CacheService {
    private static volatile List<String> friendIds = new ArrayList<String>();

    public static MsnaUser lookupUser(String userId) {
        return AVUserCacheUtils.getCachedUser(userId);
    }

    public static void registerUser(MsnaUser user) {
        AVUserCacheUtils.cacheUser(user.getObjectId(), user);
    }

    public static void registerUsers(List<MsnaUser> users) {
        for (MsnaUser user : users) {
            registerUser(user);
        }
    }

    public static List<String> getFriendIds() {
        return friendIds;
    }

    public static void setFriendIds(List<String> friendList) {
        friendIds.clear();
        if (friendList != null) {
            friendIds.addAll(friendList);
        }
    }

    public static void cacheUsers(List<String> ids) throws AVException {
        Set<String> uncachedIds = new HashSet<String>();
        for (String id : ids) {
            if (lookupUser(id) == null) {
                uncachedIds.add(id);
            }
        }
        List<MsnaUser> foundUsers = findUsers(new ArrayList<String>(uncachedIds));
        registerUsers(foundUsers);
    }

    public static List<MsnaUser> findUsers(List<String> userIds) throws AVException {
        if (userIds.size() <= 0) {
            return Collections.EMPTY_LIST;
        }
        AVQuery<MsnaUser> q = AVUser.getQuery(MsnaUser.class);
        q.whereContainedIn(Constants.OBJECT_ID, userIds);
        q.setLimit(1000);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        return q.find();
    }
}
