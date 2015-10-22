package com.fdu.socialapp.custom;

import android.app.Application;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;


/**
 * Created by mao on 2015/10/12 0012.
 * A class for users
 */
public class User extends Application{
    private final String TAG = "User";

    private String userName;
    private String installationId;

    private static User myUser;

    public static User getMyUser(){
        return myUser;
    }
    public boolean isLogin() {
        if(AVUser.getCurrentUser() != null) return true;
        else return false;
    }
    public String getUserName() {
        if (isLogin()) return userName;
        else return null;
    }
    public void setUserName(String name) {
        userName = name;
    }


    public void logout() {
        AVUser user = AVUser.getCurrentUser();
        user.put("installationId", null);
        user.put("num", 0);
        user.saveInBackground();
        AVUser.logOut();
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
        userName = null;
        myUser = this;
    }
}
