package configure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import calendar.R;

public class ListAdapterLang extends BaseAdapter {

    public final String[] list;
    private final Context context;
    private LayoutInflater inflater;
    private final Map<String,String> langs;

    public ListAdapterLang(String[] list, Context context,Map<String,String> langs) {
        this.list = list;
        this.context = context;
        this.langs=langs;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // main.xml の <GridView .../> に grid_items.xml を inflate して convertView とする
            convertView = inflater.inflate(R.layout.lang_list_item, parent, false);
        }

        TextView textView =convertView.findViewById(R.id.lang_list_item_tx);
        textView.setText(langs.get(list[position]));

        return convertView;
    }
}
