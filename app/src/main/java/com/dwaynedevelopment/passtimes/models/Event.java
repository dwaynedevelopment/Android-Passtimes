package com.dwaynedevelopment.passtimes.models;

import java.util.Calendar;

public class Event {

    private String id;
    private String title;
    private String location;
    private String day;

    public Event() {
    }

    public Event(String title, String location, String day) {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
