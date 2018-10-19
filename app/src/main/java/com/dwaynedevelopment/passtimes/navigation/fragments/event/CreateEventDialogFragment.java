package com.dwaynedevelopment.passtimes.navigation.fragments.event;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.adapters.FavoriteViewAdapter;
import com.dwaynedevelopment.passtimes.adapters.PlacesApiAdapter;
import com.dwaynedevelopment.passtimes.adapters.SelectedViewAdapter;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.PlaceData;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.models.Sport;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.CalendarUtils;
import com.dwaynedevelopment.passtimes.utils.DatabaseUtils;
import com.github.badoualy.datepicker.DatePickerTimeline;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import static com.dwaynedevelopment.passtimes.utils.GoogleApiClientUtils.getApiClient;
import static com.dwaynedevelopment.passtimes.utils.GoogleApiClientUtils.getPlacesAdapter;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_FAVORITE_SELECTED;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_SELECT_SELECTED;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_SPORTS;
import static com.dwaynedevelopment.passtimes.utils.SnackbarUtils.invokeSnackBar;

public class CreateEventDialogFragment extends DialogFragment {

    public static final String TAG = "CreateEventDialogFragme";

    private DatePickerTimeline timeline;
    private DatabaseUtils mDb;
    private AuthUtils mAuth;

    private PlacesApiAdapter mPlacesApiAdapter;
    private GoogleApiClient mGoogleApiClient;
    private SelectReceiver selectReceiver;

    private Calendar mStartCalendar;
    private Calendar mEndCalendar;
    private EditText etStartTime;
    private EditText etEndTime;
    private AutoCompleteTextView etAddress;
    private PlaceData mPlaceData;

