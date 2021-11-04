package cn.tihuxueyuan.activity;

import static cn.tihuxueyuan.utils.Constant.TAG;
import static cn.tihuxueyuan.utils.Constant.dbUtils;
import static cn.tihuxueyuan.utils.Constant.musicControl;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import cn.tihuxueyuan.http.CustomResponse;
import cn.tihuxueyuan.http.HttpCallback;
import cn.tihuxueyuan.http.HttpClient;
import cn.tihuxueyuan.http.JsonPost;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.ListView;

public class CourseListActivity extends BaseActivity {
    private AppData appData;
    private boolean mCourseListOrder = true;
    private int mCouseId;
    private int mUpdateVersion;
    private int mLastListenedCourseFileId = -1;
    private String mTitle;
    private String mIntroduction;

    private ImageView mImageView;
    private TextView mTitleView;
    private TextView mIntroductionView;
    private TextView mLastPlayTextView;
    private TextView mReverseTextView;
    private ListView mCourseListView;

    private CommonAdapter<CourseFile> mAdapter;
    private List<CourseFileList.CourseFile> mCourseFileList = new ArrayList<>();
    public static Map<Integer, CourseFileList.CourseFile> mCourseFileMap = new HashMap<>();
    private Map<Integer, ListenedFile> currentListenedFileMap = null;
    private LiveDataBus.BusMutableLiveData<ListenedFile> mCourseListActivityLiveData;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_list_activity);

        mCouseId = getIntent().getIntExtra("course_id", 0);
        mUpdateVersion = getIntent().getIntExtra("update_version", 0);
        mTitle = getIntent().getStringExtra("title");
        mIntroduction = getIntent().getStringExtra("introduction");
        appData = (AppData) getApplication();
        mTitleView = findViewById(R.id.course_title);
        mIntroductionView = findViewById(R.id.introduction);
        mImageView = findViewById(R.id.course_image);
        mCourseListView = findViewById(R.id.courseList);
        mLastPlayTextView = findViewById(R.id.last_play);
        mReverseTextView = findViewById(R.id.reverse);

        mTitleView.setText(mTitle);
        mIntroductionView.setText(mIntroduction);

        setImageView(getApplicationContext());

        registerListener();

        getListViewData();

        courseListActivityObserver();

        ActivityManager.setCurrentActivity(CourseListActivity.this);

        if (mCourseListOrder == true) {
            Constant.order = true;
        } else {
            Constant.order = false;
        }
        Collections.sort(mCourseFileList, new ComparatorValues());
    }

    private void TestReadFileWriteFile() {
        // 测试代码
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            File f1 = new File("/data/user/0/cn.tihuxueyuan/files/11_23.jpeg");
            // 测试读文件
            fis = new FileInputStream(f1);
            int size = fis.available();
            Log.d(TAG, "本地图片文件存在， 文件大小=" + size);
            fis.close();

            // 测试写文件
            fos = new FileOutputStream(f1);
            String s = "aaaa";
            fos.write(s.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setImageView(Context c) {
        // c.getFilesDir().getAbsolutePath() + File.separator 结果为 /data/user/0/cn.tihuxueyuan/files/
        String bitmapFilePath = c.getFilesDir().getAbsolutePath() + File.separator + mCouseId + "_" + appData.currentCourseImageFileName;
        File file = new File(bitmapFilePath);
        if (file.exists()) {
            if (false) {
                TestReadFileWriteFile();
            }

            BitmapFactory.Options opts = new BitmapFactory.Options();
//            opts.inJustDecodeBounds = true;
            opts.inSampleSize = 2;  // 非常重要，没有这个设置， BitmapFactory.decodeFile 会返回空
            Bitmap bitmap = BitmapFactory.decodeFile(bitmapFilePath, opts);

            if (bitmap != null) {
                mImageView.setImageBitmap(bitmap);
                Log.d(TAG, "用本地文件设置图片");
            } else {
                Log.d(TAG, "用网络获取设置图片");
                SPUtils.httpGetCourseImage(getApplicationContext(), mCouseId, mImageView);
            }

        } else {
            Log.d(TAG, "本地图片文件不存在, 用网络方式获取");
            SPUtils.httpGetCourseImage(getApplicationContext(), mCouseId, mImageView);
        }
    }


    private void getListViewData() {
        if (!getCourseListFromSqlite3()) {
            // 本地没获取到，再走网络获取
            if (Constant.HAS_USER) {
                httpGetListenedFile();
            } else {
                httpGetCourseFilesV1();
            }
        } else {
            Message msg = mListActivityHandler.obtainMessage();
            msg.what = 3;
            Bundle bundle = new Bundle();
            bundle.putInt("course_id", mCouseId);
            bundle.putInt("update_version", mCourseFileList.size());



            bundle.putInt("max_course_file_id", mCourseFileList.size());

//            MaxCourseFileId int    `json:"max_course_file_id"`
            msg.setData(bundle);
            mListActivityHandler.sendMessage(msg);
        }
        currentCourseFileListToCurrentCourseFileMap();
    }

    private void registerListener() {
        mCourseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MusicActivity.class);

                String musicUrl = SPUtils.getImgOrMp3Url(mCourseFileList.get(position).getCourseId(), mCourseFileList.get(position).getMp3FileName());
                intent.putExtra("music_url", musicUrl);
                intent.putExtra("current_position", position);
                intent.putExtra("title", SPUtils.getTitleFromName(mCourseFileList.get(position).getFileName()));

                appData.playingCourseId = mCouseId;
                appData.playingCourseFileId = mCourseFileList.get(position).getId();
                appData.playingCourseFileList = mCourseFileList;
                SPUtils.playingListToPlayingMap();

                intent.putExtra(Constant.MUSIC_ACTIVITY_MODE_NAME, Constant.LIST_MODE_VALUE);
//                if (hasDownload != 1) {
//                    intent.putExtra(Constant.MUSIC_ACTIVITY_MODE_NAME, Constant.LIST_MODE_NET_REQUEST_VALUE);
//                } else {
//                    intent.putExtra(Constant.MUSIC_ACTIVITY_MODE_NAME, Constant.LIST_MODE_LOCAL_VALUE);
//                }
                // debug log
//                for (CourseFileList.CourseFile c : Constant.appData.playingCourseFileMap.values()) {
//                    Log.d(TAG, "mp3_file_name=" + c.mp3_file_name + ", listenedPercent=" + c.listenedPercent
//                            + ", listenedPosition=" + c.listenedPosition);
//                }

                startActivity(intent);
            }
        });
        mReverseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCourseListOrder == true) {
                    mCourseListOrder = false;
                    Constant.order = false;
                    Collections.sort(mCourseFileList, new ComparatorValues());
                    mReverseTextView.setText(" 倒序");
                } else {
                    mCourseListOrder = true;
                    Constant.order = true;
                    Collections.sort(mCourseFileList, new ComparatorValues());
                    mReverseTextView.setText(" 正序");
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void currentCourseFileListToCurrentCourseFileMap() {
        if (mCourseFileMap != null) {
            mCourseFileMap.clear();
        }
        mCourseFileMap = new HashMap<>();
        for (CourseFileList.CourseFile c : mCourseFileList) {
            int i = c.getId();
            mCourseFileMap.put(i, c);

            Constant.appData.playingCourseFileMap.put(i, c);

            if (c.getDownloadMode() == 2) {

                if (Constant.downloadingMap.get(c.getId()) == null || Constant.downloadingMap.get(c.getId()) != 1) {
                    // 未在下载列表里
                    Constant.dbUtils.updateCourseFileDownload(c.getId(), 0, "");
                    c.downloadMode = 0;
                }
            }
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

        int lastTop = mCourseListView.getFirstVisiblePosition();
        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(mCouseId), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("lastTop", lastTop);
        editor.commit();
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


    private void refreshLastPlay() {
//        if ( appData.lastCourseId == -1  && lastListenedCourseFileId >= 0 && appData.lastCourseId != Integer.parseInt(currentCouseId)) {
//        if (lastListenedCourseFileId >= 0 && appData.lastCourseId != Integer.parseInt(currentCouseId)) {
        if (mLastListenedCourseFileId >= 0 && appData.playingCourseId != mCouseId) {
            Log.d(TAG, " freshLastPlay 准备设置为 可见 ");
            if (musicControl != null) {
                if (mCouseId != musicControl.getCurrentCourseId()) {
                    Log.d(TAG, " freshLastPlay 当前课程列表的courseId  和 当前正在播放的courseId 不同， 设置为可见");
                    if (mCourseFileMap != null && mCourseFileMap.get(mLastListenedCourseFileId) != null) {
                        Log.d(TAG, " currentCourseFileMap.get(lastListenedCourseFileId) != null， 设置为可见");
                        String lastTitle = SPUtils.getTitleFromName(mCourseFileMap.get(mLastListenedCourseFileId).getFileName());
                        mLastPlayTextView.setText("上次播放: " + lastTitle);
                        mLastPlayTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d(TAG, " freshLastPlay 当前课程列表的courseId  和 当前正在播放的courseId 相同， 设置为不可见");
                    mLastPlayTextView.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mCourseFileMap != null && mCourseFileMap.get(mLastListenedCourseFileId) != null) {
                    Log.d(TAG, " appData.courseFileMap 有数据， 设置为可见");
                    String lastTitle = SPUtils.getTitleFromName(mCourseFileMap.get(mLastListenedCourseFileId).getFileName());
                    mLastPlayTextView.setText("上次播放: " + lastTitle);
                    mLastPlayTextView.setVisibility(View.VISIBLE);
                }
            }
        } else {
            Log.d(TAG, " freshLastPlay 设置为 不可见 " + " appData.lastCourseId  = " + appData.lastCourseId +
                    ", lastListenedCourseFileId=" + mLastListenedCourseFileId +
                    ", appData.lastCourseId= " + appData.lastCourseId +
                    ", currentCouseId=" + mCouseId);
            mLastPlayTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCourseFileList != null && mCourseFileList.size() > appData.playingCourseFileListPostion) {
            // 从悬浮窗进入音乐界面， 再回到列表界面时，如果是其他课程列表，不刷新
            Log.d(TAG, " 课程列表 onResume 刷新");
            if (Constant.musicControl != null && mCouseId == Constant.appData.playingCourseId) {
                Log.d(TAG, " 课程列表 onResume 刷新, 调用 notifyDataSetChanged ");
                if (mAdapter != null && mCourseListView != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        } else {
            Log.d(TAG, " 课程列表 onResume 不刷新");
        }

        refreshLastPlay();
        if (mCourseListOrder == true) {
            mReverseTextView.setText("正序");
        } else {
            mReverseTextView.setText("倒序");
        }

        if (mAdapter != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(mCouseId), Context.MODE_PRIVATE);
            int lastTop = sharedPreferences.getInt("lastTop", 0);
            mCourseListView.setSelection(lastTop);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void courseListActivityObserver() {
        mCourseListActivityLiveData = LiveDataBus.getInstance().with(Constant.CourseListLiveDataObserverTag, ListenedFile.class);
        mCourseListActivityLiveData.observe(CourseListActivity.this, true, new Observer<ListenedFile>() {
            @Override
            public void onChanged(ListenedFile value) {
                Log.d(TAG, " CourseListActivity 观察者监控到消息 = " + value);
                if (mCourseFileList != null && mCourseFileList.size() >= Constant.appData.playingCourseFileListPostion
                        && mCouseId == Constant.appData.playingCourseId) {
                    mCourseFileList.get(Constant.appData.playingCourseFileListPostion).listenedPercent = value.listenedPercent;
                    mCourseFileList.get(Constant.appData.playingCourseFileListPostion).listenedPosition = value.position;
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void refreshView() {
        refreshLastPlay();

        mLastPlayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseFile courseFile = mCourseFileMap.get(mLastListenedCourseFileId);
                // android手机里播放音乐 按在listView中的位置找到音乐，而不是按fileId去找音乐，以便与播放上一首， 下一首一致, 以及自动播放下一首处理一致
                appData.playingCourseFileListPostion = SPUtils.findPositionByFileId(mLastListenedCourseFileId, mCourseFileList);
                if (courseFile != null) {
                    Intent intent = new Intent(getApplicationContext(), MusicActivity.class);
                    String musicUrl = SPUtils.getImgOrMp3Url(courseFile.getId(), courseFile.getFileName());
                    intent.putExtra("music_url", musicUrl);
                    intent.putExtra("current_position", appData.playingCourseFileListPostion);
                    intent.putExtra("title", SPUtils.getTitleFromName(courseFile.getFileName()));
                    intent.putExtra(Constant.MUSIC_ACTIVITY_MODE_NAME, Constant.LAST_PlAY_MODE_VALUE);
                    appData.playingCourseFileList = mCourseFileList;
                    appData.playingCourseFileId = courseFile.getId();
                    appData.playingCourseId = mCouseId;
                    SPUtils.playingListToPlayingMap();
                    startActivity(intent);
                }
            }
        });

        mAdapter = new CommonAdapter<CourseFile>(getApplicationContext(), mCourseFileList, R.layout.dashboard_item_layout) {
            @Override
            public void convertView(ViewHolder holder, CourseFile courseFile) {
                int percent = courseFile.getListenedPercent();
                int pos = courseFile.getListenedPosition();
                int color;

                String duration = courseFile.getDuration();

                if (Constant.appData.playingCourseFileId >= 0 && Constant.appData.playingCourseFileId == courseFile.id) {
                    color = Color.parseColor("#FF0000");
                } else {
                    if (percent > 0) {
                        color = Color.parseColor("#777777");
                    } else {
                        color = Color.parseColor("#000000");
                    }
                }
                holder.set(R.id.name, SPUtils.getTitleFromName(courseFile.getFileName()), color);
                holder.set(R.id.number, String.valueOf(courseFile.getNumber()), color);
                holder.set(R.id.duration, "时长: " + duration, color);
//                holder.set(R.id.pos, "位置:" + pos, color);

                if (percent > 0) {
                    if (percent == 100) {
                        holder.set(R.id.percent, "已听完", color);
                    } else {
                        holder.set(R.id.percent, "已听" + percent + "%", color);
                    }
                    holder.getView(R.id.percent).setVisibility(View.VISIBLE);
                } else {
                    holder.set(R.id.percent, "", color);
                    holder.getView(R.id.percent).setVisibility(View.INVISIBLE);
                }

                int downloadMode = courseFile.getDownloadMode();
                TextView downloadView = holder.getView(R.id.download);
                View downloadProgressBar = holder.getView(R.id.download_progress_bar);

                if (downloadMode == 0) {
                    downloadView.setVisibility(View.VISIBLE);
                    downloadProgressBar.setVisibility(View.INVISIBLE);
                    downloadView.setText("下载");

                    downloadView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            downloadProgressBar.setVisibility(View.VISIBLE);
                            downloadView.setVisibility(View.INVISIBLE);


                            Log.d(TAG, "发送开始下载的消息");
                            Message msg = mListActivityHandler.obtainMessage();
                            msg.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putInt("course_file_id", courseFile.id);
                            msg.setData(bundle);
                            mListActivityHandler.sendMessage(msg);
                        }
                    });

                } else if (downloadMode == 1) {
                    downloadView.setText("已下载");
                    downloadView.setVisibility(View.VISIBLE);
                    downloadProgressBar.setVisibility(View.INVISIBLE);
                } else if (downloadMode == 2) {
                    downloadView.setVisibility(View.INVISIBLE);
                    downloadProgressBar.setVisibility(View.VISIBLE);
                }
            }
        };

        mCourseListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void downloadFileImpl(int courseFileId) {
        CourseFile courseFile = mCourseFileMap.get(courseFileId);
        Context c = getApplicationContext();
        Constant.dbUtils.updateCourseFileDownload(courseFile.getId(), 2, "");
        // 更新列表
        for (CourseFile courseFileItem : mCourseFileList) {
            if (courseFileItem.getId() == courseFile.getId()) {
                courseFileItem.downloadMode = 2;
                break;
            }
        }

        Constant.downloadingMap.put(courseFileId, 1);
        String mp3Url = SPUtils.getImgOrMp3Url(courseFile.getCourseId(), courseFile.getFileName());
        Log.d(TAG, "开始下载 courseFile id=" + courseFileId + ", 文件名=" + courseFile.getFileName());
        HttpClient.okHttpDownloadFile(mp3Url, new HttpCallback<Response>() {
            @Override
            public void onSuccess(Response response) {
                String localStorePath = c.getFilesDir().getAbsolutePath() +
                        File.separator + courseFile.getCourseId() + "_" + courseFile.getFileName();

                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    File dest = new File(localStorePath);
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());

                    bufferedSink.close();
                    Constant.dbUtils.updateCourseFileDownload(courseFile.getId(), 1, localStorePath);
                    Log.i(Constant.TAG, "下载成功, 文件路径保存到数据库，保存路径=" + localStorePath);

                } catch (Exception e) {
                    e.printStackTrace();
                    Constant.downloadingMap.remove(courseFile.getId());
                    Constant.dbUtils.updateCourseFileDownload(courseFile.getId(), 0, "");
                    Log.i(Constant.TAG, "下载失败");
                } finally {
                    if (bufferedSink != null) {
                        try {
                            bufferedSink.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    Constant.downloadingMap.remove(courseFile.getId());

                    // 更新列表
                    for (CourseFile courseFileItem : mCourseFileList) {
                        if (courseFileItem.getId() == courseFile.getId()) {
                            courseFileItem.downloadMode = 1;
                            courseFileItem.localStorePath = localStorePath;
                            break;
                        }
                    }
                    Log.d(TAG, "下载完成，发送下载完成的消息");

                    Message msg = mListActivityHandler.obtainMessage();
                    msg.what = 2;
                    Bundle bundle = new Bundle();
                    msg.setData(bundle);
                    mListActivityHandler.sendMessage(msg);
                }
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }

    public Handler mListActivityHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int courseFileId = msg.getData().getInt("course_file_id");
                    Log.d(TAG, " 接受到下载 消息， courseFileId =" + courseFileId);
                    downloadFileImpl(courseFileId);
                    break;
                case 2:
                    mAdapter.notifyDataSetChanged();
                    break;
                case 3:
                    Map map = new HashMap<>();
                    int courseId = msg.getData().getInt("course_id");
                    int updateVersion = msg.getData().getInt("update_version");
                    map.put("id", courseId);
                    map.put("update_version", updateVersion);
//                    map.put("course_file_id", fileCount);


//                    type CommonRequest struct {
//                    Id           int    `json:"id"` // id 为course id 或为 type id
//                    CourseFileId int    `json:"course_file_id"`
//                    Name         string `json:"name"`
//                    FileCount    int    `json:"file_count"`
//                }
                    getFileListDelayed(map);
                    break;
            }

        }
    };

    private boolean getCourseListFromSqlite3() {
        mCourseFileList = Constant.dbUtils.getSqliteCourseFileList(mCouseId);

        if (mCourseFileList != null && mCourseFileList.size() > 0) {
            Log.d(TAG, "mList 不为空，不走网络， 从本地sqlite3数据库读取 已听数据， 已听位置， 刷新列表");
            SqliteUserCourse sqlite3UserCourse = SPUtils.getUserListened(appData.UserCode, mCouseId);
            if (sqlite3UserCourse != null) {
                Map<Integer, ListenedFile> listendFileMap = sqlite3UserCourse.listenedFileMap;
                if (listendFileMap != null) {
                    for (CourseFile courseFile : mCourseFileList) {
                        courseFile.id = courseFile.getId();
                        ListenedFile listenedFile = listendFileMap.get(courseFile.id);
                        if (listenedFile != null) {
                            courseFile.listenedPercent = listenedFile.listenedPercent;
                            courseFile.listenedPosition = listenedFile.position;
                        }
                    }
                }
                mLastListenedCourseFileId = sqlite3UserCourse.lastListenedCourseFileId;
            }

            refreshView();

            mCourseListOrder = true;
            return true;
        } else {
            Log.d(TAG, "mList 为空， 走网络， 从本地sqlite数据库读取， 刷新列表");
            return false;
        }
    }

    private void getFileListDelayed(Map map) {
        Gson gson = new Gson();
        String param = gson.toJson(map);
        JsonPost.postHttpRequest("findCourseFileByCourseIdAndUpdateVersion", param, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(Constant.TAG, "findCourseFileByCourseIdAndUpdateVersion onFailure: 失败" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response == null) {
                        Log.d(TAG, "findCourseFileByCourseIdAndCount 接口返回为空， 课程列表不需要更新");
                        return;
                    }

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                    StringBuffer stringBuffer = new StringBuffer("");
                    // 获取本系统的行分割线
//                    String NL = System.getProperty("line.separator");
//                    // 把http响应的输入流数据按行读取到StringBuffer中
//                    String line = "";
//                    while ((line = bufferedReader.readLine()) != null) {
//                        stringBuffer.append(line + NL);
//                    }

                    // 把http响应的输入流数据按行读取到StringBuffer中
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                    String result = stringBuffer.toString();
                    Log.d(ContentValues.TAG, "result = " + result);

                    if (result.length() < 5 ) {
                        return;
                    }
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<CourseFile>>() {}.getType();
                    List<CourseFile> courseFileList = gson.fromJson(result, listType);

                    List<CourseFile> courseFileListDiff = new ArrayList<>();

                    int saveFlag = 0;
                    int updateFlag = 0;
                    for (CourseFile c : courseFileList) {
                        if (mCourseFileMap.get(c.getId()) == null) {
                            mCourseFileMap.put(c.getId(), c);
                            mCourseFileList.add(c);
                            saveFlag = 1;
                            dbUtils.saveCourseFile(c);
                            Log.d(TAG, " 新增courseFile文件更新到当前表");
                        } else if ( !mCourseFileMap.get(c.getId()).getFileName().equalsIgnoreCase( c.getFileName())) {
                            updateFlag = 1;
                            dbUtils.updateCourseFileFilename(c.id, c.getFileName());
                        }
                    }


                    if (  updateFlag == 1){
                        Log.d(TAG, " 有新增courseFile文件， 当前课程列表重新读取数据库更新");
                        getCourseListFromSqlite3();

//                        mAdapter.notifyDataSetChanged();
                        Message msg = mListActivityHandler.obtainMessage();
                        msg.what = 2;
                        Bundle bundle = new Bundle();
                        msg.setData(bundle);
                        mListActivityHandler.sendMessage(msg);
                    }else if (saveFlag == 1) {
//                        mAdapter.notifyDataSetChanged();

                        Message msg = mListActivityHandler.obtainMessage();
                        msg.what = 2;
                        Bundle bundle = new Bundle();
                        msg.setData(bundle);
                        mListActivityHandler.sendMessage(msg);
                    }



//                    mCourseFileList =
//                    CourseFile course = gson.fromJson(result, CourseFile.class);


//                    intent.putExtra("music_url", musicUrl);
//                    intent.putExtra("current_position", position);
//                    intent.putExtra(Constant.MUSIC_ACTIVITY_MODE_NAME, Constant.LIST_MODE_VALUE);
//
//                    appData.playingCourseFileId = mList.get(position).getId();
//                    appData.playingCourseId = mList.get(position).getCourseId();
//
//                    appData.currentCourseImageFileName = course.getData().getImgFileName();
//
//                    String titleArr[] = mList.get(position).getFileName().split("\\.");
//                    intent.putExtra("title", titleArr[0]);
//
//
//                    Constant.order = true;
//                    Collections.sort(mList, new ComparatorValues());
//
//                    appData.playingCourseFileList = mList;
//                    appData.playingCourseFileId = mList.get(position).getId();
//                    SPUtils.playingListToPlayingMap();
//                    startActivity(intent);
//                    Log.d(Constant.TAG, "onResponse: " + result);

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

    private void httpGetCourseFilesV1() {
        HttpClient.getCourseFilesByCourseIdV1(mCouseId, new HttpCallback<CourseFileList>() {
            @Override
            public void onSuccess(CourseFileList response) {
                if (response == null || response.getCourseFileList() == null || response.getCourseFileList().isEmpty()) {
                    onFail(null);
                    return;
                }
                mCourseFileList = response.getCourseFileList();
                SPUtils.playingListToPlayingMap();

                int count = Constant.dbUtils.getFileCountByCourseId(mCouseId);
                if (count <= 0) {
                    Log.d(TAG, "网络获取的CourseFile 保存到本地数据库");
                    Constant.dbUtils.saveCourseFiles(mCourseFileList);
                }

                if (Constant.HAS_USER) {
                    mLastListenedCourseFileId = response.getLastListenedCourseFileId();
                    Log.d(Constant.TAG, " 上次播放 lastListenedCourseFileId :" + mLastListenedCourseFileId);
                    if (currentListenedFileMap != null) {
                        for (CourseFile courseFile : mCourseFileList) {
                            ListenedFile listenedFile = currentListenedFileMap.get(courseFile.id);
                            if (listenedFile != null) {
                                courseFile.listenedPercent = listenedFile.listenedPercent;
                                courseFile.listenedPosition = listenedFile.position;
                            }
                        }
                    }
                }

//                courseListOrder = true;
                if (mCourseListOrder == true) {
                    Constant.order = true;
                    Collections.sort(mCourseFileList, new ComparatorValues());
                } else {
                    Constant.order = false;
                    Collections.sort(mCourseFileList, new ComparatorValues());
                }


                currentCourseFileListToCurrentCourseFileMap();

                refreshView();
            }

            @Override
            public void onFail(Exception e) {
                Log.d(Constant.TAG, "getCourseFiles exception=" + e);
            }
        });
    }

    private void httpGetListenedFile() {
        HttpClient.getUserListenedFilesByCodeAndCourseIdV1(mCouseId, new HttpCallback<UserListenedCourse>() {
            @Override
            public void onSuccess(UserListenedCourse response) {
                Log.d(TAG, "httpGetListenedFile onSuccess ");

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
