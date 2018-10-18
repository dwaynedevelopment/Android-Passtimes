package com.dwaynedevelopment.passtimes.navigation.fragments.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.dwaynedevelopment.passtimes.R;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.PREFERENCE_SIGN_OUT;


public class SettingsPreferenceFragment extends PreferenceFragmentCompat {

    //private INavigationHandler mListener;

    public static SettingsPreferenceFragment newInstance() {
        return new SettingsPreferenceFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

//        if(context instanceof INavigationHandler) {
//            mListener = (INavigationHandler) context;
//        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        android.support.v7.preference.Preference preference = findPreference(PREFERENCE_SIGN_OUT);
        preference.setOnPreferenceClickListener(signOutPreferenceListener);

    }

    android.support.v7.preference.Preference.OnPreferenceClickListener signOutPreferenceListener = new android.support.v7.preference.Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(android.support.v7.preference.Preference preference) {

//            if(mListener != null) {
//                mListener.signOut();
//            }

            return false;
        }
    };
}
