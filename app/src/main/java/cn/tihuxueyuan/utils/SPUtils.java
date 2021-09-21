package cn.tihuxueyuan.utils;

import static cn.tihuxueyuan.utils.Constant.TAG;
import static cn.tihuxueyuan.utils.Constant.musicControl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.tihuxueyuan.globaldata.AppData;
import cn.tihuxueyuan.http.JsonPost;
import cn.tihuxueyuan.model.CourseFileList;

public class SPUtils {
    private static final String NAME = "config";

    public static void sendListenedPerscent() {
        JsonPost.ListenedFile listenedFile = new JsonPost.ListenedFile();

        // 从其他列表课程的悬浮穿 进入音乐播放界面，currentPostion 大于了Constant.appData.courseFileList.size， 导致报错
        // todo : 考虑在什么适合时间 调用用sendListenedPerscent， 现在都是在音乐播放页面 的onstop调用
        if  (  Constant.appData.courseFileList.size() <= Constant.appData.currentPostion ) {
            Log.d(TAG, " sendListenedPerscent 出错，原因：Constant.appData.courseFileList.size() <= Constant.appData.currentPostion ");
            return;
        }

        listenedFile.CourseFileId = Constant.appData.courseFileList.get(Constant.appData.currentPostion).getId();
        listenedFile.ListenedPercent = musicControl.getListenedPercent();
        listenedFile.Position = musicControl.getPosition();
        Log.d(TAG, "调jsonpost网络接口， 写入已听数据" +
                ", 文件名= " + Constant.appData.courseFileList.get(Constant.appData.currentPostion).getFileName() +
                ", CourseFileId= " + listenedFile.CourseFileId +
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

//    public static void f() {
//        // 获得缓存文件路径，磁盘空间不足或清除缓存时数据会被删掉，一般存放一些临时文件
//// /data/data/<application package>/cache目录
//        File cacheDir = getCacheDir();
//        Log.d("TAG", "getCacheDir() : " + cacheDir.getAbsolutePath());
//
//// 获得文件存放路径，一般存放一些需要长期保留的文件
//// /data/data/<application package>/files目录
//        File fileDir = getFilesDir();
//        Log.d("TAG", "getFilesDir() : " + fileDir.getAbsolutePath());
//
//// 这是一个可以存放你自己应用程序自定义的文件，你可以通过该方法返回的File实例来创建或者访问这个目录
//// /data/data/<application package>/
//        File dir = getDir("fileName", MODE_PRIVATE);
//        Log.d("TAG", "getDir() : " + dir.getAbsolutePath());
//
//// 获取应用程序外部存储的缓存目录路径
//// SDCard/Android/data/<application package>/cache目录
//        File externalCacheDir = getExternalCacheDir();
//        Log.d("TAG", "getExternalCacheDir() : " + externalCacheDir.getAbsolutePath());
//
//// 获取应用程序外部存储的某一类型的文件目录，
//// SDCard/Android/data/<application package>/files目录
//// 这里的类型有
//// Environment.DIRECTORY_MUSIC音乐
//// Environment.DIRECTORY_PODCASTS 音频
//// Environment.DIRECTORY_RINGTONES 铃声
//// Environment.DIRECTORY_ALARMS 闹铃
//// Environment.DIRECTORY_NOTIFICATIONS 通知铃声
//// Environment.DIRECTORY_PICTURES 图片
//// Environment.DIRECTORY_MOVIES 视频
//        File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
//        Log.d("TAG", "getExternalFilesDir() : " + externalFilesDir.getAbsolutePath());
//
//// 获取应用的外部存储的缓存目录
//        File[] externalCacheDirs = getExternalCacheDirs();
//        for (int i = 0; i < externalCacheDirs.length; i++) {
//            Log.d("TAG", "getExternalCacheDirs() " + i + " : " + externalCacheDirs[i].getAbsolutePath());
//        }
//
//// 获取应用的外部存储的某一类型的文件目录
//        File[] externalFilesDirs = getExternalFilesDirs(Environment.DIRECTORY_MUSIC);
//        for (int i = 0; i < externalFilesDirs.length; i++) {
//            Log.d("TAG", "getExternalFilesDirs() " + i + " : " + externalFilesDirs[i].getAbsolutePath());
//        }
//
//// 获取应用的外部媒体文件目录
//        File[] externalMediaDirs = getExternalMediaDirs();
//        for (int i = 0; i < externalMediaDirs.length; i++) {
//            Log.d("TAG", "getExternalMediaDirs() " + i + " : " + externalMediaDirs[i].getAbsolutePath());
//        }
//
//// 获得应用程序指定数据库的绝对路径
//// /data/data/<application package>/database/database.db目录
//        File databasePath = getDatabasePath("database.db");
//        Log.d("TAG", "getDatabasePath() : " + databasePath.getAbsolutePath());
//
//
//// -------------分界线-----------------------
//// 以下是一些共有的目录，与APP包名无关，不会随APP卸载被删除
//// /data目录
//        File dataDirectory = Environment.getDataDirectory();
//        Log.d("TAG", "Environment.getDataDirectory() : " + dataDirectory.getAbsolutePath());
//// /cache目录
//        File downloadCacheDirectory = Environment.getDownloadCacheDirectory();
//        Log.d("TAG", "Environment.getDownloadCacheDirectory() : " + downloadCacheDirectory.getAbsolutePath());
//// /sdcard目录
//        File externalStorageDirectory = Environment.getExternalStorageDirectory();
//        Log.d("TAG", "Environment.getExternalStorageDirectory() : " + externalStorageDirectory.getAbsolutePath());
//// /sdcard/Pictures目录
//        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        Log.d("TAG", "Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) : " + externalStoragePublicDirectory.getAbsolutePath());
//// /system目录
//        File rootDirectory = Environment.getRootDirectory();
//        Log.d("TAG", "Environment.getRootDirectory()() : " + rootDirectory.getAbsolutePath());
//    }
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
