package todays_memo;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.util.Pair;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class WidgetSizeProvider {
    final private Context context;
    final private AppWidgetManager appWidgetManager;
    public WidgetSizeProvider(android.content.Context context){
        this.context=context;
        appWidgetManager=AppWidgetManager.getInstance(context);
    }


    public Pair<Integer,Integer> getWidgetsSize(int widgetId){
        boolean isPortrait = context.getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT;
        int width = getWidgetWidth(isPortrait, widgetId);
        int height = getWidgetHeight(isPortrait, widgetId);
        int widthInPx = dip(width);
        int heightInPx = dip(height);
        return Pair.create(widthInPx,heightInPx);
    }
    public Pair<Integer,Integer> getWidgetsSize(int widgetId,int margin){
        boolean isPortrait = context.getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT;
        int width = getWidgetWidth(isPortrait, widgetId);
        int height = getWidgetHeight(isPortrait, widgetId);
        int widthInPx = dip(width);
        int heightInPx = dip(height);
        return Pair.create(widthInPx-2*dip(margin),heightInPx-2*dip(margin));
    }

    private int getWidgetWidth(boolean isPortrait, int widgetId) {

        if (isPortrait) {
            return getWidgetSizeInDp(widgetId, AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        } else {
            return getWidgetSizeInDp(widgetId, AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        }
    }
    private int getWidgetHeight(boolean isPortrait, int widgetId) {

        if (isPortrait) {
            return getWidgetSizeInDp(widgetId, AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        } else {
            return getWidgetSizeInDp(widgetId, AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        }
    }

    private int getWidgetSizeInDp(int widgetId,String key) {
        return appWidgetManager.getAppWidgetOptions(widgetId).getInt(key, 0);
    }

    public int dip(int value){
        return (int) (value * context.getResources().getDisplayMetrics().density);
    }

}
