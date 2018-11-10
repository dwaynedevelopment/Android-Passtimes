package com.dwaynedevelopment.passtimes.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Player implements Parcelable {

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

    protected Player(Parcel in) {
        id = in.readString();
        name = in.readString();
        thumbnail = in.readString();
        overallXP = in.readInt();
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(thumbnail);
        dest.writeInt(overallXP);
    }

    public static class PlayerComparator implements Comparator<Player> {
        @Override
        public int compare(Player o1, Player o2) {
            return Long.compare(o1.overallXP, o2.overallXP);
        }
    }
}
