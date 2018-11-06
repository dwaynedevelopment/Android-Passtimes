package com.dwaynedevelopment.passtimes.account.signup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.account.signup.interfaces.ISignUpHandler;

import java.util.Arrays;
import java.util.Objects;

import static com.dwaynedevelopment.passtimes.utils.ValidationUtils.credentialSignUpValidation;

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
                fullNameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                //fullNameEditText.addTextChangedListener(fullNameTextWatcher);
                // Please note that this will only work if your device keyboard Auto Capitalize Setting enabled.
                emailEditText = view.findViewById(R.id.et_email);
                passwordEditText = view.findViewById(R.id.et_password);
                rePasswordEditText = view.findViewById(R.id.et_re_password);
            }
        }
    }

//    private static final String TAG = "SignUpFragment";
//    private final TextWatcher fullNameTextWatcher = new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            String editable = s.toString();
//            String[] editableSegments = editable.split(" ");
//
//
//            StringBuilder fullName = new StringBuilder();
//            char[] firstSegment = editableSegments[0].toCharArray();
//            char firstLetter = Character.toUpperCase(firstSegment[0]);
//            fullName.append(firstLetter);
//
//            for (int i = 0; i <editableSegments.length; i++) {
//                char[] nameSegments = editableSegments[i].toCharArray();
//
//                for (int j = 1; j <nameSegments.length ; j++) {
//
//                    fullName.append(nameSegments[j]);
//
//                    if (nameSegments[j] == ' ') {
//                        char secondLetter = Character.toUpperCase(nameSegments[0]);
//                        fullName.append(secondLetter);
//
//                    }
//
////                    fullName.append(nameSegments[j]);
//                }
//
//
//            }
//
//            Log.i(TAG, "afterTextChanged: " + fullName);
//
//
////                if (firstSegment.length > 0) {
////                    char firstLetter = Character.toUpperCase(firstSegment[0]);
////                    StringBuilder firstName = new StringBuilder().append(firstLetter);
////                    for (int i = 1; i <firstSegment.length ; i++) {
////                        char restOfSegment = firstSegment[i];
////                        firstName.append(restOfSegment);
////                    }
////
////                    //String firstWord = first + Arrays.toString(firstSegment);
////                    Log.i(TAG, "afterTextChanged: " + firstName);
////                    //fullNameEditText.setText(firstName);
////                }
//
//            }
//
//    };

    private final View.OnClickListener signUpLayoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int login = R.id.ll_bottom_signup;
            final int terms = R.id.ll_terms;
            final int gallery = R.id.ic_camera_signup;
            final int signUp = R.id.btn_signup;
            switch (v.getId()) {
                case login:
                    if (iSignUpHandler != null) {
                        iSignUpHandler.invokeLogin();
                    }
                    break;
                case terms:
                    if (iSignUpHandler != null) {
                        iSignUpHandler.invokeTerms();
                    }
                    break;
                case gallery:
                    if (iSignUpHandler != null) {
                        iSignUpHandler.invokeGallery();
                    }
                    break;
                case signUp:
                    if (credentialSignUpValidation(getContext(), fullNameEditText, emailEditText, passwordEditText, rePasswordEditText)) {
                        if (iSignUpHandler != null) {
                            iSignUpHandler.authenticateSignUpWithEmail(
                                    Objects.requireNonNull(emailEditText.getText()).toString(),
                                    Objects.requireNonNull(passwordEditText.getText()).toString(),
                                    Objects.requireNonNull(fullNameEditText.getText()).toString());
                        }
                    }
                    break;
            }
        }
    };
}
