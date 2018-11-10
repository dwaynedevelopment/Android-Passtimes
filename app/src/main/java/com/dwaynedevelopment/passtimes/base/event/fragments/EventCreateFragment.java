package com.dwaynedevelopment.passtimes.base.event.fragments;

import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TimePicker;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.base.account.edit.adapters.SelectedViewAdapter;
import com.dwaynedevelopment.passtimes.base.event.adapters.PlacesApiAdapter;
import com.dwaynedevelopment.passtimes.base.event.interfaces.IEventHandler;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.PlaceData;
import com.dwaynedevelopment.passtimes.models.Sport;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.CalendarUtils;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.github.badoualy.datepicker.DatePickerTimeline;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static com.dwaynedevelopment.passtimes.utils.CalendarUtils.getTimeFromDate;
import static com.dwaynedevelopment.passtimes.utils.GoogleApiClientUtils.getApiClient;
import static com.dwaynedevelopment.passtimes.utils.GoogleApiClientUtils.getPlacesAdapter;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_SELECT_SELECTED;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_EVENTS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_SPORTS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_USERS;

public class EventCreateFragment extends Fragment {

    public static final String TAG = "CreateEventDialogFragme";

    private IEventHandler iEventHandler;
    private FirebaseFirestoreUtils mDb;
    private AuthUtils mAuth;

    private PlacesApiAdapter mPlacesApiAdapter;
    private GoogleApiClient mGoogleApiClient;
    private SelectReceiver selectReceiver;

    private Calendar mStartCalendar;
    private Calendar mEndCalendar;
    private EditText etTitle;
    private EditText etStartTime;
    private EditText etEndTime;
    private AutoCompleteTextView etAddress;
    private PlaceData mPlaceData;

    private Sport selectedSport;
    private Event eventToModify;

    public static EventCreateFragment newInstance(String editEventDocumentReference) {
        Bundle args = new Bundle();
        args.putString("ARGS_EDIT_SELECTED_EVENT", editEventDocumentReference);
        EventCreateFragment fragment = new EventCreateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IEventHandler) {
            iEventHandler = (IEventHandler) context;
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

        if (getActivity() != null) {

            mDb = FirebaseFirestoreUtils.getInstance();
            mAuth = AuthUtils.getInstance();

            if (getView() != null) {
                mDb.databaseCollection(DATABASE_REFERENCE_SPORTS).get()
                        .addOnCompleteListener(selectEventSportListener);

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
                int month = Calendar.getInstance().get(Calendar.MONTH);
                int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                DatePickerTimeline timeline = getView().findViewById(R.id.date_timeline);
                timeline.setFirstVisibleDate(year, month, day);
                timeline.setLastVisibleDate(year, month, day + 6);
                timeline.setOnDateSelectedListener(dateSelectedListener);

                etTitle = getView().findViewById(R.id.et_title);

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

                if (getArguments() != null) {
                    String stringReference = getArguments().getString("ARGS_EDIT_SELECTED_EVENT");
                    if (stringReference != null && !stringReference.isEmpty()) {
                        final DocumentReference editEventDocumentReference = mDb.getFirestore().document(stringReference);
                        editEventDocumentReference.get().addOnCompleteListener(task -> {
                            if (task.getResult() != null) {
                                eventToModify = task.getResult().toObject(Event.class);
                                if (eventToModify != null) {

                                    etTitle.setText(eventToModify.getTitle());

                                    etAddress.setText(eventToModify.getLocation());
                                    etAddress.setFocusable(true);
                                    etAddress.setFocusableInTouchMode(true);
                                    etAddress.requestFocus();

                                    etStartTime.setText(getTimeFromDate(eventToModify.getStartDate()));
                                    etEndTime.setText(getTimeFromDate(eventToModify.getEndDate()));

                                }
                            }
                        });
                    }
                }

            }


        }
    }

