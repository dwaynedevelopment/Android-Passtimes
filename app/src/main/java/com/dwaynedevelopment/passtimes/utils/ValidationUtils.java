package com.dwaynedevelopment.passtimes.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Patterns;

import com.dwaynedevelopment.passtimes.R;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.dwaynedevelopment.passtimes.utils.ViewUtils.shakeViewWithAnimation;

public class ValidationUtils {

    private static Drawable getErrorDrawableForView(Context context) {
        Drawable customErrorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_error);
        if (customErrorDrawable != null) {
            customErrorDrawable.setBounds(
                    0,
                    0,
                    customErrorDrawable.getIntrinsicWidth(),
                    customErrorDrawable.getIntrinsicHeight());
            return customErrorDrawable;
        }
        return null;
    }

    public static boolean credentialLogInValidation(Context context, TextInputEditText emailInputEditText, TextInputEditText passwordTextInputEdit) {
        return isValidEmailFormat(context, emailInputEditText) && isPasswordFieldEmpty(context, passwordTextInputEdit);
    }


    public static boolean credentialSignUpValidation(Context context, TextInputEditText fullNameTextInputEdit, TextInputEditText emailInputEditText, TextInputEditText passwordTextInputEdit, TextInputEditText matchingTextInputEdit) {
        return isValidFullNameFormat(context, fullNameTextInputEdit) && isValidEmailFormat(context, emailInputEditText) && isValidPasswordFormat(context, passwordTextInputEdit) && isPasswordFieldMatches(context, passwordTextInputEdit, matchingTextInputEdit);
    }

    public static boolean isValidEmailFormat(Context context, TextInputEditText emailInputEditText) {

        CharSequence emailToValidate = Objects.requireNonNull(emailInputEditText.getText()).toString();

        if (TextUtils.isEmpty(emailToValidate)) {
            shakeViewWithAnimation(context, emailInputEditText);
            emailInputEditText.setError("Email Field Is Empty.", getErrorDrawableForView(context));
            emailInputEditText.setFocusable(true);
            emailInputEditText.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailToValidate).matches()) {
            shakeViewWithAnimation(context, emailInputEditText);
            emailInputEditText.setError("Email Format Not Valid", getErrorDrawableForView(context));
            emailInputEditText.setFocusable(true);
            emailInputEditText.requestFocus();
            return false;
        }

        return true;
    }

    private static boolean isValidFullNameFormat(Context context, TextInputEditText fullNameTextInputEdit){

        String fullNameToValidate = Objects.requireNonNull(fullNameTextInputEdit.getText()).toString();

        if (TextUtils.isEmpty(fullNameToValidate)) {
            shakeViewWithAnimation(context, fullNameTextInputEdit);
            fullNameTextInputEdit.setError("Full Name Field Is Empty.", getErrorDrawableForView(context));
            fullNameTextInputEdit.setFocusable(true);
            fullNameTextInputEdit.requestFocus();
            return false;
        }

        if (!fullNameToValidate.contains(" ")) {
            shakeViewWithAnimation(context, fullNameTextInputEdit);
            fullNameTextInputEdit.setError("First And Last Name Required.", getErrorDrawableForView(context));
            fullNameTextInputEdit.setFocusable(true);
            fullNameTextInputEdit.requestFocus();
            return false;
        }

        return true;

    }

    private static boolean isValidPasswordFormat(Context context, TextInputEditText passwordTextInputEdit) {
        Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$");
        CharSequence passwordToValidate = Objects.requireNonNull(passwordTextInputEdit.getText()).toString();

        if (TextUtils.isEmpty(passwordToValidate)) {
            shakeViewWithAnimation(context, passwordTextInputEdit);
            passwordTextInputEdit.setError("Password Field Is Empty.", getErrorDrawableForView(context));
            passwordTextInputEdit.setFocusable(true);
            passwordTextInputEdit.requestFocus();
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(passwordToValidate).matches()) {
            shakeViewWithAnimation(context, passwordTextInputEdit);
            passwordTextInputEdit.setError("Required: 1 Special Character, 1 Upper Case, 1 Lower Case, and 1 Digit.", getErrorDrawableForView(context));
            passwordTextInputEdit.setFocusable(true);
            passwordTextInputEdit.requestFocus();
            return false;
        }

        return true;
    }

    private static boolean isPasswordFieldEmpty(Context context, TextInputEditText passwordTextInputEdit) {

        CharSequence passwordToValidate = Objects.requireNonNull(passwordTextInputEdit.getText()).toString();

        if (TextUtils.isEmpty(passwordToValidate)) {
            shakeViewWithAnimation(context, passwordTextInputEdit);
            passwordTextInputEdit.setError("Password Field Is Empty.", getErrorDrawableForView(context));
            passwordTextInputEdit.setFocusable(true);
            passwordTextInputEdit.requestFocus();
            return false;
        }

        return true;
    }

    private static boolean isPasswordFieldMatches(Context context, TextInputEditText passwordTextInputEdit, TextInputEditText matchingTextInputEdit) {

        String password = Objects.requireNonNull(passwordTextInputEdit.getText()).toString();
        String matcher = Objects.requireNonNull(matchingTextInputEdit.getText()).toString();

        if (TextUtils.isEmpty(matcher)) {
            shakeViewWithAnimation(context, matchingTextInputEdit);
            matchingTextInputEdit.setError("Password Field Is Empty.", getErrorDrawableForView(context));
            matchingTextInputEdit.setFocusable(true);
            matchingTextInputEdit.requestFocus();
        }

        if (!matcher.equals(password)) {
            shakeViewWithAnimation(context, matchingTextInputEdit);
            matchingTextInputEdit.setError("Password Do Not Match.", getErrorDrawableForView(context));
            matchingTextInputEdit.setFocusable(true);
            matchingTextInputEdit.requestFocus();
            return false;
        }

        return true;
    }
}
