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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.account.login.interfaces.ILoginHandler;

import java.util.Objects;

import static com.dwaynedevelopment.passtimes.utils.AuthUtils.credentialValidation;
import static com.dwaynedevelopment.passtimes.utils.SnackbarUtils.invokeSnackBar;

public class LoginFragment extends Fragment {

    private ILoginHandler iLoginHandler;
    private EditText emailEditText;
    private EditText passwordEditText;

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
                signUpLayout.setOnClickListener(loginLayoutListener);

                emailEditText = view.findViewById(R.id.et_email_login);
                passwordEditText = view.findViewById(R.id.et_password_login);

                Button loginButton = view.findViewById(R.id.btn_login);
                loginButton.setOnClickListener(loginLayoutListener);

            }
        }
    }

    private final View.OnClickListener loginLayoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (iLoginHandler != null) {
                int id = v.getId();
                final int signUp = R.id.ll_bottom_login;
                final int login = R.id.btn_login;
                switch (id) {
                    case signUp:
                        iLoginHandler.invokeSignUp();
                        break;
                    case login:
                        String email = emailEditText.getText().toString();
                        String password = passwordEditText.getText().toString();

                        if (email.isEmpty() && password.isEmpty()){
                            invokeSnackBar((AppCompatActivity) Objects.requireNonNull(getContext()),
                                    "One or more fields poorly formatted.",
                                    getResources().getColor(R.color.colorDarkPrimary),
                                    getResources().getColor(R.color.colorPrimaryAccent));

                        } else if (credentialValidation(getContext(), email, password)) {
                            iLoginHandler.authenticateSignInWithEmail(email, password);
                        }
                        break;
                }
            }
        }
    };
}
