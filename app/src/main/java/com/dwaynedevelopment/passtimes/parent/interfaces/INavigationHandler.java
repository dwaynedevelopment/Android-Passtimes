package com.dwaynedevelopment.passtimes.parent.interfaces;

public interface INavigationHandler {
    void invokeSettings();
    void invokeFavorites();
    void invokeEditProfile();
    void invokeCreateEvent();
    void invokeViewEvent(String eventDocumentReference);

}
