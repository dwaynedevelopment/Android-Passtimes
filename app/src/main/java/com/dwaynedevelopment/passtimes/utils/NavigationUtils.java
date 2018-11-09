package com.dwaynedevelopment.passtimes.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.dwaynedevelopment.passtimes.parent.adapters.BaseViewPagerAdapter;
import com.dwaynedevelopment.passtimes.base.feed.fragments.FeedFragment;
import com.dwaynedevelopment.passtimes.base.leaderboard.fragments.LeaderboardFragment;
import com.dwaynedevelopment.passtimes.base.profile.fragments.ProfileFragment;
import com.dwaynedevelopment.passtimes.base.profile.fragments.SettingsPreferenceFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;


public class NavigationUtils {

    // Pre-load fragments
    private static final ArrayList<Fragment> fragments = new ArrayList<Fragment>() {{
        add(FeedFragment.newInstance());
        add(LeaderboardFragment.newInstance());
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

    public static void viewPagerSetup(ViewPager viewPager, BaseViewPagerAdapter adapter) {
        for (Fragment fragment : fragments) {
            adapter.addFragment(fragment);
        }
        viewPager.setAdapter(adapter);
    }
}
