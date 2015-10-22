package com.fdu.socialapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.fdu.socialapp.R;
import com.fdu.socialapp.custom.User;

public class Login extends Activity {
    private static final String TAG = "Login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVAnalytics.trackAppOpened(getIntent());

        if (User.getMyUser().isLogin()) {
            // 允许用户使用应用
            Intent intent = new Intent(this, Main.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_login);



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
        EditText Text_username = (EditText) findViewById(R.id.userName);
        EditText Text_pwd = (EditText) findViewById(R.id.pwd);
        String username = Text_username.getText().toString();
        String pwd = Text_pwd.getText().toString();
        AVUser.logInInBackground(username, pwd, new LogInCallback() {
            public void done(AVUser user, AVException e) {
                if (user != null) {
                    // 登录成功
                    Toast.makeText(Login.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, Main.class);
                    startActivity(intent);
                    finish();
                } else {
                    // 登录失败
                    Toast.makeText(Login.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void toSignUp(View view){
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

}
