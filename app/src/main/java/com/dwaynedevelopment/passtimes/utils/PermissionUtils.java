package com.dwaynedevelopment.passtimes.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.REQUEST_READ_EXTERNAL_STORAGE;

public class PermissionUtils {

    public static boolean permissionReadExternalStorage(AppCompatActivity appCompatActivity) {
        if(ContextCompat.checkSelfPermission(appCompatActivity.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                appCompatActivity.requestPermissions(
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READ_EXTERNAL_STORAGE);
            }
            return false;
        }
        return true;
    }
}
