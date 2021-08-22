package cn.tihuxueyuan.verticaltabrecycler;

import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import cn.tihuxueyuan.R;
import cn.tihuxueyuan.adapter.GridRecycleAdapter;

public class ItemFragment extends Fragment {
    private View view;
    private List<String> list;
    private String title;
    private GridRecycleAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tvName;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_item, container, false);
            initView();
        } else {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        return view;
    }

    public static ItemFragment getInstance(TestData testData){
        ItemFragment fragment = new ItemFragment();
        fragment.list = testData.getItemName();
        fragment.title = testData.getName();
        return fragment;
    }

    private void initView(){
        recyclerView = view.findViewById(R.id.recycler_view);
        tvName = view.findViewById(R.id.tv_name);
        GridLayoutManager glm = new GridLayoutManager(getContext(),3);
        tvName.setText(title);
        recyclerView.setLayoutManager(glm);
        adapter = new GridRecycleAdapter(getContext(),list);
        recyclerView.setAdapter(adapter);
    }
}
