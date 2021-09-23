package cn.tihuxueyuan.basic;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import cn.tihuxueyuan.floatview.weight.FloatingView;
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

    private FloatingView floatingView;
    private int numberMask;

    @Override
    protected void onResume() {
        super.onResume();
        String className = this.getLocalClassName();
        Log.d(Constant.TAG, " name ="+ this.getLocalClassName());
//        if (Constant.floatingControl != null) {
//            if (className.contains("Music")){
//                Constant.floatingControl.setVisibility(false);
//            }else{
//                Constant.floatingControl.setVisibility(true);
//            }
//        }

        if (null == floatingView) {
            floatingView = new FloatingView(this);
            floatingView.showFloat();
//            floatingView.setVisibility(View.INVISIBLE);
            floatingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    numberMask++;
                    if (numberMask == 3) {
                        Toast.makeText(BaseActivity.this, "恭喜你发现了隐藏页面！", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(BaseActivity.this, EggActivity.class));
                    } else {
                        Toast.makeText(BaseActivity.this, "你点击了", Toast.LENGTH_SHORT).show();
                    }

                }
            });



                if (className.contains("Music")){
                    floatingView.setVisibility(View.INVISIBLE);
                }else{
                    floatingView.setVisibility(View.VISIBLE);
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
