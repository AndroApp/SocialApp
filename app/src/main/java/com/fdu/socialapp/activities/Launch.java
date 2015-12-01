package com.fdu.socialapp.activities;

import android.os.Bundle;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.fdu.socialapp.Constants;
import com.fdu.socialapp.R;
import com.fdu.socialapp.model.ChatManager;
import com.fdu.socialapp.model.MsnaUser;


public class Launch extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVAnalytics.trackAppOpened(getIntent());
        setContentView(R.layout.activity_launch);
        if (MsnaUser.isLogin()) {
            // 允许用户使用应用
            updateUserInfo(MsnaUser.getCurrentUser());
        } else {
            Login.goLoginActivityFromActivity(Launch.this);
            finish();
        }
    }

    public void updateUserInfo(final MsnaUser user) {
        AVInstallation installation = AVInstallation.getCurrentInstallation();
        if (installation != null) {
            user.put(Constants.INSTALLATION, installation);
            user.put("num", 1);
            user.saveInBackground(new SaveCallback() {
                public void done(AVException e) {
                    if (filterException(e)) {
                        //根据用户名生成一个Client
                        ChatManager.getInstance().open(new AVIMClientCallback() {
                            @Override
                            public void done(AVIMClient avimClient, AVIMException e) {
                                if (filterException(e)) {
                                    toast("登录成功");
                                    Main.goMainActivityFromActivity(Launch.this);
                                    finish();
                                } else {
                                    Login.goLoginActivityFromActivity(Launch.this);
                                    finish();
                                }
                            }
                        });

                    }
                }
            });
        }
    }
}
