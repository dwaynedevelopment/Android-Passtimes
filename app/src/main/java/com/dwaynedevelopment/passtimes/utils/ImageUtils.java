package com.dwaynedevelopment.passtimes.utils;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;

public class ImageUtils {

    public static Uri getRealPathFromURI(AppCompatActivity activity, Uri contentURI) {
        String result;
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
//        Toasty.error(this, result, Toast.LENGTH_SHORT).show();
        return Uri.parse(result);
    }


    public static Uri getRealFilePathFromURI(Uri uriStringPath) {
        return Uri.fromFile(new File(uriStringPath.getPath()));
    }

}
