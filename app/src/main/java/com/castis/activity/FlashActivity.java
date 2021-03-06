package com.castis.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.castis.model.AvailableBeacons;
import com.castis.service.BeaconService;
import com.castis.service.HttpRequest;
import com.castis.service.LocalBeacon;
import com.castis.service.RequestService;
import com.castis.utils.Constants;
import com.castis.utils.PreferenceUtils;
import com.google.gson.Gson;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


public class FlashActivity extends AppCompatActivity implements RequestService.AsyncResponse {


    protected static final String TAG = "FlashActivity";
    public static final String MyPREFERENCES = "MyPrefs";
    Intent i;
    boolean isLogin;
//    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_flash);

        // check login
        if (!userAlreadyLogin()) {
            i = new Intent(FlashActivity.this, LoginActivity.class);
        } else {
            i = new Intent(FlashActivity.this, MainActivity.class);

        }
        URL url = null;
        try {
            url = new URL(PreferenceUtils.getInstance(this.getApplicationContext()).getSharedPref().getString(Constants.SERVER, Constants.DEFAULT_SERVER) + Constants.BEACON_CONFIG_URI);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        HttpRequest request = new HttpRequest();
        request.setUrl(url);
        request.setMethod("GET");

        new RequestService(this, getApplicationContext()).execute(request);


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();

        }/* else {
            Toast.makeText(getApplicationContext(), "Bluetooth Al-Ready Enable", Toast.LENGTH_LONG).show();
        }*/

        /*BeaconService.getInstance(this.getApplicationContext()).bindService(this);*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean userAlreadyLogin() {
        return PreferenceUtils.getInstance(this.getApplicationContext()).getSharedPref().getBoolean("isLogin", Boolean.FALSE);

    }

    @Override
    public void ServerResponse(String response) throws Exception {
        Log.i(TAG, response);
        AvailableBeacons obj = new AvailableBeacons();
        List<com.castis.model.Beacon> beacons = new ArrayList<com.castis.model.Beacon>();
        Gson gsonParser = new Gson();
        JSONObject responseJson = null;
        try {
            responseJson = new JSONObject(response);
            obj = gsonParser.fromJson(response, AvailableBeacons.class);
            BeaconService.getInstance(FlashActivity.this.getApplicationContext()).setAvailableBeacons(obj);

        } catch (Exception e) {
            Log.e(TAG, "response not json format");
            Log.e(TAG, e.toString());
        }
        startActivity(i);
        finish();
    }


}
