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
    public boolean private_party;
    public List<UserAccount> confirmedGuests;
    public Queue<UserAccount> pendingGuests;

    public Event(String address, String date, String time, String description, boolean private_party) {
        this.address = address;
        this.date = date;
        this.time = time;
        this.description = description;
        this.private_party = private_party;
        confirmedGuests = new ArrayList<UserAccount>();
        pendingGuests = new LinkedList<UserAccount>();
    }

    public String toString() {
        return name + " " + description + " on " + date + " at " + time;
    }
}
