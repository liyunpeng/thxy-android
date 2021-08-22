package cn.tihuxueyuan.fragment.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.tihuxueyuan.R;
import cn.tihuxueyuan.adapter.TabAdapterA;
import cn.tihuxueyuan.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.List;

import cn.tihuxueyuan.http.HttpClient;
import cn.tihuxueyuan.http.HttpCallback;
import cn.tihuxueyuan.model.CourseList;
import cn.tihuxueyuan.model.CourseList.Course;
import cn.tihuxueyuan.model.SearchMusic;
import cn.tihuxueyuan.model.CourseTypeList;
import cn.tihuxueyuan.adapter.GridRecycleAdapter;
import cn.tihuxueyuan.model.CourseTypeList.CourseType;
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


//        TabAdapterA ac = new TabAdapterA();
//        tabLayout.setTabAdapter(ac);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void initRecycleView(){
        recyclelist = new ArrayList<>();
//        for (int i = 0; i < 13; i++) {
//            TestData testData = new TestData();
//            List<String> itecourseTypeList = new ArrayList<>();
//
//            for (int j = 0; j < (i%2 == 0 ? 6 : 10); j++) {
//                itecourseTypeList.add("二级类目" + i + "-" + j);
//            }
//            testData.setItemName(itecourseTypeList);
//            testData.setName("类目12345 : "+ i);
//            recyclelist.add(testData);
//            tabLayout.addTab(new QTabView(this.getActivity().getBaseContext()).setTitle(
//                    new QTabView.TabTitle.Builder().setContent(testData.getName()).build()));
//        }

        GridLayoutManager glm = new GridLayoutManager(this.getActivity().getBaseContext(),2);
        recyclerView.setLayoutManager(glm);
//        tvName.setText(recyclelist.get(0).getName());
        recycleAdapter = new GridRecycleAdapter(this.getActivity().getBaseContext(), courseList);
        recyclerView.setAdapter(recycleAdapter);


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
                Log.d("tag1", "onTabSelected:  id :" +courseTypeList.get(position).getId());
//                getCourseByType(courseTypeList.get(position).getId());
                getCourseByType(courseTypeList.get(position).getId());
//                recycleAdapter.setList(recyclelist.get(position).getItemName());

            }

            @Override
            public void onTabReselected(TabView tab, int position) {

            }
        });
    }

    public void getCourseByType( String typeId ) {
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


}