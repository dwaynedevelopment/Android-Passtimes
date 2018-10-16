package com.dwaynedevelopment.passtimes.navigation.fragments.event;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dwaynedevelopment.passtimes.R;

public class CreateEventDialogFragment extends DialogFragment {

    public static final String TAG = "CreateEventDialogFragme";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null) {
            Toolbar createEventToolbar = getView().findViewById(R.id.tb_create_event);
            createEventToolbar.inflateMenu(R.menu.menu_create_event);
            createEventToolbar.setOnMenuItemClickListener(menuItemClickListener);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_create_event, container, false);

        if(getView() != null) {
            // TODO: layout
        }

        return view;
    }

    Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(item.getItemId() == R.id.action_close) {
                dismiss();
            } else if(item.getItemId() == R.id.action_save) {
                // TODO: Save event to database
            }
            return false;
        }
    };
}
