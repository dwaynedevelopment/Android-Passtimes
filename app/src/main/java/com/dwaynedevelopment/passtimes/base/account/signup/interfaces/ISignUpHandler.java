package com.dwaynedevelopment.passtimes.base.account.signup.interfaces;

public interface ISignUpHandler {
    void invokeLogin();
    void invokeTerms();
    void invokeGallery();
    void authenticateSignUpWithEmail(String email, String password, String name);
}
