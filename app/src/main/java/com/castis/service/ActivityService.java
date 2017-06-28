package com.castis.service;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.castis.activity.MainActivity;
import com.castis.activity.R;
import com.castis.model.ActivityDto;
import com.castis.service.RequestService.AsyncResponse;
import com.castis.utils.Constants;
import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by trand on 6/28/2017.
 */

public class ActivityService implements AsyncResponse {
    private String TAG = "ActivityService";
    private ListView view;
    private Context ctx;
    public ActivityService(Context ctx, ListView view) {

        this.view = view;

        this.ctx = ctx;
        reload();
    }
    private void reload() {
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
//        Log.i(TAG, response);
        Gson gsonParser = new Gson();

        ActivityDto[] activities;
        List<TextView> textViews = new ArrayList<TextView>();
        try {
            activities = gsonParser.fromJson(response, ActivityDto[].class);
            if (activities == null) {

            } else {
                view = (ListView) ((Activity)ctx).findViewById(R.id.timeline_activity);
                int i = 1;
                List<String> activityList = new ArrayList<String>();
                for (ActivityDto activityDto : activities) {
                    // create TextView for each row
                    /*TextView textView = new TextView(ctx);
                    textView.setPadding(40, 5 * i + textView.getHeight(), 20, 10);
                    textView.setPaddingRelative(10,10,10,10);
                    textView.setText(activityDto.toString());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(20, 20, 10, 0);
                    textView.setLayoutParams(params);
                    textView.setBackground( ctx.getResources().getDrawable(R.drawable.custom_background_orange1));
                    textView.setTextColor(Color.parseColor("#242424"));
                    view.addView(textView);*/
                    activityList.add(activityDto.toString());


                }
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                        (ctx, android.R.layout.simple_list_item_1, activityList);
                view.text(TypedValue.COMPLEX_UNIT_DIP,25);
                /*view.setBackground(ctx.getResources().getDrawable(R.drawable.custom_background_orange1));*/

                view.setAdapter(arrayAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
