package com.dwaynedevelopment.passtimes.onboarding.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.account.login.activities.LoginActivity;
import com.dwaynedevelopment.passtimes.account.signup.activities.SignUpActivity;
import com.dwaynedevelopment.passtimes.adapters.ViewPagerAdapter;
import com.dwaynedevelopment.passtimes.navigation.activities.BaseActivity;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.eyalbira.loadingdots.LoadingDots;

import static com.dwaynedevelopment.passtimes.utils.OnboardingUtils.setupOnboardingViewPager;

public class OnboardActivity extends AppCompatActivity {

    private ViewPager onboardingViewPager;
    private AuthUtils mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        mAuth = AuthUtils.getInstance();
        LinearLayout bottomLayout = findViewById(R.id.ll_onboarding_bottom);
        final LoadingDots progress = findViewById(R.id.pb_dot_onboard);
        if (mAuth.getCurrentSignedUser() != null) {
            bottomLayout.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress.setVisibility(View.VISIBLE);
                    progress.startAnimation();
                }
            }, 250);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress.stopAnimation();
                    progress.setVisibility(View.GONE);
                    finish();
                    Intent intent = new Intent(OnboardActivity.this, BaseActivity.class);
                    startActivity(intent);
                }
            }, 1500);

        } else {
            bottomLayout.setVisibility(View.VISIBLE);
            onboardingViewPager = findViewById(R.id.vp_onboarding);

            TabLayout dotLayout = findViewById(R.id.tl_dots);
            dotLayout.setupWithViewPager(onboardingViewPager, true);

            setupOnboardingViewPager(new ViewPagerAdapter(getSupportFragmentManager()), onboardingViewPager);

            Button loginButton = findViewById(R.id.btn_login_onboard);
            loginButton.setOnClickListener(bottomSignUpListener);

            LinearLayout bottomLinearLayout = findViewById(R.id.ll_bottom_message);
            bottomLinearLayout.setOnClickListener(bottomSignUpListener);
        }


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
            int id = v.getId();
            final int login = R.id.btn_login_onboard;
            final int signup = R.id.ll_bottom_message;
            switch (id) {
                case login:
                    intentHandler(login);
                    break;
                case signup:
                    intentHandler(signup);
                    break;
            }
        }
    };

    private void intentHandler(int id) {
        Intent intent = null;

        if (R.id.btn_login_onboard == id) {
            intent = new Intent(this, LoginActivity.class);
        } else if (R.id.ll_bottom_message == id) {
            intent = new Intent(this, SignUpActivity.class);
        }

        if (intent != null) {
            finish();
            startActivity(intent);
        }

    }
}