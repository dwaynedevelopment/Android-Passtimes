package com.dwaynedevelopment.passtimes.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.models.Sport;

import java.util.ArrayList;

public class SportsViewAdapter extends RecyclerView.Adapter<SportsViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Sport> sportsArray;

    public SportsViewAdapter(Context context, ArrayList<Sport> sportsArray) {
        this.context = context;
        this.sportsArray = sportsArray;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_sport, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Sport sport = sportsArray.get(i);

        //Glide.with(context).load(sport.getUrl()).into(viewHolder.realButton);
        viewHolder.sportButton.setText(sport.getCategory());
    }

    @Override
    public int getItemCount() {
        return sportsArray.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private Button sportButton;

        ViewHolder(View itemView) {
            super(itemView);
            sportButton = itemView.findViewById(R.id.sport_button);
        }
    }
}
