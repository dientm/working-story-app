package com.castis.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.castis.utils.Constants;
import com.castis.utils.PreferenceUtils;

public class ConfigurationActivity extends AppCompatActivity {


    EditText serverAddress;
    Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        serverAddress = (EditText)findViewById(R.id.server_address);
        String currentAddress = PreferenceUtils.getInstance(getApplicationContext()).getSharedPref().getString(Constants.SERVER, Constants.DEFAULT_SERVER);
        serverAddress.setText(currentAddress);

        btnUpdate = (Button) findViewById(R.id.update_server_address_button);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = !"".equals(serverAddress.getText().toString())? serverAddress.getText().toString() : Constants.DEFAULT_SERVER;
                updateConfiguration(address);
            }
        });
    }

    private void updateConfiguration(String address) {
        PreferenceUtils.getInstance(getApplicationContext()).getSharedPrefEditor().putString(Constants.SERVER, address);
        PreferenceUtils.getInstance(getApplicationContext()).getSharedPrefEditor().apply();
        Toast.makeText(getApplicationContext(), "Update Server: " + address, Toast.LENGTH_LONG);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent i = new Intent(ConfigurationActivity.this, LoginActivity.class);
        startActivity(i);
    }
}
