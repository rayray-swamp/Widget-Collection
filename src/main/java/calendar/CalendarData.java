package calendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.ical.compat.jodatime.LocalDateIterable;
import com.google.ical.compat.jodatime.LocalDateIterator;
import com.google.ical.compat.jodatime.LocalDateIteratorFactory;

import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CalendarData {
    // プロジェクション配列。
    // 取得したいプロパティの一覧を指定する。
    public static final String[] CALENDAR_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
            CalendarContract.Calendars.CALENDAR_TIME_ZONE,
            CalendarContract.Calendars.VISIBLE,
            CalendarContract.Calendars.SYNC_EVENTS,
            CalendarContract.Calendars.OWNER_ACCOUNT,
    };

    // プロジェクション配列のインデックス。
    // パフォーマンス向上のために、動的に取得せずに、静的に定義しておく。
    public static final int CALENDAR_PROJECTION_IDX_ID = 0;
    public static final int CALENDAR_PROJECTION_IDX_NAME = 1;
    public static final int CALENDAR_PROJECTION_IDX_ACCOUNT_NAME = 2;
    public static final int CALENDAR_PROJECTION_IDX_ACCOUNT_TYPE = 3;
    public static final int CALENDAR_PROJECTION_IDX_CALENDAR_COLOR = 4;
    public static final int CALENDAR_PROJECTION_IDX_CALENDAR_DISPLAY_NAME = 5;
    public static final int CALENDAR_PROJECTION_IDX_CALENDAR_ACCESS_LEVEL = 6;
    public static final int CALENDAR_PROJECTION_IDX_CALENDAR_TIME_ZONE = 7;
    public static final int CALENDAR_PROJECTION_IDX_VISIBLE = 8;
    public static final int CALENDAR_PROJECTION_IDX_SYNC_EVENTS = 9;
    public static final int CALENDAR_PROJECTION_IDX_OWNER_ACCOUNT = 10;

    public static final int EVENT_PROJECTION_IDX_CALENDAR_ID = 0;
    public static final int EVENT_PROJECTION_IDX_TITLE = 1;
    public static final int EVENT_PROJECTION_IDX_DESCRIPTION = 2;
    public static final int EVENT_PROJECTION_IDX_EVENT_LOCATION = 3;
    public static final int EVENT_PROJECTION_IDX_EVENT_COLOR = 4;
    public static final int EVENT_PROJECTION_IDX_DISPLAY_COLOR = 5;
    public static final int EVENT_PROJECTION_IDX_DTSTART = 6;
    public static final int EVENT_PROJECTION_IDX_DTEND = 7;
    public static final int EVENT_PROJECTION_IDX_DURATION = 8;
    public static final int EVENT_PROJECTION_IDX_EVENT_TIMEZONE = 9;
    public static final int EVENT_PROJECTION_IDX_EVENT_END_TIMEZONE = 10;
    public static final int EVENT_PROJECTION_IDX_ALL_DAY = 11;
    public static final int EVENT_PROJECTION_IDX_RRULE = 12;
    public static final int EVENT_PROJECTION_IDX_RDATE = 13;
    public static final int EVENT_PROJECTION_IDX_GUESTS_CAN_MODIFY = 14;
    public static final int EVENT_PROJECTION_IDX_GUESTS_CAN_INVITE_OTHERS = 15;
    public static final int EVENT_PROJECTION_IDX_GUESTS_CAN_SEE_GUESTS = 16;
    public static final int EVENT_PROJECTION_IDX_ORGANIZER = 17;
    public static final int _ID = 18;
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.EVENT_COLOR,
            CalendarContract.Events.DISPLAY_COLOR,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.DURATION,
            CalendarContract.Events.EVENT_TIMEZONE,
            CalendarContract.Events.EVENT_END_TIMEZONE,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.RRULE,
            CalendarContract.Events.RDATE,
            CalendarContract.Events.GUESTS_CAN_MODIFY,
            CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS,
            CalendarContract.Events.GUESTS_CAN_SEE_GUESTS,
            CalendarContract.Events.ORGANIZER,
            CalendarContract.Events._ID
    };

    /*public static class Events{
        public long calendar_id;
        public String title;
        public String description;
        public String eventLocation;
        public int eventColor;
        public int displayColor;
        public long dtstart;
        public long dtend;
        public String duration;
        public String eventTimezone;
        public String eventEndTimezone;
        public int allDay;
        public String rrule;
        public String rdate;
        public int guestsCanModify;
        public int guestsCanInviteOthers;
        public int guestsCanSeeGuests;
        public String organizer;

    }
    public static class Calendars{
        public long id;
        public String name;
        public String accountName;
        public String accountType;
        public int calendarColor;
        public String calendarDisplayName;
        public int calendarAccessLevel;
        public String calendarTimeZone;
        public int visible;
        public int syncEvents;
        public String ownerAccount;
    }*/

    public static List<List<String>> getCalendarList(Context context){
        final List<List<String>> cal=new ArrayList<>();
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED)
            return cal;

        // クエリ条件を設定する
        final Uri uri = CalendarContract.Calendars.CONTENT_URI;
        final String[] projection = CALENDAR_PROJECTION;
        final String selection = null;
        final String[] selectionArgs = null;
        final String sortOrder = null;

        // クエリを発行してカーソルを取得する
        final ContentResolver cr = context.getContentResolver();
        final Cursor cur = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        while (cur.moveToNext()) {
            List<String> list=new ArrayList<>();
            // カーソルから各プロパティを取得する
            list.add("" + cur.getLong(CALENDAR_PROJECTION_IDX_ID));
            list.add("" + cur.getString(CALENDAR_PROJECTION_IDX_NAME));
            list.add("" + cur.getString(CALENDAR_PROJECTION_IDX_ACCOUNT_NAME));
            list.add("" + cur.getString(CALENDAR_PROJECTION_IDX_ACCOUNT_TYPE));
            list.add("" + cur.getInt(CALENDAR_PROJECTION_IDX_CALENDAR_COLOR));
            list.add("" + cur.getString(CALENDAR_PROJECTION_IDX_CALENDAR_DISPLAY_NAME));
            list.add("" + cur.getInt(CALENDAR_PROJECTION_IDX_CALENDAR_ACCESS_LEVEL));
            list.add("" + cur.getString(CALENDAR_PROJECTION_IDX_CALENDAR_TIME_ZONE));
            list.add("" + cur.getInt(CALENDAR_PROJECTION_IDX_VISIBLE));
            list.add("" + cur.getInt(CALENDAR_PROJECTION_IDX_SYNC_EVENTS));
            list.add("" + cur.getString(CALENDAR_PROJECTION_IDX_OWNER_ACCOUNT));
            cal.add(list);

        }
        return cal;
    }
    public static List<List<String>> getEventList(Context context,long targetCalendarId){
        List<List<String>> ev=new ArrayList<>();

        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED)
            return ev;
        // クエリ条件を設定する
        final Uri uri = CalendarContract.Events.CONTENT_URI;
        final String selection = "(" + CalendarContract.Events.CALENDAR_ID + " = ?)";
        final String[] selectionArgs = new String[] {String.valueOf(targetCalendarId)};

        // クエリを発行してカーソルを取得する
        final ContentResolver cr = context.getContentResolver();
        final Cursor cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);


        while (cur.moveToNext()) {
            List<String> list =new ArrayList<>();
            // カーソルから各プロパティを取得する
            list.add("" + cur.getLong(EVENT_PROJECTION_IDX_CALENDAR_ID));
            list.add("" + cur.getString(EVENT_PROJECTION_IDX_TITLE));
            list.add("" + cur.getString(EVENT_PROJECTION_IDX_DESCRIPTION));
            list.add("" + cur.getString(EVENT_PROJECTION_IDX_EVENT_LOCATION));
            list.add("" + cur.getInt(EVENT_PROJECTION_IDX_EVENT_COLOR));
            list.add("" + cur.getInt(EVENT_PROJECTION_IDX_DISPLAY_COLOR));
            list.add("" + cur.getLong(EVENT_PROJECTION_IDX_DTSTART));
            list.add("" + cur.getLong(EVENT_PROJECTION_IDX_DTEND));
            list.add("" + cur.getString(EVENT_PROJECTION_IDX_DURATION));
            list.add("" + cur.getString(EVENT_PROJECTION_IDX_EVENT_TIMEZONE));
            list.add("" + cur.getString(EVENT_PROJECTION_IDX_EVENT_END_TIMEZONE));
            list.add("" + cur.getInt(EVENT_PROJECTION_IDX_ALL_DAY));
            list.add("" + cur.getString(EVENT_PROJECTION_IDX_RRULE));
            list.add("" + cur.getString(EVENT_PROJECTION_IDX_RDATE));
            list.add("" + cur.getInt(EVENT_PROJECTION_IDX_GUESTS_CAN_MODIFY));
            list.add("" + cur.getInt(EVENT_PROJECTION_IDX_GUESTS_CAN_INVITE_OTHERS));
            list.add("" + cur.getInt(EVENT_PROJECTION_IDX_GUESTS_CAN_SEE_GUESTS));
            list.add("" + cur.getString(EVENT_PROJECTION_IDX_ORGANIZER));
            list.add("" + cur.getLong(_ID));
            ev.add(list);
        }
        return ev;
    }
    private static List<List<String>> getAllEventList(Context context){
        List<List<String>> cals=getCalendarList(context);
        List<List<String>> eves=new ArrayList<>();
        for (List<String> cal:cals)
            eves.addAll(getEventList(context,Integer.valueOf(cal.get(CALENDAR_PROJECTION_IDX_ID))));
        return eves;
    }

    public static List<List<String>> getAllEventList(Context context,int year,int month){
        /*LocalDateIterable localDateIterable = LocalDateIteratorFactory.createLocalDateIterable();
        LocalDateIterator iterator = localDateIterable.iterator();
        LocalDate localDate=iterator.next();
        localDate*/

        List<List<String>> neves=new ArrayList<>();


        year=(12+month)/12-1+year;
        month=(1200+month)%12;
        List<List<String>> eves= CalendarData.getAllEventList(context);
        final long smonth=DateData.getLongOnTime(year,month,1,0,0,0);
        final long emonth=DateData.getLongOnTime(year,month,DateData.getNumberOfDaysInMonth(year, month),23,59,59);

        for (List<String>events:eves){
            final String rule=events.get(EVENT_PROJECTION_IDX_RRULE);
            if ("null".equals(rule)) {
                final boolean allday = events.get(CalendarData.EVENT_PROJECTION_IDX_ALL_DAY).equals("1");
                final long time = Long.valueOf(events.get(CalendarData.EVENT_PROJECTION_IDX_DTSTART));
                final long stime = allday ?
                        DateData.getLongOnTime(DateData.getYear(time), DateData.getMonth(time), DateData.getDateOfMonth(time), 0, 0, 0)
                        : Long.valueOf(events.get(CalendarData.EVENT_PROJECTION_IDX_DTSTART));
                final long etime = allday ?
                        DateData.getLongOnTime(DateData.getYear(time), DateData.getMonth(time), DateData.getDateOfMonth(time), 23, 59, 59) :
                        Long.valueOf(events.get(CalendarData.EVENT_PROJECTION_IDX_DTEND));
                if (smonth < etime && emonth > stime) {
                    neves.add(events);
                }
            }
            else {
                LocalDateIterable localDateIterable = null;

                String rdate=events.get(CalendarData.EVENT_PROJECTION_IDX_RDATE);

                final long stime = !"null".equals(rdate) ? Long.valueOf(rdate)
                        : Long.valueOf(events.get(CalendarData.EVENT_PROJECTION_IDX_DTSTART));



                if (emonth<stime)
                    continue;

                if ("null".equals(events.get(CalendarData.EVENT_PROJECTION_IDX_DURATION)))
                    continue;

                final long duration=Long.valueOf(events.get(CalendarData.EVENT_PROJECTION_IDX_DURATION)
                        .replaceAll("[^0-9]", ""))*1000;

                try {
                    localDateIterable = LocalDateIteratorFactory.createLocalDateIterable("RRULE:"+rule
                            , LocalDate.fromDateFields(new Date(stime)),true);
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue;
                }


                if (localDateIterable==null)
                    continue;


                LocalDateIterator iterator = localDateIterable.iterator();


                int safety=0;
                while (iterator.hasNext()) {
                    LocalDate localDate=iterator.next();

                   long st=localDate.toDate().getTime();

                    if (smonth > st+duration)
                        continue;

                    if (emonth < st)
                        break;

                    st=DateData.getLongOnTime(DateData.getYear(st),DateData.getMonth(st),DateData.getDateOfMonth(st)
                            ,DateData.getHour(stime),DateData.getMinute(stime),DateData.getSecond(stime));



                    List<String> list=new ArrayList<>(events);


                    list.set(EVENT_PROJECTION_IDX_DTSTART,String.valueOf(
                            st
                    ));
                    list.set(EVENT_PROJECTION_IDX_DTEND,String.valueOf(
                            st+duration
                    ));

                    neves.add(list);

                    if (safety>10000)
                        break;
                    safety++;
                }

            }
        }
        return neves;
    }
}
