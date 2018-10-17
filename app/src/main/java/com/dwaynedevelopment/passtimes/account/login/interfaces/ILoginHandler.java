package com.dwaynedevelopment.passtimes.account.login.interfaces;

public interface ILoginHandler {
    void invokeSignUp();
    void authenticateSignInWithEmail(String email, String password);
}
