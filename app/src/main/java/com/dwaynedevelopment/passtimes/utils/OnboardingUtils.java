package com.dwaynedevelopment.passtimes.utils;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.dwaynedevelopment.passtimes.adapters.ViewPagerAdapter;
import com.dwaynedevelopment.passtimes.onboarding.fragments.OnboardOneFragment;
import com.dwaynedevelopment.passtimes.onboarding.fragments.OnboardThreeFragment;
import com.dwaynedevelopment.passtimes.onboarding.fragments.OnboardTwoFragment;

import java.util.ArrayList;

public class OnboardingUtils {

    private static ArrayList<Fragment> onboardingFragments = new ArrayList<Fragment>() {{
        add(new OnboardOneFragment());
        add(new OnboardTwoFragment());
        add(new OnboardThreeFragment());
    }};

    public static void setupOnboardingViewPager(ViewPagerAdapter adapter, ViewPager viewPager) {
        for (Fragment fragment : onboardingFragments) {
            adapter.addFragment(fragment);
        }
        viewPager.setOffscreenPageLimit(onboardingFragments.size());
        viewPager.animate();
        viewPager.setAdapter(adapter);
    }

}



