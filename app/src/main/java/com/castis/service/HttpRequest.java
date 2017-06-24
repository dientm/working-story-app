package com.castis.service;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by Mark on 6/24/2017.
 */

public class HttpRequest {
    private URL url;
    private String method;
    private Map<String, String> headers;
    private JSONObject payLoad;

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public JSONObject getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(JSONObject payLoad) {
        this.payLoad = payLoad;
    }
}
