package todays_memo;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.aigestudio.wheelpicker.WheelPicker;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import calendar.CalendarData;
import calendar.DateData;
import calendar.R;
import calendar.TextBitmap;
import save.ObjectStorage;


public class Memo_Activity extends BaseActivity {

    public static final int REPEAT_EVERY_DAY=0;//repaeat time
    public static final int REPEAT_EVERY_WEEK=1;//repaeat time , week condition
    public static final int REPEAT_EVERY_MONTH=2;//repaeat time , is by date , month condition
    public static final int REPEAT_EVERY_YEAR=3;//repaeat time , year condition , is by date , month condition
    public static final int REPEAT_EVERY_MONTH_NO_REPEAT=4;//is by date , month condition
    public static final int REPEAT_EVERY_YEAR_NO_VF=5;//repaeat time , year condition
    public static final int TO_DO_IN_THE_DAY=6;
    public static final int TO_DO_IN_THE_WEEK=7;
    public static final int TO_DO_IN_THE_MONTH=8;
    public static final int START_DAY=9;// yyyy/MM/dd
    public static final int REPEAT_EVERY_WEEK_IN_WEEK=11;//repaeat time
    public static final int REPEAT_EVERY_MONTH_IN_WEEK=12;//repaeat time , month condition
    public static final int REPEAT_EVERY_YEAR_IN_WEEK=13;//repaeat time , year condition , month condition
    public static final int REPEAT_EVERY_MONTH_IN_WEEK_NO_REPEAT=14;//month condition
    public static final int REPEAT_EVERY_YEAR_IN_WEEK_NO_VF=15;//repaeat time , year condition
    public static final int REPEAT_EVERY_MONTH_IN_MONTH=16;//repaeat time
    public static final int REPEAT_EVERY_YEAR_IN_MONTH=17;//repaeat time , year condition
    public static final int NO_REPEAT=18;
    public static final int END_DAY=19;// yyyy/MM/dd
    public static final int DO_NOT_SHOW_ON_CHECKED=20;// bool



    public static final int SUNDAY=1;
    public static final int MONDAY=1<<1;
    public static final int TUESDAY=1<<2;
    public static final int WEDNESDAY=1<<3;
    public static final int THURSDAY=1<<4;
    public static final int FRIDAY=1<<5;
    public static final int SATURDAY=1<<6;

    public static final int FIRST_WEEK=1;
    public static final int SECOND_WEEK=1<<1;
    public static final int THIRD_WEEK=1<<2;
    public static final int FOURTH_WEEK=1<<3;
    public static final int FIFTH_WEEK=1<<4;
    public static final int LAST_WEEK=1<<5;

    public static final int DATE=0;
    public static final int WEEK=1;
    public static final int MONTH=2;



