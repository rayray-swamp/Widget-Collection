package simple_memo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import calendar.DateData;
import calendar.NewAppWidget;
import calendar.R;
import calendar.TextBitmap;
import save.ObjectStorage;
import todays_memo.Memo_Activity;
import todays_memo.todays_memo;

public class Simple_Memo_activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple__memo_activity);
        Context context= Simple_Memo_activity.this;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dialogWidth = (int) (metrics.widthPixels * 0.8);
        Dialog dialog = new Dialog(context,R.style.MyDialogTheme);
        View view=View.inflate(this, R.layout.simple_memo_dialog,null);
        dialog.setContentView(view);
        final int id=getIntent().getExtras().getInt("ID");
        final EditText editText = view.findViewById(R.id.simple_memo_dialog_text);
        final Button button_ok=view.findViewById(R.id.simple_memo_dialog_ok);
        final Button button_cancel=view.findViewById(R.id.simple_memo_dialog_cancel);
        String text="";
        Map<String,String> data= ObjectStorage.get(Simple_Memo.SIMPLE_MEMO, Map.class,context);

        if (data!=null){
            text=data.getOrDefault(String.valueOf(id),"");
        }
        editText.setText(text);

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=editText.getText().toString();
                Map<String,String> data= ObjectStorage.get(Simple_Memo.SIMPLE_MEMO, Map.class,context);
                if (data==null)
                    data=new HashMap<>();
                data.put(String.valueOf(id),text);
                ObjectStorage.save(data,Simple_Memo.SIMPLE_MEMO,context);
                Intent refreshIntent = new Intent(context, Simple_Memo.class);
                refreshIntent.setAction("action");;
                PendingIntent configPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                try {
                    configPendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });



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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                editText.setSelection(editText.getText().length());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        },500);
    }
}