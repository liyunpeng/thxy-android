package cn.tihuxueyuan.utils;

import static cn.tihuxueyuan.utils.Constant.TAG;
import static cn.tihuxueyuan.utils.Constant.musicControl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import cn.tihuxueyuan.globaldata.AppData;
import cn.tihuxueyuan.http.JsonPost;
import cn.tihuxueyuan.model.CourseFileList;

public class SPUtils {
    private static final String NAME = "config";

    public static void sendListenedPerscent() {
        JsonPost.ListenedFile listenedFile = new JsonPost.ListenedFile();
        listenedFile.CourseFileId = Constant.appData.courseFileList.get(Constant.appData.currentPostion).getId();
        listenedFile.ListenedPercent = musicControl.getListenedPercent();
        listenedFile.Position = musicControl.getPosition();
        Log.d(TAG, "调jsonpost网络接口， 写入已听数据" +
                ", 文件名= " + Constant.appData.courseFileList.get(Constant.appData.currentPostion).getFileName() +
                ", ListenedPercent = " + listenedFile.ListenedPercent +
                ", Position=" + listenedFile.Position +
                ", duration=" + musicControl.getDuration());
        Map map = new HashMap<>();
        map.put("code", "7899000");
        map.put("course_id", Constant.appData.courseFileList.get(Constant.appData.currentPostion).getCourseId());
        map.put("listened_file", listenedFile);
        map.put("last_listened_file_id", Constant.appData.courseFileList.get(Constant.appData.currentPostion).getId());

        Gson gson = new Gson();
        String param = gson.toJson(map);
        JsonPost.postListenedPercent(param);
        Log.d(TAG, "Musicactivity onStop ");
        Constant.appData.lastCourseFileId = Constant.appData.currentCourseFileId;
    }

    public static void putBoolean(String key, boolean value, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key, boolean defValue, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    public static void putString(String key, String value, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static String getString(String key, String defValue, Context context) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(NAME,
                    Context.MODE_PRIVATE);
            return sp.getString(key, defValue);
        }
        return "";
    }

    public static void putInt(String key, int value, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }


    public static int getInt(String key, int defValue, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    public static String getTimeStrFromSecond(int seconds) {
        int minute = seconds / 60;
        int second = seconds % 60;

        if (second == 0) {
            return minute + ":00";
        } else {
            return minute + ":" + second;
        }
    }

    public static void remove(String key, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }

    /*
        输入：测试(21)课程.mp3
        输出：测试(21)课程
     */
    public static String getTitleFromName(String fileName) {
        /* 在正则表达式中是个已经被使用的特殊符号（"."、"|"、"^"等字符）
            所以想要使用 | ，必须用 \ 来进行转义，而在java字符串中，\ 也是个已经被使用的特殊符号，也需要使用 \ 来转义。
            所以应为：
             String titleArr[] = courseFile.getFileName().split("\\.");
         */
        String titleArr[] = fileName.split("\\.");
        return titleArr[0];
    }

    // 获取 img 或 mp3 的url
    public static String getImgOrMp3Url(int courseId, String fileName) {
        AppData appData = Constant.appData;
        String titleArr[] = fileName.split("\\.");
        String fileType = titleArr[1];

        if (fileType.contains("mp3") != true && fileType.contains("MP3") != true && fileType.contains("Mp3") != true) {
            fileType = "img";
        }else{
            fileType = "mp3";
        }
        String imgOrMp3Url = appData.baseUrl + appData.mp3SourceRouter +
                "?course_id=" + courseId +
                "&file_type=" + fileType +
                "&file_name=" + fileName;

        Log.d(TAG, " imgOrMp3Url = " + imgOrMp3Url);
        return imgOrMp3Url;
    }

    // list 转 map
    public static void listToMap() {
        Map<Integer, CourseFileList.CourseFile> m = Constant.appData.courseFileMap;
        m.clear();
        for (CourseFileList.CourseFile c : Constant.appData.courseFileList) {
            int i = c.getId();
            m.put(i, c);
        }
    }

    public static int findPositionByFileId( int fileId)  {
        int position = 0 ;
        for (CourseFileList.CourseFile c : Constant.appData.courseFileList) {
            if( c.getId() == fileId ) {
                return position;
            }
            position++;
        }
        return 0;
    }
}
