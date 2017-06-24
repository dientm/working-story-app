package com.castis.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;


public class FlashActivity extends ActionBarActivity {

    protected static final String TAG = "FlashActivity";
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    public static String USERNAME = "username";
    public static String LAST_LOGIN = "last login";
    Intent i;
    boolean isLogin;
//    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        // check if user Login or not
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username = sharedpreferences.getString(USERNAME, null);
        String last_login = sharedpreferences.getString(LAST_LOGIN, null);
        if (true) {
            i = new Intent(FlashActivity.this, LoginActivity.class);
        } else {
            i = new Intent(FlashActivity.this, MainActivity.class);
        }
        startActivity(i);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();

        }else{
            Toast.makeText(getApplicationContext(), "Bluetooth Al-Ready Enable", Toast.LENGTH_LONG).show();
        }

//        beaconManager = BeaconManager.getInstanceForApplication(this);
//        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
//        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
//        // beaconManager.getBeaconParsers().add(new BeaconParser().
//        //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
//        beaconManager.getBeaconParsers().add(new BeaconParser().
//                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
//        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        beaconManager.unbind(this);
    }
//    @Override
//    public void onBeaconServiceConnect() {
//        beaconManager.addRangeNotifier(new RangeNotifier() {
//            @Override
//            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
//                if (beacons.size() > 0) {
//                    Log.i(TAG, String.valueOf(beacons.iterator().next().getServiceUuid()));
//                    Log.i(TAG, String.valueOf(beacons.iterator().next().getDataFields()));
//                    Log.i(TAG, String.valueOf(beacons.iterator().next().getManufacturer()));
//                    Log.i(TAG, String.valueOf(beacons.iterator().next().getBeaconTypeCode()));
//                    Log.i(TAG, String.valueOf(beacons.iterator().next().getBluetoothName()));
//                    Log.i(TAG, String.valueOf(beacons.iterator().next().getParserIdentifier()));
//                    Log.i(TAG, String.valueOf(beacons.iterator().next().getId1()));
//                    Log.i(TAG, String.valueOf(beacons.iterator().next().getId2()));
//                    Log.i(TAG, String.valueOf(beacons.iterator().next().getId3()));
//                    Log.i(TAG, String.valueOf(beacons.iterator().next().getTxPower()));
//                    Log.i(TAG, String.valueOf(beacons.iterator().next().getRssi()));
//                    Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
//                } else {
//                    Log.i(TAG, "Beacon not found");
//                }
//            }
//        });
//
//        try {
//            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
//        } catch (RemoteException e) {    }
//    }
}
