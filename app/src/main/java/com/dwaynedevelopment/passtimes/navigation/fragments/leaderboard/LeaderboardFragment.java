package com.dwaynedevelopment.passtimes.navigation.fragments.leaderboard;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.adapters.LeaderboardViewAdapter;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.navigation.fragments.feed.FeedFragment;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.dwaynedevelopment.passtimes.utils.AdapterUtils.adapterViewStatus;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_EVENT_SELECTED;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_USERS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_INSERTED_DATA;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_MODIFIED_DATA;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_REMOVED_DATA;
import static com.google.firebase.firestore.Query.Direction.DESCENDING;

public class LeaderboardFragment extends Fragment {

    private FirebaseFirestoreUtils mDb;
    private AuthUtils mAuth;

    private Thread leaderExcecuteThread;
    private ListenerRegistration leaderboardListenerRegistration;

    private LeaderboardViewAdapter leaderboardViewAdapter;
    private Map<String, Player> leaderboardHashMap = new HashMap<>();

    private TextView playerRankName;
    private TextView playerRankNumber;
    private CircleImageView playerRankProfile;

    private RankingReceiver rankingReceiver;


    public LeaderboardFragment() {
        mDb = FirebaseFirestoreUtils.getInstance();
        mAuth = AuthUtils.getInstance();
    }

    public static LeaderboardFragment newInstance() {
        Bundle args = new Bundle();
        LeaderboardFragment fragment = new LeaderboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            if (getView() != null) {
                playerRankName = getView().findViewById(R.id.tv_rank_name);
                playerRankName.setText(mAuth.getCurrentSignedUser().getName());
                playerRankNumber = getView().findViewById(R.id.tv_rank_num);
                playerRankProfile = getView().findViewById(R.id.ci_rank_profile);
                Glide.with(getActivity().getApplicationContext()).load(mAuth.getCurrentSignedUser().getThumbnail()).into(playerRankProfile);
            }

            //THREAD: EVENT FETCH
            leaderExcecuteThread = new Thread() {
                @Override
                public void run() {
                    leaderboardListenerRegistration = mDb.databaseCollection(DATABASE_REFERENCE_USERS)
                            .orderBy("overallXP", DESCENDING)
                            .addSnapshotListener(leaderboardQuerSnapshotListener);
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                                    getActivity().runOnUiThread(() ->
                                            setUpOngoingRecyclerView(leaderboardHashMap)),
                            250);
                }
            };

            if (leaderExcecuteThread.getState().equals(Thread.State.NEW)) {
                leaderExcecuteThread.start();
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (leaderboardListenerRegistration != null) {
            leaderboardListenerRegistration.remove();
            leaderboardListenerRegistration = null;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        leaderboardHashMap.clear();
    }


    private final EventListener<QuerySnapshot> leaderboardQuerSnapshotListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                            @javax.annotation.Nullable FirebaseFirestoreException e) {
            if (e != null) {
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
                                        Player playerAdded = documentChange.getDocument().toObject(Player.class);
                                        if (!leaderboardHashMap.containsKey(playerAdded.getId())) {
                                            final int index = i;
                                            if (getActivity() != null) {
                                                //THREAD: ADDED PLAYER
                                                getActivity().runOnUiThread(() -> {
                                                    leaderboardHashMap.put(playerAdded.getId(), playerAdded);
                                                    adapterViewStatus(leaderboardViewAdapter, NOTIFY_INSERTED_DATA, index);
                                                });
                                            }
                                        }
                                    }
                                }
                                break;
                            case MODIFIED:
                                if (queryDocumentSnapshots.getDocuments().get(i) != null) {
                                    if (queryDocumentSnapshots.getDocuments().get(i).exists()) {
                                        final Player playerEdited = documentChange.getDocument().toObject(Player.class);
                                        if (leaderboardHashMap.containsKey(playerEdited.getId())) {
                                            final int index = i;
                                            if (getActivity() != null) {
                                                //THREAD: MODIFIED PLAYER
                                                getActivity().runOnUiThread(() -> {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                        leaderboardHashMap.replace(playerEdited.getId(), playerEdited);
                                                        adapterViewStatus(leaderboardViewAdapter, NOTIFY_MODIFIED_DATA, index);
                                                    }
                                                });
                                            }

                                        }
                                    }
                                }
                                break;
                            case REMOVED:
                                final Player playerRemoved = documentChange.getDocument().toObject(Player.class);
                                if (leaderboardHashMap.containsKey(playerRemoved.getId())) {
                                    final int index = i;
                                    if (getActivity() != null) {
                                        //THREAD: REMOVED PLAYER
                                        getActivity().runOnUiThread(() -> {
                                            leaderboardHashMap.remove(playerRemoved.getId());
                                            adapterViewStatus(leaderboardViewAdapter, NOTIFY_REMOVED_DATA, index);
                                        });
                                    }

                                }
                                break;
                        }
                    }
                }
            }
        }
    };

    private void setUpOngoingRecyclerView(Map<String, Player> leaderboardHashMap) {
        if (getActivity() != null) {
            if (getView() != null) {
                leaderboardViewAdapter = new LeaderboardViewAdapter(leaderboardHashMap, getActivity().getApplicationContext());
                RecyclerView leaderboardRecylerView = getView().findViewById(R.id.rv_leaderboard);
                leaderboardRecylerView.setHasFixedSize(true);
                leaderboardRecylerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),
                        LinearLayoutManager.VERTICAL, false));
                leaderboardRecylerView.setAdapter(leaderboardViewAdapter);
                leaderboardViewAdapter.notifyDataSetChanged();
            }
        }
    }

    private void registerBroadcastReceiver() {
        rankingReceiver = new LeaderboardFragment.RankingReceiver();
        IntentFilter actionFilter = new IntentFilter();
        actionFilter.addAction("ACTION_RANKING_NUMBER");
        if (getActivity() != null) {
            getActivity().registerReceiver(rankingReceiver, actionFilter);
        }
    }

    private void unregisterBroadcastReceiver() {
        if (getActivity() != null) {
            getActivity().unregisterReceiver(rankingReceiver);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceiver();
    }

    public class RankingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int ranking = intent.getIntExtra("EXTRA_RANKING_NUMBER", 0);
            playerRankNumber.setText(String.valueOf(ranking));
        }
    }

}
