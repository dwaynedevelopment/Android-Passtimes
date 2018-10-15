package com.dwaynedevelopment.passtimes.onboarding.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.adapters.NavPageAdapter;
import com.dwaynedevelopment.passtimes.onboarding.fragments.OnboardOneFragment;

import java.util.ArrayList;

public class OnboardActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private NavPageAdapter adapter;

    public ArrayList<Fragment> fragments = new ArrayList<Fragment>() {{
        add(new OnboardOneFragment());
        add(new Fragment());
        add(new Fragment());
        add(new Fragment());
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        viewPager = findViewById(R.id.vp_onboarding);
        TabLayout tabLayout = findViewById(R.id.tl_dots);
        tabLayout.setupWithViewPager(viewPager, true);

        adapter = new NavPageAdapter(getSupportFragmentManager());
        viewPager.addOnPageChangeListener(viewPagerListener);
        viewPager.setOnTouchListener(viewPagerOnTouchListener);
        setupViewPager(adapter, viewPager);
    }

    private void setupViewPager(NavPageAdapter adapter, ViewPager viewPager) {
        for (Fragment fragment : fragments) {
            adapter.addFragment(fragment);
        }
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.animate();
        viewPager.setAdapter(adapter);
    }


    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
//            if (prevMenuItem != null) {
//                prevMenuItem.setChecked(false);
//            } else {
//                mBottomNav.getMenu().getItem(INVOKE_FEED_FRAGMENT).setChecked(false);
//            }
//            if(position < 3) {
//                mBottomNav.getMenu().getItem(position).setChecked(true);
//                prevMenuItem = mBottomNav.getMenu().getItem(position);
//            }

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            /**/

            //viewPager.setCurrentItem(position, true);
        }

        @Override
        public void onPageScrollStateChanged(int state) { /**/ }
    };

    ViewPager.OnTouchListener viewPagerOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    };
}
