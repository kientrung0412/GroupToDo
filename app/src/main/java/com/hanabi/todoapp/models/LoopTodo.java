package com.hanabi.todoapp.models;

import android.app.Activity;

import androidx.work.BackoffPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.hanabi.todoapp.works.LoopWork;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoopTodo {

    private int days;
    private int weeks;
    private int months;
    private int years;
    private Boolean monday = false;
    private Boolean tuesday = false;
    private Boolean wednesday = false;
    private Boolean thursday = false;
    private Boolean friday = false;
    private Boolean saturday = false;
    private Boolean sunday = false;

    public void setDays(int days) {
        this.days = days;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public void setMonday(Boolean monday) {
        this.monday = monday;
    }

    public void setTuesday(Boolean tuesday) {
        this.tuesday = tuesday;
    }

    public void setWednesday(Boolean wednesday) {
        this.wednesday = wednesday;
    }

    public void setThursday(Boolean thursday) {
        this.thursday = thursday;
    }

    public void setFriday(Boolean friday) {
        this.friday = friday;
    }

    public void setSaturday(Boolean saturday) {
        this.saturday = saturday;
    }

    public void setSunday(Boolean sunday) {
        this.sunday = sunday;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }

    public int getDays() {
        return days;
    }

    public int getWeeks() {
        return weeks;
    }

    public int getMonths() {
        return months;
    }

    public int getYears() {
        return years;
    }

    public Boolean getMonday() {
        return monday;
    }

    public Boolean getTuesday() {
        return tuesday;
    }

    public Boolean getWednesday() {
        return wednesday;
    }

    public Boolean getThursday() {
        return thursday;
    }

    public Boolean getFriday() {
        return friday;
    }

    public Boolean getSaturday() {
        return saturday;
    }

    public Boolean getSunday() {
        return sunday;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("days", days);
        map.put("weeks", weeks);
        map.put("months", months);
        map.put("years", years);
        map.put("monday", monday);
        map.put("tuesday", tuesday);
        map.put("wednesday", wednesday);
        map.put("thursday", thursday);
        map.put("friday", friday);
        map.put("saturday", saturday);
        map.put("sunday", sunday);
        return map;
    }

    public void reset() {
        days = 0;
        weeks = 0;
        months = 0;
        years = 0;
        monday = false;
        tuesday = false;
        wednesday = false;
        thursday = false;
        friday = false;
        saturday = false;
        sunday = false;
    }

    @Override
    public String toString() {
        String aboutTime = "";
        String listDay = "";

        if (days > 0) {
            aboutTime = days + " ngày";
        } else if (weeks > 0) {
            aboutTime = weeks + " tuần";

            if (monday) {
                listDay += "Thứ Hai, ";
            }
            if (tuesday) {
                listDay += "Thứ Ba, ";
            }
            if (wednesday) {
                listDay += "Thứ Tư, ";
            }
            if (thursday) {
                listDay += "Thứ Năm, ";
            }
            if (friday) {
                listDay += "Thứ Sáu, ";
            }
            if (saturday) {
                listDay += "Thứ Bảy, ";
            }
            if (sunday) {
                listDay += "Chủ Nhật, ";
            }

        } else if (months > 0) {
            aboutTime = months + " tháng";
        } else if (years > 0) {
            aboutTime = years + " năm";
        }


        String loopStr = String.format("Lặp lại mỗi %s", aboutTime);
        if (!listDay.isEmpty()) {
            loopStr += " vào " + listDay.trim().substring(0, listDay.length() - 2);
        }

        return loopStr;
    }

    public static LoopTodo parse(Map<String, Object> map) {
        LoopTodo loopTodo = new LoopTodo();
        loopTodo.setDays(Integer.parseInt("" + map.get("days")));
        loopTodo.setWeeks(Integer.valueOf("" + map.get("weeks")));
        loopTodo.setMonths(Integer.valueOf("" + map.get("months")));
        loopTodo.setYears(Integer.valueOf("" + map.get("years")));
        loopTodo.setMonday((Boolean) map.get("monday"));
        loopTodo.setTuesday((Boolean) map.get("tuesday"));
        loopTodo.setWednesday((Boolean) map.get("wednesday"));
        loopTodo.setThursday((Boolean) map.get("thursday"));
        loopTodo.setFriday((Boolean) map.get("friday"));
        loopTodo.setSaturday((Boolean) map.get("saturday"));
        loopTodo.setSunday((Boolean) map.get("sunday"));
        return loopTodo;

    }

    public static void habit(Activity activity) {
        WorkManager workManager = WorkManager.getInstance(activity);
        PeriodicWorkRequest periodicWorkLoop =
                new PeriodicWorkRequest.Builder(LoopWork.class, 1, TimeUnit.DAYS, 15, TimeUnit.MINUTES)
                        .setBackoffCriteria(
                                BackoffPolicy.LINEAR,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS)
                        .build();
        workManager.enqueue(periodicWorkLoop);
    }
}
