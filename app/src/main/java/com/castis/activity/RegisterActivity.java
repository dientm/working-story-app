package com.castis.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.castis.model.ResponseObject;
import com.castis.model.User;
import com.castis.service.HttpRequest;
import com.castis.service.RequestService;
import com.castis.utils.Constants;
import com.castis.utils.PreferenceUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity implements RequestService.AsyncResponse {
    private final String TAG = "RegisterActivity";

    // UI references.
    private EditText email;
    private EditText name;
    private EditText passwd;
    private EditText repasswd;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setTitle("Register");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                beaconManager.unbind(FinishWorkingActivity.this);
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        email = (EditText) findViewById(R.id.email);
        name = (EditText) findViewById(R.id.name);
        passwd = (EditText) findViewById(R.id.password);
        repasswd = (EditText) findViewById(R.id.repassword);

        btnRegister = (Button) findViewById(R.id.register_button);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        // do login
        URL url = null;
        try {
            url = new URL(Constants.REGISTER_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("email", email.getText());
            jsonBody.put("password", passwd.getText());
            jsonBody.put("name", name.getText());
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

        } catch (Exception e) {
            Log.e(TAG, "response not json format");
            Log.e(TAG, e.toString());
        }
        if (obj.getStatusCode() == 200) {
            Toast.makeText(getBaseContext(), "Register successfully", Toast.LENGTH_LONG).show();
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }
}
