package todays_memo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import calendar.R;

public class DialogRecycleViewAdapter extends RecyclerView.Adapter<DialogViewHolder> {
    final Context context;
    final List<String> list;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(String text);
    }

    public DialogRecycleViewAdapter(Context context, int[] list){

        this.context = context;
        this.list = new ArrayList<>();
        for (int i:list)
            this.list.add(context.getString(i));
    }

    public void setOnItemClickLisner(OnItemClickListener listener) {
        this.listener = listener;
    }



    @NonNull
    @Override
    public DialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.keybord_suport_list, parent,false);
        DialogViewHolder vh = new DialogViewHolder(inflate);
        return vh;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(@NonNull DialogViewHolder holder, int position) {
        holder.textView.setText(list.get(position));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final OnItemClickListener onItemClickListener=listener;
                if (onItemClickListener!=null)
                    onItemClickListener.onItemClick(((TextView)v).getText().toString());
            }
        });
    }
}
