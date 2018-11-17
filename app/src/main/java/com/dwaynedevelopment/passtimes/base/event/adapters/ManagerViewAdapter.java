package com.dwaynedevelopment.passtimes.base.event.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_FAVORITE_SELECTED;

public class ManagerViewAdapter extends RecyclerView.Adapter<ManagerViewAdapter.ViewHolder> {

    private Map<String, Player> attendeesMap;
    private Context context;
    private ArrayList<Player> selectedPlayers = new ArrayList<>();

    public ManagerViewAdapter(Map<String, Player> attendeesMap, Context context) {
        this.attendeesMap = attendeesMap;
        this.context = context;
    }

    @NonNull
    @Override
    public ManagerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_attendees, parent, false);
        return new ManagerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        List<Player> result = createListFromMapEntries(attendeesMap);

        result.sort(Collections.reverseOrder(new Player.PlayerComparator()));
        final Player playerAttendee = result.get(i);

        if (playerAttendee != null) {
            viewHolder.attendeeNameTextView.setText(playerAttendee.getName());
            viewHolder.attendeeExperienceTextView.setText(String.valueOf(playerAttendee.getOverallXP()));
            Glide.with(context).load(playerAttendee.getThumbnail()).into(viewHolder.attendeeProfileImageView);

            viewHolder.cv_attendee.setOnClickListener(v -> {

                if (v.isSelected()) {
                    v.setSelected(false);
                    viewHolder.attendeeNameTextView.setTextColor(ViewUtils.getColorResourceFromPackage(context, R.color.colorDarkPrimary));
                    viewHolder.cv_attendee.setBackgroundResource(R.drawable.cv_absent);
                    viewHolder.cv_attendee.setSelected(false);
                    selectedPlayers.remove(playerAttendee);
                } else {
                    v.setSelected(true);
                    viewHolder.attendeeNameTextView.setTextColor(ViewUtils.getColorResourceFromPackage(context, R.color.colorLightPrimary));
                    viewHolder.cv_attendee.setSelected(true);
                    viewHolder.cv_attendee.setBackgroundResource(R.drawable.cv_attendee);
                    selectedPlayers.add(playerAttendee);
                }

                Intent selectIntent = new Intent("ACTION_ATTENDEE_SELECTED");
                selectIntent.putParcelableArrayListExtra("SELECTED_ATTENDEES", selectedPlayers);
                context.sendBroadcast(selectIntent);

            });

        }
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

        private View cv_attendee; // cv_attending
        private ImageView attendeeProfileImageView; // ic_sport_attending
        private TextView attendeeNameTextView; // tv_date_attending
        private TextView attendeeExperienceTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            cv_attendee = itemView.findViewById(R.id.v_attendee_view);
            attendeeProfileImageView = itemView.findViewById(R.id.ci_attendee_profile);
            attendeeNameTextView = itemView.findViewById(R.id.tv_attendee_name);
            attendeeExperienceTextView = itemView.findViewById(R.id.tv_experience_points);
        }
    }
}
