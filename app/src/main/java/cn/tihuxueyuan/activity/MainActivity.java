package cn.tihuxueyuan.activity;

import static android.os.Build.VERSION.SDK_INT;
import static cn.tihuxueyuan.utils.Constant.CLOSE;
import static cn.tihuxueyuan.utils.Constant.NEXT;
import static cn.tihuxueyuan.utils.Constant.CONTINURE_PLAY;
import static cn.tihuxueyuan.utils.Constant.PREV;
import static cn.tihuxueyuan.utils.Constant.TAG;
import static cn.tihuxueyuan.utils.Constant.appData;
import static cn.tihuxueyuan.utils.Constant.dbUtils;
import static cn.tihuxueyuan.utils.Constant.logcatHelper;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import cn.tihuxueyuan.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.List;
import java.util.Stack;

import cn.tihuxueyuan.basic.BaseActivity;
import cn.tihuxueyuan.databinding.ActivityMainBinding;
import cn.tihuxueyuan.db.DBUtils;
import cn.tihuxueyuan.globaldata.AppData;
import cn.tihuxueyuan.http.HttpCallback;
import cn.tihuxueyuan.http.HttpClient;
import cn.tihuxueyuan.model.Config;
import cn.tihuxueyuan.receiver.HomeReceiver;
import cn.tihuxueyuan.receiver.MediaButtonReceiver;
import cn.tihuxueyuan.service.MusicService;
import cn.tihuxueyuan.setting.AppConfig;
import cn.tihuxueyuan.utils.Constant;
import cn.tihuxueyuan.utils.ForegroundCallbacks;
import cn.tihuxueyuan.utils.LogcatHelper;
import cn.tihuxueyuan.utils.SPUtils;

public class MainActivity extends BaseActivity {

    public ActivityMainBinding binding;
    private Context mContext;
    private AudioManager mAudioManager;
    private ComponentName mRemoteControlReceiverComponent;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constant.dbUtils = DBUtils.getInstance(getApplicationContext());

        logcatHelper = LogcatHelper.getInstance(getApplicationContext());
        logcatHelper.start();
        appData = (AppData) getApplication();
        verifyStoragePermissions(this);

        getConfig();
        registerReceiver(headSetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));

        String curProcess = getProcessName(this, Process.myPid());
        if (!TextUtils.equals(curProcess, "cn.tihuxueyuan")) {
            return;
        }
        initAppStatusListener();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();
        setContentView(binding.getRoot());

        mContext = this.getApplicationContext();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
