package cn.tihuxueyuan.basic;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import cn.tihuxueyuan.activity.Music_Activity;
import cn.tihuxueyuan.floatview.weight.FloatingView;
import cn.tihuxueyuan.livedata.LiveDataBus;
import cn.tihuxueyuan.utils.Constant;

public class BaseActivity extends AppCompatActivity {

    boolean isForeground;
    boolean isRunInBackground;
    int appCount = 0;
    public String customFloatViewText = "123";

    private LiveDataBus.BusMutableLiveData<String> floatViewLiveData;

    private void floatViewObserver() {
        Log.d(Constant.TAG, " 创建了 floatViewObserver ");
        floatViewLiveData = LiveDataBus.getInstance().with(Constant.BaseActivityFloatTextViewDataObserverTag, String.class);
        floatViewLiveData.observe(BaseActivity.this, true, new Observer<String>() {
            @Override
            public void onChanged(String musicTitle) {
                Log.d(Constant.TAG, "floatViewObserver onChanged state= "+ musicTitle);
                if (floatingView != null) {
                    customFloatViewText = musicTitle;
                    floatingView.setText(musicTitle);
                    floatingView.refreshDrawableState();
                }
            }
        });
    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();




//        getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
//            @Override
//            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
//                isForeground = true;
//            }
//
//            @Override
//            public void onActivityStarted(@NonNull Activity activity) {
//                if (isForeground) {
//                    //do nothing
//                } else {
////                    long currentTimeMillis = System.currentTimeMillis();
////                    if ((currentTimeMillis - startSwitchBackgroundTime) > Constants.THREE_MINUTES) {
////                        sendBroadcast(new Intent(Constants.ACTION_VERIFY_PIN));
////                    }
//                }
//
//                appCount++;
//                if (isRunInBackground) {
//                    Log.d(Constant.TAG, "应用从后台回到前台 需要做的操作");
////                    back2App(activity);
//                    isRunInBackground = false;
//
////                    if (Constant.floatingControl != null) {
////                        Constant.floatingControl.setVisibility(true);
////                    }
//                }
//
//            }
//
//            @Override
//            public void onActivityResumed(@NonNull Activity activity) {
//
//            }
//
//            @Override
//            public void onActivityPaused(@NonNull Activity activity) {
//
//            }
//
//            @Override
//            public void onActivityStopped(@NonNull Activity activity) {
//
//                appCount--;
//                if (appCount == 0) {
//                    //
////                    leaveApp(activity);
//                    isRunInBackground = true;
//                    Log.d(Constant.TAG, "应用进入后台 需要做的操作");
////                    if (Constant.floatingControl != null) {
////                        Constant.floatingControl.setVisibility(false);
////                    }
//                }
//
//
////                if (UIUtils.isBackground(BaseApplication.this)) {
////                    isForeground = false;
////                    startSwitchBackgroundTime = System.currentTimeMillis();
////                }
//            }
//
//            @Override
//            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
//
//            }
//
//            @Override
//            public void onActivityDestroyed(@NonNull Activity activity) {
//
//            }
//        });
        String className = this.getLocalClassName();
        if ( !className.contains("Music")) {
            floatViewObserver();
        }
    }


    private int numberMask;
    FloatingView floatingView;
    @Override
    protected void onResume() {
        super.onResume();
        String className = this.getLocalClassName();
        Log.d(Constant.TAG, "BaseActivity onResume classname =" + this.getLocalClassName());
//        if (Constant.floatingControl != null) {
//            if (className.contains("Music")){
//                Constant.floatingControl.setVisibility(false);
//            }else{
//                Constant.floatingControl.setVisibility(true);
//            }
//        }


        if (null == floatingView) {
            floatingView = new FloatingView(this);
            floatingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), Music_Activity.class);
                    intent.putExtra(Constant.FromIntent, Constant.FloatWindow);
                    intent.putExtra("float_text", customFloatViewText);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

            if (!isFinishing() && !className.contains("Music")) {
                floatingView.showFloat();
            }
        }

        if (className.contains("Music")) {
            Log.d(Constant.TAG, " 当前是音乐播放界面， 不显示悬浮窗");
            // 当前是音乐播放界面， 不显示悬浮窗
            floatingView.setVisibility(View.GONE);
        } else {
            if (Constant.musicControl != null) {
                //  Constant.musicControl 不空，说明在活跃状态
                Log.d(Constant.TAG, " Constant.musicControl 不空，说明在活跃状态， 显示悬浮窗, text=" + customFloatViewText);
//                floatingView.setText(customFloatViewText);
                floatingView.setVisibility(View.VISIBLE);
            } else {
                Log.d(Constant.TAG, " Constant.musicControl 为空，说明不在活跃状态， 不显示悬浮窗");
                floatingView.setVisibility(View.GONE);
            }

//            Glide.with(getApplicationContext()).load("https://p26-tt.byteimg.com/origin/pgc-image/6ffb4c43c75749dd9af820028871242b")
//                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
//                    .into(floatingView.CircleImageView());
        }

    }

    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Constant.TAG, " onDestroy className =" + this.getLocalClassName());
    }

    @Override
    protected void onStop() {
        super.onStop();
//        String className = this.getLocalClassName();
//        if (Constant.floatingControl != null) {
//            if (className.contains("Music")){
//                Constant.floatingControl.setVisibility(false);
//            }else{
//                Constant.floatingControl.setVisibility(true);
//            }
//        }
    }
}
