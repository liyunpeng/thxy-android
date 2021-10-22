package cn.tihuxueyuan.activity;

import static cn.tihuxueyuan.utils.Constant.TAG;
import static cn.tihuxueyuan.utils.Constant.musicControl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import cn.tihuxueyuan.model.ListenedFile;
import cn.tihuxueyuan.model.SqliteUserCourse;
import cn.tihuxueyuan.model.UserListenedCourse;
import cn.tihuxueyuan.utils.ComparatorValues;
import cn.tihuxueyuan.utils.Constant;
import cn.tihuxueyuan.utils.SPUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseListActivity extends BaseActivity {
    private AppData appData;
    private int mCouseId;
    private boolean courseListOrder = true;
    private int createFlag = 0;
    private String title;
    private CommonAdapter<CourseFile> mAdapter;
    public List<CourseFileList.CourseFile> mList = new ArrayList<>();
    private android.widget.ListView courseListView;
    private TextView lastPlayTextView;
    private TextView reverseTextView;
    private TextView titleView;
    private ImageView imageView;
    private LiveDataBus.BusMutableLiveData<ListenedFile> courseListActivityLiveData;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_list_activity);

        Log.d(TAG, "CourseListActivity 创建， 调用oncreate");
        mCouseId = getIntent().getIntExtra("course_id", 0);
        title = getIntent().getStringExtra("title");

        appData = (AppData) getApplication();
        titleView = findViewById(R.id.course_title);
        imageView = findViewById(R.id.course_image);
        courseListView = findViewById(R.id.courseList);
        lastPlayTextView = findViewById(R.id.last_play);
        reverseTextView = findViewById(R.id.reverse);

        titleView.setText(title);

        String bitmapFilePath = "/data/user/0/cn.tihuxueyuan/files/11_23.jpeg";
