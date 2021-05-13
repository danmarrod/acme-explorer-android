package com.tecmov.acmeexplorer.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util {

    public static String formatCalendar(Calendar calendar) {
        int yy=calendar.get(Calendar.YEAR);
        int mm=calendar.get(Calendar.MONTH);
        int dd=calendar.get(Calendar.DAY_OF_MONTH);
        DateFormat formatDate=DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
        calendar.setTimeInMillis(0);
        calendar.set(yy, mm, dd, 0, 0, 0);
        Date chosenDate = calendar.getTime();
        return(formatDate.format(chosenDate));

    }

    public static String formatDate(long date) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(date*1000);
        DateFormat formatDate=DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
        Date chosenDate = calendar.getTime();
        return(formatDate.format(chosenDate));
    }

    public static long Calendar2long(Calendar date) {
        return(date.getTimeInMillis()/1000);
    }

    public static long StringToLong (String date) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        try {
            return (df.parse(date).getTime()/1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
