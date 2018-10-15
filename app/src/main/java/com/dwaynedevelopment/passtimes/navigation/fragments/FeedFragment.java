package com.dwaynedevelopment.passtimes.navigation.fragments;

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
