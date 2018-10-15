package com.dwaynedevelopment.passtimes.onboarding.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class FeedFragment extends Fragment {

    public static FeedFragment newInstance() {
        Bundle args = new Bundle();
        
        FeedFragment fragment = new FeedFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
}
