package com.dwaynedevelopment.passtimes.base.event.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.base.account.edit.activities.EditActivity;
import com.dwaynedevelopment.passtimes.base.event.fragments.EventCreateFragment;
import com.dwaynedevelopment.passtimes.base.event.fragments.EventDetailFragment;
import com.dwaynedevelopment.passtimes.base.event.interfaces.IEventHandler;
import com.dwaynedevelopment.passtimes.parent.activities.BaseActivity;
import com.dwaynedevelopment.passtimes.utils.LocationUtils;

import java.util.Objects;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_EVENTS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.LOCATION_PERMISSION_REQUEST_CODE;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.REQUEST_COARSE_LOCATION;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.REQUEST_FINE_LOCATION;
import static com.dwaynedevelopment.passtimes.utils.LocationUtils.getLocationPermission;
import static com.dwaynedevelopment.passtimes.utils.LocationUtils.mLocationServicesGranted;
import static com.squareup.okhttp.internal.Internal.instance;


public class EventActivity extends AppCompatActivity implements IEventHandler, LocationListener {

    private LocationManager locationManager;
    private boolean requestUpdates = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    LOCATION_PERMISSION_REQUEST_CODE);

        } else {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    REQUEST_FINE_LOCATION) == PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        REQUEST_COARSE_LOCATION) == PERMISSION_GRANTED) {
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    getLocationPermission(this, locationManager);
                    if (mLocationServicesGranted) {
                        requestUpdates = true;
                        invokeFragment(
                                getIntent().getStringExtra("EXTRA_EVENT_VIEW_ID"),
                                getIntent().getBooleanExtra("EXTRA_EVENT_EDIT_ID", false)
                        );
                    }

                }

            }
        }


    }

    public void invokeFragment(@Nullable String eventId, boolean isEditing) {
        if (eventId != null) {
            if (eventId.isEmpty() && !isEditing) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_event, EventCreateFragment.newInstance(null))
                        .commit();
            } else if (!eventId.isEmpty()) {
                if (isEditing) {

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_event, EventCreateFragment.newInstance(eventId))
                                .commit();

                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_event, EventDetailFragment.newInstance(eventId))
                            .commit();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, BaseActivity.class);// New activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.overridePendingTransition(0, 0);
        startActivity(intent);
        finish();
    }

    @Override
    public void invokeEditDetailView(String eventDocumentReference) {
        final String eventId = getIntent().getStringExtra("EXTRA_EVENT_VIEW_ID");
        if (eventId != null) {
            if (!eventId.isEmpty()) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_event, EventCreateFragment.newInstance("/" + DATABASE_REFERENCE_EVENTS + "/" + eventId))
                        .commit();
                }
            }

    }

    @Override
    public void dismissDetailView() {
        Intent intent = new Intent(this, BaseActivity.class);// New activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.overridePendingTransition(0, 0);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                getLocationPermission(this, locationManager);
                if (mLocationServicesGranted) {
                    requestUpdates = true;
                    invokeFragment(
                            getIntent().getStringExtra("EXTRA_EVENT_VIEW_ID"),
                            getIntent().getBooleanExtra("EXTRA_EVENT_EDIT_ID", false)
                    );
                }
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION },
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void removeLocationUpdates() {
        if (requestUpdates) {
            requestUpdates = false;
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        removeLocationUpdates();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}


