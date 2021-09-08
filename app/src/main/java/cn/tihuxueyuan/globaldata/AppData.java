package cn.tihuxueyuan.globaldata;

import android.app.Application;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tihuxueyuan.activity.MainActivity;
import cn.tihuxueyuan.model.CourseFileList;
import cn.tihuxueyuan.receiver.HomeReceiver;

public class AppData extends Application {
    public static int currentPostion = -1;
    public static String baseUrl;
    public static String mp3SourceRouter;
    public static String courseStorePath = "";
    public static int direction = 1;
    public static String UserCode = "7899000";
    public static int lastCourseId = -1;
    public static int currentMusicCourseId = -3;
    public static int lastCourseFileId = -1;
    public static int currentCourseFileId = -2;

    public static Bitmap notificationBitMap = null;
    public static int currentCourseId = -1;
    public static String currentCourseImageFileName ;

    public static MainActivity.MusicReceiver musicReceiver = null;
    public static HomeReceiver mHomeKeyReceiver = null;
    public static List<CourseFileList.CourseFile> courseFileList = new ArrayList<>();
    public static Map<Integer, CourseFileList.CourseFile> courseFileMap = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
