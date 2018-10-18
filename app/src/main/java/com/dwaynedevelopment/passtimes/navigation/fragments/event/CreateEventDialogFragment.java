package com.dwaynedevelopment.passtimes.navigation.fragments.event;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.adapters.SportsViewAdapter;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.models.Sport;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.CalendarUtils;
import com.dwaynedevelopment.passtimes.utils.DatabaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class CreateEventDialogFragment extends DialogFragment {

    public static final String TAG = "CreateEventDialogFragme";

    private HorizontalCalendar mHorizontalCalendar;
    private DatabaseUtils mDb;
    private AuthUtils mAuth;

    private Calendar mCalendar;
    private EditText etStartTime;
    private EditText etEndTime;
    private Button btnSelectedSport;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }

        mAuth = AuthUtils.getInstance();
        mDb = DatabaseUtils.getInstance();

        DatabaseUtils.Reference sportsRef = DatabaseUtils.Reference.sports;
        mDb.reference(sportsRef).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Sport> sportsArray = new ArrayList<>();

                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Sport sport = new Sport();
                    sport.setCategory(ds.getValue(Sport.class).getCategory());
                    sport.setUrl(ds.getValue(Sport.class).getUrl());

                    sportsArray.add(sport);
                }
                SportsViewAdapter adapter = new SportsViewAdapter(getContext(), sportsArray);

                RecyclerView recyclerView = getView().findViewById(R.id.rv_sports);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), RadioGroup.HORIZONTAL, false));
                recyclerView.setAdapter(adapter);

                btnSelectedSport = null;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_create_event, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDb = DatabaseUtils.getInstance();

        if(getView() != null) {
            Toolbar createEventToolbar = getView().findViewById(R.id.tb_create_event);
            createEventToolbar.inflateMenu(R.menu.menu_create_event);
            createEventToolbar.setOnMenuItemClickListener(menuItemClickListener);

            // TODO: layout
            mCalendar = Calendar.getInstance();

            Calendar startDate = Calendar.getInstance();
            startDate.add(Calendar.WEEK_OF_MONTH, 0);

            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.WEEK_OF_MONTH, 1);

            mHorizontalCalendar = new HorizontalCalendar.Builder(getView(), R.id.horizontal_calendar)
                    .range(startDate, endDate)
                    .datesNumberOnScreen(7)
                    .configure()
                    .formatTopText("E")
                    .showBottomText(false)
                    .end()
                    .build();

            mHorizontalCalendar.setCalendarListener(horizontalCalendarListener);

            etStartTime = getView().findViewById(R.id.et_start_time);
            etStartTime.setShowSoftInputOnFocus(false);
            etStartTime.setKeyListener(null);
            etStartTime.setOnFocusChangeListener(focusChangeListener);

            etEndTime = getView().findViewById(R.id.et_end_time);
            etEndTime.setShowSoftInputOnFocus(false);
            etEndTime.setKeyListener(null);
            etEndTime.setOnFocusChangeListener(focusChangeListener);
        }
    }

    // Save and close event creator
    Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(item.getItemId() == R.id.action_close) {
                dismiss();
            } else if(item.getItemId() == R.id.action_save) {
                // TODO: Validate inputs

                TextView title = getView().findViewById(R.id.et_title);
                TextView location = getView().findViewById(R.id.et_location);

                Player currentPlayer = mAuth.getCurrentSignedUser();
                Event event = new Event(currentPlayer.getId(), currentPlayer.getThumbnail(), "Soccer",title.getText().toString(), 28.596285, -81.301245, location.getText().toString(), mCalendar.getTimeInMillis(), 5);
                mDb.addEvent(event);
                dismiss();
            }
            return false;
        }
    };

    private final HorizontalCalendarListener horizontalCalendarListener = new HorizontalCalendarListener() {
        @Override
        public void onDateSelected(Calendar date, int position) {
            // TODO: get date selected
            date.set(Calendar.HOUR_OF_DAY, mCalendar.get(Calendar.HOUR_OF_DAY));
            date.set(Calendar.MINUTE, mCalendar.get(Calendar.MINUTE));
            mCalendar = date;
        }
    };

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            int id = v.getId();
            Context context = getContext();

            if(!hasFocus) {

            } else {
                switch (id) {
                    case R.id.et_start_time:
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, startTimeSetListener, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
                        timePickerDialog.show();
                        break;
                    case R.id.et_end_time:
                        timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, endTimeSetListener, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
                        timePickerDialog.show();
                        break;
                }
            }
        }
    };

    TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.setTime(CalendarUtils.setTime(mCalendar, hourOfDay, minute));
            etStartTime.setText(new SimpleDateFormat("hh:mm aa", Locale.US).format(mCalendar.getTime()));
        }
    };

    TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar startTime = Calendar.getInstance();
            startTime.setTime(CalendarUtils.setTime(startTime, hourOfDay, minute));
            etEndTime.setText(new SimpleDateFormat("hh:mm aa", Locale.US).format(startTime.getTime()));
        }
    };


}
