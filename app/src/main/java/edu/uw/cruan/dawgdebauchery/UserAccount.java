package edu.uw.cruan.dawgdebauchery;

/**
 * Created by cherieruan on 11/29/17.
 */

public class UserAccount {

    public String fName;
    public String lName;
    public String imgURL;
    public Event event;

    public UserAccount(String f, String l, String url) {
        this.fName = f;
        this.lName = l;
        this.imgURL = url;
    }

    public void addEvent(Event event) {
        this.event = event;
    }

    public String toString() {
        return fName + " " + lName;
    }

}
