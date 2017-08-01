package com.castis.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.castis.model.ChatMessage;
import com.castis.model.ResponseObject;
import com.castis.service.HttpRequest;
import com.castis.service.RequestService;
import com.castis.utils.Constants;
import com.castis.utils.PreferenceUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FinishWorkingActivity extends AppCompatActivity implements RequestService.AsyncResponse {

    String TAG = "FinishWorkingActivity";
    TextView location;
    Button submit;
    EditText report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_working);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setTitle("Finish working");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                beaconManager.unbind(FinishWorkingActivity.this);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        // avatar
        ImageView avatar = (ImageView) findViewById(R.id.avatar);
        Picasso.with(this).load(PreferenceUtils.getInstance(this.getApplicationContext()).getSharedPref().getString(Constants.SERVER, Constants.DEFAULT_SERVER) + Constants.GET_AVATAR_URI
                + PreferenceUtils.getInstance(this).getSharedPref().getString("username", "")).resize(100, 100).into(avatar);
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
                                location.setText(PreferenceUtils.getInstance(FinishWorkingActivity.this).getSharedPref().getString("location", "N/A"));
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
        showDialog();
        boolean cancel = false;

        String username = PreferenceUtils.getInstance(this.getApplicationContext()).getSharedPref().getString("username", "");
        report = (EditText) findViewById(R.id.report);
        String strReport = report.getText().toString();
        URL url = null;
        try {

            url = new URL(PreferenceUtils.getInstance(this.getApplicationContext()).getSharedPref().getString(Constants.SERVER, Constants.DEFAULT_SERVER) + Constants.WORKING_ACTION_URI);
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
        new RequestService(this, getApplicationContext()).execute(request);
    }


    @Override
    protected void onDestroy() {
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
            FirebaseDatabase.getInstance().getReference().push().setValue(
                    new ChatMessage(
                            PreferenceUtils.getInstance(getApplicationContext()).getSharedPref().getString("name", "")
                                    + " has finished working",
//                    FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                            "bot@castis.com"));

            Thread.sleep(1000);
            hideDialog();
            Toast.makeText(getBaseContext(), "Done", Toast.LENGTH_LONG).show();
            Intent i = new Intent(FinishWorkingActivity.this, MainActivity.class);
            i.putExtra("message", obj.getMessage());
            startActivity(i);
        } else {

        }
    }

    AlertDialog malertDialog;

    public void showDialog() {
        malertDialog = new AlertDialog.Builder(FinishWorkingActivity.this).create();
        malertDialog.setTitle("Finsh working");
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

}
