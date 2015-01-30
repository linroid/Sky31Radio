package com.linroid.radio.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by linroid on 1/30/15.
 */
public class BitmapUtils {
    public static byte[] encodeToByteArray(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return  baos.toByteArray();
    }
}
