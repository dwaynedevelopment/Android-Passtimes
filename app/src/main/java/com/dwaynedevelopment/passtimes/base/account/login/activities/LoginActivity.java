package com.dwaynedevelopment.passtimes.base.account.login.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.base.account.login.fragments.LoginFragment;
import com.dwaynedevelopment.passtimes.base.account.signup.activities.SignUpActivity;
import com.dwaynedevelopment.passtimes.base.account.login.interfaces.ILoginHandler;
import com.dwaynedevelopment.passtimes.parent.activities.BaseActivity;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.Objects;

import static com.dwaynedevelopment.passtimes.utils.ViewUtils.parentLayoutStatus;

public class LoginActivity extends AppCompatActivity implements ILoginHandler {

    private AuthUtils mAuth;
    private ProgressBar progress;

    private RelativeLayout loginParentLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = AuthUtils.getInstance();
        invokeFragment();
    }

    private void invokeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_login, LoginFragment.newInstance())
                .commit();
    }

    @Override
    public void invokeSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void authenticateSignInWithEmail(String email, String password) {
        loginParentLayout = findViewById(R.id.rl_login_parent);
        if (email != null && password != null) {
            parentLayoutStatus(loginParentLayout, false);
            this.runOnUiThread(() -> {
                progress = findViewById(R.id.pb_dot);
                progress.setVisibility(View.VISIBLE);
            });

            mAuth.getFireAuth().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(authenticateEPSignInListener)
                    .addOnFailureListener(authenticateFailureListener);
        } else {
            parentLayoutStatus(loginParentLayout, true);
        }
    }

    private final OnCompleteListener<AuthResult> authenticateEPSignInListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                new Handler().postDelayed(() ->
                        LoginActivity.this.runOnUiThread(() ->
                                progress.setVisibility(View.GONE)), 150);

                new Handler().postDelayed(() ->
                        LoginActivity.this.runOnUiThread(() -> {
                            finish();
                            Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
                            startActivity(intent);
                }), 250);
            }
        }
    };

    private final OnFailureListener authenticateFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            // If sign in fails, display a message to the user.
            //showTaskException(LoginActivity.this, task);
            LoginActivity.this.runOnUiThread(() ->
                    progress.setVisibility(View.GONE));

            Toast.makeText(LoginActivity.this, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show();
            loginParentLayout = findViewById(R.id.rl_login_parent);
            parentLayoutStatus(loginParentLayout, true);
        }
    };
}
