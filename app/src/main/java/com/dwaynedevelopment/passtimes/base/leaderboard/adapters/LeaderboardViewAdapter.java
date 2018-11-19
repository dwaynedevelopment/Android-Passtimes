package com.dwaynedevelopment.passtimes.base.leaderboard.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LeaderboardViewAdapter extends RecyclerView.Adapter<LeaderboardViewAdapter.ViewHolder> {


    private static final String TAG = "LeaderboardViewAdapter";
    private AuthUtils mAuth;
    private Map<String, Player> leaderboardMap;
    private Context context;

    public LeaderboardViewAdapter(Map<String, Player> leaderboardMap, Context context) {
        this.leaderboardMap = leaderboardMap;
        this.context = context;
        this.mAuth = AuthUtils.getInstance();
    }

    @NonNull
    @Override
    public LeaderboardViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_leaderboard, parent, false);
        return new LeaderboardViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewAdapter.ViewHolder viewHolder, int i) {
        List<Player> result = createListFromMapEntries(leaderboardMap);

        result.sort(Collections.reverseOrder(new Player.PlayerComparator()));

        final Player playerAttendee = result.get(i);

        if (playerAttendee != null) {
            viewHolder.leaderboardName.setText(playerAttendee.getName());
            viewHolder.leaderboardNumber.setText(String.valueOf(i + 1));
            viewHolder.leaderboardPoints.setText(String.valueOf(playerAttendee.getOverallXP()));
            Glide.with(context).load(playerAttendee.getThumbnail()).into(viewHolder.leaderboardProfile);

            if (playerAttendee.getId().equals(mAuth.getCurrentSignedUser().getId())) {
                Log.i(TAG, "onBindViewHolder: " + i);

                Intent rankingIntent = new Intent("ACTION_RANKING_NUMBER");
                rankingIntent.putExtra("EXTRA_RANKING_NUMBER", i + 1);
                context.sendBroadcast(rankingIntent);

            }

        }
    }


    @Override
    public int getItemCount() {
        if (leaderboardMap != null) {
            return leaderboardMap.size();
        }
        return 0;
    }

    private <K, V> List<V> createListFromMapEntries(Map<K, V> map) {
        return new ArrayList<>(map.values());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView leaderboardPoints;
        private TextView leaderboardNumber;
        private ImageView leaderboardProfile;
        private TextView leaderboardName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            leaderboardPoints = itemView.findViewById(R.id.tv_experience_points_rank);
            leaderboardNumber = itemView.findViewById(R.id.tv_leaderboard_number);
            leaderboardProfile = itemView.findViewById(R.id.ci_leaderboard_profile);
            leaderboardName = itemView.findViewById(R.id.tv_leaderboard_name);
        }
    }
}
