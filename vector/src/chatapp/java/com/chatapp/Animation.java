package com.chatapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

import java.util.logging.Handler;

import at.wirecube.additiveanimations.additive_animator.AdditiveAnimator;
import im.vector.R;


public class Animation {

    public static void anim(View view) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "x", 300);
        anim.setDuration(800); // duration 5 seconds
        anim.start();
    }
    public static void reversAnim(View view) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "x", 0);
        anim1.setDuration(1000); // duration 5 seconds
        anim1.start();
    }

    public static void anim(View imageView, Activity activity) {
        // translationX to move object along x axis
        // next values are position value
//        float halfW;
//        Display display = activity.getWindowManager().getDefaultDisplay();
//        Point point = new Point();
//        display.getSize(point);
//        final int width = point.x; // screen width
//        halfW = width / 2.0f;
//        ObjectAnimator lftToRgt, rgtToLft;
//        AnimatorSet animatorSet = new AnimatorSet();
//        lftToRgt = ObjectAnimator.ofFloat(imageView, "translationX", 0f, halfW)
//                .setDuration(700); // to animate left to right
//        rgtToLft = ObjectAnimator.ofFloat(imageView, "translationX", halfW, 0f)
//                .setDuration(700); // to animate right to left
//
//        animatorSet.play(lftToRgt).before(rgtToLft); // manage sequence
//        animatorSet.start(); // play the animation
    }
}
