package com.dwaynedevelopment.passtimes.utils;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ERROR_DIALOG_REQUEST;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.LOCATION_PERMISSION_REQUEST_CODE;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.REQUEST_COARSE_LOCATION;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.REQUEST_FINE_LOCATION;

public class LocationUtils {

    private static final String TAG = "LocationUtils";
    public static boolean mLocationServicesGranted = false;

    private static final String[] permissions = {
            REQUEST_FINE_LOCATION,
            REQUEST_COARSE_LOCATION
    };


    public static boolean googlePlayServicesValid(Context context) {
        if (context != null) {
            final int googlePlayAvailability = GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(context);
            if (googlePlayAvailability == ConnectionResult.SUCCESS) {
                return true;
            } else if (GoogleApiAvailability.getInstance().isUserResolvableError(googlePlayAvailability)) {
                Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((AppCompatActivity) context,
                        googlePlayAvailability, ERROR_DIALOG_REQUEST);
                dialog.show();
            } else {
                Log.i(TAG, "googlePlayServicesValid: CAN'T MAKE REQUEST");
            }
        }
        return false;
    }

    public static Location getLocationPermission(AppCompatActivity context, LocationManager locationManager) {
        Location mLocation = null;
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                REQUEST_FINE_LOCATION) == PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                    REQUEST_COARSE_LOCATION) == PERMISSION_GRANTED) {

                    boolean mIsGPSEnabled = locationManager.isProviderEnabled(GPS_PROVIDER);
                    boolean mIsNetworkEnabled = locationManager.isProviderEnabled(NETWORK_PROVIDER);

                    if (mIsGPSEnabled) {
                        locationManager.requestLocationUpdates(
                                GPS_PROVIDER,
                                10000, 4,
                                (LocationListener) context);
                        mLocation = locationManager.getLastKnownLocation(GPS_PROVIDER);
                        mLocationServicesGranted = true;
                    } else if (mIsNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                NETWORK_PROVIDER,
                                10000,
                                4,
                                (LocationListener) context);
                        mLocation = locationManager.getLastKnownLocation(NETWORK_PROVIDER);
                        mLocationServicesGranted = true;
                    }
                } else {
                    ActivityCompat.requestPermissions(context,
                            permissions,
                            LOCATION_PERMISSION_REQUEST_CODE);
                }
            } else {
                ActivityCompat.requestPermissions(context,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }

        return mLocation;
    }
}
