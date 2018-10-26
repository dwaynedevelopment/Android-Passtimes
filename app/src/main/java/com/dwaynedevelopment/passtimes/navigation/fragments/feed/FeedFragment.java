package com.dwaynedevelopment.passtimes.navigation.fragments.feed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.adapters.EventFeedViewAdapter;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Sport;
import com.dwaynedevelopment.passtimes.navigation.fragments.event.CreateEventDialogFragment;
import com.dwaynedevelopment.passtimes.navigation.fragments.event.ViewEventDialogFragment;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_EVENT_SELECTED;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_EVENTS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_USERS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.EXTRA_SELECTED_EVENT_ID;

public class FeedFragment extends Fragment {

    private FirebaseFirestoreUtils mDb;
    private AuthUtils mAuth;
    private RecyclerView eventsRecyclerView;
    private EventFeedViewAdapter eventFeedViewAdapter;

    private EventReceiver eventReceiver;
    private PopupMenu popupMenu;
    private ProgressBar progressBar;

    private List<Sport> menuSports = new ArrayList<>();
    private Map<String, Event> mainFeedEvents = new HashMap<>();
    private Map<String, Event> filteredEventsByCategory = new HashMap<>();
    private List<String> selectedSports = new ArrayList<>();
    private List<String> initialSports = new ArrayList<>();

    private static final String TAG = "FeedFragment";

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

