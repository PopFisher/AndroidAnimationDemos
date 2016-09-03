package com.androidanimation.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * 工具类
 */

public class ViewUtil {

    public static float dp2px(Context context, float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }
}
