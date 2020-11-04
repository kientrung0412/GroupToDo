package com.hanabi.todoapp.models;

import java.util.HashMap;
import java.util.Map;

public class LoopTodo {

    private int days;
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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("days", days);
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
            if (days % 7 == 0) {
                aboutTime = days / 7 + " tuần";

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

            } else {
                aboutTime = days + " ngày";
            }
        }
        if (months > 0) {
            aboutTime = months + " tháng";
        }
        if (years > 0) {
            aboutTime = years + " năm";
        }


        String loopStr = String.format("Lặp lại mỗi %s", aboutTime);
        if (!listDay.isEmpty()) {
            loopStr += " vào " + listDay.trim().substring(0, listDay.length() - 2);
        }

        return loopStr;
    }
}
