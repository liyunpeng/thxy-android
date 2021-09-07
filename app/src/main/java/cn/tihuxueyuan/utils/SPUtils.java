package cn.tihuxueyuan.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;
import java.util.Map;

import cn.tihuxueyuan.globaldata.AppData;
import cn.tihuxueyuan.model.CourseFileList;

public class SPUtils {
    private static final String NAME = "config";

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

    public static String getMp3Url(String fileName) {
        AppData appData = Constant.appData;
        String mp3Url = appData.baseUrl + appData.mp3SourceRouter + appData.courseStorePath + "?fileName=" + fileName;
        return mp3Url;
    }

    // list 转 map
    public static void listToMap() {
        Map<Integer, CourseFileList.CourseFile> m = Constant.appData.mListMap;
        m.clear();
        for (CourseFileList.CourseFile c : Constant.appData.mList) {
            int i = c.getId();
            m.put(i, c);
        }
    }
}
