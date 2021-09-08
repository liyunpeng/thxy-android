package cn.tihuxueyuan.activity;

import static java.lang.Integer.parseInt;

import static cn.tihuxueyuan.utils.Constant.NEWPLAY;
import static cn.tihuxueyuan.utils.Constant.PAUSE;
import static cn.tihuxueyuan.utils.Constant.PLAY;
import static cn.tihuxueyuan.utils.Constant.TAG;
import static cn.tihuxueyuan.utils.Constant.floatingControl;
import static cn.tihuxueyuan.utils.Constant.musicControl;
//import static cn.tihuxueyuan.utils.Constant.musicReceiver;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;

import cn.tihuxueyuan.R;
import cn.tihuxueyuan.basic.ActivityManager;
import cn.tihuxueyuan.basic.BaseActivity;
import cn.tihuxueyuan.globaldata.AppData;
import cn.tihuxueyuan.livedata.LiveDataBus;
import cn.tihuxueyuan.service.FloatingImageDisplayService;
import cn.tihuxueyuan.service.MusicService;
import cn.tihuxueyuan.utils.Constant;
import cn.tihuxueyuan.utils.SPUtils;

public class Music_Activity extends BaseActivity implements View.OnClickListener {
    private static SeekBar seekBar;
    private static TextView tv_progress, tv_total, name_song;
    private ImageView playPauseView;
    private ObjectAnimator animator;
    private String musicTitle;
    private Intent intent3;
    private MyServiceConn conn1;
    private String name;
    private String musicUrl;
    private boolean isUnbind = false; //记录服务是否被解绑
    private AppData appData;
    private LiveDataBus.BusMutableLiveData<String> musicActivityLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        musicUrl = getIntent().getStringExtra("music_url");
        Log.d(Constant.TAG, "Music Activity oncreate  musicUrl= " + musicUrl);
        boolean isNew = getIntent().getBooleanExtra("is_new", false);
        appData = (AppData) getApplication();

        appData.currentMusicCourseId = appData.courseFileList.get(0).getCourseId();
        //通知栏的观察者
        musicActivityObserver();
        //控制通知栏
//        notificationLiveData = LiveDataBus.getInstance().with("notification_control", String.class);
//        floatLiveData = LiveDataBus.getInstance().with("notification_control", String.class);
        init();
        if (isNew == true) {
//            startService(new Intent(Music_Activity.this, FloatingImageDisplayService.class));
            appData.currentPostion = getIntent().getIntExtra("current_position", 0);
            musicTitle = getIntent().getStringExtra("title");

            appData.currentCourseFileId = appData.courseFileList.get(appData.currentPostion).getId();

            bindMusicService();

        } else {
//            bootstrapReflect();
            musicTitle = SPUtils.getTitleFromName(appData.courseFileList.get(appData.currentPostion).getFileName());
            musicControl.setText();
            if (musicControl.isPlaying()) {
                playPauseView.setImageResource(R.drawable.pause_dark);
            } else {
                playPauseView.setImageResource(R.drawable.play_dark);
            }
        }

        name_song.setText(musicTitle);
        if (Constant.floatingControl != null) {
            Constant.floatingControl.setText(musicTitle);
        }

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && Settings.canDrawOverlays(getApplicationContext()))
//            getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);

