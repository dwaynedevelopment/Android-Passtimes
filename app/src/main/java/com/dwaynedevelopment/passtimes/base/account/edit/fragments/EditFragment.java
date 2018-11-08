package com.dwaynedevelopment.passtimes.base.account.edit.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.base.account.edit.interfaces.IEditHandler;
import com.dwaynedevelopment.passtimes.utils.AuthUtils;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.dwaynedevelopment.passtimes.utils.AlertUtils.invokeSnackBar;

public class EditFragment extends Fragment {

    private IEditHandler iEditHandler;
    private TextInputEditText displayNameEditText;

    public static EditFragment newInstance() {
        
        Bundle args = new Bundle();
        
        EditFragment fragment = new EditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IEditHandler) {
            iEditHandler = (IEditHandler) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            if (getView() != null) {
                View view = getView();

                displayNameEditText = view.findViewById(R.id.et_fullname_edit);
                if (AuthUtils.getInstance().getCurrentSignedUser() != null) {
                    CircleImageView circleImageView = view.findViewById(R.id.ci_edit_image);
                    View placeHolder = view.findViewById(R.id.vw_placeholder_edit);
                    placeHolder.setVisibility(View.GONE);
                    Glide.with(getActivity().getApplicationContext()).load(AuthUtils.getInstance().getCurrentSignedUser().getThumbnail()).into(circleImageView);
                    displayNameEditText.setText(AuthUtils.getInstance().getCurrentSignedUser().getName());
                    displayNameEditText.setFocusable(true);
                    displayNameEditText.setFocusableInTouchMode(true);
                    displayNameEditText.requestFocus();
                }


                ImageButton cameraImageButton = view.findViewById(R.id.ic_camera_edit);
                cameraImageButton.setOnClickListener(cameraListener);

                displayNameEditText = view.findViewById(R.id.et_fullname_edit);
                Button submitButton = view.findViewById(R.id.btn_edit_submit);
                submitButton.setOnClickListener(cameraListener);
            }
        }

    }

    private final View.OnClickListener cameraListener = v -> {
        if (v.getId() == R.id.ic_camera_edit) {
            if (iEditHandler != null) {
                iEditHandler.invokeCamera();
            }
        } else if (v.getId() == R.id.btn_edit_submit) {
            if (!Objects.requireNonNull(displayNameEditText.getText()).toString().isEmpty()) {
                iEditHandler.submitEditChanges(displayNameEditText.getText().toString());
            } else {
                invokeSnackBar((AppCompatActivity) Objects.requireNonNull(getActivity()),
                        "Please fill out your full name.",
                        getResources().getColor(R.color.colorDarkPrimary),
                        getResources().getColor(R.color.colorPrimaryAccent));
                //iEditHandler.submitEditChanges(AuthUtils.getInstance().getCurrentSignedUser().getName());
            }
        }
    };
}
