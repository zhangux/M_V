package com.cn.mv.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Administrator on 2016/8/16.
 */
public class SharedPreferencesUtils {
    private static SharedPreferences getSharedPreference(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp;
    }
    public static void saveVideoOrderType(Context context, int type) {
        SharedPreferences sp = getSharedPreference(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("orderType", type);
        editor.commit();
    }

    public static int getVideoOrderType(Context context) {
        SharedPreferences sp = getSharedPreference(context);
        int order=0;
        order=sp.getInt("orderType", 0);
        return order;
    }

    public static void saveVideoModType(Context context, int type) {
        SharedPreferences sp = getSharedPreference(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("modType", type);
        editor.commit();
    }

    public static int getVideoModType(Context context) {
        SharedPreferences sp = getSharedPreference(context);
        int order=0;
        order=sp.getInt("modType", 0);
        return order;
    }

    public static void saveMusicOrderType(Context context, int type) {
        SharedPreferences sp = getSharedPreference(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("musicOrderType", type);
        editor.commit();
    }

    public static int getMusicOrderType(Context context) {
        SharedPreferences sp = getSharedPreference(context);
        int order=0;
        order=sp.getInt("musicOrderType", 0);
        return order;
    }

    public static void saveMusicModType(Context context, int type) {
        SharedPreferences sp = getSharedPreference(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("musicModType", type);
        editor.commit();
    }

    public static int getMusicModType(Context context) {
        SharedPreferences sp = getSharedPreference(context);
        int order=0;
        order=sp.getInt("musicModType", 0);
        return order;
    }
}
