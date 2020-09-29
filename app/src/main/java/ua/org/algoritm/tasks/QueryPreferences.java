package ua.org.algoritm.tasks;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {
    public static String getLogin(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("Login", "");
    }

    public static void setLogin(Context context, String Login) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString("Login", Login)
                .apply();
    }

    public static String getPassword(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("Password", "");
    }

    public static void setPassword(Context context, String Password) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString("Password", Password)
                .apply();
    }

    public static String getApi(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("Api", "");
    }

    public static void setApi(Context context, String Api) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString("Api", Api)
                .apply();
    }

    public static boolean isAlarmOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("isAlarmOn", false);
    }

    public static void setAlarmOn(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean("isAlarmOn", isOn)
                .apply();
    }

    public static int getIntervalPoll(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt("intervalPoll", 1);
    }

    public static void setIntervalPoll(Context context, int intervalPoll) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt("intervalPoll", intervalPoll)
                .apply();
    }
}
