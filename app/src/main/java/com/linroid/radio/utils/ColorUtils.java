package com.linroid.radio.utils;

import android.graphics.Color;

/**
 * Created by linroid on 2/2/15.
 */
public class ColorUtils {
    public static int transformIfTooWhite(int color){
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        float whiteRatio = (red+green+blue)/3f / 255f *(alpha/255f);
        float transformRatio = 0.7f;
        if(whiteRatio > transformRatio){
            red *= transformRatio;
            green *= transformRatio;
            blue *= transformRatio;
            color = Color.argb(alpha, red, green, blue);
        }
        return color;
    }
}
