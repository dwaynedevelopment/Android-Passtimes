package com.dwaynedevelopment.passtimes.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dwaynedevelopment.passtimes.R;

public class AlertUtils {

    public static void invokeSnackBar(AppCompatActivity activity, String message, @ColorInt int background, @ColorInt int messageColor) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        View snackView = snackbar.getView();
        TextView snackViewText = snackView.findViewById(android.support.design.R.id.snackbar_text);
        snackView.setBackgroundColor(background);
        snackViewText.setTextColor(messageColor);
        snackbar.show();
    }

    public static Snackbar invokeSnackBar(AppCompatActivity activity, String message, @ColorInt int background, @ColorInt int messageColor, @ColorInt int primaryActionColor) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        View snackView = snackbar.getView();
        TextView snackViewText = snackView.findViewById(android.support.design.R.id.snackbar_text);
        Button snackViewButton = snackView.findViewById(android.support.design.R.id.snackbar_action);
        snackView.setBackgroundColor(background);
        snackViewText.setTextColor(messageColor);
        snackViewButton.setTextColor(primaryActionColor);
        snackViewButton.setAllCaps(false);

        return snackbar;
    }
}
