package com.dwaynedevelopment.passtimes.account.login.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.account.login.fragments.LoginFragment;
import com.dwaynedevelopment.passtimes.account.signup.activities.SignUpActivity;
import com.dwaynedevelopment.passtimes.account.login.interfaces.ILoginHandler;
import com.dwaynedevelopment.passtimes.navigation.activities.BaseActivity;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import static com.dwaynedevelopment.passtimes.utils.AuthUtils.showTaskException;

public class LoginActivity extends AppCompatActivity implements ILoginHandler {

    private AuthUtils mAuth;
    private ProgressBar progress;

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
        Intent intent = new Intent(this, SignUpActivity.class) ;
        startActivity(intent);
    }

    @Override
    public void authenticateSignInWithEmail(String email, String password) {
        mAuth.getFireAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(authenticateEPSignInListener);
        progress = findViewById(R.id.pb_dot);
        progress.setVisibility(View.VISIBLE);
    }

    private final OnCompleteListener<AuthResult> authenticateEPSignInListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                    }
                }, 250);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
                        startActivity(intent);
                    }
                }, 1000);
            } else {
                // If sign in fails, display a message to the user.
                showTaskException(LoginActivity.this, task);
                progress.setVisibility(View.GONE);
            }
        }
    };
}
