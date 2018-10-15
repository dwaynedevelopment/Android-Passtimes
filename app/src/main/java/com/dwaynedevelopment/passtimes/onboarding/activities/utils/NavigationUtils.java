package com.dwaynedevelopment.passtimes.onboarding.activities.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.app.Fragment;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.onboarding.activities.fragments.FeedFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class NavigationUtils {

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>() {{
        add(FeedFragment.newInstance());
    }};

    public static void bottomNavigationInitialization(Context context, BottomNavigationViewEx bottomNav) {
        bottomNav.enableAnimation(false);
        //bottomNav.enableShiftingMode(false);
        //bottomNav.enableItemShiftingMode(false);

        final int[][] state = new int[][]{
                new int[]{android.R.attr.state_checked}, // checked
                new int[]{-android.R.attr.state_checked} // unchecked
        };

        final int[] color = new int[]{
                (context.getResources().getColor(R.color.colorAccent)),
                (Color.WHITE)
        };

        bottomNav.setItemIconTintList(new ColorStateList(state, color));

    }
}
