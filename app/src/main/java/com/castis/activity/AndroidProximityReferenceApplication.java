package com.castis.activity;

import android.app.Application;
import android.util.Log;

import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Created by Mark on 6/27/2017.
 */

public class AndroidProximityReferenceApplication extends Application implements BootstrapNotifier {
    private static final String TAG = "AndroidProximity";
    private RegionBootstrap regionBootstrap;

    @Override
    public void onCreate() {
        super.onCreate();
        Region region = new Region("com.example.backgroundRegion",null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }
    @Override
    public void didEnterRegion(Region region) {

        Log.i(TAG, "didEnterRegion");

        // Important:  make sure to add android:launchMode="singleInstance" in the manifest
        // to keep multiple copies of this activity from getting created if the user has
        // already manually launched the app.

    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
