package com.dwaynedevelopment.passtimes.base.onboarding.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.base.account.login.activities.LoginActivity;
import com.dwaynedevelopment.passtimes.base.account.signup.activities.SignUpActivity;
import com.dwaynedevelopment.passtimes.parent.adapters.BaseViewPagerAdapter;
import com.dwaynedevelopment.passtimes.parent.activities.BaseActivity;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;

import static com.dwaynedevelopment.passtimes.utils.OnboardingUtils.setupOnboardingViewPager;
import static com.dwaynedevelopment.passtimes.utils.ViewUtils.parentLayoutStatus;

public class OnboardActivity extends AppCompatActivity {

    private ViewPager onboardingViewPager;
    private ConstraintLayout onboardingParentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        AuthUtils mAuth = AuthUtils.getInstance();
        onboardingParentLayout = findViewById(R.id.cl_onboarding_parent);
        final LinearLayout bottomLayout = findViewById(R.id.ll_onboarding_bottom);
        final ProgressBar progress = findViewById(R.id.pb_dot_onboard);
        final TextView appTitle = findViewById(R.id.tv_app_name);

        new Handler().postDelayed(() -> {
            progress.setVisibility(View.VISIBLE);
            appTitle.setVisibility(View.VISIBLE);
        }, 550);

        if (mAuth.isCurrentUserAuthenticated()) {
            bottomLayout.setVisibility(View.GONE);
            new Handler().postDelayed(() -> {
                progress.setVisibility(View.GONE);
                appTitle.setVisibility(View.GONE);
                finish();
                Intent intent = new Intent(OnboardActivity.this, BaseActivity.class);
                startActivity(intent);
            }, 1750);

        } else {
            new Handler().postDelayed(() -> {
                progress.setVisibility(View.GONE);
                appTitle.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.VISIBLE);
                onboardingViewPager = findViewById(R.id.vp_onboarding);

                TabLayout dotLayout = findViewById(R.id.tl_dots);
                dotLayout.setupWithViewPager(onboardingViewPager, true);

                setupOnboardingViewPager(new BaseViewPagerAdapter(getSupportFragmentManager()), onboardingViewPager);

                Button loginButton = findViewById(R.id.btn_login_onboard);
                loginButton.setOnClickListener(bottomSignUpListener);

                LinearLayout bottomLinearLayout = findViewById(R.id.ll_bottom_message);
                bottomLinearLayout.setOnClickListener(bottomSignUpListener);
            }, 1750);
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

    private final View.OnClickListener bottomSignUpListener = v -> {
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
    };

    private void intentHandler(int id) {
        Intent intent = null;

        if (R.id.btn_login_onboard == id) {
            intent = new Intent(this, LoginActivity.class);
        } else if (R.id.ll_bottom_message == id) {
            intent = new Intent(this, SignUpActivity.class);
        }

        if (intent != null) {
            parentLayoutStatus(onboardingParentLayout, false);
            finish();
            startActivity(intent);
        }

    }
}