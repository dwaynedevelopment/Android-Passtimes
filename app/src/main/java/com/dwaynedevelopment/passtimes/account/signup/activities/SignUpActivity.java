package com.dwaynedevelopment.passtimes.account.signup.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.favorites.activities.FavoriteActivity;
import com.dwaynedevelopment.passtimes.account.terms.activities.TermsActivity;
import com.dwaynedevelopment.passtimes.account.login.activities.LoginActivity;
import com.dwaynedevelopment.passtimes.account.signup.interfaces.ISignUpHandler;
import com.dwaynedevelopment.passtimes.account.signup.fragments.SignUpFragment;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.DatabaseUtils;
import com.eyalbira.loadingdots.LoadingDots;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.dwaynedevelopment.passtimes.utils.ImageUtils.getRealFilePathFromURI;
import static com.dwaynedevelopment.passtimes.utils.ImageUtils.getRealPathFromURI;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.EXTRA_REGISTRATION;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.REQUEST_GALLERY_IMAGE_SELECT;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.REQUEST_READ_EXTERNAL_STORAGE;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ROOT_STORAGE_USER_PROFILES;
import static com.dwaynedevelopment.passtimes.utils.PermissionUtils.permissionReadExternalStorage;
import static com.dwaynedevelopment.passtimes.utils.SnackbarUtils.invokeSnackBar;

public class SignUpActivity extends AppCompatActivity implements ISignUpHandler {

    private Uri userPhotoUri = null;
    private String username = null;
    private AuthUtils mAuth;
    private StorageReference userFilePath;
    private LoadingDots progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        invokeFragment();
        mAuth = AuthUtils.getInstance();
    }

    private void invokeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_signup, SignUpFragment.newInstance())
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_GALLERY_IMAGE_SELECT:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (data != null) {
                            invokeSnackBar(SignUpActivity.this,
                                    "Image selected.",
                                    getResources().getColor(R.color.colorDarkPrimary),
                                    getResources().getColor(R.color.colorSecondaryAccent));

                            userPhotoUri = data.getData();
                            final View view = findViewById(R.id.vw_placeholder);
                            view.setVisibility(View.GONE);

                            final CircleImageView circleImageView = findViewById(R.id.ci_signup);
                            Uri photoUri = getRealFilePathFromURI(getRealPathFromURI(SignUpActivity.this, Objects.requireNonNull(userPhotoUri)));
                            circleImageView.setVisibility(View.VISIBLE);
                            Glide.with(SignUpActivity.this).load(photoUri).into(circleImageView);

                        } else {
                            Snackbar snackbar = invokeSnackBar(SignUpActivity.this,
                                    "Image not selected.",
                                    getResources().getColor(R.color.colorDarkPrimary),
                                    getResources().getColor(R.color.colorPrimaryAccent),
                                    getResources().getColor(R.color.colorSecondaryAccent));

                            snackbar.setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (permissionReadExternalStorage(SignUpActivity.this)) {
                                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(galleryIntent, REQUEST_GALLERY_IMAGE_SELECT);
                                    }
                                }
                            });
                            snackbar.show();
                        }
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

    @Override
    public void invokeLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void invokeTerms() {
        Intent intent = new Intent(this, TermsActivity.class);
        startActivity(intent);
    }

    @Override
    public void invokeGallery() {
        if (permissionReadExternalStorage(this)) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_GALLERY_IMAGE_SELECT);
        }
    }

    @Override
    public void authenticateSignUpWithEmail(String email, String password, String name) {
        if (userPhotoUri != null) {
            progress = findViewById(R.id.pb_dots);
            progress.setVisibility(View.VISIBLE);
            progress.startAnimation();
            username = name;
            mAuth.getFireAuth().createUserWithEmailAndPassword(email, password)
                    .continueWithTask(signUpWithTaskListener)
                    .addOnSuccessListener(this, onSuccessListener)
                    .addOnFailureListener(this, onFailureListener);
        } else {
            Snackbar snackbar = invokeSnackBar(SignUpActivity.this,
                    "Image not selected.",
                    getResources().getColor(R.color.colorDarkPrimary),
                    getResources().getColor(R.color.colorPrimaryAccent),
                    getResources().getColor(R.color.colorSecondaryAccent));

            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (permissionReadExternalStorage(SignUpActivity.this)) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, REQUEST_GALLERY_IMAGE_SELECT);
                    }
                }
            });
            snackbar.show();
        }
    }

    private final Continuation<AuthResult, Task<Void>> signUpWithTaskListener = new Continuation<AuthResult, Task<Void>>() {
        @Override
        public Task<Void> then(@NonNull Task<AuthResult> task) {

            UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();
            return Objects.requireNonNull(task.getResult()).getUser().updateProfile(updateProfile);
        }
    };

    private final OnSuccessListener<Void> onSuccessListener = new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            if (userPhotoUri != null) {
                userFilePath = mAuth.getStorage()
                        .child(ROOT_STORAGE_USER_PROFILES)
                        .child(mAuth.getCurrentSignedUser().getId())
                        .child(Objects.requireNonNull(userPhotoUri.getLastPathSegment()));

                userFilePath.putFile(userPhotoUri)
                        .continueWithTask(uploadImageListener)
                        .addOnCompleteListener(uploadedWithCredentials)
                        .addOnSuccessListener(completedSignUpListener);
            }
        }
    };

    private final Continuation<UploadTask.TaskSnapshot, Task<Uri>> uploadImageListener = new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
        @Override
        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
            return userFilePath.getDownloadUrl();
        }
    };

    private static final String TAG = "SignUpActivity";

    private final OnCompleteListener<Uri> uploadedWithCredentials = new OnCompleteListener<Uri>() {
        @Override
        public void onComplete(@NonNull Task<Uri> task) {
            UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(task.getResult())
                    .build();
            Log.i(TAG, "onComplete: " + task.getResult());
            Objects.requireNonNull(mAuth.getFireAuth().getCurrentUser()).updateProfile(updateProfile);
        }
    };

    private final OnSuccessListener<Uri> completedSignUpListener = new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress.stopAnimation();
                    progress.setVisibility(View.GONE);
                    DatabaseUtils database = DatabaseUtils.getInstance();
                    database.insertUser(mAuth.getCurrentSignedUser());
                }
            }, 250);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    DatabaseUtils.getInstance().updateImage(mAuth.getCurrentSignedUser());
                    Intent intent = new Intent(SignUpActivity.this, FavoriteActivity.class);
                    intent.putExtra(EXTRA_REGISTRATION, true);
                    startActivity(intent);
                }
            }, 1500);
        }
    };

    private final OnFailureListener onFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            progress.stopAnimation();
            progress.setVisibility(View.GONE);
            invokeSnackBar(SignUpActivity.this,
                    e.getMessage(),
                    getResources().getColor(R.color.colorDarkPrimary),
                    getResources().getColor(R.color.colorPrimaryAccent));
        }
    };
}
