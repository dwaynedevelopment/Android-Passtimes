package com.dwaynedevelopment.passtimes.utils;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.dwaynedevelopment.passtimes.adapters.ViewPagerAdapter;
import com.dwaynedevelopment.passtimes.navigation.fragments.feed.FeedFragment;
import com.dwaynedevelopment.passtimes.navigation.fragments.profile.ProfileFragment;
import com.dwaynedevelopment.passtimes.navigation.fragments.profile.SettingsPreferenceFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class NavigationUtils {

    // Pre-load fragments
    public static ArrayList<Fragment> fragments = new ArrayList<Fragment>() {{
        add(FeedFragment.newInstance());
        add(ProfileFragment.newInstance());
        add(SettingsPreferenceFragment.newInstance());
    }};

    // Initialize custom bottom navigation
    public static void bottomNavigationSetup(BottomNavigationViewEx bottomNav) {
        bottomNav.enableAnimation(false);
        bottomNav.setIconsMarginTop(60);
        //bottomNav.enableShiftingMode(false);
        //bottomNav.enableItemShiftingMode(false);
    }

    public static void viewPagerSetup(ViewPager viewPager, ViewPagerAdapter adapter) {
        for (Fragment fragment : fragments) {
            adapter.addFragment(fragment);
        }
        viewPager.setAdapter(adapter);
    }
}
