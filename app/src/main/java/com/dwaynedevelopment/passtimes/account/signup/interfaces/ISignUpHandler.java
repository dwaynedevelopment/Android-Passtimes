package com.dwaynedevelopment.passtimes.account.signup.interfaces;

public interface ISignUpHandler {
    void invokeLogin();
    void invokeTerms();
    void invokeGallery();
    void authenticateSignUpWithEmail(String email, String password, String name);
}
