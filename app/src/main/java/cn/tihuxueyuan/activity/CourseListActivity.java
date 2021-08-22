package cn.tihuxueyuan.activity;
//
import android.content.Intent;
import android.os.Bundle;
////import android.support.v7.app.AppCompatActivity;
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

import cn.tihuxueyuan.commonlistview.CommonAdapter;
import cn.tihuxueyuan.commonlistview.ViewHolder;
import cn.tihuxueyuan.fragment.home.HomeFragment;
import cn.tihuxueyuan.http.HttpCallback;
import cn.tihuxueyuan.http.HttpClient;
import cn.tihuxueyuan.model.CourseFileList;
import cn.tihuxueyuan.model.CourseFileList.CourseFile;
import cn.tihuxueyuan.model.CourseTypeList;
import cn.tihuxueyuan.verticaltabrecycler.MainActivity;
import okhttp3.OkHttpClient;
import okhttp3.FormBody;

import androidx.appcompat.app.AppCompatActivity;

import cn.tihuxueyuan.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CourseListActivity extends AppCompatActivity{

    final OkHttpClient client = new OkHttpClient();
    private android.widget.ListView lv;
    private CommonAdapter mAdapter;
    private  String couseId;
    private List<CourseFileList.CourseFile> mList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_list_activity);
        couseId = (String) getIntent().getStringExtra("course_id");

        this.lv = (ListView) findViewById(R.id.courseList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


        Log.d("tag2", "onCreate: param: "+ couseId);
    }

    @Override
    public void onResume() {
        super.onResume();
        initCourseType();
    }

    public void refreshListView() {
        lv.setAdapter(mAdapter = new CommonAdapter<CourseFile>(getApplicationContext(), mList, R.layout.dashboard_item_layout) {
            @Override
            public void convertView(ViewHolder holder, CourseFile contactsBean) {
                holder.set(R.id.name, contactsBean.getTitle());
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
                refreshListView();
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }
}
