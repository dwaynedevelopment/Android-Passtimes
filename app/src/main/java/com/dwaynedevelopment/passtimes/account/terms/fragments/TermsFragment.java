package com.dwaynedevelopment.passtimes.account.terms.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.account.terms.interfaces.ITermsHandler;

public class TermsFragment extends Fragment {

    private ITermsHandler iTermsHandler;

    public static TermsFragment newInstance() {
        return new TermsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ITermsHandler) {
            iTermsHandler = (ITermsHandler) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_terms, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            //AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (getView() != null) {
                View view = getView();
                ImageButton closeTermsButton = view.findViewById(R.id.ic_close_terms);
                closeTermsButton.setOnClickListener(closeTermsListener);
            }
        }
    }

    private final View.OnClickListener closeTermsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (iTermsHandler != null) {
                iTermsHandler.dismissTerms();
            }
        }
    };
}
