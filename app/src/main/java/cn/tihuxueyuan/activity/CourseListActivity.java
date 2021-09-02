package cn.tihuxueyuan.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.tihuxueyuan.basic.ActivityManager;
import cn.tihuxueyuan.basic.BaseActivity;
import cn.tihuxueyuan.commonlistview.CommonAdapter;
import cn.tihuxueyuan.commonlistview.ViewHolder;
import cn.tihuxueyuan.globaldata.AppData;
import cn.tihuxueyuan.http.HttpCallback;
import cn.tihuxueyuan.http.HttpClient;
import cn.tihuxueyuan.model.CourseFileList;
import cn.tihuxueyuan.model.CourseFileList.CourseFile;
import cn.tihuxueyuan.R;
import cn.tihuxueyuan.utils.Constant;
import cn.tihuxueyuan.utils.SPUtils;
import cn.tihuxueyuan.verticaltabrecycler.MainActivity;
import cn.tihuxueyuan.verticaltabrecycler.RecyclerActivity;
import okhttp3.internal.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CourseListActivity extends BaseActivity {
    private android.widget.ListView lv;
    private String couseId;
    private String title;
    private CommonAdapter<CourseFile> mAdapter;
    public List<CourseFileList.CourseFile> mList = new ArrayList<>();
    private AppData appData;
    TextView lp;
    Button bt;
    TextView titleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_list_activity);

        couseId = getIntent().getStringExtra("course_id");
        title = getIntent().getStringExtra("title");

        this.titleView =  findViewById(R.id.course_title);
        titleView.setText(title);
        this.lv =  findViewById(R.id.courseList);
        this.lp =  findViewById(R.id.last_play);
        bt = findViewById(R.id.reverse);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), Music_Activity.class);//创建Intent对象，启动check
//                String musicUrll = mList.get(position).getMp3url() + "?fileName=" + mList.get(position).getMp3FileName();
                String musicUrl = SPUtils.getMp3Url(mList.get(position).getMp3FileName());
                intent.putExtra("music_url", musicUrl);
                intent.putExtra("current_position", position);
                intent.putExtra("is_new", true);
                intent.putExtra("title", SPUtils.getTitleFromName(mList.get(position).getFileName()));
                startActivity(intent);
            }
        });
        appData = (AppData) getApplication();
        Log.d("tag2", "onCreate: param: " + couseId);
    }

//        @Override
//    public void onBackPressed() {
//        moveTaskToBack(true);
//    }

    /*
    进入时log输出：
    E/====: onRestart()
    E/====: onStart()
     */
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

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
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
//    }


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
        Log.e("====", "onResume()");
        ActivityManager.setCurrentActivity(CourseListActivity.this);
        getCourseFiles();
    }

    public void refreshListView() {

        bt.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Collections.reverse(mList);
                mAdapter.notifyDataSetChanged();
            }
        });

        String lastTitle = SPUtils.getTitleFromName(appData.mListMap.get(  lastListenedCourseFileId).getFileName());
        lp.setText("上次播放:" + lastTitle);
        lp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Music_Activity.class);//创建Intent对象，启动check
//                String musicUrll = mList.get(position).getMp3url() + "?fileName=" + mList.get(position).getMp3FileName();
                int position;
                CourseFile c = null;
                for ( CourseFile c1 : mList) {
                    int i = c1.getId();
                    if (i == lastListenedCourseFileId) {
                        c = c1;
                        break;
                    }
                }



                if (c != null ){
                    String musicUrl = SPUtils.getMp3Url(c.getFileName());
                    intent.putExtra("music_url", musicUrl);
                    intent.putExtra("current_position", lastListenedCourseFileId+1);
                    intent.putExtra("is_new", true);
                    intent.putExtra("title", SPUtils.getTitleFromName(c.getFileName()));
                    startActivity(intent);
                }

//                startActivity(new Intent(CourseListActivity.this, Music_Activity.class));

            }
        });
        lv.setAdapter(mAdapter = new CommonAdapter<CourseFile>(getApplicationContext(), mList, R.layout.dashboard_item_layout) {
            @Override
            public void convertView(ViewHolder holder, CourseFile courseFile) {
                int percent = courseFile.getListenedPercent();
//                String duration = SPUtils.getTimeStrFromSecond(courseFile.getDuration());
                String duration =courseFile.getDuration();

//                courseFile.getLastListenedCourseFileId();
                int color;
                if  ( appData.currentPostion >=0  &&  Constant.appData.mList.get(Constant.appData.currentPostion).getId() == courseFile.getId()) {
                    color =  Color.parseColor("#FF0000");
                }else {
                    if (percent > 0 ) {
                        color =  Color.parseColor("#777777");
                    }else{
                        color =  Color.parseColor("#000000");
                    }
                }

                if (percent > 0 ) {
                    holder.set(R.id.name, SPUtils.getTitleFromName(courseFile.getFileName()), color);
                    holder.set(R.id.number, courseFile.getNumber(), color);
                    holder.set(R.id.percent,  "已听" + percent + "%", color);
                    holder.set(R.id.duration,  "时长" + duration, color);
                }else{
                    holder.set(R.id.name, SPUtils.getTitleFromName(courseFile.getFileName()), color);
                    holder.set(R.id.number, courseFile.getNumber(), color);
                    holder.set(R.id.duration,  "时长" + duration, color);
                    holder.getView(R.id.percent).setVisibility(View.INVISIBLE);
                }
            }
        });

//        mAdapter.notifyDataSetChanged();
    }
    int lastListenedCourseFileId;
    public void getCourseFiles() {
        HttpClient.getCourseFilesByCourseId(couseId, new HttpCallback<CourseFileList>() {
            @Override
            public void onSuccess(CourseFileList response) {
                if (response == null || response.getCourseFileList() == null || response.getCourseFileList().isEmpty()) {
                    onFail(null);
                    return;
                }
                mList = response.getCourseFileList();
                appData.mList = mList;
                Map<Integer, CourseFile> m = appData.mListMap;
                m.clear();
//                mListMap
                for ( CourseFile c1 : mList) {
                    int i = c1.getId();
                    m.put(i, c1);
//                    if m[i]
//                    if (i == lastListenedCourseFileId) {
//                        c = c1;
//                        break;
//                    }
                }

                lastListenedCourseFileId = response.getLastListenedCourseFileId();
                refreshListView();
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }
}
