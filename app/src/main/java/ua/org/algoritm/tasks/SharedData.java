package ua.org.algoritm.tasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

public class SharedData {
    public static ArrayList<User> USERS = new ArrayList<>();

    public static MainActivity app;
    public static String LOGIN;
    public static String PASSWORD;
    public static String API;
    public static String VERSION;

    public static String hostFTP;
    public static int portFTP;
    public static String usernameFTP;
    public static String passwordFTP;
    public static boolean thisSFTP;

    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

}