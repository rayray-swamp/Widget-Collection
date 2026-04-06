package configure;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import AnalogClock.AnalogClock;
import DigitalClock.DigitalClock;
import Schedule.Schedule;
import calendar.DateData;
import calendar.MainActivity;
import calendar.NewAppWidget;
import calendar.R;
import calendar.TextBitmap;
import save.ObjectStorage;
import simple_memo.Simple_Memo;
import todays_memo.GridAdapter2;
import todays_memo.WidgetSizeProvider;
import todays_memo.todays_memo;

import static Schedule.Schedule.dip;
import static calendar.NewAppWidget.CALENDAR_DATA;
import static calendar.NewAppWidget.FLIPPER_NONE;
import static todays_memo.TodayMemoRemoteViewsFactory.LAYOUT_W;
import static todays_memo.TodayMemoRemoteViewsFactory.LIST_ID;

public class AppWidgetConfigureActivity extends AppCompatActivity {
    private int appWidgetId=0;
    private final int SIMPLE_MEMO=R.layout.simple__memo;
    private final int DIGITAL_CLOCK=R.layout.digital_clock;
    private final int SCHEDULE=R.layout.schedule;
    private final int TODAYS_MEMO=R.layout.todays_memo;
    private final int CALENDAR=R.layout.new_app_widget;
    private final int ANALOG_CLOCK=R.layout.analog_clock;
    private int initialKeyguardLayout=0;
    private Map<String,String> data=new HashMap<>();
    private static final int PERMISSION_WRITE_EX_STR = 123456789;
    private boolean fromMain=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_widget_configure_layout);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            fromMain=extras.getBoolean("FromMain",false);
            if (fromMain) {
                initialKeyguardLayout = appWidgetId;
                appWidgetId=-1;
                findViewById(R.id.configure_done).setVisibility(View.GONE);
            }
            else
                initialKeyguardLayout=AppWidgetManager.getInstance(this).getAppWidgetInfo(appWidgetId).initialKeyguardLayout;
        }
        else
            return;



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewFlipper viewFlipper=findViewById(R.id.configure_scale_in);
                viewFlipper.showNext();
                setact();
            }
        },200);

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public void setact(){
        final int width = this.getWindow().getDecorView().getRight()- Simple_Memo.dip(this,40);
        final int height = Simple_Memo.dip(this,300);

        Map<String,String>langs=new LinkedHashMap<>();

        langs.put(null,"Device");
        langs.put("en","English");
        langs.put("ja","日本語");

        if (fromMain){
            findViewById(R.id.configure_back).setVisibility(View.VISIBLE);
            findViewById(R.id.configure_reset).setVisibility(View.VISIBLE);

        }



        FrameLayout screen=findViewById(R.id.configure_sc);
        LinearLayout select=findViewById(R.id.configure_ll);
        FrameLayout set=findViewById(R.id.configure_fl);
        ViewFlipper vf=findViewById(R.id.configure_vf);
        View done=findViewById(R.id.configure_done);
        screen.removeAllViews();
        select.removeAllViews();
        Log.d("height",height+"");
        switch (initialKeyguardLayout){
            case SIMPLE_MEMO:{
                //Data  color, text color, font
                data.clear();
                GridAdapterFont gridAdapter=new GridAdapterFont(getApplicationContext());

                String color_key="Color";
                String text_color_key="TextColor";
                String font_key="Font";
                data.put(color_key,String.valueOf(Color.WHITE));
                data.put(text_color_key,String.valueOf(Color.BLACK));
                data.put(font_key,null);
                Map<String,String> style= ObjectStorage.get(Simple_Memo.SIMPLE_MEMO_DATA+appWidgetId, Map.class,this);
                if (style!=null){
                    data.putAll(style);;
                }


                String defaultkey=MainActivity.SIMPLE_MEMO;
                Map<String,String> defaultstyle= ObjectStorage.get(defaultkey+MainActivity.DEFAULT,
                        Map.class,this);
                if ((fromMain||style==null)&&(defaultstyle!=null)){
                    data.putAll(defaultstyle);
                }

                findViewById(R.id.configure_reset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectStorage.clear(defaultkey+MainActivity.DEFAULT,getApplicationContext());
                        ViewFlipper viewFlipper=findViewById(R.id.configure_scale_in);
                        viewFlipper.showNext();
                        setact();
                    }
                });
                findViewById(R.id.configure_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectStorage.save(data,defaultkey+MainActivity.DEFAULT,getApplicationContext());
                        ObjectStorage.save(MainActivity.getViewBitmap(screen),defaultkey,getApplicationContext());
                        Intent configIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(configIntent);overridePendingTransition(R.anim.slide_in_left, R.anim.scale_out);
                    }
                });
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view=screen;

                        ObjectStorage.save(data,Simple_Memo.SIMPLE_MEMO_DATA+appWidgetId,getApplicationContext());
                        Simple_Memo.updateAppWidget(getApplicationContext(),AppWidgetManager.getInstance(getApplicationContext()),appWidgetId);
                        Intent resultValue = new Intent();
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        setResult(RESULT_OK, resultValue);
                        finish();
                    }
                });

                screen.setLayoutParams(new FrameLayout.LayoutParams((int) (width*0.8), (int) (height*0.8)));
                View sc=inflate(R.layout.simple__memo,null);
                View color=inflate(R.layout.configure_item_color,null);
                View textcolor=inflate(R.layout.configure_item_color,null);
                View font=inflate(R.layout.configure_item_font,null);

                int textSize= (int) getResources().getDimensionPixelSize(R.dimen.simple_memo_textsize);

                screen.addView(sc);
                select.addView(color);
                select.addView(textcolor);
                select.addView(font);
                ((TextView)textcolor.findViewById(R.id.configure_color_title)).setText("Text Color");
                ((TextView)color.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(color_key))));
                ((TextView)textcolor.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(text_color_key))));
                if (data.get(font_key)!=null){
                    Typeface font_ = Typeface.createFromAsset(getAssets(), String.format("fonts/%s", data.get(font_key)));
                    ((TextView) font.findViewById(R.id.configure_font_tx)).setTypeface(font_);
                }
                ((ImageView)sc.findViewById(R.id.simplememo_view)).setImageBitmap(Simple_Memo.getTextBitmap(this,"abcABC123\nHello こんにちは Bonjour Guten tag Hola Päivää 你好 Xin chào Selamat siang Magandang Dobrý den Goede middag Γειά　σαζ tanghali नमस्कार नमस्ते আসসালাম আলাইকুম। வணக்கம் ನಮಸ್ಕಾರ  สวัสดีครับ 안녕하세요 Boa tarde Ciao Merhaba Hej Здравейте Здравствуйте سلام علیکم السلام عليكم שלום"
                        ,(int) (width*0.8), (int) (height*0.8),textSize,data.get(font_key)));
                ((ImageView)sc.findViewById(R.id.simplememo_view_im)).setColorFilter(Integer.valueOf(data.get(color_key)));
                ((ImageView)sc.findViewById(R.id.simplememo_view_im)).setImageAlpha(Color.alpha(Integer.valueOf(data.get(color_key))));
                ((ImageView)sc.findViewById(R.id.simplememo_view)).setColorFilter(Integer.valueOf(data.get(text_color_key)));
                ((ImageView)sc.findViewById(R.id.simplememo_view)).setImageAlpha(Color.alpha(Integer.valueOf(data.get(text_color_key))));

                vf.setInAnimation(this, R.anim.slide_in_right);
                color.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        ImageView imageView=((ImageView)sc.findViewById(R.id.simplememo_view_im));
                        String key=color_key;
                        final int[] color = {Integer.valueOf(data.get(color_key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                    }
                });

                textcolor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        ImageView imageView=((ImageView)sc.findViewById(R.id.simplememo_view));
                        String key=text_color_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });


                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                    }
                });

                font.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_font_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        gv.setAdapter(gridAdapter);
                        String key=font_key;
                        final String[] font = {data.get(key)};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);
                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                if (font[0]!=null) {
                                    Typeface font_ = Typeface.createFromAsset(getAssets(), String.format("fonts/%s", font[0]));
                                    ((TextView) v.findViewById(R.id.configure_font_tx)).setTypeface(font_);
                                }
                                else
                                    ((TextView) v.findViewById(R.id.configure_font_tx)).setTypeface(null);
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                font[0]=gridAdapter.fonts[position];
                                data.put(key,font[0]);
                                ((ImageView)sc.findViewById(R.id.simplememo_view)).setImageBitmap(Simple_Memo.getTextBitmap(getApplicationContext(),"abcABC123\nHello こんにちは Bonjour Guten tag Hola Päivää 你好 Xin chào Selamat siang Magandang Dobrý den Goede middag Γειά　σαζ tanghali नमस्कार नमस्ते আসসালাম আলাইকুম। வணக்கம் ನಮಸ್ಕಾರ  สวัสดีครับ 안녕하세요 Boa tarde Ciao Merhaba Hej Здравейте Здравствуйте سلام علیکم السلام عليكم שלום"
                                        ,(int) (width*0.8), (int) (height*0.8),textSize,font[0]));
                            }
                        });
                    }
                });


                break;
            }
            case DIGITAL_CLOCK:{
                //Data  color, text color, font
                data.clear();
                GridAdapterFont gridAdapter=new GridAdapterFont(getApplicationContext());
                String color_key="Color";
                String color2_key="Color2";
                String text_color_key="TextColor";
                String font_key="Font";
                data.put(color_key,String.valueOf(Color.WHITE));
                data.put(color2_key,String.valueOf(Color.BLACK));
                data.put(text_color_key,String.valueOf(Color.WHITE));
                data.put(font_key,null);
                Map<String,String> style_= ObjectStorage.get(DigitalClock.DIGITAL_CLOCK_DATA+appWidgetId, Map.class,this);
                if (style_!=null){
                    data.putAll(style_);
                }

                String defaultkey=MainActivity.DIGITAL_CLOCK;
                Map<String,String> defaultstyle= ObjectStorage.get(defaultkey+MainActivity.DEFAULT,
                        Map.class,this);
                if ((fromMain||style_==null)&&defaultstyle!=null){
                    data.putAll(defaultstyle);
                }

                findViewById(R.id.configure_reset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectStorage.clear(defaultkey+MainActivity.DEFAULT,getApplicationContext());
                        ViewFlipper viewFlipper=findViewById(R.id.configure_scale_in);
                        viewFlipper.showNext();
                        setact();
                    }
                });
                findViewById(R.id.configure_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectStorage.save(data,defaultkey+MainActivity.DEFAULT,getApplicationContext());
                        ObjectStorage.save(MainActivity.getViewBitmap(screen),defaultkey,getApplicationContext());
                        Intent configIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(configIntent);overridePendingTransition(R.anim.slide_in_left, R.anim.scale_out);
                    }
                });

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view=screen;
                        ObjectStorage.save(data,DigitalClock.DIGITAL_CLOCK_DATA+appWidgetId,getApplicationContext());
                        DigitalClock.updateAppWidget(getApplicationContext(),AppWidgetManager.getInstance(getApplicationContext()),new int[]{appWidgetId});
                        Intent resultValue = new Intent();
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        setResult(RESULT_OK, resultValue);
                        finish();
                    }
                });
                Pair<Integer,Integer> wh=new Pair<>((int)(width*0.8),(int)(height*0.8));

                int diameter= (int) (Math.min(wh.first/2,wh.second)*0.8);
                int half=diameter/2;
                int ts= (int) (diameter*0.8);
                int style=diameter>150?R.drawable.frame_style2:R.drawable.frame_style3;
                screen.setLayoutParams(new FrameLayout.LayoutParams(wh.first,Math.min(wh.first/2,wh.second)));
                View sc=inflate(R.layout.digital_clock,null);
                View color=inflate(R.layout.configure_item_color,null);
                View color2=inflate(R.layout.configure_item_color,null);
                View textcolor=inflate(R.layout.configure_item_color,null);
                View font=inflate(R.layout.configure_item_font,null);

                Bitmap bitmap_back=DigitalClock.getTextImageBitmap(getApplicationContext(),"",ts,new Pair<>(wh.first,Math.min(wh.first/2,wh.second))
                        ,Color.WHITE,Color.BLACK,diameter>150?R.drawable.frame_style:diameter>100?R.drawable.frame_style2:R.drawable.frame_style3,null);
                ((ImageView)sc.findViewById(R.id.digital_clock_back_img)).setImageBitmap(bitmap_back);
                ((ImageView)sc.findViewById(R.id.digital_clock_back_img)).setImageAlpha(Color.alpha(Integer.valueOf(data.get(color_key))));
                ((ImageView)sc.findViewById(R.id.digital_clock_back_img)).setColorFilter(Integer.valueOf(data.get(color_key)));


                int textSize= ts;

                screen.addView(sc);
                select.addView(color);
                select.addView(color2);
                select.addView(textcolor);
                select.addView(font);
                ((TextView)color2.findViewById(R.id.configure_color_title)).setText("Color2");
                ((TextView)textcolor.findViewById(R.id.configure_color_title)).setText("Text Color");

                ((TextView)color.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(color_key))));
                ((TextView)color2.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(color2_key))));
                ((TextView)textcolor.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(text_color_key))));
                if (data.get(font_key)!=null){
                    Typeface font_ = Typeface.createFromAsset(getAssets(), String.format("fonts/%s", data.get(font_key)));
                    ((TextView) font.findViewById(R.id.configure_font_tx)).setTypeface(font_);
                }

                int[] f=new int[]{(DateData.getHour())/10,(DateData.getHour())%10
                        ,(DateData.getMinute())/10,(DateData.getMinute())%10};
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
                for (int i=0;i<4;i++){

                    sc.findViewById(tfv[i]).setVisibility(View.GONE);
                    sc.findViewById(tfv2[i]).setVisibility(View.GONE);
                    sc.findViewById(bfv[i]).setVisibility(View.GONE);
                    Bitmap bitmap_f=DigitalClock.getTextImageBitmap(getApplicationContext(),f[i]+""
                            ,ts,new Pair<>(diameter/2,diameter),Integer.valueOf(data.get(color2_key))
                            ,Integer.valueOf(data.get(text_color_key)),style,data.get(font_key));

                    ((ImageView)sc.findViewById(th[i])).setImageBitmap(Bitmap.createBitmap(1,(Math.min(wh.first/2,wh.second)-diameter)/2, Bitmap.Config.ARGB_8888));
                    Bitmap bitmap_tb=Bitmap.createBitmap(bitmap_f,0,0,half,half);
                    Bitmap bitmap_bf=Bitmap.createBitmap(bitmap_f,0,half,half,half);

                    ((ImageView)sc.findViewById(tb[i])).setImageBitmap(bitmap_tb);
                    ((ImageView)sc.findViewById(bb[i])).setImageBitmap(bitmap_bf);

                }

                vf.setInAnimation(this, R.anim.slide_in_right);
                color.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        ImageView imageView=((ImageView)sc.findViewById(R.id.digital_clock_back_img));
                        String key=color_key;
                        final int[] color = {Integer.valueOf(data.get(color_key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                    }
                });
                color2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        alpha.setVisibility(View.GONE);
                        String key=color2_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });


                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                for (int i=0;i<4;i++){

                                    Bitmap bitmap_f=DigitalClock.getTextImageBitmap(getApplicationContext(),f[i]+""
                                            ,ts,new Pair<>(diameter/2,diameter),color[0],Integer.valueOf(data.get(text_color_key)),style,data.get(font_key));

                                    ((ImageView)sc.findViewById(th[i])).setImageBitmap(Bitmap.createBitmap(1,(Math.min(wh.first/2,wh.second)-diameter)/2, Bitmap.Config.ARGB_8888));
                                    Bitmap bitmap_tb=Bitmap.createBitmap(bitmap_f,0,0,half,half);
                                    Bitmap bitmap_bf=Bitmap.createBitmap(bitmap_f,0,half,half,half);

                                    ((ImageView)sc.findViewById(tb[i])).setImageBitmap(bitmap_tb);
                                    ((ImageView)sc.findViewById(bb[i])).setImageBitmap(bitmap_bf);

                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                for (int i=0;i<4;i++){

                                    Bitmap bitmap_f=DigitalClock.getTextImageBitmap(getApplicationContext(),f[i]+""
                                            ,ts,new Pair<>(diameter/2,diameter),color[0],Integer.valueOf(data.get(text_color_key)),style,data.get(font_key));

                                    ((ImageView)sc.findViewById(th[i])).setImageBitmap(Bitmap.createBitmap(1,(Math.min(wh.first/2,wh.second)-diameter)/2, Bitmap.Config.ARGB_8888));
                                    Bitmap bitmap_tb=Bitmap.createBitmap(bitmap_f,0,0,half,half);
                                    Bitmap bitmap_bf=Bitmap.createBitmap(bitmap_f,0,half,half,half);

                                    ((ImageView)sc.findViewById(tb[i])).setImageBitmap(bitmap_tb);
                                    ((ImageView)sc.findViewById(bb[i])).setImageBitmap(bitmap_bf);

                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                for (int i=0;i<4;i++){

                                    Bitmap bitmap_f=DigitalClock.getTextImageBitmap(getApplicationContext(),f[i]+""
                                            ,ts,new Pair<>(diameter/2,diameter),color[0],Integer.valueOf(data.get(text_color_key)),style,data.get(font_key));

                                    ((ImageView)sc.findViewById(th[i])).setImageBitmap(Bitmap.createBitmap(1,(Math.min(wh.first/2,wh.second)-diameter)/2, Bitmap.Config.ARGB_8888));
                                    Bitmap bitmap_tb=Bitmap.createBitmap(bitmap_f,0,0,half,half);
                                    Bitmap bitmap_bf=Bitmap.createBitmap(bitmap_f,0,half,half,half);

                                    ((ImageView)sc.findViewById(tb[i])).setImageBitmap(bitmap_tb);
                                    ((ImageView)sc.findViewById(bb[i])).setImageBitmap(bitmap_bf);

                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];

                                for (int i=0;i<4;i++){

                                    Bitmap bitmap_f=DigitalClock.getTextImageBitmap(getApplicationContext(),f[i]+""
                                            ,ts,new Pair<>(diameter/2,diameter),color[0],Integer.valueOf(data.get(text_color_key)),style,data.get(font_key));

                                    ((ImageView)sc.findViewById(th[i])).setImageBitmap(Bitmap.createBitmap(1,(Math.min(wh.first/2,wh.second)-diameter)/2, Bitmap.Config.ARGB_8888));
                                    Bitmap bitmap_tb=Bitmap.createBitmap(bitmap_f,0,0,half,half);
                                    Bitmap bitmap_bf=Bitmap.createBitmap(bitmap_f,0,half,half,half);

                                    ((ImageView)sc.findViewById(tb[i])).setImageBitmap(bitmap_tb);
                                    ((ImageView)sc.findViewById(bb[i])).setImageBitmap(bitmap_bf);

                                }

                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                    }
                });
                textcolor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        alpha.setVisibility(View.GONE);
                        String key=text_color_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });


                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                for (int i=0;i<4;i++){

                                    Bitmap bitmap_f=DigitalClock.getTextImageBitmap(getApplicationContext(),f[i]+""
                                            ,ts,new Pair<>(diameter/2,diameter),Integer.valueOf(data.get(color2_key)),color[0],style,data.get(font_key));

                                    ((ImageView)sc.findViewById(th[i])).setImageBitmap(Bitmap.createBitmap(1,(Math.min(wh.first/2,wh.second)-diameter)/2, Bitmap.Config.ARGB_8888));
                                    Bitmap bitmap_tb=Bitmap.createBitmap(bitmap_f,0,0,half,half);
                                    Bitmap bitmap_bf=Bitmap.createBitmap(bitmap_f,0,half,half,half);

                                    ((ImageView)sc.findViewById(tb[i])).setImageBitmap(bitmap_tb);
                                    ((ImageView)sc.findViewById(bb[i])).setImageBitmap(bitmap_bf);

                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                for (int i=0;i<4;i++){

                                    Bitmap bitmap_f=DigitalClock.getTextImageBitmap(getApplicationContext(),f[i]+""
                                            ,ts,new Pair<>(diameter/2,diameter),Integer.valueOf(data.get(color2_key)),color[0],style,data.get(font_key));

                                    ((ImageView)sc.findViewById(th[i])).setImageBitmap(Bitmap.createBitmap(1,(Math.min(wh.first/2,wh.second)-diameter)/2, Bitmap.Config.ARGB_8888));
                                    Bitmap bitmap_tb=Bitmap.createBitmap(bitmap_f,0,0,half,half);
                                    Bitmap bitmap_bf=Bitmap.createBitmap(bitmap_f,0,half,half,half);

                                    ((ImageView)sc.findViewById(tb[i])).setImageBitmap(bitmap_tb);
                                    ((ImageView)sc.findViewById(bb[i])).setImageBitmap(bitmap_bf);

                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                for (int i=0;i<4;i++){

                                    Bitmap bitmap_f=DigitalClock.getTextImageBitmap(getApplicationContext(),f[i]+""
                                            ,ts,new Pair<>(diameter/2,diameter),Integer.valueOf(data.get(color2_key)),color[0],style,data.get(font_key));

                                    ((ImageView)sc.findViewById(th[i])).setImageBitmap(Bitmap.createBitmap(1,(Math.min(wh.first/2,wh.second)-diameter)/2, Bitmap.Config.ARGB_8888));
                                    Bitmap bitmap_tb=Bitmap.createBitmap(bitmap_f,0,0,half,half);
                                    Bitmap bitmap_bf=Bitmap.createBitmap(bitmap_f,0,half,half,half);

                                    ((ImageView)sc.findViewById(tb[i])).setImageBitmap(bitmap_tb);
                                    ((ImageView)sc.findViewById(bb[i])).setImageBitmap(bitmap_bf);

                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];

                                for (int i=0;i<4;i++){

                                    Bitmap bitmap_f=DigitalClock.getTextImageBitmap(getApplicationContext(),f[i]+""
                                            ,ts,new Pair<>(diameter/2,diameter),Integer.valueOf(data.get(color2_key)),color[0],style,data.get(font_key));

                                    ((ImageView)sc.findViewById(th[i])).setImageBitmap(Bitmap.createBitmap(1,(Math.min(wh.first/2,wh.second)-diameter)/2, Bitmap.Config.ARGB_8888));
                                    Bitmap bitmap_tb=Bitmap.createBitmap(bitmap_f,0,0,half,half);
                                    Bitmap bitmap_bf=Bitmap.createBitmap(bitmap_f,0,half,half,half);

                                    ((ImageView)sc.findViewById(tb[i])).setImageBitmap(bitmap_tb);
                                    ((ImageView)sc.findViewById(bb[i])).setImageBitmap(bitmap_bf);

                                }

                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                    }
                });

                font.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_font_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        gv.setAdapter(gridAdapter);
                        String key=font_key;
                        final String[] font = {data.get(key)};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);
                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                if (font[0]!=null) {
                                    Typeface font_ = Typeface.createFromAsset(getAssets(), String.format("fonts/%s", font[0]));
                                    ((TextView) v.findViewById(R.id.configure_font_tx)).setTypeface(font_);
                                }
                                else
                                    ((TextView) v.findViewById(R.id.configure_font_tx)).setTypeface(null);
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                font[0]=gridAdapter.fonts[position];
                                data.put(key,font[0]);
                                for (int i=0;i<4;i++){

                                    Bitmap bitmap_f=DigitalClock.getTextImageBitmap(getApplicationContext(),f[i]+""
                                            ,ts,new Pair<>(diameter/2,diameter),Integer.valueOf(data.get(color2_key))
                                            ,Integer.valueOf(data.get(text_color_key)),style,font[0]);

                                    ((ImageView)sc.findViewById(th[i])).setImageBitmap(Bitmap.createBitmap(1,(Math.min(wh.first/2,wh.second)-diameter)/2, Bitmap.Config.ARGB_8888));
                                    Bitmap bitmap_tb=Bitmap.createBitmap(bitmap_f,0,0,half,half);
                                    Bitmap bitmap_bf=Bitmap.createBitmap(bitmap_f,0,half,half,half);

                                    ((ImageView)sc.findViewById(tb[i])).setImageBitmap(bitmap_tb);
                                    ((ImageView)sc.findViewById(bb[i])).setImageBitmap(bitmap_bf);

                                }
                            }
                        });
                    }
                });


                break;
            }
            case SCHEDULE:{
                //Data  color, text color, font
                data.clear();
                GridAdapterFont gridAdapter=new GridAdapterFont(getApplicationContext());

                String color_key="Color";//back color
                String color2_key="Color2";//＋など color

                String language_key="Language";

                String effect_key="Effect";//even color effect

                String day_text_color_key="DayTextColor";//text color
                String day_font_key="DayFont";//font

                String text_color_key="TextColor";//event text color
                String font_key="Font";//event font
                data.put(color_key,String.valueOf(Color.WHITE));
                data.put(color2_key,String.valueOf(getColor(R.color.gray)));
                data.put(effect_key,100+","+1f+","+1f);//alpha  彩度　明度
                data.put(text_color_key,String.valueOf(Color.BLACK));
                data.put(font_key,null);
                data.put(day_text_color_key,String.valueOf(Color.BLACK));
                data.put(day_font_key,null);
                Map<String,String> style= ObjectStorage.get(Schedule.SCHEDULE +appWidgetId, Map.class,this);
                data.put(language_key,null);
                if (style!=null){
                    data.putAll(style);;
                }

                String defaultkey=MainActivity.SCHEDULE;
                Map<String,String> defaultstyle= ObjectStorage.get(defaultkey+MainActivity.DEFAULT,
                        Map.class,this);
                if ((fromMain||style==null)&&(defaultstyle!=null)){
                    data.putAll(defaultstyle);
                }

                findViewById(R.id.configure_reset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectStorage.clear(defaultkey+MainActivity.DEFAULT,getApplicationContext());
                        ViewFlipper viewFlipper=findViewById(R.id.configure_scale_in);
                        viewFlipper.showNext();
                        setact();
                    }
                });
                findViewById(R.id.configure_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectStorage.save(data,defaultkey+MainActivity.DEFAULT,getApplicationContext());
                        ObjectStorage.save(MainActivity.getViewBitmap(screen),defaultkey,getApplicationContext());
                        Intent configIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(configIntent);overridePendingTransition(R.anim.slide_in_left, R.anim.scale_out);
                    }
                });

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view=screen;
                        ObjectStorage.save(data,Schedule.SCHEDULE+appWidgetId,getApplicationContext());
                        Schedule.updateAppWidgets(getApplicationContext(),AppWidgetManager.getInstance(getApplicationContext())
                                ,new int[]{appWidgetId}, System.currentTimeMillis());
                        Intent resultValue = new Intent();
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        setResult(RESULT_OK, resultValue);
                        checkPermission();
                    }
                });

                screen.setLayoutParams(new FrameLayout.LayoutParams((int) (width*0.8), (int) (height*0.8)));
                View sc=inflate(R.layout.schedule,null);
                View color=inflate(R.layout.configure_item_color,null);
                View color2=inflate(R.layout.configure_item_color,null);
                View day_textcolor=inflate(R.layout.configure_item_color,null);
                View day_font=inflate(R.layout.configure_item_font,null);
                View textcolor=inflate(R.layout.configure_item_color,null);
                View font=inflate(R.layout.configure_item_font,null);
                View lang=inflate(R.layout.configure_item_lang,null);

                int textSize= (int) getResources().getDimensionPixelSize(R.dimen.simple_memo_textsize);

                //start minute , end minute , text , color , eventid ,all day
                List<String[]> list =new ArrayList<>();
                list.add(new String[]{"5","230",getString(R.string.event1),""+Color.RED,"0",""+false});
                list.add(new String[]{"10","90",getString(R.string.event2),""+Color.BLUE,"0",""+false});
                list.add(new String[]{"110","270",getString(R.string.event3),""+Color.GREEN,"0",""+false});
                list.add(new String[]{"120","250",getString(R.string.event4),""+Color.YELLOW,"0",""+false});

                List<String[]> tasks=Schedule.getFrame(list);

                screen.addView(sc);
                select.addView(color);
                select.addView(color2);
                select.addView(day_textcolor);
                select.addView(day_font);
                select.addView(textcolor);
                select.addView(font);
                select.addView(lang);
                ((TextView)day_textcolor.findViewById(R.id.configure_color_title)).setText("Text Color");
                ((TextView)textcolor.findViewById(R.id.configure_color_title)).setText("Event Text Color");
                ((TextView)font.findViewById(R.id.configure_font_title)).setText("Event Font");
                ((TextView)color2.findViewById(R.id.configure_color_title)).setText("Color2");
                ((TextView)color.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(color_key))));
                ((TextView)color2.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(color2_key))));
                ((TextView)day_textcolor.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(day_text_color_key))));
                if (data.get(day_font_key)!=null){
                    Typeface font_ = Typeface.createFromAsset(getAssets(), String.format("fonts/%s", data.get(day_font_key)));
                    ((TextView) day_font.findViewById(R.id.configure_font_tx)).setTypeface(font_);
                }
                ((TextView)textcolor.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(text_color_key))));
                if (data.get(font_key)!=null){
                    Typeface font_ = Typeface.createFromAsset(getAssets(), String.format("fonts/%s", data.get(font_key)));
                    ((TextView) font.findViewById(R.id.configure_font_tx)).setTypeface(font_);
                }

                if (data.get(language_key)!=null){
                    ((TextView)lang.findViewById(R.id.configure_lang_tx)).setText(langs.get(data.get(language_key)));
                }



                class SetView{
                    public void setView(){

                        int text_color=Integer.valueOf(data.get(text_color_key));
                        String font =data.get(font_key);

                        LinearLayout ll=sc.findViewById(R.id.schedule_ll);
                        LinearLayout all_day_ll=sc.findViewById(R.id.schedule_all_day_ll);
                        FrameLayout fl=sc.findViewById(R.id.schedule_fl);

                        ll.removeAllViews();
                        all_day_ll.removeAllViews();
                        fl.removeAllViews();
                        int sh=0;
                        Context context=getApplicationContext();

                        setBack();

                        setViewC2();

                        setViewDay();

                        Pair<Integer,Integer> wh=new Pair<>((int)(width*0.8),(int)(height*0.8));

                        float dh=context.getResources().getDimensionPixelSize(R.dimen.dp_on_hour_of_schedule);
                        float dm=dh/60f;
                        int w=wh.first-dip(5,context)*2-dip(15,context);

                        setTask();

                        {
                            View line=inflate(R.layout.schedule_now_line,null);
                            fl.addView(line);
                            int nh = (int) (dm * ((DateData.getHour()-sh)*60+DateData.getMinute()));
                            if (nh > 0)
                                ((ImageView)sc.findViewById(R.id.schedule_now_line_h)).setImageBitmap( Bitmap.createBitmap( 1, nh, Bitmap.Config.ARGB_8888));
                        }

                    }
                    public void setViewC2(){
                        LinearLayout ll=sc.findViewById(R.id.schedule_ll);
                        ll.removeAllViews();
                        int color2=Integer.valueOf(data.get(color2_key));
                        int sh=0;
                        Context context=getApplicationContext();
                        float dh=getResources().getDimensionPixelSize(R.dimen.dp_on_hour_of_schedule);
                        for (int i=sh;i<=24;i++){
                            View line=inflate(R.layout.schedule_line,null);
                            ((ImageView)line.findViewById(R.id.schedule_line_text)).setImageBitmap(TextBitmap.textAsBitmap(getApplicationContext()
                                    ,(i%24)+"",Schedule.converPxToSp(Simple_Memo.dip(context,10),context),color2,data.get(day_font_key)));
                            line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) dh));
                            ((ImageView)line.findViewById(R.id.schedule_line_line)).setColorFilter(color2);
                            ((ImageView)line.findViewById(R.id.schedule_line_line)).setImageAlpha(Color.alpha(color2));
                            ll.addView(line);
                        }

                        ((ImageView)sc.findViewById(R.id.schedule_plus)).setColorFilter(color2);
                        ((ImageView)sc.findViewById(R.id.schedule_plus)).setImageAlpha(Color.alpha(color2));
                    }
                    public void setViewDay(){
                        int text_color_day=Integer.valueOf(data.get(day_text_color_key));
                        String font_day =data.get(day_font_key);

                        long cp=System.currentTimeMillis();
                        Context context=getApplicationContext();

                        ((ImageView)sc.findViewById(R.id.schedule_day)).setImageBitmap(TextBitmap.textAsBitmap(context
                                , "" + DateData.getDateOfMonth(cp), 40, text_color_day, font_day));
                        ((ImageView)sc.findViewById(R.id.schedule_month)).setImageBitmap(TextBitmap.textAsBitmap(context
                                , TextBitmap.getMonthText(
                                        DateData.getYear(cp), DateData.getMonth(cp),DateData.getYear(), context
                                        , null,data.get(language_key)
                                ), 20,text_color_day, font_day));
                        ((ImageView)sc.findViewById(R.id.schedule_dow)).setImageBitmap(TextBitmap.textAsBitmap(context
                                , TextBitmap.getDowTextPE(DateData.getDateOfWeek(cp)-1, context, null,data.get(language_key))
                                , 20, text_color_day, font_day));
                    }
                    public void setBack(){
                        ((ImageView)sc.findViewById(R.id.schedule_img)).setColorFilter(Integer.valueOf(data.get(color_key)));
                        ((ImageView)sc.findViewById(R.id.schedule_img)).setImageAlpha(Color.alpha(Integer.valueOf(data.get(color_key))));
                    }
                    public void setTask(){
                        int sh=0;
                        Context context=getApplicationContext();
                        FrameLayout fl=sc.findViewById(R.id.schedule_fl);
                        fl.removeAllViews();

                        Pair<Integer,Integer> wh=new Pair<>((int)(width*0.8),(int)(height*0.8));

                        float dh=context.getResources().getDimensionPixelSize(R.dimen.dp_on_hour_of_schedule);
                        float dm=dh/60f;
                        int w=wh.first-dip(5,context)*2-dip(15,context);
                        int textSize= (int) context.getResources().getDimensionPixelSize(R.dimen.schedule_textsize);

                        for (String[] t:tasks){
                            View tv=inflate(R.layout.schedule_task,null);

                            int color =Integer.valueOf(t[5]);
                            color = Color.argb(200,Color.red(color),Color.green(color),Color.blue(color));

                            String timetext=(sh+Integer.valueOf(t[0])/60)+":"+(Integer.valueOf(t[0])%60)+"~ ";

                            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(timetext+t[4]);
                            spannableStringBuilder.setSpan(new ForegroundColorSpan(Integer.valueOf(data.get(color2_key))), 0, timetext.length(), 0);
                            spannableStringBuilder.setSpan(new ForegroundColorSpan(Integer.valueOf(data.get(text_color_key))), timetext.length()
                                    ,(timetext+t[4]).length() , 0);
                            CharSequence cs=spannableStringBuilder.subSequence(0, spannableStringBuilder.length());

                            ((ImageView)tv.findViewById(R.id.schedule_task_img)).setImageBitmap(Schedule.getTextBitmap(context,cs
                                    ,new Pair<>((int)(w*Float.valueOf(t[3]))-dip(1,context),(int)(dm*Integer.valueOf(t[1]))-dip(1,context))
                                    ,textSize
                                    ,color,data.get(font_key)));

                            int x= (int) (w*Float.valueOf(t[2]));
                            if (x>0)
                                ((ImageView)tv.findViewById(R.id.schedule_task_w))
                                        .setImageBitmap(Bitmap.createBitmap(x+dip(1,context),1, Bitmap.Config.ARGB_8888));
                            int y= (int) (dm*Float.valueOf(t[0]));
                            if (y>0)
                                ((ImageView)tv.findViewById(R.id.schedule_task_h))
                                        .setImageBitmap(Bitmap.createBitmap(1,y+dip(1,context), Bitmap.Config.ARGB_8888));
                            fl.addView(tv);

                        }
                    }

                }

                SetView setView =new SetView();
                setView.setView();

                vf.setInAnimation(this, R.anim.slide_in_right);
                color.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        ImageView imageView=((ImageView)sc.findViewById(R.id.schedule_img));
                        String key=color_key;
                        final int[] color = {Integer.valueOf(data.get(color_key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                imageView.setColorFilter(color[0]);
                                imageView.setImageAlpha(Color.alpha(color[0]));
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                    }
                });
                color2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        String key=color2_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                                setView.setTask();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                                setView.setTask();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                                setView.setTask();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                                setView.setTask();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                                setView.setTask();
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                    }
                });

                day_textcolor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        String key=day_text_color_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });


                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                    }
                });
                day_font.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_font_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        gv.setAdapter(gridAdapter);
                        String key=day_font_key;
                        final String[] font = {data.get(key)};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);
                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                if (font[0]!=null) {
                                    Typeface font_ = Typeface.createFromAsset(getAssets(), String.format("fonts/%s", font[0]));
                                    ((TextView) v.findViewById(R.id.configure_font_tx)).setTypeface(font_);
                                }
                                else
                                    ((TextView) v.findViewById(R.id.configure_font_tx)).setTypeface(null);
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                font[0]=gridAdapter.fonts[position];
                                data.put(key,font[0]);
                                setView.setViewDay();
                                setView.setViewC2();
                            }
                        });
                    }
                });

                textcolor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        String key=text_color_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });


                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setTask();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setTask();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));
                                setView.setTask();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setTask();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                data.put(key,String.valueOf(color[0]));
                                setView.setTask();
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                    }
                });
                font.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_font_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        gv.setAdapter(gridAdapter);
                        String key=font_key;
                        final String[] font = {data.get(key)};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);
                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                if (font[0]!=null) {
                                    Typeface font_ = Typeface.createFromAsset(getAssets(), String.format("fonts/%s", font[0]));
                                    ((TextView) v.findViewById(R.id.configure_font_tx)).setTypeface(font_);
                                }
                                else
                                    ((TextView) v.findViewById(R.id.configure_font_tx)).setTypeface(null);
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                font[0]=gridAdapter.fonts[position];
                                data.put(key,font[0]);
                                setView.setTask();
                            }
                        });
                    }
                });

                lang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_langt_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        ListView li=l.findViewById(R.id.configure_lang_select_li);

                        ListAdapterLang listAdapterLang=new ListAdapterLang(langs.keySet().toArray(new String[langs.size()])
                                ,getApplicationContext(),langs);

                        li.setAdapter(listAdapterLang);
                        String key=language_key;
                        final String[] language = {data.get(key)};

                        l.findViewById(R.id.configure_lang_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)lang.findViewById(R.id.configure_lang_tx)).setText(langs.get(data.get(key)));
                            }
                        });

                        li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                language[0]=listAdapterLang.list[position];
                                data.put(key,language[0]);
                                setView.setViewDay();
                                setView.setViewC2();
                            }
                        });
                    }
                });

                break;
            }
            case TODAYS_MEMO:{
                //Data  color, text color, font

                data.clear();
                GridAdapterFont gridAdapter=new GridAdapterFont(getApplicationContext());

                String color_key="Color";//back color
                String color2_key="Color2";//＋など color

                String language_key="Language";

                String effect_key="Effect";//even color effect

                String day_text_color_key="DayTextColor";//text color
                String day_font_key="DayFont";//font

                String text_color_key="TextColor";//event text color
                String stext_color_key="SelectedTextColor";//event text color

                String font_key="Font";//event font
                data.put(color_key,String.valueOf(Color.WHITE));
                data.put(color2_key,String.valueOf(getColor(R.color.gray)));
                data.put(effect_key,100+","+1f+","+1f);//alpha  彩度　明度
                data.put(text_color_key,String.valueOf(Color.BLACK));
                data.put(stext_color_key,String.valueOf(getColor(R.color.gray)));
                data.put(font_key,null);
                data.put(day_text_color_key,String.valueOf(Color.BLACK));
                data.put(day_font_key,null);
                data.put(language_key,null);
                Map<String,String> style= ObjectStorage.get(todays_memo.TODAYS_MEMO_DATA +appWidgetId, Map.class,this);
                if (style!=null){
                    data.putAll(style);
                }

                String defaultkey=MainActivity.TODAYS_MEMO;
                Map<String,String> defaultstyle= ObjectStorage.get(defaultkey+MainActivity.DEFAULT,
                        Map.class,this);
                if ((fromMain||style==null)&&(defaultstyle!=null)){
                    data.putAll(defaultstyle);
                }

                findViewById(R.id.configure_reset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectStorage.clear(defaultkey+MainActivity.DEFAULT,getApplicationContext());
                        ViewFlipper viewFlipper=findViewById(R.id.configure_scale_in);
                        viewFlipper.showNext();
                        setact();
                    }
                });
                findViewById(R.id.configure_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectStorage.save(data,defaultkey+MainActivity.DEFAULT,getApplicationContext());
                        ObjectStorage.save(MainActivity.getViewBitmap(screen),defaultkey,getApplicationContext());
                        Intent configIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(configIntent);overridePendingTransition(R.anim.slide_in_left, R.anim.scale_out);
                    }
                });

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view=screen;
                        ObjectStorage.save(data,todays_memo.TODAYS_MEMO_DATA +appWidgetId,getApplicationContext());
                        todays_memo.updateAppWidget(getApplicationContext(),AppWidgetManager.getInstance(getApplicationContext())
                                ,appWidgetId, System.currentTimeMillis());
                        Intent resultValue = new Intent();
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        setResult(RESULT_OK, resultValue);
                        finish();
                    }
                });

                screen.setLayoutParams(new FrameLayout.LayoutParams((int) (width*0.8), (int) (height*0.8)));
                View sc=inflate(R.layout.todays_memo,null);
                View color=inflate(R.layout.configure_item_color,null);
                View color2=inflate(R.layout.configure_item_color,null);
                View day_textcolor=inflate(R.layout.configure_item_color,null);
                View day_font=inflate(R.layout.configure_item_font,null);
                View textcolor=inflate(R.layout.configure_item_color,null);
                View stextcolor=inflate(R.layout.configure_item_color,null);
                View font=inflate(R.layout.configure_item_font,null);
                View lang=inflate(R.layout.configure_item_lang,null);

                int textSize= (int) getResources().getDimensionPixelSize(R.dimen.simple_memo_textsize);


                screen.addView(sc);
                select.addView(color);
                select.addView(color2);
                select.addView(day_textcolor);
                select.addView(day_font);
                select.addView(textcolor);
                select.addView(stextcolor);
                select.addView(font);
                select.addView(lang);

                ((TextView)day_textcolor.findViewById(R.id.configure_color_title)).setText("Text Color");
                ((TextView)textcolor.findViewById(R.id.configure_color_title)).setText("Event Text Color");
                ((TextView)stextcolor.findViewById(R.id.configure_color_title)).setText("Selected Event Text Color");
                ((TextView)font.findViewById(R.id.configure_font_title)).setText("Event Font");
                ((TextView)color2.findViewById(R.id.configure_color_title)).setText("Color2");
                ((TextView)color.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(color_key))));
                ((TextView)color2.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(color2_key))));
                ((TextView)day_textcolor.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(day_text_color_key))));
                if (data.get(day_font_key)!=null){
                    Typeface font_ = Typeface.createFromAsset(getAssets(), String.format("fonts/%s", data.get(day_font_key)));
                    ((TextView) day_font.findViewById(R.id.configure_font_tx)).setTypeface(font_);
                }
                ((TextView)textcolor.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(text_color_key))));
                ((TextView)stextcolor.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(stext_color_key))));
                if (data.get(font_key)!=null){
                    Typeface font_ = Typeface.createFromAsset(getAssets(), String.format("fonts/%s", data.get(font_key)));
                    ((TextView) font.findViewById(R.id.configure_font_tx)).setTypeface(font_);
                }
                if (data.get(language_key)!=null){
                    ((TextView)lang.findViewById(R.id.configure_lang_tx)).setText(langs.get(data.get(language_key)));
                }

                class SetView{
                    public void setView(){
                        setBack();
                        setViewC2();

                        sc.findViewById(R.id.today_memo_view).setVisibility(View.VISIBLE);
                        sc.findViewById(R.id.today_memo_listview).setVisibility(View.GONE);

                        setViewDay();

                        setTask();


                    }
                    public void setViewC2(){
                        int color2=Integer.valueOf(data.get(color2_key));
                        ((ImageView)sc.findViewById(R.id.today_memo_list)).setColorFilter(color2);
                        ((ImageView)sc.findViewById(R.id.today_memo_list)).setImageAlpha(Color.alpha(color2));
                    }
                    public void setViewDay(){
                        int text_color_day=Integer.valueOf(data.get(day_text_color_key));
                        String font_day =data.get(day_font_key);

                        long cp=System.currentTimeMillis();
                        Context context=getApplicationContext();

                        ((ImageView)sc.findViewById(R.id.today_memo_day)).setImageBitmap(TextBitmap.textAsBitmap(context
                                , "" + DateData.getDateOfMonth(cp), 40, text_color_day, font_day));
                        ((ImageView)sc.findViewById(R.id.today_memo_month)).setImageBitmap(TextBitmap.textAsBitmap(context
                                , TextBitmap.getMonthText(
                                        DateData.getYear(cp), DateData.getMonth(cp),DateData.getYear(), context, null
                                        ,data.get(language_key)
                                ), 20,text_color_day, font_day));
                        ((ImageView)sc.findViewById(R.id.today_memo_dow)).setImageBitmap(TextBitmap.textAsBitmap(context
                                , TextBitmap.getDowTextPE(DateData.getDateOfWeek(cp)-1, context, null,data.get(language_key))
                                , 20, text_color_day, font_day));
                    }
                    public void setBack(){
                        ((ImageView)sc.findViewById(R.id.today_memo_img)).setColorFilter(Integer.valueOf(data.get(color_key)));
                        ((ImageView)sc.findViewById(R.id.today_memo_img)).setImageAlpha(Color.alpha(Integer.valueOf(data.get(color_key))));
                    }
                    public void setTask(){



                        Context context=getApplicationContext();


                        Pair<Integer,Integer> wh=new Pair<>((int)(width*0.8),(int)(height*0.8));

                        int w=wh.first;
                        int h=wh.second - (int) context.getResources().getDimensionPixelSize(R.dimen.today_memo_day)
                                -Simple_Memo.dip(context,10);
                        int textSize= (int) context.getResources().getDimensionPixelSize(R.dimen.today_memo_textsize);
                        List<String[]> list= new ArrayList<>();
                        list.add(new String[]{"Γειά　σαζ tanghali नमस्कार नमस्ते আসসালাম আলাইকুম। வணக்கம் ನಮಸ್ಕಾರ  สวัสดีครับ 안녕하세요 Boa tarde Ciao Merhaba Hej Здравейте Здравствуйте سلام علیکم السلام عليكم שלום",String.valueOf(true),String.valueOf(false),String.valueOf(false)});
                        list.add(new String[]{"abcABC123\nHello こんにちは Bonjour Guten tag Hola Päivää 你好 Xin chào Selamat siang Magandang Dobrý den Goede middag"
                                ,String.valueOf(false)});


                        ((ImageView)sc.findViewById(R.id.today_memo_view)).setImageBitmap(Simple_Memo.getTextBitmap(context,
                                todays_memo.getHtmlText(list,Integer.valueOf(data.get(text_color_key)),Integer.valueOf(data.get(stext_color_key)))
                                ,w,h,textSize,data.get(font_key)));

                    }

                }

                SetView setView =new SetView();
                setView.setView();

                vf.setInAnimation(this, R.anim.slide_in_right);
                color.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        ImageView imageView=((ImageView)sc.findViewById(R.id.schedule_img));
                        String key=color_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setBack();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));

                                setView.setBack();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));

                                setView.setBack();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));

                                setView.setBack();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                                setView.setBack();

                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                    }
                });
                color2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        String key=color2_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                                setView.setTask();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                                setView.setTask();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                                setView.setTask();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                                setView.setTask();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                                setView.setTask();
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                    }
                });

                day_textcolor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        String key=day_text_color_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });


                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                    }
                });
                day_font.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_font_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        gv.setAdapter(gridAdapter);
                        String key=day_font_key;
                        final String[] font = {data.get(key)};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);
                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                if (font[0]!=null) {
                                    Typeface font_ = Typeface.createFromAsset(getAssets(), String.format("fonts/%s", font[0]));
                                    ((TextView) v.findViewById(R.id.configure_font_tx)).setTypeface(font_);
                                }
                                else
                                    ((TextView) v.findViewById(R.id.configure_font_tx)).setTypeface(null);
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                font[0]=gridAdapter.fonts[position];
                                data.put(key,font[0]);
                                setView.setViewDay();
                                setView.setViewC2();
                            }
                        });
                    }
                });

                textcolor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        String key=text_color_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });


                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setTask();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setTask();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));
                                setView.setTask();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setTask();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                data.put(key,String.valueOf(color[0]));
                                setView.setTask();
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                    }
                });
                stextcolor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        String key=stext_color_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });


                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setTask();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setTask();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));
                                setView.setTask();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setTask();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                data.put(key,String.valueOf(color[0]));
                                setView.setTask();
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                    }
                });
                font.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_font_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        gv.setAdapter(gridAdapter);
                        String key=font_key;
                        final String[] font = {data.get(key)};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);
                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                if (font[0]!=null) {
                                    Typeface font_ = Typeface.createFromAsset(getAssets(), String.format("fonts/%s", font[0]));
                                    ((TextView) v.findViewById(R.id.configure_font_tx)).setTypeface(font_);
                                }
                                else
                                    ((TextView) v.findViewById(R.id.configure_font_tx)).setTypeface(null);
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                font[0]=gridAdapter.fonts[position];
                                data.put(key,font[0]);
                                setView.setTask();
                            }
                        });
                    }
                });

                lang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_langt_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        ListView li=l.findViewById(R.id.configure_lang_select_li);

                        ListAdapterLang listAdapterLang=new ListAdapterLang(langs.keySet().toArray(new String[langs.size()])
                                ,getApplicationContext(),langs);

                        li.setAdapter(listAdapterLang);
                        String key=language_key;
                        final String[] language = {data.get(key)};

                        l.findViewById(R.id.configure_lang_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)lang.findViewById(R.id.configure_lang_tx)).setText(langs.get(data.get(key)));
                            }
                        });

                        li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                language[0]=listAdapterLang.list[position];
                                data.put(key,language[0]);
                                setView.setViewDay();
                                setView.setViewC2();
                            }
                        });
                    }
                });

                break;
            }
            case CALENDAR:{
                //Data  color, text color, font


                data.clear();
                GridAdapterFont gridAdapter=new GridAdapterFont(getApplicationContext());

                Map<String,Integer>mlist=new LinkedHashMap<>();

                mlist.put("Style1",R.drawable.underline);
                mlist.put("Style2",R.drawable.underline2);
                mlist.put("Style3",R.drawable.underline3);

                Map<String,String>slist=new LinkedHashMap<>();

                slist.put("Style1","Style1");
                slist.put("Style2","Style2");
                slist.put("Style3","Style3");

                String color_key="Color";//back color
                String color2_key="Color2";//today text
                String color3_key="Color3";//today back

                String language_key="Language";
                String style_key="style";

                String day_text_color_key="DayTextColor";//text color
                String day_font_key="DayFont";//font

                String dc_key_w="dc_key_w";
                String sdc_key_w="sdc_key_w";
                String sdf_key_w="sdf_key_w";

                String dc_key_sa="dc_key_sa";
                String sdc_key_sa="sdc_key_sa";
                String sdf_key_sa="sdf_key_sa";

                String dc_key_su="dc_key_su";
                String sdc_key_su="sdc_key_su";
                String sdf_key_su="sdf_key_su";

                String dc_key_h="dc_key_h";
                String sdc_key_h="sdc_key_h";
                String sdf_key_h="sdf_key_h";

                data.put(dc_key_w,String.valueOf(Color.BLACK));
                data.put(sdc_key_w,String.valueOf(Color.BLACK));
                data.put(sdf_key_w,String.valueOf(getColor(R.color.gray)));

                data.put(dc_key_sa,String.valueOf(getColor(R.color.blue)));
                data.put(sdc_key_sa,String.valueOf(getColor(R.color.blue)));
                data.put(sdf_key_sa,String.valueOf(getColor(R.color.gray)));

                data.put(dc_key_su,String.valueOf(getColor(R.color.red)));
                data.put(sdc_key_su,String.valueOf(getColor(R.color.red)));
                data.put(sdf_key_su,String.valueOf(getColor(R.color.gray)));

                data.put(dc_key_h,String.valueOf(getColor(R.color.red)));
                data.put(sdc_key_h,String.valueOf(getColor(R.color.red)));
                data.put(sdf_key_h,String.valueOf(getColor(R.color.gray)));

                data.put(color_key,String.valueOf(Color.WHITE));
                data.put(color2_key,String.valueOf(Color.WHITE));
                data.put(color3_key,String.valueOf(getColor(R.color.blue)));
                data.put(day_text_color_key,String.valueOf(Color.BLACK));
                data.put(day_font_key,null);
                data.put(language_key,null);
                data.put(style_key,"Style1");
                Map<String,String> style= ObjectStorage.get(CALENDAR_DATA +appWidgetId, Map.class,this);
                if (style!=null){
                    data.putAll(style);;
                }

                String defaultkey=MainActivity.CALENDAR;
                Map<String,String> defaultstyle= ObjectStorage.get(defaultkey+MainActivity.DEFAULT,
                        Map.class,this);
                if ((fromMain||style==null)&&(defaultstyle!=null)){
                    data.putAll(defaultstyle);
                }

                findViewById(R.id.configure_reset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectStorage.clear(defaultkey+MainActivity.DEFAULT,getApplicationContext());
                        ViewFlipper viewFlipper=findViewById(R.id.configure_scale_in);
                        viewFlipper.showNext();
                        setact();
                    }
                });
                findViewById(R.id.configure_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectStorage.save(data,defaultkey+MainActivity.DEFAULT,getApplicationContext());
                        ObjectStorage.save(MainActivity.getViewBitmap(screen),defaultkey,getApplicationContext());
                        Intent configIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(configIntent);overridePendingTransition(R.anim.slide_in_left, R.anim.scale_out);
                    }
                });

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view=screen;
                        ObjectStorage.save(data,CALENDAR_DATA +appWidgetId,getApplicationContext());
                        NewAppWidget.updateAppWidget(getApplicationContext(),AppWidgetManager.getInstance(getApplicationContext())
                                ,appWidgetId,0, FLIPPER_NONE,"now");
                        Intent resultValue = new Intent();
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        setResult(RESULT_OK, resultValue);
                        checkPermission();
                    }
                });

                screen.setLayoutParams(new FrameLayout.LayoutParams((int) (width*0.8), (int) (height*0.8)));
                View sc=inflate(R.layout.new_app_widget,null);
                View color=inflate(R.layout.configure_item_color,null);
                View color2=inflate(R.layout.configure_item_color,null);
                View color3=inflate(R.layout.configure_item_color,null);
                View day_textcolor=inflate(R.layout.configure_item_color,null);
                View day_font=inflate(R.layout.configure_item_font,null);
                View weekday=inflate(R.layout.configure_item_day,null);
                View saturday=inflate(R.layout.configure_item_day,null);
                View sunday=inflate(R.layout.configure_item_day,null);
                View holiday=inflate(R.layout.configure_item_day,null);
                View lang=inflate(R.layout.configure_item_lang,null);
                View style_v=inflate(R.layout.configure_item_lang,null);

                screen.addView(sc);
                select.addView(color);
                select.addView(color2);
                select.addView(color3);
                select.addView(day_textcolor);
                select.addView(day_font);
                select.addView(weekday);
                select.addView(sunday);
                select.addView(saturday);
                select.addView(holiday);
                select.addView(lang);
                select.addView(style_v);

                ((TextView)day_textcolor.findViewById(R.id.configure_color_title)).setText("Text Color");
                ((TextView)color2.findViewById(R.id.configure_color_title)).setText("Today Text Color");
                ((TextView)color3.findViewById(R.id.configure_color_title)).setText("Today Back Color");
                ((TextView)weekday.findViewById(R.id.configure_day_title)).setText("Weekday");
                ((TextView)sunday.findViewById(R.id.configure_day_title)).setText("Sunday");
                ((TextView)saturday.findViewById(R.id.configure_day_title)).setText("Saturday");
                ((TextView)holiday.findViewById(R.id.configure_day_title)).setText("Holiday");
                ((TextView)style_v.findViewById(R.id.configure_lang_title)).setText("Style");
                ((TextView)color.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(color_key))));
                ((TextView)color2.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(color2_key))));
                ((TextView)color3.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(color3_key))));
                ((TextView)day_textcolor.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(day_text_color_key))));
                if (data.get(day_font_key)!=null){
                    Typeface font_ = Typeface.createFromAsset(getAssets(), String.format("fonts/%s", data.get(day_font_key)));
                    ((TextView) day_font.findViewById(R.id.configure_font_tx)).setTypeface(font_);
                    ((TextView) weekday.findViewById(R.id.configure_day_im)).setTypeface(font_);
                    ((TextView) weekday.findViewById(R.id.configure_day_im_select)).setTypeface(font_);
                    ((TextView) saturday.findViewById(R.id.configure_day_im)).setTypeface(font_);
                    ((TextView) saturday.findViewById(R.id.configure_day_im_select)).setTypeface(font_);
                    ((TextView) sunday.findViewById(R.id.configure_day_im)).setTypeface(font_);
                    ((TextView) sunday.findViewById(R.id.configure_day_im_select)).setTypeface(font_);
                    ((TextView) holiday.findViewById(R.id.configure_day_im)).setTypeface(font_);
                    ((TextView) holiday.findViewById(R.id.configure_day_im_select)).setTypeface(font_);
                }
                if (data.get(language_key)!=null){
                    ((TextView)lang.findViewById(R.id.configure_lang_tx)).setText(langs.get(data.get(language_key)));
                }
                if (data.get(style_key)!=null){
                    ((TextView)style_v.findViewById(R.id.configure_lang_tx)).setText(slist.get(data.get(style_key)));
                }
                else data.put(style_key,String.valueOf(R.drawable.underline));

                ((TextView)weekday.findViewById(R.id.configure_day_im))
                        .setTextColor(Integer.valueOf(data.get(dc_key_w)));
                ((TextView)weekday.findViewById(R.id.configure_day_im_select))
                        .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(sdf_key_w))));
                ((TextView)weekday.findViewById(R.id.configure_day_im_select))
                        .setTextColor(Integer.valueOf(data.get(sdc_key_w)));

                ((TextView)sunday.findViewById(R.id.configure_day_im))
                        .setTextColor(Integer.valueOf(data.get(dc_key_su)));
                ((TextView)sunday.findViewById(R.id.configure_day_im_select))
                        .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(sdf_key_su))));
                ((TextView)sunday.findViewById(R.id.configure_day_im_select))
                        .setTextColor(Integer.valueOf(data.get(sdc_key_su)));

                ((TextView)saturday.findViewById(R.id.configure_day_im))
                        .setTextColor(Integer.valueOf(data.get(dc_key_sa)));
                ((TextView)saturday.findViewById(R.id.configure_day_im_select))
                        .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(sdf_key_sa))));
                ((TextView)saturday.findViewById(R.id.configure_day_im_select))
                        .setTextColor(Integer.valueOf(data.get(sdc_key_sa)));

                ((TextView)holiday.findViewById(R.id.configure_day_im))
                        .setTextColor(Integer.valueOf(data.get(dc_key_h)));
                ((TextView)holiday.findViewById(R.id.configure_day_im_select))
                        .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(sdf_key_h))));
                ((TextView)holiday.findViewById(R.id.configure_day_im_select))
                        .setTextColor(Integer.valueOf(data.get(sdc_key_h)));



                class SetView{
                    private final TextBitmap textBitmap=new TextBitmap();
                    public void setView(){

                        Context context=getApplicationContext();
                        textBitmap.Initialization(context,data.get(day_font_key),context.getResources().getDimension(R.dimen.date_text_size),
                                new int[]{Color.BLACK,Color.BLACK,Color.BLACK},
                                Color.WHITE,
                                DateData.getYear(),DateData.getMonth()
                                ,Color.BLACK,data.get(language_key));
                        textBitmap.startInit();

                        sc.findViewById(R.id.calendar_today).setVisibility(View.VISIBLE);
                        sc.findViewById(R.id.next_arrow_vf).setVisibility(View.VISIBLE);
                        sc.findViewById(R.id.previous_arrow_vf).setVisibility(View.VISIBLE);

                        setTask2();
                        setTask();
                        setBack();

                        ((ImageView)sc.findViewById(R.id.calendar_title)).setImageBitmap(
                                textBitmap.getMonthBitmap(textBitmap.getMonthText(DateData.getYear(), DateData.getMonth())));
                        ((ImageView)sc.findViewById(R.id.next_arrow)).setImageBitmap(textBitmap.getNext());
                        ((ImageView)sc.findViewById(R.id.previous_arrow)).setImageBitmap(textBitmap.getPrevious());

                        setViewDay();


                        ((ImageView)sc.findViewById(R.id.calendar_today)).setImageBitmap(textBitmap.getToday());
                        setViewC2();
                    }
                    public void setViewC2(){
                        int color=Integer.valueOf(data.get(color2_key));

                        ((ImageView)sc.findViewById(R.id.calendar_today))
                                .setColorFilter(color);
                        ((ImageView)sc.findViewById(R.id.calendar_today))
                                .setImageAlpha(Color.alpha(color));

                        color=Integer.valueOf(data.get(color3_key));

                        ((ImageView)sc.findViewById(R.id.calendar_today_img))
                                .setColorFilter(color);
                        ((ImageView)sc.findViewById(R.id.calendar_today_img))
                                .setImageAlpha(Color.alpha(color));
                    }
                    public void setViewDay(){
                        int color=Integer.valueOf(data.get(day_text_color_key));

                        ((ImageView)sc.findViewById(R.id.calendar_title))
                                .setColorFilter(color);
                        ((ImageView)sc.findViewById(R.id.calendar_title))
                                .setImageAlpha(Color.alpha(color));

                        ((ImageView)sc.findViewById(R.id.next_arrow))
                                .setColorFilter(color);
                        ((ImageView)sc.findViewById(R.id.next_arrow))
                                .setImageAlpha(Color.alpha(color));

                        ((ImageView)sc.findViewById(R.id.previous_arrow))
                                .setColorFilter(color);
                        ((ImageView)sc.findViewById(R.id.previous_arrow))
                                .setImageAlpha(Color.alpha(color));
                    }
                    public void setBack(){
                        ((ImageView)sc.findViewById(R.id.calendar_BG)).setColorFilter(Integer.valueOf(data.get(color_key)));
                        ((ImageView)sc.findViewById(R.id.calendar_BG)).setImageAlpha(Color.alpha(Integer.valueOf(data.get(color_key))));
                    }
                    public void setTask2(){

                        int year=DateData.getYear();
                        int month=DateData.getMonth();

                        int[] dow=new int[]{R.id.su,R.id.mo,R.id.tu,R.id.we,R.id.th,R.id.fr,R.id.sa};
                        for (int i=0;i<7;i++){
                            ((ImageView)sc.findViewById(dow[i])).setImageBitmap(textBitmap.getDowBitmap(i));
                        }

                        int[][] cellses={
                                {R.id.su1,R.id.mo1,R.id.tu1,R.id.we1,R.id.th1,R.id.fr1,R.id.sa1},
                                {R.id.su2,R.id.mo2,R.id.tu2,R.id.we2,R.id.th2,R.id.fr2,R.id.sa2},
                                {R.id.su3,R.id.mo3,R.id.tu3,R.id.we3,R.id.th3,R.id.fr3,R.id.sa3},
                                {R.id.su4,R.id.mo4,R.id.tu4,R.id.we4,R.id.th4,R.id.fr4,R.id.sa4},
                                {R.id.su5,R.id.mo5,R.id.tu5,R.id.we5,R.id.th5,R.id.fr5,R.id.sa5},
                                {R.id.su6,R.id.mo6,R.id.tu6,R.id.we6,R.id.th6,R.id.fr6,R.id.sa6}};
                        int[][] layouts={
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

                                int textcolor=0;
                                int date=DateData.getDateonCalendar(year,month,week,dateofweek);

                                ((FrameLayout)sc.findViewById(layouts[week-1][dateofweek-1])).removeAllViews();
                                if (dateofweek==4&&week==2) {
                                    textcolor = 7;
                                    View csl=inflate(R.layout.calendar_select_layout,null);
                                    ((FrameLayout)sc.findViewById(layouts[week-1][dateofweek-1])).addView(csl);
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img)).setImageResource(mlist.getOrDefault(data.get(style_key),R.drawable.underline));
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img))
                                            .setColorFilter(Integer.valueOf(data.get(sdf_key_h)));
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img))
                                            .setImageAlpha(Color.alpha(Integer.valueOf(data.get(sdf_key_h))));
                                }
                                else if (dateofweek==4&&week==3) {
                                    textcolor = 4;
                                    View csl=inflate(R.layout.calendar_select_layout,null);
                                    ((FrameLayout)sc.findViewById(layouts[week-1][dateofweek-1])).addView(csl);
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img)).setImageResource(mlist.getOrDefault(data.get(style_key),R.drawable.underline));
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img))
                                            .setColorFilter(Integer.valueOf(data.get(sdf_key_w)));
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img))
                                            .setImageAlpha(Color.alpha(Integer.valueOf(data.get(sdf_key_w))));
                                }
                                else if (dateofweek==1&&week==3) {
                                    textcolor = 5;
                                    View csl=inflate(R.layout.calendar_select_layout,null);
                                    ((FrameLayout)sc.findViewById(layouts[week-1][dateofweek-1])).addView(csl);
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img)).setImageResource(mlist.getOrDefault(data.get(style_key),R.drawable.underline));
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img))
                                            .setColorFilter(Integer.valueOf(data.get(sdf_key_su)));
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img))
                                            .setImageAlpha(Color.alpha(Integer.valueOf(data.get(sdf_key_su))));
                                }
                                else if (dateofweek==7&&week==1) {
                                    textcolor = 6;
                                    View csl=inflate(R.layout.calendar_select_layout,null);
                                    ((FrameLayout)sc.findViewById(layouts[week-1][dateofweek-1])).addView(csl);
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img)).setImageResource(mlist.getOrDefault(data.get(style_key),R.drawable.underline));
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img))
                                            .setColorFilter(Integer.valueOf(data.get(sdf_key_sa)));
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img))
                                            .setImageAlpha(Color.alpha(Integer.valueOf(data.get(sdf_key_sa))));
                                }

                                if (dateofweek==3&&week==2)
                                    textcolor=3;
                                else if (dateofweek==1)
                                    textcolor=1;
                                else if (dateofweek==7)
                                    textcolor=2;


                                if(date>0&&date<32) {
                                    sc.findViewById(cell).setVisibility(View.VISIBLE);
                                    ((ImageView)sc.findViewById(cell)).setImageBitmap(textBitmap.getDayBitmap(date, 0));
                                }
                                else
                                    sc.findViewById(cell).setVisibility(View.INVISIBLE);


                            }

                        }
                        int visibility = (DateData.getDateonCalendar(year,month,6,1)!=0)? View.VISIBLE:View.GONE;
                        sc.findViewById(R.id.sixth_week).setVisibility(visibility);

                    }
                    public void setTask(){

                        int year=DateData.getYear();
                        int month=DateData.getMonth();
                        int[] color=new int[]{Integer.valueOf(data.get(dc_key_w)),
                                Integer.valueOf(data.get(dc_key_su)),
                                Integer.valueOf(data.get(dc_key_sa)),
                                Integer.valueOf(data.get(dc_key_h)),
                                Integer.valueOf(data.get(sdc_key_w)),
                                Integer.valueOf(data.get(sdc_key_su)),
                                Integer.valueOf(data.get(sdc_key_sa)),
                                Integer.valueOf(data.get(sdc_key_h))};
                        int[] dow=new int[]{R.id.su,R.id.mo,R.id.tu,R.id.we,R.id.th,R.id.fr,R.id.sa};
                        ((ImageView)sc.findViewById(dow[0])).setColorFilter(Integer.valueOf(data.get(dc_key_su)));
                        ((ImageView)sc.findViewById(dow[0])).setImageAlpha(Color.alpha(Integer.valueOf(data.get(dc_key_su))));
                        ((ImageView)sc.findViewById(dow[6])).setColorFilter(Integer.valueOf(data.get(dc_key_sa)));
                        ((ImageView)sc.findViewById(dow[6])).setImageAlpha(Color.alpha(Integer.valueOf(data.get(dc_key_sa))));
                        for (int i=1;i<6;i++){
                            ((ImageView)sc.findViewById(dow[i])).setColorFilter(Integer.valueOf(data.get(dc_key_w)));
                            ((ImageView)sc.findViewById(dow[i])).setImageAlpha(Color.alpha(Integer.valueOf(data.get(dc_key_w))));
                        }

                        int[][] cellses={
                                {R.id.su1,R.id.mo1,R.id.tu1,R.id.we1,R.id.th1,R.id.fr1,R.id.sa1},
                                {R.id.su2,R.id.mo2,R.id.tu2,R.id.we2,R.id.th2,R.id.fr2,R.id.sa2},
                                {R.id.su3,R.id.mo3,R.id.tu3,R.id.we3,R.id.th3,R.id.fr3,R.id.sa3},
                                {R.id.su4,R.id.mo4,R.id.tu4,R.id.we4,R.id.th4,R.id.fr4,R.id.sa4},
                                {R.id.su5,R.id.mo5,R.id.tu5,R.id.we5,R.id.th5,R.id.fr5,R.id.sa5},
                                {R.id.su6,R.id.mo6,R.id.tu6,R.id.we6,R.id.th6,R.id.fr6,R.id.sa6}};
                        int[][] layouts={
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

                                int textcolor=0;
                                int date=DateData.getDateonCalendar(year,month,week,dateofweek);

                                ((FrameLayout)sc.findViewById(layouts[week-1][dateofweek-1])).removeAllViews();
                                if (dateofweek==4&&week==2) {
                                    textcolor = 7;
                                    View csl=inflate(R.layout.calendar_select_layout,null);
                                    ((FrameLayout)sc.findViewById(layouts[week-1][dateofweek-1])).addView(csl);
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img)).setImageResource(mlist.getOrDefault(data.get(style_key),R.drawable.underline));
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img))
                                            .setColorFilter(Integer.valueOf(data.get(sdf_key_h)));
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img))
                                            .setImageAlpha(Color.alpha(Integer.valueOf(data.get(sdf_key_h))));
                                }
                                else if (dateofweek==4&&week==3) {
                                    textcolor = 4;
                                    View csl=inflate(R.layout.calendar_select_layout,null);
                                    ((FrameLayout)sc.findViewById(layouts[week-1][dateofweek-1])).addView(csl);
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img)).setImageResource(mlist.getOrDefault(data.get(style_key),R.drawable.underline));
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img))
                                            .setColorFilter(Integer.valueOf(data.get(sdf_key_w)));
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img))
                                            .setImageAlpha(Color.alpha(Integer.valueOf(data.get(sdf_key_w))));
                                }
                                else if (dateofweek==1&&week==3) {
                                    textcolor = 5;
                                    View csl=inflate(R.layout.calendar_select_layout,null);
                                    ((FrameLayout)sc.findViewById(layouts[week-1][dateofweek-1])).addView(csl);
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img)).setImageResource(mlist.getOrDefault(data.get(style_key),R.drawable.underline));
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img))
                                            .setColorFilter(Integer.valueOf(data.get(sdf_key_su)));
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img))
                                            .setImageAlpha(Color.alpha(Integer.valueOf(data.get(sdf_key_su))));
                                }
                                else if (dateofweek==7&&week==1) {
                                    textcolor = 6;
                                    View csl=inflate(R.layout.calendar_select_layout,null);
                                    ((FrameLayout)sc.findViewById(layouts[week-1][dateofweek-1])).addView(csl);
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img)).setImageResource(mlist.getOrDefault(data.get(style_key),R.drawable.underline));
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img))
                                            .setColorFilter(Integer.valueOf(data.get(sdf_key_sa)));
                                    ((ImageView)csl.findViewById(R.id.calendar_select_img))
                                            .setImageAlpha(Color.alpha(Integer.valueOf(data.get(sdf_key_sa))));
                                }

                                else if (dateofweek==3&&week==2)
                                    textcolor=3;
                                else if (dateofweek==1)
                                    textcolor=1;
                                else if (dateofweek==7)
                                    textcolor=2;


                                if(date>0&&date<32) {
                                    sc.findViewById(cell).setVisibility(View.VISIBLE);
                                    ((ImageView)sc.findViewById(cell)).setColorFilter(color[textcolor]);
                                    ((ImageView)sc.findViewById(cell)).setImageAlpha(Color.alpha(color[textcolor]));
                                }
                                else
                                    sc.findViewById(cell).setVisibility(View.INVISIBLE);


                            }

                        }
                        int visibility = (DateData.getDateonCalendar(year,month,6,1)!=0)? View.VISIBLE:View.GONE;
                        sc.findViewById(R.id.sixth_week).setVisibility(visibility);

                    }

                }

                SetView setView =new SetView();
                setView.setView();

                vf.setInAnimation(this, R.anim.slide_in_right);
                color.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        ImageView imageView=((ImageView)sc.findViewById(R.id.schedule_img));
                        String key=color_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setBack();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));

                                setView.setBack();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));

                                setView.setBack();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));

                                setView.setBack();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                                setView.setBack();

                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                    }
                });
                color2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        String key=color2_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                    }
                });
                color3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        String key=color3_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                    }
                });

                day_textcolor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        String key=day_text_color_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });


                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                    }
                });
                day_font.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_font_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        gv.setAdapter(gridAdapter);
                        String key=day_font_key;
                        final String[] font = {data.get(key)};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);
                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                if (font[0]!=null) {
                                    Typeface font_ = Typeface.createFromAsset(getAssets(), String.format("fonts/%s", font[0]));
                                    ((TextView) v.findViewById(R.id.configure_font_tx)).setTypeface(font_);
                                    ((TextView) weekday.findViewById(R.id.configure_day_im)).setTypeface(font_);
                                    ((TextView) weekday.findViewById(R.id.configure_day_im_select)).setTypeface(font_);
                                    ((TextView) saturday.findViewById(R.id.configure_day_im)).setTypeface(font_);
                                    ((TextView) saturday.findViewById(R.id.configure_day_im_select)).setTypeface(font_);
                                    ((TextView) sunday.findViewById(R.id.configure_day_im)).setTypeface(font_);
                                    ((TextView) sunday.findViewById(R.id.configure_day_im_select)).setTypeface(font_);
                                    ((TextView) holiday.findViewById(R.id.configure_day_im)).setTypeface(font_);
                                    ((TextView) holiday.findViewById(R.id.configure_day_im_select)).setTypeface(font_);
                                }
                                else
                                    ((TextView) v.findViewById(R.id.configure_font_tx)).setTypeface(null);
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                font[0]=gridAdapter.fonts[position];
                                data.put(key,font[0]);
                                setView.setView();
                            }
                        });
                    }
                });

                weekday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_day_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        l.findViewById(R.id.configure_day_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                ((TextView)weekday.findViewById(R.id.configure_day_im))
                                        .setTextColor(Integer.valueOf(data.get(dc_key_w)));
                                ((TextView)weekday.findViewById(R.id.configure_day_im_select))
                                        .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(sdf_key_w))));
                                ((TextView)weekday.findViewById(R.id.configure_day_im_select))
                                        .setTextColor(Integer.valueOf(data.get(sdc_key_w)));
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                            }
                        });

                        View dc=inflate(R.layout.configure_item_color,null);
                        View sdc=inflate(R.layout.configure_item_color,null);
                        View sdf=inflate(R.layout.configure_item_color,null);

                        LinearLayout cdll=l.findViewById(R.id.configure_day_ll);
                        cdll.addView(dc);
                        cdll.addView(sdc);
                        cdll.addView(sdf);

                        ((TextView)dc.findViewById(R.id.configure_color_im))
                                .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(dc_key_w))));
                        ((TextView)sdc.findViewById(R.id.configure_color_im))
                                .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(sdc_key_w))));
                        ((TextView)sdf.findViewById(R.id.configure_color_im))
                                .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(sdf_key_w))));

                        ((TextView)dc.findViewById(R.id.configure_color_title)).setText("Text Color");
                        ((TextView)sdc.findViewById(R.id.configure_color_title)).setText("Text Color When Selected");
                        ((TextView)sdf.findViewById(R.id.configure_color_title)).setText("Back Color When Selected");


                        dc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FrameLayout set=l.findViewById(R.id.configure_day_fl);
                                ViewFlipper vf=l.findViewById(R.id.configure_day_vf);
                                vf.showNext();
                                set.removeAllViews();
                                View l=inflate(R.layout.configure_color_select,null);
                                set.addView(l);
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                                GridView gv=l.findViewById(R.id.configure_color_select_gv);
                                GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                                gv.setAdapter(gridAdapter);

                                SeekBar red=l.findViewById(R.id.configure_color_select_red);
                                SeekBar green=l.findViewById(R.id.configure_color_select_green);
                                SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                                SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                                String key=dc_key_w;
                                final int[] color = {Integer.valueOf(data.get(key))};

                                int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                        Simple_Memo.dip(getApplicationContext(),40);
                                if (colum<1)
                                    colum=1;
                                gv.setNumColumns(colum);

                                l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View pv) {
                                        vf.showPrevious();
                                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                        ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                                    }
                                });


                                red.setMax(255);
                                green.setMax(255);
                                blue.setMax(255);
                                alpha.setMax(255);
                                red.setProgress(Color.red(color[0]));
                                green.setProgress(Color.green(color[0]));
                                blue.setProgress(Color.blue(color[0]));
                                alpha.setProgress(Color.alpha(color[0]));

                                red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });

                                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        color[0]=GridAdapterColor.clolrs[position];
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                        red.setProgress(Color.red(color[0]),true);
                                        green.setProgress(Color.green(color[0]),true);
                                        blue.setProgress(Color.blue(color[0]),true);
                                        alpha.setProgress(Color.alpha(color[0]),true);
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                            }
                        });
                        sdc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FrameLayout set=l.findViewById(R.id.configure_day_fl);
                                ViewFlipper vf=l.findViewById(R.id.configure_day_vf);
                                vf.showNext();
                                set.removeAllViews();
                                View l=inflate(R.layout.configure_color_select,null);
                                set.addView(l);
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                                GridView gv=l.findViewById(R.id.configure_color_select_gv);
                                GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                                gv.setAdapter(gridAdapter);

                                SeekBar red=l.findViewById(R.id.configure_color_select_red);
                                SeekBar green=l.findViewById(R.id.configure_color_select_green);
                                SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                                SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                                String key=sdc_key_w;
                                final int[] color = {Integer.valueOf(data.get(key))};

                                int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                        Simple_Memo.dip(getApplicationContext(),40);
                                if (colum<1)
                                    colum=1;
                                gv.setNumColumns(colum);

                                l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View pv) {
                                        vf.showPrevious();
                                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                        ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                                    }
                                });


                                red.setMax(255);
                                green.setMax(255);
                                blue.setMax(255);
                                alpha.setMax(255);
                                red.setProgress(Color.red(color[0]));
                                green.setProgress(Color.green(color[0]));
                                blue.setProgress(Color.blue(color[0]));
                                alpha.setProgress(Color.alpha(color[0]));

                                red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });

                                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        color[0]=GridAdapterColor.clolrs[position];
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                        red.setProgress(Color.red(color[0]),true);
                                        green.setProgress(Color.green(color[0]),true);
                                        blue.setProgress(Color.blue(color[0]),true);
                                        alpha.setProgress(Color.alpha(color[0]),true);
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                            }
                        });
                        sdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FrameLayout set=l.findViewById(R.id.configure_day_fl);
                                ViewFlipper vf=l.findViewById(R.id.configure_day_vf);
                                vf.showNext();
                                set.removeAllViews();
                                View l=inflate(R.layout.configure_color_select,null);
                                set.addView(l);
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                                GridView gv=l.findViewById(R.id.configure_color_select_gv);
                                GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                                gv.setAdapter(gridAdapter);

                                SeekBar red=l.findViewById(R.id.configure_color_select_red);
                                SeekBar green=l.findViewById(R.id.configure_color_select_green);
                                SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                                SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                                String key=sdf_key_w;
                                final int[] color = {Integer.valueOf(data.get(key))};

                                int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                        Simple_Memo.dip(getApplicationContext(),40);
                                if (colum<1)
                                    colum=1;
                                gv.setNumColumns(colum);

                                l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View pv) {
                                        vf.showPrevious();
                                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                        ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                                    }
                                });


                                red.setMax(255);
                                green.setMax(255);
                                blue.setMax(255);
                                alpha.setMax(255);
                                red.setProgress(Color.red(color[0]));
                                green.setProgress(Color.green(color[0]));
                                blue.setProgress(Color.blue(color[0]));
                                alpha.setProgress(Color.alpha(color[0]));

                                red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });

                                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        color[0]=GridAdapterColor.clolrs[position];
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                        red.setProgress(Color.red(color[0]),true);
                                        green.setProgress(Color.green(color[0]),true);
                                        blue.setProgress(Color.blue(color[0]),true);
                                        alpha.setProgress(Color.alpha(color[0]),true);
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                            }
                        });

                    }
                });
                sunday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_day_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        l.findViewById(R.id.configure_day_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                ((TextView)sunday.findViewById(R.id.configure_day_im))
                                        .setTextColor(Integer.valueOf(data.get(dc_key_su)));
                                ((TextView)sunday.findViewById(R.id.configure_day_im_select))
                                        .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(sdf_key_su))));
                                ((TextView)sunday.findViewById(R.id.configure_day_im_select))
                                        .setTextColor(Integer.valueOf(data.get(sdc_key_su)));
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                            }
                        });

                        View dc=inflate(R.layout.configure_item_color,null);
                        View sdc=inflate(R.layout.configure_item_color,null);
                        View sdf=inflate(R.layout.configure_item_color,null);

                        LinearLayout cdll=l.findViewById(R.id.configure_day_ll);
                        cdll.addView(dc);
                        cdll.addView(sdc);
                        cdll.addView(sdf);

                        ((TextView)dc.findViewById(R.id.configure_color_im))
                                .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(dc_key_su))));
                        ((TextView)sdc.findViewById(R.id.configure_color_im))
                                .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(sdc_key_su))));
                        ((TextView)sdf.findViewById(R.id.configure_color_im))
                                .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(sdf_key_su))));

                        ((TextView)dc.findViewById(R.id.configure_color_title)).setText("Text Color");
                        ((TextView)sdc.findViewById(R.id.configure_color_title)).setText("Text Color When Selected");
                        ((TextView)sdf.findViewById(R.id.configure_color_title)).setText("Back Color When Selected");


                        dc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FrameLayout set=l.findViewById(R.id.configure_day_fl);
                                ViewFlipper vf=l.findViewById(R.id.configure_day_vf);
                                vf.showNext();
                                set.removeAllViews();
                                View l=inflate(R.layout.configure_color_select,null);
                                set.addView(l);
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                                GridView gv=l.findViewById(R.id.configure_color_select_gv);
                                GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                                gv.setAdapter(gridAdapter);

                                SeekBar red=l.findViewById(R.id.configure_color_select_red);
                                SeekBar green=l.findViewById(R.id.configure_color_select_green);
                                SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                                SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                                String key=dc_key_su;
                                final int[] color = {Integer.valueOf(data.get(key))};

                                int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                        Simple_Memo.dip(getApplicationContext(),40);
                                if (colum<1)
                                    colum=1;
                                gv.setNumColumns(colum);

                                l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View pv) {
                                        vf.showPrevious();
                                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                        ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                                    }
                                });


                                red.setMax(255);
                                green.setMax(255);
                                blue.setMax(255);
                                alpha.setMax(255);
                                red.setProgress(Color.red(color[0]));
                                green.setProgress(Color.green(color[0]));
                                blue.setProgress(Color.blue(color[0]));
                                alpha.setProgress(Color.alpha(color[0]));

                                red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });

                                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        color[0]=GridAdapterColor.clolrs[position];
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                        red.setProgress(Color.red(color[0]),true);
                                        green.setProgress(Color.green(color[0]),true);
                                        blue.setProgress(Color.blue(color[0]),true);
                                        alpha.setProgress(Color.alpha(color[0]),true);
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                            }
                        });
                        sdc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FrameLayout set=l.findViewById(R.id.configure_day_fl);
                                ViewFlipper vf=l.findViewById(R.id.configure_day_vf);
                                vf.showNext();
                                set.removeAllViews();
                                View l=inflate(R.layout.configure_color_select,null);
                                set.addView(l);
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                                GridView gv=l.findViewById(R.id.configure_color_select_gv);
                                GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                                gv.setAdapter(gridAdapter);

                                SeekBar red=l.findViewById(R.id.configure_color_select_red);
                                SeekBar green=l.findViewById(R.id.configure_color_select_green);
                                SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                                SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                                String key=sdc_key_su;
                                final int[] color = {Integer.valueOf(data.get(key))};

                                int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                        Simple_Memo.dip(getApplicationContext(),40);
                                if (colum<1)
                                    colum=1;
                                gv.setNumColumns(colum);

                                l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View pv) {
                                        vf.showPrevious();
                                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                        ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                                    }
                                });


                                red.setMax(255);
                                green.setMax(255);
                                blue.setMax(255);
                                alpha.setMax(255);
                                red.setProgress(Color.red(color[0]));
                                green.setProgress(Color.green(color[0]));
                                blue.setProgress(Color.blue(color[0]));
                                alpha.setProgress(Color.alpha(color[0]));

                                red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });

                                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        color[0]=GridAdapterColor.clolrs[position];
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                        red.setProgress(Color.red(color[0]),true);
                                        green.setProgress(Color.green(color[0]),true);
                                        blue.setProgress(Color.blue(color[0]),true);
                                        alpha.setProgress(Color.alpha(color[0]),true);
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                            }
                        });
                        sdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FrameLayout set=l.findViewById(R.id.configure_day_fl);
                                ViewFlipper vf=l.findViewById(R.id.configure_day_vf);
                                vf.showNext();
                                set.removeAllViews();
                                View l=inflate(R.layout.configure_color_select,null);
                                set.addView(l);
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                                GridView gv=l.findViewById(R.id.configure_color_select_gv);
                                GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                                gv.setAdapter(gridAdapter);

                                SeekBar red=l.findViewById(R.id.configure_color_select_red);
                                SeekBar green=l.findViewById(R.id.configure_color_select_green);
                                SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                                SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                                String key=sdf_key_su;
                                final int[] color = {Integer.valueOf(data.get(key))};

                                int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                        Simple_Memo.dip(getApplicationContext(),40);
                                if (colum<1)
                                    colum=1;
                                gv.setNumColumns(colum);

                                l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View pv) {
                                        vf.showPrevious();
                                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                        ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                                    }
                                });


                                red.setMax(255);
                                green.setMax(255);
                                blue.setMax(255);
                                alpha.setMax(255);
                                red.setProgress(Color.red(color[0]));
                                green.setProgress(Color.green(color[0]));
                                blue.setProgress(Color.blue(color[0]));
                                alpha.setProgress(Color.alpha(color[0]));

                                red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });

                                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        color[0]=GridAdapterColor.clolrs[position];
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                        red.setProgress(Color.red(color[0]),true);
                                        green.setProgress(Color.green(color[0]),true);
                                        blue.setProgress(Color.blue(color[0]),true);
                                        alpha.setProgress(Color.alpha(color[0]),true);
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                            }
                        });

                    }
                });
                saturday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_day_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        l.findViewById(R.id.configure_day_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                ((TextView)saturday.findViewById(R.id.configure_day_im))
                                        .setTextColor(Integer.valueOf(data.get(dc_key_sa)));
                                ((TextView)saturday.findViewById(R.id.configure_day_im_select))
                                        .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(sdf_key_sa))));
                                ((TextView)saturday.findViewById(R.id.configure_day_im_select))
                                        .setTextColor(Integer.valueOf(data.get(sdc_key_sa)));
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                            }
                        });

                        View dc=inflate(R.layout.configure_item_color,null);
                        View sdc=inflate(R.layout.configure_item_color,null);
                        View sdf=inflate(R.layout.configure_item_color,null);

                        LinearLayout cdll=l.findViewById(R.id.configure_day_ll);
                        cdll.addView(dc);
                        cdll.addView(sdc);
                        cdll.addView(sdf);

                        ((TextView)dc.findViewById(R.id.configure_color_im))
                                .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(dc_key_sa))));
                        ((TextView)sdc.findViewById(R.id.configure_color_im))
                                .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(sdc_key_sa))));
                        ((TextView)sdf.findViewById(R.id.configure_color_im))
                                .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(sdf_key_sa))));

                        ((TextView)dc.findViewById(R.id.configure_color_title)).setText("Text Color");
                        ((TextView)sdc.findViewById(R.id.configure_color_title)).setText("Text Color When Selected");
                        ((TextView)sdf.findViewById(R.id.configure_color_title)).setText("Back Color When Selected");


                        dc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FrameLayout set=l.findViewById(R.id.configure_day_fl);
                                ViewFlipper vf=l.findViewById(R.id.configure_day_vf);
                                vf.showNext();
                                set.removeAllViews();
                                View l=inflate(R.layout.configure_color_select,null);
                                set.addView(l);
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                                GridView gv=l.findViewById(R.id.configure_color_select_gv);
                                GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                                gv.setAdapter(gridAdapter);

                                SeekBar red=l.findViewById(R.id.configure_color_select_red);
                                SeekBar green=l.findViewById(R.id.configure_color_select_green);
                                SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                                SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                                String key=dc_key_sa;
                                final int[] color = {Integer.valueOf(data.get(key))};

                                int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                        Simple_Memo.dip(getApplicationContext(),40);
                                if (colum<1)
                                    colum=1;
                                gv.setNumColumns(colum);

                                l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View pv) {
                                        vf.showPrevious();
                                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                        ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                                    }
                                });


                                red.setMax(255);
                                green.setMax(255);
                                blue.setMax(255);
                                alpha.setMax(255);
                                red.setProgress(Color.red(color[0]));
                                green.setProgress(Color.green(color[0]));
                                blue.setProgress(Color.blue(color[0]));
                                alpha.setProgress(Color.alpha(color[0]));

                                red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });

                                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        color[0]=GridAdapterColor.clolrs[position];
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                        red.setProgress(Color.red(color[0]),true);
                                        green.setProgress(Color.green(color[0]),true);
                                        blue.setProgress(Color.blue(color[0]),true);
                                        alpha.setProgress(Color.alpha(color[0]),true);
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                            }
                        });
                        sdc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FrameLayout set=l.findViewById(R.id.configure_day_fl);
                                ViewFlipper vf=l.findViewById(R.id.configure_day_vf);
                                vf.showNext();
                                set.removeAllViews();
                                View l=inflate(R.layout.configure_color_select,null);
                                set.addView(l);
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                                GridView gv=l.findViewById(R.id.configure_color_select_gv);
                                GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                                gv.setAdapter(gridAdapter);

                                SeekBar red=l.findViewById(R.id.configure_color_select_red);
                                SeekBar green=l.findViewById(R.id.configure_color_select_green);
                                SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                                SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                                String key=sdc_key_sa;
                                final int[] color = {Integer.valueOf(data.get(key))};

                                int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                        Simple_Memo.dip(getApplicationContext(),40);
                                if (colum<1)
                                    colum=1;
                                gv.setNumColumns(colum);

                                l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View pv) {
                                        vf.showPrevious();
                                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                        ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                                    }
                                });


                                red.setMax(255);
                                green.setMax(255);
                                blue.setMax(255);
                                alpha.setMax(255);
                                red.setProgress(Color.red(color[0]));
                                green.setProgress(Color.green(color[0]));
                                blue.setProgress(Color.blue(color[0]));
                                alpha.setProgress(Color.alpha(color[0]));

                                red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });

                                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        color[0]=GridAdapterColor.clolrs[position];
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                        red.setProgress(Color.red(color[0]),true);
                                        green.setProgress(Color.green(color[0]),true);
                                        blue.setProgress(Color.blue(color[0]),true);
                                        alpha.setProgress(Color.alpha(color[0]),true);
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                            }
                        });
                        sdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FrameLayout set=l.findViewById(R.id.configure_day_fl);
                                ViewFlipper vf=l.findViewById(R.id.configure_day_vf);
                                vf.showNext();
                                set.removeAllViews();
                                View l=inflate(R.layout.configure_color_select,null);
                                set.addView(l);
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                                GridView gv=l.findViewById(R.id.configure_color_select_gv);
                                GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                                gv.setAdapter(gridAdapter);

                                SeekBar red=l.findViewById(R.id.configure_color_select_red);
                                SeekBar green=l.findViewById(R.id.configure_color_select_green);
                                SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                                SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                                String key=sdf_key_sa;
                                final int[] color = {Integer.valueOf(data.get(key))};

                                int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                        Simple_Memo.dip(getApplicationContext(),40);
                                if (colum<1)
                                    colum=1;
                                gv.setNumColumns(colum);

                                l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View pv) {
                                        vf.showPrevious();
                                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                        ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                                    }
                                });


                                red.setMax(255);
                                green.setMax(255);
                                blue.setMax(255);
                                alpha.setMax(255);
                                red.setProgress(Color.red(color[0]));
                                green.setProgress(Color.green(color[0]));
                                blue.setProgress(Color.blue(color[0]));
                                alpha.setProgress(Color.alpha(color[0]));

                                red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });

                                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        color[0]=GridAdapterColor.clolrs[position];
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                        red.setProgress(Color.red(color[0]),true);
                                        green.setProgress(Color.green(color[0]),true);
                                        blue.setProgress(Color.blue(color[0]),true);
                                        alpha.setProgress(Color.alpha(color[0]),true);
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                            }
                        });

                    }
                });
                holiday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_day_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        l.findViewById(R.id.configure_day_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                ((TextView)holiday.findViewById(R.id.configure_day_im))
                                        .setTextColor(Integer.valueOf(data.get(dc_key_h)));
                                ((TextView)holiday.findViewById(R.id.configure_day_im_select))
                                        .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(sdf_key_h))));
                                ((TextView)holiday.findViewById(R.id.configure_day_im_select))
                                        .setTextColor(Integer.valueOf(data.get(sdc_key_h)));
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                            }
                        });

                        View dc=inflate(R.layout.configure_item_color,null);
                        View sdc=inflate(R.layout.configure_item_color,null);
                        View sdf=inflate(R.layout.configure_item_color,null);

                        LinearLayout cdll=l.findViewById(R.id.configure_day_ll);
                        cdll.addView(dc);
                        cdll.addView(sdc);
                        cdll.addView(sdf);

                        ((TextView)dc.findViewById(R.id.configure_color_im))
                                .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(dc_key_h))));
                        ((TextView)sdc.findViewById(R.id.configure_color_im))
                                .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(sdc_key_h))));
                        ((TextView)sdf.findViewById(R.id.configure_color_im))
                                .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(sdf_key_h))));

                        ((TextView)dc.findViewById(R.id.configure_color_title)).setText("Text Color");
                        ((TextView)sdc.findViewById(R.id.configure_color_title)).setText("Text Color When Selected");
                        ((TextView)sdf.findViewById(R.id.configure_color_title)).setText("Back Color When Selected");


                        dc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FrameLayout set=l.findViewById(R.id.configure_day_fl);
                                ViewFlipper vf=l.findViewById(R.id.configure_day_vf);
                                vf.showNext();
                                set.removeAllViews();
                                View l=inflate(R.layout.configure_color_select,null);
                                set.addView(l);
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                                GridView gv=l.findViewById(R.id.configure_color_select_gv);
                                GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                                gv.setAdapter(gridAdapter);

                                SeekBar red=l.findViewById(R.id.configure_color_select_red);
                                SeekBar green=l.findViewById(R.id.configure_color_select_green);
                                SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                                SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                                String key=dc_key_h;
                                final int[] color = {Integer.valueOf(data.get(key))};

                                int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                        Simple_Memo.dip(getApplicationContext(),40);
                                if (colum<1)
                                    colum=1;
                                gv.setNumColumns(colum);

                                l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View pv) {
                                        vf.showPrevious();
                                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                        ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                                    }
                                });


                                red.setMax(255);
                                green.setMax(255);
                                blue.setMax(255);
                                alpha.setMax(255);
                                red.setProgress(Color.red(color[0]));
                                green.setProgress(Color.green(color[0]));
                                blue.setProgress(Color.blue(color[0]));
                                alpha.setProgress(Color.alpha(color[0]));

                                red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });

                                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        color[0]=GridAdapterColor.clolrs[position];
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                        red.setProgress(Color.red(color[0]),true);
                                        green.setProgress(Color.green(color[0]),true);
                                        blue.setProgress(Color.blue(color[0]),true);
                                        alpha.setProgress(Color.alpha(color[0]),true);
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                            }
                        });
                        sdc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FrameLayout set=l.findViewById(R.id.configure_day_fl);
                                ViewFlipper vf=l.findViewById(R.id.configure_day_vf);
                                vf.showNext();
                                set.removeAllViews();
                                View l=inflate(R.layout.configure_color_select,null);
                                set.addView(l);
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                                GridView gv=l.findViewById(R.id.configure_color_select_gv);
                                GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                                gv.setAdapter(gridAdapter);

                                SeekBar red=l.findViewById(R.id.configure_color_select_red);
                                SeekBar green=l.findViewById(R.id.configure_color_select_green);
                                SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                                SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                                String key=sdc_key_h;
                                final int[] color = {Integer.valueOf(data.get(key))};

                                int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                        Simple_Memo.dip(getApplicationContext(),40);
                                if (colum<1)
                                    colum=1;
                                gv.setNumColumns(colum);

                                l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View pv) {
                                        vf.showPrevious();
                                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                        ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                                    }
                                });


                                red.setMax(255);
                                green.setMax(255);
                                blue.setMax(255);
                                alpha.setMax(255);
                                red.setProgress(Color.red(color[0]));
                                green.setProgress(Color.green(color[0]));
                                blue.setProgress(Color.blue(color[0]));
                                alpha.setProgress(Color.alpha(color[0]));

                                red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });

                                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        color[0]=GridAdapterColor.clolrs[position];
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                        red.setProgress(Color.red(color[0]),true);
                                        green.setProgress(Color.green(color[0]),true);
                                        blue.setProgress(Color.blue(color[0]),true);
                                        alpha.setProgress(Color.alpha(color[0]),true);
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                            }
                        });
                        sdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FrameLayout set=l.findViewById(R.id.configure_day_fl);
                                ViewFlipper vf=l.findViewById(R.id.configure_day_vf);
                                vf.showNext();
                                set.removeAllViews();
                                View l=inflate(R.layout.configure_color_select,null);
                                set.addView(l);
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                                GridView gv=l.findViewById(R.id.configure_color_select_gv);
                                GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                                gv.setAdapter(gridAdapter);

                                SeekBar red=l.findViewById(R.id.configure_color_select_red);
                                SeekBar green=l.findViewById(R.id.configure_color_select_green);
                                SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                                SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                                String key=sdf_key_h;
                                final int[] color = {Integer.valueOf(data.get(key))};

                                int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                        Simple_Memo.dip(getApplicationContext(),40);
                                if (colum<1)
                                    colum=1;
                                gv.setNumColumns(colum);

                                l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View pv) {
                                        vf.showPrevious();
                                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                        ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                                    }
                                });


                                red.setMax(255);
                                green.setMax(255);
                                blue.setMax(255);
                                alpha.setMax(255);
                                red.setProgress(Color.red(color[0]));
                                green.setProgress(Color.green(color[0]));
                                blue.setProgress(Color.blue(color[0]));
                                alpha.setProgress(Color.alpha(color[0]));

                                red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                                alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });

                                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        color[0]=GridAdapterColor.clolrs[position];
                                        data.put(key,String.valueOf(color[0]));
                                        setView.setTask();
                                        red.setProgress(Color.red(color[0]),true);
                                        green.setProgress(Color.green(color[0]),true);
                                        blue.setProgress(Color.blue(color[0]),true);
                                        alpha.setProgress(Color.alpha(color[0]),true);
                                        data.put(key,String.valueOf(color[0]));
                                    }
                                });
                            }
                        });

                    }
                });

                lang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_langt_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        ListView li=l.findViewById(R.id.configure_lang_select_li);

                        ListAdapterLang listAdapterLang=new ListAdapterLang(langs.keySet().toArray(new String[langs.size()])
                                ,getApplicationContext(),langs);

                        li.setAdapter(listAdapterLang);
                        String key=language_key;
                        final String[] language = {data.get(key)};

                        l.findViewById(R.id.configure_lang_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)lang.findViewById(R.id.configure_lang_tx)).setText(langs.get(data.get(key)));
                            }
                        });

                        li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                language[0]=listAdapterLang.list[position];
                                data.put(key,language[0]);
                                setView.setView();
                            }
                        });
                    }
                });
                style_v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_langt_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        ListView li=l.findViewById(R.id.configure_lang_select_li);

                        ListAdapterLang listAdapterLang=new ListAdapterLang(slist.keySet().toArray(new String[slist.size()])
                                ,getApplicationContext(),slist);

                        li.setAdapter(listAdapterLang);
                        String key=style_key;
                        final String[] language = {data.get(key)};

                        l.findViewById(R.id.configure_lang_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_lang_tx)).setText(slist.get(data.get(key)));
                            }
                        });

                        li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                language[0]=listAdapterLang.list[position];
                                data.put(key,language[0]);
                                setView.setTask();
                                setView.setTask2();
                            }
                        });
                    }
                });


                break;
            }
            case ANALOG_CLOCK:{
                //Data  color, text color, font
                data.clear();
                GridAdapterFont gridAdapter=new GridAdapterFont(getApplicationContext());

                Map<String,Integer>mlist=new LinkedHashMap<>();

                mlist.put("Style1",0);
                mlist.put("Style2",R.layout.analog_clock_num);
                mlist.put("Style3",R.layout.analog_clock_num2);
                mlist.put("Style4",R.layout.analog_clock_bar);
                mlist.put("Style5",R.layout.analog_clock_bar2);
                

                Map<String,String>slist=new LinkedHashMap<>();

                slist.put("Style1","Style1");
                slist.put("Style2","Style2");
                slist.put("Style3","Style3");
                slist.put("Style4","Style4");
                slist.put("Style5","Style5");

                String color_key="Color";
                String bcolor_key="backColor";
                String hcolor_key="Colorh";
                String mcolor_key="Colorm";
                String scolor_key="Colors";
                String language_key="Language";
                String style_key="style";
                String text_color_key="TextColor";
                String font_key="Font";
                data.put(bcolor_key,String.valueOf(Color.WHITE));
                data.put(color_key,String.valueOf(Color.BLACK));
                data.put(hcolor_key,String.valueOf(Color.BLACK));
                data.put(mcolor_key,String.valueOf(Color.BLACK));
                data.put(scolor_key,String.valueOf(Color.BLACK));
                data.put(text_color_key,String.valueOf(Color.BLACK));
                data.put(font_key,null);
                data.put(language_key,null);
                data.put(style_key,"Style1");

                Map<String,String> style= ObjectStorage.get(AnalogClock.ANALOG_CLOCK_DATA +appWidgetId, Map.class,this);
                if (style!=null){
                    data.putAll(style);;
                }

                String defaultkey=MainActivity.ANALOG_CLOCK;
                Map<String,String> defaultstyle= ObjectStorage.get(defaultkey+MainActivity.DEFAULT,
                        Map.class,this);
                if ((fromMain||style==null)&&(defaultstyle!=null)){
                    data.putAll(defaultstyle);
                }

                findViewById(R.id.configure_reset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectStorage.clear(defaultkey+MainActivity.DEFAULT,getApplicationContext());
                        ViewFlipper viewFlipper=findViewById(R.id.configure_scale_in);
                        viewFlipper.showNext();
                        setact();
                    }
                });


                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectStorage.save(data,AnalogClock.ANALOG_CLOCK_DATA+appWidgetId,getApplicationContext());
                        AnalogClock.updateAppWidgets(getApplicationContext()
                                ,AppWidgetManager.getInstance(getApplicationContext()),new int[]{appWidgetId});
                        Intent resultValue = new Intent();

                        View view=screen;

                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        setResult(RESULT_OK, resultValue);
                        finish();
                    }
                });

                screen.setLayoutParams(new FrameLayout.LayoutParams((int) (width*0.8), (int) (height*0.8*0.5)));
                LinearLayout sc=new LinearLayout(getApplicationContext());
                sc.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                sc.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams((int) (width*0.8*0.5), (int) (height*0.8*0.5));
                View view=inflate(R.layout.analog_clock,null);
                view.setLayoutParams(layoutParams);

                View finalView = view;
                findViewById(R.id.configure_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectStorage.save(data,defaultkey+MainActivity.DEFAULT,getApplicationContext());
                        ObjectStorage.save(MainActivity.getViewBitmap(finalView),defaultkey,getApplicationContext());
                        Intent configIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(configIntent);overridePendingTransition(R.anim.slide_in_left, R.anim.scale_out);
                    }
                });

                sc.addView(view);
                view=inflate(R.layout.analog_clock_only_date,null);
                view.setLayoutParams(layoutParams);
                sc.addView(view);

                View color=inflate(R.layout.configure_item_color,null);
                View bcolor=inflate(R.layout.configure_item_color,null);
                View hcolor=inflate(R.layout.configure_item_color,null);
                View mcolor=inflate(R.layout.configure_item_color,null);
                View scolor=inflate(R.layout.configure_item_color,null);
                View textcolor=inflate(R.layout.configure_item_color,null);
                View font=inflate(R.layout.configure_item_font,null);
                View lang=inflate(R.layout.configure_item_lang,null);
                View style_v=inflate(R.layout.configure_item_lang,null);


                int textSize= (int) getResources().getDimensionPixelSize(R.dimen.simple_memo_textsize);

                screen.addView(sc);
                select.addView(bcolor);
                select.addView(hcolor);
                select.addView(mcolor);
                select.addView(scolor);
                select.addView(color);
                select.addView(textcolor);
                select.addView(font);
                select.addView(lang);
                select.addView(style_v);

                ((TextView)bcolor.findViewById(R.id.configure_color_title)).setText("Back Color");
                ((TextView)hcolor.findViewById(R.id.configure_color_title)).setText("Hour Hand Color");
                ((TextView)mcolor.findViewById(R.id.configure_color_title)).setText("Minute Hand Color");
                ((TextView)scolor.findViewById(R.id.configure_color_title)).setText("Second Hand Color");
                ((TextView)textcolor.findViewById(R.id.configure_color_title)).setText("Text Color");
                ((TextView)style_v.findViewById(R.id.configure_lang_title)).setText("Style");
                ((TextView)color.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(color_key))));
                ((TextView)bcolor.findViewById(R.id.configure_color_im))
                        .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(bcolor_key))));
                ((TextView)hcolor.findViewById(R.id.configure_color_im))
                        .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(hcolor_key))));
                ((TextView)mcolor.findViewById(R.id.configure_color_im))
                        .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(mcolor_key))));
                ((TextView)scolor.findViewById(R.id.configure_color_im))
                        .setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(scolor_key))));
                ((TextView)textcolor.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(data.get(text_color_key))));
                if (data.get(font_key)!=null){
                    Typeface font_ = Typeface.createFromAsset(getAssets(), String.format("fonts/%s", data.get(font_key)));
                    ((TextView) font.findViewById(R.id.configure_font_tx)).setTypeface(font_);
                }
                if (data.get(language_key)!=null){
                    ((TextView)lang.findViewById(R.id.configure_lang_tx)).setText(langs.get(data.get(language_key)));
                }
                if (data.get(style_key)!=null){
                    ((TextView)style_v.findViewById(R.id.configure_lang_tx)).setText(slist.get(data.get(style_key)));
                }

                class SetView{
                    Context context=getApplicationContext();
                    final long now=System.currentTimeMillis();
                    final int now_second= (int) (now%(60*1000))/1000;
                    float second_rotate=now_second*6;


                    int main=R.id.analog_clock_s1;
                    int sub=R.id.analog_clock_s2;
                    Bitmap bitmap1d;
                    Bitmap bitmap2d;

                    public void setView(){

                        Pair<Integer,Integer> wh=new Pair<>((int)(width*0.8/2),(int)(height*0.8/2));
                        int diameter=Math.min(wh.first,wh.second);
                        int back_color=Integer.valueOf(data.get(bcolor_key));
                        int dp10=dip(10,context);
                        int dp20=dip(20,context);


                        sc.findViewById(main).setVisibility(View.VISIBLE);
                        sc.findViewById(sub).setVisibility(View.GONE);
                        sc.findViewById(R.id.analog_clock_back_img2).setVisibility(View.GONE);



                        setViewC1();

                        setViewC2();

                        bitmap1d=AnalogClock.getTextBitmap(context,DateData.getNowTime(now),new Pair<>(diameter-dp20,diameter/2)
                                ,200,1,data.get(font_key));
                        bitmap2d=AnalogClock.getTextBitmap(context
                                , TextBitmap.getDowTextPE(DateData.getDateOfWeek()-1,context,null,data.get(language_key))+", "+DateData.getDateOfMonth(now)
                                ,new Pair<>(diameter-dp20,diameter/2),2000,1,data.get(font_key));

                        setViewDay();


                    }
                    public void setViewC1(){
                        Bitmap bitmap;
                        Pair<Integer,Integer> wh=new Pair<>((int)(width*0.8/2),(int)(height*0.8/2));

                        {
                            int diameter=Math.min(wh.first,wh.second)-dip(20,context);
                            if (diameter<=100)
                                diameter= (int) (Math.min(wh.first,wh.second)*0.8);
                            int second_color=Integer.valueOf(data.get(scolor_key));
                            bitmap=AnalogClock.rotatedRV(context,second_color,new Pair<>(diameter,diameter),second_rotate
                                    ,diameter>150?R.drawable.second_hand:R.drawable.second_hand2);
                            ((ImageView)sc.findViewById(main)).setImageBitmap(bitmap);
                            ((ImageView)sc.findViewById(main)).setColorFilter(Integer.valueOf(data.get(scolor_key)));
                            ((ImageView)sc.findViewById(main)).setImageAlpha(Color.alpha(Integer.valueOf(data.get(scolor_key))));

                        }
                    }
                    public void setViewC2(){
                        Bitmap bitmap;
                        Pair<Integer,Integer> wh=new Pair<>((int)(width*0.8/2),(int)(height*0.8/2));
                        int diameter=Math.min(wh.first,wh.second)-dip(20,context);
                        if (diameter<=100)
                            diameter= (int) (Math.min(wh.first,wh.second)*0.8);
                        int minute_color=Integer.valueOf(data.get(mcolor_key));
                        int hour_color=Integer.valueOf(data.get(hcolor_key));
                        int back_color=Integer.valueOf(data.get(bcolor_key));
                        bitmap=AnalogClock.getImageBitmap(context,new Pair<>(Math.min(wh.first,wh.second)
                                ,Math.min(wh.first,wh.second)),back_color,R.drawable.frame_style);
                        Canvas canvas=new Canvas(bitmap);

                        AnalogClock.getMark(context,canvas,Integer.valueOf(data.get(color_key)),mlist.getOrDefault(data.get(style_key),0)
                                ,data.get(font_key));

                        Bitmap bitmap1=AnalogClock.rotatedRV(context,hour_color,new Pair<>(diameter,diameter)
                                ,DateData.getHour(now)*30+DateData.getMinute(now)/2,R.drawable.hour_hand);
                        canvas.drawBitmap(bitmap1,(bitmap.getWidth()-bitmap1.getWidth())/2
                                ,(bitmap.getHeight()-bitmap1.getHeight())/2,null);
                        bitmap1=AnalogClock.rotatedRV(context,minute_color,new Pair<>(diameter,diameter)
                                ,DateData.getMinute(now)*6,R.drawable.minute_hand);
                        canvas.drawBitmap(bitmap1,(bitmap.getWidth()-bitmap1.getWidth())/2
                                ,(bitmap.getHeight()-bitmap1.getHeight())/2,null);
                        ((ImageView)sc.findViewById(R.id.analog_clock_back_img)).setImageBitmap(bitmap);
                    }
                    public void setViewDay(){
                        Pair<Integer,Integer> wh=new Pair<>((int)(width*0.8/2),(int)(height*0.8/2));
                        int diameter=Math.min(wh.first,wh.second);
                        int back_color=Integer.valueOf(data.get(bcolor_key));
                        int dp10=dip(10,context);
                        int dp20=dip(20,context);
                        if (diameter-dp20<1)
                            return;

                        //paint.setColorFilter(new BlendModeColorFilter(Integer.valueOf(data.get(text_color_key)), BlendMode.COLOR));
                        Bitmap bitmap=AnalogClock
                                .getImageBitmap(context,new Pair<>(Math.min(wh.first,wh.second)
                                ,Math.min(wh.first,wh.second)),back_color,R.drawable.frame_style);
                        Bitmap bitmap_vf=AnalogClock.getImageBitmap(context,new Pair<>(Math.min(wh.first,wh.second)
                                ,Math.min(wh.first,wh.second)),back_color,R.drawable.frame_style);

                        Paint paint=new Paint();
                        paint.setColorFilter(new PorterDuffColorFilter(Integer.valueOf(data.get(text_color_key)), PorterDuff.Mode.SRC_IN));
                        paint.setAlpha(Color.alpha(Integer.valueOf(data.get(text_color_key))));
                        Canvas canvas=new Canvas(bitmap_vf);
                        canvas.drawBitmap(bitmap1d,dp10,diameter/2,paint);
                        canvas.drawBitmap(bitmap2d,dp10,0,paint);
                        ((ImageView)sc.findViewById(R.id.analog_clock_back_img_od)).setImageBitmap(bitmap);
                        ((ImageView)sc.findViewById(R.id.analog_clock_back_img2_od)).setImageBitmap(bitmap_vf);
                    }


                }


                SetView setView=new SetView();
                setView.setView();

                vf.setInAnimation(this, R.anim.slide_in_right);

                color.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        ImageView imageView=((ImageView)sc.findViewById(R.id.schedule_img));
                        String key=color_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));

                                setView.setViewC2();


                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();


                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();


                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();

                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                    }
                });

                bcolor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        ImageView imageView=((ImageView)sc.findViewById(R.id.schedule_img));
                        String key=bcolor_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                                setView.setViewDay();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));

                                setView.setViewC2();
                                setView.setViewDay();


                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                                setView.setViewDay();


                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();

                                setView.setViewDay();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();
                                setView.setViewDay();

                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                    }
                });
                hcolor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        ImageView imageView=((ImageView)sc.findViewById(R.id.schedule_img));
                        String key=hcolor_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));

                                setView.setViewC2();


                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();


                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();


                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();

                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                    }
                });
                mcolor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        ImageView imageView=((ImageView)sc.findViewById(R.id.schedule_img));
                        String key=mcolor_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));

                                setView.setViewC2();


                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();


                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();


                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC2();

                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                    }
                });
                scolor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        ImageView imageView=((ImageView)sc.findViewById(R.id.schedule_img));
                        String key=scolor_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));

                                setView.setViewC1();


                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));

                                setView.setViewC1();


                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC1();


                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewC1();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));

                                setView.setViewC1();


                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });
                    }
                });

                textcolor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_color_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        GridAdapterColor gridAdapter=new GridAdapterColor(getApplicationContext());
                        gv.setAdapter(gridAdapter);

                        SeekBar red=l.findViewById(R.id.configure_color_select_red);
                        SeekBar green=l.findViewById(R.id.configure_color_select_green);
                        SeekBar blue=l.findViewById(R.id.configure_color_select_blue);
                        SeekBar alpha=l.findViewById(R.id.configure_color_select_alpha);
                        String key=text_color_key;
                        final int[] color = {Integer.valueOf(data.get(key))};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);

                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_color_im)).setBackgroundTintList(ColorStateList.valueOf(color[0]));
                            }
                        });


                        red.setMax(255);
                        green.setMax(255);
                        blue.setMax(255);
                        alpha.setMax(255);
                        red.setProgress(Color.red(color[0]));
                        green.setProgress(Color.green(color[0]));
                        blue.setProgress(Color.blue(color[0]));
                        alpha.setProgress(Color.alpha(color[0]));

                        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),progress,Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),progress,Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(Color.alpha(color[0]),Color.red(color[0]),Color.green(color[0]),progress);
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                color[0] =Color.argb(progress,Color.red(color[0]),Color.green(color[0]),Color.blue(color[0]));
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                data.put(key,String.valueOf(color[0]));
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color[0]=GridAdapterColor.clolrs[position];
                                data.put(key,String.valueOf(color[0]));
                                setView.setViewDay();

                                red.setProgress(Color.red(color[0]),true);
                                green.setProgress(Color.green(color[0]),true);
                                blue.setProgress(Color.blue(color[0]),true);
                                alpha.setProgress(Color.alpha(color[0]),true);
                                data.put(key,String.valueOf(color[0]));
                            }
                        });
                    }
                });
                font.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_font_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
                        GridView gv=l.findViewById(R.id.configure_color_select_gv);
                        gv.setAdapter(gridAdapter);
                        String key=font_key;
                        final String[] font = {data.get(key)};

                        int colum=(width-Simple_Memo.dip(getApplicationContext(),40))/
                                Simple_Memo.dip(getApplicationContext(),40);
                        if (colum<1)
                            colum=1;
                        gv.setNumColumns(colum);
                        l.findViewById(R.id.configure_color_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                if (font[0]!=null) {
                                    Typeface font_ = Typeface.createFromAsset(getAssets(), String.format("fonts/%s", font[0]));
                                    ((TextView) v.findViewById(R.id.configure_font_tx)).setTypeface(font_);
                                }
                                else
                                    ((TextView) v.findViewById(R.id.configure_font_tx)).setTypeface(null);
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                font[0]=gridAdapter.fonts[position];
                                data.put(key,font[0]);
                                setView.setView();
                            }
                        });
                    }
                });

                lang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_langt_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        ListView li=l.findViewById(R.id.configure_lang_select_li);

                        ListAdapterLang listAdapterLang=new ListAdapterLang(langs.keySet().toArray(new String[langs.size()])
                                ,getApplicationContext(),langs);

                        li.setAdapter(listAdapterLang);
                        String key=language_key;
                        final String[] language = {data.get(key)};

                        l.findViewById(R.id.configure_lang_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)lang.findViewById(R.id.configure_lang_tx)).setText(langs.get(data.get(key)));
                            }
                        });

                        li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                language[0]=listAdapterLang.list[position];
                                data.put(key,language[0]);
                                setView.setView();
                            }
                        });
                    }
                });
                style_v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vf.showNext();
                        set.removeAllViews();
                        View l=inflate(R.layout.configure_langt_select,null);
                        set.addView(l);
                        vf.setInAnimation(getApplicationContext(), R.anim.slide_in_left);

                        ListView li=l.findViewById(R.id.configure_lang_select_li);

                        ListAdapterLang listAdapterLang=new ListAdapterLang(slist.keySet().toArray(new String[slist.size()])
                                ,getApplicationContext(),slist);

                        li.setAdapter(listAdapterLang);
                        String key=style_key;
                        final String[] language = {data.get(key)};

                        l.findViewById(R.id.configure_lang_select_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View pv) {
                                vf.showPrevious();
                                vf.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                                ((TextView)v.findViewById(R.id.configure_lang_tx)).setText(slist.get(data.get(key)));
                            }
                        });

                        li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                language[0]=listAdapterLang.list[position];
                                data.put(key,language[0]);
                                setView.setViewC2();
                            }
                        });
                    }
                });

                break;
            }
        }
    }
    public static View inflate(Context context,int resource, ViewGroup root) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        return inflater.inflate(resource, root, root != null);
    }
    public View inflate(int resource, ViewGroup root) {
        return getLayoutInflater().inflate(resource, root, root != null);
    }
    private String[] displayAssets(Context context, String dir){
        AssetManager assetMgr = context.getResources().getAssets();
        try {
            String files[] = assetMgr.list(dir);
            return files;
        } catch (IOException e) {
        }
        return new String[]{};
    }

    public void checkPermission(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_CALENDAR
                    },
                    PERMISSION_WRITE_EX_STR);
        }
        else
            finish();
    }

    public Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(1000,1000, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length <= 0) {
            return;
        }
        if (requestCode == PERMISSION_WRITE_EX_STR) {
            finish();
        }
    }
}