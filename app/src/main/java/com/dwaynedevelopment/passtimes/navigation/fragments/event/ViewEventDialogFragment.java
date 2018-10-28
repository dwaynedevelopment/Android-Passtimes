package com.dwaynedevelopment.passtimes.navigation.fragments.event;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.CalendarUtils;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.dwaynedevelopment.passtimes.utils.CalendarUtils.timeRangeString;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ARGS_SELECTED_EVENT_ID;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_EVENTS;

public class ViewEventDialogFragment extends DialogFragment {

    public static final String TAG = "ViewEventDialogFragment";
    private FirebaseFirestoreUtils mDb;
    private AuthUtils mAuth;

    private Button joinEventButton;
    private ImageButton deleteImageButton;
    private ImageButton editImageButton;

    private String eventIdExtra;


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
            mAuth = AuthUtils.getInstance();
            if (getArguments() != null) {

                eventIdExtra = getArguments().getString(ARGS_SELECTED_EVENT_ID);
                if (eventIdExtra != null) {

                    if (!eventIdExtra.isEmpty()) {
                        mDb.databaseCollection(DATABASE_REFERENCE_EVENTS).document(eventIdExtra)
                                .addSnapshotListener(eventSnapshotListener);

                        if (getView() != null) {

                            ImageButton closeImageButton = getView().findViewById(R.id.ib_close);
                            closeImageButton.setOnClickListener(eventOnClickListener);

                            joinEventButton = getView().findViewById(R.id.btn_event_join);
                            joinEventButton.setOnClickListener(eventOnClickListener);
                            joinEventButton.setVisibility(View.GONE);

                            deleteImageButton = getView().findViewById(R.id.ib_delete);
                            deleteImageButton.setOnClickListener(eventOnClickListener);
                            deleteImageButton.setVisibility(View.GONE);

                            editImageButton = getView().findViewById(R.id.ib_edit_event);
                            editImageButton.setOnClickListener(eventOnClickListener);
                            editImageButton.setVisibility(View.GONE);
                        }
                    } else {
                        Log.i(TAG, "onActivityCreated: NOTHING HERE");
                    }
                }

            }
        }
    }

    private final EventListener<DocumentSnapshot> eventSnapshotListener = (documentParentSnapshot, e) -> {

        Event eventSelected = Objects.requireNonNull(documentParentSnapshot).toObject(Event.class);

        if (eventSelected != null) {
            if (getView() != null) {

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

                DocumentReference hostReference = eventSelected.getEventHost();

                CircleImageView ciHost = getView().findViewById(R.id.ci_host);

                if (hostReference != null) {
                    hostReference.addSnapshotListener((documentChildSnapshot, e1) -> {
                        final Player eventHost = Objects.requireNonNull(documentChildSnapshot).toObject(Player.class);
                        if (eventHost != null) {
                            if (getActivity() != null) {
                                Glide.with(getActivity().getApplicationContext()).load(eventHost.getThumbnail()).into(ciHost);

                                if (eventHost.getId().equals(mAuth.getCurrentSignedUser().getId())) {
                                    deleteImageButton.setVisibility(View.VISIBLE);
                                    editImageButton.setVisibility(View.VISIBLE);
                                } else {
                                    joinEventButton.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });

                }


                List<DocumentReference> attendeesReference = eventSelected.getAttendees();

            }
        }

    };

    private final View.OnClickListener eventOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_close:
                    dismiss();
                    break;
                case R.id.btn_event_join:
//                    AuthUtils auth = AuthUtils.getInstance();
//                    Player player = auth.getCurrentSignedUser();
//
//                    DocumentReference documentReference = mDb.getFirestore().document("/" + DATABASE_REFERENCE_USERS + "/" + player.getId());
//                    //mDb.addAttendee(event, documentReference);

                    v.setVisibility(View.GONE);
                    break;
                case R.id.ib_delete:

                    //TODO: Fix Prompt but for now testing delete.

                    mDb.databaseCollection(DATABASE_REFERENCE_EVENTS)
                            .document(eventIdExtra)
                            .delete()
                            .addOnSuccessListener(deleteEventListener);
                    break;
            }
        }
    };

    private final OnSuccessListener<Void> deleteEventListener = aVoid -> dismiss();

}