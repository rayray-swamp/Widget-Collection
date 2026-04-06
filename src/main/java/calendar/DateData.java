package calendar;

import static calendar.TextBitmap.getLocalizedResources;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class DateData {
    public static String getNowDate(){
        return getNowDate(System.currentTimeMillis());
    }
    public static String getNowDate(long time){
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");// HH:mm:ss
        final Date date = new Date(time);
        return df.format(date);
    }
    public static String getNowTime(){
        return getNowTime(System.currentTimeMillis());
    }
    public static String getNowTime(long time){
        final DateFormat df = new SimpleDateFormat("HH:mm");// HH:mm:ss
        final Date date = new Date(time);
        return df.format(date);
    }
    public static String getNowDate(long time, Context context){
        final DateFormat df = new SimpleDateFormat(context.getString(R.string.date_order));// HH:mm:ss
        final Date date = new Date(time);
        return df.format(date);
    }
    public static String getNowMonth(){
        return getNowMonth(System.currentTimeMillis());
    }
    public static String getNowMonth(long time){
        final DateFormat df = new SimpleDateFormat("yyyy/MM" /*HH:mm:ss*/);
        final Date date = new Date(time);
        return df.format(date);
    }
    public static String getNowMonth(long time,Context context){
        final DateFormat df = new SimpleDateFormat(context.getString(R.string.month_order));
        final Date date = new Date(time);
        return df.format(date);
    }
    public static String getNowMonth(long time,Context context,String lang){

        String text=context.getString(R.string.month_order);
        if (lang!=null){
            text=getLocalizedResources(context,new Locale(lang)).getString(R.string.month_order);
        }

        final DateFormat df = new SimpleDateFormat(text);
        final Date date = new Date(time);
        return df.format(date);
    }
    public static String getNowMonth(long time,Context context,boolean monthistext){

        if (monthistext){
            if ("MM/yyyy".equals(context.getString(R.string.month_order))){
                return TextBitmap.getMonthText(DateData.getYear(time),DateData.getMonth(time),context,null,null)+
                        " "+getYear(time)+context.getString(R.string.Year_unit);
            }
            else {
                return getYear(time)+context.getString(R.string.Year_unit)+
                        " "+TextBitmap.getMonthText(DateData.getYear(time),DateData.getMonth(time),context,null,null);
            }
        }
        else
            return getNowMonth(time, context);


    }
    public static String getNowWeek(){
        return getNowWeek(System.currentTimeMillis());
    }
    public static String getNowWeek(long time){
        final DateFormat df = new SimpleDateFormat("yyyy/" /*HH:mm:ss*/);
        final Date date = new Date(time);
        return df.format(date)+getWeekOfYear(time)+"/Week";
    }
    public static String getNowWeekOfMonth(long time,Context context){
        if ("first".equals(context.getString(R.string.week_ordar))){
            return TextBitmap.getWeekText(getWeekOfMonth(time)-1,context)+" "+getNowMonth(time, context);
        }
        return getNowMonth(time, context)+" "+TextBitmap.getWeekText(getWeekOfMonth(time)-1,context);
    }

    public static long getLongOnTime(int year,int month,int date,int hour,int minute,int second){
        Calendar c = Calendar.getInstance();
        c.set(year, month, date, hour, minute, second);
        return c.getTimeInMillis();
    }
    public static long getLongOnTime(int year,int month){
        Calendar c = Calendar.getInstance();
        c.set(year, month,1);
        return c.getTimeInMillis();
    }
    public static long getLongOnTime(int year,int month,int date){
        Calendar c = Calendar.getInstance();
        c.set(year, month, date);
        return c.getTimeInMillis();
    }
    public static long getLongOnTime(String format){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        try {
            return sdf.parse(format).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }
    /*
    * sunday:1
    * monday:2
    * tuesday:3
    * wednesday:4
    * thursday:5
    * friday:6
    * saturday:7
    *
    * first week:1
    * second week:2
    * third week:3
    * fourth week:4
    * fifth week:5
    * sixth week:6
    *
    * Jan: 0
    * Feb: 1
    * Mar: 2
    * Apr: 3
    * May: 4
    * Jun: 5
    * Jul: 6
    * Aug: 7
    * Sep: 8
    * Oct: 9
    * Nov: 10
    * Dec: 11
    */



    public static int getDateOfWeek(){
        Calendar cl = Calendar.getInstance();//カレンダーのオブジェクトを取得
        cl.setTime(new Date(System.currentTimeMillis())); //今日の日付を取得
        return cl.get(Calendar.DAY_OF_WEEK);
    }
    public static int getDateOfWeek(long time){
        Calendar cl = Calendar.getInstance();//カレンダーのオブジェクトを取得
        cl.setTime(new Date(time)); //今日の日付を取得
        return cl.get(Calendar.DAY_OF_WEEK);
    }
    public static int getDateOfMonth(){
        Calendar cl = Calendar.getInstance();//カレンダーのオブジェクトを取得
        cl.setTime(new Date(System.currentTimeMillis())); //今日の日付を取得
        return cl.get(Calendar.DAY_OF_MONTH);
    }
    public static int getWeekOfMonth(){
        Calendar cl = Calendar.getInstance();//カレンダーのオブジェクトを取得
        cl.setTime(new Date(System.currentTimeMillis())); //今日の日付を取得
        return cl.get(Calendar.WEEK_OF_MONTH);
    }
    public static int getWeekOfMonth(long time){
        Calendar cl = Calendar.getInstance();//カレンダーのオブジェクトを取得
        cl.setTime(new Date(time)); //今日の日付を取得
        return cl.get(Calendar.WEEK_OF_MONTH);
    }
    public static int getWeekOfYear(){
        Calendar cl = Calendar.getInstance();//カレンダーのオブジェクトを取得
        cl.setTime(new Date(System.currentTimeMillis())); //今日の日付を取得
        return cl.get(Calendar.WEEK_OF_YEAR);
    }
    public static int getWeekOfYear(long time){
        Calendar cl = Calendar.getInstance();//カレンダーのオブジェクトを取得
        cl.setTime(new Date(time)); //今日の日付を取得
        return cl.get(Calendar.WEEK_OF_YEAR);
    }
    public static int getYear(){
        Calendar cl = Calendar.getInstance();//カレンダーのオブジェクトを取得
        cl.setTime(new Date(System.currentTimeMillis())); //今日の日付を取得
        return cl.get(Calendar.YEAR);
    }
    public static int getMonth(){
        Calendar cl = Calendar.getInstance();//カレンダーのオブジェクトを取得
        cl.setTime(new Date(System.currentTimeMillis())); //今日の日付を取得
        return cl.get(Calendar.MONTH);
    }
    public static int getDateOfMonth(long time){
        Calendar cl = Calendar.getInstance();//カレンダーのオブジェクトを取得
        cl.setTime(new Date(time)); //今日の日付を取得
        return cl.get(Calendar.DAY_OF_MONTH);
    }
    public static int getYear(long time){
        Calendar cl = Calendar.getInstance();//カレンダーのオブジェクトを取得
        cl.setTime(new Date(time)); //今日の日付を取得
        return cl.get(Calendar.YEAR);
    }
    public static int getMonth(long time){
        Calendar cl = Calendar.getInstance();//カレンダーのオブジェクトを取得
        cl.setTime(new Date(time)); //今日の日付を取得
        return cl.get(Calendar.MONTH);
    }
    public static int getHour(){
        return getHour(System.currentTimeMillis());
    }
    public static int getHour(long time){
        Calendar cl = Calendar.getInstance();//カレンダーのオブジェクトを取得
        cl.setTime(new Date(time)); //今日の日付を取得
        return cl.get(Calendar.HOUR_OF_DAY);
    }
    public static int getMinute(){
        return getMinute(System.currentTimeMillis());
    }
    public static int getMinute(long time){
        Calendar cl = Calendar.getInstance();//カレンダーのオブジェクトを取得
        cl.setTime(new Date(time)); //今日の日付を取得
        return cl.get(Calendar.MINUTE);
    }

    public static int getSecond(){
        return getSecond(System.currentTimeMillis());
    }
    public static int getSecond(long time){
        Calendar cl = Calendar.getInstance();//カレンダーのオブジェクトを取得
        cl.setTime(new Date(time)); //今日の日付を取得
        return cl.get(Calendar.SECOND);
    }

    public static int getDayOfWeekOnMonth(){
        return getDayOfWeekOnMonth(System.currentTimeMillis());
    }
    public static int getDayOfWeekOnMonth(long time){
        Calendar cl = Calendar.getInstance();//カレンダーのオブジェクトを取得
        cl.setTime(new Date(time)); //今日の日付を取得
        return cl.get(Calendar.DAY_OF_WEEK_IN_MONTH);
    }


    public static int getNumberOfDaysInMonth(int year,int month){
        int[] numberOfDays ={31,28,31,30,31,30,31,31,30,31,30,31};
        int[] numberOfDaysInLeapYear ={31,29,31,30,31,30,31,31,30,31,30,31};
        if (year%4==0&&year%100!=0){
            return numberOfDaysInLeapYear[month];
        }
        else {
            return numberOfDays[month];
        }
    }
    public static int getDateonCalendar(int year,int month,int week,int date_of_week){
        final java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year,month,1);
        final long time = calendar.getTimeInMillis();
        int d;
        d=-getDateOfWeek(time)+1;//初日の曜日を追加
        d+=7*(week-1);
        d+=date_of_week;

        if (d<1||d>getNumberOfDaysInMonth(year, month))
            return 0;

        return d;
    }
    public static int[] getCalendarPointOfDate(int year,int month,int date){
        final java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year,month,1);
        final long time = calendar.getTimeInMillis();
        int d;
        d=getDateOfWeek(time)-2;//初日の曜日を追加
        d+=date;
        int[] ds=new int[]{d/7+1,d%7+1};

        if (ds[0]<1||ds[0]>getNumberOfDaysInMonth(year, month))
            return new int[]{0,0};

        return ds;
    }//{week,day of week}

    public static boolean isLastDayOFWeek(long t){
        int num=getNumberOfDaysInMonth(getYear(t),getMonth(t));
        return (num-(getDateOfMonth(t)+1))<7;
    }

}
