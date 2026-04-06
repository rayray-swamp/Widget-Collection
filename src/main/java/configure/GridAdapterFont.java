package configure;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import calendar.R;

public class GridAdapterFont extends BaseAdapter {
    private final Context context;
    private LayoutInflater inflater;
    public final Set<Integer> set=new HashSet<>();
    public String[] fonts;
    public Typeface[] fonts2;
    public GridAdapterFont(Context context){
        this.context = context;
        this.inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String[] fonts;
        fonts=displayAssets(context,"fonts");
        /*for (int i=0;i<fonts.length;i++){
            fonts[i]=fonts[i].split("\\.")[0];
        }*/
        String[] bar = new String[fonts.length+1]; // データを5個追加したい場合に用意する配列

        System.arraycopy(fonts, 0, bar, 1, fonts.length);
        bar[0]=null;
        fonts=bar;
        this.fonts=fonts;
        this.fonts2=new Typeface[fonts.length];
        int i=0;
        for (String font:fonts){
            if (font!=null)
                fonts2[i]=Typeface.createFromAsset(context.getAssets(), String.format("fonts/%s", font));
            else
                fonts2[i]=null;
            i++;
        }

    }
    private String[] displayAssets(Context context,String dir){
        AssetManager assetMgr = context.getResources().getAssets();
        try {
            String files[] = assetMgr.list(dir);
            return files;
        } catch (IOException e) {
        }
        return new String[]{};
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // main.xml の <GridView .../> に grid_items.xml を inflate して convertView とする
            convertView = inflater.inflate(R.layout.font_grid_item, parent, false);
        }

        TextView textView =convertView.findViewById(R.id.font_grid_item_im);
        if (fonts[position]!=null) {
            textView.setTypeface(fonts2[position]);
        }
        else
            textView.setTypeface(null);
        return convertView;
    }



    @Override
    public int getCount() {
        return fonts.length;
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
