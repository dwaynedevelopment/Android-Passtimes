package com.dwaynedevelopment.passtimes.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dwaynedevelopment.passtimes.R;

public class AttendingFeedViewAdapter extends RecyclerView.Adapter<AttendingFeedViewAdapter.ViewHolder> {


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

//        private final CardView eventCard;
//        private final TextView tvMonth;
//        private final TextView tvDay;
//        private final TextView tvSport;
//        private final TextView tvTitle;
//        private final TextView tvLocation;
//        private final TextView tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
//            eventCard = itemView.findViewById(R.id.cv_ongoing);
//            tvMonth = itemView.findViewById(R.id.tv_month);
//            tvDay = itemView.findViewById(R.id.tv_day);
//            tvSport = itemView.findViewById(R.id.tv_sport);
//            tvTitle = itemView.findViewById(R.id.tv_title);
//            tvLocation = itemView.findViewById(R.id.tv_location);
//            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}
