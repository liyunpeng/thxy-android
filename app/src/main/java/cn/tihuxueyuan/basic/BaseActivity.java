package cn.tihuxueyuan.basic;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import cn.tihuxueyuan.utils.Constant;

public class BaseActivity extends AppCompatActivity {

    boolean isForeground;
    boolean isRunInBackground;
    int appCount = 0;
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
    }
    @Override
    protected void onResume() {
        super.onResume();
        String className = this.getLocalClassName();
        Log.d(Constant.TAG, " name ="+ this.getLocalClassName());
        if (Constant.floatingControl != null) {
            if (className.contains("Music")){
                Constant.floatingControl.setVisibility(false);
            }else{
                Constant.floatingControl.setVisibility(true);
            }
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
        Log.d(Constant.TAG, " onDestroy className ="+ this.getLocalClassName());
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
