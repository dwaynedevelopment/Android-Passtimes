package com.dwaynedevelopment.passtimes.base.feed.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.CalendarUtils;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_EVENT_SELECTED;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_USERS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.EXTRA_SELECTED_EVENT_ID;

public class EventFeedViewAdapter extends RecyclerView.Adapter<EventFeedViewAdapter.ViewHolder> {

    private Map<String, Event> eventMap;
    private Context context;
    private AuthUtils mAuth;
    private FirebaseFirestoreUtils mDb;

    public EventFeedViewAdapter(Map<String, Event> eventMap, Context context) {
        this.eventMap = eventMap;
        this.context = context;
        this.mAuth = AuthUtils.getInstance();
        this.mDb = FirebaseFirestoreUtils.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_ongoing, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        List<Event> result = createListFromMapEntries(eventMap);

        result.sort(new Event.EventComparator());

        final Event event = result.get(i);

        final DocumentReference playerDocumentReference = mDb.getFirestore()
                .document("/"+DATABASE_REFERENCE_USERS+"/" + mAuth.getCurrentSignedUser().getId());

        if (event != null) {
            if (event.getAttendees() != null && event.getAttendees().size() > 1) {
                for (int j = 0; j <event.getAttendees().size() ; j++) {
                    if (event.getAttendees().get(j).equals(playerDocumentReference)) {
                        holder.statusView.setBackgroundResource(R.drawable.cv_active);
                        break;
                    } else {
                        holder.statusView.setBackgroundResource(R.drawable.cv_idle);
                    }
                }
            }

            String month = CalendarUtils.getMonthFromDate(event.getStartDate());
            holder.tvMonth.setText(month);
            String day = CalendarUtils.getDayFromDate(event.getStartDate());
            holder.tvDay.setText(day);

            holder.tvSport.setText(event.getSport());
            holder.tvTitle.setText(event.getTitle());
            holder.tvLocation.setText(event.getLocation());

            String time = CalendarUtils.getDateTimeFromDate(event.getStartDate());
            holder.tvTime.setText(time);

            holder.eventCard.setOnClickListener(v -> {
                Intent selectIntent = new Intent(ACTION_EVENT_SELECTED);
                selectIntent.putExtra(EXTRA_SELECTED_EVENT_ID,  event.getId());
                context.sendBroadcast(selectIntent);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (eventMap != null) {
            return eventMap.size();
        }
        return 0;
    }

    private <K, V> List<V> createListFromMapEntries(Map<K, V> map) {
        return new ArrayList<>(map.values());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final View statusView;
        private final CardView eventCard;
        private final TextView tvMonth;
        private final TextView tvDay;
        private final TextView tvSport;
        private final TextView tvTitle;
        private final TextView tvLocation;
        private final TextView tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            statusView = itemView.findViewById(R.id.cv_status_view);
            eventCard = itemView.findViewById(R.id.cv_ongoing);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvSport = itemView.findViewById(R.id.tv_sport);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}
