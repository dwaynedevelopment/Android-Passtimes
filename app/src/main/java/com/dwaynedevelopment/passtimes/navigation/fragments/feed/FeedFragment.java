package com.dwaynedevelopment.passtimes.navigation.fragments.feed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
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
import com.dwaynedevelopment.passtimes.adapters.AttendingFeedViewAdapter;
import com.dwaynedevelopment.passtimes.adapters.EventFeedViewAdapter;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.models.Sport;
import com.dwaynedevelopment.passtimes.navigation.fragments.event.CreateEventDialogFragment;
import com.dwaynedevelopment.passtimes.navigation.fragments.event.ViewEventDialogFragment;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.dwaynedevelopment.passtimes.utils.AdapterUtils.adapterViewStatus;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_EVENT_SELECTED;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_EVENTS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_USERS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.EXTRA_SELECTED_EVENT_ID;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_INSERTED_DATA;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_MODIFIED_DATA;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_REMOVED_DATA;

public class FeedFragment extends Fragment {

    private FirebaseFirestoreUtils mDb;
    private AuthUtils mAuth;

    private EventReceiver eventReceiver;
    private EventFeedViewAdapter eventFeedViewAdapter;
    private AttendingFeedViewAdapter attendingFeedViewAdapter;

    private PopupMenu popupMenu;
    private ProgressBar progressBar;

    private Map<String, Event> attendedEventsMap = new HashMap<>();
    private Map<String, Event> mainFeedEvents = new HashMap<>();
    private Map<String, Event> filteredEventsByCategory = new HashMap<>();
    private List<String> selectedSports = new ArrayList<>();
    private List<String> initialSports = new ArrayList<>();

    private Thread eventThreadExecute;
    private Thread favoriteThreadExecute;
    private Thread attendingThreadExecute;

    private ListenerRegistration eventListenerRegister;
    private ListenerRegistration attendingListenerRegister;
    private ListenerRegistration favoritesListenerRegister;

    private static final String TAG = "FeedFragment";

