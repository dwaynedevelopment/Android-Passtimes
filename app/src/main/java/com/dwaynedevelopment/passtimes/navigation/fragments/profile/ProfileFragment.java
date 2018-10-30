package com.dwaynedevelopment.passtimes.navigation.fragments.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.navigation.interfaces.INavigationHandler;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private INavigationHandler iNavigationHandler;

    public ProfileFragment() { }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof INavigationHandler) {
            iNavigationHandler = (INavigationHandler) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            AuthUtils mAuth = AuthUtils.getInstance();
            AppCompatActivity activity = (AppCompatActivity) getActivity();


            if (getView() != null) {

                View view = getView();

                Toolbar feedToolbar = view.findViewById(R.id.tb_profile);
                feedToolbar.inflateMenu(R.menu.menu_profile);
                feedToolbar.setOnMenuItemClickListener(menuItemClickListener);

                CircleImageView profileImage = view.findViewById(R.id.ci_profile);
                Glide.with(activity).load(mAuth.getCurrentSignedUser().getThumbnail()).into(profileImage);
                TextView profileName = view.findViewById(R.id.tv_profile_name);
                profileName.setText(mAuth.getCurrentSignedUser().getName());
            }
        }

    }

    private final Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int itemId = item.getItemId();
            final int settings = R.id.action_settings;
            final int edit = R.id.action_edit;
            final int favorites = R.id.action_favorite;
            switch (itemId) {
                case settings:
                    if (iNavigationHandler != null) {
                        iNavigationHandler.invokeSettings();
                    }
                    break;
                case edit:
                    if (iNavigationHandler != null) {
                        iNavigationHandler.invokeEditProfile();
                    }
                    break;
                case favorites:
                    if (iNavigationHandler != null) {
                        iNavigationHandler.invokeFavorites();
                    }
                    break;
            }
            return false;
        }
    };
}
