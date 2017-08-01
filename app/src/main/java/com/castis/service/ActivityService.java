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

import org.w3c.dom.Text;

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
            url = new URL(PreferenceUtils.getInstance(ctx).getSharedPref().getString(Constants.SERVER, Constants.DEFAULT_SERVER) + Constants.GET_LIST_USER_STATUS);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpRequest request = new HttpRequest();
        request.setUrl(url);
        request.setMethod("GET");
        new RequestService(this, this.ctx).execute(request);
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
//                view = (ListView) ((Activity)ctx).findViewById(R.id.timeline_activity);
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
            ActivityDto activityDto = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_status_layout, parent, false);
            }

            TextView username = (TextView) convertView.findViewById((R.id.username));
            username.setText(activityDto.getUsername());
            ImageView imageView = (ImageView) convertView.findViewById(R.id.status_image);
            if (activityDto.getIs_working().equals("true")) {
                Picasso.with(getContext()).load(R.drawable.smiley).into(imageView);
            } else {
                Picasso.with(getContext()).load(R.drawable.custom_sleep).into(imageView);
            }
            return convertView;
        }
    }
}
