package com.castis.service;

/**
 * Created by trand on 6/25/2017.
 */

public class LocationService {
    private static String TAG = "LocationService";

    private static LocationService instance;

    public static LocationService getInstance() {
        if (instance == null) {
            instance = new LocationService();
        }
        return instance;
    }

    public LocalBeacon getLocalBeacon(BeaconService beaconService) {
        return beaconService.getLocation();
    }
}
