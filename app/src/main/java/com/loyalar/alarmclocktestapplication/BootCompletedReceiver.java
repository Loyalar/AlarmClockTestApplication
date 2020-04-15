package com.loyalar.alarmclocktestapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.Calendar;
import java.util.Locale;

import Logic.AlarmLogic;
import Logic.DataStore;
import Objects.AlarmDataWrapper;

/**
 * Created by lj on 27-06-2017.
 */

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.getApplicationContext().registerReceiver(new UserPresentReceiver(), new IntentFilter("android.intent.action.USER_PRESENT"));

        DataStore<AlarmDataWrapper> alarmDataWrapperDataStore = new DataStore<>(context);
        AlarmDataWrapper alarmDataWrapper = alarmDataWrapperDataStore.getObject(context.getString(R.string.DATASTORE_ALARM));

        Calendar alarmCalendar = alarmDataWrapper.getAlarmCalendar();
        Calendar rightNow = Calendar.getInstance();

        if (alarmCalendar != null) {
            if (alarmCalendar.before(rightNow) && !alarmDataWrapper.isRecurring()) {
                alarmDataWrapperDataStore.setObject(context.getString(R.string.DATASTORE_ALARM), null);
                return;
            }

            String hourOfDayFormatted = String.format(Locale.getDefault(), "%02d", alarmCalendar.get(Calendar.HOUR_OF_DAY));
            String minuteFormatted = String.format(Locale.getDefault(), "%02d", alarmCalendar.get(Calendar.MINUTE));

            Intent notificationIntent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            String notificationText = String.format("Boot completed and an alarm was registered. Will start alarm again for  %1$s:%2$s", hourOfDayFormatted, minuteFormatted);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setContentTitle("BootCompletedReceiver")
                            .setContentText(notificationText)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText).setSummaryText("Boot Completed"))
                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                            .setLargeIcon(getPlaceholderBitmapCircle(BitmapFactory.decodeResource(context.getResources(), R.drawable.landscape)))
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(pendingIntent); //Required on Gingerbread and below

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(context.getResources().getInteger(R.integer.BOOT_COMPLETED_NOTIFICATION_IDENTIFIER), mBuilder.build());
            AlarmLogic.setAlarm(context, alarmCalendar);
        }
    }

    private Bitmap getPlaceholderBitmapCircle(Bitmap bitmapimg) {
        Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(),
                bitmapimg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmapimg.getWidth(),
                bitmapimg.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmapimg.getWidth() / 2,
                bitmapimg.getHeight() / 2, bitmapimg.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmapimg, rect, rect, paint);
        return output;
    }
}
