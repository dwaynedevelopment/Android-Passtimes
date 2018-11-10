package com.dwaynedevelopment.passtimes.base.event.interfaces;

public interface IEventHandler {
    void invokeEditDetailView(String eventDocumentReference);
    void dismissDetailView();
    void invokeEndEvent(String eventDocumentReference);
}
