package com.dwaynedevelopment.passtimes.utils;

import com.dwaynedevelopment.passtimes.models.Event;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseUtils {

    private FirebaseDatabase database;

    private DatabaseUtils() {
        database = FirebaseDatabase.getInstance();
    }

    private static DatabaseUtils instance = null;

    public static DatabaseUtils getInstance() {
        if(instance == null) {
            instance = new DatabaseUtils();
        }

        return instance;
    }

    public void addEvent(Event event) {

    }
}
