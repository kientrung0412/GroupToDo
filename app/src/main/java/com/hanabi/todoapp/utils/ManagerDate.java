package com.hanabi.todoapp.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;

public class ManagerDate {

    private Calendar cal = Calendar.getInstance();

    public String getStingDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case 1:
                return "Chủ nhật";
            case 2:
                return "Thứ hai";
            case 3:
                return "Thứ ba";
            case 4:
                return "Thứ tư";
            case 5:
                return "Thứ năm";
            case 6:
                return "Thứ sáu";
            case 7:
                return "Thứ bảy";
        }
        return null;
    }

    public Date getDateTomorrow(Date date) {
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    public Date getDateNextTomorrow(Date date) {
        cal.setTime(date);
        cal.add(Calendar.DATE, 2);
        return cal.getTime();
    }

    public Date getDateNextWeek(Date date) {
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == 1) {
            return getDateTomorrow(date);
        } else {
            cal.add(Calendar.DATE, 9 - dayOfWeek);
            return cal.getTime();
        }
    }

    public String getTomorrow(Date date) {
        return getStingDayOfWeek(getDateTomorrow(date));
    }

    public String getNextTomorrow(Date date) {
        return getStingDayOfWeek(getDateNextTomorrow(date));
    }

    public Date getDate(Date date) {
        cal.setTime(date);
        String strNow = String.format("%s/%s/%s", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return dateFormat.parse(strNow);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isEqualDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        if (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
            return true;
        }

        return false;
    }
}