//                .Builder(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .Builder( R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        String appId = "android_" + AppConfig.getVersionName(MainActivity.this) + "_" + AppConfig.getAppMetaData(MainActivity.this, "UMENG_CHANNEL");

        Log.d(TAG, "onCreate: id:" + appId);

        registerMusicReceiver();
        registerHomeKeyReceiver();
        registerHeadsetButtonReceiver();
    }

    private void initAppStatusListener() {
        ForegroundCallbacks.init(getApplication()).addListener(new ForegroundCallbacks.Listener() {
            @Override
            public void onBecameForeground() {
                Log.d(TAG, "onBecameForeground 应用进入前台 ");

//                if (Constant.floatingControl != null) {
//                    Constant.floatingControl.setVisibility(true);
//                }
            }

            @Override
            public void onBecameBackground() {
                Log.d(TAG, "onBecameForeground  应用退至后台 ");

//                if (Constant.floatingControl != null) {
//                    Constant.floatingControl.setVisibility(false);
//                }
            }
        });
    }

    // 进程名
    private String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    private final BroadcastReceiver headSetReceiver = new BroadcastReceiver() {
        @Override

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
                if (intent.getIntExtra("state", 0) == 1) {
                    Log.d(TAG, "耳机检测：插入");
                    if (Constant.musicControl != null) {
                        Constant.musicControl.pause();
                    }
                } else {
                    Log.d(TAG, "耳机检测：没有插入");
                    if (Constant.musicControl != null) {
                        Constant.musicControl.pause();
                    }
                }
            }
        }
    };

    private static final String[] PERMISSION_EXTERNAL_STORAGE = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_EXTERNAL_STORAGE = 100;

    private void verifyStoragePermissions(Activity activity) {
        int permissionWrite = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION_EXTERNAL_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void getConfig() {
        HttpClient.getConfig("", new HttpCallback<Config>() {
            @Override
            public void onSuccess(Config response) {
                if (response == null) {
                    onFail(null);
                    return;
                }
//                Constant.appData.baseUrl = response.getBaseUrl();
                Constant.appData.serviceCurrentVersion = response.getServiceCurrentVersion();
                Constant.appData.serviceCourseTypeUpdateVersion = response.getCourseTypeUpdateVersion();
//                Constant.appData.mp3SourceRouter = response.getMp3SourceRouter();

                Constant.appData.serverConfigId = response.getId();

            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }

    // 含有全部的权限
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (!hasAllPermissionsGranted(grantResults)) {
            Log.d(TAG, "onRequestPermissionsResult return ");
            return;
        }
        switch (requestCode) {
            case 222:
                //todo：  获取到创建目录权限  ， 还放目录用系统自带的document目录， 放在这个地方， 一些初始化的log打印不到
                Log.d(TAG, " onRequestPermissionsResult 响应， 创建log目录");
                Constant.logcatHelper = LogcatHelper.getInstance(getApplicationContext());
                logcatHelper.start();
//                Toast.makeText(getApplicationContext(), "已申请权限", Toast.LENGTH_SHORT).show();
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void getOverlayPermission() {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 100);
            }
        }

        if (SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
    }

    public class MusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, " 音乐播放接收器 MusicReceiver onReceive : " + intent.toString());
            SPUtils.putBoolean(Constant.IS_CHANGE, true, context);
            Constant.musicControl.UIControl(intent.getAction(), TAG);
        }
    }

    private void registerMusicReceiver() {
        Log.d(TAG, "调用 registerMusicReceiver");
        if (appData.musicReceiver == null) {
            Log.d(TAG, " 保证只动态注册一次receiver");
            appData.musicReceiver = new MusicReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(CONTINURE_PLAY);
            intentFilter.addAction(PREV);
            intentFilter.addAction(NEXT);
            intentFilter.addAction(CLOSE);
            registerReceiver(appData.musicReceiver, intentFilter);
        }
    }

    //退出栈顶Activity
    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

//获得当前栈顶Activity

    private static Stack activityStack;

//    private static ScreenManager instance;

    public Activity currentActivity() {
        Activity activity = (Activity) activityStack.lastElement();

        return activity;

    }

//将当前Activity推入栈中

    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack();
        }

        activityStack.add(activity);
    }

    private long lastBackTime = 0;
    private long currentBackTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //捕获返回键按下的事件
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            currentBackTime = System.currentTimeMillis();
            //比较上次按下返回键和当前按下返回键的时间差，如果大于2秒，则提示再按一次退出
            if (currentBackTime - lastBackTime > 2 * 1000) {
                Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                lastBackTime = currentBackTime;
            } else {   //如果两次按下的时间差小于2秒，则退出程序
                unregisterHomeKeyReceiver(this);
                unregisterHeadsetButtonReceiver();

                if (Constant.musicControl != null) {
                    Constant.musicControl.release();
                }

                if (appData.musicReceiver != null) {
                    //解除动态注册的广播
                    unregisterReceiver(appData.musicReceiver);
                }



                Intent intent = new Intent(this, MusicService.class);//创建意图对象
                intent.setPackage(getPackageName());
                Log.i("当前包", getPackageName());
                stopService(intent);

                logcatHelper.stop();

                onDestroy();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//退出栈中所有Activity

    public void popAllActivityExceptOne(Class cls) {

        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            popActivity(activity);
        }
    }

