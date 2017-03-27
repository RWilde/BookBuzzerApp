package com.fyp.n3015509.apppreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by tomha on 23-Mar-17.
 */

public class SaveSharedPreference
{
    //static final String PREF_USER_NAME= "username";
    static final String PREF_TOKEN= "token";
    static final String PREF_GOODREADS= "goodreads_id";
    static final String PREF_IMPORTED = "imported";


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
}
