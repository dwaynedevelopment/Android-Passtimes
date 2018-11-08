package com.dwaynedevelopment.passtimes.base.account.edit.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.base.account.edit.fragments.EditFragment;
import com.dwaynedevelopment.passtimes.base.account.edit.interfaces.IEditHandler;
import com.dwaynedevelopment.passtimes.parent.activities.BaseActivity;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.dwaynedevelopment.passtimes.utils.AlertUtils.invokeSnackBar;
import static com.dwaynedevelopment.passtimes.utils.ImageUtils.getRealFilePathFromURI;
import static com.dwaynedevelopment.passtimes.utils.ImageUtils.getRealPathFromURI;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_USERS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.REQUEST_GALLERY_IMAGE_SELECT;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.REQUEST_READ_EXTERNAL_STORAGE;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ROOT_STORAGE_USER_PROFILES;
import static com.dwaynedevelopment.passtimes.utils.PermissionUtils.permissionReadExternalStorage;

public class EditActivity extends AppCompatActivity implements IEditHandler {

    private Uri userPhotoUri = null;
    private String username = null;
    private AuthUtils mAuth;
    private FirebaseFirestoreUtils mDatabase;
    private StorageReference userFilePath;
    private ProgressBar progress;
    private CircleImageView circleImageView;
    private View view;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mAuth = AuthUtils.getInstance();
        mDatabase = FirebaseFirestoreUtils.getInstance();

        invokeFragment();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_GALLERY_IMAGE_SELECT:
                new Handler().postDelayed(() -> {
                    if (data != null) {
                        invokeSnackBar(EditActivity.this,
                                "Image selected.",
                                getResources().getColor(R.color.colorDarkPrimary),
                                getResources().getColor(R.color.colorSecondaryAccent));

                        userPhotoUri = data.getData();
                        circleImageView = findViewById(R.id.ci_edit_image);
                        view = findViewById(R.id.vw_placeholder_edit);

                        view = findViewById(R.id.vw_placeholder_edit);
                        view.setVisibility(View.GONE);

                        Uri photoUri = getRealFilePathFromURI(getRealPathFromURI(EditActivity.this, Objects.requireNonNull(userPhotoUri)));
                        circleImageView.setVisibility(View.VISIBLE);
                        Glide.with(EditActivity.this).load(photoUri).into(circleImageView);

                    } else {
                        Snackbar snackbar = invokeSnackBar(EditActivity.this,
                                "Image not selected.",
                                getResources().getColor(R.color.colorDarkPrimary),
                                getResources().getColor(R.color.colorPrimaryAccent),
                                getResources().getColor(R.color.colorSecondaryAccent));

                        snackbar.setAction("Retry", v -> {
                            if (permissionReadExternalStorage(EditActivity.this)) {
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(galleryIntent, REQUEST_GALLERY_IMAGE_SELECT);
                            }
                        });
                        snackbar.show();
                    }
                }, 750);
                break;
            default:
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int imagePermission = grantResults[0];
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (imagePermission == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_GALLERY_IMAGE_SELECT);
            }
        }
    }

    private void invokeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_edit, EditFragment.newInstance())
                .commit();
    }

    @Override
    public void invokeCamera() {
        if (permissionReadExternalStorage(this)) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_GALLERY_IMAGE_SELECT);
        }
    }

    @Override
    public void submitEditChanges(String displayName) {
        username = displayName;
        progress = findViewById(R.id.pb_edit_bar);
        progress.setVisibility(View.VISIBLE);
        if (userPhotoUri != null) {
            userFilePath = mAuth.getStorage()
                    .child(ROOT_STORAGE_USER_PROFILES)
                    .child(mAuth.getCurrentSignedUser().getId())
                    .child("profile_image_" + mAuth.getCurrentSignedUser().getId());

            userFilePath.putFile(userPhotoUri)
                    .continueWithTask(uploadImageListener)
                    .addOnCompleteListener(uploadedWithCredentials)
                    .addOnSuccessListener(completedSignUpListener);
        } else {
            UserProfileChangeRequest updateProfile;
            if (!username.isEmpty()) {
                updateProfile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .setPhotoUri(Uri.parse(mAuth.getCurrentSignedUser().getThumbnail()))
                        .build();
            } else {
                updateProfile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(mAuth.getCurrentSignedUser().getName())
                        .setPhotoUri(Uri.parse(mAuth.getCurrentSignedUser().getThumbnail()))
                        .build();
            }

            Objects.requireNonNull(mAuth.getFireAuth().getCurrentUser()).updateProfile(updateProfile)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            mDatabase.getFirestore().collection(DATABASE_REFERENCE_USERS)
                                    .document(mAuth.getCurrentSignedUser().getId())
                                    .update("name", username,
                                            "thumbnail", mAuth.getCurrentSignedUser().getThumbnail());
                            new Handler().postDelayed(() -> {
                                progress.setVisibility(View.GONE);
                                finish();
                                Intent intent = new Intent(EditActivity.this, BaseActivity.class);
                                startActivity(intent);
                            }, 250);

                        }
                    });
        }
    }

    private final Continuation<UploadTask.TaskSnapshot, Task<Uri>> uploadImageListener = new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
        @Override
        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
            return userFilePath.getDownloadUrl();
        }
    };


    private final OnCompleteListener<Uri> uploadedWithCredentials = new OnCompleteListener<Uri>() {
        @Override
        public void onComplete(@NonNull Task<Uri> task) {
            UserProfileChangeRequest updateProfile;
            if (!username.isEmpty()) {
                 updateProfile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .setPhotoUri(task.getResult())
                        .build();
            } else {
                 updateProfile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(mAuth.getCurrentSignedUser().getName())
                        .setPhotoUri(task.getResult())
                        .build();
            }

            Objects.requireNonNull(mAuth.getFireAuth().getCurrentUser()).updateProfile(updateProfile);
        }
    };

    private final OnSuccessListener<Uri> completedSignUpListener = new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {
            new Handler().postDelayed(() -> mDatabase.getFirestore().collection(DATABASE_REFERENCE_USERS)
                    .document(mAuth.getCurrentSignedUser().getId())
                    .update("name", username,
                            "thumbnail", mAuth.getCurrentSignedUser().getThumbnail()), 250);
            new Handler().postDelayed(() -> {
                progress.setVisibility(View.GONE);
                finish();
                Intent intent = new Intent(EditActivity.this, BaseActivity.class);
                startActivity(intent);
            }, 1000);
        }
    };


    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(EditActivity.this, BaseActivity.class);
        startActivity(intent);
    }
}