//    private View mView;// 显示的view
//    private WindowManager mWM;//窗口管理者
//    private int startX;//坐标
//    private int startY;//坐标
//    private WindowManager.LayoutParams params;
//
//    public void hide() {
//        if (mView != null) {
//            // note: checking parent() just to make sure the view has
//            // been added... i have seen cases where we get here when
//            // the view isn't yet added, so let's try not to crash.
//            //英文意思就是判断之前有没有add，就是有没有显示过自定义的view
//            if (mView.getParent() != null) {
//                mWM.removeView(mView);
//            }
//            //增加严谨性
//            mView = null;
//        }
//    }
//   因为showToast需要适配很多不同版本的情况， showToast 这个方法费了很
//    //自定义显示的方法，根据需求传入需要的参数，这里我传入一个String。
//    public void showToast(String addr) {
//        // 每次显示之前 先隐藏（确保界面只有一个view）
//        hide();
//        // 要显示的view（可以任意，根据需求来定）
//        mView = View.inflate(context, R.layout.image_display, null);
//        // 显示内容的view（简单的一个展示，数据是我传入的String）
//        TextView tvAddr = (TextView) mView.findViewById(R.id.float_text);
//        tvAddr.setText(addr);
//        // 设置背景（简单的设置背景，其实你可以任意操作）
////        mView.setBackgroundResource(R.drawable.bg_addr_normal_shape);
////        // 设置触摸监听（因为我们一会要拖动他，当然类要实现这个接口）
////        mView.setOnTouchListener(this);
//
//        // 获取窗口管理器
//        // window 窗口 其实我们的activity dialog toast ...都是显示在窗口上
//        mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//
//        // 布局参数 在布局里，以layout_开头的属性都是可以可以在代码里设置
//        //下面代码是从toast源码拷贝出来，并做了修改。有兴趣可以自己去看看
//        params = new WindowManager.LayoutParams();
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE 注释掉 因为我们要实现拖动的功能
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//        params.format = PixelFormat.TRANSLUCENT;
//        // 更改显示级别 需要权限 <uses-permission
//        // android:name="android.permission.SYSTEM_ALERT_WINDOW" />
////        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
////        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        params.type = WindowManager.LayoutParams.TYPE_TOAST;
//        params.setTitle("Toast");
//        // 给窗口添加一个顶级的布局（显示在所有布局之上）（mView我们自己的view）
//        mWM.addView(mView, params);
//    }

    private void registerHomeKeyReceiver() {
        Log.i(TAG, "注册HomeKeyReceiver");
        if (appData.mHomeKeyReceiver == null) {
            Log.i(TAG, "保证只注册一次 home 键的receiver ");
            appData.mHomeKeyReceiver = new HomeReceiver();
            final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            registerReceiver(appData.mHomeKeyReceiver, homeFilter);
        }
    }

    private void registerHeadsetButtonReceiver() {
        Log.i(TAG, "注册耳机事件 的receiver");
        if (appData.mButtonReceiver == null) {
            Log.i(TAG, "保证只注册一次耳机事件 的receiver ");
            appData.mButtonReceiver = new MediaButtonReceiver();
            final IntentFilter homeFilter1 = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
            registerReceiver(appData.mButtonReceiver, homeFilter1);
        }

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mRemoteControlReceiverComponent = new ComponentName(this, MediaButtonReceiver.class);
//        mComponent = new ComponentName(getPackageName(), MediaButtonReceiver.class.getName());  这个是无效的， 试过很多次， 发现这个坑
        mAudioManager.registerMediaButtonEventReceiver(mRemoteControlReceiverComponent);
    }


    private void unregisterHeadsetButtonReceiver() {
        Log.i(TAG, "注销耳机事件 的receiver");
        if (null != mRemoteControlReceiverComponent) {
            mAudioManager.unregisterMediaButtonEventReceiver(mRemoteControlReceiverComponent);
        }

        if (null != appData.mButtonReceiver) {
            this.unregisterReceiver(appData.mButtonReceiver);
        }
    }

    private static void unregisterHomeKeyReceiver(Context context) {
        Log.i(TAG, "注销HomeKeyReceiver");
        if (null != appData.mHomeKeyReceiver) {
            context.unregisterReceiver(appData.mHomeKeyReceiver);


        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, " MainActivity onDestroy");
        super.onDestroy();
        finish();
    }
}