    private Sport selectedSport;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_event_create, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDb = DatabaseUtils.getInstance();
        mAuth = AuthUtils.getInstance();
        mDb.reference(DATABASE_REFERENCE_SPORTS).addListenerForSingleValueEvent(valueEventListener);
        if (getActivity() != null) {

            if (getView() != null) {
                Toolbar createEventToolbar = getView().findViewById(R.id.tb_create_event);
                createEventToolbar.inflateMenu(R.menu.menu_create_event);
                createEventToolbar.setOnMenuItemClickListener(menuItemClickListener);

                mGoogleApiClient = getApiClient(
                        (AppCompatActivity) getActivity(),
                        onConnectionFailedListener);

                mPlacesApiAdapter = getPlacesAdapter(
                        (AppCompatActivity) getActivity(),
                        mGoogleApiClient);

                selectReceiver = new SelectReceiver();
                IntentFilter actionFilter = new IntentFilter();
                actionFilter.addAction(ACTION_SELECT_SELECTED);
                getActivity().registerReceiver(selectReceiver, actionFilter);

                mStartCalendar = Calendar.getInstance();
                mEndCalendar = Calendar.getInstance();

                int year = Calendar.getInstance().get(Calendar.YEAR);
                int month = Calendar.getInstance().get(Calendar.YEAR);
                int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                timeline = getView().findViewById(R.id.date_timeline);
                timeline.setFirstVisibleDate(year, month, day);
                timeline.setLastVisibleDate(year, month, day + 6);
                timeline.setOnDateSelectedListener(dateSelectedListener);

                etStartTime = getView().findViewById(R.id.et_start_time);
                etStartTime.setShowSoftInputOnFocus(false);
                etStartTime.setKeyListener(null);
                etStartTime.setOnFocusChangeListener(focusChangeListener);

                etEndTime = getView().findViewById(R.id.et_end_time);
                etEndTime.setShowSoftInputOnFocus(false);
                etEndTime.setKeyListener(null);
                etEndTime.setOnFocusChangeListener(focusChangeListener);

                etAddress = getView().findViewById(R.id.et_location);
                etAddress.setAdapter(mPlacesApiAdapter);
                etAddress.setOnFocusChangeListener(focusChangeListener);
                etAddress.setOnItemClickListener(autoCompleteClickListener);

            }
        }
    }

    // Save and close event creator
    Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.action_close) {
                dismiss();
            } else if (item.getItemId() == R.id.action_save) {
                // TODO: Validate inputs

                EditText title = getView().findViewById(R.id.et_title);

                // Validate for empty EditTexts
                if (validateTextField(title, "Please enter a Title for the event") &&
                        validateTextField(etAddress, "Please enter a Location for the event") &&
                        validateTextField(etStartTime, "Please select a Start Time") &&
                        validateTextField(etEndTime, "Please select an End Time")) {
                    // Validate for Time
                    if (validateTime()) {
                        Player currentPlayer = mAuth.getCurrentSignedUser();
                        Event event = new Event(currentPlayer.getId(), currentPlayer.getThumbnail(), selectedSport.getCategory(), title.getText().toString(), mPlaceData.getLatLng().latitude, mPlaceData.getLatLng().longitude, etAddress.getText().toString(), mStartCalendar.getTimeInMillis(), mEndCalendar.getTimeInMillis(), 5);
                        mDb.addEvent(event);
                        dismiss();
                    }
                }
            }
            return false;
        }
    };

    private boolean validateTime() {
        if (Calendar.getInstance().getTimeInMillis() >= mStartCalendar.getTimeInMillis()) {
            Snackbar sb = Snackbar.make(getView(), "Please select a valid Start Time", Snackbar.LENGTH_SHORT);
            sb.show();
            return false;
        } else if (mStartCalendar.getTimeInMillis() == mEndCalendar.getTimeInMillis()) {
            Snackbar sb = Snackbar.make(getView(), "Please select a valid End Time", Snackbar.LENGTH_SHORT);
            sb.show();
            return false;
        } else if (mStartCalendar.getTimeInMillis() > mEndCalendar.getTimeInMillis()) {
            mEndCalendar.set(Calendar.DAY_OF_MONTH, mStartCalendar.get(Calendar.DAY_OF_MONTH + 1));
        }

        return true;
    }

    private boolean validateTextField(EditText editText, String message) {
        if (editText.getText().toString().isEmpty()) {
            Snackbar sb = Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
            sb.show();
            return false;
        }

        return true;
    }

    private final DatePickerTimeline.OnDateSelectedListener dateSelectedListener = new DatePickerTimeline.OnDateSelectedListener() {
        @Override
        public void onDateSelected(int year, int month, int day, int index) {
            mStartCalendar = CalendarUtils.setDate(mStartCalendar, year, month, day);
            mEndCalendar = CalendarUtils.setDate(mEndCalendar, year, month, day);
        }
    };

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            int id = v.getId();
            Context context = getContext();

            if (!hasFocus) {

            } else {
                switch (id) {
                    case R.id.et_start_time:
                        TimePickerDialog timePickerDialog = new TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog, startTimeSetListener, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE + 1), false);
                        timePickerDialog.show();
                        break;
                    case R.id.et_end_time:
                        timePickerDialog = new TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog, endTimeSetListener, mStartCalendar.get(Calendar.HOUR_OF_DAY + 1), mStartCalendar.get(Calendar.MINUTE), false);
                        timePickerDialog.show();
                        break;
                }
            }
        }
    };

    TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mStartCalendar.setTime(CalendarUtils.setTime(mStartCalendar, hourOfDay, minute));
            etStartTime.setText(new SimpleDateFormat("hh:mm aa", Locale.US).format(mStartCalendar.getTime()));
        }
    };

    TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mEndCalendar.setTime(CalendarUtils.setTime(mEndCalendar, hourOfDay, minute));
            etEndTime.setText(new SimpleDateFormat("hh:mm aa", Locale.US).format(mEndCalendar.getTime()));
        }
    };

    private final AdapterView.OnItemClickListener autoCompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (getActivity() != null) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                etAddress.setText(" ");
                final AutocompletePrediction item = mPlacesApiAdapter.getItem(position);
                if (item != null) {
                    final String placeId = item.getPlaceId();
                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
                    placeResult.setResultCallback(updatePlaceDetailsCallback);
                }
            }
        }
    };

    private final ResultCallback<PlaceBuffer> updatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }

            final Place place = places.get(0);
            if (place != null) {
                try {
                    mPlaceData = new PlaceData(
                            place.getId(),
                            place.getName().toString(),
                            Objects.requireNonNull(place.getAddress()).toString(),
                            new LatLng(Objects.requireNonNull(
                                    place.getViewport()).getCenter().latitude,
                                    place.getViewport().getCenter().longitude));

                    //mLatLng = mPlaceData.getLatLng();
                    etAddress.setText(mPlaceData.getName());
                    etAddress.clearFocus();
                    etAddress.clearListSelection();
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                }
            }
            places.release();
        }
    };

    private final GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(selectReceiver);
        }
    }

    private final ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            ArrayList<Sport> sportsArray = new ArrayList<>();

            for (DataSnapshot ds: dataSnapshot.getChildren()) {

                if (ds != null) {
                    Sport sport = ds.getValue(Sport.class);
                    sportsArray.add(sport);
                }
            }
            SelectedViewAdapter adapter = new SelectedViewAdapter((AppCompatActivity) getActivity(), sportsArray);
            if (getActivity() != null) {
                if (getView() != null) {
                    RecyclerView recyclerView = getView().findViewById(R.id.rv_sports);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                    recyclerView.setAdapter(adapter);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    public class SelectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            selectedSport = intent.getParcelableExtra("SELECTED_SELECT");
        }
    }

}
