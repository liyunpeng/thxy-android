package cn.tihuxueyuan.verticaltabrecycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.tihuxueyuan.R;
import androidx.recyclerview.widget.RecyclerView;


public class GirdItemViewHolder extends RecyclerView.ViewHolder {
    public TextView tvName;
    public GirdItemViewHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.vh_gird_item, parent, false));
        tvName = itemView.findViewById(R.id.tv_name);
    }
}
