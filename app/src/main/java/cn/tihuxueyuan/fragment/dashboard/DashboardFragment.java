package cn.tihuxueyuan.fragment.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.tihuxueyuan.R;
import cn.tihuxueyuan.activity.CourseListActivity;
import cn.tihuxueyuan.activity.Music_Activity;
import cn.tihuxueyuan.activity.OkhttpClientActivity;
import cn.tihuxueyuan.adapter.TabAdapterA;
import cn.tihuxueyuan.basic.ActivityManager;
import cn.tihuxueyuan.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.List;

import cn.tihuxueyuan.http.HttpClient;
import cn.tihuxueyuan.http.HttpCallback;
import cn.tihuxueyuan.listenner.RecyclerViewClickListener2;
import cn.tihuxueyuan.model.CourseList;
import cn.tihuxueyuan.model.CourseList.Course;
import cn.tihuxueyuan.model.SearchMusic;
import cn.tihuxueyuan.model.CourseTypeList;
import cn.tihuxueyuan.adapter.GridRecycleAdapter;
import cn.tihuxueyuan.model.CourseTypeList.CourseType;
import cn.tihuxueyuan.utils.Constant;
import cn.tihuxueyuan.verticaltabrecycler.TestData;
import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.widget.TabView;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;

    private List<CourseType> courseTypeList = new ArrayList<>();
    private List<Course> courseList = new ArrayList<>();
    //    private List<CourseType> courseTypeList = new ArrayList<>();
    private TextView tvName;
    private VerticalTabLayout tabLayout;
    private RecyclerView recyclerView;
    List<TestData> recyclelist;

    private GridRecycleAdapter recycleAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tvName = root.findViewById(R.id.tv_name);
        tabLayout = root.findViewById(R.id.tab_layout);
        recyclerView = root.findViewById(R.id.recycler_view);

        initRecycleView();
        initCourseType();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
//        initCourseType();
        ActivityManager.setCurrentActivity(DashboardFragment.this.getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initRecycleView() {
        recyclelist = new ArrayList<>();

        GridLayoutManager glm = new GridLayoutManager(this.getActivity().getBaseContext(), 2);
        recyclerView.setLayoutManager(glm);
//        tvName.setText(recyclelist.get(0).getName());
        recycleAdapter = new GridRecycleAdapter(this.getActivity().getBaseContext(), courseList);
        recyclerView.setAdapter(recycleAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerViewClickListener2(DashboardFragment.this.getActivity().getBaseContext(), recyclerView,
                new RecyclerViewClickListener2.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
//                        Toast.makeText(DashboardFragment.this.getActivity().getBaseContext(), "Click " + courseList.get(position).getTitle(), Toast.LENGTH_SHORT).show();

                        Intent intent =new Intent(DashboardFragment.this.getActivity().getBaseContext(), CourseListActivity.class);//创建意图对象
                        intent.putExtra("course_id", courseList.get(position).getId());
                        intent.putExtra("title", courseList.get(position).getTitle());
                        Constant.appData.courseStorePath = courseList.get(position).getStorePath();
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Toast.makeText(DashboardFragment.this.getActivity().getBaseContext(), "Long Click " + courseList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                    }
                }));

//        LinearLayoutManager glm = new LinearLayoutManager(this.getActivity().getBaseContext());
//        recyclerView.setLayoutManager(glm);
//        adapter = new GridRecycleAdapter(this.getActivity().getBaseContext(),list.get(0).getItemName());
//        tvName.setText(list.get(0).getName());
//        recyclerView.setAdapter(adapter);


    }

    public void refreshView() {

        TabAdapterA ta = new TabAdapterA();
        ta.titles = courseTypeList;
        tabLayout.setTabAdapter(ta);

        tabLayout.addOnTabSelectedListener(new VerticalTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tab, int position) {
                Log.d("tag1", "onTabSelected:  id :" + courseTypeList.get(position).getId());
                getCourseByType(courseTypeList.get(position).getId());
//                recycleAdapter.setList(recyclelist.get(position).getItemName());
            }

            @Override
            public void onTabReselected(TabView tab, int position) {

            }
        });
    }

    public void getCourseByType(String typeId) {
        HttpClient.getCourseByTypeId(typeId, new HttpCallback<CourseList>() {
            @Override
            public void onSuccess(CourseList response) {
                if (response == null || response.getCourseList() == null || response.getCourseList().isEmpty()) {
                    onFail(null);
                    return;
                }
                courseList = response.getCourseList();
//                tvName.setText(recyclelist.get(typeId).getName());
                recycleAdapter.setList(courseList);
                recycleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }

    public void initCourseType() {
        HttpClient.getCourseTypes("", new HttpCallback<CourseTypeList>() {
            @Override
            public void onSuccess(CourseTypeList response) {
                if (response == null || response.getCourseType() == null || response.getCourseType().isEmpty()) {
                    onFail(null);
                    return;
                }
                courseTypeList = response.getCourseType();
                refreshView();
                getCourseByType(courseTypeList.get(0).getId());
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }
}