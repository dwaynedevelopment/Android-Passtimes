package com.dwaynedevelopment.passtimes.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class KeyUtils {
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 0x1004;
    public static final int REQUEST_GALLERY_IMAGE_SELECT = 0x1005;
    public static final String ROOT_STORAGE_USER_PROFILES = "ROOT_STORAGE_USER_PROFILES";
    static final int SOCIAL_PROFILE_DIMEN = 300;
    public static final String DATABASE_REFERENCE_USERS = "players";
    public static final String DATABASE_REFERENCE_EVENTS = "events";
    public static final String DATABASE_REFERENCE_SPORTS = "sports";
    public static final String ACTION_FAVORITE_SELECTED = "com.dwaynedevelopment.passtimes.ACTION_FAVORITE_SELECTED";
    public static final String ACTION_EVENT_SELECTED = "com.dwaynedevelopment.passtimes.ACTION_EVENT_SELECTED";
    public static final String ACTION_SELECT_SELECTED = "com.dwaynedevelopment.passtimes.ACTION_SELECT_SELECTED";
    public static final String EXTRA_REGISTRATION  = "com.dwaynedevelopment.passtimes.EXTRA_REGISTRATION";
    public static final String PREFERENCE_SIGN_OUT = "com.dwaynedevelopment.passtimes.PREFERENCE_SIGN_OUT";

    public static final int NOTIFY_INSERTED_DATA = 0x0311;
    public static final int NOTIFY_MODIFIED_DATA = 0x0411;
    public static final int NOTIFY_REMOVED_DATA = 0x0511;

    public static final int TOAST_SUCCESS = 0x0611;
    public static final int TOAST_ERROR = 0x0711;
    public static final int TOAST_WARNING = 0x0811;

    public static final String ARGS_SELECTED_EVENT_ID = "com.dwaynedevelopment.passtimes.ARGS_SELECTED_EVENT_ID";
    public static final String EXTRA_SELECTED_EVENT_ID = "com.dwaynedevelopment.passtimes.EXTRA_SELECTED_EVENT_ID";

    static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168),
            new LatLng(71, 136));


}
