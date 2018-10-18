package com.dwaynedevelopment.passtimes.account.login.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.account.FavoriteActivity;
import com.dwaynedevelopment.passtimes.account.login.fragments.LoginFragment;
import com.dwaynedevelopment.passtimes.account.signup.activities.SignUpActivity;
import com.dwaynedevelopment.passtimes.account.login.interfaces.ILoginHandler;
import com.dwaynedevelopment.passtimes.navigation.activities.BaseActivity;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.eyalbira.loadingdots.LoadingDots;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import static com.dwaynedevelopment.passtimes.utils.AuthUtils.showTaskException;

public class LoginActivity extends AppCompatActivity implements ILoginHandler {

    private AuthUtils mAuth;
    private LoadingDots progress;

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
        progress.startAnimation();
    }

    private final OnCompleteListener<AuthResult> authenticateEPSignInListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                try {
                    Thread.sleep(1000);
                    progress.stopAnimation();
                    progress.setVisibility(View.GONE);

                    finish();
                    Intent intent = new Intent(LoginActivity.this, FavoriteActivity.class);
                    startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else {
                // If sign in fails, display a message to the user.
                showTaskException(LoginActivity.this, task);
                progress.stopAnimation();
                progress.setVisibility(View.GONE);
            }
        }
    };
}
