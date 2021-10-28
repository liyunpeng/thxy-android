package cn.tihuxueyuan.fragment.home;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tihuxueyuan.R;
import cn.tihuxueyuan.activity.MusicActivity;
import cn.tihuxueyuan.commonlistview.CommonAdapter;
import cn.tihuxueyuan.commonlistview.ViewHolder;
import cn.tihuxueyuan.databinding.FragmentHomeBinding;
import cn.tihuxueyuan.globaldata.AppData;
import cn.tihuxueyuan.http.HttpCallback;
import cn.tihuxueyuan.http.HttpClient;
import cn.tihuxueyuan.http.JsonPost;
import cn.tihuxueyuan.http.CustomResponse;
import cn.tihuxueyuan.model.CourseFileList;
import cn.tihuxueyuan.utils.ComparatorValues;
import cn.tihuxueyuan.utils.Constant;
import cn.tihuxueyuan.utils.SPUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private CommonAdapter mAdapter;
    private android.widget.ListView listView;
    private android.widget.RelativeLayout activitymain;
    private AppData appData;
    private List<CourseFileList.CourseFile> mList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.listView = root.findViewById(R.id.lv);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomeFragment.this.getActivity(), MusicActivity.class);
                String musicUrl = SPUtils.getImgOrMp3Url(mList.get(position).getCourseId(), mList.get(position).getMp3FileName());

//                HttpClient.getCourseById(String.valueOf(mList.get(position).getCourseId()), new HttpCallback<CourseList.Course>() {
//                    @Override
//                    public void onSuccess(CourseList.Course response) {
////                        if (response == null || response.getCourseList() == null || response.getCourseList().isEmpty()) {
////                            Log.d(Constant.TAG, " 该类型没有课程");
////                            recyclerView.setAdapter(null);
////                            return;
////                        }
////                        if (recyclerView.getAdapter() == null) {
////                            recyclerView.setAdapter(recycleAdapter);
////                        }
////                        courseList = response.getCourseList();
////                        recycleAdapter.setList(courseList);
////                        recycleAdapter.notifyDataSetChanged();
//                        intent.putExtra("music_url", musicUrl);
//                        intent.putExtra("current_position", position);
//                        intent.putExtra("is_new", true);
//
//                        appData.currentCourseFileId = mList.get(position).getId();
//                        appData.currentPostion = position;
//                        appData.currentCourseId = mList.get(position).getCourseId();
//                        appData.currentCourseImageFileName = response.getImgFileName();
//                        String titleArr[] = mList.get(position).getFileName().split("\\.");
//                        intent.putExtra("title", titleArr[0]);
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onFail(Exception e) {
//
//                    }
//                });

                Map map = new HashMap<>();
                map.put("id", mList.get(position).getCourseId());
                Gson gson = new Gson();
                String param = gson.toJson(map);
                JsonPost.postHttpRequest("getCourseById", param, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(Constant.TAG, "onFailure: 失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                            StringBuffer stringBuffer = new StringBuffer("");
                            // 获取本系统的行分割线
                            String NL = System.getProperty("line.separator");
                            // 把http响应的输入流数据按行读取到StringBuffer中
                            String line = "";
                            while ((line = bufferedReader.readLine()) != null) {
                                stringBuffer.append(line + NL);
                            }
                            String result = stringBuffer.toString();
                            Log.d(TAG, "result = " + result);

                            Gson gson = new Gson();
                            CustomResponse course = gson.fromJson(result, CustomResponse.class);

                            intent.putExtra("music_url", musicUrl);
                            intent.putExtra("current_position", position);
                            intent.putExtra(Constant.MUSIC_ACTIVITY_MODE_NAME, Constant.LIST_MODE_VALUE);

                            appData.playingCourseFileId = mList.get(position).getId();
                            appData.playingCourseId = mList.get(position).getCourseId();

                            appData.currentCourseImageFileName = course.getData().getImgFileName();

                            String titleArr[] = mList.get(position).getFileName().split("\\.");
                            intent.putExtra("title", titleArr[0]);


                            Constant.order = true;
                            Collections.sort(mList, new ComparatorValues());

                            appData.playingCourseFileList = mList;
                            appData.playingCourseFileId = mList.get(position).getId();
                            SPUtils.listToMap();
                            startActivity(intent);
                            Log.d(Constant.TAG, "onResponse: " + result);

                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
//                        ResponseBody r = response.body();
//                        r.string();

                    }
                });

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
    public void onStop() {
        super.onStop();
        appData.lastCourseId = -1000;  // 表示在最新列表播放
    }


    @Override
    public void onResume() {
        super.onResume();
        appData.UserCode = "uc";  // 记录在本地文件里
        getFilelist();
//        getConfig();
    }

    public void refreshListView() {
        listView.setAdapter(mAdapter = new CommonAdapter<CourseFileList.CourseFile>(this.getContext(), mList, R.layout.dashboard_item_layout) {
            @Override
            public void convertView(ViewHolder holder, CourseFileList.CourseFile courseFile) {
                holder.set(R.id.name, SPUtils.getTitleFromName(courseFile.getFileName()), Color.parseColor("#000000"));
                holder.getView(R.id.number).setVisibility(View.GONE);
                String duration = courseFile.getDuration();
                holder.set(R.id.duration, "时长: " + duration, Color.parseColor("#000000"));
                holder.getView(R.id.percent).setVisibility(View.GONE);
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