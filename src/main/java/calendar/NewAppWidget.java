package calendar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Schedule.Schedule;
import save.ObjectStorage;
import simple_memo.Simple_Memo;
import todays_memo.todays_memo;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    public static final String PARAM="6stKwNFG8QH6Fp44ZTuC9m2j38FnHtpA85j2SJaLgE_65b6jVbBNUQesz82fp-2Cmg3Hp-cx9HkmG-BymcGGwcTu5dEpMyh5bUst";
    private static final String NEXT_MONTH="NEXT_MONTH"+PARAM;
    private static final String PREVIOUS_MONTH="PREVIOUS_MONTH"+PARAM;
    private static final String TODAY="TODAY"+PARAM;
    private static final String CALENDAR_SELECT="CALENDAR_SELECT"+PARAM;
    public static final String CALENDAR_DATA="CALENDAR_DATA"+PARAM;
    private static final int FLIPPER_NEXT=1;
    private static final int FLIPPER_PREVIOUS=2;
    public static final int FLIPPER_NONE=0;
    private static final int FLIPPER_FADE=3;

    private static final String SCALE_MONTH="SCALE_MONTH";
    public static final String CALENDAR_POINT="CALENDAR_POINT";

    private static final TextBitmap textBitmap=new TextBitmap();
    private static final Map<Integer,int[][][]> colors=new HashMap<>();
    private static final String[] fonts=new String[]{null};


    private static PendingIntent getPendingSelfIntent(Context context,String action,int appWidgetId,int scalemonth){
        Intent refreshIntent = new Intent(context, NewAppWidget.class);
        refreshIntent.setAction(action+appWidgetId);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        refreshIntent.putExtra(SCALE_MONTH,scalemonth);
        return PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_CANCEL_CURRENT);

    }
    private static PendingIntent getPendingSelfIntent_cp(Context context,String action,int appWidgetId,int scalemonth,String cp){
        Intent refreshIntent = new Intent(context, NewAppWidget.class);
        refreshIntent.setAction(action+appWidgetId);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        refreshIntent.putExtra(SCALE_MONTH,scalemonth);
        refreshIntent.putExtra(CALENDAR_POINT,cp);
        return PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_CANCEL_CURRENT);

    }
    private static void updateIntent(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId,int scaleMonth,int flipper,String calendarpoint) {
        RemoteViews views= new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        
        int[][] cellses={
                {R.id.su1_layout,R.id.mo1_layout,R.id.tu1_layout,R.id.we1_layout,R.id.th1_layout,R.id.fr1_layout,R.id.sa1_layout},
                {R.id.su2_layout,R.id.mo2_layout,R.id.tu2_layout,R.id.we2_layout,R.id.th2_layout,R.id.fr2_layout,R.id.sa2_layout},
                {R.id.su3_layout,R.id.mo3_layout,R.id.tu3_layout,R.id.we3_layout,R.id.th3_layout,R.id.fr3_layout,R.id.sa3_layout},
                {R.id.su4_layout,R.id.mo4_layout,R.id.tu4_layout,R.id.we4_layout,R.id.th4_layout,R.id.fr4_layout,R.id.sa4_layout},
                {R.id.su5_layout,R.id.mo5_layout,R.id.tu5_layout,R.id.we5_layout,R.id.th5_layout,R.id.fr5_layout,R.id.sa5_layout},
                {R.id.su6_layout,R.id.mo6_layout,R.id.tu6_layout,R.id.we6_layout,R.id.th6_layout,R.id.fr6_layout,R.id.sa6_layout}};
        int i=0;
        for (int[] cells:cellses) {
            int j=0;
            for (int cell : cells) {
                views.setOnClickPendingIntent(cell,null);
                j++;
            }
            i++;
        }
        views.setOnClickPendingIntent(R.id.next_arrow_vf,null);
        views.setOnClickPendingIntent(R.id.previous_arrow_vf,null);
        views.setOnClickPendingIntent(R.id.calendar_today,null);
        views.setOnClickPendingIntent(R.id.calendar_title,null);
        appWidgetManager.updateAppWidget(appWidgetId,views);
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId,int scaleMonth,int flipper,String calendarpoint) {
        // Construct the RemoteViews object
        //String font ="Inspiration-Regular";

        Map<String,String> style_= ObjectStorage.get(CALENDAR_DATA+appWidgetId, Map.class,context);

        String color_key="Color";//back color
        String color2_key="Color2";//today text
        String color3_key="Color3";//today back

        String day_text_color_key="DayTextColor";//text color
        String day_font_key="DayFont";//font

        String dc_key_w="dc_key_w";
        String sdc_key_w="sdc_key_w";
        String sdf_key_w="sdf_key_w";

        String dc_key_sa="dc_key_sa";
        String sdc_key_sa="sdc_key_sa";
        String sdf_key_sa="sdf_key_sa";

        String dc_key_su="dc_key_su";
        String sdc_key_su="sdc_key_su";
        String sdf_key_su="sdf_key_su";

        String dc_key_h="dc_key_h";
        String sdc_key_h="sdc_key_h";
        String sdf_key_h="sdf_key_h";

        String style_key="style";


        if (style_==null){
            style_=new HashMap<>();

            style_.put(dc_key_w,String.valueOf(Color.BLACK));
            style_.put(sdc_key_w,String.valueOf(Color.BLACK));
            style_.put(sdf_key_w,String.valueOf(context.getColor(R.color.gray)));

            style_.put(dc_key_sa,String.valueOf(context.getColor(R.color.blue)));
            style_.put(sdc_key_sa,String.valueOf(context.getColor(R.color.blue)));
            style_.put(sdf_key_sa,String.valueOf(context.getColor(R.color.gray)));

            style_.put(dc_key_su,String.valueOf(context.getColor(R.color.red)));
            style_.put(sdc_key_su,String.valueOf(context.getColor(R.color.red)));
            style_.put(sdf_key_su,String.valueOf(context.getColor(R.color.gray)));

            style_.put(dc_key_h,String.valueOf(context.getColor(R.color.red)));
            style_.put(sdc_key_h,String.valueOf(context.getColor(R.color.red)));
            style_.put(sdf_key_h,String.valueOf(context.getColor(R.color.gray)));

            style_.put(color_key,String.valueOf(Color.WHITE));
            style_.put(color2_key,String.valueOf(Color.WHITE));
            style_.put(color3_key,String.valueOf(context.getColor(R.color.blue)));
            style_.put(day_text_color_key,String.valueOf(Color.BLACK));
            style_.put(day_font_key,null);
            style_.put(style_key,String.valueOf(R.drawable.underline));
        }

        String font = style_.get(day_font_key);



        textBitmap.Initialization(context,font,context.getResources().getDimension(R.dimen.date_text_size),
                new int[]{Color.BLACK},
                Color.WHITE,
                DateData.getYear(),DateData.getMonth(),Color.BLACK,style_.getOrDefault("Language",null));


        if (fonts[0]==null) {
            if (font!=null) {
                fonts[0]=font;
                textBitmap.startInit();
            }
        }
        else if (!fonts[0].equals(font)) {
            fonts[0]=font;
            textBitmap.startInit();
        }

        RemoteViews views= new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        List<eveData> ne=null;

        if (!colors.containsKey(appWidgetId)) {
            ne=geteves(context,DateData.getYear(), DateData.getMonth() + scaleMonth);
            colors.put(appWidgetId, getDayColors(DateData.getYear(), DateData.getMonth() + scaleMonth, context, style_,ne));
        }
        else if (calendarpoint==null){
            ne=geteves(context,DateData.getYear(), DateData.getMonth() + scaleMonth);
            colors.put(appWidgetId, getDayColors(DateData.getYear(), DateData.getMonth() + scaleMonth, context, style_,ne));
        }
        else if (!calendarpoint.contains(",")) {
            ne=geteves(context,DateData.getYear(), DateData.getMonth() + scaleMonth);
            colors.put(appWidgetId, getDayColors(DateData.getYear(), DateData.getMonth() + scaleMonth, context, style_,ne));
        }



        int seDate=setDateAct(DateData.getYear(),DateData.getMonth()+scaleMonth,System.currentTimeMillis()
                ,calendarpoint,views,context,style_,appWidgetId);


        //views.addView(R.id.su2_bar,new RemoteViews(context.getPackageName(), R.layout.calendar_bar_point));
        //cellの空白部分を押した場合
        if (calendarpoint!=null&&seDate<=0){
            if (calendarpoint.contains(",")) {
                return;
            }
        }

        updateIntent(context, appWidgetManager, appWidgetId, scaleMonth, flipper, calendarpoint);


        views.setOnClickPendingIntent(R.id.next_arrow_vf,getPendingSelfIntent(context,NEXT_MONTH,appWidgetId,scaleMonth));
        views.setOnClickPendingIntent(R.id.previous_arrow_vf,getPendingSelfIntent(context,PREVIOUS_MONTH,appWidgetId,scaleMonth));
        views.setOnClickPendingIntent(R.id.calendar_today,getPendingSelfIntent(context,TODAY,appWidgetId,scaleMonth));



        //todays memo, scheduleの呼び出し
        if (calendarpoint!=null&&seDate>0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (calendarpoint.contains(",")||"now".equals(calendarpoint)) {
                        int year = (12 + DateData.getMonth() + scaleMonth) / 12 - 1 + DateData.getYear();
                        int month = (1200 + DateData.getMonth() + scaleMonth) % 12;
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, seDate, 12, 00, 00);
                        PendingIntent configPendingIntent = todays_memo.getPendingSelfIntentFromCalendar(context, todays_memo.CP, c.getTimeInMillis());
                        PendingIntent configPendingIntent2 = Schedule.getPendingSelfIntentFromCalendar(context, Schedule.CP, c.getTimeInMillis());
                        try {
                            configPendingIntent.send();
                            configPendingIntent2.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        int[][] cellses={
                {R.id.su1_layout,R.id.mo1_layout,R.id.tu1_layout,R.id.we1_layout,R.id.th1_layout,R.id.fr1_layout,R.id.sa1_layout},
                {R.id.su2_layout,R.id.mo2_layout,R.id.tu2_layout,R.id.we2_layout,R.id.th2_layout,R.id.fr2_layout,R.id.sa2_layout},
                {R.id.su3_layout,R.id.mo3_layout,R.id.tu3_layout,R.id.we3_layout,R.id.th3_layout,R.id.fr3_layout,R.id.sa3_layout},
                {R.id.su4_layout,R.id.mo4_layout,R.id.tu4_layout,R.id.we4_layout,R.id.th4_layout,R.id.fr4_layout,R.id.sa4_layout},
                {R.id.su5_layout,R.id.mo5_layout,R.id.tu5_layout,R.id.we5_layout,R.id.th5_layout,R.id.fr5_layout,R.id.sa5_layout},
                {R.id.su6_layout,R.id.mo6_layout,R.id.tu6_layout,R.id.we6_layout,R.id.th6_layout,R.id.fr6_layout,R.id.sa6_layout}};





        //矢印とtodayボタンが出る処理
        boolean isNow=("now".equals(calendarpoint)||seDate==DateData.getDateOfMonth())&&scaleMonth==0;
        if (isNow){
            views.setViewVisibility(R.id.calendar_today,View.GONE);
            views.setViewVisibility(R.id.calendar_today_img,View.GONE);
            views.setViewVisibility(R.id.next_arrow_vf,View.GONE);
            views.setViewVisibility(R.id.previous_arrow_vf,View.GONE);
            views.setOnClickPendingIntent(R.id.calendar_title2,getPendingSelfIntent_cp(context,CALENDAR_SELECT+"TITLE",appWidgetId,scaleMonth,"isNow"));

            int i=0;
            for (int[] cells:cellses) {
                int j=0;
                for (int cell : cells) {
                    views.setOnClickPendingIntent(cell,getPendingSelfIntent_cp(context, CALENDAR_SELECT+i + "," + j, appWidgetId,scaleMonth,i + "," + j + "," +"isNow"));
                    j++;
                }
                i++;
            }
        }
        else{
            views.setOnClickPendingIntent(R.id.calendar_title2,null);
            int i=0;
            for (int[] cells:cellses) {
                int j=0;
                for (int cell : cells) {
                    views.setOnClickPendingIntent(cell,getPendingSelfIntent_cp(context, CALENDAR_SELECT+i + "," + j, appWidgetId,scaleMonth,i + "," + j));
                    j++;
                }
                i++;
            }

            views.setViewVisibility(R.id.calendar_today,View.VISIBLE);
            views.setViewVisibility(R.id.calendar_today_img,View.VISIBLE);
            views.setViewVisibility(R.id.next_arrow_vf,View.VISIBLE);
            views.setViewVisibility(R.id.previous_arrow_vf,View.VISIBLE);
            if (calendarpoint != null) {
                if (calendarpoint.contains("isNow")) {
                    views.showNext(R.id.next_arrow_vf);
                    views.showNext(R.id.previous_arrow_vf);
                    views.showNext(R.id.calendar_today_vf);
                }
            }
        }

        views.setViewVisibility(R.id.calendar_title,isNow?View.INVISIBLE:View.VISIBLE);
        views.setViewVisibility(R.id.calendar_title2,isNow?View.VISIBLE:View.GONE);

        //cellを押しただけの時はこのまま終了
        if (calendarpoint!=null){
            if (calendarpoint.contains(",")) {
                appWidgetManager.updateAppWidget(appWidgetId, views);
                return;
            }
        }

        if (ne==null){
            ne=geteves(context,DateData.getYear(),DateData.getMonth()+scaleMonth);
        }

        //カレンダー更新
        setCalendar(DateData.getYear(),DateData.getMonth()+scaleMonth,System.currentTimeMillis()
                ,views,context,style_,ne);

        views.setImageViewBitmap(R.id.calendar_title,textBitmap
                .getMonthBitmap(textBitmap.getMonthText(DateData.getYear(), DateData.getMonth()+scaleMonth)));
        views.setInt(R.id.calendar_title, "setColorFilter", Integer.valueOf(style_.get("DayTextColor")));
        views.setInt(R.id.calendar_title, "setImageAlpha", Color.alpha(Integer.valueOf(style_.get("DayTextColor"))));

        views.setImageViewBitmap(R.id.calendar_title2,textBitmap
                .getMonthBitmap(textBitmap.getMonthText(DateData.getYear(), DateData.getMonth()+scaleMonth)));
        views.setInt(R.id.calendar_title2, "setColorFilter", Integer.valueOf(style_.get("DayTextColor")));
        views.setInt(R.id.calendar_title2, "setImageAlpha", Color.alpha(Integer.valueOf(style_.get("DayTextColor"))));

        if (flipper==FLIPPER_PREVIOUS)
            views.showNext(R.id.calendar_flipper1);
        else if (flipper==FLIPPER_NEXT)
            views.showNext(R.id.calendar_flipper2);
        else if (flipper==FLIPPER_FADE)
            views.showNext(R.id.calendar_flipper3);

        views.setImageViewBitmap(R.id.calendar_today,textBitmap.getToday());
        views.setInt(R.id.calendar_today, "setColorFilter", Integer.valueOf(style_.get(color2_key)));
        views.setInt(R.id.calendar_today, "setImageAlpha", Color.alpha(Integer.valueOf(style_.get(color2_key))));

        views.setInt(R.id.calendar_today_img, "setColorFilter", Integer.valueOf(style_.get(color3_key)));
        views.setInt(R.id.calendar_today_img, "setImageAlpha", Color.alpha(Integer.valueOf(style_.get(color3_key))));

        views.setInt(R.id.calendar_BG, "setColorFilter", Integer.valueOf(style_.get(color_key)));
        views.setInt(R.id.calendar_BG, "setImageAlpha", Color.alpha(Integer.valueOf(style_.get(color_key))));

        views.setImageViewBitmap(R.id.next_arrow,textBitmap.getNext());
        views.setInt(R.id.next_arrow, "setColorFilter", Integer.valueOf(style_.get(day_text_color_key)));
        views.setInt(R.id.next_arrow, "setImageAlpha", Color.alpha(Integer.valueOf(style_.get(day_text_color_key))));

        views.setImageViewBitmap(R.id.previous_arrow,textBitmap.getPrevious());
        views.setInt(R.id.previous_arrow, "setColorFilter", Integer.valueOf(style_.get(day_text_color_key)));
        views.setInt(R.id.previous_arrow, "setImageAlpha", Color.alpha(Integer.valueOf(style_.get(day_text_color_key))));
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //String font ="Inspiration-Regular";
        String font =null;
        // There may be multiple widgets active, so update all of them
        textBitmap.Initialization(context,font,context.getResources().getDimension(R.dimen.date_text_size),
                new int[]{context.getColor(R.color.black),
                        context.getColor(R.color.red),
                        context.getColor(R.color.blue)},
                Color.WHITE,
                DateData.getYear(),DateData.getMonth(),Color.BLACK,null);
        textBitmap.startInit();
        fonts[0]=null;
        for (int appWidgetId:appWidgetIds) {
            Log.d("appWidgetId",""+appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId, 0, FLIPPER_NONE,"now");
        }
        context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NewAppWidget.this.onReceive(context, intent);
            }
        }, new IntentFilter(Intent.ACTION_DATE_CHANGED));
        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if (action.contains(PREVIOUS_MONTH)){
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int scaleMonth=extras.getInt(SCALE_MONTH,1)-1;
                int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,-1);
                if (appWidgetId!=-1) {
                    updateAppWidget(context,AppWidgetManager.getInstance(context),appWidgetId,scaleMonth,FLIPPER_PREVIOUS,null);
                }
            }
        }
        else if (action.contains(NEXT_MONTH)){
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int scaleMonth=extras.getInt(SCALE_MONTH,-1)+1;
                int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,-1);
                if (appWidgetId!=-1) {
                    updateAppWidget(context,AppWidgetManager.getInstance(context),appWidgetId,scaleMonth,FLIPPER_NEXT,null);
                }
            }
        }
        else if (action.contains(TODAY)){
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int scaleMonth=0;
                int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,-1);
                if (appWidgetId!=-1) {
                    updateAppWidget(context,AppWidgetManager.getInstance(context),appWidgetId,scaleMonth,FLIPPER_FADE,"now");
                }
            }
        }
        else if (action.contains(CALENDAR_SELECT)){
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int scaleMonth=extras.getInt(SCALE_MONTH,0);
                int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,-1);
                String cp=extras.getString(CALENDAR_POINT);
                if (appWidgetId!=-1) {
                    updateAppWidget(context,AppWidgetManager.getInstance(context),appWidgetId,scaleMonth,FLIPPER_NONE,cp);
                }
            }
        }
        else if (Intent.ACTION_DATE_CHANGED.equals(action)){
            for (int appWidgetId:AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, NewAppWidget.class))) {
                updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId, 0, FLIPPER_NONE,"now");
            }
        }
        super.onReceive(context, intent);

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        for (int id:appWidgetIds){
            ObjectStorage.clear(CALENDAR_DATA+id,context);
            colors.remove(id);
        }

        super.onDeleted(context, appWidgetIds);
    }

    private static List<eveData> geteves(Context context,int year,int month){

        CalendarData.getAllEventList(context,year,month);

        year=(12+month)/12-1+year;
        month=(1200+month)%12;
        List<List<String>> eves= CalendarData.getAllEventList(context,year,month);
        List<eveData> ne=new ArrayList<>();
        final long smonth=DateData.getLongOnTime(year,month,1,0,0,0);
        final long emonth=DateData.getLongOnTime(year,month,DateData.getNumberOfDaysInMonth(year, month),23,59,59);

        for (List<String>events:eves){

            Log.d("rule",events.get(CalendarData.EVENT_PROJECTION_IDX_RRULE));
            Log.d("rdate",events.get(CalendarData.EVENT_PROJECTION_IDX_RDATE));
            Log.d("duration",events.get(CalendarData.EVENT_PROJECTION_IDX_DURATION));

            final boolean allday=events.get(CalendarData.EVENT_PROJECTION_IDX_ALL_DAY).equals("1");
            final long time=Long.valueOf(events.get(CalendarData.EVENT_PROJECTION_IDX_DTSTART));
            final long stime=allday?
                    DateData.getLongOnTime(DateData.getYear(time),DateData.getMonth(time),DateData.getDateOfMonth(time),0,0,0)
                    :Long.valueOf(events.get(CalendarData.EVENT_PROJECTION_IDX_DTSTART));
            final long etime=allday?
                    DateData.getLongOnTime(DateData.getYear(time),DateData.getMonth(time),DateData.getDateOfMonth(time),23,59,59):
                    Long.valueOf(events.get(CalendarData.EVENT_PROJECTION_IDX_DTEND));
            if (smonth<etime&&emonth>stime){
                boolean ish=events.get(CalendarData.EVENT_PROJECTION_IDX_ORGANIZER).contains("#holiday@group.v.calendar.google.com");
                int color=Integer.valueOf(events.get(CalendarData.EVENT_PROJECTION_IDX_DISPLAY_COLOR));
                ne.add(new eveData(stime,etime,ish,color));
            }
        }
        return ne;
    }

    //0:null=0,1:textcolor,2:stextcolor,3:sbackcolor
    private static int[][][] getDayColors(int year,int month,Context context,Map<String,String> style_,List<eveData> ne){

        year=(12+month)/12-1+year;
        month=(1200+month)%12;

        String dc_key_w="dc_key_w";
        String sdc_key_w="sdc_key_w";
        String sdf_key_w="sdf_key_w";

        String dc_key_sa="dc_key_sa";
        String sdc_key_sa="sdc_key_sa";
        String sdf_key_sa="sdf_key_sa";

        String dc_key_su="dc_key_su";
        String sdc_key_su="sdc_key_su";
        String sdf_key_su="sdf_key_su";

        String dc_key_h="dc_key_h";
        String sdc_key_h="sdc_key_h";
        String sdf_key_h="sdf_key_h";



        int[][][] c=new int[6][7][4];



        int week;


        for (week=1;week<=6;week++){
            int dateofweek;

            for (dateofweek=1;dateofweek<=7;dateofweek++){
                int date=DateData.getDateonCalendar(year,month,week,dateofweek);

                if (date!=0) {
                    String dc=dc_key_w;
                    String sdc=sdc_key_w;
                    String sdf=sdf_key_w;
                    c[week-1][dateofweek-1][0]=1;

                    if (dateofweek==1){
                        dc = dc_key_su;
                        sdc = sdc_key_su;
                        sdf = sdf_key_su;
                    }
                    else if (dateofweek==7){
                        dc = dc_key_sa;
                        sdc = sdc_key_sa;
                        sdf = sdf_key_sa;
                    }
                    else {
                        final long fmiltime = DateData.getLongOnTime(year, month, date, 0, 0, 0);
                        final long emiltime = DateData.getLongOnTime(year, month, date, 23, 59, 59);
                        final long fmiltime_on_fo = DateData.getLongOnTime(year, month, date, 10, 0, 0);
                        for (eveData events : ne) {
                            if (fmiltime < events.etime && emiltime > events.stime) {
                                if (events.isHoliday) {
                                    if (fmiltime_on_fo < events.etime && (dateofweek != 1) && (dateofweek != 7)) {
                                        dc = dc_key_h;
                                        sdc = sdc_key_h;
                                        sdf = sdf_key_h;
                                    }
                                }
                            }
                        }
                    }

                    c[week-1][dateofweek-1][1]=Integer.valueOf(style_.get(dc));
                    c[week-1][dateofweek-1][2]=Integer.valueOf(style_.get(sdc));
                    c[week-1][dateofweek-1][3]=Integer.valueOf(style_.get(sdf));
                }
                else
                    c[week-1][dateofweek-1][0]=0;


            }

        }

        return c;
    }

    private static void setCalendar(int year,int month,long nowdate,RemoteViews views
            ,Context context,Map<String,String> style_,List<eveData> ne){




        year=(12+month)/12-1+year;
        month=(1200+month)%12;

        int[] dow=new int[]{R.id.su,R.id.mo,R.id.tu,R.id.we,R.id.th,R.id.fr,R.id.sa};

        views.setImageViewBitmap(dow[0],textBitmap.getDowBitmap(0));
        views.setInt(dow[0], "setColorFilter", Integer.valueOf(style_.get("dc_key_su")));
        views.setInt(dow[0], "setImageAlpha", Color.alpha(Integer.valueOf(style_.get("dc_key_su"))));

        views.setImageViewBitmap(dow[6],textBitmap.getDowBitmap(6));
        views.setInt(dow[6], "setColorFilter", Integer.valueOf(style_.get("dc_key_sa")));
        views.setInt(dow[6], "setImageAlpha", Color.alpha(Integer.valueOf(style_.get("dc_key_sa"))));

        for (int i=1;i<6;i++){
            views.setImageViewBitmap(dow[i],textBitmap.getDowBitmap(i));
            views.setInt(dow[i], "setColorFilter", Integer.valueOf(style_.get("dc_key_w")));
            views.setInt(dow[i], "setImageAlpha", Color.alpha(Integer.valueOf(style_.get("dc_key_w"))));
        }

        int[][] cellses={
                {R.id.su1,R.id.mo1,R.id.tu1,R.id.we1,R.id.th1,R.id.fr1,R.id.sa1},
                {R.id.su2,R.id.mo2,R.id.tu2,R.id.we2,R.id.th2,R.id.fr2,R.id.sa2},
                {R.id.su3,R.id.mo3,R.id.tu3,R.id.we3,R.id.th3,R.id.fr3,R.id.sa3},
                {R.id.su4,R.id.mo4,R.id.tu4,R.id.we4,R.id.th4,R.id.fr4,R.id.sa4},
                {R.id.su5,R.id.mo5,R.id.tu5,R.id.we5,R.id.th5,R.id.fr5,R.id.sa5},
                {R.id.su6,R.id.mo6,R.id.tu6,R.id.we6,R.id.th6,R.id.fr6,R.id.sa6}};
        int[][] barses={
                {R.id.su1_bar,R.id.mo1_bar,R.id.tu1_bar,R.id.we1_bar,R.id.th1_bar,R.id.fr1_bar,R.id.sa1_bar},
                {R.id.su2_bar,R.id.mo2_bar,R.id.tu2_bar,R.id.we2_bar,R.id.th2_bar,R.id.fr2_bar,R.id.sa2_bar},
                {R.id.su3_bar,R.id.mo3_bar,R.id.tu3_bar,R.id.we3_bar,R.id.th3_bar,R.id.fr3_bar,R.id.sa3_bar},
                {R.id.su4_bar,R.id.mo4_bar,R.id.tu4_bar,R.id.we4_bar,R.id.th4_bar,R.id.fr4_bar,R.id.sa4_bar},
                {R.id.su5_bar,R.id.mo5_bar,R.id.tu5_bar,R.id.we5_bar,R.id.th5_bar,R.id.fr5_bar,R.id.sa5_bar},
                {R.id.su6_bar,R.id.mo6_bar,R.id.tu6_bar,R.id.we6_bar,R.id.th6_bar,R.id.fr6_bar,R.id.sa6_bar}};
        int nowYear=DateData.getYear(nowdate),nowMonth=DateData.getMonth(nowdate);
        nowdate=DateData.getDateOfMonth(nowdate);

        int week=0;
        for (int[] cells:cellses){
            week++;
            int dateofweek=0;
            for (int cell:cells){
                dateofweek++;

                views.removeAllViews(barses[week-1][dateofweek-1]);
                int date=DateData.getDateonCalendar(year,month,week,dateofweek);

                if (date!=0) {
                    final long fmiltime=DateData.getLongOnTime(year,month,date,0,0,0);
                    final long emiltime=DateData.getLongOnTime(year,month,date,23,59,59);
                    for (eveData events:ne){
                        if (fmiltime<events.etime&&emiltime>events.stime){
                            if (!events.isHoliday){
                                //Log.d("color",);
                                try {
                                    RemoteViews rv=new RemoteViews(context.getPackageName(), R.layout.calendar_bar_point);
                                    rv.setInt(R.id.calendar_bar_point_im, "setColorFilter",events.color);
                                    views.addView(barses[week-1][dateofweek-1],rv);
                                }catch (Exception e){}
                            }
                        }
                    }

                }

                /*SpannableString content = new SpannableString(d);
                if (year==nowYear&&month==nowMonth&&date==nowdate) {
                    views.setInt(cell, "setBackgroundResource", R.drawable.underline);
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                }
                else
                    views.setInt(cell, "setBackgroundResource", 0);

                views.setTextViewText(cell, content);
                 */
                if(date>0&&date<32) {
                    views.setViewVisibility(cell,View.VISIBLE);
                    views.setImageViewBitmap(cell, textBitmap.getDayBitmap(date,0));


                }
                else
                    views.setViewVisibility(cell,View.INVISIBLE);


            }

        }
        int visibility = (DateData.getDateonCalendar(year,month,6,1)!=0)? View.VISIBLE:View.GONE;
        views.setViewVisibility(R.id.sixth_week,visibility);
    }

    private static int setDateAct(int year,int month,long nowdate, String selectday
            , RemoteViews views,Context context,Map<String,String> style_,int id){
        String style_key="style";

        year=(12+month)/12-1+year;
        month=(1200+month)%12;
        int week=-1,dow=-1;
        boolean setSelect=true;
        if (selectday==null){
            setSelect=false;
        }
        else if ("now".equals(selectday)){
            if (DateData.getYear(nowdate)==year&&DateData.getMonth(nowdate)==month){
                int[] d=DateData.getCalendarPointOfDate(year,month,DateData.getDateOfMonth(nowdate));
                week=d[0]-1;
                dow=d[1]-1;
            }
            else {
                setSelect=false;
            }
        }
        else if ("isNow".equals(selectday)){
            setSelect=false;
        }
        else{
            String[] sd=selectday.split(",");
            week=Integer.valueOf(sd[0]);
            dow=Integer.valueOf(sd[1]);
            if (DateData.getDateonCalendar(year,month,week+1,dow+1)==0){
                return -1;
            }
        }

        int[][] days={
                {R.id.su1,R.id.mo1,R.id.tu1,R.id.we1,R.id.th1,R.id.fr1,R.id.sa1},
                {R.id.su2,R.id.mo2,R.id.tu2,R.id.we2,R.id.th2,R.id.fr2,R.id.sa2},
                {R.id.su3,R.id.mo3,R.id.tu3,R.id.we3,R.id.th3,R.id.fr3,R.id.sa3},
                {R.id.su4,R.id.mo4,R.id.tu4,R.id.we4,R.id.th4,R.id.fr4,R.id.sa4},
                {R.id.su5,R.id.mo5,R.id.tu5,R.id.we5,R.id.th5,R.id.fr5,R.id.sa5},
                {R.id.su6,R.id.mo6,R.id.tu6,R.id.we6,R.id.th6,R.id.fr6,R.id.sa6}};

        int[][] cellses={
                {R.id.su1_layout,R.id.mo1_layout,R.id.tu1_layout,R.id.we1_layout,R.id.th1_layout,R.id.fr1_layout,R.id.sa1_layout},
                {R.id.su2_layout,R.id.mo2_layout,R.id.tu2_layout,R.id.we2_layout,R.id.th2_layout,R.id.fr2_layout,R.id.sa2_layout},
                {R.id.su3_layout,R.id.mo3_layout,R.id.tu3_layout,R.id.we3_layout,R.id.th3_layout,R.id.fr3_layout,R.id.sa3_layout},
                {R.id.su4_layout,R.id.mo4_layout,R.id.tu4_layout,R.id.we4_layout,R.id.th4_layout,R.id.fr4_layout,R.id.sa4_layout},
                {R.id.su5_layout,R.id.mo5_layout,R.id.tu5_layout,R.id.we5_layout,R.id.th5_layout,R.id.fr5_layout,R.id.sa5_layout},
                {R.id.su6_layout,R.id.mo6_layout,R.id.tu6_layout,R.id.we6_layout,R.id.th6_layout,R.id.fr6_layout,R.id.sa6_layout}};


        int[][][] d=colors.get(id);

        int w=0;
        for (int[] cells:cellses) {
            int dow_=0;
            for (int cell : cells) {
                views.removeAllViews(cell);
                if (w == week&&dow_==dow) {
                    views.setInt(days[w][dow_], "setColorFilter", d[w][dow_][2]);
                    views.setInt(days[w][dow_], "setImageAlpha", Color.alpha(Integer.valueOf(d[w][dow_][2])));
                } else {
                    views.setInt(days[w][dow_], "setColorFilter", d[w][dow_][1]);
                    views.setInt(days[w][dow_], "setImageAlpha", Color.alpha(Integer.valueOf(d[w][dow_][1])));
                }
                dow_++;
            }
            w++;
        }
        if (!setSelect)
            return -1;

        Map<String,Integer>slist=new LinkedHashMap<>();

        slist.put("Style1",R.drawable.underline);
        slist.put("Style2",R.drawable.underline2);
        slist.put("Style3",R.drawable.underline3);

        views.addView(cellses[week][dow],new RemoteViews(context.getPackageName(),R.layout.calendar_select_layout));
        views.showNext(R.id.calendar_select);
        views.setInt(R.id.calendar_select_img, "setImageResource"
                ,slist.getOrDefault(style_.getOrDefault(style_key,"Style1"),R.drawable.underline));
        views.setInt(R.id.calendar_select_img, "setColorFilter", d[week][dow][3]);
        views.setInt(R.id.calendar_select_img, "setImageAlpha", Color.alpha(d[week][dow][3]));
        //views.setInt(R.id.calendar_BG, "setColorFilter", Color.RED);
        //views.setInt(R.id.calendar_BG, "setImageAlpha", Color.alpha(yourColor));
        return DateData.getDateonCalendar(year,month,week+1,dow+1);
    }

    public static class eveData{
        final long stime;
        final long etime;
        final boolean isHoliday;
        final int color;

        eveData(long stime, long etime, boolean isHoliday, int color) {
            this.stime = stime;
            this.etime = etime;
            this.isHoliday = isHoliday;
            this.color = color;
        }
    }



}