package Schedule;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.aigestudio.wheelpicker.WheelPicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import calendar.DateData;
import calendar.R;

import calendar.TextBitmap;
import save.ObjectStorage;
import simple_memo.Simple_Memo;
import todays_memo.todays_memo;

public class Select_day_activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d("cccccc","");


        setContentView(R.layout.simple__memo_activity);
        Context context= Select_day_activity.this;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dialogWidth = (int) (metrics.widthPixels * 0.8);
        Dialog dialog = new Dialog(context,R.style.MyDialogTheme);
        View view=View.inflate(this, R.layout.select_day_dialog,null);
        dialog.setContentView(view);
        final long date=getIntent().getExtras().getLong("Date");
        final Button button_ok=view.findViewById(R.id.simple_memo_dialog_ok);
        final Button button_cancel=view.findViewById(R.id.simple_memo_dialog_cancel);
        final Button button_reset=view.findViewById(R.id.simple_memo_dialog_reset);
        final long et = DateData.getLongOnTime(1990,0);




        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = dialogWidth;
        dialog.getWindow().setAttributes(lp);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });
        try {
            dialog.show();
        }catch (Exception e){
            finish();
        }

        View calendarview=View.inflate(context, R.layout.new_app_widget,null);
        ((FrameLayout)view.findViewById(R.id.today_memo_dialog_re_fr)).addView(calendarview);

        calendarview.findViewById(R.id.calendar_relative_layout).setVisibility(View.GONE);

        TextBitmap textBitmap=new TextBitmap();
        textBitmap.Initialization(context,null,context.getResources().getDimension(R.dimen.date_text_size),
                new int[]{context.getColor(R.color.black),
                        context.getColor(R.color.black),
                        context.getColor(R.color.black),
                        context.getColor(R.color.gray),
                        context.getColor(R.color.red)},
                Color.WHITE,
                DateData.getYear(),DateData.getMonth(),Color.BLACK,null);

        class eF{
            int scaleMonth=DateData.getYear(date)*12+DateData.getMonth(date)-1990*12;
            int nowScaleMonth=DateData.getYear(date)*12+DateData.getMonth(date)-1990*12;
            int nowDate= DateData.getDateOfMonth(date);
            int picker_year=0;
            int picker_month=0;

            int getMonth(){
                int year=DateData.getYear(et);
                int month=DateData.getMonth(et)+scaleMonth;
                year=(12+month)/12-1+year;
                month=(1200+month)%12;
                return month;
            }
            int getYear(){
                int year=DateData.getYear(et);
                int month=DateData.getMonth(et)+scaleMonth;
                year=(12+month)/12-1+year;
                month=(1200+month)%12;
                return year;
            }

            int getnowMonth(){
                int year=DateData.getYear(et);
                int month=DateData.getMonth(et)+nowScaleMonth;
                year=(12+month)/12-1+year;
                month=(1200+month)%12;
                return month;
            }
            int getnowYear(){
                int year=DateData.getYear(et);
                int month=DateData.getMonth(et)+nowScaleMonth;
                year=(12+month)/12-1+year;
                month=(1200+month)%12;
                return year;
            }

            void setTitle(){
                ((TextView)view.findViewById(R.id.today_memo_dialog_re_title)).setText(
                        DateData.getNowMonth(DateData.getLongOnTime(getYear(),getMonth()),context,true)
                );
            }

            void setCalendar(){
                int year=DateData.getYear(et);
                int month=DateData.getMonth(et)+scaleMonth;
                year=(12+month)/12-1+year;
                month=(1200+month)%12;

                int ndate=DateData.getDateOfMonth(et);

                int[] dow=new int[]{R.id.su,R.id.mo,R.id.tu,R.id.we,R.id.th,R.id.fr,R.id.sa};
                for (int i=0;i<7;i++){
                    ((ImageView)calendarview.findViewById(dow[i])).setImageBitmap(textBitmap.getDowBitmap(i));
                }

                int[][] cellses={
                        {R.id.su1,R.id.mo1,R.id.tu1,R.id.we1,R.id.th1,R.id.fr1,R.id.sa1},
                        {R.id.su2,R.id.mo2,R.id.tu2,R.id.we2,R.id.th2,R.id.fr2,R.id.sa2},
                        {R.id.su3,R.id.mo3,R.id.tu3,R.id.we3,R.id.th3,R.id.fr3,R.id.sa3},
                        {R.id.su4,R.id.mo4,R.id.tu4,R.id.we4,R.id.th4,R.id.fr4,R.id.sa4},
                        {R.id.su5,R.id.mo5,R.id.tu5,R.id.we5,R.id.th5,R.id.fr5,R.id.sa5},
                        {R.id.su6,R.id.mo6,R.id.tu6,R.id.we6,R.id.th6,R.id.fr6,R.id.sa6}};

                int week=0;
                for (int[] cells:cellses){
                    week++;
                    int dateofweek=0;
                    for (int cell:cells){
                        dateofweek++;
                        int date=DateData.getDateonCalendar(year,month,week,dateofweek);
                        int textcolor=0;
                        if (scaleMonth==0&&date<ndate)
                            textcolor=3;
                        else if (scaleMonth==0&&date==ndate)
                            textcolor=4;
                        ((ImageView)calendarview.findViewById(cell)).setImageBitmap(textBitmap.getDayBitmap(date,textcolor));
                    }
                }
                int visibility = (DateData.getDateonCalendar(year,month,6,1)!=0)? View.VISIBLE:View.GONE;
                calendarview.findViewById(R.id.sixth_week).setVisibility(visibility);
            }

            void setNowTouch(boolean animVf){
                int[][] cellses={
                        {R.id.su1_layout,R.id.mo1_layout,R.id.tu1_layout,R.id.we1_layout,R.id.th1_layout,R.id.fr1_layout,R.id.sa1_layout},
                        {R.id.su2_layout,R.id.mo2_layout,R.id.tu2_layout,R.id.we2_layout,R.id.th2_layout,R.id.fr2_layout,R.id.sa2_layout},
                        {R.id.su3_layout,R.id.mo3_layout,R.id.tu3_layout,R.id.we3_layout,R.id.th3_layout,R.id.fr3_layout,R.id.sa3_layout},
                        {R.id.su4_layout,R.id.mo4_layout,R.id.tu4_layout,R.id.we4_layout,R.id.th4_layout,R.id.fr4_layout,R.id.sa4_layout},
                        {R.id.su5_layout,R.id.mo5_layout,R.id.tu5_layout,R.id.we5_layout,R.id.th5_layout,R.id.fr5_layout,R.id.sa5_layout},
                        {R.id.su6_layout,R.id.mo6_layout,R.id.tu6_layout,R.id.we6_layout,R.id.th6_layout,R.id.fr6_layout,R.id.sa6_layout}};

                for (int[] cells:cellses) {
                    for (int cell : cells) {
                        ((FrameLayout) calendarview.findViewById(cell)).removeAllViews();
                    }
                }
                if (nowScaleMonth!=scaleMonth)
                    return ;

                long nowtime=DateData.getLongOnTime(getYear(),getMonth(),nowDate);
                int week=DateData.getWeekOfMonth(nowtime)-1;
                int dow=DateData.getDateOfWeek(nowtime)-1;

                View nv=View.inflate(context, R.layout.calendar_select_layout,null);
                ((FrameLayout) calendarview.findViewById(cellses[week][dow])).addView(nv);
                if (animVf)
                    ((ViewFlipper)nv.findViewById((R.id.calendar_select))).showNext();
                ((ImageView)nv.findViewById((R.id.calendar_select_img))).setImageResource(R.drawable.underline);
                ((ImageView)nv.findViewById(R.id.calendar_select_img))
                        .setColorFilter(getColor(R.color.gray));
                ((ImageView)nv.findViewById(R.id.calendar_select_img))
                        .setImageAlpha(Color.alpha(getColor(R.color.gray)));
            }

            void setClick(){
                int year=getYear();
                int month=getMonth();
                int ndate=DateData.getDateOfMonth(et);

                int[][] cellses={
                        {R.id.su1_layout,R.id.mo1_layout,R.id.tu1_layout,R.id.we1_layout,R.id.th1_layout,R.id.fr1_layout,R.id.sa1_layout},
                        {R.id.su2_layout,R.id.mo2_layout,R.id.tu2_layout,R.id.we2_layout,R.id.th2_layout,R.id.fr2_layout,R.id.sa2_layout},
                        {R.id.su3_layout,R.id.mo3_layout,R.id.tu3_layout,R.id.we3_layout,R.id.th3_layout,R.id.fr3_layout,R.id.sa3_layout},
                        {R.id.su4_layout,R.id.mo4_layout,R.id.tu4_layout,R.id.we4_layout,R.id.th4_layout,R.id.fr4_layout,R.id.sa4_layout},
                        {R.id.su5_layout,R.id.mo5_layout,R.id.tu5_layout,R.id.we5_layout,R.id.th5_layout,R.id.fr5_layout,R.id.sa5_layout},
                        {R.id.su6_layout,R.id.mo6_layout,R.id.tu6_layout,R.id.we6_layout,R.id.th6_layout,R.id.fr6_layout,R.id.sa6_layout}};
                int week=0;
                for (int[] cells:cellses){
                    week++;
                    int dateofweek=0;
                    for (int cell:cells){
                        dateofweek++;
                        final int date=DateData.getDateonCalendar(year,month,week,dateofweek);
                        if (scaleMonth==0&&date<ndate)
                            ((FrameLayout) calendarview.findViewById(cell)).setOnClickListener(null);
                        else if (date==0)
                            ((FrameLayout) calendarview.findViewById(cell)).setOnClickListener(null);
                        else
                            ((FrameLayout) calendarview.findViewById(cell)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    nowScaleMonth=scaleMonth;
                                    nowDate=date;
                                    setNowTouch(true);
                                }
                            });
                    }
                }
            }

            void InitNumberPicker(){

                int ny=DateData.getYear(et);
                List<String> years = new ArrayList<>();
                for (int i = ny; i < ny+500; i++)
                    years.add(i+context.getString(R.string.Year_unit));

                List<String> months = new ArrayList<>();
                for (int i = 0; i < 12; i++)
                    months.add(textBitmap.getMonthText(2000,i));

                WheelPicker np1=view.findViewById(R.id.today_memo_dialog_re_np1);
                WheelPicker np2=view.findViewById(R.id.today_memo_dialog_re_np2);

                np1.setSelectedItemTextColor(Color.BLACK);
                np2.setSelectedItemTextColor(Color.BLACK);

                if ("yyyy/MM".equals(context.getString(R.string.month_order))){
                    np1.setData(years);
                    np2.setData(months);
                }
                else {
                    np1.setData(months);
                    np2.setData(years);
                }
            }

            void setNumberPicker(){
                picker_month=getMonth();
                picker_year=getYear()-DateData.getYear(et);
                WheelPicker np1=view.findViewById(R.id.today_memo_dialog_re_np1);
                WheelPicker np2=view.findViewById(R.id.today_memo_dialog_re_np2);

                int nm=DateData.getMonth(et);
                if ("yyyy/MM".equals(context.getString(R.string.month_order))){
                    np1.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(WheelPicker picker, Object data, int position) {
                            picker_year=position;
                            if (position==0&&picker_month<nm){
                                np2.setSelectedItemPosition(DateData.getMonth(et),true);
                                picker_month=DateData.getMonth(et);
                            }
                            scaleMonth=picker_month-DateData.getMonth(et)+picker_year*12;
                            setTitle();
                        }
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            np2.setSelectedItemPosition(picker_month);
                            np1.setSelectedItemPosition(picker_year);
                        }
                    },500);
                    np2.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(WheelPicker picker, Object data, int position) {
                            if (picker_year==0&&position<nm){
                                np2.setSelectedItemPosition(DateData.getMonth(et),true);
                                picker_month=DateData.getMonth(et);
                            }
                            else {
                                picker_month=position;
                            }
                            scaleMonth=picker_month-DateData.getMonth(et)+picker_year*12;
                            setTitle();
                        }
                    });
                }
                else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            np1.setSelectedItemPosition(picker_month);
                            np2.setSelectedItemPosition(picker_year);
                        }
                    },500);
                    np1.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(WheelPicker picker, Object data, int position) {
                            if (picker_year==0&&position<nm){
                                np1.setSelectedItemPosition(DateData.getMonth(et),true);
                                picker_month=DateData.getMonth(et);
                            }
                            else {
                                picker_month=position;
                            }
                            scaleMonth=picker_month-DateData.getMonth(et)+picker_year*12;
                            setTitle();
                        }
                    });
                    np2.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(WheelPicker picker, Object data, int position) {
                            picker_year=position;
                            if (position==0&&picker_month<nm){
                                np1.setSelectedItemPosition(DateData.getMonth(et),true);
                                picker_month=DateData.getMonth(et);
                            }
                            scaleMonth=picker_month-DateData.getMonth(et)+picker_year*12;
                            setTitle();
                        }
                    });
                }
            }
        }
        final eF ef=new eF();

        ef.setCalendar();
        ef.setTitle();
        ef.setNowTouch(true);
        ef.setClick();
        ef.InitNumberPicker();

        View ll=view.findViewById(R.id.today_memo_dialog_re_ll);
        View fr=view.findViewById(R.id.today_memo_dialog_re_fr);
        ll.setVisibility(View.GONE);
        fr.setVisibility(View.VISIBLE);

        TextView title=view.findViewById(R.id.today_memo_dialog_re_title);

        View bl=view.findViewById(R.id.today_memo_dialog_re_back_ll);
        View nl=view.findViewById(R.id.today_memo_dialog_re_next_ll);

        if (ef.scaleMonth==0)
            view.findViewById(R.id.today_memo_dialog_re_back).setVisibility(View.INVISIBLE);
        else
            view.findViewById(R.id.today_memo_dialog_re_back).setVisibility(View.VISIBLE);

        view.findViewById(R.id.today_memo_dialog_re_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ef.scaleMonth--;

                if (ef.scaleMonth==0)
                    v.setVisibility(View.INVISIBLE);

                ef.setCalendar();
                ef.setTitle();
                ef.setNowTouch(false);
                ef.setClick();
                ((ViewFlipper)calendarview.findViewById(R.id.calendar_flipper1)).showNext();
            }
        });
        view.findViewById(R.id.today_memo_dialog_re_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.today_memo_dialog_re_back).setVisibility(View.VISIBLE);
                ef.scaleMonth++;
                ef.setCalendar();
                ef.setTitle();
                ef.setNowTouch(false);
                ef.setClick();
                ((ViewFlipper)calendarview.findViewById(R.id.calendar_flipper2)).showNext();
            }
        });
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((ViewFlipper)view.findViewById(R.id.today_memo_dialog_re_vf2)).showNext();
                if (ll.getVisibility()==View.GONE){
                    ll.setVisibility(View.VISIBLE);
                    fr.setVisibility(View.GONE);
                    bl.setVisibility(View.GONE);
                    nl.setVisibility(View.GONE);
                    title.setTextColor(Color.WHITE);
                    title.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue)));
                    ef.setNumberPicker();
                }
                else {
                    ll.setVisibility(View.GONE);
                    fr.setVisibility(View.VISIBLE);
                    bl.setVisibility(View.VISIBLE);
                    nl.setVisibility(View.VISIBLE);
                    title.setTextColor(Color.BLACK);
                    title.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    ef.scaleMonth=ef.picker_month-DateData.getMonth(et)+ef.picker_year*12;
                    ef.setCalendar();
                    ef.setTitle();
                    ef.setNowTouch(false);
                    ef.setClick();
                    if (ef.scaleMonth==0)
                        view.findViewById(R.id.today_memo_dialog_re_back).setVisibility(View.INVISIBLE);
                    else
                        view.findViewById(R.id.today_memo_dialog_re_back).setVisibility(View.VISIBLE);

                }
            }
        });

        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ef.scaleMonth=DateData.getYear()*12+DateData.getMonth()-1990*12;
                ef.nowScaleMonth=DateData.getYear()*12+DateData.getMonth()-1990*12;
                ef.nowDate=DateData.getDateOfMonth();
                ef.setCalendar();
                ef.setTitle();
                ef.setNowTouch(true);
                ef.setClick();
                ef.InitNumberPicker();
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = ef.getnowYear();
                int month = ef.getnowMonth();
                Calendar c = Calendar.getInstance();
                c.set(year, month, ef.nowDate, 12, 00, 00);
                PendingIntent configPendingIntent = todays_memo.getPendingSelfIntentFromCalendar(context, todays_memo.CP, c.getTimeInMillis());
                PendingIntent configPendingIntent2 = Schedule.getPendingSelfIntentFromCalendar(context, Schedule.CP, c.getTimeInMillis());
                try {
                    configPendingIntent.send();
                    configPendingIntent2.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });


    }
}