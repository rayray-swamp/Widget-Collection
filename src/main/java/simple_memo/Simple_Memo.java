package simple_memo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.StaticLayout;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.core.widget.AutoSizeableTextView;
import androidx.core.widget.TextViewCompat;

import java.util.HashMap;
import java.util.Map;

import AnalogClock.AnalogClock;
import Schedule.Schedule;
import calendar.NewAppWidget;
import calendar.R;
import save.ObjectStorage;
import todays_memo.todays_memo;
import todays_memo.WidgetSizeProvider;

/**
 * Implementation of App Widget functionality.
 */
public class Simple_Memo extends AppWidgetProvider {
    public static String SIMPLE_MEMO="SIMPLE_MEMO"+ NewAppWidget.PARAM;
    public static String SIMPLE_MEMO_DATA="SIMPLE_MEMO_DATA"+ NewAppWidget.PARAM;

    private static PendingIntent getPendingIntent(Context context,String action,int id){
        Intent alarmIntent = new Intent(context,Simple_Memo.class);
        alarmIntent.setAction(action);
        alarmIntent.putExtra("ID",id);
        return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
    static public void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.simple__memo);

        String font =null;
        Map<String,String> style= ObjectStorage.get(Simple_Memo.SIMPLE_MEMO_DATA+appWidgetId, Map.class,context);
        if (style!=null){
            font=style.get("Font");
            int color=Integer.valueOf(style.get("Color"));
            views.setInt(R.id.simplememo_view_im, "setColorFilter", color);
            views.setInt(R.id.simplememo_view_im, "setImageAlpha", Color.alpha(color));
            int textcolor=Integer.valueOf(style.get("TextColor"));
            views.setInt(R.id.simplememo_view, "setColorFilter", textcolor);
            views.setInt(R.id.simplememo_view, "setImageAlpha", Color.alpha(textcolor));
        }

        String text="";
        Map<String,String> data= ObjectStorage.get(Simple_Memo.SIMPLE_MEMO, Map.class,context);
        if (data!=null){
            text=data.getOrDefault(String.valueOf(appWidgetId),"");
        }
        WidgetSizeProvider widgetSizeProvider=new WidgetSizeProvider(context);
        Pair<Integer,Integer> pair = widgetSizeProvider.getWidgetsSize(appWidgetId,5);

        int textSize= (int) context.getResources().getDimensionPixelSize(R.dimen.simple_memo_textsize);

        views.setImageViewBitmap(R.id.simplememo_view,getTextBitmap(context,text,pair,textSize,2,font));

