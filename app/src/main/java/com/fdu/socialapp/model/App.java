package com.fdu.socialapp.model;

import android.app.Application;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;


/**
 * Created by mao on 2015/10/12 0012.
 */
public class App extends Application{

    private final String TAG = "App";
    private String installationId;

    private static App myApp;

    public static App getMyApp(){
        return myApp;
    }
    public boolean isLogin() {
        if(AVUser.getCurrentUser() != null) return true;
        else return false;
    }

    public String  getInstallationId(){ return installationId; }

    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, "zFeMVYB4tMuBVvjcAWt8uBOh", "IInViyO81sNlBNj4TUSoXyQH");
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    // 关联  installationId 到用户表等操作……
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
        myApp = this;
    }
}
