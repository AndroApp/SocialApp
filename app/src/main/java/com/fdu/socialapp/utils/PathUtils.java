package com.fdu.socialapp.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.fdu.socialapp.model.ChatManager;

import java.io.File;

/**
 * Created by mao on 2015/11/24 0024.
 *
 */
public class PathUtils {
    private static File checkAndMkdirs(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * @return file
     */
    private static File getAvailableCacheDir() {
        if (isExternalStorageWritable()) {
            return ChatManager.getContext().getExternalCacheDir();
        } else {
            // 只有此应用才能访问。拍照的时候有问题，因为拍照的应用写入不了该文件
            return ChatManager.getContext().getCacheDir();
        }
    }

    /**
     * 可能文件会被清除掉，需要检查是否存在
     *
     * @param id
     * @return file
     */
    public static String getChatFilePath(String id) {
        return (TextUtils.isEmpty(id) ? null : new File(getAvailableCacheDir(), id).getAbsolutePath());
    }

    /**
     * 录音保存的地址
     *
     * @return file
     */
    public static String getRecordPathByCurrentTime() {
        return new File(getAvailableCacheDir(), "record_" + System.currentTimeMillis()).getAbsolutePath();
    }

    /**
     * 拍照保存的地址
     *
     * @return
     */
    public static String getPicturePathByCurrentTime() {
        String path = new File(getAvailableCacheDir(), "picture_" + System.currentTimeMillis()).getAbsolutePath();
//    LogUtils.d("picture path ", path);
        return path;
    }

    public static String getAvatarCropPath() {
        return new File(getAvailableCacheDir(), "avatar_crop").getAbsolutePath();
    }

    public static String getAvatarTmpPath() {
        return new File(getAvailableCacheDir(), "avatar_tmp").getAbsolutePath();
    }


    public static String getQRcodeImagePath() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MSNA_QRcode");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("PathUtils", "failed to create directory");
                return null;
            }
        }
        return new File(mediaStorageDir.getPath() + File.separator +
                "QRcode"+".PNG").getAbsolutePath();
    }

}
