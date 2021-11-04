package cn.tihuxueyuan.utils;

import static android.os.Build.VERSION.SDK_INT;

import android.os.Build;
import android.util.Log;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import cn.tihuxueyuan.db.DBUtils;
import cn.tihuxueyuan.globaldata.AppData;
import cn.tihuxueyuan.service.MusicService;

public class Constant {

    public static String TYPE_SELECTED = "type_selected";
    public static String LAST_TYPE_ID = "last_type_id";
    public static String LAST_TAB_SELECTED_POSITION = "type_selected_position";
    public static boolean HAS_USER = false;  // 是否有用户登录
    public static boolean order;
    public static String version = "1.0";
    public static String MusicLiveDataObserverTag = "music_activity_observer";
    public static String BaseActivityFloatTextViewDataObserverTag = "base_activity_float_observer";
    public static String CourseListLiveDataObserverTag = "course_list_activity_observer";

    public static String FromIntent = "from_intent";
    public static String FloatWindow = "float_window";
    public static String currentMusicName = "456";
    public static AppData appData;

    public static MusicService.MusicControl musicControl;
    public static WindowManager mWM;

    public static UpdateManager updateManager = null;
    public static LogcatHelper logcatHelper;
    public static DBUtils dbUtils;
    public static final String TAG = "thxy";

    public static String MUSIC_ACTIVITY_MODE_NAME = "music_activity_mode_name";       // 启动模式
    public static String LAST_PlAY_MODE_VALUE = "last_mode_value";  // 点上次播放进入音乐播放
    public static String LIST_MODE_VALUE = "list_mode_net_request_value";       //  从列表点击列表项进入音乐播放
    public static String LIST_MODE_LOCAL_VALUE = "list_mode_local_value";       //  从列表点击列表项进入音乐播放


    public static Map<Integer, Integer> downloadingMap = new HashMap<>();
    public static final String CONTINURE_PLAY = "play";
    public static final String NEWPLAY = "new_play";
    public static final String WAITING = "waiting";
//    public static final String NEWPLAY = "new_play";

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
