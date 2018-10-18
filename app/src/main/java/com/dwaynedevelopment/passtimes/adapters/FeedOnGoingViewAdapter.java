package com.dwaynedevelopment.passtimes.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.utils.CalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class FeedOnGoingViewAdapter  extends RecyclerView.Adapter<FeedOnGoingViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Event> eventsArray;

    public FeedOnGoingViewAdapter(Context context, ArrayList<Event> eventsArray) {
        this.context = context;
        this.eventsArray= eventsArray;
    }

    @NonNull
    @Override
    public FeedOnGoingViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ongoing, parent, false);
        return new FeedOnGoingViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Event event = eventsArray.get(i);

        String month = CalendarUtils.getMonthFromDate(event.getDate());
        viewHolder.tvMonth.setText(month);
        String day = CalendarUtils.getDayFromDate(event.getDate());
        viewHolder.tvDay.setText(day);

        viewHolder.tvSport.setText(event.getSport());
        viewHolder.tvTitle.setText(event.getTitle());
        viewHolder.tvLocation.setText(event.getLocation());

        String time = CalendarUtils.getTimeFromDate(event.getDate());
        viewHolder.tvTime.setText(time);
    }


    @Override
    public int getItemCount() {
        return eventsArray.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        //private
        private TextView tvMonth;
        private TextView tvDay;
        private TextView tvSport;
        private TextView tvTitle;
        private TextView tvLocation;
        private TextView tvTime;

        ViewHolder(View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvSport = itemView.findViewById(R.id.tv_sport);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}
