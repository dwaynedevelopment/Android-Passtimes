package com.dwaynedevelopment.passtimes.models;

import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

public class Event {

    private String id;
    private String hostId;
    private String hostThumbnail;
    private String sport;
    private String title;
    private double latitude;
    private double longitude;
    private String location;
    private long date;
    private int maxPlayers;
    private Map<String, Map<String, String>> playerList;

    public Event() {

    }


    public Event(String hostId, String hostThumbnail, String sport, String title, double latitude, double longitude, String location, long date, int maxPlayers) {
        this.id = UUID.randomUUID().toString();
        this.hostId = hostId;
        this.hostThumbnail = hostThumbnail;
        this.sport = sport;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.date = date;
        this.maxPlayers = maxPlayers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getHostThumbnail() {
        return hostThumbnail;
    }

    public void setHostThumbnail(String hostThumbnail) {
        this.hostThumbnail = hostThumbnail;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
