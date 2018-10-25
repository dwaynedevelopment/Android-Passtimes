package com.dwaynedevelopment.passtimes.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Sport implements Parcelable {

    private String id;
    private String category;
    private String idle;
    private String active;

    public Sport() { }

    public Sport(String id, String category, String idle, String active) {
        this.id = id;
        this.category = category;
        this.idle = idle;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIdle() {
        return idle;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIdle(String idle) {
        this.idle = idle;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Sport{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", idle='" + idle + '\'' +
                ", active='" + active + '\'' +
                '}';
    }

    private Sport(Parcel in) {
        id = in.readString();
        category = in.readString();
        idle = in.readString();
        active = in.readString();
    }

    public static final Creator<Sport> CREATOR = new Creator<Sport>() {
        @Override
        public Sport createFromParcel(Parcel in) {
            return new Sport(in);
        }

        @Override
        public Sport[] newArray(int size) {
            return new Sport[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(category);
        dest.writeString(idle);
        dest.writeString(active);
    }
}
