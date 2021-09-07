package cn.tihuxueyuan.globaldata;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tihuxueyuan.activity.MainActivity;
import cn.tihuxueyuan.model.CourseFileList;
import cn.tihuxueyuan.receiver.HomeReceiver;

public class AppData extends Application{
    private String b;
    public int currentPostion = -1;
    public String baseUrl;
    public String mp3SourceRouter;
    public String courseStorePath = "";
    public int direction = 1;
    public String UserCode = "7899000";
    public int lastCourseId = -1;
    public int currentMusicCourseId = -3;

    public int lastCourseFileId = -1;
    public int currentCourseFileId = -2;
    public static MainActivity.MusicReceiver musicReceiver = null;
    public  static HomeReceiver mHomeKeyReceiver = null;
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
