package com.dwaynedevelopment.passtimes.utils;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;

import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.SOCIAL_PROFILE_DIMEN;
import static com.dwaynedevelopment.passtimes.utils.AlertUtils.invokeSnackBar;

public class AuthUtils {

    private static FirebaseAuth mFireAuth;
    private final StorageReference mStorage;

    //CONSTRUCTOR: private constructor will make sure that the singleton manages the object and does not need further instantiation.
    private AuthUtils() {
        mFireAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    //SINGLETON: This is the single instances of the helper class and will be the manager of the database.
    private static AuthUtils instance = null;

    public static AuthUtils getInstance() {
        if (instance == null) {
            instance = new AuthUtils();
        }
        return instance;
    }

    public FirebaseAuth getFireAuth() {
        return mFireAuth;
    }

    public StorageReference getStorage() { return mStorage; }

    public boolean isCurrentUserAuthenticated() {
        return mFireAuth.getCurrentUser() != null;
    }

//    public CallbackManager getFBCallbackManager() {
//        return CallbackManager.Factory.create();
//    }
//
//    public AuthCredential authenticateWithFB(AccessToken token) {
//        return getCredential(token.getToken());
//    }
//
//    public AuthCredential authenticateWithGP(GoogleSignInAccount acct) {
//        return GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//    }
//
//    public GoogleSignInOptions googleSignInOptions(Context context) {
//        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(context.getString(R.string.default_web_client_id))
//                .requestEmail()
//                .requestProfile()
//                .build();
//    }

    private String getUserProfileImage() {
        if (mFireAuth != null) {
            if (mFireAuth.getCurrentUser() != null) {
                Uri pictureUri = mFireAuth.getCurrentUser().getPhotoUrl();
                if (pictureUri != null) {
                    String thumbnail = pictureUri.toString();
                    if (thumbnail.contains("https")) {
                        if (!thumbnail.contains("https://firebasestorage.googleapis.com")) {
                            thumbnail += "?height=" + SOCIAL_PROFILE_DIMEN;
                        }
                    }
                    return thumbnail;
                }
            }
        }
        return "Current Size Not Available.";
    }

    public Player getCurrentSignedUser() {
        String id = null;
        String name = null;
        String thumbnail = null;

        if (mFireAuth != null) {
            if (mFireAuth.getCurrentUser() != null) {
                id = mFireAuth.getCurrentUser().getUid();
                name = mFireAuth.getCurrentUser().getDisplayName();
                if (!getUserProfileImage().isEmpty()) {
                    thumbnail = getUserProfileImage();
                } else {
                    thumbnail = "uri";
                }
            }
        }

        if (id != null && name != null && thumbnail != null) {
            return new Player(id, name, thumbnail);
        } else  {
            return new Player("_id", "default", "uri");
        }
    }

    public void signOutFromHostAndSocial() {
        if(isCurrentUserAuthenticated()) {
            mFireAuth.signOut();
//            LoginManager.getInstance().logOut();
        }
    }



    public static void showTaskException(Context context, Task<AuthResult> task) {

        if (task != null) {
            String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
            switch (errorCode) {

                case "ERROR_INVALID_EMAIL":
                    invokeSnackBar((AppCompatActivity) Objects.requireNonNull(context),
                            "The email address is badly formatted.",
                            context.getResources().getColor(R.color.colorDarkPrimary),
                            context.getResources().getColor(R.color.colorPrimaryAccent));
                    break;

                case "ERROR_WRONG_PASSWORD":
                    invokeSnackBar((AppCompatActivity) Objects.requireNonNull(context),
                            "The password is invalid.",
                            context.getResources().getColor(R.color.colorDarkPrimary),
                            context.getResources().getColor(R.color.colorPrimaryAccent));
                    break;

                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                    invokeSnackBar((AppCompatActivity) Objects.requireNonNull(context),
                            "An account already exists with the same email address",
                            context.getResources().getColor(R.color.colorDarkPrimary),
                            context.getResources().getColor(R.color.colorPrimaryAccent));
                    break;

                case "ERROR_EMAIL_ALREADY_IN_USE":
                    invokeSnackBar((AppCompatActivity) Objects.requireNonNull(context),
                            "The email address is already in use by another account.",
                            context.getResources().getColor(R.color.colorDarkPrimary),
                            context.getResources().getColor(R.color.colorPrimaryAccent));
                    break;

                case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                    invokeSnackBar((AppCompatActivity) Objects.requireNonNull(context),
                            "This credential is already associated with a different user account.",
                            context.getResources().getColor(R.color.colorDarkPrimary),
                            context.getResources().getColor(R.color.colorPrimaryAccent));
                    break;

                case "ERROR_USER_DISABLED":
                    invokeSnackBar((AppCompatActivity) Objects.requireNonNull(context),
                            "The user account has been disabled",
                            context.getResources().getColor(R.color.colorDarkPrimary),
                            context.getResources().getColor(R.color.colorPrimaryAccent));
                    break;

                case "ERROR_USER_NOT_FOUND":
                    invokeSnackBar((AppCompatActivity) Objects.requireNonNull(context),
                            "This user does not exist.",
                            context.getResources().getColor(R.color.colorDarkPrimary),
                            context.getResources().getColor(R.color.colorPrimaryAccent));
                    break;

                case "ERROR_WEAK_PASSWORD":
                    invokeSnackBar((AppCompatActivity) Objects.requireNonNull(context),
                            "The given password is invalid.",
                            context.getResources().getColor(R.color.colorDarkPrimary),
                            context.getResources().getColor(R.color.colorPrimaryAccent));
                    break;

            }
        }

    }
}

