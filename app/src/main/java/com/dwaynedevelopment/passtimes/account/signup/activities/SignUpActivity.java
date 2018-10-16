package com.dwaynedevelopment.passtimes.account.signup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.account.terms.activities.TermsActivity;
import com.dwaynedevelopment.passtimes.account.login.activities.LoginActivity;
import com.dwaynedevelopment.passtimes.account.signup.interfaces.ISignUpHandler;
import com.dwaynedevelopment.passtimes.account.signup.fragments.SignUpFragment;

public class SignUpActivity extends AppCompatActivity implements ISignUpHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        invokeFragment();
    }

    private void invokeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_signup, SignUpFragment.newInstance())
                .commit();
    }

    @Override
    public void invokeLogin() {
        Intent intent = new Intent(this, LoginActivity.class) ;
        startActivity(intent);
    }

    @Override
    public void invokeTerms() {
        Intent intent = new Intent(this, TermsActivity.class) ;
        startActivity(intent);
    }
}
