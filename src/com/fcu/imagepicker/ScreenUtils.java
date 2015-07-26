package com.fcu.imagepicker;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * ScreenUtils
 */
public class ScreenUtils {
    private static int screenW;
    private static int screenH;
    private static float screenDensity;

    public static void initScreen(Activity mActivity){
        DisplayMetrics metric = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenW = metric.widthPixels;
        screenH = metric.heightPixels;
        screenDensity = metric.density;
    }

    public static int getScreenW(){
        return screenW;
    }

    public static int getScreenH(){
        return screenH;
    }

    public static float getScreenDensity(){
        return screenDensity;
    }

    /** 根據手機的分辨率從 dp 的單位 轉成為 px(像素) */
    public static int dp2px(float dpValue) {
        return (int) (dpValue * getScreenDensity() + 0.5f);
    }

    /** 根據手機的分辨率從 px(像素) 的單位 轉成為 dp */
    public static int px2dp(float pxValue) {
        return (int) (pxValue / getScreenDensity() + 0.5f);
    }
}
