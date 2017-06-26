package com.castis.service;

import java.util.UUID;

/**
 * Created by trand on 6/25/2017.
 */

public class LocalBeacon {
    double distance;
    String location;
    String name;
    UUID uuid;

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


    public LocalBeacon() {
        this.location = "Loading...";
    }

    public LocalBeacon(UUID uuid, String name, String location, double distance) {
        this.uuid = uuid;
        this.name = name;
        this.location = location;
        this.distance = distance;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
