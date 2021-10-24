package cn.tihuxueyuan.activity;

import static cn.tihuxueyuan.utils.Constant.NEWPLAY;
import static cn.tihuxueyuan.utils.Constant.PAUSE;
import static cn.tihuxueyuan.utils.Constant.CONTINURE_PLAY;
import static cn.tihuxueyuan.utils.Constant.TAG;
import static cn.tihuxueyuan.utils.Constant.floatingControl;
import static cn.tihuxueyuan.utils.Constant.musicControl;

import android.animation.ObjectAnimator;
import android.app.Activity;
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
import android.view.WindowManager;
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
import cn.tihuxueyuan.model.ListenedFile;
import cn.tihuxueyuan.service.FloatingImageDisplayService;
import cn.tihuxueyuan.service.MusicService;
import cn.tihuxueyuan.utils.Constant;
import cn.tihuxueyuan.utils.SPUtils;

public class MusicActivity extends BaseActivity implements View.OnClickListener {
    private static SeekBar seekBar;
    private static TextView mProgressTextView;
    private static TextView mTotalTextView;
    private static TextView mTitleTextView;
    private ImageView mPlayPauseView;
    private ObjectAnimator animator;

    private boolean isUnbind = false; //记录服务是否被解绑
    private String mMusicTitle;
    private String mMusicUrl;
    private String mMode;

    private static String mTotalDurationText;
    private static String mProgressText;
    private Intent intent3;
    private AppData appData;
    private MusiceServiceConnection mServiceConnection;
    private LiveDataBus.BusMutableLiveData<String> mMusicActivityLiveData;
    private static LiveDataBus.BusMutableLiveData<ListenedFile> mCourseListActivityLiveData;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCourseListActivityLiveData = LiveDataBus.getInstance().with(Constant.CourseListLiveDataObserverTag, ListenedFile.class);
//        if (isBluetoothA2dpOn()) {
//
//// Adjust output for Bluetooth.
//
//        } else if (isSpeakerphoneOn()) {
//
//// Adjust output for Speakerphone.
//
//        } else if (isWiredHeadsetOn()) {
//
//// Adjust output for headsets
//
//        } else {
//
//// If audio plays and noone can hear it, is it still playing?
//
//        }

//        //获得AudioManager对象
//        AudioManager mAudioManager =(AudioManager) getSystemService(Context.AUDIO_SERVICE);
//
////构造一个ComponentName，指向MediaoButtonReceiver类
//        ComponentName  mComponent = new ComponentName(getPackageName(), MediaButtonReceiver.class.getName());
//
////注册一个MediaButtonReceiver广播监听
//        mAudioManager.registerMediaButtonEventReceiver(mComponent);
//
////注销方法
//        mAudioManager.unregisterMediaButtonEventReceiver(mComponent);
//
        appData = Constant.appData;

        setContentView(R.layout.activity_music);
        mMusicUrl = getIntent().getStringExtra("music_url");
        Log.d(TAG, "Music Activity oncreate  musicUrl= " + mMusicUrl);
        mMode = getIntent().getStringExtra("mode");

//        appData.currentMusicCourseId = appData.playingCourseFileList.get(0).getCourseId();
        musicActivityObserver();

        initView();

