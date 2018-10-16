package com.dwaynedevelopment.passtimes.account.login.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.account.signup.interfaces.ILoginHandler;

public class LoginFragment extends Fragment {

    private ILoginHandler iLoginHandler;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ILoginHandler) {
            iLoginHandler = (ILoginHandler) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            //AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (getView() != null) {
                View view = getView();
                LinearLayout signUpLayout = view.findViewById(R.id.ll_bottom_login);
                signUpLayout.setOnClickListener(signUpLayoutListener);
            }
        }
    }

    private final View.OnClickListener signUpLayoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (iLoginHandler != null) {
                iLoginHandler.invokeSignUp();
            }
        }
    };
}
