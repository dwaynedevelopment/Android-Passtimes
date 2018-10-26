package com.dwaynedevelopment.passtimes.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;
import java.util.UUID;

public class Event implements Parcelable {

    private String id;
    private String sport;
    private String title;
    private String location;
    private double latitude;
    private double longitude;
    private long startDate;
    private long endDate;
    private int maxAttendees;
    private DocumentReference eventHost;
    private List<DocumentReference> attendees;

    public Event() { }

    public Event(DocumentReference eventHost, String sport, String title, double latitude, double longitude,
                 String location, long startDate, long endDate, int maxAttendees) {
        this.id = UUID.randomUUID().toString();
        this.eventHost = eventHost;
        this.sport = sport;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxAttendees = maxAttendees;
    }

    private Event(Parcel in) {
        id = in.readString();
        sport = in.readString();
        title = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        location = in.readString();
        startDate = in.readLong();
        endDate = in.readLong();
        maxAttendees = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(sport);
        dest.writeString(title);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(location);
        dest.writeLong(startDate);
        dest.writeLong(endDate);
        dest.writeInt(maxAttendees);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public DocumentReference getEventHost() {
        return eventHost;
    }

    public void setEventHost(DocumentReference eventHost) {
        this.eventHost = eventHost;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", sport='" + sport + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public List<DocumentReference> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<DocumentReference> attendees) {
        this.attendees = attendees;
    }


}