//
//        try {
//            FileInputStream fis = new FileInputStream(s);
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
//
////            byte[] a =
//
//
//
//
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//

        File f=new File(bitmapFilePath);
        if(f.exists()) {

            Log.d(TAG, "本地图片文件存, 文件长度=" + f.length());
        }else{
            Log.d(TAG, "本地图片文件不存在");
        }


        BitmapFactory.Options opts = new BitmapFactory.Options();

        opts.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeFile(bitmapFilePath, opts);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            Log.d(TAG, "用本地文件设置图片");
        }else{
            Log.d(TAG, "用网络获取设置图片");
            SPUtils.httpGetCourseImage(getApplicationContext(),  mCouseId, imageView);
        }






        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String musicUrl = SPUtils.getImgOrMp3Url(mList.get(position).getCourseId(), mList.get(position).getMp3FileName());
                Intent intent = new Intent(getApplicationContext(), Music_Activity.class);
                intent.putExtra("music_url", musicUrl);
                intent.putExtra("current_position", position);
                intent.putExtra("is_new", true);
                intent.putExtra("title", SPUtils.getTitleFromName(mList.get(position).getFileName()));

                appData.playingCourseFileList = mList;
                appData.playingCourseFileId = mList.get(position).getId();
                appData.playingCourseId = mCouseId;
                SPUtils.listToMap();

                // debug log
                for (CourseFileList.CourseFile c : Constant.appData.playingCourseFileMap.values()) {
                    Log.d(TAG, "mp3_file_name=" + c.mp3_file_name + ", listenedPercent=" + c.listenedPercent
                            + ", listenedPosition=" + c.listenedPosition);
                }

                startActivity(intent);
            }
        });

        ActivityManager.setCurrentActivity(CourseListActivity.this);

        Log.d(TAG, "onCreate: currentCouseId: " + mCouseId);

        if (!getCourseListFromSqlite3()) {
            // 本地没获取到，再走网络获取
            if (Constant.HAS_USER) {
                httpGetListenedFile();
            } else {
                httpGetCourseFilesV1();
            }
        }

        listToMap();

        reverseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (courseListOrder == true) {
                    courseListOrder = false;
                    Constant.order = false;
                    Collections.sort(mList, new ComparatorValues());
                    reverseTextView.setText(" 倒序");
                } else {
                    courseListOrder = true;
                    Constant.order = true;
                    Collections.sort(mList, new ComparatorValues());
                    reverseTextView.setText(" 正序");
                }

                mAdapter.notifyDataSetChanged();
            }
        });

        if (courseListOrder == true) {
            Constant.order = true;
            Collections.sort(mList, new ComparatorValues());
        } else {
            Constant.order = false;
            Collections.sort(mList, new ComparatorValues());
        }
        courseListActivityObserver();
    }

    private void listToMap() {
        if (currentCourseFileMap != null) {
            currentCourseFileMap.clear();
        }
        currentCourseFileMap = new HashMap<>();
        for (CourseFileList.CourseFile c : mList) {
            int i = c.getId();
            currentCourseFileMap.put(i, c);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        appData.lastCourseId = mCouseId;
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

    public static Map<Integer, CourseFileList.CourseFile> currentCourseFileMap = new HashMap<>();

    private void freshLastPlay() {
//        if ( appData.lastCourseId == -1  && lastListenedCourseFileId >= 0 && appData.lastCourseId != Integer.parseInt(currentCouseId)) {
//        if (lastListenedCourseFileId >= 0 && appData.lastCourseId != Integer.parseInt(currentCouseId)) {
        if (lastListenedCourseFileId >= 0 && appData.lastCourseId != mCouseId) {
            Log.d(TAG, " freshLastPlay 准备设置为 可见 ");
            if (musicControl != null) {
                if (mCouseId != musicControl.getCurrentCourseId()) {
                    Log.d(TAG, " freshLastPlay 当前课程列表的courseId  和 当前正在播放的courseId 不同， 设置为可见");
                    if (currentCourseFileMap != null && currentCourseFileMap.get(lastListenedCourseFileId) != null) {
                        Log.d(TAG, " currentCourseFileMap.get(lastListenedCourseFileId) != null， 设置为可见");
                        String lastTitle = SPUtils.getTitleFromName(currentCourseFileMap.get(lastListenedCourseFileId).getFileName());
                        lastPlayTextView.setText("上次播放: " + lastTitle);
                        lastPlayTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d(TAG, " freshLastPlay 当前课程列表的courseId  和 当前正在播放的courseId 相同， 设置为不可见");
                    lastPlayTextView.setVisibility(View.INVISIBLE);
                }
            } else {
                if (currentCourseFileMap != null && currentCourseFileMap.get(lastListenedCourseFileId) != null) {
                    Log.d(TAG, " appData.courseFileMap 有数据， 设置为可见");
                    String lastTitle = SPUtils.getTitleFromName(currentCourseFileMap.get(lastListenedCourseFileId).getFileName());
                    lastPlayTextView.setText("上次播放: " + lastTitle);
                    lastPlayTextView.setVisibility(View.VISIBLE);
                }
            }
        } else {
            Log.d(TAG, " freshLastPlay 设置为 不可见 " + " appData.lastCourseId  = " + appData.lastCourseId +
                    ", lastListenedCourseFileId=" + lastListenedCourseFileId +
                    ", appData.lastCourseId= " + appData.lastCourseId +
                    ", currentCouseId=" + mCouseId);
            lastPlayTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (createFlag == 1 && mList != null && mList.size() > appData.currentPostion) {
        if (mList != null && mList.size() > appData.playingCourseFileListPostion) {
            // 从悬浮窗进入音乐界面， 再回到列表界面时，如果是其他课程列表，不刷新
            Log.d(TAG, " 课程列表 onResume 刷新");
            if (Constant.musicControl != null && mCouseId == Constant.appData.playingCourseId) {
                Log.d(TAG, " 课程列表 onResume 刷新, 调用 notifyDataSetChanged ");
                if (mAdapter != null && courseListView != null) {
                    mAdapter.notifyDataSetChanged();
//                    if (appData.playingCourseFileListPostion >= 2) {
//                        courseListView.setSelection(appData.playingCourseFileListPostion - 2);
//                    } else {
//                        courseListView.setSelection(appData.playingCourseFileListPostion);
//                    }
//                courseListView.getTop()
                }
            }

        } else {
            Log.d(TAG, " 课程列表 onResume 不刷新");
        }

        freshLastPlay();
        if (courseListOrder == true) {
            reverseTextView.setText("正序");
        } else {
            reverseTextView.setText("倒序");
        }
    }

    private void courseListActivityObserver() {
        courseListActivityLiveData = LiveDataBus.getInstance().with(Constant.CourseListLiveDataObserverTag, ListenedFile.class);
        courseListActivityLiveData.observe(CourseListActivity.this, true, new Observer<ListenedFile>() {
            @Override
            public void onChanged(ListenedFile value) {
                Log.d(TAG, " CourseListActivity 观察者监控到消息 = " + value);
                if (mList != null && mList.size() >= Constant.appData.playingCourseFileListPostion
                        && mCouseId == Constant.appData.playingCourseId) {
                    mList.get(Constant.appData.playingCourseFileListPostion).listenedPercent = value.listenedPercent;
                    mList.get(Constant.appData.playingCourseFileListPostion).listenedPosition = value.position;

                    SPUtils.updateUserListenedV1(
                            Constant.appData.UserCode,
                            Constant.appData.playingCourseId,
                            Constant.appData.playingCourseFileId,
                            value.listenedPercent,
                            value.position);

                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void refreshListView() {
        freshLastPlay();

        lastPlayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseFile courseFile = currentCourseFileMap.get(lastListenedCourseFileId);
                // android手机里播放音乐 按在listView中的位置找到音乐，而不是按fileId去找音乐，以便与播放上一首， 下一首一致, 以及自动播放下一首处理一致
                appData.playingCourseFileListPostion = SPUtils.findPositionByFileId(lastListenedCourseFileId, mList);
//                 = SPUtils.findPositionByFileId(lastListenedCourseFileId);
                if (courseFile != null) {
                    Intent intent = new Intent(getApplicationContext(), Music_Activity.class);
                    String musicUrl = SPUtils.getImgOrMp3Url(courseFile.getId(), courseFile.getFileName());
                    intent.putExtra("music_url", musicUrl);
                    intent.putExtra("current_position", appData.playingCourseFileListPostion);
                    intent.putExtra("is_new", true);
                    intent.putExtra("title", SPUtils.getTitleFromName(courseFile.getFileName()));
//

                    ////////
//                    String musicUrl = SPUtils.getImgOrMp3Url(mList.get(position).getCourseId(), mList.get(position).getMp3FileName());
//                    Intent intent = new Intent(getApplicationContext(), Music_Activity.class);
//                    intent.putExtra("music_url", musicUrl);
//                    intent.putExtra("current_position", position);
//                    intent.putExtra("is_new", true);
//                    intent.putExtra("title", SPUtils.getTitleFromName(mList.get(position).getFileName()));

                    appData.playingCourseFileList = mList;
                    appData.playingCourseFileId = courseFile.getId();
                    appData.playingCourseId = mCouseId;
                    SPUtils.listToMap();
                    startActivity(intent);
                }
            }
        });
        mAdapter = new CommonAdapter<CourseFile>(getApplicationContext(), mList, R.layout.dashboard_item_layout) {
            @Override
            public void convertView(ViewHolder holder, CourseFile courseFile) {
                int percent = courseFile.getListenedPercent();
                String duration = courseFile.getDuration();
                int pos = courseFile.getListenedPosition();
                int color;
//                if (currentCouseId == appData.playingCourseId &&
//                if (appData.playingCourseFileListPostion >= 0 &&
//                        Constant.appData.playingCourseFileListPostion < Constant.appData.playingCourseFileList.size()) {
//                    if (Constant.appData.playingCourseFileMap.get(Constant.appData.playingCourseFileId) != null) {
////                        if (Constant.appData.courseFileMap.get(Constant.appData.currentCourseFileId).getId() == courseFile.getId()) {
////                        if (Constant.appData.courseFileMap.get(Constant.appData.currentCourseFileId).getId() == courseFile.courseFileId) {
//                        if (Constant.appData.playingCourseFileId == courseFile.courseFileId) {
//                            color = Color.parseColor("#FF0000");
//                        }
//                    } else {
//                        color = Color.parseColor("#000000");
//                    }
//
//                } else {
////                    if (Constant.appData.playingCourseFileId == courseFile.courseFileId) {
////                        color = Color.parseColor("#FF0000");
////                    }else{
//                        if (percent > 0) {
//                            color = Color.parseColor("#777777");
//                        } else {
//                            color = Color.parseColor("#000000");
//                        }
////                    }
//
//                }

                if (Constant.appData.playingCourseFileId >= 0 && Constant.appData.playingCourseFileId == courseFile.id) {
                    color = Color.parseColor("#FF0000");
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
                holder.set(R.id.pos, "位置:" + pos, color);
                holder.getView(R.id.duration).setVisibility(View.GONE);
            }
        };

        courseListView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
    }

    int lastListenedCourseFileId = -1;

    private boolean getCourseListFromSqlite3() {
        mList = Constant.dbUtils.getSqlite3CourseFileList(mCouseId);

        if (mList != null && mList.size() > 0) {
            Log.d(TAG, "mList 不为空，不走网络， 从本地sqlite3数据库读取， 刷新列表");
            SqliteUserCourse sqlite3UserCourse = SPUtils.getUserListened(appData.UserCode, mCouseId);
            if (sqlite3UserCourse != null) {
                Map<Integer, ListenedFile> listendFileMap = sqlite3UserCourse.listenedFileMap;
                if (listendFileMap != null) {
                    for (CourseFile courseFile : mList) {
//                        courseFile.courseFileId = courseFile.getId();
                        courseFile.id = courseFile.getId();
                        ListenedFile listenedFile = listendFileMap.get(courseFile.id);
                        if (listenedFile != null) {
                            courseFile.listenedPercent = listenedFile.listenedPercent;
                            courseFile.listenedPosition = listenedFile.position;
                        }
                    }
                }
                lastListenedCourseFileId = sqlite3UserCourse.lastListenedCourseFileId;
            }

            refreshListView();

            createFlag = 1;
            courseListOrder = true;
            return true;
        } else {
            Log.d(TAG, "mList 为空， 走网络， 从本地sqlite数据库读取， 刷新列表");
            return false;
        }
    }

//    private void httpGetCourseFiles() {
//        HttpClient.getCourseFilesByCourseId(currentCouseId, new HttpCallback<CourseFileList>() {
//            @Override
//            public void onSuccess(CourseFileList response) {
//                if (response == null || response.getCourseFileList() == null || response.getCourseFileList().isEmpty()) {
//                    onFail(null);
//                    return;
//                }
//                mList = response.getCourseFileList();
//                SPUtils.listToMap();
//                lastListenedCourseFileId = response.getLastListenedCourseFileId();
//                Log.d(Constant.TAG, " 上次播放 lastListenedCourseFileId :" + lastListenedCourseFileId);
//                refreshListView();
//
//                int count = Constant.dbUtils.getFileCountByCourseId(playingCourseId);
//                if (count <= 0) {
//                    Log.d(TAG, "保存到本地数据库");
//                    Constant.dbUtils.saveCourseFiles();
//                }
//
//                createFlag = 1;
//                courseListOrder = true;
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                Log.d(Constant.TAG, "getCourseFiles exception=" + e);
//            }
//        });
//    }

    private void httpGetCourseFilesV1() {
        HttpClient.getCourseFilesByCourseIdV1(mCouseId, new HttpCallback<CourseFileList>() {
            @Override
            public void onSuccess(CourseFileList response) {
                if (response == null || response.getCourseFileList() == null || response.getCourseFileList().isEmpty()) {
                    onFail(null);
                    return;
                }
                mList = response.getCourseFileList();
                SPUtils.listToMap();

                int count = Constant.dbUtils.getFileCountByCourseId(mCouseId);
                if (count <= 0) {
                    Log.d(TAG, "保存到本地数据库");
                    Constant.dbUtils.saveCourseFiles(mList);
                }

                if (Constant.HAS_USER) {
                    lastListenedCourseFileId = response.getLastListenedCourseFileId();
                    Log.d(Constant.TAG, " 上次播放 lastListenedCourseFileId :" + lastListenedCourseFileId);
                    if (currentListenedFileMap != null) {
                        for (CourseFile courseFile : mList) {
//                        courseFile.courseFileId = courseFile.id;
                            ListenedFile listenedFile = currentListenedFileMap.get(courseFile.id);
                            if (listenedFile != null) {
                                courseFile.listenedPercent = listenedFile.listenedPercent;
                                courseFile.listenedPosition = listenedFile.position;
                            }
                        }
                    }
                }

                createFlag = 1;
//                courseListOrder = true;
                if (courseListOrder == true) {
                    Constant.order = true;
                    Collections.sort(mList, new ComparatorValues());
                } else {
                    Constant.order = false;
                    Collections.sort(mList, new ComparatorValues());
                }

                refreshListView();
            }

            @Override
            public void onFail(Exception e) {
                Log.d(Constant.TAG, "getCourseFiles exception=" + e);
            }
        });
    }

    Map<Integer, ListenedFile> currentListenedFileMap = null;

    private void httpGetListenedFile() {
        HttpClient.getUserListenedFilesByCodeAndCourseIdV1(mCouseId, new HttpCallback<UserListenedCourse>() {
            @Override
            public void onSuccess(UserListenedCourse response) {
                Log.d(TAG, "httpGetListenedFile onSuccess ");
//                if (response == null || response.getCourseFileList() == null || response.getCourseFileList().isEmpty()) {
//                    onFail(null);
//                    return;
//                }
//                mList = response.getCourseFileList();
//                appData.courseFileList = mList;
//                SPUtils.listToMap();
//                lastListenedCourseFileId = response.getLastListenedCourseFileId();
//                Log.d(Constant.TAG, " 上次播放 lastListenedCourseFileId :" + lastListenedCourseFileId);
//                refreshListView();
//
//                int count = Constant.dbUtils.getFileCountByCourseId(currentCourseId);
//                if (count <= 0) {
//                    Log.d(TAG, "保存到本地数据库");
//                    Constant.dbUtils.saveCourseFiles();
//                }
//                createFlag = 1;
//                courseListOrder = true;

                if (response != null) {
                    Gson gson = new Gson();
                    currentListenedFileMap = gson.fromJson(response.listenedFiles, new TypeToken<Map<Integer, ListenedFile>>() {
                    }.getType());
                }
                httpGetCourseFilesV1();
            }

            @Override
            public void onFail(Exception e) {
                Log.d(Constant.TAG, "getCourseFiles exception=" + e);
                httpGetCourseFilesV1();
            }
        });
    }
}
