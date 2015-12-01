package com.fdu.socialapp.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.fdu.socialapp.R;
import com.fdu.socialapp.model.MsnaUser;
import com.fdu.socialapp.utils.PathUtils;
import com.fdu.socialapp.utils.PhotoUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/**
 * Created by mao on 2015/10/16 0016.
 *
 */
public class NavFragment extends BaseFragment {
    public static final String ARG_POSITION = "layout_id";
    private static final String TAG="NavFragment";
    private static final int IMAGE_PICK_REQUEST = 1;
    private static final int CROP_REQUEST = 2;

    public NavFragment() {
        // Empty constructor required for fragment subclasses
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Created");
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int position = getArguments().getInt(ARG_POSITION);
        View view;
        switch (position){
            case R.id.contacts_layout:
                Log.i(TAG, "CreateView: contacts");
                view = inflater.inflate(R.layout.fragment_contacts, container,false);
                break;

            case R.id.aboutme_layout:
                Log.i(TAG, "CreateView: aboutMe");
                view = inflater.inflate(R.layout.fragment_aboutme, container,false);
                ((TextView)view.findViewById(R.id.userInfoName)).setText(MsnaUser.getCurrentUser().getUsername());

                final TextView nickname = (TextView) view.findViewById(R.id.nickname);
                getNickname(nickname);
                view.findViewById(R.id.nickname_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setNickname(nickname);
                    }
                });

                final ImageView avatar = (ImageView) view.findViewById(R.id.userIcon);
                getAvatar(avatar);
                view.findViewById(R.id.userInfo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setAvatar(avatar);
                    }
                });
                break;

            case R.id.etc_layout:
                Log.i(TAG, "CreateView: etc");
                view = inflater.inflate(R.layout.fragment_etc, container,false);
                break;
            default:
                view = inflater.inflate(R.layout.fragment_etc, container,false);
        }
        return view;
    }

    private void getAvatar(ImageView avatar) {
        MsnaUser user = MsnaUser.getCurrentUser();
        ImageLoader.getInstance().displayImage(user.getAvatarUrl(), avatar, PhotoUtils.avatarImageOptions);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    private void getNickname(TextView nickname) {
        String nicknameStr = MsnaUser.getNickname();
        if (nicknameStr != null) {
            nickname.setText(nicknameStr);
        } else {
            nickname.setText("未设置");
        }
    }

    private void setNickname(final TextView nicknameView) {
        final EditText nicknameTxt = new EditText(this.getActivity());
        new AlertDialog.Builder(this.getActivity())
                .setTitle("请输入昵称")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(nicknameTxt)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String nick_name;
                        nick_name = nicknameTxt.getText().toString().trim();
                        if (nick_name.length() > 0) {
                            AVUser user = AVUser.getCurrentUser();
                            if (user != null) {
                                user.put("nickname", nick_name);
                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            toast("修改成功");
                                            (nicknameView).setText(nick_name);
                                        } else {
                                            toast("修改失败");
                                        }
                                    }
                                });
                            }
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    protected void toast(String str) {
        Toast.makeText(this.getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_REQUEST) {
                Uri uri = data.getData();
                startImageCrop(uri, 200, 200, CROP_REQUEST);
            } else if (requestCode == CROP_REQUEST) {
                final String path = saveCropAvatar(data);
                MsnaUser user = (MsnaUser)AVUser.getCurrentUser();
                user.saveAvatar(path, null);
            }
        }
    }



    private void setAvatar(ImageView avatar) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, IMAGE_PICK_REQUEST);
    }

    private Uri startImageCrop(Uri uri, int outputX, int outputY,
                              int requestCode) {
        Intent intent;
        intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        String outputPath = PathUtils.getAvatarTmpPath();
        Uri outputUri = Uri.fromFile(new File(outputPath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false); // face detection
        startActivityForResult(intent, requestCode);
        return outputUri;
    }

    private String saveCropAvatar(Intent data) {
        Bundle extras = data.getExtras();
        String path = null;
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            if (bitmap != null) {
                bitmap = PhotoUtils.toRoundCorner(bitmap, 10);
                path = PathUtils.getAvatarCropPath();
                PhotoUtils.saveBitmap(path, bitmap);
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
        return path;
    }


}
