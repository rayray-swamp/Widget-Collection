package todays_memo;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.provider.CalendarContract;

import androidx.navigation.ui.AppBarConfiguration;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import calendar.DateData;
import save.ObjectStorage;

import static todays_memo.Memo_Activity.FIFTH_WEEK;
import static todays_memo.Memo_Activity.FIRST_WEEK;
import static todays_memo.Memo_Activity.FOURTH_WEEK;
import static todays_memo.Memo_Activity.LAST_WEEK;
import static todays_memo.Memo_Activity.REPEAT_EVERY_MONTH_IN_MONTH;
import static todays_memo.Memo_Activity.REPEAT_EVERY_MONTH_IN_WEEK;
import static todays_memo.Memo_Activity.REPEAT_EVERY_WEEK_IN_WEEK;
import static todays_memo.Memo_Activity.REPEAT_EVERY_YEAR_IN_MONTH;
import static todays_memo.Memo_Activity.REPEAT_EVERY_YEAR_IN_WEEK;
import static todays_memo.Memo_Activity.SECOND_WEEK;
import static todays_memo.Memo_Activity.START_DAY;
import static todays_memo.Memo_Activity.END_DAY;
import static todays_memo.Memo_Activity.REPEAT_EVERY_DAY;
import static todays_memo.Memo_Activity.REPEAT_EVERY_WEEK;
import static todays_memo.Memo_Activity.REPEAT_EVERY_MONTH;
import static todays_memo.Memo_Activity.REPEAT_EVERY_YEAR;
import static todays_memo.Memo_Activity.THIRD_WEEK;
import static todays_memo.Memo_Activity.TO_DO_IN_THE_DAY;
import static todays_memo.Memo_Activity.TO_DO_IN_THE_MONTH;
import static todays_memo.Memo_Activity.TO_DO_IN_THE_WEEK;
import static todays_memo.Memo_Activity.DO_NOT_SHOW_ON_CHECKED;

import static todays_memo.Memo_Activity.SUNDAY;
import static todays_memo.Memo_Activity.MONDAY;
import static todays_memo.Memo_Activity.TUESDAY;
import static todays_memo.Memo_Activity.WEDNESDAY;
import static todays_memo.Memo_Activity.THURSDAY;
import static todays_memo.Memo_Activity.FRIDAY;
import static todays_memo.Memo_Activity.SATURDAY;

import static todays_memo.Memo_Activity.DATE;
import static todays_memo.Memo_Activity.WEEK;
import static todays_memo.Memo_Activity.MONTH;






public class ComparisonDate {

    public static int getMode(String data){

        Map<Integer,String> map= getMap(data);
        if (map==null)
            return DATE;
        if (map.containsKey(TO_DO_IN_THE_WEEK)){
            return WEEK;
        }
        if (map.containsKey(TO_DO_IN_THE_MONTH)){
            return MONTH;
        }
        return DATE;
    }

    public static Map<Integer,String> getMap(String data){
        Map<String,String> map=null;
        try {
            map= ObjectStorage.getData(data,Map.class);
        }catch (Exception e){
            return null;
        }
        if (map==null){
            return null;
        }
        List<String> list=new ArrayList<>(map.keySet());
        Map<Integer,String> nmap=new HashMap<>();
        for (String s:list){
            try {
                nmap.put(Integer.valueOf(s),map.get(s));
            }catch (Exception e){
                return null;
            }
        }
        return nmap;
    }

    public static boolean onCheked(String data){
        Map<Integer,String> map= getMap(data);
        if (map==null)
            return false;
        return map.containsKey(DO_NOT_SHOW_ON_CHECKED);
    }

