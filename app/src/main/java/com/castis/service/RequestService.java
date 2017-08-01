package com.castis.service;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.castis.activity.FlashActivity;
import com.castis.activity.LoginActivity;
import com.castis.utils.PreferenceUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;

/**
 * Created by Mark on 6/24/2017.
 */

public class RequestService extends AsyncTask<HttpRequest, String, String> {
    String[] params = new String[]{"url", "header", "body", "method"};
    private static String TAG = "RequestService";
    String response = "";
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    InputStream inputStream = null;
    Context mContext;


    @Override
    protected String doInBackground(HttpRequest... httpRequests) {

        HttpRequest request = httpRequests[0];

        try {
            if (request.getMethod().equalsIgnoreCase("GET")) {
                return sendGet(request);
            }
            return sendPost(request);
        } catch (Exception e) {
            FirebaseAuth.getInstance().signOut();
            PreferenceUtils.getInstance(mContext).getSharedPrefEditor().clear();
            PreferenceUtils.getInstance(mContext).getSharedPrefEditor().commit();
            Intent i = new Intent(mContext, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
            Log.e(TAG, "Exception: " + e.getMessage());

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.toString();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("TAG", "Error closing stream", e);
                }
            }


        }
        return response;
    }

    public interface AsyncResponse {
        void ServerResponse(String response) throws Exception;
    }
    public AsyncResponse asyncResponse = null;

    public RequestService(AsyncResponse asyncResponse, Context context){
        this.asyncResponse = asyncResponse;
        this.mContext = context;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            asyncResponse.ServerResponse(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String sendGet(HttpRequest request) throws Exception {
        final String USER_AGENT = "Mozilla/5.0";
        HttpURLConnection con = (HttpURLConnection) request.getUrl().openConnection();
        con.setConnectTimeout(3000);
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responsecode = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    private String sendPost(HttpRequest request) throws Exception {
        String USER_AGENT = "Mozilla/5.0";
        HttpURLConnection con = (HttpURLConnection) request.getUrl().openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setConnectTimeout(3000);
        if (null != request.getHeaders()) {
            for (String key : request.getHeaders().keySet()) {
                con.setRequestProperty(key, request.getHeaders().get(key));
            }
        } else {
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
        }
        Writer writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
        writer.write(request.getPayLoad().toString());

        // json data
        writer.close();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

}
