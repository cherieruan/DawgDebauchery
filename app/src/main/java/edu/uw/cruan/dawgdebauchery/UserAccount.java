package edu.uw.cruan.dawgdebauchery;

/**
 * Created by cherieruan on 11/29/17.
 */

public class UserAccount {

    public String fName;
    public String lName;
    public int imgURL;

    public UserAccount(String f, String l, int url) {
        this.fName = f;
        this.lName = l;
        this.imgURL = url;
    }

    public String toString() {
        return fName + " " + lName;
    }

}
