package cn.tihuxueyuan.globaldata;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tihuxueyuan.model.CourseFileList;

public class AppData extends Application{
    private String b;
    public int currentPostion = -1;
    public String baseUrl;
    public String mp3SourceRouter;
    public String courseStorePath = "";
    public int direction = 1;
    public String UserCode = "";
    public int lastCourseId = -1;
    public List<CourseFileList.CourseFile> mList = new ArrayList<>();
//    public List<CourseFileList.CourseFile> mList = new ArrayList<>();
    public Map<Integer, CourseFileList.CourseFile> mListMap = new HashMap<>();
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
