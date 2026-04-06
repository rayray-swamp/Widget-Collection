package todays_memo;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import calendar.R;

public class DialogViewHolder extends RecyclerView.ViewHolder {
    public TextView textView;
    public DialogViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.ksl_button);
    }
}