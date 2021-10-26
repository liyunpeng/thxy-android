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

import cn.tihuxueyuan.activity.MusicActivity;
import cn.tihuxueyuan.floatview.weight.FloatingView;
import cn.tihuxueyuan.livedata.LiveDataBus;
import cn.tihuxueyuan.utils.Constant;

public class BaseActivity extends AppCompatActivity {
    public String customFloatViewText = "123";
    private FloatingView floatingView;
    private LiveDataBus.BusMutableLiveData<String> floatViewLiveData;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        String className = this.getLocalClassName();
//        if ( !className.contains("Music")) {
        floatViewObserver();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (customFloatViewText.contains("123")){
        customFloatViewText = Constant.currentMusicName;
//        }
        String className = this.getLocalClassName();
        Log.d(Constant.TAG, "BaseActivity onResume classname =" + this.getLocalClassName());

        if (null == floatingView) {
            floatingView = new FloatingView(this);
            floatingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), MusicActivity.class);
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
                floatingView.setText(customFloatViewText);
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

    private void floatViewObserver() {
        Log.d(Constant.TAG, " 创建 floatViewObserver ");
        floatViewLiveData = LiveDataBus.getInstance().with(Constant.BaseActivityFloatTextViewDataObserverTag, String.class);
        floatViewLiveData.observe(BaseActivity.this, true, new Observer<String>() {
            @Override
            public void onChanged(String musicTitle) {
                Log.d(Constant.TAG, "悬浮窗Observer = " + musicTitle);
                if (floatingView != null) {
                    customFloatViewText = musicTitle;
                    floatingView.setText(musicTitle);
                    floatingView.refreshDrawableState();
                }
            }
        });
    }
}
