package cn.tihuxueyuan.verticaltabrecycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import cn.tihuxueyuan.R;


public class GirdItemViewHolderA extends RecyclerView.ViewHolder {
    public TextView tvName;
    public GirdItemViewHolderA(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.vh_girg_itema, parent, false));
        tvName = itemView.findViewById(R.id.tv_name);
    }
}
