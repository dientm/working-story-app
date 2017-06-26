package com.castis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.castis.model.ResponseObject;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public class FinishWorkingActivity extends AppCompatActivity implements BeaconConsumer, RequestService.AsyncResponse {
    LocalBeacon localBeacon = new LocalBeacon();
    String TAG = "FinishWorkingActivity";
    TextView location;
    Button submit;
    EditText report;
    BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_working);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setTitle("Finish working");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                beaconManager.unbind(FinishWorkingActivity.this);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        // set time counter
        TextView current_time_view = (TextView) findViewById(R.id.current_time);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        current_time_view.setText(dateFormat.format(date));
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                TextView current_time_view = (TextView) findViewById(R.id.current_time);
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = new Date();
                                current_time_view.setText(dateFormat.format(date));
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();


        // set name
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(PreferenceUtils.getInstance(this.getApplicationContext()).getSharedPref().getString("name", "Howdy"));


        // set location
//        LocationService locationService = LocationService.getInstance();
//        localBeacon = locationService.getLocalBeacon(BeaconService.getInstance(this.getApplicationContext(), this));
        location = (TextView) findViewById(R.id.location);

        /*BeaconService.getInstance(this.getApplicationContext()).bindService(this);*/
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
        Thread t2 = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView location = (TextView) findViewById(R.id.location);
                                location.setText(localBeacon.getLocation());
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t2.start();


        report = (EditText) findViewById(R.id.report);

        // button submit
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitFinishWorking();
            }
        });
    }

    private void submitFinishWorking() {
        boolean cancel = false;

        String username = PreferenceUtils.getInstance(this.getApplicationContext()).getSharedPref().getString("username", "");
        String location = localBeacon.getLocation();
        report = (EditText) findViewById(R.id.report);
        String strReport = report.getText().toString();
        URL url = null;
        try {
            url = new URL(Constants.WORKING_ACTION_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("username", username);
            jsonBody.put("location", location);
            jsonBody.put("report", strReport);
            jsonBody.put("action", Constants.FINISH_WORKING);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpRequest request = new HttpRequest();
        request.setUrl(url);
        request.setMethod("POST");
        request.setPayLoad(jsonBody);
        new RequestService(this).execute(request);
    }

    UUID preferUUID = null;
    int i = 0;
    @Override
    public void onBeaconServiceConnect() {
        final Region region = new Region("CASTIS", null, null, null);

       beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    if (BeaconService.getInstance(FinishWorkingActivity.this.getApplicationContext())
                            .getAvailableBeaconUUIDs().contains(beacons.iterator().next().getId1().toUuid())){
                        preferUUID = beacons.iterator().next().getId1().toUuid();
                        localBeacon = new LocalBeacon(preferUUID,
                                String.valueOf(beacons.iterator().next().getBluetoothName())
                                , BeaconService.getInstance(FinishWorkingActivity.this.getApplicationContext()).getLocalBeaconByUUID(preferUUID).getLocation()
                                , beacons.iterator().next().getDistance());
                        Log.i(TAG, String.valueOf(beacons.iterator().next().getBluetoothName()));
                        Log.i(TAG, beacons.iterator().next().getDistance() + " meters away.");
                        i = 0;
                    } else {
                        i++;Log.i(TAG, "i = " + i);
                        if (i > 200) {
                            i = 0;
                        }
                        Log.i(TAG, "Beacon not found");
                    }
                }
            }
        });


       try {
            beaconManager.stopRangingBeaconsInRegion(region);
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.e(TAG, e.toString());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void ServerResponse(String response) throws Exception {
        Log.i(TAG, response);
        ResponseObject obj = new ResponseObject();
        Gson gsonParser = new Gson();
        JSONObject responseJson = null;
        try {
            responseJson = new JSONObject(response);
            obj = gsonParser.fromJson(response, ResponseObject.class);

        } catch (Exception e) {
            Log.e(TAG, "response not json format");
            Log.e(TAG, e.toString());
        }
        if (obj.getStatusCode() == 200) {
            BeaconService.getInstance(this.getApplicationContext()).getBeaconManager().stopMonitoringBeaconsInRegion(new Region("ranged region", null, null, null));
            BeaconService.getInstance(this.getApplicationContext()).getBeaconManager().unbind(this);
            Toast.makeText(getBaseContext(), "Done", Toast.LENGTH_LONG).show();
            Intent i = new Intent(FinishWorkingActivity.this, FlashActivity.class);
            startActivity(i);
        } else {

        }
    }


}
