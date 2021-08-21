package cn.tihuxueyuan.fragment.dashboard;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import cn.tihuxueyuan.verticaltabrecycler.GridRecycleAdapter;
import cn.tihuxueyuan.verticaltabrecycler.MainActivity;
import cn.tihuxueyuan.model.CourseTypeList.CourseType;
import cn.tihuxueyuan.verticaltabrecycler.TestData;
import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.widget.QTabView;
import q.rorbin.verticaltablayout.widget.TabView;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;


    private TextView tvName;
    private VerticalTabLayout tabLayout;
    private RecyclerView recyclerView;
    List<TestData> list;

    private GridRecycleAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        this.getActivity().setContentView(R.layout.activity_simple);
        tvName = root.findViewById(R.id.tv_name);
        tabLayout = root.findViewById(R.id.tab_layout);
        recyclerView = root.findViewById(R.id.recycler_view);
        initData();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
//        initCourseType();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_simple);
//        tvName = findViewById(R.id.tv_name);
//        tabLayout = findViewById(R.id.tab_layout);
//        recyclerView = findViewById(R.id.recycler_view);
//        initData();
//    }

    private void initData(){
        list = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            TestData testData = new TestData();
            List<String> itemList = new ArrayList<>();

            for (int j = 0; j < (i%2 == 0 ? 6 : 10); j++) {
                itemList.add("二级类目" + i + "-" + j);
            }
            testData.setItemName(itemList);
            testData.setName("类目"+ i);
            list.add(testData);
            tabLayout.addTab(new QTabView(this.getActivity().getBaseContext()).setTitle(
                    new QTabView.TabTitle.Builder().setContent(testData.getName()).build()));
        }

        GridLayoutManager glm = new GridLayoutManager(this.getActivity().getBaseContext(),3);
        recyclerView.setLayoutManager(glm);
        adapter = new GridRecycleAdapter(this.getActivity().getBaseContext(),list.get(0).getItemName());
        tvName.setText(list.get(0).getName());
        recyclerView.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new VerticalTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tab, int position) {
                adapter.setList(list.get(position).getItemName());
                tvName.setText(list.get(position).getName());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTabReselected(TabView tab, int position) {

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