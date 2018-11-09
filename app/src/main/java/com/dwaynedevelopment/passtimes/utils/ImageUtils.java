package com.dwaynedevelopment.passtimes.utils;

import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import com.dwaynedevelopment.passtimes.models.Player;

import java.io.File;
import java.io.IOException;


public class ImageUtils {


    public static File getFileFromImageCaptured(AppCompatActivity activity, Player player) throws IOException {
        String imageFileName = "profile_image_" + player.getId();
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir     /* directory */
        );
    }

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
        return Uri.parse(result);
    }

    public static Uri getRealFilePathFromURI(Uri uriStringPath) {
        return Uri.fromFile(new File(uriStringPath.getPath()));
    }

}
