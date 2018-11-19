package com.dwaynedevelopment.passtimes.parent.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.base.account.edit.activities.EditActivity;
import com.dwaynedevelopment.passtimes.base.event.activities.EventActivity;
import com.dwaynedevelopment.passtimes.base.favorites.activities.FavoriteActivity;
import com.dwaynedevelopment.passtimes.base.profile.fragments.ProfileDialogFragment;
import com.dwaynedevelopment.passtimes.parent.adapters.BaseViewPagerAdapter;
import com.dwaynedevelopment.passtimes.parent.interfaces.INavigationHandler;
import com.dwaynedevelopment.passtimes.base.profile.interfaces.IAccountHandler;
import com.dwaynedevelopment.passtimes.base.onboarding.activities.OnboardActivity;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.NavigationUtils;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import static com.dwaynedevelopment.passtimes.utils.ViewUtils.dissmissKeyboardOnItemSelected;



public class BaseActivity extends AppCompatActivity implements INavigationHandler, IAccountHandler {

    private ViewPager viewPager;
    private AuthUtils mAuth;
    private ProfileReceiver profileReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mAuth = AuthUtils.getInstance();
        bottomNavigationSetup();
    }

    @Override
    protected void onStart() {
        super.onStart();
        profileReceiver = new ProfileReceiver();
        IntentFilter actionFilter = new IntentFilter();
        actionFilter.addAction("ACTION_SEND_USER");
        registerReceiver(profileReceiver, actionFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(profileReceiver);
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
    @SuppressLint("ClickableViewAccessibility")
    private void bottomNavigationSetup() {
        BottomNavigationViewEx bottomNav = findViewById(R.id.bottom_navigation_controller);
        NavigationUtils.bottomNavigationSetup(this, bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setOnTouchListener(viewPagerOnTouchListener);
        BaseViewPagerAdapter adapter = new BaseViewPagerAdapter(getSupportFragmentManager());
        NavigationUtils.viewPagerSetup(viewPager, adapter);

        viewPager.setCurrentItem(0);
    }

    @SuppressLint("ClickableViewAccessibility")
    private final ViewPager.OnTouchListener viewPagerOnTouchListener = (v, event) -> true;


    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = menuItem -> {
        selectedFragment(menuItem);
        return false;
    };


    // Notify view pager to change current Item selected
    private void selectedFragment(MenuItem item) {
        item.setChecked(true);
        int navItem = item.getItemId();
        View view = this.getCurrentFocus();
        switch (navItem) {
            case R.id.nv_item_feed:
                dissmissKeyboardOnItemSelected(this, view);
                // Set viewpager current item to feed without animation
                viewPager.setCurrentItem(0, false);
                break;
            case R.id.nv_item_search:
                // Set viewpager current item to profile without animation
                viewPager.setCurrentItem(1, false);
                break;
            case R.id.nv_item_leaderboard:
                dissmissKeyboardOnItemSelected(this, view);
                // Set viewpager current item to profile without animation
                viewPager.setCurrentItem(2, false);
                break;
            case R.id.nv_item_profile:
                dissmissKeyboardOnItemSelected(this, view);
                // Set viewpager current item to profile without animation
                viewPager.setCurrentItem(3, false);
                break;

        }
    }

    @Override
    public void invokeSettings() {
        viewPager.setCurrentItem(4, true);
    }

    @Override
    public void invokeFavorites() {
        Intent intent = new Intent(this, FavoriteActivity.class);// New activity
        intent.putExtra("EXTRA_EDIT_FAVORITES", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.overridePendingTransition(0, 0);
        startActivity(intent);
        finish();
    }

    @Override
    public void invokeEditProfile() {
        Intent intent = new Intent(this, EditActivity.class);// New activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.overridePendingTransition(0, 0);
        startActivity(intent);
        finish();
    }

    @Override
    public void invokeCreateEvent() {
        Intent intent = new Intent(this, EventActivity.class);// New activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("EXTRA_EVENT_VIEW_ID", "");
        intent.putExtra("EXTRA_EVENT_EDIT_ID", false);
        this.overridePendingTransition(0, 0);
        startActivity(intent);
        finish();
    }

    @Override
    public void invokeViewEvent(String eventDocumentReference) {
        Intent intent = new Intent(this, EventActivity.class);// New activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("EXTRA_EVENT_VIEW_ID", eventDocumentReference);
        intent.putExtra("EXTRA_EVENT_EDIT_ID", false);
        this.overridePendingTransition(0, 0);
        startActivity(intent);
        finish();
    }

    @Override
    public void signOutOfAccount() {
        if (mAuth.isCurrentUserAuthenticated()) {
            mAuth.signOutFromHostAndSocial();
            Intent intent = new Intent(this, OnboardActivity.class);// New activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            this.overridePendingTransition(0, 0);
            startActivity(intent);
            finish();
        }
    }


    private static final String TAG = "BaseActivity";
    private class ProfileReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra("EXTRA_USER_ID");
            Log.i(TAG, "onReceive: " + id);
            ProfileDialogFragment viewEventDialogFragment = ProfileDialogFragment.newInstance(id);
            viewEventDialogFragment.show(getSupportFragmentManager(), ProfileDialogFragment.TAG);
        }
    }
}
