package calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.time.Clock;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import Schedule.Select_day_activity;
import configure.AppWidgetConfigureActivity;
import save.ObjectStorage;

public class MainActivity extends AppCompatActivity {

    static public final String SIMPLE_MEMO="sm"+NewAppWidget.PARAM;
    static public final String DIGITAL_CLOCK="dc"+NewAppWidget.PARAM;
    static public final String SCHEDULE="sc"+NewAppWidget.PARAM;
    static public final String TODAYS_MEMO="td"+NewAppWidget.PARAM;
    static public final String CALENDAR="ca"+NewAppWidget.PARAM;
    static public final String ANALOG_CLOCK="ac"+NewAppWidget.PARAM;
    static public final String DEFAULT="default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        set();

    }

    public void set(){
        LinearLayout ll=findViewById(R.id.main_ll);
        ll.removeAllViews();

        class data{
            public final String name;
            public final int id;
            public final int drawable;
            public String video=null;
            public Map<Integer,String> Description=new HashMap<>();

            data(String name, int id, int drawable) {
                this.name = name;
                this.id = id;
                this.drawable = drawable;
            }

            data(String name, int id, int drawable,String video) {
                this.name = name;
                this.id = id;
                this.drawable = drawable;
                this.video=video;
            }

            data(String name, int id, int drawable,String video,String[][] descriptions) {
                this.name = name;
                this.id = id;
                this.drawable = drawable;
                this.video=video;
                if (descriptions!=null){
                    for (String[] d:descriptions){
                        Description.put(Integer.valueOf(d[0]),d[1]);
                    }
                }
            }
        }

        Map<String,data> widges=new LinkedHashMap<>();
        widges.put(ANALOG_CLOCK,new data(getString(R.string.Analog_Clock)
                ,R.layout.analog_clock,R.drawable.analog_clock));
        widges.put(CALENDAR,new data(getString(R.string.calendar)
                ,R.layout.new_app_widget, R.drawable.ca,"/ca_htu"
                ,new String[][]{{""+10,getString(R.string.Allow_it_to_work_with_Google_Calendar)},
                {""+16,getString(R.string.Select_by_pressing_the_date_on_the_calendar)},
                {""+21,getString(R.string.You_can_change_the_month)},
                {""+35,getString(R.string.ca35)},
                {""+2,getString(R.string.ca2)}}));
        widges.put(DIGITAL_CLOCK,new data(getString(R.string.Digital_Clock)
                ,R.layout.digital_clock, R.drawable.dc));
        widges.put(SIMPLE_MEMO,new data(getString(R.string.simple_memo)
                ,R.layout.simple__memo, R.drawable.sm));
        widges.put(SCHEDULE,new data(getString(R.string.Schedule)
                ,R.layout.schedule,R.drawable.sc,"/sc_htu"
                ,new String[][]{{""+2,getString(R.string.sc2)},
                {""+12,getString(R.string.Allow_it_to_work_with_Google_Calendar)},
                {""+27,getString(R.string.sc30)},
                {""+34,getString(R.string.sc34)},
                {""+42,getString(R.string.sc42)},
                {""+53,getString(R.string.sc53)}}));
        widges.put(TODAYS_MEMO,new data(getString(R.string.todays_memo)
                ,R.layout.todays_memo, R.drawable.td,"/tm_htu"
                ,new String[][]{{""+2,getString(R.string.tm2)},
                {""+17,getString(R.string.sc30)},
                {""+23,getString(R.string.tm23)},
                {""+40,getString(R.string.tm40)},
                {""+54,getString(R.string.tm54)}}));


        for (Map.Entry<String,data> entry:widges.entrySet()){
            View view= AppWidgetConfigureActivity.inflate(this,R.layout.main_ll_item,null);
            setBitmapFromArray(view.findViewById(R.id.main_ll_item_img),entry.getValue().drawable, entry.getKey());
            ((TextView)view.findViewById(R.id.main_ll_item_text)).setText(entry.getValue().name);
            if (entry.getValue().video==null)
                view.findViewById(R.id.main_ll_item_howtouse_ll).setVisibility(View.INVISIBLE);
            else {
                view.findViewById(R.id.main_ll_item_howtouse).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog dialog = new Dialog(MainActivity.this,R.style.MyDialogTheme);
                        View view=View.inflate(MainActivity.this, R.layout.configure_dialog,null);
                        VideoView videoView=view.findViewById(R.id.configure_dialog_video);
                        String uriPath = "android.resource://"+getPackageName()+"/raw"+entry.getValue().video;
                        Uri uri = Uri.parse(uriPath);
                        videoView.setVideoURI(uri);
                        dialog.setContentView(view);
                        DisplayMetrics metrics = getResources().getDisplayMetrics();
                        int dialogWidth = (int) (metrics.widthPixels * 0.7);
                        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                        lp.width = dialogWidth;
                        dialog.getWindow().setAttributes(lp);

                        Handler mHandler = new Handler();
                        Runnable runnable=new Runnable() {

                            @Override
                            public void run() {
                                if(videoView != null){
                                    int mCurrentPosition = videoView.getCurrentPosition() / 1000;
                                    if (entry.getValue().Description.containsKey(mCurrentPosition)){
                                        Context context = getApplicationContext();
                                        Toast.makeText(context , entry.getValue().Description.get(mCurrentPosition)
                                                , Toast.LENGTH_LONG).show();
                                    }

                                }
                                mHandler.postDelayed(this, 1000);
                            }
                        };
                        MainActivity.this.runOnUiThread(runnable);

                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                mHandler.removeCallbacks(runnable);
                            }
                        });

                        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mHandler.removeCallbacks(runnable);
                                dialog.dismiss();
                            }
                        });

                        try {
                            dialog.show();
                            videoView.start();

                        }catch (Exception e){}
                    }
                });
            }
            view.findViewById(R.id.main_ll_item_custom).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent configIntent = new Intent(getApplicationContext(), AppWidgetConfigureActivity.class);
                    configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,entry.getValue().id);
                    configIntent.putExtra("FromMain",true);
                    startActivity(configIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.scale_out);
                }
            });
            ll.addView(view);
        }

    }

    private static float spToPx(float sp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
    public static Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static byte[] getViewBitmap(View view){
        if (view==null)
            return null;

        try {

            Bitmap bitmap = getBitmapFromView(view);

            bitmap=getResizedBitmap(bitmap,1000, (int) (((float)bitmap.getHeight())/((float) bitmap.getWidth())*1000f));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void setBitmapFromArray(ImageView imageView,int defaultId,String imageId){
        byte[] bytes=ObjectStorage.get(imageId,byte[].class,this);

        if (bytes==null){
            imageView.setImageResource(defaultId);
            return;
        }

        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        imageView.setImageBitmap(bitmap);
    }
    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
