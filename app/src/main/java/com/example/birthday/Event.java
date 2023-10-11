package com.example.birthday;

public class Event {
    private String eventName;
    private String eventDate;
    private int eventColor;

    public Event(String eventName, String eventDate, int eventColor) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventColor = eventColor;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public int getEventColor() {
        return eventColor;
    }
}
