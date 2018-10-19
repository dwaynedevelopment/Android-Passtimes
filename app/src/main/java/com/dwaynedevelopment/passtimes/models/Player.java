package com.dwaynedevelopment.passtimes.models;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Player {

    private String id;
    private String name;
    private String thumbnail;
    private HashMap<String, HashMap<String, String>> favorites;

    public Player() {}

    public Player(String id, String name, String thumbnail, HashMap<String, HashMap<String, String>> favorites) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
        this.favorites = favorites;
    }

    public Player(String id, String name, String thumbnail) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public HashMap<String, HashMap<String, String>> getFavorites() {
        return favorites;
    }

    public void setFavorites(HashMap<String, HashMap<String, String>> favorites) {
        this.favorites = favorites;
    }

    private static final String TAG = "Player";
    public ArrayList<String> getListOfFavoriteSports() {
        ArrayList<String> sports = new ArrayList();
        if(favorites != null) {
            for (Map.Entry<String, HashMap<String, String>> favoriteSports : favorites.entrySet()) {
                String sportsKey = favoriteSports.getKey();
                // ...
                for (Map.Entry<String, String> favSport : favoriteSports.getValue().entrySet()) {
                    String name = favSport.getKey();
                    String sport = favSport.getValue();
                    sports.add(sport);
                    Log.i(TAG, "getListOfFavoriteSports: SPORT " + sport);
                }
            }
        }
        return sports;
    }
}
