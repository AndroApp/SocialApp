package com.fdu.socialapp.model;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.fdu.socialapp.avobject.AddRequest;
import com.fdu.socialapp.service.ConversationManager;
import com.fdu.socialapp.service.PushManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;


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

    /**
     * 初始化ImageLoader
     */
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                        //.memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        AVObject.registerSubclass(AddRequest.class);
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
        initImageLoader(myApp);
    }
}
