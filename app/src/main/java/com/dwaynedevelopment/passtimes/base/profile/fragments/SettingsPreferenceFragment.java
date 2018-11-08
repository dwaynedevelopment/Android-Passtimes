package com.dwaynedevelopment.passtimes.base.profile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.base.profile.interfaces.IAccountHandler;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.PREFERENCE_SIGN_OUT;


public class SettingsPreferenceFragment extends PreferenceFragmentCompat {

    private IAccountHandler iAccountHandler;

    public static SettingsPreferenceFragment newInstance() {
        return new SettingsPreferenceFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof IAccountHandler) {
            iAccountHandler = (IAccountHandler) context;
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        android.support.v7.preference.Preference preference = findPreference(PREFERENCE_SIGN_OUT);
        preference.setOnPreferenceClickListener(signOutPreferenceListener);

    }

    private final android.support.v7.preference.Preference.OnPreferenceClickListener signOutPreferenceListener = new android.support.v7.preference.Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(android.support.v7.preference.Preference preference) {

            if(iAccountHandler != null) {
                iAccountHandler.signOutOfAccount();
            }

            return false;
        }
    };
}
