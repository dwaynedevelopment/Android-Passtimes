package com.dwaynedevelopment.passtimes.utils;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.navigation.fragments.FeedFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class NavigationUtils {

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>() {{
        add(FeedFragment.newInstance());
    }};

    // Initialize bottom navifation with desired properties
    public static void bottomNavigationSetup(Context context, BottomNavigationViewEx bottomNav) {
        bottomNav.enableAnimation(false);
        bottomNav.setIconsMarginTop(60);
        //bottomNav.enableShiftingMode(false);
        //bottomNav.enableItemShiftingMode(false);
    }
}
