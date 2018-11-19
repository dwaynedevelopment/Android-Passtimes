package com.dwaynedevelopment.passtimes.base.profile.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.base.feed.adapters.AttendingFeedViewAdapter;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.dwaynedevelopment.passtimes.utils.AdapterUtils.adapterViewStatus;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_EVENTS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_USERS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_INSERTED_DATA;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_MODIFIED_DATA;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_REMOVED_DATA;

public class ProfileDialogFragment extends DialogFragment {


    public static final String TAG = "ProfileDialogFragment";
    private ListenerRegistration attendingListenerRegister;
    private AttendingFeedViewAdapter attendingFeedViewAdapter;
    private FirebaseFirestoreUtils mDb;
    private Map<String, Event> attendedEventsMap = new HashMap<>();


    public static ProfileDialogFragment newInstance(String userId) {
        
        Bundle args = new Bundle();
        args.putString("ARGS_USER_ID", userId);
        ProfileDialogFragment fragment = new ProfileDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDb = FirebaseFirestoreUtils.getInstance();
        if (getView() != null) {
            if (getArguments() != null) {
                String userIdArgs = getArguments().getString("ARGS_USER_ID");

                //THREAD: ATTENDING FETCH
                Thread attendingThreadExecute = new Thread() {
                    @Override
                    public void run() {
                        if (userIdArgs != null) {
                            attendingListenerRegister = mDb.databaseCollection(DATABASE_REFERENCE_USERS)
                                    .document(userIdArgs)
                                    .addSnapshotListener(attendingSnapshotListener);
                            getActivity().runOnUiThread(() -> setUpAttendingRecyclerView());
                        }
                    }
                };

                //THREAD: ATTENDING EXECUTE
                if (attendingThreadExecute.getState().equals(Thread.State.NEW)) {
                    attendingThreadExecute.start();
                }

                ImageButton im = getView().findViewById(R.id.ic_profile_close);

                im.setOnClickListener(v -> dismiss());
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

                    TextView name = Objects.requireNonNull(getView()).findViewById(R.id.tv_user_name);
                    name.setText(attendedPlayer.getName());
                    TextView xp = getView().findViewById(R.id.tv_user_xp);
                    xp.setText(String.valueOf(attendedPlayer.getOverallXP()));
                    CircleImageView profileImage = Objects.requireNonNull(getView()).findViewById(R.id.ci_user_profile);
                    Glide.with(Objects.requireNonNull(getActivity())).load(attendedPlayer.getThumbnail()).into(profileImage);

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
                                                    if (Calendar.getInstance().getTimeInMillis() > attendedEvents.getEndDate() || attendedEvents.getIsClosed()) {
                                                        getActivity().runOnUiThread(() -> {
                                                            attendedEventsMap.put(attendedEvents.getId(), attendedEvents);
                                                            adapterViewStatus(attendingFeedViewAdapter, NOTIFY_INSERTED_DATA, index);
                                                        });
                                                    }
                                                }
                                            } else {
                                                if (getActivity() != null) {
                                                    if (Calendar.getInstance().getTimeInMillis() < attendedEvents.getEndDate() || attendedEvents.getIsClosed()) {
                                                        getActivity().runOnUiThread(() -> {
                                                            attendedEventsMap.replace(attendedEvents.getId(), attendedEvents);
                                                            adapterViewStatus(attendingFeedViewAdapter, NOTIFY_MODIFIED_DATA, index);
                                                        });
                                                    }
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
        }
    };

    private void setUpAttendingRecyclerView() {
        if (getActivity() != null) {
            if (getView() != null) {
                attendingFeedViewAdapter = new AttendingFeedViewAdapter(attendedEventsMap, getActivity().getApplicationContext());
                RecyclerView attendedRecyclerView = getView().findViewById(R.id.rv_attending_user);
                attendedRecyclerView.setHasFixedSize(true);
                attendedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),
                        LinearLayoutManager.HORIZONTAL, false));
                attendedRecyclerView.setAdapter(attendingFeedViewAdapter);
            }
        }
    }
}