    Dialog dialog;
    View keysuport;
    EditText editText;
    Map<View, Map<Integer,String>> itemViews=new LinkedHashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_);

        attachKeyboardListeners(findViewById(R.id.rootLayout_tm));
        Context context=Memo_Activity.this;



        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dialogWidth = (int) (metrics.widthPixels * 1.0);
        dialog = new Dialog(context,R.style.MyDialogTheme);
        View view=View.inflate(this, R.layout.todays_memo_dialog,null);
        dialog.setContentView(view);

        final EditText editText_day = view.findViewById(R.id.today_memo_dialog_day);
        final LinearLayout items=view.findViewById(R.id.today_memo_dialog_items);
        final TextView now_day=view.findViewById(R.id.today_memo_dialog_now);
        final TextView button_month=view.findViewById(R.id.today_memo_dialog_now_month);
        final TextView button_week=view.findViewById(R.id.today_memo_dialog_now_week);
        final TextView button_day=view.findViewById(R.id.today_memo_dialog_now_day);
        final TextView addEditText=view.findViewById(R.id.today_memo_dialog_add);
        final Button button_ok=view.findViewById(R.id.today_memo_dialog_ok);
        final Button button_cancel=view.findViewById(R.id.today_memo_dialog_cancel);
        final ViewFlipper window=view.findViewById(R.id.today_memo_dialog_add_window);
        final ViewFlipper window2=view.findViewById(R.id.today_memo_dialog_add_window2);
        final ImageView im=view.findViewById(R.id.today_memo_dialog_im);
        keysuport=view.findViewById(R.id.today_memo_dialog_ks);

        Map<String,String> map= ObjectStorage.get(todays_memo.MEMO, Map.class,context);
        final long t=ObjectStorage.get(todays_memo.TODAYS_MEMO,Long.class,context);

        Set<String> removeSet=new HashSet<>();

        if (map!=null){
            if (map.containsKey(DateData.getNowDate(t)))
                editText_day.setText(map.get(DateData.getNowDate(t)));
            if (map.containsKey(DateData.getNowMonth(t))) {
                removeSet.add(DateData.getNowMonth(t));
                EditText et=addItemView(MONTH,view,context,t,map.get(DateData.getNowMonth(t)),false,null);
                if (et!=null) {
                    et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                editText = (EditText) v;
                            }
                        }
                    });
                }
            }
            if (map.containsKey(DateData.getNowWeek(t))) {
                removeSet.add(DateData.getNowWeek(t));
                EditText et=addItemView(WEEK,view,context,t,map.get(DateData.getNowWeek(t)),false,null);
                if (et!=null) {
                    et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                editText = (EditText) v;
                            }
                        }
                    });
                }
            }

            for (Map.Entry<String,String> entry:map.entrySet()){
                if (!entry.getKey().contains("{"))
                    continue;
                if (ComparisonDate.isContainCondition(DateData.getNowDate(t),entry.getKey())){
                    removeSet.add(entry.getKey());
                    EditText et=addItemView(ComparisonDate.getMode(entry.getKey()),view,context,t,entry.getValue(),false,entry.getKey());
                    if (et!=null) {
                        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    editText = (EditText) v;
                                }
                            }
                        });
                    }
                }
            }
        }

        now_day.setText(DateData.getNowDate(t,context));
        /*button_month.setText(TextBitmap.getMonthText(
                DateData.getYear(t), DateData.getMonth(t), context, null
        ));
        button_week.setText(TextBitmap.getWeekText(DateData.getWeekOfMonth(t)-1,context));*/

        ((TextView)view.findViewById(R.id.today_memo_dialog_today_text)).setText(getDayText(context,DateData.getDateOfMonth(t)-1));

        Log.d(",","k");

        /*if ("".equals(editText_month.getText().toString())){
            editText_month.setVisibility(View.GONE);
            button_month.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.gray)));
            button_month.setTextColor(Color.BLACK);
        }
        if ("".equals(editText_week.getText().toString())){
            editText_week.setVisibility(View.GONE);
            button_week.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.gray)));
            button_week.setTextColor(Color.BLACK);
        button_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_month.getVisibility()==View.GONE){
                    editText_month.setVisibility(View.VISIBLE);
                    button_month.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue)));
                    button_month.setTextColor(Color.WHITE);
                }
                else {
                    editText_month.setVisibility(View.GONE);
                    button_month.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.gray)));
                    button_month.setTextColor(Color.BLACK);
                }
            }
        });
        button_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_week.getVisibility()==View.GONE){
                    editText_week.setVisibility(View.VISIBLE);
                    button_week.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue)));
                    button_week.setTextColor(Color.WHITE);
                }
                else {
                    editText_week.setVisibility(View.GONE);
                    button_week.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.gray)));
                    button_week.setTextColor(Color.BLACK);
                }
            }
        });
        */
        im.setVisibility(View.GONE);
        window.setVisibility(View.GONE);
        window2.setVisibility(View.GONE);



        button_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et=addItemView(MONTH,view,context,t,"",true,null);
                if (et!=null) {
                    et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                editText = (EditText) v;
                            }
                        }
                    });
                }
                im.setVisibility(View.GONE);
                window.setVisibility(View.GONE);
            }
        });
        button_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et=addItemView(WEEK,view,context,t,"",true,null);
                if (et!=null) {
                    et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                editText = (EditText) v;
                            }
                        }
                    });
                }
                im.setVisibility(View.GONE);
                window.setVisibility(View.GONE);
            }
        });
        button_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et=addItemView(DATE,view,context,t,"",true,null);
                if (et!=null) {
                    et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                editText = (EditText) v;
                            }
                        }
                    });
                }
                im.setVisibility(View.GONE);
                window.setVisibility(View.GONE);
            }
        });
        addEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                im.setVisibility(View.VISIBLE);
                window.setVisibility(View.VISIBLE);
                window.showNext();
            }
        });
        view.findViewById(R.id.today_memo_dialog_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                im.setVisibility(View.GONE);
                window.setVisibility(View.GONE);
            }
        });

        im.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
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
                Map<String,String> map=ObjectStorage.get(todays_memo.MEMO, Map.class,context);
                Map<String,String> addMap=new HashMap<>();
                if (map==null)
                    map = new HashMap<>();
                String text_day = editText_day.getText().toString();
                if (!"".equals(text_day))
                    addMap.put(DateData.getNowDate(t),text_day);
                else
                    removeSet.add(DateData.getNowDate(t));

                for (Map.Entry<View,Map<Integer,String>> ventry:itemViews.entrySet()){
                    String text = ((EditText)ventry.getKey().findViewById(R.id.today_memo_dialog_data)).getText().toString();

                    String keyText;
                    Map<Integer,String> set=ventry.getValue();
                    if (set.size()>2){

                        List<Integer> list=new ArrayList<>(set.keySet());
                        Collections.sort(list);
                        Map<Integer,String> nset=new LinkedHashMap<>();
                        for (int i:list)
                            nset.put(i,set.get(i));
                        keyText=ObjectStorage.getString(nset);

                    }
                    else if (set.containsKey(TO_DO_IN_THE_DAY)){
                        keyText=DateData.getNowDate(t);
                    }
                    else if (set.containsKey(TO_DO_IN_THE_WEEK)){
                        keyText=DateData.getNowWeek(t);
                    }
                    else {
                        keyText=DateData.getNowMonth(t);
                    }


                    if (!"".equals(text)){
                        if (addMap.containsKey(keyText)){
                            text=addMap.get(keyText)+"\n"+text;
                        }
                        addMap.put(keyText,text);
                    }
                    else {
                        removeSet.add(keyText);
                    }

                }

                removeSet.removeAll(addMap.keySet());
                map.putAll(addMap);
                for (String i:removeSet)
                    map.remove(i);

                /*String text_month= editText_month.getText().toString();
                if (!"".equals(text_month))
                    map.put(DateData.getNowMonth(t),text_month);
                else
                    map.remove(DateData.getNowMonth(t));

                String text_week= editText_week.getText().toString();
                if (!"".equals(text_week))
                    map.put(DateData.getNowWeek(t),text_week);
                else
                    map.remove(DateData.getNowWeek(t));
                */

                ObjectStorage.save(map,todays_memo.MEMO,context);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        PendingIntent configPendingIntent = todays_memo.getPendingSelfIntentFromCalendar(context,todays_memo.CP,t);
                        try {
                            configPendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                });
                finish();
            }
        });



        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = dialogWidth;
        dialog.getWindow().setAttributes(lp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dialog.getWindow().setDecorFitsSystemWindows(true);
        }
        else {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                editText_day.requestFocus();
                editText_day.setSelection(editText_day.getText().length());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText_day, InputMethodManager.SHOW_IMPLICIT);
            }
        },500);
        final int h=150;
        editText_day.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    editText = (EditText) v;
                }
            }
        });
        /*editText_month.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    editText = (EditText) v;
                }
            }
        });
        editText_week.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    editText = (EditText) v;
                }
            }
        });*/



        RecyclerView rv = (RecyclerView) view.findViewById(R.id.today_memo_dialog_ks);
        DialogRecycleViewAdapter adapter = new DialogRecycleViewAdapter(context,new int[]{
                R.string.Call,R.string.Do,R.string.Go,R.string.Email,R.string.Meet,R.string.Buy,R.string.Clean,R.string.Exercise,
                R.string.Ask,R.string.Read,R.string.Work,R.string.Pick,R.string.Pay,R.string.Study,R.string.Sleep,R.string.Cancel
        });
        adapter.setOnItemClickLisner(new DialogRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String text) {
                final EditText et=editText;
                if (et==null)
                    return;
                int position=et.getSelectionStart();
                try {
                    et.getText().insert(position,text);
                }catch (Exception e){

                }
            }
        });

        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);

        rv.setHasFixedSize(true);

        rv.setLayoutManager(layoutManager);

        rv.setAdapter(adapter);

    }

    @Override
    protected void onShowKeyboard(int keyboardHeight) {
        if (keysuport!=null)
            keysuport.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onHideKeyboard() {
        if (keysuport!=null)
            keysuport.setVisibility(View.GONE);

    }
    public static String getDayText(Context context,int day){
        int[] days=new int[]{R.string.day1,R.string.day2,R.string.day3,R.string.day4,R.string.day5,
                R.string.day6,R.string.day7,R.string.day8,R.string.day9,R.string.day10,R.string.day11,
                R.string.day12,R.string.day13,R.string.day14,R.string.day15,R.string.day16,R.string.day17,
                R.string.day18,R.string.day19,R.string.day20,R.string.day21,R.string.day22,R.string.day23,
                R.string.day24,R.string.day25,R.string.day26,R.string.day27,R.string.day28,R.string.day29,
                R.string.day30,R.string.day31};
        try {
            return context.getString(days[day]);
        }catch (Exception e){
            return context.getString(R.string.Day);
        }
    }
    public void setmodeWindow(int mode,final View view,Context context,final View item,final long t,boolean ontouchplus){
        LinearLayout layout=view.findViewById(R.id.today_memo_dialog_mode_window);
        layout.setVisibility(View.VISIBLE);
        layout.removeAllViews();
        TextView done=view.findViewById(R.id.today_memo_dialog_add_mode);
        done.setVisibility(View.VISIBLE);
        done.setText(R.string.done);
        done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
        view.findViewById(R.id.today_memo_dialog_back2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.GONE);
                view.findViewById(R.id.today_memo_dialog_mode_list).setVisibility(View.VISIBLE);
                done.setVisibility(View.GONE);
                ((ViewFlipper)view.findViewById(R.id.today_memo_dialog_mode_list_vf)).showNext();
                view.findViewById(R.id.today_memo_dialog_back2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                    }
                });
            }
        });
        view.findViewById(R.id.today_memo_dialog_mode_list).setVisibility(View.GONE);
        class F{
            void removeOtherTask(View v){
                if (itemViews.containsKey(v)){
                    itemViews.get(v).remove(REPEAT_EVERY_DAY);
                    itemViews.get(v).remove(REPEAT_EVERY_WEEK);
                    itemViews.get(v).remove(REPEAT_EVERY_MONTH);
                    itemViews.get(v).remove(REPEAT_EVERY_YEAR);
                    itemViews.get(v).remove(REPEAT_EVERY_YEAR_NO_VF);
                    itemViews.get(v).remove(REPEAT_EVERY_MONTH_NO_REPEAT);
                    itemViews.get(v).remove(REPEAT_EVERY_WEEK_IN_WEEK);
                    itemViews.get(v).remove(REPEAT_EVERY_MONTH_IN_WEEK);
                    itemViews.get(v).remove(REPEAT_EVERY_MONTH_IN_WEEK_NO_REPEAT);
                    itemViews.get(v).remove(REPEAT_EVERY_YEAR_IN_WEEK);
                    itemViews.get(v).remove(REPEAT_EVERY_YEAR_IN_WEEK_NO_VF);
                    itemViews.get(v).remove(REPEAT_EVERY_MONTH_IN_MONTH);
                    itemViews.get(v).remove(REPEAT_EVERY_YEAR_IN_MONTH);

                }
            }
        }
        F f=new F();

        TextView back2=view.findViewById(R.id.today_memo_dialog_back2);
        back2.setText(R.string.back);
        if (!ontouchplus){
            if (mode!=REPEAT_EVERY_MONTH_NO_REPEAT&&
                    mode!=REPEAT_EVERY_MONTH_IN_WEEK_NO_REPEAT){
                back2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                        updateItemBar(view,context,item,t);
                    }
                });
                back2=new TextView(context);
            }
        }

        switch (mode){
            case REPEAT_EVERY_DAY:{
                View modeview=View.inflate(context, R.layout.today_memo_rd,null);
                layout.addView(modeview);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_rd_vf)).showNext();
                EditText myEditText = modeview.findViewById(R.id.today_memo_dialog_rd_num);
                myEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                myEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
                myEditText.setText(itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_DAY,"1"));
                TextView textView=modeview.findViewById(R.id.today_memo_dialog_rd_unit);

                try {
                    int u=Integer.valueOf(myEditText.getText().toString());
                    if (u<=1){
                        textView.setText(R.string.day);
                    }
                    else {
                        textView.setText(R.string.days);
                    }
                }catch (Exception e){}

                myEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int u=0;
                        try {
                            u=Integer.valueOf(s.toString());
                        }catch (Exception e){}
                        if (u<=1){
                            textView.setText(R.string.day);
                        }
                        else {
                            textView.setText(R.string.days);
                        }
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myEditText.requestFocus();
                    }
                },200);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                        String text=myEditText.getText().toString();
                        if ("".equals(text)||Integer.valueOf(text).equals(0)){
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_DAY);
                        }
                        else {
                            f.removeOtherTask(item);
                            itemViews.getOrDefault(item,new HashMap<>()).put(REPEAT_EVERY_DAY,text);
                        }
                        updateItemBar(view,context,item,t);
                    }
                });
                break;
            }
            case REPEAT_EVERY_WEEK:{
                View modeview=View.inflate(context, R.layout.today_memo_rw,null);
                layout.addView(modeview);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_rw_vf)).showNext();
                EditText myEditText = modeview.findViewById(R.id.today_memo_dialog_rw_num);
                myEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                myEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());

                TextView textView=modeview.findViewById(R.id.today_memo_dialog_rw_unit);
                myEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int u=0;
                        try {
                            u=Integer.valueOf(s.toString());
                        }catch (Exception e){}
                        if (u<=1){
                            textView.setText(R.string.week);
                        }
                        else {
                            textView.setText(R.string.weeks);
                        }
                    }
                });

                final int[] days=new int[]{R.id.today_memo_dialog_rw_su,R.id.today_memo_dialog_rw_mo,R.id.today_memo_dialog_rw_tu,
                        R.id.today_memo_dialog_rw_we,R.id.today_memo_dialog_rw_th,R.id.today_memo_dialog_rw_fr,R.id.today_memo_dialog_rw_sa};

                final int[] days_b=new int[]{R.id.today_memo_dialog_rw_su_b,R.id.today_memo_dialog_rw_mo_b,R.id.today_memo_dialog_rw_tu_b,
                        R.id.today_memo_dialog_rw_we_b,R.id.today_memo_dialog_rw_th_b,R.id.today_memo_dialog_rw_fr_b,R.id.today_memo_dialog_rw_sa_b};

                final int[] days_v=new int[]{R.id.today_memo_dialog_rw_su_v,R.id.today_memo_dialog_rw_mo_v,R.id.today_memo_dialog_rw_tu_v,
                        R.id.today_memo_dialog_rw_we_v,R.id.today_memo_dialog_rw_th_v,R.id.today_memo_dialog_rw_fr_v,R.id.today_memo_dialog_rw_sa_v};

                final boolean[] bs=new boolean[]{false,false,false,false,false,false,false};

                final int[] ds=new int[]{SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY};

                String text=itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_WEEK,"");
                if (!"".equals(text)) {
                    try {
                        myEditText.setText(text.split(",")[0]);
                        int u=Integer.valueOf(text.split(",")[0]);

                        if (u<=1){
                            textView.setText(R.string.week);
                        }
                        else {
                            textView.setText(R.string.weeks);
                        }
                        int bsOnText=Integer.valueOf(text.split(",")[1]);
                        for (int i=0;i<7;i++){
                            bs[i]=(ds[i]&bsOnText)>0;
                        }
                    }catch (Exception e){

                    }
                }
                else {
                    int d=DateData.getDateOfWeek(t)-1;
                    if (d>=0&&d<7)
                        bs[d]=true;
                }

                for (int i=0;i<7;i++){
                    final int day=i;
                    final RadioButton radioButton=modeview.findViewById(days_b[day]);
                    View.OnClickListener onClickListener=new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bs[day]=!bs[day];
                            radioButton.setChecked(bs[day]);
                            ((ViewFlipper)modeview.findViewById(days_v[day])).showNext();
                        }
                    };
                    modeview.findViewById(days_b[day]).setOnClickListener(onClickListener);
                    radioButton.setChecked(bs[day]);
                    modeview.findViewById(days[day]).setOnClickListener(onClickListener);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myEditText.requestFocus();
                    }
                },200);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                        String text=myEditText.getText().toString();
                        int d=0;
                        for (int i=0;i<7;i++){
                            if (bs[i])
                                d=d|ds[i];
                        }
                        if ("".equals(text)||Integer.valueOf(text).equals(0)||d==0){
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_WEEK);
                        }
                        else {
                            f.removeOtherTask(item);
                            itemViews.getOrDefault(item,new HashMap<>()).put(REPEAT_EVERY_WEEK,text+","+d);
                        }
                        updateItemBar(view,context,item,t);

                    }
                });
                break;
            }
            case REPEAT_EVERY_MONTH:{
                View modeview=View.inflate(context, R.layout.today_memo_rm,null);
                layout.addView(modeview);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_rm_vf)).showNext();
                EditText myEditText = modeview.findViewById(R.id.today_memo_dialog_rm_num);
                myEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                myEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
                myEditText.setText(itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_MONTH,"1"));

                TextView unit=modeview.findViewById(R.id.today_memo_dialog_rm_unit);
                myEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int u=0;
                        try {
                            u=Integer.valueOf(s.toString());
                        }catch (Exception e){}
                        if (u<=1){
                            unit.setText(R.string.month);
                        }
                        else {
                            unit.setText(R.string.months);
                        }
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myEditText.requestFocus();
                    }
                },200);

                int[][] dses=new int[][]{{R.id.t_1st_su,R.id.t_1st_mo,R.id.t_1st_tu,R.id.t_1st_we,R.id.t_1st_th,R.id.t_1st_fr,R.id.t_1st_sa},
                        {R.id.t_2nd_su,R.id.t_2nd_mo,R.id.t_2nd_tu,R.id.t_2nd_we,R.id.t_2nd_th,R.id.t_2nd_fr,R.id.t_2nd_sa},
                        {R.id.t_3rd_su,R.id.t_3rd_mo,R.id.t_3rd_tu,R.id.t_3rd_we,R.id.t_3rd_th,R.id.t_3rd_fr,R.id.t_3rd_sa},
                        {R.id.t_4th_su,R.id.t_4th_mo,R.id.t_4th_tu,R.id.t_4th_we,R.id.t_4th_th,R.id.t_4th_fr,R.id.t_4th_sa},
                        {R.id.t_5th_su,R.id.t_5th_mo,R.id.t_5th_tu,R.id.t_5th_we,R.id.t_5th_th,R.id.t_5th_fr,R.id.t_5th_sa},
                        {R.id.t_last_su,R.id.t_last_mo,R.id.t_last_tu,R.id.t_last_we,R.id.t_last_th,R.id.t_last_fr,R.id.t_last_sa}};
                int[][] vfses=new int[][]{{R.id.t_1st_su_vf,R.id.t_1st_mo_vf,R.id.t_1st_tu_vf,R.id.t_1st_we_vf,R.id.t_1st_th_vf,R.id.t_1st_fr_vf,R.id.t_1st_sa_vf},
                        {R.id.t_2nd_su_vf,R.id.t_2nd_mo_vf,R.id.t_2nd_tu_vf,R.id.t_2nd_we_vf,R.id.t_2nd_th_vf,R.id.t_2nd_fr_vf,R.id.t_2nd_sa_vf},
                        {R.id.t_3rd_su_vf,R.id.t_3rd_mo_vf,R.id.t_3rd_tu_vf,R.id.t_3rd_we_vf,R.id.t_3rd_th_vf,R.id.t_3rd_fr_vf,R.id.t_3rd_sa_vf},
                        {R.id.t_4th_su_vf,R.id.t_4th_mo_vf,R.id.t_4th_tu_vf,R.id.t_4th_we_vf,R.id.t_4th_th_vf,R.id.t_4th_fr_vf,R.id.t_4th_sa_vf},
                        {R.id.t_5th_su_vf,R.id.t_5th_mo_vf,R.id.t_5th_tu_vf,R.id.t_5th_we_vf,R.id.t_5th_th_vf,R.id.t_5th_fr_vf,R.id.t_5th_sa_vf},
                        {R.id.t_last_su_vf,R.id.t_last_mo_vf,R.id.t_last_tu_vf,R.id.t_last_we_vf,R.id.t_last_th_vf,R.id.t_last_fr_vf,R.id.t_last_sa_vf}};
                Set<Long> set=new HashSet<>();

                ViewFlipper vf2=modeview.findViewById(R.id.today_memo_dialog_rm_vf2);
                ViewFlipper vf3=modeview.findViewById(R.id.today_memo_dialog_rm_vf3);

                GridView gv=modeview.findViewById(R.id.today_memo_dialog_rm_gv);
                LinearLayout lv=modeview.findViewById(R.id.today_memo_dialog_rm_li);

                RadioButton rb1 = modeview.findViewById(R.id.today_memo_dialog_rm_b1);
                RadioButton rb2 = modeview.findViewById(R.id.today_memo_dialog_rm_b2);

                rb1.setChecked(true);
                rb2.setChecked(false);
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(rb1,1,14
                        ,1, TypedValue.COMPLEX_UNIT_SP);
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(rb2,1,14
                        ,1, TypedValue.COMPLEX_UNIT_SP);

                gv.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);

                RadioGroup radioGroup = modeview.findViewById(R.id.today_memo_dialog_rm_rg);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (R.id.today_memo_dialog_rm_b1!=checkedId) {
                            gv.setVisibility(View.GONE);
                            lv.setVisibility(View.VISIBLE);
                            vf2.showNext();
                        }
                        else {
                            gv.setVisibility(View.VISIBLE);
                            lv.setVisibility(View.GONE);
                            vf3.showNext();
                        }
                    }
                });


                GridAdapter gridAdapter=new GridAdapter(context,DateData.getDateOfMonth(t));
                gv.setAdapter(gridAdapter);

                String text=itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_MONTH,"");
                if (!"".equals(text)) {
                    try {
                        myEditText.setText(text.split(",")[0]);

                        int u=Integer.valueOf(text.split(",")[0]);

                        if (u<=1){
                            unit.setText(R.string.month);
                        }
                        else {
                            unit.setText(R.string.months);
                        }

                        boolean isgv=Boolean.valueOf(text.split(",")[1]);

                        if (isgv){
                            int bsOnText=Integer.valueOf(text.split(",")[2]);
                            if (bsOnText!=0)
                                gridAdapter.set.clear();
                            for (int i=0;i<31;i++){
                                if ((bsOnText&(1<<i))>0){
                                    gridAdapter.set.add(i+1);
                                }
                            }
                            gridAdapter.notifyDataSetChanged();
                        }
                        else {

                            gv.setVisibility(View.GONE);
                            lv.setVisibility(View.VISIBLE);
                            radioGroup.check(R.id.today_memo_dialog_rm_b2);
                            long bsOnText=Long.valueOf(text.split(",")[2]);
                            for (int i=0;i<49;i++){
                                if ((bsOnText&(1l<<i))>0){
                                    set.add(1l<<i);
                                }
                            }
                        }


                    }catch (Exception e){}
                }

                int i=1;
                for (int[] ds:dses){
                    int j=1;
                    for (int d:ds){
                        long l=1;
                        l=l<<((i-1)*7+(j-1));
                        final long fl = l;

                        TextView textView=view.findViewById(d);
                        final ViewFlipper viewFlipper=view.findViewById(vfses[i-1][j-1]);

                        if ((DateData.getDateOfWeek(t)==j)&&(DateData.getDayOfWeekOnMonth(t)==i)&&(set.isEmpty())){
                            set.add(fl);
                        }

                        if (set.contains(fl)){
                            textView.setBackgroundColor(context.getColor(R.color.red));
                            textView.setTextColor(context.getColor(R.color.white));
                        }

                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewFlipper.showNext();
                                if (set.contains(fl)){
                                    set.remove(fl);
                                    textView.setBackgroundColor(Color.argb(0,0,0,0));
                                    textView.setTextColor(context.getColor(R.color.black));
                                }
                                else {
                                    set.add(fl);
                                    textView.setBackgroundColor(context.getColor(R.color.red));
                                    textView.setTextColor(context.getColor(R.color.white));
                                }
                            }
                        });
                        j++;
                    }
                    i++;
                }

                int number = getWindowManager()
                        .getDefaultDisplay().getWidth();

                int colum=(number-2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_layout_padding)
                        -2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_layout_mrgin))/
                        (context.getResources().getDimensionPixelSize(R.dimen.today_memo_rm_grid_im_size)+2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_rm_grid_ma_size));
                if (colum<1)
                    colum=1;
                gv.setNumColumns(colum);

                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        gridAdapter.setdate(position+1);
                        gridAdapter.notifyDataSetChanged();
                        ViewFlipper vf=view.findViewById(R.id.today_memo_dialog_rm_grid_vf);
                        vf.showNext();
                    }
                });

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                        String text=myEditText.getText().toString();
                        if ("".equals(text)||Integer.valueOf(text).equals(0)){
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_MONTH);
                        }
                        else {
                            long d=0;
                            if (gv.getVisibility()==View.VISIBLE) {
                                text += "," + true;
                                for (int i:gridAdapter.set){
                                    d=d|(1l<<(i-1));
                                }
                            }
                            else {
                                text += "," + false;
                                for (long i:set){
                                    d=d|i;
                                }
                            }
                            if (d==0){
                                itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_MONTH);
                            }
                            else {
                                f.removeOtherTask(item);
                                itemViews.getOrDefault(item, new HashMap<>()).put(REPEAT_EVERY_MONTH, text+","+d);
                            }
                        }
                        updateItemBar(view,context,item,t);

                    }
                });
                break;
            }
            case REPEAT_EVERY_YEAR:{
                done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue)));
                done.setText(R.string.next);

                View modeview=View.inflate(context, R.layout.today_memo_ry,null);
                layout.addView(modeview);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_ry_vf)).showNext();
                EditText myEditText = modeview.findViewById(R.id.today_memo_dialog_ry_num);
                myEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                myEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());

                GridView gv=modeview.findViewById(R.id.today_memo_dialog_ry_gv);
                GridAdapter2 gridAdapter=new GridAdapter2(context,DateData.getMonth(t));
                gv.setAdapter(gridAdapter);

                int number = getWindowManager()
                        .getDefaultDisplay().getWidth();
                int colum=(number-2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_layout_padding)
                        -2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_layout_mrgin))/
                        (context.getResources().getDimensionPixelSize(R.dimen.today_memo_ry_grid_im_width_size)+
                                2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_ry_grid_ma_size));
                if (colum<1)
                    colum=1;
                gv.setNumColumns(colum);

                TextView textView=modeview.findViewById(R.id.today_memo_dialog_ry_unit);
                myEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int u=0;
                        try {
                            u=Integer.valueOf(s.toString());
                        }catch (Exception e){}

                        if ("".equals(s.toString())||u==0||gridAdapter.set.isEmpty()){
                            done.setText(R.string.done);
                            done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
                        }
                        else {
                            done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue)));
                            done.setText(R.string.next);
                        }


                        if (u<=1){
                            textView.setText(R.string.year);
                        }
                        else {
                            textView.setText(R.string.years);
                        }
                    }
                });

                String text=itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_YEAR,"");
                if (!"".equals(text)) {
                    try {
                        myEditText.setText(text.split(",")[0]);
                        int u=Integer.valueOf(text.split(",")[0]);

                        if (u<=1){
                            textView.setText(R.string.year);
                        }
                        else {
                            textView.setText(R.string.years);
                        }
                        int bsOnText=Integer.valueOf(text.split(",")[1]);
                        if (bsOnText!=0)
                            gridAdapter.set.clear();
                        for (int i=0;i<12;i++){
                            if ((bsOnText&(1<<i))>0){
                                gridAdapter.set.add(i+1);
                            }
                        }
                        gridAdapter.notifyDataSetChanged();
                    }catch (Exception e){

                    }
                }

                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        gridAdapter.setmonth(position+1);
                        gridAdapter.notifyDataSetChanged();
                        ViewFlipper vf=view.findViewById(R.id.today_memo_dialog_ry_grid_vf);
                        vf.showNext();
                        if ("".equals(myEditText.getText().toString())||Integer.valueOf(myEditText.getText().toString()).equals(0)||
                                gridAdapter.set.isEmpty()){
                            done.setText(R.string.done);
                            done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
                        }
                        else {
                            done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue)));
                            done.setText(R.string.next);
                        }
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myEditText.requestFocus();
                    }
                },200);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String text=myEditText.getText().toString();
                        int d=0;
                        for (int i:gridAdapter.set){
                            d=d|(1<<(i-1));
                        }
                        if ("".equals(text)||Integer.valueOf(text).equals(0)||d==0){
                            view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                            view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_YEAR);
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_MONTH_NO_REPEAT);
                            updateItemBar(view,context,item,t);
                        }
                        else {
                            itemViews.getOrDefault(item,new HashMap<>()).put(REPEAT_EVERY_YEAR_NO_VF,text+","+d);
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_MONTH_NO_REPEAT);
                            setmodeWindow(REPEAT_EVERY_MONTH_NO_REPEAT, view, context, item, t,ontouchplus);
                        }
                    }
                });
                break;
            }
            case REPEAT_EVERY_YEAR_NO_VF:{
                done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue)));
                done.setText(R.string.next);

                back2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layout.setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_mode_list).setVisibility(View.VISIBLE);
                        done.setVisibility(View.GONE);
                        ((ViewFlipper)view.findViewById(R.id.today_memo_dialog_mode_list_vf)).showNext();
                        itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_YEAR_NO_VF);
                        itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_MONTH_NO_REPEAT);
                        view.findViewById(R.id.today_memo_dialog_back2).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                                view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                            }
                        });
                    }
                });

                View modeview=View.inflate(context, R.layout.today_memo_ry,null);
                layout.addView(modeview);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_ry_vf)).setInAnimation(context,R.anim.slide_in_left);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_ry_vf)).showNext();
                EditText myEditText = modeview.findViewById(R.id.today_memo_dialog_ry_num);
                myEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                myEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());

                GridView gv=modeview.findViewById(R.id.today_memo_dialog_ry_gv);
                GridAdapter2 gridAdapter=new GridAdapter2(context,DateData.getMonth(t));
                gv.setAdapter(gridAdapter);

                int number = getWindowManager()
                        .getDefaultDisplay().getWidth();
                int colum=(number-2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_layout_padding)
                        -2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_layout_mrgin))/
                        (context.getResources().getDimensionPixelSize(R.dimen.today_memo_ry_grid_im_width_size)+
                                2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_ry_grid_ma_size));
                if (colum<1)
                    colum=1;
                gv.setNumColumns(colum);

                TextView textView=modeview.findViewById(R.id.today_memo_dialog_ry_unit);

                myEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int u=0;
                        try {
                            u=Integer.valueOf(s.toString());
                        }catch (Exception e){}

                        if ("".equals(s.toString())||u==0||gridAdapter.set.isEmpty()){
                            done.setText(R.string.done);
                            done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
                        }
                        else {
                            done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue)));
                            done.setText(R.string.next);
                        }


                        if (u<=1){
                            textView.setText(R.string.year);
                        }
                        else {
                            textView.setText(R.string.years);
                        }
                    }
                });

                String text=itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_YEAR_NO_VF,"");
                if (!"".equals(text)) {
                    try {
                        myEditText.setText(text.split(",")[0]);
                        int u=Integer.valueOf(text.split(",")[0]);

                        if (u<=1){
                            textView.setText(R.string.year);
                        }
                        else {
                            textView.setText(R.string.years);
                        }
                        int bsOnText=Integer.valueOf(text.split(",")[1]);
                        if (bsOnText!=0)
                            gridAdapter.set.clear();
                        for (int i=0;i<12;i++){
                            if ((bsOnText&(1<<i))>0){
                                gridAdapter.set.add(i+1);
                            }
                        }
                        gridAdapter.notifyDataSetChanged();
                    }catch (Exception e){

                    }
                }

                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        gridAdapter.setmonth(position+1);
                        gridAdapter.notifyDataSetChanged();
                        ViewFlipper vf=view.findViewById(R.id.today_memo_dialog_ry_grid_vf);
                        vf.showNext();
                        if ("".equals(myEditText.getText().toString())||Integer.valueOf(myEditText.getText().toString()).equals(0)||
                                gridAdapter.set.isEmpty()){
                            done.setText(R.string.done);
                            done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
                        }
                        else {
                            done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue)));
                            done.setText(R.string.next);
                        }
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myEditText.requestFocus();
                    }
                },200);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String text=myEditText.getText().toString();
                        int d=0;
                        for (int i:gridAdapter.set){
                            d=d|(1<<(i-1));
                        }
                        if ("".equals(text)||Integer.valueOf(text).equals(0)||d==0){
                            view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                            view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_YEAR);
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_MONTH_NO_REPEAT);
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_YEAR_NO_VF);
                            updateItemBar(view,context,item,t);

                        }
                        else {
                            itemViews.getOrDefault(item,new HashMap<>()).put(REPEAT_EVERY_YEAR_NO_VF,text+","+d);
                            setmodeWindow(REPEAT_EVERY_MONTH_NO_REPEAT, view, context, item, t,ontouchplus);
                        }
                    }
                });
                break;
            }
            case REPEAT_EVERY_MONTH_NO_REPEAT:{


                View modeview=View.inflate(context, R.layout.today_memo_rm,null);
                modeview.findViewById(R.id.today_memo_dialog_rm_ll).setVisibility(View.GONE);
                layout.addView(modeview);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_rm_vf)).showNext();
                int[][] dses=new int[][]{{R.id.t_1st_su,R.id.t_1st_mo,R.id.t_1st_tu,R.id.t_1st_we,R.id.t_1st_th,R.id.t_1st_fr,R.id.t_1st_sa},
                        {R.id.t_2nd_su,R.id.t_2nd_mo,R.id.t_2nd_tu,R.id.t_2nd_we,R.id.t_2nd_th,R.id.t_2nd_fr,R.id.t_2nd_sa},
                        {R.id.t_3rd_su,R.id.t_3rd_mo,R.id.t_3rd_tu,R.id.t_3rd_we,R.id.t_3rd_th,R.id.t_3rd_fr,R.id.t_3rd_sa},
                        {R.id.t_4th_su,R.id.t_4th_mo,R.id.t_4th_tu,R.id.t_4th_we,R.id.t_4th_th,R.id.t_4th_fr,R.id.t_4th_sa},
                        {R.id.t_5th_su,R.id.t_5th_mo,R.id.t_5th_tu,R.id.t_5th_we,R.id.t_5th_th,R.id.t_5th_fr,R.id.t_5th_sa},
                        {R.id.t_last_su,R.id.t_last_mo,R.id.t_last_tu,R.id.t_last_we,R.id.t_last_th,R.id.t_last_fr,R.id.t_last_sa}};
                int[][] vfses=new int[][]{{R.id.t_1st_su_vf,R.id.t_1st_mo_vf,R.id.t_1st_tu_vf,R.id.t_1st_we_vf,R.id.t_1st_th_vf,R.id.t_1st_fr_vf,R.id.t_1st_sa_vf},
                        {R.id.t_2nd_su_vf,R.id.t_2nd_mo_vf,R.id.t_2nd_tu_vf,R.id.t_2nd_we_vf,R.id.t_2nd_th_vf,R.id.t_2nd_fr_vf,R.id.t_2nd_sa_vf},
                        {R.id.t_3rd_su_vf,R.id.t_3rd_mo_vf,R.id.t_3rd_tu_vf,R.id.t_3rd_we_vf,R.id.t_3rd_th_vf,R.id.t_3rd_fr_vf,R.id.t_3rd_sa_vf},
                        {R.id.t_4th_su_vf,R.id.t_4th_mo_vf,R.id.t_4th_tu_vf,R.id.t_4th_we_vf,R.id.t_4th_th_vf,R.id.t_4th_fr_vf,R.id.t_4th_sa_vf},
                        {R.id.t_5th_su_vf,R.id.t_5th_mo_vf,R.id.t_5th_tu_vf,R.id.t_5th_we_vf,R.id.t_5th_th_vf,R.id.t_5th_fr_vf,R.id.t_5th_sa_vf},
                        {R.id.t_last_su_vf,R.id.t_last_mo_vf,R.id.t_last_tu_vf,R.id.t_last_we_vf,R.id.t_last_th_vf,R.id.t_last_fr_vf,R.id.t_last_sa_vf}};
                Set<Long> set=new HashSet<>();

                ViewFlipper vf2=modeview.findViewById(R.id.today_memo_dialog_rm_vf2);
                ViewFlipper vf3=modeview.findViewById(R.id.today_memo_dialog_rm_vf3);

                GridView gv=modeview.findViewById(R.id.today_memo_dialog_rm_gv);
                LinearLayout lv=modeview.findViewById(R.id.today_memo_dialog_rm_li);

                RadioButton rb1 = modeview.findViewById(R.id.today_memo_dialog_rm_b1);
                RadioButton rb2 = modeview.findViewById(R.id.today_memo_dialog_rm_b2);

                rb1.setChecked(true);
                rb2.setChecked(false);
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(rb1,1,14
                        ,1, TypedValue.COMPLEX_UNIT_SP);
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(rb2,1,14
                        ,1, TypedValue.COMPLEX_UNIT_SP);

                gv.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);

                RadioGroup radioGroup = modeview.findViewById(R.id.today_memo_dialog_rm_rg);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (R.id.today_memo_dialog_rm_b1!=checkedId) {
                            gv.setVisibility(View.GONE);
                            lv.setVisibility(View.VISIBLE);
                            vf2.showNext();
                        }
                        else {
                            gv.setVisibility(View.VISIBLE);
                            lv.setVisibility(View.GONE);
                            vf3.showNext();
                        }
                    }
                });


                GridAdapter gridAdapter=new GridAdapter(context,DateData.getDateOfMonth(t));
                gv.setAdapter(gridAdapter);

                String text=itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_MONTH_NO_REPEAT,"");
                if (!"".equals(text)) {
                    try {
                        boolean isgv=Boolean.valueOf(text.split(",")[0]);

                        if (isgv){
                            int bsOnText=Integer.valueOf(text.split(",")[1]);
                            if (bsOnText!=0)
                                gridAdapter.set.clear();
                            for (int i=0;i<31;i++){
                                if ((bsOnText&(1<<i))>0){
                                    gridAdapter.set.add(i+1);
                                }
                            }
                            gridAdapter.notifyDataSetChanged();
                        }
                        else {

                            gv.setVisibility(View.GONE);
                            lv.setVisibility(View.VISIBLE);
                            radioGroup.check(R.id.today_memo_dialog_rm_b2);
                            long bsOnText=Long.valueOf(text.split(",")[1]);
                            for (int i=0;i<49;i++){
                                if ((bsOnText&(1l<<i))>0){
                                    set.add(1l<<i);
                                }
                            }
                        }


                    }catch (Exception e){}
                }
                else {
                    text=itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_YEAR,"");
                    if (!"".equals(text)) {
                        try {
                            boolean isgv=Boolean.valueOf(text.split(",")[2]);

                            if (isgv){
                                int bsOnText=Integer.valueOf(text.split(",")[3]);
                                if (bsOnText!=0)
                                    gridAdapter.set.clear();
                                for (int i=0;i<31;i++){
                                    if ((bsOnText&(1<<i))>0){
                                        gridAdapter.set.add(i+1);
                                    }
                                }
                                gridAdapter.notifyDataSetChanged();
                            }
                            else {

                                gv.setVisibility(View.GONE);
                                lv.setVisibility(View.VISIBLE);
                                radioGroup.check(R.id.today_memo_dialog_rm_b2);
                                long bsOnText=Long.valueOf(text.split(",")[3]);
                                for (int i=0;i<49;i++){
                                    if ((bsOnText&(1l<<i))>0){
                                        set.add(1l<<i);
                                    }
                                }
                            }


                        }catch (Exception e){}
                    }
                }

                int i=1;
                for (int[] ds:dses){
                    int j=1;
                    for (int d:ds){
                        long l=1;
                        l=l<<((i-1)*7+(j-1));
                        final long fl = l;

                        TextView textView=view.findViewById(d);
                        final ViewFlipper viewFlipper=view.findViewById(vfses[i-1][j-1]);

                        if ((DateData.getDateOfWeek(t)==j)&&(DateData.getDayOfWeekOnMonth(t)==i)&&(set.isEmpty())){
                            set.add(fl);
                        }

                        if (set.contains(fl)){
                            textView.setBackgroundColor(context.getColor(R.color.red));
                            textView.setTextColor(context.getColor(R.color.white));
                        }

                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewFlipper.showNext();
                                if (set.contains(fl)){
                                    set.remove(fl);
                                    textView.setBackgroundColor(Color.argb(0,0,0,0));
                                    textView.setTextColor(context.getColor(R.color.black));
                                }
                                else {
                                    set.add(fl);
                                    textView.setBackgroundColor(context.getColor(R.color.red));
                                    textView.setTextColor(context.getColor(R.color.white));
                                }
                            }
                        });
                        j++;
                    }
                    i++;
                }

                int number = getWindowManager()
                        .getDefaultDisplay().getWidth();

                int colum=(number-2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_layout_padding)
                        -2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_layout_mrgin))/
                        (context.getResources().getDimensionPixelSize(R.dimen.today_memo_rm_grid_im_size)+2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_rm_grid_ma_size));
                if (colum<1)
                    colum=1;
                gv.setNumColumns(colum);

                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        gridAdapter.setdate(position+1);
                        gridAdapter.notifyDataSetChanged();
                        ViewFlipper vf=view.findViewById(R.id.today_memo_dialog_rm_grid_vf);
                        vf.showNext();
                    }
                });

                back2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = "";

                        long d = 0;
                        if (gv.getVisibility() == View.VISIBLE) {
                            text += true;
                            for (int i : gridAdapter.set) {
                                d = d | (1l << (i - 1));
                            }
                        } else {
                            text += false;
                            for (long i : set) {
                                d = d | i;
                            }
                        }

                        if (d == 0) {
                            itemViews.getOrDefault(item, new HashMap<>()).remove(REPEAT_EVERY_MONTH_NO_REPEAT);

                        } else {
                            itemViews.getOrDefault(item, new HashMap<>()).put(REPEAT_EVERY_MONTH_NO_REPEAT, text + "," + d);
                        }
                        setmodeWindow(REPEAT_EVERY_YEAR_NO_VF, view, context, item, t,ontouchplus);
                    }
                });

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                        String text =itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_YEAR_NO_VF,"");;
                        itemViews.getOrDefault(item, new HashMap<>()).remove(REPEAT_EVERY_MONTH_NO_REPEAT);
                        itemViews.getOrDefault(item, new HashMap<>()).remove(REPEAT_EVERY_YEAR_NO_VF);
                        long d = 0;
                        if (gv.getVisibility() == View.VISIBLE) {
                            text += ","+true;
                            for (int i : gridAdapter.set) {
                                d = d | (1l << (i - 1));
                            }
                        } else {
                            text += ","+false;
                            for (long i : set) {
                                d = d | i;
                            }
                        }

                        if (d == 0) {
                            itemViews.getOrDefault(item, new HashMap<>()).remove(REPEAT_EVERY_YEAR);

                        }
                        else {
                            f.removeOtherTask(item);
                            itemViews.getOrDefault(item, new HashMap<>()).put(REPEAT_EVERY_YEAR, text + "," + d);
                        }
                        updateItemBar(view,context,item,t);
                    }
                });
                break;
            }
            case REPEAT_EVERY_WEEK_IN_WEEK:{
                View modeview=View.inflate(context, R.layout.today_memo_rd,null);
                layout.addView(modeview);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_rd_vf)).showNext();
                EditText myEditText = modeview.findViewById(R.id.today_memo_dialog_rd_num);
                myEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                myEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
                myEditText.setText(itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_WEEK_IN_WEEK,"1"));
                TextView textView=modeview.findViewById(R.id.today_memo_dialog_rd_unit);

                try {
                    int u=Integer.valueOf(myEditText.getText().toString());
                    if (u<=1){
                        textView.setText(R.string.week);
                    }
                    else {
                        textView.setText(R.string.weeks);
                    }
                }catch (Exception e){}

                myEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int u=0;
                        try {
                            u=Integer.valueOf(s.toString());
                        }catch (Exception e){}
                        if (u<=1){
                            textView.setText(R.string.week);
                        }
                        else {
                            textView.setText(R.string.weeks);
                        }
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myEditText.requestFocus();
                    }
                },200);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                        String text=myEditText.getText().toString();
                        if ("".equals(text)||Integer.valueOf(text).equals(0)){
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_WEEK_IN_WEEK);
                        }
                        else {
                            f.removeOtherTask(item);
                            itemViews.getOrDefault(item,new HashMap<>()).put(REPEAT_EVERY_WEEK_IN_WEEK,text);
                        }
                        updateItemBar(view,context,item,t);
                    }
                });
                break;
            }
            case REPEAT_EVERY_MONTH_IN_WEEK:{
                View modeview=View.inflate(context, R.layout.today_memo_rm_in_week,null);
                layout.addView(modeview);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_rm_iw_vf)).showNext();
                EditText myEditText = modeview.findViewById(R.id.today_memo_dialog_rm_iw_num);
                myEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                myEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());

                TextView textView=modeview.findViewById(R.id.today_memo_dialog_rm_iw_unit);
                myEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int u=0;
                        try {
                            u=Integer.valueOf(s.toString());
                        }catch (Exception e){}
                        if (u<=1){
                            textView.setText(R.string.month);
                        }
                        else {
                            textView.setText(R.string.months);
                        }
                    }
                });

                final int[] weeks=new int[]{R.id.today_memo_dialog_rm_iw_1st,R.id.today_memo_dialog_rm_iw_2nd,R.id.today_memo_dialog_rm_iw_3rd,
                        R.id.today_memo_dialog_rm_iw_4th,R.id.today_memo_dialog_rm_iw_5th,R.id.today_memo_dialog_rm_iw_last};

                final int[] weeks_b=new int[]{R.id.today_memo_dialog_rm_iw_1st_rb,R.id.today_memo_dialog_rm_iw_2nd_rb,R.id.today_memo_dialog_rm_iw_3rd_rb,
                        R.id.today_memo_dialog_rm_iw_4th_rb,R.id.today_memo_dialog_rm_iw_5th_rb,R.id.today_memo_dialog_rm_iw_last_rb};

                final int[] weeks_v=new int[]{R.id.today_memo_dialog_rm_iw_1st_vf,R.id.today_memo_dialog_rm_iw_2nd_vf,R.id.today_memo_dialog_rm_iw_3rd_vf,
                        R.id.today_memo_dialog_rm_iw_4th_vf,R.id.today_memo_dialog_rm_iw_5th_vf,R.id.today_memo_dialog_rm_iw_last_vf};

                final boolean[] bs=new boolean[]{false,false,false,false,false,false,false};

                final int[] ds=new int[]{FIRST_WEEK,SECOND_WEEK,THIRD_WEEK,FOURTH_WEEK,FIFTH_WEEK,LAST_WEEK};

                String text=itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_MONTH_IN_WEEK,"");
                if (!"".equals(text)) {
                    try {
                        myEditText.setText(text.split(",")[0]);
                        int u=Integer.valueOf(text.split(",")[0]);

                        if (u<=1){
                            textView.setText(R.string.month);
                        }
                        else {
                            textView.setText(R.string.months);
                        }
                        int bsOnText=Integer.valueOf(text.split(",")[1]);
                        for (int i=0;i<6;i++){
                            bs[i]=(ds[i]&bsOnText)>0;
                        }
                    }catch (Exception e){

                    }
                }
                else {
                    int d=DateData.getWeekOfMonth(t)-1;
                    if (d>=0&&d<6)
                        bs[d]=true;
                }

                for (int i=0;i<6;i++){
                    final int day=i;
                    final RadioButton radioButton=modeview.findViewById(weeks_b[day]);
                    View.OnClickListener onClickListener=new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bs[day]=!bs[day];
                            radioButton.setChecked(bs[day]);
                            ((ViewFlipper)modeview.findViewById(weeks_v[day])).showNext();
                        }
                    };
                    modeview.findViewById(weeks_b[day]).setOnClickListener(onClickListener);
                    radioButton.setChecked(bs[day]);
                    modeview.findViewById(weeks[day]).setOnClickListener(onClickListener);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myEditText.requestFocus();
                    }
                },200);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                        String text=myEditText.getText().toString();
                        int d=0;
                        for (int i=0;i<6;i++){
                            if (bs[i])
                                d=d|ds[i];
                        }
                        if ("".equals(text)||Integer.valueOf(text).equals(0)||d==0){
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_MONTH_IN_WEEK);
                        }
                        else {
                            f.removeOtherTask(item);
                            itemViews.getOrDefault(item,new HashMap<>()).put(REPEAT_EVERY_MONTH_IN_WEEK,text+","+d);
                        }
                        updateItemBar(view,context,item,t);

                    }
                });
                break;
            }
            case REPEAT_EVERY_YEAR_IN_WEEK:{
                done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue)));
                done.setText(R.string.next);

                View modeview=View.inflate(context, R.layout.today_memo_ry,null);
                layout.addView(modeview);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_ry_vf)).showNext();
                EditText myEditText = modeview.findViewById(R.id.today_memo_dialog_ry_num);
                myEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                myEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());

                GridView gv=modeview.findViewById(R.id.today_memo_dialog_ry_gv);
                GridAdapter2 gridAdapter=new GridAdapter2(context,DateData.getMonth(t));
                gv.setAdapter(gridAdapter);

                int number = getWindowManager()
                        .getDefaultDisplay().getWidth();
                int colum=(number-2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_layout_padding)
                        -2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_layout_mrgin))/
                        (context.getResources().getDimensionPixelSize(R.dimen.today_memo_ry_grid_im_width_size)+
                                2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_ry_grid_ma_size));
                if (colum<1)
                    colum=1;
                gv.setNumColumns(colum);

                TextView textView=modeview.findViewById(R.id.today_memo_dialog_ry_unit);
                myEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int u=0;
                        try {
                            u=Integer.valueOf(s.toString());
                        }catch (Exception e){}
                        if ("".equals(s.toString())||u==0||gridAdapter.set.isEmpty()){
                            done.setText(R.string.done);
                            done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
                        }
                        else {
                            done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue)));
                            done.setText(R.string.next);
                        }

                        if (u<=1){
                            textView.setText(R.string.year);
                        }
                        else {
                            textView.setText(R.string.years);
                        }
                    }
                });

                String text=itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_YEAR_IN_WEEK,"");
                if (!"".equals(text)) {
                    try {
                        myEditText.setText(text.split(",")[0]);
                        int u=Integer.valueOf(text.split(",")[0]);

                        if (u<=1){
                            textView.setText(R.string.year);
                        }
                        else {
                            textView.setText(R.string.years);
                        }
                        int bsOnText=Integer.valueOf(text.split(",")[1]);
                        if (bsOnText!=0)
                            gridAdapter.set.clear();
                        for (int i=0;i<12;i++){
                            if ((bsOnText&(1<<i))>0){
                                gridAdapter.set.add(i+1);
                            }
                        }
                        gridAdapter.notifyDataSetChanged();
                    }catch (Exception e){

                    }
                }

                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        gridAdapter.setmonth(position+1);
                        gridAdapter.notifyDataSetChanged();
                        ViewFlipper vf=view.findViewById(R.id.today_memo_dialog_ry_grid_vf);
                        vf.showNext();
                        if ("".equals(myEditText.getText().toString())||Integer.valueOf(myEditText.getText().toString()).equals(0)||
                                gridAdapter.set.isEmpty()){
                            done.setText(R.string.done);
                            done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
                        }
                        else {
                            done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue)));
                            done.setText(R.string.next);
                        }
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myEditText.requestFocus();
                    }
                },200);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String text=myEditText.getText().toString();
                        int d=0;
                        for (int i:gridAdapter.set){
                            d=d|(1<<(i-1));
                        }
                        if ("".equals(text)||Integer.valueOf(text).equals(0)||d==0){
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_YEAR_IN_WEEK);
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_YEAR_IN_WEEK_NO_VF);
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_MONTH_IN_WEEK_NO_REPEAT);
                            view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                            view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                        }
                        else {
                            itemViews.getOrDefault(item,new HashMap<>()).put(REPEAT_EVERY_YEAR_IN_WEEK_NO_VF,text+","+d);
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_MONTH_IN_WEEK_NO_REPEAT);
                            setmodeWindow(REPEAT_EVERY_MONTH_IN_WEEK_NO_REPEAT, view, context, item, t,ontouchplus);
                        }
                        updateItemBar(view,context,item,t);

                    }
                });
                break;
            }
            case REPEAT_EVERY_MONTH_IN_WEEK_NO_REPEAT:{
                View modeview=View.inflate(context, R.layout.today_memo_rm_in_week,null);
                modeview.findViewById(R.id.today_memo_dialog_rm_iw_ll).setVisibility(View.GONE);
                layout.addView(modeview);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_rm_iw_vf)).showNext();

                final int[] weeks=new int[]{R.id.today_memo_dialog_rm_iw_1st,R.id.today_memo_dialog_rm_iw_2nd,R.id.today_memo_dialog_rm_iw_3rd,
                        R.id.today_memo_dialog_rm_iw_4th,R.id.today_memo_dialog_rm_iw_5th,R.id.today_memo_dialog_rm_iw_last};

                final int[] weeks_b=new int[]{R.id.today_memo_dialog_rm_iw_1st_rb,R.id.today_memo_dialog_rm_iw_2nd_rb,R.id.today_memo_dialog_rm_iw_3rd_rb,
                        R.id.today_memo_dialog_rm_iw_4th_rb,R.id.today_memo_dialog_rm_iw_5th_rb,R.id.today_memo_dialog_rm_iw_last_rb};

                final int[] weeks_v=new int[]{R.id.today_memo_dialog_rm_iw_1st_vf,R.id.today_memo_dialog_rm_iw_2nd_vf,R.id.today_memo_dialog_rm_iw_3rd_vf,
                        R.id.today_memo_dialog_rm_iw_4th_vf,R.id.today_memo_dialog_rm_iw_5th_vf,R.id.today_memo_dialog_rm_iw_last_vf};

                final boolean[] bs=new boolean[]{false,false,false,false,false,false,false};

                final int[] ds=new int[]{FIRST_WEEK,SECOND_WEEK,THIRD_WEEK,FOURTH_WEEK,FIFTH_WEEK,LAST_WEEK};


                String text=itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_MONTH_IN_WEEK_NO_REPEAT,"");
                if (!"".equals(text)) {
                    try {
                        int bsOnText=Integer.valueOf(text);
                        for (int i=0;i<6;i++){
                            bs[i]=(ds[i]&bsOnText)>0;
                        }
                    }catch (Exception e){

                    }
                }
                else {
                    text=itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_YEAR_IN_WEEK,"");
                    if (!"".equals(text)) {
                        try {
                            int bsOnText=Integer.valueOf(text.split(",")[2]);
                            for (int i=0;i<6;i++){
                                bs[i]=(ds[i]&bsOnText)>0;
                            }
                        }catch (Exception e){

                        }
                    }
                    else {
                        int d=DateData.getWeekOfMonth(t)-1;
                        if (d>=0&&d<6)
                            bs[d]=true;
                    }
                }

                for (int i=0;i<6;i++){
                    final int day=i;
                    final RadioButton radioButton=modeview.findViewById(weeks_b[day]);
                    View.OnClickListener onClickListener=new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bs[day]=!bs[day];
                            radioButton.setChecked(bs[day]);
                            ((ViewFlipper)modeview.findViewById(weeks_v[day])).showNext();
                        }
                    };
                    modeview.findViewById(weeks_b[day]).setOnClickListener(onClickListener);
                    radioButton.setChecked(bs[day]);
                    modeview.findViewById(weeks[day]).setOnClickListener(onClickListener);
                }

                back2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = "";

                        int d=0;
                        for (int i=0;i<6;i++){
                            if (bs[i])
                                d=d|ds[i];
                        }
                        if (d == 0) {
                            itemViews.getOrDefault(item, new HashMap<>()).remove(REPEAT_EVERY_MONTH_IN_WEEK_NO_REPEAT);

                        } else {
                            itemViews.getOrDefault(item, new HashMap<>()).put(REPEAT_EVERY_MONTH_IN_WEEK_NO_REPEAT,"" + d);
                        }
                        setmodeWindow(REPEAT_EVERY_YEAR_IN_WEEK_NO_VF, view, context, item, t,ontouchplus);
                    }
                });


                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                        String text =itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_YEAR_IN_WEEK_NO_VF,"");;
                        itemViews.getOrDefault(item, new HashMap<>()).remove(REPEAT_EVERY_MONTH_IN_WEEK_NO_REPEAT);
                        itemViews.getOrDefault(item, new HashMap<>()).remove(REPEAT_EVERY_YEAR_IN_WEEK_NO_VF);
                        int d=0;
                        for (int i=0;i<6;i++){
                            if (bs[i])
                                d=d|ds[i];
                        }
                        if (d==0){
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_YEAR_IN_WEEK);
                        }
                        else {
                            f.removeOtherTask(item);
                            itemViews.getOrDefault(item, new HashMap<>()).put(REPEAT_EVERY_YEAR_IN_WEEK, text + "," + d);
                        }
                        updateItemBar(view,context,item,t);

                    }
                });
                break;
            }
            case REPEAT_EVERY_YEAR_IN_WEEK_NO_VF:{
                done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue)));
                done.setText(R.string.next);

                back2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layout.setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_mode_list).setVisibility(View.VISIBLE);
                        done.setVisibility(View.GONE);
                        ((ViewFlipper)view.findViewById(R.id.today_memo_dialog_mode_list_vf)).showNext();
                        itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_YEAR_IN_WEEK_NO_VF);
                        itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_MONTH_IN_WEEK_NO_REPEAT);
                        view.findViewById(R.id.today_memo_dialog_back2).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                                view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                            }
                        });
                    }
                });

                View modeview=View.inflate(context, R.layout.today_memo_ry,null);
                layout.addView(modeview);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_ry_vf)).setInAnimation(context,R.anim.slide_in_left);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_ry_vf)).showNext();
                EditText myEditText = modeview.findViewById(R.id.today_memo_dialog_ry_num);
                myEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                myEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());

                GridView gv=modeview.findViewById(R.id.today_memo_dialog_ry_gv);
                GridAdapter2 gridAdapter=new GridAdapter2(context,DateData.getMonth(t));
                gv.setAdapter(gridAdapter);

                int number = getWindowManager()
                        .getDefaultDisplay().getWidth();
                int colum=(number-2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_layout_padding)
                        -2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_layout_mrgin))/
                        (context.getResources().getDimensionPixelSize(R.dimen.today_memo_ry_grid_im_width_size)+
                                2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_ry_grid_ma_size));
                if (colum<1)
                    colum=1;
                gv.setNumColumns(colum);

                TextView textView=modeview.findViewById(R.id.today_memo_dialog_ry_unit);

                myEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int u=0;
                        try {
                            u=Integer.valueOf(s.toString());
                        }catch (Exception e){}

                        if ("".equals(s.toString())||u==0||gridAdapter.set.isEmpty()){
                            done.setText(R.string.done);
                            done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
                        }
                        else {
                            done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue)));
                            done.setText(R.string.next);
                        }


                        if (u<=1){
                            textView.setText(R.string.year);
                        }
                        else {
                            textView.setText(R.string.years);
                        }
                    }
                });

                String text=itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_YEAR_IN_WEEK_NO_VF,"");
                if (!"".equals(text)) {
                    try {
                        myEditText.setText(text.split(",")[0]);
                        int u=Integer.valueOf(text.split(",")[0]);

                        if (u<=1){
                            textView.setText(R.string.year);
                        }
                        else {
                            textView.setText(R.string.years);
                        }
                        int bsOnText=Integer.valueOf(text.split(",")[1]);
                        if (bsOnText!=0)
                            gridAdapter.set.clear();
                        for (int i=0;i<12;i++){
                            if ((bsOnText&(1<<i))>0){
                                gridAdapter.set.add(i+1);
                            }
                        }
                        gridAdapter.notifyDataSetChanged();
                    }catch (Exception e){

                    }
                }

                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        gridAdapter.setmonth(position+1);
                        gridAdapter.notifyDataSetChanged();
                        ViewFlipper vf=view.findViewById(R.id.today_memo_dialog_ry_grid_vf);
                        vf.showNext();
                        if ("".equals(myEditText.getText().toString())||Integer.valueOf(myEditText.getText().toString()).equals(0)||
                                gridAdapter.set.isEmpty()){
                            done.setText(R.string.done);
                            done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
                        }
                        else {
                            done.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue)));
                            done.setText(R.string.next);
                        }
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myEditText.requestFocus();
                    }
                },200);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String text=myEditText.getText().toString();
                        int d=0;
                        for (int i:gridAdapter.set){
                            d=d|(1<<(i-1));
                        }
                        if ("".equals(text)||Integer.valueOf(text).equals(0)||d==0){
                            view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                            view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_YEAR_IN_WEEK_NO_VF);
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_YEAR_IN_WEEK);
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_MONTH_IN_WEEK_NO_REPEAT);
                            updateItemBar(view,context,item,t);

                        }
                        else {
                            itemViews.getOrDefault(item,new HashMap<>()).put(REPEAT_EVERY_YEAR_IN_WEEK_NO_VF,text+","+d);
                            setmodeWindow(REPEAT_EVERY_MONTH_IN_WEEK_NO_REPEAT, view, context, item, t,ontouchplus);
                        }
                    }
                });
                break;
            }
            case REPEAT_EVERY_MONTH_IN_MONTH:{
                View modeview=View.inflate(context, R.layout.today_memo_rd,null);
                layout.addView(modeview);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_rd_vf)).showNext();
                EditText myEditText = modeview.findViewById(R.id.today_memo_dialog_rd_num);
                myEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                myEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
                myEditText.setText(itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_MONTH_IN_MONTH,"1"));
                TextView textView=modeview.findViewById(R.id.today_memo_dialog_rd_unit);

                try {
                    int u=Integer.valueOf(myEditText.getText().toString());
                    if (u<=1){
                        textView.setText(R.string.month);
                    }
                    else {
                        textView.setText(R.string.months);
                    }
                }catch (Exception e){}

                myEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int u=0;
                        try {
                            u=Integer.valueOf(s.toString());
                        }catch (Exception e){}
                        if (u<=1){
                            textView.setText(R.string.month);
                        }
                        else {
                            textView.setText(R.string.months);
                        }
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myEditText.requestFocus();
                    }
                },200);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                        String text=myEditText.getText().toString();
                        if ("".equals(text)||Integer.valueOf(text).equals(0)){
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_MONTH_IN_MONTH);
                        }
                        else {
                            f.removeOtherTask(item);
                            itemViews.getOrDefault(item,new HashMap<>()).put(REPEAT_EVERY_MONTH_IN_MONTH,text);
                        }
                        updateItemBar(view,context,item,t);

                    }
                });
                break;
            }
            case REPEAT_EVERY_YEAR_IN_MONTH:{
                View modeview=View.inflate(context, R.layout.today_memo_ry,null);
                layout.addView(modeview);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_ry_vf)).showNext();
                EditText myEditText = modeview.findViewById(R.id.today_memo_dialog_ry_num);
                myEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                myEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());

                GridView gv=modeview.findViewById(R.id.today_memo_dialog_ry_gv);
                GridAdapter2 gridAdapter=new GridAdapter2(context,DateData.getMonth(t));
                gv.setAdapter(gridAdapter);

                int number = getWindowManager()
                        .getDefaultDisplay().getWidth();
                int colum=(number-2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_layout_padding)
                        -2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_layout_mrgin))/
                        (context.getResources().getDimensionPixelSize(R.dimen.today_memo_ry_grid_im_width_size)+
                                2*context.getResources().getDimensionPixelSize(R.dimen.today_memo_ry_grid_ma_size));
                if (colum<1)
                    colum=1;
                gv.setNumColumns(colum);

                TextView textView=modeview.findViewById(R.id.today_memo_dialog_ry_unit);
                myEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int u=0;
                        try {
                            u=Integer.valueOf(s.toString());
                        }catch (Exception e){}
                        if (u<=1){
                            textView.setText(R.string.year);
                        }
                        else {
                            textView.setText(R.string.years);
                        }
                    }
                });

                String text=itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(REPEAT_EVERY_YEAR_IN_MONTH,"");
                if (!"".equals(text)) {
                    try {
                        myEditText.setText(text.split(",")[0]);
                        int u=Integer.valueOf(text.split(",")[0]);

                        if (u<=1){
                            textView.setText(R.string.year);
                        }
                        else {
                            textView.setText(R.string.years);
                        }
                        int bsOnText=Integer.valueOf(text.split(",")[1]);
                        if (bsOnText!=0)
                            gridAdapter.set.clear();
                        for (int i=0;i<12;i++){
                            if ((bsOnText&(1<<i))>0){
                                gridAdapter.set.add(i+1);
                            }
                        }
                        gridAdapter.notifyDataSetChanged();
                    }catch (Exception e){

                    }
                }

                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        gridAdapter.setmonth(position+1);
                        gridAdapter.notifyDataSetChanged();
                        ViewFlipper vf=view.findViewById(R.id.today_memo_dialog_ry_grid_vf);
                        vf.showNext();
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myEditText.requestFocus();
                    }
                },200);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);

                        String text=myEditText.getText().toString();
                        int d=0;
                        for (int i:gridAdapter.set){
                            d=d|(1<<(i-1));
                        }
                        if ("".equals(text)||Integer.valueOf(text).equals(0)||d==0){
                            itemViews.getOrDefault(item,new HashMap<>()).remove(REPEAT_EVERY_YEAR_IN_MONTH);

                        }
                        else {
                            f.removeOtherTask(item);
                            itemViews.getOrDefault(item,new HashMap<>()).put(REPEAT_EVERY_YEAR_IN_MONTH,text+","+d);
                        }
                        updateItemBar(view,context,item,t);

                    }
                });
                break;
            }
            case NO_REPEAT:{
                hideKeyboard(this);
                View modeview=View.inflate(context, R.layout.today_memo_nr,null);
                layout.addView(modeview);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_nr_vf)).showNext();
                done.setText(R.string.Delete);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                        f.removeOtherTask(item);
                        updateItemBar(view,context,item,t);

                    }
                });
                break;
            }
            case END_DAY:{
                long ets=t;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                try {
                    Date date;
                    date= sdf.parse(itemViews.get(item).get(START_DAY));
                    ets=date.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                final long et=ets;

                hideKeyboard(this);
                View modeview=View.inflate(context, R.layout.today_memo_re,null);
                layout.addView(modeview);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_re_vf)).showNext();

                View calendarview=View.inflate(context, R.layout.new_app_widget,null);
                ((FrameLayout)modeview.findViewById(R.id.today_memo_dialog_re_fr)).addView(calendarview);

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
                    int scaleMonth=0;
                    int nowScaleMonth=0;
                    int nowDate=DateData.getDateOfMonth(et);
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
                        ((TextView)modeview.findViewById(R.id.today_memo_dialog_re_title)).setText(
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

                        WheelPicker np1=modeview.findViewById(R.id.today_memo_dialog_re_np1);
                        WheelPicker np2=modeview.findViewById(R.id.today_memo_dialog_re_np2);

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
                        WheelPicker np1=modeview.findViewById(R.id.today_memo_dialog_re_np1);
                        WheelPicker np2=modeview.findViewById(R.id.today_memo_dialog_re_np2);

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

                String text=itemViews.getOrDefault(item,new HashMap<>()).getOrDefault(END_DAY,"");
                if (!"".equals(text)) {
                    try {
                        long i=DateData.getLongOnTime(text);
                        ef.scaleMonth= ComparisonDate.monthDiff(text,DateData.getNowDate(et));
                        ef.nowScaleMonth=ef.scaleMonth;
                        ef.nowDate=DateData.getDateOfMonth(i);
                    }catch (Exception e){}
                }

                ef.setCalendar();
                ef.setTitle();
                ef.setNowTouch(true);
                ef.setClick();
                ef.InitNumberPicker();

                View ll=modeview.findViewById(R.id.today_memo_dialog_re_ll);
                View fr=modeview.findViewById(R.id.today_memo_dialog_re_fr);
                ll.setVisibility(View.GONE);
                fr.setVisibility(View.VISIBLE);

                TextView title=modeview.findViewById(R.id.today_memo_dialog_re_title);

                View bl=modeview.findViewById(R.id.today_memo_dialog_re_back_ll);
                View nl=modeview.findViewById(R.id.today_memo_dialog_re_next_ll);

                if (ef.scaleMonth==0)
                    modeview.findViewById(R.id.today_memo_dialog_re_back).setVisibility(View.INVISIBLE);
                else
                    modeview.findViewById(R.id.today_memo_dialog_re_back).setVisibility(View.VISIBLE);

                modeview.findViewById(R.id.today_memo_dialog_re_back).setOnClickListener(new View.OnClickListener() {
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
                modeview.findViewById(R.id.today_memo_dialog_re_next).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modeview.findViewById(R.id.today_memo_dialog_re_back).setVisibility(View.VISIBLE);
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

                        ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_re_vf2)).showNext();
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
                                modeview.findViewById(R.id.today_memo_dialog_re_back).setVisibility(View.INVISIBLE);
                            else
                                modeview.findViewById(R.id.today_memo_dialog_re_back).setVisibility(View.VISIBLE);

                        }
                    }
                });

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                        itemViews.getOrDefault(item,new HashMap<>()).put(END_DAY,DateData.getNowDate(
                                DateData.getLongOnTime(ef.getnowYear(),ef.getnowMonth(),ef.nowDate)
                        ));
                        updateItemBar(view,context,item,t);
                    }
                });
                break;
            }
            case DO_NOT_SHOW_ON_CHECKED:{
                View modeview=View.inflate(context, R.layout.today_memo_oncheked,null);
                layout.addView(modeview);
                ((ViewFlipper)modeview.findViewById(R.id.today_memo_dialog_oncheckd_vf)).showNext();
                CheckBox checkBox=modeview.findViewById(R.id.today_memo_dialog_oncheckd_cb);
                checkBox.setChecked(itemViews.getOrDefault(item,new HashMap<>()).containsKey(DO_NOT_SHOW_ON_CHECKED));
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.GONE);
                        if (checkBox.isChecked())
                            itemViews.getOrDefault(item,new HashMap<>()).put(DO_NOT_SHOW_ON_CHECKED,"");
                        else
                            itemViews.getOrDefault(item,new HashMap<>()).remove(DO_NOT_SHOW_ON_CHECKED);

                        updateItemBar(view,context,item,t);
                    }
                });
                break;
            }
        }
    }
    private EditText addItemView(int mode,View view,Context context,long t,String text,boolean request,String data){
        final ViewFlipper window2=view.findViewById(R.id.today_memo_dialog_add_window2);
        final ImageView im=view.findViewById(R.id.today_memo_dialog_im);
        final LinearLayout items=view.findViewById(R.id.today_memo_dialog_items);
        final View newView=View.inflate(getApplicationContext(), R.layout.today_memo_item,null);
        final Activity activity=this;

        class F{
            boolean containRepeat(View v){
                if (itemViews.containsKey(v)){
                    if (
                    itemViews.get(v).containsKey(REPEAT_EVERY_DAY)||
                    itemViews.get(v).containsKey(REPEAT_EVERY_WEEK)||
                    itemViews.get(v).containsKey(REPEAT_EVERY_MONTH)||
                    itemViews.get(v).containsKey(REPEAT_EVERY_YEAR)||
                    itemViews.get(v).containsKey(REPEAT_EVERY_YEAR_NO_VF)||
                    itemViews.get(v).containsKey(REPEAT_EVERY_MONTH_NO_REPEAT)||
                    itemViews.get(v).containsKey(REPEAT_EVERY_WEEK_IN_WEEK)||
                    itemViews.get(v).containsKey(REPEAT_EVERY_MONTH_IN_WEEK)||
                    itemViews.get(v).containsKey(REPEAT_EVERY_MONTH_IN_WEEK_NO_REPEAT)||
                    itemViews.get(v).containsKey(REPEAT_EVERY_YEAR_IN_WEEK)||
                    itemViews.get(v).containsKey(REPEAT_EVERY_YEAR_IN_WEEK_NO_VF)||
                    itemViews.get(v).containsKey(REPEAT_EVERY_MONTH_IN_MONTH)||
                    itemViews.get(v).containsKey(REPEAT_EVERY_YEAR_IN_MONTH)
                    ){
                        return true;
                    }

                }
                return false;
            }
        }
        F f=new F();

        Map<Integer,String> map=null;
        try {
            map= ComparisonDate.getMap(data);
        }catch (Exception e){}

        itemViews.put(newView,map==null?new HashMap<>():map);


        switch (mode){
            case DATE:{
                if (!itemViews.get(newView).containsKey(START_DAY))
                    itemViews.get(newView).put(START_DAY,DateData.getNowDate(t));
                TextView textView=newView.findViewById(R.id.today_memo_dialog_text);
                if (DateData.getNowDate(t).equals(itemViews.get(newView).get(START_DAY)))
                    textView.setText(getDayText(context,DateData.getDateOfMonth(t)-1));
                else
                    textView.setText(DateData.getNowDate(DateData.getLongOnTime(itemViews.get(newView).get(START_DAY)),context));

                final EditText editText=newView.findViewById(R.id.today_memo_dialog_data);
                editText.setHint(R.string.to_do_in_day);
                editText.setText(text);
                if (request){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editText.requestFocus();
                        }
                    },100);
                }
                ViewFlipper viewFlipper=newView.findViewById(R.id.today_memo_dialog_VF);
                viewFlipper.showNext();
                items.addView(newView);
                itemViews.get(newView).put(TO_DO_IN_THE_DAY,"");


                newView.findViewById(R.id.today_memo_dialog_plus).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyboard(activity);
                        im.setVisibility(View.VISIBLE);
                        window2.setVisibility(View.VISIBLE);
                        window2.showNext();
                        view.findViewById(R.id.today_memo_dialog_mode_window).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_mode).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_mode_list).setVisibility(View.VISIBLE);
                        TextView back2=view.findViewById(R.id.today_memo_dialog_back2);
                        back2.setText(R.string.back);
                        back2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                im.setVisibility(View.GONE);
                                window2.setVisibility(View.GONE);
                            }
                        });
                        Map<Integer,String> map1=itemViews.getOrDefault(newView,new HashMap<>());

                        view.findViewById(R.id.today_memo_dialog_rd).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.today_memo_dialog_rd).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(REPEAT_EVERY_DAY,view,context,newView,t,true);
                            }
                        });

                        view.findViewById(R.id.today_memo_dialog_rw).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.today_memo_dialog_rw).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(REPEAT_EVERY_WEEK,view,context,newView,t,true);
                            }
                        });

                        view.findViewById(R.id.today_memo_dialog_rm).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.today_memo_dialog_rm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(REPEAT_EVERY_MONTH,view,context,newView,t,true);
                            }
                        });

                        view.findViewById(R.id.today_memo_dialog_ry).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.today_memo_dialog_ry).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(REPEAT_EVERY_YEAR,view,context,newView,t,true);
                            }
                        });

                        if (f.containRepeat(newView)) {
                            view.findViewById(R.id.today_memo_dialog_nr).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.today_memo_dialog_re).setVisibility(View.VISIBLE);
                        }
                        else {
                            view.findViewById(R.id.today_memo_dialog_nr).setVisibility(View.GONE);
                            view.findViewById(R.id.today_memo_dialog_re).setVisibility(View.GONE);
                        }
                        view.findViewById(R.id.today_memo_dialog_nr).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(NO_REPEAT,view,context,newView,t,true);
                            }
                        });
                        view.findViewById(R.id.today_memo_dialog_re).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(END_DAY,view,context,newView,t,true);
                            }
                        });
                        view.findViewById(R.id.today_memo_dialog_oncheckd).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(DO_NOT_SHOW_ON_CHECKED,view,context,newView,t,true);
                            }
                        });
                    }
                });
                updateItemBar(view,context,newView,t);
                return editText;
            }
            case WEEK:{
                Date date = new Date(t);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_MONTH,-(calendar.get(Calendar.DAY_OF_WEEK)-1));
                date=calendar.getTime();
                if (!itemViews.get(newView).containsKey(START_DAY)) {
                    itemViews.get(newView).put(START_DAY, DateData.getNowDate(date.getTime()));
                }
                TextView textView=newView.findViewById(R.id.today_memo_dialog_text);

                if (DateData.getNowDate(date.getTime()).equals(itemViews.get(newView).get(START_DAY)))
                    textView.setText(TextBitmap.getWeekText(DateData.getWeekOfMonth(t)-1,context));
                else
                    textView.setText(DateData.getNowWeekOfMonth(DateData.getLongOnTime(itemViews.get(newView).get(START_DAY)),context));

                final EditText editText=newView.findViewById(R.id.today_memo_dialog_data);
                editText.setHint(R.string.to_do_in_week);
                editText.setText(text);
                if (request){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editText.requestFocus();
                        }
                    },100);
                }
                ViewFlipper viewFlipper=newView.findViewById(R.id.today_memo_dialog_VF);
                viewFlipper.showNext();
                items.addView(newView);
                itemViews.get(newView).put(TO_DO_IN_THE_WEEK,"");
                newView.findViewById(R.id.today_memo_dialog_plus).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyboard(activity);
                        im.setVisibility(View.VISIBLE);
                        window2.setVisibility(View.VISIBLE);
                        window2.showNext();
                        view.findViewById(R.id.today_memo_dialog_mode_window).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_mode).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_mode_list).setVisibility(View.VISIBLE);
                        TextView back2=view.findViewById(R.id.today_memo_dialog_back2);
                        back2.setText(R.string.back);
                        back2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                im.setVisibility(View.GONE);
                                window2.setVisibility(View.GONE);
                            }
                        });
                        Map<Integer,String> map1=itemViews.getOrDefault(newView,new HashMap<>());

                        view.findViewById(R.id.today_memo_dialog_rd).setVisibility(View.GONE);

                        view.findViewById(R.id.today_memo_dialog_rw).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.today_memo_dialog_rw).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(REPEAT_EVERY_WEEK_IN_WEEK,view,context,newView,t,true);
                            }
                        });

                        view.findViewById(R.id.today_memo_dialog_rm).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.today_memo_dialog_rm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(REPEAT_EVERY_MONTH_IN_WEEK,view,context,newView,t,true);
                            }
                        });

                        view.findViewById(R.id.today_memo_dialog_ry).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.today_memo_dialog_ry).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(REPEAT_EVERY_YEAR_IN_WEEK,view,context,newView,t,true);
                            }
                        });

                        if (f.containRepeat(newView)) {
                            view.findViewById(R.id.today_memo_dialog_nr).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.today_memo_dialog_re).setVisibility(View.VISIBLE);
                        }
                        else {
                            view.findViewById(R.id.today_memo_dialog_nr).setVisibility(View.GONE);
                            view.findViewById(R.id.today_memo_dialog_re).setVisibility(View.GONE);
                        }
                        view.findViewById(R.id.today_memo_dialog_nr).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(NO_REPEAT,view,context,newView,t,true);
                            }
                        });
                        view.findViewById(R.id.today_memo_dialog_re).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(END_DAY,view,context,newView,t,true);
                            }
                        });
                        view.findViewById(R.id.today_memo_dialog_oncheckd).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(DO_NOT_SHOW_ON_CHECKED,view,context,newView,t,true);
                            }
                        });
                    }
                });

                updateItemBar(view,context,newView,t);

                return editText;
            }
            case MONTH:{
                Date date = new Date(t);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                date.setTime(calendar.getTimeInMillis());
                if (!itemViews.get(newView).containsKey(START_DAY)) {

                    itemViews.get(newView).put(START_DAY, DateData.getNowDate(date.getTime()));
                }
                TextView textView=newView.findViewById(R.id.today_memo_dialog_text);

                if (DateData.getNowDate(date.getTime()).equals(itemViews.get(newView).get(START_DAY)))
                    textView.setText(TextBitmap.getMonthText(DateData.getYear(t), DateData.getMonth(t), context, null,null));
                else
                    textView.setText(DateData.getNowMonth(DateData.getLongOnTime(itemViews.get(newView).get(START_DAY)),context));

                final EditText editText=newView.findViewById(R.id.today_memo_dialog_data);
                editText.setHint(R.string.to_do_in_month);
                editText.setText(text);
                if (request){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editText.requestFocus();
                        }
                    },100);
                }
                ViewFlipper viewFlipper=newView.findViewById(R.id.today_memo_dialog_VF);
                viewFlipper.showNext();
                items.addView(newView);
                itemViews.get(newView).put(TO_DO_IN_THE_MONTH,"");
                newView.findViewById(R.id.today_memo_dialog_plus).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyboard(activity);
                        im.setVisibility(View.VISIBLE);
                        window2.setVisibility(View.VISIBLE);
                        window2.showNext();
                        view.findViewById(R.id.today_memo_dialog_mode_window).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_add_mode).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_mode_list).setVisibility(View.VISIBLE);
                        TextView back2=view.findViewById(R.id.today_memo_dialog_back2);
                        back2.setText(R.string.back);
                        back2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                im.setVisibility(View.GONE);
                                window2.setVisibility(View.GONE);
                            }
                        });
                        Map<Integer,String> map1=itemViews.getOrDefault(newView,new HashMap<>());

                        view.findViewById(R.id.today_memo_dialog_rd).setVisibility(View.GONE);
                        view.findViewById(R.id.today_memo_dialog_rw).setVisibility(View.GONE);

                        view.findViewById(R.id.today_memo_dialog_rm).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.today_memo_dialog_rm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(REPEAT_EVERY_MONTH_IN_MONTH,view,context,newView,t,true);
                            }
                        });

                        view.findViewById(R.id.today_memo_dialog_ry).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.today_memo_dialog_ry).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(REPEAT_EVERY_YEAR_IN_MONTH,view,context,newView,t,true);
                            }
                        });

                        if (f.containRepeat(newView)) {
                            view.findViewById(R.id.today_memo_dialog_nr).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.today_memo_dialog_re).setVisibility(View.VISIBLE);
                        }
                        else {
                            view.findViewById(R.id.today_memo_dialog_nr).setVisibility(View.GONE);
                            view.findViewById(R.id.today_memo_dialog_re).setVisibility(View.GONE);
                        }
                        view.findViewById(R.id.today_memo_dialog_nr).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(NO_REPEAT,view,context,newView,t,true);
                            }
                        });
                        view.findViewById(R.id.today_memo_dialog_re).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(END_DAY,view,context,newView,t,true);
                            }
                        });
                        view.findViewById(R.id.today_memo_dialog_oncheckd).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmodeWindow(DO_NOT_SHOW_ON_CHECKED,view,context,newView,t,true);
                            }
                        });
                    }
                });

                updateItemBar(view,context,newView,t);

                return editText;
            }
        }
        return null;
    }
    private void updateItemBar(final View view,Context context,final View item,long t){
        class F{
            boolean containRepeat(View v){
                if (itemViews.containsKey(v)){
                    if (
                            itemViews.get(v).containsKey(REPEAT_EVERY_DAY)||
                                    itemViews.get(v).containsKey(REPEAT_EVERY_WEEK)||
                                    itemViews.get(v).containsKey(REPEAT_EVERY_MONTH)||
                                    itemViews.get(v).containsKey(REPEAT_EVERY_YEAR)||
                                    itemViews.get(v).containsKey(REPEAT_EVERY_YEAR_NO_VF)||
                                    itemViews.get(v).containsKey(REPEAT_EVERY_MONTH_NO_REPEAT)||
                                    itemViews.get(v).containsKey(REPEAT_EVERY_WEEK_IN_WEEK)||
                                    itemViews.get(v).containsKey(REPEAT_EVERY_MONTH_IN_WEEK)||
                                    itemViews.get(v).containsKey(REPEAT_EVERY_MONTH_IN_WEEK_NO_REPEAT)||
                                    itemViews.get(v).containsKey(REPEAT_EVERY_YEAR_IN_WEEK)||
                                    itemViews.get(v).containsKey(REPEAT_EVERY_YEAR_IN_WEEK_NO_VF)||
                                    itemViews.get(v).containsKey(REPEAT_EVERY_MONTH_IN_MONTH)||
                                    itemViews.get(v).containsKey(REPEAT_EVERY_YEAR_IN_MONTH)
                    ){
                        return true;
                    }

                }
                return false;
            }
        }
        F f=new F();

        if (!f.containRepeat(item)){
            itemViews.getOrDefault(item,new HashMap<>()).remove(END_DAY);
        }


        LinearLayout ll=item.findViewById(R.id.today_memo_dialog_mode);
        ll.removeAllViews();
        Map<Integer,String> map=itemViews.get(item);

        List<Integer> list = new ArrayList<>(itemViews.get(item).keySet());
        Collections.sort(list);


        for (int k:list){
            View nv=View.inflate(context,R.layout.today_memo_item_bar_item,null);
            nv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.findViewById(R.id.today_memo_dialog_add_window2).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.today_memo_dialog_im).setVisibility(View.VISIBLE);
                    setmodeWindow(k,view,context,item,t,false);
                }
            });
            String value=map.get(k);

            switch (k){
                case REPEAT_EVERY_DAY:{
                    TextView textView=nv.findViewById(R.id.today_memo_bar_item_text);
                    try {
                        int i = Integer.valueOf(value);
                        if (i==1){
                            textView.setText(R.string.repeat_every_day);
                        }
                        else {
                            ImageView imageView=nv.findViewById(R.id.today_memo_bar_item_im);
                            imageView.setImageResource(R.drawable.repeat);
                            imageView.setColorFilter(Color.WHITE);
                            textView.setText(i+context.getString(R.string.days));
                        }
                    }catch (Exception e){}
                    ll.addView(nv);
                    break;
                }
                case REPEAT_EVERY_WEEK:{
                    TextView textView=nv.findViewById(R.id.today_memo_bar_item_text);
                    try {
                        int i = Integer.valueOf(value.split(",")[0]);
                        if (i==1){
                            textView.setText(R.string.repeat_every_week);
                        }
                        else {
                            ImageView imageView=nv.findViewById(R.id.today_memo_bar_item_im);
                            imageView.setImageResource(R.drawable.repeat);
                            imageView.setColorFilter(Color.WHITE);
                            textView.setText(i+context.getString(R.string.weeks));
                        }
                    }catch (Exception e){}
                    ll.addView(nv);
                    break;
                }
                case REPEAT_EVERY_MONTH:{
                    TextView textView=nv.findViewById(R.id.today_memo_bar_item_text);
                    try {
                        int i = Integer.valueOf(value.split(",")[0]);
                        if (i==1){
                            textView.setText(R.string.repeat_every_month);
                        }
                        else {
                            ImageView imageView=nv.findViewById(R.id.today_memo_bar_item_im);
                            imageView.setImageResource(R.drawable.repeat);
                            imageView.setColorFilter(Color.WHITE);
                            textView.setText(i+context.getString(R.string.months));
                        }
                    }catch (Exception e){}
                    ll.addView(nv);
                    break;
                }
                case REPEAT_EVERY_YEAR:{
                    TextView textView=nv.findViewById(R.id.today_memo_bar_item_text);
                    try {
                        int i = Integer.valueOf(value.split(",")[0]);
                        if (i==1){
                            textView.setText(R.string.repeat_every_year);
                        }
                        else {
                            ImageView imageView=nv.findViewById(R.id.today_memo_bar_item_im);
                            imageView.setImageResource(R.drawable.repeat);
                            imageView.setColorFilter(Color.WHITE);
                            textView.setText(i+context.getString(R.string.years));
                        }
                    }catch (Exception e){}
                    ll.addView(nv);
                    break;
                }
                case REPEAT_EVERY_WEEK_IN_WEEK:{
                    TextView textView=nv.findViewById(R.id.today_memo_bar_item_text);
                    try {
                        int i = Integer.valueOf(value);
                        if (i==1){
                            textView.setText(R.string.repeat_every_week);
                        }
                        else {
                            ImageView imageView=nv.findViewById(R.id.today_memo_bar_item_im);
                            imageView.setImageResource(R.drawable.repeat);
                            imageView.setColorFilter(Color.WHITE);
                            textView.setText(i+context.getString(R.string.weeks));
                        }
                    }catch (Exception e){}
                    ll.addView(nv);
                    break;
                }
                case REPEAT_EVERY_MONTH_IN_WEEK:{
                    TextView textView=nv.findViewById(R.id.today_memo_bar_item_text);
                    try {
                        int i = Integer.valueOf(value.split(",")[0]);
                        if (i==1){
                            textView.setText(R.string.repeat_every_month);
                        }
                        else {
                            ImageView imageView=nv.findViewById(R.id.today_memo_bar_item_im);
                            imageView.setImageResource(R.drawable.repeat);
                            imageView.setColorFilter(Color.WHITE);
                            textView.setText(i+context.getString(R.string.months));
                        }
                    }catch (Exception e){}
                    ll.addView(nv);
                    break;
                }
                case REPEAT_EVERY_YEAR_IN_WEEK:{
                    TextView textView=nv.findViewById(R.id.today_memo_bar_item_text);
                    try {
                        int i = Integer.valueOf(value.split(",")[0]);
                        if (i==1){
                            textView.setText(R.string.repeat_every_year);
                        }
                        else {
                            ImageView imageView=nv.findViewById(R.id.today_memo_bar_item_im);
                            imageView.setImageResource(R.drawable.repeat);
                            imageView.setColorFilter(Color.WHITE);
                            textView.setText(i+context.getString(R.string.years));
                        }
                    }catch (Exception e){}
                    ll.addView(nv);
                    break;
                }
                case REPEAT_EVERY_MONTH_IN_MONTH:{
                    TextView textView=nv.findViewById(R.id.today_memo_bar_item_text);
                    try {
                        int i = Integer.valueOf(value);
                        if (i==1){
                            textView.setText(R.string.repeat_every_month);
                        }
                        else {
                            ImageView imageView=nv.findViewById(R.id.today_memo_bar_item_im);
                            imageView.setImageResource(R.drawable.repeat);
                            imageView.setColorFilter(Color.WHITE);
                            textView.setText(i+context.getString(R.string.months));
                        }
                    }catch (Exception e){}
                    ll.addView(nv);
                    break;
                }
                case REPEAT_EVERY_YEAR_IN_MONTH:{
                    TextView textView=nv.findViewById(R.id.today_memo_bar_item_text);
                    try {
                        int i = Integer.valueOf(value.split(",")[0]);
                        if (i==1){
                            textView.setText(R.string.repeat_every_year);
                        }
                        else {
                            ImageView imageView=nv.findViewById(R.id.today_memo_bar_item_im);
                            imageView.setImageResource(R.drawable.repeat);
                            imageView.setColorFilter(Color.WHITE);
                            textView.setText(i+context.getString(R.string.years));
                        }
                    }catch (Exception e){}
                    ll.addView(nv);
                    break;
                }
                case END_DAY:{
                    TextView textView=nv.findViewById(R.id.today_memo_bar_item_text);
                    try {
                        long i=DateData.getLongOnTime(value);
                        textView.setText(context.getString(R.string.end_in)+DateData.getNowDate(i,context));
                    }catch (Exception e){}
                    ll.addView(nv);
                    break;
                }
                case DO_NOT_SHOW_ON_CHECKED:{
                    TextView textView=nv.findViewById(R.id.today_memo_bar_item_text);
                    try {
                        long i=DateData.getLongOnTime(value);
                        textView.setText(context.getString(R.string.Do_not_show_on_checked));
                    }catch (Exception e){}
                    ll.addView(nv);
                    break;
                }
            }
        }
    }
    private static class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }
    public static int dip(Context context,int value){
        return (int) (value * context.getResources().getDisplayMetrics().density);
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}