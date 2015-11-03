package com.fdu.socialapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.fdu.socialapp.R;
import com.fdu.socialapp.model.App;
import com.fdu.socialapp.model.MsnaUser;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Login extends BaseActivity {
    private static final String TAG = "Login";

    @Bind(R.id.userName)
    TextView txtUserName;

    @Bind(R.id.pwd)
    TextView txtPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVAnalytics.trackAppOpened(getIntent());

        if (App.getMyApp().isLogin()) {
            // 允许用户使用应用
            Intent intent = new Intent(this, Main.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logOut) {
            Log.i("shit", "退出1");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void login(View view){
        final String username = txtUserName.getText().toString().trim();
        final String pwd = txtPwd.getText().toString().trim();
        MsnaUser.logInInBackground(username, pwd, new LogInCallback<MsnaUser>() {
            public void done(MsnaUser user, AVException e) {
                if (user != null) {
                    if (user.getInt("num") == 0) {
                        // 登录成功
                        Toast.makeText(Login.this, "登录成功", Toast.LENGTH_SHORT).show();
                        AVUser.getCurrentUser(MsnaUser.class).updateUserInfo(Login.this);

                    } else {
                        Toast.makeText(Login.this, "该账号已在其他设备登录", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 登录失败
                    Toast.makeText(Login.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
            }
        }, MsnaUser.class);

    }


    public void toSignUp(View view){
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

}
