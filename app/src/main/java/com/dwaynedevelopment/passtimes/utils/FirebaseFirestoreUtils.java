package com.dwaynedevelopment.passtimes.utils;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.dwaynedevelopment.passtimes.models.Event;
import com.dwaynedevelopment.passtimes.models.Player;
import com.dwaynedevelopment.passtimes.models.Sport;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_EVENTS;
import static com.dwaynedevelopment.passtimes.utils.KeyUtils.DATABASE_REFERENCE_USERS;

public class FirebaseFirestoreUtils {


    private final FirebaseFirestore mFirestore;

    private FirebaseFirestoreUtils() {
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mFirestore.setFirestoreSettings(settings);

    }

    private static FirebaseFirestoreUtils instance = null;

    public static FirebaseFirestoreUtils getInstance() {
        if (instance == null) {
            instance = new FirebaseFirestoreUtils();
        }

        return instance;
    }

    public FirebaseFirestore getFirestore() {
        return mFirestore;
    }

    public CollectionReference databaseCollection(String COLLECTION_REFERENCE) {
        return mFirestore.collection(COLLECTION_REFERENCE);
    }

    public DocumentReference databaseDocument(String COLLECTION_REFERENCE, String DOCUMENT_REFERENCE) {
        return mFirestore.collection(COLLECTION_REFERENCE).document(DOCUMENT_REFERENCE);
    }

    public <T> void insertDocument(String COLLECTION_REFERENCE, String DOCUMENT_REFERENCE, T documentObject) {
        mFirestore.collection(COLLECTION_REFERENCE).document(DOCUMENT_REFERENCE).set(documentObject);
    }

    public void updateImage(Player documentObject) {
        mFirestore.collection(DATABASE_REFERENCE_USERS).document(documentObject.getId()).update("thumbnail", documentObject.getThumbnail());
    }

    public void insertFavorites(Player documentObject) {
        mFirestore.collection(DATABASE_REFERENCE_USERS).document(documentObject.getId()).update("favorites", documentObject.getFavorites());
    }

    public void addAttendee(Event eventDocument, DocumentReference playerReference) {
        DocumentReference documentReference = mFirestore.collection(DATABASE_REFERENCE_EVENTS).document(eventDocument.getId());
        documentReference.update("attendees", FieldValue.arrayUnion(playerReference));
    }

    public void addAttendings(Player documentObject, DocumentReference eventReference) {
        DocumentReference documentReference = mFirestore.collection(DATABASE_REFERENCE_USERS).document(documentObject.getId());
        documentReference.update("attending", FieldValue.arrayUnion(eventReference));
    }


    public Map<String, Event> filterEventByFavoriteSport(Map<String, Event> eventMap, final List<String> selectedSports) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            switch (selectedSports.size()) {
                case 1:
                    return eventMap.entrySet()
                            .stream()
                            .filter(map -> selectedSports.get(0).equals(map.getValue().getSport()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                case 2:
                    return eventMap.entrySet()
                            .stream()
                            .filter(map -> selectedSports.get(0).equals(map.getValue().getSport()) ||
                                    selectedSports.get(1).equals(map.getValue().getSport()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                case 3:
                    return eventMap.entrySet()
                            .stream()
                            .filter(map -> selectedSports.get(0).equals(map.getValue().getSport()) ||
                                    selectedSports.get(1).equals(map.getValue().getSport()) ||
                                    selectedSports.get(2).equals(map.getValue().getSport()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                case 4:
                    return eventMap.entrySet()
                            .stream()
                            .filter(map -> selectedSports.get(0).equals(map.getValue().getSport()) ||
                                    selectedSports.get(1).equals(map.getValue().getSport()) ||
                                    selectedSports.get(2).equals(map.getValue().getSport()) ||
                                    selectedSports.get(3).equals(map.getValue().getSport()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                case 5:
                    return eventMap.entrySet()
                            .stream()
                            .filter(map -> selectedSports.get(0).equals(map.getValue().getSport()) ||
                                    selectedSports.get(1).equals(map.getValue().getSport()) ||
                                    selectedSports.get(2).equals(map.getValue().getSport()) ||
                                    selectedSports.get(3).equals(map.getValue().getSport()) ||
                                    selectedSports.get(4).equals(map.getValue().getSport()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            }
        }
        return null;
    }


    public void f() {


        Map<Integer, String> h = new HashMap<>();
        h.put(11, "Apple");
        h.put(22, "Orange");
        h.put(33, "Kiwi");
        h.put(44, "Banana");

        Map<Integer, String> hh = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            hh = h.entrySet()
                    .stream()
                    .filter(map -> map.getKey().intValue() <= 22)
                    .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
        }


        //Log.i(TAG, "Result: " + hh);

        Map<Integer, String> m = new HashMap<>();
        m.put(11, "Apple");
        m.put(22, "Orange");
        m.put(33, "Kiwi");
        m.put(44, "Banana");


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Map<Integer, String> mm = m.entrySet()
                    .stream()
                    .filter(map -> "Orange".equals(map.getValue()) || "".equals(map.getValue()) || "".equals(map.getValue()))
                    .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
            //Log.i(TAG, "Result: " + mm);
        }
    }

}
