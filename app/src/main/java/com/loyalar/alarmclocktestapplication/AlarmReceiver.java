package com.loyalar.alarmclocktestapplication;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import Logic.DataStore;
import Logic.LOG;
import Objects.AlarmDataWrapper;

/**
 * Created by lj on 27-06-2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i("LJ", "AlarmReceiver.OnReceive invoked");

        Toast.makeText(context, "Alarm invoked", Toast.LENGTH_LONG).show();

        Intent notificationIntent = new Intent(context, MapsActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AsyncImageLoader asyncImageLoader = new AsyncImageLoader("http://i.imgur.com/yWyJcD5.jpg")
        {
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if( keyguardManager.inKeyguardRestrictedInputMode()) {
                    LOG.log("AlarmReceiver was invoked, and screen was locked");
                } else {
                    LOG.log("AlarmReceiver was invoked, and screen was NOT locked");
                }

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setContentTitle("Alarm Clock Test Application")
                                .setContentText("Alarm Receiver was invoked - alarm was run!")
                                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                                .setAutoCancel(true)
                                .setLocalOnly(true)
                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setLargeIcon(getPlaceholderBitmapCircle(BitmapFactory.decodeResource(context.getResources(), R.drawable.landscape)))
                                .setContentIntent(pendingIntent); //Required on Gingerbread and below

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(context.getResources().getInteger(R.integer.ALARM_NOTIFICATION_IDENTIFIER), mBuilder.build());

                DataStore<AlarmDataWrapper> dataStore = new DataStore<>(context);
                AlarmDataWrapper alarmDataWrapper = dataStore.getObject(context.getString(R.string.DATASTORE_ALARM));
                if (alarmDataWrapper != null && !alarmDataWrapper.isRecurring())
                    dataStore.setObject(context.getString(R.string.DATASTORE_ALARM), null);
            }
        };

        try {
            Bitmap bitmap = asyncImageLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
        } catch (Exception ex) {
            Log.e("LJ", ex.getMessage(), ex);
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

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class AsyncImageLoader extends AsyncTask<Void, Void, Bitmap> {
        private String source;

        public AsyncImageLoader(String source) {
            this.source = source;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            try {
                URL url = new URL(source);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    }
}
