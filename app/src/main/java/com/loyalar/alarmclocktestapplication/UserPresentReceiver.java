package com.loyalar.alarmclocktestapplication;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import Logic.LOG;

/**
 * Created by lj on 29-06-2017.
 */

public class UserPresentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LOG.log("ACTION_USER_PRESENT INVOKED");

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        Notification notification = new NotificationCompat.Builder(context).setStyle(new NotificationCompat.BigTextStyle().setSummaryText("ACTION_USER_PRESENT").bigText("ACTION_USER_PRESENT action was invoked. Time now: " + calendar.getTime())).setSmallIcon(R.drawable.landscape).build();
        NotificationManagerCompat.from(context).notify(231987231, notification);
    }
}
