package cn.tihuxueyuan.verticaltabrecycler;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.tihuxueyuan.R;

public class LinerItemViewHolder extends RecyclerView.ViewHolder {
    public TextView tvName;
    public RecyclerView recyclerView;
    public LinearLayout llMain;
    public View blockView;
    public LinerItemViewHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.vh_liner_item, parent, false));
        tvName = itemView.findViewById(R.id.tv_name);
        recyclerView = itemView.findViewById(R.id.recycler_view);
        llMain = itemView.findViewById(R.id.ll_main);
        blockView = itemView.findViewById(R.id.block_view);
    }
}
