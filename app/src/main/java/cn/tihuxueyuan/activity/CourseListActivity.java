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
import cn.tihuxueyuan.fragment.list.ListFragment;
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

public class CourseListActivity extends AppCompatActivity {

    final OkHttpClient client = new OkHttpClient();
    private android.widget.ListView lv;
    private CommonAdapter mAdapter;
    private String couseId;
    private String title;
    private List<CourseFileList.CourseFile> mList = new ArrayList<>();

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
                //将数据存入Intent对象
//                player.setDataSource("http://47.102.146.8:8082/api/fileDownload?fileName=一声佛号一声心.mp3");
                String musicUrl = mList.get(position).getMp3url() + "?fileName=" + mList.get(position).getMp3FileName();
                intent.putExtra("music_url", musicUrl);
                String a[] = mList.get(position).getTitle().split("\\.");
                intent.putExtra("title", a[0]);
//                intent.putExtra("position",String.valueOf(position));
                // ，
                startActivity(intent);
            }
        });


        Log.d("tag2", "onCreate: param: " + couseId);
    }

    @Override
    public void onResume() {
        super.onResume();
        initCourseType();
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
                refreshListView();
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }
}
