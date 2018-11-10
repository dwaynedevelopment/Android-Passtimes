package com.dwaynedevelopment.passtimes.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.dwaynedevelopment.passtimes.R;

import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.animation.AnimationUtils.loadAnimation;

public class ViewUtils {

    /** invokeToastMessage();
     * @param context
     * @param status
     * @param message
     */
//    public static void invokeToastMessage(Context context, int status, String message) {
//        switch (status) {
//            case TOAST_SUCCESS:
//                Toasty.success(context, message);
//                break;
//            case TOAST_WARNING:
//                Toasty.warning(context, message);
//                break;
//            case TOAST_ERROR:
//                Toasty.error(context, message);
//                break;
//        }
//    }

    @SuppressLint("ClickableViewAccessibility")
    public static void onTouchesBegan(AppCompatActivity activity, int resourceView) {
        activity.findViewById(resourceView).setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
            return false;
        });
    }

    /** shakeViewWithAnimation();
     * @param context
     * @param viewToAnimate
     * @param <T>
     */
    public static <T> void shakeViewWithAnimation(Context context, T viewToAnimate) {
        if (viewToAnimate instanceof View) {
            ((View) viewToAnimate).startAnimation(loadAnimation(context, R.anim.shake_animation));
        }
    }

    /** getColorResourceFromPackage();
     * @param context
     * @param colorResource
     * @return
     */
    public static @ColorInt int getColorResourceFromPackage(Context context, int colorResource) {
        return ContextCompat.getColor(context, colorResource);
    }

    public static <T> void parentLayoutStatus(T layoutGroup, boolean enable) {
        if (layoutGroup instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) layoutGroup).getChildCount(); i++) {
                View child = ((ViewGroup) layoutGroup).getChildAt(i);
                child.setEnabled(enable);
                if (child instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) child;
                    for (int j = 0; j < group.getChildCount(); j++) {
                        group.getChildAt(j).setEnabled(enable);
                    }
                }
            }
        }
    }


}
