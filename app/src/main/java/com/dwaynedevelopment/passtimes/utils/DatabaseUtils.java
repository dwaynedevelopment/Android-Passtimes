package com.dwaynedevelopment.passtimes.utils;

import com.dwaynedevelopment.passtimes.models.Event;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseUtils {

    public static enum Reference {
        sports,
        events
    }

    private FirebaseDatabase mDatabase;

    private DatabaseUtils() {
        mDatabase = FirebaseDatabase.getInstance();
    }

    private static DatabaseUtils instance = null;

    public static DatabaseUtils getInstance() {
        if(instance == null) {
            instance = new DatabaseUtils();
        }

        return instance;
    }

    public DatabaseReference reference(Reference reference) {
        return mDatabase.getReference(reference.toString());
    }

    public void addEvent(Event event) {
        mDatabase.getReference("events").child(event.getId()).setValue(event);
    }
}
