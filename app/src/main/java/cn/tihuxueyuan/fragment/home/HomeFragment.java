package cn.tihuxueyuan.fragment.home;

import static cn.tihuxueyuan.utils.Constant.appData;

import android.content.Intent;
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
    private android.widget.ListView lv;
    private android.widget.RelativeLayout activitymain;
    private AppData appData;
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

        this.lv =  root.findViewById(R.id.lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomeFragment.this.getActivity(), Music_Activity.class);//创建Intent对象，启动check
//                String musicUrl = mList.get(position).getMp3url() + "?fileName=" + mList.get(position).getMp3FileName();
                String musicUrl = SPUtils.getMp3Url( mList.get(position).getMp3FileName());

                intent.putExtra("music_url", musicUrl);
                intent.putExtra("current_position", position);
                intent.putExtra("is_new", true);
                String titleArr[] = mList.get(position).getFileName().split("\\.");
                intent.putExtra("title", titleArr[0]);
                startActivity(intent);

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
            }
        });
        appData = (AppData) HomeFragment.this.getActivity().getApplication();
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
        appData.UserCode = "7899000";  // 记录在本地文件里
        getFilelist();
        getConfig();
    }

    public void refreshListView() {
        lv.setAdapter(mAdapter = new CommonAdapter<CourseFileList.CourseFile>(this.getContext(), mList, R.layout.dashboard_item_layout) {
            @Override
            public void convertView(ViewHolder holder, CourseFileList.CourseFile contactsBean) {
                holder.set(R.id.name, contactsBean.getFileName());
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

                appData.mList = mList;
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
                if (response == null || response.getBaseUrl() == null ) {
                    onFail(null);
                    return;
                }
                Constant.appData.baseUrl = response.getBaseUrl();
                Constant.appData.mp3SourceRouter = response.getMp3SourceRouter();
//                refreshListView();
//                appData.mList = mList;
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