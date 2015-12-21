package com.fdu.socialapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.fdu.socialapp.Constants;
import com.fdu.socialapp.R;
import com.fdu.socialapp.adapter.MyPagerAdapter;
import com.fdu.socialapp.controller.ShakeListener;
import com.fdu.socialapp.custom.PagerSlidingTabStrip;
import com.fdu.socialapp.decoding.QRcodeController;
import com.fdu.socialapp.model.ChatManager;
import com.fdu.socialapp.model.MsnaUser;
import com.fdu.socialapp.service.CacheService;
import com.fdu.socialapp.service.PushManager;
import com.fdu.socialapp.utils.DisplayUtil;
import com.fdu.socialapp.utils.PathUtils;
import com.fdu.socialapp.utils.PhotoUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

public class Main extends BaseActivity {
    private static final String TAG = "Main";
    private final static int SCANNIN_GREQUEST_CODE = 1;

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
            logout();
            return true;
        }

        if (id == R.id.addFriends){
            Intent intent = new Intent();
            intent.setClass(this, AddFriends.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.scanQRcode) {
            Intent intent = new Intent();
            intent.setClass(Main.this, CodeCapture.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    String userId = bundle.getString("result");
                    setUserInfoDialog(userId);
                }
                break;
        }
    }

    public void logout() {
        MsnaUser user = MsnaUser.getCurrentUser();
        if (user != null) {
            user.put(Constants.INSTALLATION, null);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (filterException(e)) {
                        PushManager.getInstance().unsubscribeCurrentUserChannel();
                        ChatManager.getInstance().closeWithCallback(new AVIMClientCallback() {
                            @Override
                            public void done(AVIMClient avimClient, AVIMException e) {
                                AVUser.logOut();
                                finish();
                                Intent intent = new Intent(Main.this, Launch.class);
                                startActivity(intent);
                            }
                        });
                    }
                }
            });
        } else {
            toast("异常！用户为空");
            Intent intent = new Intent(Main.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void userInfo(View view) {

    }

    public void generateCode(View view) {
        switch (view.getId()) {
            case R.id.ErCode_layout:
                try {
                    setQRcodeDialog(QRcodeController.generateUserCode(ChatManager.getInstance().getSelfId()));
                } catch (WriterException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            default:
                break;
        }
    }

    public void setQRcodeDialog(final Bitmap bitmap) {

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);

        ImageView QRcode = new ImageView(this);
        QRcode.setImageBitmap(bitmap);

        builder.setView(QRcode)
            .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String path = PathUtils.getQRcodeImagePath();
                    Uri uri = PhotoUtils.saveBitmap(path, bitmap);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(uri);
                    Main.this.sendBroadcast(intent);
                }
            });
        final AlertDialog dialog;
        dialog = builder.create();
        dialog.show();
    }

    public void setUserInfoDialog(String userId) {

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        final ImageView avatarView = new ImageView(this);
        avatarView.setImageDrawable(getDrawable(R.mipmap.ic_launcher));
        final TextView userTxt = new TextView(this);

        final MsnaUser user = CacheService.lookupUser(userId);
        if (user == null) {
            AVQuery<MsnaUser> q = AVUser.getQuery(MsnaUser.class);
            q.whereEqualTo(Constants.OBJECT_ID, userId);
            q.findInBackground(new FindCallback<MsnaUser>() {
                @Override
                public void done(List<MsnaUser> list, AVException e) {
                    if (filterException(e)) {
                        if (list.size() > 0) {
                            MsnaUser user = list.get(0);
                            ImageLoader.getInstance().displayImage(user.getAvatarUrl(), avatarView, PhotoUtils.avatarImageOptions);
                            userTxt.setText(user.getUsername());
                        }
                    }
                }
            });
        } else {
            ImageLoader.getInstance().displayImage(user.getAvatarUrl(), avatarView, PhotoUtils.avatarImageOptions);
            userTxt.setText(user.getUsername());
        }
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        userTxt.setTextSize(24);
        userTxt.setGravity(Gravity.CENTER);
        linearLayout.addView(avatarView);
        linearLayout.addView(userTxt);
        linearLayout.setPadding(0, 20, 0, 0);
        builder.setView(linearLayout);
        builder.setTitle("确认添加好友")
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null);
        Dialog dialog = builder.create();
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width =  DisplayUtil.dip2px(this, 240);
        dialog.getWindow().setAttributes(params);
    }



    public void send(View view) {
        EditText testUser = (EditText) findViewById(R.id.testMessage);
        final String userName = testUser.getText().toString().trim();

        AVQuery<AVUser> query = MsnaUser.getQuery();
        query.whereEqualTo("username", userName);
        query.limit(1);

        query.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (filterException(e)) {
                    if (list.size() > 0) {
                        String userId = list.get(0).getObjectId();
                        Intent intent = new Intent(Main.this, SingleChat.class);
                        intent.putExtra(Constants.MEMBER_ID, userId);
                        intent.putExtra(Constants.MEMBER_NAME, userName);
                        startActivity(intent);
                    }
                }
            }
        });

    }

}
