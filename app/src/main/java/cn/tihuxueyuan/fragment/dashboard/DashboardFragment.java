package cn.tihuxueyuan.fragment.dashboard;

import static cn.tihuxueyuan.utils.Constant.TAG;

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
    private TextView tvName;
    private VerticalTabLayout tabLayout;
    private RecyclerView recyclerView;
    private List<TestData> recyclelist;
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

        courseTypeList = Constant.dbUtils.getCourseTypes();
        if (courseList == null || courseTypeList.size() <=0) {
            httpGetCourseType();
        }else{
            refreshView();

            courseList = Constant.dbUtils.getCourseList(courseTypeList.get(0).getId());

            if (courseList == null || courseList.size() <= 0) {
                httpGetCourseByType(courseTypeList.get(0).getId());
            }else{
                Log.d(TAG, " 课程列表取sqlite3数据库");
                if (recyclerView.getAdapter() == null) {
                    recyclerView.setAdapter(recycleAdapter);
                }
                recycleAdapter.setList(courseList);
                recycleAdapter.notifyDataSetChanged();
            }


        }
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
        recycleAdapter = new GridRecycleAdapter(this.getActivity().getBaseContext(), courseList);
        recyclerView.setAdapter(recycleAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerViewClickListener2(DashboardFragment.this.getActivity().getBaseContext(), recyclerView,
                new RecyclerViewClickListener2.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent =new Intent(DashboardFragment.this.getActivity().getBaseContext(), CourseListActivity.class);//创建意图对象
                        intent.putExtra("course_id", courseList.get(position).getId());
                        Constant.appData.currentCourseImageFileName = courseList.get(position).getImgFileName();
                        intent.putExtra("title", courseList.get(position).getTitle());
                        Constant.appData.courseStorePath = courseList.get(position).getStorePath();
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Toast.makeText(DashboardFragment.this.getActivity().getBaseContext(), "Long Click " + courseList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    public void refreshView() {
        TabAdapterA adapterA = new TabAdapterA();
        adapterA.titles = courseTypeList;
        tabLayout.setTabAdapter(adapterA);
        tabLayout.setBackgroundResource(R.drawable.tab_background);
        tabLayout.addOnTabSelectedListener(new VerticalTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tab, int position) {
                Log.d(Constant.TAG, "onTabSelected:  id :" + courseTypeList.get(position).getId());
                httpGetCourseByType(courseTypeList.get(position).getId());
//                recycleAdapter.setList(recyclelist.get(position).getItemName());
            }

            @Override
            public void onTabReselected(TabView tab, int position) {

            }
        });
    }

    public void httpGetCourseByType(int typeId) {
        HttpClient.getCourseByTypeId(typeId, new HttpCallback<CourseList>() {
            @Override
            public void onSuccess(CourseList response) {
                if (response == null || response.getCourseList() == null || response.getCourseList().isEmpty()) {
                    Log.d(Constant.TAG, " 该类型没有课程");
                    recyclerView.setAdapter(null);
                    return;
                }
                if (recyclerView.getAdapter() == null) {
                    recyclerView.setAdapter(recycleAdapter);
                }
                courseList = response.getCourseList();
                recycleAdapter.setList(courseList);
                recycleAdapter.notifyDataSetChanged();

                int count = Constant.dbUtils.getCourseListCountByTypeId( typeId);
                if (count <= 0) {
                    Log.d(TAG, "类型保存到本地数据库");
                    Constant.dbUtils.saveCourseList(courseList);
                }
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }

    public void httpGetCourseType() {
        HttpClient.getCourseTypes("", new HttpCallback<CourseTypeList>() {
            @Override
            public void onSuccess(CourseTypeList response) {
                if (response == null || response.getCourseType() == null || response.getCourseType().isEmpty()) {
                    onFail(null);
                    return;
                }
                courseTypeList = response.getCourseType();
                refreshView();

                int count = Constant.dbUtils.getCourseTypeCount();
                if (count <= 0) {
                    Log.d(TAG, "类型保存到本地数据库");
                    Constant.dbUtils.saveCourseTypes(courseTypeList);
                }

                httpGetCourseByType(courseTypeList.get(0).getId());
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }
}