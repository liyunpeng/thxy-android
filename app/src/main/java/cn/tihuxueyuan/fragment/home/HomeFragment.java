package cn.tihuxueyuan.fragment.home;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import cn.tihuxueyuan.R;
import cn.tihuxueyuan.activity.MusicMainActivity;
import cn.tihuxueyuan.activity.OkActivity;
import cn.tihuxueyuan.commonlistview.CommonAdapter;
import cn.tihuxueyuan.commonlistview.ViewHolder;
import cn.tihuxueyuan.databinding.FragmentHomeBinding;
import cn.tihuxueyuan.fragment.dashboard.DashboardFragment;
import cn.tihuxueyuan.http.HttpCallback;
import cn.tihuxueyuan.http.HttpClient;
import cn.tihuxueyuan.model.CourseTypeList;
import cn.tihuxueyuan.verticaltabrecycler.MainActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private CommonAdapter mAdapter;
    private android.widget.ListView lv;
    private android.widget.RelativeLayout activitymain;

    private List<CourseTypeList.CourseType> mList = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });


        this.lv = (ListView) root.findViewById(R.id.lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id == 1) {
                    Intent intent = new Intent(HomeFragment.this.getContext(), MusicMainActivity.class);//创建Intent对象，启动check
//                intent.putExtra("name",name[position]);
//                intent.putExtra("position",String.valueOf(position));
                    startActivity(intent);
                } else if (id == 2) {
                    Intent intent = new Intent(HomeFragment.this.getContext(), OkActivity.class);//创建Intent对象，启动check
                    startActivity(intent);
                } else if ( id == 3){
                    Intent intent = new Intent(HomeFragment.this.getContext(), MainActivity.class);//创建Intent对象，启动check
                    startActivity(intent);
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    };

    @Override
    public void onResume() {
        super.onResume();
        initCourseType();
    }

    public void refreshListView() {
        lv.setAdapter(mAdapter = new CommonAdapter<CourseTypeList.CourseType>(this.getContext(), mList, R.layout.dashboard_item_layout) {
            @Override
            public void convertView(ViewHolder holder, CourseTypeList.CourseType contactsBean) {
                holder.set(R.id.name, contactsBean.getName());
            }
        });

//        mAdapter.notifyDataSetChanged();
    }

    public void initCourseType() {
        HttpClient.getCourseTypes("", new HttpCallback<CourseTypeList>() {
            @Override
            public void onSuccess(CourseTypeList response) {
                if (response == null || response.getCourseType() == null || response.getCourseType().isEmpty()) {
                    onFail(null);
                    return;
                }
                mList = response.getCourseType();
//                for (int i = 0; i < 5; i++) {
//                    Log.d("tag2", "item： " +  mList.get(i).getName());
//                }

                refreshListView();
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }
}