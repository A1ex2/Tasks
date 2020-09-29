package ua.org.algoritm.tasks;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import ua.org.algoritm.tasks.ConnectTo1c.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PollService extends IntentService {
    private static final String TAG = "PollService";

//    // 60 секунд
//    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1);

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    public static void setServiceAlarm(Context context, boolean isOn, int intervalPoll) {

        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(intervalPoll);

            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL_MS, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected()) {
            Log.i(TAG, "no Internet");
            return;
        }

        Log.i(TAG, "Received an intent: " + intent);

        String login = QueryPreferences.getLogin(this);
        String password = QueryPreferences.getPassword(this);
        String server = QueryPreferences.getApi(this);

        Message message = new Message(server, login, password, getApplicationContext());
        message.getNotifications();

        Boolean isMessage = message.isMessage;
        String text = message.text;

        if (!isMessage) {
            Log.i(TAG, "is empty");
        } else {
            Resources resources = getResources();
            NotificationHelper notificationHelper = new NotificationHelper(this);
            notificationHelper.createNotification(resources.getString(R.string.message_title),
                    text);
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent
                .getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }
}