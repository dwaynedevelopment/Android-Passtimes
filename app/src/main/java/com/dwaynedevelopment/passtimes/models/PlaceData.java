package com.dwaynedevelopment.passtimes.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class PlaceData {

    private String mId;
    private String mName;
    private String mAddress;
    private LatLng mLatLng;

    public PlaceData() {}

    public PlaceData(String mId, String mName, String mAddress, LatLng mLatLng) {
        this.mId = mId;
        this.mName = mName;
        this.mAddress = mAddress;
        this.mLatLng = mLatLng;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }


    @NonNull
    @Override
    public String toString() {
        return "PlaceData{" +
                "mId='" + mId + '\'' +
                ", mName='" + mName + '\'' +
                ", mAddress='" + mAddress + '\'' +
                ", mLatLng=" + mLatLng + '}';
    }
}
