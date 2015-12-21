package com.fdu.socialapp.service;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FollowCallback;
import com.avos.avoscloud.SaveCallback;
import com.fdu.socialapp.R;
import com.fdu.socialapp.avobject.AddRequest;
import com.fdu.socialapp.model.App;
import com.fdu.socialapp.model.MsnaUser;
import com.fdu.socialapp.utils.AVUserCacheUtils;
import com.fdu.socialapp.utils.SimpleNetTask;
import com.fdu.socialapp.utils.Utils;

import java.util.List;

/**
 * Created by mh on 2015/12/1.
 */
public class AddRequestManager {
    private static AddRequestManager addRequestManager;

    /**
     * 用户端未读的邀请消息的数量
     */
    private int unreadAddRequestsCount = 0;

    public static synchronized AddRequestManager getInstance() {
        if (addRequestManager == null) {
            addRequestManager = new AddRequestManager();
        }
        return addRequestManager;
    }

    public AddRequestManager() {}

    /**
     * 是否有未读的消息
     */
    public boolean hasUnreadRequests() {
        return unreadAddRequestsCount > 0;
    }

    /**
     * 推送过来时自增
     */
    public void unreadRequestsIncrement() {
        ++ unreadAddRequestsCount;
    }

    /**
     * 从 server 获取未读消息的数量
     */
    public void countUnreadRequests(final CountCallback countCallback) {
        AVQuery<AddRequest> addRequestAVQuery = AVObject.getQuery(AddRequest.class);
        addRequestAVQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
        addRequestAVQuery.whereEqualTo(AddRequest.TO_USER, MsnaUser.getCurrentUser());
        addRequestAVQuery.whereEqualTo(AddRequest.IS_READ, false);
        addRequestAVQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (null != countCallback) {
                    unreadAddRequestsCount = i;
                    countCallback.done(i, e);
                }
            }
        });
    }

    /**
     * 标记消息为已读，标记完后会刷新未读消息数量
     */
    public void markAddRequestsRead(List<AddRequest> addRequestList) {
        if (addRequestList != null) {
            for (AddRequest request : addRequestList) {
                request.put(AddRequest.IS_READ, true);
            }
            AVObject.saveAllInBackground(addRequestList, new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        countUnreadRequests(null);
                    }
                }
            });
        }
    }

    public List<AddRequest> findAddRequests(int skip, int limit) throws AVException {
        MsnaUser user = MsnaUser.getCurrentUser();
        AVQuery<AddRequest> q = AVObject.getQuery(AddRequest.class);
        q.include(AddRequest.FROM_USER);
        q.skip(skip);
        q.limit(limit);
        q.whereEqualTo(AddRequest.TO_USER, user);
        q.orderByDescending(AVObject.CREATED_AT);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        return q.find();
    }

    public void findAddRequestInBackground(int skip,int limit,FindCallback<AddRequest> findCallback) throws AVException{
        MsnaUser user = MsnaUser.getCurrentUser();
        AVQuery<AddRequest> q = AVObject.getQuery(AddRequest.class);
        q.include(AddRequest.FROM_USER);
        q.skip(skip);
        q.limit(limit);
        q.whereEqualTo(AddRequest.TO_USER, user);
        q.orderByDescending(AVObject.CREATED_AT);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        q.findInBackground(findCallback);
    }

    public void agreeAddRequest(final AddRequest addRequest, final SaveCallback saveCallback) {
        addFriend(addRequest.getFromUser().getObjectId(), new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    if (e.getCode() == AVException.DUPLICATE_VALUE) {
                        addRequest.setStatus(AddRequest.STATUS_DONE);
                        addRequest.saveInBackground(saveCallback);
                    } else {
                        saveCallback.done(e);
                    }
                } else {
                    addRequest.setStatus(AddRequest.STATUS_DONE);
                    addRequest.saveInBackground(saveCallback);
                }
            }
        });
    }

    public static void addFriend(String friendId, final SaveCallback saveCallback) {
        MsnaUser user = MsnaUser.getCurrentUser();
        user.followInBackground(friendId, new FollowCallback() {
            @Override
            public void done(AVObject object, AVException e) {
                if (saveCallback != null) {
                    saveCallback.done(e);
                }
            }
        });
        AVUserCacheUtils.getCachedUser(friendId).followInBackground(user.getObjectId(), new FollowCallback() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (saveCallback != null){
                    saveCallback.done(e);
                }
            }

            @Override
            protected void internalDone0(Object o, AVException e) {

            }
        });
    }

    private void createAddRequest(MsnaUser toUser) throws Exception {
        MsnaUser curUser = MsnaUser.getCurrentUser();
        AVQuery<AddRequest> q = AVObject.getQuery(AddRequest.class);
        q.whereEqualTo(AddRequest.FROM_USER, curUser);
        q.whereEqualTo(AddRequest.TO_USER, toUser);
        q.whereEqualTo(AddRequest.STATUS, AddRequest.STATUS_WAIT);
        int count = 0;
        try {
            count = q.count();
        } catch (AVException e) {
            Log.e("msna",e.getMessage());
            if (e.getCode() == AVException.OBJECT_NOT_FOUND) {
                count = 0;
            } else {
                throw e;
            }
        }
        if (count > 0) {
            // 抛出异常，然后提示用户
            throw new IllegalStateException(App.myApp.getString(R.string.chat_alreadyCreateAddRequest));
        } else {
            AddRequest add = new AddRequest();
            add.setFromUser(curUser);
            add.setToUser(toUser);
            add.setStatus(AddRequest.STATUS_WAIT);
            add.setIsRead(false);
            add.save();
        }
    }

    public void createAddRequestInBackground(final Context ctx, final MsnaUser user) {
        new SimpleNetTask(ctx) {
            @Override
            protected void doInBack() throws Exception {
                createAddRequest(user);
            }

            @Override
            protected void onSucceed() {
                PushManager.getInstance().pushMessage(user.getObjectId(), ctx.getString(R.string.push_add_request),
                        ctx.getString(R.string.invitation_action));
                Utils.toast(R.string.chat_sendRequestSucceed);
            }
        }.execute();
    }

}
