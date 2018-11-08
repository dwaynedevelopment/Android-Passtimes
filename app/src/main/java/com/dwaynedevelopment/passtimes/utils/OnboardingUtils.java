package com.dwaynedevelopment.passtimes.utils;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.dwaynedevelopment.passtimes.parent.adapters.BaseViewPagerAdapter;
import com.dwaynedevelopment.passtimes.base.onboarding.fragments.OnboardOneFragment;
import com.dwaynedevelopment.passtimes.base.onboarding.fragments.OnboardThreeFragment;
import com.dwaynedevelopment.passtimes.base.onboarding.fragments.OnboardTwoFragment;

import java.util.ArrayList;

public class OnboardingUtils {

    private static final ArrayList<Fragment> onboardingFragments = new ArrayList<Fragment>() {{
        add(new OnboardOneFragment());
        add(new OnboardTwoFragment());
        add(new OnboardThreeFragment());
    }};

    public static void setupOnboardingViewPager(BaseViewPagerAdapter adapter, ViewPager viewPager) {
        for (Fragment fragment : onboardingFragments) {
            adapter.addFragment(fragment);
        }
        viewPager.setOffscreenPageLimit(onboardingFragments.size());
        viewPager.animate();
        viewPager.setAdapter(adapter);
    }

}