    private final OnCompleteListener<QuerySnapshot> selectEventSportListener = new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            ArrayList<Sport> sportsArray = new ArrayList<>();

            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    sportsArray.add(document.toObject(Sport.class));

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
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        }
    };

    // Save and close event creator
    private final Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.action_close) {
                if (iEventHandler != null) {
                    iEventHandler.dismissEvent();
                }
            } else if (item.getItemId() == R.id.action_save) {
                // TODO: Validate inputs

                // Validate for empty EditTexts
                if (validateTextField(etTitle, "Please enter a Title for the event") &&
                        validateTextField(etAddress, "Please enter a Location for the event") &&
                        validateTextField(etStartTime, "Please select a Start Time") &&
                        validateTextField(etEndTime, "Please select an End Time")) {
                    // Validate for Time
                    if (validateTime()) {
                        if (mPlaceData != null) {
                            if (eventToModify != null) {
                                final DocumentReference eventDocumentReference = mDb.getFirestore()
                                        .document("/"+DATABASE_REFERENCE_EVENTS+"/"+ eventToModify.getId());
                                eventDocumentReference.update("enDate", mEndCalendar.getTimeInMillis());
                                eventDocumentReference.update("startDate", mStartCalendar.getTimeInMillis());
                                eventDocumentReference.update("latitude", mPlaceData.getLatLng().latitude);
                                eventDocumentReference.update("location", etAddress.getText().toString());
                                eventDocumentReference.update("longitude", mPlaceData.getLatLng().longitude);
                                eventDocumentReference.update("maxAttendees", 5);
                                eventDocumentReference.update("title", etTitle.getText().toString())
                                        .addOnSuccessListener(aVoid -> {
                                            if (iEventHandler != null) {
                                                iEventHandler.dismissEvent();
                                            }
                                        });

                            } else {
                                final DocumentReference playerDocumentReference = mDb.getFirestore()
                                        .document("/"+DATABASE_REFERENCE_USERS+"/"+mAuth.getCurrentSignedUser().getId());

                                final Event eventCreated = new Event(selectedSport.getCategory(), selectedSport.getActive(), etTitle.getText().toString(), etAddress.getText().toString(),
                                        mPlaceData.getLatLng().latitude, mPlaceData.getLatLng().longitude, mStartCalendar.getTimeInMillis(), mEndCalendar.getTimeInMillis(), 5, playerDocumentReference);

                                final DocumentReference eventDocumentReference = mDb.getFirestore()
                                        .document("/"+DATABASE_REFERENCE_EVENTS+"/"+ eventCreated.getId());

                                mDb.insertDocument(DATABASE_REFERENCE_EVENTS, eventCreated.getId(), eventCreated);
                                mDb.addAttendee(eventCreated, playerDocumentReference);
                                mDb.addAttendings(mAuth.getCurrentSignedUser(), eventDocumentReference);

                                if (iEventHandler != null) {
                                    iEventHandler.dismissEvent();
                                }
                            }

                        } else {
                            Snackbar sb = Snackbar.make(Objects.requireNonNull(getView()), "Please Confirm Address By Re-Selecting.", Snackbar.LENGTH_SHORT);
                            sb.show();
                        }
                    }
                }
            }
            return false;
        }
    };

    private boolean validateTime() {
        if (Calendar.getInstance().getTimeInMillis() >= mStartCalendar.getTimeInMillis()) {
            Snackbar sb = Snackbar.make(Objects.requireNonNull(getView()), "Please select a valid Start Time", Snackbar.LENGTH_SHORT);
            sb.show();
            return false;
        } else if (mStartCalendar.getTimeInMillis() == mEndCalendar.getTimeInMillis()) {
            Snackbar sb = Snackbar.make(Objects.requireNonNull(getView()), "Please select a valid End Time", Snackbar.LENGTH_SHORT);
            sb.show();
            return false;
        } else if (mStartCalendar.getTimeInMillis() > mEndCalendar.getTimeInMillis()) {
            mEndCalendar.set(Calendar.DAY_OF_MONTH, mStartCalendar.get(Calendar.DAY_OF_MONTH + 1));
        }

        return true;
    }

    private boolean validateTextField(EditText editText, String message) {
        if (editText.getText().toString().isEmpty()) {
            Snackbar sb = Snackbar.make(Objects.requireNonNull(getView()), message, Snackbar.LENGTH_SHORT);
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

    private final View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            int id = v.getId();
            Context context = getContext();

            if (!hasFocus) {
                Log.i(TAG, "onFocusChange: ");
            } else {
                switch (id) {
                    case R.id.et_start_time:
                        TimePickerDialog timePickerDialog = new TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog, startTimeSetListener, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
                        timePickerDialog.show();
                        break;
                    case R.id.et_end_time:
                        timePickerDialog = new TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog, endTimeSetListener, mStartCalendar.get(Calendar.HOUR_OF_DAY), mStartCalendar.get(Calendar.MINUTE), false);
                        timePickerDialog.show();
                        break;
                }
            }
        }
    };

    private final TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mStartCalendar.setTime(CalendarUtils.setTime(mStartCalendar, hourOfDay, minute));
            etStartTime.setText(new SimpleDateFormat("hh:mm aa", Locale.US).format(mStartCalendar.getTime()));
        }
    };

    private final TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
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

    private final GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = connectionResult -> { };

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(selectReceiver);
        }
    }

    public class SelectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            selectedSport = intent.getParcelableExtra("SELECTED_SELECT");
        }
    }

}
