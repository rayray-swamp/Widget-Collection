package Schedule;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.ContactsContract;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.microedition.khronos.egl.EGLConfig;

import DigitalClock.DigitalClock;
import calendar.CalendarData;
import calendar.DateData;
import calendar.NewAppWidget;
import calendar.R;
import calendar.TextBitmap;
import configure.AppWidgetConfigureActivity;
import save.ObjectStorage;
import simple_memo.Simple_Memo;
import todays_memo.WidgetSizeProvider;
import todays_memo.todays_memo;

/**
 * Implementation of App Widget functionality.
 */
public class Schedule extends AppWidgetProvider {
    public static final String CP="CP"+NewAppWidget.PARAM;
    public static final String EVENT_ID="EVENT_ID"+NewAppWidget.PARAM;
    public static final String VIEW_CALENDAR_EVENT="VIEW_CALENDAR_EVENT"+NewAppWidget.PARAM;
    public static String SCHEDULE="SCHEDULE_DATA"+ NewAppWidget.PARAM;


    public static PendingIntent getPendingSelfIntentFromCalendar(Context context, String action, long cp){
        Intent refreshIntent = new Intent(context, Schedule.class);
        refreshIntent.setAction(action);
        refreshIntent.putExtra(NewAppWidget.CALENDAR_POINT,cp);
        return PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static PendingIntent getPendingIntent(Context context,String action,long date){

        Intent alarmIntent = new Intent(context, Schedule.class);
        alarmIntent.setAction(action);
        alarmIntent.putExtra("Date",date);
        return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetIds[], long cp) {
        if (cp<0) {
            cp=System.currentTimeMillis();
        }

        List<List<String>> eves = getEve(context,DateData.getYear(cp),DateData.getMonth(cp));

        int sh=getHourOnSchedule(cp,eves);

        List<String[]> sd=getScheduledata(eves,cp,sh);

        List<String[]> ed=getFrame(sd);

        for (int appWidgetId:appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId,sh,ed,DateData.getNowDate().equals(DateData.getNowDate(cp)),cp);
        }
    }
    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId,int sh,List<String[]> tasks,boolean istoday,long cp) {
        Map<String,String> style_= ObjectStorage.get(SCHEDULE+appWidgetId, Map.class,context);

        int backcolor=Color.WHITE;
        int color_2=context.getColor(R.color.gray);
        int textcolor=Color.BLACK;
        String font=null;
        int textcolor_ev=Color.BLACK;
        String font_ev=null;
        if (style_!=null){
            backcolor=Integer.valueOf(style_.getOrDefault("Color",Color.WHITE+""));
            color_2=Integer.valueOf(style_.getOrDefault("Color2",context.getColor(R.color.gray)+""));
            textcolor=Integer.valueOf(style_.getOrDefault("DayTextColor",Color.BLACK+""));
            font=style_.getOrDefault("DayFont",null);
            textcolor_ev=Integer.valueOf(style_.getOrDefault("TextColor",Color.BLACK+""));
            font_ev=style_.getOrDefault("Font",null);
        }
        else
            style_=new HashMap<>();


        RemoteViews views= new RemoteViews(context.getPackageName(), R.layout.schedule);

        views.removeAllViews(R.id.schedule_ll);
        views.removeAllViews(R.id.schedule_all_day_ll);
        views.removeAllViews(R.id.schedule_fl);

        views.showNext(R.id.schedule_VF);
        Log.d("dp",convertDp2Px(10,context)+"");
        for (int i=sh;i<=24;i++){
            RemoteViews nv= new RemoteViews(context.getPackageName(), R.layout.schedule_line);
            nv.setImageViewBitmap(R.id.schedule_line_text,TextBitmap.textAsBitmap(context
                    ,(i%24)+"",converPxToSp(Simple_Memo.dip(context,10),context),color_2,font));
            nv.setInt(R.id.schedule_line_line, "setColorFilter", color_2);
            nv.setInt(R.id.schedule_line_line, "setImageAlpha", Color.alpha(color_2));
            views.addView(R.id.schedule_ll,nv);
        }

        views.setInt(R.id.schedule_plus, "setColorFilter", color_2);
        views.setInt(R.id.schedule_plus, "setImageAlpha", Color.alpha(color_2));

        String alldaytext=context.getString(R.string.All_day);

        if (style_.getOrDefault("Language",null)!=null){
            alldaytext= TextBitmap.getLocalizedResources(context,new Locale(style_.getOrDefault("Language",null)))
                    .getString(R.string.All_day);
        }

        views.setImageViewBitmap(R.id.schedule_all_day_text_img,TextBitmap.textAsBitmap(context
                ,alldaytext,converPxToSp(context.getResources().getDimension(R.dimen.schedule_textsize),context)
                ,color_2,font));
        views.setInt(R.id.schedule_img, "setColorFilter", backcolor);
        views.setInt(R.id.schedule_img, "setImageAlpha", Color.alpha(backcolor));

        views.setImageViewBitmap(R.id.schedule_day, TextBitmap.textAsBitmap(context
                , "" + DateData.getDateOfMonth(cp), 40, textcolor, font));
        views.setImageViewBitmap(R.id.schedule_month, TextBitmap.textAsBitmap(context
                , TextBitmap.getMonthText(
                        DateData.getYear(cp), DateData.getMonth(cp),DateData.getYear(), context
                        , null,style_.getOrDefault("Language",null)
                ), 20, textcolor, font));
        views.setImageViewBitmap(R.id.schedule_dow, TextBitmap.textAsBitmap(context
                , TextBitmap.getDowTextPE(DateData.getDateOfWeek(cp)-1, context
                        , null,style_.getOrDefault("Language",null))
                , 20, textcolor, font));

        views.setOnClickPendingIntent(R.id.schedule_day,getPendingIntent(context,CP+appWidgetId,cp));


        if (istoday){
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(Events.CONTENT_URI);
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.schedule_plus_click, configPendingIntent);
        }
        else {
            long t=DateData.getLongOnTime(DateData.getYear(cp),DateData.getMonth(cp),DateData.getDateOfMonth(cp),0,0,0);
            Log.d("dates",DateData.getNowDate(t));
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setData(Events.CONTENT_URI);
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, t);
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.schedule_plus_click, configPendingIntent);
        }

        WidgetSizeProvider widgetSizeProvider=new WidgetSizeProvider(context);
        Pair<Integer,Integer> wh=widgetSizeProvider.getWidgetsSize(appWidgetId);

        float dh=context.getResources().getDimensionPixelSize(R.dimen.dp_on_hour_of_schedule);
        float dm=dh/60f;
        int w=wh.first-dip(5,context)*2-dip(15,context);
        int textSize= (int) context.getResources().getDimensionPixelSize(R.dimen.schedule_textsize);

        if (istoday) {
            RemoteViews nv = new RemoteViews(context.getPackageName(), R.layout.schedule_now_line);
            views.addView(R.id.schedule_fl, nv);
            int nh = (int) (dm * ((DateData.getHour()-sh)*60+DateData.getMinute()));
            if (nh > 0)
                nv.setImageViewBitmap(R.id.schedule_now_line_h, Bitmap.createBitmap( 1, nh, Bitmap.Config.ARGB_8888));
        }

        int all_day_visible=View.GONE;

        for (String[] t:tasks){
            RemoteViews tv= new RemoteViews(context.getPackageName(), R.layout.schedule_task);
            int color =Integer.valueOf(t[5]);
            color = Color.argb(200,Color.red(color),Color.green(color),Color.blue(color));


            if (Boolean.valueOf(t[7])){
                all_day_visible=View.VISIBLE;
                RemoteViews av= new RemoteViews(context.getPackageName(), R.layout.schedule_allday);
                av.setInt(R.id.schedule_allday_color, "setColorFilter", color);
                av.setInt(R.id.schedule_allday_color, "setImageAlpha", Color.alpha(color));
                av.setImageViewBitmap(R.id.schedule_allday_text,TextBitmap.textAsBitmap(context,t[4]
                        ,converPxToSp(context.getResources().getDimension(R.dimen.schedule_textsize),context),textcolor_ev,font_ev));
                Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, Long.valueOf(t[6]));
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(uri);
                PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                av.setOnClickPendingIntent(R.id.schedule_allday_text,configPendingIntent);
                views.addView(R.id.schedule_all_day_ll,av);
                continue;
            }

            String timetext=(sh+Integer.valueOf(t[0])/60)+":"+(Integer.valueOf(t[0])%60)+"~ ";

            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(timetext+t[4]);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(context.getColor(R.color.darkgray)), 0, timetext.length(), 0);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(textcolor_ev), timetext.length()
                    ,(timetext+t[4]).length() , 0);
            CharSequence cs=spannableStringBuilder.subSequence(0, spannableStringBuilder.length());


            tv.setImageViewBitmap(R.id.schedule_task_img, getTextBitmap(context,cs
                    ,new Pair<>((int)(w*Float.valueOf(t[3]))-dip(1,context),(int)(dm*Integer.valueOf(t[1]))-dip(1,context))
                    ,textSize
                    ,color,font_ev));

            //tv.setOnClickPendingIntent(R.id.schedule_task_img,getPendingSelfIntentForViewCalendarEvent(context,VIEW_CALENDAR_EVENT,Long.valueOf(t[6])));

            Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, Long.valueOf(t[6]));
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(uri);
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            tv.setOnClickPendingIntent(R.id.schedule_task_img,configPendingIntent);

            int x= (int) (w*Float.valueOf(t[2]));
            if (x>0)
                tv.setImageViewBitmap(R.id.schedule_task_w, Bitmap.createBitmap(x+dip(1,context),1, Bitmap.Config.ARGB_8888));
            int y= (int) (dm*Float.valueOf(t[0]));
            if (y>0)
                tv.setImageViewBitmap(R.id.schedule_task_h, Bitmap.createBitmap(1,y+dip(1,context), Bitmap.Config.ARGB_8888));


            /*tv.setFloat(R.id.schedule_task_fl,"setX",x);
            tv.setFloat(R.id.schedule_task_fl,"setY",y);*/
            views.addView(R.id.schedule_fl,tv);

        }
        views.setViewVisibility(R.id.schedule_all_day,all_day_visible);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        updateAppWidgets(context, appWidgetManager, appWidgetIds,-1);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if (action.equals(CP)){
            Bundle extras = intent.getExtras();
            updateAppWidgets(context, AppWidgetManager.getInstance(context),
                    AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context,Schedule.class)),
                    extras.getLong(NewAppWidget.CALENDAR_POINT));
        }
        else if (action.contains(CP)){
            Bundle extras = intent.getExtras();
            long date = extras.getLong("Date",Long.MAX_VALUE);
            if (date!=Long.MAX_VALUE){
                Intent configIntent = new Intent(context, Select_day_activity.class);
                configIntent.putExtra("Date",date);
                configIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(configIntent);
            }
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        for (int id:appWidgetIds){
            ObjectStorage.clear(SCHEDULE+id,context);

        }

        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateAppWidgets(context,appWidgetManager,new int[]{appWidgetId},-1);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    public static int getHourOnSchedule(long t, List<List<String>> eves){
        String nowDate= DateData.getNowDate(t);
        boolean isToday= DateData.getNowDate().equals(nowDate);
        int sh=isToday?DateData.getHour():0;

        int year=DateData.getYear(t);
        int month=DateData.getMonth(t);
        int date=DateData.getDateOfMonth(t);

        final long fmiltime=DateData.getLongOnTime(year,month,date,0,0,0);
        final long emiltime=DateData.getLongOnTime(year,month,date,23,59,59);

        int h=24;
        for (List<String> eve:eves){
            final long stime=Long.valueOf(eve.get(CalendarData.EVENT_PROJECTION_IDX_DTSTART));
            final long etime=Long.valueOf(eve.get(CalendarData.EVENT_PROJECTION_IDX_DTEND));
            final boolean allday=eve.get(CalendarData.EVENT_PROJECTION_IDX_ALL_DAY).equals("1");
            if (allday)
                continue;
            if (fmiltime<etime&&emiltime>stime){
                if (nowDate.equals(DateData.getNowDate(stime))){
                    int nh=DateData.getHour(stime);
                    if (sh<=nh&&nh<h){
                        h=nh;
                    }
                }
                else
                    return sh;
            }
        }
        if (h==24)
            return sh;
        return h;
    }
    public static List<List<String>> getEve(Context context,int year,int month){
        List<List<String>> eves= CalendarData.getAllEventList(context,year,month);
        List<Integer> rl=new ArrayList<>();
        int i=0;
        for (List<String> events:eves){
            if (events.get(CalendarData.EVENT_PROJECTION_IDX_ORGANIZER).contains("#holiday@group.v.calendar.google.com")) {
                rl.add(i);
            }
            i++;
        }
        Collections.sort(rl);
        Collections.reverse(rl);
        for (int num:rl){
            eves.remove(num);
        }
        return eves;
    }
    //start minute , end minute , text , color , eventid ,all day
    public static List<String[]> getScheduledata(List<List<String>> eves,long t,int sh){
        List<String[]> sd=new ArrayList<>();

        int year=DateData.getYear(t);
        int month=DateData.getMonth(t);
        int date=DateData.getDateOfMonth(t);

        final long fmiltime=DateData.getLongOnTime(year,month,date,0,0,0);
        final long emiltime=DateData.getLongOnTime(year,month,date,23,59,59);
        final long smiltime=DateData.getLongOnTime(year,month,date,sh,0,0);

        for (List<String> eve:eves){
            final boolean allday=eve.get(CalendarData.EVENT_PROJECTION_IDX_ALL_DAY).equals("1");
            final long time=Long.valueOf(eve.get(CalendarData.EVENT_PROJECTION_IDX_DTSTART));
            final long stime=allday?
                    DateData.getLongOnTime(DateData.getYear(time),DateData.getMonth(time),DateData.getDateOfMonth(time),0,0,0)
                    :Long.valueOf(eve.get(CalendarData.EVENT_PROJECTION_IDX_DTSTART));
            final long etime=allday?
                    DateData.getLongOnTime(DateData.getYear(time),DateData.getMonth(time),DateData.getDateOfMonth(time),23,59,59):
                    Long.valueOf(eve.get(CalendarData.EVENT_PROJECTION_IDX_DTEND));

            if (fmiltime<etime&&emiltime>stime){
                if (stime<smiltime-1000){
                    int len=(emiltime<etime)?(24-sh)*60:(DateData.getHour(etime)-sh)*60+DateData.getMinute(etime);
                    sd.add(new String[]{String.valueOf(0)
                            ,String.valueOf(len)
                            ,eve.get(CalendarData.EVENT_PROJECTION_IDX_TITLE)
                            ,eve.get(CalendarData.EVENT_PROJECTION_IDX_DISPLAY_COLOR)
                            ,eve.get(CalendarData._ID)
                            ,String.valueOf(allday)});
                }
                else {
                    int len=(emiltime<etime)?(24-sh)*60:(DateData.getHour(etime)-sh)*60+DateData.getMinute(etime);
                    sd.add(new String[]{String.valueOf((DateData.getHour(stime)-sh)*60+DateData.getMinute(stime))
                            ,String.valueOf(len)
                            ,eve.get(CalendarData.EVENT_PROJECTION_IDX_TITLE)
                            ,eve.get(CalendarData.EVENT_PROJECTION_IDX_DISPLAY_COLOR)
                            ,eve.get(CalendarData._ID)
                            ,String.valueOf(allday)});
                }
            }
        }
        return sd;
    }
    //start minute , len minute , y(ratio) , w(ratio) , text , color , eventId , all day
    public static List<String[]> getFrame(List<String[]> sd){
        class Task{
            public Set<Task> parent = new HashSet<>();
            public Set<Task> child = new HashSet<>();
            int column=0;
            int sm=0;
            int em=0;
            int len=0;
            String text;
            int color=0;
            String eventId="";
            String allday="";

            float lp=0;
            float rp=1;
            boolean lock=false;

            List<List<Task>> getChain(List<Task> c){
                List<Task> chain=new ArrayList<>(c);
                chain.add(this);

                List<List<Task>> cs=new ArrayList<>();

                if (parent.isEmpty()){
                    cs.add(chain);
                    return cs;
                }
                for (Task t:parent){
                    cs.addAll(t.getChain(chain));
                }
                return cs;
            }
            float width(){
                return rp-lp;
            }

        }
        class OpTask{
            List<Task> convToTask(List<String[]> fd){
                List<Task> nd=new ArrayList<>();
                for (String[] d:fd){
                    Task task=new Task();
                    task.sm=Integer.valueOf(d[0]);
                    task.em=Integer.valueOf(d[1]);
                    task.len=task.em-task.sm;
                    task.text=d[2];
                    task.color=Integer.valueOf(d[3]);
                    task.eventId=d[4];
                    task.allday=d[5];
                    nd.add(task);
                }
                return nd;
            }
            List<Task> removeAllDayTask(List<Task> d){
                List<Task> rd=new ArrayList<>();
                for (Task t:d){
                    if (Boolean.valueOf(t.allday))
                        rd.add(t);
                }
                d.removeAll(rd);
                return rd;
            }
            List<Task> sortTask(List<Task> d){
                List<Integer> list=new ArrayList<>();
                for (Task t:d)
                    list.add(t.sm);
                Collections.sort(list);

                List<Task> nd=new ArrayList<>();
                for (int i:list){
                    int p=0;
                    for (Task t:d) {
                        if (t.sm==i)
                            break;
                        p++;
                    }
                    nd.add(d.get(p));
                    d.remove(p);
                }

                List<Task> cnd=new ArrayList<>(nd);
                List<Task> od=new ArrayList<>();
                List<Integer> c=new ArrayList<>();
                int nsm=0;
                for (Task t:nd){
                    if (t.sm==nsm){
                        c.add(t.len);
                    }
                    else {
                        Collections.sort(c);
                        Collections.reverse(c);
                        for (int i:c){
                            int p=0;
                            for (Task task:cnd){
                                if (task.len==i&&task.sm==nsm){
                                    break;
                                }
                                p++;
                            }
                            od.add(cnd.get(p));
                            cnd.remove(p);
                        }
                        nsm=t.sm;
                        c.clear();
                        c.add(t.len);
                    }
                }
                for (int i:c){
                    int p=0;
                    for (Task task:cnd){
                        if (task.len==i&&task.sm==nsm){
                            break;
                        }
                        p++;
                    }
                    od.add(cnd.get(p));
                    cnd.remove(p);
                }
                return od;
            }
            boolean isOverLap(List<Task> d, Task t){
                for (Task s:d){
                    if (s.column!=t.column)
                        continue;
                    if (s.sm>=t.em)
                        continue;
                    if (s.em<=t.sm)
                        continue;
                    return false;
                }
                return true;
            }
            int setColumOfTask(List<Task> d){
                if (d.isEmpty())
                    return 0;

                List<Task> c=new ArrayList<>();
                int maxcolum=0;
                for (Task t:d){
                    int colum=0;
                    do {
                        t.column=colum;
                        colum++;
                    }while (!isOverLap(c,t));
                    c.add(t);
                    if (maxcolum<colum)
                        maxcolum=colum;
                }
                return maxcolum;
            }
            void setParentAndChild(List<Task> d,int maxcolum){
                for (Task t:d){
                    for (int i=t.column+1;i<=maxcolum;i++){
                        for (Task s:d){
                            if (s.column!=i)
                                continue;
                            if (s.sm>=t.em)
                                continue;
                            if (s.em<=t.sm)
                                continue;
                            if (s==t)
                                continue;
                            t.parent.add(s);
                            s.child.add(t);
                        }
                    }
                }

                Map<Task,Set<Task>> parentdata=new HashMap<>();
                Map<Task,Set<Task>> childdata=new HashMap<>();

                for (Task t:d){
                    parentdata.put(t,new HashSet<>(t.parent));
                    childdata.put(t,new HashSet<>(t.child));
                }

                for (Task t:d){
                    for (Task s:d){
                        if (s==t)
                            continue;
                        if (childdata.get(s).contains(t)){
                            t.parent.removeAll(parentdata.get(s));
                        }
                        if (parentdata.get(s).contains(t)){
                            t.child.removeAll(childdata.get(s));
                        }
                    }
                }
            }

            List<List<Task>> getChain(List<Task> d){
                List<List<Task>> chain=new ArrayList<>();

                for (Task t:d){
                    if (t.column==0){
                        chain.addAll(t.getChain(new ArrayList<>()));
                    }
                }
                return chain;
            }
            boolean inE(float v1,float v2){
                return v1<v2+0.01&&v1>v2-0.01;
            }
            void setChain(List<List<Task>> d){
                for (List<Task> ts:d){
                    float s=ts.get(0).lp;
                    float e=ts.get(ts.size()-1).rp;
                    float w=(e-s)/ts.size();
                    for (int i=0;i<ts.size();i++){
                        Task t=ts.get(i);
                        if (t.width()>w){
                            t.lp=w*i+s;
                            t.rp=w*(i+1)+s;
                        }
                    }
                }
                for (List<Task> ts:d){
                    float s=ts.get(0).lp;
                    float e=ts.get(ts.size()-1).rp;
                    float w=(e-s)/ts.size();

                    boolean tf=true;
                    for (int i=0;i<ts.size();i++){
                        Task t=ts.get(i);
                        if (t.lock
                                ||!inE(t.lp,w*i+s)
                                ||!inE(t.rp,w*(i+1)+s)){
                            tf=false;
                            break;
                        }
                    }
                    if (tf) {
                        for (Task t : ts) {
                            t.lock = true;
                        }
                    }
                }
            }
            boolean isAllLock(List<Task> d){
                for (Task t:d){
                    if (!t.lock)
                        return false;
                }
                return true;
            }
            void InitTasks(List<Task> d){
                for (Task t:d){
                    if (!t.lock){
                        t.lp=0;
                        t.rp=1;
                    }
                }
            }
            List<List<Task>> newChain(List<List<Task>> d){
                List<List<Task>> chain=new ArrayList<>();
                for (List<Task> ts:d){
                    List<Task> nc=new ArrayList<>();
                    float p=0;
                    for (Task t:ts){
                        if (t.lock){
                            if (!nc.isEmpty()){
                                nc.get(0).lp=p;
                                nc.get(nc.size()-1).rp=t.lp;
                                chain.add(nc);
                                nc=new ArrayList<>();
                            }
                            p=t.rp;
                        }
                        else {
                            nc.add(t);
                        }
                    }
                    if (!nc.isEmpty()){
                        nc.get(0).lp=p;
                        nc.get(nc.size()-1).rp=1;
                        chain.add(nc);
                    }
                }
                return chain;
            }

            List<String[]> convToString(List<Task> d){
                List<String[]> list=new ArrayList<>();
                for (Task t:d){
                    list.add(new String[]{
                            String.valueOf(t.sm),
                            String.valueOf(t.em-t.sm),
                            String.valueOf(t.lp),
                            String.valueOf(t.rp-t.lp),
                            t.text,
                            String.valueOf(t.color),
                            t.eventId,
                            t.allday
                    });
                }
                return list;
            }
        }

        OpTask op=new OpTask();
        List<Task> t=op.convToTask(sd);
        List<Task> rt=op.removeAllDayTask(t);
        t=op.sortTask(t);
        int maxcolum=op.setColumOfTask(t);
        op.setParentAndChild(t,maxcolum);

        List<List<Task>> chain=op.getChain(t);
        int safety=0;
        do {
            op.setChain(chain);
            op.InitTasks(t);
            chain=op.newChain(chain);
            safety++;
        }while ((!chain.isEmpty())&&safety<100);

        t.addAll(rt);
        return op.convToString(t);
    }
    public static int dip(int value,Context context){
        return (int) (value * context.getResources().getDisplayMetrics().density);
    }

    public static Bitmap getTextBitmap(Context context,CharSequence text,Pair<Integer,Integer> pair,int maxTextsize,int color,String font){
        //Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Inspiration-Regular");
        TextView textView=new TextView(context);
        if (font!=null){
            Typeface font_ = Typeface.createFromAsset(context.getAssets(), String.format("fonts/%s",font));
            textView.setTypeface(font_);
        }
        textView.setPadding(dip(5,context),dip(5,context),dip(5,context),dip(5,context));
        textView.layout(0, 0, (int) (pair.first), (int) (pair.second)); //text box size 300px x 500px
        textView.setWidth((int) (pair.first));
        textView.setHeight((int) (pair.second));
        //textView.setTypeface(tf);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        for (;maxTextsize>=1;maxTextsize--){
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,maxTextsize);
            if (Simple_Memo.isFit(textView))
                break;
        }

        textView.setDrawingCacheEnabled(true);
        textView.setBackgroundResource(R.drawable.frame_style2);
        textView.setBackgroundTintList(
                ColorStateList.valueOf(color)
        );
        return Simple_Memo.getBitmapFromView(textView);

    }
    public static float convertDp2Px(float dp, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * metrics.density;
    }
    public static float converPxToSp(float px, Context context) {
        return px / context.getResources().getDisplayMetrics().scaledDensity;
    }

}


