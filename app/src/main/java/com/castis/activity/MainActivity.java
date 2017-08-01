package com.castis.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
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
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.castis.fragment.HomeFragment;
import com.castis.fragment.IncommingFragment;
import com.castis.model.ChatMessage;
import com.castis.model.ResponseObject;
import com.castis.service.ActivityService;
import com.castis.service.BeaconService;
import com.castis.service.HttpRequest;
import com.castis.service.LocalBeacon;
import com.castis.service.RequestService;
import com.castis.utils.Constants;
import com.castis.utils.PreferenceUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

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
import java.util.Collection;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener,
        IncommingFragment.OnFragmentInteractionListener, BeaconConsumer {
    protected static final String TAG = "MainActivity";
    BeaconManager beaconManager;

    TextView message;
    AlertDialog alertDialog;
    ListView timeline_activity;

    ListView list_user_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String initMessage = getIntent().getStringExtra("message");
        // R.id.textview_home_great
/*        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_home);
        message = (TextView) homeFragment.getView().findViewById(R.id.textview_home_great);


        if (initMessage != null) {
            message.setText(initMessage);
        }*/

        loadActivity();
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
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
        /*FloatingActionMenu fab = (FloatingActionMenu) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        // event click button start working



        /*FloatingActionButton button_start_working = (FloatingActionButton) findViewById(R.id.button_start_working);
        button_start_working.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
                performStartWorking();



            }
        });*/

        // event click button finish working
        /*com.github.clans.fab.FloatingActionButton fab_finish_working = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.button_finish_working);
        fab_finish_working.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(MainActivity.this, FinishWorkingActivity.class);
                startActivity(i);
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // draw here 1111
        /*Menu menu = navigationView.getMenu();

        for (int i = 1; i < 5; i++) {
            *//*SubMenu subMenu = menu.addSubMenu(R.id.nav_view, 999, Menu.NONE, "user " + i);
            subMenu.setIcon(R.drawable.smiley);*//*

            MenuItem user = menu.add(999, i, 999, "user  " + i);
            user.setIcon(R.drawable.smiley);

        }
*/

        View header = navigationView.getHeaderView(0);
        // display nav_header
        TextView name_view = (TextView) header.findViewById(R.id.full_name);
        name_view.setText(PreferenceUtils.getInstance(this).getSharedPref().getString("name", "Castis User"));
        TextView email_view = (TextView) header.findViewById(R.id.email);
        email_view.setText(PreferenceUtils.getInstance(this).getSharedPref().getString("email", ""));
        ImageView avatar = (ImageView) header.findViewById(R.id.avatar);
        Picasso.with(this).load(PreferenceUtils.getInstance(getApplicationContext()).getSharedPref().getString(Constants.SERVER, Constants.DEFAULT_SERVER) + Constants.GET_AVATAR_URI + PreferenceUtils.getInstance(this).getSharedPref().getString("username", "")).resize(150, 150).into(avatar);

    }

    Thread loadActivitythread;

    private void loadActivity() {

        loadActivitythread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
//                            timeline_activity = (ListView) findViewById(R.id.timeline_activity);
                        list_user_status = (ListView) findViewById((R.id.list_user_status));
                        new ActivityService(MainActivity.this, list_user_status);
                        Thread.sleep(1000);
                    }


                } catch (InterruptedException e) {
                }
            }
        };

        loadActivitythread.start();
    }



    boolean isLocated = false;

    private void performStartWorking() {


        int i = 0;
        while (!isLocated) {

            i++;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!"".equalsIgnoreCase(PreferenceUtils.getInstance(MainActivity.this.getApplicationContext()).getSharedPref().getString("location", ""))) {
                // do post
                isLocated = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new StartWorking().doStart(PreferenceUtils.getInstance(MainActivity.this).getSharedPref().getString("location", "").toString(), "");
                    }
                });


            }
            if (i > 20 && !isLocated || isLocated) break;
        }
        if (!isLocated) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    hideDialog();
                    showPopUp();
                }
            });
        }

        isLocated = false;

    }

    void showPopUp() {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.note);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // do post
                                new StartWorking().doStart(userInput.getText().toString(), "AAA");
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                hideDialog();
                            }
                        });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // show it
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.show();
            }
        });
    }


    private class StartWorking implements RequestService.AsyncResponse {

        public void doStart(String location, String report) {
            URL url = null;
            try {
                url = new URL(PreferenceUtils.getInstance(getApplicationContext()).getSharedPref().getString(Constants.SERVER, Constants.DEFAULT_SERVER) + Constants.WORKING_ACTION_URI);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONObject jsonBody = new JSONObject();

            try {
                jsonBody.put("username", PreferenceUtils.getInstance(MainActivity.this).getSharedPref().getString("username", ""));

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
            new RequestService(this, getApplicationContext()).execute(request);
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
                FirebaseDatabase.getInstance().getReference().push().setValue(
                        new ChatMessage(
                                PreferenceUtils.getInstance(getApplicationContext()).getSharedPref().getString("name", "")
                                        + " has started working",
//                    FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                                "bot@castis.com"));
                Thread.sleep(1000);
                Toast.makeText(getBaseContext(), "Success", Toast.LENGTH_LONG).show();
                hideDialog();
            } catch (Exception e) {
                Log.e(TAG, "response not json format");
                Log.e(TAG, e.toString());
            }

        }
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
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
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        if (id == R.id.action_start_working) {
            showDialog();
            performStartWorking();
            return true;
        }

        if (id == R.id.action_finish_working) {
            Intent i = new Intent(MainActivity.this, FinishWorkingActivity.class);
            startActivity(i);
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
        } else if (id == R.id.nav_logout) {
            loadActivitythread.interrupt();
            beaconManager.unbind(this);
            FirebaseAuth.getInstance().signOut();
            PreferenceUtils.getInstance(this.getApplicationContext()).getSharedPrefEditor().clear();
            PreferenceUtils.getInstance(this.getApplicationContext()).getSharedPrefEditor().commit();
            Intent i = new Intent(MainActivity.this, FlashActivity.class);
            startActivity(i);
            return true;
        } /*else if (id == R.id.nav_manage) {
            fragmentClass = IncommingFragment.class;
        } */ else {
            fragmentClass = IncommingFragment.class;
           /* NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);*/
            // draw here
            /*Menu menu = navigationView.getMenu();
            for (int i = 2; i < 7; i++) {
                MenuItem iteml;
                try {

                    iteml = menu.getItem(i);
                    iteml.setIcon(R.drawable.smiley);
                    iteml.setTitle("User " + (i + 1));
                } catch (Exception e) {
                    menu.add(999, i, 999, "user  " + (i + 1));
                }
                *//*SubMenu subMenu = menu.addSubMenu(R.id.nav_view, 999, Menu.NONE, "user " + i);
                subMenu.setIcon(R.drawable.smiley);*//*
                *//*MenuItem user = menu.add(3, i, Menu.NONE , "User " + (i + 1) );
                user.setIcon(R.drawable.smiley);*//*
            }
            return true;*/
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
    public void onPause() {
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
                    }else {
                        count++;
                        Log.i(TAG, "i = " + count);
                        if (count > 10) {
                            PreferenceUtils.getInstance(MainActivity.this).getSharedPrefEditor().putString("location", "");
                            PreferenceUtils.getInstance(MainActivity.this).getSharedPrefEditor().commit();
                        }
                        if (count > 20) {
                            try {
                                Thread.currentThread().sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (!mBluetoothAdapter.isEnabled()) {
                                mBluetoothAdapter.enable();

                            }
                            count = 0;
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
}
