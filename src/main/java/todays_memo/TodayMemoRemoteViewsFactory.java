package todays_memo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.text.StaticLayout;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import calendar.CalendarData;
import calendar.DateData;
import calendar.NewAppWidget;
import calendar.R;
import calendar.TextBitmap;
import save.ObjectStorage;
import simple_memo.Simple_Memo;
import static todays_memo.Memo_Activity.WEEK;
import static todays_memo.Memo_Activity.MONTH;

import static simple_memo.Simple_Memo.getBitmapFromView;
import static todays_memo.todays_memo.TODAYS_MEMO_DATA;

public class TodayMemoRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        public static final String LAYOUT_W="LAYOUT_W"+ NewAppWidget.PARAM;
        public static final String LIST_ID="LIST_ID"+ NewAppWidget.PARAM;
        public static final String LIST_TOUCH="LIST_TOUCH"+ NewAppWidget.PARAM;
        public static final String LIST_TOUCH_TEXT_ID="LIST_TOUCH_TEXT_ID"+ NewAppWidget.PARAM;
    private Context mContext;
    //1:text  2:bool  3:map key
    private List<String[]> mList;
    private List<Bitmap> bList=new ArrayList<>();
    private int w;
    private int id=0;
    private int textcolor=Color.BLACK;
    private int stextcolor;
    private String touch_text_id="";

    public TodayMemoRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        mList=setmList(mContext);
        w=intent.getIntExtra(LAYOUT_W,10000);
        stextcolor=applicationContext.getColor(R.color.gray);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        Integer w=ObjectStorage.get(LAYOUT_W,Integer.class,mContext);
        if (w!=null)
            this.w=w;
        Integer id=ObjectStorage.get(LIST_ID,Integer.class,mContext);
        textcolor=Color.BLACK;
        stextcolor=mContext.getColor(R.color.gray);
        if (id!=null) {
            this.id = id;
            Map<String,String> style_= ObjectStorage.get(TODAYS_MEMO_DATA+id, Map.class,mContext);
            if (style_!=null){
                textcolor=Integer.valueOf(style_.getOrDefault("TextColor",Color.BLACK+""));
                stextcolor=Integer.valueOf(style_.getOrDefault("SelectedTextColor",stextcolor+""));
            }
        }
        touch_text_id=ObjectStorage.get(LIST_TOUCH_TEXT_ID,String.class,mContext);
        if (touch_text_id==null)
            touch_text_id="";
        mList=setmList(mContext);
    }


    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position<0||position>=mList.size())
            return null;


        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.today_memo_list_item);

        /*rv.setImageViewBitmap(R.id.tmli_im, getTextBitmap(mContext,
                w - Simple_Memo.dip(mContext, 30),
                mList.get(position)[0],
                (int) mContext.getResources().getDimensionPixelSize(R.dimen.today_memo_textsize)));*/
        rv.setTextViewText(R.id.tmli_text,mList.get(position)[0]);

        rv.setOnClickFillInIntent(R.id.tmli_vf, getPendingSelfIntent(LIST_TOUCH,
                mList.get(position)[0],
                mList.get(position)[2],
                id));
        if ((mList.get(position)[0]+mList.get(position)[2]).equals(touch_text_id)) {
            rv.showNext(R.id.tmli_vf);
        }

        if (Boolean.valueOf(mList.get(position)[1])){
            int color = stextcolor;
            //rv.setInt(R.id.tmli_im, "setColorFilter", color);
            //rv.setInt(R.id.tmli_im, "setImageAlpha", Color.alpha(color));
            rv.setTextColor(R.id.tmli_text,color);

            rv.setInt(R.id.tmli_ch,"setImageResource",R.drawable.check2);
            rv.setInt(R.id.tmli_ch, "setColorFilter", color);
            rv.setInt(R.id.tmli_ch, "setImageAlpha", Color.alpha(color));
        }
        else {
            int color = textcolor;
            //rv.setInt(R.id.tmli_im, "setColorFilter", color);
            //rv.setInt(R.id.tmli_im, "setImageAlpha", Color.alpha(color));
            rv.setTextColor(R.id.tmli_text,color);

            rv.setInt(R.id.tmli_ch,"setImageResource",R.drawable.check);
            rv.setInt(R.id.tmli_ch, "setColorFilter", color);
            rv.setInt(R.id.tmli_ch, "setImageAlpha", Color.alpha(color));
        }

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    private static List<String[]> getList(String text,Map<String,Boolean> map,String key,boolean oncheked){
        if (map==null)
            map=new HashMap<>();
        List<String[]> list=new ArrayList<>();
        String[] ts = text.split("\n");
        for (String t:ts){
            if (!t.equals("")){
                if (map.containsKey(t)){
                    list.add(new String[]{t,map.get(t).toString(),key,Boolean.toString(oncheked)});
                    continue;
                }
                list.add(new String[]{t,Boolean.toString(false),key,Boolean.toString(oncheked)});
            }
        }
        return list;
    }
    public static List<String[]> setmList(Context context){
        Long t=null;
        try {
            t= ObjectStorage.get(todays_memo.TODAYS_MEMO,Long.class,context);
        }catch (Exception e){}
        final long cp=(t==null? System.currentTimeMillis():t);
        String text="";
        Map<String,String> map= ObjectStorage.get(todays_memo.MEMO, Map.class,context);
        Map<String,Map<String,Boolean>> textmap= ObjectStorage.get(todays_memo.MEMO_LIST, Map.class,context);
        if (textmap==null)
            textmap=new HashMap();

        List<String[]> list=new ArrayList<>();


        if (map!=null){
            if (map.containsKey(DateData.getNowMonth(cp))){
                String key=DateData.getNowMonth(cp);
                String ntext=map.get(key);
                text+=ntext;
                list.addAll(getList(ntext,textmap.getOrDefault(key,new HashMap<>()),key,false));
            }
            if (map.containsKey(DateData.getNowWeek(cp))){
                if (!"".equals(text))
                    text+="\n";
                String key=DateData.getNowWeek(cp);
                String ntext=map.get(key);
                text+=ntext;
                list.addAll(getList(ntext,textmap.getOrDefault(key,new HashMap<>()),key,false));
            }
            if (map.containsKey(DateData.getNowDate(cp))) {
                if (!"".equals(text))
                    text+="\n";
                String key=DateData.getNowDate(cp);
                String ntext=map.get(key);
                text+=ntext;
                list.addAll(getList(ntext,textmap.getOrDefault(key,new HashMap<>()),key,false));
            }
            for (Map.Entry<String,String> entry:map.entrySet()){
                if (!entry.getKey().contains("{"))
                    continue;
                if (ComparisonDate.isContainCondition(DateData.getNowDate(cp),entry.getKey())){
                    if (!"".equals(text))
                        text+="\n";
                    String key=DateData.getNowDate(cp);
                    int mode=ComparisonDate.getMode(entry.getKey());
                    if (mode==WEEK)
                        key=DateData.getNowWeek(cp);
                    else if(mode==MONTH)
                        key=DateData.getNowMonth(cp);

                    String ntext=entry.getValue();
                    text+=ntext;
                    list.addAll(getList(ntext,textmap.getOrDefault(key,new HashMap<>()),key,ComparisonDate.onCheked(entry.getKey())));
                }
            }
        }
        return list;
    }
    private static int getHeight(TextView textView){
        final StaticLayout.Builder layoutBuilder = StaticLayout.Builder.obtain(
                textView.getText(), 0, textView.getText().length(),  textView.getPaint(),textView.getWidth());

        layoutBuilder.setLineSpacing(textView.getLineSpacingExtra(), textView.getLineSpacingMultiplier())
                .setIncludePad(textView.getIncludeFontPadding())
                .setBreakStrategy(textView.getBreakStrategy())
                .setHyphenationFrequency(textView.getHyphenationFrequency())
                .setMaxLines(Integer.MAX_VALUE)
        ;
        final StaticLayout layout = layoutBuilder.build();

        return (int) (layout.getHeight()*1.25);
    }
    public static Bitmap getTextBitmap(Context context,int w, String text, int textsize){
        float scale=2;
        TextView textView=new TextView(context);
        textView.layout(0, 0, (int) (w*scale), (int) (w*scale)); //text box size 300px x 500px
        textView.setWidth((int) (w*scale));
        textView.setHeight((int) (w*scale));
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textsize*scale);
        int h=getHeight(textView);

        textView.layout(0, 0, (int) (w*scale), (int) (h)); //text box size 300px x 500px
        textView.setHeight(h);

        textView.setDrawingCacheEnabled(true);
        return getBitmapFromView(textView);

    }
    public static Intent getPendingSelfIntent(String action,String text,String key,int id) {
        Intent refreshIntent = new Intent();
        refreshIntent.setAction(action + id);
        refreshIntent.putExtra("KEY", key);
        refreshIntent.putExtra("TEXT", text);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
        return refreshIntent;
    }
}
