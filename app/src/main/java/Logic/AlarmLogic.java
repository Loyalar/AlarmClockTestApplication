package Logic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.loyalar.alarmclocktestapplication.AlarmReceiver;
import com.loyalar.alarmclocktestapplication.R;

import java.util.Calendar;

import Objects.AlarmDataWrapper;

/**
 * Created by lj on 27-06-2017.
 */

public class AlarmLogic {

    private static PendingIntent mAlarmPendingIntent;
    private static AlarmManager mAlarmManager;

    public static void setAlarm(Context context, Calendar calendar) {
        AlarmDataWrapper alarmDataWrapper = new AlarmDataWrapper();
        alarmDataWrapper.setRecurring(true);
        alarmDataWrapper.setAlarmCalendar(calendar);

        DataStore<AlarmDataWrapper> dataStore = new DataStore<>(context);
        dataStore.setObject(context.getString(R.string.DATASTORE_ALARM), alarmDataWrapper);

        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        buildPendingIntent(context);

        if (alarmDataWrapper.isRecurring())
            mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, mAlarmPendingIntent);
        else
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mAlarmPendingIntent);
    }

    public static void cancelAlarm(Context context) {
        buildPendingIntent(context);

        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        mAlarmManager.cancel(mAlarmPendingIntent);
        mAlarmPendingIntent.cancel();
    }

    public static boolean isAlarmRunning(Context context) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.setAction("com.loyalar.alarmclocktestapplication.alarmreceiver");

        boolean isRunning = (PendingIntent.getBroadcast(context, context.getResources().getInteger(R.integer.ALARM_PENDING_INTENT_REQUESTCODE), alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);
        return isRunning;
    }

    private static void buildPendingIntent(Context context) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.setAction("com.loyalar.alarmclocktestapplication.alarmreceiver");
        mAlarmPendingIntent = PendingIntent.getBroadcast(context, context.getResources().getInteger(R.integer.ALARM_PENDING_INTENT_REQUESTCODE), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
