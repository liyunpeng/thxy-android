package cn.tihuxueyuan.fragment.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cn.tihuxueyuan.activity.MusicMainActivity;
import cn.tihuxueyuan.activity.OkActivity;
import cn.tihuxueyuan.R;
import cn.tihuxueyuan.commonlistview.CommonAdapter;
import cn.tihuxueyuan.commonlistview.ViewHolder;
import cn.tihuxueyuan.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.List;

import cn.tihuxueyuan.http.HttpClient;
import cn.tihuxueyuan.http.HttpCallback;
import cn.tihuxueyuan.model.SearchMusic;
import cn.tihuxueyuan.model.CourseTypeList;
import cn.tihuxueyuan.model.CourseTypeList.CourseType;

public class DashboardFragment extends Fragment {
    //    private List<ContactsBean> mList = new ArrayList<>();
    private List<CourseType> mList = new ArrayList<>();
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


        this.lv = (ListView) root.findViewById(R.id.lv);
//        lv.setAdapter(mAdapter=new ContactsAdapter(this,mList));
//        lv.setAdapter(mAdapter = new CommonAdapter<CourseType>(this.getContext(), mList, R.layout.dashboard_item_layout) {
//            @Override
//            public void convertView(ViewHolder holder, CourseType contactsBean) {
////                holder.set(R.id.name, contactsBean.getName())
////                        .set(R.id.phone_number, contactsBean.getPhoneNumber())
////                        .set(R.id.head_img, contactsBean.getHead_img());
//                holder.set(R.id.name, contactsBean.getName());
//            }
//        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id == 1) {
                    Intent intent = new Intent(DashboardFragment.this.getContext(), MusicMainActivity.class);//创建Intent对象，启动check
//                intent.putExtra("name",name[position]);
//                intent.putExtra("position",String.valueOf(position));
                    startActivity(intent);
                } else if (id == 2) {
                    Intent intent = new Intent(DashboardFragment.this.getContext(), OkActivity.class);//创建Intent对象，启动check
                    startActivity(intent);
                }
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        initCourseType();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void f1() {
        HttpClient.searchMusic("", new HttpCallback<SearchMusic>() {
            @Override
            public void onSuccess(SearchMusic response) {
                if (response == null || response.getSong() == null || response.getSong().isEmpty()) {
                    onFail(null);
                    return;
                }
                List<SearchMusic.Song> s = response.getSong();
                for (int i = 0; i < 1; i++) {
                    Log.d("tag2", "onSuccess:  " + s.get(i).getSongname());
                }
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }

    public void refreshListView() {
        lv.setAdapter(mAdapter = new CommonAdapter<CourseType>(this.getContext(), mList, R.layout.dashboard_item_layout) {
            @Override
            public void convertView(ViewHolder holder, CourseType contactsBean) {
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