package AnalogClock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.*;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.StaticLayout;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.CDATASection;

import DigitalClock.DigitalClock;
import Schedule.Schedule;
import calendar.DateData;
import calendar.NewAppWidget;
import calendar.R;
import calendar.TextBitmap;
import configure.AppWidgetConfigureActivity;
import save.ObjectStorage;
import simple_memo.Simple_Memo;
import todays_memo.WidgetSizeProvider;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
import static configure.AppWidgetConfigureActivity.inflate;


public class AnalogClock extends AppWidgetProvider {
    private static final String ACTION_KAIROS_INTERVAL = "INTERVAL"+ NewAppWidget.PARAM;
    public static final String ANALOG_CLOCK_DATA = "ANALOG_CLOCK_DATA"+ NewAppWidget.PARAM;
    private static final String TOUCH = "ANALOG_TOUCH"+ NewAppWidget.PARAM;
    private static final long INTERVAL = 5 * 1000;
    private final static long[] vz=new long[]{0};

    //----------------------------------------------------------
    private static PendingIntent getAlermPendingIntent(Context context,String action){
        Intent alarmIntent = new Intent(context, AnalogClock.class);

        alarmIntent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    //----------------------------------------------------------
    private static void setInterval(Context context, long interval) {
        PendingIntent operation = getAlermPendingIntent(context,ACTION_KAIROS_INTERVAL);

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        long now          = System.currentTimeMillis();
        long oneHourAfter = ((long)(now / interval)) * interval + interval;


        am.setExact(AlarmManager.RTC, oneHourAfter, operation);

    }
    private static void setInterval(Context context, long interval ,String action) {
        PendingIntent operation = getAlermPendingIntent(context,action);

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        long now          = System.currentTimeMillis();
        long oneHourAfter = ((long)(now / interval)) * interval + interval;


        am.set(AlarmManager.RTC, oneHourAfter, operation);

    }
    private static void setIntervalNotNomal(Context context, long interval) {
        PendingIntent operation = getAlermPendingIntent(context,ACTION_KAIROS_INTERVAL);

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        long now          = System.currentTimeMillis();
        long oneHourAfter = now+ interval;


        am.setExact(AlarmManager.RTC, oneHourAfter, operation);

    }


    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                         int appWidgetIds[]) {
        Map<Integer,Map<String,String>> datas=new HashMap<>();
        String color_key="Color";
        String bcolor_key="backColor";
        String hcolor_key="Colorh";
        String mcolor_key="Colorm";
        String scolor_key="Colors";
        String language_key="Language";
        String style_key="style";
        String text_color_key="TextColor";
        String font_key="Font";


        for (int appWidgetId:appWidgetIds){
            Map<String,String> data=new HashMap<>();
            data.put(bcolor_key,String.valueOf(Color.WHITE));
            data.put(color_key,String.valueOf(Color.BLACK));
            data.put(hcolor_key,String.valueOf(Color.BLACK));
            data.put(mcolor_key,String.valueOf(Color.BLACK));
            data.put(scolor_key,String.valueOf(Color.BLACK));
            data.put(text_color_key,String.valueOf(Color.BLACK));
            data.put(font_key,null);
            data.put(language_key,null);
            data.put(style_key,String.valueOf(0));
            Map<String,String> style= ObjectStorage.get(AnalogClock.ANALOG_CLOCK_DATA +appWidgetId, Map.class,context);
            if (style!=null){
                data=style;
            }
            datas.put(appWidgetId,data);
        }



        Map<String,Integer>slist=new LinkedHashMap<>();

        slist.put("Style1",0);
        slist.put("Style2",R.layout.analog_clock_num);
        slist.put("Style3",R.layout.analog_clock_num2);
        slist.put("Style4",R.layout.analog_clock_bar);
        slist.put("Style5",R.layout.analog_clock_bar2);


        setIntervalNotNomal(context, INTERVAL);
        RemoteViews	views = new RemoteViews(context.getPackageName(), R.layout.analog_clock);
        final long now=System.currentTimeMillis();
        final int now_second= (int) (now%(60*1000))/1000;
        Log.d("second",now_second+","+((now/1000)-(vz[0]/1000)));
        long g=((now/1000)-(vz[0]/1000));
        if (g<5)
            return;
        if (g!=5)
            views.showNext(R.id.analog_clock_s_vf_z);
        vz[0]=now;
        views.showNext(R.id.analog_clock_s_vf_60);
        float second_rotate=0;


        if (now_second>1){
            second_rotate=now_second*6;
        }
        float next_second_rotate=((now_second+INTERVAL/1000)%60)*6;

        int main=(now_second%2)==0?R.id.analog_clock_s1:R.id.analog_clock_s2;
        int sub=(now_second%2)==1?R.id.analog_clock_s1:R.id.analog_clock_s2;

        views.setViewVisibility(main, View.VISIBLE);
        views.setViewVisibility(sub, View.GONE);
        views.setViewVisibility(R.id.analog_clock_back_img2,View.GONE);



        appWidgetManager.updateAppWidget(appWidgetIds, views);
        Bitmap bitmap;



        for (int appWidgetId:appWidgetIds){
            RemoteViews	view = new RemoteViews(context.getPackageName(), R.layout.analog_clock);
            WidgetSizeProvider widgetSizeProvider=new WidgetSizeProvider(context);
            Pair<Integer,Integer> wh=widgetSizeProvider.getWidgetsSize(appWidgetId);
            int diameter=Math.min(wh.first,wh.second)-dip(20,context);
            if (diameter<=100)
                diameter= (int) (Math.min(wh.first,wh.second)*0.8);
            int second_color=Integer.valueOf(datas.get(appWidgetId).get(scolor_key));
            bitmap=rotatedRV(context,second_color,new Pair<>(diameter,diameter),second_rotate
                    ,diameter>150?R.drawable.second_hand:R.drawable.second_hand2);
            view.setImageViewBitmap(main,bitmap);
            appWidgetManager.updateAppWidget(appWidgetId, view);
        }
        for (int appWidgetId:appWidgetIds){
            RemoteViews	view = new RemoteViews(context.getPackageName(), R.layout.analog_clock);
            WidgetSizeProvider widgetSizeProvider=new WidgetSizeProvider(context);
            Pair<Integer,Integer> wh=widgetSizeProvider.getWidgetsSize(appWidgetId);
            int diameter=Math.min(wh.first,wh.second)-dip(20,context);
            if (diameter<=100)
                diameter= (int) (Math.min(wh.first,wh.second)*0.8);
            int second_color=Integer.valueOf(datas.get(appWidgetId).get(scolor_key));
            bitmap=rotatedRV(context,second_color,new Pair<>(diameter,diameter),next_second_rotate
                    ,diameter>150?R.drawable.second_hand:R.drawable.second_hand2);
            view.setImageViewBitmap(sub,bitmap);
            appWidgetManager.updateAppWidget(appWidgetId, view);
        }


        for (int appWidgetId:appWidgetIds){
            Map<String,String> data=datas.get(appWidgetId);
            RemoteViews	view = new RemoteViews(context.getPackageName(), R.layout.analog_clock);
            //view.setImageViewBitmap(R.id.analog_clock_s,getImageBitmap(context,new Pair<>(diameter,diameter),second_color,R.drawable.second_hand));
            WidgetSizeProvider widgetSizeProvider=new WidgetSizeProvider(context);
            Pair<Integer,Integer> wh=widgetSizeProvider.getWidgetsSize(appWidgetId);
            int diameter=Math.min(wh.first,wh.second)-dip(20,context);
            if (diameter<=100)
                diameter= (int) (Math.min(wh.first,wh.second)*0.8);
            int minute_color=Integer.valueOf(data.get(mcolor_key));
            int hour_color=Integer.valueOf(data.get(hcolor_key));
            int back_color=Integer.valueOf(data.get(bcolor_key));
            bitmap=getImageBitmap(context,new Pair<>(Math.min(wh.first,wh.second)
                    ,Math.min(wh.first,wh.second)),back_color,R.drawable.frame_style);
            Canvas canvas=new Canvas(bitmap);

            getMark(context,canvas,Integer.valueOf(data.get(color_key)),slist.getOrDefault(data.get(style_key),0)
                    ,data.get(font_key));

            Bitmap bitmap1=rotatedRV(context,hour_color,new Pair<>(diameter,diameter),DateData.getHour(now)*30+DateData.getMinute(now)/2,R.drawable.hour_hand);
            canvas.drawBitmap(bitmap1,(bitmap.getWidth()-bitmap1.getWidth())/2
                    ,(bitmap.getHeight()-bitmap1.getHeight())/2,null);
            bitmap1=rotatedRV(context,minute_color,new Pair<>(diameter,diameter),DateData.getMinute(now)*6,R.drawable.minute_hand);
            canvas.drawBitmap(bitmap1,(bitmap.getWidth()-bitmap1.getWidth())/2
                    ,(bitmap.getHeight()-bitmap1.getHeight())/2,null);
            view.setImageViewBitmap(R.id.analog_clock_back_img,bitmap);

            Intent refreshIntent = new Intent(context, AnalogClock.class);
            refreshIntent.setAction(TOUCH+appWidgetId);
            refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            view.setOnClickPendingIntent(R.id.analog_clock_back_img
                    ,PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_CANCEL_CURRENT));

            appWidgetManager.updateAppWidget(appWidgetId, view);
        }


    }
    private static void onClick(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId){
        String color_key="Color";
        String bcolor_key="backColor";
        String hcolor_key="Colorh";
        String mcolor_key="Colorm";
        String scolor_key="Colors";
        String language_key="Language";
        String style_key="style";
        String text_color_key="TextColor";
        String font_key="Font";


        Map<String,String> data=new HashMap<>();
        data.put(bcolor_key,String.valueOf(Color.WHITE));
        data.put(color_key,String.valueOf(Color.BLACK));
        data.put(hcolor_key,String.valueOf(Color.BLACK));
        data.put(mcolor_key,String.valueOf(Color.BLACK));
        data.put(scolor_key,String.valueOf(Color.BLACK));
        data.put(text_color_key,String.valueOf(Color.BLACK));
        data.put(font_key,null);
        data.put(language_key,null);
        data.put(style_key,String.valueOf(0));
        Map<String,String> style= ObjectStorage.get(AnalogClock.ANALOG_CLOCK_DATA +appWidgetId, Map.class,context);
        if (style!=null){
            data=style;
        }

        RemoteViews	view = new RemoteViews(context.getPackageName(), R.layout.analog_clock);
        WidgetSizeProvider widgetSizeProvider=new WidgetSizeProvider(context);
        Pair<Integer,Integer> wh=widgetSizeProvider.getWidgetsSize(appWidgetId);
        int diameter=Math.min(wh.first,wh.second);
        int back_color=Integer.valueOf(data.get(bcolor_key));
        int dp10=dip(10,context);
        int dp20=dip(20,context);
        if (diameter-dp20<1)
            return;
        setIntervalNotNomal(context,6*1000);
        view.setViewVisibility(R.id.analog_clock_s1,View.GONE);
        view.setViewVisibility(R.id.analog_clock_s2,View.GONE);
        view.setViewVisibility(R.id.analog_clock_back_img2,View.VISIBLE);
        view.showNext(R.id.analog_clock_vf);
        Bitmap bitmap=getImageBitmap(context,new Pair<>(Math.min(wh.first,wh.second)
                ,Math.min(wh.first,wh.second)),back_color,R.drawable.frame_style);
        Bitmap bitmap_vf=getImageBitmap(context,new Pair<>(Math.min(wh.first,wh.second)
                ,Math.min(wh.first,wh.second)),back_color,R.drawable.frame_style);
        Bitmap bitmap1=getTextBitmap(context,DateData.getNowTime(),new Pair<>(diameter-dp20,diameter/2),200,1,data.get(font_key));
        Bitmap bitmap2=getTextBitmap(context
                , TextBitmap.getDowTextPE(DateData.getDateOfWeek()-1,context,data.get(font_key)
                        ,data.get(language_key))+", "+DateData.getDateOfMonth()
                ,new Pair<>(diameter-dp20,diameter/2),2000,1,data.get(font_key));

        Paint paint=new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(Integer.valueOf(data.get(text_color_key)), PorterDuff.Mode.SRC_IN));
        paint.setAlpha(Color.alpha(Integer.valueOf(data.get(text_color_key))));

        Canvas canvas=new Canvas(bitmap_vf);
        canvas.drawBitmap(bitmap1,dp10,diameter/2,paint);
        canvas.drawBitmap(bitmap2,dp10,0,paint);
        view.setImageViewBitmap(R.id.analog_clock_back_img,bitmap);
        view.setImageViewBitmap(R.id.analog_clock_back_img2,bitmap_vf);
        appWidgetManager.updateAppWidget(appWidgetId,view);
    }

    

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int ids []){
        super.onUpdate(context, manager, ids);
        setInterval(context,6000);
        updateAppWidgets(context, AppWidgetManager.getInstance(context),
                AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, AnalogClock.class)));


        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenStateFilter.addAction(Intent.ACTION_USER_PRESENT);
        context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                AnalogClock.this.onReceive(context, intent);
            }
        }, screenStateFilter);
    }


    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        updateAppWidgets(context,appWidgetManager,new int[]{appWidgetId});
    }

    @Override
    public void onReceive(Context context, Intent intent){
        Log.d("action",intent.getAction());
        if(Intent.ACTION_USER_PRESENT.equals(intent.getAction())){
            updateAppWidgets(context, AppWidgetManager.getInstance(context),
                    AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, AnalogClock.class)));
        }
        else if(ACTION_KAIROS_INTERVAL.equals(intent.getAction())){
            updateAppWidgets(context, AppWidgetManager.getInstance(context),
                    AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, AnalogClock.class)));
        }
        else if(intent.getAction().contains(TOUCH)){
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,-1);
                if (appWidgetId!=-1) {
                    onClick(context, AppWidgetManager.getInstance(context),appWidgetId);
                }
            }

        }

        super.onReceive(context, intent);
    }

    public static void getMark(Context context,Canvas canvas,int color,int layout,String font){
        //Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Inspiration-Regular");
        if (layout==0)
            return;
        int w=canvas.getWidth();
        int h=canvas.getHeight();

        FrameLayout frameLayout=new FrameLayout(context);

        View la= AppWidgetConfigureActivity.inflate(context,layout,frameLayout);


        if (layout==R.layout.analog_clock_num){
            ((ImageView)la.findViewById(R.id.analog_clock_img)).setImageBitmap(Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888));
            int pair=Math.min(w,h)/6;
            pair= (int) DigitalClock.converPxToSp(context,pair);
            int ids[]=new int[]{R.id.analog_clock_h1,R.id.analog_clock_h2,R.id.analog_clock_h3,R.id.analog_clock_h4
                    ,R.id.analog_clock_h5,R.id.analog_clock_h6,R.id.analog_clock_h7,R.id.analog_clock_h8
                    ,R.id.analog_clock_h9,R.id.analog_clock_h10,R.id.analog_clock_h11,R.id.analog_clock_h12};

            int top=10000;
            int bottom = 0;
            int left=10000;
            int right=0;

            for (int i=1;i<=12;i++){
                int[] d=TextBitmap.getTrimInt(TextBitmap.textAsBitmapNoTrim(context,""+i,pair,Color.BLACK,font));
                if (left>d[0])
                    left=d[0];
                if (right<d[1])
                    right=d[1];
                if (top>d[2])
                    top=d[2];
                if (bottom<d[3])
                    bottom=d[3];
            }
            for (int i=1;i<=12;i++){
                Bitmap bitmap;

                bitmap=TextBitmap.textAsBitmapNoTrim(context,""+i,pair,color,font);
                bitmap=TextBitmap.TrimBitmapLock(bitmap,top,bottom);
                Bitmap bitmap1=Bitmap.createBitmap(right-left,bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas1=new Canvas(bitmap1);
                canvas1.drawBitmap(bitmap,(bitmap1.getWidth()-bitmap.getWidth())/2,0,null);

                ((ImageView)la.findViewById(ids[i-1])).setImageBitmap(bitmap1);
            }

        }
        else if (layout==R.layout.analog_clock_num2){
            ((ImageView)la.findViewById(R.id.analog_clock_img)).setImageBitmap(Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888));
            int pair=Math.min(w,h)/4;
            pair= (int) DigitalClock.converPxToSp(context,pair);
            int ids[]=new int[]{R.id.analog_clock_h3,R.id.analog_clock_h6
                    ,R.id.analog_clock_h9,R.id.analog_clock_h12};

            int top=10000;
            int bottom = 0;
            int left=10000;
            int right=0;

            for (int i=3;i<=12;i+=3){
                int[] d=TextBitmap.getTrimInt(TextBitmap.textAsBitmapNoTrim(context,""+i,pair,Color.BLACK,font));
                if (left>d[0])
                    left=d[0];
                if (right<d[1])
                    right=d[1];
                if (top>d[2])
                    top=d[2];
                if (bottom<d[3])
                    bottom=d[3];
            }
            for (int i=3;i<=12;i+=3){
                Bitmap bitmap;

                bitmap=TextBitmap.textAsBitmapNoTrim(context,""+i,pair,color,font);
                bitmap=TextBitmap.TrimBitmapLock(bitmap,top,bottom);
                Bitmap bitmap1=Bitmap.createBitmap(right-left,bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas1=new Canvas(bitmap1);
                canvas1.drawBitmap(bitmap,(bitmap1.getWidth()-bitmap.getWidth())/2,0,null);

                ((ImageView)la.findViewById(ids[i/3-1])).setImageBitmap(bitmap1);
            }

        }
        else if (layout==R.layout.analog_clock_bar){
            ((ImageView)la.findViewById(R.id.analog_clock_img)).setImageBitmap(Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888));
            int pair=Math.min(w,h)/6;
            pair= (int) DigitalClock.converPxToSp(context,pair);
            int ids[]=new int[]{R.id.analog_clock_h1,R.id.analog_clock_h2,R.id.analog_clock_h3,R.id.analog_clock_h4
                    ,R.id.analog_clock_h5,R.id.analog_clock_h6,R.id.analog_clock_h7,R.id.analog_clock_h8
                    ,R.id.analog_clock_h9,R.id.analog_clock_h10,R.id.analog_clock_h11,R.id.analog_clock_h12};

            float rad= (float) 30;

            float ro[]=new float[]{90+rad,180+-rad,180,180+rad
                    ,270-rad,270,270+rad,-rad
                    ,0,rad,90-rad,90};
            int top=10000;
            int bottom = 0;
            int left=10000;
            int right=0;

            for (int i=1;i<=1;i++){
                int[] d=TextBitmap.getTrimInt(TextBitmap.textAsBitmapNoTrim(context,"-",pair,Color.BLACK,font));
                if (left>d[0])
                    left=d[0];
                if (right<d[1])
                    right=d[1];
                if (top>d[2])
                    top=d[2];
                if (bottom<d[3])
                    bottom=d[3];
            }

            Bitmap bitmap;

            bitmap=TextBitmap.textAsBitmapNoTrim(context,"-",pair,color,font);
            bitmap=TextBitmap.TrimBitmapLock(bitmap,top,bottom);
            Bitmap bitmap1=Bitmap.createBitmap(right-left,bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas1=new Canvas(bitmap1);
            canvas1.drawBitmap(bitmap,(bitmap1.getWidth()-bitmap.getWidth())/2,0,null);

            for (int i=1;i<=12;i++){


                Matrix matrix = new Matrix();

                float px=bitmap1.getWidth()/2;
                float py=bitmap1.getHeight()/2;

                matrix.setRotate(ro[i-1], px, py);
                Bitmap bitmap2=Bitmap.createBitmap(bitmap1, 0, 0,
                        bitmap1.getWidth(), bitmap1.getHeight(), matrix, true);

                int mh= (int) (bitmap1.getWidth()+Math.sin(rad/360*2*Math.PI)*(bottom-top));

                if (i==1||i==11){
                    Bitmap bitmap3=Bitmap.createBitmap(bitmap2.getWidth(),mh, Bitmap.Config.ARGB_8888);
                    Canvas canvas2=new Canvas(bitmap3);
                    canvas2.drawBitmap(bitmap2,0,mh-bitmap2.getHeight(),null);
                    bitmap2=bitmap3;
                }
                else if (i==2||i==4){
                    Bitmap bitmap3=Bitmap.createBitmap(mh,bitmap2.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas2=new Canvas(bitmap3);
                    canvas2.drawBitmap(bitmap2,0,0,null);
                    bitmap2=bitmap3;
                }
                else if (i==5||i==7){
                    Bitmap bitmap3=Bitmap.createBitmap(bitmap2.getWidth(),mh, Bitmap.Config.ARGB_8888);
                    Canvas canvas2=new Canvas(bitmap3);
                    canvas2.drawBitmap(bitmap2,0,0,null);
                    bitmap2=bitmap3;
                }
                else if (i==8||i==10){
                    Bitmap bitmap3=Bitmap.createBitmap(mh,bitmap2.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas2=new Canvas(bitmap3);
                    canvas2.drawBitmap(bitmap2,mh-bitmap2.getWidth(),0,null);
                    bitmap2=bitmap3;
                }


                ((ImageView)la.findViewById(ids[i-1])).setImageBitmap(bitmap2);
            }

        }
        else if (layout==R.layout.analog_clock_bar2){
            ((ImageView)la.findViewById(R.id.analog_clock_img)).setImageBitmap(Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888));
            int pair=Math.min(w,h)/5;
            pair= (int) DigitalClock.converPxToSp(context,pair);
            int ids[]=new int[]{R.id.analog_clock_h1,R.id.analog_clock_h2,R.id.analog_clock_h3,R.id.analog_clock_h4
                    ,R.id.analog_clock_h5,R.id.analog_clock_h6,R.id.analog_clock_h7,R.id.analog_clock_h8
                    ,R.id.analog_clock_h9,R.id.analog_clock_h10,R.id.analog_clock_h11,R.id.analog_clock_h12};

            float rad= (float) 26.794;

            float ro[]=new float[]{90+rad,180+-rad,180,180+rad
                    ,270-rad,270,270+rad,-rad
                    ,0,rad,90-rad,90};
            int top=10000;
            int bottom = 0;
            int left=10000;
            int right=0;

            for (int i=1;i<=1;i++){
                int[] d=TextBitmap.getTrimInt(TextBitmap.textAsBitmapNoTrim(context,"|",pair,Color.BLACK,font));
                if (left>d[0])
                    left=d[0];
                if (right<d[1])
                    right=d[1];
                if (top>d[2])
                    top=d[2];
                if (bottom<d[3])
                    bottom=d[3];
            }

            Bitmap bitmap;

            bitmap=TextBitmap.textAsBitmapNoTrim(context,"|",pair,color,font);
            bitmap=TextBitmap.TrimBitmapLock(bitmap,top,bottom);
            Bitmap bitmap1=Bitmap.createBitmap(right-left,bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas1=new Canvas(bitmap1);
            canvas1.drawBitmap(bitmap,(bitmap1.getWidth()-bitmap.getWidth())/2,0,null);

            for (int i=1;i<=12;i++){


                Matrix matrix = new Matrix();

                float px=bitmap1.getWidth()/2;
                float py=bitmap1.getHeight()/2;

                matrix.setRotate(ro[i-1]+90, px, py);
                Bitmap bitmap2=Bitmap.createBitmap(bitmap1, 0, 0,
                        bitmap1.getWidth(), bitmap1.getHeight(), matrix, true);

                int mh= (int) (bitmap1.getHeight()+Math.sin(rad/360*2*Math.PI)*(right-left));

                if (i==1||i==11){
                    Bitmap bitmap3=Bitmap.createBitmap(bitmap2.getWidth(),mh, Bitmap.Config.ARGB_8888);
                    Canvas canvas2=new Canvas(bitmap3);
                    canvas2.drawBitmap(bitmap2,0,mh-bitmap2.getHeight(),null);
                    bitmap2=bitmap3;
                }
                else if (i==2||i==4){
                    Bitmap bitmap3=Bitmap.createBitmap(mh,bitmap2.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas2=new Canvas(bitmap3);
                    canvas2.drawBitmap(bitmap2,0,0,null);
                    bitmap2=bitmap3;
                }
                else if (i==5||i==7){
                    Bitmap bitmap3=Bitmap.createBitmap(bitmap2.getWidth(),mh, Bitmap.Config.ARGB_8888);
                    Canvas canvas2=new Canvas(bitmap3);
                    canvas2.drawBitmap(bitmap2,0,0,null);
                    bitmap2=bitmap3;
                }
                else if (i==8||i==10){
                    Bitmap bitmap3=Bitmap.createBitmap(mh,bitmap2.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas2=new Canvas(bitmap3);
                    canvas2.drawBitmap(bitmap2,mh-bitmap2.getWidth(),0,null);
                    bitmap2=bitmap3;
                }


                ((ImageView)la.findViewById(ids[i-1])).setImageBitmap(bitmap2);
            }

        }

        frameLayout.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayout.layout(0,0,w,h);


        frameLayout.draw(canvas);
        //canvas.drawBitmap(Simple_Memo.getBitmapFromView(la),0,0,null);


    }

    public static Bitmap getImageBitmap(Context context,Pair<Integer,Integer> pair,int color,int id){
        //Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Inspiration-Regular");
        ImageView imageView=new ImageView(context);
        imageView.layout(0, 0, (int) (pair.first), (int) (pair.second)); //text box size 300px x 500px
        imageView.setDrawingCacheEnabled(true);
        imageView.setImageResource(id);
        imageView.setColorFilter(color);
        imageView.setImageAlpha(Color.alpha(color));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return Simple_Memo.getBitmapFromView(imageView);

    }
    public static int dip(int value,Context context){
        return (int) (value * context.getResources().getDisplayMetrics().density);
    }
    public static Bitmap rotatedRV(Context context,int color,Pair<Integer,Integer> pair,float degress,int id){
        Bitmap bitmap1=getImageBitmap(context,pair,color,id);

        // 画像の横、縦サイズを取得
        int imageWidth = bitmap1.getWidth();
        int imageHeight = bitmap1.getHeight();

        // Matrix インスタンス生成
        Matrix matrix = new Matrix();

        // 画像中心を基点に90度回転
        matrix.postRotate(degress);

        // 90度回転したBitmap画像を生成
        return Bitmap.createBitmap(bitmap1, 0, 0,
                imageWidth, imageHeight, matrix, true);
    }
    public static Bitmap getTextBitmap(Context context,String text,Pair<Integer,Integer> pair,int maxTextsize,float scale,String font){
        //Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Inspiration-Regular");
        TextView textView=new TextView(context);
        textView.layout(0, 0, (int) (pair.first*scale), (int) (pair.second*scale)); //text box size 300px x 500px
        textView.setWidth((int) (pair.first*scale));
        textView.setHeight((int) (pair.second*scale));

        if (font!=null){
            Typeface font_ = Typeface.createFromAsset(context.getAssets(), String.format("fonts/%s",font));
            textView.setTypeface(font_);
        }

        //textView.setTypeface(tf);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        maxTextsize= (int) (maxTextsize*scale);
        for (;maxTextsize>=1;maxTextsize--){
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,maxTextsize);
            if (isFit(textView))
                break;
        }
        textView.setMaxLines(1);
        textView.setDrawingCacheEnabled(true);
        int w=textView.getWidth();
        int h=textView.getHeight();
        int mindp=1;
        Bitmap bitmap = Bitmap.createBitmap((w<mindp)?mindp:w,(h<mindp)?mindp:h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Bitmap bitmap1= TextBitmap.TrimBitmap(Simple_Memo.getBitmapFromView(textView));
        canvas.drawBitmap(bitmap1,(w-bitmap1.getWidth())/2,(h-bitmap1.getHeight())/2,null);

        return bitmap;

    }
    public static boolean isFit(TextView textView){
        final int maxLine = 1;
        final StaticLayout.Builder layoutBuilder = StaticLayout.Builder.obtain(
                textView.getText(), 0, textView.getText().length(),  textView.getPaint(),textView.getWidth()
                        -textView.getPaddingLeft()-textView.getPaddingRight());

        layoutBuilder.setLineSpacing(textView.getLineSpacingExtra(), textView.getLineSpacingMultiplier())
                .setIncludePad(textView.getIncludeFontPadding())
                .setBreakStrategy(textView.getBreakStrategy())
                .setHyphenationFrequency(textView.getHyphenationFrequency())
                .setMaxLines(Integer.MAX_VALUE)
        ;
        final StaticLayout layout = layoutBuilder.build();

        if (layout.getHeight() >= (textView.getHeight()-textView.getPaddingTop()-textView.getPaddingBottom())*0.8) {
            return false;
        }
        return layout.getLineCount()<=maxLine;
    }



}