package com.dwaynedevelopment.passtimes.navigation.fragments.event;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.utils.DatabaseUtils;

import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class CreateEventDialogFragment extends DialogFragment {

    public static final String TAG = "CreateEventDialogFragme";

    private HorizontalCalendar mHorizontalCalendar;
    private DatabaseUtils mDb;

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
        }
    }

    Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(item.getItemId() == R.id.action_close) {
                dismiss();
            } else if(item.getItemId() == R.id.action_save) {
                // TODO: Save event to database
                Event event = new Event();
                mDb.addEvent(event);
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
}
