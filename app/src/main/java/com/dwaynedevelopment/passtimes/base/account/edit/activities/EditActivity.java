package com.dwaynedevelopment.passtimes.base.account.edit.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.base.account.edit.fragments.EditFragment;
import com.dwaynedevelopment.passtimes.base.account.edit.interfaces.IEditHandler;
import com.dwaynedevelopment.passtimes.base.account.signup.activities.SignUpActivity;
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

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.dwaynedevelopment.passtimes.utils.AlertUtils.invokeSnackBar;
import static com.dwaynedevelopment.passtimes.utils.ImageUtils.getFileFromImageCaptured;
import static com.dwaynedevelopment.passtimes.utils.ImageUtils.getRealFilePathFromURI;
import static com.dwaynedevelopment.passtimes.utils.ImageUtils.getRealPathFromURI;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.APPLICATION_PACKAGE_AUTHORITY;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_USERS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.IMAGE_EXTRA_OUTPUT_DATA;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.REQUEST_CAMERA_IMAGE_CAPTURE;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.REQUEST_GALLERY_IMAGE_SELECT;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.REQUEST_READ_EXTERNAL_STORAGE;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ROOT_STORAGE_USER_PROFILES;
import static com.dwaynedevelopment.passtimes.utils.PermissionUtils.permissionReadExternalStorage;
import static com.dwaynedevelopment.passtimes.utils.ViewUtils.onTouchesBegan;
import static com.dwaynedevelopment.passtimes.utils.ViewUtils.parentLayoutStatus;
import static com.dwaynedevelopment.passtimes.utils.ViewUtils.shakeViewWithAnimation;

public class EditActivity extends AppCompatActivity implements IEditHandler {

    private RelativeLayout editParentLayout;
    private String imageFilePath;
    private Uri userPhotoUri = null;
    private String username = null;
    private AuthUtils mAuth;
    private FirebaseFirestoreUtils mDatabase;
    private StorageReference userFilePath;
    private ProgressBar progress;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mAuth = AuthUtils.getInstance();
        mDatabase = FirebaseFirestoreUtils.getInstance();
        invokeFragment();
    }


    @Override
    protected void onStart() {
        super.onStart();
        onTouchesBegan(this, R.id.ac_edit);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        View view = findViewById(R.id.vw_placeholder_edit);
        switch (requestCode) {
            case REQUEST_GALLERY_IMAGE_SELECT:
                if (data != null) {
                    view.setVisibility(View.GONE);
                    editParentLayout = findViewById(R.id.rl_edit_parent);

                    userPhotoUri = data.getData();
                    final CircleImageView circleImageView = findViewById(R.id.ci_edit_image);
                    Uri photoUri = getRealFilePathFromURI(getRealPathFromURI(EditActivity.this, Objects.requireNonNull(userPhotoUri)));
                    circleImageView.setVisibility(View.VISIBLE);

                    Glide.with(EditActivity.this).load(photoUri).into(circleImageView);
                } else {
                    Toast.makeText(this, "No Image Has Been Selected.", Toast.LENGTH_SHORT).show();
                    shakeViewWithAnimation(this, view);
                }
                break;
            case REQUEST_CAMERA_IMAGE_CAPTURE:
                view.setVisibility(View.GONE);
               //signUpParentLayout = findViewById(R.id.rl_signup_parent);

                userPhotoUri = getRealFilePathFromURI(Uri.parse(imageFilePath));
                final CircleImageView circleImageView = findViewById(R.id.ci_edit_image);
                circleImageView.setVisibility(View.VISIBLE);

                Glide.with(EditActivity.this).load(userPhotoUri).into(circleImageView);
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
                invokeGalleryOrCameraIntent();
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
            invokeGalleryOrCameraIntent();
        }
    }

    @Override
    public void submitEditChanges(String displayName) {
        editParentLayout = findViewById(R.id.rl_edit_parent);
        parentLayoutStatus(editParentLayout, false);
        username = displayName;
        progress = findViewById(R.id.pb_edit_bar);
        progress.setVisibility(View.VISIBLE);
        if (userPhotoUri != null) {
            parentLayoutStatus(editParentLayout, false);
            userFilePath = mAuth.getStorage()
                    .child(ROOT_STORAGE_USER_PROFILES)
                    .child(mAuth.getCurrentSignedUser().getId())
                    .child("profile_image_" + mAuth.getCurrentSignedUser().getId());

            userFilePath.putFile(userPhotoUri)
                    .continueWithTask(uploadImageListener)
                    .addOnCompleteListener(uploadedWithCredentials)
                    .addOnSuccessListener(completedSignUpListener);
        } else {
            parentLayoutStatus(editParentLayout, false);
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
                            "thumbnail", uri.toString()),
                    1000);
//            mDatabase.updateImage(mAuth.getCurrentSignedUser());
            new Handler().postDelayed(() -> {
                progress.setVisibility(View.GONE);
                finish();
                Intent intent = new Intent(EditActivity.this, BaseActivity.class);
                startActivity(intent);
            }, 250);
        }
    };

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(EditActivity.this, BaseActivity.class);
        startActivity(intent);
    }

    private void invokeGalleryOrCameraIntent() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery", (arg0, arg1) -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_GALLERY_IMAGE_SELECT);
        });

        myAlertDialog.setNegativeButton("Camera", (arg0, arg1) -> {
            Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(pictureIntent.resolveActivity(getPackageManager()) != null){
                //Create a file to store the image
                File photoFile = null;
                try {
                    photoFile = getFileFromImageCaptured(EditActivity.this, mAuth.getCurrentSignedUser());
                    imageFilePath = photoFile.getAbsolutePath();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(EditActivity.this, APPLICATION_PACKAGE_AUTHORITY, photoFile);
                    pictureIntent.putExtra(IMAGE_EXTRA_OUTPUT_DATA, photoURI);
                    startActivityForResult(pictureIntent, REQUEST_CAMERA_IMAGE_CAPTURE);
                }
            }
        });
        myAlertDialog.show();
    }
}
