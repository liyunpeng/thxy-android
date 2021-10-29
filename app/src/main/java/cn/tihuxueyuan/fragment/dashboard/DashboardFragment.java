package cn.tihuxueyuan.fragment.dashboard;

import static cn.tihuxueyuan.utils.Constant.LAST_TYPE_ID;
import static cn.tihuxueyuan.utils.Constant.TAG;
import static cn.tihuxueyuan.utils.Constant.TYPE_SELECTED;
import static cn.tihuxueyuan.utils.Constant.LAST_TAB_SELECTED_POSITION;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.widget.TabView;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    private List<CourseType> mCourseTypeList = new ArrayList<>();
    private List<Course> mCourseList = new ArrayList<>();
    private VerticalTabLayout mVerticalTabView;
    private RecyclerView mRecyclerView;
    private GridRecycleAdapter mRecycleAdapter;
    private int mTypeId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mVerticalTabView = root.findViewById(R.id.tab_layout);
        mRecyclerView = root.findViewById(R.id.recycler_view);

        initRecycleView();

        mCourseTypeList = Constant.dbUtils.getCourseTypes();
        if (mCourseList == null || mCourseTypeList.size() <= 0) {
            httpGetCourseType();
        } else {
            Log.d(TAG, " 课程类型列表从数据库获取成功 ");
            refreshView();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(TYPE_SELECTED, Context.MODE_PRIVATE);
            int lastTypeId = sharedPreferences.getInt(LAST_TYPE_ID, -1);
            int lastTabSelectedPosition = sharedPreferences.getInt(LAST_TAB_SELECTED_POSITION, -1);
            if (lastTypeId > 0) {
                Log.d(TAG, "从sharedPreferences获取上次typeId.  lastTypeId=" + lastTypeId);
                mTypeId = lastTypeId;
            } else {
                Log.d(TAG, "第一次用，从mCourseTypeList获取垂直导航菜单栏第一个菜单项的typeId, typeId=" + mCourseTypeList.get(0).getId());
                mTypeId = mCourseTypeList.get(0).getId();
            }
            refreshRecycleView(mTypeId);
            mVerticalTabView.setTabSelected(lastTabSelectedPosition);
        }
        return root;
    }

    private void refreshRecycleView(int typeId) {
        mCourseList = Constant.dbUtils.getCourseListByTypeId(typeId);

        if (mCourseList == null || mCourseList.size() <= 0) {
            Log.d(TAG, " 课程类型 " + typeId + ", recycleView走网络获取 ");
            httpGetCourseByType(typeId);
        } else {
            Log.d(TAG, " 课程类型 " + typeId + ", recycleView走sqlite3数据库获取");
            if (mRecyclerView.getAdapter() == null) {
                mRecyclerView.setAdapter(mRecycleAdapter);
            }
            mRecycleAdapter.setList(mCourseList);
            mRecycleAdapter.notifyDataSetChanged();
        }
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
        GridLayoutManager glm = new GridLayoutManager(this.getActivity().getBaseContext(), 2);
        mRecyclerView.setLayoutManager(glm);
        mRecycleAdapter = new GridRecycleAdapter(this.getActivity().getBaseContext(), mCourseList);
        mRecyclerView.setAdapter(mRecycleAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewClickListener2(DashboardFragment.this.getActivity().getBaseContext(), mRecyclerView,
                new RecyclerViewClickListener2.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(DashboardFragment.this.getActivity().getBaseContext(), CourseListActivity.class);//创建意图对象
                        intent.putExtra("course_id", mCourseList.get(position).getId());
                        intent.putExtra("title", mCourseList.get(position).getTitle());
                        intent.putExtra("introduction", mCourseList.get(position).getIntroduction());
                        Constant.appData.currentCourseImageFileName = mCourseList.get(position).getImgFileName();
                        Constant.appData.courseStorePath = mCourseList.get(position).getStorePath();
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Toast.makeText(DashboardFragment.this.getActivity().getBaseContext(), "Long Click " + mCourseList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    public void refreshView() {
        TabAdapterA adapterA = new TabAdapterA();
        adapterA.titles = mCourseTypeList;
        mVerticalTabView.setTabAdapter(adapterA);
        mVerticalTabView.setBackgroundResource(R.drawable.tab_background);
        mVerticalTabView.addOnTabSelectedListener(new VerticalTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tab, int position) {
                Log.d(Constant.TAG, "onTabSelected:  id :" + mCourseTypeList.get(position).getId());
                mTypeId = mCourseTypeList.get(position).getId();
                refreshRecycleView(mTypeId);
//                recycleAdapter.setList(recyclelist.get(position).getItemName());

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(TYPE_SELECTED, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(LAST_TYPE_ID, mTypeId);
                editor.putInt(LAST_TAB_SELECTED_POSITION, mVerticalTabView.getSelectedTabPosition());
                editor.commit();
            }

            @Override
            public void onTabReselected(TabView tab, int position) {

            }
        });

//        mVerticalTabView.setTabSelected(3);

    }

    public void httpGetCourseByType(int typeId) {
        HttpClient.getCourseByTypeId(typeId, new HttpCallback<CourseList>() {
            @Override
            public void onSuccess(CourseList response) {
                if (response == null || response.getCourseList() == null || response.getCourseList().isEmpty()) {
                    Log.d(Constant.TAG, " 该类型没有课程");
                    mRecyclerView.setAdapter(null);
                    return;
                }
                if (mRecyclerView.getAdapter() == null) {
                    mRecyclerView.setAdapter(mRecycleAdapter);
                }
                mCourseList = response.getCourseList();
                mRecycleAdapter.setList(mCourseList);
                mRecycleAdapter.notifyDataSetChanged();

                int count = Constant.dbUtils.getCourseListCountByTypeId(typeId);
                if (count <= 0) {
                    Log.d(TAG, "类型保存到本地数据库");
                    Constant.dbUtils.saveCourseList(mCourseList);
                }
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, " onstop 垂直导航记录本次位置,  position=" +  mVerticalTabView.getSelectedTabPosition());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(TYPE_SELECTED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LAST_TYPE_ID, mTypeId);
        editor.putInt(LAST_TAB_SELECTED_POSITION, mVerticalTabView.getSelectedTabPosition());
        editor.commit();
    }

    public void httpGetCourseType() {
        HttpClient.getCourseTypes("", new HttpCallback<CourseTypeList>() {
            @Override
            public void onSuccess(CourseTypeList response) {
                if (response == null || response.getCourseType() == null || response.getCourseType().isEmpty()) {
                    onFail(null);
                    return;
                }
                mCourseTypeList = response.getCourseType();
                refreshView();

                int count = Constant.dbUtils.getCourseTypeCount();
                if (count <= 0) {
                    Log.d(TAG, "类型保存到本地数据库");
                    Constant.dbUtils.saveCourseTypes(mCourseTypeList);
                }

                httpGetCourseByType(mCourseTypeList.get(0).getId());
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }
}