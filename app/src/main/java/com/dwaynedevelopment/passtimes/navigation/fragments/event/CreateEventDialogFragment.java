package com.dwaynedevelopment.passtimes.navigation.fragments.event;

import android.app.Dialog;
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

import java.util.ArrayList;
import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class CreateEventDialogFragment extends DialogFragment {

    public static final String TAG = "CreateEventDialogFragme";

    private HorizontalCalendar mHorizontalCalendar;
    private DatabaseUtils mDb;
    private AuthUtils mAuth;

    TextView tvStartTime;
    Button btnSelectedSport;

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
            Calendar startDate = Calendar.getInstance();
            startDate.add(Calendar.DAY_OF_WEEK, 0);

            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.DAY_OF_WEEK, 6);

            mHorizontalCalendar = new HorizontalCalendar.Builder(getView(), R.id.horizontal_calendar)
                    .range(startDate, endDate)
                    .datesNumberOnScreen(7)
                    .configure()
                    .formatTopText("E")
                    .showBottomText(false)
                    .end()
                    .build();

            mHorizontalCalendar.setCalendarListener(horizontalCalendarListener);
            mHorizontalCalendar.refresh();

            tvStartTime = getView().findViewById(R.id.tv_start_time);
            tvStartTime.setText(CalendarUtils.getCurrentTimeAsString(Calendar.getInstance()));
            tvStartTime.setOnClickListener(clickListener);

            TextView tvEndTime = getView().findViewById(R.id.tv_end_time);
            tvEndTime.setText(CalendarUtils.getCurrentTimeAsString(Calendar.getInstance()));
            tvEndTime.setOnClickListener(clickListener);
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
                Player currentPlayer = mAuth.getCurrentSignedUser();
                Event event = new Event(currentPlayer.getId(), currentPlayer.getThumbnail(), "Soccer","Giorgio's event", 28.596285, -81.301245, "Full Sail University", CalendarUtils.getStartCalendarDate(), 5);
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
        }

        @Override
        public void onCalendarScroll(HorizontalCalendarView calendarView, int dx, int dy) {
            super.onCalendarScroll(calendarView, dx, dy);
        }
    };

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            switch (id) {
                case R.id.tv_start_time:
                    TimePicker timePicker = getView().findViewById(R.id.time_spinner);
                    timePicker.setOnTimeChangedListener(timeChangedListener);

                    Animation timePickerAnimation = null;
                    // Check if TimePicker is visible and perform corresponding animation
                    if(timePicker.getVisibility() == View.VISIBLE) {
                        timePickerAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.collapsible);
                        timePicker.setVisibility(View.GONE);
                        timePicker.startAnimation(timePickerAnimation);
                    } else {
                        timePicker.setVisibility(View.VISIBLE);
                        timePickerAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.expandable);
                        timePicker.startAnimation(timePickerAnimation);
                    }
                    break;
            }

        }
    };

    TimePicker.OnTimeChangedListener timeChangedListener = new TimePicker.OnTimeChangedListener() {
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            // Set calendar with net time, get the string from the calendar and set the time text to the new time
            tvStartTime.setText(CalendarUtils.getCurrentTimeAsString(CalendarUtils.setTime(hourOfDay, minute)));
        }
    };
}
