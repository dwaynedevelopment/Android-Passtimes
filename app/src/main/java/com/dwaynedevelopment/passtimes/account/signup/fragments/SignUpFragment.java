package com.dwaynedevelopment.passtimes.account.signup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.account.signup.activities.SignUpActivity;
import com.dwaynedevelopment.passtimes.account.signup.interfaces.ISignUpHandler;

import java.util.Objects;

import static com.dwaynedevelopment.passtimes.utils.AuthUtils.credentialValidation;
import static com.dwaynedevelopment.passtimes.utils.AuthUtils.isValidEmail;
import static com.dwaynedevelopment.passtimes.utils.AuthUtils.isValidName;
import static com.dwaynedevelopment.passtimes.utils.AuthUtils.isValidPassword;
import static com.dwaynedevelopment.passtimes.utils.SnackbarUtils.invokeSnackBar;

public class SignUpFragment extends Fragment {

    private ISignUpHandler iSignUpHandler;
    private TextInputEditText fullNameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText rePasswordEditText;

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

                ImageButton galleryButton = view.findViewById(R.id.ic_camera_signup);
                galleryButton.setOnClickListener(signUpLayoutListener);

                Button signUpButton = view.findViewById(R.id.btn_signup);
                signUpButton.setOnClickListener(signUpLayoutListener);

                fullNameEditText = view.findViewById(R.id.et_fullname);
                emailEditText = view.findViewById(R.id.et_email);
                passwordEditText = view.findViewById(R.id.et_password);
                rePasswordEditText = view.findViewById(R.id.et_re_password);
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
                final int gallery = R.id.ic_camera_signup;
                final int signUp = R.id.btn_signup;
                switch (id) {
                    case login:
                        iSignUpHandler.invokeLogin();
                        break;
                    case terms:
                        iSignUpHandler.invokeTerms();
                        break;
                    case gallery:
                        iSignUpHandler.invokeGallery();
                        break;
                    case signUp:
                        String fullName = fullNameEditText.getText().toString();
                        String email = emailEditText.getText().toString();
                        String password = passwordEditText.getText().toString();
                        String rePassword = rePasswordEditText.getText().toString();

                        if (email.isEmpty() && password.isEmpty() && fullName.isEmpty() && rePassword.isEmpty()){
                            invokeSnackBar((AppCompatActivity) Objects.requireNonNull(getContext()),
                                    "One or more fields poorly formatted.",
                                    getResources().getColor(R.color.colorDarkPrimary),
                                    getResources().getColor(R.color.colorPrimaryAccent));

                        } else if (credentialValidation(getContext(), email, password, fullName)) {
                            if (password.equals(rePassword)) {
                                iSignUpHandler.authenticateSignUpWithEmail(email, password, fullName);
                            } else {
                                invokeSnackBar((AppCompatActivity) Objects.requireNonNull(getContext()),
                                        "Passwords don't match",
                                        getResources().getColor(R.color.colorDarkPrimary),
                                        getResources().getColor(R.color.colorPrimaryAccent));
                            }
                        }
                        break;
                }
            }
        }
    };
}
