package com.dwaynedevelopment.passtimes.account.signup.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.favorites.activities.FavoriteActivity;
import com.dwaynedevelopment.passtimes.account.terms.activities.TermsActivity;
import com.dwaynedevelopment.passtimes.account.login.activities.LoginActivity;
import com.dwaynedevelopment.passtimes.account.signup.interfaces.ISignUpHandler;
import com.dwaynedevelopment.passtimes.account.signup.fragments.SignUpFragment;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
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
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_USERS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.EXTRA_REGISTRATION;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.REQUEST_GALLERY_IMAGE_SELECT;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.REQUEST_READ_EXTERNAL_STORAGE;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ROOT_STORAGE_USER_PROFILES;
import static com.dwaynedevelopment.passtimes.utils.PermissionUtils.permissionReadExternalStorage;
import static com.dwaynedevelopment.passtimes.utils.ViewUtils.parentLayoutStatus;
import static com.dwaynedevelopment.passtimes.utils.ViewUtils.shakeViewWithAnimation;

public class SignUpActivity extends AppCompatActivity implements ISignUpHandler {

    private Uri userPhotoUri = null;
    private String username = null;
    private AuthUtils mAuth;
    private FirebaseFirestoreUtils mDatabase;
    private StorageReference userFilePath;

    private RelativeLayout signUpParentLayout;
    private ProgressBar progress;
    private View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = AuthUtils.getInstance();
        mDatabase = FirebaseFirestoreUtils.getInstance();
        invokeFragment();
    }

    private void invokeFragment() {
        signUpParentLayout = findViewById(R.id.rl_signup_parent);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_signup, SignUpFragment.newInstance())
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        view = findViewById(R.id.vw_placeholder);
        switch (requestCode) {
            case REQUEST_GALLERY_IMAGE_SELECT:
                if (data != null) {
                    view.setVisibility(View.GONE);
                    signUpParentLayout = findViewById(R.id.rl_signup_parent);

                    userPhotoUri = data.getData();
                    final CircleImageView circleImageView = findViewById(R.id.ci_signup);
                    Uri photoUri = getRealFilePathFromURI(getRealPathFromURI(SignUpActivity.this, Objects.requireNonNull(userPhotoUri)));
                    circleImageView.setVisibility(View.VISIBLE);

                    Glide.with(SignUpActivity.this).load(photoUri).into(circleImageView);
                } else {
                    Toast.makeText(this, "No Image Has Been Selected.", Toast.LENGTH_SHORT).show();
                    shakeViewWithAnimation(this, view);
                }
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
            this.runOnUiThread(() -> {
                progress = findViewById(R.id.pb_dots);
                progress.setVisibility(View.VISIBLE);
            });
            parentLayoutStatus(signUpParentLayout, false);
            username = name;
            mAuth.getFireAuth().createUserWithEmailAndPassword(email, password)
                    .continueWithTask(signUpWithTaskListener)
                    .addOnSuccessListener(this, onSuccessListener)
                    .addOnFailureListener(this, onFailureListener);
        } else {
            view = findViewById(R.id.vw_placeholder);
            Toast.makeText(this, "No Image Has Been Selected.", Toast.LENGTH_SHORT).show();
            shakeViewWithAnimation(this, view);
            parentLayoutStatus(signUpParentLayout, true);
        }
    }

    private final Continuation<AuthResult, Task<Void>> signUpWithTaskListener = task -> {
        UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        return Objects.requireNonNull(task.getResult()).getUser().updateProfile(updateProfile);
    };

    private final OnSuccessListener<Void> onSuccessListener = new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            if (userPhotoUri != null) {
                userFilePath = mAuth.getStorage()
                        .child(ROOT_STORAGE_USER_PROFILES)
                        .child(mAuth.getCurrentSignedUser().getId())
                        .child("profile_image_" + mAuth.getCurrentSignedUser().getId());

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


    private final OnCompleteListener<Uri> uploadedWithCredentials = new OnCompleteListener<Uri>() {
        @Override
        public void onComplete(@NonNull Task<Uri> task) {
            UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(task.getResult())
                    .build();
            Objects.requireNonNull(mAuth.getFireAuth().getCurrentUser()).updateProfile(updateProfile);
        }
    };

    private final OnSuccessListener<Uri> completedSignUpListener = new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {
            new Handler().postDelayed(() -> {
                mDatabase.insertDocument(DATABASE_REFERENCE_USERS, mAuth.getCurrentSignedUser().getId(), mAuth.getCurrentSignedUser());
                SignUpActivity.this.runOnUiThread(() -> progress.setVisibility(View.GONE));
            }, 250);


            new Handler().postDelayed(() -> {
                SignUpActivity.this.runOnUiThread(() -> {
                    mDatabase.updateImage(mAuth.getCurrentSignedUser());
                    finish();
                    Intent intent = new Intent(SignUpActivity.this, FavoriteActivity.class);
                    intent.putExtra(EXTRA_REGISTRATION, true);
                    startActivity(intent);
                });
            }, 550);
        }
    };

    private final OnFailureListener onFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            parentLayoutStatus(signUpParentLayout, true);
            runOnUiThread(() -> {
                progress.setVisibility(View.GONE);
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
            parentLayoutStatus(signUpParentLayout, true);
        }
    };
}
