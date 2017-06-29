package com.castis.utils;

/**
 * Created by Mark on 6/26/2017.
 */

public class Constants {
    public static String SERVER = "http://192.168.1.71:8000";
    public static String LOGIN_URL = SERVER + "/account/login";
    public static String REGISTER_URL = SERVER + "/account/register";
    public static String WORKING_ACTION_URL = SERVER + "/worklog/working-action";
    public static String ACTIVITIES_URL = SERVER + "/worklog/get-activity";
    public static String BEACON_CONFIG_URL = SERVER + "/worklog/get-beacon-configuration";
    public static String GET_AVATAR_URL = SERVER + "/account/avatar/";
    public static int START_WORKING = 1;
    public static int FINISH_WORKING = 2;

}
