package com.castis.activity;

import android.content.Intent;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.castis.model.ResponseObject;
import com.castis.model.User;
import com.castis.service.BeaconService;
import com.castis.service.HttpRequest;
import com.castis.service.LocalBeacon;
import com.castis.service.LocationService;
import com.castis.service.RequestService;
import com.castis.utils.PreferenceUtils;
import com.google.gson.Gson;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public class FinishWorkingActivity extends ActionBarActivity implements BeaconConsumer, RequestService.AsyncResponse {
    LocalBeacon localBeacon = new LocalBeacon();
    String TAG = "FinishWorkingActivity";
    TextView location;
    Button submit;
    EditText report;
    int i = 0;

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
                BeaconService.getInstance(FinishWorkingActivity.this.getApplicationContext()).getBeaconManager().unbind(FinishWorkingActivity.this);
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
        BeaconService.getInstance(this.getApplicationContext()).bindService(this);

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
            url = new URL("http://192.168.0.108:8000/worklog/finishworking");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("username", username);
            jsonBody.put("location", location);
            jsonBody.put("report", strReport);

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

    @Override
    public void onBeaconServiceConnect() {

        BeaconService.getInstance(this.getApplicationContext()).getBeaconManager().addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                if (beacons.size() > 0) {
                    preferUUID = beacons.iterator().next().getId1().toUuid();
                    if (BeaconService.getInstance(FinishWorkingActivity.this.getApplicationContext()).getAvailableBeaconUUIDs().contains(preferUUID)) {
                        localBeacon = new LocalBeacon(String.valueOf(beacons.iterator().next().getBluetoothName()), "Dien HOme", beacons.iterator().next().getDistance());
                        Log.i(TAG, String.valueOf(beacons.iterator().next().getBluetoothName()));
                        Log.i(TAG, beacons.iterator().next().getDistance() + " meters away.");
                        i = 0;
                    }
                } else {
                    i++;
                    Log.i(TAG, "i = " + i);
                    if (i > 200) {
                        i = 0;
                    }
                    Log.i(TAG, "Beacon not found");
                }
            }
        });

        try {
            if (null == preferUUID) {
                BeaconService.getInstance(this.getApplicationContext()).getBeaconManager()
                        .startRangingBeaconsInRegion(new Region("ranged region", null, null, null));

            } else {
                Log.i(TAG, "Using prefer UUID " + preferUUID);
                BeaconService.getInstance(this.getApplicationContext()).getBeaconManager()
                        .startRangingBeaconsInRegion(new Region("ranged region", Identifier.fromUuid(preferUUID), null, null));
            }
            /*BeaconService.getInstance(this.getApplicationContext()).getBeaconManager()
                    .startRangingBeaconsInRegion(new Region("ranged region", Identifier.fromUuid(UUID.fromString("FDA50693-A4E2-4FB1-AFCF-C6EB07647825")), null, null));*/
        } catch (RemoteException e) {
        }

    }

    @Override
    protected void onDestroy() {
        BeaconService.getInstance(this.getApplicationContext()).getBeaconManager().unbind(this);
        super.onDestroy();
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
            BeaconService.getInstance(this.getApplicationContext()).getBeaconManager().unbind(this);
            Toast.makeText(getBaseContext(), "Done", Toast.LENGTH_LONG).show();
            Intent i = new Intent(FinishWorkingActivity.this, FlashActivity.class);

            startActivity(i);
        } else {

        }
    }
}