        if ( mMode != null && (mMode.contains("list") || mMode.contains("last")) ) {
            appData.playingCourseFileListPostion = getIntent().getIntExtra("current_position", 0);
            mMusicTitle = getIntent().getStringExtra("title");
            appData.playingCourseFileId = appData.playingCourseFileList.get(appData.playingCourseFileListPostion).getId();
            Log.d(TAG, "设置 app.currentCourseFileId= " + appData.playingCourseFileId);
            bindMusicService();
        } else {
            if (getIntent().getStringExtra(Constant.FromIntent).contains(Constant.FloatWindow)) {
                Log.d(TAG, " intent 的发送者为" + getIntent().getStringExtra(Constant.FromIntent) + ", 需要设置时间进度文本");
                setTimeText();
                mMusicTitle = getIntent().getStringExtra("float_text");
            } else {
                mMusicTitle = SPUtils.getTitleFromName(appData.playingCourseFileList.get(appData.playingCourseFileListPostion).getFileName());
            }

            musicControl.setText();
            if (musicControl.isPlaying()) {
                mPlayPauseView.setImageResource(R.drawable.pause_dark);
            } else {
                mPlayPauseView.setImageResource(R.drawable.play_dark);
            }
        }

//        setLockedScreen();
        mTitleTextView.setText(mMusicTitle);
    }

    // 设置锁屏显示
    private void setLockedScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //解锁
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON //保持屏幕不息屏
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);//点亮屏幕

        if (Build.VERSION.SDK_INT > 27) {
            setShowWhenLocked(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }

        WindowManager.LayoutParams params = getWindow().getAttributes();

//低调模式, 会隐藏不重要的状态栏图标，https://blog.csdn.net/QQsongQQ/article/details/89312763
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;

        getWindow().setAttributes(params);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Todo 待定
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);

        SPUtils.sendListenedPerscent();
    }

    private void bindMusicService() {
        if (musicControl == null) {
            Constant.intent2 = new Intent(this, MusicService.class);//创建意图对象
            Constant.conn1 = new MusiceServiceConnection();
            bindService(Constant.intent2, Constant.conn1, BIND_AUTO_CREATE); //绑定服务
        } else {
            musicControl.initPlayer(mMusicUrl);
            if (mMode.contains("last")) {
                musicControl.playListened( NEWPLAY);
            } else {
                musicControl.playListened(CONTINURE_PLAY);
            }
        }

//        if (floatingControl == null) {
//            intent3 = new Intent(this, FloatingImageDisplayService.class);//创建意图对象
//            mServiceConnection = new MusiceServiceConnection();
//            bindService(intent3, mServiceConnection, BIND_AUTO_CREATE);  //绑定服务
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void initView() {
        mProgressTextView = (TextView) findViewById(R.id.tv_progress);
        mTotalTextView = (TextView) findViewById(R.id.tv_total);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        mTitleTextView = (TextView) findViewById(R.id.song_name);
        mPlayPauseView = findViewById(R.id.play_pause);
        mPlayPauseView.setOnClickListener(this);
        findViewById(R.id.play_previous).setOnClickListener(this);
        findViewById(R.id.play_next).setOnClickListener(this);

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
//                    mPlayPauseView.setImageResource(R.drawable.stop);
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
        ActivityManager.setCurrentActivity(MusicActivity.this);
    }

    public static Handler handler = new Handler() {
        public int lastPercent;

        @Override
        public void handleMessage(Message msg) {
            if (!musicControl.isPlaying()) {
                return;
            }
            Bundle bundle = msg.getData();
            int duration = bundle.getInt("duration");
            int currentPosition = bundle.getInt("currentPosition");
            seekBar.setMax(duration);
            seekBar.setProgress(currentPosition);
            int minute = duration / 1000 / 60;
            int second = duration / 1000 % 60;
            String strMinute = null;
            String strSecond = null;
            if (minute < 10) {
                strMinute = "0" + minute;
            } else {
                strMinute = minute + "";
            }
            if (second < 10) {
                strSecond = "0" + second;
            } else {
                strSecond = second + "";
            }
            mTotalDurationText = strMinute + ":" + strSecond;
            mTotalTextView.setText(mTotalDurationText);

            minute = currentPosition / 1000 / 60;
            second = currentPosition / 1000 % 60;
            if (minute < 10) {
                strMinute = "0" + minute;
            } else {
                strMinute = minute + " ";
            }
            if (second < 10) {
                strSecond = "0" + second;
            } else {
                strSecond = second + " ";
            }
            mProgressText = strMinute + ":" + strSecond;
            mProgressTextView.setText(mProgressText);

            int currentPercent = 0;
            if (duration > 0) {
                currentPercent = (currentPosition * 100 / duration);

                if (currentPercent == 0) {
                    currentPercent = 1;
                }

                if (currentPercent != lastPercent || (currentPercent == 1 && lastPercent != 1)) {
                    String listenedPercent = Integer.toString(currentPercent);
                    Log.d(TAG, "handleMessage  listenedPercent = " + listenedPercent
                            + ", currentPosition=" + currentPosition + ", duration=" + duration
                            + ", cp=" + currentPercent
                    );
                    ListenedFile listenedFile = new ListenedFile();
                    listenedFile.listenedPercent = currentPercent;
                    listenedFile.position = currentPosition;

                    SPUtils.updateUserListenedV1(
                            Constant.appData.UserCode,
                            Constant.appData.playingCourseId,
                            Constant.appData.playingCourseFileId,
                            listenedFile.listenedPercent,
                            listenedFile.position);


                    mCourseListActivityLiveData.postValue(listenedFile);
                }
            }
            lastPercent = currentPercent;
        }
    };


    private void setTimeText() {
        mTotalTextView.setText(mTotalDurationText);
        mProgressTextView.setText(mProgressText);
    }

    private void musicActivityObserver() {
        mMusicActivityLiveData = LiveDataBus.getInstance().with(Constant.MusicLiveDataObserverTag, String.class);
        mMusicActivityLiveData.observe(MusicActivity.this, true, new Observer<String>() {
            @Override
            public void onChanged(String state) {
                Log.d(TAG, " 观察者监控到消息 = " + state);
                switch (state) {
                    case PAUSE:
                        mPlayPauseView.setImageResource(R.drawable.play_dark);
                        setTimeText();
                        break;
                    case CONTINURE_PLAY:
                        mPlayPauseView.setImageResource(R.drawable.pause_dark);
                        setTimeText();
                        break;
                    case NEWPLAY:
                        mMusicTitle = SPUtils.getTitleFromName(appData.playingCourseFileList.get(appData.playingCourseFileListPostion).getFileName());
                        mTitleTextView.setText(mMusicTitle);
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

//    public void startFloatingImageDisplayService() {
//        if (FloatingImageDisplayService.isStarted) {
//            return;
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////            if (!Settings.canDrawOverlays(this)) {
////                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
////                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 1);
////            } else {
//            startService(new Intent(MusicActivity.this, FloatingImageDisplayService.class));
////            }
//        }
//    }

    public class MusiceServiceConnection implements ServiceConnection {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: 服务连接成功, service类名=" + name.getShortClassName());
            String shortClassName = name.getShortClassName();
            if (shortClassName.contains("MusicService")) {
                musicControl = (MusicService.MusicControl) service;
                musicControl.initPlayer(mMusicUrl);
                if (mMode.contains("last")) {
                    musicControl.playListened(NEWPLAY);
                } else {
                    musicControl.playListened(CONTINURE_PLAY);
                }
                Log.d(Constant.TAG, "musicControl 初始化完成 ");
            }
//            else if (shortClassName.contains("FloatingImageDisplayService")) {
////                Constant.floatingControl = (FloatingImageDisplayService.FloatingControl) service;
////                Constant.floatingControl.initFloatingWindow();
////                Constant.floatingControl.setVisibility(false);
////                Constant.floatingControl.setText(musicTitle);
////                Log.d(Constant.TAG, "floatingControl 初始化完成");
//            }
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
                if (appData.playingCourseFileListPostion <= 0) {
                    appData.playingCourseFileListPostion = 0;
                } else {
                    appData.playingCourseFileListPostion--;
                    appData.playingCourseFileId = appData.playingCourseFileList.get(appData.playingCourseFileListPostion).getId();
                    musicControl.playListened(NEWPLAY);
                }
                break;
            case R.id.play_next:
                if (appData.playingCourseFileListPostion >= (appData.playingCourseFileList.size() - 1)) {
                    appData.playingCourseFileListPostion = (appData.playingCourseFileList.size() - 1);
                } else {
                    appData.playingCourseFileListPostion++;
                    appData.playingCourseFileId = appData.playingCourseFileList.get(appData.playingCourseFileListPostion).getId();
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
}


