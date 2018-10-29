package com.dwaynedevelopment.passtimes.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.utils.CalendarUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_EVENT_SELECTED;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.EXTRA_SELECTED_EVENT_ID;

public class AttendingFeedViewAdapter extends RecyclerView.Adapter<AttendingFeedViewAdapter.ViewHolder> {


    private static final String TAG = "AttendingFeedViewAdapte";
    private Map<String, Event> attendingMap;
    private Context context;

    public AttendingFeedViewAdapter(Map<String, Event> attendingMap, Context context) {
        this.attendingMap = attendingMap;
        this.context = context;
    }

    @NonNull
    @Override
    public AttendingFeedViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_attending, parent, false);
        return new AttendingFeedViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        List<Event> result = createListFromMapEntries(attendingMap);
        final Event event = result.get(i);

        String time = CalendarUtils.getDateTimeFromDate(event.getStartDate());
        viewHolder.dateTextView.setText(time);
        viewHolder.titleTextView.setText(event.getTitle());

        Glide.with(context).load(event.getSportThumbnail()).into(viewHolder.sportImageView);

        viewHolder.cv_attending.setOnClickListener(v -> {
//            Intent selectIntent = new Intent(ACTION_EVENT_SELECTED);
//            selectIntent.putExtra(EXTRA_SELECTED_EVENT_ID,  event.getId());
//            context.sendBroadcast(selectIntent);
            Log.i(TAG, "onBindViewHolder: ATTENDING CLICKED");
        });
    }

    @Override
    public int getItemCount() {
        if (attendingMap != null) {
            return attendingMap.size();
        }
        return 0;
    }

    private <K, V> List<V> createListFromMapEntries(Map<K, V> map) {
        return new ArrayList<>(map.values());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView cv_attending; // cv_attending
        private ImageView sportImageView; // ic_sport_attending
        private TextView dateTextView; // tv_date_attending
        private TextView titleTextView; // tv_tittle_attending

        ViewHolder(@NonNull View itemView) {
            super(itemView);
             cv_attending = itemView.findViewById(R.id.cv_attending);
             sportImageView = itemView.findViewById(R.id.ic_sport_attending);
             dateTextView = itemView.findViewById(R.id.tv_date_attending);
             titleTextView = itemView.findViewById(R.id.tv_tittle_attending);
        }
    }
}
