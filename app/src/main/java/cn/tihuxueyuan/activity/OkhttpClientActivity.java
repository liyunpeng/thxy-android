package cn.tihuxueyuan.activity;
//
import android.os.Bundle;
////import android.support.v7.app.AppCompatActivity;
import android.util.Log;
//import android.view.View;
import android.widget.Button;
//
////
//import  com.squareup.*;
//
import android.view.View;

import cn.tihuxueyuan.basic.BaseActivity;
import okhttp3.OkHttpClient;
import okhttp3.FormBody;

import androidx.appcompat.app.AppCompatActivity;

import cn.tihuxueyuan.R;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkhttpClientActivity extends BaseActivity implements View.OnClickListener {

    private Button bt_get;
    private Button bt_post;

    final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        bt_get=(Button)findViewById(R.id.bt_get);
        bt_post=(Button)findViewById(R.id.bt_post);

        bt_get.setOnClickListener(this);
        bt_post.setOnClickListener(this);

        String s = (String) getIntent().getStringExtra("course_id");
        Log.d("tag2", "onCreate: param: "+ s);
    }
    
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_get:
                getRequest();
                break;

            case R.id.bt_post:
                postRequest();
                break;

        }
    }

    private void getRequest() {

        final Request request=new Request.Builder()
                .get()
                .tag(this)
                .url("http://www.wooyun.org")
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
//                    response = client.newCall(request).execute();
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Log.i("WY","打印GET响应的数据：" + response.body().string());
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void postRequest() {
//        RequestBody formBody = new FormEncodingBuilder()
        RequestBody formBody = new FormBody.Builder()
                .add("","")
                .build();

        final Request request = new Request.Builder()
                .url("http://10.0.2.2:8082/api/getCourseTypes")
                .post(formBody)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Log.i("WY","打印POST响应的数据：" + response.body().string());
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
