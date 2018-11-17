package com.dwaynedevelopment.passtimes.base.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.utils.FirebaseFirestoreUtils;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

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

        if (getActivity() != null) {
            if (getView() != null) {
                Toolbar searchToolbar = getView().findViewById(R.id.tb_search);
                searchToolbar.inflateMenu(R.menu.menu_search);
                searchToolbar.setOnMenuItemClickListener(searchMenuListener);
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
                return false;
            }
        });


        return false;
    };
}
