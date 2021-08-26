package cn.tihuxueyuan.utils;

import static android.os.Build.VERSION.SDK_INT;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

import cn.tihuxueyuan.activity.Music_Activity;
import cn.tihuxueyuan.service.FloatingImageDisplayService;
import cn.tihuxueyuan.service.MusicService;

public class Constant {

    public static Music_Activity.MyServiceConn conn1;
    private String name;
    public static Intent intent1, intent2;

    public static FloatingImageDisplayService.FloatingControl floatingControl;
    public static MusicService.MusicControl musicControl;

    public static final String TAG = "thxy";
    /**
     * 歌曲播放
     */
    public static final String PLAY = "play";
    /**
     * 歌曲暂停
     */
    public static final String PAUSE = "pause";
    /**
     * 上一曲
     */
    public static final String PREV = "prev";
    /**
     * 下一曲
     */
    public static final String NEXT = "next";


    /**
     * 关闭通知栏
     */
    public static final String CLOSE = "close";
    /**
     * 进度变化
     */
    public static final String PROGRESS = "progress";

    /**
     * 用于判断当前滑动歌名改变的通知栏播放状态
     */
    public static final String IS_CHANGE = "isChange";

    public static void bootstrapReflect() {
        if (SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        try {
            Method forName = Class.class.getDeclaredMethod("forName", String.class);
            Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
            Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null, "dalvik.system.VMRuntime");
            Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null);
            Method setHiddenApiExemptions = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
            Object sVmRuntime = getRuntime.invoke(null);
            setHiddenApiExemptions.invoke(sVmRuntime, new Object[]{new String[]{"L"}});
        } catch (Throwable e) {
            Log.e(Constant.TAG, "reflect bootstrap failed:", e);
        }
    }
}
