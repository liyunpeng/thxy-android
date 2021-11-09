package cn.tihuxueyuan.fragment.dashboard;

import static cn.tihuxueyuan.utils.Constant.LAST_TYPE_ID;
import static cn.tihuxueyuan.utils.Constant.TAG;
import static cn.tihuxueyuan.utils.Constant.TYPE_SELECTED;
import static cn.tihuxueyuan.utils.Constant.LAST_TAB_SELECTED_POSITION;
import static cn.tihuxueyuan.utils.Constant.appData;
import static cn.tihuxueyuan.utils.Constant.dbUtils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import cn.tihuxueyuan.R;
import cn.tihuxueyuan.activity.CourseListActivity;
import cn.tihuxueyuan.adapter.TabAdapterA;
import cn.tihuxueyuan.basic.ActivityManager;
import cn.tihuxueyuan.databinding.FragmentDashboardBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tihuxueyuan.http.HttpClient;
import cn.tihuxueyuan.http.HttpCallback;
import cn.tihuxueyuan.http.JsonPost;
import cn.tihuxueyuan.listenner.RecyclerViewClickListener2;
import cn.tihuxueyuan.model.Config;
import cn.tihuxueyuan.model.CourseList;
import cn.tihuxueyuan.model.CourseList.Course;
import cn.tihuxueyuan.model.CourseTypeList;
import cn.tihuxueyuan.adapter.GridRecycleAdapter;
import cn.tihuxueyuan.model.CourseTypeList.CourseType;
import cn.tihuxueyuan.utils.Constant;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.widget.TabView;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    private List<CourseType> mCourseTypeList = new ArrayList<>();
    private Map<Integer, CourseType> mCourseTypeMap = new HashMap<>();
    private List<Course> mCourseList = new ArrayList<>();
    private Map<Integer, Course> mCourseMap = new HashMap<>();
    private VerticalTabLayout mVerticalTabView;
    private RecyclerView mRecyclerView;
    private GridRecycleAdapter mRecycleAdapter;
    private int mTypeId;
    private int mCourseUpdateVersion;


    public void listToMap() {
        if (mCourseTypeMap != null) {
            mCourseTypeMap.clear();
        }
        mCourseTypeMap = new HashMap<>();
        for (CourseType c : mCourseTypeList) {
            int i = c.getId();
            mCourseTypeMap.put(i, c);
        }
    }

    public void courseListToCourseMap() {
        if (mCourseMap != null) {
            mCourseMap.clear();
        }
        mCourseMap = new HashMap<>();
        for (Course c : mCourseList) {
            int i = c.getId();
            mCourseMap.put(i, c);
        }
    }

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
            listToMap();
            mCourseUpdateVersion = mCourseTypeMap.get(mTypeId).getCourseUpdateVersion();
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
            courseListToCourseMap();
            mRecycleAdapter.setList(mCourseList);

            mRecycleAdapter.notifyDataSetChanged();

            Message msg = mDashHandler.obtainMessage();
            msg.what = 3;
            Bundle bundle = new Bundle();
            bundle.putInt("type_id", mTypeId);
            bundle.putInt("course_update_version", mCourseUpdateVersion);
            msg.setData(bundle);
            mDashHandler.sendMessage(msg);
            sendTypeSyncMessage();
        }
    }

    void sendTypeSyncMessage() {
        Message msg = mDashHandler.obtainMessage();
        msg.what = 4;
        Bundle bundle = new Bundle();
        Config c = dbUtils.getConfig(appData.serverConfigId);

        if ( c == null ) {
            bundle.putInt("course_type_update_version", -1);
        }else {
            bundle.putInt("course_type_update_version", c.getCourseTypeUpdateVersion());
        }

        msg.setData(bundle);
        mDashHandler.sendMessage(msg);
    }

    public Handler mDashHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    break;
                case 2:
                    if (mRecyclerView.getAdapter() == null) {
                        mRecyclerView.setAdapter(mRecycleAdapter);
                    }
                    mRecycleAdapter.setList(mCourseList);
                    courseListToCourseMap();
                    mRecycleAdapter.notifyDataSetChanged();
                    break;

                case 3:
                    Map map = new HashMap<>();
                    int typeId = msg.getData().getInt("type_id");
                    int courseUpdateVersion = msg.getData().getInt("course_update_version");
                    map.put("id", typeId);
                    map.put("course_update_version", courseUpdateVersion);
                    Log.d(TAG, " 发送课程列表刷新 消息");
                    getCourseListDelayed(map);
                    break;

                case 4:
                    int CourseTypeUpdateVersion = msg.getData().getInt("course_type_update_version");

                    if (CourseTypeUpdateVersion != appData.serviceCourseTypeUpdateVersion) {
                        httpGetCourseType();
                        Config c;
                        c = dbUtils.getConfig(appData.serverConfigId);
                        if (c != null) {
                            dbUtils.updateConfig(appData.serverConfigId, appData.serviceCourseTypeUpdateVersion);
                        } else {
                            c = new Config();
                            c.setId(appData.serverConfigId);
                            c.setCourseTypeUpdateVersion(appData.serviceCourseTypeUpdateVersion);
//                            c.setBaseUrl(appData.bas);
                            dbUtils.saveConfig(c);
                        }

                    }
                    break;
            }
        }
    };

    private void getCourseListDelayed(Map map) {
        Gson gson = new Gson();
        String param = gson.toJson(map);
        JsonPost.postHttpRequest("findCourseByTypeIdAndUpdateVersion", param, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(Constant.TAG, "findCourseByTypeIdAndUpdateVersion onFailure: 失败" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response == null) {
                        Log.d(TAG, "服务端返回null, 服务端出错");
                        return;
                    }

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                    StringBuffer stringBuffer = new StringBuffer("");
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                    String result = stringBuffer.toString();
                    Log.d(ContentValues.TAG, "result = " + result);

                    if (result.length() < 5) {
                        Log.d(TAG, "手机端 课程列表 updateversion与服务端课程列表 updateverson一致， 课程列表不需要更新");
                        return;
                    }
                    Gson gson = new Gson();
                    CourseList httpCourseList = gson.fromJson(result, CourseList.class);

                    List<CourseList.Course> courseList = httpCourseList.getCourseList();
                    int updateVersion = httpCourseList.getCourseUpdateVersion();
                    int saveFlag = 0;   // 新增课程
                    int updateFlag = 0;  // 课程名更新
                    for (CourseList.Course c : courseList) {
                        if (mCourseMap.get(c.getId()) == null) {
                            mCourseMap.put(c.getId(), c);
                            mCourseList.add(c);
                            saveFlag = 1;
                            dbUtils.saveCourse(c);
                            Log.d(TAG, " 有新增课程" + c.getTitle() + ", 添加到到当前课程列表，并保存到本地数据库");
                        } else if (!mCourseMap.get(c.getId()).getTitle().equalsIgnoreCase(c.getTitle())) {
                            updateFlag = 1;
                            dbUtils.updateCourse(c.getId(), c.getTitle());
                            Log.d(TAG, " 有课程" + c.getTitle() + "更新了名字, 同步更新到本地数据库");
                        }
                    }

                    if (updateFlag == 1) {
                        Log.d(TAG, " 重新读取本地数据库到mCourseList变量。 发送课程列表页面刷新的消息。更新本地数据库update_version与服务器一致。  ");
                        mCourseList = Constant.dbUtils.getCourseListByTypeId(mTypeId);
                        sendRecyleAdapterNotify();
                        dbUtils.updateCourseTypeUpdateVersion(mTypeId, updateVersion);
                    } else if (saveFlag == 1) {
                        Log.d(TAG, " 发送课程列表页面刷新的消息。 更新本地数据库update_version与服务器一致。  ");
                        sendRecyleAdapterNotify();
                        dbUtils.updateCourseTypeUpdateVersion(mTypeId, updateVersion);
                    }
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendRecyleAdapterNotify() {
        Message msg = mDashHandler.obtainMessage();
        msg.what = 2;
        Bundle bundle = new Bundle();
        msg.setData(bundle);
        mDashHandler.sendMessage(msg);
    }

    @Override
    public void onResume() {
        super.onResume();
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
                        Intent intent = new Intent(DashboardFragment.this.getActivity().getBaseContext(), CourseListActivity.class);
                        intent.putExtra("course_id", mCourseList.get(position).getId());
                        intent.putExtra("update_version", mCourseList.get(position).getUpdateVersion());
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
                mCourseUpdateVersion = mCourseTypeList.get(position).getCourseUpdateVersion();

                refreshRecycleView(mTypeId);
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

                // 安隐
                int count = Constant.dbUtils.getCourseListCountByTypeId(typeId);
                if (count <= mCourseList.size()) {
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

        Log.d(TAG, " onstop 垂直导航记录本次位置,  position=" + mVerticalTabView.getSelectedTabPosition());
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
                listToMap();
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