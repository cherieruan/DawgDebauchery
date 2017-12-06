package edu.uw.cruan.dawgdebauchery;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Class that stores all the information related to an event
 */

public class Event {

    public String name;
    public String address;
    public String date;
    public String time;
    public String description;
    public String id;
    public List<String> confirmedGuests;
    public Queue<String> pendingGuests;
    public boolean private_party;

    public Event() {

    }

    public Event(String name, String address, String date, String time, String description, String id,
                 boolean private_party) {
        this.name = name;
        this.address = address;
        this.date = date;
        this.time = time;
        this.description = description;
        this.id = id;
        this.private_party = private_party;
        confirmedGuests = new ArrayList<String>();
        pendingGuests = new LinkedList<String>();
    }

    public String toString() {
        return name + " " + description + " on " + date + " at " + time;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getConfirmedGuests() {
        return confirmedGuests;
    }

    public Queue<String> getPendingGuests() {
        return pendingGuests;
    }

    public boolean getPrivate_party() {
        return this.private_party;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setConfirmedGuests(List<String> confirmedGuests) {
        this.confirmedGuests = confirmedGuests;
    }

    public void setPendingGuests(Queue<String> pendingGuests) {
        this.pendingGuests = pendingGuests;
    }

    public void setPrivate_party(boolean private_party) {
        this.private_party = private_party;
    }
}
