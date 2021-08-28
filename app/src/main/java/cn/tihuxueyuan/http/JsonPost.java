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

    Gson gson = new Gson();

    //自己的回调接口
    private ReturnHttpResult returnHttpResult;

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public void HttpToSettingClassified(int id) {
        this.id= id;
    }

    public void saveSettingMsg() {
        ListenedFile a = new ListenedFile();
        a.CourseFileId = 11;
        a.ListenedPercent = (float) 12.34;

        Map map = new HashMap<>();
        map.put("code", "123456");
        map.put("course_id", 12); // id 为 course_file_id 简写
        map.put("listened_file", a);  // pc 为percent 简写
        String param= gson.toJson(map);

        RequestBody requestBody = RequestBody.create(JSON, param);
        Request request = new Request.Builder()
                .post(requestBody)
                .url(Constant.appData.baseUrl+"updateUserListenedFiles")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                returnHttpResult.clickReturnHttpResult(e.getMessage());
                Log.d(Constant.TAG, "onFailure: 失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                returnHttpResult.clickReturnHttpResult(result);
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

    //        type ListenedFile struct {
//            CourseFileId    int     `json:"course_file_id"`
//            ListenedPercent float32 `json:"listened_percent"`
//        }

    private class ListenedFile {
        @SerializedName("cfi")
        public int CourseFileId;
        @SerializedName("pc")
        public float ListenedPercent;

    }
}
