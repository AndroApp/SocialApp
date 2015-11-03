package com.fdu.socialapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;
import com.fdu.socialapp.R;
import com.fdu.socialapp.adapter.MyPagerAdapter;
import com.fdu.socialapp.custom.PagerSlidingTabStrip;


import java.util.List;

public class Main extends BaseActivity {
    private static final String TAG = "Main";

    public static void goMainActivityFromActivity(Activity fromActivity) {
        Intent intent = new Intent(fromActivity, Main.class);
        fromActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setActionBar(toolbar);

        //Set the sliding pages
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        MyPagerAdapter adapter = new MyPagerAdapter(getFragmentManager());
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(adapter);
        tabs.setViewPager(pager);
        tabs.setSelectedTextColor(getResources().getColor(R.color.myPrimaryDark));
        tabs.setTextColor(getResources().getColor(R.color.TextColorDark));
        tabs.setBackgroundColor(getResources().getColor(R.color.white));

        PushService.setDefaultPushCallback(this, Main.class);
        PushService.subscribe(this, "private", Login.class);

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
            AVUser user = AVUser.getCurrentUser();
            if(user != null){
                user.put("installationId", null);
                user.put("num", 0);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            AVUser.logOut();
                            Intent intent = new Intent(Main.this, Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
            }
            else{
                Toast.makeText(Main.this, "异常！用户为空", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Main.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void userInfo(View view){

    }

    public void setting(View view){

    }

    public void test(View view){
        Log.i(TAG, "test");
        EditText testUser = (EditText) findViewById(R.id.testMessage);
        String username = testUser.getText().toString();
        AVQuery<AVUser> query = AVUser.getQuery();
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<AVUser>() {
            public void done(List<AVUser> objects, AVException e) {
                if (e == null) {
                    // 查询成功
                    for (AVUser user : objects) {
                        AVQuery pushQuery = AVInstallation.getQuery();
                        pushQuery.whereEqualTo("installationId", user.getString("installationId"));
                        AVPush.sendMessageInBackground("message to installation", pushQuery, new SendCallback() {
                            @Override
                            public void done(AVException e) {
                                if(e == null){
                                    Toast.makeText(Main.this, "push success", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(Main.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    // 查询出错
                    Toast.makeText(Main.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }




}
