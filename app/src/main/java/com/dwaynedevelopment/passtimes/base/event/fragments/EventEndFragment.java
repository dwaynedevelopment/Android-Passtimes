package com.dwaynedevelopment.passtimes.base.event.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.base.event.adapters.AttendeesViewAdapter;
import com.dwaynedevelopment.passtimes.base.event.adapters.ManagerViewAdapter;
import com.dwaynedevelopment.passtimes.base.event.interfaces.IEventHandler;
import com.dwaynedevelopment.passtimes.base.favorites.fragments.FavoriteFragment;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.models.Sport;
import com.dwaynedevelopment.passtimes.utils.AdapterUtils;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.CalendarUtils;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


import static com.dwaynedevelopment.passtimes.utils.AdapterUtils.adapterViewStatus;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_FAVORITE_SELECTED;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_EVENTS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_SPORTS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_INSERTED_DATA;

public class EventEndFragment extends Fragment {

    private ManagerReceiver managerReceiver;
    private FirebaseFirestoreUtils mDb;
    private AuthUtils mAuth;
    private IEventHandler iEventHandler;
    private String eventIdExtra;
    private Map<String, Player> attendeesList = new HashMap<>();
    private ManagerViewAdapter attendeeFeedViewAdapter;
    private Event eventSelected;
    private final List<Player> playerReferences = new ArrayList<>();

    public static EventEndFragment newInstance(String eventId) {

        Bundle args = new Bundle();
        //TODO: Double Check
        args.putString("ARGS_EVENT_END_ID", eventId);
        EventEndFragment fragment = new EventEndFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IEventHandler) {
            iEventHandler = (IEventHandler) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_end, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            if (getView() != null) {
                mDb = FirebaseFirestoreUtils.getInstance();
                mAuth = AuthUtils.getInstance();

                Toolbar feedToolbar = getView().findViewById(R.id.tb_feed);
                feedToolbar.inflateMenu(R.menu.menu_manager);
                feedToolbar.setOnMenuItemClickListener(menuItemClickListener);

                managerReceiver = new ManagerReceiver();
                IntentFilter actionFilter = new IntentFilter();
                actionFilter.addAction("ACTION_ATTENDEE_SELECTED");
                getActivity().registerReceiver(managerReceiver, actionFilter);

                if (getArguments() != null) {

                    eventIdExtra = getArguments().getString("ARGS_EVENT_END_ID");
                    if (eventIdExtra != null) {

                        if (!eventIdExtra.isEmpty()) {
                            mDb.databaseCollection(DATABASE_REFERENCE_EVENTS).document(eventIdExtra)
                                    .addSnapshotListener(eventSnapshotListener);


                            attendeeFeedViewAdapter = new ManagerViewAdapter(attendeesList, getActivity().getApplicationContext());
                            RecyclerView attendeeRecyclerView = getView().findViewById(R.id.rv_manager);
                            attendeeRecyclerView.setHasFixedSize(true);
                            attendeeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),
                                    LinearLayoutManager.VERTICAL, false));

                            attendeeRecyclerView.setAdapter(attendeeFeedViewAdapter);
                            attendeeFeedViewAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    }

    private final Toolbar.OnMenuItemClickListener menuItemClickListener = toolBarItem -> {
        if (toolBarItem.getItemId() == R.id.action_manager) {
                if (getActivity() != null) {
                    if (playerReferences.size() > 1) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle(mAuth.getCurrentSignedUser().getName());
                        alertDialog.setMessage("Is this selection correct?");

                        alertDialog.setPositiveButton("Yes", (dialog, which) -> {
                            for (int i = 0; i < playerReferences.size(); i++) {
                                int xp = playerReferences.get(i).getOverallXP();
                                playerReferences.get(i).setOverallXP(xp + 50);
                                mDb.updateOverall(playerReferences.get(i));
                            }

                            eventSelected.setIsClosed(true);
                            mDb.updateEvenStatus(eventSelected);

                            new Handler().postDelayed(() -> {
                                iEventHandler.dismissDetailView();
                            }, 550);
                        });

                        alertDialog.setNegativeButton("Cancel", (dialog, which) ->
                                dialog.cancel());

                        alertDialog.show();

                    }
                }
                }

        return false;
    };

    private final EventListener<DocumentSnapshot> eventSnapshotListener = (DocumentSnapshot documentParentSnapshot,
                                                                           FirebaseFirestoreException eventException) -> {

        eventSelected = Objects.requireNonNull(documentParentSnapshot).toObject(Event.class);

        if (eventSelected != null) {
            if (getView() != null) {

                List<DocumentReference> attendeesReference = eventSelected.getAttendees();
                //TODO: FIX FOR LIVE IMPLEMENTATION:
                if (attendeesReference != null) {
                    for (int i = 0; i < attendeesReference.size(); i++) {
                        int finalI = i;
                        attendeesReference.get(i).addSnapshotListener((documentSnapshot, attendeeException) -> {
                            if (documentSnapshot != null) {
                                if (documentSnapshot.exists()) {
                                    final Player attendeeReference = documentSnapshot.toObject(Player.class);
                                    if (attendeeReference != null) {
                                        if (!attendeesList.containsKey(attendeeReference.getId())) {
                                            attendeesList.put(attendeeReference.getId(), attendeeReference);

                                            if (getActivity() != null) {
                                                getActivity().runOnUiThread(() -> {
                                                    adapterViewStatus(attendeeFeedViewAdapter, NOTIFY_INSERTED_DATA, finalI);
                                                });
                                            }

                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(managerReceiver);
        }
    }

    private static final String TAG = "EventEndFragment";
    public class ManagerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Player> selectedPlayers = intent.getParcelableArrayListExtra("SELECTED_ATTENDEES");

            //CLEAR LIST TO AVOID DUPLICATES ENTRIES.

            playerReferences.clear();
            for (int i = 0; i < selectedPlayers.size() ; i++) {
                playerReferences.add(selectedPlayers.get(i));
                Log.i(TAG, "onReceive: " + selectedPlayers.get(i));
            }

        }
    }
}
