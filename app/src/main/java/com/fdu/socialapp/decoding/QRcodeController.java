package com.fdu.socialapp.decoding;

import android.graphics.Bitmap;
import android.graphics.Path;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mao on 2015/12/8 0008.
 *
 */
public class QRcodeController {
    /**
     * 生成图像
     *
     * @throws WriterException
     * @throws IOException
     */
    public static Bitmap generateUserCode(String userId) throws WriterException, IOException {

        int width = 960; // 图像宽度
        int height = 960; // 图像高度
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(userId,
                BarcodeFormat.QR_CODE, width, height, hints);// 生成矩阵
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[offset + x] = 0xff000000;
                } else {
                    pixels[offset + x] = 0xffffffff;
                }
            }
        }
        //生成二维码图片的格式，使用ARGB_8888
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
