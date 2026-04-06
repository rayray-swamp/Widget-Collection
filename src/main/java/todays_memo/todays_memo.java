package todays_memo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Schedule.Select_day_activity;
import calendar.DateData;
import calendar.NewAppWidget;
import calendar.R;
import calendar.TextBitmap;
import save.ObjectStorage;
import simple_memo.Simple_Memo;
import simple_memo.Simple_Memo_activity;

import static todays_memo.TodayMemoRemoteViewsFactory.LAYOUT_W;
import static todays_memo.TodayMemoRemoteViewsFactory.LIST_ID;
import static todays_memo.TodayMemoRemoteViewsFactory.LIST_TOUCH;
import static todays_memo.TodayMemoRemoteViewsFactory.LIST_TOUCH_TEXT_ID;

/**
 * Implementation of App Widget functionality.
 */
public class todays_memo extends AppWidgetProvider {
    public static final String MEMO="MEMO"+NewAppWidget.PARAM;
    public static final String CP="CP"+NewAppWidget.PARAM;
    public static final String TODAYS_MEMO="TODAYS_MEMO"+NewAppWidget.PARAM;
    public static final String TODAYS_MEMO_DATA="TODAYS_MEMO_DATA"+NewAppWidget.PARAM;
    public static final String MEMO_LIST="MEMO_LIST"+NewAppWidget.PARAM;
    public static final Map<Integer,Boolean> listMap=new HashMap<>();



    public static PendingIntent getPendingSelfIntentFromCalendar(Context context, String action, long cp){
        Intent refreshIntent = new Intent(context, todays_memo.class);
        refreshIntent.setAction(action);
        refreshIntent.putExtra(NewAppWidget.CALENDAR_POINT,cp);
        return PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
    public static PendingIntent getPendingSelfIntent(Context context, String action, long cp,int Id){
        Intent refreshIntent = new Intent(context, todays_memo.class);
        refreshIntent.setAction(action+Id);
        refreshIntent.putExtra(NewAppWidget.CALENDAR_POINT,cp);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, Id);
        return PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
    private static PendingIntent getPendingIntent(Context context,String action,long date){

        Intent alarmIntent = new Intent(context, todays_memo.class);
        alarmIntent.setAction(action);
        alarmIntent.putExtra("Date",date);
        return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, long cp){
        updateAppWidget(context, appWidgetManager, appWidgetId, cp,Pair.create(false,false));
    }
    //listtouch 1:no list update 2:no viwflipper uodate
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,int appWidgetId,long cp,Pair<Boolean,Boolean> listtouch) {
        // Construct the RemoteViews object

        Map<String,String> style_= ObjectStorage.get(TODAYS_MEMO_DATA+appWidgetId, Map.class,context);

        int backcolor=Color.WHITE;
        int color_2=context.getColor(R.color.gray);
        int textcolor=Color.BLACK;
        String font=null;
        int textcolor_ev=Color.BLACK;
        int stextcolor_ev=context.getColor(R.color.gray);
        String font_ev=null;
        if (style_!=null){
            backcolor=Integer.valueOf(style_.getOrDefault("Color",Color.WHITE+""));
            color_2=Integer.valueOf(style_.getOrDefault("Color2",context.getColor(R.color.gray)+""));
            textcolor=Integer.valueOf(style_.getOrDefault("DayTextColor",Color.BLACK+""));
            font=style_.getOrDefault("DayFont",null);
            textcolor_ev=Integer.valueOf(style_.getOrDefault("TextColor",Color.BLACK+""));
            stextcolor_ev=Integer.valueOf(style_.getOrDefault("SelectedTextColor",context.getColor(R.color.gray)+""));
            font_ev=style_.getOrDefault("Font",null);
        }
        else
            style_=new HashMap<>();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.todays_memo);
        views.setOnClickPendingIntent(R.id.today_memo_img,null);
        views.setInt(R.id.today_memo_img, "setColorFilter", backcolor);
        views.setInt(R.id.today_memo_img, "setImageAlpha", Color.alpha(backcolor));


