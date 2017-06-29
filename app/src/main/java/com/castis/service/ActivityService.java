package com.castis.service;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.castis.activity.MainActivity;
import com.castis.activity.R;
import com.castis.model.ActivityDto;
import com.castis.service.RequestService.AsyncResponse;
import com.castis.utils.Constants;
import com.castis.utils.PreferenceUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

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
        try {
            activities = gsonParser.fromJson(response, ActivityDto[].class);
            if (activities == null) {

            } else {
                view = (ListView) ((Activity)ctx).findViewById(R.id.timeline_activity);
                int i = 1;
                List<String[]> activityList = new ArrayList<String[]>();
                for (ActivityDto activityDto : activities) {
                    activityList.add(activityDto.toStringArr());


                }

                ActivityTextViewAdapter adapter = new ActivityTextViewAdapter(ctx, activities);

                view.setAdapter(adapter );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private class ActivityTextViewAdapter extends ArrayAdapter<ActivityDto> {
        public ActivityTextViewAdapter(Context context, ActivityDto[] activities) {
            super(context, 0, activities);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            ActivityDto activity_item = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_activies_timeline, parent, false);
            }
            // Lookup view for data population
            /*TextView name = (TextView) convertView.findViewById(R.id.name);*/
            ImageView avatar = (ImageView) convertView.findViewById(R.id.avatar);
            TextView action = (TextView) convertView.findViewById(R.id.action);
            TextView action_on = (TextView) convertView.findViewById(R.id.action_on);
            // Populate the data into the template view using the data object
            /*name.setText(activity_item.getName());*/
            if (activity_item.getNote() != null) {
                action.setText(activity_item.getName() + " : " + activity_item.getAction() + " \n\r" + activity_item.getNote());
            } else {
                action.setText(activity_item.getName() + " : " + activity_item.getAction());
            }
            Picasso.with(ctx).load(Constants.GET_AVATAR_URL + activity_item.getUsername()).resize(100,100).into(avatar);
            action_on.setText(activity_item.getAction_on());
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
