package com.surgical.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils{

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public enum DateFormatType{

        yyyyMMddHHmmssSSS("yyyyMMddHHmmssSSS");

        private String dateFormatType;

        private DateFormatType(String dateFormatType){
            this.dateFormatType = dateFormatType;
        }

        @Override
        public String toString(){
            return this.dateFormatType;
        }
    }

    public static String getDateInString(String format, Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String dateString = sdf.format(date);
        return dateString;
    }

    public static Date getDateBeforeDays(Calendar cal, Integer beforeDays){
        cal.add(Calendar.DAY_OF_MONTH, beforeDays);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    public static Date getTodayDate(Calendar cal){
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }
}
