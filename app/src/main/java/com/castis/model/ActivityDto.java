package com.castis.model;

/**
 * Created by trand on 6/28/2017.
 */
public class ActivityDto {
    private String username;

    private String name;
    private String is_working;


    public String getIs_working() {
        return is_working;
    }

    public void setIs_working(String is_working) {
        this.is_working = is_working;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String toString() {
        return name;
    }

    public String[] toStringArr() {
        return new String[]{name};
    }
}

