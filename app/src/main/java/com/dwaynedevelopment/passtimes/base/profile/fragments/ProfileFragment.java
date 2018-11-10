package com.dwaynedevelopment.passtimes.base.profile.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.base.feed.adapters.AttendingFeedViewAdapter;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.parent.interfaces.INavigationHandler;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.dwaynedevelopment.passtimes.utils.AdapterUtils.adapterViewStatus;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_EVENT_SELECTED;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_EVENTS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_USERS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_INSERTED_DATA;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_MODIFIED_DATA;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_REMOVED_DATA;

public class ProfileFragment extends Fragment {

    private FirebaseFirestoreUtils mDb;
    private AuthUtils mAuth;

    private EventReceiver eventReceiver;

    private INavigationHandler iNavigationHandler;
    private ListenerRegistration attendingListenerRegister;
    private Thread attendingThreadExecute;
    private RecyclerView attendedRecyclerView;
    private LinearLayout attendedEmptyStub;
    private AttendingFeedViewAdapter attendingFeedViewAdapter;
    private Map<String, Event> attendedEventsMap = new HashMap<>();

    public ProfileFragment() { }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof INavigationHandler) {
            iNavigationHandler = (INavigationHandler) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            mAuth = AuthUtils.getInstance();
            mDb = FirebaseFirestoreUtils.getInstance();
            AppCompatActivity activity = (AppCompatActivity) getActivity();


            if (getView() != null) {

                View view = getView();

               // attendedEmptyStub = view.findViewById(R.id.rv_attendeed_empty);
                Toolbar feedToolbar = view.findViewById(R.id.tb_profile);
                feedToolbar.inflateMenu(R.menu.menu_profile);
                feedToolbar.setOnMenuItemClickListener(menuItemClickListener);

                CircleImageView profileImage = view.findViewById(R.id.ci_profile);
                Glide.with(activity).load(mAuth.getCurrentSignedUser().getThumbnail()).into(profileImage);
                TextView profileName = view.findViewById(R.id.tv_profile_name);
                profileName.setText(mAuth.getCurrentSignedUser().getName());

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

                //THREAD: ATTENDING EXECUTE
                if (attendingThreadExecute.getState().equals(Thread.State.NEW)) {
                    attendingThreadExecute.start();
                }
            }
        }
    }

    private void setUpAttendingRecyclerView() {
        if (getActivity() != null) {
            if (getView() != null) {
                attendingFeedViewAdapter = new AttendingFeedViewAdapter(attendedEventsMap, getActivity().getApplicationContext());
                attendedRecyclerView = getView().findViewById(R.id.rv_attending_profile);
                attendedRecyclerView.setHasFixedSize(true);
                attendedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),
                        LinearLayoutManager.HORIZONTAL, false));
                attendedRecyclerView.setAdapter(attendingFeedViewAdapter);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (attendingListenerRegister != null) {
            attendingListenerRegister.remove();
            attendingListenerRegister = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        attendedEventsMap.clear();
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
                            final int index = i;
                            attendingEventsReference.get(i).addSnapshotListener((DocumentSnapshot attendedDocumentSnapshot, FirebaseFirestoreException attendedException) -> {
                                if (attendedDocumentSnapshot != null) {
                                    if (attendedDocumentSnapshot.exists()) {
                                        final Event attendedEvents = attendedDocumentSnapshot.toObject(Event.class);
                                        if (attendedEvents != null) {
                                            if (!attendedEventsMap.containsKey(attendedEvents.getId())) {
                                                if (getActivity() != null) {
                                                    getActivity().runOnUiThread(() -> {
                                                        attendedEventsMap.put(attendedEvents.getId(), attendedEvents);
                                                        adapterViewStatus(attendingFeedViewAdapter, NOTIFY_INSERTED_DATA, index);
                                                        if (!attendedEventsMap.isEmpty()) {
//                                                            attendedEmptyStub.setVisibility(View.GONE);
                                                        }
                                                    });

                                                }
                                            } else {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                    if (getActivity() != null) {
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
                                            //THREAD: REMOVED EVENT
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
                if (getActivity() != null) {
                    //THREAD: REMOVED ATTENDING
                    getActivity().runOnUiThread(() -> {
                        if (attendedEventsMap.isEmpty()) {
//                            attendedEmptyStub.setVisibility(View.VISIBLE);
                        } else {
//                            attendedEmptyStub.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }
    };

    private final Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int itemId = item.getItemId();
            final int settings = R.id.action_settings;
            final int edit = R.id.action_edit;
            final int favorites = R.id.action_favorite;
            switch (itemId) {
                case settings:
                    if (iNavigationHandler != null) {
                        iNavigationHandler.invokeSettings();
                    }
                    break;
                case edit:
                    if (iNavigationHandler != null) {
                        iNavigationHandler.invokeEditProfile();
                    }
                    break;
                case favorites:
                    if (iNavigationHandler != null) {
                        iNavigationHandler.invokeFavorites();
                    }
                    break;
            }
            return false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        eventReceiver = new ProfileFragment.EventReceiver();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceiver();
    }

    public class EventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String receivedEventId = intent.getStringExtra(EXTRA_SELECTED_EVENT_ID);
//            EventDetailFragment viewEventDialogFragment = EventDetailFragment.newInstance(receivedEventId);
//            FragmentTransaction fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
//            viewEventDialogFragment.show(fragmentTransaction, EventDetailFragment.TAG);
        }
    }
}
