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
import android.support.v7.widget.GridLayoutManager;
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

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.adapters.FavoriteViewAdapter;
import com.dwaynedevelopment.passtimes.adapters.FeedOnGoingViewAdapter;
import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.models.Sport;
import com.dwaynedevelopment.passtimes.navigation.fragments.event.CreateEventDialogFragment;
import com.dwaynedevelopment.passtimes.navigation.fragments.event.ViewEventDialogFragment;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_EVENT_SELECTED;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_EVENTS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_SPORTS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_USERS;

public class FeedFragment extends Fragment {

    private FeedOnGoingViewAdapter mAdapter;
    private EventReceiver eventReceiver;
    private List<Sport> menuSports = new ArrayList<>();

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
        FirebaseFirestoreUtils mDb = FirebaseFirestoreUtils.getInstance();
        AuthUtils mAuth = AuthUtils.getInstance();
        View view = getView();

        if (view != null) {
            Toolbar feedToolbar = view.findViewById(R.id.tb_feed);
            feedToolbar.inflateMenu(R.menu.menu_feed);
            feedToolbar.setOnMenuItemClickListener(menuItemClickListener);

            eventReceiver = new EventReceiver();
            IntentFilter actionFilter = new IntentFilter();
            actionFilter.addAction(ACTION_EVENT_SELECTED);
            Objects.requireNonNull(getActivity()).registerReceiver(eventReceiver, actionFilter);


            ImageButton filterImageButton = view.findViewById(R.id.iv_filter_btn);
            filterImageButton.setOnClickListener(filterListener);

            CollectionReference eventSports = mDb.getFirestore().collection("events");

            // [START chain_filters]
            //Query q = eventSports.whereEqualTo("sport", "Soccer").orderBy("title");
            // .whereEqualTo("sport", "Basketball").orderBy("title");


//            FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
//                    .setQuery(mDb.databaseCollection(DATABASE_REFERENCE_EVENTS), Event.class)
//                    .build();

            FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                    .setQuery(eventSports, Event.class)
                    .build();


            mAdapter = new FeedOnGoingViewAdapter(options, getContext());

            RecyclerView recyclerView = getView().findViewById(R.id.rv_ongoing);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setHasFixedSize(true);


            mDb.databaseDocument(DATABASE_REFERENCE_USERS, mAuth.getCurrentSignedUser().getId())
                    .get().addOnCompleteListener(playerCompleteListener);

        }
    }

    private static final String TAG = "FeedFragment";
    private final OnCompleteListener<DocumentSnapshot> playerCompleteListener = new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

            if (task.isSuccessful()) {
                List<DocumentReference> references = ((List<DocumentReference>) task.getResult().getData().get("favorites"));

                for (int i = 0; i < references.size(); i++) {

                    references.get(i).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            menuSports.add(Objects.requireNonNull(task.getResult()).toObject(Sport.class));
                        }
                    });
                }
            }

        }
    };

    private final View.OnClickListener filterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getActivity() != null) {
                PopupMenu pop = new PopupMenu(getActivity().getApplicationContext(), v, Gravity.BOTTOM);
                pop.getMenuInflater().inflate(R.menu.menu_filter, pop.getMenu());

                for (int i = 0; i < menuSports.size(); i++) {
                    pop.getMenu().add(menuSports.get(i).getCategory());
                }

                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        return true;
                    }
                });
                pop.show();
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
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
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

    public class EventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Event event = intent.getParcelableExtra("SELECTED_EVENT");

            ViewEventDialogFragment viewEventDialogFragment = ViewEventDialogFragment.newInstance(event);
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
            viewEventDialogFragment.show(fragmentTransaction, ViewEventDialogFragment.TAG);
        }
    }
}
