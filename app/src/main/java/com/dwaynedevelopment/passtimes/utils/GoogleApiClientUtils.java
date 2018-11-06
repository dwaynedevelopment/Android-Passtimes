package com.dwaynedevelopment.passtimes.utils;

import android.support.v7.app.AppCompatActivity;

import com.dwaynedevelopment.passtimes.adapters.PlacesApiAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Places;

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
            GoogleApiClient googleApiClient) {

        return new PlacesApiAdapter(
                activity.getApplicationContext(),
                googleApiClient,
                LAT_LNG_BOUNDS,
                null);
    }
}
