package com.fcu.photocollage.library;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by user on 2015/8/27.
 */
public class AnimationTool {
    /**晃動動畫
     *
     * @param v
     */
    public static void shakeAnimation(View v) {
        // 創建動畫集
        AnimationSet animSet = new AnimationSet(true);
        // 加入大小動畫
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f);
        scaleAnimation.setDuration(100);
        animSet.addAnimation(scaleAnimation);
        // 加入角度動畫
        RotateAnimation rotateAnimation = new RotateAnimation(-4.0f, 4.0f, 60.0f, 50.0f);
        rotateAnimation.setDuration(100);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setRepeatMode(Animation.REVERSE);
        animSet.addAnimation(rotateAnimation);
        // 加入位置動畫
        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, -13.0f, 0.0f, -5.0f);
        translateAnimation.setDuration(100);
        animSet.addAnimation(translateAnimation);
        v.setAnimation(animSet);
        v.startAnimation(animSet);
    }
}
