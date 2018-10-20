package com.sususweet.lottery.utils;

/*
 * Created by tangyq on 2017/4/17.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class SharedPrefsUtils {

    public static SharedPreferences getSharedPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static ArrayList<Integer> getArray(String key, Context context) {
        //SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences prefs = getSharedPrefs(context);
        ArrayList<Integer> resArray = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(prefs.getString(key, "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                resArray.add(jsonArray.getInt(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resArray;
    }
    public static String getString(String key, String defaultValue, Context context) {
        return getSharedPrefs(context).getString(key, defaultValue);
    }

    public static Set<String> getStringSet(String key, Set<String> defaultValue, Context context) {
        return getSharedPrefs(context).getStringSet(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue, Context context) {
        return getSharedPrefs(context).getInt(key, defaultValue);
    }

    public static long getLong(String key, long defaultValue, Context context) {
        return getSharedPrefs(context).getLong(key, defaultValue);
    }

    public static float getFloat(String key, float defaultValue, Context context) {
        return getSharedPrefs(context).getFloat(key, defaultValue);
    }

    public static boolean getBoolean(String key, boolean defaultValue, Context context) {
        return getSharedPrefs(context).getBoolean(key, defaultValue);
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPrefs(context).edit();
    }

    public static void putArray(String key, ArrayList<Integer> array,Context context) {
        //SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();
        for (int b : array) jsonArray.put(b);
        getEditor(context).putString(key, jsonArray.toString()).apply();
        //editor.putString(key,jsonArray.toString());
        //editor.apply();
    }

    public static void putString(String key, String value, Context context) {
        getEditor(context).putString(key, value).apply();
    }

    public static void putStringSet(String key, Set<String> value, Context context) {
        getEditor(context).putStringSet(key, value).apply();
    }

    public static void putInt(String key, int value, Context context) {
        getEditor(context).putInt(key, value).apply();
    }

    public static void putLong(String key, long value, Context context) {
        getEditor(context).putLong(key, value).apply();
    }

    public static void putFloat(String key, float value, Context context) {
        getEditor(context).putFloat(key, value).apply();
    }

    public static void putBoolean(String key, boolean value, Context context) {
        getEditor(context).putBoolean(key, value).apply();
    }

    public static void remove(String key, Context context) {
        getEditor(context).remove(key).apply();
    }

    public static void clear(Context context) {
        getEditor(context).clear().apply();
    }

    private SharedPrefsUtils() {}
}

