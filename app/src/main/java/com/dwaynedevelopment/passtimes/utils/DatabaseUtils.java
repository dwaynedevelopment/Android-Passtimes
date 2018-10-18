package com.dwaynedevelopment.passtimes.utils;

import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Player;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_USERS;

public class DatabaseUtils {



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

    public DatabaseReference reference(String DATABASE_REFERENCE) {
        return mDatabase.getReference(DATABASE_REFERENCE);
    }

    public void insertUser(Player player) {
        mDatabase.getReference(DATABASE_REFERENCE_USERS).child(player.getId()).setValue(player);
    }

    public void updateImage(Player player) {
        mDatabase.getReference(DATABASE_REFERENCE_USERS).child(player.getId()).child("thumbnail").setValue(player.getThumbnail());
    }

    public void insertFavorites(Player player) {
        mDatabase.getReference(DATABASE_REFERENCE_USERS).child(player.getId()).child("favorites").setValue(player.getFavorites());
    }

    public void addEvent(Event event) {
        mDatabase.getReference("events").child(event.getId()).setValue(event);
    }
}
