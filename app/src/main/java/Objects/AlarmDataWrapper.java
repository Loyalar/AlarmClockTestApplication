package Objects;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by lj on 28-06-2017.
 */

public class AlarmDataWrapper implements Serializable {
    private Calendar alarmCalendar;
    private boolean recurring;

    public Calendar getAlarmCalendar() { return alarmCalendar; }

    public void setAlarmCalendar(Calendar alarmCalendar) { this.alarmCalendar = alarmCalendar; }

    public boolean isRecurring() { return recurring; }

    public void setRecurring(boolean recurring) { this.recurring = recurring; }
}
