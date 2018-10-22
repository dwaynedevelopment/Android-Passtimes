package com.dwaynedevelopment.passtimes.utils;

import com.dwaynedevelopment.passtimes.models.Player;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_USERS;

public class FirebaseFirestoreUtils {


    private FirebaseFirestore mFirestore;

    private FirebaseFirestoreUtils() {
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mFirestore.setFirestoreSettings(settings);

    }

    private static FirebaseFirestoreUtils instance = null;

    public static FirebaseFirestoreUtils getInstance() {
        if(instance == null) {
            instance = new FirebaseFirestoreUtils();
        }

        return instance;
    }

    public FirebaseFirestore getFirestore() {
        return mFirestore;
    }

    public <T> void insertDocument(String COLLECTION_REFERENCE, String DOCUMENT_REFERENCE, T documentObject) {
        mFirestore.collection(COLLECTION_REFERENCE).document(DOCUMENT_REFERENCE).set(documentObject);
    }


    public void updateImage(Player documentObject) {
        mFirestore.collection(DATABASE_REFERENCE_USERS).document(documentObject.getId()).update("thumbnail", documentObject.getThumbnail());
    }

    public void insertFavorites(Player player) {
        mFirestore.collection(DATABASE_REFERENCE_USERS).document(player.getId()).update("favorites", player.getFavoriteReferences());
    }

    public CollectionReference databaseCollection(String COLLECTION_REFERENCE) {
        return mFirestore.collection(COLLECTION_REFERENCE);
    }

}
