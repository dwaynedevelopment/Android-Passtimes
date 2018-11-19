package com.dwaynedevelopment.passtimes.base.search.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.models.Player;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Player> playerList;
    private List<Player> playerListFull;
    private Context context;


    public SearchAdapter(Context context, List<Player> playerList) {
        this.playerList = playerList;
        this.playerListFull = new ArrayList<>(playerList);
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PlayerListHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_search, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        PlayerListHolder playerListHolder = (PlayerListHolder) viewHolder;
        final Player player = playerList.get(i);
        if (player != null) {
            Glide.with(context).load(player.getThumbnail()).into(playerListHolder.profileImageView);
            playerListHolder.profileNameTextView.setText(player.getName());
            ((PlayerListHolder) viewHolder).cardView.setOnClickListener(v -> {
                Intent selectIntent = new Intent("ACTION_SEND_USER");
                selectIntent.putExtra("EXTRA_USER_ID", player.getId());
                context.sendBroadcast(selectIntent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public void reset(List<Player> newList){
        playerList = new ArrayList<>();
        playerList.addAll(newList);
        notifyDataSetChanged();
    }


    private class PlayerListHolder extends RecyclerView.ViewHolder {

        private RelativeLayout cardView;
        private CircleImageView profileImageView;
        private TextView profileNameTextView;

        PlayerListHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.rl_search);
            profileImageView = itemView.findViewById(R.id.ci_search_profile);
            profileNameTextView = itemView.findViewById(R.id.tv_search_name);
        }
    }
}
