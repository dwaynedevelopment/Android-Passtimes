package com.dwaynedevelopment.passtimes.base.favorites.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.base.account.signup.activities.SignUpActivity;
import com.dwaynedevelopment.passtimes.base.favorites.fragments.FavoriteFragment;
import com.dwaynedevelopment.passtimes.base.favorites.interfaces.IFavoriteHandler;
import com.dwaynedevelopment.passtimes.parent.activities.BaseActivity;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.EXTRA_REGISTRATION;
import static com.dwaynedevelopment.passtimes.utils.ViewUtils.parentLayoutStatus;

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
        Intent intent = new Intent(this, BaseActivity.class);// New activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.overridePendingTransition(0, 0);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (getIntent() != null) {
            final boolean editFavorites = getIntent().getBooleanExtra("EXTRA_EDIT_FAVORITES", false);
            final boolean registration =  getIntent().getBooleanExtra(EXTRA_REGISTRATION, false);
            if (editFavorites) {
                Intent intent = new Intent(this, BaseActivity.class);// New activity
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                this.overridePendingTransition(0, 0);
                startActivity(intent);
                finish();
            } else if (registration) {
                this.overridePendingTransition(0, 0);
                finish();
            }
        }
    }
}
