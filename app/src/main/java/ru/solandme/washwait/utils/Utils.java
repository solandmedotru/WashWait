package ru.solandme.washwait.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.solandme.washwait.R;

public class Utils {
    private static String sTheme;
    private static SharedPreferences sharedPreferences;

    public final static String THEME_MATERIAL_BLUE = "1";
    public final static String THEME_MATERIAL_VIOLET = "2";
    public final static String THEME_MATERIAL_GREEN = "3";
    public final static String THEME_MATERIAL_DAYNIGHT = "4";

    public static void changeToTheme(String theme, Activity activity) {

        sTheme = theme;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(activity.getString(R.string.pref_theme_color_key), sTheme).apply();

        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);

    }

    public static void onActivityCreateSetTheme(Activity activity) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        sTheme = sharedPreferences.getString(activity.getString(R.string.pref_theme_color_key), "1");
        switch (sTheme) {
            default:
            case THEME_MATERIAL_BLUE:
                activity.setTheme(R.style.Blue);
                break;
            case THEME_MATERIAL_VIOLET:
                activity.setTheme(R.style.Violet);
                break;
            case THEME_MATERIAL_GREEN:
                activity.setTheme(R.style.Green);
                break;
            case THEME_MATERIAL_DAYNIGHT:
                activity.setTheme(R.style.Night);
                break;
        }
    }
}

