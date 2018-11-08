package com.dwaynedevelopment.passtimes.base.account.terms.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.base.account.terms.fragments.TermsFragment;
import com.dwaynedevelopment.passtimes.base.account.terms.interfaces.ITermsHandler;

public class TermsActivity extends AppCompatActivity implements ITermsHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        invokeFragment();
    }

    private void invokeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_terms, TermsFragment.newInstance())
                .commit();
    }

    @Override
    public void dismissTerms() {
        finish();
    }
}
