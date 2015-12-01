package com.fdu.socialapp.model;

import android.app.Application;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.SaveCallback;
import com.fdu.socialapp.service.ConversationManager;
import com.fdu.socialapp.service.PushManager;


/**
 * Created by mao on 2015/10/12 0012.
 * 初始化工作
 */
public class App extends Application{
    public static boolean debug = true;
    public static App myApp;

    private final String TAG = "App";

    private void initChatManager() {
        final ChatManager chatManager = ChatManager.getInstance();
        chatManager.init(this);
        if (MsnaUser.getCurrentUser() != null) {
            chatManager.setupManagerWithUserId(MsnaUser.getCurrentUser().getObjectId());
        }
        chatManager.setConversationEventHandler(ConversationManager.getEventHandler());
    }


    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        AVOSCloud.initialize(this, "zFeMVYB4tMuBVvjcAWt8uBOh", "IInViyO81sNlBNj4TUSoXyQH");
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    // 关联  installationId 到用户表等操作……
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
        PushManager.getInstance().init(this);
        initChatManager();
    }
}