    public FeedFragment() {
        mDb = FirebaseFirestoreUtils.getInstance();
        mAuth = AuthUtils.getInstance();
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
        if (getActivity() != null) {
            if (getView() != null) {
                View view = getView();
                if (view != null) {
                    Toolbar feedToolbar = view.findViewById(R.id.tb_feed);
                    feedToolbar.inflateMenu(R.menu.menu_feed);
                    feedToolbar.setOnMenuItemClickListener(menuItemClickListener);

//                    progressBar = view.findViewById(R.id.pb_feed);
//                    progressBar.setVisibility(View.VISIBLE);

                    ImageButton filterImageButton = view.findViewById(R.id.iv_filter_btn);
                    filterImageButton.setOnClickListener(filterListener);

                    popupMenu = new PopupMenu(getActivity().getApplicationContext(), filterImageButton, Gravity.BOTTOM);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_filter, popupMenu.getMenu());
                }
            }


            //THREAD: FAVORITES FETCH
            favoriteThreadExecute = new Thread() {
                @Override
                public void run() {
                    favoritesListenerRegister = mDb.databaseCollection(DATABASE_REFERENCE_USERS)
                            .document(mAuth.getCurrentSignedUser().getId())
                            .addSnapshotListener(playerFavoritesListener);

                }
            };

            //THREAD: FAVORITE EXECUTE
            if (favoriteThreadExecute.getState().equals(Thread.State.NEW)) {
                favoriteThreadExecute.start();
            }

            //THREAD: EVENT FETCH
            eventThreadExecute = new Thread() {
                @Override
                public void run() {
                    eventListenerRegister = mDb.databaseCollection(DATABASE_REFERENCE_EVENTS)
                            .addSnapshotListener(eventSnapshotListener);
                    getActivity().runOnUiThread(() -> {
                        setUpOngoingRecyclerView(mainFeedEvents);
                    });
                }
            };

            //THREAD: ATTENDING FETCH
            attendingThreadExecute = new Thread() {
                @Override
                public void run() {
                    attendingListenerRegister = mDb.databaseCollection(DATABASE_REFERENCE_USERS)
                            .document(mAuth.getCurrentSignedUser().getId())
                            .addSnapshotListener(attendingSnapshotListener);
                    getActivity().runOnUiThread(() -> {
                        setUpAttendingRecyclerView();
                    });
                }
            };
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    private final EventListener<DocumentSnapshot> playerFavoritesListener = new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(@javax.annotation.Nullable DocumentSnapshot playerDocumentSnapshot,
                            @javax.annotation.Nullable FirebaseFirestoreException e) {

            if (playerDocumentSnapshot != null) {
                final Player playerForFavorites = playerDocumentSnapshot.toObject(Player.class);

                if (playerForFavorites != null) {
                    List<DocumentReference> favoritesReference = playerForFavorites.getFavorites();
                    if (favoritesReference != null) {
                        for (int i = 0; i < favoritesReference.size(); i++) {
                            favoritesReference.get(i).addSnapshotListener((DocumentSnapshot favoritesDocumentSnapshot,
                                                                           FirebaseFirestoreException attendedException) -> {
                                if (favoritesDocumentSnapshot != null) {
                                    final Sport favoriteSport = favoritesDocumentSnapshot.toObject(Sport.class);
                                    if (favoriteSport != null) {
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(() -> {
                                                if (!initialSports.contains(favoriteSport.getCategory())) {
                                                    popupMenu.getMenu().add(favoriteSport.getCategory());
                                                    initialSports.add(favoriteSport.getCategory());
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    }
                    //THREAD: EVENT / ATTENDING EXECUTE
                    if (!favoriteThreadExecute.isAlive()) {
                        if (attendingThreadExecute.getState().equals(Thread.State.NEW)) {
                            if (eventThreadExecute.getState().equals(Thread.State.NEW)) {
                                attendingThreadExecute.start();
                                eventThreadExecute.start();
                            }
                        }
                    }
                }
            }
        }
    };

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
                            final int index = i;
                            attendingEventsReference.get(i).addSnapshotListener((DocumentSnapshot attendedDocumentSnapshot,
                                                                                 FirebaseFirestoreException attendedException) -> {
                                if (attendedDocumentSnapshot != null) {
                                    if (attendedDocumentSnapshot.exists()) {
                                        final Event attendedEvents = attendedDocumentSnapshot.toObject(Event.class);
                                        if (attendedEvents != null) {
                                            if (!attendedEventsMap.containsKey(attendedEvents.getId())) {
                                                if (getActivity() != null) {
                                                    //THREAD: INSERTED ATTENDING
                                                    getActivity().runOnUiThread(() -> {
                                                        attendedEventsMap.put(attendedEvents.getId(), attendedEvents);
                                                        adapterViewStatus(attendingFeedViewAdapter, NOTIFY_INSERTED_DATA, index);
                                                    });
                                                }
                                            } else {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                    if (getActivity() != null) {
                                                        //THREAD: MODIFIED ATTENDING
                                                        getActivity().runOnUiThread(() -> {
                                                            attendedEventsMap.replace(attendedEvents.getId(), attendedEvents);
                                                            adapterViewStatus(attendingFeedViewAdapter, NOTIFY_MODIFIED_DATA, index);
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        DocumentReference documentReference = mDb.databaseCollection(DATABASE_REFERENCE_USERS)
                                                .document(mAuth.getCurrentSignedUser().getId());

                                        final DocumentReference eventRemoveDocument = mDb.getFirestore()
                                                .document("/" + DATABASE_REFERENCE_EVENTS + "/" + attendedDocumentSnapshot.getId());
                                        documentReference.update("attending", FieldValue.arrayRemove(eventRemoveDocument));

                                        if (getActivity() != null) {
                                            //THREAD: REMOVED ATTENDING
                                            getActivity().runOnUiThread(() -> {
                                                attendedEventsMap.remove(attendedDocumentSnapshot.getId());
                                                adapterViewStatus(attendingFeedViewAdapter, NOTIFY_REMOVED_DATA, index);
                                            });
                                        }
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
                Log.i(TAG, "eventSnapshotListener: " + e.getLocalizedMessage());
                return;
            }

            if (queryDocumentSnapshots != null) {
                for (int i = 0; i < queryDocumentSnapshots.getDocumentChanges().size(); i++) {
                    final DocumentChange documentChange = queryDocumentSnapshots.getDocumentChanges().get(i);
                    if (documentChange != null) {
                        switch (documentChange.getType()) {
                            case ADDED:
                                if (queryDocumentSnapshots.getDocuments().get(i) != null) {
                                    if (queryDocumentSnapshots.getDocuments().get(i).exists()) {
                                        Event addedEvent = documentChange.getDocument().toObject(Event.class);
                                        if (!mainFeedEvents.containsKey(addedEvent.getId()) && !filteredEventsByCategory.containsKey(addedEvent.getId())) {
                                            if (initialSports.contains(addedEvent.getSport())) {
                                                final int index = i;
                                                if (getActivity() != null) {
                                                    //THREAD: ADDED EVENT
                                                    getActivity().runOnUiThread(() -> {
                                                        mainFeedEvents.put(addedEvent.getId(), addedEvent);
                                                        filteredEventsByCategory.put(addedEvent.getId(), addedEvent);
                                                        adapterViewStatus(eventFeedViewAdapter, NOTIFY_INSERTED_DATA, index);
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            case MODIFIED:
                                if (queryDocumentSnapshots.getDocuments().get(i) != null) {
                                    if (queryDocumentSnapshots.getDocuments().get(i).exists()) {
                                        final Event editEvent = documentChange.getDocument().toObject(Event.class);
                                        if (mainFeedEvents.containsKey(editEvent.getId()) && filteredEventsByCategory.containsKey(editEvent.getId())) {
                                            if (initialSports.contains(editEvent.getSport())) {
                                                final int index = i;
                                                if (getActivity() != null) {
                                                    //THREAD: MODIFIED EVENT
                                                    getActivity().runOnUiThread(() -> {
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                            mainFeedEvents.replace(editEvent.getId(), editEvent);
                                                            filteredEventsByCategory.replace(editEvent.getId(), editEvent);
                                                            adapterViewStatus(eventFeedViewAdapter, NOTIFY_MODIFIED_DATA, index);
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            case REMOVED:
                                if (queryDocumentSnapshots.getDocuments().get(i) != null) {
                                    if (queryDocumentSnapshots.getDocuments().get(i).exists()) {
                                        Event removedEvent = documentChange.getDocument().toObject(Event.class);
                                        if (mainFeedEvents.containsKey(removedEvent.getId()) && filteredEventsByCategory.containsKey(removedEvent.getId())) {
                                            if (initialSports.contains(removedEvent.getSport())) {
                                                final int index = i;
                                                if (getActivity() != null) {
                                                    //THREAD: REMOVED EVENT
                                                    getActivity().runOnUiThread(() -> {
                                                        mainFeedEvents.remove(removedEvent.getId());
                                                        filteredEventsByCategory.remove(removedEvent.getId());
                                                        adapterViewStatus(eventFeedViewAdapter, NOTIFY_REMOVED_DATA, index);
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        }
    };

    private void setUpOngoingRecyclerView(Map<String, Event> eventsHashMap) {
        if (getActivity() != null) {
            if (getView() != null) {
                eventFeedViewAdapter = new EventFeedViewAdapter(eventsHashMap, getActivity().getApplicationContext());
                RecyclerView eventsRecyclerView = getView().findViewById(R.id.rv_ongoing);
                eventsRecyclerView.setHasFixedSize(true);
                eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),
                        LinearLayoutManager.VERTICAL, false));
                eventsRecyclerView.setAdapter(eventFeedViewAdapter);
                Log.i(TAG, "setUpOngoingRecyclerView: ONGOING");
            }
        }
    }

    private void setUpAttendingRecyclerView() {
        if (getActivity() != null) {
            if (getView() != null) {
                attendingFeedViewAdapter = new AttendingFeedViewAdapter(attendedEventsMap, getActivity().getApplicationContext());
                RecyclerView attendedRecyclerView = getView().findViewById(R.id.rv_attending);
                attendedRecyclerView.setHasFixedSize(true);
                attendedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),
                        LinearLayoutManager.HORIZONTAL, false));
                attendedRecyclerView.setAdapter(attendingFeedViewAdapter);
                Log.i(TAG, "setUpAttendingRecyclerView: ATTENDING");
            }
        }
    }

    private void registerBroadcastReceiver() {
        eventReceiver = new EventReceiver();
        IntentFilter actionFilter = new IntentFilter();
        actionFilter.addAction(ACTION_EVENT_SELECTED);
        if (getActivity() != null) {
            getActivity().registerReceiver(eventReceiver, actionFilter);
        }
    }

    private void unregisterBroadcastReceiver() {
        if (getActivity() != null) {
            getActivity().unregisterReceiver(eventReceiver);
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
                        getActivity().runOnUiThread(() -> {
                            setUpOngoingRecyclerView(filteredEventsByCategory);
                        });
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
        if (eventListenerRegister != null && attendingListenerRegister != null && favoritesListenerRegister != null) {
            eventListenerRegister.remove();
            eventListenerRegister = null;
            attendingListenerRegister.remove();
            attendingListenerRegister = null;
            favoritesListenerRegister.remove();
            favoritesListenerRegister = null;

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        selectedSports.clear();
        initialSports.clear();
        attendedEventsMap.clear();
        mainFeedEvents.clear();
        filteredEventsByCategory.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceiver();
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