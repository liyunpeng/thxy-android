package cn.tihuxueyuan.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.tihuxueyuan.utils.Constant;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JsonPost{

    private int id;

    //自己的回调接口
    private static ReturnHttpResult returnHttpResult;

    static  OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public void HttpToSettingClassified(int id) {
        this.id= id;
    }

    public static void postListenedPercent(String jsonStr) {
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder()
                .post(requestBody)
                .url(Constant.appData.baseUrl+"updateUserListenedFiles")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                returnHttpResult.clickReturnHttpResult(e.getMessage());
                Log.d(Constant.TAG, "onFailure: 失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
//                returnHttpResult.clickReturnHttpResult(result);
                Log.d(Constant.TAG, "onResponse: " + result);
            }
        });
    }

    //回调得到response内容
    public void setReturnHttpResult(ReturnHttpResult returnHttpResult) {
        this.returnHttpResult = returnHttpResult;
    }

    private class ReturnHttpResult {
        public void clickReturnHttpResult(String result) {
        }
    }

    public static class ListenedFile {
        @SerializedName("cfi") // cfi 为 course_file_id 简写
        public int CourseFileId;
        @SerializedName("pc")  // pc 为percent 简写
        public int ListenedPercent;



    }
}
