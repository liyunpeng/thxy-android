package cn.tihuxueyuan.fragment.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import cn.tihuxueyuan.R;
import cn.tihuxueyuan.activity.Music_Activity;
import cn.tihuxueyuan.commonlistview.CommonAdapter;
import cn.tihuxueyuan.commonlistview.ViewHolder;
import cn.tihuxueyuan.databinding.FragmentHomeBinding;
import cn.tihuxueyuan.globaldata.AppData;
import cn.tihuxueyuan.http.HttpCallback;
import cn.tihuxueyuan.http.HttpClient;
import cn.tihuxueyuan.model.Config;
import cn.tihuxueyuan.model.CourseFileList;
import cn.tihuxueyuan.utils.Constant;
import cn.tihuxueyuan.utils.SPUtils;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private CommonAdapter mAdapter;
    private android.widget.ListView listView;
    private android.widget.RelativeLayout activitymain;
    private AppData appData;
    private List<CourseFileList.CourseFile> mList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        this.listView = root.findViewById(R.id.lv);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomeFragment.this.getActivity(), Music_Activity.class);//创建Intent对象，启动check
                String musicUrl = SPUtils.getImgOrMp3Url(mList.get(position).getCourseId(), mList.get(position).getMp3FileName());
                intent.putExtra("music_url", musicUrl);
                intent.putExtra("current_position", position);
                intent.putExtra("is_new", true);

                appData.currentCourseFileId = mList.get(position).getId();
                appData.currentPostion = position;

                String titleArr[] = mList.get(position).getFileName().split("\\.");
                intent.putExtra("title", titleArr[0]);
                startActivity(intent);
            }
        });
        appData = (AppData) HomeFragment.this.getActivity().getApplication();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        appData.UserCode = "7899000";  // 记录在本地文件里
        getFilelist();
        getConfig();
    }

    public void refreshListView() {
        listView.setAdapter(mAdapter = new CommonAdapter<CourseFileList.CourseFile>(this.getContext(), mList, R.layout.dashboard_item_layout) {
            @Override
            public void convertView(ViewHolder holder, CourseFileList.CourseFile courseFile) {
                holder.set(R.id.name, SPUtils.getTitleFromName( courseFile.getFileName()), Color.parseColor("#000000"));
                holder.getView(R.id.number).setVisibility(View.GONE);
                String duration = courseFile.getDuration();
                holder.set(R.id.duration, "时长" + duration,   Color.parseColor("#000000"));
                holder.getView(R.id.percent).setVisibility(View.GONE);
                holder.getView(R.id.duration).setVisibility(View.GONE);
            }
        });

//        mAdapter.notifyDataSetChanged();
    }

    public void getFilelist() {
        HttpClient.getLatest("", new HttpCallback<CourseFileList>() {
            @Override
            public void onSuccess(CourseFileList response) {
                if (response == null || response.getCourseFileList() == null || response.getCourseFileList().isEmpty()) {
                    onFail(null);
                    return;
                }
                mList = response.getCourseFileList();
                refreshListView();

                appData.courseFileList = mList;

                SPUtils.listToMap();
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }

    public void getConfig() {
        HttpClient.getConfig("", new HttpCallback<Config>() {
            @Override
            public void onSuccess(Config response) {
                if (response == null || response.getBaseUrl() == null) {
                    onFail(null);
                    return;
                }
                Constant.appData.baseUrl = response.getBaseUrl();
                Constant.appData.mp3SourceRouter = response.getMp3SourceRouter();
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}