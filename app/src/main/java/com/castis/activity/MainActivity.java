package com.castis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.castis.fragment.HomeFragment;
import com.castis.fragment.ReportFragment;
import com.castis.model.ResponseObject;
import com.castis.service.ActivityService;
import com.castis.service.BeaconService;
import com.castis.service.HttpRequest;
import com.castis.service.LocalBeacon;
import com.castis.service.RequestService;
import com.castis.utils.Constants;
import com.castis.utils.PreferenceUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener,
        ReportFragment.OnFragmentInteractionListener, BeaconConsumer {
    protected static final String TAG = "MainActivity";
    BeaconManager beaconManager  ;

    TextView message;

    ListView  timeline_activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String initMessage =  getIntent().getStringExtra("message");
        // R.id.textview_home_great
/*        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_home);
        message = (TextView) homeFragment.getView().findViewById(R.id.textview_home_great);


        if (initMessage != null) {
            message.setText(initMessage);
        }*/
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);

        timeline_activity = (ListView) findViewById(R.id.timeline_activity);
        // start get activity
        loadActivity();

        if (savedInstanceState == null) {
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = HomeFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);*/
        FloatingActionMenu fab = (FloatingActionMenu) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // event click button start working
        FloatingActionButton button_start_working = (FloatingActionButton) findViewById(R.id.button_start_working);
        button_start_working.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performStartWorking();
            }
        });

       // event click button finish working
        com.github.clans.fab.FloatingActionButton fab_finish_working = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.button_finish_working);
        fab_finish_working.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(MainActivity.this, FinishWorkingActivity.class);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // display nav_header
        TextView name_view = (TextView) findViewById(R.id.name);
        name_view.setText(PreferenceUtils.getInstance(this).getSharedPref().getString("name","Castis User"));
        TextView email_view = (TextView) findViewById(R.id.email);
        email_view.setText(PreferenceUtils.getInstance(this).getSharedPref().getString("email", ""));




    }

    private void loadActivity() {

        Thread t2 = new Thread() {

            @Override
            public void run() {
                try {
                        while(!isInterrupted()) {
                            timeline_activity = (ListView) findViewById(R.id.timeline_activity);
                            new ActivityService(MainActivity.this, timeline_activity);
                            Thread.sleep(1000);
                        }


                } catch (InterruptedException e) {
                }
            }
        };

        t2.start();
    }


    boolean isLocated = false;
    private void performStartWorking() {
        showDialog();

        Thread t2 = new Thread() {

            @Override
            public void run() {
                try {

                    while (!isInterrupted()) {

                        if (!"".equalsIgnoreCase(PreferenceUtils.getInstance(MainActivity.this.getApplicationContext()).getSharedPref().getString("location",""))) {
                            // do post
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new StartWorking().doStart(PreferenceUtils.getInstance(MainActivity.this).getSharedPref().getString("location", "").toString(), "");
                                }
                            });

                            Thread.currentThread().interrupt();
                            return;
                        } else {
                            Thread.sleep(500);
                            if (count > 10) {
                                isLocated = false;
                                break;
//                                Thread.currentThread().interrupt();


                            }
                        }
                    }
                } catch (InterruptedException e) {
                }
                if (!isLocated) {
                    hideDialog();
                    showPopUp();
                }
            }
            void showPopUp() {
                LayoutInflater li = LayoutInflater.from(MainActivity.this.getApplicationContext());
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MainActivity.this.getApplicationContext());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.note);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // do post
                                        new StartWorking().doStart(userInput.getText().toString(), "AAA");
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        };
        t2.start();

    }
    AlertDialog alertDialog;
    private class StartWorking implements RequestService.AsyncResponse{

        public void doStart(String location, String report) {
            URL url = null;
            try {
                url = new URL(Constants.WORKING_ACTION_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONObject jsonBody = new JSONObject();

            try {
                jsonBody.put("username", PreferenceUtils.getInstance(MainActivity.this).getSharedPref().getString("username",""));

                jsonBody.put("location", location);
                jsonBody.put("report", report);
                jsonBody.put("action", Constants.START_WORKING);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            HttpRequest request = new HttpRequest();
            request.setUrl(url);
            request.setMethod("POST");
            request.setPayLoad(jsonBody);
            new RequestService(this).execute(request);
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
                message = (TextView) findViewById(R.id.textview_home_great);
                message.setText(obj.getMessage().toString());
                Thread.sleep(1000);
                Toast.makeText(getBaseContext(), "Success", Toast.LENGTH_LONG).show();
                hideDialog();
            } catch (Exception e) {
                Log.e(TAG, "response not json format");
                Log.e(TAG, e.toString());
            }

        }
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        if (id == R.id.nav_dashboard) {
            fragmentClass = HomeFragment.class;
        } else if (id == R.id.nav_report) {
            fragmentClass = ReportFragment.class;
        } else if (id == R.id.nav_logout) {

            PreferenceUtils.getInstance(this.getApplicationContext()).getSharedPrefEditor().clear();
            PreferenceUtils.getInstance(this.getApplicationContext()).getSharedPrefEditor().commit();
            Intent i = new Intent(MainActivity.this, FlashActivity.class);
            startActivity(i);
            return true;
        } else {
            fragmentClass = HomeFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onPause()  {
        super.onPause();
        if (beaconManager.isBound(this)) {
            beaconManager.setBackgroundMode(false);
        }
    }


    AlertDialog malertDialog;
    public void showDialog() {
        malertDialog = new AlertDialog.Builder(MainActivity.this).create();
        malertDialog.setTitle("Start working");
        malertDialog.setMessage("Please wait...");
        /*alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });*/
        malertDialog.show();
    }
    public void hideDialog() {
        malertDialog.dismiss();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) {
            beaconManager.setBackgroundMode(false);
        }
    }
    LocalBeacon localBeacon = new LocalBeacon();
    UUID preferUUID = null;
    int count = 0;
    final Region region = new Region("CASTIS", null, null, null);
    @Override
    public void onBeaconServiceConnect() {


        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    if (BeaconService.getInstance(MainActivity.this.getApplicationContext())
                            .getAvailableBeaconUUIDs().contains(beacons.iterator().next().getId1().toUuid())) {
                        preferUUID = beacons.iterator().next().getId1().toUuid();
                        localBeacon = new LocalBeacon(preferUUID,
                                String.valueOf(beacons.iterator().next().getBluetoothName())
                                , BeaconService.getInstance(MainActivity.this.getApplicationContext()).getLocalBeaconByUUID(preferUUID).getLocation()
                                , beacons.iterator().next().getDistance());
                        Log.i(TAG, String.valueOf(beacons.iterator().next().getBluetoothName()));
                        Log.i(TAG, beacons.iterator().next().getDistance() + " meters away.");
                        count = 0;
                        PreferenceUtils.getInstance(MainActivity.this).getSharedPrefEditor().putString("location", localBeacon.getLocation());
                        PreferenceUtils.getInstance(MainActivity.this).getSharedPrefEditor().commit();
                    }
                } else {
                    count++;
                    Log.i(TAG, "i = " + count);
                    if (count > 10) {
                        PreferenceUtils.getInstance(MainActivity.this).getSharedPrefEditor().putString("location", "");
                        PreferenceUtils.getInstance(MainActivity.this).getSharedPrefEditor().commit();
                    }
                    if (count > 20) {
                        count = 0;
                    }
                    Log.i(TAG, "Beacon not found");
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
}
