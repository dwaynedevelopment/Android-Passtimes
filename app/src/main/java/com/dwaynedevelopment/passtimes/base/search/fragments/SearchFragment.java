package com.dwaynedevelopment.passtimes.base.search.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.base.search.adapters.SearchAdapter;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.dwaynedevelopment.passtimes.utils.AdapterUtils.adapterViewStatus;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_USERS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_INSERTED_DATA;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_MODIFIED_DATA;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.NOTIFY_REMOVED_DATA;
import static com.google.firebase.firestore.Query.Direction.ASCENDING;


public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private ListenerRegistration playerListenerRegistration;
    private static final String TAG = "SearchFragment";
    private TextView searchStub;
    private SearchAdapter searchAdapter;
    private List<Player> playerList = new ArrayList<>();
    private FirebaseFirestoreUtils mDb;

    public static SearchFragment newInstance() {

        Bundle args = new Bundle();

        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDb = FirebaseFirestoreUtils.getInstance();
        if (getActivity() != null) {
            if (getView() != null) {
                Toolbar searchToolbar = getView().findViewById(R.id.tb_search);
                searchToolbar.inflateMenu(R.menu.menu_search);
                searchToolbar.setOnMenuItemClickListener(searchMenuListener);

                searchStub = getView().findViewById(R.id.tv_search_stub);

                //THREAD: EVENT FETCH
                Thread playerSearchThread = new Thread() {
                    @Override
                    public void run() {
                        playerListenerRegistration = mDb.databaseCollection(DATABASE_REFERENCE_USERS)
                                .orderBy("name", ASCENDING)
                                .addSnapshotListener(playerQuerySnapshotListener);

                        getActivity().runOnUiThread(() ->
                                recyclerViewSetup(getView(), getContext(), playerList));
                    }
                };

                if (playerSearchThread.getState().equals(Thread.State.NEW)) {
                    playerSearchThread.start();
                }
            }
        }

    }

    private final EventListener<QuerySnapshot> playerQuerySnapshotListener = (queryDocumentSnapshots, e) -> {
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
                                    if (!playerList.contains(playerAdded)) {
                                        final int index = i;
                                        if (getActivity() != null) {
                                            //THREAD: ADDED PLAYER
                                            getActivity().runOnUiThread(() -> {
                                                playerList.add(playerAdded);
                                                adapterViewStatus(searchAdapter, NOTIFY_INSERTED_DATA, index);
                                                Log.i(TAG, "PLAYERS ADDED: " + playerAdded.getName());
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
                                    if (playerList.contains(playerEdited)) {
                                        final int index = i;
                                        if (getActivity() != null) {
                                            //THREAD: MODIFIED PLAYER
                                            getActivity().runOnUiThread(() -> {
                                                playerList.set(index, playerEdited);
                                                adapterViewStatus(searchAdapter, NOTIFY_MODIFIED_DATA, index);
                                            });
                                        }
                                    }
                                }
                            }
                            break;
                        case REMOVED:
                            final Player playerRemoved = documentChange.getDocument().toObject(Player.class);
                            if (playerList.contains(playerRemoved)) {
                                final int index = i;
                                if (getActivity() != null) {
                                    //THREAD: REMOVED PLAYER
                                    getActivity().runOnUiThread(() -> {
                                        playerList.remove(playerRemoved);
                                        adapterViewStatus(searchAdapter, NOTIFY_REMOVED_DATA, index);
                                    });
                                }
                            }
                            break;
                    }
                }
            }
        }


    };


    private void recyclerViewSetup(View view, Context context, List<Player> playerList) {
        if (getActivity() != null) {
            if (getView() != null) {
                searchAdapter = new SearchAdapter(context, playerList);
                recyclerView = view.findViewById(R.id.rv_search);
                recyclerView.setHasFixedSize(true);
                recyclerView.setVisibility(View.GONE);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),
                        LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(searchAdapter);
                searchAdapter.notifyDataSetChanged();
            }
        }
    }

    private final Toolbar.OnMenuItemClickListener searchMenuListener = menuItem -> {

        SearchView search = (SearchView) menuItem.getActionView();
        search.setImeOptions(EditorInfo.IME_ACTION_DONE);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (getActivity() != null) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, 0);
                    return false;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty()) {
                    searchStub.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    searchStub.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    List<Player> newPlayers = new ArrayList<>();
                    for (Player p: playerList) {
                        if (p.getName().toLowerCase().contains(s)) {
                            newPlayers.add(p);
                            searchStub.setVisibility(View.GONE);
                        } else {
                            searchStub.setVisibility(View.VISIBLE);
                        }
                    }
                    searchAdapter.reset(newPlayers);
                }

                return true;
            }
        });
        return false;
    };



    @Override
    public void onPause() {
        super.onPause();
        if (playerListenerRegistration != null) {
            playerListenerRegistration.remove();
            playerListenerRegistration = null;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        playerList.clear();
    }
}
