package com.dwaynedevelopment.passtimes.adapters;

import android.arch.lifecycle.LifecycleObserver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.navigation.fragments.event.CreateEventDialogFragment;
import com.dwaynedevelopment.passtimes.utils.CalendarUtils;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;


import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_EVENT_SELECTED;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_FAVORITE_SELECTED;

public class FeedOnGoingViewAdapter extends FirebaseRecyclerAdapter<Event, FeedOnGoingViewAdapter.OnGoingViewHolder> implements ChangeEventListener, LifecycleObserver {

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
    protected void onBindViewHolder(@NonNull OnGoingViewHolder holder, int position, @NonNull final Event event) {

        String month = CalendarUtils.getMonthFromDate(event.getStartDate());
        holder.tvMonth.setText(month);
        String day = CalendarUtils.getDayFromDate(event.getStartDate());
        holder.tvDay.setText(day);

        holder.tvSport.setText(event.getSport());
        holder.tvTitle.setText(event.getTitle());
        holder.tvLocation.setText(event.getLocation());

        String time = CalendarUtils.getDateTimeFromDate(event.getStartDate());
        holder.tvTime.setText(time);

        holder.eventCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectIntent = new Intent(ACTION_EVENT_SELECTED);
                selectIntent.putExtra("SELECTED_EVENT",  event);
                context.sendBroadcast(selectIntent);
            }
        });
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
        private CardView eventCard;
        private TextView tvMonth;
        private TextView tvDay;
        private TextView tvSport;
        private TextView tvTitle;
        private TextView tvLocation;
        private TextView tvTime;

        OnGoingViewHolder(View itemView) {
            super(itemView);
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