        if (listMap.getOrDefault(appWidgetId,false)) {
            int color = context.getColor(R.color.blue);
            views.setInt(R.id.today_memo_list, "setColorFilter", color);
            views.setInt(R.id.today_memo_list, "setImageAlpha", Color.alpha(color));
            views.setViewVisibility(R.id.today_memo_view, View.GONE);
            views.setViewVisibility(R.id.today_memo_listview, View.VISIBLE);
        }
        else {
            int color = color_2;
            views.setInt(R.id.today_memo_list, "setColorFilter", color);
            views.setInt(R.id.today_memo_list, "setImageAlpha", Color.alpha(color));
            views.setViewVisibility(R.id.today_memo_view, View.VISIBLE);
            views.setViewVisibility(R.id.today_memo_listview, View.GONE);
            listtouch=Pair.create(true,listtouch.second);
        }
        if (!listtouch.second)
            views.showNext(R.id.today_memo_VF);


        Map<String,Map<String,Boolean>> textmap= ObjectStorage.get(todays_memo.MEMO_LIST, Map.class,context);
        if (textmap==null)
            textmap=new HashMap<>();
        Map<String,Boolean> tmap=textmap.getOrDefault(DateData.getNowDate(cp),new HashMap<>());



        if (cp<0) {
            cp=System.currentTimeMillis();
        }

        ObjectStorage.save(cp,TODAYS_MEMO,context);

        views.setImageViewBitmap(R.id.today_memo_day, TextBitmap.textAsBitmap(context
                , "" + DateData.getDateOfMonth(cp), 40, textcolor, font));
        views.setImageViewBitmap(R.id.today_memo_month, TextBitmap.textAsBitmap(context
                , TextBitmap.getMonthText(
                        DateData.getYear(cp), DateData.getMonth(cp),DateData.getYear(), context
                        , null,style_.getOrDefault("Language",null)
                ), 20, textcolor, font));
        views.setImageViewBitmap(R.id.today_memo_dow, TextBitmap.textAsBitmap(context
                , TextBitmap.getDowTextPE(DateData.getDateOfWeek(cp)-1, context
                        , null,style_.getOrDefault("Language",null))
                , 20, textcolor, font));


        WidgetSizeProvider widgetSizeProvider=new WidgetSizeProvider(context);
        Pair<Integer,Integer> pair =widgetSizeProvider.getWidgetsSize(appWidgetId,5);
        int w=pair.first;
        int h=pair.second - (int) context.getResources().getDimensionPixelSize(R.dimen.today_memo_day)
                -widgetSizeProvider.dip(10);
        int textSize= (int) context.getResources().getDimensionPixelSize(R.dimen.today_memo_textsize);

        List<String[]> list=TodayMemoRemoteViewsFactory.setmList(context);

        views.setImageViewBitmap(R.id.today_memo_view,Simple_Memo.getTextBitmap(context,
                getHtmlText(list,textcolor_ev,stextcolor_ev)
                ,w,h,textSize,font_ev));

        views.setOnClickPendingIntent(R.id.today_memo_day,getPendingIntent(context,CP+appWidgetId,cp));


        Intent configIntent = new Intent(context, Memo_Activity.class);
        configIntent.setAction(""+appWidgetId);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
        views.setOnClickPendingIntent(R.id.today_memo_view,configPendingIntent);
        views.setOnClickPendingIntent(R.id.today_memo_list_click,getPendingSelfIntent(context,MEMO_LIST,cp,appWidgetId));

