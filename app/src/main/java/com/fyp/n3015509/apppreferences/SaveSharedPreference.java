package com.fyp.n3015509.apppreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tomha on 23-Mar-17.
 */

public class SaveSharedPreference
{
    //static final String PREF_USER_NAME= "username";
    static final String PREF_TOKEN= "token";
    static final String PREF_GOODREADS= "goodreads_id";
    static final String PREF_IMPORTED = "imported";
    static final String PREF_USERNAME = "username";
    private static final String PREF_LAST_ACTIVE = "last_active";


    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setToken(Context ctx, String token)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_TOKEN, token);
        editor.apply();
    }

    public static String getToken(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_TOKEN, "");
    }

    public static void clearToken(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        editor.apply();
    }

    public static void setGoodreadsId(Context ctx, String userId)
    {
        try {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_GOODREADS, userId);
            editor.apply();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static String getGoodreadsId(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_GOODREADS, "");
    }

    public static void setImported(Context ctx, boolean imported)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(PREF_IMPORTED, imported);
        editor.apply();
    }

    public static Boolean getImported(Context ctx)
    {
        return getSharedPreferences(ctx).getBoolean(PREF_IMPORTED, false);

    }

    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USERNAME, "");
    }

    public static void setUserName(Context ctx, String username) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USERNAME, username);
        editor.apply();
    }

    public static Date getLastActiveDate(Context ctx) {
        String date = getSharedPreferences(ctx).getString(PREF_LAST_ACTIVE, "");
        DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return sourceFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setLastActiveDate(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_LAST_ACTIVE, new Date(System.currentTimeMillis()).toString());
        editor.apply();
    }
}
