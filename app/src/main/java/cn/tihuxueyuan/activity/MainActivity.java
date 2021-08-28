package cn.tihuxueyuan.activity;

import static cn.tihuxueyuan.utils.Constant.CLOSE;
import static cn.tihuxueyuan.utils.Constant.NEXT;
import static cn.tihuxueyuan.utils.Constant.PLAY;
import static cn.tihuxueyuan.utils.Constant.PREV;
import static cn.tihuxueyuan.utils.Constant.TAG;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import cn.tihuxueyuan.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Stack;

import cn.tihuxueyuan.basic.BaseActivity;
import cn.tihuxueyuan.databinding.ActivityMainBinding;
import cn.tihuxueyuan.livedata.LiveDataBus;
import cn.tihuxueyuan.receiver.HomeReceiver;
import cn.tihuxueyuan.service.FloatingImageDisplayService;
import cn.tihuxueyuan.service.MusicService;
import cn.tihuxueyuan.setting.AppConfig;
import cn.tihuxueyuan.utils.Constant;
import cn.tihuxueyuan.utils.SPUtils;

public class MainActivity extends BaseActivity {

    public ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 100);
            }
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        String appId = "android_" + AppConfig.getVersionName(MainActivity.this) + "_" + AppConfig.getAppMetaData(MainActivity.this, "UMENG_CHANNEL");

        Log.d(Constant.TAG, "onCreate: id:" + appId);
        registerMusicReceiver();

        notificationObserver();
    }

    public static MusicReceiver musicReceiver;

    public class MusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "MusicReceiver onReceive : " + intent.toString());
            SPUtils.putBoolean(Constant.IS_CHANGE, true, context);
            Constant.musicControl.UIControl(intent.getAction(), TAG);
        }
    }

    /**
     * 注册动态广播
     * 动态广播不能放在服务里， 服务在ondestory后，服务就在存在了， 这是musicReceiver new 的服务 就会报错
     */
    private void registerMusicReceiver() {
        musicReceiver = new MusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PLAY);
        intentFilter.addAction(PREV);
        intentFilter.addAction(NEXT);
        intentFilter.addAction(CLOSE);
        registerReceiver(musicReceiver, intentFilter);
    }


    private LiveDataBus.BusMutableLiveData<String> activityLiveData;

    /**
     * 通知栏动作观察者
     */
    private void notificationObserver() {
        activityLiveData = LiveDataBus.getInstance().with("float_control", String.class);
        activityLiveData.observe(MainActivity.this, true, new Observer<String>() {
            @Override
            public void onChanged(String state) {
                Log.d("tag2", "float onChanged state = " + state);
                Constant.floatingControl.setText("aaaaaa");
                switch (state) {

                    default:
                        break;
                }

            }
        });
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

    //上次按下返回键的系统时间
    private long lastBackTime = 0;
    //当前按下返回键的系统时间
    private long currentBackTime = 0;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //捕获返回键按下的事件
        if(keyCode == KeyEvent.KEYCODE_BACK){
            //获取当前系统时间的毫秒数
            currentBackTime = System.currentTimeMillis();
            //比较上次按下返回键和当前按下返回键的时间差，如果大于2秒，则提示再按一次退出
            if(currentBackTime - lastBackTime > 2 * 1000){
                Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                lastBackTime = currentBackTime;
            }else{  //如果两次按下的时间差小于2秒，则退出程序
//                MyApplication.getInstance().exit();
                if ( Constant.floatingControl != null) {
                    Constant.floatingControl.remove();
                }
                if (Constant.musicControl != null) {
                    Constant.musicControl.release();
                }

                if (musicReceiver != null) {
                    //解除动态注册的广播
                    unregisterReceiver(musicReceiver);
                }

                Intent intent = new Intent(this, FloatingImageDisplayService.class);//创建意图对象
                intent.setPackage(getPackageName());
                //Log.i("当前包", getPackageName());
                stopService(intent);

                intent = new Intent(this, MusicService.class);//创建意图对象
                intent.setPackage(getPackageName());
                //Log.i("当前包", getPackageName());
                stopService(intent);
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

    private static HomeReceiver mHomeKeyReceiver = null;

    private static void registerHomeKeyReceiver(Context context) {
        Log.i(TAG, "registerHomeKeyReceiver");
        mHomeKeyReceiver = new HomeReceiver();
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        context.registerReceiver(mHomeKeyReceiver, homeFilter);
    }

    private static void unregisterHomeKeyReceiver(Context context) {
        Log.i(TAG, "unregisterHomeKeyReceiver");
        if (null != mHomeKeyReceiver) {
            context.unregisterReceiver(mHomeKeyReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerHomeKeyReceiver(this);
    }

    @Override
    protected void onDestroy() {
        unregisterHomeKeyReceiver(this);
        super.onDestroy();

    }
}