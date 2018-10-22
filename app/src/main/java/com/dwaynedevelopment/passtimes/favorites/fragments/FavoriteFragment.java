package com.dwaynedevelopment.passtimes.favorites.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.adapters.FavoriteViewAdapter;
import com.dwaynedevelopment.passtimes.favorites.interfaces.IFavoriteHandler;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.models.Sport;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;
import com.dwaynedevelopment.passtimes.utils.DatabaseUtils;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


import static com.dwaynedevelopment.passtimes.utils.KeyUtils.ACTION_FAVORITE_SELECTED;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_SPORTS;
import static com.dwaynedevelopment.passtimes.utils.SnackbarUtils.invokeSnackBar;

public class FavoriteFragment extends Fragment {

    private FirebaseFirestoreUtils mDb;
    private AuthUtils mAuth;
    private IFavoriteHandler iFavoriteHandler;
    private FavoritesReceiver favoritesReceiver;
    private ArrayList<Sport> selectedFavorites = new ArrayList<>();
    private HashMap<String, HashMap<String, String>> favoriteSports = new HashMap<>();
    private List<DocumentReference> favoriteReferences = new ArrayList<>();


    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IFavoriteHandler) {
            iFavoriteHandler = (IFavoriteHandler) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    private static final String TAG = "FavoriteFragment";
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDb = FirebaseFirestoreUtils.getInstance();
        mAuth = AuthUtils.getInstance();


        mDb.databaseCollection(DATABASE_REFERENCE_SPORTS).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<Sport> sportsArray = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                sportsArray.add(document.toObject(Sport.class));

                                FavoriteViewAdapter adapter = new FavoriteViewAdapter((AppCompatActivity) getActivity(), sportsArray);
                                if (getActivity() != null) {
                                    if (getView() != null) {
                                        RecyclerView recyclerView = getView().findViewById(R.id.rv_favorite);
                                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 3));
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setAdapter(adapter);
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        if (getActivity() != null) {
            favoritesReceiver = new FavoritesReceiver();
            IntentFilter actionFilter = new IntentFilter();
            actionFilter.addAction(ACTION_FAVORITE_SELECTED);
            getActivity().registerReceiver(favoritesReceiver, actionFilter);

            if (getView() != null) {
                Button continueButton = getView().findViewById(R.id.btn_continue);
                continueButton.setOnClickListener(continueListener);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(favoritesReceiver);
        }
    }


    private final View.OnClickListener continueListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mAuth.getCurrentSignedUser() != null) {
                Player player = mAuth.getCurrentSignedUser();
                //player.setFavorites(favoriteSports);
                player.setFavoriteReferences(favoriteReferences);

                if (getView() != null) {
                    final ProgressBar progress = getView().findViewById(R.id.pb_dots_fav);
                    progress.setVisibility(View.VISIBLE);

                    mDb.insertFavorites(player);

                    if (player.getFavoriteReferences().size() >= 1) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (iFavoriteHandler != null) {
                                    progress.setVisibility(View.GONE);
                                    iFavoriteHandler.dismissActivity();
                                }
                            }
                        }, 1000);
                    } else {
                        progress.setVisibility(View.GONE);
                        invokeSnackBar((AppCompatActivity) Objects.requireNonNull(getContext()),
                                "Please select a sport.",
                                getResources().getColor(R.color.colorDarkPrimary),
                                getResources().getColor(R.color.colorPrimaryAccent));
                    }
                }
            }
        }
    };


    public class FavoritesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            selectedFavorites = intent.getParcelableArrayListExtra("SELECTED_SPORTS");

            //CLEAR LIST TO AVOID DUPLICATES ENTRIES.
//            favoriteSports.clear();
            favoriteReferences.clear();
            for (int i = 0; i <selectedFavorites.size() ; i++) {
                favoriteReferences.add(mDb.getFirestore().document("/"+DATABASE_REFERENCE_SPORTS+"/"+selectedFavorites.get(i).getId()));
//                HashMap<String, String> selected = new HashMap<>();
//                selected.put("id", selectedFavorites.get(i).getId());
//                selected.put("category", selectedFavorites.get(i).getCategory());
//                favoriteSports.put(selectedFavorites.get(i).getId(), selected);
            }

        }
    }
 }
