package com.dwaynedevelopment.passtimes.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.navigation.activities.BaseActivity;

public class FavoriteActivity extends AppCompatActivity implements IFavoriteHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        invokeFragment();
    }

    private void invokeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_favorite, FavoriteFragment.newInstance())
                .commit();
    }

    @Override
    public void invokeBaseActivity() {
        finish();
        Intent intent = new Intent(FavoriteActivity.this, BaseActivity.class);
        startActivity(intent);
    }
}
