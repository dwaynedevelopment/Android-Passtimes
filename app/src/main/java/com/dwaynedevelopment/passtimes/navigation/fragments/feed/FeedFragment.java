package com.dwaynedevelopment.passtimes.navigation.fragments.feed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.adapters.FeedOnGoingViewAdapter;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.navigation.fragments.event.CreateEventDialogFragment;
import com.dwaynedevelopment.passtimes.navigation.fragments.event.ViewEventDialogFragment;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.DatabaseUtils;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_EVENT_SELECTED;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_EVENTS;

public class FeedFragment extends Fragment {

    FirebaseFirestoreUtils mDb;
    AuthUtils mAuth;
    FeedOnGoingViewAdapter mAdapter;
    EventReceiver eventReceiver;

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
        mDb = FirebaseFirestoreUtils.getInstance();
        mAuth = AuthUtils.getInstance();
        View view = getView();

        if(view != null) {
            Toolbar feedToolbar = view.findViewById(R.id.tb_feed);
            feedToolbar.inflateMenu(R.menu.menu_feed);
            feedToolbar.setOnMenuItemClickListener(menuItemClickListener);

            eventReceiver = new EventReceiver();
            IntentFilter actionFilter = new IntentFilter();
            actionFilter.addAction(ACTION_EVENT_SELECTED);
            getActivity().registerReceiver(eventReceiver, actionFilter);



            Player player = mAuth.getCurrentSignedUser();
            //ArrayList<String> favorites = player.getListOfFavoriteSports();
            //Log.i("TAG", "onActivityCreated: PLAYER FAVORITES SIZE " + favorites.size());

            FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                    .setQuery(mDb.databaseCollection(DATABASE_REFERENCE_EVENTS), Event.class)
                    .build();


            mAdapter = new FeedOnGoingViewAdapter(options, getContext());

            RecyclerView recyclerView = getView().findViewById(R.id.rv_ongoing);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(mAdapter);
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

    @Override
    public void onStart() {
        super.onStart();
        if(mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(eventReceiver);
        }
    }

    private static final String TAG = "FeedFragment";
    public class EventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Event event = intent.getParcelableExtra("SELECTED_EVENT");
            
            ViewEventDialogFragment viewEventDialogFragment = ViewEventDialogFragment.newInstance(event);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            viewEventDialogFragment.show(fragmentTransaction, ViewEventDialogFragment.TAG);
        }
    }
}
