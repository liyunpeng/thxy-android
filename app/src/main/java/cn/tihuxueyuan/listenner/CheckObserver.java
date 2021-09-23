package cn.tihuxueyuan.listenner;


import android.util.Log;

import androidx.lifecycle.Lifecycle;

import androidx.lifecycle.LifecycleObserver;

import androidx.lifecycle.OnLifecycleEvent;

import cn.tihuxueyuan.utils.Constant;

/**
 * Chronometer 可以实现自动计数
 */
public class CheckObserver implements LifecycleObserver {
    private static long lastTime;

//    public MyChronometer(Context context, AttributeSet attrs) {
//        super(context, attrs);
//
//    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE) //LifecycleObserver特有的写法
    private void pauseMeter() {
//        lastTime = SystemClock.elapsedRealtime() - getBase();
//
//        stop();

    }

//监听Activity onResume事件
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void resumeMeter() {
//        setBase(SystemClock.elapsedRealtime()- lastTime);
//
//        start();

    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        Log.d(Constant.TAG, "app进入后台 onAppBackgrounded  调用");
//        if (Constant.floatingControl != null) {
//            Constant.floatingControl.setVisibility(false);
//        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        Log.d(Constant.TAG, "app进入前台 onAppForegrounded  调用");
//        if (Constant.floatingControl != null) {
//            Constant.floatingControl.setVisibility(true);
//        }
    }

//        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun onAppBackgrounded() {
//        time = System.currentTimeMillis()
//        Log.d("cxg", "后台 $time")
//    }
//
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onAppForegrounded() {
//        Log.d("cxg", "前台 $time")
//        if (time > 1 && System.currentTimeMillis() - time > 10000) {
//            Log.d("cxg", "check=======")
//        }
//    }

}


//class CheckObserver extends LifecycleObserver {
//    private var time = 0L
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun onAppBackgrounded() {
//        time = System.currentTimeMillis()
//        Log.d("cxg", "后台 $time")
//    }
//
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onAppForegrounded() {
//        Log.d("cxg", "前台 $time")
//        if (time > 1 && System.currentTimeMillis() - time > 10000) {
//            Log.d("cxg", "check=======")
//        }
//    }
//}
