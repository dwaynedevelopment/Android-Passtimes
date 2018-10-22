package com.dwaynedevelopment.passtimes.navigation.fragments.event;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.CalendarUtils;
import com.dwaynedevelopment.passtimes.utils.DatabaseUtils;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewEventDialogFragment extends DialogFragment {

    public static final String TAG = "ViewEventDialogFragment";
    private static final String EVENT = "EVENT";
    private DatabaseUtils mDb;

    private Event event;

    public static ViewEventDialogFragment newInstance(Event event) {
        
        Bundle args = new Bundle();
        args.putParcelable(EVENT, event);
        
        ViewEventDialogFragment fragment = new ViewEventDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
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
        return inflater.inflate(R.layout.dialog_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        event = getArguments().getParcelable(EVENT);

        Button btnJoin = getView().findViewById(R.id.btn_event_join);
        btnJoin.setOnClickListener(clickListener);

        AuthUtils authUtils = AuthUtils.getInstance();
        if (event.getHostId().equals(authUtils.getCurrentSignedUser().getId())) {
            btnJoin.setVisibility(View.GONE);

            //ImageButton delete = getView().findViewById(R.id.ib_delete);
            //delete.setVisibility(View.VISIBLE);
            //delete.setOnClickListener(clickListener);
        }

        mDb = DatabaseUtils.getInstance();

        CircleImageView ciHost = getView().findViewById(R.id.ci_host);
        Glide.with(getContext()).load(event.getHostThumbnail()).into(ciHost);

        TextView tvMonth = getView().findViewById(R.id.tv_event_month);
        tvMonth.setText(CalendarUtils.getMonthFromDate(event.getStartDate()));

        TextView tvDay = getView().findViewById(R.id.tv_event_day);
        tvDay.setText(CalendarUtils.getDayFromDate(event.getStartDate()));

        TextView tvTitle = getView().findViewById(R.id.tv_event_title);
        tvTitle.setText(event.getTitle());

        TextView tvTime = getView().findViewById(R.id.tv_event_time);
        tvTime.setText(timeRangeString(event));

        TextView tvLocation = getView().findViewById(R.id.tv_event_location);
        tvLocation.setText(event.getLocation());

        ImageButton ibClose = getView().findViewById(R.id.ib_close);
        ibClose.setOnClickListener(clickListener);



    }

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_close:
                    dismiss();
                    break;
                case R.id.btn_event_join:
                    AuthUtils auth = AuthUtils.getInstance();
                    Player player = auth.getCurrentSignedUser();

                    HashMap<String, HashMap<String, String>> attendeesList = new HashMap<>();
                    HashMap<String, String> attendees = new HashMap<>();
                    attendees.put("id", player.getId());
                    attendees.put("name", player.getName());
                    attendees.put("thumbnail", player.getThumbnail());
                    attendeesList.put(player.getId(), attendees);

                    event.setPlayerList(attendeesList);

                    DatabaseUtils db = DatabaseUtils.getInstance();
                    //FIXME
                    //TODO
                    //db.insertPlayerToEvent(event);

                    v.setVisibility(View.GONE);
                    break;
                case R.id.ib_delete:
                    //FIXME
                    //TODO
                    //mDb.deleteEvent(event);
                    dismiss();
                    break;
            }
        }
    };

    private String timeRangeString(Event event) {
        String startTime = CalendarUtils.getTimeFromDate(event.getStartDate());
        String endTime = CalendarUtils.getTimeFromDate(event.getEndDate());

        return startTime + " - " + endTime;
    }
}
