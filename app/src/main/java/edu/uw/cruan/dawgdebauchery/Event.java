package edu.uw.cruan.dawgdebauchery;

import java.util.Date;

/**
 * Class that stores all the information related to an event
 */

public class Event {

    public String name;
    public String address;
    public Date date;
    public String time;
    public String description;

    public Event(String address, Date date, String time, String description) {
        this.address = address;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    public String toString() {
        return name + " " + description + " on " + date + " at " + time;

    }
}
