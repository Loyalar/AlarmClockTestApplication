package com.loyalar.alarmclocktestapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Calendar;
import java.util.Locale;

import Logic.AlarmLogic;
import Logic.DataStore;
import Objects.AlarmDataWrapper;

public class MainActivity extends AppCompatActivity {

    private Button mSelectAlarmTimerButton, mStartAlarmButton, mCancelAlarmButton, mCheckAlarmIsRunningButton;
    private TextView mAlarmTimerTextView;
    private Activity mActivity;
    private AlarmManager mAlarmManager;
    private PendingIntent mAlarmPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            unregisterReceiver(new UserPresentReceiver());
        } catch (Exception ex) {
            //Do nothing. Receiver had not been registered.
        }
        IntentFilter i1 = new IntentFilter("android.intent.action.USER_PRESENT");
        IntentFilter i2 = new IntentFilter("android.intent.action.USER_PRESENT");
        IntentFilter i3 = new IntentFilter("android.intent.action.USER_PRESENT");


        registerReceiver(new UserPresentReceiver(), new IntentFilter("android.intent.action.USER_PRESENT"));
        registerReceiver(new UserPresentReceiver(), new IntentFilter("android.intent.action.USER_PRESENT"));
        registerReceiver(new UserPresentReceiver(), new IntentFilter("android.intent.action.USER_PRESENT"));

        setupMemberVariables();
        setupButtonEvents();
    }

    private void setupMemberVariables() {
        mActivity = this;
        mSelectAlarmTimerButton = (Button) findViewById(R.id.button_select_alarm_timer);
        mStartAlarmButton = (Button) findViewById(R.id.button_start_alarm);
        mCancelAlarmButton = (Button) findViewById(R.id.button_cancel_alarm);
        mAlarmTimerTextView = (TextView) findViewById(R.id.textview_alarm_timer);
        mCheckAlarmIsRunningButton = (Button) findViewById(R.id.button_check_alarm_is_running);

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    private void setupButtonEvents() {
        mSelectAlarmTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentTime = Calendar.getInstance();
                mCurrentTime.add(Calendar.MINUTE, 2);
                TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {
                        final String hourOfDayFormatted = String.format(Locale.getDefault(), "%02d", hourOfDay);
                        final String minuteFormatted = String.format(Locale.getDefault(), "%02d", minute);

                        new MaterialDialog.Builder(mActivity)
                                .title("Set alarm?")
                                .content(String.format("Are you sure you want to set an alarm at %1$s:%2$s?", hourOfDayFormatted, minuteFormatted))
                                .positiveText("Yes")
                                .negativeText("Cancel")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        mAlarmTimerTextView.setText(String.format("Alarm set for %1s", hourOfDayFormatted + ":" + minuteFormatted));
                                        Calendar calendar = Calendar.getInstance();

                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(Calendar.MINUTE, minute);
                                        calendar.set(Calendar.SECOND, 0);

                                        AlarmLogic.setAlarm(mActivity, calendar);
                                    }
                                })
                                .show();
                    }
                }, mCurrentTime.get(Calendar.HOUR_OF_DAY), mCurrentTime.get(Calendar.MINUTE), true);

                timePickerDialog.setTitle("Select alarm time");
                timePickerDialog.show();
            }
        });

        mCancelAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });

        mCheckAlarmIsRunningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAlarmRunning = AlarmLogic.isAlarmRunning(mActivity);
                mAlarmTimerTextView.setText("Alarm running status: " + isAlarmRunning);
            }
        });
    }

    private void cancelAlarm() {
        AlarmLogic.cancelAlarm(mActivity);
        mAlarmTimerTextView.setText("Alarm cancelled");

        DataStore<AlarmDataWrapper> dataStore = new DataStore<>(mActivity);
        dataStore.setObject(getString(R.string.DATASTORE_ALARM), null);
    }
}
