package com.dwaynedevelopment.passtimes.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class PlaceData {

    private String mName;
    private String mAddress;
    private LatLng mLatLng;

    public PlaceData() {}

    public PlaceData(String mName, String mAddress, LatLng mLatLng) {
        this.mName = mName;
        this.mAddress = mAddress;
        this.mLatLng = mLatLng;
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
                ", mName='" + mName + '\'' +
                ", mAddress='" + mAddress + '\'' +
                ", mLatLng=" + mLatLng + '}';
    }
}
