package calendar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TextBitmap {
    private Context context;
    private String font;
    private float dateTextSize;
    private List<Bitmap[]> dayBitmaps;
    private int[] daycolors;
    private int todaycolor;
    private int color;
    private Bitmap today;
    private Bitmap next;
    private Bitmap previous;
    private int year;
    private int month;
    private String lang;
    private Map<String,Bitmap> bitmapMap;
    private Map<Integer,Bitmap> dowBitmap;

    public void Initialization(Context context,String font,float dateTextSize,final int[] daycolors
            ,int todaycolor,int year,int month,int color,String lang){
        this.context=context;
        this.font=font;
        this.dateTextSize=dateTextSize;
        this.daycolors=daycolors;
        this.todaycolor=todaycolor;
        this.year=year;
        this.month=month;
        this.color=color;
        this.lang=lang;
    }
    public void startInit(){
        Log.d("start_init","");
        int top=10000;
        int bottom = 0;

        today=textAsBitmap(context,"Today",42,todaycolor,font);
        previous=textAsBitmap(context,"<",42,color,font);
        next=textAsBitmap(context,">",42,color,font);

        dayBitmaps=new ArrayList<>();
        bitmapMap=new HashMap<>();
        dowBitmap=new HashMap<>();

        for (int i=1;i<32;i++){
            int[] d=getTrimInt(textAsBitmapNoTrim(context,""+i,dateTextSize,Color.BLACK,font));
            if (top>d[2])
                top=d[2];
            if (bottom<d[3])
                bottom=d[3];
        }
        for (int i=0;i<32;i++){
            Bitmap[] bitmaps=new Bitmap[daycolors.length];

            for (int j=0;j<daycolors.length;j++){
                bitmaps[j]=textAsBitmapNoTrim(context,(i==0)?"":""+i,dateTextSize,daycolors[j],font);
                bitmaps[j]=TrimBitmapLock(bitmaps[j],top,bottom);
            }

            dayBitmaps.add(bitmaps);
        }
        for (int i=0;i<=24;i++){
            String text=getMonthText(year, month+i);
            bitmapMap.put(text,textAsBitmap(context,text,42,color,font));
        }
        for (int i=0;i<7;i++){
            dowBitmap.put(i,textAsBitmapNoTrim(context,getDowText(i,context,font,lang),context.getResources().getDimension(R.dimen.calendar_dow),daycolors[0],font));
        }
    }

    public static String getDowText(int dow,Context context,String font,String lang){
        if (dow<0||dow>6)
            return "";

        int[] dows=new int[]{R.string.Sunday,R.string.Monday,R.string.Tuesday,R.string.Wednesday,R.string.Thursday,R.string.Friday,R.string.Saturday};

        String text=context.getString(dows[dow]);

        if (lang!=null){
            text=getLocalizedResources(context,new Locale(lang)).getString(dows[dow]);
        }

        return text;
    }

    public static String getDowTextPE(int dow,Context context,String font,String lang){
        if (dow<0||dow>6)
            return "";

        int[] dows=new int[]{R.string.SUNDAY,R.string.MONDAY,R.string.TUESDAY,R.string.WEDNESDAY,R.string.THURSDAY,R.string.FRIDAY,R.string.SATURDAY};

        String text=context.getString(dows[dow]);

        if (lang!=null){
            text=getLocalizedResources(context,new Locale(lang)).getString(dows[dow]);
        }

        return text;
    }

    public static Resources getLocalizedResources(Context context, Locale desiredLocale) {
        Configuration conf = context.getResources().getConfiguration();
        conf = new Configuration(conf);
        conf.setLocale(desiredLocale);
        Context localizedContext = context.createConfigurationContext(conf);
        return localizedContext.getResources();
    }

    public Bitmap getMonthBitmap(String text){
        if (bitmapMap==null)
            startInit();
        if (bitmapMap.containsKey(text)) {
            return bitmapMap.get(text);
        }
        startInit();
        return textAsBitmap(context,text,42,Color.BLACK,font);
    }
    public String getMonthText(int year,int month){
        return getMonthText(year, month,context,font,lang);
    }
    public String getMonthText(int year,int month,int nyear){
        return getMonthText(year, month,nyear,context,font,lang);
    }

    public static String getMonthText(int year,int month,Context context,String font,String lang){
        final int nyear=year;
        year=(12+month)/12-1+year;
        month=((1200+month)%12);

        if (year!=nyear)
            return DateData.getNowMonth(DateData.getLongOnTime(year,month),context,lang);

        int[] ms = new int[]{R.string.Month_1,R.string.Month_2,R.string.Month_3,R.string.Month_4,R.string.Month_5,R.string.Month_6,
                R.string.Month_7,R.string.Month_8,R.string.Month_9,R.string.Month_10,R.string.Month_11,R.string.Month_12,};
        String[] dms = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};

        String text=context.getString(ms[month]);
        if (lang!=null){
            text=getLocalizedResources(context,new Locale(lang)).getString(ms[month]);
        }
        return text;
    }
    public static String getMonthText(int year,int month,int nyear,Context context,String font,String lang){
        year=(12+month)/12-1+year;
        month=((1200+month)%12);

        if (year!=nyear)
            return DateData.getNowMonth(DateData.getLongOnTime(year,month),context,lang);

        int[] ms = new int[]{R.string.Month_1,R.string.Month_2,R.string.Month_3,R.string.Month_4,R.string.Month_5,R.string.Month_6,
                R.string.Month_7,R.string.Month_8,R.string.Month_9,R.string.Month_10,R.string.Month_11,R.string.Month_12,};
        String[] dms = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};

        String text=context.getString(ms[month]);
        if (lang!=null){
            text=getLocalizedResources(context,new Locale(lang)).getString(ms[month]);
        }
        return text;
    }
    public static String getWeekText(int week,Context context){
        int[] ms = new int[]{R.string.week_1,R.string.week_2,R.string.week_3,R.string.week_4,R.string.week_5,R.string.week_6};
        try {
            String mo=context.getString(ms[week]);
            return mo;
        }
        catch (Exception e){
            e.printStackTrace();
        }


        return context.getString(R.string.Week);
    }

    public Bitmap getDayBitmap(int day,int color_number){
        if (dayBitmaps==null)
            startInit();
        if (dayBitmaps.size()!=32) {
            startInit();
        }


        return dayBitmaps.get(day<0||day>=dayBitmaps.size()?0:day)[color_number<0||color_number>=daycolors.length?0:color_number];
    }
    public Bitmap getToday() {
        if (today==null)
            startInit();
        return today;
    }
    public Bitmap getNext() {
        if (next==null)
            startInit();
        return next;
    }
    public Bitmap getPrevious() {
        if (previous==null)
            startInit();
        return previous;
    }
    public Bitmap getDowBitmap(int dow){
        if (dowBitmap==null)
            startInit();
        if (dowBitmap.containsKey(dow))
            return dowBitmap.get(dow);
        startInit();
        if (dowBitmap.containsKey(dow))
            return dowBitmap.get(dow);
        return null;
    }


    public static Bitmap textAsBitmap(Context context, String messageText, float textSizeSP, int textColor, String fontName){
        if (messageText.length()==0)
            messageText=" ";
        float textSize=spToPx(textSizeSP,context);
        Paint paint=new Paint();
        if (fontName!=null) {
            Typeface font = Typeface.createFromAsset(context.getAssets(), String.format("fonts/%s", fontName));
            paint.setTypeface(font);
        }
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline=-paint.ascent(); // ascent() is negative
        int width=(int)(paint.measureText(messageText)*2.5f); // round
        int height=(int)(baseline+paint.descent()*2.5f);
        Bitmap image=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(image);
        canvas.drawText(messageText,0,baseline,paint);
        return TrimBitmap(image);
    }
    public static Bitmap textAsBitmapNoTrim(Context context, String messageText, float textSizeSP, int textColor,String fontName){
        if (messageText.length()==0)
            messageText=" ";
        float textSize=spToPx(textSizeSP,context);
        Paint paint=new Paint();
        if (fontName!=null) {
            Typeface font = Typeface.createFromAsset(context.getAssets(), String.format("fonts/%s", fontName));
            paint.setTypeface(font);
        }
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline=-paint.ascent(); // ascent() is negative
        int width=(int)(paint.measureText(messageText)+0.5f); // round
        int height=(int)(baseline+paint.descent()+0.5f);
        Bitmap image=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(image);
        canvas.drawText(messageText,0,baseline,paint);
        return image;
    }

    private static float spToPx(float sp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
    public static int[] getTrimInt(Bitmap bmp) {
        final int imgHeight = bmp.getHeight();
        final int imgWidth = bmp.getWidth();

        //Take Data
        int[] pixels = new int[imgWidth * imgHeight];
        bmp.getPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
        int[] horizontalData = new int[imgHeight], verticalData = new int[imgWidth];
        int num = 0;
        for (int pixel : pixels) {
            horizontalData[num / imgWidth] |= pixel;
            verticalData[num % imgWidth] |= pixel;
            num++;
        }


        //TRIM WIDTH - LEFT
        int startWidth = 0;
        for (int data : verticalData) {
            if ((data >> 24) != 0)
                break;
            startWidth++;
        }


        //TRIM WIDTH - RIGHT
        int endWidth = imgWidth;
        for (int x = imgWidth - 1; x >= 0; x--) {
            int data = verticalData[x];
            if ((data >> 24) != 0)
                break;
            endWidth--;
        }


        //TRIM HEIGHT - TOP
        int startHeight = 0;
        for (int data : horizontalData) {
            if ((data >> 24) != 0)
                break;
            startHeight++;
        }


        //TRIM HEIGHT - BOTTOM
        int endHeight = imgHeight;
        for (int x = imgHeight - 1; x >= 0; x--) {
            int data = horizontalData[x];
            if ((data >> 24) != 0)
                break;
            endHeight--;
        }

        return new int[]{startWidth,endWidth,startHeight,endHeight};
    }
    public static Bitmap TrimBitmap(Bitmap bmp) {
        final int imgHeight = bmp.getHeight();
        final int imgWidth  = bmp.getWidth();

        //Take Data
        int[] pixels = new int[imgWidth * imgHeight];
        bmp.getPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
        int[] horizontalData=new int[imgHeight],verticalData=new int[imgWidth];
        int num=0;
        for (int pixel:pixels){
            horizontalData[num/imgWidth]|=pixel;
            verticalData[num%imgWidth]|=pixel;
            num++;
        }


        //TRIM WIDTH - LEFT
        int startWidth = 0;
        for(int data:verticalData) {
            if ((data>>24)!=0)
                break;
            startWidth++;
        }


        //TRIM WIDTH - RIGHT
        int endWidth  = imgWidth;
        for(int x = imgWidth - 1; x >= 0; x--) {
            int data = verticalData[x];
            if ((data>>24)!=0)
                break;
            endWidth--;
        }



        //TRIM HEIGHT - TOP
        int startHeight = 0;
        for(int data:horizontalData) {
            if ((data>>24)!=0)
                break;
            startHeight++;
        }



        //TRIM HEIGHT - BOTTOM
        int endHeight = imgHeight;
        for(int x = imgHeight - 1; x >= 0; x--) {
            int data =horizontalData[x];
            if ((data>>24)!=0)
                break;
            endHeight--;
        }

        if (startHeight==imgHeight)
            return bmp;
        return Bitmap.createBitmap(
                bmp,
                startWidth,
                startHeight,
                endWidth - startWidth,
                endHeight - startHeight
        );

    }
    public static Bitmap TrimBitmapLock(Bitmap bmp,int top,int bottom) {
        final int imgHeight = bmp.getHeight();
        final int imgWidth  = bmp.getWidth();

        if (top>=imgHeight||bottom>imgHeight||top>=bottom)
            return bmp;

        //Take Data
        int[] pixels = new int[imgWidth * imgHeight];
        bmp.getPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
        int[] horizontalData=new int[imgHeight],verticalData=new int[imgWidth];
        int num=0;
        for (int pixel:pixels){
            horizontalData[num/imgWidth]|=pixel;
            verticalData[num%imgWidth]|=pixel;
            num++;
        }


        //TRIM WIDTH - LEFT
        int startWidth = 0;
        for(int data:verticalData) {
            if ((data>>24)!=0)
                break;
            startWidth++;
        }


        //TRIM WIDTH - RIGHT
        int endWidth  = imgWidth;
        for(int x = imgWidth - 1; x >= 0; x--) {
            int data = verticalData[x];
            if ((data>>24)!=0)
                break;
            endWidth--;
        }



        //TRIM HEIGHT - BOTTOM
        int endHeight = imgHeight;
        for(int x = imgHeight - 1; x >= 0; x--) {
            int data =horizontalData[x];
            if ((data>>24)!=0)
                break;
            endHeight--;
        }


        if (endWidth<=startWidth)
            return bmp;

        return Bitmap.createBitmap(
                bmp,
                startWidth,
                top,
                endWidth - startWidth,
                bottom - top
        );

    }


    private  static boolean isPrintable( String c,String fontName) {
        return fontName==null;
        /*
        String[] ss=c.split("");
        Paint paint=new Paint();
        if (fontName!=null) {
            Typeface font = Typeface.createFromAsset(context.getAssets(), String.format("fonts/%s", fontName));
            paint.setTypeface(font);
        }
        boolean hasGlyph=true;
        for (String s:ss) {
            if (s.equals(""))
                continue;
            hasGlyph = hasGlyph && paint.hasGlyph(s);
        }
        return hasGlyph;*/
    }
    private boolean isPrintable( String c) {
        return isPrintable(c,font);
    }



}
