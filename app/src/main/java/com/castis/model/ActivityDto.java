package com.castis.model;

/**
 * Created by trand on 6/28/2017.
 */

public class ActivityDto {
    private String name;
    private String action;
    private String action_on;
    private String note;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction_on() {
        return action_on;
    }

    public void setAction_on(String action_on) {
        this.action_on = action_on;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String toString() {
        String mess =  "" + this.name + " has " + this.action + " at " + this.action_on;
        return mess;
    }
}
