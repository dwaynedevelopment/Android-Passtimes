package com.dwaynedevelopment.passtimes.navigation.fragments.feed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.navigation.fragments.event.CreateEventDialogFragment;

import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;

public class FeedFragment extends Fragment {

    public FeedFragment() {
    }

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        if(view != null) {
            Toolbar feedToolbar = view.findViewById(R.id.tb_feed);
            feedToolbar.inflateMenu(R.menu.menu_feed);
            feedToolbar.setOnMenuItemClickListener(menuItemClickListener);
        }

    }

    Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(item.getItemId() == R.id.action_add) {
                CreateEventDialogFragment createEventDialog = new CreateEventDialogFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                createEventDialog.show(fragmentTransaction, CreateEventDialogFragment.TAG);
            }
            return false;
        }
    };
}
