package com.dwaynedevelopment.passtimes.navigation.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.MenuItem;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.adapters.ViewPagerAdapter;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.NavigationUtils;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


public class BaseActivity extends AppCompatActivity {

    private BottomNavigationViewEx bottomNav;
    private ViewPager viewPager;

    private static final String TAG = "BaseActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        bottomNavigationSetup();
    }


    // Setup bottom navigation with view pager
    private void bottomNavigationSetup() {
        bottomNav = findViewById(R.id.bottom_navigation_controller);
        NavigationUtils.bottomNavigationSetup(bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        viewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        NavigationUtils.viewPagerSetup(viewPager, adapter);

        viewPager.setCurrentItem(0);
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            selectedFragment(menuItem);
            return false;
        }
    };

    // Notify view pager to change current Item selected
    public void selectedFragment(MenuItem item) {
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
}
