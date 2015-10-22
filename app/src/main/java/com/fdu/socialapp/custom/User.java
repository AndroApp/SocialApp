package com.fdu.socialapp.custom;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.fdu.socialapp.activities.Login;
import com.fdu.socialapp.activities.Main;


/**
 * Created by mao on 2015/10/12 0012.
 * A class for users
 */
public class User extends Application{

    private final String TAG = "User";
    private String installationId;

    private static User myUser;

    public static User getMyUser(){
        return myUser;
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
        myUser = this;
    }
}
