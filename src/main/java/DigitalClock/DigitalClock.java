package DigitalClock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.Map;

import AnalogClock.AnalogClock;
import calendar.DateData;
import calendar.NewAppWidget;
import calendar.R;
import calendar.TextBitmap;
import save.ObjectStorage;
import simple_memo.Simple_Memo;
import todays_memo.WidgetSizeProvider;

/**
 * Implementation of App Widget functionality.
 */
public class DigitalClock extends AppWidgetProvider {
    private static final String ACTION_KAIROS_INTERVAL = "INTERVAL_"+ NewAppWidget.PARAM;
    public static String DIGITAL_CLOCK_DATA="DIGITAL_CLOCK_DATA"+ NewAppWidget.PARAM;

    private static final long INTERVAL = 60 * 1000;
    private final static long[] vz=new long[]{0};

    //----------------------------------------------------------
    private static PendingIntent getAlermPendingIntent(Context context, String action){
        Intent alarmIntent = new Intent(context, DigitalClock.class);

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

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        updateAppWidget(context, appWidgetManager, appWidgetIds);
        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenStateFilter.addAction(Intent.ACTION_USER_PRESENT);
        context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DigitalClock.this.onReceive(context, intent);
            }
        }, screenStateFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(Intent.ACTION_USER_PRESENT.equals(intent.getAction())){
            updateAppWidget(context, AppWidgetManager.getInstance(context),
                    AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context,DigitalClock.class)));
        }
        else if(ACTION_KAIROS_INTERVAL.equals(intent.getAction())){
            updateAppWidget(context, AppWidgetManager.getInstance(context),
                    AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, DigitalClock.class)));
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        updateAppWidget(context,appWidgetManager,new int[]{appWidgetId});
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int id:appWidgetIds){
            ObjectStorage.clear(DIGITAL_CLOCK_DATA+id,context);
        }
    }

    static public void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int[] appWidgetIds) {
        long t=System.currentTimeMillis();
        setInterval(context,INTERVAL);



        for (int appWidgetId:appWidgetIds){
            WidgetSizeProvider widgetSizeProvider=new WidgetSizeProvider(context);
            Pair<Integer,Integer> wh=widgetSizeProvider.getWidgetsSize(appWidgetId);
            RemoteViews	view = new RemoteViews(context.getPackageName(), R.layout.digital_clock);
            String font=null;


            int diameter= (int) (Math.min(wh.first/2,wh.second)*0.8);
            int half=diameter/2;
            int ts= (int) (diameter*0.8);
            int second_color=context.getColor(R.color.white);

            Map<String,String> style_= ObjectStorage.get(DigitalClock.DIGITAL_CLOCK_DATA+appWidgetId, Map.class,context);

            int backcolor=Color.WHITE;
            int color=Color.BLACK;
            int textcolor=Color.WHITE;
            if (style_!=null){
                backcolor=Integer.valueOf(style_.getOrDefault("Color",Color.WHITE+""));
                color=Integer.valueOf(style_.getOrDefault("Color2",Color.BLACK+""));
                textcolor=Integer.valueOf(style_.getOrDefault("TextColor",Color.WHITE+""));
                font=style_.getOrDefault("Font",null);
            }

            int style=diameter>150?R.drawable.frame_style2:R.drawable.frame_style3;
            Bitmap bitmap_back=getTextImageBitmap(context,"",ts,new Pair<>(wh.first,Math.min(wh.first/2,wh.second))
                    ,backcolor,textcolor,diameter>150?R.drawable.frame_style:diameter>100?R.drawable.frame_style2:R.drawable.frame_style3,null);
            view.setImageViewBitmap(R.id.digital_clock_back_img,bitmap_back);

            int[] b=new int[]{(DateData.getHour(vz[0]))/10,(DateData.getHour(vz[0]))%10
                    ,(DateData.getMinute(vz[0]))/10,(DateData.getMinute(vz[0]))%10};
            int[] f=new int[]{(DateData.getHour(t))/10,(DateData.getHour(t))%10
                    ,(DateData.getMinute(t))/10,(DateData.getMinute(t))%10};
            int[] bfv=new int[]{R.id.digital_clock_bottom_front_vf_1,R.id.digital_clock_bottom_front_vf_2
                    ,R.id.digital_clock_bottom_front_vf_3,R.id.digital_clock_bottom_front_vf_4};
            int[] tfv=new int[]{R.id.digital_clock_top_front_vf_1,R.id.digital_clock_top_front_vf_2
                    ,R.id.digital_clock_top_front_vf_3,R.id.digital_clock_top_front_vf_4};
            int[] tfv2=new int[]{R.id.digital_clock_top_front_vf2_1,R.id.digital_clock_top_front_vf2_2
                    ,R.id.digital_clock_top_front_vf2_3,R.id.digital_clock_top_front_vf2_4};
            int[] th=new int[]{R.id.digital_clock_top_h_1,R.id.digital_clock_top_h_2
                    ,R.id.digital_clock_top_h_3,R.id.digital_clock_top_h_4};
            int[] tb=new int[]{R.id.digital_clock_top_back_1,R.id.digital_clock_top_back_2
                    ,R.id.digital_clock_top_back_3,R.id.digital_clock_top_back_4};
            int[] bb=new int[]{R.id.digital_clock_bottom_back_1,R.id.digital_clock_bottom_back_2
                    ,R.id.digital_clock_bottom_back_3,R.id.digital_clock_bottom_back_4};
            int[] tf=new int[]{R.id.digital_clock_top_front_1,R.id.digital_clock_top_front_2
                    ,R.id.digital_clock_top_front_3,R.id.digital_clock_top_front_4};
            int[] tf2=new int[]{R.id.digital_clock_top_front2_1,R.id.digital_clock_top_front2_2
                    ,R.id.digital_clock_top_front2_3,R.id.digital_clock_top_front2_4};
            int[] bf=new int[]{R.id.digital_clock_bottom_front_1,R.id.digital_clock_bottom_front_2
                    ,R.id.digital_clock_bottom_front_3,R.id.digital_clock_bottom_front_4};

            for (int i=0;i<4;i++){

                if (f[i]!=b[i]) {
                    view.showNext(bfv[i]);

                    view.setViewVisibility(tfv[i], View.VISIBLE);
                    view.setViewVisibility(tfv2[i], View.VISIBLE);

                    view.showNext(tfv[i]);
                    view.showNext(tfv2[i]);
                }
                else {
                    view.setViewVisibility(tfv[i], View.GONE);
                    view.setViewVisibility(tfv2[i], View.GONE);
                }
                Bitmap bitmap_b=getTextImageBitmap(context,b[i]+"",ts,new Pair<>(diameter/2,diameter),color,textcolor,style,font);
                Bitmap bitmap_f=getTextImageBitmap(context,f[i]+"",ts,new Pair<>(diameter/2,diameter),color,textcolor,style,font);

                view.setImageViewBitmap(th[i],Bitmap.createBitmap(1,(wh.second-diameter)/2, Bitmap.Config.ARGB_8888));
                Bitmap bitmap_tb=Bitmap.createBitmap(bitmap_f,0,0,half,half);
                Bitmap bitmap_bb=Bitmap.createBitmap(bitmap_b,0,half,half,half);
                Bitmap bitmap_tf=Bitmap.createBitmap(half,diameter, Bitmap.Config.ARGB_8888);
                Canvas canvas=new Canvas(bitmap_tf);
                canvas.drawBitmap(Bitmap.createBitmap(bitmap_b,0,0,half,half/2),0,half,null);
                Bitmap bitmap_tf2=Bitmap.createBitmap(half,diameter, Bitmap.Config.ARGB_8888);
                canvas=new Canvas(bitmap_tf2);
                canvas.drawBitmap(Bitmap.createBitmap(bitmap_b,0,half/2,half,half/2),0,half,null);
                Bitmap bitmap_bf=Bitmap.createBitmap(bitmap_f,0,half,half,half);

                view.setImageViewBitmap(tb[i],bitmap_tb);
                view.setImageViewBitmap(bb[i],bitmap_bb);
                view.setImageViewBitmap(tf[i],bitmap_tf);
                view.setImageViewBitmap(tf2[i],bitmap_tf2);
                view.setImageViewBitmap(bf[i],bitmap_bf);

            }
            appWidgetManager.updateAppWidget(appWidgetId, view);
        }
        vz[0]=t;
    }
    public static int dip(int value,Context context){
        return (int) (value * context.getResources().getDisplayMetrics().density);
    }
    public static Bitmap getTextImageBitmap(Context context,String text, int textsize,Pair<Integer,Integer> pair, int color,int textcolor, int id,String font){
        textsize*=0.8;
        //Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Inspiration-Regular\");
        /*TextView textView = new TextView(context);
        if (font!=null) {
            Typeface font_ = Typeface.createFromAsset(context.getAssets(), String.format("fonts/%s", font));
            textView.setTypeface(font_);
        }
        textView.layout(0, 0, (int) (pair.first), (int) (pair.second)); //text box size 300px x 500px
        textView.setDrawingCacheEnabled(true);
        textView.setBackgroundResource(id);
        textView.setBackgroundTintList(
                ColorStateList.valueOf(color)
        );

        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(textcolor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textsize);*/
        Bitmap bitmap=TextBitmap.textAsBitmap(context,text,converPxToSp(context,textsize),textcolor,font);
        ImageView imageView = new ImageView(context);
        imageView.layout(0, 0, (int) (pair.first), (int) (pair.second)); //text box size 300px x 500px
        imageView.setDrawingCacheEnabled(true);
        imageView.setBackgroundResource(id);
        imageView.setBackgroundTintList(
                ColorStateList.valueOf(color)
        );

        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageBitmap(bitmap);


        return Simple_Memo.getBitmapFromView(imageView);

    }
    public static float converPxToSp(Context context,float px) {
        return px / context.getResources().getDisplayMetrics().scaledDensity;
    }
}