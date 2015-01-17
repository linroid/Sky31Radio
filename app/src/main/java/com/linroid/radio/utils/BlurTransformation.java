package com.linroid.radio.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import com.squareup.picasso.Transformation;

import timber.log.Timber;

/**
 * Created by linroid on 1/16/15.
 */
public class BlurTransformation implements Transformation {
    Context ctx;
    String url;

    public BlurTransformation(Context ctx, String url) {
        this.ctx = ctx;
        this.url = url;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Timber.i("start bitmap transform");
        try {
            //                    Bitmap outputBitmap = Bitmap.createBitmap(centerThumbnailIV.getWidth(), centerThumbnailIV.getHeight(), Bitmap.Config.ARGB_8888);
            Bitmap outputBitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            RenderScript rs = RenderScript.create(ctx);
            ScriptIntrinsicBlur sib = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, source);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            sib.setRadius(20f);
            sib.setInput(tmpIn);
            sib.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);
            source.recycle();
            Timber.d("blur bitmap success");
            return outputBitmap;
        } catch (Exception e) {
            Timber.e(e, "occur an error during blurring bitmap");
            return source;
        }
    }

    @Override
    public String key() {
        return url+"-blurred";
    }
}