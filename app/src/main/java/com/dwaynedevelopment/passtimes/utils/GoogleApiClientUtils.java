package com.dwaynedevelopment.passtimes.utils;

import android.support.v7.app.AppCompatActivity;

import com.dwaynedevelopment.passtimes.base.event.adapters.PlacesApiAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.LAT_LNG_BOUNDS;

public class GoogleApiClientUtils {

    public static GoogleApiClient getApiClient(
            AppCompatActivity activity,
            GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        return new GoogleApiClient
                .Builder(activity.getApplicationContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(activity,
                        onConnectionFailedListener)
                .build();
    }

    public static PlacesApiAdapter getPlacesAdapter(
            AppCompatActivity activity,
            GoogleApiClient googleApiClient,
            LatLng currentLocation) {

        double radiusDegrees = 2.0;
        LatLng northEast = new LatLng(currentLocation.latitude + radiusDegrees, currentLocation.longitude + radiusDegrees);
        LatLng southWest = new LatLng(currentLocation.latitude - radiusDegrees, currentLocation.longitude - radiusDegrees);
        LatLngBounds bounds = LatLngBounds.builder()
                .include(northEast)
                .include(southWest)
                .build();

        return new PlacesApiAdapter(
                activity.getApplicationContext(),
                googleApiClient,
                bounds,
                null);
    }
}
