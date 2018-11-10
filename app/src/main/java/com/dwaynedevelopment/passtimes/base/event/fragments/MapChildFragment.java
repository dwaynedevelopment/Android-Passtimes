package com.dwaynedevelopment.passtimes.base.event.fragments;

import android.os.Bundle;
import android.util.Log;

import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.EventListener;
import java.util.Objects;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_EVENTS;

public class MapChildFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static final String TAG = "MapChildFragment";
    private FirebaseFirestoreUtils mDb;
    private GoogleMap mGoogleMapObject;

    public static MapChildFragment newInstance(String eventId) {
        Bundle args = new Bundle();
        MapChildFragment fragment = new MapChildFragment();
        args.putString("ARGS_EVENT_COORDS", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (getActivity() != null) {
            mDb = FirebaseFirestoreUtils.getInstance();
            getMapAsync(this);

            if (getArguments() != null) {
                String event = getArguments().getString("ARGS_EVENT_COORDS");
                if (event != null) {
                    mDb.databaseCollection(DATABASE_REFERENCE_EVENTS).document(event)
                            .addSnapshotListener(eventSnapshotListener);
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMapObject = googleMap;
    }

    private final com.google.firebase.firestore.EventListener<DocumentSnapshot> eventSnapshotListener = (DocumentSnapshot documentParentSnapshot,
                                                                                                         FirebaseFirestoreException eventException) -> {
        Event eventSelected = Objects.requireNonNull(documentParentSnapshot).toObject(Event.class);

        if (eventSelected != null) {
            LatLng latLng = new LatLng(eventSelected.getLatitude(), eventSelected.getLongitude());
            mGoogleMapObject.addMarker(new MarkerOptions().position(latLng).title(eventSelected.getTitle()));
            cameraZoomMoveAnimation(latLng, false, 14.5f);
        }
    };

    private void cameraZoomMoveAnimation(LatLng latLng, boolean animate, float zoom) {
        if (animate) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            mGoogleMapObject.animateCamera(cameraUpdate);
        } else {
            mGoogleMapObject.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    }

}
