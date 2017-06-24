package com.castis.service;

import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by Mark on 6/24/2017.
 */

public class RequestService extends AsyncTask<HttpRequest, String, String> {
    String[] params = new String[]{"url", "header", "body", "method"};
    private static String TAG = "RequestService";

    @Override
    protected String doInBackground(HttpRequest... httpRequests) {
        HttpRequest request = httpRequests[0];

        String response = "";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            urlConnection = (HttpURLConnection) request.getUrl().openConnection();
            urlConnection.setDoOutput(true);
            // is output buffer writter
            urlConnection.setRequestMethod(request.getMethod());

            if (null != request.getHeaders()) {
                for (String key : request.getHeaders().keySet()) {
                    urlConnection.setRequestProperty(key, request.getHeaders().get(key));
                }
            } else {
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
            }

            Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            writer.write(request.getPayLoad().toString());

            // json data
            writer.close();
            InputStream inputStream = urlConnection.getInputStream();
            //input stream
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            while ((inputLine = reader.readLine()) != null)
                buffer.append(inputLine + "\n");
            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }
            response = buffer.toString();
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

}
