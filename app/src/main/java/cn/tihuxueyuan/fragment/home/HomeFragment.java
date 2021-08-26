package cn.tihuxueyuan.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import cn.tihuxueyuan.R;
import cn.tihuxueyuan.activity.MusicMainActivity;
import cn.tihuxueyuan.activity.OkhttpClientActivity;
import cn.tihuxueyuan.commonlistview.CommonAdapter;
import cn.tihuxueyuan.commonlistview.ViewHolder;
import cn.tihuxueyuan.databinding.FragmentHomeBinding;
import cn.tihuxueyuan.http.HttpCallback;
import cn.tihuxueyuan.http.HttpClient;
import cn.tihuxueyuan.model.CourseFileList;
import cn.tihuxueyuan.model.CourseTypeList;
import cn.tihuxueyuan.verticaltabrecycler.MainActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private CommonAdapter mAdapter;
    private android.widget.ListView lv;
    private android.widget.RelativeLayout activitymain;

    private List<CourseFileList.CourseFile> mList = new ArrayList<>();
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
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (id == 1) {
//                    Intent intent = new Intent(HomeFragment.this.getContext(), MusicMainActivity.class);//创建Intent对象，启动check
////                intent.putExtra("name",name[position]);
////                intent.putExtra("position",String.valueOf(position));
//                    startActivity(intent);
//                } else if (id == 2) {
//                    Intent intent = new Intent(HomeFragment.this.getContext(), OkhttpClientActivity.class);//创建Intent对象，启动check
//                    startActivity(intent);
//                } else if ( id == 3){
//                    Intent intent = new Intent(HomeFragment.this.getContext(), MainActivity.class);//创建Intent对象，启动check
//                    startActivity(intent);
//                }
//            }
//        });
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
        lv.setAdapter(mAdapter = new CommonAdapter<CourseFileList.CourseFile>(this.getContext(), mList, R.layout.dashboard_item_layout) {
            @Override
            public void convertView(ViewHolder holder, CourseFileList.CourseFile contactsBean) {
                holder.set(R.id.name, contactsBean.getTitle());
            }
        });

//        mAdapter.notifyDataSetChanged();
    }

    public void initCourseType() {
        HttpClient.getLatest("", new HttpCallback<CourseFileList>() {
            @Override
            public void onSuccess(CourseFileList response) {
                if (response == null || response.getCourseFileList() == null || response.getCourseFileList().isEmpty()) {
                    onFail(null);
                    return;
                }
                mList = response.getCourseFileList();
                refreshListView();
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }
}