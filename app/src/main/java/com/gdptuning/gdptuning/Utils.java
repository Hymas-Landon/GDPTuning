package com.gdptuning.gdptuning;

import android.app.Activity;
import android.content.Intent;

public class Utils {
    public final static int THEME_DEFAULT = 0;
    public final static int THEME_GREEN = 1;
    public final static int THEME_BLUE = 2;
    public final static int THEME_RED = 3;
    private static int sTheme;

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeToTheme(Activity activity, int theme) {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    /**
     * Set the theme of the activity, according to the configuration.
     */
    public static void onActivityCreateSetTheme(Activity activity) {
        switch (sTheme) {
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppThemeNoActionBarOrangeMain);
                break;
            case THEME_GREEN:
                activity.setTheme(R.style.AppThemeNoActionBarGreen);
                break;
            case THEME_BLUE:
                activity.setTheme(R.style.AppThemeNoActionBarBlue);
                break;
            case THEME_RED:
                activity.setTheme(R.style.AppThemeNoActionBarRed);
                break;
        }
    }
}