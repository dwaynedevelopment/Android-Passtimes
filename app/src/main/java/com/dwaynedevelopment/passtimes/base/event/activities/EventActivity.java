package com.dwaynedevelopment.passtimes.base.event.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dwaynedevelopment.passtimes.R;
import com.dwaynedevelopment.passtimes.base.event.fragments.EventCreateFragment;
import com.dwaynedevelopment.passtimes.base.event.fragments.EventDetailFragment;
import com.dwaynedevelopment.passtimes.base.event.interfaces.IEventHandler;
import com.dwaynedevelopment.passtimes.parent.activities.BaseActivity;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_EVENTS;


public class EventActivity extends AppCompatActivity implements IEventHandler {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        invokeFragment(
                getIntent().getStringExtra("EXTRA_EVENT_VIEW_ID"),
                getIntent().getBooleanExtra("EXTRA_EVENT_EDIT_ID", false)
        );
    }

    public void invokeFragment(@Nullable String eventId, boolean isEditing) {
        if (eventId != null) {
            if (eventId.isEmpty() && !isEditing) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_event, EventCreateFragment.newInstance(null))
                        .commit();
            } else if (!eventId.isEmpty()) {
                if (isEditing) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_event, EventCreateFragment.newInstance(eventId))
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_event, EventDetailFragment.newInstance(eventId))
                            .commit();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, BaseActivity.class);// New activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.overridePendingTransition(0, 0);
        startActivity(intent);
        finish();
    }

    @Override
    public void invokeEditDetailView(String eventDocumentReference) {
        final String eventId = getIntent().getStringExtra("EXTRA_EVENT_VIEW_ID");
        if (eventId != null) {
            if (!eventId.isEmpty()) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_event, EventCreateFragment.newInstance("/" + DATABASE_REFERENCE_EVENTS + "/" + eventId))
                        .commit();
                }
            }

    }

    @Override
    public void dismissDetailView() {
        Intent intent = new Intent(this, BaseActivity.class);// New activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.overridePendingTransition(0, 0);
        startActivity(intent);
        finish();
    }
}
