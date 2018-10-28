package com.dwaynedevelopment.passtimes.navigation.fragments.feed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.adapters.EventFeedViewAdapter;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Player;
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
import com.google.firebase.firestore.ListenerRegistration;
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
    private ImageButton filterImageButton;

    private Map<String, Event> mainFeedEvents = new HashMap<>();
    private Map<String, Event> filteredEventsByCategory = new HashMap<>();
    private List<String> selectedSports = new ArrayList<>();
    private List<String> initialSports = new ArrayList<>();

    private ListenerRegistration eventListenerRegister;
    private ListenerRegistration attendingListenerRegister;

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

        if (getActivity() != null) {
            if (getView() != null) {
                View view = getView();
                if (view != null) {
                    Toolbar feedToolbar = view.findViewById(R.id.tb_feed);
                    feedToolbar.inflateMenu(R.menu.menu_feed);
                    feedToolbar.setOnMenuItemClickListener(menuItemClickListener);

                    progressBar = view.findViewById(R.id.pb_feed);
                    progressBar.setVisibility(View.VISIBLE);

                    filterImageButton = view.findViewById(R.id.iv_filter_btn);
                    filterImageButton.setOnClickListener(filterListener);

                    popupMenu = new PopupMenu(getActivity().getApplicationContext(), filterImageButton, Gravity.BOTTOM);

                    mDb.databaseDocument(DATABASE_REFERENCE_USERS, mAuth.getCurrentSignedUser().getId())
                            .get().addOnCompleteListener(playerFavoritesListener);

                    eventListenerRegister =  mDb.databaseCollection(DATABASE_REFERENCE_EVENTS)
                            .addSnapshotListener(eventSnapshotListener);

                    attendingListenerRegister = mDb.databaseCollection(DATABASE_REFERENCE_USERS).document(mAuth.getCurrentSignedUser().getId())
                            .addSnapshotListener(attendingSnapshotListener);

                    setupInitialRecyclerView(true);

                    eventReceiver = new EventReceiver();
                    IntentFilter actionFilter = new IntentFilter();
                    actionFilter.addAction(ACTION_EVENT_SELECTED);
                    getActivity().registerReceiver(eventReceiver, actionFilter);
                }
            }
        }
    }



    private final EventListener<DocumentSnapshot> attendingSnapshotListener = new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(@javax.annotation.Nullable DocumentSnapshot playerDocumentSnapshot,
                            @javax.annotation.Nullable FirebaseFirestoreException playerException) {

            if (playerDocumentSnapshot != null) {
                final Player attendedPlayer = playerDocumentSnapshot.toObject(Player.class);

                if (attendedPlayer != null) {
                    List<DocumentReference> attendingEventsReference = attendedPlayer.getAttending();
                    if (attendingEventsReference != null) {
                        for (int i = 0; i < attendingEventsReference.size(); i++) {
                            attendingEventsReference.get(i).addSnapshotListener((attendedDocumentSnapshot, attendedException) -> {
                                if (attendedDocumentSnapshot != null) {
                                    final Event attendedEvents = attendedDocumentSnapshot.toObject(Event.class);

                                    if (attendedEvents != null) {
                                        Log.i(TAG, "onEvent: ATTENDING" + attendedEvents.toString());
                                    }


                                }
                            });
                        }
                    }
                }

            }


        }
    };

    private final EventListener<QuerySnapshot> eventSnapshotListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                            @javax.annotation.Nullable FirebaseFirestoreException e) {

            if (e != null) {
                Log.i(TAG, "onEvent: " + e.getLocalizedMessage());
                return;
            }
            if (queryDocumentSnapshots != null) {
                for (int i = 0; i <queryDocumentSnapshots.getDocumentChanges().size() ; i++) {
                    final DocumentChange documentChange = queryDocumentSnapshots.getDocumentChanges().get(i);

                    if (documentChange != null) {
                        switch (documentChange.getType()) {
                            case ADDED:
                                Event addedEvent = documentChange.getDocument().toObject(Event.class);
                                if (!mainFeedEvents.containsKey(addedEvent.getId()) && !filteredEventsByCategory.containsKey(addedEvent.getId())) {
                                    mainFeedEvents.put(addedEvent.getId(), addedEvent);
                                    filteredEventsByCategory.put(addedEvent.getId(), addedEvent);
                                    if (!initialSports.contains(addedEvent.getSport())) {
                                        filteredEventsByCategory.remove(addedEvent.getId());
                                    }
                                    eventFeedViewAdapter.notifyItemInserted(i);
                                    eventFeedViewAdapter.notifyDataSetChanged();
                                    Log.i(TAG, "onEvent: ADDED " + documentChange.getDocument().toObject(Event.class).toString());
                                } else {
                                    Log.i(TAG, "onEvent: NOT ADDED " + documentChange.getDocument().toObject(Event.class).toString());
                                }
                                break;
                            case MODIFIED:
                                final Event editEvent = documentChange.getDocument().toObject(Event.class);
                                if (mainFeedEvents.containsKey(editEvent.getId()) && filteredEventsByCategory.containsKey(editEvent.getId())) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        mainFeedEvents.replace(editEvent.getId(), editEvent);
                                        filteredEventsByCategory.replace(editEvent.getId(), editEvent);
                                        Log.i(TAG, "onEvent: MODIFIED " + documentChange.getDocument().toObject(Event.class).toString());
                                        eventFeedViewAdapter.notifyItemChanged(i);
                                        eventFeedViewAdapter.notifyDataSetChanged();
                                    }
                                }
                                break;
                            case REMOVED:
                                Event removedEvent = documentChange.getDocument().toObject(Event.class);
                                if (mainFeedEvents.containsKey(removedEvent.getId()) && filteredEventsByCategory.containsKey(removedEvent.getId())) {
                                    mainFeedEvents.remove(removedEvent.getId());
                                    filteredEventsByCategory.remove(removedEvent.getId());

                                    eventFeedViewAdapter.notifyItemRemoved(i);
                                    eventFeedViewAdapter.notifyDataSetChanged();
                                    Log.i(TAG, "onEvent: REMOVED " + documentChange.getDocument().toObject(Event.class).toString());
                                }
                                Log.i(TAG, "onEvent: REMOVED " + documentChange.getDocument().toObject(Event.class).toString());
                                break;

                        }
                    }
                }
            }

        }
    };


    private final OnCompleteListener<DocumentSnapshot> playerFavoritesListener = new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> sportsTask) {

            if (sportsTask.isSuccessful()) {
                List<DocumentReference> sportReferences = ((List<DocumentReference>) Objects.requireNonNull(sportsTask.getResult()).getData().get("favorites"));

                if (sportReferences != null) {
                    for (int i = 0; i < sportReferences.size(); i++) {

                        sportReferences.get(i).get().addOnCompleteListener(favoriteSportTask -> {

                            Sport sport = Objects.requireNonNull(favoriteSportTask.getResult()).toObject(Sport.class);
                            popupMenu.getMenuInflater().inflate(R.menu.menu_filter, popupMenu.getMenu());
                            if (sport != null) {
                                popupMenu.getMenu().add(sport.getCategory());
                                initialSports.add(sport.getCategory());
                            }
                        });
                    }
                }

                setupInitialRecyclerView(false);
                progressBar.setVisibility(View.GONE);
            }
        }
    };


    private void setupInitialRecyclerView(boolean initialSetup) {
        filteredEventsByCategory.clear();
        if (getActivity() != null) {
            if (getView() != null) {
                if (initialSetup) {
                    eventFeedViewAdapter = new EventFeedViewAdapter(mainFeedEvents, getActivity().getApplicationContext());

                    eventsRecyclerView = getView().findViewById(R.id.rv_ongoing);
                    eventsRecyclerView.setHasFixedSize(true);
                    eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

                    eventsRecyclerView.setAdapter(eventFeedViewAdapter);
                    eventsRecyclerView.setVisibility(View.GONE);

                } else {
                    new Handler().postDelayed(() -> {
                        filteredEventsByCategory = mDb.filterEventByFavoriteSport(mainFeedEvents, initialSports);

                        eventFeedViewAdapter = new EventFeedViewAdapter(filteredEventsByCategory, getActivity().getApplicationContext());
                        eventsRecyclerView.setAdapter(eventFeedViewAdapter);
                        eventsRecyclerView.setVisibility(View.VISIBLE);

                    }, 350);
                }
            }
        }
    }


    private final View.OnClickListener filterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getActivity() != null) {

                for (int i = 0; i < popupMenu.getMenu().size(); i++) {
                    popupMenu.getMenu().getItem(i).setCheckable(true);
                }

                popupMenu.setOnMenuItemClickListener(menuItem -> {

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
                });
                popupMenu.show();
            }
        }
    };

    private final Toolbar.OnMenuItemClickListener menuItemClickListener = toolBarItem -> {
        if (toolBarItem.getItemId() == R.id.action_add) {
            CreateEventDialogFragment createEventDialog = new CreateEventDialogFragment();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
            createEventDialog.show(fragmentTransaction, CreateEventDialogFragment.TAG);
        }
        return false;
    };

    @Override
    public void onPause() {
        super.onPause();
        if (eventListenerRegister != null && attendingListenerRegister != null) {
            eventListenerRegister.remove();
            eventListenerRegister = null;
            attendingListenerRegister.remove();
            attendingListenerRegister = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        selectedSports.clear();
        initialSports.clear();
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