        if (!listtouch.first) {
            Intent intent = new Intent(context, TodayMemoRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            views.setRemoteAdapter(R.id.today_memo_listview, intent);

            Intent clickIntentTemplate = new Intent(context, todays_memo.class);
            PendingIntent clickPendingIntentTemplate = PendingIntent.getBroadcast(context,
                    0, clickIntentTemplate, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.today_memo_listview, clickPendingIntentTemplate);

            ObjectStorage.save(w, LAYOUT_W, context);
            ObjectStorage.save(appWidgetId, LIST_ID, context);
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
        if (!listtouch.first)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.today_memo_listview);

    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        listMap.clear();
        for (int appWidgetId:appWidgetIds)
            updateAppWidget(context, appWidgetManager, appWidgetId,-1);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action=intent.getAction();
        if (action.equals(MEMO)){
            onUpdate(context,AppWidgetManager.getInstance(context),AppWidgetManager.getInstance(context).getAppWidgetIds(
                    new ComponentName(context,todays_memo.class)
            ));
        }
        else if (action.equals(CP)){
            Bundle extras = intent.getExtras();
            listMap.clear();
            for (int appwidgetId:AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context,todays_memo.class)))
                updateAppWidget(context, AppWidgetManager.getInstance(context), appwidgetId,extras.getLong(NewAppWidget.CALENDAR_POINT));
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
        else if (action.contains(MEMO_LIST)){
            Bundle extras = intent.getExtras();
            int id=extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
            listMap.put(id,!listMap.getOrDefault(id,false));
            ObjectStorage.save("",LIST_TOUCH_TEXT_ID,context);
            updateAppWidget(context, AppWidgetManager.getInstance(context), id,extras.getLong(NewAppWidget.CALENDAR_POINT));
        }
        else if (action.contains(LIST_TOUCH)){
            Bundle extras = intent.getExtras();
            int id=extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
            String key=extras.getString("KEY");
            String text=extras.getString("TEXT");
            Map<String,Map<String,Boolean>> textmap= ObjectStorage.get(todays_memo.MEMO_LIST, Map.class,context);
            if (textmap==null)
                textmap=new HashMap<String,Map<String,Boolean>>();
            Map<String,Boolean> map=textmap.getOrDefault(key,new HashMap<>());
            map.put(text,!map.getOrDefault(text,false));
            textmap.put(key,map);
            ObjectStorage.save(textmap,todays_memo.MEMO_LIST,context);
            ObjectStorage.save(text+key,LIST_TOUCH_TEXT_ID,context);

            listMap.clear();
            listMap.put(id,true);

            final Long t= ObjectStorage.get(todays_memo.TODAYS_MEMO,Long.class,context);
            final long cp=(t==null? System.currentTimeMillis():t);
            for (int appwidgetId:AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context,todays_memo.class)))
                updateAppWidget(context, AppWidgetManager.getInstance(context), appwidgetId,cp,Pair.create(appwidgetId!=id,appwidgetId==id));
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        final Long t=ObjectStorage.get(todays_memo.TODAYS_MEMO,Long.class,context);
        updateAppWidget(context,appWidgetManager,appWidgetId,t==null?-1:t);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        for (int id:appWidgetIds){
            ObjectStorage.clear(TODAYS_MEMO_DATA+id,context);

        }

        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    public static String getSplitText(String text, Map<String,Boolean> map){
        //String Color = String.format("#%06X", (0xFFFFFF & color));
        if (map==null)
            map=new HashMap<>();
        String[] ts = text.split("\n");
        String newtext="";
        String ctext="";
        for (String t:ts){
            if (!t.equals("")){
                if (map.getOrDefault(t,false)){
                    if (!ctext.equals(""))
                        ctext+="\n";
                    ctext+=t;
                    continue;
                }
            }
            if (!newtext.equals(""))
                newtext+="\n";
            newtext+=t;
        }
        if (newtext.equals(""))
            return ctext;
        return newtext+"\n"+ctext;
    }
    public static CharSequence getHtmlText(List<String[]> list, int ncolor, int ccolor){

        String ntext="";
        String ctext="";
        for (String[] s:list){
            if (Boolean.valueOf(s[1])){
                if (Boolean.valueOf(s[3])){
                    continue;
                }
                else if (!ctext.equals(""))
                    ctext+="\n";
                ctext+=s[0];
            }
            else {
                if (!ntext.equals(""))
                    ntext+="\n";
                ntext+=s[0];
            }
        }

        String text=ntext.equals("")?ctext:ctext.equals("")?ntext:ntext+"\n"+ctext;

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(ncolor), 0, ntext.length(), 0);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(ccolor), ntext.length(), text.length(), 0);
        return spannableStringBuilder.subSequence(0, spannableStringBuilder.length());
    }



}