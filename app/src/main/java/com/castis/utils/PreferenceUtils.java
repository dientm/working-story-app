package com.castis.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.castis.activity.LoginActivity;

/**
 * Created by Mark on 6/24/2017.
 */

public class PreferenceUtils {
    private static PreferenceUtils instance;

    public SharedPreferences getSharedPref() {
        return sharedPref;
    }

    public void setSharedPref(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedPrefEditor;
    private static Context mCtx;


    public PreferenceUtils(Context context) {
        mCtx = context;
        sharedPref = PreferenceManager
                .getDefaultSharedPreferences(mCtx);
        sharedPrefEditor = sharedPref.edit();
    }

    public static synchronized PreferenceUtils getInstance(Context context) {
        if ( instance == null) {
            instance = new PreferenceUtils(context);
        }
        return instance;
    }


    public SharedPreferences.Editor getSharedPrefEditor() {
        return sharedPrefEditor;
    }

    public void setSharedPrefEditor(SharedPreferences.Editor sharedPrefEditor) {
        this.sharedPrefEditor = sharedPrefEditor;
    }
}
