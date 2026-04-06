package todays_memo;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import calendar.R;

public class GridAdapter extends BaseAdapter {
    private final Context context;
    private LayoutInflater inflater;
    public final Set<Integer> set=new HashSet<>();

    public GridAdapter(Context context, int nowdate){
        this.context = context;
        this.inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        set.add(nowdate);
    }

    public void setdate(int date){
        if (set.contains(date))
            set.remove(date);
        else
            set.add(date);
    }
    public void removedate(int date){
        set.remove(date);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // main.xml の <GridView .../> に grid_items.xml を inflate して convertView とする
            convertView = inflater.inflate(R.layout.today_memo_rm_grid_item, parent, false);
        }

        TextView textView=convertView.findViewById(R.id.today_memo_dialog_rm_grid_im);

        textView.setText(""+(position+1));
        if (set.contains(position+1)) {
            textView.setBackgroundColor((context.getColor(R.color.red)));
            textView.setTextColor(context.getColor(R.color.white));
        }
        else {
            textView.setBackgroundColor((Color.argb(0, 0, 0, 0)));
            textView.setTextColor(context.getColor(R.color.black));
        }
        return convertView;
    }



    @Override
    public int getCount() {
        return 31;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}