//        floatLiveData = LiveDataBus.getInstance().with("float_control", String.class);
//        floatLiveData.postValue(PLAY);
    }


    @Override
    protected void onStop() {
        super.onStop();
        SPUtils.sendListenedPerscent();
    }

    private void bindMusicService() {
        if (musicControl == null) {
            Constant.intent2 = new Intent(this, MusicService.class);//创建意图对象
            Constant.conn1 = new MyServiceConn();
            bindService(Constant.intent2, Constant.conn1, BIND_AUTO_CREATE); //绑定服务
        } else {
            musicControl.init(musicUrl);
            musicControl.playListened(PLAY);

        }

        if (floatingControl == null) {
            intent3 = new Intent(this, FloatingImageDisplayService.class);//创建意图对象
            conn1 = new MyServiceConn();
            bindService(intent3, conn1, BIND_AUTO_CREATE);  //绑定服务
        }
    }

    @Override
    protected void onStart() {
        Log.e("====", "onStart()");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.e("====", "onRestart()");
        super.onRestart();
    }

    private void init() {
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        tv_total = (TextView) findViewById(R.id.tv_total);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        name_song = (TextView) findViewById(R.id.song_name);
        playPauseView = findViewById(R.id.play_pause);
        playPauseView.setOnClickListener(this);
        findViewById(R.id.play_previous).setOnClickListener(this);
        findViewById(R.id.play_next).setOnClickListener(this);

        //为滑动条添加事件监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //进度条改变时，会调用此方法
//                if (progress == seekBar.getMax()) {//当滑动条到末端时，结束动画
//                    animator.pause();//停止播放动画
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {//滑动条开始滑动时调用
//                int progress = seekBar.getProgress();//获取seekBar的进度
//                musicControl.seekTo(progress);//改变播放进度
//                int progress = seekBar.getProgress();//获取seekBar的进度
//                musicControl.setText();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {//滑动条停止滑动时调用
                //根据拖动的进度改变音乐播放进度
                int progress = seekBar.getProgress();//获取seekBar的进度
                Log.d(TAG, " 改变播放进度 progress=" + progress);

                musicControl.seekTo(progress);//改变播放进度
                if (musicControl.isPlaying() != true) {
                    musicControl.playOrPause();
                    playPauseView.setImageResource(R.drawable.stop);
                }
            }
        });

