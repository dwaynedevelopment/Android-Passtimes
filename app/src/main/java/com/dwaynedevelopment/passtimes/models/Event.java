package com.dwaynedevelopment.passtimes.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Event implements Parcelable{

    private String id;
    private String hostId;
    private String hostThumbnail;
    private String sport;
    private String title;
    private double latitude;
    private double longitude;
    private String location;
    private long startDate;
    private long endDate;
    private int maxPlayers;
    private HashMap<String, HashMap<String, String>> playerList;

    public Event() {

    }

    public Event(String hostId, String hostThumbnail, String sport, String title, double latitude, double longitude, String location, long startDate, long endDate, int maxPlayers) {
        this.id = UUID.randomUUID().toString();
        this.hostId = hostId;
        this.hostThumbnail = hostThumbnail;
        this.sport = sport;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxPlayers = maxPlayers;
    }

    protected Event(Parcel in) {
        id = in.readString();
        hostId = in.readString();
        hostThumbnail = in.readString();
        sport = in.readString();
        title = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        location = in.readString();
        startDate = in.readLong();
        endDate = in.readLong();
        maxPlayers = in.readInt();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

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

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public HashMap<String, HashMap<String, String>> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(HashMap<String, HashMap<String, String>> playerList) {
        this.playerList = playerList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(hostId);
        dest.writeString(hostThumbnail);
        dest.writeString(sport);
        dest.writeString(title);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(location);
        dest.writeLong(startDate);
        dest.writeLong(endDate);
        dest.writeInt(maxPlayers);
        dest.writeMap(playerList);
    }
}
