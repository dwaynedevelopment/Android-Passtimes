package com.dwaynedevelopment.passtimes.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Player {

    private String id;
    private String name;
    private String thumbnail;

    public Player() {}

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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("thumbnail", thumbnail);

        return result;
    }
}
