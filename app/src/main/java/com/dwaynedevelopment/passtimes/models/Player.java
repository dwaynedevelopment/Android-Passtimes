package com.dwaynedevelopment.passtimes.models;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String id;
    private String name;
    private String thumbnail;
    private int overallXP;
    private List<DocumentReference> favorites = new ArrayList<>();
    private List<DocumentReference> attending = new ArrayList<>();

    public Player() {}

    public Player(String id, String name, String thumbnail) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
        this.overallXP = 0;
    }

    public int getOverallXP() {
        return overallXP;
    }

    public void setOverallXP(int overallXP) {
        this.overallXP = overallXP;
    }

    public List<DocumentReference> getAttending() {
        return attending;
    }

    public void setAttending(List<DocumentReference> attending) {
        this.attending = attending;
    }

    public List<DocumentReference> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<DocumentReference> favorites) {
        this.favorites = favorites;
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

    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
