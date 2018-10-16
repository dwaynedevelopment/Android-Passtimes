package com.dwaynedevelopment.passtimes.account.signup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.account.signup.interfaces.ISignUpHandler;

public class SignUpFragment extends Fragment {

    private ISignUpHandler iSignUpHandler;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ISignUpHandler) {
            iSignUpHandler = (ISignUpHandler) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            //AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (getView() != null) {
                View view = getView();
                LinearLayout loginLayout = view.findViewById(R.id.ll_bottom_signup);
                loginLayout.setOnClickListener(signUpLayoutListener);
                LinearLayout termsLayout = view.findViewById(R.id.ll_terms);
                termsLayout.setOnClickListener(signUpLayoutListener);
            }
        }
    }

    private final View.OnClickListener signUpLayoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (iSignUpHandler != null) {
                int id = v.getId();
                final int login = R.id.ll_bottom_signup;
                final int terms = R.id.ll_terms;
                switch (id) {
                    case login:
                        iSignUpHandler.invokeLogin();
                        break;
                    case terms:
                        iSignUpHandler.invokeTerms();
                        break;
                }
            }
        }
    };
}
