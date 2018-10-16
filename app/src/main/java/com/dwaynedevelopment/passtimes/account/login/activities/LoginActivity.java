package com.dwaynedevelopment.passtimes.account.login.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.account.login.fragments.LoginFragment;
import com.dwaynedevelopment.passtimes.account.signup.activities.SignUpActivity;
import com.dwaynedevelopment.passtimes.account.login.interfaces.ILoginHandler;

public class LoginActivity extends AppCompatActivity implements ILoginHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
}
