package cn.tihuxueyuan.activity;

import static cn.tihuxueyuan.globaldata.AppData.notificationBitMap;
import static cn.tihuxueyuan.utils.Constant.TAG;
import static cn.tihuxueyuan.utils.Constant.musicControl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import cn.tihuxueyuan.basic.ActivityManager;
import cn.tihuxueyuan.basic.BaseActivity;
import cn.tihuxueyuan.commonlistview.CommonAdapter;
import cn.tihuxueyuan.commonlistview.ViewHolder;
import cn.tihuxueyuan.globaldata.AppData;
import cn.tihuxueyuan.http.HttpCallback;
import cn.tihuxueyuan.http.HttpClient;
import cn.tihuxueyuan.livedata.LiveDataBus;
import cn.tihuxueyuan.model.CourseFileList;
import cn.tihuxueyuan.model.CourseFileList.CourseFile;
import cn.tihuxueyuan.R;
import cn.tihuxueyuan.utils.ComparatorValues;
import cn.tihuxueyuan.utils.Constant;
import cn.tihuxueyuan.utils.SPUtils;
import okhttp3.Call;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseListActivity extends BaseActivity {
    private android.widget.ListView courseListView;
    private int currentCouseId;
    private String title;
    int createFlag = 0;
    private CommonAdapter<CourseFile> mAdapter;
    public List<CourseFileList.CourseFile> mList = new ArrayList<>();
    private AppData appData;
    TextView lastPlayTextView;
    TextView reverseTextView;
    TextView titleView;
    ImageView imageView;
    private LiveDataBus.BusMutableLiveData<String> courseListActivityLiveData;

    boolean order = true;



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_list_activity);

        Log.d(TAG, "CourseListActivity 创建， 调用oncreate");
        currentCouseId = getIntent().getIntExtra("course_id", 0);
        title = getIntent().getStringExtra("title");

        appData = (AppData) getApplication();
        appData.currentCourseId = this.currentCouseId;

        this.titleView = findViewById(R.id.course_title);
        this.imageView = findViewById(R.id.course_image);

        titleView.setText(title);
        this.courseListView = findViewById(R.id.courseList);
        this.lastPlayTextView = findViewById(R.id.last_play);
        reverseTextView = findViewById(R.id.reverse);

        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String musicUrl = SPUtils.getImgOrMp3Url(mList.get(position).getCourseId(), mList.get(position).getMp3FileName());
                Intent intent = new Intent(getApplicationContext(), Music_Activity.class);
                intent.putExtra("music_url", musicUrl);
                intent.putExtra("current_position", position);
                intent.putExtra("is_new", true);
                intent.putExtra("title", SPUtils.getTitleFromName(mList.get(position).getFileName()));
                startActivity(intent);
            }
        });


        ActivityManager.setCurrentActivity(CourseListActivity.this);
        httpGetCourseFiles();
        Log.d("tag2", "onCreate: currentCouseId: " + currentCouseId);

        courseListActivityObserver();
        reverseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order == true) {
                    order = false;
                    Constant.order = false;
                    Collections.sort(mList, new ComparatorValues());
                    reverseTextView.setText(" 倒序");
                } else {
                    order = true;
                    Constant.order = true;
                    Collections.sort(mList, new ComparatorValues());
                    reverseTextView.setText(" 正序");
                }

                mAdapter.notifyDataSetChanged();
            }
        });
        SPUtils.httpGetCourseImage(imageView);
    }

    @Override
    protected void onRestart() {
        Log.e("====", "onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.e("====", "onStart()");
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        appData.lastCourseId = Integer.parseInt(currentCouseId);
        appData.lastCourseId = currentCouseId;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(Constant.TAG, "CourseListActivity   onActivityResult ");

//        if (requestCode == 0) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (!Settings.canDrawOverlays(this)) {
//                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
//                    startService(new Intent(CourseListActivity.this, FloatingImageDisplayService.class));
//                }
//            }
//        } else if (requestCode == 1) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (!Settings.canDrawOverlays(this)) {
//                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
//                    startService(new Intent(CourseListActivity.this, FloatingImageDisplayService.class));
//                }
//            }
//        } else if (requestCode == 2) {
//            if (!Settings.canDrawOverlays(this)) {
//                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
//                startService(new Intent(CourseListActivity.this, FloatingImageDisplayService.class));
//            }
//        }
    }


    private void freshLastPlay() {
//        if ( appData.lastCourseId == -1  && lastListenedCourseFileId >= 0 && appData.lastCourseId != Integer.parseInt(currentCouseId)) {
//        if (lastListenedCourseFileId >= 0 && appData.lastCourseId != Integer.parseInt(currentCouseId)) {
        if (lastListenedCourseFileId >= 0 && appData.lastCourseId != currentCouseId) {
            Log.d(TAG, " freshLastPlay 准备设置为 可见 ");
            if (musicControl != null) {
                if (currentCouseId != musicControl.getCurrentCourseId()) {
                    Log.d(TAG, " freshLastPlay 当前课程列表的courseId  和 当前正在播放的courseId 不同， 设置为可见");
                    String lastTitle = SPUtils.getTitleFromName(appData.courseFileMap.get(lastListenedCourseFileId).getFileName());
                    lastPlayTextView.setText("上次播放: " + lastTitle);
                    lastPlayTextView.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, " freshLastPlay 当前课程列表的courseId  和 当前正在播放的courseId 相同， 设置为不可见");
                    lastPlayTextView.setVisibility(View.INVISIBLE);
                }
            } else {
                if (appData.courseFileMap != null && appData.courseFileMap.get(lastListenedCourseFileId) != null) {
                    Log.d(TAG, " appData.courseFileMap 有数据， 设置为可见");
                    String lastTitle = SPUtils.getTitleFromName(appData.courseFileMap.get(lastListenedCourseFileId).getFileName());
                    lastPlayTextView.setText("上次播放: " + lastTitle);
                    lastPlayTextView.setVisibility(View.VISIBLE);
                }
            }

        } else {
            Log.d(TAG, " freshLastPlay 设置为 不可见 " + " appData.lastCourseId  = " + appData.lastCourseId +
                    ", lastListenedCourseFileId=" + lastListenedCourseFileId +
                    ", appData.lastCourseId= " + appData.lastCourseId +
                    ", currentCouseId=" + currentCouseId);

            lastPlayTextView.setVisibility(View.INVISIBLE);
        }
    }

    /*

    点回退键时：
    D/tag2: onCreate: param: 1
E/====: onStart()
E/====: onResume()
D/tag1: parseNetworkResponse:

再点应用时：
E/====: onRestart()
E/====: onStart()
E/====: onResume()
D/tag1: parseNetworkResponse:
     */
    @Override
    public void onResume() {
        super.onResume();
        if (createFlag == 1 && mList != null && mList.size() > appData.currentPostion) {
            // 从悬浮窗进入音乐界面， 再回到列表界面时，如果是其他课程列表，不刷新
            if (currentCouseId == Constant.musicControl.getCurrentCourseId()) {
                mAdapter.notifyDataSetChanged();
            }
            freshLastPlay();
        }

        if (order == true) {
            reverseTextView.setText("正序");
        } else {
            reverseTextView.setText("倒序");
        }

    }

    private void courseListActivityObserver() {
        courseListActivityLiveData = LiveDataBus.getInstance().with(Constant.CourseListLiveDataObserverTag, String.class);
        courseListActivityLiveData.observe(CourseListActivity.this, true, new Observer<String>() {
            @Override
            public void onChanged(String value) {
                Log.d(TAG, " CourseListActivity 观察者监控到消息 = " + value);

                if (mList != null && mList.size() > Constant.appData.currentPostion) {
                    mList.get(Constant.appData.currentPostion).listenedPercent = Integer.parseInt(value);
                    mAdapter.notifyDataSetChanged();
                }

//                switch (state) {
//                    default:
//                        break;
//                }
            }
        });
    }

    public void refreshListView() {
        freshLastPlay();

        lastPlayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseFile courseFile = appData.courseFileMap.get(lastListenedCourseFileId);
                // android手机里播放音乐 按在listView中的位置找到音乐，而不是按fileId去找音乐，以便与播放上一首， 下一首一致, 以及自动播放下一首处理一致
                appData.currentPostion = SPUtils.findPositionByFileId(lastListenedCourseFileId);
                if (courseFile != null) {
                    Intent intent = new Intent(getApplicationContext(), Music_Activity.class);
                    String musicUrl = SPUtils.getImgOrMp3Url(courseFile.getCourseId(), courseFile.getFileName());
                    intent.putExtra("music_url", musicUrl);
                    intent.putExtra("current_position", appData.currentPostion);
                    intent.putExtra("is_new", true);
                    intent.putExtra("title", SPUtils.getTitleFromName(courseFile.getFileName()));
                    startActivity(intent);
                }
            }
        });

        mAdapter = new CommonAdapter<CourseFile>(getApplicationContext(), mList, R.layout.dashboard_item_layout) {
            @Override
            public void convertView(ViewHolder holder, CourseFile courseFile) {
                int percent = courseFile.getListenedPercent();
                String duration = courseFile.getDuration();
                int color = Color.parseColor("#000000");
                if (currentCouseId == appData.currentMusicCourseId &&
                        appData.currentPostion >= 0 && Constant.appData.currentPostion < Constant.appData.courseFileList.size()
                ) {
                    if (Constant.appData.courseFileMap.get(Constant.appData.currentCourseFileId) != null) {
                        if (Constant.appData.courseFileMap.get(Constant.appData.currentCourseFileId).getId() == courseFile.getId()) {
                            color = Color.parseColor("#FF0000");
                        }
                    } else {
                        color = Color.parseColor("#000000");
                    }

                } else {
                    if (percent > 0) {
                        color = Color.parseColor("#777777");
                    } else {
                        color = Color.parseColor("#000000");
                    }
                }

                if (percent > 0) {
                    holder.set(R.id.name, SPUtils.getTitleFromName(courseFile.getFileName()), color);
                    holder.set(R.id.number, String.valueOf(courseFile.getNumber()), color);

                    if (percent == 100) {
                        holder.set(R.id.percent, "已听完", color);
                    } else {
                        holder.set(R.id.percent, "已听" + percent + "%", color);
                    }
                    holder.set(R.id.duration, "时长" + duration, color);
                    holder.getView(R.id.percent).setVisibility(View.VISIBLE);
                } else {
                    holder.set(R.id.name, SPUtils.getTitleFromName(courseFile.getFileName()), color);
                    holder.set(R.id.number, String.valueOf(courseFile.getNumber()), color);
                    holder.set(R.id.percent, "", color);
                    holder.set(R.id.duration, "时长" + duration, color);
                    holder.getView(R.id.percent).setVisibility(View.INVISIBLE);
                }

                holder.getView(R.id.duration).setVisibility(View.GONE);
            }
        };
//        courseListView.set
        courseListView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
    }

    int lastListenedCourseFileId;

    public void httpGetCourseFiles() {
        HttpClient.getCourseFilesByCourseId(currentCouseId, new HttpCallback<CourseFileList>() {
            @Override
            public void onSuccess(CourseFileList response) {
                if (response == null || response.getCourseFileList() == null || response.getCourseFileList().isEmpty()) {
                    onFail(null);
                    return;
                }
                mList = response.getCourseFileList();
                appData.courseFileList = mList;
                SPUtils.listToMap();
                lastListenedCourseFileId = response.getLastListenedCourseFileId();
                Log.d(Constant.TAG, " 上次播放 lastListenedCourseFileId :" + lastListenedCourseFileId);
                refreshListView();

//                Constant.dbUtils.saveCourseFiles();

                createFlag = 1;
                order = true;
            }

            @Override
            public void onFail(Exception e) {
                Log.d(Constant.TAG, "getCourseFiles exception=" + e);
            }
        });
    }
}
