package com.dwaynedevelopment.passtimes.navigation.fragments.event;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.adapters.FavoriteViewAdapter;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.models.Sport;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.CalendarUtils;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ARGS_SELECTED_EVENT_ID;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_EVENTS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_SPORTS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_USERS;

public class ViewEventDialogFragment extends DialogFragment {

    public static final String TAG = "ViewEventDialogFragment";
    private FirebaseFirestoreUtils mDb;
    private final List<DocumentReference> attendingUsers = new ArrayList<>();


    public static ViewEventDialogFragment newInstance(String eventId) {

        Bundle args = new Bundle();
        args.putString(ARGS_SELECTED_EVENT_ID, eventId);

        ViewEventDialogFragment fragment = new ViewEventDialogFragment();
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
        return inflater.inflate(R.layout.dialog_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            mDb = FirebaseFirestoreUtils.getInstance();
            if (getArguments() != null) {

                final String eventIdExtra = getArguments().getString(ARGS_SELECTED_EVENT_ID);
                if (eventIdExtra != null) {
                    Log.i(TAG, "onActivityCreated: " + eventIdExtra);
                    mDb.databaseCollection(DATABASE_REFERENCE_EVENTS).document(eventIdExtra)
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot queryDocumentSnapshots,
                                            @javax.annotation.Nullable FirebaseFirestoreException e) {

                            Event eventSelected = Objects.requireNonNull(queryDocumentSnapshots).toObject(Event.class);

                            if (eventSelected != null) {
                                if (getView() != null) {

                                    DocumentReference hostReference = eventSelected.getEventHost();

                                    CircleImageView ciHost = getView().findViewById(R.id.ci_host);

                                    if (hostReference != null) {
                                        hostReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                                final Player eventHost = Objects.requireNonNull(documentSnapshot).toObject(Player.class);
                                                if (eventHost != null) {
                                                    Glide.with(Objects.requireNonNull(getContext())).load(eventHost.getThumbnail()).into(ciHost);
                                                }
                                            }
                                        });

//                                        hostReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                final Player eventHost = Objects.requireNonNull(task.getResult()).toObject(Player.class);
//                                                if (eventHost != null) {
//                                                    Glide.with(Objects.requireNonNull(getContext())).load(eventHost.getThumbnail()).into(ciHost);
//                                                }
//                                            }
//                                        });
                                    }
                                    TextView tvMonth = getView().findViewById(R.id.tv_event_month);
                                    tvMonth.setText(CalendarUtils.getMonthFromDate(eventSelected.getStartDate()));

                                    TextView tvDay = getView().findViewById(R.id.tv_event_day);
                                    tvDay.setText(CalendarUtils.getDayFromDate(eventSelected.getStartDate()));

                                    TextView tvTitle = getView().findViewById(R.id.tv_event_title);
                                    tvTitle.setText(eventSelected.getTitle());

                                    TextView tvTime = getView().findViewById(R.id.tv_event_time);
                                    tvTime.setText(timeRangeString(eventSelected));

                                    TextView tvLocation = getView().findViewById(R.id.tv_event_location);
                                    tvLocation.setText(eventSelected.getLocation());
                                }
                            }

                        }
                    });
                    //.get().addOnCompleteListener(eventCompleteListener);
                }

            }
        }


        Button btnJoin = Objects.requireNonNull(getView()).findViewById(R.id.btn_event_join);
        btnJoin.setOnClickListener(clickListener);

//        AuthUtils authUtils = AuthUtils.getInstance();
//        if (event.getHostId().equals(authUtils.getCurrentSignedUser().getId())) {
//            btnJoin.setVisibility(View.GONE);
//
//            //ImageButton delete = getView().findViewById(R.id.ib_delete);
//            //delete.setVisibility(View.VISIBLE);
//            //delete.setOnClickListener(clickListener);
//        }

        ImageButton ibClose = getView().findViewById(R.id.ib_close);
        ibClose.setOnClickListener(clickListener);

    }


    private final OnCompleteListener<QuerySnapshot> eventCompleteListener = new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {

                Event eventSelected = Objects.requireNonNull(task.getResult()).getDocuments().get(0).toObject(Event.class);

                if (eventSelected != null) {
                    if (getView() != null) {

                        DocumentReference hostReference = (DocumentReference) Objects.requireNonNull(task.getResult()).getDocuments().get(0).get("eventHost");
                        CircleImageView ciHost = getView().findViewById(R.id.ci_host);
                        if (hostReference != null) {

                            hostReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    final Player eventHost = Objects.requireNonNull(task.getResult()).toObject(Player.class);
                                    if (eventHost != null) {
                                        Glide.with(Objects.requireNonNull(getContext())).load(eventHost.getThumbnail()).into(ciHost);
                                    }
                                }
                            });
                        }
                        TextView tvMonth = getView().findViewById(R.id.tv_event_month);
                        tvMonth.setText(CalendarUtils.getMonthFromDate(eventSelected.getStartDate()));

                        TextView tvDay = getView().findViewById(R.id.tv_event_day);
                        tvDay.setText(CalendarUtils.getDayFromDate(eventSelected.getStartDate()));

                        TextView tvTitle = getView().findViewById(R.id.tv_event_title);
                        tvTitle.setText(eventSelected.getTitle());

                        TextView tvTime = getView().findViewById(R.id.tv_event_time);
                        tvTime.setText(timeRangeString(eventSelected));

                        TextView tvLocation = getView().findViewById(R.id.tv_event_location);
                        tvLocation.setText(eventSelected.getLocation());
                    }
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        }
    };

//    private final OnCompleteListener<DocumentSnapshot> playerCompleteListener = new OnCompleteListener<DocumentSnapshot>() {
//        @Override
//        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//            if (task.isSuccessful()) {
//                List<DocumentReference> sportReferences = ((List<DocumentReference>) Objects.requireNonNull(task.getResult()).getData().get("favorites"));
//
//                if (sportReferences != null) {
//                    for (int i = 0; i < sportReferences.size(); i++) {
//
//                        sportReferences.get(i).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                                Sport sport = Objects.requireNonNull(task.getResult()).toObject(Sport.class);
//                                popupMenu.getMenuInflater().inflate(R.menu.menu_filter, popupMenu.getMenu());
//                                if (sport != null) {
//                                    popupMenu.getMenu().add(sport.getCategory());
//                                    menuSports.add(sport);
//                                    initialSports.add(sport.getCategory());
//                                }
//                            }
//                        });
//                    }
//
//                    for (int i = 0; i < popupMenu.getMenu().size(); i++) {
//                        popupMenu.getMenu().getItem(i).setChecked(true);
//                    }
//                }
//
//                setupInitialRecyclerView(false);
//                progressBar.setVisibility(View.GONE);
//            }
//        }
//    };

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_close:
                    dismiss();
                    break;
                case R.id.btn_event_join:
                    AuthUtils auth = AuthUtils.getInstance();
                    Player player = auth.getCurrentSignedUser();

                    DocumentReference documentReference = mDb.getFirestore().document("/" + DATABASE_REFERENCE_USERS + "/" + player.getId());
                    //mDb.addAttendee(event, documentReference);

                    v.setVisibility(View.GONE);
                    break;
                case R.id.ib_delete:
                    //FIXME
                    //TODO
                    //mDb.deleteEvent(event);
                    dismiss();
                    break;
            }
        }
    };

    private String timeRangeString(Event event) {
        String startTime = CalendarUtils.getTimeFromDate(event.getStartDate());
        String endTime = CalendarUtils.getTimeFromDate(event.getEndDate());

        return startTime + " - " + endTime;
    }
}
