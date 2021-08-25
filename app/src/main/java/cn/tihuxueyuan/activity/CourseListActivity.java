package cn.tihuxueyuan.activity;
//

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
////import android.support.v7.app.AppCompatActivity;
import android.provider.Settings;
import android.util.Log;
//import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
//
////
//import  com.squareup.*;
//
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import cn.tihuxueyuan.basic.ActivityManager;
import cn.tihuxueyuan.basic.BaseActivity;
import cn.tihuxueyuan.commonlistview.CommonAdapter;
import cn.tihuxueyuan.commonlistview.ViewHolder;
import cn.tihuxueyuan.fragment.dashboard.DashboardFragment;
import cn.tihuxueyuan.fragment.home.HomeFragment;
import cn.tihuxueyuan.fragment.list.ListFragment;
import cn.tihuxueyuan.globaldata.Data;
import cn.tihuxueyuan.http.HttpCallback;
import cn.tihuxueyuan.http.HttpClient;
import cn.tihuxueyuan.model.CourseFileList;
import cn.tihuxueyuan.model.CourseFileList.CourseFile;
import cn.tihuxueyuan.model.CourseTypeList;
import cn.tihuxueyuan.service.FloatingImageDisplayService;
import cn.tihuxueyuan.utils.Constant;
import cn.tihuxueyuan.verticaltabrecycler.MainActivity;
import okhttp3.OkHttpClient;
import okhttp3.FormBody;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import cn.tihuxueyuan.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CourseListActivity extends BaseActivity {

    final OkHttpClient client = new OkHttpClient();
    private android.widget.ListView lv;
    private CommonAdapter mAdapter;
    private String couseId;
    private String title;
    public List<CourseFileList.CourseFile> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_list_activity);

        couseId = (String) getIntent().getStringExtra("course_id");
        title = (String) getIntent().getStringExtra("title");
        setTitle(title);
        this.lv = (ListView) findViewById(R.id.courseList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), Music_Activity.class);//创建Intent对象，启动check
                String musicUrl = mList.get(position).getMp3url() + "?fileName=" + mList.get(position).getMp3FileName();
                intent.putExtra("music_url", musicUrl);
                intent.putExtra("current_position", position);
                intent.putExtra("is_new", true);
                String titleArr[] = mList.get(position).getTitle().split("\\.");
                intent.putExtra("title", titleArr[0]);
                startActivity(intent);
            }
        });


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
        initCourseType();


        if (Constant.floatingControl != null) {
            Constant.floatingControl.setVisibility(true);
        }
    }

    public void refreshListView() {
        lv.setAdapter(mAdapter = new CommonAdapter<CourseFile>(getApplicationContext(), mList, R.layout.dashboard_item_layout) {
            @Override
            public void convertView(ViewHolder holder, CourseFile courseFile) {
//                String s = courseFile.getTitle();
//                Log.d("tag1", "s: " + s);

                /*
                在正则表达式中是个已经被使用的特殊符号（"."、"|"、"^"等字符）
所以想要使用 | ，必须用 \ 来进行转义，而在java字符串中，\ 也是个已经被使用的特殊符号，也需要使用 \ 来转义。
所以应为：String[] all=str.split(("\\.")
                 */
                String a[] = courseFile.getTitle().split("\\.");
//                if (a != null  && a[0] != null) {
//                    holder.set(R.id.name, a[0]);
//                }else{
//                    holder.set(R.id.name,contactsBean.getTitle());
//                }

                holder.set(R.id.name, a[0]);

            }
        });

//        mAdapter.notifyDataSetChanged();
    }

    public void initCourseType() {
        HttpClient.getCourseFilesByCourseId(couseId, new HttpCallback<CourseFileList>() {
            @Override
            public void onSuccess(CourseFileList response) {
                if (response == null || response.getCourseFileList() == null || response.getCourseFileList().isEmpty()) {
                    onFail(null);
                    return;
                }
                mList = response.getCourseFileList();

                final Data app = (Data)getApplication();
                app.mList = mList;
                refreshListView();
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }
}
