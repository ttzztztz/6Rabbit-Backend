package com.rabbit.backend.Utilities;

import java.util.Calendar;

public class TimestampUtil {
    public static long getDayEndTimestamp(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 23, 59, 59);
        return calendar.getTimeInMillis();
    }
}
