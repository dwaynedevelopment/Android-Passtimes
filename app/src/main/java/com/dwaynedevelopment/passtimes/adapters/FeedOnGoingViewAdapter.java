package com.dwaynedevelopment.passtimes.adapters;

import android.arch.lifecycle.LifecycleObserver;
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
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class FeedOnGoingViewAdapter  extends FirebaseRecyclerAdapter<Event, FeedOnGoingViewAdapter.OnGoingViewHolder> implements ChangeEventListener, LifecycleObserver {

    private Context context;

    public FeedOnGoingViewAdapter(Context context, FirebaseRecyclerOptions<Event> options) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public FeedOnGoingViewAdapter.OnGoingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ongoing, parent, false);
        return new OnGoingViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull OnGoingViewHolder holder, int position, @NonNull Event event) {

        String month = CalendarUtils.getMonthFromDate(event.getDate());
        holder.tvMonth.setText(month);
        String day = CalendarUtils.getDayFromDate(event.getDate());
        holder.tvDay.setText(day);

        holder.tvSport.setText(event.getSport());
        holder.tvTitle.setText(event.getTitle());
        holder.tvLocation.setText(event.getLocation());

        String time = CalendarUtils.getTimeFromDate(event.getDate());
        holder.tvTime.setText(time);
    }

    @Override
    public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
        super.onChildChanged(type, snapshot, newIndex, oldIndex);

        switch (type) {
            case ADDED:
                notifyItemInserted(newIndex);
                break;
            case CHANGED:
                notifyItemChanged(newIndex);
                break;
            case REMOVED:
                notifyItemRemoved(oldIndex);
                break;
            case MOVED:
                notifyItemMoved(oldIndex, newIndex);
                break;
            default:
                throw new IllegalStateException("Incomplete case statement");
        }
    }

    public class OnGoingViewHolder extends RecyclerView.ViewHolder {

        //private
        private TextView tvMonth;
        private TextView tvDay;
        private TextView tvSport;
        private TextView tvTitle;
        private TextView tvLocation;
        private TextView tvTime;

        OnGoingViewHolder(View itemView) {
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
