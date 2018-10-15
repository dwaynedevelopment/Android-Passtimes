package com.dwaynedevelopment.passtimes.onboarding.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.adapters.OnboardPageAdapter;
import static com.dwaynedevelopment.passtimes.utils.OnboardingUtils.setupOnboardingViewPager;

public class OnboardActivity extends AppCompatActivity {

    private ViewPager onboardingViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        onboardingViewPager = findViewById(R.id.vp_onboarding);

        TabLayout dotLayout = findViewById(R.id.tl_dots);
        dotLayout.setupWithViewPager(onboardingViewPager, true);

        setupOnboardingViewPager(new OnboardPageAdapter(getSupportFragmentManager()), onboardingViewPager);

        Button loginButton = findViewById(R.id.btn_login);
        loginButton.setOnClickListener(bottomSignUpListener);
        LinearLayout bottomLinearLayout = findViewById(R.id.ll_bottom_message);
        bottomLinearLayout.setOnClickListener(bottomSignUpListener);
    }

    @Override
    public void onBackPressed() {
        if (onboardingViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem() - 1);
        }
    }

    private final View.OnClickListener bottomSignUpListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_login:
                    //TODO: Login Intent.
                        break;
                case R.id.ll_bottom_message:
                    //TODO: Signup Intent.
                        break;

                    default:
                        break;
            }
        }
    };
}