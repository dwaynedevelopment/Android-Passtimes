package com.dwaynedevelopment.passtimes.navigation.fragments.event;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.CalendarUtils;
import com.dwaynedevelopment.passtimes.utils.DatabaseUtils;
import com.github.badoualy.datepicker.DatePickerTimeline;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static com.dwaynedevelopment.passtimes.utils.SnackbarUtils.invokeSnackBar;

public class CreateEventDialogFragment extends DialogFragment {

    public static final String TAG = "CreateEventDialogFragme";

    private DatePickerTimeline timeline;
    private DatabaseUtils mDb;
    private AuthUtils mAuth;

    private Calendar mStartCalendar;
    private Calendar mEndCalendar;
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
        mAuth = AuthUtils.getInstance();

        if(getView() != null) {
            Toolbar createEventToolbar = getView().findViewById(R.id.tb_create_event);
            createEventToolbar.inflateMenu(R.menu.menu_create_event);
            createEventToolbar.setOnMenuItemClickListener(menuItemClickListener);

            // TODO: layout
            mStartCalendar = Calendar.getInstance();
            mEndCalendar = Calendar.getInstance();

            Calendar startDate = Calendar.getInstance();
            startDate.add(Calendar.WEEK_OF_MONTH, 0);

            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.WEEK_OF_MONTH, 1);

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

                EditText title = getView().findViewById(R.id.et_title);
                EditText location = getView().findViewById(R.id.et_location);
                // Validate for empty EditTexts
                if(validateTextField(title, "Please enter a Title for the event") &&
                        validateTextField(location, "Please enter a Location for the event") &&
                        validateTextField(etStartTime, "Please select a Start Time") &&
                        validateTextField(etEndTime, "Please select an End Time")) {
                    // Validate for Time
                    if(validateTime()) {
                        Player currentPlayer = mAuth.getCurrentSignedUser();
                        Event event = new Event(currentPlayer.getId(), currentPlayer.getThumbnail(), "Soccer", title.getText().toString(), 28.596285, -81.301245, location.getText().toString(), mStartCalendar.getTimeInMillis(), mEndCalendar.getTimeInMillis(), 5);
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
        } else if( mStartCalendar.getTimeInMillis() == mEndCalendar.getTimeInMillis()) {
            Snackbar sb = Snackbar.make(getView(), "Please select a valid End Time", Snackbar.LENGTH_SHORT);
            sb.show();
            return false;
        } else if (mStartCalendar.getTimeInMillis() > mEndCalendar.getTimeInMillis()) {
            mEndCalendar.set(Calendar.DAY_OF_MONTH, mStartCalendar.get(Calendar.DAY_OF_MONTH + 1));
        }

        return true;
    }

    private boolean validateTextField(EditText editText, String message) {
        if(editText.getText().toString().isEmpty()) {
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

            if(!hasFocus) {

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


}
