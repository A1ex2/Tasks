package ua.org.algoritm.tasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");
        Log.i(TAG, "Received broadcast intent: " + intent.getAction());

        boolean isOn = QueryPreferences.isAlarmOn(context);
        int intervalPoll = QueryPreferences.getIntervalPoll(context);
        PollService.setServiceAlarm(context, isOn, intervalPoll);
    }
}