        if (view != null) {
            Toolbar feedToolbar = view.findViewById(R.id.tb_feed);
            feedToolbar.inflateMenu(R.menu.menu_feed);
            feedToolbar.setOnMenuItemClickListener(menuItemClickListener);

            progressBar = view.findViewById(R.id.pb_feed);
            progressBar.setVisibility(View.VISIBLE);

            eventReceiver = new EventReceiver();
            IntentFilter actionFilter = new IntentFilter();
            actionFilter.addAction(ACTION_EVENT_SELECTED);
            Objects.requireNonNull(getActivity()).registerReceiver(eventReceiver, actionFilter);

            ImageButton filterImageButton = view.findViewById(R.id.iv_filter_btn);
            filterImageButton.setOnClickListener(filterListener);

            popupMenu = new PopupMenu(getActivity().getApplicationContext(), filterImageButton, Gravity.BOTTOM);

            mDb.databaseDocument(DATABASE_REFERENCE_USERS, mAuth.getCurrentSignedUser().getId())
                    .get().addOnCompleteListener(playerCompleteListener);

            mDb.databaseCollection(DATABASE_REFERENCE_EVENTS)
                    .addSnapshotListener(eventSnapshotListener);

            setupInitialRecyclerView(true);


        }
    }

    private final EventListener<QuerySnapshot> eventSnapshotListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                            @javax.annotation.Nullable FirebaseFirestoreException e) {

            if (e != null) {
                Log.i(TAG, "onEvent: " + e.getLocalizedMessage());
                return;
            }
            if (queryDocumentSnapshots != null) {
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {

                    if (documentChange != null) {
                        switch (documentChange.getType()) {
                            case ADDED:
                                Event addedEvent = documentChange.getDocument().toObject(Event.class);
                                if (!mainFeedEvents.containsKey(addedEvent.getId())) {
                                    mainFeedEvents.put(addedEvent.getId(), addedEvent);
                                    Log.i(TAG, "onEvent: ADDED " + documentChange.getDocument().toObject(Event.class).toString());

                                } else {
                                    Log.i(TAG, "onEvent: NOT ADDED " + documentChange.getDocument().toObject(Event.class).toString());
                                }
                                eventFeedViewAdapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                Log.i(TAG, "onEvent: MODIFIED " + documentChange.getDocument().toObject(Event.class).toString());
                                break;
                            case REMOVED:
                                Log.i(TAG, "onEvent: REMOVED " + documentChange.getDocument().toObject(Event.class).toString());
                                break;

                        }
                    }
                }
            }

        }
    };


    private final OnCompleteListener<DocumentSnapshot> playerCompleteListener = new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

            if (task.isSuccessful()) {
                List<DocumentReference> sportReferences = ((List<DocumentReference>) Objects.requireNonNull(task.getResult()).getData().get("favorites"));

                if (sportReferences != null) {
                    for (int i = 0; i < sportReferences.size(); i++) {

                        sportReferences.get(i).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                Sport sport = Objects.requireNonNull(task.getResult()).toObject(Sport.class);
                                popupMenu.getMenuInflater().inflate(R.menu.menu_filter, popupMenu.getMenu());
                                if (sport != null) {
                                    popupMenu.getMenu().add(sport.getCategory());
                                    menuSports.add(sport);
                                    initialSports.add(sport.getCategory());
                                }
                            }
                        });
                    }

                    for (int i = 0; i < popupMenu.getMenu().size(); i++) {
                        popupMenu.getMenu().getItem(i).setChecked(true);
                    }
                }

                setupInitialRecyclerView(false);
                progressBar.setVisibility(View.GONE);
            }
        }
    };

    private void setupInitialRecyclerView(boolean initialSetup) {

        if (initialSetup) {


            eventFeedViewAdapter = new EventFeedViewAdapter(mainFeedEvents, getActivity().getApplicationContext());

            eventsRecyclerView = getView().findViewById(R.id.rv_ongoing);
            eventsRecyclerView.setHasFixedSize(true);
            eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

            eventsRecyclerView.setAdapter(eventFeedViewAdapter);
            eventsRecyclerView.setVisibility(View.GONE);

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    filteredEventsByCategory = mDb.filterEventByFavoriteSport(mainFeedEvents, initialSports);

                    eventFeedViewAdapter = new EventFeedViewAdapter(filteredEventsByCategory, getActivity().getApplicationContext());
                    eventsRecyclerView.setAdapter(eventFeedViewAdapter);
                    eventsRecyclerView.setVisibility(View.VISIBLE);

                }
            }, 350);
        }

    }


    private final View.OnClickListener filterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getActivity() != null) {

                for (int i = 0; i < popupMenu.getMenu().size(); i++) {
                    popupMenu.getMenu().getItem(i).setCheckable(true);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (!menuItem.isChecked()) {
                            menuItem.setChecked(true);
                            if (!selectedSports.contains(menuItem.getTitle().toString())) {
                                selectedSports.add(menuItem.getTitle().toString());
                                filteredEventsByCategory = mDb.filterEventByFavoriteSport(mainFeedEvents, selectedSports);
                            }
                        } else {

                            if (selectedSports.size() < 1) {
                                menuItem.setChecked(true);
                                return true;
                            } else {
                                menuItem.setChecked(false);
                                selectedSports.remove(menuItem.getTitle().toString());
                                filteredEventsByCategory = mDb.filterEventByFavoriteSport(mainFeedEvents, selectedSports);
                            }

                        }

                        if (filteredEventsByCategory != null) {
                            eventFeedViewAdapter = new EventFeedViewAdapter(filteredEventsByCategory, getActivity().getApplicationContext());
                            if (getView() != null) {
                                eventsRecyclerView = getView().findViewById(R.id.rv_ongoing);
                                eventsRecyclerView.setHasFixedSize(true);
                                eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                                eventsRecyclerView.setAdapter(eventFeedViewAdapter);
                            }
                        } else {
                            menuItem.setChecked(true);
                            if (!selectedSports.contains(menuItem.getTitle().toString())) {
                                selectedSports.add(menuItem.getTitle().toString());
                                filteredEventsByCategory = mDb.filterEventByFavoriteSport(mainFeedEvents, selectedSports);
                            }
                        }

                        return true;
                    }
                });
                popupMenu.show();
            }
        }
    };

    private final Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.action_add) {
                CreateEventDialogFragment createEventDialog = new CreateEventDialogFragment();
                FragmentTransaction fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                createEventDialog.show(fragmentTransaction, CreateEventDialogFragment.TAG);
            }
            return false;
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        menuSports.clear();
        selectedSports.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(eventReceiver);
        }
    }

    public class EventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String receivedEventId = intent.getStringExtra(EXTRA_SELECTED_EVENT_ID);
            ViewEventDialogFragment viewEventDialogFragment = ViewEventDialogFragment.newInstance(receivedEventId);
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
            viewEventDialogFragment.show(fragmentTransaction, ViewEventDialogFragment.TAG);
        }
    }
}
