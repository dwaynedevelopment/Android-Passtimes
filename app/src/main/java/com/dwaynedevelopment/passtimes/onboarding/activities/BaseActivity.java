package com.dwaynedevelopment.passtimes.onboarding.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.onboarding.activities.utils.NavigationUtils;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        bottomNavigationSetup();
    }

    //
    private void bottomNavigationSetup() {
        BottomNavigationViewEx bottomNav = findViewById(R.id.bottom_navigation_controller);
        NavigationUtils.bottomNavigationSetup(this, bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    BottomNavigationViewEx.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            selectedFragment(menuItem);
            return false;
        }
    };

    public void selectedFragment(MenuItem item) {

        item.setChecked(true);
        int navItem = item.getItemId();

        switch (navItem) {
            case R.id.nv_item_feed:
                // TODO: LOAD FRAGMENT
                break;
            case R.id.nv_item_profile:
                // TODO: LOAD FRAGMENT
                break;
        }
    }
}
