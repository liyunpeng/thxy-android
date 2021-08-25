package cn.tihuxueyuan.globaldata;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import cn.tihuxueyuan.model.CourseFileList;

public class Data extends Application{
    private String b;
    public int currentPostion;
    public String musicTitle;
    public List<CourseFileList.CourseFile> mList = new ArrayList<>();

    public String getB(){
        return this.b;
    }
    public void setB(String c){
        this.b= c;
    }
    @Override
    public void onCreate(){
        b = "hello";
        super.onCreate();
    }
}