    public static boolean isContainCondition(String t,String data){
        Map<Integer,String> map= getMap(data);
        if (map==null)
            return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");


        Date d;
        try {d=sdf.parse(t);}catch (Exception e){return false;}
        Date sd;
        try {sd=sdf.parse(map.getOrDefault(START_DAY,null)); }catch (Exception e){return false;}
        Date ed;
        try {ed=sdf.parse(map.getOrDefault(END_DAY,null)); }catch (Exception e){ed=new Date(Long.MAX_VALUE);}

        long flt=d.getTime();

        if (map.containsKey(TO_DO_IN_THE_WEEK)){
            for (Date date:new Date[]{d,sd,ed}){
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DAY_OF_MONTH,-(cal.get(Calendar.DAY_OF_WEEK)-1));
                date.setTime(cal.getTimeInMillis());
            }
        }
        else if (map.containsKey(TO_DO_IN_THE_MONTH)){
            for (Date date:new Date[]{d,sd,ed}){
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.DATE, 1);
                date.setTime(cal.getTimeInMillis());
            }
        }

        if (d.getTime()<sd.getTime()||d.getTime()>ed.getTime())
            return false;

        long lt=d.getTime();

        if (map.containsKey(REPEAT_EVERY_DAY)){
            try {
                int rp=Integer.valueOf(map.get(REPEAT_EVERY_DAY));
                if (dateDiff(sd,d)%rp!=0)
                    return false;
            }catch (Exception e){return false;}
        }
        else if (map.containsKey(REPEAT_EVERY_WEEK)){
            try {
                int rp=Integer.valueOf(map.get(REPEAT_EVERY_WEEK).split(",")[0]);
                if (weekDiff(sd,d)%rp!=0)
                    return false;
                int[] dows = new int[]{SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY};
                int dow=Integer.valueOf(map.get(REPEAT_EVERY_WEEK).split(",")[1]);
                if ((dows[DateData.getDateOfWeek(lt)-1]&dow)==0){
                    return false;
                }

            }catch (Exception e){return false;}
        }
        else if (map.containsKey(REPEAT_EVERY_MONTH)){
            try {
                int rp=Integer.valueOf(map.get(REPEAT_EVERY_MONTH).split(",")[0]);
                if (monthDiff(sd,d)%rp!=0)
                    return false;
                boolean is_by_date=Boolean.valueOf(map.get(REPEAT_EVERY_MONTH).split(",")[1]);
                if(is_by_date){
                    int date=Integer.valueOf(map.get(REPEAT_EVERY_MONTH).split(",")[2]);
                    if ((date&(1<<(DateData.getDateOfMonth(lt)-1)))==0){
                        return false;
                    }
                }
                else {
                    long date=Long.valueOf(map.get(REPEAT_EVERY_MONTH).split(",")[2]);
                    long l=1;
                    l=l<<((DateData.getDayOfWeekOnMonth(lt)-1)*7+(DateData.getDateOfWeek(lt)-1));
                    if ((date&l)==0){
                        if (DateData.isLastDayOFWeek(lt)){
                            l=1;
                            l=l<<(35+(DateData.getDateOfWeek(lt)-1));
                            if ((date&l)==0){
                                return false;
                            }
                        }
                        else
                            return false;
                    }
                }
            }catch (Exception e){return false;}
        }
        else if (map.containsKey(REPEAT_EVERY_YEAR)){
            try {
                int rp=Integer.valueOf(map.get(REPEAT_EVERY_YEAR).split(",")[0]);
                if (yearDiff(sd,d)%rp!=0)
                    return false;
                int months=Integer.valueOf(map.get(REPEAT_EVERY_YEAR).split(",")[1]);
                if (((1<<(DateData.getMonth(lt)))&months)==0){
                    return false;
                }
                boolean is_by_date=Boolean.valueOf(map.get(REPEAT_EVERY_YEAR).split(",")[2]);
                if(is_by_date){
                    int date=Integer.valueOf(map.get(REPEAT_EVERY_YEAR).split(",")[3]);
                    if ((date&(1<<(DateData.getDateOfMonth(lt)-1)))==0){
                        return false;
                    }
                }
                else {
                    long date=Integer.valueOf(map.get(REPEAT_EVERY_YEAR).split(",")[3]);
                    long l=1;
                    l=l<<((DateData.getDayOfWeekOnMonth(lt)-1)*7+(DateData.getDateOfWeek(lt)-1));
                    if ((date&l)==0){
                        if (DateData.isLastDayOFWeek(lt)){
                            l=1;
                            l=l<<(35+(DateData.getDateOfWeek(lt)-1));
                            if ((date&l)==0){
                                return false;
                            }
                        }
                        else
                            return false;
                    }
                }
            }catch (Exception e){return false;}
        }

        else if (map.containsKey(REPEAT_EVERY_WEEK_IN_WEEK)){
            try {
                int rp=Integer.valueOf(map.get(REPEAT_EVERY_WEEK_IN_WEEK));
                if (weekDiff(sd,d)%rp!=0)
                    return false;
            }catch (Exception e){return false;}
        }
        else if (map.containsKey(REPEAT_EVERY_MONTH_IN_WEEK)){
            try {
                int rp=Integer.valueOf(map.get(REPEAT_EVERY_MONTH_IN_WEEK).split(",")[0]);
                if (monthDiff(sd,d)%rp!=0)
                    return false;

                final int[] weeks = new int[]{FIRST_WEEK,SECOND_WEEK,THIRD_WEEK,FOURTH_WEEK,FIFTH_WEEK};
                int week=Integer.valueOf(map.get(REPEAT_EVERY_MONTH_IN_WEEK).split(",")[1]);
                if ((weeks[DateData.getWeekOfMonth(flt)-1]&week)==0){
                    if (DateData.isLastDayOFWeek(get1stDowInWeek(flt))){
                        if ((LAST_WEEK&week)==0)
                            return false;
                    }
                    else
                        return false;
                }

            }catch (Exception e){return false;}
        }
        else if (map.containsKey(REPEAT_EVERY_YEAR_IN_WEEK)){
            try {
                int rp=Integer.valueOf(map.get(REPEAT_EVERY_YEAR_IN_WEEK).split(",")[0]);
                if (yearDiff(sd,d)%rp!=0)
                    return false;
                int months=Integer.valueOf(map.get(REPEAT_EVERY_YEAR_IN_WEEK).split(",")[1]);
                if (((1<<(DateData.getMonth(lt)))&months)==0){
                    return false;
                }
                int[] weeks = new int[]{FIRST_WEEK,SECOND_WEEK,THIRD_WEEK,FOURTH_WEEK,FIFTH_WEEK};
                int week=Integer.valueOf(map.get(REPEAT_EVERY_YEAR_IN_WEEK).split(",")[2]);
                if ((weeks[DateData.getWeekOfMonth(flt)-1]&week)==0){
                    if (DateData.isLastDayOFWeek(get1stDowInWeek(flt))){
                        if ((LAST_WEEK&week)==0)
                            return false;
                    }
                    else
                        return false;
                }

            }catch (Exception e){return false;}
        }

        else if (map.containsKey(REPEAT_EVERY_MONTH_IN_MONTH)){
            try {
                int rp=Integer.valueOf(map.get(REPEAT_EVERY_MONTH_IN_MONTH));
                if (monthDiff(sd,d)%rp!=0)
                    return false;

            }catch (Exception e){return false;}
        }
        else if (map.containsKey(REPEAT_EVERY_YEAR_IN_MONTH)){
            try {
                int rp=Integer.valueOf(map.get(REPEAT_EVERY_YEAR_IN_MONTH).split(",")[0]);
                if (yearDiff(sd,d)%rp!=0)
                    return false;
                int months=Integer.valueOf(map.get(REPEAT_EVERY_YEAR_IN_MONTH).split(",")[1]);
                if (((1<<(DateData.getMonth(lt)))&months)==0){
                    return false;
                }

            }catch (Exception e){return false;}
        }

        else if ((map.containsKey(DO_NOT_SHOW_ON_CHECKED))){
            if (!d.equals(sd)){
                return false;
            }
        }


        return true;
   }





    public static int dateDiff(String dateFromStrig, String dateToString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date dateTo = null;
        Date dateFrom = null;


        // Date型に変換
        try {
            dateFrom = sdf.parse(dateFromStrig);
            dateTo = sdf.parse(dateToString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return dateDiff(dateFrom,dateTo);
    }
    public static int dateDiff(Date dateFrom, Date dateTo) {
        // 差分の日数を計算する
        long dateTimeTo = dateTo.getTime();
        long dateTimeFrom = dateFrom.getTime();
        long dayDiff = ( dateTimeTo - dateTimeFrom  ) / (1000 * 60 * 60 * 24 );
        return (int) dayDiff;
    }

    public static int weekDiff(String strDate1, String strDate2)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date date1 = sdf.parse(strDate1);
        Date date2 = sdf.parse(strDate2);
        return weekDiff(date1,date2);
    }
    public static int weekDiff(Date date1, Date date2){
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        cal1.add(Calendar.DAY_OF_MONTH,-(cal1.get(Calendar.DAY_OF_WEEK)-1));
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        cal2.add(Calendar.DAY_OF_MONTH,-(cal2.get(Calendar.DAY_OF_WEEK)-1));

        return dateDiff(cal1.getTime(),cal2.getTime())/7;
    }


    /**
     * 2つの日付の月数の差を求めます。
     * 日付文字列 strDate1 – strDate2 が何ヵ月かを整数で返します。
     * ※端数の日数は無視します。
     *
     * @param strDate1    日付文字列1    yyyy/MM/dd
     * @param strDate2    日付文字列2    yyyy/MM/dd
     * @return 2つの日付の月数の差
     * @throws ParseException 日付フォーマットが不正な場合
     */
    public static int monthDiff(String strDate1, String strDate2)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date date1 = sdf.parse(strDate1);
        Date date2 = sdf.parse(strDate2);
        return monthDiff(date1,date2);
    }
    /**
     * 2つの日付の月数の差を求めます。
     * java.util.Date 型の日付 date1 – date2 が何ヵ月かを整数で返します。
     * ※端数の日数は無視します。
     *
     * @param date1    日付1 java.util.Date
     * @param date2    日付2 java.util.Date
     * @return 2つの日付の月数の差
     */
    public static int monthDiff(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        cal1.set(Calendar.DATE, 1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        cal2.set(Calendar.DATE, 1);
        int count = 0;
        if (cal1.before(cal2)) {
            while (cal1.before(cal2)) {
                cal1.add(Calendar.MONTH, 1);
                count--;
            }
        } else {
            count--;
            while (!cal1.before(cal2)) {
                cal1.add(Calendar.MONTH, -1);
                count++;
            }
        }
        return count;
    }
    public static int yearDiff(String strDate1, String strDate2)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date date1 = sdf.parse(strDate1);
        Date date2 = sdf.parse(strDate2);
        return yearDiff(date1,date2);
    }
    public static int yearDiff(Date date1, Date date2){
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        cal1.set(Calendar.DAY_OF_YEAR, 1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        cal2.set(Calendar.DAY_OF_YEAR, 1);

        return monthDiff(cal1.getTime(),cal2.getTime())/12;
    }
    public static long get1stDowInWeek(long t){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(t);
        if (DateData.getWeekOfMonth(t)==1)
            cal.set(Calendar.DAY_OF_MONTH, 1);
        else
            cal.add(Calendar.DAY_OF_MONTH,-(cal.get(Calendar.DAY_OF_WEEK)-1));
        return cal.getTimeInMillis();
    }
}