//        ImageView iv_music = (ImageView) findViewById(R.id.iv_music);
////        String position= intent1.getStringExtra("position");
//        String position = "1";
//        int i = parseInt(position);
//        iv_music.setImageResource(ListFragment.icons[i]);

        /*
        animator = ObjectAnimator.ofFloat(iv_music, "rotation", 0f, 360.0f);
        animator.setDuration(10000);//动画旋转一周的时间为10秒
        animator.setInterpolator(new LinearInterpolator());//匀速
        animator.setRepeatCount(-1);//-1表示设置动画无限循环

         */
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityManager.setCurrentActivity(Music_Activity.this);
    }

    public static Handler handler = new Handler() {//创建消息处理器对象
        //在主线程中处理从子线程发送过来的消息
        @Override
        public void handleMessage(Message msg) {
//            Log.d("tag1", "handleMessage: 处理消息 ： " + msg.toString());
            Bundle bundle = msg.getData();//获取从子线程发送过来的音乐播放进度
            int duration = bundle.getInt("duration");
            int currentPosition = bundle.getInt("currentPosition");
            seekBar.setMax(duration);
            seekBar.setProgress(currentPosition);
            //歌曲总时长
            int minute = duration / 1000 / 60;
            int second = duration / 1000 % 60;
            String strMinute = null;
            String strSecond = null;
            if (minute < 10) {//如果歌曲的时间中的分钟小于10
                strMinute = "0" + minute;//在分钟的前面加一个0
            } else {
                strMinute = minute + "";
            }
            if (second < 10) {//如果歌曲中的秒钟小于10
                strSecond = "0" + second;//在秒钟前面加一个0
            } else {
                strSecond = second + "";
            }
            tv_total.setText(strMinute + ":" + strSecond);
            //歌曲当前播放时长
            minute = currentPosition / 1000 / 60;
            second = currentPosition / 1000 % 60;
            if (minute < 10) {//如果歌曲的时间中的分钟小于10
                strMinute = "0" + minute;//在分钟的前面加一个0
            } else {
                strMinute = minute + " ";
            }
            if (second < 10) {//如果歌曲中的秒钟小于10
                strSecond = "0" + second;//在秒钟前面加一个0
            } else {
                strSecond = second + " ";
            }
            tv_progress.setText(strMinute + ":" + strSecond);
        }
    };

    private void musicActivityObserver() {
        musicActivityLiveData = LiveDataBus.getInstance().with(Constant.MusicLiveDataObserverTag, String.class);
        musicActivityLiveData.observe(Music_Activity.this, true, new Observer<String>() {
            @Override
            public void onChanged(String state) {
                Log.d(TAG, " 观察者监控到消息 = " + state);
                switch (state) {
                    case PAUSE:
                        playPauseView.setImageResource(R.drawable.play_dark);
                        break;
                    case PLAY:
                        playPauseView.setImageResource(R.drawable.pause_dark);
                        break;
                    case NEWPLAY:
                        musicTitle = SPUtils.getTitleFromName(appData.courseFileList.get(appData.currentPostion).getFileName());
                        name_song.setText(musicTitle);
                        floatingControl.setText(musicTitle);
                        break;
//                    case CLOSE:
//                        btnPlay.setIcon(getDrawable(R.mipmap.icon_play));
//                        btnPlay.setIconTint(getColorStateList(R.color.white));
//                        changeUI(musicService.getPlayPosition());
//                        break;
//                    case PREV:
//                        BLog.d(TAG, "上一曲");
//                        changeUI(musicService.getPlayPosition());
//                        break;
//                    case NEXT:
//                        BLog.d(TAG, "下一曲");
//                        changeUI(musicService.getPlayPosition());
//                        break;
//                    case PROGRESS:
//                        //播放进度发生改变时,只改变进度，不改变其他
//                        musicProgress.setProgress(musicService.mediaPlayer.getCurrentPosition(), musicService.mediaPlayer.getDuration());
//                        break;
                    default:
                        break;
                }

            }
        });
    }

    public void startFloatingImageDisplayService() {
        if (FloatingImageDisplayService.isStarted) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!Settings.canDrawOverlays(this)) {
//                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
//                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 1);
//            } else {
            startService(new Intent(Music_Activity.this, FloatingImageDisplayService.class));
//            }
        }
    }

    public class MyServiceConn implements ServiceConnection {//用于实现连接服务

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: 服务连接成功, serrvice 类名：" + name.getShortClassName());
            String shortClassName = name.getShortClassName();
            if (shortClassName.contains("MusicService")) {
                musicControl = (MusicService.MusicControl) service;
                musicControl.init(musicUrl);
                musicControl.playListened(PLAY);

                Log.d(Constant.TAG, "musicControl 初始化完成 ");
            } else if (shortClassName.contains("FloatingImageDisplayService")) {
                Constant.floatingControl = (FloatingImageDisplayService.FloatingControl) service;
                Constant.floatingControl.initFloatingWindow();
                Constant.floatingControl.setVisibility(false);
                Constant.floatingControl.setText(musicTitle);
                Log.d(Constant.TAG, "floatingControl 初始化完成");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private void unbind(boolean isUnbind) {
        if (!isUnbind) {//判断服务是否被解绑
            unbindService(Constant.conn1);//解绑服务
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_pause:
                musicControl.playOrPause();
                break;
            case R.id.play_previous:
                if (appData.currentPostion <= 0) {
                    appData.currentPostion = 0;
                } else {
                    appData.currentPostion--;
                    appData.currentCourseFileId = appData.courseFileList.get(appData.currentPostion).getId();
                    musicControl.playListened(NEWPLAY );
                }
                break;
            case R.id.play_next:
                if (appData.currentPostion >= (appData.courseFileList.size() - 1)) {
                    appData.currentPostion = (appData.courseFileList.size() - 1);
                } else {
                    appData.currentPostion++;
                    appData.currentCourseFileId = appData.courseFileList.get(appData.currentPostion).getId();
                    musicControl.playListened(NEWPLAY);
                }
                break;

//            case R.id.btn_pause://暂停按钮点击事件
//
//                break;
//            case R.id.btn_continue_play://继续播放按钮点击事件
//                musicControl.continuePlay();
//                animator.start();
//                break;
//            case R.id.btn_exit://退出按钮点击事件
//                unbind(isUnbind);
//                isUnbind = true;
//                finish();
//                break;
        }

//        musicControl.updateNotify(app.currentPostion);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Constant.TAG, " Music activity onDestroy");
//        unbind(isUnbind);
//        isUnbind = true;
//        finish();
    }

//    private void playNextPrevious() {
//
//        CourseFileList.CourseFile c = app.mList.get(app.currentPostion);
//        String musicUrl = c.getMp3url() + "?fileName=" + c.getMp3FileName();
//        musicControl.init(musicUrl);
//
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                    musicControl.setText();
//                    musicControl.play();
////                        animator.start();
//                    playPauseView.setImageResource(R.drawable.stop);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
}


