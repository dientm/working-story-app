package com.castis.service;

import android.os.AsyncTask;
import android.util.Log;

import com.castis.model.User;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Exchanger;

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
    @Override
    protected String doInBackground(HttpRequest... httpRequests) {

        HttpRequest request = httpRequests[0];

        try {
            if (request.getMethod().equalsIgnoreCase("GET")) {
                return sendGet(request);
            }
            return sendPost(request);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
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
                    Log.e("truong", "Error closing stream", e);
                }
            }
        }
        return response;
    }

    public interface AsyncResponse {
        void ServerResponse(String response) throws Exception;
    }
    public AsyncResponse asyncResponse = null;

    public RequestService(AsyncResponse asyncResponse){
        this.asyncResponse = asyncResponse;
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