        views.setOnClickPendingIntent(R.id.simplememo_view,getPendingIntent(context,SIMPLE_MEMO+appWidgetId,appWidgetId));
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId:appWidgetIds)
            updateAppWidget(context, appWidgetManager, appWidgetId);

    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateAppWidget(context, appWidgetManager, appWidgetId);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        if (oldWidgetIds==null||newWidgetIds==null)
            return;

        if (oldWidgetIds.length!=newWidgetIds.length)
            return;


        Map<String,String> data= ObjectStorage.get(Simple_Memo.SIMPLE_MEMO, Map.class,context);

        if (data!=null){
            Map<String,String> newdata= new HashMap<>();
            int i=0;
            for (int oldId:oldWidgetIds){
                if (data.containsKey(String.valueOf(oldId))){
                    newdata.put(String.valueOf(newWidgetIds[i])
                            ,data.get(String.valueOf(oldId)));
                }
                i++;
            }
            ObjectStorage.save(newdata,Simple_Memo.SIMPLE_MEMO,context);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle= intent.getExtras();
        if (bundle!=null) {
            int id=bundle.getInt("ID",-1);
            if (id!=-1) {
                Intent configIntent = new Intent(context, Simple_Memo_activity.class);
                configIntent.putExtra("ID",id);
                configIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(configIntent);
            }
        }
        for (int id: AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, Simple_Memo.class)))
            updateAppWidget(context, AppWidgetManager.getInstance(context),id);
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Map<String,String> data= ObjectStorage.get(Simple_Memo.SIMPLE_MEMO, Map.class,context);
        if (data!=null){
            for (int id:appWidgetIds){
                data.remove(String.valueOf(id));
            }
            ObjectStorage.save(data,Simple_Memo.SIMPLE_MEMO,context);
        }
        for (int id:appWidgetIds){
            ObjectStorage.clear(Simple_Memo.SIMPLE_MEMO_DATA+id,context);
        }

        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    private static float spToPx(float sp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
    public static Bitmap getBitmapFromView(View view) {
        int w=view.getWidth();
        int h=view.getHeight();
        int mindp=1;
        Bitmap bitmap = Bitmap.createBitmap((w<mindp)?mindp:w,(h<mindp)?mindp:h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    public static int dip(Context context,int value){
        return (int) (value * context.getResources().getDisplayMetrics().density);
    }
    public static boolean isFit(TextView textView){
        final int maxLine = (int)(textView.getHeight() / (textView.getLineHeight()));
        final StaticLayout.Builder layoutBuilder = StaticLayout.Builder.obtain(
                textView.getText(), 0, textView.getText().length(),  textView.getPaint(),textView.getWidth()
                        -textView.getPaddingLeft()-textView.getPaddingRight());

        layoutBuilder.setLineSpacing(textView.getLineSpacingExtra(), textView.getLineSpacingMultiplier())
                .setIncludePad(textView.getIncludeFontPadding())
                .setBreakStrategy(textView.getBreakStrategy())
                .setHyphenationFrequency(textView.getHyphenationFrequency())
                .setMaxLines(Integer.MAX_VALUE)
        ;
        try {
            final StaticLayout layout = layoutBuilder.build();

            if (layout.getHeight() >= (textView.getHeight()-textView.getPaddingTop()-textView.getPaddingBottom())*0.8) {
                return false;
            }
            return layout.getLineCount()<maxLine;
        }catch (Exception e){}
        return false;
    }
    public static Bitmap getTextBitmap(Context context,String text,Pair<Integer,Integer> pair,int maxTextsize,float scale,String font){
        //Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Inspiration-Regular");
        TextView textView=new TextView(context);
        if (font!=null){
            Typeface font_ = Typeface.createFromAsset(context.getAssets(), String.format("fonts/%s",font));
            textView.setTypeface(font_);
        }
        textView.layout(0, 0, (int) (pair.first*scale), (int) (pair.second*scale)); //text box size 300px x 500px
        textView.setWidth((int) (pair.first*scale));
        textView.setHeight((int) (pair.second*scale));
        //textView.setTypeface(tf);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        maxTextsize= (int) (maxTextsize*scale);
        for (;maxTextsize>=1;maxTextsize--){
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,maxTextsize);
            if (isFit(textView))
                break;
        }

        textView.setDrawingCacheEnabled(true);
        return getBitmapFromView(textView);

    }
    public static Bitmap getTextBitmap(Context context,CharSequence text,Pair<Integer,Integer> pair,int maxTextsize,float scale,String font){
        //Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Inspiration-Regular");
        TextView textView=new TextView(context);
        if (font!=null){
            Typeface font_ = Typeface.createFromAsset(context.getAssets(), String.format("fonts/%s",font));
            textView.setTypeface(font_);
        }
        textView.layout(0, 0, (int) (pair.first*scale), (int) (pair.second*scale)); //text box size 300px x 500px
        textView.setWidth((int) (pair.first*scale));
        textView.setHeight((int) (pair.second*scale));
        //textView.setTypeface(tf);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        maxTextsize= (int) (maxTextsize*scale);
        for (;maxTextsize>=1;maxTextsize--){
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,maxTextsize);
            if (isFit(textView))
                break;
        }

        textView.setDrawingCacheEnabled(true);
        return getBitmapFromView(textView);

    }
    public static Bitmap getTextBitmap(Context context,String text,int w,int h,int maxTextsize,String font){
        return getTextBitmap(context, text, new Pair<>(w,h), maxTextsize,2,font);

    }
    public static Bitmap getTextBitmap(Context context,CharSequence text,int w,int h,int maxTextsize,String font){
        return getTextBitmap(context, text, new Pair<>(w,h), maxTextsize,2,font);

    }


}