package com.dwaynedevelopment.passtimes.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.adapters.ViewPagerAdapter;
import com.dwaynedevelopment.passtimes.navigation.fragments.feed.FeedFragment;
import com.dwaynedevelopment.passtimes.navigation.fragments.profile.ProfileFragment;
import com.dwaynedevelopment.passtimes.navigation.fragments.profile.SettingsPreferenceFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class NavigationUtils {

    // Pre-load fragments
    private static final ArrayList<Fragment> fragments = new ArrayList<Fragment>() {{
        add(FeedFragment.newInstance());
        add(ProfileFragment.newInstance());
        add(SettingsPreferenceFragment.newInstance());
    }};

    // Initialize custom bottom navigation
    public static void bottomNavigationSetup(Context context, BottomNavigationViewEx bottomNav) {
        bottomNav.enableAnimation(false);
        bottomNav.setIconsMarginTop(45);
//        Toasty.Config.getInstance()
//                .setErrorColor(ContextCompat.getColor(context, R.color.colorPrimaryAccent))
//                .setSuccessColor(ContextCompat.getColor(context, R.color.colorSecondaryAccent))
//                .apply();
    }

    public static void viewPagerSetup(ViewPager viewPager, ViewPagerAdapter adapter) {
        for (Fragment fragment : fragments) {
            adapter.addFragment(fragment);
        }
        viewPager.setAdapter(adapter);
    }
}
