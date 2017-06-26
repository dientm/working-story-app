package com.castis.service;

import android.content.Context;

import com.castis.model.AvailableBeacons;
import com.castis.model.Beacon;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Mark on 6/25/2017.
 */

public class BeaconService {
    private static String TAG = "BeaconService";
    private static BeaconService instance;

    private BeaconManager beaconManager;
    private Region region;
    private UUID[] uuid;

    static LocalBeacon localBeacon;
    private AvailableBeacons availableBeacons;
    private List<UUID> availableBeaconUUIDs;

    public BeaconService(Context context) {

        uuid = new UUID[]{UUID.fromString("FDA50693-A4E2-4FB1-AFCF-C6EB07647825")};

        beaconManager = BeaconManager.getInstanceForApplication(context);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
    }

    public void bindService(BeaconConsumer consumer) {
        beaconManager.unbind(consumer);
        beaconManager.bind(consumer);
    }

    public void unbindService(BeaconConsumer consumer) {
        beaconManager.unbind(consumer);

    }
    public static BeaconService getInstance(Context context) {
        // beacon
        if (instance == null) {
            instance = new BeaconService(context);
        }
        return instance;
    }

    public LocalBeacon getLocation() {
        return localBeacon;
    }

    public BeaconManager getBeaconManager() {
        return beaconManager;
    }

    public AvailableBeacons getAvailableBeacons() {
        return availableBeacons;
    }

    public void setAvailableBeacons(AvailableBeacons availableBeacons) {
        this.availableBeacons = availableBeacons;
    }

    public List<UUID> getAvailableBeaconUUIDs() {
        List<UUID> lst = new ArrayList<UUID>();
        for (Beacon beacon : availableBeacons.getBeacons()) {
            lst.add(beacon.getUuid());
        }
        return lst;
    }

    public void setAvailableBeaconUUIDs(List<UUID> availableBeaconUUIDs) {
        this.availableBeaconUUIDs = availableBeaconUUIDs;
    }

    public LocalBeacon getLocalBeaconByUUID(UUID uuid) {
        LocalBeacon localBeacon = null;
        for (Beacon beacon : availableBeacons.getBeacons()) {
            if (beacon.getUuid().equals(uuid)) {
                localBeacon =  new LocalBeacon(uuid, beacon.getName(), beacon.getLocation(),0.0);
            }
        }
        return localBeacon;
    }

}
