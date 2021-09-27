package cn.tihuxueyuan.http;

import com.google.gson.annotations.SerializedName;

import cn.tihuxueyuan.model.CourseList;

public class CustomResponse {
    @SerializedName("code")
    int code;

    @SerializedName("msg")
    String msg;

    @SerializedName("data")
    CourseList.Course data;

    public CourseList.Course getData() {
        return data;
    }
}
