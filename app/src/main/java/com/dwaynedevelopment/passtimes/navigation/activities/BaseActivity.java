package com.dwaynedevelopment.passtimes.navigation.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.adapters.ViewPagerAdapter;
import com.dwaynedevelopment.passtimes.navigation.interfaces.IAccountHandler;
import com.dwaynedevelopment.passtimes.navigation.interfaces.INavigationHandler;
import com.dwaynedevelopment.passtimes.onboarding.activities.OnboardActivity;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.NavigationUtils;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class BaseActivity extends AppCompatActivity implements INavigationHandler, IAccountHandler {

    private ViewPager viewPager;
    private AuthUtils mAuth;

    private static final String TAG = "BaseActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mAuth = AuthUtils.getInstance();
        bottomNavigationSetup();

    }


    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    // Setup bottom navigation with view pager
    private void bottomNavigationSetup() {
        BottomNavigationViewEx bottomNav = findViewById(R.id.bottom_navigation_controller);
        NavigationUtils.bottomNavigationSetup(bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setOnTouchListener(viewPagerOnTouchListener);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        NavigationUtils.viewPagerSetup(viewPager, adapter);

        viewPager.setCurrentItem(0);
    }

    @SuppressLint("ClickableViewAccessibility")
    private final ViewPager.OnTouchListener viewPagerOnTouchListener = (v, event) -> true;


    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            selectedFragment(menuItem);
            return false;
        }
    };

    // Notify view pager to change current Item selected
    private void selectedFragment(MenuItem item) {
        item.setChecked(true);
        int navItem = item.getItemId();

        switch (navItem) {
            case R.id.nv_item_feed:
                // Set viewpager current item to feed without animation
                viewPager.setCurrentItem(0, false);
                break;
            case R.id.nv_item_profile:
                // Set viewpager current item to profile without animation
                viewPager.setCurrentItem(1, false);
                break;
        }
    }

    @Override
    public void invokeSettings() {
        viewPager.setCurrentItem(2, true);
    }

    @Override
    public void signOutOfAccount() {
        if (mAuth.isCurrentUserAuthenticated()) {
            mAuth.signOutFromHostAndSocial();
            finish();
            Intent intent = new Intent(BaseActivity.this, OnboardActivity.class);
            startActivity(intent);
        }
    }
}
