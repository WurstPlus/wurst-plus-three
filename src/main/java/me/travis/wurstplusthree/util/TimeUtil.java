package me.travis.wurstplusthree.util;

import java.util.Calendar;

public class TimeUtil implements Globals {

    public static int get_hour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public static int get_day() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static int get_month() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    public static int get_year() {return Calendar.getInstance().get(Calendar.YEAR);}

    public static int get_minuite() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public static int get_second() {
        return Calendar.getInstance().get(Calendar.SECOND);
    }
}
