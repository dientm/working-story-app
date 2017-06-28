package com.castis.service;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.castis.activity.LoginActivity;
import com.castis.activity.MainActivity;
import com.castis.activity.R;
import com.castis.model.ActivityDto;
import com.castis.model.ResponseObject;
import com.castis.model.User;
import com.castis.service.RequestService.AsyncResponse;
import com.castis.utils.Constants;
import com.castis.utils.PreferenceUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by trand on 6/28/2017.
 */

public class ActivityService implements AsyncResponse {
    private String TAG = "ActivityService";

    public void reload() {
        URL url = null;
        try {
            url = new URL(Constants.ACTIVITIES_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpRequest request = new HttpRequest();
        request.setUrl(url);
        request.setMethod("GET");
        new RequestService(this).execute(request);
    }

    @Override
    public void ServerResponse(String response) throws Exception {
        Log.i(TAG, response);
        Gson gsonParser123 = new Gson();

        ActivityDto[] activities;
        try {
            activities = gsonParser.fromJson(response, ActivityDto[].class);
            if (activities == null) {

            } else {
                for (ActivityDto activityDto : activities) {
                    Log.i(TAG,activityDto.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
