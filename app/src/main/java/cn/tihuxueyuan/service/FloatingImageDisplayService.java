package cn.tihuxueyuan.service;

import static android.os.Build.VERSION.SDK_INT;
import static cn.tihuxueyuan.utils.Constant.CLOSE;
import static cn.tihuxueyuan.utils.Constant.NEXT;
import static cn.tihuxueyuan.utils.Constant.PLAY;
import static cn.tihuxueyuan.utils.Constant.PREV;
import static cn.tihuxueyuan.utils.Constant.TAG;
import static cn.tihuxueyuan.utils.Constant.bootstrapReflect;
//import static cn.tihuxueyuan.utils.Constant.musicReceiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import cn.tihuxueyuan.R;
import cn.tihuxueyuan.activity.Music_Activity;
import cn.tihuxueyuan.utils.Constant;
import cn.tihuxueyuan.utils.SPUtils;


public class FloatingImageDisplayService extends Service {
    public static boolean isStarted = false;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private View displayView;

    private int[] images;
    private int imageIndex = 0;

    private Handler changeImageHandler;







    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 500;
        layoutParams.height = 500;
        layoutParams.x = 300;
        layoutParams.y = 300;

        images = new int[]{
                R.drawable.image_01,
                R.drawable.image_02,
        };

        changeImageHandler = new Handler(this.getMainLooper(), changeImageCallback);


    }


    @Override
    public IBinder onBind(Intent intent) {
        return new FloatingImageDisplayService.FloatingControl();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Constant.TAG, " 悬浮窗服务 onStartCommand  ");
        bootstrapReflect();
        return super.onStartCommand(intent, flags, startId);
    }

    public class FloatingControl extends Binder { //Binder是一种跨进程的通信方式

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void initFloatingWindow() {
//        if (Settings.canDrawOverlays(this)) {

            LayoutInflater layoutInflater = LayoutInflater.from(FloatingImageDisplayService.this);
            displayView = layoutInflater.inflate(R.layout.image_display, null);
            displayView.setOnTouchListener(new FloatingOnTouchListener());
//            ImageView imageView = displayView.findViewById(R.id.image_display_imageview);
//            imageView.setImageResource(images[imageIndex]);

            TextView textView = displayView.findViewById(R.id.float_text);
            textView.setText("12345");

            textView.setBackgroundResource (R.drawable.shape);
            windowManager.addView(displayView, layoutParams);
            displayView.setVisibility(View.GONE);

//        changeImageHandler.sendEmptyMessageDelayed(0, 2000);
//        }
        }

        public void setVisibility(boolean visible) {
            if (visible == true) {
                displayView.setVisibility(View.VISIBLE);
            } else {
                displayView.setVisibility(View.GONE);
            }
        }
    }

    private Handler.Callback changeImageCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                Log.d(Constant.TAG, "handleMessage: " + msg);
                //获取栈顶的Activity
//                Activity currentActivity = ActivityManager.getCurrentActivity();
//                Intent intent = new Intent(Intent.ACTION_MAIN);
////                intent.addCategory(Intent.CATEGORY_LAUNCHER);
////                intent.setClass(context, currentActivity.getClass());
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                startActivity(intent);
//                imageIndex++;
//                if (imageIndex >= 5) {
//                    imageIndex = 0;
//                }
//                if (displayView != null) {
//                    ((ImageView) displayView.findViewById(R.id.image_display_imageview)).setImageResource(images[imageIndex]);
//                }
//
//                changeImageHandler.sendEmptyMessageDelayed(0, 2000);

//                Intent intent = new Intent(getApplicationContext(), Music_Activity.class);//创建Intent对象，启动check
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //将数据存入Intent对象
//                player.setDataSource("http://47.102.146.8:8082/api/fileDownload?fileName=一声佛号一声心.mp3");
//                String musicUrl = mList.get(position).getMp3url() + "?fileName=" + mList.get(position).getMp3FileName();
//                intent.putExtra("music_url", musicUrl);
//                intent.putExtra("current_position", position);
//                String a[] = mList.get(position).getTitle().split("\\.");
//                intent.putExtra("title", a[0]);
//                intent.putExtra("position",String.valueOf(position));
                // ，
//                startActivity(intent);
            }
            return false;
        }
    };

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    Intent intent = new Intent(getApplicationContext(), Music_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}
