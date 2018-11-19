package com.dwaynedevelopment.passtimes.base.event.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;

import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AttendeesViewAdapter extends RecyclerView.Adapter<AttendeesViewAdapter.ViewHolder> {

    private Map<String, Player> attendeesMap;
    private Event attendingEvent;
    private Context context;

    public AttendeesViewAdapter(Map<String, Player> attendeesMap, Context context, Event attendingEvent) {
        this.attendeesMap = attendeesMap;
        this.context = context;
        this.attendingEvent = attendingEvent;
    }

    @NonNull
    @Override
    public AttendeesViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_attendees, parent, false);
        return new AttendeesViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        List<Player> result = createListFromMapEntries(attendeesMap);

        result.sort(Collections.reverseOrder(new Player.PlayerComparator()));
//        Collections.sort(result, new Person.AgeComparator());
        final Player playerAttendee = result.get(i);

        if (playerAttendee != null) {
            viewHolder.attendeeNameTextView.setText(playerAttendee.getName());
            viewHolder.attendeePoints.setText(String.valueOf(playerAttendee.getOverallXP()));
            Glide.with(context).load(playerAttendee.getThumbnail()).into(viewHolder.attendeeProfileImageView);
            if (attendingEvent != null) {
                attendingEvent.getEventHost().get().addOnCompleteListener(task -> {
                    if (task.getResult() != null) {
                        final Player host = task.getResult().toObject(Player.class);
                        if (host != null) {
                            if (host.equals(playerAttendee)) {
                                viewHolder.cv_attendee.setBackgroundResource(R.drawable.cv_host);
                            }
                        }
                    }
                });

            }
        }

//        viewHolder.cv_attending.setOnClickListener(v -> {
//            Intent selectIntent = new Intent(ACTION_EVENT_SELECTED);
//            selectIntent.putExtra(EXTRA_SELECTED_EVENT_ID,  event.getId());
//            context.sendBroadcast(selectIntent);
//        });
    }

    @Override
    public int getItemCount() {
        if (attendeesMap != null) {
            return attendeesMap.size();
        }
        return 0;
    }

    private <K, V> List<V> createListFromMapEntries(Map<K, V> map) {
        return new ArrayList<>(map.values());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView attendeePoints;
        private View cv_attendee; // cv_attending
        private ImageView attendeeProfileImageView; // ic_sport_attending
        private TextView attendeeNameTextView; // tv_date_attending

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            attendeePoints = itemView.findViewById(R.id.tv_experience_points);
            cv_attendee = itemView.findViewById(R.id.v_attendee_view);
            attendeeProfileImageView = itemView.findViewById(R.id.ci_attendee_profile);
            attendeeNameTextView = itemView.findViewById(R.id.tv_attendee_name);
        }
    }
}
