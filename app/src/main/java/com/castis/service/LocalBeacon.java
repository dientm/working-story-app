package com.castis.service;

/**
 * Created by trand on 6/25/2017.
 */

public class LocalBeacon {
    double distance;
    String location;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;
    public LocalBeacon() {
        this.location = "Loading...";
    }

    public LocalBeacon(String name, String location, double distance) {
        this.name = name;
        this.location = location;
        this.distance = distance;
    }
}
