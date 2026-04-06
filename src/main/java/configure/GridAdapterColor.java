package configure;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import calendar.R;

public class GridAdapterColor extends BaseAdapter {
    private final Context context;
    private LayoutInflater inflater;
    public final Set<Integer> set=new HashSet<>();
    public static final int[] clolrs=new int[]{Color.WHITE,Color.BLACK,Color.parseColor("#808080")
            ,Color.parseColor("#CD5C5C"),Color.parseColor("#F08080"),Color.parseColor("#FA8072"),Color.parseColor("#E9967A"),Color.parseColor("#FFA07A"),Color.parseColor("#DC143C"),Color.parseColor("#FF0000"),Color.parseColor("#B22222"),Color.parseColor("#8B0000"),Color.parseColor("#FFC0CB"),Color.parseColor("#FFB6C1"),Color.parseColor("#FF69B4"),Color.parseColor("#FF1493"),Color.parseColor("#C71585"),Color.parseColor("#DB7093"),Color.parseColor("#FF7F50"),Color.parseColor("#FF6347"),Color.parseColor("#FF4500"),Color.parseColor("#FF8C00"),Color.parseColor("#FFA500"),Color.parseColor("#FFD700"),Color.parseColor("#FFFF00"),Color.parseColor("#FFFFE0"),Color.parseColor("#FFFACD"),Color.parseColor("#FAFAD2"),Color.parseColor("#FFEFD5"),Color.parseColor("#FFE4B5"),Color.parseColor("#FFDAB9"),Color.parseColor("#EEE8AA"),Color.parseColor("#F0E68C"),Color.parseColor("#BDB76B"),Color.parseColor("#E6E6FA"),Color.parseColor("#D8BFD8"),Color.parseColor("#DDA0DD"),Color.parseColor("#EE82EE"),Color.parseColor("#DA70D6"),Color.parseColor("#FF00FF"),Color.parseColor("#FF00FF"),Color.parseColor("#BA55D3"),Color.parseColor("#9370DB"),Color.parseColor("#9966CC"),Color.parseColor("#8A2BE2"),Color.parseColor("#9400D3"),Color.parseColor("#9932CC"),Color.parseColor("#8B008B"),Color.parseColor("#800080"),Color.parseColor("#4B0082"),Color.parseColor("#6A5ACD"),Color.parseColor("#483D8B"),Color.parseColor("#ADFF2F"),Color.parseColor("#7FFF00"),Color.parseColor("#7CFC00"),Color.parseColor("#00FF00"),Color.parseColor("#32CD32"),Color.parseColor("#98FB98"),Color.parseColor("#90EE90"),Color.parseColor("#00FA9A"),Color.parseColor("#00FF7F"),Color.parseColor("#3CB371"),Color.parseColor("#2E8B57"),Color.parseColor("#228B22"),Color.parseColor("#008000"),Color.parseColor("#006400"),Color.parseColor("#9ACD32"),Color.parseColor("#6B8E23"),Color.parseColor("#808000"),Color.parseColor("#556B2F"),Color.parseColor("#66CDAA"),Color.parseColor("#8FBC8F"),Color.parseColor("#20B2AA"),Color.parseColor("#008B8B"),Color.parseColor("#008080"),Color.parseColor("#00FFFF"),Color.parseColor("#00FFFF"),Color.parseColor("#E0FFFF"),Color.parseColor("#AFEEEE"),Color.parseColor("#7FFFD4"),Color.parseColor("#40E0D0"),Color.parseColor("#48D1CC"),Color.parseColor("#00CED1"),Color.parseColor("#5F9EA0"),Color.parseColor("#4682B4"),Color.parseColor("#B0C4DE"),Color.parseColor("#B0E0E6"),Color.parseColor("#ADD8E6"),Color.parseColor("#87CEEB"),Color.parseColor("#87CEFA"),Color.parseColor("#00BFFF"),Color.parseColor("#1E90FF"),Color.parseColor("#6495ED"),Color.parseColor("#7B68EE"),Color.parseColor("#4169E1"),Color.parseColor("#0000FF"),Color.parseColor("#0000CD"),Color.parseColor("#00008B"),Color.parseColor("#000080"),Color.parseColor("#191970"),Color.parseColor("#FFF8DC"),Color.parseColor("#FFEBCD"),Color.parseColor("#FFE4C4"),Color.parseColor("#FFDEAD"),Color.parseColor("#F5DEB3"),Color.parseColor("#DEB887"),Color.parseColor("#D2B48C"),Color.parseColor("#BC8F8F"),Color.parseColor("#F4A460"),Color.parseColor("#DAA520"),Color.parseColor("#B8860B"),Color.parseColor("#CD853F"),Color.parseColor("#D2691E"),Color.parseColor("#8B4513"),Color.parseColor("#A0522D"),Color.parseColor("#A52A2A"),Color.parseColor("#800000"),Color.parseColor("#FFFFFF"),Color.parseColor("#FFFAFA"),Color.parseColor("#F0FFF0"),Color.parseColor("#F5FFFA"),Color.parseColor("#F0FFFF"),Color.parseColor("#F0F8FF"),Color.parseColor("#F8F8FF"),Color.parseColor("#F5F5F5"),Color.parseColor("#FFF5EE"),Color.parseColor("#F5F5DC"),Color.parseColor("#FDF5E6"),Color.parseColor("#FFFAF0"),Color.parseColor("#FFFFF0"),Color.parseColor("#FAEBD7"),Color.parseColor("#FAF0E6"),Color.parseColor("#FFF0F5"),Color.parseColor("#FFE4E1"),Color.parseColor("#DCDCDC"),Color.parseColor("#D3D3D3"),Color.parseColor("#C0C0C0"),Color.parseColor("#A9A9A9"),Color.parseColor("#808080"),Color.parseColor("#696969"),Color.parseColor("#778899"),Color.parseColor("#708090"),Color.parseColor("#2F4F4F"),Color.parseColor("#000000"),Color.parseColor("#233B6C"),Color.parseColor("#165E83"),Color.parseColor("#411445"),Color.parseColor("#6A4C9C"),Color.parseColor("#745399"),Color.parseColor("#918D40"),Color.parseColor("#BCB09C"),Color.parseColor("#D6C6AF"),Color.parseColor("#F8B400"),Color.parseColor("#98514B"),Color.parseColor("#B3424A"),Color.parseColor("#B13546"),Color.parseColor("#B1063A")};

    public GridAdapterColor(Context context){
        this.context = context;
        this.inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // main.xml の <GridView .../> に grid_items.xml を inflate して convertView とする
            convertView = inflater.inflate(R.layout.color_grid_item, parent, false);
        }

        ImageView imageView =convertView.findViewById(R.id.color_grid_item_im);

        imageView.setBackgroundTintList(ColorStateList.valueOf(clolrs[position]));
        return convertView;
    }



    @Override
    public int getCount() {
        return clolrs.length;
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
