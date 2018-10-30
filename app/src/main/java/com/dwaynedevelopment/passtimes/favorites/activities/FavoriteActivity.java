package com.dwaynedevelopment.passtimes.favorites.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.favorites.fragments.FavoriteFragment;
import com.dwaynedevelopment.passtimes.favorites.interfaces.IFavoriteHandler;
import com.dwaynedevelopment.passtimes.navigation.activities.BaseActivity;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.EXTRA_REGISTRATION;

public class FavoriteActivity extends AppCompatActivity implements IFavoriteHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        invokeFragment();
    }

    private void invokeFragment() {

        if (getIntent() != null) {
            final boolean editFavorites = getIntent().getBooleanExtra("EXTRA_EDIT_FAVORITES", false);
            if (editFavorites) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_favorite, FavoriteFragment.newInstance(true))
                        .commit();
                return;
            }
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_favorite, FavoriteFragment.newInstance(false))
                .commit();
    }

    @Override
    public void dismissActivity() {
        if (getIntent() != null) {
            if (getIntent().hasExtra(EXTRA_REGISTRATION) || getIntent().hasExtra("EXTRA_EDIT_FAVORITES")) {
                finish();
                Intent intent = new Intent(FavoriteActivity.this, BaseActivity.class);
                startActivity(intent);
            }
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (getIntent() != null) {
            if (getIntent().hasExtra(EXTRA_REGISTRATION) || getIntent().hasExtra("EXTRA_EDIT_FAVORITES")) {
                finish();
                Intent intent = new Intent(FavoriteActivity.this, BaseActivity.class);
                startActivity(intent);
            }
        } else {
            super.onBackPressed();
        }
    }
}
