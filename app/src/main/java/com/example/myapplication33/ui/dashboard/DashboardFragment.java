package com.example.myapplication33.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication33.Music_Activity;
import com.example.myapplication33.MusicMainActivity;
import com.example.myapplication33.OkActivity;
import com.example.myapplication33.R;
import com.example.myapplication33.commonlistview.CommonAdapter;
import com.example.myapplication33.commonlistview.ContactsBean;
import com.example.myapplication33.commonlistview.ViewHolder;
import com.example.myapplication33.databinding.FragmentDashboardBinding;
import com.example.myapplication33.frag1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DashboardFragment extends Fragment {
    private List<ContactsBean> mList = new ArrayList<>();
    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;

    private CommonAdapter mAdapter;
    private android.widget.ListView lv;
    private android.widget.RelativeLayout activitymain;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*
        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
*/

        this.lv = (ListView) root.findViewById(R.id.lv);
//        lv.setAdapter(mAdapter=new ContactsAdapter(this,mList));
        lv.setAdapter(mAdapter = new CommonAdapter<ContactsBean>(this.getContext(), mList, R.layout.dashboard_item_layout) {
            @Override
            public void convertView(ViewHolder holder, ContactsBean contactsBean) {
                holder.set(R.id.name, contactsBean.getName())
                        .set(R.id.phone_number, contactsBean.getPhoneNumber())
                        .set(R.id.head_img, contactsBean.getHead_img());
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ( id == 1 ) {
                    Intent intent=new Intent(DashboardFragment.this.getContext(), MusicMainActivity.class);//创建Intent对象，启动check
//                intent.putExtra("name",name[position]);
//                intent.putExtra("position",String.valueOf(position));
                    // ，
                    startActivity(intent);
                }else if (id == 2) {
                    Intent intent=new Intent(DashboardFragment.this.getContext(), OkActivity.class);//创建Intent对象，启动check
                    startActivity(intent);
                }



            }
        });




        initData();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void initData() {
        for (int i = 0; i < 20; i++) {
            ContactsBean bean = new ContactsBean();
            bean.setName("小明");
            bean.setPhoneNumber("110");
            bean.setHead_img("http://img.qqai.net/uploads/i_2_1826721258x2403292640_21.jpg");
            mList.add(bean);
        }
        mAdapter.notifyDataSetChanged();
    }
}